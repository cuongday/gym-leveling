# Giai đoạn 0 — Nền móng: Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Dựng monorepo chạy được end-to-end: Postgres + Redis bằng Docker Compose, backend Spring Boot có Flyway, xử lý lỗi chuẩn và song ngữ vi/en, app Flutter gọi được API, CI build + test cả hai phía.

**Architecture:** Monorepo `backend/` (Spring Boot modular monolith, Spring Modulith kiểm tra ranh giới module), `mobile/` (Flutter + Riverpod + Dio + go_router), `infra/` (Docker Compose). Giai đoạn này chỉ có một endpoint `GET /api/v1/ping` để chứng minh đường đi app → API → locale, chưa có tính năng nghiệp vụ.

**Tech Stack:** Java 21, Spring Boot 4.x (GA mới nhất từ start.spring.io), Gradle Kotlin DSL, Spring Modulith, Spring Data JPA, Flyway, PostgreSQL 16, Redis 7, Testcontainers, springdoc-openapi (dòng hỗ trợ Boot 4); Flutter stable, flutter_riverpod, dio, go_router, flutter_localizations + intl; GitHub Actions.

**Spec:** `docs/superpowers/specs/2026-11-15-gym-leveling-design.md`

## Global Constraints

- Không dùng Firebase/Supabase cho backend, DB hay auth.
- Java package gốc: `com.gymleveling`. Mỗi module nghiệp vụ là một package con trực tiếp (`com.gymleveling.auth`, `...quest`…); module chỉ gọi nhau qua API công khai của module.
- Mọi API nằm dưới `/api/v1`.
- Lỗi API trả về RFC 7807 Problem Details (`application/problem+json`).
- Song ngữ: chỉ hỗ trợ `vi` và `en`. Locale không hỗ trợ hoặc không có → `en`. `vi-VN` → `vi`.
- Cấu hình nhạy cảm (mật khẩu DB, secret) lấy từ biến môi trường; repo chỉ chứa `.env.example`.
- Màu, chữ, khoảng cách, bo góc của app lấy từ `design/DESIGN.md` (nguồn: `design/gym-leveling.pen`); không tự chế giá trị.
- Mọi con số nghiệp vụ (ngưỡng rank, RP…) để ở cấu hình/DB — không áp dụng ở giai đoạn này nhưng không hard-code khi thêm.

## Review Focus

- Client gửi `Accept-Language` không hỗ trợ (`fr`) hoặc không gửi → phản hồi tiếng Anh, không lỗi. (Task 3, test `ping_fallsBackToEnglish`)
- Client gửi `Accept-Language: vi-VN,vi;q=0.9` → tiếng Việt. (Task 3, test `ping_vietnameseRegionTag`)
- Gọi route không tồn tại → 404 dạng `application/problem+json`, không phải trang HTML Whitelabel. (Task 3, test `unknownRoute_returnsProblemDetail`)
- App mở khi backend không chạy → hiện thông báo lỗi thân thiện có nút thử lại, không crash. (Task 5, test `homeShowsErrorWhenApiFails`)
- Máy để ngôn ngữ ngoài vi/en (ví dụ `ja`) → app hiện tiếng Anh. (Task 5, test `unsupportedDeviceLocaleFallsBackToEnglish`)

---

## Cấu trúc file

```
.gitignore
README.md
.env.example
infra/docker-compose.yml
backend/                          (sinh từ start.spring.io)
  build.gradle.kts
  src/main/java/com/gymleveling/GymLevelingApplication.java
  src/main/java/com/gymleveling/shared/web/PingController.java
  src/main/java/com/gymleveling/shared/web/LocaleConfig.java
  src/main/resources/application.yml
  src/main/resources/messages.properties
  src/main/resources/messages_vi.properties
  src/main/resources/db/migration/V1__baseline.sql
  src/test/java/com/gymleveling/TestcontainersConfiguration.java
  src/test/java/com/gymleveling/ApplicationTests.java
  src/test/java/com/gymleveling/ModularityTests.java
  src/test/java/com/gymleveling/shared/web/PingControllerTest.java
mobile/                           (sinh từ flutter create)
  pubspec.yaml
  l10n.yaml
  lib/main.dart
  lib/app.dart
  lib/core/config.dart
  lib/core/api/api_client.dart
  lib/core/api/api_exception.dart
  lib/core/l10n/app_en.arb
  lib/core/l10n/app_vi.arb
  lib/core/router.dart
  lib/core/theme.dart
  lib/features/home/home_screen.dart
  lib/features/home/ping_provider.dart
  test/core/api/api_client_test.dart
  test/features/home/home_screen_test.dart
.github/workflows/ci.yml
```

---

### Task 0: Chuẩn bị máy (không có code)

Máy hiện chưa có JDK và Flutter; Docker đã có.

- [ ] **Step 1: Cài JDK 21** (khuyên dùng SDKMAN: `sdk install java 21-tem`). Kiểm tra: `java -version` → `21.x`.
- [ ] **Step 2: Cài Flutter stable** theo hướng dẫn chính thức cho Linux, thêm vào PATH. Kiểm tra: `flutter doctor` không có lỗi ở mục Flutter và Android toolchain (iOS build cần máy Mac — để sau).
- [ ] **Step 3: Cấu hình git identity** nếu chưa có: `git config user.name "..."` và `git config user.email "..."`.

---

### Task 1: Monorepo + Docker Compose

**Files:**
- Create: `.gitignore`, `README.md`, `.env.example`, `infra/docker-compose.yml`

**Interfaces:**
- Produces: Postgres ở `localhost:5432` (db `gymleveling`), Redis ở `localhost:6379`; biến môi trường `DB_URL`, `DB_USER`, `DB_PASSWORD`, `REDIS_HOST`, `REDIS_PORT` (tên dùng ở Task 2).

- [ ] **Step 1: Viết `infra/docker-compose.yml`** với service `postgres` (`postgres:16-alpine`, db/user/password lấy từ `.env`, volume `pgdata`, healthcheck `pg_isready`) và `redis` (`redis:7-alpine`, healthcheck `redis-cli ping`). `.env.example` chứa `POSTGRES_DB=gymleveling`, `POSTGRES_USER=gym`, `POSTGRES_PASSWORD=change-me` và 5 biến ở trên. `.gitignore` gồm `.env`, `build/`, `.gradle/`, `.idea/`, `.dart_tool/`, `mobile/build/`. `README.md`: cách chạy (`cp .env.example .env`, `docker compose -f infra/docker-compose.yml --env-file .env up -d`).
- [ ] **Step 2: Chạy và kiểm tra**

Run: `cp .env.example .env && docker compose -f infra/docker-compose.yml --env-file .env up -d && docker compose -f infra/docker-compose.yml ps`
Expected: cả hai service `healthy`.

- [ ] **Step 3: Commit**

```bash
git add .gitignore README.md .env.example infra/
git commit -m "chore: add monorepo layout and local Postgres/Redis"
```

---

### Task 2: Khung backend Spring Boot

**Files:**
- Create: toàn bộ `backend/` từ start.spring.io; `application.yml`, `V1__baseline.sql`, `ModularityTests.java`; sửa `ApplicationTests.java`

**Interfaces:**
- Consumes: biến môi trường của Task 1.
- Produces: app khởi động được; `TestcontainersConfiguration` (Postgres + Redis qua `@ServiceConnection`) để mọi test tích hợp sau `@Import`.

- [ ] **Step 1: Sinh project**

```bash
mkdir backend && curl -s https://start.spring.io/starter.tgz \
  -d type=gradle-project-kotlin -d language=java -d javaVersion=21 \
  -d groupId=com.gymleveling -d artifactId=backend -d name=GymLeveling \
  -d packageName=com.gymleveling \
  -d dependencies=web,data-jpa,validation,flyway,postgresql,data-redis,actuator,testcontainers,modulith \
  | tar -xzf - -C backend
```

Sau đó thêm springdoc (`org.springdoc:springdoc-openapi-starter-webmvc-ui`, bản tương thích Boot 4) vào `build.gradle.kts`. Đổi tên class main thành `GymLevelingApplication` nếu khác.

- [ ] **Step 2: Viết test thất bại**

`ApplicationTests.contextLoads_andBaselineMigrationApplied`: `@SpringBootTest` + `@Import(TestcontainersConfiguration.class)`, inject `JdbcTemplate`, assert
`select count(*) from flyway_schema_history where version = '1' and success` == 1.

`ModularityTests.verifiesModuleStructure`: `ApplicationModules.of(GymLevelingApplication.class).verify();`

- [ ] **Step 3: Chạy test, xác nhận fail**

Run: `cd backend && ./gradlew test`
Expected: `contextLoads_andBaselineMigrationApplied` FAIL (chưa có migration V1).

- [ ] **Step 4: Thêm cấu hình**

`V1__baseline.sql` chứa `-- baseline: schema bắt đầu từ V2 trở đi`. `application.yml`: datasource từ `${DB_URL:jdbc:postgresql://localhost:5432/gymleveling}`, `${DB_USER:gym}`, `${DB_PASSWORD:change-me}`; redis từ `${REDIS_HOST:localhost}`/`${REDIS_PORT:6379}`; `spring.jpa.hibernate.ddl-auto: validate`; `spring.jpa.open-in-view: false`; `spring.mvc.problemdetails.enabled: true`; actuator chỉ expose `health`. Đảm bảo `TestcontainersConfiguration` dùng image `postgres:16-alpine` và `redis:7-alpine`.

- [ ] **Step 5: Chạy test, xác nhận pass**

Run: `cd backend && ./gradlew test`
Expected: BUILD SUCCESSFUL, 2 test pass.

- [ ] **Step 6: Kiểm tra chạy thật**

Run: `cd backend && ./gradlew bootRun` (với Docker Compose đang chạy), rồi `curl -s localhost:8080/actuator/health`
Expected: `{"status":"UP"...}`

- [ ] **Step 7: Commit**

```bash
git add backend/
git commit -m "feat(backend): scaffold Spring Boot app with Flyway and Testcontainers"
```

---

### Task 3: Song ngữ, lỗi chuẩn và endpoint ping

**Files:**
- Create: `shared/web/LocaleConfig.java`, `shared/web/PingController.java`, `messages.properties`, `messages_vi.properties`
- Test: `shared/web/PingControllerTest.java`

**Interfaces:**
- Consumes: `TestcontainersConfiguration` (Task 2).
- Produces: `GET /api/v1/ping` → `200 {"status":"ok","message":"<localized>"}`; bean `LocaleResolver` (Accept-Language, hỗ trợ `en`,`vi`, mặc định `en`) mà mọi controller sau dùng qua `MessageSource`. `com.gymleveling.shared` là module dùng chung (đánh dấu `@ApplicationModule(type = Type.OPEN)` trong `package-info.java`).

- [ ] **Step 1: Viết test thất bại** — `@SpringBootTest` + `@AutoConfigureMockMvc` + `@Import(TestcontainersConfiguration.class)`:

```java
@Test void ping_english() {
  mvc.perform(get("/api/v1/ping").header("Accept-Language", "en"))
     .andExpect(status().isOk())
     .andExpect(jsonPath("$.status").value("ok"))
     .andExpect(jsonPath("$.message").value("Hello from Gym Leveling"));
}
@Test void ping_vietnamese()          // "vi"            → "Xin chào từ Gym Leveling"
@Test void ping_vietnameseRegionTag() // "vi-VN,vi;q=0.9" → "Xin chào từ Gym Leveling"
@Test void ping_fallsBackToEnglish()  // "fr" và không có header → "Hello from Gym Leveling"
@Test void unknownRoute_returnsProblemDetail() {
  mvc.perform(get("/api/v1/does-not-exist"))
     .andExpect(status().isNotFound())
     .andExpect(content().contentTypeCompatibleWith("application/problem+json"))
     .andExpect(jsonPath("$.status").value(404));
}
```

- [ ] **Step 2: Chạy test, xác nhận fail**

Run: `cd backend && ./gradlew test --tests '*PingControllerTest'`
Expected: FAIL (404 cho `/api/v1/ping`).

- [ ] **Step 3: Implement**

`LocaleConfig`: bean `LocaleResolver` = `AcceptHeaderLocaleResolver` với `setSupportedLocales(List.of(Locale.ENGLISH, Locale.forLanguageTag("vi")))`, `setDefaultLocale(Locale.ENGLISH)`. `messages.properties`: `ping.message=Hello from Gym Leveling`; `messages_vi.properties`: `ping.message=Xin chào từ Gym Leveling` (UTF-8; đặt `spring.messages.encoding: UTF-8`, `spring.messages.fallback-to-system-locale: false`). `PingController` trả `record PingResponse(String status, String message)` dùng `MessageSource.getMessage("ping.message", null, LocaleContextHolder.getLocale())`.

- [ ] **Step 4: Chạy toàn bộ test, xác nhận pass**

Run: `cd backend && ./gradlew test`
Expected: BUILD SUCCESSFUL (gồm `ModularityTests`).

- [ ] **Step 5: Commit**

```bash
git add backend/
git commit -m "feat(backend): add vi/en locale resolution, problem details, ping endpoint"
```

---

### Task 4: Khung app Flutter

**Files:**
- Create: `mobile/` từ `flutter create`; `l10n.yaml`, `lib/app.dart`, `lib/core/{config,router,theme}.dart`, `lib/core/l10n/app_{en,vi}.arb`, `lib/features/home/home_screen.dart`; sửa `lib/main.dart`

**Interfaces:**
- Produces: `GymLevelingApp` (widget gốc, `ProviderScope` bọc ngoài ở `main.dart`) nhận tham số tuỳ chọn `Locale? locale` (dùng cho test và cho nút chuyển ngôn ngữ ở plan sau); `AppConfig.apiBaseUrl` đọc từ `--dart-define=API_BASE_URL`, mặc định `http://10.0.2.2:8080` (máy ảo Android trỏ về host); `AppLocalizations` với key `appTitle`, `homeGreeting`, `errorGeneric`, `retry`.

- [ ] **Step 1: Sinh project và thêm package**

```bash
flutter create --org com.gymleveling --project-name gym_leveling --platforms android,ios mobile
cd mobile && flutter pub add flutter_riverpod dio go_router intl google_fonts \
  && flutter pub add flutter_localizations --sdk=flutter \
  && flutter pub add --dev http_mock_adapter
```

Bật `generate: true` trong `pubspec.yaml`; `l10n.yaml`: `arb-dir: lib/core/l10n`, `template-arb-file: app_en.arb`, `output-class: AppLocalizations`. Xoá test counter mặc định.

- [ ] **Step 2: Implement khung**

ARB: `appTitle` = "Gym Leveling" (cả hai); `retry` = "Retry" / "Thử lại"; `errorGeneric` = "Can't connect to server" / "Không kết nối được máy chủ". `GymLevelingApp`: `MaterialApp.router` với `supportedLocales: [Locale('en'), Locale('vi')]`, `locale: locale`, `localeResolutionCallback` trả `vi` nếu `languageCode == 'vi'`, còn lại `en`. `router.dart`: go_router với `/` → `HomeScreen`. `theme.dart`: `ThemeData` Material 3, light + dark, **giá trị lấy đúng từ `design/DESIGN.md`**: `ColorScheme` sáng/tối (primary `#FFA21F`/`#FFAA2C`, onPrimary `#1E1406`, surface `#FFFFFF`/`#181613`, scaffold bg `#FAF6EF`/`#0E0D0B`, error = token `danger`) + `ThemeExtension<GymColors>` chứa toàn bộ token còn lại (`surface2`, `border`, `textSecondary`, `textMuted`, `primaryText`, `primarySoft`, `accent`, `accentSoft`, `success`, `warning`, `info`, 7 màu rank) + hằng số `Gap` (4/8/12/16/24/32) và `Radii` (8/12/20/999). Font: Chakra Petch (display/heading) và Be Vietnam Pro (body) qua `google_fonts`. Test `test/core/theme_test.dart`: `lightTheme.colorScheme.primary == Color(0xFFFFA21F)`, `darkTheme.scaffoldBackgroundColor == Color(0xFF0E0D0B)`, `lightTheme.extension<GymColors>()!.rankMaster == Color(0xFF8A35D0)`. `HomeScreen` tạm hiện `appTitle` ở AppBar (nội dung thật thêm ở Task 5).

- [ ] **Step 3: Kiểm tra**

Run: `cd mobile && flutter analyze && flutter build apk --debug`
Expected: `No issues found!` và build thành công.

- [ ] **Step 4: Commit**

```bash
git add mobile/
git commit -m "feat(mobile): scaffold Flutter app with Riverpod, go_router, vi/en l10n"
```

---

### Task 5: App gọi API ping

**Files:**
- Create: `lib/core/api/api_client.dart`, `lib/core/api/api_exception.dart`, `lib/features/home/ping_provider.dart`; sửa `home_screen.dart`
- Test: `test/core/api/api_client_test.dart`, `test/features/home/home_screen_test.dart`

**Interfaces:**
- Consumes: `AppConfig.apiBaseUrl`, `GymLevelingApp`, `AppLocalizations` (Task 4).
- Produces:
  - `class ApiClient { ApiClient(Dio dio); Future<PingResult> ping(); }` — Dio có interceptor gắn header `Accept-Language` theo locale hiện tại của app (provider `appLocaleProvider`, kiểu `StateProvider<Locale>`).
  - `class ApiException implements Exception { final int? statusCode; final String? detail; }` — mọi `DioException` được đổi sang loại này (đọc `detail` từ body Problem Details nếu có).
  - `dioProvider`, `apiClientProvider` (Riverpod `Provider`), `pingProvider` (`FutureProvider<PingResult>`). Plan sau ghi đè `dioProvider` trong test.
  - `class PingResult { final String status; final String message; }`

- [ ] **Step 1: Viết test thất bại**

`api_client_test.dart` (dùng `DioAdapter` của http_mock_adapter):
- `pingParsesResponse`: mock `GET /api/v1/ping` → 200 `{"status":"ok","message":"Hello from Gym Leveling"}`; expect `result.message == 'Hello from Gym Leveling'`.
- `pingSendsAcceptLanguage`: locale `vi` → request có header `Accept-Language: vi`.
- `problemDetailBecomesApiException`: mock 500 `{"status":500,"detail":"boom"}` → `throwsA(isA<ApiException>().having((e) => e.detail, 'detail', 'boom'))`.
- `connectionErrorBecomesApiException`: mock lỗi kết nối → `throwsA(isA<ApiException>())`, `statusCode == null`.

`home_screen_test.dart` (ghi đè `apiClientProvider` bằng fake):
- `homeShowsServerMessage`: fake trả message "Xin chào từ Gym Leveling" → `find.text('Xin chào từ Gym Leveling')` tìm thấy.
- `homeShowsErrorWhenApiFails`: fake ném `ApiException` → thấy text `errorGeneric` và nút `retry`; bấm `retry` → fake được gọi lần 2.
- `unsupportedDeviceLocaleFallsBackToEnglish`: pump với `tester.platformDispatcher.localesTestValue = [Locale('ja')]` → thấy "Can't connect to server" khi fake ném lỗi.

- [ ] **Step 2: Chạy test, xác nhận fail**

Run: `cd mobile && flutter test`
Expected: FAIL (chưa có `ApiClient`).

- [ ] **Step 3: Implement** các class/provider theo Interfaces. `HomeScreen` dùng `ref.watch(pingProvider).when(...)`: loading → `CircularProgressIndicator`; data → message; error → `errorGeneric` + nút `retry` gọi `ref.invalidate(pingProvider)`.

- [ ] **Step 4: Chạy test, xác nhận pass**

Run: `cd mobile && flutter analyze && flutter test`
Expected: `No issues found!`, tất cả test pass.

- [ ] **Step 5: Kiểm tra end-to-end trên máy ảo Android** (backend đang `bootRun`)

Run: `cd mobile && flutter run`
Expected: màn hình hiện "Hello from Gym Leveling" (hoặc bản tiếng Việt nếu máy ảo để tiếng Việt). Tắt backend, bấm "Thử lại" → hiện thông báo lỗi, app không crash.

- [ ] **Step 6: Commit**

```bash
git add mobile/
git commit -m "feat(mobile): add API client with locale header and error handling"
```

---

### Task 6: CI GitHub Actions

**Files:**
- Create: `.github/workflows/ci.yml`

- [ ] **Step 1: Viết workflow** chạy khi push/PR vào `main`, 2 job, dùng `paths` filter (`backend/**`, `mobile/**`, file workflow):
  - `backend`: `ubuntu-latest`, `actions/setup-java` (temurin 21, cache gradle), `cd backend && ./gradlew build` (Testcontainers dùng Docker có sẵn trên runner).
  - `mobile`: `subosito/flutter-action` (channel stable, cache), `flutter pub get`, `flutter analyze`, `flutter test`.
- [ ] **Step 2: Kiểm tra cú pháp** — Run: `docker run --rm -v "$PWD":/repo -w /repo rhysd/actionlint:latest` → không có lỗi. (Chạy thật khi repo được đẩy lên GitHub.)
- [ ] **Step 3: Commit**

```bash
git add .github/
git commit -m "ci: build and test backend and mobile"
```

---

## Các plan tiếp theo (Giai đoạn 1 — viết khi xong plan này)

Mỗi plan dưới đây là một bản riêng, chạy được và test được độc lập, theo thứ tự:

1. **Auth + User** — đăng ký/đăng nhập, JWT access + refresh, Spring Security, hồ sơ, locale, timezone (đổi 1 lần/30 ngày), chế độ tạm nghỉ.
2. **Nội dung** — schema catalog (jsonb vi/en), loader file seed YAML có cờ `reviewed`; Claude soạn nháp thư viện bài tập + mẫu giáo án 2–6 buổi/tuần + 3 mẫu nhẹ cho `safety_flag` → gửi PT duyệt (**bắt đầu song song ngay sau plan này**).
3. **Onboarding + chọn giáo án** — câu hỏi, sàng lọc an toàn, điểm 0–12 → rank ban đầu, chấm điểm mẫu → 2 phương án, chọn lịch tuần.
4. **Nhiệm vụ ngày + tăng tiến độ** — buổi kế tiếp trong chuỗi, bắt đầu/hoàn thành bài/kết thúc, Dễ/Vừa/Khó.
5. **Điểm, streak, rank** — sổ cái RP, giới hạn 80 RP, chống gian lận, streak theo ngày lịch tập, ngưỡng rank, trừ điểm từ Bạch kim, Cao thủ top 100.
6. **Thành tựu + bước chân** — Health Connect/HealthKit, đồng bộ 7 ngày, trao thưởng.
7. **Bảng xếp hạng** — Redis sorted set: toàn server, theo rank, tuần.
8. **Cài đặt + deploy** — nút chuyển vi/en, Docker image, VPS, HTTPS, backup, phát hành thử nội bộ.

---

## Kết quả thực thi & việc chuyển sang Giai đoạn 1

Giai đoạn 0 hoàn thành trên nhánh `phase0-foundation` (backend 7/7 test, mobile 11/11 test). Các việc review đề nghị nhưng hoãn lại — đưa vào plan Giai đoạn 1 tương ứng:

- **Auth/Deploy:** bỏ giá trị mặc định `DB_PASSWORD:change-me` ngoài profile local (thiếu biến → khởi động lỗi ngay); giới hạn/tắt springdoc (`/swagger-ui`, `/v3/api-docs`) ở môi trường deploy.
- **Phát hành thử nội bộ:** thêm quyền `INTERNET` vào `AndroidManifest.xml` chính (bản release chưa có); đóng gói font Chakra Petch + Be Vietnam Pro vào assets thay vì tải lúc chạy.
- **Cài đặt / đổi ngôn ngữ:** khởi tạo `AppLocaleNotifier` từ `PlatformDispatcher.locale` (hiện việc gửi đúng `Accept-Language` ở lần gọi đầu phụ thuộc thứ tự microtask của Dio); làm mới các provider phụ thuộc ngôn ngữ khi đổi locale; thêm test toàn app kiểm tra header.
- **API client:** gói cả lỗi parse (không phải `DioException`) vào `ApiException`; phân biệt loại lỗi (mất mạng / 4xx / 5xx) khi có endpoint thật.
- **CI:** pin `flutter-version` (đang dùng 3.50.0); xử lý `paths` filter nếu bật required checks; thêm concurrency group.
- **Modulith events:** nếu cần event bền vững, thêm lại `spring-modulith-starter-jpa` kèm migration bảng `event_publication`.
