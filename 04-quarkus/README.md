# Quarkus - Supersonic Subatomic Java

Quarkus é um framework Java nativo para Kubernetes, otimizado para GraalVM e HotSpot, projetado para criar aplicações cloud-native de alto desempenho.

## 📋 Índice

1. [Introdução ao Quarkus](#1-introdução-ao-quarkus)
2. [Setup e Configuração](#2-setup-e-configuração)
3. [RESTful APIs](#3-restful-apis)
4. [Dependency Injection (CDI)](#4-dependency-injection-cdi)
5. [Persistence com Panache](#5-persistence-com-panache)
6. [Reactive Programming](#6-reactive-programming)
7. [Native Image](#7-native-image)
8. [Integração com Kafka](#8-integração-com-kafka)

---

## 1. Introdução ao Quarkus

### O que é Quarkus?

Quarkus é um framework Java projetado para:
- **Container First**: Otimizado para executar em containers
- **Cloud Native**: Preparado para ambientes cloud e Kubernetes
- **Reactive**: Suporte nativo a programação reativa
- **Developer Joy**: Desenvolvimento rápido com live reload

### Vantagens

✅ **Startup ultrarrápido** (milissegundos)  
✅ **Baixo consumo de memória** (RSS ~12MB)  
✅ **Live Reload** durante desenvolvimento  
✅ **Unificação de paradigmas** (imperativo e reativo)  
✅ **Compilação nativa** com GraalVM  
✅ **Amplo ecossistema** de extensões  

### Comparação de Performance

```
┌────────────────┬──────────────┬──────────────┬──────────────┐
│ Framework      │ Startup Time │ Memory (RSS) │ Image Size   │
├────────────────┼──────────────┼──────────────┼──────────────┤
│ Quarkus Native │ 0.016s       │ 12 MB        │ 35 MB        │
│ Quarkus JVM    │ 0.943s       │ 73 MB        │ 140 MB       │
│ Spring Boot    │ 2.5s         │ 150 MB       │ 200 MB       │
└────────────────┴──────────────┴──────────────┴──────────────┘
```

---

## 2. Setup e Configuração

### Criando um Projeto Quarkus

#### Via CLI
```bash
# Instalar Quarkus CLI
curl -Ls https://sh.jbang.dev | bash -s - trust add https://repo1.maven.org/maven2/io/quarkus/quarkus-cli/
curl -Ls https://sh.jbang.dev | bash -s - app install --fresh --force quarkus@quarkusio

# Criar novo projeto
quarkus create app com.example:myapp
cd myapp
```

#### Via Maven
```bash
mvn io.quarkus:quarkus-maven-plugin:3.6.0:create \
    -DprojectGroupId=com.example \
    -DprojectArtifactId=myapp \
    -DclassName="com.example.GreetingResource" \
    -Dpath="/hello"
cd myapp
```

### Estrutura do Projeto
```
myapp/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/example/
│   │   │       └── GreetingResource.java
│   │   └── resources/
│   │       ├── application.properties
│   │       └── META-INF/resources/
│   └── test/
│       └── java/
├── pom.xml
└── README.md
```

### application.properties
```properties
# Configuração básica
quarkus.application.name=myapp
quarkus.http.port=8080

# Database
quarkus.datasource.db-kind=postgresql
quarkus.datasource.username=postgres
quarkus.datasource.password=postgres
quarkus.datasource.jdbc.url=jdbc:postgresql://localhost:5432/mydb

# Hibernate
quarkus.hibernate-orm.database.generation=drop-and-create
quarkus.hibernate-orm.log.sql=true

# Dev Services
quarkus.devservices.enabled=true
```

### Executando a Aplicação

```bash
# Modo desenvolvimento (com live reload)
./mvnw quarkus:dev

# Modo produção
./mvnw clean package
java -jar target/quarkus-app/quarkus-run.jar

# Build nativo
./mvnw clean package -Pnative
./target/myapp-1.0.0-SNAPSHOT-runner
```

---

## 3. RESTful APIs

### Criando um REST Endpoint Básico

```java
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {

    @GET
    public Response getAllUsers() {
        List<User> users = userService.findAll();
        return Response.ok(users).build();
    }

    @GET
    @Path("/{id}")
    public Response getUserById(@PathParam("id") Long id) {
        return userService.findById(id)
            .map(user -> Response.ok(user).build())
            .orElse(Response.status(404).build());
    }

    @POST
    public Response createUser(User user) {
        User created = userService.create(user);
        return Response.status(201).entity(created).build();
    }

    @PUT
    @Path("/{id}")
    public Response updateUser(@PathParam("id") Long id, User user) {
        User updated = userService.update(id, user);
        return Response.ok(updated).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteUser(@PathParam("id") Long id) {
        userService.delete(id);
        return Response.noContent().build();
    }
}
```

### Validação com Bean Validation

```java
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

public class User {
    
    @NotNull
    @Size(min = 2, max = 50)
    private String name;
    
    @Email
    @NotBlank
    private String email;
    
    @Min(18)
    @Max(120)
    private Integer age;
    
    // getters e setters
}

@Path("/api/users")
public class UserResource {
    
    @POST
    public Response createUser(@Valid User user) {
        // Se validação falhar, retorna 400 automaticamente
        User created = userService.create(user);
        return Response.status(201).entity(created).build();
    }
}
```

### Exception Handling

```java
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(Long id) {
        super("User not found: " + id);
    }
}

@Provider
public class UserNotFoundExceptionMapper 
    implements ExceptionMapper<UserNotFoundException> {
    
    @Override
    public Response toResponse(UserNotFoundException exception) {
        ErrorResponse error = new ErrorResponse(
            404,
            exception.getMessage()
        );
        return Response.status(404).entity(error).build();
    }
}
```

---

## 4. Dependency Injection (CDI)

### Conceitos Básicos

Quarkus usa ArC, uma implementação CDI otimizada.

```java
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

// Bean gerenciado pelo CDI
@ApplicationScoped
public class UserService {
    
    @Inject
    UserRepository repository;
    
    public List<User> findAll() {
        return repository.listAll();
    }
    
    public User create(User user) {
        repository.persist(user);
        return user;
    }
}
```

### Escopos

```java
// Singleton - Uma instância para toda aplicação
@ApplicationScoped
public class ConfigService { }

// Request - Uma instância por requisição HTTP
@RequestScoped
public class RequestContextService { }

// Dependent - Nova instância a cada injeção (padrão)
@Dependent
public class HelperService { }

// Singleton - Criado no startup
@Singleton
public class StartupService { }
```

### Qualifiers

```java
// Definindo qualifiers
@Qualifier
@Retention(RUNTIME)
@Target({FIELD, TYPE, METHOD, PARAMETER})
public @interface Primary { }

@Qualifier
@Retention(RUNTIME)
@Target({FIELD, TYPE, METHOD, PARAMETER})
public @interface Secondary { }

// Implementações
@Primary
@ApplicationScoped
public class PrimaryDatabase implements Database { }

@Secondary
@ApplicationScoped
public class SecondaryDatabase implements Database { }

// Injeção com qualifier
@Inject
@Primary
Database primaryDb;

@Inject
@Secondary
Database secondaryDb;
```

### Producers

```java
@ApplicationScoped
public class ResourceProducer {
    
    @Produces
    @ApplicationScoped
    public EntityManager entityManager() {
        return Persistence
            .createEntityManagerFactory("myPU")
            .createEntityManager();
    }
    
    @Produces
    @ApplicationScoped
    public ObjectMapper objectMapper() {
        return new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }
}
```

---

## 5. Persistence com Panache

### Introdução ao Panache

Panache simplifica a camada de persistência, eliminando boilerplate.

#### Dependency
```xml
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-hibernate-orm-panache</artifactId>
</dependency>
```

### Active Record Pattern

```java
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;

@Entity
public class User extends PanacheEntity {
    // id já vem de PanacheEntity
    
    public String name;
    public String email;
    public Integer age;
    
    // Métodos de negócio
    public static List<User> findByName(String name) {
        return find("name", name).list();
    }
    
    public static List<User> findAdults() {
        return find("age >= ?1", 18).list();
    }
    
    public static long deleteByEmail(String email) {
        return delete("email", email);
    }
}

// Uso
@Path("/api/users")
public class UserResource {
    
    @GET
    @Transactional
    public List<User> getAllUsers() {
        return User.listAll();
    }
    
    @POST
    @Transactional
    public Response createUser(User user) {
        user.persist();
        return Response.status(201).entity(user).build();
    }
    
    @GET
    @Path("/adults")
    @Transactional
    public List<User> getAdults() {
        return User.findAdults();
    }
}
```

### Repository Pattern

```java
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@Entity
public class Product {
    @Id
    @GeneratedValue
    public Long id;
    
    public String name;
    public BigDecimal price;
}

@ApplicationScoped
public class ProductRepository implements PanacheRepository<Product> {
    
    public List<Product> findByPriceRange(BigDecimal min, BigDecimal max) {
        return find("price >= ?1 and price <= ?2", min, max).list();
    }
    
    public Product findByName(String name) {
        return find("name", name).firstResult();
    }
}

// Uso
@Path("/api/products")
public class ProductResource {
    
    @Inject
    ProductRepository repository;
    
    @GET
    @Transactional
    public List<Product> getAllProducts() {
        return repository.listAll();
    }
    
    @GET
    @Path("/price-range")
    @Transactional
    public List<Product> getByPriceRange(
            @QueryParam("min") BigDecimal min,
            @QueryParam("max") BigDecimal max) {
        return repository.findByPriceRange(min, max);
    }
}
```

### Consultas Avançadas

```java
// Paginação
List<User> users = User.findAll()
    .page(0, 10)
    .list();

// Ordenação
List<User> users = User.findAll(Sort.by("name").descending())
    .list();

// Projeção
List<String> names = User.find("select name from User").list();

// Count
long count = User.count();
long adultsCount = User.count("age >= ?1", 18);

// Stream
Stream<User> userStream = User.streamAll();

// PanacheQL
List<User> users = User.find("name like ?1", "%John%").list();

// Update em batch
long updated = User.update("status = ?1 where active = ?2", 
                          "INACTIVE", false);
```

---

## 6. Reactive Programming

### Mutiny - Reactive Programming for Quarkus

```java
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.Multi;

@Path("/api/reactive")
public class ReactiveResource {
    
    @Inject
    UserReactiveRepository repository;
    
    // Uni - representa 0 ou 1 item
    @GET
    @Path("/{id}")
    public Uni<User> getUser(@PathParam("id") Long id) {
        return repository.findById(id);
    }
    
    // Multi - representa 0 ou N itens
    @GET
    public Multi<User> getAllUsers() {
        return repository.streamAll();
    }
    
    // Transformações
    @GET
    @Path("/names")
    public Multi<String> getUserNames() {
        return repository.streamAll()
            .map(user -> user.name)
            .filter(name -> name.startsWith("A"));
    }
    
    // Composição
    @POST
    public Uni<Response> createUser(User user) {
        return repository.persist(user)
            .map(saved -> Response.status(201).entity(saved).build())
            .onFailure()
            .recoverWithItem(Response.status(500).build());
    }
}
```

### Panache Reactive

```java
import io.quarkus.hibernate.reactive.panache.PanacheEntity;
import io.quarkus.hibernate.reactive.panache.Panache;

@Entity
public class ReactiveUser extends PanacheEntity {
    public String name;
    public String email;
    
    public static Uni<List<ReactiveUser>> findByName(String name) {
        return find("name", name).list();
    }
}

@Path("/api/reactive/users")
public class ReactiveUserResource {
    
    @GET
    public Uni<List<ReactiveUser>> list() {
        return ReactiveUser.listAll();
    }
    
    @POST
    public Uni<Response> create(ReactiveUser user) {
        return Panache.withTransaction(user::persist)
            .map(inserted -> Response.status(201).entity(inserted).build());
    }
    
    @GET
    @Path("/{id}")
    public Uni<Response> get(@PathParam("id") Long id) {
        return ReactiveUser.findById(id)
            .map(user -> user != null 
                ? Response.ok(user) 
                : Response.status(404))
            .map(Response.ResponseBuilder::build);
    }
}
```

---

## 7. Native Image

### Compilando para Nativo

```bash
# Pré-requisito: GraalVM instalado
./mvnw package -Pnative

# Executar
./target/myapp-1.0.0-SNAPSHOT-runner
```

### Docker Native

```bash
# Build usando container
./mvnw package -Pnative -Dquarkus.native.container-build=true

# Dockerfile multi-stage
```

**Dockerfile.native**:
```dockerfile
FROM quay.io/quarkus/ubi-quarkus-mandrel-builder-image:jdk-21 AS build
COPY --chown=quarkus:quarkus mvnw /code/mvnw
COPY --chown=quarkus:quarkus .mvn /code/.mvn
COPY --chown=quarkus:quarkus pom.xml /code/
USER quarkus
WORKDIR /code
RUN ./mvnw -B org.apache.maven.plugins:maven-dependency-plugin:3.1.2:go-offline
COPY src /code/src
RUN ./mvnw package -Pnative -DskipTests

FROM quay.io/quarkus/quarkus-micro-image:2.0
WORKDIR /work/
COPY --from=build /code/target/*-runner /work/application
RUN chmod 775 /work
EXPOSE 8080
CMD ["./application", "-Dquarkus.http.host=0.0.0.0"]
```

### Configuração para Native

```java
// Reflexão
@RegisterForReflection
public class MyClass {
    // Classe será incluída na imagem nativa
}

// Resources
// application.properties
quarkus.native.resources.includes=config/*.properties,data/*.json
```

---

## 8. Integração com Kafka

Ver seção detalhada em [05-kafka/README.md](../05-kafka/README.md#integração-com-quarkus)

### Exemplo Rápido

```xml
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-smallrye-reactive-messaging-kafka</artifactId>
</dependency>
```

```properties
# application.properties
kafka.bootstrap.servers=localhost:9092

mp.messaging.outgoing.orders.connector=smallrye-kafka
mp.messaging.outgoing.orders.topic=orders
mp.messaging.outgoing.orders.value.serializer=io.quarkus.kafka.client.serialization.ObjectMapperSerializer

mp.messaging.incoming.orders.connector=smallrye-kafka
mp.messaging.incoming.orders.topic=orders
mp.messaging.incoming.orders.value.deserializer=io.quarkus.kafka.client.serialization.ObjectMapperDeserializer
```

```java
@ApplicationScoped
public class OrderService {
    
    @Inject
    @Channel("orders")
    Emitter<Order> orderEmitter;
    
    public void createOrder(Order order) {
        orderEmitter.send(order);
    }
    
    @Incoming("orders")
    public void processOrder(Order order) {
        System.out.println("Processing: " + order);
    }
}
```

---

## 🎯 Melhores Práticas

1. **Use Dev Services**: Deixe Quarkus gerenciar containers de desenvolvimento
2. **Aproveite Live Reload**: Desenvolvimento mais produtivo
3. **Prefira Reactive**: Para maior throughput e melhor uso de recursos
4. **Use Panache**: Reduz boilerplate significativamente
5. **Configure Native Build**: Desde o início se for usar em produção
6. **Monitore com Health**: Use extensões de health e metrics

## 📚 Recursos Adicionais

- **Documentação Oficial**: https://quarkus.io/guides/
- **Quarkiverse**: https://quarkiverse.io (extensões da comunidade)
- **Examples**: https://github.com/quarkusio/quarkus-quickstarts
- **Blog**: https://quarkus.io/blog/

---

[← Voltar ao Índice Principal](../README.md)
