// Implementação COM Template Method
public class DocumentProcessorTemplate {
    
    // Classe abstrata com o Template Method
    public static abstract class DocumentProcessor {
        
        // Template Method - define o esqueleto do algoritmo
        public final void processDocument(String filename) {
            openDocument(filename);
            validateFormat();
            extractContent();
            processContent();
            performSpecificOperations();
            saveResult();
            closeDocument();
            System.out.println();
        }
        
        // Métodos concretos (comuns a todos)
        private void openDocument(String filename) {
            System.out.println("Abrindo documento: " + filename);
        }
        
        private void closeDocument() {
            System.out.println("Fechando documento");
        }
        
        private void saveResult() {
            System.out.println("Salvando resultado...");
        }
        
        // Métodos abstratos (específicos de cada tipo)
        protected abstract void validateFormat();
        protected abstract void extractContent();
        protected abstract void processContent();
        
        // Hook method - pode ser sobrescrito opcionalmente
        protected void performSpecificOperations() {
            // Implementação padrão vazia
        }
    }
    
    // Processador de PDF
    public static class PdfProcessor extends DocumentProcessor {
        @Override
        protected void validateFormat() {
            System.out.println("Validando formato PDF...");
        }
        
        @Override
        protected void extractContent() {
            System.out.println("Extraindo texto do PDF...");
        }
        
        @Override
        protected void processContent() {
            System.out.println("Processando conteúdo PDF...");
        }
    }
    
    // Processador de Word
    public static class WordProcessor extends DocumentProcessor {
        @Override
        protected void validateFormat() {
            System.out.println("Validando formato DOCX...");
        }
        
        @Override
        protected void extractContent() {
            System.out.println("Extraindo texto do Word...");
        }
        
        @Override
        protected void processContent() {
            System.out.println("Processando conteúdo Word...");
        }
        
        @Override
        protected void performSpecificOperations() {
            System.out.println("Aplicando estilos do Word...");
        }
    }
    
    // Processador de Excel
    public static class ExcelProcessor extends DocumentProcessor {
        @Override
        protected void validateFormat() {
            System.out.println("Validando formato XLSX...");
        }
        
        @Override
        protected void extractContent() {
            System.out.println("Extraindo dados das planilhas...");
        }
        
        @Override
        protected void processContent() {
            System.out.println("Processando tabelas do Excel...");
        }
        
        @Override
        protected void performSpecificOperations() {
            System.out.println("Calculando fórmulas...");
        }
    }
    
    public static void main(String[] args) {
        DocumentProcessor pdfProcessor = new PdfProcessor();
        DocumentProcessor wordProcessor = new WordProcessor();
        DocumentProcessor excelProcessor = new ExcelProcessor();
        
        pdfProcessor.processDocument("relatorio.pdf");
        wordProcessor.processDocument("proposta.docx");
        excelProcessor.processDocument("planilha.xlsx");
    }
}
