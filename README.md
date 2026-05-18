# Bunge Summary — SDLC + Build Pipeline

Parliamentary intelligence platform: Hansard PDFs → plain-English summaries for Kenyan citizens.

## Repository layout

| Path | Purpose |
|------|---------|
| `01_BRD_*.md` … `14_*.md` | Full SDLC documentation set |
| `doc_store.json` | Chunked docs for AI agents (Phase 1) |
| `build_plan.json` | Ticket queue for specialist agents (Phase 2) |
| `Progress.md` | Persistent agent memory — read first every session |
| `bunge-summary/` | Spring Boot application source |
| `scripts/` | Ingestion, planning, validation, orchestrator |

## Quick start (application)

```bash
cd bunge-summary
cp .env.example .env   # edit secrets; DB URL must use port 5433
docker compose up -d
mvn spring-boot:run
```

Postgres runs on **host port 5433** (not 5432) to avoid conflicting with a system PostgreSQL install.  
See [bunge-summary/README.md](bunge-summary/README.md) for full local setup and troubleshooting.

See [Progress.md](Progress.md) for stack, env vars, and sprint history.

## Pipeline skill

Agent workflow is defined in [.agent/skills/sdlc-to-product/SKILL.md](.agent/skills/sdlc-to-product/SKILL.md).

**Current status:** Phase 3 in progress · Sprints 1–4 verified · **Sprint 5 active** (notifications, launch polish, deploy — T-050–T-063).

## Web UI (Sprint 4)

With the app running (`dev` profile includes `api`):

| URL | Description |
|-----|-------------|
| http://localhost:8080/ | Home — recent proceedings, browse links |
| http://localhost:8080/browse/date | Calendar browse |
| http://localhost:8080/browse/topic | Topic grid |
| http://localhost:8080/login | Sign in (JWT cookie) |
| http://localhost:8080/profile | Topic subscriptions (authenticated) |
