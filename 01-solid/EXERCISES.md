# Exercícios Práticos - SOLID Principles

Este arquivo contém exercícios práticos para aplicar os princípios SOLID.

## Exercício 1: Single Responsibility Principle (SRP)

### Problema
Refatore a classe abaixo que viola o SRP:

```java
public class Employee {
    private String name;
    private String email;
    private double salary;
    
    public void save() {
        // Salva no banco de dados
        Connection conn = DriverManager.getConnection("jdbc:...");
        // ...
    }
    
    public void sendEmail(String message) {
        // Envia email
        EmailClient.send(email, message);
    }
    
    public double calculateTax() {
        return salary * 0.15;
    }
    
    public void generateReport() {
        // Gera relatório PDF
        PDFGenerator.generate(this);
    }
}
```

### Tarefa
1. Identifique todas as responsabilidades da classe `Employee`
2. Crie novas classes seguindo o SRP
3. Refatore o código para separar as responsabilidades

### Dica
Pense em: persistência, notificação, cálculos de negócio, geração de relatórios

---

## Exercício 2: Open/Closed Principle (OCP)

### Problema
Refatore a classe abaixo para ficar aberta para extensão e fechada para modificação:

```java
public class DiscountCalculator {
    public double calculateDiscount(String customerType, double amount) {
        if (customerType.equals("REGULAR")) {
            return amount * 0.05;
        } else if (customerType.equals("VIP")) {
            return amount * 0.10;
        } else if (customerType.equals("GOLD")) {
            return amount * 0.15;
        }
        return 0;
    }
}
```

### Tarefa
1. Crie uma interface ou classe abstrata para representar estratégias de desconto
2. Implemente classes concretas para cada tipo de cliente
3. Refatore `DiscountCalculator` para usar polimorfismo
4. Adicione um novo tipo de cliente (PLATINUM com 20% de desconto) sem modificar código existente

---

## Exercício 3: Liskov Substitution Principle (LSP)

### Problema
Identifique e corrija a violação do LSP:

```java
public class Bird {
    public void fly() {
        System.out.println("Flying...");
    }
}

public class Penguin extends Bird {
    @Override
    public void fly() {
        throw new UnsupportedOperationException("Penguins can't fly!");
    }
}
```

### Tarefa
1. Identifique por que isso viola o LSP
2. Redesenhe a hierarquia de classes
3. Crie interfaces apropriadas
4. Implemente classes que não violem o LSP

### Dica
Nem todas as aves voam. Pense em características comuns e específicas.

---

## Exercício 4: Interface Segregation Principle (ISP)

### Problema
Refatore a interface "gorda" abaixo:

```java
public interface Worker {
    void work();
    void eat();
    void sleep();
    void attendMeeting();
    void writeCode();
    void testSoftware();
    void designArchitecture();
}

public class Developer implements Worker {
    public void work() { /* implementação */ }
    public void eat() { /* implementação */ }
    public void sleep() { /* implementação */ }
    public void attendMeeting() { /* implementação */ }
    public void writeCode() { /* implementação */ }
    public void testSoftware() { /* implementação */ }
    public void designArchitecture() { /* implementação */ }
}

public class Tester implements Worker {
    public void work() { /* implementação */ }
    public void eat() { /* implementação */ }
    public void sleep() { /* implementação */ }
    public void attendMeeting() { /* implementação */ }
    public void writeCode() { throw new UnsupportedOperationException(); }
    public void testSoftware() { /* implementação */ }
    public void designArchitecture() { throw new UnsupportedOperationException(); }
}
```

### Tarefa
1. Identifique os métodos que não são comuns a todos os trabalhadores
2. Crie interfaces menores e mais específicas
3. Faça as classes implementarem apenas as interfaces necessárias

---

## Exercício 5: Dependency Inversion Principle (DIP)

### Problema
Refatore o código abaixo que viola o DIP:

```java
public class NotificationService {
    private EmailSender emailSender = new EmailSender();
    
    public void sendNotification(String message) {
        emailSender.sendEmail(message);
    }
}

public class EmailSender {
    public void sendEmail(String message) {
        System.out.println("Email sent: " + message);
    }
}
```

### Tarefa
1. Crie uma abstração (interface) para o sender
2. Implemente diferentes tipos de sender (Email, SMS, Push)
3. Refatore `NotificationService` para depender da abstração
4. Use injeção de dependência via construtor

---

## Exercício 6: Desafio Completo - Sistema de Pedidos

### Descrição
Desenvolva um sistema de pedidos aplicando TODOS os princípios SOLID.

### Requisitos

1. **Entidades**:
   - Order (pedido)
   - OrderItem (item do pedido)
   - Customer (cliente)

2. **Funcionalidades**:
   - Criar pedido
   - Adicionar/remover itens
   - Calcular total com impostos
   - Aplicar descontos baseados no tipo de cliente
   - Salvar pedido (em memória, banco de dados)
   - Enviar notificação (email, SMS)
   - Gerar relatório (PDF, Excel)

3. **Restrições**:
   - Cada classe deve ter uma única responsabilidade (SRP)
   - Deve ser fácil adicionar novos tipos de desconto sem modificar código existente (OCP)
   - Subclasses devem ser substituíveis (LSP)
   - Interfaces devem ser segregadas e específicas (ISP)
   - Classes de alto nível não devem depender de implementações concretas (DIP)

### Entregáveis
- Diagrama de classes
- Código implementado
- Testes unitários demonstrando os princípios

---

## Exercício 7: Code Review

### Tarefa
Analise o código abaixo e identifique TODOS os princípios SOLID violados:

```java
public class OrderProcessor {
    public void processOrder(int orderId) {
        // Buscar pedido do banco
        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/db");
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM orders WHERE id = " + orderId);
        
        // Processar
        if (rs.next()) {
            String customerType = rs.getString("customer_type");
            double total = rs.getDouble("total");
            
            // Calcular desconto
            double discount = 0;
            if (customerType.equals("REGULAR")) {
                discount = total * 0.05;
            } else if (customerType.equals("VIP")) {
                discount = total * 0.10;
            }
            
            double finalTotal = total - discount;
            
            // Atualizar pedido
            stmt.executeUpdate("UPDATE orders SET final_total = " + finalTotal + 
                             " WHERE id = " + orderId);
            
            // Enviar email
            String email = rs.getString("customer_email");
            EmailClient client = new EmailClient();
            client.send(email, "Seu pedido foi processado. Total: " + finalTotal);
            
            // Gerar relatório
            PDFGenerator pdf = new PDFGenerator();
            pdf.generate("Pedido #" + orderId + " - Total: " + finalTotal);
        }
        
        conn.close();
    }
}
```

### Perguntas
1. Quais princípios SOLID são violados?
2. Como você refatoraria este código?
3. Desenhe uma arquitetura melhor para este sistema

---

## Recursos para Estudo

- Revise o README.md principal sobre SOLID
- Estude os exemplos em `examples/UserService.java`
- Pratique refatoração gradual
- Escreva testes para validar suas refatorações

## Soluções

As soluções comentadas estão disponíveis na pasta `solutions/` (criar após completar os exercícios).

---

**Dica**: Comece pelos exercícios mais simples e vá progredindo. A prática leva à perfeição!

[← Voltar](../README.md)
