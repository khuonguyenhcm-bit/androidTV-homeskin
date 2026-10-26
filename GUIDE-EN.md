# Install guide — Android TV Home Skin

> **Before you start:** this is a complete Android Studio project that has **never run on a real Android TV** (the author runs an LG). The steps below are the standard flow for building and installing a self-made Android TV launcher.
>
> ⚠️ **Use at your own risk:** this is community software installed outside the official store. A **TV firmware upgrade** can reset the default launcher to stock, remove sideloaded apps, or break the app — think before upgrading; if it's lost, reinstall with this guide.

## 1. Build the APK in Android Studio

1. Install **Android Studio** (developer.android.com, free).
2. **Open** this repo's folder and let Gradle sync finish (first sync downloads a while; the project uses zero third-party libraries, so it's light).
3. **Build → Build App Bundle(s) / APK(s) → Build APK(s)**.
4. The APK lands at `app/build/outputs/apk/debug/app-debug.apk`.

*No Android Studio? Command line works too: JDK 17 + Android SDK, then `./gradlew assembleDebug` in the repo folder (run `gradle wrapper` first if your machine has no Gradle).*

## 2. Enable ADB debugging on the TV

1. **Settings → Device Preferences → About** → tap **Build** 7 times to unlock Developer options.
2. **Settings → Device Preferences → Developer options** → enable **USB debugging** (and **Wireless debugging** if present).
3. Note the TV's IP under **Settings → Network & Internet**.

## 3. Install the APK

On your computer (same Wi-Fi as the TV):

```bash
adb connect 192.168.1.20:5555        # your TV's IP
adb install app/build/outputs/apk/debug/app-debug.apk
```

On the first connection the TV shows a debugging prompt — choose **Allow**.

## 4. Set it as the default home screen

- **Older Android TV:** press Home — the TV asks which launcher to use; pick **Android Home Skin** → **Always**.
- **Newer Google TV (usually no picker):** use ADB:

```bash
adb shell cmd package set-home-activity cloud.khuong.androidhomeskin/.MainActivity
```

Or install a **Launcher Manager**-style app from the Play Store to switch between this launcher and the stock one.

To go back to stock: point `set-home-activity` at the vendor's launcher package (Google TV example: `com.google.android.apps.tv.launcherx/.home.HomeActivity` — varies by brand), or simply uninstall this app and the TV falls back on its own:
`adb uninstall cloud.khuong.androidhomeskin`.

## 5. Day-to-day use

- **Left/Right:** move between tiles; **OK:** open the app; a tile's name only appears while it's focused.
- **Home:** returns here (once set as default). **Back:** does nothing special inside the launcher.
- Default front order: YouTube → Apple Music → Netflix → Apple TV (matched by app label), then alphabetical. Newly installed apps show up automatically when you return home.

## 6. Customising (in `MainActivity.kt`)

> **Note:** the LG original gained an on-TV Settings screen plus a phone settings page (26 Oct update). This Android TV port does not have it yet — customisation still happens in code, below.

- **App order:** edit the `orderLabels` list (case-insensitive label substrings).
- **Weather city:** edit `weatherCity`, `latitude`, `longitude` (default Ho Chi Minh City, free Open-Meteo data).
- **Wallpaper:** replace `app/src/main/res/drawable-nodpi/wallpaper.jpg` with any 16:9 image.
- **App icons:** this version uses each app's system icon; per-app custom icons need a code change (no override folder like the LG version yet).

After editing: rebuild + reinstall as in sections 1 and 3 (installing over the top keeps data).

## 7. Troubleshooting

- **First Gradle sync/build fails:** usually a missing JDK/Android SDK — let Android Studio download them when opening the project; check your connection.
- **`adb connect` fails:** different Wi-Fi network, debugging not enabled, or the TV's IP changed (give the TV a static IP). Android 11+ TVs may need Wireless debugging with a 6-digit pairing code (`adb pair`).
- **Home still opens the stock launcher:** the default wasn't set — redo section 4; on Google TV the ADB command is usually required.
- **An app is missing from the row:** that app doesn't declare a TV launch intent (LEANBACK/LAUNCHER), so no launcher can see it — a limitation of that app itself.
- **No weather line:** the TV has no internet route, or Open-Meteo hiccuped; the app retries every 30 minutes.

## 8. Credits

The Android edition of the home-skin family (siblings: LG webOS and Samsung Tizen ports), design originating from webos-vibehack (OmkarK97) / cleanyourtv (Gathin). On Android, Gathin's CleanYourTV project already offers a full guide if you also want system debloating. The wallpaper is Dragon Ball fan art found on Pinterest — personal use only.
