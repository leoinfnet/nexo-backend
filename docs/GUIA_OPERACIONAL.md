# Guia operacional do Nexo Backend

Este guia descreve como preparar, iniciar, validar e encerrar o ambiente local do Nexo.

## 1. Componentes do ambiente

O ambiente atual é composto por:

- Nexo Backend com Spring Boot 4 e Java 25;
- PostgreSQL 18 executado em Docker;
- Flyway para criação e evolução do banco;
- massa didática de lançamentos, parceiros e contas.

O Qdrant ainda não faz parte deste ambiente. Ele será incluído quando o módulo de RAG for implementado.

## 2. Pré-requisitos

Instale e deixe disponíveis no terminal:

- Java 25;
- Docker Desktop ou Docker Engine com Compose;
- Git;
- `curl`, para as verificações manuais.

O Maven não precisa ser instalado. O projeto utiliza o Maven Wrapper (`mvnw`).

Confirme as versões:

```bash
java -version
docker --version
docker compose version
```

A saída de `java -version` deve indicar a versão 25.

### Java com SDKMAN

Se o Java 25 estiver instalado pelo SDKMAN:

```bash
sdk list java
sdk use java 25.0.1-amzn
java -version
```

O identificador da versão pode mudar conforme a versão instalada na máquina.

## 3. Acessar o projeto

Entre na raiz do backend:

```bash
cd /Users/leonardogloria/Documents/Projetos/Infnet/agentes/nexo/nexo-backend
```

Todos os comandos deste guia consideram que o terminal está nesse diretório.

## 4. Iniciar o PostgreSQL

Execute:

```bash
docker compose -f infra/compose/compose.yaml up -d --wait postgres
```

O parâmetro `--wait` aguarda o health check do PostgreSQL.

Confira o estado:

```bash
docker compose -f infra/compose/compose.yaml ps
```

O container `nexo-postgres` deve aparecer como `healthy`.

### Configuração local do banco

| Propriedade | Valor |
|---|---|
| Host | `localhost` |
| Porta | `5432` |
| Banco | `nexo` |
| Usuário | `nexo` |
| Senha | `nexo` |
| JDBC URL | `jdbc:postgresql://localhost:5432/nexo` |

Essas credenciais são apenas para desenvolvimento local.

## 5. Iniciar o backend

Com o PostgreSQL saudável, execute:

```bash
./mvnw spring-boot:run
```

Na primeira execução, o Maven poderá baixar dependências. O Flyway será executado automaticamente antes da validação do Hibernate.

Uma inicialização bem-sucedida apresenta mensagens semelhantes a:

```text
Successfully validated 5 migrations
Schema "public" is up to date
Tomcat started on port 8080
Started NexoBackendApplication
```

Na primeira inicialização de um banco vazio, o Flyway executa:

| Migration | Finalidade |
|---|---|
| `V1` | Cria a tabela de lançamentos |
| `V2` | Insere lançamentos dos últimos três meses |
| `V3` | Cria a tabela de parceiros |
| `V4` | Cria a tabela de contas |
| `V5` | Insere parceiros e contas didáticas |

O backend fica disponível em:

```text
http://localhost:8080
```

Mantenha esse terminal aberto enquanto estiver utilizando a API.

## 6. Validar o ambiente

Abra outro terminal na raiz do projeto.

### Saúde da aplicação

```bash
curl -fsS http://localhost:8080/actuator/health
```

Resposta esperada:

```json
{"status":"UP"}
```

### Resumo financeiro

```bash
curl -fsS 'http://localhost:8080/api/v1/relatorios/financeiro/resumo'
```

### Lançamentos paginados

```bash
curl -fsS 'http://localhost:8080/api/v1/lancamentos?page=0&size=5'
```

### Despesas de software

```bash
curl -fsS 'http://localhost:8080/api/v1/lancamentos?tipo=DESPESA&categoria=Software&page=0&size=10'
```

### Parceiros fornecedores

```bash
curl -fsS 'http://localhost:8080/api/v1/parceiros?tipo=FORNECEDOR&page=0&size=10'
```

### Contas vencidas

```bash
curl -fsS 'http://localhost:8080/api/v1/contas?status=VENCIDA&page=0&size=10'
```

## 7. Endpoints disponíveis

### Lançamentos

| Método | Endpoint | Operação |
|---|---|---|
| `POST` | `/api/v1/lancamentos` | Cadastrar lançamento |
| `GET` | `/api/v1/lancamentos` | Listar e filtrar lançamentos |
| `GET` | `/api/v1/lancamentos/{id}` | Consultar lançamento |
| `PUT` | `/api/v1/lancamentos/{id}` | Atualizar lançamento |
| `DELETE` | `/api/v1/lancamentos/{id}` | Excluir lançamento |

Filtros disponíveis na listagem:

- `tipo`;
- `categoria`;
- `dataInicio`;
- `dataFim`;
- `valorMinimo`;
- `valorMaximo`;
- `page`, `size` e `sort`.

### Relatórios financeiros

| Método | Endpoint |
|---|---|
| `GET` | `/api/v1/relatorios/financeiro/resumo` |
| `GET` | `/api/v1/relatorios/financeiro/despesas-por-categoria` |
| `GET` | `/api/v1/relatorios/financeiro/fluxo-diario` |
| `GET` | `/api/v1/relatorios/financeiro/maiores-despesas` |

Os relatórios aceitam `dataInicio` e `dataFim` no formato `AAAA-MM-DD`. Sem datas, consideram os últimos 30 dias.

### Parceiros

| Método | Endpoint | Operação |
|---|---|---|
| `POST` | `/api/v1/parceiros` | Cadastrar cliente ou fornecedor |
| `GET` | `/api/v1/parceiros` | Listar parceiros |
| `GET` | `/api/v1/parceiros/{id}` | Consultar parceiro |
| `PUT` | `/api/v1/parceiros/{id}` | Atualizar parceiro |
| `DELETE` | `/api/v1/parceiros/{id}` | Excluir parceiro sem contas vinculadas |

Tipos disponíveis: `CLIENTE`, `FORNECEDOR` e `AMBOS`.

### Contas

| Método | Endpoint | Operação |
|---|---|---|
| `POST` | `/api/v1/contas` | Cadastrar conta |
| `GET` | `/api/v1/contas` | Listar e filtrar contas |
| `GET` | `/api/v1/contas/{id}` | Consultar conta |
| `PUT` | `/api/v1/contas/{id}` | Atualizar conta pendente |
| `POST` | `/api/v1/contas/{id}/liquidacao` | Liquidar e gerar lançamento |
| `POST` | `/api/v1/contas/{id}/cancelamento` | Cancelar conta pendente |
| `DELETE` | `/api/v1/contas/{id}` | Excluir conta não liquidada |

Tipos disponíveis: `PAGAR` e `RECEBER`.

Situações disponíveis para consulta: `PENDENTE`, `PAGA`, `VENCIDA` e `CANCELADA`.

## 8. Configuração por variáveis de ambiente

O backend utiliza os seguintes valores padrão:

```text
DB_URL=jdbc:postgresql://localhost:5432/nexo
DB_USERNAME=nexo
DB_PASSWORD=nexo
```

Para usar outra instância do PostgreSQL:

```bash
DB_URL='jdbc:postgresql://servidor:5432/nexo' \
DB_USERNAME='usuario' \
DB_PASSWORD='senha' \
./mvnw spring-boot:run
```

## 9. Consultar o PostgreSQL pelo container

Abra o terminal do `psql`:

```bash
docker compose -f infra/compose/compose.yaml exec postgres psql -U nexo -d nexo
```

Algumas consultas úteis:

```sql
SELECT version, description, success
FROM flyway_schema_history
ORDER BY installed_rank;

SELECT tipo, COUNT(*), SUM(valor)
FROM lancamentos
GROUP BY tipo;

SELECT tipo, status, COUNT(*)
FROM contas
GROUP BY tipo, status
ORDER BY tipo, status;

SELECT tipo, COUNT(*)
FROM parceiros
GROUP BY tipo;
```

Para sair do `psql`:

```text
\q
```

## 10. Encerrar o ambiente

Encerre primeiro o backend com `Ctrl+C` no terminal onde ele está executando.

Depois pare o PostgreSQL:

```bash
docker compose -f infra/compose/compose.yaml down
```

Esse comando preserva o volume e os dados. Na próxima execução, o banco continuará no mesmo estado.

## 11. Recriar o banco do zero

> **Atenção:** o comando abaixo apaga permanentemente todos os dados locais do PostgreSQL do Nexo.

Encerre a aplicação e execute:

```bash
docker compose -f infra/compose/compose.yaml down --volumes
docker compose -f infra/compose/compose.yaml up -d --wait postgres
./mvnw spring-boot:run
```

O Flyway recriará o schema e carregará novamente toda a massa didática.

## 12. Solução de problemas

### `release version 25 not supported`

O terminal está usando um JDK anterior ao Java 25.

```bash
java -version
sdk use java 25.0.1-amzn
```

### Porta `5432` já está em uso

Verifique se existe outro PostgreSQL ou container utilizando a porta:

```bash
docker ps
```

Pare o serviço conflitante ou altere o mapeamento de porta no Compose e ajuste `DB_URL`.

### Backend não conecta ao PostgreSQL

Confira se o container está saudável:

```bash
docker compose -f infra/compose/compose.yaml ps
docker compose -f infra/compose/compose.yaml logs --tail=100 postgres
```

Não inicie o backend antes de o PostgreSQL ficar `healthy`.

### Migration do Flyway falhou

Consulte os logs completos da aplicação e não edite uma migration que já tenha sido aplicada em um banco compartilhado. Crie uma nova migration com o próximo número de versão.

Em um ambiente estritamente local e descartável, recrie o volume conforme a seção anterior.

### Porta `8080` já está em uso

Encerre a aplicação que está utilizando a porta ou inicie o Nexo em outra porta:

```bash
SERVER_PORT=8081 ./mvnw spring-boot:run
```

Nesse caso, substitua `8080` por `8081` nas chamadas da API.
