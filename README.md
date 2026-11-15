# Gym Leveling

Monorepo gồm backend Spring Boot và app mobile Flutter.

## Cấu trúc thư mục

- `backend/` — API Spring Boot (Java 21, Gradle, Flyway, Postgres, Redis).
- `mobile/` — app Flutter (Riverpod, go_router, l10n vi/en).
- `infra/` — Docker Compose cho Postgres và Redis local.
- `design/` — thiết kế UI (DESIGN.md, file `.pen`, màn hình).
- `docs/` — tài liệu dự án và kế hoạch.

## Yêu cầu môi trường

- Docker (chạy Postgres/Redis và Testcontainers khi test backend).
- JDK 21.
- Flutter 3.50.x stable (kèm Android SDK nếu build Android).

## Chạy hạ tầng local (Postgres + Redis)

```bash
cp .env.example .env
docker compose -f infra/docker-compose.yml --env-file .env up -d
docker compose -f infra/docker-compose.yml --env-file .env ps
```

Postgres chạy ở `127.0.0.1:5432` (db `gymleveling`), Redis ở `127.0.0.1:6379`. Các cổng chỉ mở trên loopback.

Compose yêu cầu `POSTGRES_DB`, `POSTGRES_USER`, `POSTGRES_PASSWORD` trong `.env`. Nếu đổi `POSTGRES_PASSWORD`, hãy đổi `DB_PASSWORD` cùng giá trị trong `.env`.

## Backend

Nạp `.env` vào shell trước khi chạy:

```bash
set -a; . ./.env; set +a
cd backend && ./gradlew bootRun
```

Kiểm tra:

```bash
curl localhost:8080/actuator/health
curl -H 'Accept-Language: vi' localhost:8080/api/v1/ping
```

Chạy test (cần Docker, dùng Testcontainers):

```bash
cd backend && ./gradlew test
```

## Mobile

```bash
cd mobile && flutter pub get
flutter run --dart-define=API_BASE_URL=<url>
```

URL theo thiết bị:

- Android emulator: `http://10.0.2.2:8080` (mặc định)
- iOS simulator: `http://localhost:8080`
- Thiết bị thật: `http://<IP-LAN-của-máy-bạn>:8080`

Chạy test: `cd mobile && flutter test`.
