# Sistema de Contas a Pagar

Sistema REST API para gerenciamento de contas a pagar com cálculo automático de multas e juros por atraso.

## 🚀 Tecnologias

### Backend
- Java 17
- Spring Boot 3.4.11
- Spring Data JPA
- PostgreSQL 15
- Flyway
- Lombok
- Maven
- Docker & Docker Compose
- JUnit 5
- H2 Database (testes)

### Frontend
- Angular 20.3.9
- TypeScript
- RxJS
- Node.js 22+
- npm

## 📋 Requisitos

### Para executar apenas o Backend:
- Docker e Docker Compose
- Java 17+ (apenas para desenvolvimento local)
- Maven 3.9+ (apenas para desenvolvimento local)

### Para executar com Frontend:
- Node.js 22+ e npm
- Angular CLI (`npm install -g @angular/cli`)
- Todos os requisitos do backend acima

## 🔧 Configuração e Execução

### Opção 1: Docker Compose (Recomendado)

```bash
# Clonar o repositório
git clone https://github.com/marcus6n/contas-pagar.git
cd contas-pagar

# Subir aplicação e banco de dados
docker-compose up -d

# Verificar logs
docker-compose logs -f app

# Acessar API
# http://localhost:8080/api/contas
```

### Opção 2: Execução Local

```bash
# 1. Subir apenas o PostgreSQL
docker-compose up -d postgres

# 2. Aguardar banco estar pronto
docker logs -f contaspagar-db

# 3. Executar aplicação
./mvnw spring-boot:run

# Windows: mvnw.cmd spring-boot:run
```

### Opção 3: Com Frontend Angular

```bash
# Terminal 1: Subir PostgreSQL e executar backend
docker-compose up -d postgres
./mvnw spring-boot:run

# Terminal 2: Frontend
cd frontend
npm install
ng serve

# Acessar:
# Frontend: http://localhost:4200
# Backend: http://localhost:8080/api/contas
```

## 📡 Endpoints da API

### Criar Conta a Pagar

```http
POST /api/contas
Content-Type: application/json

{
  "nome": "Conta de Luz",
  "valorOriginal": 1000.00,
  "dataVencimento": "2025-11-01",
  "dataPagamento": "2025-11-05"
}
```

**Resposta:**
```json
{
  "id": 1,
  "nome": "Conta de Luz",
  "valorOriginal": 1000.00,
  "valorCorrigido": 1040.00,
  "diasAtraso": 4,
  "dataPagamento": "2025-11-05",
  "percentualMulta": 3.00,
  "percentualJurosDia": 0.200
}
```

### Listar Contas

```http
GET /api/contas
```

**Resposta:**
```json
[
  {
    "id": 1,
    "nome": "Conta de Luz",
    "valorOriginal": 1000.00,
    "valorCorrigido": 1040.00,
    "diasAtraso": 4,
    "dataPagamento": "2025-11-05",
    "percentualMulta": 3.00,
    "percentualJurosDia": 0.200
  }
]
```

## 💰 Regras de Negócio

### Cálculo de Multa e Juros por Atraso

| Dias de Atraso | Multa | Juros por Dia |
|----------------|-------|---------------|
| 0 dias         | 0%    | 0%            |
| Até 3 dias     | 2%    | 0,1%          |
| 4 a 5 dias     | 3%    | 0,2%          |
| Acima de 5 dias| 5%    | 0,3%          |

**Fórmula:**
```
Valor Corrigido = Valor Original + (Valor Original × Multa%) + (Valor Original × Juros% × Dias)
```

**Exemplo (4 dias de atraso):**
```
Valor Original: R$ 1.000,00
Multa: R$ 1.000,00 × 3% = R$ 30,00
Juros: R$ 1.000,00 × 0,2% × 4 = R$ 8,00
Valor Corrigido: R$ 1.000,00 + R$ 30,00 + R$ 8,00 = R$ 1.038,00
```

### Validações

- Todos os campos são obrigatórios
- Valor original deve ser maior que zero
- Nome não pode estar em branco

## 🧪 Testes

```bash
# Rodar todos os testes
./mvnw test

# Rodar com cobertura
./mvnw test jacoco:report
```

**Cenários testados:**
- ✅ Conta sem atraso
- ✅ Atraso até 3 dias (2% multa + 0,1% juros)
- ✅ Atraso exatamente 3 dias
- ✅ Atraso de 4 dias (3% multa + 0,2% juros)
- ✅ Atraso de 5 dias
- ✅ Atraso acima de 5 dias (5% multa + 0,3% juros)
- ✅ Listagem de contas
- ✅ Persistência dos dados calculados

## 🗄️ Banco de Dados

### Estrutura da Tabela `contas`

```sql
CREATE TABLE contas (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    valor_original NUMERIC(10,2) NOT NULL,
    data_vencimento DATE NOT NULL,
    data_pagamento DATE NOT NULL,
    dias_atraso INTEGER NOT NULL,
    percentual_multa NUMERIC(5,2) NOT NULL,
    percentual_juros_dia NUMERIC(5,3) NOT NULL,
    valor_corrigido NUMERIC(10,2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### Índices

```sql
CREATE INDEX idx_contas_data_vencimento ON contas(data_vencimento);
CREATE INDEX idx_contas_data_pagamento ON contas(data_pagamento);
```

### Acessar PostgreSQL

```bash
# Via Docker
docker exec -it contaspagar-db psql -U postgres -d contaspagar

# Comandos úteis
\dt                           # Listar tabelas
\d contas                     # Descrever tabela contas
SELECT * FROM contas;         # Listar dados
\q                            # Sair
```

## 📦 Build

```bash
# Gerar JAR
./mvnw clean package

# Build da imagem Docker
docker build -t contas-pagar:latest .

# Rodar container
docker run -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/contaspagar \
  -e SPRING_DATASOURCE_USERNAME=postgres \
  -e SPRING_DATASOURCE_PASSWORD=postgres \
  contas-pagar:latest
```

## 🛑 Parar Aplicação

```bash
# Parar containers
docker-compose down

# Parar e remover volumes (limpa banco de dados)
docker-compose down -v
```

## 📁 Estrutura do Projeto

```
contas-pagar/
├── src/
│   ├── main/
│   │   ├── java/br/com/deliverit/contas_pagar/
│   │   │   ├── controller/      # REST Controllers
│   │   │   ├── dto/              # Data Transfer Objects
│   │   │   ├── entity/           # Entidades JPA
│   │   │   ├── exception/        # Exception Handlers
│   │   │   ├── repository/       # Repositories
│   │   │   ├── service/          # Lógica de Negócio
│   │   │   └── ContasPagarApplication.java
│   │   └── resources/
│   │       ├── db/migration/     # Scripts Flyway
│   │       ├── application.yaml  # Configurações
│   │       └── application-test.yaml
│   └── test/
│       └── java/                 # Testes Unitários
├── frontend/                     # Aplicação Angular
│   ├── src/
│   │   ├── app/
│   │   │   ├── app.ts           # Componente Principal
│   │   │   ├── app.html         # Template
│   │   │   ├── app.css          # Estilos
│   │   │   ├── app.config.ts    # Configuração
│   │   │   └── services/        # Serviços HTTP
│   │   ├── styles.css           # Estilos Globais
│   │   └── index.html
│   ├── package.json
│   └── angular.json
├── Dockerfile
├── docker-compose.yml
├── .dockerignore
├── .gitignore
├── pom.xml
└── README.md
```

## 👨‍💻 Desenvolvimento

### Adicionar nova Migration

1. Criar arquivo em `src/main/resources/db/migration/`
2. Nomenclatura: `V{numero}__{descricao}.sql`
3. Exemplo: `V2__add_column_observacao.sql`

### Rodar aplicação em modo debug

```bash
./mvnw spring-boot:run -Dspring-boot.run.jvmArguments="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005"
```

## 🔍 Ferramentas de Banco de Dados

Você pode usar qualquer client PostgreSQL para visualizar os dados:

- **Postbird** (macOS/Linux/Windows)
- **DBeaver** (multiplataforma)
- **pgAdmin** (oficial PostgreSQL)

**Configuração:**
- Host: `localhost`
- Port: `5432`
- Database: `contaspagar`
- Username: `postgres`
- Password: `postgres`

## 📄 Licença

Projeto desenvolvido como teste técnico para Deliver IT.

## ✉️ Contato

Para dúvidas ou sugestões, entre em contato.
