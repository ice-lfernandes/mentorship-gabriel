// Implementação COM Chain of Responsibility
public class ExpenseApprovalChain {
    
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
    
    // Handler abstrato
    public static abstract class ApprovalHandler {
        protected ApprovalHandler nextHandler;
        
        public void setNext(ApprovalHandler handler) {
            this.nextHandler = handler;
        }
        
        public abstract void approve(Expense expense);
    }
    
    // Handlers concretos
    public static class SupervisorHandler extends ApprovalHandler {
        @Override
        public void approve(Expense expense) {
            if (expense.getAmount() <= 1000) {
                System.out.println("Supervisor aprovou: " + expense.getDescription() 
                                 + " - R$" + expense.getAmount());
            } else if (nextHandler != null) {
                nextHandler.approve(expense);
            }
        }
    }
    
    public static class ManagerHandler extends ApprovalHandler {
        @Override
        public void approve(Expense expense) {
            if (expense.getAmount() <= 5000) {
                System.out.println("Gerente aprovou: " + expense.getDescription() 
                                 + " - R$" + expense.getAmount());
            } else if (nextHandler != null) {
                nextHandler.approve(expense);
            }
        }
    }
    
    public static class DirectorHandler extends ApprovalHandler {
        @Override
        public void approve(Expense expense) {
            if (expense.getAmount() <= 10000) {
                System.out.println("Diretor aprovou: " + expense.getDescription() 
                                 + " - R$" + expense.getAmount());
            } else if (nextHandler != null) {
                nextHandler.approve(expense);
            }
        }
    }
    
    public static class PresidentHandler extends ApprovalHandler {
        @Override
        public void approve(Expense expense) {
            System.out.println("Presidente aprovou: " + expense.getDescription() 
                             + " - R$" + expense.getAmount());
        }
    }
    
    public static void main(String[] args) {
        // Montando a cadeia
        ApprovalHandler supervisor = new SupervisorHandler();
        ApprovalHandler manager = new ManagerHandler();
        ApprovalHandler director = new DirectorHandler();
        ApprovalHandler president = new PresidentHandler();
        
        supervisor.setNext(manager);
        manager.setNext(director);
        director.setNext(president);
        
        // Testando
        Expense exp1 = new Expense("Material de escritório", 500);
        Expense exp2 = new Expense("Novo servidor", 8000);
        Expense exp3 = new Expense("Reforma do prédio", 50000);
        
        supervisor.approve(exp1);
        supervisor.approve(exp2);
        supervisor.approve(exp3);
    }
}
