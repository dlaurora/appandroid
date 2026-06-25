# Android Studio Preview And Live Edit

## Open The Project

1. Open Android Studio.
2. Choose **File > Open**.
3. Select `E:\AppAndroid`.
4. Wait for Gradle Sync to finish.

TechQuote uses the Gradle wrapper in this repository and JDK 17 configured for the project. Do not point the Android project JDK to Java 24.

## Resolve Gradle Sync

If sync fails:

1. Confirm `local.properties` exists locally and points to the Android SDK.
2. Confirm the SDK is installed outside the repository, normally under the user Android SDK directory.
3. Confirm Android Studio uses JDK 17 for Gradle.
4. Run this from PowerShell to compare with Android Studio:

```powershell
.\gradlew.bat :app:assembleDebug
```

Do not commit `local.properties`, Android SDKs, Gradle caches, downloaded tools, or signing files.

## Open Compose Preview

1. Open a screen or component file under `app/src/main/java/com/techquote/app/ui`.
2. Find a function annotated with `@TechQuotePhonePreviews` or `@TechQuoteComponentPreviews`.
3. Open the **Preview** pane in Android Studio.
4. Click **Build & Refresh** if the preview is stale.

TechQuote previews are pure Compose mocks. They do not depend on Room, Hilt ViewModels, files, permissions, network, PDFs, or external APIs.

## View Light And Dark Previews

The grouped preview annotations render both light and dark variants:

- `@TechQuotePhonePreviews` for full phone screens.
- `@TechQuoteComponentPreviews` for focused component previews.

The runtime app follows the system theme. Settings only show visual theme options and do not persist a selection yet.

## Create An Android Emulator

1. Open **Tools > Device Manager**.
2. Select **Create virtual device**.
3. Pick a phone profile such as Pixel.
4. Choose a current Android system image already installed or download it through Android Studio.
5. Finish creation and start the emulator from Device Manager.

Android's emulator documentation recommends creating an Android Virtual Device, running the app on it, and using it to navigate the app virtually.

## Run The App

1. Start an emulator or connect a physical device.
2. Select the `app` run configuration.
3. Click **Run**.
4. Navigate from Dashboard to Clientes, Catálogo, Presupuestos, Informes, Configuración, and Legal y privacidad.

Phase 2 client actions and Phase 3 catalog actions persist locally through Room. Quote, report, and settings business actions remain placeholders.

## Enable Live Edit

1. Use Android Studio Giraffe or newer.
2. Make sure the emulator or physical device API level is 30 or newer.
3. Open **File > Settings > Editor > Live Edit** on Windows.
4. Enable Live Edit and choose the desired mode.
5. Run the app on a device or emulator.
6. Edit Compose UI code and watch for the Live Edit status indicator.

Live Edit is useful for quick UI iteration. It is not a replacement for Gradle builds, tests, lint, or emulator navigation checks.

## Live Edit Limitations

Live Edit can fail to update when code has compilation errors. If Android Studio shows the Live Edit state as out of date, inspect the error and run:

```powershell
.\gradlew.bat :app:assembleDebug
```

Compose previews also have limitations: they are lightweight and should avoid network, file access, and ViewModel construction. TechQuote previews follow that rule by using pure mock data.

## Use Logcat

1. Open **View > Tool Windows > Logcat**.
2. Select the running device and TechQuote process.
3. Use filters to inspect crashes or framework messages.

TechQuote does not add production logging for client or catalog data. Do not log personal data, catalog names, SKU, prices, quote content, report content, local URIs, or future PDF/photo contents.

## Run On A Physical Device

1. Enable Developer options on the phone.
2. Enable USB debugging.
3. Connect the device through USB.
4. Accept the debugging prompt on the device.
5. Select the device in Android Studio and click **Run**.

Always test on a real device before commercial release. Phase 3 should verify client and catalog persistence across app restarts on at least one emulator or physical device.

## Official References

- Compose Preview: https://developer.android.com/develop/ui/compose/tooling/previews
- Compose Live Edit: https://developer.android.com/develop/ui/compose/tooling/iterative-development
- Android Emulator: https://developer.android.com/studio/run/emulator
- Manage AVDs: https://developer.android.com/studio/run/managing-avds
- Logcat: https://developer.android.com/studio/debug/logcat
- Physical device testing: https://developer.android.com/studio/run/device
