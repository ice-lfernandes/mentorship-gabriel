# Guia de Aprendizado - Mentoria de Software Engineering

Este guia sugere uma trilha estruturada de aprendizado, com ordem recomendada e marcos de progresso.

## 🎯 Visão Geral

Este programa de mentoria é projetado para levar você de um nível intermediário a avançado em engenharia de software, focando em práticas modernas, padrões e tecnologias relevantes para o mercado.

**Duração estimada**: 3-6 meses (dedicação de 10-15h/semana)

---

## 📅 Trilha de Aprendizado

### Fase 1: Fundamentos (Semanas 1-4)

#### Semana 1-2: SOLID Principles

**Objetivos**:
- [ ] Entender cada princípio SOLID
- [ ] Identificar violações em código existente
- [ ] Refatorar código aplicando SOLID

**Atividades**:
1. Ler a documentação completa em `01-solid/README.md`
2. Estudar o exemplo `UserService.java`
3. Completar exercícios 1-5 em `EXERCISES.md`
4. Refatorar um projeto pessoal aplicando SOLID

**Recursos**:
- Livro: "Clean Code" - Capítulos sobre design de classes
- Vídeo: [SOLID Principles Explained](https://www.youtube.com/results?search_query=solid+principles)

**Checkpoint**: ✅ Consegue explicar cada princípio e aplicar em código real

---

#### Semana 3-4: Design Patterns Essenciais

**Objetivos**:
- [ ] Dominar os 3 principais patterns de cada categoria
- [ ] Saber quando aplicar cada pattern
- [ ] Implementar patterns em projetos práticos

**Atividades**:
1. Ler `02-design-patterns/README.md`
2. Implementar cada pattern do zero
3. Estudar o exemplo `PaymentStrategy.java`
4. Criar um projeto usando ao menos 3 patterns diferentes

**Foco nos Patterns**:
- **Criacionais**: Singleton, Factory, Builder
- **Estruturais**: Adapter, Decorator, Facade
- **Comportamentais**: Strategy, Observer, Command

**Checkpoint**: ✅ Implementou ao menos 9 patterns diferentes com testes

---

### Fase 2: Arquitetura (Semanas 5-8)

#### Semana 5-6: Padrões Arquiteturais

**Objetivos**:
- [ ] Entender diferentes estilos arquiteturais
- [ ] Comparar trade-offs entre padrões
- [ ] Desenhar arquitetura de sistemas

**Atividades**:
1. Estudar `03-architecture-patterns/README.md`
2. Desenhar diagramas de arquitetura
3. Analisar arquitetura de sistemas open source
4. Criar um projeto usando Clean Architecture

**Foco**:
- Monolítico vs Microservices
- Clean Architecture
- Hexagonal Architecture
- Event-Driven Architecture

**Projeto Prático**: 
Criar uma aplicação de e-commerce simples usando Clean Architecture com separação clara de camadas.

**Checkpoint**: ✅ Desenhou e implementou uma arquitetura limpa e testável

---

#### Semana 7-8: CQRS e Saga Pattern

**Objetivos**:
- [ ] Implementar CQRS
- [ ] Entender Saga Pattern
- [ ] Gerenciar transações distribuídas

**Atividades**:
1. Estudar CQRS e Event Sourcing
2. Implementar separação de leitura/escrita
3. Estudar Saga Pattern (Choreography e Orchestration)
4. Implementar compensação de transações

**Projeto Prático**:
Estender o e-commerce com CQRS para separar comandos e queries.

**Checkpoint**: ✅ Implementou CQRS e entende quando usar Saga Pattern

---

### Fase 3: Quarkus (Semanas 9-11)

#### Semana 9-10: Quarkus Fundamentals

**Objetivos**:
- [ ] Criar aplicações Quarkus
- [ ] Dominar CDI e injeção de dependências
- [ ] Implementar APIs REST completas
- [ ] Trabalhar com Panache

**Atividades**:
1. Estudar `04-quarkus/README.md`
2. Seguir tutorial `QUARKUS-API-EXAMPLE.md`
3. Criar API REST do zero
4. Implementar CRUD com Panache
5. Adicionar validação e exception handling

**Projeto Prático**:
Criar uma API de gerenciamento de tarefas (TODO list) com:
- CRUD completo
- Validação
- Paginação
- Filtros
- Testes

**Checkpoint**: ✅ Criou API REST completa com Quarkus

---

#### Semana 11: Quarkus Reactive e Native

**Objetivos**:
- [ ] Entender programação reativa com Mutiny
- [ ] Criar imagens nativas
- [ ] Comparar performance

**Atividades**:
1. Converter API para reativa
2. Usar Uni e Multi
3. Build native image
4. Medir startup time e memory usage

**Projeto Prático**:
Refatorar a API de tarefas para usar programação reativa.

**Checkpoint**: ✅ API reativa funcionando com native image

---

### Fase 4: Apache Kafka (Semanas 12-15)

#### Semana 12-13: Kafka Fundamentals

**Objetivos**:
- [ ] Entender arquitetura Kafka
- [ ] Trabalhar com producers e consumers
- [ ] Configurar topics e partitions
- [ ] Gerenciar offsets

**Atividades**:
1. Estudar `05-kafka/README.md`
2. Instalar Kafka localmente (Docker)
3. Criar producers e consumers básicos
4. Experimentar com diferentes configurações
5. Monitorar lag e performance

**Projeto Prático**:
Sistema de notificações:
- Producer publica eventos
- Múltiplos consumers processam
- Diferentes tipos de notificação (email, SMS, push)

**Checkpoint**: ✅ Sistema de mensageria funcionando com Kafka

---

#### Semana 14-15: Kafka com Quarkus + Event-Driven

**Objetivos**:
- [ ] Integrar Kafka com Quarkus
- [ ] Implementar Event-Driven Architecture
- [ ] Criar Saga Pattern com Kafka

**Atividades**:
1. Estudar `ORDER-SYSTEM-EXAMPLE.md`
2. Implementar sistema de pedidos
3. Criar múltiplos microservices
4. Implementar compensação

**Projeto Final**:
Sistema completo de e-commerce com:
- Order Service
- Payment Service
- Inventory Service
- Notification Service
- Shipping Service

Todos comunicando via Kafka, com:
- Event-Driven Architecture
- Saga Pattern para transações distribuídas
- Compensação em caso de falha
- Monitoramento e observabilidade

**Checkpoint**: ✅ Sistema distribuído completo funcionando

---

## 🎓 Avaliação de Progresso

### Iniciante → Intermediário
- [ ] Conhece todos os princípios SOLID
- [ ] Implementou ao menos 5 design patterns
- [ ] Criou uma API REST básica

### Intermediário → Avançado
- [ ] Aplicou Clean Architecture em projeto real
- [ ] Criou aplicação com Quarkus e Panache
- [ ] Entende CQRS e Event Sourcing
- [ ] Trabalhou com Kafka (producer/consumer)

### Avançado → Expert
- [ ] Arquitetou sistema com microservices
- [ ] Implementou Event-Driven Architecture
- [ ] Usou Saga Pattern em produção
- [ ] Sistema completo com Quarkus + Kafka
- [ ] Contribuiu para projeto open source

---

## 📊 Projetos Sugeridos por Nível

### Nível 1: Fundamentos
**Biblioteca de Livros**
- CRUD de livros
- Aplicar SOLID
- Usar 3-4 design patterns
- Testes unitários

### Nível 2: Intermediário
**Sistema de Blog**
- API REST com Quarkus
- Clean Architecture
- Autenticação JWT
- Testes de integração
- Docker

### Nível 3: Avançado
**E-commerce Completo**
- Microservices (Order, Payment, Inventory, User)
- Kafka para comunicação
- Event-Driven Architecture
- Saga Pattern
- CQRS
- Native images
- Kubernetes deploy

---

## 🛠️ Ferramentas Necessárias

### Essenciais
- [ ] Java 17+
- [ ] Maven ou Gradle
- [ ] IDE (IntelliJ IDEA ou VS Code)
- [ ] Git
- [ ] Docker
- [ ] Postman ou Insomnia

### Recomendadas
- [ ] Docker Compose
- [ ] Kafka (via Docker)
- [ ] PostgreSQL (via Docker)
- [ ] Kubernetes (Minikube)
- [ ] Prometheus + Grafana (monitoramento)

---

## 📝 Dicas de Estudo

### Para Aprender Efetivamente

1. **Prática Deliberada**
   - Não apenas leia, IMPLEMENTE
   - Escreva código todos os dias
   - Refatore código antigo

2. **Projetos Reais**
   - Aplique em projetos pessoais
   - Contribua para open source
   - Crie portfolio no GitHub

3. **Ensine Outros**
   - Escreva blog posts
   - Faça code reviews
   - Ajude em fóruns (Stack Overflow)

4. **Leia Código**
   - Estude código de projetos populares
   - Analise decisões arquiteturais
   - Entenda trade-offs

5. **Networking**
   - Participe de comunidades
   - Vá a meetups e conferências
   - Conecte com outros desenvolvedores

---

## 🏆 Metas e Marcos

### Mês 1
- ✅ SOLID principles dominados
- ✅ 9+ design patterns implementados
- ✅ Projeto com arquitetura limpa

### Mês 2
- ✅ API REST completa com Quarkus
- ✅ Testes automatizados
- ✅ Entendimento de padrões arquiteturais

### Mês 3
- ✅ Sistema com Kafka funcionando
- ✅ Event-Driven Architecture implementada
- ✅ Microservices comunicando

### Mês 4-6
- ✅ Projeto final completo
- ✅ Deploy em produção (cloud)
- ✅ Contribuição open source
- ✅ Portfolio técnico robusto

---

## 🎯 Próximos Passos Após Completar

1. **Especialização**
   - Cloud (AWS, Azure, GCP)
   - DevOps (CI/CD, Kubernetes)
   - Security
   - Performance tuning

2. **Certificações**
   - Java certifications
   - Cloud certifications
   - Kafka certifications

3. **Liderança Técnica**
   - Mentoria de outros
   - Tech lead
   - Arquitetura de soluções

---

## 📞 Suporte

- Revise o `RESOURCES.md` para materiais complementares
- Faça perguntas em issues do GitHub
- Participe de comunidades online
- Marque sessões de code review com mentor

---

**Lembre-se**: Aprendizado é uma jornada, não um destino. Seja paciente consigo mesmo e celebre pequenas vitórias! 🚀

[← Voltar ao Índice Principal](./README.md)
