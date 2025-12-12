# Exemplo Prático: Sistema de Pedidos com Kafka e Quarkus

Este exemplo demonstra um sistema completo de processamento de pedidos usando Kafka para comunicação assíncrona entre microservices.

## Arquitetura do Sistema

```
┌──────────────────┐         ┌──────────────────┐
│  Order Service   │────────►│  Kafka Broker    │
│  (Producer)      │         │                  │
└──────────────────┘         │  Topic: orders   │
                             │                  │
                             └────────┬─────────┘
                                      │
                        ┌─────────────┼─────────────┐
                        │             │             │
                        ▼             ▼             ▼
              ┌────────────┐  ┌────────────┐  ┌────────────┐
              │  Payment   │  │ Inventory  │  │   Email    │
              │  Service   │  │  Service   │  │  Service   │
              │ (Consumer) │  │ (Consumer) │  │ (Consumer) │
              └────────────┘  └────────────┘  └────────────┘
```

## 1. Modelo de Domínio

```java
package com.example.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class Order {
    private String orderId;
    private String customerId;
    private List<OrderItem> items;
    private BigDecimal totalAmount;
    private OrderStatus status;
    private LocalDateTime createdAt;
    
    public Order() {
        this.createdAt = LocalDateTime.now();
        this.status = OrderStatus.PENDING;
    }
    
    // Getters e Setters
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    
    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }
    
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    
    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

class OrderItem {
    private String productId;
    private String productName;
    private Integer quantity;
    private BigDecimal unitPrice;
    
    // Getters e Setters
    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }
    
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    
    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
}

enum OrderStatus {
    PENDING,
    PAYMENT_PROCESSING,
    PAYMENT_APPROVED,
    PAYMENT_FAILED,
    INVENTORY_RESERVED,
    INVENTORY_FAILED,
    COMPLETED,
    CANCELLED
}
```

## 2. Events (Eventos do Sistema)

```java
package com.example.events;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// Evento base
public abstract class OrderEvent {
    protected String orderId;
    protected String customerId;
    protected LocalDateTime timestamp;
    
    public OrderEvent() {
        this.timestamp = LocalDateTime.now();
    }
    
    // Getters e Setters
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    
    public LocalDateTime getTimestamp() { return timestamp; }
}

// Evento: Pedido Criado
public class OrderCreatedEvent extends OrderEvent {
    private BigDecimal totalAmount;
    private List<OrderItemDto> items;
    
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    
    public List<OrderItemDto> getItems() { return items; }
    public void setItems(List<OrderItemDto> items) { this.items = items; }
}

// Evento: Pagamento Processado
public class PaymentProcessedEvent extends OrderEvent {
    private boolean success;
    private String transactionId;
    private String message;
    
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    
    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}

// Evento: Inventário Reservado
public class InventoryReservedEvent extends OrderEvent {
    private boolean success;
    private String message;
    
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}

// Evento: Pedido Completado
public class OrderCompletedEvent extends OrderEvent {
    private BigDecimal totalAmount;
    
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
}

class OrderItemDto {
    private String productId;
    private Integer quantity;
    private BigDecimal unitPrice;
    
    // Getters e Setters
}
```

## 3. Order Service (Producer)

```java
package com.example.order;

import com.example.events.OrderCreatedEvent;
import com.example.model.Order;
import io.smallrye.reactive.messaging.kafka.Record;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.jboss.logging.Logger;

import java.util.UUID;

@ApplicationScoped
public class OrderService {
    
    private static final Logger LOG = Logger.getLogger(OrderService.class);
    
    @Inject
    @Channel("orders-created")
    Emitter<Record<String, OrderCreatedEvent>> orderCreatedEmitter;
    
    public Order createOrder(Order order) {
        // Gerar ID único
        order.setOrderId(UUID.randomUUID().toString());
        
        LOG.info("Creating order: " + order.getOrderId());
        
        // Criar evento
        OrderCreatedEvent event = new OrderCreatedEvent();
        event.setOrderId(order.getOrderId());
        event.setCustomerId(order.getCustomerId());
        event.setTotalAmount(order.getTotalAmount());
        // Mapear items...
        
        // Publicar evento no Kafka
        orderCreatedEmitter.send(Record.of(order.getOrderId(), event));
        
        LOG.info("Order created event published: " + order.getOrderId());
        
        return order;
    }
}
```

```java
package com.example.order;

import com.example.model.Order;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/orders")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OrderResource {
    
    @Inject
    OrderService orderService;
    
    @POST
    public Response createOrder(Order order) {
        Order created = orderService.createOrder(order);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }
}
```

## 4. Payment Service (Consumer & Producer)

```java
package com.example.payment;

import com.example.events.OrderCreatedEvent;
import com.example.events.PaymentProcessedEvent;
import io.smallrye.reactive.messaging.kafka.Record;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

import java.util.UUID;
import java.util.concurrent.CompletionStage;

@ApplicationScoped
public class PaymentService {
    
    private static final Logger LOG = Logger.getLogger(PaymentService.class);
    
    @Inject
    @Channel("payment-processed")
    Emitter<Record<String, PaymentProcessedEvent>> paymentEmitter;
    
    @Incoming("orders-created")
    public CompletionStage<Void> processPayment(Record<String, OrderCreatedEvent> record) {
        OrderCreatedEvent event = record.value();
        
        LOG.info("Processing payment for order: " + event.getOrderId());
        
        // Simular processamento de pagamento
        boolean paymentSuccess = processPaymentLogic(event);
        
        // Criar evento de resposta
        PaymentProcessedEvent paymentEvent = new PaymentProcessedEvent();
        paymentEvent.setOrderId(event.getOrderId());
        paymentEvent.setCustomerId(event.getCustomerId());
        paymentEvent.setSuccess(paymentSuccess);
        paymentEvent.setTransactionId(UUID.randomUUID().toString());
        
        if (paymentSuccess) {
            paymentEvent.setMessage("Payment approved");
            LOG.info("Payment approved for order: " + event.getOrderId());
        } else {
            paymentEvent.setMessage("Payment failed - insufficient funds");
            LOG.warn("Payment failed for order: " + event.getOrderId());
        }
        
        // Publicar evento de pagamento processado
        paymentEmitter.send(Record.of(event.getOrderId(), paymentEvent));
        
        return record.ack();
    }
    
    private boolean processPaymentLogic(OrderCreatedEvent event) {
        // Simular validação de cartão de crédito
        // Em produção: chamar gateway de pagamento
        try {
            Thread.sleep(1000); // Simular latência
            // 90% de aprovação
            return Math.random() > 0.1;
        } catch (InterruptedException e) {
            return false;
        }
    }
}
```

## 5. Inventory Service (Consumer & Producer)

```java
package com.example.inventory;

import com.example.events.InventoryReservedEvent;
import com.example.events.PaymentProcessedEvent;
import io.smallrye.reactive.messaging.kafka.Record;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

import java.util.concurrent.CompletionStage;

@ApplicationScoped
public class InventoryService {
    
    private static final Logger LOG = Logger.getLogger(InventoryService.class);
    
    @Inject
    @Channel("inventory-reserved")
    Emitter<Record<String, InventoryReservedEvent>> inventoryEmitter;
    
    @Incoming("payment-processed")
    public CompletionStage<Void> reserveInventory(Record<String, PaymentProcessedEvent> record) {
        PaymentProcessedEvent event = record.value();
        
        // Só processar se pagamento foi aprovado
        if (!event.isSuccess()) {
            LOG.info("Skipping inventory reservation - payment failed for order: " 
                    + event.getOrderId());
            return record.ack();
        }
        
        LOG.info("Reserving inventory for order: " + event.getOrderId());
        
        // Simular reserva de inventário
        boolean inventorySuccess = reserveInventoryLogic(event);
        
        // Criar evento de resposta
        InventoryReservedEvent inventoryEvent = new InventoryReservedEvent();
        inventoryEvent.setOrderId(event.getOrderId());
        inventoryEvent.setCustomerId(event.getCustomerId());
        inventoryEvent.setSuccess(inventorySuccess);
        
        if (inventorySuccess) {
            inventoryEvent.setMessage("Inventory reserved successfully");
            LOG.info("Inventory reserved for order: " + event.getOrderId());
        } else {
            inventoryEvent.setMessage("Inventory reservation failed - out of stock");
            LOG.warn("Inventory failed for order: " + event.getOrderId());
        }
        
        // Publicar evento
        inventoryEmitter.send(Record.of(event.getOrderId(), inventoryEvent));
        
        return record.ack();
    }
    
    private boolean reserveInventoryLogic(PaymentProcessedEvent event) {
        // Simular verificação de estoque
        // Em produção: verificar banco de dados de inventário
        try {
            Thread.sleep(500);
            // 95% de sucesso
            return Math.random() > 0.05;
        } catch (InterruptedException e) {
            return false;
        }
    }
}
```

## 6. Email Notification Service (Consumer)

```java
package com.example.notification;

import com.example.events.OrderCompletedEvent;
import com.example.events.InventoryReservedEvent;
import io.smallrye.reactive.messaging.kafka.Record;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

import java.util.concurrent.CompletionStage;

@ApplicationScoped
public class EmailNotificationService {
    
    private static final Logger LOG = Logger.getLogger(EmailNotificationService.class);
    
    @Incoming("inventory-reserved")
    public CompletionStage<Void> sendOrderConfirmation(Record<String, InventoryReservedEvent> record) {
        InventoryReservedEvent event = record.value();
        
        if (event.isSuccess()) {
            LOG.info("Sending order confirmation email for order: " + event.getOrderId());
            sendEmail(
                event.getCustomerId(),
                "Order Confirmed",
                "Your order " + event.getOrderId() + " has been confirmed!"
            );
        } else {
            LOG.info("Sending order cancellation email for order: " + event.getOrderId());
            sendEmail(
                event.getCustomerId(),
                "Order Cancelled",
                "Your order " + event.getOrderId() + " has been cancelled due to inventory issues."
            );
        }
        
        return record.ack();
    }
    
    private void sendEmail(String to, String subject, String body) {
        // Simular envio de email
        LOG.info(String.format("EMAIL | To: %s | Subject: %s | Body: %s", to, subject, body));
        // Em produção: integrar com serviço de email (SendGrid, AWS SES, etc.)
    }
}
```

## 7. Configuração (application.properties)

### Order Service
```properties
# application.properties (Order Service)
quarkus.http.port=8081

# Kafka
kafka.bootstrap.servers=localhost:9092

# Outgoing - Order Created
mp.messaging.outgoing.orders-created.connector=smallrye-kafka
mp.messaging.outgoing.orders-created.topic=orders-created
mp.messaging.outgoing.orders-created.value.serializer=io.quarkus.kafka.client.serialization.ObjectMapperSerializer
```

### Payment Service
```properties
# application.properties (Payment Service)
quarkus.http.port=8082

# Kafka
kafka.bootstrap.servers=localhost:9092

# Incoming - Orders Created
mp.messaging.incoming.orders-created.connector=smallrye-kafka
mp.messaging.incoming.orders-created.topic=orders-created
mp.messaging.incoming.orders-created.group.id=payment-service
mp.messaging.incoming.orders-created.value.deserializer=io.quarkus.kafka.client.serialization.ObjectMapperDeserializer
mp.messaging.incoming.orders-created.auto.offset.reset=earliest

# Outgoing - Payment Processed
mp.messaging.outgoing.payment-processed.connector=smallrye-kafka
mp.messaging.outgoing.payment-processed.topic=payment-processed
mp.messaging.outgoing.payment-processed.value.serializer=io.quarkus.kafka.client.serialization.ObjectMapperSerializer
```

### Inventory Service
```properties
# application.properties (Inventory Service)
quarkus.http.port=8083

# Kafka
kafka.bootstrap.servers=localhost:9092

# Incoming - Payment Processed
mp.messaging.incoming.payment-processed.connector=smallrye-kafka
mp.messaging.incoming.payment-processed.topic=payment-processed
mp.messaging.incoming.payment-processed.group.id=inventory-service
mp.messaging.incoming.payment-processed.value.deserializer=io.quarkus.kafka.client.serialization.ObjectMapperDeserializer

# Outgoing - Inventory Reserved
mp.messaging.outgoing.inventory-reserved.connector=smallrye-kafka
mp.messaging.outgoing.inventory-reserved.topic=inventory-reserved
mp.messaging.outgoing.inventory-reserved.value.serializer=io.quarkus.kafka.client.serialization.ObjectMapperSerializer
```

### Email Service
```properties
# application.properties (Email Service)
quarkus.http.port=8084

# Kafka
kafka.bootstrap.servers=localhost:9092

# Incoming - Inventory Reserved
mp.messaging.incoming.inventory-reserved.connector=smallrye-kafka
mp.messaging.incoming.inventory-reserved.topic=inventory-reserved
mp.messaging.incoming.inventory-reserved.group.id=email-service
mp.messaging.incoming.inventory-reserved.value.deserializer=io.quarkus.kafka.client.serialization.ObjectMapperDeserializer
```

## 8. Docker Compose

```yaml
version: '3.8'

services:
  zookeeper:
    image: confluentinc/cp-zookeeper:7.5.0
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181
      ZOOKEEPER_TICK_TIME: 2000
    ports:
      - "2181:2181"

  kafka:
    image: confluentinc/cp-kafka:7.5.0
    depends_on:
      - zookeeper
    ports:
      - "9092:9092"
    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://localhost:9092
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
```

## Como Executar

```bash
# 1. Iniciar Kafka
docker-compose up -d

# 2. Iniciar cada serviço em terminais diferentes
cd order-service && ./mvnw quarkus:dev
cd payment-service && ./mvnw quarkus:dev
cd inventory-service && ./mvnw quarkus:dev
cd email-service && ./mvnw quarkus:dev

# 3. Criar um pedido
curl -X POST http://localhost:8081/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "customer-123",
    "items": [
      {
        "productId": "prod-1",
        "productName": "Notebook",
        "quantity": 1,
        "unitPrice": 3000.00
      }
    ],
    "totalAmount": 3000.00
  }'
```

## Fluxo de Eventos

1. **Order Service** cria pedido → publica `OrderCreatedEvent`
2. **Payment Service** consome evento → processa pagamento → publica `PaymentProcessedEvent`
3. **Inventory Service** consome evento → reserva estoque → publica `InventoryReservedEvent`
4. **Email Service** consome evento → envia email de confirmação

## Monitoramento

```bash
# Ver mensagens em um tópico
kafka-console-consumer --bootstrap-server localhost:9092 \
  --topic orders-created --from-beginning

# Listar consumer groups
kafka-consumer-groups --bootstrap-server localhost:9092 --list

# Verificar lag
kafka-consumer-groups --bootstrap-server localhost:9092 \
  --group payment-service --describe
```

## Conceitos Demonstrados

✅ **Event-Driven Architecture**: Comunicação via eventos  
✅ **Choreography-based Saga**: Cada serviço reage a eventos  
✅ **Asynchronous Communication**: Desacoplamento entre serviços  
✅ **At-least-once Delivery**: Garantia de entrega do Kafka  
✅ **Consumer Groups**: Paralelismo e escalabilidade  

---

[← Voltar](../README.md)
