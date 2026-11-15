# Giai đoạn 1 · Plan 1 — Auth + User: Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Backend có đăng ký / đăng nhập bằng email + mật khẩu, JWT access + refresh (xoay vòng, phát hiện dùng lại), hồ sơ người dùng (tên, ngôn ngữ, múi giờ với luật đổi 1 lần/30 ngày có hiệu lực từ hôm sau) và chế độ tạm nghỉ (tối đa 7 ngày/tháng), tất cả lỗi trả về problem+json song ngữ.

**Architecture:** Hai module Modulith mới: `com.gymleveling.user` (sở hữu bảng `users`, `pause_periods`; API công khai `UserService`, `UserTime`, `PauseService`) và `com.gymleveling.auth` (sở hữu `refresh_tokens`, Spring Security, phát/kiểm JWT; chỉ gọi `user` qua API công khai). Lấy userId hiện tại qua `shared.security.CurrentUser` (module `shared` OPEN) để `user` không phụ thuộc `auth` — tránh vòng phụ thuộc mà Modulith từ chối. Code nội bộ mỗi module nằm trong sub-package `internal` để Modulith chặn truy cập chéo. Spring Security dạng stateless + OAuth2 Resource Server (JWT HS256, Nimbus). Thời gian lấy từ bean `Clock` để test được.

**Tech Stack:** Spring Boot 4.1.2, Spring Security 7 (`spring-boot-starter-security`, `spring-boot-starter-oauth2-resource-server`), BCrypt, Spring Data JPA, Flyway, Redis (giới hạn đăng nhập sai), Testcontainers, MockMvc.

**Spec:** `docs/superpowers/specs/2026-11-15-gym-leveling-design.md` (§5.2 module auth/user, §5.6–5.7 múi giờ + tạm nghỉ, §5.12 dữ liệu, §5.13 API).

**Phạm vi:** chỉ backend. Màn hình đăng nhập/đăng ký trên app làm trong plan Onboarding (cần thiết kế trước). Xoá tài khoản, quên mật khẩu, xác minh email thuộc Giai đoạn 1.5.

## Global Constraints

- Mọi API dưới `/api/v1`. Lỗi là RFC 7807 `application/problem+json`, có thêm thuộc tính `code` (chuỗi ổn định, client dùng để hiện thông báo) và `detail` đã dịch theo `Accept-Language` (`vi`/`en`, mặc định `en`).
- Java package gốc `com.gymleveling`; module chỉ gọi nhau qua lớp công khai ở package gốc của module; `ModularityTests` phải pass.
- Secret lấy từ env: `JWT_SECRET` bắt buộc, ≥ 32 byte; thiếu hoặc ngắn hơn → app không khởi động (fail fast). Không có giá trị mặc định trong `src/main`.
- Thời gian luôn lấy từ bean `java.time.Clock` (không gọi `Instant.now()` / `LocalDate.now()` không tham số).
- Mật khẩu chỉ lưu BCrypt; refresh token chỉ lưu SHA-256 hex; không log mật khẩu hay token.
- Không dùng Firebase/Supabase.

### Giá trị cố định (dùng nguyên văn)

| Thứ | Giá trị |
|---|---|
| Access token TTL | 15 phút; claim `sub` = user UUID, `iat`, `exp`, `iss` = `gym-leveling` |
| Refresh token | 32 byte ngẫu nhiên (SecureRandom) → base64url không padding; TTL 30 ngày |
| Mật khẩu | 8–72 ký tự |
| Email | trim + lowercase; tối đa 254 ký tự; định dạng `@Email` |
| Tên hiển thị | trim; 1–40 ký tự |
| Locale | `vi` hoặc `en` |
| Múi giờ | IANA hợp lệ (`ZoneId.of`, không nhận offset kiểu `+07:00`); mặc định khi đăng ký thiếu: `Asia/Ho_Chi_Minh` |
| Đổi múi giờ | tối đa 1 lần / 30 ngày (tính từ `timezone_changed_at`); hiệu lực từ 00:00 ngày mai theo múi giờ **cũ** |
| Đăng nhập sai | 5 lần / 15 phút / email → 429 `auth.too_many_attempts`; đăng nhập đúng xoá bộ đếm |
| Tạm nghỉ | 1–7 ngày mỗi lần; bắt đầu hôm nay hoặc tương lai (≤ 30 ngày tới); tổng ngày nghỉ rơi vào mỗi tháng dương lịch ≤ 7; chỉ 1 kỳ nghỉ hiện tại/tương lai |

### Mã lỗi (`code`) và HTTP status

| code | status | en | vi |
|---|---|---|---|
| `validation.failed` | 400 | Some fields are invalid. | Một số trường không hợp lệ. |
| `auth.email_taken` | 409 | This email is already registered. | Email này đã được đăng ký. |
| `auth.invalid_credentials` | 401 | Email or password is incorrect. | Email hoặc mật khẩu không đúng. |
| `auth.too_many_attempts` | 429 | Too many attempts. Try again in 15 minutes. | Thử quá nhiều lần. Vui lòng thử lại sau 15 phút. |
| `auth.invalid_refresh_token` | 401 | Your session has expired. Please sign in again. | Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại. |
| `auth.unauthorized` | 401 | Please sign in to continue. | Vui lòng đăng nhập để tiếp tục. |
| `user.timezone_change_too_soon` | 409 | You can change your time zone once every 30 days. | Bạn chỉ có thể đổi múi giờ 1 lần mỗi 30 ngày. |
| `user.invalid_timezone` | 400 | Unknown time zone. | Múi giờ không hợp lệ. |
| `pause.limit_exceeded` | 409 | You can pause at most 7 days per month. | Bạn chỉ được tạm nghỉ tối đa 7 ngày mỗi tháng. |
| `pause.already_scheduled` | 409 | You already have a pause scheduled. | Bạn đã có một kỳ tạm nghỉ. |
| `pause.invalid_dates` | 400 | Pause dates are invalid. | Ngày tạm nghỉ không hợp lệ. |
| `pause.none_active` | 404 | You have no active pause. | Bạn không có kỳ tạm nghỉ nào. |

`validation.failed` thêm thuộc tính `errors`: danh sách `{field, message}` (message đã dịch theo locale; dùng thông điệp mặc định của Bean Validation là đủ).

## Review Focus

- Email khác hoa/thường hoặc có khoảng trắng (`" A@B.com "` rồi `"a@b.com"`) → coi là cùng một tài khoản: đăng ký lần 2 bị 409, đăng nhập bằng dạng nào cũng được. (Task 3, `register_emailNormalized_duplicateRejected`, `login_emailCaseInsensitive`)
- Refresh token đã dùng bị gửi lại (bị đánh cắp hoặc app gửi 2 lần) → 401 và **mọi** refresh token của user đó bị thu hồi. (Task 4, `refresh_reuseRevokesAllTokens`)
- Token hết hạn / sai chữ ký / thiếu → 401 problem+json `auth.unauthorized`, không phải trang lỗi HTML hay 403. (Task 1, `protectedEndpoint_expiredToken_returns401Problem`, `..._tamperedToken_...`)
- Người dùng ở UTC+7 đổi sang múi giờ khác lúc 23:30 → ngày hôm đó vẫn tính theo múi giờ cũ; từ 00:00 hôm sau (giờ cũ) mới đổi. (Task 5, `timezoneChange_appliesFromNextLocalMidnight`)
- Kỳ nghỉ vắt qua 2 tháng (28/1 → 2/2) → đếm 4 ngày vào tháng 1, 2 ngày vào tháng 2, mỗi tháng kiểm tra riêng. (Task 6, `pause_spanningMonths_countsPerMonth`)

---

## Cấu trúc file

```
backend/
  build.gradle.kts                                   (sửa: thêm security + oauth2-resource-server)
  src/main/resources/application.yml                 (sửa: app.jwt.*)
  src/main/resources/messages*.properties            (sửa: thêm mã lỗi)
  src/main/resources/db/migration/V2__users_auth.sql
  src/main/java/com/gymleveling/shared/
    time/ClockConfig.java                            bean Clock (UTC)
    security/CurrentUser.java                        userId từ JWT trong SecurityContext
    web/ApiException.java                            exception mang code + status + args
    web/ApiExceptionHandler.java                     @RestControllerAdvice → problem+json có code, detail dịch
  src/main/java/com/gymleveling/user/
    UserService.java  UserTime.java  PauseService.java     (API công khai: interface)
    UserAccount.java  UserProfile.java  PauseInfo.java     (record công khai)
    internal/UserEntity.java  UserRepository.java  UserServiceImpl.java  UserTimeImpl.java
    internal/PausePeriodEntity.java  PausePeriodRepository.java  PauseServiceImpl.java
    internal/MeController.java  PauseController.java
  src/main/java/com/gymleveling/auth/
    internal/SecurityConfig.java  JwtProperties.java  JwtService.java
    internal/RefreshTokenEntity.java  RefreshTokenRepository.java  RefreshTokenService.java
    internal/LoginAttemptLimiter.java  AuthController.java  ProblemAuthenticationEntryPoint.java
  src/test/java/com/gymleveling/
    MutableClock.java  TestClockConfiguration.java
    auth/…Test.java   user/…Test.java
  src/test/resources/config/application.yml          (JWT_SECRET cho test)
.env.example                                         (thêm JWT_SECRET)
README.md                                            (thêm bước JWT_SECRET)
```

---

### Task 1: Hạ tầng chung — Clock, lỗi có mã, Spring Security + JWT

**Files:**
- Modify: `backend/build.gradle.kts`, `application.yml`, `messages.properties`, `messages_vi.properties`, `.env.example`, `README.md`
- Create: `shared/time/ClockConfig.java`, `shared/web/ApiException.java`, `shared/web/ApiExceptionHandler.java`, `shared/security/CurrentUser.java`, `auth/internal/{SecurityConfig,JwtProperties,JwtService,ProblemAuthenticationEntryPoint}.java`, `auth/package-info.java` (module thường, không OPEN)
- Test: `src/test/java/com/gymleveling/{MutableClock,TestClockConfiguration}.java`, `src/test/resources/config/application.yml`, `auth/JwtServiceTest.java`, `auth/SecurityTest.java`

**Interfaces:**
- Produces:
  - `@Bean Clock clock()` → `Clock.systemUTC()`.
  - `public class ApiException extends RuntimeException { public ApiException(HttpStatus status, String code, Object... args) }` — handler dịch `detail` bằng `MessageSource` key `error.<code>` theo `LocaleContextHolder`, set `title` = reason phrase, thuộc tính `code`. Handler cũng xử lý `MethodArgumentNotValidException` → `validation.failed` + `errors`.
  - `JwtService` (internal): `String issueAccessToken(UUID userId)`; Spring `JwtDecoder` bean dùng cùng secret, kiểm `iss` và `exp` theo `Clock`.
  - `public final class CurrentUser { public static UUID id() }` (package `com.gymleveling.shared.security`) — đọc từ `SecurityContextHolder` (`JwtAuthenticationToken` → `sub`).
  - Security: stateless, CSRF tắt; `permitAll` cho `/api/v1/auth/**`, `/api/v1/ping`, `/actuator/health`, `/v3/api-docs/**`, `/swagger-ui/**`; còn lại cần JWT. 401 trả problem+json `auth.unauthorized` qua `ProblemAuthenticationEntryPoint`.
  - Test: `MutableClock extends Clock` (`setInstant(Instant)`, `advance(Duration)`, zone UTC); `TestClockConfiguration` cung cấp nó làm `@Primary Clock`. Mặc định `2026-11-16T03:00:00Z`.
  - `JwtProperties` (`app.jwt.secret`, `app.jwt.issuer`=`gym-leveling`, `app.jwt.access-ttl`=`15m`, `app.jwt.refresh-ttl`=`30d`), `@Validated`, secret ≥ 32 byte UTF-8.

- [ ] **Step 1: Thêm dependency** `spring-boot-starter-security`, `spring-boot-starter-oauth2-resource-server` (+ `spring-boot-starter-security-test` cho test). `application.yml`: `app.jwt.secret: ${JWT_SECRET}` (không default). `src/test/resources/config/application.yml`: `app.jwt.secret: test-secret-test-secret-test-secret-0123`. `.env.example`: `JWT_SECRET=` + comment "chuỗi ngẫu nhiên ≥ 32 ký tự, ví dụ: openssl rand -base64 48". README: thêm vào bước backend.

- [ ] **Step 2: Viết test thất bại**

`JwtServiceTest` (unit, không cần Spring context):
- `issuedToken_decodesWithSubjectIssuerAndExpiry`: token từ `issueAccessToken(id)` decode được, `sub == id.toString()`, `iss == "gym-leveling"`, `exp - iat == 900s`.
- `expiredToken_rejected`: clock tiến 16 phút → decode ném `JwtException`.
- `shortSecret_failsValidation`: secret 31 byte → binding/validation lỗi.

`SecurityTest` (`@SpringBootTest @AutoConfigureMockMvc @Import({TestcontainersConfiguration, TestClockConfiguration})`):
- `ping_stillPublic`: `GET /api/v1/ping` → 200.
- `protectedEndpoint_noToken_returns401Problem`: `GET /api/v1/me` → 401, content type `application/problem+json`, `$.code == "auth.unauthorized"`, với `Accept-Language: vi` → `$.detail == "Vui lòng đăng nhập để tiếp tục."`.
- `protectedEndpoint_expiredToken_returns401Problem`, `protectedEndpoint_tamperedToken_returns401Problem` (đổi 1 ký tự chữ ký) → như trên.

- [ ] **Step 3: Chạy, xác nhận fail** — `cd backend && ./gradlew test --tests '*JwtServiceTest' --tests '*SecurityTest'` → FAIL (chưa có class/security).
- [ ] **Step 4: Implement** theo Interfaces. Đăng ký key `error.*` cho **mọi** mã trong bảng mã lỗi ngay ở task này (cả en và vi). Đặt `ApiExceptionHandler` trong `shared.web` (module OPEN).
- [ ] **Step 5: Chạy toàn bộ** `./gradlew test` → pass (gồm `PingControllerTest`, `ModularityTests`).
- [ ] **Step 6: Commit** `feat(auth): add JWT security, coded problem details, injectable clock`

---

### Task 2: Module user — bảng và `UserService`

**Files:**
- Create: `db/migration/V2__users_auth.sql`, `user/{UserService,UserAccount,UserProfile}.java`, `user/internal/{UserEntity,UserRepository,UserServiceImpl}.java`
- Test: `user/UserServiceTest.java`

**Interfaces:**
- Consumes: `Clock`, `ApiException` (Task 1).
- Produces:
  - `V2__users_auth.sql` tạo **cả 3 bảng** của plan này:
    ```
    users(id uuid pk default gen_random_uuid(), email varchar(254) not null unique,
          password_hash varchar(100) not null, display_name varchar(40) not null,
          locale varchar(2) not null check (locale in ('vi','en')),
          timezone varchar(64) not null, timezone_changed_at timestamptz,
          pending_timezone varchar(64), pending_timezone_from timestamptz,
          premium_until timestamptz, created_at timestamptz not null, updated_at timestamptz not null)
    refresh_tokens(id uuid pk default gen_random_uuid(), user_id uuid not null references users on delete cascade,
          token_hash char(64) not null unique, family_id uuid not null, expires_at timestamptz not null,
          used_at timestamptz, revoked_at timestamptz, created_at timestamptz not null)
          + index (user_id)
    pause_periods(id uuid pk default gen_random_uuid(), user_id uuid not null references users on delete cascade,
          start_date date not null, end_date date not null, created_at timestamptz not null,
          check (end_date >= start_date)) + index (user_id, start_date)
    ```
  - `public record UserAccount(UUID id, String email, String passwordHash)`
  - `public record UserProfile(UUID id, String email, String displayName, String locale, String timezone, String pendingTimezone, Instant pendingTimezoneFrom, Instant timezoneChangeAvailableAt, boolean premium)` — `timezoneChangeAvailableAt` null nếu đổi được ngay; `premium = premium_until != null && premium_until > now`.
  - `public interface UserService`:
    - `UserProfile register(String email, String passwordHash /* đã BCrypt */, String displayName, String locale, String timezone)` — chuẩn hoá email, ném `ApiException(409, "auth.email_taken")` nếu trùng (bắt cả `DataIntegrityViolationException` cho race), `ApiException(400,"user.invalid_timezone")` nếu tz sai, tz null → `Asia/Ho_Chi_Minh`.
    - `Optional<UserAccount> findAccountByEmail(String email)` (chuẩn hoá trước khi tìm)
    - `UserProfile getProfile(UUID id)` (404 problem nếu không có — `ApiException(404,"user.not_found")`; thêm key `error.user.not_found`: en "User not found." / vi "Không tìm thấy người dùng.")
  - `static String normalizeEmail(String)` trong `UserService` (trim + `toLowerCase(Locale.ROOT)`).

- [ ] **Step 1: Viết test thất bại** `UserServiceTest` (`@SpringBootTest` + Testcontainers + clock):
  - `register_persistsNormalizedEmailAndDefaults`: `register(" A@B.com ", hash, "Cường", "vi", null)` → email `a@b.com`, timezone `Asia/Ho_Chi_Minh`, `timezoneChangeAvailableAt == null`, `premium == false`.
  - `register_duplicateEmailDifferentCase_throwsEmailTaken`: rồi `register("a@b.COM", …)` → `ApiException` code `auth.email_taken`.
  - `register_invalidTimezone_throws`: tz `"+07:00"` và `"Mars/Base"` → code `user.invalid_timezone`.
  - `findAccountByEmail_isCaseInsensitive`.
- [ ] **Step 2: Chạy, xác nhận fail.**
- [ ] **Step 3: Implement.** Entity map đúng cột; `ddl-auto: validate` phải pass. Mỗi test dọn dữ liệu (`@Transactional` rollback hoặc `deleteAll` trong `@BeforeEach`).
- [ ] **Step 4: `./gradlew test` pass.**
- [ ] **Step 5: Commit** `feat(user): add users schema and user service`

---

### Task 3: Đăng ký và đăng nhập

**Files:**
- Create: `auth/internal/{AuthController,RefreshTokenEntity,RefreshTokenRepository,RefreshTokenService,LoginAttemptLimiter}.java`
- Test: `auth/AuthControllerTest.java`

**Interfaces:**
- Consumes: `UserService`, `JwtService`, `ApiException`, `Clock`.
- Produces:
  - `POST /api/v1/auth/register` body `{email, password, displayName, locale, timezone?}` → **201** `TokenResponse`.
  - `POST /api/v1/auth/login` body `{email, password}` → **200** `TokenResponse`.
  - `record TokenResponse(String accessToken, String refreshToken, long expiresIn /*900*/, String tokenType /*"Bearer"*/, UserProfile user)`.
  - `RefreshTokenService.issue(UUID userId, UUID familyId)` → raw token; lưu `sha256Hex(raw)`, `expires_at = now + 30d`. Mỗi lần login/register tạo `family_id` mới.
  - `LoginAttemptLimiter`: Redis key `auth:login-fail:<normalizedEmail>`, `INCR` + `EXPIRE 900` khi sai; ≥ 5 → 429 **trước khi** kiểm mật khẩu; đúng → `DEL`.
  - Validation bằng Bean Validation trên request record (giới hạn theo bảng Giá trị cố định).

- [ ] **Step 1: Viết test thất bại** `AuthControllerTest` (MockMvc, full context; Redis flush trong `@BeforeEach`):
  - `register_returns201WithTokensAndProfile`: `$.accessToken` không rỗng, `$.tokenType=="Bearer"`, `$.expiresIn==900`, `$.user.email=="a@b.com"`; dùng accessToken gọi `GET /api/v1/ping` với header Authorization → 200 (token hợp lệ không làm hỏng endpoint public).
  - `register_emailNormalized_duplicateRejected`: đăng ký `" A@B.com "` rồi `"a@b.com"` → 409 `$.code=="auth.email_taken"`.
  - `register_invalidInput_returns400WithFieldErrors`: password 7 ký tự, displayName rỗng, locale `"fr"` → 400 `validation.failed`, `$.errors[*].field` chứa `password`, `displayName`, `locale`.
  - `login_emailCaseInsensitive`: đăng ký `a@b.com`, login `A@B.COM` → 200.
  - `login_wrongPassword_returns401Generic` và `login_unknownEmail_returns401Generic`: cùng `code` `auth.invalid_credentials` và cùng `detail` (không lộ email có tồn tại hay không).
  - `login_fiveFailures_thenLocked`: 5 lần sai → lần 6 (kể cả mật khẩu **đúng**) → 429 `auth.too_many_attempts`.
  - `login_success_resetsFailureCounter`: 4 lần sai, 1 lần đúng, rồi 4 lần sai nữa → vẫn 401 (không 429).
- [ ] **Step 2: Chạy, xác nhận fail.**
- [ ] **Step 3: Implement.** BCrypt qua `PasswordEncoderFactories.createDelegatingPasswordEncoder()`. Với email không tồn tại vẫn gọi `matches` với một hash giả để thời gian phản hồi tương đương.
- [ ] **Step 4: `./gradlew test` pass.**
- [ ] **Step 5: Commit** `feat(auth): add register and login with attempt limiting`

---

### Task 4: Refresh xoay vòng, phát hiện dùng lại, đăng xuất

**Files:**
- Modify: `AuthController.java`, `RefreshTokenService.java`
- Test: `auth/RefreshTokenTest.java`

**Interfaces:**
- Produces:
  - `POST /api/v1/auth/refresh` body `{refreshToken}` → 200 `TokenResponse` (token mới cùng `family_id`; token cũ đánh dấu `used_at`).
  - `POST /api/v1/auth/logout` body `{refreshToken}` → **204**; thu hồi (`revoked_at`) mọi token cùng `family_id`. Token không tồn tại vẫn 204 (idempotent).
  - Luật: token không tồn tại / hết hạn / đã thu hồi → 401 `auth.invalid_refresh_token`. Token **đã dùng** (`used_at != null`) → 401 **và** thu hồi mọi refresh token chưa thu hồi của user đó (mọi family).
  - Refresh đọc lại `UserProfile` (để app nhận profile mới nhất).

- [ ] **Step 1: Viết test thất bại**:
  - `refresh_rotatesToken`: login → refresh(R1) → 200 trả R2 ≠ R1; refresh(R2) → 200.
  - `refresh_reuseRevokesAllTokens`: login lần 1 (R1, family F1) và login lần 2 (S1, family F2); refresh(R1) → R2; refresh(R1) lần nữa → 401 `auth.invalid_refresh_token`; sau đó refresh(R2) → 401 và refresh(S1) → 401.
  - `refresh_expired_returns401`: clock tiến 30 ngày + 1 giây → 401.
  - `logout_revokesFamily_isIdempotent`: logout(R1) → 204; refresh(R1) → 401; logout(R1) lần nữa → 204; logout("garbage") → 204.
  - `logout_doesNotAffectOtherDevices`: 2 lần login (2 family), logout family 1 → refresh của family 2 vẫn 200.
- [ ] **Step 2: Chạy, xác nhận fail.**
- [ ] **Step 3: Implement.** Refresh chạy trong một transaction, khoá dòng token (`@Lock(PESSIMISTIC_WRITE)` khi tìm theo hash) để 2 request đồng thời với cùng token không cùng thành công.
- [ ] **Step 4: `./gradlew test` pass.**
- [ ] **Step 5: Commit** `feat(auth): add refresh token rotation with reuse detection and logout`

---

### Task 5: Hồ sơ `/me` và luật múi giờ

**Files:**
- Create: `user/UserTime.java`, `user/internal/{UserTimeImpl,MeController}.java`; modify `UserService`/`UserServiceImpl`
- Test: `user/MeControllerTest.java`, `user/UserTimeTest.java`

**Interfaces:**
- Consumes: `CurrentUser.id()`.
- Produces:
  - `GET /api/v1/me` → `UserProfile`.
  - `PATCH /api/v1/me` body `{displayName?, locale?, timezone?}` (trường null = không đổi) → `UserProfile`.
  - Đổi múi giờ (chỉ khi khác múi giờ hiệu lực hiện tại): nếu `timezone_changed_at != null` và `now < timezone_changed_at + 30d` → 409 `user.timezone_change_too_soon`. Ngược lại set `pending_timezone`, `pending_timezone_from = đầu ngày mai theo múi giờ hiện tại (cũ)`, `timezone_changed_at = now`. Gửi lại đúng múi giờ đang hiệu lực khi đang có pending → xoá pending; lượt đổi vẫn bị tính (`timezone_changed_at` giữ nguyên).
  - `public interface UserTime { ZoneId zoneAt(UUID userId, Instant at); LocalDate today(UUID userId); }` — nếu `pending_timezone_from <= at` thì dùng pending (và lần ghi kế tiếp gộp pending vào `timezone`). Module khác (quest, scoring) chỉ dùng interface này để biết "hôm nay" của người dùng.
  - `UserProfile.timezone` trả múi giờ **đang hiệu lực**; `pendingTimezone/pendingTimezoneFrom` cho app hiển thị "Có hiệu lực từ …".

- [ ] **Step 1: Viết test thất bại**:
  - `me_returnsProfile` / `patch_updatesDisplayNameAndLocale` (displayName trim; locale `"fr"` → 400 `validation.failed`).
  - `timezoneChange_appliesFromNextLocalMidnight`: user `Asia/Ho_Chi_Minh`, clock = `2026-11-16T16:30:00Z` (23:30 giờ VN). PATCH tz `Europe/Paris` → 200, `pendingTimezone=="Europe/Paris"`, `pendingTimezoneFrom=="2026-11-16T17:00:00Z"`, `timezone` vẫn `Asia/Ho_Chi_Minh`. `UserTime.today` = `2026-11-16`. Tiến clock tới `17:00:00Z` → `timezone=="Europe/Paris"`, `today` = `2026-11-16` (18:00 Paris).
  - `timezoneChange_secondWithin30Days_rejected`: đổi lần 1 ok; +29 ngày đổi tiếp → 409 `user.timezone_change_too_soon`; +30 ngày → 200.
  - `timezoneChange_firstSetAtRegister_doesNotCount`: user mới đăng ký đổi múi giờ ngay → 200.
  - `invalidTimezone_returns400`.
- [ ] **Step 2: Chạy, xác nhận fail.**
- [ ] **Step 3: Implement.**
- [ ] **Step 4: `./gradlew test` pass.**
- [ ] **Step 5: Commit** `feat(user): add profile endpoints and timezone change rules`

---

### Task 6: Chế độ tạm nghỉ

**Files:**
- Create: `user/{PauseService,PauseInfo}.java`, `user/internal/{PausePeriodEntity,PausePeriodRepository,PauseServiceImpl,PauseController}.java`
- Test: `user/PauseTest.java`

**Interfaces:**
- Consumes: `UserTime.today(userId)`.
- Produces:
  - `POST /api/v1/me/pause` body `{startDate?: "yyyy-MM-dd" (mặc định hôm nay theo UserTime), days: 1..7}` → 201 `PauseInfo`.
  - `GET /api/v1/me/pause` → 200 `PauseInfo` của kỳ hiện tại/tương lai, hoặc 404 `pause.none_active`.
  - `DELETE /api/v1/me/pause` → 204. Nếu kỳ chưa bắt đầu → xoá. Nếu đang diễn ra → `end_date = hôm qua`; nếu như vậy `end_date < start_date` (bắt đầu hôm nay) → xoá. Không có → 404 `pause.none_active`.
  - `public record PauseInfo(LocalDate startDate, LocalDate endDate, int daysUsedThisMonth)`.
  - `public interface PauseService { boolean isPaused(UUID userId, LocalDate date); }` — dùng cho streak/trừ điểm sau này.
  - Luật: `startDate < today` hoặc `> today + 30` → 400 `pause.invalid_dates`; đã có kỳ có `end_date >= today` → 409 `pause.already_scheduled`; với mỗi tháng mà kỳ mới chạm vào: (ngày nghỉ đã có trong tháng đó) + (ngày của kỳ mới trong tháng đó) > 7 → 409 `pause.limit_exceeded`. Ngày của kỳ bị huỷ trước khi bắt đầu không tính; kỳ bị cắt ngắn chỉ tính phần đã diễn ra.

- [ ] **Step 1: Viết test thất bại**:
  - `pause_startToday_isPaused`: POST `{days:3}` → 201; `isPaused(today)`, `isPaused(today+2)` true; `isPaused(today+3)` false.
  - `pause_secondWhileScheduled_rejected` → 409 `pause.already_scheduled`.
  - `pause_monthlyLimit`: tháng 11: nghỉ 5 ngày (16–20/11), huỷ không được vì đã qua → tiến clock tới 21/11, xin 3 ngày → 409 `pause.limit_exceeded`; xin 2 ngày → 201.
  - `pause_spanningMonths_countsPerMonth`: clock 2027-01-20; đã có kỳ 2027-01-20..01-22 (3 ngày, tiến clock qua); xin `{startDate: 2027-01-28, days: 6}` → tháng 1: 3+4=7 ok, tháng 2: 2 ok → 201; `daysUsedThisMonth` theo tháng của `today`.
  - `pause_cancelFuture_doesNotCount` và `pause_cancelOngoing_keepsElapsedDays`.
  - `pause_pastStartDate_rejected` → 400 `pause.invalid_dates`; `days: 8` → 400 `validation.failed`.
- [ ] **Step 2: Chạy, xác nhận fail.**
- [ ] **Step 3: Implement.**
- [ ] **Step 4: `./gradlew test` pass, `ModularityTests` pass** (auth chỉ dùng `user.UserService`/`UserProfile`/`UserAccount`; user không phụ thuộc auth).
- [ ] **Step 5: Kiểm tra tay** với Docker Compose đang chạy và `.env` có `JWT_SECRET`: `./gradlew bootRun`; `curl -X POST localhost:8080/api/v1/auth/register -H 'Content-Type: application/json' -H 'Accept-Language: vi' -d '{"email":"test@example.com","password":"12345678","displayName":"Test","locale":"vi"}'` → 201; `curl localhost:8080/api/v1/me -H "Authorization: Bearer <accessToken>"` → 200. Không có `JWT_SECRET` → app dừng ngay khi khởi động với lỗi rõ ràng.
- [ ] **Step 6: Commit** `feat(user): add pause mode with monthly limit`
