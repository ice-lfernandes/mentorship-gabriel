# Apache Kafka

Apache Kafka é uma plataforma de streaming distribuído usada para construir pipelines de dados em tempo real e aplicações de streaming.

## 📋 Índice

1. [Conceitos Fundamentais](#1-conceitos-fundamentais)
2. [Arquitetura do Kafka](#2-arquitetura-do-kafka)
3. [Producers](#3-producers)
4. [Consumers](#4-consumers)
5. [Topics e Partitions](#5-topics-e-partitions)
6. [Consumer Groups](#6-consumer-groups)
7. [Kafka Streams](#7-kafka-streams)
8. [Integração com Quarkus](#8-integração-com-quarkus)

---

## 1. Conceitos Fundamentais

### O que é Kafka?

Apache Kafka é uma plataforma de streaming de eventos distribuída capaz de:
- **Publicar e subscrever** streams de eventos
- **Armazenar** streams de eventos de forma durável e confiável
- **Processar** streams de eventos em tempo real

### Casos de Uso

✅ **Mensageria**: Comunicação entre microservices  
✅ **Event Sourcing**: Armazenar estado como sequência de eventos  
✅ **Stream Processing**: Processar dados em tempo real  
✅ **Log Aggregation**: Centralizar logs de múltiplos serviços  
✅ **Data Integration**: Pipeline de dados entre sistemas  
✅ **Metrics & Monitoring**: Coletar e processar métricas  

### Características Principais

- **Alta throughput**: Milhões de mensagens/segundo
- **Baixa latência**: Milissegundos
- **Escalável**: Horizontal scaling
- **Durável**: Replicação e persistência
- **Distribuído**: Tolerância a falhas

---

## 2. Arquitetura do Kafka

### Componentes Principais

```
┌─────────────────────────────────────────────────────┐
│                    Producers                        │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐          │
│  │  App 1   │  │  App 2   │  │  App 3   │          │
│  └────┬─────┘  └────┬─────┘  └────┬─────┘          │
└───────┼─────────────┼─────────────┼────────────────┘
        │             │             │
        ▼             ▼             ▼
┌─────────────────────────────────────────────────────┐
│              Kafka Cluster                          │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐          │
│  │ Broker 1 │  │ Broker 2 │  │ Broker 3 │          │
│  │ Topics   │  │ Topics   │  │ Topics   │          │
│  │ Partitions│ │Partitions│  │Partitions│          │
│  └──────────┘  └──────────┘  └──────────┘          │
│                                                     │
│  ┌──────────────────────────────────────┐          │
│  │         ZooKeeper / KRaft            │          │
│  └──────────────────────────────────────┘          │
└─────────────────────────────────────────────────────┘
        │             │             │
        ▼             ▼             ▼
┌─────────────────────────────────────────────────────┐
│                   Consumers                         │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐          │
│  │  App A   │  │  App B   │  │  App C   │          │
│  └──────────┘  └──────────┘  └──────────┘          │
└─────────────────────────────────────────────────────┘
```

### Broker
- Servidor Kafka que armazena e serve dados
- Cluster possui múltiplos brokers para redundância
- Cada broker pode conter múltiplas partições de diferentes topics

### ZooKeeper / KRaft
- **ZooKeeper** (Legacy): Coordenação e metadados
- **KRaft** (Novo): Kafka Raft - modo sem ZooKeeper

---

## 3. Producers

### Conceito
Producer é uma aplicação que publica (envia) eventos para Kafka topics.

### Producer Básico em Java

```java
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

public class SimpleProducer {
    
    public static void main(String[] args) {
        // Configuração do Producer
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.RETRIES_CONFIG, 3);
        
        // Criar Producer
        KafkaProducer<String, String> producer = new KafkaProducer<>(props);
        
        try {
            // Enviar mensagem
            ProducerRecord<String, String> record = 
                new ProducerRecord<>("orders", "order-123", "Order details...");
            
            // Envio síncrono
            RecordMetadata metadata = producer.send(record).get();
            System.out.println("Sent to partition: " + metadata.partition() + 
                             ", offset: " + metadata.offset());
            
            // Envio assíncrono com callback
            producer.send(record, new Callback() {
                @Override
                public void onCompletion(RecordMetadata metadata, Exception exception) {
                    if (exception == null) {
                        System.out.println("Success! Partition: " + metadata.partition() + 
                                         ", Offset: " + metadata.offset());
                    } else {
                        exception.printStackTrace();
                    }
                }
            });
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            producer.close();
        }
    }
}
```

### Configurações Importantes

```java
// Confirmação de escrita
props.put(ProducerConfig.ACKS_CONFIG, "all");
// 0 = sem confirmação (rápido, pode perder mensagens)
// 1 = confirmação do líder (balanço)
// all/-1 = confirmação de todas as réplicas (seguro)

// Retentativas
props.put(ProducerConfig.RETRIES_CONFIG, 3);

// Batch
props.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
props.put(ProducerConfig.LINGER_MS_CONFIG, 10);

// Compressão
props.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy");
// Opções: none, gzip, snappy, lz4, zstd

// Idempotência (evita duplicatas)
props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
```

### Serialização Customizada

```java
public class Order {
    private String orderId;
    private BigDecimal amount;
    // getters, setters, constructors
}

public class OrderSerializer implements Serializer<Order> {
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    public byte[] serialize(String topic, Order order) {
        try {
            return objectMapper.writeValueAsBytes(order);
        } catch (Exception e) {
            throw new SerializationException("Error serializing Order", e);
        }
    }
}

// Uso
props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, OrderSerializer.class.getName());
```

---

## 4. Consumers

### Conceito
Consumer é uma aplicação que lê (consome) eventos de Kafka topics.

### Consumer Básico em Java

```java
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.*;

public class SimpleConsumer {
    
    public static void main(String[] args) {
        // Configuração do Consumer
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "order-processor-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        
        // Criar Consumer
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        
        // Subscrever topic
        consumer.subscribe(Collections.singletonList("orders"));
        
        try {
            while (true) {
                // Poll por mensagens
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
                
                for (ConsumerRecord<String, String> record : records) {
                    System.out.printf("Received: key=%s, value=%s, partition=%d, offset=%d%n",
                        record.key(), record.value(), record.partition(), record.offset());
                    
                    // Processar mensagem
                    processOrder(record.value());
                }
                
                // Commit manual
                consumer.commitSync();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            consumer.close();
        }
    }
    
    private static void processOrder(String order) {
        // Lógica de processamento
        System.out.println("Processing order: " + order);
    }
}
```

### Configurações Importantes

```java
// Grupo de consumidores
props.put(ConsumerConfig.GROUP_ID_CONFIG, "my-consumer-group");

// Onde começar a ler
props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
// earliest = do início
// latest = apenas novas mensagens
// none = erro se não houver offset

// Commit automático
props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "5000");

// Max records por poll
props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "500");

// Session timeout
props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "10000");
```

### Commit de Offset

```java
// Auto commit (configuração)
props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");

// Commit síncrono manual
consumer.commitSync();

// Commit assíncrono manual
consumer.commitAsync();

// Commit com callback
consumer.commitAsync(new OffsetCommitCallback() {
    @Override
    public void onComplete(Map<TopicPartition, OffsetAndMetadata> offsets, 
                          Exception exception) {
        if (exception != null) {
            System.err.println("Commit failed: " + exception.getMessage());
        }
    }
});

// Commit de offset específico
Map<TopicPartition, OffsetAndMetadata> offsets = new HashMap<>();
offsets.put(new TopicPartition("orders", 0), new OffsetAndMetadata(100));
consumer.commitSync(offsets);
```

---

## 5. Topics e Partitions

### Topics

Topic é uma categoria ou feed name onde eventos são publicados.

```bash
# Criar topic
kafka-topics.sh --create \
  --bootstrap-server localhost:9092 \
  --topic orders \
  --partitions 3 \
  --replication-factor 2

# Listar topics
kafka-topics.sh --list --bootstrap-server localhost:9092

# Descrever topic
kafka-topics.sh --describe --topic orders --bootstrap-server localhost:9092

# Deletar topic
kafka-topics.sh --delete --topic orders --bootstrap-server localhost:9092
```

### Partitions

Partition é uma divisão ordenada de um topic. Permite paralelismo e escalabilidade.

```
Topic: orders (3 partitions)

Partition 0: [msg0] [msg3] [msg6] [msg9]  ...
Partition 1: [msg1] [msg4] [msg7] [msg10] ...
Partition 2: [msg2] [msg5] [msg8] [msg11] ...
```

### Características das Partitions

- **Ordem garantida**: Dentro de uma partition
- **Imutabilidade**: Mensagens não podem ser alteradas
- **Retenção**: Configurável (tempo ou tamanho)
- **Offset**: Identificador único e sequencial

### Particionamento

```java
// Por key (hash da key determina partition)
ProducerRecord<String, String> record = 
    new ProducerRecord<>("orders", "customer-123", "Order data");

// Partition específica
ProducerRecord<String, String> record = 
    new ProducerRecord<>("orders", 2, "key", "value");

// Partitioner customizado
public class CustomPartitioner implements Partitioner {
    @Override
    public int partition(String topic, Object key, byte[] keyBytes,
                        Object value, byte[] valueBytes, Cluster cluster) {
        // Lógica customizada
        int numPartitions = cluster.partitionCountForTopic(topic);
        if (key.toString().startsWith("VIP")) {
            return 0; // VIP sempre na partition 0
        }
        return Math.abs(key.hashCode()) % numPartitions;
    }
}
```

### Replicação

```
Topic: orders (3 partitions, replication-factor: 2)

Broker 1:  Partition 0 (Leader), Partition 1 (Replica)
Broker 2:  Partition 1 (Leader), Partition 2 (Replica)
Broker 3:  Partition 2 (Leader), Partition 0 (Replica)
```

- **Leader**: Recebe leituras e escritas
- **Replica**: Cópia de backup
- **ISR (In-Sync Replicas)**: Réplicas sincronizadas

---

## 6. Consumer Groups

### Conceito

Consumer Group permite que múltiplos consumers trabalhem juntos para consumir um topic, distribuindo a carga.

```
Topic: orders (4 partitions)

Consumer Group: order-processors
┌─────────────┐    ┌─────────────┐
│ Consumer 1  │    │ Consumer 2  │
│ Part 0, 1   │    │ Part 2, 3   │
└─────────────┘    └─────────────┘
```

### Regras

- **1 partition → 1 consumer** (no mesmo grupo)
- **Rebalanceamento**: Quando consumer entra/sai
- **Múltiplos grupos**: Podem ler o mesmo topic independentemente

### Exemplo com Múltiplos Consumers

```java
// Consumer 1
public class OrderConsumer1 {
    public static void main(String[] args) {
        Properties props = new Properties();
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "order-processors");
        props.put(ConsumerConfig.CLIENT_ID_CONFIG, "consumer-1");
        // ... outras configs
        
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList("orders"));
        // ... consumir
    }
}

// Consumer 2
public class OrderConsumer2 {
    public static void main(String[] args) {
        Properties props = new Properties();
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "order-processors");
        props.put(ConsumerConfig.CLIENT_ID_CONFIG, "consumer-2");
        // ... outras configs
        
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList("orders"));
        // ... consumir
    }
}
```

### Rebalancing

```java
// Listener de rebalanceamento
consumer.subscribe(Collections.singletonList("orders"), 
    new ConsumerRebalanceListener() {
        
        @Override
        public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
            System.out.println("Partitions revoked: " + partitions);
            // Commit offsets antes de perder partitions
            consumer.commitSync();
        }
        
        @Override
        public void onPartitionsAssigned(Collection<TopicPartition> partitions) {
            System.out.println("Partitions assigned: " + partitions);
            // Inicializar estado para novas partitions
        }
    });
```

---

## 7. Kafka Streams

### Conceito

Kafka Streams é uma biblioteca cliente para processar e analisar dados armazenados no Kafka.

### Exemplo Básico

```java
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.*;

import java.util.Properties;

public class WordCountExample {
    
    public static void main(String[] args) {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "wordcount-app");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        
        StreamsBuilder builder = new StreamsBuilder();
        
        // Stream de entrada
        KStream<String, String> textLines = builder.stream("text-input");
        
        // Processar: split, group, count
        KTable<String, Long> wordCounts = textLines
            .flatMapValues(value -> Arrays.asList(value.toLowerCase().split("\\W+")))
            .groupBy((key, word) -> word)
            .count();
        
        // Output
        wordCounts.toStream().to("word-count-output", Produced.with(Serdes.String(), Serdes.Long()));
        
        // Criar e iniciar topology
        KafkaStreams streams = new KafkaStreams(builder.build(), props);
        streams.start();
        
        // Shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }
}
```

### Operações Comuns

```java
// Filter
KStream<String, String> filtered = stream.filter((key, value) -> value.length() > 10);

// Map
KStream<String, Integer> mapped = stream.mapValues(value -> value.length());

// FlatMap
KStream<String, String> flatMapped = stream.flatMapValues(
    value -> Arrays.asList(value.split(" "))
);

// GroupBy
KGroupedStream<String, String> grouped = stream.groupByKey();

// Aggregate
KTable<String, Long> aggregated = grouped.count();

// Join
KStream<String, String> joined = stream1.join(
    stream2,
    (value1, value2) -> value1 + " " + value2,
    JoinWindows.of(Duration.ofMinutes(5))
);

// Windowing
TimeWindowedKStream<String, String> windowed = grouped.windowedBy(
    TimeWindows.of(Duration.ofMinutes(1))
);
```

---

## 8. Integração com Quarkus

### Dependency

```xml
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-smallrye-reactive-messaging-kafka</artifactId>
</dependency>
```

### Configuração

```properties
# application.properties

# Kafka broker
kafka.bootstrap.servers=localhost:9092

# Outgoing channel (Producer)
mp.messaging.outgoing.orders.connector=smallrye-kafka
mp.messaging.outgoing.orders.topic=orders
mp.messaging.outgoing.orders.value.serializer=io.quarkus.kafka.client.serialization.ObjectMapperSerializer

# Incoming channel (Consumer)
mp.messaging.incoming.orders.connector=smallrye-kafka
mp.messaging.incoming.orders.topic=orders
mp.messaging.incoming.orders.group.id=order-processor-group
mp.messaging.incoming.orders.value.deserializer=io.quarkus.kafka.client.serialization.ObjectMapperDeserializer
mp.messaging.incoming.orders.auto.offset.reset=earliest
```

### Producer com Quarkus

```java
import io.smallrye.reactive.messaging.kafka.Record;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class OrderProducer {
    
    @Inject
    @Channel("orders")
    Emitter<Record<String, Order>> orderEmitter;
    
    public void sendOrder(Order order) {
        // Envio com key
        orderEmitter.send(Record.of(order.getId(), order));
    }
    
    // Ou com método anotado
    @Outgoing("orders")
    public Multi<Order> generateOrders() {
        return Multi.createFrom().ticks().every(Duration.ofSeconds(1))
            .map(tick -> new Order("order-" + tick, BigDecimal.valueOf(100)));
    }
}
```

### Consumer com Quarkus

```java
import org.eclipse.microprofile.reactive.messaging.Incoming;
import io.smallrye.reactive.messaging.kafka.Record;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class OrderConsumer {
    
    @Incoming("orders")
    public void processOrder(Order order) {
        System.out.println("Processing order: " + order.getId());
        // Processar lógica de negócio
    }
    
    // Com Record (acesso a key e metadata)
    @Incoming("orders")
    public void processOrderRecord(Record<String, Order> record) {
        System.out.println("Key: " + record.key());
        System.out.println("Order: " + record.value());
        System.out.println("Partition: " + record.getPartition());
    }
    
    // Reativo com Uni
    @Incoming("orders")
    public Uni<Void> processOrderReactive(Order order) {
        return Uni.createFrom().item(order)
            .onItem().transform(o -> {
                System.out.println("Processing: " + o);
                return null;
            });
    }
    
    // Com acknowledgment manual
    @Incoming("orders")
    public CompletionStage<Void> processWithAck(Message<Order> message) {
        Order order = message.getPayload();
        // Processar
        return message.ack(); // Manual ack
    }
}
```

### Dead Letter Queue (DLQ)

```properties
# DLQ configuration
mp.messaging.incoming.orders.failure-strategy=dead-letter-queue
mp.messaging.incoming.orders.dead-letter-queue.topic=orders-dlq
mp.messaging.incoming.orders.dead-letter-queue.key.serializer=org.apache.kafka.common.serialization.StringSerializer
mp.messaging.incoming.orders.dead-letter-queue.value.serializer=org.apache.kafka.common.serialization.StringSerializer
```

```java
@Incoming("orders")
public void processOrder(Order order) {
    if (order.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
        throw new IllegalArgumentException("Invalid amount");
        // Mensagem vai para DLQ
    }
    // Processar normalmente
}
```

### Docker Compose para Kafka

```yaml
version: '3.8'

services:
  zookeeper:
    image: confluentinc/cp-zookeeper:7.5.0
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181
      ZOOKEEPER_TICK_TIME: 2000
    ports:
      - "2181:2181"

  kafka:
    image: confluentinc/cp-kafka:7.5.0
    depends_on:
      - zookeeper
    ports:
      - "9092:9092"
    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://localhost:9092
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
      KAFKA_TRANSACTION_STATE_LOG_MIN_ISR: 1
      KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR: 1
```

---

## 🎯 Melhores Práticas

1. **Use Consumer Groups**: Para paralelismo e tolerância a falhas
2. **Monitore Lag**: Diferença entre offset produzido e consumido
3. **Configure Retenção**: Baseado em requisitos de negócio
4. **Use Idempotência**: Para evitar duplicatas
5. **Gerencie Offsets**: Commit manual para maior controle
6. **Escolha Partitions**: Baseado em paralelismo desejado
7. **Serialize Eficientemente**: Avro, Protobuf para performance
8. **Dead Letter Queue**: Para mensagens com falha

## 📊 Monitoramento

```java
// Métricas importantes
- Consumer Lag
- Producer Throughput
- Broker Disk Usage
- Replication Status
- Under-Replicated Partitions
```

## 📚 Recursos Adicionais

- **Documentação Oficial**: https://kafka.apache.org/documentation/
- **Confluent Documentation**: https://docs.confluent.io/
- **Kafka: The Definitive Guide**: Livro O'Reilly
- **Conduktor**: Ferramenta de gerenciamento Kafka

---

[← Voltar ao Índice Principal](../README.md)
