# Padrões de Arquitetura

Padrões arquiteturais definem a estrutura fundamental de um sistema de software. Eles determinam como os componentes do sistema se organizam e interagem.

## 📋 Índice

1. [Arquitetura Monolítica](#1-arquitetura-monolítica)
2. [Microservices](#2-microservices)
3. [Event-Driven Architecture](#3-event-driven-architecture)
4. [Clean Architecture](#4-clean-architecture)
5. [Hexagonal Architecture](#5-hexagonal-architecture-ports--adapters)
6. [CQRS](#6-cqrs-command-query-responsibility-segregation)
7. [Saga Pattern](#7-saga-pattern)

---

## 1. Arquitetura Monolítica

### Conceito
Aplicação construída como uma única unidade indivisível, onde todos os componentes estão fortemente acoplados e executam no mesmo processo.

### Estrutura
```
┌─────────────────────────────────┐
│      Aplicação Monolítica       │
├─────────────────────────────────┤
│   UI Layer                      │
├─────────────────────────────────┤
│   Business Logic Layer          │
├─────────────────────────────────┤
│   Data Access Layer             │
├─────────────────────────────────┤
│   Database                      │
└─────────────────────────────────┘
```

### Vantagens
✅ Simples de desenvolver e testar  
✅ Fácil de fazer deploy  
✅ Menor complexidade operacional  
✅ Performance (sem overhead de rede)  

### Desvantagens
❌ Difícil de escalar horizontalmente  
❌ Deployment arriscado (tudo ou nada)  
❌ Dificuldade em adotar novas tecnologias  
❌ Equipe grande trabalhando na mesma base de código  

### Quando Usar
- Aplicações pequenas ou médias
- MVPs e protótipos
- Equipes pequenas
- Requisitos de negócio simples

---

## 2. Microservices

### Conceito
Arquitetura que estrutura a aplicação como uma coleção de serviços pequenos, independentes e que se comunicam através de APIs.

### Estrutura
```
┌──────────────┐    ┌──────────────┐    ┌──────────────┐
│   User       │    │   Order      │    │   Payment    │
│   Service    │◄──►│   Service    │◄──►│   Service    │
└──────┬───────┘    └──────┬───────┘    └──────┬───────┘
       │                   │                   │
       ▼                   ▼                   ▼
   ┌───────┐          ┌───────┐          ┌───────┐
   │  DB   │          │  DB   │          │  DB   │
   └───────┘          └───────┘          └───────┘
```

### Princípios
- **Serviços independentes**: Cada serviço pode ser desenvolvido, testado e deployado independentemente
- **Organização por domínio de negócio**: Serviços alinhados com bounded contexts
- **Descentralização**: Dados, governança e decisões tecnológicas descentralizadas
- **Resiliência**: Falha de um serviço não derruba todo o sistema

### Vantagens
✅ Escalabilidade independente  
✅ Flexibilidade tecnológica  
✅ Deploy independente  
✅ Isolamento de falhas  
✅ Equipes autônomas  

### Desvantagens
❌ Complexidade operacional  
❌ Comunicação entre serviços (latência)  
❌ Transações distribuídas  
❌ Testes mais complexos  
❌ Monitoramento e debugging mais difícil  

### Exemplo de Comunicação
```java
// Serviço de Pedidos chamando Serviço de Pagamento
@RestClient
public interface PaymentServiceClient {
    @POST
    @Path("/payments")
    PaymentResponse processPayment(PaymentRequest request);
}

public class OrderService {
    @Inject
    PaymentServiceClient paymentClient;
    
    public Order createOrder(OrderRequest request) {
        Order order = new Order(request);
        
        PaymentResponse payment = paymentClient.processPayment(
            new PaymentRequest(order.getTotalAmount())
        );
        
        if (payment.isSuccessful()) {
            order.setStatus(OrderStatus.CONFIRMED);
        }
        
        return orderRepository.save(order);
    }
}
```

### Quando Usar
- Aplicações grandes e complexas
- Equipes grandes distribuídas
- Necessidade de escalar partes específicas
- Diferentes requisitos tecnológicos

---

## 3. Event-Driven Architecture

### Conceito
Arquitetura onde componentes se comunicam através de eventos assíncronos. Produtores emitem eventos e consumidores reagem a eles.

### Estrutura
```
┌───────────┐                        ┌───────────┐
│ Producer  │──┐                  ┌──│ Consumer  │
└───────────┘  │    ┌─────────┐   │  └───────────┘
               ├───►│  Event  │───┤
┌───────────┐  │    │  Broker │   │  ┌───────────┐
│ Producer  │──┘    └─────────┘   └──│ Consumer  │
└───────────┘                        └───────────┘
```

### Componentes
- **Event Producers**: Geram eventos
- **Event Broker**: Kafka, RabbitMQ, AWS SNS/SQS
- **Event Consumers**: Processam eventos

### Vantagens
✅ Baixo acoplamento  
✅ Alta escalabilidade  
✅ Flexibilidade  
✅ Auditoria natural (event log)  

### Desvantagens
❌ Complexidade  
❌ Eventual consistency  
❌ Debugging mais difícil  

### Exemplo com Kafka
```java
// Producer
@ApplicationScoped
public class OrderEventProducer {
    
    @Inject
    @Channel("orders")
    Emitter<OrderCreatedEvent> orderEmitter;
    
    public void publishOrderCreated(Order order) {
        OrderCreatedEvent event = new OrderCreatedEvent(
            order.getId(),
            order.getCustomerId(),
            order.getTotalAmount()
        );
        orderEmitter.send(event);
    }
}

// Consumer
@ApplicationScoped
public class OrderEventConsumer {
    
    @Incoming("orders")
    public void processOrderCreated(OrderCreatedEvent event) {
        // Processar evento
        System.out.println("Order created: " + event.getOrderId());
        
        // Atualizar estoque
        // Enviar notificação
        // Iniciar processo de pagamento
    }
}
```

### Quando Usar
- Sistemas distribuídos
- Processamento assíncrono
- Integração entre sistemas
- Event sourcing

---

## 4. Clean Architecture

### Conceito
Arquitetura proposta por Robert C. Martin (Uncle Bob) que enfatiza a separação de responsabilidades e independência de frameworks.

### Estrutura (Camadas Concêntricas)
```
┌─────────────────────────────────────────┐
│        Frameworks & Drivers             │
│  (Web, DB, UI, External Interfaces)     │
├─────────────────────────────────────────┤
│        Interface Adapters               │
│  (Controllers, Gateways, Presenters)    │
├─────────────────────────────────────────┤
│        Application Business Rules       │
│        (Use Cases)                      │
├─────────────────────────────────────────┤
│        Enterprise Business Rules        │
│        (Entities)                       │
└─────────────────────────────────────────┘
```

### Princípios
1. **Independência de Frameworks**
2. **Testável**: Regras de negócio testáveis sem UI, BD, servidor
3. **Independência de UI**
4. **Independência de Banco de Dados**
5. **Independência de qualquer agente externo**

### Regra de Dependência
**As dependências apontam para dentro (das camadas externas para internas)**

### Exemplo
```java
// Entities (Camada mais interna)
public class Order {
    private String id;
    private BigDecimal totalAmount;
    private OrderStatus status;
    
    public void confirm() {
        if (totalAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidOrderException("Amount must be positive");
        }
        this.status = OrderStatus.CONFIRMED;
    }
}

// Use Cases
public class CreateOrderUseCase {
    private final OrderRepository repository;
    private final PaymentGateway paymentGateway;
    
    public CreateOrderUseCase(OrderRepository repository, 
                              PaymentGateway paymentGateway) {
        this.repository = repository;
        this.paymentGateway = paymentGateway;
    }
    
    public Order execute(CreateOrderRequest request) {
        Order order = new Order(request.getItems());
        
        Payment payment = paymentGateway.process(order.getTotalAmount());
        
        if (payment.isSuccessful()) {
            order.confirm();
            return repository.save(order);
        }
        
        throw new PaymentFailedException();
    }
}

// Interface Adapters
public interface OrderRepository {
    Order save(Order order);
    Optional<Order> findById(String id);
}

// Frameworks & Drivers
@Repository
public class JpaOrderRepository implements OrderRepository {
    @PersistenceContext
    private EntityManager em;
    
    @Override
    public Order save(Order order) {
        OrderEntity entity = OrderMapper.toEntity(order);
        em.persist(entity);
        return OrderMapper.toDomain(entity);
    }
}
```

### Quando Usar
- Aplicações com lógica de negócio complexa
- Projetos de longa duração
- Necessidade de mudanças frequentes de tecnologia

---

## 5. Hexagonal Architecture (Ports & Adapters)

### Conceito
Arquitetura que coloca a lógica de negócio no centro, com portas (interfaces) e adaptadores para comunicação externa.

### Estrutura
```
           ┌──────────────┐
           │   REST API   │
           │   Adapter    │
           └──────┬───────┘
                  │
      ┌───────────▼───────────┐
      │      Port (I/F)       │
      ├───────────────────────┤
      │                       │
      │   Domain/Business     │
      │       Logic           │
      │                       │
      ├───────────────────────┤
      │      Port (I/F)       │
      └───────────┬───────────┘
                  │
           ┌──────▼───────┐
           │   Database   │
           │   Adapter    │
           └──────────────┘
```

### Componentes
- **Hexágono (Núcleo)**: Lógica de negócio
- **Portas**: Interfaces que definem contratos
- **Adaptadores**: Implementações concretas (REST, DB, etc.)

### Exemplo
```java
// Port (Interface de entrada)
public interface CreateOrderPort {
    Order createOrder(OrderRequest request);
}

// Port (Interface de saída)
public interface OrderPersistencePort {
    Order save(Order order);
}

public interface PaymentPort {
    Payment processPayment(BigDecimal amount);
}

// Domain Service (Hexágono)
public class OrderService implements CreateOrderPort {
    private final OrderPersistencePort persistencePort;
    private final PaymentPort paymentPort;
    
    public OrderService(OrderPersistencePort persistencePort, 
                       PaymentPort paymentPort) {
        this.persistencePort = persistencePort;
        this.paymentPort = paymentPort;
    }
    
    @Override
    public Order createOrder(OrderRequest request) {
        Order order = new Order(request);
        
        Payment payment = paymentPort.processPayment(order.getTotalAmount());
        
        if (payment.isSuccessful()) {
            order.confirm();
            return persistencePort.save(order);
        }
        
        throw new PaymentFailedException();
    }
}

// Adapter de entrada (REST)
@Path("/orders")
public class OrderRestAdapter {
    private final CreateOrderPort createOrderPort;
    
    @POST
    public Response createOrder(OrderDTO orderDTO) {
        Order order = createOrderPort.createOrder(
            OrderMapper.toRequest(orderDTO)
        );
        return Response.ok(OrderMapper.toDTO(order)).build();
    }
}

// Adapter de saída (Database)
public class OrderDatabaseAdapter implements OrderPersistencePort {
    @PersistenceContext
    private EntityManager em;
    
    @Override
    public Order save(Order order) {
        OrderEntity entity = OrderMapper.toEntity(order);
        em.persist(entity);
        return OrderMapper.toDomain(entity);
    }
}
```

### Quando Usar
- Aplicações que precisam de alta testabilidade
- Sistemas que integram com múltiplos sistemas externos
- Necessidade de trocar implementações facilmente

---

## 6. CQRS (Command Query Responsibility Segregation)

### Conceito
Separação entre operações de leitura (Query) e escrita (Command).

### Estrutura
```
┌──────────┐                    ┌──────────┐
│ Commands │──►Write Model──►   │ Write DB │
└──────────┘                    └────┬─────┘
                                     │
                                     ▼
                                Event Bus
                                     │
                                     ▼
┌──────────┐                    ┌────┴─────┐
│ Queries  │◄───Read Model ◄────│ Read DB  │
└──────────┘                    └──────────┘
```

### Commands (Escrita)
```java
// Command
public class CreateOrderCommand {
    private String customerId;
    private List<OrderItem> items;
    // getters
}

// Command Handler
public class CreateOrderCommandHandler {
    private final OrderRepository repository;
    private final EventPublisher eventPublisher;
    
    public void handle(CreateOrderCommand command) {
        Order order = new Order(command.getCustomerId(), command.getItems());
        repository.save(order);
        
        eventPublisher.publish(new OrderCreatedEvent(order.getId()));
    }
}
```

### Queries (Leitura)
```java
// Query
public class GetOrderByIdQuery {
    private String orderId;
    // getters
}

// Query Handler
public class GetOrderByIdQueryHandler {
    private final OrderReadRepository readRepository;
    
    public OrderDTO handle(GetOrderByIdQuery query) {
        return readRepository.findById(query.getOrderId());
    }
}
```

### Vantagens
✅ Otimização independente de leitura e escrita  
✅ Escalabilidade  
✅ Separação de responsabilidades  

### Desvantagens
❌ Complexidade  
❌ Eventual consistency  
❌ Duplicação de dados  

### Quando Usar
- Sistemas com alta carga de leitura
- Diferentes requisitos de performance para leitura e escrita
- Combinado com Event Sourcing

---

## 7. Saga Pattern

### Conceito
Padrão para gerenciar transações distribuídas através de uma sequência de transações locais.

### Tipos

#### 7.1 Choreography-Based Saga
Cada serviço publica eventos e outros serviços reagem.

```
Order Service ──► Payment Service ──► Inventory Service
     │                  │                    │
     └──────────────────┴────────────────────┘
              Event Bus (Kafka)
```

```java
// Order Service
public class OrderService {
    @Inject
    @Channel("order-created")
    Emitter<OrderCreatedEvent> orderEmitter;
    
    public void createOrder(Order order) {
        orderRepository.save(order);
        orderEmitter.send(new OrderCreatedEvent(order.getId()));
    }
    
    @Incoming("payment-failed")
    public void handlePaymentFailed(PaymentFailedEvent event) {
        Order order = orderRepository.findById(event.getOrderId());
        order.cancel();
        orderRepository.save(order);
    }
}

// Payment Service
public class PaymentService {
    @Incoming("order-created")
    @Outgoing("payment-processed")
    public PaymentProcessedEvent processPayment(OrderCreatedEvent event) {
        // Processar pagamento
        if (success) {
            return new PaymentProcessedEvent(event.getOrderId());
        } else {
            emitter.send(new PaymentFailedEvent(event.getOrderId()));
        }
    }
}
```

#### 7.2 Orchestration-Based Saga
Um orquestrador central coordena a transação.

```
             ┌─────────────────┐
             │  Saga           │
             │  Orchestrator   │
             └────────┬────────┘
                      │
        ┌─────────────┼─────────────┐
        ▼             ▼             ▼
   Order Svc    Payment Svc   Inventory Svc
```

```java
public class OrderSagaOrchestrator {
    
    public void createOrder(OrderRequest request) {
        SagaExecution saga = new SagaExecution();
        
        try {
            // Step 1: Create order
            Order order = orderService.createOrder(request);
            saga.addCompensation(() -> orderService.cancelOrder(order.getId()));
            
            // Step 2: Process payment
            Payment payment = paymentService.processPayment(order);
            saga.addCompensation(() -> paymentService.refund(payment.getId()));
            
            // Step 3: Reserve inventory
            inventoryService.reserveItems(order.getItems());
            saga.addCompensation(() -> inventoryService.releaseItems(order.getItems()));
            
            // Success - commit
            saga.complete();
            
        } catch (Exception e) {
            // Failure - compensate
            saga.compensate();
            throw new SagaFailedException(e);
        }
    }
}
```

### Vantagens
✅ Mantém consistência em sistemas distribuídos  
✅ Não requer 2PC (Two-Phase Commit)  
✅ Cada serviço mantém autonomia  

### Desvantagens
❌ Complexidade  
❌ Dificuldade de debugging  
❌ Eventual consistency  

### Quando Usar
- Transações que envolvem múltiplos microservices
- Impossibilidade de usar transações ACID distribuídas
- Necessidade de compensação em caso de falha

---

## 📊 Comparação de Padrões

| Padrão | Complexidade | Escalabilidade | Uso Típico |
|--------|--------------|----------------|------------|
| Monolítico | Baixa | Baixa | Aplicações pequenas |
| Microservices | Alta | Alta | Sistemas grandes |
| Event-Driven | Alta | Muito Alta | Integração assíncrona |
| Clean Architecture | Média | N/A | Organização interna |
| Hexagonal | Média | N/A | Testabilidade |
| CQRS | Alta | Alta | Separação leitura/escrita |
| Saga | Alta | Alta | Transações distribuídas |

## 🎯 Escolhendo o Padrão Correto

1. **Tamanho do sistema**: Monolítico para pequeno, Microservices para grande
2. **Requisitos de escalabilidade**: Event-Driven, CQRS para alta escala
3. **Complexidade da lógica**: Clean/Hexagonal para lógica complexa
4. **Transações distribuídas**: Saga Pattern
5. **Equipe**: Monolítico para equipes pequenas, Microservices para grandes

## 📚 Recursos Adicionais

- **Livros**:
  - "Building Microservices" - Sam Newman
  - "Clean Architecture" - Robert C. Martin
  - "Implementing Domain-Driven Design" - Vaughn Vernon
  
- **Sites**:
  - [Microsoft Architecture Guide](https://docs.microsoft.com/en-us/azure/architecture/)
  - [Martin Fowler's Blog](https://martinfowler.com/)

---

[← Voltar ao Índice Principal](../README.md)
