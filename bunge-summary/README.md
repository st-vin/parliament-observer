# Bunge Summary — Application

Spring Boot 3.3 service for parliamentary Hansard summaries (Kenya National Assembly MVP).

## Prerequisites

- Java 21+
- Maven 3.9+
- Docker Desktop (for local PostgreSQL + pgvector)

## Local development

### 1. Configure environment and Spring YAML

From this directory (`bunge-summary/`):

```bash
cp .env.example .env
cp src/main/resources/application.yml.example src/main/resources/application.yml
cp src/main/resources/application-prod.yml.example src/main/resources/application-prod.yml
```

Edit `.env` and set at least `DB_PASSWORD`, `GEMINI_API_KEY`, `RESEND_API_KEY`, and a random `JWT_SECRET` (32+ characters).  
The dev profile loads `.env` automatically (`optional:file:.env[.properties]`). Database credentials and JWT are **not** stored in committed YAML — only in `.env` (gitignored).

`application.yml` and `application-prod.yml` are gitignored; the `*.example` files are the templates to copy.

**Important:** `SPRING_DATASOURCE_URL` must use port **5433**, not 5432, unless you have stopped your local PostgreSQL service and remapped Docker to 5432 yourself.

| Setting  | Source |
|----------|--------|
| Host     | `localhost` (via `SPRING_DATASOURCE_URL` in `.env`) |
| Port     | `5433` |
| Database | `bunge_summary` |
| User / password | `DB_USERNAME` / `DB_PASSWORD` in `.env` |

### 2. Start the database

Requires `.env` (Docker Compose reads `DB_USERNAME` and `DB_PASSWORD` from it):

```bash
docker compose up -d
```

Postgres listens on **host port `5433`** (container `5432`). We use 5433 deliberately so the app does not connect to a **system PostgreSQL** service that may already be bound to `5432` (common on Windows).

Check the container is healthy:

```bash
docker compose ps
docker exec bunge-postgres pg_isready -U bunge -d bunge_summary
```

(Use your `DB_USERNAME` value if it is not `bunge`.)

### 3. Run the application

```bash
mvn spring-boot:run
```

Active profiles by default: `dev` + `api` (JWT auth enabled).

Server port comes from `PORT` in `.env` (default **8080**). If 8080 is in use, set e.g. `PORT=8085` and use that in URLs below.

- API base: `http://localhost:8085` when `PORT=8085` in `.env` (default would be 8080)
- Register: `POST /api/v1/auth/register`
- Login: `POST /api/v1/auth/login`
- Public sittings: `GET /api/v1/sittings`

### 4. Run tests

```bash
mvn test
```

Flyway integration tests that use Testcontainers are skipped when Docker is unavailable.

---

## Troubleshooting

### `FATAL: password authentication failed for user "bunge"`

**Cause:** The app is connecting to the wrong PostgreSQL instance—almost always **host port 5432** (Windows/macOS system Postgres) instead of **Docker on port 5433**.

**Fix:**

1. Ensure `.env` has:
   ```
   SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5433/bunge_summary
   DB_USERNAME=bunge
   DB_PASSWORD=bunge
   ```
2. Recreate the stack so port mapping applies:
   ```bash
   docker compose down
   docker compose up -d
   ```
3. Confirm nothing else is required on 5433:
   ```bash
   docker compose ps
   ```

On Windows, check for a port clash:

```powershell
netstat -ano | findstr ":5432"
netstat -ano | findstr ":5433"
```

If `postgres.exe` owns 5432, leave it running; the app should only use **5433** for Docker.

**Alternative:** Stop the system PostgreSQL service and map Docker to `5432:5432` in `docker-compose.yml`, then set `SPRING_DATASOURCE_URL` back to port 5432. The project default is 5433 to avoid this conflict.

### Stale Docker volume / wrong password after changing compose env

Postgres only applies `POSTGRES_USER` / `POSTGRES_PASSWORD` on **first** volume init. If you changed credentials in `.env` but kept an old volume:

```bash
docker compose down -v
docker compose up -d
```

This deletes local DB data; Flyway will re-run migrations on next app start.

### Docker not running

`docker compose up` and Testcontainers-based tests fail. Start Docker Desktop first.

---

## Configuration reference

| Variable | Purpose |
|----------|---------|
| `SPRING_DATASOURCE_URL` | JDBC URL (dev default: `localhost:5433`) |
| `DB_USERNAME` / `DB_PASSWORD` | Postgres credentials |
| `JWT_SECRET` | HS256 signing key (required in prod) |
| `GEMINI_API_KEY` | Google AI Studio |
| `RESEND_API_KEY` | Transactional email |

See `.env.example` for the full list.
