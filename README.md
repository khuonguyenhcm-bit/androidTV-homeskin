# androidTV-homeskin

**Tiếng Việt** — Bản **Android TV / Google TV** của bộ home skin (cùng dòng với [bản LG webOS](https://github.com/khuonguyenhcm-bit/LGtv-Webos-homeskin) và [bản Samsung Tizen](https://github.com/khuonguyenhcm-bit/Samsungtv-Tizen-homeskin)): hình nền tràn màn hình, đồng hồ lớn + thời tiết góc trái, **một hàng icon vuông ở đáy**, tên app chỉ hiện khi ô được chọn. App đăng ký làm **launcher (màn hình chính) mặc định** được.

**English** — The **Android TV / Google TV** edition of the home skin (sibling of the [LG webOS](https://github.com/khuonguyenhcm-bit/LGtv-Webos-homeskin) and [Samsung Tizen](https://github.com/khuonguyenhcm-bit/Samsungtv-Tizen-homeskin) ports): full-screen wallpaper, big clock + weather top-left, **one bottom row of square app tiles**, labels shown only on the focused tile. Registers as a **default home (launcher) app**.

![Launcher screenshot — LG version / bản LG](https://raw.githubusercontent.com/khuonguyenhcm-bit/LGtv-Webos-homeskin/main/docs/screenshot.jpg)

> Ảnh trên là bản LG đang chạy thật; bản Android dùng chung thiết kế.
> The photo shows the LG version running; the Android port shares its design.

## ⚠️ Trạng thái / Status

**Bản dựng trước, CHƯA kiểm chứng trên tivi Android thật** (tác giả đang dùng LG). Đây là project Android Studio hoàn chỉnh (Kotlin, không thư viện ngoài) — mở ra build là có APK. Lần cài đầu lên máy thật có thể cần chỉnh nhỏ; phản hồi/PR rất được chào đón.

**Pre-built, NOT yet tested on a real Android TV** (the author runs an LG). This is a complete Android Studio project (Kotlin, zero third-party dependencies) — open it, build, get an APK. First installs on real hardware may need small fixes; reports and PRs are welcome.

## ⚠️ Cảnh báo — tự chịu trách nhiệm / Warning — use at your own risk

Đây là phần mềm cộng đồng cài ngoài kho chính thức (sideload) — **bạn tự chịu trách nhiệm khi cài và sử dụng**. Đặc biệt về **nâng cấp firmware của tivi**: bản cập nhật của hãng có thể đổi launcher mặc định về bản gốc, gỡ app tự cài hoặc làm app ngừng hoạt động. Cân nhắc kỹ trước khi nâng cấp firmware; nếu app/launcher mất sau khi nâng cấp, cài lại theo hướng dẫn trong repo. Tác giả không chịu trách nhiệm cho trục trặc phát sinh.

This is community software installed outside the official store (sideloading) — **you install and use it at your own risk**. Regarding **TV firmware upgrades**: a vendor update can reset the default launcher to stock, remove sideloaded apps, or break the app. Think before upgrading; if the launcher is lost after an upgrade, reinstall following this repo's guide. The author takes no responsibility for issues arising from installing, using, or upgrading.

## Giới hạn cần biết / Known limitations

1. **Google TV kén launcher mặc định:** nhiều máy Google TV đời mới không cho chọn launcher ngoài trong Cài đặt — phải đặt qua ADB (`cmd package set-home-activity`) hoặc app trung gian kiểu Launcher Manager; Android TV đời cũ (trước Google TV) thì chọn thẳng trong Cài đặt.
   **Google TV is picky about default launchers:** many newer Google TV devices hide the picker — set it via ADB (`cmd package set-home-activity`) or a Launcher Manager-style helper; older Android TV lets you pick in Settings.
2. **Icon dùng của hệ thống:** bản này lấy icon do từng app cung cấp (qua PackageManager) nên độ nét tuỳ app — chưa có bộ icon thay thế 1024 như bản LG.
   **Icons come from each app** (via PackageManager), so sharpness varies — no bundled 1024 replacement set like the LG version yet.
3. **Chưa có kéo-thả sắp xếp** trên tivi — thứ tự chỉnh trong code (`orderLabels` trong `MainActivity.kt`).
   **No on-TV drag-to-reorder yet** — order is set in code (`orderLabels` in `MainActivity.kt`).

## Cài đặt / Install

Đọc **[HUONG-DAN.md](HUONG-DAN.md)** (tiếng Việt) hoặc **[GUIDE-EN.md](GUIDE-EN.md)** (English). Tóm tắt: mở project bằng Android Studio → Build APK → chép APK vào tivi cài qua ADB → đặt làm launcher mặc định.

Short version: open the project in Android Studio → build the APK → install it on the TV over ADB → set it as the default launcher.

## Ghi công / Credits

- Thiết kế + tài nguyên: bộ home skin của [khuong nguyen](https://khuong.cloud), gốc gác từ webos-vibehack (OmkarK97) / cleanyourtv (Gathin).
- Trên Android đã có sẵn dự án gốc **CleanYourTV** của Gathin (dọn bloatware + launcher) kèm [hướng dẫn tiếng Việt](https://github.com/Gathin23/cleanyourtv/blob/main/README.vi.md) — repo này là một launcher tối giản theo cùng phong cách giao diện, không liên quan đến bộ dọn bloatware đó.
- Hình nền là fan-art Dragon Ball (Kame House) sưu tầm từ Pinterest; icon app thuộc về các hãng tương ứng — chỉ dùng cho mục đích cá nhân.
- Design and assets: khuong nguyen's home-skin family, originating from webos-vibehack (OmkarK97) / cleanyourtv (Gathin). Gathin's CleanYourTV already covers Android (debloat + launcher) with a Vietnamese guide — this repo is a minimal launcher in the same visual style, unrelated to that debloat toolset. Wallpaper is Dragon Ball fan art from Pinterest; app icons belong to their brands — personal use only.
