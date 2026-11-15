# Gym Leveling — Tài liệu thiết kế

Ngày: 2026-11-15 · Trạng thái: bản nháp chờ duyệt

## 1. Ý tưởng và lý do

App mobile tập luyện kiểu "lên rank như game" cho:
- người **chưa có kinh nghiệm**, không biết nên tập gì;
- người **đã tập nhưng chưa đạt mục tiêu**.

App hỏi một bộ câu hỏi đầu vào → xếp rank ban đầu (tối đa Bạc) → sinh giáo án phù hợp → mỗi ngày tập là một nhiệm vụ gồm các bài tập cụ thể. Người dùng kiếm điểm (RP) qua nhiệm vụ, streak, thành tựu để lên rank và lên bảng xếp hạng. Mục tiêu cốt lõi: **giữ người dùng tập đều đặn bằng động lực game hoá, với giáo án an toàn và phù hợp thể trạng.**

Người làm: 1 người (solo). Backend tự viết (không Firebase/Supabase).

## 2. Mục tiêu / Không làm / Thất bại nếu

**Mục tiêu**
- Onboarding < 3 phút, ra rank ban đầu + giáo án ngay.
- Nhiệm vụ ngày rõ ràng: hôm nay tập bài gì, bao nhiêu set/rep.
- Hệ thống RP, rank, streak, thành tựu, bảng xếp hạng hoạt động đúng và khó lạm dụng.
- Gói trả phí: giáo án được AI tinh chỉnh (giai đoạn 2).

**Không làm (ở MVP)**
- Xác minh bài tập bằng ảnh/video hay cảm biến (bài tập là tự khai báo).
- Mạng xã hội đầy đủ (feed, bình luận, chat).
- Dinh dưỡng / đếm calo.
- Web app.

**Thất bại nếu** (dù kỹ thuật chạy được)
- Người mới bị giao bài quá sức → chấn thương hoặc bỏ app.
- Bảng xếp hạng bị "cày" dễ dàng bằng khai báo ảo.
- Cơ chế trừ điểm khiến người dùng nản và bỏ hẳn.

## 3. Ràng buộc

- Solo dev → **modular monolith**, ít thành phần vận hành.
- Backend: **Java 21 + Spring Boot 4**. Mobile: **Flutter**.
- Không dùng BaaS (Firebase/Supabase) cho backend/DB/auth.
- Gói trả phí là nội dung số → **bắt buộc In-App Purchase** (Google Play Billing, App Store). Backend tự xác minh giao dịch.
- App liên quan sức khoẻ → cần câu hỏi sàng lọc an toàn và disclaimer.
- **Song ngữ Tiếng Việt + Tiếng Anh**, người dùng chuyển qua lại trong Cài đặt. Áp dụng cho cả giao diện lẫn nội dung (bài tập, mẫu giáo án, thành tựu, câu hỏi onboarding). Mặc định theo ngôn ngữ máy, không phải vi/en thì dùng en.
- **MVP chỉ để test nội bộ** (TestFlight / Google Play Internal testing). Các yêu cầu cho bản phát hành công khai nằm ở Giai đoạn 1.5.
- **Nội dung tập luyện**: Claude soạn bản nháp thư viện bài tập và mẫu giáo án; **một người có chuyên môn (PT/HLV) duyệt** trước khi đưa cho người dùng thử. Nội dung chưa duyệt không được dùng.

## 4. Các phương án đã cân nhắc

### 4.1 Xác minh việc tập
| Phương án | Đánh đổi | Kết quả |
|---|---|---|
| Tự khai báo + giới hạn điểm | Nhanh, đơn giản; dễ gian lận nếu không có giới hạn | **Chọn** |
| Đồng bộ Health/Fit/đồng hồ | Tin cậy với cardio/bước, không đo được tạ; phức tạp | Chỉ dùng cho **bước chân** |
| Ảnh/video + kiểm duyệt | Tin cậy nhất; tốn công vận hành | Không làm |

### 4.2 Sinh giáo án
| Phương án | Đánh đổi | Kết quả |
|---|---|---|
| A. Luật + mẫu giáo án | Dễ đoán, an toàn, rẻ; phải soạn nội dung | **Gói miễn phí** |
| B. LLM sinh toàn bộ | Cá nhân hoá cao; tốn tiền, khó kiểm soát, rủi ro an toàn | Không làm |
| C. Lai: A làm khung, AI tinh chỉnh | Cá nhân hoá vừa đủ, vẫn an toàn | **Gói trả phí** (giai đoạn 2) |

### 4.3 Tụt rank
| Phương án | Kết quả |
|---|---|
| Không bao giờ tụt | Không chọn |
| Reset theo mùa | Không chọn |
| **Từ Bạch kim: bỏ tập 3 ngày liên tiếp → bị trừ dần** | **Chọn** |

### 4.4 Kiến trúc backend
| Phương án | Kết quả |
|---|---|
| **Modular monolith** (1 app Spring Boot, chia module) | **Chọn** — hợp solo dev |
| Microservices | Không — quá nặng vận hành |

## 5. Những gì đã quyết

### 5.1 Stack (quyết định của Claude, chủ dự án có thể sửa)
- **Backend**: Java 21, Spring Boot 4 (bản GA mới nhất; Spring Boot 3.x đã hết hỗ trợ miễn phí), Spring Modulith (kiểm tra ranh giới module), Spring Security (JWT access + refresh), Spring Data JPA, Flyway, PostgreSQL 16, Redis 7 (bảng xếp hạng + cache), OpenAPI (springdoc).
- **Mobile**: Flutter, Riverpod (state), Dio (HTTP), go_router, `health` package (Health Connect / HealthKit — chỉ đọc bước chân).
- **Hạ tầng**: Docker Compose trên 1 VPS (app + Postgres + Redis + Nginx/Caddy reverse proxy, HTTPS). CI bằng GitHub Actions.
- **Push notification** (giai đoạn 2): bắt buộc đi qua FCM (Android) và APNs (iOS) vì đó là kênh của hệ điều hành. Chỉ dùng FCM làm "đường truyền" thông báo, không dùng Firebase cho backend/DB/auth.
- **Repo**: monorepo `backend/`, `mobile/`, `docs/`.

### 5.2 Module backend
```
auth          đăng ký/đăng nhập email+mật khẩu, JWT, refresh token
user          hồ sơ, timezone, cài đặt, chế độ tạm nghỉ
onboarding    bộ câu hỏi, sàng lọc an toàn, tính điểm xếp rank ban đầu
catalog       thư viện bài tập, mẫu giáo án (template)
plan          giáo án của người dùng, lịch tập, tăng tiến độ
quest         nhiệm vụ ngày (= buổi tập), ghi nhận hoàn thành bài
scoring       sổ cái RP (ledger), giới hạn ngày, streak, hệ số nhân
rank          tính rank từ RP, trừ điểm khi bỏ tập, Cao thủ
achievement   định nghĩa thành tựu, theo dõi tiến độ, trao thưởng
activity      đồng bộ bước chân theo ngày
leaderboard   bảng xếp hạng (Redis sorted set)
billing       (GĐ2) xác minh IAP, trạng thái premium
ai            (GĐ2) điểm nối AI tinh chỉnh giáo án
```
Mỗi module chỉ gọi module khác qua service interface công khai; giao tiếp bất đồng bộ (ví dụ "đã hoàn thành bài" → scoring, achievement) qua Spring application events.

### 5.3 Onboarding và xếp rank ban đầu

**Câu hỏi**
1. Tuổi, giới tính, chiều cao, cân nặng.
2. Mục tiêu chính: giảm mỡ / tăng cơ / khoẻ hơn – bền hơn / duy trì.
3. Kinh nghiệm: chưa từng / < 6 tháng / 6–24 tháng / > 2 năm.
4. Hiện tại tập bao nhiêu buổi/tuần: 0 / 1–2 / 3–4 / 5+.
5. Số buổi có thể tập/tuần (2–6) và thời lượng mỗi buổi (20/30/45/60 phút).
6. Nơi tập + dụng cụ: ở nhà không dụng cụ / ở nhà có tạ đơn / phòng gym.
7. Tự kiểm tra nhanh: số hít đất tối đa liên tục, số giây plank tối đa (có tuỳ chọn "không làm được").
8. **Sàng lọc an toàn** (theo kiểu PAR-Q): bệnh tim, đau ngực khi vận động, chóng mặt, chấn thương xương khớp, đang mang thai… Có "có" → hiện khuyến cáo hỏi ý kiến bác sĩ, giao giáo án mức nhẹ nhất, và ghi cờ `safety_flag`.

**Điểm xếp hạng (0–12)**
| Yếu tố | 0 | 1 | 2 | 3 |
|---|---|---|---|---|
| Kinh nghiệm | chưa từng | < 6 th | 6–24 th | > 2 năm |
| Tần suất hiện tại | 0 | 1–2 | 3–4 | 5+ |
| Hít đất | < 5 | 5–14 | 15–29 | ≥ 30 |
| Plank | < 20s | 20–44s | 45–89s | ≥ 90s |

Điều chỉnh: BMI < 17 hoặc ≥ 30 → −1; tuổi ≥ 55 → −1 (không âm). `safety_flag` → tối đa Sắt I.

**Ánh xạ điểm → rank ban đầu** (người dùng bắt đầu ở ngưỡng RP của bậc đó)
| Điểm | 0–1 | 2–3 | 4 | 5 | 6 | 7 | 8 | 9–10 | 11–12 |
|---|---|---|---|---|---|---|---|---|---|
| Rank | Sắt III | Sắt II | Sắt I | Đồng III | Đồng II | Đồng I | Bạc III | Bạc II | Bạc I |

Tuổi, BMI, mục tiêu, dụng cụ, thời gian chủ yếu dùng để **chọn giáo án** (5.4), không phải xếp rank.

### 5.4 Giáo án (gói miễn phí — phương án A)

- **Thư viện bài tập**: mỗi bài có tên, nhóm cơ, dụng cụ cần, độ khó (1–5), kiểu đo (rep / giây / km / kg×rep), video/ảnh hướng dẫn, mô tả, các bài thay thế dễ hơn/khó hơn.
- **Định dạng nội dung** *(quyết định của Claude)*: bài tập, mẫu giáo án và thành tựu được viết thành file seed (YAML/JSON, có đủ cả vi và en) nằm trong repo. Backend nạp các file này khi khởi động hoặc qua migration. MVP không làm công cụ soạn nội dung, nên người duyệt chỉ cần đọc file hoặc bảng xuất ra. Mỗi mục có trường `reviewed: true/false`; mục nào chưa duyệt thì không nạp ở môi trường cho người dùng thử.
- **Minh hoạ bài tập** *(quyết định của Claude)*: MVP dùng ảnh tĩnh hoặc ảnh động nhẹ (GIF/WebP), có thể kèm link YouTube hướng dẫn. Không tự lưu video trên VPS. File ảnh đặt sau CDN hoặc object storage, hoặc đóng gói thẳng vào app nếu dung lượng nhỏ.
- **Giáo án nhẹ cho người có `safety_flag`**: mỗi nơi tập (ở nhà / có tạ đơn / gym) có 1 mẫu cường độ thấp riêng.
- **Mẫu giáo án (template)**: khoảng 12–20 mẫu ở MVP, gắn thẻ theo: mục tiêu × cấp độ (mới / trung bình / khá) × số buổi/tuần (2–3 / 4 / 5–6) × nơi tập. Mỗi mẫu là chu kỳ 4 tuần, gồm các "ngày tập" (danh sách bài + set/rep/thời gian/nghỉ) và ngày nghỉ.
- **Số buổi/tuần**: mẫu giáo án có đủ các biến thể từ 2 đến 6 buổi/tuần (ví dụ cùng mục tiêu tăng cơ có bản 4, 5 và 6 buổi). Số buổi người dùng chọn ở onboarding quyết định biến thể nào được dùng.
- **Đưa ra 2 phương án để người dùng chọn**: sau onboarding, hệ thống chấm điểm các mẫu phù hợp rồi đưa ra **2 phương án**. Mỗi phương án hiển thị tên, số buổi/tuần, thời lượng mỗi buổi, trọng tâm và một tuần tập mẫu. Người dùng chọn 1 thì mới tạo giáo án.
  - Lọc theo nơi tập + mục tiêu, chọn cấp độ theo điểm onboarding (0–4 mới, 5–8 trung bình, 9–12 khá).
  - Phương án 1 = mẫu khớp nhất với số buổi và thời lượng người dùng khai.
  - Phương án 2 = mẫu khác biệt rõ với phương án 1 *(quyết định của Claude)*: ưu tiên cùng mục tiêu nhưng khác cách chia buổi, ví dụ toàn thân và chia nhóm cơ. Nếu không có thì lấy mẫu cùng mục tiêu, số buổi lệch ±1.
  - Không đủ mẫu khớp thì nới điều kiện theo thứ tự: thời lượng → số buổi → mục tiêu.
  - Người có `safety_flag`: phương án 1 là mẫu cường độ thấp cho nơi tập của họ.
  - Sau này người dùng có thể xem lại 2 phương án và đổi giáo án (`POST /plan/regenerate`).
- **Lịch tập**: khi chọn phương án, người dùng chọn các thứ trong tuần sẽ tập, **đúng bằng số buổi/tuần của giáo án** (app gợi ý sẵn một lịch dàn đều, ví dụ 4 buổi → T2-T3-T5-T6). Các ngày còn lại là ngày nghỉ theo kế hoạch. Lịch có thể đổi trong Cài đặt, có hiệu lực từ ngày hôm sau.
- **Giáo án tiến theo buổi đã tập, không theo ngày lịch** *(quyết định của Claude)*: một chu kỳ là một chuỗi có thứ tự gồm `số tuần × số buổi/tuần` buổi tập. Mẫu giáo án được định nghĩa cho đúng một số buổi/tuần (mẫu "2–3 buổi" thực chất là 2 biến thể riêng). Nhiệm vụ của ngày tập luôn là **buổi kế tiếp chưa làm** trong chuỗi. Bỏ một ngày thì buổi đó không bị mất, mà dời sang lần tập sau. Tập bù vào ngày nghỉ cũng nhận buổi kế tiếp. Hết chuỗi thì hết chu kỳ.
- **Tăng tiến độ**: sau mỗi buổi, người dùng đánh giá Dễ / Vừa / Khó.
  - 2 buổi "Dễ" liên tiếp cho một bài → tăng rep (+1–2) hoặc thời gian (+5–10s) hoặc tạ (+2.5–5%).
  - "Khó" → giữ nguyên; 2 lần "Khó" liên tiếp → giảm hoặc đổi sang bài thay thế dễ hơn.
  - Hết chu kỳ 4 tuần → gợi ý chuyển sang mẫu cấp cao hơn nếu phần lớn đánh giá là "Dễ".
- Người dùng được làm lại onboarding / đổi mục tiêu → sinh giáo án mới (RP và rank giữ nguyên).

### 5.5 Nhiệm vụ ngày

- Vào mỗi ngày tập theo lịch (giờ địa phương của người dùng), hệ thống sinh **một nhiệm vụ** = buổi tập hôm đó, gồm các bài cụ thể (ví dụ Squat 3×12, Plank 3×30s, Push-up 3×8).
- Ngày nghỉ không có nhiệm vụ tập (có thể có nhiệm vụ nhẹ như đi bộ — xem phần thành tựu bước chân).
- Người dùng có thể tập bù vào ngày nghỉ (tính là ngày có tập, tính streak, vẫn chịu giới hạn ngày).
- Nhiệm vụ hết hạn lúc 23:59 giờ địa phương.
- Người dùng bấm "bắt đầu buổi tập" → đánh dấu từng bài hoàn thành (có thể nhập số rep/tạ thực tế) → "kết thúc buổi".

### 5.6 Điểm RP

**Kiếm điểm**
- Mỗi bài hoàn thành: **5–10 RP** theo độ khó bài (1–2: 5, 3: 7, 4–5: 10).
- Hoàn thành **toàn bộ** nhiệm vụ: thưởng thêm **+15 RP**.
- Làm được một phần (4/5 bài) → nhận điểm 4 bài đó, không có thưởng hoàn thành.
- **Hệ số streak** áp lên điểm nhiệm vụ: ≥ 7 ngày ×1.1, ≥ 30 ngày ×1.2, ≥ 100 ngày ×1.5.
- **Giới hạn ngày: 80 RP** cho điểm từ nhiệm vụ (sau khi nhân hệ số).
- **Thành tựu: nằm ngoài giới hạn ngày.**

**Chống gian lận (server kiểm tra)**
- Thời gian buổi tối thiểu = 40% thời lượng dự kiến của nhiệm vụ (tính từ "bắt đầu" đến "kết thúc", theo giờ server). Dưới mức → không cộng điểm thưởng hoàn thành, chỉ cộng điểm bài tương ứng tỷ lệ thời gian.
- Mỗi bài chỉ tính điểm 1 lần/ngày.
- Bất thường: số liệu khai báo vượt > 50% so với lần gần nhất của cùng bài, hoặc vượt ngưỡng phi thực tế của bài → bài vẫn được ghi nhận nhưng **không cộng điểm** và gắn cờ `suspicious`. Tài khoản có nhiều cờ → loại khỏi bảng xếp hạng chờ xem xét (admin xem qua endpoint quản trị đơn giản).
- Mọi điểm được ghi vào **sổ cái RP** (append-only, mỗi dòng có loại, lý do, tham chiếu). Tổng RP của người dùng là giá trị cache từ sổ cái.

**Streak**
- Streak = số ngày tập theo lịch liên tiếp có hoàn thành **ít nhất 1 bài** (tập bù vào ngày nghỉ cũng tăng streak). Ngày nghỉ theo kế hoạch và ngày tạm nghỉ **không làm đứt** streak (cũng không tăng streak).
- **Chỉ những ngày lịch bắt buộc tập mới làm mất streak.** Ví dụ giáo án 4 buổi/tuần tập Thứ 2-3-5-6 thì Thứ 4, Thứ 7, CN là ngày nghỉ: nghỉ những hôm đó streak vẫn giữ nguyên.
- Bỏ một ngày tập theo lịch → streak về 0 ngay khi qua nửa đêm. Tập bù ngày hôm sau không cứu được streak cũ mà bắt đầu streak mới *(quyết định của Claude, để luật đơn giản và dễ hiểu)*.
- **Đổi timezone**: tối đa 1 lần / 30 ngày, có hiệu lực từ ngày hôm sau, để không thể lợi dụng đổi múi giờ nhằm thêm/bớt ngày *(quyết định của Claude)*.

### 5.7 Rank

7 rank, mỗi rank 3 bậc (III → II → I), trừ Cao thủ. Ngưỡng là **tổng RP** (lưu trong DB để chỉnh được):

| Rank | III | II | I |
|---|---|---|---|
| Sắt | 0 | 100 | 200 |
| Đồng | 350 | 500 | 650 |
| Bạc | 850 | 1050 | 1250 |
| Vàng | 1500 | 1800 | 2100 |
| Bạch kim | 2500 | 2900 | 3300 |
| Kim cương | 3800 | 4400 | 5000 |
| Cao thủ | ≥ 5600 RP **và** nằm trong top 100 toàn server | | |

Ước tính: tập 3–4 buổi/tuần ≈ 200–250 RP/tuần → từ Sắt lên Kim cương khoảng 5–6 tháng. Đây là điểm khởi đầu, sẽ chỉnh sau khi có dữ liệu thật.

**Cao thủ**: job chạy hằng ngày, lấy top 100 trong số người có ≥ 5600 RP. Rơi khỏi top 100 → hiển thị Kim cương I (RP giữ nguyên).

**Trừ điểm (từ Bạch kim III trở lên)**
- Đếm số **ngày tập theo lịch** liên tiếp bị bỏ (ngày nghỉ theo kế hoạch và ngày tạm nghỉ không tính).
- Bỏ ngày thứ 1–3: không trừ. Ngày thứ 4: −10 RP, ngày 5: −20, từ ngày 6 trở đi: −30/ngày.
- Được tụt xuống dưới Bạch kim, nhưng **dừng trừ ở mốc Vàng I (2100 RP)**.
- Tập lại (hoàn thành ≥ 1 bài) → bộ đếm về 0.
- **Chế độ tạm nghỉ** (ốm, du lịch): tối đa 7 ngày/tháng dương lịch, phải bật trước hoặc trong ngày; trong thời gian đó không trừ điểm, không đứt streak.
- Job chạy mỗi giờ, xử lý những người dùng vừa qua nửa đêm theo timezone của họ.

### 5.8 Thành tựu

- Mốc tích luỹ dài hạn, mỗi mốc nhận **một lần**, điểm **ngoài giới hạn ngày**.
- Ví dụ MVP: tổng bước 10k / 100k / 1M; số buổi hoàn thành 1 / 10 / 50 / 200; streak 7 / 30 / 100; tổng khối lượng tạ 1 / 10 / 100 tấn; hoàn thành trọn chu kỳ 4 tuần đầu tiên; lên rank lần đầu.
- Định nghĩa thành tựu lưu trong DB: `metric` (steps_total, sessions_total, streak_max, volume_total…), `threshold`, `rp_reward`, icon.
- Khi có sự kiện liên quan (hoàn thành buổi, đồng bộ bước) → cập nhật tiến độ → trao thưởng nếu đạt.

### 5.9 Bước chân

- App đọc **số bước theo ngày** từ Health Connect (Android) / HealthKit (iOS), chỉ quyền đọc bước chân, xin quyền ở màn hình riêng (có thể từ chối → thành tựu bước bị khoá, app vẫn dùng bình thường).
- App đồng bộ khi mở app và khi kéo làm mới; gửi lên tổng bước từng ngày của 7 ngày gần nhất (server lưu giá trị lớn nhất mỗi ngày → idempotent).
- Giới hạn hợp lý: > 60.000 bước/ngày → gắn cờ, không tính vào thành tựu.
- Bước chân **chỉ dùng cho thành tựu** ở MVP, không cộng RP hằng ngày.

### 5.10 Bảng xếp hạng

- **Toàn server** (theo tổng RP), **theo rank hiện tại** (người cùng rank), **tuần** (RP kiếm được trong tuần, reset thứ Hai 00:00 giờ Việt Nam — giúp người mới có cơ hội lên top).
- **Bạn bè** ở giai đoạn 2.
- Redis sorted set; Postgres là nguồn dữ liệu gốc, có job dựng lại Redis từ DB khi cần.
- Hiển thị top 100 + vị trí của chính mình.
- **RP từ xếp hạng ban đầu** *(quyết định của Claude)*: được tính vào tổng RP, rank và bảng toàn server/theo rank (nếu không, rank sẽ không khớp với điểm). **Không** tính vào bảng tuần, và không kích hoạt thành tựu "lên rank lần đầu". Bảng tuần vì thế là sân chơi công bằng cho người mới.

### 5.11 Gói trả phí (giai đoạn 2 — phương án C)

- Thanh toán qua Google Play Billing / StoreKit; backend xác minh qua Google Play Developer API và App Store Server API, nhận webhook (Real-time Developer Notifications / App Store Server Notifications) để cập nhật gia hạn/huỷ.
- Premium = khung giáo án như gói miễn phí, sau đó AI:
  - đổi bài tương đương theo dụng cụ / chấn thương / sở thích;
  - điều chỉnh khối lượng theo lịch sử đánh giá Dễ/Vừa/Khó;
  - viết lời khuyên, giải thích ngắn.
- **Ràng buộc an toàn**: AI chỉ được chọn bài trong thư viện và trong biên độ khối lượng mà luật cho phép; server kiểm tra đầu ra trước khi áp dụng; lỗi → dùng giáo án gốc.
- MVP chỉ cần: cột `is_premium`/`premium_until` trên user, và interface `PlanAdjuster` (bản miễn phí = không đổi gì).
- Nhà cung cấp AI chọn khi bắt đầu giai đoạn 2.

### 5.12 Mô hình dữ liệu chính (Postgres)

```
-- Nội dung song ngữ: các trường văn bản dạng jsonb {"vi": "...", "en": "..."}
-- (name, description, title của exercises/templates/achievements/câu hỏi onboarding).
-- API trả về theo header Accept-Language hoặc users.locale.
users(id, email, password_hash, display_name, locale{vi,en}, timezone,
      timezone_changed_at, premium_until, created_at)
refresh_tokens(id, user_id, token_hash, expires_at, revoked)
onboarding_profiles(user_id, age, sex, height_cm, weight_kg, goal, experience,
                    current_freq, sessions_per_week, session_minutes, location,
                    equipment[], pushups_max, plank_sec, safety_flag, score, created_at)
exercises(id, name, muscle_groups[], equipment[], difficulty, measure_type,
          media_url, description, easier_id, harder_id)
plan_templates(id, name, goal, level, sessions_min, sessions_max, location,
               session_minutes, weeks)
template_days(id, template_id, week_no, day_index, title)
template_day_exercises(id, template_day_id, exercise_id, order, sets, reps,
                       duration_sec, rest_sec)
user_plans(id, user_id, template_id, started_at, active, weekdays[])
user_exercise_targets(user_plan_id, exercise_id, sets, reps, duration_sec, weight_kg)
daily_quests(id, user_id, local_date, template_day_id, status, expected_minutes,
             started_at, finished_at, feedback)
quest_exercises(id, quest_id, exercise_id, target_json, actual_json, completed_at,
                rp_awarded, suspicious)
rp_ledger(id, user_id, amount, type{EXERCISE,QUEST_BONUS,ACHIEVEMENT,DECAY,
          PLACEMENT,ADMIN}, ref_id, local_date, created_at)
user_stats(user_id, total_rp, tier_code, streak_current, streak_max,
           missed_scheduled_days, sessions_total, volume_total_kg, steps_total)
pause_periods(id, user_id, start_date, end_date)
daily_steps(user_id, local_date, steps, flagged)
achievements(id, code, metric, threshold, rp_reward, icon)
user_achievements(user_id, achievement_id, achieved_at)
rank_tiers(code, rank, division, min_rp)
```

### 5.13 API chính (REST, `/api/v1`)

```
POST /auth/register | /auth/login | /auth/refresh | /auth/logout
GET  /onboarding/questions        POST /onboarding/submit → rank + 2 phương án giáo án
POST /plan/select                 (chọn 1 trong 2 phương án → tạo user_plan)
GET  /me  · PATCH /me             GET /me/stats
GET  /plan/current                POST /plan/regenerate
GET  /quests/today                POST /quests/{id}/start
POST /quests/{id}/exercises/{exId}/complete   POST /quests/{id}/finish
POST /activity/steps              (batch 7 ngày)
GET  /achievements                (kèm tiến độ)
GET  /leaderboards/{global|tier|weekly}
POST /pause                       DELETE /pause
GET  /exercises/{id}
```

## 6. Lộ trình

**Giai đoạn 0 — Nền móng (≈ 1–2 tuần)**
Monorepo, Docker Compose (Postgres, Redis), skeleton Spring Boot (modules, Flyway, OpenAPI, test với Testcontainers), skeleton Flutter (routing, Riverpod, Dio, theme), CI build + test.

**Giai đoạn 1 — MVP (≈ 8–12 tuần, solo)**
1. Auth (email + mật khẩu, JWT).
2. Thư viện bài tập + 12–20 mẫu giáo án (nội dung — **việc tốn thời gian nhất, nên bắt đầu song song từ sớm**).
3. Onboarding + sàng lọc an toàn + xếp rank + chọn giáo án.
4. Nhiệm vụ ngày + ghi nhận bài + đánh giá Dễ/Vừa/Khó + tăng tiến độ.
5. Sổ cái RP, giới hạn ngày, chống gian lận, streak, rank.
6. Thành tựu + đồng bộ bước chân.
7. Trừ điểm + chế độ tạm nghỉ + job Cao thủ.
8. Bảng xếp hạng toàn server / theo rank / tuần.
9. Song ngữ vi/en: Flutter i18n (ARB / `flutter_localizations`), có nút chuyển ngôn ngữ trong Cài đặt; nội dung lấy từ trường jsonb.
10. Deploy VPS, HTTPS, backup DB, phát hành thử nội bộ (TestFlight / Internal testing).

Nội dung (thư viện bài tập + mẫu giáo án) do Claude soạn nháp ngay từ đầu Giai đoạn 1, gửi PT duyệt song song trong lúc code, để không bị nghẽn ở cuối.

**Giai đoạn 1.5 — Chuẩn bị phát hành công khai (≈ 2–3 tuần)**
Quên mật khẩu + xác minh email (cần dịch vụ gửi email qua SMTP), xoá tài khoản trong app (App Store bắt buộc), màn hình đồng ý xử lý dữ liệu sức khoẻ, giới hạn tuổi tối thiểu, chính sách bảo mật + điều khoản (theo Nghị định 13/2023), hồ sơ store.

**Giai đoạn 2 — Trả phí + giữ chân**
Push notification (nhắc tập, sắp bị trừ điểm), IAP + premium, AI tinh chỉnh giáo án, bảng xếp hạng bạn bè, đăng nhập Google/Apple.

**Giai đoạn 3 — Mở rộng**
Thử thách theo sự kiện, đồng bộ thêm dữ liệu từ Health (cardio), tính năng cộng đồng.

## 7. Để lại cho người xây dựng
- Cấu trúc package chi tiết, tên lớp, thiết kế UI chi tiết.
- Thư viện cụ thể trong phạm vi stack đã chọn (mapper, logging…).
- Cách viết job định kỳ (Spring `@Scheduled` + ShedLock là gợi ý).
- Định dạng lỗi API (gợi ý RFC 7807 Problem Details).
- Các con số (ngưỡng rank, RP mỗi bài, giới hạn ngày, mức trừ) là giá trị khởi đầu, lưu ở cấu hình/DB để chỉnh.
