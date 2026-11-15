# Gym Leveling

Monorepo gồm backend Spring Boot và app mobile Flutter.

## Chạy hạ tầng local (Postgres + Redis)

```bash
cp .env.example .env
docker compose -f infra/docker-compose.yml --env-file .env up -d
docker compose -f infra/docker-compose.yml ps
```

Postgres chạy ở `localhost:5432` (db `gymleveling`), Redis ở `localhost:6379`.
