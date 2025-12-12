package examples.solid.srp;

/**
 * Exemplo prático de aplicação dos princípios SOLID
 * Demonstra como refatorar código que viola princípios SOLID
 */

// ❌ ANTES: Violação de múltiplos princípios SOLID
class UserServiceBad {
    public void registerUser(String name, String email, String password) {
        // Validação (responsabilidade extra)
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Nome inválido");
        }
        if (!email.contains("@")) {
            throw new IllegalArgumentException("Email inválido");
        }
        
        // Persistência direta (viola SRP e DIP)
        String sql = "INSERT INTO users (name, email, password) VALUES (?, ?, ?)";
        // executar SQL diretamente...
        
        // Envio de email (responsabilidade extra)
        String emailBody = "Bem-vindo " + name;
        // enviar email...
        
        // Logging (responsabilidade extra)
        System.out.println("Usuário registrado: " + email);
    }
}

// ✅ DEPOIS: Aplicando SOLID

// SRP: Cada classe tem uma única responsabilidade
class UserValidator {
    public void validate(User user) {
        if (user.getName() == null || user.getName().isEmpty()) {
            throw new IllegalArgumentException("Nome inválido");
        }
        if (!user.getEmail().contains("@")) {
            throw new IllegalArgumentException("Email inválido");
        }
    }
}

// DIP: Interface em vez de implementação concreta
interface UserRepository {
    void save(User user);
    User findByEmail(String email);
}

// Implementação específica
class DatabaseUserRepository implements UserRepository {
    @Override
    public void save(User user) {
        // Implementação de persistência em banco de dados
        System.out.println("Salvando usuário no banco: " + user.getEmail());
    }
    
    @Override
    public User findByEmail(String email) {
        // Buscar usuário no banco
        return null; // Implementação simplificada
    }
}

// SRP: Responsabilidade de notificação separada
interface NotificationService {
    void sendWelcomeEmail(User user);
}

class EmailNotificationService implements NotificationService {
    @Override
    public void sendWelcomeEmail(User user) {
        System.out.println("Enviando email de boas-vindas para: " + user.getEmail());
    }
}

// SRP: Responsabilidade de auditoria separada
interface AuditLogger {
    void logUserRegistration(User user);
}

class ConsoleAuditLogger implements AuditLogger {
    @Override
    public void logUserRegistration(User user) {
        System.out.println("LOG: Usuário registrado - " + user.getEmail());
    }
}

// OCP e DIP: Serviço depende de abstrações
class UserService {
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final AuditLogger auditLogger;
    private final UserValidator userValidator;
    
    // DIP: Dependências injetadas via construtor
    public UserService(
            UserRepository userRepository,
            NotificationService notificationService,
            AuditLogger auditLogger,
            UserValidator userValidator) {
        this.userRepository = userRepository;
        this.notificationService = notificationService;
        this.auditLogger = auditLogger;
        this.userValidator = userValidator;
    }
    
    public void registerUser(User user) {
        // Validar
        userValidator.validate(user);
        
        // Salvar
        userRepository.save(user);
        
        // Notificar
        notificationService.sendWelcomeEmail(user);
        
        // Auditar
        auditLogger.logUserRegistration(user);
    }
}

// Entidade de domínio
class User {
    private Long id;
    private String name;
    private String email;
    private String password;
    
    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }
    
    // Getters e setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
}

// Exemplo de uso
class Main {
    public static void main(String[] args) {
        // Criar dependências
        UserRepository repository = new DatabaseUserRepository();
        NotificationService notificationService = new EmailNotificationService();
        AuditLogger auditLogger = new ConsoleAuditLogger();
        UserValidator validator = new UserValidator();
        
        // Criar serviço com dependências injetadas
        UserService userService = new UserService(
            repository,
            notificationService,
            auditLogger,
            validator
        );
        
        // Usar o serviço
        User user = new User("Gabriel", "gabriel@example.com", "password123");
        userService.registerUser(user);
    }
}
