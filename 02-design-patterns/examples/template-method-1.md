# Exercício 2: Template Method
### Cenário: Sistema de processamento de diferentes tipos de documentos (PDF, Word, Excel) que seguem etapas semelhantes mas com implementações específicas.

```java
// Implementação SEM Template Method
public class DocumentProcessor {
    
    public static void processPdfDocument(String filename) {
        // Código duplicado com pequenas variações
        System.out.println("Abrindo documento PDF: " + filename);
        System.out.println("Validando formato PDF...");
        System.out.println("Extraindo texto do PDF...");
        System.out.println("Processando conteúdo PDF...");
        System.out.println("Salvando resultado do PDF...");
        System.out.println("Fechando documento PDF");
        System.out.println();
    }
    
    public static void processWordDocument(String filename) {
        // Código duplicado com pequenas variações
        System.out.println("Abrindo documento Word: " + filename);
        System.out.println("Validando formato DOCX...");
        System.out.println("Extraindo texto do Word...");
        System.out.println("Processando conteúdo Word...");
        System.out.println("Aplicando estilos do Word...");
        System.out.println("Salvando resultado do Word...");
        System.out.println("Fechando documento Word");
        System.out.println();
    }
    
    public static void processExcelDocument(String filename) {
        // Código duplicado com pequenas variações
        System.out.println("Abrindo documento Excel: " + filename);
        System.out.println("Validando formato XLSX...");
        System.out.println("Extraindo dados das planilhas...");
        System.out.println("Processando tabelas do Excel...");
        System.out.println("Calculando fórmulas...");
        System.out.println("Salvando resultado do Excel...");
        System.out.println("Fechando documento Excel");
        System.out.println();
    }
    
    public static void main(String[] args) {
        processPdfDocument("relatorio.pdf");
        processWordDocument("proposta.docx");
        processExcelDocument("planilha.xlsx");
    }
}
```
