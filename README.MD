<div align="center">

# CineLog

### Uma API REST de nível de produção para filmes, plataformas de streaming e categorias.
### Construída da forma certa — segurança com JWT, arquitetura em camadas, zero atalhos, encapsulamento total via DTO.

&nbsp;

[![Java](https://img.shields.io/badge/java-17_LTS-ED8B00?style=flat-square&labelColor=0a0e14&logo=openjdk&logoColor=ED8B00)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/spring_boot-3.x-6DB33F?style=flat-square&labelColor=0a0e14&logo=spring&logoColor=6DB33F)](https://spring.io/projects/spring-boot)
[![Spring Security](https://img.shields.io/badge/spring_security-JWT-6DB33F?style=flat-square&labelColor=0a0e14&logo=springsecurity&logoColor=6DB33F)](https://spring.io/projects/spring-security)
[![PostgreSQL](https://img.shields.io/badge/postgresql-15+-4169E1?style=flat-square&labelColor=0a0e14&logo=postgresql&logoColor=4169E1)](https://www.postgresql.org/)
[![Docker](https://img.shields.io/badge/docker-compose-2496ED?style=flat-square&labelColor=0a0e14&logo=docker&logoColor=2496ED)](https://www.docker.com/)
[![Flyway](https://img.shields.io/badge/flyway-migrations-CC0200?style=flat-square&labelColor=0a0e14&logo=flyway&logoColor=white)](https://flywaydb.org/)
[![Maven](https://img.shields.io/badge/maven-3.8+-C71A36?style=flat-square&labelColor=0a0e14&logo=apache-maven&logoColor=white)](https://maven.apache.org/)
[![Status](https://img.shields.io/badge/status-active_development-F0A500?style=flat-square&labelColor=0a0e14)](.)
[![License](https://img.shields.io/badge/license-MIT-b0e8ff?style=flat-square&labelColor=0a0e14)](./LICENSE)

&nbsp;

[Início Rápido](#início-rápido) · [Arquitetura](#arquitetura) · [Referência da API](#referência-da-api) · [Segurança](#segurança) · [Decisões de Design](#decisões-de-design) · [Roadmap](#roadmap)

&nbsp;

| | | |
|---|---|---|
| **4 camadas** estritamente separadas | Autenticação **JWT** stateless | Configuração compatível com **12-Factor** |
| **Zero** vazamento do modelo de domínio | **Exceções customizadas** e handler global | Schema versionado com **Flyway** |

</div>

---

> ⚠️ **Trabalho em andamento** — a API principal está estável e funcional. Cobertura de exceções, refatoração e recursos de filtragem estão em desenvolvimento ativo. Veja o [Roadmap](#roadmap) para saber o que vem por aí.

---

## Início Rápido

**Pré-requisitos:** JDK 17+, Maven 3.8+, Docker

```bash
git clone https://github.com/Andrius-Anselmi/cinelog.git
cd cinelog
```

Suba o PostgreSQL via Docker:

```bash
docker compose up -d
```

Defina as variáveis de ambiente:

```bash
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/cinelog
export SPRING_DATASOURCE_USERNAME=seu_usuario
export SPRING_DATASOURCE_PASSWORD=sua_senha
export CINELOG_API_SECRET=seu_jwt_secret_aqui
```

Compile e rode:

```bash
mvn clean install
mvn spring-boot:run
```

API disponível em `http://localhost:8080`. Não é necessário configurar o schema manualmente — o Flyway roda as migrações automaticamente na inicialização.

> **Nota:** Todos os endpoints, exceto `/user/register` e `/user/login`, exigem um token JWT válido.

---

## Arquitetura

Quatro camadas. Cada uma não sabe nada sobre as outras, exceto a interface da qual depende.

```
  Requisição HTTP
       │
       ▼
┌─────────────────────────────────────────────┐
│  Camada de Segurança                        │
│  Validação de JWT em toda requisição        │
│  SecurityFilter + TokenService              │
└────────────────────┬────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────┐
│  Camada de Controller                       │
│  Lida com HTTP, status codes, roteamento    │
│  Retorna DTOs — nunca modelos de domínio    │
└────────────────────┬────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────┐
│  Camada de Service                          │
│  Todas as regras de negócio vivem aqui      │
│  Valida, orquestra, transforma              │
│  Lança exceções tipadas em caso de falha    │
└────────────────────┬────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────┐
│  Camada de Repository                       │
│  Spring Data JPA — queries otimizadas       │
│  Schema gerenciado por migrações Flyway     │
└────────────────────┬────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────┐
│  PostgreSQL 15+ (Docker)                    │
│  Fonte da verdade — nunca exposto diretamente│
└─────────────────────────────────────────────┘
```

---

## Segurança

A autenticação é stateless e baseada em JWT. Sem sessões, sem cookies.

**Registre** um usuário, **faça login** para receber um token, e então inclua-o em toda requisição:

```
Authorization: Bearer <seu_token>
```

O `SecurityFilter` intercepta toda requisição, valida o token via `TokenService`, e define o contexto de autenticação. Tokens inválidos ou ausentes resultam em `403 Forbidden`.

As senhas são criptografadas com **BCrypt** antes de serem armazenadas — senhas em texto puro nunca chegam ao banco de dados.

```
POST /user/register  →  cria usuário, criptografa senha
POST /user/login     →  valida credenciais, retorna JWT
Todas as outras rotas →  exigem um Bearer token válido
```

---

## Exemplo de Requisição

Criando um filme com categorias e plataformas de streaming em uma única requisição:

```bash
curl -X POST http://localhost:8080/cinelog/movie \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <seu_token>" \
  -d '{
    "title": "Inception",
    "description": "Um ladrão que rouba segredos através dos sonhos recebe a chance de ter seu passado apagado.",
    "releaseDate": "16/07/2010",
    "rating": 8.8,
    "categories": [1, 2],
    "streamings": [1]
  }'
```

```json
{
  "id": 1,
  "title": "Inception",
  "description": "Um ladrão que rouba segredos através dos sonhos recebe a chance de ter seu passado apagado.",
  "releaseDate": "16/07/2010",
  "rating": 8.8,
  "categories": [
    { "id": 1, "name": "Sci-Fi" },
    { "id": 2, "name": "Thriller" }
  ],
  "streamings": [
    { "id": 1, "name": "Netflix" }
  ]
}
```

---

## Referência da API

### Autenticação

| Método | Rota | Descrição | Auth | Status |
|--------|-------|-------------|------|--------|
| `POST` | `/user/register` | Registra um novo usuário | ❌ | `201` |
| `POST` | `/user/login` | Autentica e retorna JWT | ❌ | `200` |

### Filmes

| Método | Rota | Descrição | Auth | Status |
|--------|-------|-------------|------|--------|
| `GET` | `/movie` | Lista todos os filmes | ✅ | `200` |
| `GET` | `/movie/{id}` | Busca filme por ID | ✅ | `200` / `404` |
| `GET` | `/movie/category/{id}` | Busca filmes por categoria | ✅ | `200` |
| `POST` | `/movie` | Cria um filme | ✅ | `201` |
| `PUT` | `/movie/{id}` | Atualiza um filme | ✅ | `200` / `404` |
| `DELETE` | `/movie/{id}` | Remove um filme | ✅ | `204` / `404` |

### Categorias

| Método | Rota | Descrição | Auth | Status |
|--------|-------|-------------|------|--------|
| `GET` | `/category` | Lista todas as categorias | ✅ | `200` |
| `GET` | `/category/{id}` | Busca categoria por ID | ✅ | `200` / `404` |
| `POST` | `/category` | Cria uma categoria | ✅ | `201` |
| `PUT` | `/category/{id}` | Atualiza uma categoria | ✅ | `200` / `404` |
| `DELETE` | `/category/{id}` | Remove uma categoria | ✅ | `204` / `404` |

### Plataformas de Streaming

| Método | Rota | Descrição | Auth | Status |
|--------|-------|-------------|------|--------|
| `GET` | `/streaming` | Lista todas as plataformas | ✅ | `200` |
| `GET` | `/streaming/{id}` | Busca plataforma por ID | ✅ | `200` / `404` |
| `POST` | `/streaming` | Cria uma plataforma | ✅ | `201` |
| `PUT` | `/streaming/{id}` | Atualiza uma plataforma | ✅ | `200` / `404` |
| `DELETE` | `/streaming/{id}` | Remove uma plataforma | ✅ | `204` / `404` |

---

## Tratamento de Exceções

Os erros são tratados globalmente via `GlobalExceptionHandler`. Sem try/catch espalhados pelos controllers.

| Exceção | Status HTTP | Quando ocorre |
|-----------|-------------|------|
| `NotFoundException` | `404 Not Found` | Recurso não encontrado pelo ID |
| `ValidationException` | `400 Bad Request` | Campos da requisição inválidos ou ausentes |

As mensagens de erro são centralizadas em `ExceptionMessages` — uma constante por mensagem, alterada em um único lugar.

> ⚠️ A cobertura de exceções está sendo ativamente expandida. Mais exceções tipadas estão a caminho.

---

## Decisões de Design

Cada padrão aqui é uma escolha deliberada, não boilerplate.

**Autenticação JWT** — Segurança stateless baseada em token. Sem estado de sessão no servidor. Cada requisição é autocontida e verificável de forma independente. Escala horizontalmente sem coordenação.

**Encapsulamento via DTO** — Entidades JPA nunca são serializadas diretamente nas respostas da API. Os DTOs dão à API um contrato estável, independente do schema do banco de dados. Renomeie uma coluna, mude um relacionamento — o cliente nunca percebe.

**DTOs baseados em Records** — `MovieRequest` e `MovieResponse` são Records do Java. Imutáveis por design, concisos por natureza, e perfeitos para carregadores de dados que nunca devem mudar após a construção.

**Padrão Mapper** — A conversão entre entidades e DTOs vive em classes `@UtilityClass` dedicadas de mapper. Controllers e services permanecem limpos. A lógica de transformação permanece testável e isolada.

**Tratamento global de exceções** — `@RestControllerAdvice` intercepta exceções tipadas e as mapeia para respostas HTTP. Controllers e services permanecem limpos. O formato de erro é consistente em toda a API.

**Bean Validation** — Anotações `@Valid` aplicam restrições na fronteira do controller. Dados inválidos são rejeitados antes de chegar à camada de service.

**Migrações com Flyway** — Alterações de schema são arquivos SQL versionados. Todo ambiente roda exatamente as mesmas migrações, na mesma ordem. Chega de schema dessincronizado entre máquinas.

**Configuração 12-Factor** — URL do banco, credenciais e o segredo JWT vêm de variáveis de ambiente. O mesmo artefato roda em qualquer ambiente sem modificação.

**ManyToMany via tabelas de junção** — `movie_category` e `movie_streaming` são tabelas de junção explícitas com chaves estrangeiras nomeadas — não as geradas por padrão pelo Hibernate. Controle total sobre o schema.

**Timestamps pelo Hibernate** — `createdAt` e `updatedAt` são gerenciados automaticamente via `@CreationTimestamp` e `@UpdateTimestamp`. Nenhum tratamento manual de datas em lugar nenhum.

---

## Schema do Banco de Dados

```
┌───────────────┐       ┌──────────────────┐       ┌────────────┐
│     movie     │       │  movie_category  │       │  category  │
│───────────────│       │──────────────────│       │────────────│
│ id            │──────▶│ movie_id         │◀──────│ id         │
│ title         │       │ category_id      │       │ name       │
│ description   │       └──────────────────┘       └────────────┘
│ release_date  │
│ rating        │       ┌──────────────────┐       ┌────────────┐
│ created_at    │       │ movie_streaming  │       │ streaming  │
│ updated_at    │──────▶│──────────────────│◀──────│────────────│
└───────────────┘       │ movie_id         │       │ id         │
                        │ streaming_id     │       │ name       │
                        └──────────────────┘       └────────────┘

┌──────────────┐
│    users     │
│──────────────│
│ id           │
│ name         │
│ email        │
│ password     │
└──────────────┘
```

---

## Roadmap

Recursos em desenvolvimento ativo:

- [ ] Expandir a cobertura de exceções customizadas para todos os cenários de erro
- [ ] Refatorar as camadas de service e controller
- [ ] Filtrar filmes por categoria, plataforma de streaming e avaliação
- [ ] Paginação e ordenação nos endpoints de listagem
- [ ] Endpoints de consulta adicionais (ex: filmes por plataforma de streaming)

---

## Stack Tecnológica

| Componente | Tecnologia | Por quê |
|-----------|-----------|-----|
| Linguagem | Java 17 LTS | Records, pattern matching, suporte de longo prazo |
| Framework | Spring Boot 3.x | Padrão da indústria, ecossistema de DI poderoso |
| Segurança | Spring Security + JWT | Autenticação stateless e escalável |
| Validação | Spring Bean Validation | Restrições declarativas na fronteira |
| Persistência | Spring Data JPA + Hibernate | Abstração limpa sobre JDBC |
| Banco de Dados | PostgreSQL 15+ | Confiável, comprovado em produção |
| Container | Docker + Docker Compose | Ambientes reprodutíveis |
| Migrações | Flyway | Schema versionado |
| Build | Maven 3.8+ | Ciclo de vida previsível |

---

## Estrutura do Projeto

```
src/
└── main/
    └── java/
        └── dev/java/cinelog/
            ├── config/         # SecurityConfig, SecurityFilter, JWTuserData
            ├── controller/     # Camada HTTP — rotas e status codes
            ├── service/        # Lógica de negócio — regras e validação
            ├── repository/     # Acesso a dados — queries JPA
            ├── entity/         # Entidades de domínio — fonte da verdade
            ├── mapper/         # Transformação de DTOs
            ├── request/        # DTOs de entrada
            ├── response/       # DTOs de saída
            └── exception/      # Exceções customizadas + GlobalExceptionHandler
    └── resources/
        └── db/migration/       # Migrações SQL do Flyway (V1__, V2__...)
```

---

## Contribuindo

Issues e PRs são bem-vindos. Se você está adicionando uma feature, comece pela camada de service — a lógica de negócio pertence ali, não nos controllers ou repositories.

## Licença

MIT — veja [LICENSE](./LICENSE).

---

<div align="center">

Feito por [Andrius Anselmi](https://github.com/Andrius-Anselmi) · [LinkedIn](https://www.linkedin.com/in/andrius-anselmi)

</div>
