# Exercício 1: Chain of Responsibility

### Cenário: Sistema de aprovação de despesas corporativas que precisa passar por diferentes níveis de aprovação dependendo do valor.

```java
// Implementação SEM Chain of Responsibility
public class ExpenseApprovalSystem {
    
    public static class Expense {
        private String description;
        private double amount;
        
        public Expense(String description, double amount) {
            this.description = description;
            this.amount = amount;
        }
        
        public String getDescription() { return description; }
        public double getAmount() { return amount; }
    }
    
    public static void approveExpense(Expense expense) {
        // Lógica totalmente acoplada com múltiplos IFs
        if (expense.getAmount() <= 1000) {
            System.out.println("Supervisor aprovou: " + expense.getDescription() 
                             + " - R$" + expense.getAmount());
        } else if (expense.getAmount() > 1000 && expense.getAmount() <= 5000) {
            System.out.println("Gerente aprovou: " + expense.getDescription() 
                             + " - R$" + expense.getAmount());
        } else if (expense.getAmount() > 5000 && expense.getAmount() <= 10000) {
            System.out.println("Diretor aprovou: " + expense.getDescription() 
                             + " - R$" + expense.getAmount());
        } else {
            System.out.println("Presidente aprovou: " + expense.getDescription() 
                             + " - R$" + expense.getAmount());
        }
    }
    
    public static void main(String[] args) {
        Expense exp1 = new Expense("Material de escritório", 500);
        Expense exp2 = new Expense("Novo servidor", 8000);
        Expense exp3 = new Expense("Reforma do prédio", 50000);
        
        approveExpense(exp1);
        approveExpense(exp2);
        approveExpense(exp3);
    }
}
```
