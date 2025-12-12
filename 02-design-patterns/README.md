# Design Patterns

Design Patterns são soluções reutilizáveis para problemas comuns no desenvolvimento de software. Eles representam as melhores práticas adotadas por desenvolvedores experientes.

## 📋 Categorias

### [Padrões Criacionais](#padrões-criacionais-1)
Lidam com mecanismos de criação de objetos

### [Padrões Estruturais](#padrões-estruturais-1)
Lidam com composição de classes e objetos

### [Padrões Comportamentais](#padrões-comportamentais-1)
Lidam com comunicação entre objetos

---

## Padrões Criacionais

### 1. Singleton

**Objetivo**: Garantir que uma classe tenha apenas uma instância e fornecer um ponto global de acesso a ela.

**Quando usar**:
- Configurações globais
- Gerenciadores de conexão
- Logger

**Exemplo**:
```java
public class DatabaseConnection {
    private static DatabaseConnection instance;
    
    private DatabaseConnection() {
        // Construtor privado
    }
    
    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }
}
```

**Versão Thread-Safe com Lazy Initialization**:
```java
public class DatabaseConnection {
    private DatabaseConnection() {}
    
    private static class SingletonHolder {
        private static final DatabaseConnection INSTANCE = new DatabaseConnection();
    }
    
    public static DatabaseConnection getInstance() {
        return SingletonHolder.INSTANCE;
    }
}
```

---

### 2. Factory Method

**Objetivo**: Definir uma interface para criar um objeto, mas deixar as subclasses decidirem qual classe instanciar.

**Quando usar**:
- Quando a classe não sabe antecipadamente qual subclasse de objetos precisa criar
- Quando você quer delegar a responsabilidade de criação para subclasses

**Exemplo**:
```java
public interface Payment {
    void processPayment(double amount);
}

public class CreditCardPayment implements Payment {
    @Override
    public void processPayment(double amount) {
        System.out.println("Processing credit card payment: $" + amount);
    }
}

public class PixPayment implements Payment {
    @Override
    public void processPayment(double amount) {
        System.out.println("Processing PIX payment: R$" + amount);
    }
}

public abstract class PaymentFactory {
    public abstract Payment createPayment();
    
    public void processTransaction(double amount) {
        Payment payment = createPayment();
        payment.processPayment(amount);
    }
}

public class CreditCardFactory extends PaymentFactory {
    @Override
    public Payment createPayment() {
        return new CreditCardPayment();
    }
}

public class PixFactory extends PaymentFactory {
    @Override
    public Payment createPayment() {
        return new PixPayment();
    }
}
```

---

### 3. Builder

**Objetivo**: Separar a construção de um objeto complexo de sua representação.

**Quando usar**:
- Objetos com muitos parâmetros opcionais
- Construção passo a passo
- Diferentes representações do mesmo objeto

**Exemplo**:
```java
public class User {
    private final String firstName;
    private final String lastName;
    private final String email;
    private final String phone;
    private final String address;
    
    private User(UserBuilder builder) {
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.email = builder.email;
        this.phone = builder.phone;
        this.address = builder.address;
    }
    
    public static class UserBuilder {
        private final String firstName; // Obrigatório
        private final String lastName;  // Obrigatório
        private String email;
        private String phone;
        private String address;
        
        public UserBuilder(String firstName, String lastName) {
            this.firstName = firstName;
            this.lastName = lastName;
        }
        
        public UserBuilder email(String email) {
            this.email = email;
            return this;
        }
        
        public UserBuilder phone(String phone) {
            this.phone = phone;
            return this;
        }
        
        public UserBuilder address(String address) {
            this.address = address;
            return this;
        }
        
        public User build() {
            return new User(this);
        }
    }
}

// Uso
User user = new User.UserBuilder("John", "Doe")
    .email("john.doe@example.com")
    .phone("123-456-7890")
    .build();
```

---

### 4. Prototype

**Objetivo**: Criar novos objetos copiando uma instância existente (protótipo).

**Quando usar**:
- Criação de objetos é custosa
- Você quer evitar subclasses de um objeto criador

**Exemplo**:
```java
public abstract class Shape implements Cloneable {
    private String id;
    protected String type;
    
    abstract void draw();
    
    @Override
    public Object clone() {
        Object clone = null;
        try {
            clone = super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return clone;
    }
    
    // Getters e Setters
}

public class Circle extends Shape {
    public Circle() {
        type = "Circle";
    }
    
    @Override
    void draw() {
        System.out.println("Drawing a Circle");
    }
}

// Uso
Circle circle1 = new Circle();
Circle circle2 = (Circle) circle1.clone();
```

---

## Padrões Estruturais

### 1. Adapter

**Objetivo**: Converter a interface de uma classe em outra interface esperada pelos clientes.

**Quando usar**:
- Integração de classes incompatíveis
- Usar uma classe existente mas sua interface não corresponde à necessária

**Exemplo**:
```java
// Interface esperada
public interface MediaPlayer {
    void play(String audioType, String fileName);
}

// Interface incompatível
public interface AdvancedMediaPlayer {
    void playVlc(String fileName);
    void playMp4(String fileName);
}

public class VlcPlayer implements AdvancedMediaPlayer {
    @Override
    public void playVlc(String fileName) {
        System.out.println("Playing vlc file: " + fileName);
    }
    
    @Override
    public void playMp4(String fileName) {
        // Não implementado
    }
}

// Adapter
public class MediaAdapter implements MediaPlayer {
    private AdvancedMediaPlayer advancedPlayer;
    
    public MediaAdapter(String audioType) {
        if (audioType.equalsIgnoreCase("vlc")) {
            advancedPlayer = new VlcPlayer();
        }
    }
    
    @Override
    public void play(String audioType, String fileName) {
        if (audioType.equalsIgnoreCase("vlc")) {
            advancedPlayer.playVlc(fileName);
        }
    }
}
```

---

### 2. Decorator

**Objetivo**: Adicionar responsabilidades adicionais a um objeto dinamicamente.

**Quando usar**:
- Adicionar funcionalidades sem alterar a estrutura
- Alternativa flexível à herança

**Exemplo**:
```java
public interface Coffee {
    double getCost();
    String getDescription();
}

public class SimpleCoffee implements Coffee {
    @Override
    public double getCost() {
        return 2.0;
    }
    
    @Override
    public String getDescription() {
        return "Simple coffee";
    }
}

public abstract class CoffeeDecorator implements Coffee {
    protected Coffee decoratedCoffee;
    
    public CoffeeDecorator(Coffee coffee) {
        this.decoratedCoffee = coffee;
    }
    
    @Override
    public double getCost() {
        return decoratedCoffee.getCost();
    }
    
    @Override
    public String getDescription() {
        return decoratedCoffee.getDescription();
    }
}

public class MilkDecorator extends CoffeeDecorator {
    public MilkDecorator(Coffee coffee) {
        super(coffee);
    }
    
    @Override
    public double getCost() {
        return super.getCost() + 0.5;
    }
    
    @Override
    public String getDescription() {
        return super.getDescription() + ", milk";
    }
}

// Uso
Coffee coffee = new SimpleCoffee();
coffee = new MilkDecorator(coffee);
System.out.println(coffee.getDescription() + " $" + coffee.getCost());
```

---

### 3. Facade

**Objetivo**: Fornecer uma interface unificada para um conjunto de interfaces em um subsistema.

**Quando usar**:
- Simplificar interface complexa
- Desacoplar cliente de subsistemas

**Exemplo**:
```java
// Subsistemas complexos
public class CPU {
    public void freeze() { System.out.println("CPU freeze"); }
    public void jump(long position) { System.out.println("CPU jump"); }
    public void execute() { System.out.println("CPU execute"); }
}

public class Memory {
    public void load(long position, byte[] data) {
        System.out.println("Memory load");
    }
}

public class HardDrive {
    public byte[] read(long lba, int size) {
        System.out.println("HardDrive read");
        return new byte[size];
    }
}

// Facade
public class ComputerFacade {
    private CPU cpu;
    private Memory memory;
    private HardDrive hardDrive;
    
    public ComputerFacade() {
        this.cpu = new CPU();
        this.memory = new Memory();
        this.hardDrive = new HardDrive();
    }
    
    public void start() {
        cpu.freeze();
        memory.load(0, hardDrive.read(0, 1024));
        cpu.jump(0);
        cpu.execute();
    }
}

// Uso simplificado
ComputerFacade computer = new ComputerFacade();
computer.start();
```

---

### 4. Proxy

**Objetivo**: Fornecer um substituto ou placeholder para outro objeto para controlar o acesso a ele.

**Quando usar**:
- Lazy initialization
- Controle de acesso
- Logging, caching

**Exemplo**:
```java
public interface Internet {
    void connectTo(String serverHost);
}

public class RealInternet implements Internet {
    @Override
    public void connectTo(String serverHost) {
        System.out.println("Connecting to " + serverHost);
    }
}

public class ProxyInternet implements Internet {
    private RealInternet internet = new RealInternet();
    private static List<String> bannedSites;
    
    static {
        bannedSites = new ArrayList<>();
        bannedSites.add("blocked.com");
    }
    
    @Override
    public void connectTo(String serverHost) {
        if (bannedSites.contains(serverHost.toLowerCase())) {
            System.out.println("Access Denied to " + serverHost);
        } else {
            internet.connectTo(serverHost);
        }
    }
}
```

---

## Padrões Comportamentais

### 1. Strategy

**Objetivo**: Definir uma família de algoritmos, encapsular cada um deles e torná-los intercambiáveis.

**Quando usar**:
- Múltiplas variantes de um algoritmo
- Evitar condicionais complexas

**Exemplo**:
```java
public interface PaymentStrategy {
    void pay(double amount);
}

public class CreditCardStrategy implements PaymentStrategy {
    private String cardNumber;
    
    public CreditCardStrategy(String cardNumber) {
        this.cardNumber = cardNumber;
    }
    
    @Override
    public void pay(double amount) {
        System.out.println("Paid $" + amount + " with credit card " + cardNumber);
    }
}

public class PixStrategy implements PaymentStrategy {
    private String pixKey;
    
    public PixStrategy(String pixKey) {
        this.pixKey = pixKey;
    }
    
    @Override
    public void pay(double amount) {
        System.out.println("Paid R$" + amount + " with PIX key " + pixKey);
    }
}

public class ShoppingCart {
    private PaymentStrategy paymentStrategy;
    
    public void setPaymentStrategy(PaymentStrategy strategy) {
        this.paymentStrategy = strategy;
    }
    
    public void checkout(double amount) {
        paymentStrategy.pay(amount);
    }
}

// Uso
ShoppingCart cart = new ShoppingCart();
cart.setPaymentStrategy(new CreditCardStrategy("1234-5678"));
cart.checkout(100.0);
```

---

### 2. Observer

**Objetivo**: Definir uma dependência um-para-muitos entre objetos de modo que quando um objeto muda de estado, todos os seus dependentes são notificados.

**Quando usar**:
- Event handling
- Notificações
- Publicação/Assinatura

**Exemplo**:
```java
import java.util.ArrayList;
import java.util.List;

public interface Observer {
    void update(String message);
}

public class Subject {
    private List<Observer> observers = new ArrayList<>();
    
    public void attach(Observer observer) {
        observers.add(observer);
    }
    
    public void detach(Observer observer) {
        observers.remove(observer);
    }
    
    public void notifyObservers(String message) {
        for (Observer observer : observers) {
            observer.update(message);
        }
    }
}

public class EmailNotification implements Observer {
    @Override
    public void update(String message) {
        System.out.println("Email notification: " + message);
    }
}

public class SMSNotification implements Observer {
    @Override
    public void update(String message) {
        System.out.println("SMS notification: " + message);
    }
}

// Uso
Subject subject = new Subject();
subject.attach(new EmailNotification());
subject.attach(new SMSNotification());
subject.notifyObservers("New message!");
```

---

### 3. Command

**Objetivo**: Encapsular uma solicitação como um objeto, permitindo parametrizar clientes com diferentes solicitações.

**Quando usar**:
- Desfazer/Refazer operações
- Enfileirar requisições
- Log de operações

**Exemplo**:
```java
public interface Command {
    void execute();
    void undo();
}

public class Light {
    public void turnOn() {
        System.out.println("Light is ON");
    }
    
    public void turnOff() {
        System.out.println("Light is OFF");
    }
}

public class LightOnCommand implements Command {
    private Light light;
    
    public LightOnCommand(Light light) {
        this.light = light;
    }
    
    @Override
    public void execute() {
        light.turnOn();
    }
    
    @Override
    public void undo() {
        light.turnOff();
    }
}

public class RemoteControl {
    private Command command;
    
    public void setCommand(Command command) {
        this.command = command;
    }
    
    public void pressButton() {
        command.execute();
    }
    
    public void pressUndo() {
        command.undo();
    }
}
```

---

### 4. Template Method

**Objetivo**: Definir o esqueleto de um algoritmo em uma operação, postergando alguns passos para as subclasses.

**Quando usar**:
- Algoritmo com passos comuns mas alguns variáveis
- Controlar pontos de extensão

**Exemplo**:
```java
public abstract class DataProcessor {
    
    // Template method
    public final void process() {
        readData();
        processData();
        writeData();
    }
    
    protected abstract void readData();
    protected abstract void processData();
    protected abstract void writeData();
}

public class CSVDataProcessor extends DataProcessor {
    @Override
    protected void readData() {
        System.out.println("Reading CSV data");
    }
    
    @Override
    protected void processData() {
        System.out.println("Processing CSV data");
    }
    
    @Override
    protected void writeData() {
        System.out.println("Writing CSV data");
    }
}

public class JSONDataProcessor extends DataProcessor {
    @Override
    protected void readData() {
        System.out.println("Reading JSON data");
    }
    
    @Override
    protected void processData() {
        System.out.println("Processing JSON data");
    }
    
    @Override
    protected void writeData() {
        System.out.println("Writing JSON data");
    }
}
```

---

## 📊 Comparação Rápida

| Padrão | Categoria | Propósito Principal |
|--------|-----------|---------------------|
| Singleton | Criacional | Uma única instância |
| Factory | Criacional | Criação de objetos |
| Builder | Criacional | Construção complexa |
| Adapter | Estrutural | Compatibilidade de interfaces |
| Decorator | Estrutural | Adicionar responsabilidades |
| Facade | Estrutural | Interface simplificada |
| Proxy | Estrutural | Controle de acesso |
| Strategy | Comportamental | Algoritmos intercambiáveis |
| Observer | Comportamental | Notificação de mudanças |
| Command | Comportamental | Encapsular requisições |
| Template Method | Comportamental | Algoritmo com passos variáveis |

## 🎯 Quando Usar Cada Padrão?

- **Singleton**: Configurações, Logger, Pool de conexões
- **Factory**: Criação de objetos baseada em condições
- **Builder**: Objetos com muitos parâmetros opcionais
- **Adapter**: Integração de APIs incompatíveis
- **Decorator**: Adicionar funcionalidades dinamicamente
- **Facade**: Simplificar sistemas complexos
- **Strategy**: Múltiplos algoritmos para mesma tarefa
- **Observer**: Sistema de eventos/notificações
- **Command**: Implementar undo/redo

## 📚 Recursos Adicionais

- **Livro**: "Design Patterns: Elements of Reusable Object-Oriented Software" - Gang of Four
- **Site**: [Refactoring Guru](https://refactoring.guru/design-patterns)
- **Site**: [Source Making](https://sourcemaking.com/design_patterns)

---

[← Voltar ao Índice Principal](../README.md)
