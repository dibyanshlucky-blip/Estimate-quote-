# Divyansh Paint – Professional Painting Estimation & Measurement App

A modern, native Android application built with **Kotlin** and **Jetpack Compose (Material 3)** for **Divyansh Enterprises (Kharadi, Pune)**. It enables contractors and painting professionals to perform room-by-room area measurements, surface calculations, deductions, work item estimations with detailed brand & product catalogues, and generate professional PDF quotations with terms & payment schedules.

---

## 📱 App Highlights & Architecture

- **Application Type**: **Native Android App**
- **Language**: Kotlin 2.2+
- **UI Toolkit**: Jetpack Compose & Material 3 (Edge-to-Edge)
- **Local Persistence**: Room Database (with SQLite offline storage)
- **PDF Engine**: Native Android `PdfDocument` with formatted tables, A4 layout, Indian Rupee formatting (`₹`), and amount in words
- **Package / Application ID**: `com.aistudio.divyanshpaint.kmpqrx`

---

## 🚀 Automated Android APK Generation via GitHub Actions

This repository is pre-configured with a continuous integration workflow (`.github/workflows/build-apk.yml`) that automatically compiles and packages a **Debug APK** every time changes are pushed to `main` (or run manually via Workflow Dispatch).

### How to Get Your APK from GitHub Actions:

1. **Push your code to GitHub**:
   ```bash
   git add .
   git commit -m "Initialize Divyansh Paint Android app"
   git branch -M main
   git remote add origin https://github.com/<YOUR_USERNAME>/<YOUR_REPOSITORY_NAME>.git
   git push -u origin main
   ```
2. **Open the Actions Tab in GitHub**:
   - In your GitHub repository, click on the **Actions** tab at the top.
   - You will see the **Build Android Debug APK** workflow running.
3. **Download the APK Artifact**:
   - Once the workflow finishes (green checkmark ✅), click on the completed workflow run.
   - Scroll down to the **Artifacts** section at the bottom.
   - Click on **`divyansh-paint-debug-apk`** to download the ZIP file.
   - Extract the ZIP archive on your computer or phone to obtain `app-debug.apk`.

### How to Install on an Android Phone:

1. Transfer `app-debug.apk` to your Android device (via WhatsApp, Google Drive, USB, or direct download).
2. Tap the APK file to install.
3. If prompted with *"Install unknown apps"*, enable **Allow from this source**.
4. Tap **Install** and open **Divyansh Paint**!

---

## 🛠 Local Build Instructions

To build the APK locally using Gradle:

```bash
# Build Debug APK
./gradlew :app:assembleDebug

# Generated APK location:
# app/build/outputs/apk/debug/app-debug.apk
```
