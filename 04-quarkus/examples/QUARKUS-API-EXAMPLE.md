# Exemplo Prático: API REST com Quarkus

Este exemplo demonstra como criar uma API REST completa com Quarkus, aplicando SOLID e design patterns.

## Estrutura do Projeto

```
quarkus-api-example/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/example/
│   │   │       ├── domain/
│   │   │       │   ├── Product.java
│   │   │       │   └── repository/
│   │   │       │       └── ProductRepository.java
│   │   │       ├── application/
│   │   │       │   ├── service/
│   │   │       │   │   └── ProductService.java
│   │   │       │   └── dto/
│   │   │       │       ├── ProductRequest.java
│   │   │       │       └── ProductResponse.java
│   │   │       └── infrastructure/
│   │   │           ├── web/
│   │   │           │   └── ProductResource.java
│   │   │           └── persistence/
│   │   │               └── ProductRepositoryImpl.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/
│           └── com/example/
│               └── ProductResourceTest.java
└── pom.xml
```

## 1. Entidade de Domínio

```java
package com.example.domain;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

@Entity
public class Product extends PanacheEntity {
    
    @NotBlank(message = "Nome é obrigatório")
    public String name;
    
    @NotBlank(message = "Descrição é obrigatória")
    public String description;
    
    @Positive(message = "Preço deve ser positivo")
    public BigDecimal price;
    
    public Integer quantity;
    
    // Construtores
    public Product() {}
    
    public Product(String name, String description, BigDecimal price, Integer quantity) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
    }
    
    // Métodos de negócio
    public boolean isAvailable() {
        return quantity != null && quantity > 0;
    }
    
    public void decreaseQuantity(int amount) {
        if (quantity < amount) {
            throw new IllegalStateException("Quantidade insuficiente em estoque");
        }
        this.quantity -= amount;
    }
    
    public void increaseQuantity(int amount) {
        this.quantity += amount;
    }
}
```

## 2. Repository (Abstração)

```java
package com.example.domain.repository;

import com.example.domain.Product;
import java.util.List;
import java.util.Optional;

// Interface - seguindo DIP (Dependency Inversion Principle)
public interface ProductRepository {
    List<Product> findAll();
    Optional<Product> findById(Long id);
    List<Product> findByName(String name);
    List<Product> findAvailable();
    Product save(Product product);
    void delete(Long id);
}
```

## 3. Repository (Implementação)

```java
package com.example.infrastructure.persistence;

import com.example.domain.Product;
import com.example.domain.repository.ProductRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class ProductRepositoryImpl implements ProductRepository {
    
    @Override
    public List<Product> findAll() {
        return Product.listAll();
    }
    
    @Override
    public Optional<Product> findById(Long id) {
        return Product.findByIdOptional(id);
    }
    
    @Override
    public List<Product> findByName(String name) {
        return Product.list("LOWER(name) LIKE LOWER(?1)", "%" + name + "%");
    }
    
    @Override
    public List<Product> findAvailable() {
        return Product.list("quantity > 0");
    }
    
    @Override
    @Transactional
    public Product save(Product product) {
        product.persist();
        return product;
    }
    
    @Override
    @Transactional
    public void delete(Long id) {
        Product.deleteById(id);
    }
}
```

## 4. DTOs (Data Transfer Objects)

```java
package com.example.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public class ProductRequest {
    
    @NotBlank(message = "Nome é obrigatório")
    public String name;
    
    @NotBlank(message = "Descrição é obrigatória")
    public String description;
    
    @Positive(message = "Preço deve ser positivo")
    public BigDecimal price;
    
    @Positive(message = "Quantidade deve ser positiva")
    public Integer quantity;
    
    // Getters e Setters
}
```

```java
package com.example.application.dto;

import java.math.BigDecimal;

public class ProductResponse {
    public Long id;
    public String name;
    public String description;
    public BigDecimal price;
    public Integer quantity;
    public boolean available;
    
    // Método estático para converter de Product para ProductResponse
    public static ProductResponse from(com.example.domain.Product product) {
        ProductResponse response = new ProductResponse();
        response.id = product.id;
        response.name = product.name;
        response.description = product.description;
        response.price = product.price;
        response.quantity = product.quantity;
        response.available = product.isAvailable();
        return response;
    }
}
```

## 5. Service (Lógica de Negócio)

```java
package com.example.application.service;

import com.example.application.dto.ProductRequest;
import com.example.application.dto.ProductResponse;
import com.example.domain.Product;
import com.example.domain.repository.ProductRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class ProductService {
    
    @Inject
    ProductRepository productRepository;
    
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll()
            .stream()
            .map(ProductResponse::from)
            .collect(Collectors.toList());
    }
    
    public ProductResponse getProductById(Long id) {
        return productRepository.findById(id)
            .map(ProductResponse::from)
            .orElseThrow(() -> new ProductNotFoundException("Product not found: " + id));
    }
    
    public List<ProductResponse> searchByName(String name) {
        return productRepository.findByName(name)
            .stream()
            .map(ProductResponse::from)
            .collect(Collectors.toList());
    }
    
    public List<ProductResponse> getAvailableProducts() {
        return productRepository.findAvailable()
            .stream()
            .map(ProductResponse::from)
            .collect(Collectors.toList());
    }
    
    public ProductResponse createProduct(ProductRequest request) {
        Product product = new Product(
            request.name,
            request.description,
            request.price,
            request.quantity
        );
        
        Product saved = productRepository.save(product);
        return ProductResponse.from(saved);
    }
    
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ProductNotFoundException("Product not found: " + id));
        
        product.name = request.name;
        product.description = request.description;
        product.price = request.price;
        product.quantity = request.quantity;
        
        Product updated = productRepository.save(product);
        return ProductResponse.from(updated);
    }
    
    public void deleteProduct(Long id) {
        if (!productRepository.findById(id).isPresent()) {
            throw new ProductNotFoundException("Product not found: " + id);
        }
        productRepository.delete(id);
    }
}

class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(String message) {
        super(message);
    }
}
```

## 6. REST Resource (Controller)

```java
package com.example.infrastructure.web;

import com.example.application.dto.ProductRequest;
import com.example.application.dto.ProductResponse;
import com.example.application.service.ProductService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/api/products")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProductResource {
    
    @Inject
    ProductService productService;
    
    @GET
    public Response getAllProducts(@QueryParam("name") String name) {
        if (name != null && !name.isEmpty()) {
            List<ProductResponse> products = productService.searchByName(name);
            return Response.ok(products).build();
        }
        
        List<ProductResponse> products = productService.getAllProducts();
        return Response.ok(products).build();
    }
    
    @GET
    @Path("/{id}")
    public Response getProductById(@PathParam("id") Long id) {
        ProductResponse product = productService.getProductById(id);
        return Response.ok(product).build();
    }
    
    @GET
    @Path("/available")
    public Response getAvailableProducts() {
        List<ProductResponse> products = productService.getAvailableProducts();
        return Response.ok(products).build();
    }
    
    @POST
    public Response createProduct(@Valid ProductRequest request) {
        ProductResponse product = productService.createProduct(request);
        return Response.status(Response.Status.CREATED).entity(product).build();
    }
    
    @PUT
    @Path("/{id}")
    public Response updateProduct(@PathParam("id") Long id, 
                                  @Valid ProductRequest request) {
        ProductResponse product = productService.updateProduct(id, request);
        return Response.ok(product).build();
    }
    
    @DELETE
    @Path("/{id}")
    public Response deleteProduct(@PathParam("id") Long id) {
        productService.deleteProduct(id);
        return Response.noContent().build();
    }
}
```

## 7. Exception Handler

```java
package com.example.infrastructure.web;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class ProductNotFoundExceptionMapper 
    implements ExceptionMapper<ProductNotFoundException> {
    
    @Override
    public Response toResponse(ProductNotFoundException exception) {
        ErrorResponse error = new ErrorResponse(
            404,
            "Not Found",
            exception.getMessage()
        );
        return Response.status(404).entity(error).build();
    }
}

class ErrorResponse {
    public int status;
    public String error;
    public String message;
    
    public ErrorResponse(int status, String error, String message) {
        this.status = status;
        this.error = error;
        this.message = message;
    }
}
```

## 8. application.properties

```properties
# Database
quarkus.datasource.db-kind=postgresql
quarkus.datasource.username=postgres
quarkus.datasource.password=postgres
quarkus.datasource.jdbc.url=jdbc:postgresql://localhost:5432/products_db

# Hibernate
# WARNING: Only use 'drop-and-create' in DEVELOPMENT!
# For production, use 'validate' or 'none'
quarkus.hibernate-orm.database.generation=drop-and-create
quarkus.hibernate-orm.log.sql=true

# HTTP
quarkus.http.port=8080

# Dev Services (banco de dados automático em dev)
quarkus.devservices.enabled=true

# Production profile example
%prod.quarkus.hibernate-orm.database.generation=validate
%prod.quarkus.hibernate-orm.log.sql=false
```

## 9. Teste

```java
package com.example;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThan;

@QuarkusTest
public class ProductResourceTest {
    
    @Test
    public void testGetAllProducts() {
        given()
            .when().get("/api/products")
            .then()
            .statusCode(200)
            .contentType(ContentType.JSON);
    }
    
    @Test
    public void testCreateProduct() {
        given()
            .contentType(ContentType.JSON)
            .body("{\"name\":\"Notebook\",\"description\":\"Dell Inspiron\",\"price\":3000.00,\"quantity\":10}")
            .when().post("/api/products")
            .then()
            .statusCode(201)
            .body("name", is("Notebook"))
            .body("id", greaterThan(0));
    }
}
```

## Como Executar

```bash
# Modo desenvolvimento (com live reload)
./mvnw quarkus:dev

# A API estará disponível em http://localhost:8080
# Swagger UI: http://localhost:8080/q/swagger-ui
```

## Endpoints Disponíveis

- `GET /api/products` - Lista todos os produtos
- `GET /api/products?name=notebook` - Busca por nome
- `GET /api/products/{id}` - Busca produto por ID
- `GET /api/products/available` - Lista produtos disponíveis
- `POST /api/products` - Cria novo produto
- `PUT /api/products/{id}` - Atualiza produto
- `DELETE /api/products/{id}` - Deleta produto

## Princípios Aplicados

✅ **SRP**: Cada classe tem uma responsabilidade única  
✅ **OCP**: Fácil adicionar novos repositórios ou services  
✅ **LSP**: Implementações podem ser substituídas  
✅ **ISP**: Interfaces específicas  
✅ **DIP**: Dependências em abstrações, não implementações  

---

[← Voltar](../README.md)
