# Gym Leveling — Design System

Nguồn thiết kế: `design/gym-leveling.pen` (mở bằng Pencil). Ảnh xuất: `design/screens/`.
Code Flutter phải lấy giá trị từ file này — không tự chế màu/khoảng cách.

## Tinh thần

"Lên cấp mỗi ngày": năng lượng của game (rank, RP, streak) nhưng gọn gàng, dễ đọc cho người mới tập.
- **Primary = cam ánh vàng (amber)** — màu của RP, tiến độ, hành động chính.
- **Accent = cam đỏ** — chỉ dùng cho streak/lửa.
- Mỗi rank có màu riêng, chỉ dùng cho huy hiệu/emblem rank.
- Hai theme: **Sáng** (trắng pha be, ấm) và **Tối** (đen ấm, không xanh lạnh). Mặc định theo hệ thống, đổi được trong Cài đặt.

## Màu (token → Sáng / Tối)

| Token | Sáng | Tối | Dùng cho |
|---|---|---|---|
| `bg` | `#FAF6EF` | `#0E0D0B` | Nền màn hình |
| `surface` | `#FFFFFF` | `#181613` | Card |
| `surface-2` | `#F3EDE3` | `#221F1B` | Nền phụ, track progress, ô stat |
| `border` | `#E7DFD2` | `#2F2B25` | Viền, divider |
| `text-primary` | `#1E1B16` | `#F5F2EC` | Chữ chính |
| `text-secondary` | `#6B6358` | `#A39C90` | Chữ phụ |
| `text-muted` | `#A69D8F` | `#625C52` | Chữ mờ, trạng thái chưa kích hoạt |
| `primary` | `#FFA21F` | `#FFAA2C` | Nút chính, thanh tiến độ, nền đã chọn (**không** dùng cho chữ trên nền sáng) |
| `primary-text` | `#B05A00` | `#FFB547` | Chữ/icon màu primary (đủ tương phản) |
| `primary-soft` | `#FFA21F24` | `#FFAA2C1F` | Nền tint cho thẻ được chọn, tag |
| `on-primary` | `#1E1406` | `#1E1406` | Chữ/icon trên nền `primary` |
| `accent` | `#E2451B` | `#FF6A3D` | Streak |
| `accent-soft` | `#E2451B1A` | `#FF6A3D1F` | Nền chip streak |
| `danger` | `#D1344A` | `#FF5468` | Lỗi, trừ điểm |
| `success` | `#2E8F4A` | `#5ED47F` | Thành công |
| `warning` | `#B7791F` | `#F5C542` | Cảnh báo (sắp bị trừ điểm) |
| `info` | `#2F6FDB` | `#7AA8FF` | Thông tin, bước chân |

### Màu rank

| Rank | Token | Sáng | Tối | Icon (lucide) |
|---|---|---|---|---|
| Sắt | `rank-iron` | `#5F6670` | `#8A929C` | `shield` |
| Đồng | `rank-bronze` | `#A35A26` | `#D08A52` | `shield` |
| Bạc | `rank-silver` | `#6F7A88` | `#C9D1DC` | `shield` |
| Vàng | `rank-gold` | `#A87A00` | `#F5C542` | `crown` |
| Bạch kim | `rank-platinum` | `#0E8F80` | `#4FE3D0` | `hexagon` |
| Kim cương | `rank-diamond` | `#3563D6` | `#7AA8FF` | `gem` |
| Cao thủ | `rank-master` | `#8A35D0` | `#C56BFF` | `swords` |

Huy hiệu rank: viền = màu rank, nền = màu rank ~12–15% alpha, chữ + icon = màu rank.

## Chữ

| Vai trò | Font | Cỡ / weight |
|---|---|---|
| Display (số RP lớn, rank) | Chakra Petch | 28–40 / 700 |
| Tiêu đề màn hình | Chakra Petch | 22 / 700 (giống nhau mọi màn) |
| Heading section | Chakra Petch | 17–20 / 700 |
| Title (tên bài, tên card) | Be Vietnam Pro | 15–16 / 600 |
| Body | Be Vietnam Pro | 14–15 / 400, line-height 1.45 |
| Caption / meta | Be Vietnam Pro | 12–13 / 400–500 |
| Eyebrow | Be Vietnam Pro | 11 / 600, letter-spacing 1.5–2, VIẾT HOA |
| Số liệu (RP, streak) | Chakra Petch | theo ngữ cảnh / 700 |

Cả hai font đều hỗ trợ đầy đủ tiếng Việt. Flutter: `google_fonts` hoặc đóng gói font vào assets.

## Khoảng cách & bo góc

- Spacing: `xs 4 · sm 8 · md 12 · lg 16 · xl 24 · 2xl 32`.
- Padding ngang màn hình: 20. Khoảng cách giữa section: 16–24.
- Bo góc: `radius-sm 8` (ô nhỏ), `radius 12` (card, nút, input), `radius-lg 20` (card lớn: giáo án, buổi tập), `pill 999` (chip, badge, progress).
- Viền 1px `border` thay cho shadow. Không dùng gradient.

## Icon

Lucide, nét 2px, cỡ 14 (trong badge) / 18–22 (thường) / 30+ (emblem).

## Components (trong file .pen)

| Component | Mô tả |
|---|---|
| `Button/Primary` | Nền `primary`, chữ `on-primary`, padding 14×24, radius 12, có icon tuỳ chọn |
| `Button/Secondary` | Nền `surface-2`, chữ `text-primary` |
| `Rank Badge` | Pill viền màu rank + icon + tên rank viết hoa |
| `Rank Card` | Emblem + eyebrow "CẤP BẬC HIỆN TẠI" + tên rank lớn |
| `XP Bar` (thanh RP) | Nhãn "Bạc III → Bạc II" + "960 / 1.050 RP" + track 8px |
| `Streak Chip` | Icon lửa + số ngày, nền `accent-soft` |
| `Quest Card` | Icon box + tiêu đề + meta + RP + progress |
| `Option` | Lựa chọn onboarding: icon + tiêu đề + phụ đề + check tròn. Trạng thái chọn: viền `primary`, nền `primary-soft` |
| `Number Input` | Nhãn + giá trị lớn (Chakra Petch 24) + đơn vị |
| `Plan Card` | Tag + tên giáo án + mô tả + 2 ô stat (buổi/tuần, phút) + dải 7 ngày + nút chọn |
| `Leaderboard Row` | Vị trí + avatar chữ tắt + tên + rank badge + RP. Dòng "Bạn": viền `primary`, nền `primary-soft` |
| `Achievement` | Icon box màu + tên + progress 4px + meta "68.420 / 100.000 · +50 RP" |
| `Tab Bar` | 5 tab: Trang chủ · Nhiệm vụ · Bài tập · Xếp hạng · Hồ sơ. Tab đang chọn: `primary-text` |

## Màn hình đã thiết kế

| Màn | Theme mẫu | Ghi chú |
|---|---|---|
| Onboarding (câu hỏi nơi tập) | Sáng | Thanh tiến độ bước 4/8, danh sách Option, nút Tiếp tục ở dưới |
| Kết quả rank + chọn giáo án | Sáng | Rank khởi đầu (tối đa Bạc) + 2 Plan Card (phương án 1 nổi bật) |
| Trang chủ | Sáng + Tối | Streak, Rank Card, thanh RP, dải 7 ngày (đã tập / hôm nay / nghỉ), **Buổi tập hôm nay** = danh sách bài có check + RP từng bài + nút Tiếp tục |
| Bảng xếp hạng | Tối | Tab Tuần / Toàn server / Theo rank, bục top 3, danh sách, dòng của bạn |
| Hồ sơ | Sáng | Stats, hành trình 7 rank, thanh RP, lưới thành tựu |

Mọi màn đều dùng token nên đổi theme chỉ là đổi `mode` — không cần thiết kế lại.

## Ánh xạ sang Flutter

- `ThemeData` Material 3 với 2 `ColorScheme` (light/dark) + `ThemeExtension<GymColors>` chứa các token không có trong ColorScheme (`surface2`, `textMuted`, `primaryText`, `primarySoft`, `accentSoft`, 7 màu rank).
- `ThemeExtension<GymSpacing>` hoặc hằng số `Gap.xs…xxl`, `Radii.sm/md/lg/pill`.
- `TextTheme` theo bảng Chữ ở trên.
- Thứ tự: token → component dùng chung (`RankBadge`, `RpBar`, `StreakChip`, `PlanCard`…) → màn hình.
