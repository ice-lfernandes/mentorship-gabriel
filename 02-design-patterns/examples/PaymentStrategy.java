package examples.patterns.strategy;

/**
 * Exemplo completo do Pattern Strategy
 * Cenário: Sistema de pagamento com diferentes métodos
 */

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

// Strategy Interface
interface PaymentStrategy {
    PaymentResult processPayment(BigDecimal amount);
    String getPaymentMethodName();
}

// Concrete Strategies
class CreditCardPaymentStrategy implements PaymentStrategy {
    private String cardNumber;
    private String cardHolder;
    private String cvv;
    private String expiryDate;
    
    public CreditCardPaymentStrategy(String cardNumber, String cardHolder, 
                                    String cvv, String expiryDate) {
        this.cardNumber = cardNumber;
        this.cardHolder = cardHolder;
        this.cvv = cvv;
        this.expiryDate = expiryDate;
    }
    
    @Override
    public PaymentResult processPayment(BigDecimal amount) {
        // Simular validação e processamento
        System.out.println("Processing credit card payment...");
        System.out.println("Card: **** **** **** " + cardNumber.substring(12));
        System.out.println("Amount: R$ " + amount);
        
        // Simular aprovação
        return new PaymentResult(
            true,
            "CC-" + System.currentTimeMillis(),
            "Pagamento aprovado",
            LocalDateTime.now()
        );
    }
    
    @Override
    public String getPaymentMethodName() {
        return "Cartão de Crédito";
    }
}

class PixPaymentStrategy implements PaymentStrategy {
    private String pixKey;
    
    public PixPaymentStrategy(String pixKey) {
        this.pixKey = pixKey;
    }
    
    @Override
    public PaymentResult processPayment(BigDecimal amount) {
        System.out.println("Processing PIX payment...");
        System.out.println("PIX Key: " + pixKey);
        System.out.println("Amount: R$ " + amount);
        
        // Gerar QR Code (simulado)
        String qrCode = "00020126580014BR.GOV.BCB.PIX0136" + pixKey;
        System.out.println("QR Code: " + qrCode);
        
        return new PaymentResult(
            true,
            "PIX-" + System.currentTimeMillis(),
            "Pagamento PIX processado",
            LocalDateTime.now()
        );
    }
    
    @Override
    public String getPaymentMethodName() {
        return "PIX";
    }
}

class PayPalPaymentStrategy implements PaymentStrategy {
    private String email;
    private String password;
    
    public PayPalPaymentStrategy(String email, String password) {
        this.email = email;
        this.password = password;
    }
    
    @Override
    public PaymentResult processPayment(BigDecimal amount) {
        System.out.println("Processing PayPal payment...");
        System.out.println("PayPal account: " + email);
        System.out.println("Amount: $ " + amount);
        
        return new PaymentResult(
            true,
            "PP-" + System.currentTimeMillis(),
            "PayPal payment approved",
            LocalDateTime.now()
        );
    }
    
    @Override
    public String getPaymentMethodName() {
        return "PayPal";
    }
}

class BoletoPaymentStrategy implements PaymentStrategy {
    private String cpf;
    
    public BoletoPaymentStrategy(String cpf) {
        this.cpf = cpf;
    }
    
    @Override
    public PaymentResult processPayment(BigDecimal amount) {
        System.out.println("Generating Boleto...");
        System.out.println("CPF: " + cpf);
        System.out.println("Amount: R$ " + amount);
        
        String boletoCode = "34191.79001 01043.510047 91020.150008 1 84380000010000";
        System.out.println("Código de barras: " + boletoCode);
        System.out.println("Vencimento: 3 dias úteis");
        
        return new PaymentResult(
            true,
            "BOL-" + System.currentTimeMillis(),
            "Boleto gerado com sucesso",
            LocalDateTime.now()
        );
    }
    
    @Override
    public String getPaymentMethodName() {
        return "Boleto Bancário";
    }
}

// Resultado do pagamento
class PaymentResult {
    private boolean success;
    private String transactionId;
    private String message;
    private LocalDateTime timestamp;
    
    public PaymentResult(boolean success, String transactionId, 
                        String message, LocalDateTime timestamp) {
        this.success = success;
        this.transactionId = transactionId;
        this.message = message;
        this.timestamp = timestamp;
    }
    
    // Getters
    public boolean isSuccess() { return success; }
    public String getTransactionId() { return transactionId; }
    public String getMessage() { return message; }
    public LocalDateTime getTimestamp() { return timestamp; }
}

// Context - Carrinho de compras
class ShoppingCart {
    private Map<String, BigDecimal> items = new HashMap<>();
    private PaymentStrategy paymentStrategy;
    
    public void addItem(String item, BigDecimal price) {
        items.put(item, price);
        System.out.println("Item adicionado: " + item + " - R$ " + price);
    }
    
    public void removeItem(String item) {
        items.remove(item);
        System.out.println("Item removido: " + item);
    }
    
    public BigDecimal getTotal() {
        return items.values().stream()
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    public void setPaymentStrategy(PaymentStrategy paymentStrategy) {
        this.paymentStrategy = paymentStrategy;
    }
    
    public PaymentResult checkout() {
        if (paymentStrategy == null) {
            throw new IllegalStateException("Payment method not selected");
        }
        
        BigDecimal total = getTotal();
        System.out.println("\n=== CHECKOUT ===");
        System.out.println("Total: R$ " + total);
        System.out.println("Método de pagamento: " + paymentStrategy.getPaymentMethodName());
        System.out.println("================\n");
        
        PaymentResult result = paymentStrategy.processPayment(total);
        
        if (result.isSuccess()) {
            items.clear(); // Limpar carrinho após pagamento
        }
        
        return result;
    }
}

// Demonstração
class PaymentStrategyDemo {
    public static void main(String[] args) {
        ShoppingCart cart = new ShoppingCart();
        
        // Adicionar itens
        cart.addItem("Notebook", new BigDecimal("3500.00"));
        cart.addItem("Mouse", new BigDecimal("50.00"));
        cart.addItem("Teclado", new BigDecimal("200.00"));
        
        System.out.println("\n--- Opção 1: Pagamento com Cartão de Crédito ---");
        cart.setPaymentStrategy(new CreditCardPaymentStrategy(
            "1234567890123456",
            "Gabriel Silva",
            "123",
            "12/25"
        ));
        PaymentResult result1 = cart.checkout();
        System.out.println("Resultado: " + result1.getMessage());
        System.out.println("ID da transação: " + result1.getTransactionId());
        
        // Novo carrinho
        ShoppingCart cart2 = new ShoppingCart();
        cart2.addItem("Curso de Java", new BigDecimal("500.00"));
        
        System.out.println("\n--- Opção 2: Pagamento com PIX ---");
        cart2.setPaymentStrategy(new PixPaymentStrategy("gabriel@example.com"));
        PaymentResult result2 = cart2.checkout();
        System.out.println("Resultado: " + result2.getMessage());
        
        // Novo carrinho
        ShoppingCart cart3 = new ShoppingCart();
        cart3.addItem("Monitor", new BigDecimal("1200.00"));
        
        System.out.println("\n--- Opção 3: Pagamento com Boleto ---");
        cart3.setPaymentStrategy(new BoletoPaymentStrategy("123.456.789-00"));
        PaymentResult result3 = cart3.checkout();
        System.out.println("Resultado: " + result3.getMessage());
        
        // Novo carrinho
        ShoppingCart cart4 = new ShoppingCart();
        cart4.addItem("Livro Clean Code", new BigDecimal("80.00"));
        
        System.out.println("\n--- Opção 4: Pagamento com PayPal ---");
        cart4.setPaymentStrategy(new PayPalPaymentStrategy(
            "gabriel@paypal.com",
            "password"
        ));
        PaymentResult result4 = cart4.checkout();
        System.out.println("Resultado: " + result4.getMessage());
    }
}
