# SOLID Principles

Os princípios SOLID são cinco princípios de design orientado a objetos que ajudam a criar sistemas mais compreensíveis, flexíveis e mantíveis.

## 📋 Índice

1. [Single Responsibility Principle (SRP)](#1-single-responsibility-principle-srp)
2. [Open/Closed Principle (OCP)](#2-openclosed-principle-ocp)
3. [Liskov Substitution Principle (LSP)](#3-liskov-substitution-principle-lsp)
4. [Interface Segregation Principle (ISP)](#4-interface-segregation-principle-isp)
5. [Dependency Inversion Principle (DIP)](#5-dependency-inversion-principle-dip)

---

## 1. Single Responsibility Principle (SRP)

### Conceito
**"Uma classe deve ter apenas uma razão para mudar"**

Uma classe deve ter apenas uma responsabilidade ou propósito único no sistema.

### Por que é importante?
- Reduz acoplamento
- Facilita manutenção
- Melhora testabilidade
- Aumenta coesão

### ❌ Violação do SRP

```java
public class UserService {
    public void createUser(User user) {
        // Salva usuário no banco
        database.save(user);
        
        // Envia email de boas-vindas
        emailService.send(user.getEmail(), "Bem-vindo!");
        
        // Registra log
        logger.log("Usuário criado: " + user.getName());
    }
}
```

**Problemas**: A classe tem múltiplas responsabilidades (persistência, email, logging).

### ✅ Aplicando o SRP

```java
public class UserService {
    private final UserRepository repository;
    private final EmailNotificationService emailService;
    private final AuditLogger auditLogger;
    
    public void createUser(User user) {
        repository.save(user);
        emailService.sendWelcomeEmail(user);
        auditLogger.logUserCreation(user);
    }
}

public class UserRepository {
    public void save(User user) {
        database.save(user);
    }
}

public class EmailNotificationService {
    public void sendWelcomeEmail(User user) {
        emailClient.send(user.getEmail(), "Bem-vindo!");
    }
}

public class AuditLogger {
    public void logUserCreation(User user) {
        logger.log("Usuário criado: " + user.getName());
    }
}
```

---

## 2. Open/Closed Principle (OCP)

### Conceito
**"Entidades de software devem estar abertas para extensão, mas fechadas para modificação"**

Você deve poder estender o comportamento de uma classe sem modificá-la.

### Por que é importante?
- Reduz riscos de quebrar código existente
- Facilita adição de novas funcionalidades
- Promove reutilização de código

### ❌ Violação do OCP

```java
public class PaymentProcessor {
    public void processPayment(String paymentType, double amount) {
        if (paymentType.equals("CREDIT_CARD")) {
            // Processa cartão de crédito
        } else if (paymentType.equals("PAYPAL")) {
            // Processa PayPal
        } else if (paymentType.equals("PIX")) {
            // Processa PIX
        }
        // Toda vez que adicionar novo método, precisa modificar esta classe
    }
}
```

### ✅ Aplicando o OCP

```java
public interface PaymentMethod {
    void process(double amount);
}

public class CreditCardPayment implements PaymentMethod {
    @Override
    public void process(double amount) {
        // Processa cartão de crédito
    }
}

public class PayPalPayment implements PaymentMethod {
    @Override
    public void process(double amount) {
        // Processa PayPal
    }
}

public class PixPayment implements PaymentMethod {
    @Override
    public void process(double amount) {
        // Processa PIX
    }
}

public class PaymentProcessor {
    public void processPayment(PaymentMethod paymentMethod, double amount) {
        paymentMethod.process(amount);
    }
}
```

---

## 3. Liskov Substitution Principle (LSP)

### Conceito
**"Objetos de uma superclasse devem poder ser substituídos por objetos de suas subclasses sem quebrar a aplicação"**

Subclasses devem ser substituíveis por suas classes base.

### Por que é importante?
- Garante contratos entre classes
- Previne comportamentos inesperados
- Facilita polimorfismo

### ❌ Violação do LSP

```java
public class Rectangle {
    protected int width;
    protected int height;
    
    public void setWidth(int width) {
        this.width = width;
    }
    
    public void setHeight(int height) {
        this.height = height;
    }
    
    public int getArea() {
        return width * height;
    }
}

public class Square extends Rectangle {
    @Override
    public void setWidth(int width) {
        this.width = width;
        this.height = width; // Quebra o comportamento esperado
    }
    
    @Override
    public void setHeight(int height) {
        this.width = height;
        this.height = height; // Quebra o comportamento esperado
    }
}
```

### ✅ Aplicando o LSP

```java
public interface Shape {
    int getArea();
}

public class Rectangle implements Shape {
    private int width;
    private int height;
    
    public Rectangle(int width, int height) {
        this.width = width;
        this.height = height;
    }
    
    @Override
    public int getArea() {
        return width * height;
    }
}

public class Square implements Shape {
    private int side;
    
    public Square(int side) {
        this.side = side;
    }
    
    @Override
    public int getArea() {
        return side * side;
    }
}
```

---

## 4. Interface Segregation Principle (ISP)

### Conceito
**"Clientes não devem ser forçados a depender de interfaces que não utilizam"**

É melhor ter várias interfaces específicas do que uma interface geral.

### Por que é importante?
- Reduz acoplamento
- Aumenta flexibilidade
- Facilita manutenção

### ❌ Violação do ISP

```java
public interface Worker {
    void work();
    void eat();
    void sleep();
}

public class Robot implements Worker {
    @Override
    public void work() {
        // Robô trabalha
    }
    
    @Override
    public void eat() {
        // Robô não come! Implementação forçada
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void sleep() {
        // Robô não dorme! Implementação forçada
        throw new UnsupportedOperationException();
    }
}
```

### ✅ Aplicando o ISP

```java
public interface Workable {
    void work();
}

public interface Eatable {
    void eat();
}

public interface Sleepable {
    void sleep();
}

public class Human implements Workable, Eatable, Sleepable {
    @Override
    public void work() {
        // Humano trabalha
    }
    
    @Override
    public void eat() {
        // Humano come
    }
    
    @Override
    public void sleep() {
        // Humano dorme
    }
}

public class Robot implements Workable {
    @Override
    public void work() {
        // Robô trabalha
    }
}
```

---

## 5. Dependency Inversion Principle (DIP)

### Conceito
**"Módulos de alto nível não devem depender de módulos de baixo nível. Ambos devem depender de abstrações"**

**"Abstrações não devem depender de detalhes. Detalhes devem depender de abstrações"**

### Por que é importante?
- Reduz acoplamento
- Facilita testes (mocking)
- Aumenta flexibilidade

### ❌ Violação do DIP

```java
public class MySQLDatabase {
    public void save(String data) {
        // Salva no MySQL
    }
}

public class UserService {
    private MySQLDatabase database = new MySQLDatabase();
    
    public void saveUser(User user) {
        database.save(user.toString());
    }
}
```

**Problema**: UserService depende diretamente de MySQLDatabase. Se quiser trocar o banco, precisa modificar UserService.

### ✅ Aplicando o DIP

```java
public interface Database {
    void save(String data);
}

public class MySQLDatabase implements Database {
    @Override
    public void save(String data) {
        // Salva no MySQL
    }
}

public class PostgreSQLDatabase implements Database {
    @Override
    public void save(String data) {
        // Salva no PostgreSQL
    }
}

public class UserService {
    private final Database database;
    
    public UserService(Database database) {
        this.database = database;
    }
    
    public void saveUser(User user) {
        database.save(user.toString());
    }
}

// Uso
Database db = new MySQLDatabase(); // Ou PostgreSQLDatabase
UserService service = new UserService(db);
```

---

## 🎯 Benefícios dos Princípios SOLID

1. **Manutenibilidade**: Código mais fácil de entender e modificar
2. **Testabilidade**: Componentes desacoplados são mais fáceis de testar
3. **Flexibilidade**: Sistema aberto para extensões
4. **Reusabilidade**: Componentes podem ser reutilizados em diferentes contextos
5. **Escalabilidade**: Arquitetura que cresce de forma sustentável

## 📚 Recursos Adicionais

- **Livros**:
  - "Clean Code" - Robert C. Martin
  - "Agile Software Development, Principles, Patterns, and Practices" - Robert C. Martin
  
- **Artigos**:
  - [SOLID Principles in Java](https://www.baeldung.com/solid-principles)
  - [The Principles of OOD](http://butunclebob.com/ArticleS.UncleBob.PrinciplesOfOod)

## 💡 Exercícios

Veja a pasta [exercises/](./exercises/) para exercícios práticos sobre cada princípio.

---

[← Voltar ao Índice Principal](../README.md)
