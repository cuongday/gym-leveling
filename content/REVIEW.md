# Gym Leveling – Phiếu duyệt nội dung tập luyện (bản nháp)

> **Trạng thái:** BẢN NHÁP, chưa được PT duyệt. Mọi bài tập và giáo án đều đang để `reviewed: false`.
> Nội dung do nhóm sản phẩm soạn, cần huấn luyện viên có chứng chỉ (PT) kiểm tra trước khi người dùng thấy.

## Cách đọc phiếu này

- **70 bài tập** (`content/exercises.yaml`) và **21 giáo án** 4 tuần (`content/templates/*.yaml`).
- Mỗi nơi tập (ở nhà không dụng cụ / ở nhà có tạ đơn / phòng gym) × mỗi cấp độ (mới / trung bình / khá) có **2 giáo án** khác nhau rõ về số buổi hoặc cách chia buổi, để app đưa ra "2 phương án". Thêm **3 giáo án nhẹ** (mỗi nơi tập một bản) cho người bị gắn `safety_flag` ở bước sàng lọc sức khoẻ; những người này đồng thời được khuyên hỏi ý kiến bác sĩ.
- Cấp độ lấy từ điểm onboarding: 0–4 mới, 5–8 trung bình, 9–12 khá.
- Số trong bảng là **tuần 1**. Các tuần sau cộng thêm theo dòng "Tăng tiến theo tuần" (tuần 4 của cấp trung bình/khá là tuần giảm tải). Ngoài ra app tự tăng/giảm theo đánh giá Dễ/Vừa/Khó của người dùng (2 lần "Dễ" liên tiếp → +1–2 rep / +5–10 giây / +2,5–5% tạ; 2 lần "Khó" → giảm hoặc đổi bài dễ hơn).
- **Ước lượng thời gian** mỗi buổi (ghi dưới tên buổi): 5 phút khởi động + Σ set × (thời gian làm + nghỉ) + 30 giây chuyển bài; 1 rep ≈ 3 giây, bài "rep/bên" tính gấp đôi. "Đỉnh" là tuần dài nhất trong chu kỳ.
- **Mức tạ** không ghi trong giáo án: người dùng tự chọn mức tạ để còn dư khoảng 2–3 rep khi hết set (RIR 2–3). Không có bài kiểm tra 1RM, không có bài cử tạ Olympic.
- Mỗi buổi tập là một "nhiệm vụ ngày". Điểm RP mỗi bài: độ khó 1–2 → 5 RP, 3 → 7 RP, 4–5 → 10 RP.

## Nguyên tắc an toàn đã áp dụng

1. **Người mới:** 2 set/bài, 6–12 rep, chỉ bài độ khó 1–2 (validator chặn độ khó > 3), ưu tiên máy và bài có điểm tựa; không bật nhảy.
2. **Nhóm nhẹ (safety_flag):** chỉ bài độ khó ≤ 2, không va đập, không tư thế đầu thấp, không nín thở gồng; ≤ 30 phút, 2–3 buổi/tuần; tiến độ chỉ tăng rep/giây, không tăng set.
3. **Cân bằng đẩy/kéo:** mỗi buổi có bài đẩy thì có bài kéo (hoặc buổi kéo riêng trong tuần); tuần nào cũng có bài bụng/core ở hầu hết các buổi (5 buổi chưa có: gym trung bình 3 buổi C, gym trung bình 4 buổi A, gym khá 4 buổi A, gym khá 6 buổi A, gym nhẹ C – xem câu hỏi 12).
4. **Bài đòn (squat, bench, overhead press, row, RDL)** chỉ có ở cấp "khá" (riêng Squat với đòn có ở gym trung bình), luôn ghi rõ khung an toàn / người hỗ trợ và dừng trước khi hỏng kỹ thuật.
5. **Bài va đập cao** (Burpee) chỉ ở cấp khá tại nhà; cấp mới dùng bản không nhảy (Jumping jack không nhảy, Burpee bước lùi).
6. Mỗi bài có lưu ý an toàn một dòng và chuỗi bài dễ hơn/khó hơn để app đổi bài khi người dùng báo "Khó".

## Câu hỏi cho PT

Những điểm chúng tôi tự quyết, mong anh/chị kiểm tra kỹ:

1. **Khối lượng cho người mới** – 2 set × 8–12 rep, 5–6 bài, nghỉ 45–90s, tuần 3–4 chỉ tăng +2 rep (không thêm set). Có quá ít/quá nhiều cho người chưa từng tập? Có nên cho tuần 4 của người mới là tuần giảm tải không?
2. **Squat với đòn ở giáo án gym trung bình (4 buổi)** – 3 × 8, trong khung có thanh an toàn. Người 5–8 điểm có thể mới tập 6 tháng: nên giữ, hay thay bằng Goblet squat / Leg press và để bài đòn cho cấp khá?
3. **Giáo án 6 buổi/tuần (tạ đơn và gym)** – mỗi nhóm cơ 2 lần/tuần, 4–5 bài/buổi. Có hợp lý cho đối tượng app (người chưa đạt mục tiêu) hay nên giới hạn ở 5 buổi và thêm cảnh báo hồi phục?
4. **Nhóm nhẹ (safety_flag)** – chúng tôi gộp chung mọi lý do (tim mạch, chóng mặt, chấn thương khớp, mang thai) vào một giáo án nhẹ mỗi nơi tập. Danh sách bài (Ngồi xuống đứng lên ghế, Hít đất dựa tường, Kéo khung cửa, Bird dog, Dead bug, Glute bridge, Đạp xe…) có an toàn chung không? Có bài nào nên loại (ví dụ bài nằm ngửa với người mang thai, Đẩy vai ngồi với người cao huyết áp)?
5. **Kéo xà dưới gầm bàn (`table_row`)** – là bài kéo chính ở nhà không dụng cụ cấp trung bình/khá. Rủi ro bàn lật có chấp nhận được không, hay nên thay bằng kéo khăn/khung cửa và khuyến khích mua dây kháng lực?
6. **Ước lượng thời gian** – công thức trên cho kết quả 17–50 phút tuần 1; một số buổi cấp khá ở tuần 3 vượt ngân sách vài phút (ví dụ tạ đơn 60′ buổi A đỉnh ~64′). Chấp nhận được hay nên bớt 1 bài?
7. **Tăng tiến theo tuần** – trung bình: T2 +1 rep, T3 +1 set, T4 giảm tải (−1 set, −1 rep); khá: T3 +1 set +1 rep, T4 −1 set −2 rep. Có nên dùng mô hình khác (ví dụ chỉ tăng tạ) không? Kết hợp với luật tự tăng Dễ/Vừa/Khó có bị tăng quá nhanh không?
8. **Độ khó (1–5)** quyết định điểm RP. Anh/chị xem cột "Độ khó" có hợp lý không, đặc biệt: Plank = 2, Leo núi = 3, Hít xà hạ chậm = 3, Hít xà = 5, Pike push-up = 4, Hít đất tay hẹp = 4.
9. **Thuật ngữ tiếng Việt** – chúng tôi giữ tên tiếng Anh ở các bài phòng gym hay gọi (Squat, Plank, Glute bridge, Dead bug, Bird dog, Face pull, Pallof press, RDL) và dịch các bài còn lại (Hít đất quỳ gối, Kéo xô, Đạp đùi, Móc đùi…). Tên nào nghe chưa tự nhiên?
10. **Cardio trong giáo án gym** – đi bộ dốc/đạp xe 2 × 5 phút hoặc 1 × 10 phút cuối buổi cho người giảm mỡ. Mức "thở nhanh nhưng vẫn nói được câu ngắn" có đủ rõ cho người mới không?
11. **Khởi động** – hiện chưa có bài khởi động riêng, chỉ tính 5 phút trong ước lượng. Có cần thêm một chuỗi khởi động chuẩn (xoay khớp, đi bộ tại chỗ…) vào đầu mỗi buổi không?
12. **Core** – 5 buổi (liệt kê ở mục Nguyên tắc 3) chưa có bài core vì đã đủ thời gian; core có trong các buổi khác cùng tuần. Có cần thêm vào mọi buổi không?

## Giáo án

### 1. Làm quen toàn thân tại nhà – 2 buổi  
`home_bodyweight_full_body_2x_beginner`

| Nơi tập | Cấp độ | Buổi/tuần | Phút/buổi | Chia buổi | Mục tiêu |
|---|---|---|---|---|---|
| Ở nhà, không dụng cụ | Mới | 2 | 30 | toàn thân | khoẻ hơn – bền hơn, duy trì, giảm mỡ |

_2 buổi/tuần, không cần dụng cụ, học đúng kỹ thuật các động tác cơ bản._

Tăng tiến theo tuần (so với tuần 1): T2: set +0, rep +1, giây +5; T3: set +0, rep +2, giây +10; T4: set +0, rep +2, giây +10.

| Buổi | Bài tập | Set × Rep/Thời gian | Nghỉ | Độ khó |
|---|---|---|---|---|
| **A** – Toàn thân<br>~25′ (đỉnh ~27′) | Squat | 2 × 10 | 60s | 1 |
|  | Hít đất tay cao | 2 × 8 | 60s | 2 |
|  | Kéo khung cửa | 2 × 10 | 60s | 1 |
|  | Glute bridge | 2 × 12 | 45s | 1 |
|  | Plank quỳ gối | 2 × 20s | 45s | 1 |
|  | Đi bộ tại chỗ nâng gối | 2 × 60s | 45s | 1 |
| **B** – Toàn thân<br>~25′ (đỉnh ~26′) | Split squat | 2 × 6/bên | 60s | 2 |
|  | Hít đất quỳ gối | 2 × 8 | 60s | 1 |
|  | Nằm sấp nâng tay chữ Y | 2 × 10 | 45s | 1 |
|  | Gập hông (hip hinge) | 2 × 10 | 45s | 1 |
|  | Dead bug | 2 × 6/bên | 45s | 1 |
|  | Jumping jack không nhảy | 2 × 40s | 45s | 1 |

### 2. Circuit đốt mỡ tại nhà – 3 buổi  
`home_bodyweight_circuit_3x_beginner`

| Nơi tập | Cấp độ | Buổi/tuần | Phút/buổi | Chia buổi | Mục tiêu |
|---|---|---|---|---|---|
| Ở nhà, không dụng cụ | Mới | 3 | 20 | circuit | giảm mỡ, khoẻ hơn – bền hơn |

_3 buổi ngắn 20 phút/tuần, nghỉ ngắn để tim đập nhanh hơn, không bật nhảy._

Tăng tiến theo tuần (so với tuần 1): T2: set +0, rep +1, giây +5; T3: set +0, rep +2, giây +10; T4: set +0, rep +2, giây +10.

| Buổi | Bài tập | Set × Rep/Thời gian | Nghỉ | Độ khó |
|---|---|---|---|---|
| **A** – Circuit toàn thân<br>~17′ (đỉnh ~18′) | Squat | 2 × 10 | 30s | 1 |
|  | Hít đất tay cao | 2 × 8 | 30s | 2 |
|  | Kéo khung cửa | 2 × 10 | 30s | 1 |
|  | Jumping jack không nhảy | 2 × 30s | 30s | 1 |
|  | Plank quỳ gối | 2 × 20s | 30s | 1 |
| **B** – Circuit toàn thân<br>~18′ (đỉnh ~20′) | Bước lên bục | 2 × 6/bên | 30s | 2 |
|  | Hít đất quỳ gối | 2 × 8 | 30s | 1 |
|  | Glute bridge | 2 × 12 | 30s | 1 |
|  | Đi bộ tại chỗ nâng gối | 2 × 40s | 30s | 1 |
|  | Bird dog | 2 × 5/bên | 30s | 1 |
| **C** – Circuit toàn thân<br>~18′ (đỉnh ~19′) | Split squat | 2 × 6/bên | 30s | 2 |
|  | Nằm sấp nâng tay chữ Y | 2 × 10 | 30s | 1 |
|  | Gập hông (hip hinge) | 2 × 10 | 30s | 1 |
|  | Jumping jack không nhảy | 2 × 30s | 30s | 1 |
|  | Dead bug | 2 × 5/bên | 30s | 1 |

### 3. Toàn thân tại nhà – 3 buổi  
`home_bodyweight_full_body_3x_intermediate`

| Nơi tập | Cấp độ | Buổi/tuần | Phút/buổi | Chia buổi | Mục tiêu |
|---|---|---|---|---|---|
| Ở nhà, không dụng cụ | Trung bình | 3 | 45 | toàn thân | khoẻ hơn – bền hơn, duy trì, giảm mỡ |

_3 buổi/tuần, tăng sức mạnh và sức bền chỉ với trọng lượng cơ thể._

Tăng tiến theo tuần (so với tuần 1): T2: set +0, rep +1, giây +5; T3: set +1, rep +0, giây +5; T4: set -1, rep -1, giây +0.

| Buổi | Bài tập | Set × Rep/Thời gian | Nghỉ | Độ khó |
|---|---|---|---|---|
| **A** – Toàn thân<br>~34′ (đỉnh ~44′) | Lunge lùi | 3 × 8/bên | 60s | 3 |
|  | Hít đất | 3 × 8 | 75s | 3 |
|  | Kéo xà dưới gầm bàn (Inverted row) | 3 × 8 | 75s | 3 |
|  | Glute bridge một chân | 3 × 8/bên | 45s | 3 |
|  | Plank | 3 × 30s | 45s | 2 |
|  | Burpee bước lùi (không nhảy) | 2 × 8 | 60s | 3 |
| **B** – Toàn thân<br>~32′ (đỉnh ~41′) | Squat | 3 × 15 | 60s | 1 |
|  | Hít đất tay cao | 3 × 12 | 60s | 2 |
|  | Kéo khung cửa | 3 × 12 | 60s | 1 |
|  | RDL một chân | 3 × 8/bên | 45s | 3 |
|  | Plank nghiêng quỳ gối | 2 × 25s | 30s | 1 |
|  | Leo núi (Mountain climber) | 2 × 30s | 45s | 3 |
| **C** – Toàn thân<br>~33′ (đỉnh ~42′) | Split squat | 3 × 10/bên | 60s | 2 |
|  | Hít đất | 3 × 8 | 75s | 3 |
|  | Kéo xà dưới gầm bàn (Inverted row) | 3 × 8 | 75s | 3 |
|  | Thiên thần tuyết nằm sấp | 2 × 10 | 45s | 2 |
|  | Gập bụng ngược (Reverse crunch) | 3 × 10 | 45s | 2 |
|  | Nhón gót | 2 × 15 | 30s | 1 |

### 4. Thân trên / thân dưới tại nhà – 4 buổi  
`home_bodyweight_upper_lower_4x_intermediate`

| Nơi tập | Cấp độ | Buổi/tuần | Phút/buổi | Chia buổi | Mục tiêu |
|---|---|---|---|---|---|
| Ở nhà, không dụng cụ | Trung bình | 4 | 45 | trên / dưới | tăng cơ, khoẻ hơn – bền hơn |

_4 buổi/tuần chia thân trên và thân dưới, thêm khối lượng cho từng nhóm cơ._

Tăng tiến theo tuần (so với tuần 1): T2: set +0, rep +1, giây +5; T3: set +1, rep +0, giây +5; T4: set -1, rep -1, giây +0.

| Buổi | Bài tập | Set × Rep/Thời gian | Nghỉ | Độ khó |
|---|---|---|---|---|
| **A** – Thân trên<br>~34′ (đỉnh ~43′) | Hít đất | 3 × 8 | 75s | 3 |
|  | Kéo xà dưới gầm bàn (Inverted row) | 3 × 8 | 75s | 3 |
|  | Hít đất tay cao | 3 × 12 | 60s | 2 |
|  | Kéo khung cửa | 3 × 12 | 60s | 1 |
|  | Nằm sấp nâng tay chữ Y | 2 × 12 | 45s | 1 |
|  | Plank | 3 × 30s | 45s | 2 |
| **B** – Thân dưới<br>~36′ (đỉnh ~46′) | Lunge lùi | 3 × 8/bên | 75s | 3 |
|  | RDL một chân | 3 × 8/bên | 60s | 3 |
|  | Glute bridge một chân | 3 × 10/bên | 45s | 3 |
|  | Wall sit (ngồi dựa tường) | 2 × 30s | 45s | 1 |
|  | Nhón gót | 3 × 15 | 30s | 1 |
|  | Dead bug | 3 × 8/bên | 45s | 1 |
| **C** – Thân trên<br>~30′ (đỉnh ~38′) | Kéo xà dưới gầm bàn (Inverted row) | 3 × 10 | 75s | 3 |
|  | Hít đất | 3 × 10 | 75s | 3 |
|  | Thiên thần tuyết nằm sấp | 3 × 10 | 45s | 2 |
|  | Hít đất quỳ gối | 2 × 15 | 45s | 1 |
|  | Plank nghiêng quỳ gối | 2 × 30s | 30s | 1 |
|  | Gập bụng ngược (Reverse crunch) | 2 × 10 | 45s | 2 |
| **D** – Thân dưới<br>~32′ (đỉnh ~42′) | Split squat | 3 × 10/bên | 60s | 2 |
|  | Bước lên bục | 3 × 10/bên | 60s | 2 |
|  | Glute bridge | 3 × 15 | 45s | 1 |
|  | Gập hông (hip hinge) | 2 × 12 | 45s | 1 |
|  | Nhón gót | 2 × 15 | 30s | 1 |
|  | Bird dog | 2 × 8/bên | 30s | 1 |

### 5. Thân trên / thân dưới nâng cao tại nhà – 4 buổi  
`home_bodyweight_upper_lower_4x_advanced`

| Nơi tập | Cấp độ | Buổi/tuần | Phút/buổi | Chia buổi | Mục tiêu |
|---|---|---|---|---|---|
| Ở nhà, không dụng cụ | Khá | 4 | 45 | trên / dưới | tăng cơ, khoẻ hơn – bền hơn |

_4 buổi/tuần với biến thể khó: Bulgarian split squat, hít đất chân cao, kéo xà gầm bàn._

Tăng tiến theo tuần (so với tuần 1): T2: set +0, rep +1, giây +5; T3: set +1, rep +1, giây +10; T4: set -1, rep -2, giây +0.

| Buổi | Bài tập | Set × Rep/Thời gian | Nghỉ | Độ khó |
|---|---|---|---|---|
| **A** – Thân trên<br>~37′ (đỉnh ~49′) | Hít đất chân cao | 3 × 8 | 90s | 4 |
|  | Kéo xà dưới gầm bàn (Inverted row) | 4 × 10 | 75s | 3 |
|  | Pike push-up | 3 × 6 | 90s | 4 |
|  | Thiên thần tuyết nằm sấp | 3 × 12 | 45s | 2 |
|  | Hít đất tay hẹp | 2 × 8 | 60s | 4 |
|  | Plank | 3 × 45s | 45s | 2 |
| **B** – Thân dưới<br>~36′ (đỉnh ~49′) | Bulgarian split squat | 3 × 8/bên | 75s | 4 |
|  | RDL một chân | 3 × 10/bên | 60s | 3 |
|  | Glute bridge một chân | 3 × 12/bên | 45s | 3 |
|  | Lunge lùi | 2 × 10/bên | 60s | 3 |
|  | Nhón gót | 3 × 20 | 30s | 1 |
|  | Plank nghiêng | 2 × 30s | 30s | 3 |
| **C** – Thân trên<br>~38′ (đỉnh ~48′) | Kéo xà dưới gầm bàn (Inverted row) | 4 × 10 | 75s | 3 |
|  | Hít đất | 4 × 12 | 75s | 3 |
|  | Kéo khung cửa | 3 × 15 | 45s | 1 |
|  | Hít đất tay hẹp | 3 × 8 | 60s | 4 |
|  | Nằm sấp nâng tay chữ Y | 2 × 15 | 30s | 1 |
|  | Gập bụng ngược (Reverse crunch) | 3 × 12 | 45s | 2 |
| **D** – Thân dưới<br>~36′ (đỉnh ~49′) | Lunge lùi | 3 × 10/bên | 60s | 3 |
|  | Bulgarian split squat | 3 × 8/bên | 75s | 4 |
|  | Glute bridge một chân | 3 × 12/bên | 45s | 3 |
|  | Wall sit (ngồi dựa tường) | 2 × 45s | 45s | 1 |
|  | Leo núi (Mountain climber) | 3 × 30s | 45s | 3 |
|  | Dead bug | 2 × 10/bên | 30s | 1 |

### 6. Đẩy / Kéo / Chân + Trên / Dưới tại nhà – 5 buổi  
`home_bodyweight_push_pull_legs_5x_advanced`

| Nơi tập | Cấp độ | Buổi/tuần | Phút/buổi | Chia buổi | Mục tiêu |
|---|---|---|---|---|---|
| Ở nhà, không dụng cụ | Khá | 5 | 45 | đẩy / kéo / chân | tăng cơ, giảm mỡ, khoẻ hơn – bền hơn |

_5 buổi/tuần: Đẩy, Kéo, Chân rồi Thân trên, Thân dưới; có bài thể lực cuối buổi._

Tăng tiến theo tuần (so với tuần 1): T2: set +0, rep +1, giây +5; T3: set +1, rep +1, giây +10; T4: set -1, rep -2, giây +0.

| Buổi | Bài tập | Set × Rep/Thời gian | Nghỉ | Độ khó |
|---|---|---|---|---|
| **A** – Đẩy (ngực, vai, tay sau)<br>~32′ (đỉnh ~41′) | Hít đất chân cao | 3 × 10 | 90s | 4 |
|  | Pike push-up | 3 × 8 | 90s | 4 |
|  | Hít đất | 3 × 12 | 60s | 3 |
|  | Hít đất tay hẹp | 2 × 10 | 60s | 4 |
|  | Plank | 3 × 45s | 45s | 2 |
| **B** – Kéo (lưng, vai sau, tay trước)<br>~31′ (đỉnh ~40′) | Kéo xà dưới gầm bàn (Inverted row) | 4 × 10 | 75s | 3 |
|  | Kéo khung cửa | 3 × 15 | 60s | 1 |
|  | Thiên thần tuyết nằm sấp | 3 × 12 | 45s | 2 |
|  | Nằm sấp nâng tay chữ Y | 3 × 12 | 30s | 1 |
|  | Gập bụng ngược (Reverse crunch) | 3 × 12 | 45s | 2 |
| **C** – Chân và mông<br>~35′ (đỉnh ~46′) | Bulgarian split squat | 3 × 10/bên | 75s | 4 |
|  | RDL một chân | 3 × 10/bên | 60s | 3 |
|  | Glute bridge một chân | 3 × 12/bên | 45s | 3 |
|  | Nhón gót | 3 × 20 | 30s | 1 |
|  | Burpee | 3 × 8 | 60s | 4 |
| **D** – Thân trên<br>~31′ (đỉnh ~42′) | Hít đất | 3 × 15 | 60s | 3 |
|  | Kéo xà dưới gầm bàn (Inverted row) | 3 × 12 | 75s | 3 |
|  | Pike push-up | 2 × 8 | 75s | 4 |
|  | Thiên thần tuyết nằm sấp | 2 × 12 | 45s | 2 |
|  | Plank nghiêng | 2 × 40s | 30s | 3 |
|  | Leo núi (Mountain climber) | 3 × 30s | 45s | 3 |
| **E** – Thân dưới<br>~33′ (đỉnh ~44′) | Lunge lùi | 3 × 12/bên | 60s | 3 |
|  | Squat | 3 × 20 | 60s | 1 |
|  | Glute bridge một chân | 3 × 12/bên | 45s | 3 |
|  | Wall sit (ngồi dựa tường) | 2 × 45s | 45s | 1 |
|  | Dead bug | 3 × 10/bên | 30s | 1 |

### 7. Vận động nhẹ tại nhà – 3 buổi  
`light_home_bodyweight_3x`

| Nơi tập | Cấp độ | Buổi/tuần | Phút/buổi | Chia buổi | Mục tiêu |
|---|---|---|---|---|---|
| Ở nhà, không dụng cụ | Nhẹ (safety_flag) | 3 | 20 | toàn thân | khoẻ hơn – bền hơn, duy trì |

_3 buổi ngắn 20 phút/tuần, cường độ thấp, không va đập; hãy hỏi ý kiến bác sĩ trước khi tập._

Tăng tiến theo tuần (so với tuần 1): T2: set +0, rep +1, giây +5; T3: set +0, rep +2, giây +5; T4: set +0, rep +2, giây +5.

| Buổi | Bài tập | Set × Rep/Thời gian | Nghỉ | Độ khó |
|---|---|---|---|---|
| **A** – Toàn thân<br>~20′ (đỉnh ~21′) | Đi bộ tại chỗ nâng gối | 1 × 90s | 30s | 1 |
|  | Ngồi xuống đứng lên ghế | 2 × 8 | 60s | 1 |
|  | Hít đất dựa tường | 2 × 8 | 60s | 1 |
|  | Kéo khung cửa | 2 × 8 | 45s | 1 |
|  | Bird dog | 2 × 4/bên | 45s | 1 |
| **B** – Toàn thân<br>~20′ (đỉnh ~21′) | Đi bộ tại chỗ nâng gối | 1 × 2 phút | 30s | 1 |
|  | Glute bridge | 2 × 8 | 60s | 1 |
|  | Nằm sấp nâng tay chữ Y | 2 × 8 | 45s | 1 |
|  | Nhón gót | 2 × 10 | 45s | 1 |
|  | Dead bug | 2 × 4/bên | 45s | 1 |
| **C** – Toàn thân<br>~20′ (đỉnh ~21′) | Jumping jack không nhảy | 1 × 90s | 30s | 1 |
|  | Ngồi xuống đứng lên ghế | 2 × 8 | 60s | 1 |
|  | Hít đất dựa tường | 2 × 8 | 60s | 1 |
|  | Gập hông (hip hinge) | 2 × 8 | 45s | 1 |
|  | Bird dog | 2 × 4/bên | 45s | 1 |

### 8. Làm quen với tạ đơn tại nhà – 2 buổi  
`home_dumbbell_full_body_2x_beginner`

| Nơi tập | Cấp độ | Buổi/tuần | Phút/buổi | Chia buổi | Mục tiêu |
|---|---|---|---|---|---|
| Ở nhà, có tạ đơn | Mới | 2 | 30 | toàn thân | tăng cơ, khoẻ hơn – bền hơn, duy trì |

_2 buổi/tuần với một đôi tạ đơn nhẹ, xây nền sức mạnh toàn thân._

Tăng tiến theo tuần (so với tuần 1): T2: set +0, rep +1, giây +5; T3: set +0, rep +2, giây +10; T4: set +0, rep +2, giây +10.

| Buổi | Bài tập | Set × Rep/Thời gian | Nghỉ | Độ khó |
|---|---|---|---|---|
| **A** – Toàn thân<br>~24′ (đỉnh ~25′) | Goblet squat | 2 × 10 | 75s | 2 |
|  | Đẩy ngực tạ đơn trên sàn | 2 × 10 | 75s | 2 |
|  | Kéo tạ đơn một tay | 2 × 10/bên | 60s | 2 |
|  | Glute bridge | 2 × 12 | 45s | 1 |
|  | Dead bug | 2 × 6/bên | 45s | 1 |
| **B** – Toàn thân<br>~24′ (đỉnh ~25′) | Bước lên bục | 2 × 8/bên | 60s | 2 |
|  | Đẩy vai tạ đơn ngồi ghế | 2 × 10 | 75s | 2 |
|  | Kéo tạ đơn một tay | 2 × 10/bên | 60s | 2 |
|  | Glute bridge với tạ đơn | 2 × 10 | 60s | 2 |
|  | Plank quỳ gối | 2 × 20s | 45s | 1 |

### 9. Circuit tạ đơn tại nhà – 3 buổi  
`home_dumbbell_circuit_3x_beginner`

| Nơi tập | Cấp độ | Buổi/tuần | Phút/buổi | Chia buổi | Mục tiêu |
|---|---|---|---|---|---|
| Ở nhà, có tạ đơn | Mới | 3 | 30 | circuit | giảm mỡ, khoẻ hơn – bền hơn |

_3 buổi/tuần, tạ nhẹ, nghỉ ngắn – vừa đốt năng lượng vừa giữ cơ._

Tăng tiến theo tuần (so với tuần 1): T2: set +0, rep +1, giây +5; T3: set +0, rep +2, giây +10; T4: set +0, rep +2, giây +10.

| Buổi | Bài tập | Set × Rep/Thời gian | Nghỉ | Độ khó |
|---|---|---|---|---|
| **A** – Circuit toàn thân<br>~24′ (đỉnh ~25′) | Goblet squat | 2 × 10 | 45s | 2 |
|  | Đẩy ngực tạ đơn trên sàn | 2 × 10 | 45s | 2 |
|  | Kéo tạ đơn một tay | 2 × 8/bên | 45s | 2 |
|  | Jumping jack không nhảy | 2 × 40s | 45s | 1 |
|  | Glute bridge | 2 × 12 | 45s | 1 |
|  | Plank quỳ gối | 2 × 20s | 45s | 1 |
| **B** – Circuit toàn thân<br>~24′ (đỉnh ~27′) | Split squat | 2 × 6/bên | 45s | 2 |
|  | Đẩy vai tạ đơn ngồi ghế | 2 × 10 | 45s | 2 |
|  | Kéo tạ đơn một tay | 2 × 8/bên | 45s | 2 |
|  | Đi bộ tại chỗ nâng gối | 2 × 45s | 45s | 1 |
|  | Dead bug | 2 × 6/bên | 45s | 1 |
|  | Xách tạ đi bộ (Farmer carry) | 2 × 30s | 45s | 2 |
| **C** – Circuit toàn thân<br>~23′ (đỉnh ~25′) | Goblet squat | 2 × 10 | 45s | 2 |
|  | Hít đất tay cao | 2 × 8 | 45s | 2 |
|  | Glute bridge với tạ đơn | 2 × 10 | 45s | 2 |
|  | Jumping jack không nhảy | 2 × 40s | 45s | 1 |
|  | Cuốn tạ tay trước (Biceps curl) | 2 × 10 | 45s | 2 |
|  | Bird dog | 2 × 6/bên | 45s | 1 |

### 10. Toàn thân với tạ đơn – 3 buổi  
`home_dumbbell_full_body_3x_intermediate`

| Nơi tập | Cấp độ | Buổi/tuần | Phút/buổi | Chia buổi | Mục tiêu |
|---|---|---|---|---|---|
| Ở nhà, có tạ đơn | Trung bình | 3 | 45 | toàn thân | tăng cơ, khoẻ hơn – bền hơn, duy trì |

_3 buổi/tuần, mỗi buổi đủ đẩy – kéo – chân – bụng, tăng tạ từ từ._

Tăng tiến theo tuần (so với tuần 1): T2: set +0, rep +1, giây +5; T3: set +1, rep +0, giây +5; T4: set -1, rep -1, giây +0.

| Buổi | Bài tập | Set × Rep/Thời gian | Nghỉ | Độ khó |
|---|---|---|---|---|
| **A** – Toàn thân<br>~34′ (đỉnh ~43′) | Goblet squat | 3 × 10 | 90s | 2 |
|  | Đẩy ngực tạ đơn trên sàn | 3 × 10 | 90s | 2 |
|  | Kéo tạ đơn cúi người | 3 × 10 | 75s | 3 |
|  | Romanian deadlift tạ đơn (RDL) | 3 × 10 | 75s | 3 |
|  | Plank | 3 × 30s | 45s | 2 |
| **B** – Toàn thân<br>~34′ (đỉnh ~43′) | Split squat với tạ đơn | 3 × 8/bên | 75s | 3 |
|  | Đẩy vai tạ đơn đứng | 3 × 10 | 75s | 3 |
|  | Kéo tạ đơn một tay | 3 × 10/bên | 60s | 2 |
|  | Glute bridge với tạ đơn | 3 × 12 | 60s | 2 |
|  | Dead bug | 3 × 8/bên | 45s | 1 |
| **C** – Toàn thân<br>~34′ (đỉnh ~42′) | Goblet squat | 3 × 12 | 75s | 2 |
|  | Hít đất | 3 × 8 | 75s | 3 |
|  | Kéo tạ đơn cúi người | 3 × 10 | 75s | 3 |
|  | Romanian deadlift tạ đơn (RDL) | 3 × 10 | 75s | 3 |
|  | Xách tạ đi bộ (Farmer carry) | 3 × 40s | 60s | 2 |

### 11. Thân trên / thân dưới với tạ đơn – 4 buổi  
`home_dumbbell_upper_lower_4x_intermediate`

| Nơi tập | Cấp độ | Buổi/tuần | Phút/buổi | Chia buổi | Mục tiêu |
|---|---|---|---|---|---|
| Ở nhà, có tạ đơn | Trung bình | 4 | 45 | trên / dưới | tăng cơ, giảm mỡ |

_4 buổi/tuần chia trên – dưới, đủ khối lượng để tăng cơ và giữ dáng săn chắc._

Tăng tiến theo tuần (so với tuần 1): T2: set +0, rep +1, giây +5; T3: set +1, rep +0, giây +5; T4: set -1, rep -1, giây +0.

| Buổi | Bài tập | Set × Rep/Thời gian | Nghỉ | Độ khó |
|---|---|---|---|---|
| **A** – Thân trên<br>~35′ (đỉnh ~46′) | Đẩy ngực tạ đơn trên sàn | 3 × 10 | 90s | 2 |
|  | Kéo tạ đơn cúi người | 3 × 10 | 90s | 3 |
|  | Đẩy vai tạ đơn đứng | 3 × 10 | 75s | 3 |
|  | Kéo tạ đơn một tay | 2 × 12/bên | 60s | 2 |
|  | Cuốn tạ tay trước (Biceps curl) | 2 × 12 | 45s | 2 |
|  | Duỗi tay sau qua đầu với tạ đơn | 2 × 12 | 45s | 2 |
| **B** – Thân dưới<br>~36′ (đỉnh ~47′) | Goblet squat | 3 × 12 | 90s | 2 |
|  | Romanian deadlift tạ đơn (RDL) | 3 × 10 | 90s | 3 |
|  | Split squat với tạ đơn | 3 × 8/bên | 75s | 3 |
|  | Glute bridge với tạ đơn | 3 × 12 | 60s | 2 |
|  | Nhón gót | 2 × 15 | 30s | 1 |
|  | Dead bug | 2 × 8/bên | 30s | 1 |
| **C** – Thân trên<br>~36′ (đỉnh ~46′) | Kéo tạ đơn một tay | 3 × 10/bên | 75s | 2 |
|  | Hít đất | 3 × 10 | 75s | 3 |
|  | Đẩy vai tạ đơn ngồi ghế | 3 × 10 | 75s | 2 |
|  | Bay vai sau tạ đơn | 3 × 12 | 45s | 2 |
|  | Nâng tạ ngang (Lateral raise) | 2 × 12 | 45s | 2 |
|  | Plank | 3 × 30s | 45s | 2 |
| **D** – Thân dưới<br>~34′ (đỉnh ~44′) | Split squat với tạ đơn | 3 × 10/bên | 75s | 3 |
|  | Romanian deadlift tạ đơn (RDL) | 3 × 10 | 90s | 3 |
|  | Goblet squat | 3 × 12 | 75s | 2 |
|  | Glute bridge một chân | 2 × 10/bên | 45s | 3 |
|  | Xách tạ đi bộ (Farmer carry) | 3 × 40s | 60s | 2 |

### 12. Thân trên / thân dưới nâng cao với tạ đơn – 4 buổi  
`home_dumbbell_upper_lower_4x_advanced`

| Nơi tập | Cấp độ | Buổi/tuần | Phút/buổi | Chia buổi | Mục tiêu |
|---|---|---|---|---|---|
| Ở nhà, có tạ đơn | Khá | 4 | 60 | trên / dưới | tăng cơ, khoẻ hơn – bền hơn |

_4 buổi/tuần, 60 phút, khối lượng cao cho người đã tập đều trên 2 năm._

Tăng tiến theo tuần (so với tuần 1): T2: set +0, rep +1, giây +5; T3: set +1, rep +1, giây +10; T4: set -1, rep -2, giây +0.

| Buổi | Bài tập | Set × Rep/Thời gian | Nghỉ | Độ khó |
|---|---|---|---|---|
| **A** – Thân trên<br>~50′ (đỉnh ~64′) | Đẩy ngực tạ đơn trên sàn | 4 × 10 | 90s | 2 |
|  | Kéo tạ đơn cúi người | 4 × 10 | 90s | 3 |
|  | Đẩy vai tạ đơn đứng | 3 × 10 | 90s | 3 |
|  | Kéo tạ đơn một tay | 3 × 12/bên | 60s | 2 |
|  | Nâng tạ ngang (Lateral raise) | 3 × 15 | 45s | 2 |
|  | Cuốn tạ tay trước (Biceps curl) | 3 × 12 | 45s | 2 |
|  | Duỗi tay sau qua đầu với tạ đơn | 3 × 12 | 45s | 2 |
| **B** – Thân dưới<br>~42′ (đỉnh ~55′) | Bulgarian split squat với tạ đơn | 3 × 10/bên | 90s | 4 |
|  | Romanian deadlift tạ đơn (RDL) | 4 × 10 | 90s | 3 |
|  | Goblet squat | 3 × 15 | 75s | 2 |
|  | Glute bridge một chân | 3 × 12/bên | 45s | 3 |
|  | Nhón gót | 3 × 20 | 30s | 1 |
|  | Plank nghiêng | 2 × 40s | 30s | 3 |
| **C** – Thân trên<br>~47′ (đỉnh ~61′) | Kéo tạ đơn một tay | 4 × 10/bên | 75s | 2 |
|  | Hít đất chân cao | 3 × 10 | 90s | 4 |
|  | Đẩy vai tạ đơn ngồi ghế | 3 × 12 | 75s | 2 |
|  | Bay vai sau tạ đơn | 3 × 15 | 45s | 2 |
|  | Hít đất tay hẹp | 3 × 10 | 60s | 4 |
|  | Cuốn tạ tay trước (Biceps curl) | 3 × 12 | 45s | 2 |
|  | Plank | 3 × 45s | 45s | 2 |
| **D** – Thân dưới<br>~44′ (đỉnh ~58′) | Split squat với tạ đơn | 4 × 10/bên | 75s | 3 |
|  | Romanian deadlift tạ đơn (RDL) | 4 × 12 | 90s | 3 |
|  | Glute bridge với tạ đơn | 3 × 15 | 60s | 2 |
|  | Lunge lùi | 2 × 12/bên | 60s | 3 |
|  | Xách tạ đi bộ (Farmer carry) | 3 × 45s | 60s | 2 |
|  | Gập bụng ngược (Reverse crunch) | 3 × 12 | 45s | 2 |

### 13. Đẩy / Kéo / Chân với tạ đơn – 6 buổi  
`home_dumbbell_push_pull_legs_6x_advanced`

| Nơi tập | Cấp độ | Buổi/tuần | Phút/buổi | Chia buổi | Mục tiêu |
|---|---|---|---|---|---|
| Ở nhà, có tạ đơn | Khá | 6 | 45 | đẩy / kéo / chân | tăng cơ |

_6 buổi/tuần, mỗi nhóm cơ tập 2 lần, buổi ngắn 45 phút; cần ngủ và ăn đủ để hồi phục._

Tăng tiến theo tuần (so với tuần 1): T2: set +0, rep +1, giây +5; T3: set +1, rep +1, giây +10; T4: set -1, rep -2, giây +0.

| Buổi | Bài tập | Set × Rep/Thời gian | Nghỉ | Độ khó |
|---|---|---|---|---|
| **A** – Đẩy (ngực, vai, tay sau)<br>~35′ (đỉnh ~44′) | Đẩy ngực tạ đơn trên sàn | 4 × 10 | 90s | 2 |
|  | Đẩy vai tạ đơn đứng | 3 × 10 | 90s | 3 |
|  | Hít đất chân cao | 3 × 10 | 60s | 4 |
|  | Nâng tạ ngang (Lateral raise) | 3 × 15 | 45s | 2 |
|  | Duỗi tay sau qua đầu với tạ đơn | 3 × 12 | 45s | 2 |
| **B** – Kéo (lưng, vai sau, tay trước)<br>~35′ (đỉnh ~44′) | Kéo tạ đơn cúi người | 4 × 10 | 90s | 3 |
|  | Kéo tạ đơn một tay | 3 × 12/bên | 60s | 2 |
|  | Bay vai sau tạ đơn | 3 × 15 | 45s | 2 |
|  | Cuốn tạ tay trước (Biceps curl) | 3 × 12 | 45s | 2 |
|  | Gập bụng ngược (Reverse crunch) | 3 × 12 | 45s | 2 |
| **C** – Chân và mông<br>~37′ (đỉnh ~48′) | Bulgarian split squat với tạ đơn | 3 × 10/bên | 90s | 4 |
|  | Romanian deadlift tạ đơn (RDL) | 4 × 10 | 90s | 3 |
|  | Glute bridge với tạ đơn | 3 × 12 | 60s | 2 |
|  | Nhón gót | 3 × 20 | 30s | 1 |
|  | Plank | 3 × 45s | 45s | 2 |
| **D** – Đẩy (ngực, vai, tay sau)<br>~34′ (đỉnh ~43′) | Đẩy vai tạ đơn ngồi ghế | 4 × 10 | 90s | 2 |
|  | Đẩy ngực tạ đơn trên sàn | 3 × 12 | 75s | 2 |
|  | Hít đất | 3 × 15 | 60s | 3 |
|  | Nâng tạ ngang (Lateral raise) | 3 × 15 | 45s | 2 |
|  | Hít đất tay hẹp | 2 × 10 | 60s | 4 |
| **E** – Kéo (lưng, vai sau, tay trước)<br>~35′ (đỉnh ~46′) | Kéo tạ đơn một tay | 4 × 10/bên | 75s | 2 |
|  | Kéo tạ đơn cúi người | 3 × 12 | 75s | 3 |
|  | Thiên thần tuyết nằm sấp | 3 × 12 | 45s | 2 |
|  | Cuốn tạ tay trước (Biceps curl) | 3 × 12 | 45s | 2 |
|  | Xách tạ đi bộ (Farmer carry) | 3 × 45s | 60s | 2 |
| **F** – Chân và mông<br>~36′ (đỉnh ~47′) | Goblet squat | 4 × 12 | 90s | 2 |
|  | Split squat với tạ đơn | 3 × 10/bên | 75s | 3 |
|  | Romanian deadlift tạ đơn (RDL) | 3 × 12 | 75s | 3 |
|  | Glute bridge một chân | 3 × 12/bên | 45s | 3 |
|  | Plank nghiêng | 2 × 40s | 30s | 3 |

### 14. Tạ nhẹ tại nhà – 2 buổi  
`light_home_dumbbell_2x`

| Nơi tập | Cấp độ | Buổi/tuần | Phút/buổi | Chia buổi | Mục tiêu |
|---|---|---|---|---|---|
| Ở nhà, có tạ đơn | Nhẹ (safety_flag) | 2 | 30 | toàn thân | khoẻ hơn – bền hơn, duy trì |

_2 buổi/tuần với tạ rất nhẹ, ngồi hoặc nằm là chính; hãy hỏi ý kiến bác sĩ trước khi tập._

Tăng tiến theo tuần (so với tuần 1): T2: set +0, rep +1, giây +5; T3: set +0, rep +2, giây +5; T4: set +0, rep +2, giây +5.

| Buổi | Bài tập | Set × Rep/Thời gian | Nghỉ | Độ khó |
|---|---|---|---|---|
| **A** – Toàn thân<br>~26′ (đỉnh ~27′) | Đi bộ tại chỗ nâng gối | 1 × 3 phút | 30s | 1 |
|  | Ngồi xuống đứng lên ghế | 2 × 8 | 60s | 1 |
|  | Đẩy ngực tạ đơn trên sàn | 2 × 8 | 60s | 2 |
|  | Kéo tạ đơn một tay | 2 × 8/bên | 60s | 2 |
|  | Glute bridge | 2 × 8 | 60s | 1 |
|  | Bird dog | 2 × 4/bên | 45s | 1 |
| **B** – Toàn thân<br>~23′ (đỉnh ~25′) | Jumping jack không nhảy | 1 × 2 phút | 30s | 1 |
|  | Bước lên bục | 2 × 6/bên | 60s | 2 |
|  | Đẩy vai tạ đơn ngồi ghế | 2 × 8 | 60s | 2 |
|  | Cuốn tạ tay trước (Biceps curl) | 2 × 8 | 45s | 2 |
|  | Gập hông (hip hinge) | 2 × 8 | 45s | 1 |
|  | Dead bug | 2 × 4/bên | 45s | 1 |

### 15. Làm quen phòng gym – 2 buổi  
`gym_full_body_2x_beginner`

| Nơi tập | Cấp độ | Buổi/tuần | Phút/buổi | Chia buổi | Mục tiêu |
|---|---|---|---|---|---|
| Phòng gym | Mới | 2 | 45 | toàn thân | tăng cơ, khoẻ hơn – bền hơn, duy trì |

_2 buổi/tuần chủ yếu với máy tập, dễ học, an toàn cho người mới._

Tăng tiến theo tuần (so với tuần 1): T2: set +0, rep +1, giây +5; T3: set +0, rep +2, giây +10; T4: set +0, rep +2, giây +10.

| Buổi | Bài tập | Set × Rep/Thời gian | Nghỉ | Độ khó |
|---|---|---|---|---|
| **A** – Toàn thân<br>~32′ (đỉnh ~33′) | Đạp đùi (Leg press) | 2 × 12 | 90s | 2 |
|  | Đẩy ngực trên máy | 2 × 12 | 75s | 2 |
|  | Kéo xô (Lat pulldown) | 2 × 12 | 75s | 2 |
|  | Glute bridge | 2 × 12 | 60s | 1 |
|  | Dead bug | 2 × 6/bên | 45s | 1 |
|  | Đạp xe tại chỗ | 1 × 5 phút | 60s | 1 |
| **B** – Toàn thân<br>~31′ (đỉnh ~32′) | Goblet squat | 2 × 10 | 90s | 2 |
|  | Đẩy vai tạ đơn ngồi ghế | 2 × 10 | 75s | 2 |
|  | Kéo cáp ngồi (Seated row) | 2 × 12 | 75s | 2 |
|  | Móc đùi trên máy (Leg curl) | 2 × 12 | 60s | 2 |
|  | Plank quỳ gối | 2 × 25s | 45s | 1 |
|  | Đi bộ dốc trên máy chạy | 1 × 5 phút | 60s | 1 |

### 16. Toàn thân ở gym + cardio – 3 buổi  
`gym_full_body_3x_beginner`

| Nơi tập | Cấp độ | Buổi/tuần | Phút/buổi | Chia buổi | Mục tiêu |
|---|---|---|---|---|---|
| Phòng gym | Mới | 3 | 45 | toàn thân | giảm mỡ, khoẻ hơn – bền hơn, tăng cơ |

_3 buổi/tuần: tập máy toàn thân rồi đi bộ dốc hoặc đạp xe để đốt thêm năng lượng._

Tăng tiến theo tuần (so với tuần 1): T2: set +0, rep +1, giây +5; T3: set +0, rep +2, giây +10; T4: set +0, rep +2, giây +10.

| Buổi | Bài tập | Set × Rep/Thời gian | Nghỉ | Độ khó |
|---|---|---|---|---|
| **A** – Toàn thân<br>~31′ (đỉnh ~32′) | Đạp đùi (Leg press) | 2 × 12 | 75s | 2 |
|  | Đẩy ngực trên máy | 2 × 12 | 60s | 2 |
|  | Kéo xô (Lat pulldown) | 2 × 12 | 60s | 2 |
|  | Dead bug | 2 × 6/bên | 45s | 1 |
|  | Đi bộ dốc trên máy chạy | 1 × 10 phút | 30s | 1 |
| **B** – Toàn thân<br>~31′ (đỉnh ~32′) | Goblet squat | 2 × 10 | 75s | 2 |
|  | Kéo cáp ngồi (Seated row) | 2 × 12 | 60s | 2 |
|  | Hít đất tay cao | 2 × 8 | 60s | 2 |
|  | Móc đùi trên máy (Leg curl) | 2 × 12 | 60s | 2 |
|  | Đạp xe tại chỗ | 1 × 10 phút | 30s | 1 |
| **C** – Toàn thân<br>~34′ (đỉnh ~36′) | Đạp đùi (Leg press) | 2 × 12 | 75s | 2 |
|  | Đẩy vai tạ đơn ngồi ghế | 2 × 10 | 60s | 2 |
|  | Kéo tạ đơn một tay | 2 × 10/bên | 60s | 2 |
|  | Glute bridge | 2 × 12 | 45s | 1 |
|  | Plank quỳ gối | 2 × 25s | 45s | 1 |
|  | Đi bộ dốc trên máy chạy | 1 × 10 phút | 30s | 1 |

### 17. Toàn thân ở gym – 3 buổi  
`gym_full_body_3x_intermediate`

| Nơi tập | Cấp độ | Buổi/tuần | Phút/buổi | Chia buổi | Mục tiêu |
|---|---|---|---|---|---|
| Phòng gym | Trung bình | 3 | 60 | toàn thân | tăng cơ, duy trì, khoẻ hơn – bền hơn |

_3 buổi/tuần với tạ đơn, máy và cáp; mỗi buổi đủ các nhóm cơ chính._

Tăng tiến theo tuần (so với tuần 1): T2: set +0, rep +1, giây +5; T3: set +1, rep +0, giây +5; T4: set -1, rep -1, giây +0.

| Buổi | Bài tập | Set × Rep/Thời gian | Nghỉ | Độ khó |
|---|---|---|---|---|
| **A** – Toàn thân<br>~38′ (đỉnh ~50′) | Goblet squat | 3 × 10 | 90s | 2 |
|  | Đẩy ngực tạ đơn trên ghế | 3 × 10 | 90s | 3 |
|  | Kéo xô (Lat pulldown) | 3 × 10 | 75s | 2 |
|  | Romanian deadlift tạ đơn (RDL) | 3 × 10 | 90s | 3 |
|  | Face pull | 2 × 15 | 45s | 2 |
|  | Plank | 3 × 40s | 45s | 2 |
| **B** – Toàn thân<br>~36′ (đỉnh ~46′) | Đạp đùi (Leg press) | 3 × 12 | 90s | 2 |
|  | Đẩy vai tạ đơn ngồi ghế | 3 × 10 | 75s | 2 |
|  | Kéo cáp ngồi (Seated row) | 3 × 10 | 75s | 2 |
|  | Móc đùi trên máy (Leg curl) | 3 × 12 | 60s | 2 |
|  | Kéo cáp tay sau (Triceps pushdown) | 2 × 12 | 45s | 2 |
|  | Pallof press | 2 × 10/bên | 45s | 2 |
| **C** – Toàn thân<br>~44′ (đỉnh ~58′) | Split squat với tạ đơn | 3 × 8/bên | 75s | 3 |
|  | Đẩy ngực trên máy | 3 × 10 | 75s | 2 |
|  | Kéo tạ đơn một tay | 3 × 10/bên | 60s | 2 |
|  | Glute bridge với tạ đơn | 3 × 12 | 60s | 2 |
|  | Cuốn tạ tay trước (Biceps curl) | 2 × 12 | 45s | 2 |
|  | Đi bộ dốc trên máy chạy | 2 × 5 phút | 30s | 1 |

### 18. Thân trên / thân dưới ở gym – 4 buổi  
`gym_upper_lower_4x_intermediate`

| Nơi tập | Cấp độ | Buổi/tuần | Phút/buổi | Chia buổi | Mục tiêu |
|---|---|---|---|---|---|
| Phòng gym | Trung bình | 4 | 60 | trên / dưới | tăng cơ, giảm mỡ |

_4 buổi/tuần chia trên – dưới, có Squat với đòn trong khung an toàn, kèm cardio nhẹ._

Tăng tiến theo tuần (so với tuần 1): T2: set +0, rep +1, giây +5; T3: set +1, rep +0, giây +5; T4: set -1, rep -1, giây +0.

| Buổi | Bài tập | Set × Rep/Thời gian | Nghỉ | Độ khó |
|---|---|---|---|---|
| **A** – Thân trên<br>~36′ (đỉnh ~47′) | Đẩy ngực tạ đơn trên ghế | 3 × 10 | 90s | 3 |
|  | Kéo xô (Lat pulldown) | 3 × 10 | 90s | 2 |
|  | Đẩy vai tạ đơn ngồi ghế | 3 × 10 | 75s | 2 |
|  | Kéo cáp ngồi (Seated row) | 3 × 12 | 75s | 2 |
|  | Face pull | 2 × 15 | 45s | 2 |
|  | Kéo cáp tay sau (Triceps pushdown) | 2 × 12 | 45s | 2 |
| **B** – Thân dưới<br>~34′ (đỉnh ~44′) | Squat với đòn (Back squat) | 3 × 8 | 120s | 4 |
|  | Romanian deadlift tạ đơn (RDL) | 3 × 10 | 90s | 3 |
|  | Đạp đùi (Leg press) | 2 × 12 | 90s | 2 |
|  | Móc đùi trên máy (Leg curl) | 2 × 12 | 60s | 2 |
|  | Nhón gót | 2 × 15 | 30s | 1 |
|  | Dead bug | 2 × 8/bên | 30s | 1 |
| **C** – Thân trên<br>~36′ (đỉnh ~46′) | Kéo cáp ngồi (Seated row) | 3 × 10 | 90s | 2 |
|  | Đẩy ngực trên máy | 3 × 10 | 90s | 2 |
|  | Hít xà hạ chậm (Negative pull-up) | 3 × 4 | 90s | 3 |
|  | Nâng tạ ngang (Lateral raise) | 3 × 12 | 45s | 2 |
|  | Cuốn tạ tay trước (Biceps curl) | 2 × 12 | 45s | 2 |
|  | Plank | 3 × 40s | 45s | 2 |
| **D** – Thân dưới<br>~43′ (đỉnh ~58′) | Đạp đùi (Leg press) | 3 × 12 | 90s | 2 |
|  | Split squat với tạ đơn | 3 × 8/bên | 75s | 3 |
|  | Glute bridge với tạ đơn | 3 × 12 | 60s | 2 |
|  | Móc đùi trên máy (Leg curl) | 2 × 12 | 60s | 2 |
|  | Pallof press | 2 × 10/bên | 45s | 2 |
|  | Đạp xe tại chỗ | 2 × 5 phút | 30s | 1 |

### 19. Thân trên / thân dưới nâng cao ở gym – 4 buổi  
`gym_upper_lower_4x_advanced`

| Nơi tập | Cấp độ | Buổi/tuần | Phút/buổi | Chia buổi | Mục tiêu |
|---|---|---|---|---|---|
| Phòng gym | Khá | 4 | 60 | trên / dưới | tăng cơ, khoẻ hơn – bền hơn |

_4 buổi/tuần, 60 phút, bài đòn cơ bản (bench, squat, row, RDL) cộng bài bổ trợ._

Tăng tiến theo tuần (so với tuần 1): T2: set +0, rep +1, giây +5; T3: set +1, rep +1, giây +10; T4: set -1, rep -2, giây +0.

| Buổi | Bài tập | Set × Rep/Thời gian | Nghỉ | Độ khó |
|---|---|---|---|---|
| **A** – Thân trên<br>~47′ (đỉnh ~59′) | Đẩy ngực đòn (Bench press) | 4 × 8 | 120s | 4 |
|  | Kéo đòn cúi người (Barbell row) | 4 × 8 | 120s | 4 |
|  | Đẩy vai tạ đơn ngồi ghế | 3 × 10 | 90s | 2 |
|  | Kéo xô (Lat pulldown) | 3 × 10 | 75s | 2 |
|  | Kéo cáp tay sau (Triceps pushdown) | 3 × 12 | 45s | 2 |
|  | Cuốn tạ tay trước (Biceps curl) | 3 × 12 | 45s | 2 |
| **B** – Thân dưới<br>~41′ (đỉnh ~52′) | Squat với đòn (Back squat) | 4 × 6 | 150s | 4 |
|  | Romanian deadlift với đòn | 3 × 8 | 120s | 4 |
|  | Bulgarian split squat với tạ đơn | 3 × 8/bên | 75s | 4 |
|  | Móc đùi trên máy (Leg curl) | 3 × 12 | 60s | 2 |
|  | Treo xà co gối | 3 × 10 | 45s | 4 |
| **C** – Thân trên<br>~46′ (đỉnh ~59′) | Đẩy vai đòn đứng (Overhead press) | 4 × 8 | 120s | 4 |
|  | Hít xà (Pull-up) | 4 × 6 | 120s | 5 |
|  | Đẩy ngực tạ đơn trên ghế | 3 × 10 | 90s | 3 |
|  | Kéo cáp ngồi (Seated row) | 3 × 12 | 75s | 2 |
|  | Nâng tạ ngang (Lateral raise) | 3 × 15 | 45s | 2 |
|  | Face pull | 2 × 15 | 45s | 2 |
| **D** – Thân dưới<br>~45′ (đỉnh ~60′) | Đạp đùi (Leg press) | 3 × 10 | 90s | 2 |
|  | Romanian deadlift tạ đơn (RDL) | 3 × 10 | 90s | 3 |
|  | Split squat với tạ đơn | 3 × 10/bên | 75s | 3 |
|  | Nhón gót | 3 × 20 | 30s | 1 |
|  | Pallof press | 3 × 10/bên | 30s | 2 |
|  | Đạp xe tại chỗ | 2 × 4 phút | 30s | 1 |

### 20. Đẩy / Kéo / Chân ở gym – 6 buổi  
`gym_push_pull_legs_6x_advanced`

| Nơi tập | Cấp độ | Buổi/tuần | Phút/buổi | Chia buổi | Mục tiêu |
|---|---|---|---|---|---|
| Phòng gym | Khá | 6 | 60 | đẩy / kéo / chân | tăng cơ, khoẻ hơn – bền hơn |

_6 buổi/tuần, mỗi nhóm cơ 2 lần; dành cho người tập đều lâu năm, hồi phục tốt._

Tăng tiến theo tuần (so với tuần 1): T2: set +0, rep +1, giây +5; T3: set +1, rep +1, giây +10; T4: set -1, rep -2, giây +0.

| Buổi | Bài tập | Set × Rep/Thời gian | Nghỉ | Độ khó |
|---|---|---|---|---|
| **A** – Đẩy (ngực, vai, tay sau)<br>~37′ (đỉnh ~47′) | Đẩy ngực đòn (Bench press) | 4 × 8 | 120s | 4 |
|  | Đẩy vai tạ đơn ngồi ghế | 3 × 10 | 90s | 2 |
|  | Đẩy ngực trên máy | 3 × 12 | 75s | 2 |
|  | Nâng tạ ngang (Lateral raise) | 3 × 15 | 45s | 2 |
|  | Kéo cáp tay sau (Triceps pushdown) | 3 × 12 | 45s | 2 |
| **B** – Kéo (lưng, vai sau, tay trước)<br>~38′ (đỉnh ~48′) | Hít xà (Pull-up) | 4 × 6 | 120s | 5 |
|  | Kéo đòn cúi người (Barbell row) | 3 × 8 | 120s | 4 |
|  | Kéo cáp ngồi (Seated row) | 3 × 12 | 75s | 2 |
|  | Face pull | 3 × 15 | 45s | 2 |
|  | Cuốn tạ tay trước (Biceps curl) | 3 × 12 | 45s | 2 |
| **C** – Chân và mông<br>~42′ (đỉnh ~53′) | Squat với đòn (Back squat) | 4 × 6 | 150s | 4 |
|  | Romanian deadlift với đòn | 3 × 8 | 120s | 4 |
|  | Đạp đùi (Leg press) | 3 × 12 | 90s | 2 |
|  | Móc đùi trên máy (Leg curl) | 3 × 12 | 60s | 2 |
|  | Nhón gót | 3 × 20 | 30s | 1 |
| **D** – Đẩy (ngực, vai, tay sau)<br>~36′ (đỉnh ~46′) | Đẩy vai đòn đứng (Overhead press) | 4 × 8 | 120s | 4 |
|  | Đẩy ngực tạ đơn trên ghế | 3 × 10 | 90s | 3 |
|  | Hít đất chân cao | 3 × 10 | 60s | 4 |
|  | Nâng tạ ngang (Lateral raise) | 3 × 15 | 45s | 2 |
|  | Duỗi tay sau qua đầu với tạ đơn | 3 × 12 | 45s | 2 |
| **E** – Kéo (lưng, vai sau, tay trước)<br>~35′ (đỉnh ~44′) | Kéo xô (Lat pulldown) | 4 × 10 | 90s | 2 |
|  | Kéo tạ đơn một tay | 3 × 10/bên | 75s | 2 |
|  | Bay vai sau tạ đơn | 3 × 15 | 45s | 2 |
|  | Cuốn tạ tay trước (Biceps curl) | 3 × 12 | 45s | 2 |
|  | Treo xà co gối | 3 × 10 | 45s | 4 |
| **F** – Chân và mông<br>~40′ (đỉnh ~52′) | Đạp đùi (Leg press) | 4 × 12 | 120s | 2 |
|  | Bulgarian split squat với tạ đơn | 3 × 10/bên | 75s | 4 |
|  | Romanian deadlift tạ đơn (RDL) | 3 × 12 | 90s | 3 |
|  | Glute bridge với tạ đơn | 3 × 12 | 60s | 2 |
|  | Pallof press | 3 × 10/bên | 30s | 2 |

### 21. Tập nhẹ ở gym – 3 buổi  
`light_gym_3x`

| Nơi tập | Cấp độ | Buổi/tuần | Phút/buổi | Chia buổi | Mục tiêu |
|---|---|---|---|---|---|
| Phòng gym | Nhẹ (safety_flag) | 3 | 30 | toàn thân | khoẻ hơn – bền hơn, duy trì, giảm mỡ |

_3 buổi/tuần: đạp xe hoặc đi bộ nhẹ rồi vài bài máy tạ nhẹ; hãy hỏi ý kiến bác sĩ trước khi tập._

Tăng tiến theo tuần (so với tuần 1): T2: set +0, rep +1, giây +5; T3: set +0, rep +2, giây +5; T4: set +0, rep +2, giây +5.

| Buổi | Bài tập | Set × Rep/Thời gian | Nghỉ | Độ khó |
|---|---|---|---|---|
| **A** – Toàn thân<br>~25′ (đỉnh ~26′) | Đạp xe tại chỗ | 1 × 6 phút | 30s | 1 |
|  | Đạp đùi (Leg press) | 2 × 10 | 60s | 2 |
|  | Đẩy ngực trên máy | 2 × 10 | 60s | 2 |
|  | Kéo cáp ngồi (Seated row) | 2 × 10 | 60s | 2 |
|  | Bird dog | 2 × 4/bên | 45s | 1 |
| **B** – Toàn thân<br>~25′ (đỉnh ~26′) | Đi bộ dốc trên máy chạy | 1 × 6 phút | 30s | 1 |
|  | Ngồi xuống đứng lên ghế | 2 × 8 | 60s | 1 |
|  | Kéo xô (Lat pulldown) | 2 × 10 | 60s | 2 |
|  | Glute bridge | 2 × 8 | 60s | 1 |
|  | Dead bug | 2 × 4/bên | 45s | 1 |
| **C** – Toàn thân<br>~25′ (đỉnh ~26′) | Đạp xe tại chỗ | 1 × 6 phút | 30s | 1 |
|  | Móc đùi trên máy (Leg curl) | 2 × 10 | 60s | 2 |
|  | Đẩy vai tạ đơn ngồi ghế | 2 × 8 | 60s | 2 |
|  | Kéo giãn dây kháng lực | 2 × 10 | 45s | 1 |
|  | Nhón gót | 2 × 10 | 45s | 1 |

## Danh sách bài tập

Độ khó 1–5. Mũi tên: bài dễ hơn ← **bài** → bài khó hơn. "Đo": rep, rep/bên hoặc giây.

| # | Bài | Nhóm cơ | Dụng cụ | Độ khó | Đo | Dễ hơn ← → Khó hơn | Lưu ý an toàn |
|---|---|---|---|---|---|---|---|
| 1 | Ngồi xuống đứng lên ghế<br>`sit_to_stand` | quads, glutes | không | 1 | rep | — ← → Squat | Dùng ghế không có bánh xe, kê sát tường; người hay chóng mặt nên đứng dậy chậm. |
| 2 | Wall sit (ngồi dựa tường)<br>`wall_sit` | quads, glutes | không | 1 | giây | — ← → — | Người đau gối hoặc cao huyết áp nên ngồi cao hơn và giữ ngắn; dừng nếu đau khớp gối. |
| 3 | Squat<br>`squat_bodyweight` | quads, glutes | không | 1 | rep | Ngồi xuống đứng lên ghế ← → Split squat | Lỗi hay gặp là đầu gối chụm vào trong; chỉ xuống sâu đến mức không đau và không gù lưng. |
| 4 | Split squat<br>`split_squat_bodyweight` | quads, glutes | không | 2 | rep/bên | Squat ← → Lunge lùi | Người yếu thăng bằng nên vịn tường; đặt khăn dưới gối sau nếu sàn cứng. |
| 5 | Lunge lùi<br>`reverse_lunge_bodyweight` | quads, glutes, hamstrings | không | 3 | rep/bên | Split squat ← → Bulgarian split squat | Dễ đau gối hơn squat nếu bước quá ngắn; quay lại Split squat nếu mất thăng bằng. |
| 6 | Bulgarian split squat<br>`bulgarian_split_squat_bodyweight` | quads, glutes | không | 4 | rep/bên | Lunge lùi ← → Bulgarian split squat với tạ đơn | Bài đòi hỏi thăng bằng tốt; ghế phải không trượt, người đau gối nên giảm biên độ. |
| 7 | Bước lên bục<br>`step_up_bodyweight` | quads, glutes | không | 2 | rep/bên | Ngồi xuống đứng lên ghế ← → Lunge lùi | Chọn bục thấp khi mới tập; không dùng ghế nhựa hay bề mặt trơn. |
| 8 | Gập hông (hip hinge)<br>`hip_hinge_bodyweight` | hamstrings, glutes | không | 1 | rep | — ← → RDL một chân | Người đau lưng chỉ cúi ở biên độ không đau; đây là bài học kỹ thuật trước khi tập RDL. |
| 9 | RDL một chân<br>`single_leg_rdl_bodyweight` | hamstrings, glutes, core | không | 3 | rep/bên | Gập hông (hip hinge) ← → — | Dễ mất thăng bằng; tập gần tường và giảm biên độ nếu thấy căng lưng dưới. |
| 10 | Glute bridge<br>`glute_bridge` | glutes, hamstrings | thảm | 1 | rep | — ← → Glute bridge một chân | Phụ nữ mang thai từ tam cá nguyệt thứ hai nên tránh nằm ngửa lâu; hỏi bác sĩ trước. |
| 11 | Glute bridge một chân<br>`single_leg_glute_bridge` | glutes, hamstrings | thảm | 3 | rep/bên | Glute bridge ← → — | Nếu chuột rút sau đùi, đặt bàn chân gần mông hơn và quay lại bài hai chân. |
| 12 | Nhón gót<br>`calf_raise_bodyweight` | calves | không | 1 | rep | — ← → — | Người có vấn đề gân Achilles nên tập biên độ nhỏ và tăng dần. |
| 13 | Goblet squat<br>`goblet_squat` | quads, glutes, core | tạ đơn | 2 | rep | Squat ← → Split squat với tạ đơn | Chọn tạ nhẹ để giữ lưng thẳng; dừng nếu đau gối hoặc lưng dưới. |
| 14 | Split squat với tạ đơn<br>`split_squat_dumbbell` | quads, glutes | tạ đơn | 3 | rep/bên | Split squat ← → Bulgarian split squat với tạ đơn | Nếu mất thăng bằng, dùng một tạ và tay kia vịn tường. |
| 15 | Bulgarian split squat với tạ đơn<br>`bulgarian_split_squat_dumbbell` | quads, glutes | tạ đơn | 4 | rep/bên | Split squat với tạ đơn ← → — | Thành thạo bản không tạ trước; tạ nặng làm tăng nguy cơ ngã. |
| 16 | Romanian deadlift tạ đơn (RDL)<br>`rdl_dumbbell` | hamstrings, glutes, back | tạ đơn | 3 | rep | Gập hông (hip hinge) ← → Romanian deadlift với đòn | Lỗi hay gặp là gù lưng khi hạ thấp; người đau lưng nên học Gập hông trước. |
| 17 | Glute bridge với tạ đơn<br>`glute_bridge_dumbbell` | glutes, hamstrings | tạ đơn, thảm | 2 | rep | Glute bridge ← → Glute bridge một chân | Lót khăn dưới tạ cho đỡ đau xương hông; người mang thai hỏi bác sĩ trước khi nằm ngửa. |
| 18 | Đạp đùi (Leg press)<br>`leg_press` | quads, glutes | máy | 2 | rep | Squat ← → Squat với đòn (Back squat) | Không hạ quá sâu đến mức hông cuộn lên; luôn khoá chốt an toàn khi xong. |
| 19 | Squat với đòn (Back squat)<br>`back_squat_barbell` | quads, glutes, core | đòn | 4 | rep | Goblet squat ← → — | Chỉ tập trong khung có thanh an toàn; không thử mức tạ tối đa, dừng 2 rep trước khi hỏng kỹ thuật. |
| 20 | Romanian deadlift với đòn<br>`rdl_barbell` | hamstrings, glutes, back | đòn | 4 | rep | Romanian deadlift tạ đơn (RDL) ← → — | Người có tiền sử đau lưng nên dùng tạ đơn nhẹ trước; không gù lưng để với thấp hơn. |
| 21 | Móc đùi trên máy (Leg curl)<br>`leg_curl_machine` | hamstrings | máy | 2 | rep | — ← → Romanian deadlift tạ đơn (RDL) | Bắt đầu với mức tạ nhẹ; dễ bị chuột rút sau đùi nếu tăng tạ quá nhanh. |
| 22 | Hít đất dựa tường<br>`wall_push_up` | chest, triceps, shoulders | không | 1 | rep | — ← → Hít đất quỳ gối | Phù hợp người mới và người cần tập nhẹ; dừng nếu đau vai. |
| 23 | Hít đất quỳ gối<br>`knee_push_up` | chest, triceps, shoulders | thảm | 1 | rep | Hít đất dựa tường ← → Hít đất tay cao | Lót thảm dưới gối; người đau cổ tay có thể nắm tay hoặc dùng tay cầm hít đất. |
| 24 | Hít đất tay cao<br>`incline_push_up` | chest, triceps, shoulders, core | không | 2 | rep | Hít đất quỳ gối ← → Hít đất | Bề mặt chống phải chắc, không trượt; lỗi hay gặp là võng hông. |
| 25 | Hít đất<br>`push_up` | chest, triceps, shoulders, core | không | 3 | rep | Hít đất tay cao ← → Hít đất chân cao | Nếu võng hông hoặc chỉ xuống được nửa chừng, quay lại Hít đất tay cao. |
| 26 | Hít đất chân cao<br>`decline_push_up` | chest, shoulders, triceps, core | không | 4 | rep | Hít đất ← → — | Chỉ tập khi làm được 15 lần Hít đất chuẩn; người cao huyết áp nên tránh tư thế đầu thấp. |
| 27 | Hít đất tay hẹp<br>`diamond_push_up` | triceps, chest | không | 4 | rep | Hít đất ← → — | Dễ gây đau cổ tay và khuỷu; không cần chụm tay thành hình kim cương nếu thấy khó chịu. |
| 28 | Pike push-up<br>`pike_push_up` | shoulders, triceps | không | 4 | rep | Hít đất ← → — | Không phù hợp người cao huyết áp, tăng nhãn áp hoặc đau vai; tư thế đầu thấp. |
| 29 | Đẩy ngực tạ đơn trên sàn<br>`floor_press_dumbbell` | chest, triceps, shoulders | tạ đơn, thảm | 2 | rep | Hít đất quỳ gối ← → Đẩy ngực tạ đơn trên ghế | Sàn giới hạn biên độ nên thân thiện với vai; nhờ người đưa tạ hoặc lăn tạ lên khi tạ nặng. |
| 30 | Đẩy ngực tạ đơn trên ghế<br>`bench_press_dumbbell` | chest, triceps, shoulders | tạ đơn, ghế tập | 3 | rep | Đẩy ngực tạ đơn trên sàn ← → Đẩy ngực đòn (Bench press) | Không hạ tạ quá sâu nếu đau vai; tạ nặng nên đặt lên đùi trước rồi mới nằm xuống. |
| 31 | Đẩy vai tạ đơn ngồi ghế<br>`seated_shoulder_press_dumbbell` | shoulders, triceps | tạ đơn | 2 | rep | — ← → Đẩy vai tạ đơn đứng | Người đau vai hoặc không giơ tay qua đầu được thoải mái nên bỏ qua bài này. |
| 32 | Đẩy vai tạ đơn đứng<br>`shoulder_press_dumbbell` | shoulders, triceps, core | tạ đơn | 3 | rep | Đẩy vai tạ đơn ngồi ghế ← → Đẩy vai đòn đứng (Overhead press) | Lỗi hay gặp là ưỡn lưng để đẩy tạ nặng; giảm tạ hoặc chuyển sang ngồi. |
| 33 | Nâng tạ ngang (Lateral raise)<br>`lateral_raise_dumbbell` | shoulders | tạ đơn | 2 | rep | — ← → — | Không nâng cao quá vai nếu bị đau vai; chọn tạ rất nhẹ. |
| 34 | Duỗi tay sau qua đầu với tạ đơn<br>`overhead_triceps_extension_dumbbell` | triceps | tạ đơn | 2 | rep | — ← → Hít đất tay hẹp | Người đau vai hoặc khuỷu nên dùng tạ nhẹ, giảm biên độ hoặc bỏ qua. |
| 35 | Đẩy ngực trên máy<br>`chest_press_machine` | chest, triceps, shoulders | máy | 2 | rep | Hít đất dựa tường ← → Đẩy ngực tạ đơn trên ghế | Máy giữ quỹ đạo ổn định nên phù hợp người mới; bắt đầu tạ nhẹ. |
| 36 | Đẩy ngực đòn (Bench press)<br>`bench_press_barbell` | chest, triceps, shoulders | đòn, ghế tập | 4 | rep | Đẩy ngực tạ đơn trên ghế ← → — | Luôn có chốt an toàn hoặc người hỗ trợ; không tập tạ tối đa, dừng 2 rep trước khi hỏng. |
| 37 | Đẩy vai đòn đứng (Overhead press)<br>`overhead_press_barbell` | shoulders, triceps, core | đòn | 4 | rep | Đẩy vai tạ đơn đứng ← → — | Bài khó cho vai và lưng dưới; người đau vai không nên tập, ưu tiên tạ đơn ngồi. |
| 38 | Kéo cáp tay sau (Triceps pushdown)<br>`triceps_pushdown_cable` | triceps | cáp | 2 | rep | — ← → Hít đất tay hẹp | Người đau khuỷu nên dùng dây cáp thay thanh thẳng và giảm tạ. |
| 39 | Kéo khung cửa<br>`doorway_row` | back, biceps | không | 1 | rep | — ← → Kéo xà dưới gầm bàn (Inverted row) | Dùng khung cửa chắc chắn, tay nắm chắc; không ngả người quá xa khi mới tập. |
| 40 | Kéo xà dưới gầm bàn (Inverted row)<br>`table_row` | back, biceps, core | không | 3 | rep | Kéo khung cửa ← → Hít xà hạ chậm (Negative pull-up) | Chỉ dùng bàn gỗ/kim loại nặng, không lật; thử tải trước khi tập. Có xà đơn thì dùng xà thấp an toàn hơn. |
| 41 | Nằm sấp nâng tay chữ Y<br>`prone_y_raise` | back, shoulders | thảm | 1 | rep | — ← → Thiên thần tuyết nằm sấp | Bài nhẹ cho tư thế và vai; dừng nếu có cảm giác chèn ép vai. |
| 42 | Thiên thần tuyết nằm sấp<br>`reverse_snow_angel` | back, shoulders | thảm | 2 | rep | Nằm sấp nâng tay chữ Y ← → — | Người đau lưng dưới nên để ngực chạm sàn và chỉ nâng tay. |
| 43 | Kéo tạ đơn một tay<br>`one_arm_row_dumbbell` | back, biceps | tạ đơn | 2 | rep/bên | Kéo khung cửa ← → Kéo tạ đơn cúi người | Tay chống giúp đỡ lưng nên phù hợp người mới; không giật tạ. |
| 44 | Kéo tạ đơn cúi người<br>`bent_over_row_dumbbell` | back, biceps | tạ đơn | 3 | rep | Kéo tạ đơn một tay ← → Kéo đòn cúi người (Barbell row) | Không có điểm chống nên tải lên lưng dưới; người đau lưng dùng Kéo tạ đơn một tay. |
| 45 | Bay vai sau tạ đơn<br>`rear_delt_fly_dumbbell` | shoulders, back | tạ đơn | 2 | rep | Nằm sấp nâng tay chữ Y ← → — | Ngồi cúi giúp đỡ lưng hơn đứng cúi; tránh đung đưa người. |
| 46 | Cuốn tạ tay trước (Biceps curl)<br>`biceps_curl_dumbbell` | biceps | tạ đơn | 2 | rep | — ← → — | Lỗi hay gặp là dùng lưng để vung tạ; giảm tạ nếu phải đung đưa. |
| 47 | Kéo xô (Lat pulldown)<br>`lat_pulldown` | back, biceps | cáp | 2 | rep | — ← → Hít xà hạ chậm (Negative pull-up) | Không kéo thanh ra sau gáy; không ngả người quá nhiều để kéo tạ nặng. |
| 48 | Kéo cáp ngồi (Seated row)<br>`seated_cable_row` | back, biceps | cáp | 2 | rep | — ← → Kéo tạ đơn cúi người | Lỗi hay gặp là đổ người ra sau để kéo; giảm tạ để giữ lưng thẳng. |
| 49 | Kéo đòn cúi người (Barbell row)<br>`bent_over_row_barbell` | back, biceps, hamstrings | đòn | 4 | rep | Kéo tạ đơn cúi người ← → — | Tải nhiều lên lưng dưới; người đau lưng nên dùng Kéo cáp ngồi hoặc tạ đơn một tay. |
| 50 | Face pull<br>`face_pull_cable` | shoulders, back | cáp | 2 | rep | Kéo giãn dây kháng lực ← → — | Bài tốt cho tư thế vai; không dùng tạ nặng đến mức phải ngả người. |
| 51 | Kéo giãn dây kháng lực<br>`band_pull_apart` | back, shoulders | dây kháng lực | 1 | rep | — ← → Face pull | Kiểm tra dây không bị rách trước khi tập; bài rất nhẹ, phù hợp khởi động. |
| 52 | Hít xà hạ chậm (Negative pull-up)<br>`negative_pull_up` | back, biceps | xà đơn | 3 | rep | Kéo xô (Lat pulldown) ← → Hít xà (Pull-up) | Gây đau cơ nhiều ngày sau; bắt đầu ít lần và không tập liên tục nhiều ngày. |
| 53 | Hít xà (Pull-up)<br>`pull_up` | back, biceps, core | xà đơn | 5 | rep | Hít xà hạ chậm (Negative pull-up) ← → — | Chỉ tập khi hạ chậm được 5 lần; người đau vai hoặc khuỷu nên giữ ở Kéo xô. |
| 54 | Plank quỳ gối<br>`knee_plank` | core | thảm | 1 | giây | — ← → Plank | Người cao huyết áp nên giữ ngắn và thở đều; dừng nếu đau lưng dưới. |
| 55 | Plank<br>`plank` | core | thảm | 2 | giây | Plank quỳ gối ← → Leo núi (Mountain climber) | Dừng khi hông bắt đầu võng; giữ đúng tư thế quan trọng hơn giữ lâu. |
| 56 | Plank nghiêng quỳ gối<br>`side_plank_knee` | core | thảm | 1 | giây | — ← → Plank nghiêng | Người đau vai bên chống nên giảm thời gian; tư thế nhẹ cho lưng. |
| 57 | Plank nghiêng<br>`side_plank` | core | thảm | 3 | giây | Plank nghiêng quỳ gối ← → — | Nếu không giữ được hông, quay lại bản quỳ gối. |
| 58 | Dead bug<br>`dead_bug` | core | thảm | 1 | rep/bên | — ← → Gập bụng ngược (Reverse crunch) | Bài an toàn cho lưng; người mang thai từ tam cá nguyệt thứ hai nên thay bằng Bird dog. |
| 59 | Bird dog<br>`bird_dog` | core, glutes, back | thảm | 1 | rep/bên | — ← → — | Bài nhẹ, thân thiện với lưng; lót thảm dưới gối. |
| 60 | Gập bụng ngược (Reverse crunch)<br>`reverse_crunch` | core | thảm | 2 | rep | Dead bug ← → Treo xà co gối | Người thoát vị đĩa đệm nên hỏi bác sĩ; không phù hợp phụ nữ mang thai. |
| 61 | Treo xà co gối<br>`hanging_knee_raise` | core | xà đơn | 4 | rep | Gập bụng ngược (Reverse crunch) ← → — | Cần đủ lực tay để treo; người đau vai nên thay bằng Gập bụng ngược. |
| 62 | Pallof press<br>`pallof_press_cable` | core | cáp | 2 | rep/bên | Dead bug ← → — | Bài chống xoay an toàn cho lưng; bắt đầu tạ nhẹ. |
| 63 | Xách tạ đi bộ (Farmer carry)<br>`farmer_carry_dumbbell` | core, full_body | tạ đơn | 2 | giây | — ← → — | Đặt tạ xuống bằng cách gập gối, không cúi lưng; đi ở chỗ không vướng. |
| 64 | Đi bộ tại chỗ nâng gối<br>`marching_in_place` | cardio | không | 1 | giây | — ← → Jumping jack không nhảy | Không va đập, phù hợp người mới và người cần tập nhẹ; vịn ghế nếu yếu thăng bằng. |
| 65 | Jumping jack không nhảy<br>`step_jack` | cardio, full_body | không | 1 | giây | Đi bộ tại chỗ nâng gối ← → Burpee bước lùi (không nhảy) | Phiên bản không va đập, an toàn cho gối; người đau vai có thể chỉ giơ tay ngang vai. |
| 66 | Burpee bước lùi (không nhảy)<br>`burpee_step_back` | full_body, cardio | không | 3 | rep | Jumping jack không nhảy ← → Burpee | Người cao huyết áp hoặc hay chóng mặt nên đứng lên chậm; không phù hợp nhóm tập nhẹ. |
| 67 | Burpee<br>`burpee` | full_body, cardio | không | 4 | rep | Burpee bước lùi (không nhảy) ← → — | Bài va đập cao, chỉ dành cho người đã quen tập; người đau gối, cổ chân, thừa cân nhiều nên dùng bản bước lùi. |
| 68 | Leo núi (Mountain climber)<br>`mountain_climber` | core, cardio, shoulders | không | 3 | giây | Plank ← → Burpee | Người đau cổ tay hoặc vai nên làm chậm hoặc chống tay lên ghế cho nhẹ hơn. |
| 69 | Đạp xe tại chỗ<br>`stationary_bike` | cardio, quads | máy | 1 | giây | — ← → — | Không va đập, phù hợp người thừa cân và người đau gối nhẹ; dừng nếu tức ngực hoặc chóng mặt. |
| 70 | Đi bộ dốc trên máy chạy<br>`treadmill_incline_walk` | cardio, glutes, calves | máy | 1 | giây | — ← → — | Kẹp dây an toàn vào áo; bắt đầu dốc thấp nếu đau bắp chân hoặc gân Achilles. |
