# Hướng dẫn cài đặt — Android TV Home Skin

> **Lưu ý trước khi bắt đầu:** bản này được dựng sẵn dạng project Android Studio hoàn chỉnh, **chưa từng chạy trên tivi Android thật** (tác giả đang dùng LG). Các bước dưới đây là quy trình chuẩn để build + cài 1 launcher Android TV tự làm.
>
> ⚠️ **Tự chịu trách nhiệm:** đây là phần mềm cộng đồng cài ngoài kho chính thức. **Nâng cấp firmware tivi** có thể đặt lại launcher mặc định về bản gốc, gỡ app tự cài hoặc làm app ngừng chạy — cân nhắc kỹ trước khi nâng cấp; mất thì cài lại theo hướng dẫn này.

## 1. Build APK bằng Android Studio

1. Cài **Android Studio** (developer.android.com, miễn phí) trên máy tính.
2. Mở Android Studio → **Open** → chọn thư mục repo này. Chờ Gradle sync xong (lần đầu tải hơi lâu; project không dùng thư viện ngoài nên nhẹ).
3. Menu **Build → Build App Bundle(s) / APK(s) → Build APK(s)**.
4. APK nằm ở `app/build/outputs/apk/debug/app-debug.apk`.

*Không có Android Studio? Dùng dòng lệnh: cài JDK 17 + Android SDK, rồi chạy `./gradlew assembleDebug` trong thư mục repo (lần đầu cần `gradle wrapper` nếu máy chưa có sẵn gradle).*

## 2. Bật gỡ lỗi ADB trên tivi

1. Vào **Cài đặt → Tuỳ chọn thiết bị → Giới thiệu** → bấm dòng **Bản dựng (Build)** 7 lần để mở Tuỳ chọn nhà phát triển.
2. Vào **Cài đặt → Tuỳ chọn thiết bị → Tuỳ chọn nhà phát triển** → bật **Gỡ lỗi USB** (và **Gỡ lỗi qua Wi-Fi / Wireless debugging** nếu có).
3. Xem IP của tivi trong **Cài đặt → Mạng & Internet**.

## 3. Cài APK lên tivi

Trên máy tính (cùng Wi-Fi với tivi), mở Terminal:

```bash
adb connect 192.168.1.20:5555        # IP tivi của bạn
adb install app/build/outputs/apk/debug/app-debug.apk
```

Lần kết nối đầu, tivi hiện bảng hỏi cho phép gỡ lỗi — chọn **Cho phép** (Allow).

## 4. Đặt làm màn hình chính mặc định

- **Android TV đời cũ:** bấm Home — tivi hỏi chọn launcher, chọn **Android Home Skin** → **Luôn luôn**.
- **Google TV đời mới (thường không hiện hộp hỏi):** dùng ADB:

```bash
adb shell cmd package set-home-activity cloud.khuong.androidhomeskin/.MainActivity
```

Hoặc cài thêm app kiểu **Launcher Manager** từ CH Play để chuyển qua lại giữa launcher này và launcher gốc.

Muốn quay về launcher gốc: `adb shell cmd package set-home-activity` trỏ lại package launcher gốc của hãng (ví dụ Google TV: `com.google.android.apps.tv.launcherx/.home.HomeActivity` — khác nhau tuỳ hãng), hoặc gỡ app này đi là tivi tự về launcher gốc:
`adb uninstall cloud.khuong.androidhomeskin`.

## 5. Dùng hàng ngày

- **Trái/phải:** di chuyển giữa các ô; **OK:** mở app; tên app chỉ hiện dưới ô đang chọn.
- **Home:** về launcher này (khi đã đặt mặc định). **Back:** trong launcher không có tác dụng gì đặc biệt.
- Thứ tự đầu hàng mặc định: YouTube → Apple Music → Netflix → Apple TV (khớp theo tên app), phần còn lại theo bảng chữ cái. App mới cài sẽ tự xuất hiện khi quay về màn hình chính.

## 6. Tuỳ biến (trong `MainActivity.kt`)

> **Lưu ý:** bản LG (bản gốc) đã có thêm màn hình Settings chỉnh ngay trên tivi + trang chỉnh bằng điện thoại (cập nhật 26/10). Bản Android TV này chưa có — các tuỳ biến vẫn sửa trong code như bên dưới.

- **Thứ tự app:** sửa danh sách `orderLabels` (khớp một phần tên app, không phân biệt hoa thường).
- **Thành phố thời tiết:** sửa `weatherCity`, `latitude`, `longitude` (mặc định TP.HCM, nguồn Open-Meteo miễn phí).
- **Hình nền:** thay file `app/src/main/res/drawable-nodpi/wallpaper.jpg` bằng ảnh 16:9 của bạn.
- **Icon app:** bản này dùng icon hệ thống trả về cho từng app; muốn icon riêng cho 1 app thì cần vá thêm code (chưa có sẵn cơ chế như bản LG).

Sau khi sửa: build + cài lại như mục 1 và 3 (cài đè giữ nguyên dữ liệu).

## 7. Lỗi thường gặp

- **Gradle sync / build lỗi lần đầu:** thường do chưa nhận JDK/Android SDK — để Android Studio tự tải khi mở project; kiểm tra mạng.
- **`adb connect` thất bại:** khác mạng Wi-Fi, chưa bật gỡ lỗi, hoặc IP tivi đổi (nên đặt IP tĩnh). Tivi Android 11+ có thể dùng Wireless debugging kèm mã ghép nối 6 số (`adb pair`).
- **Cài xong nhưng bấm Home vẫn về launcher cũ:** chưa đặt mặc định thành công — làm lại mục 4; một số hãng (Google TV) bắt buộc dùng lệnh ADB.
- **Thiếu app trong hàng icon:** app đó không khai báo cho TV (không có intent LEANBACK/LAUNCHER cho remote) thì launcher sẽ không thấy — đây là giới hạn của chính app đó.
- **Không có dòng thời tiết:** tivi chưa có internet hoặc Open-Meteo tạm lỗi; app tự thử lại sau 30 phút.

## 8. Ghi công

Bản Android của bộ home skin (cùng nhà với bản LG webOS và Samsung Tizen trên GitHub của tác giả), thiết kế gốc từ webos-vibehack (OmkarK97) / cleanyourtv (Gathin). Trên Android, dự án CleanYourTV của Gathin đã có hướng dẫn tiếng Việt đầy đủ nếu bạn muốn thêm phần dọn bloatware/quảng cáo hệ thống. Hình nền là fan-art Dragon Ball sưu tầm từ Pinterest — chỉ dùng cho mục đích cá nhân.
