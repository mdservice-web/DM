# ગુજરાતી કેલ્ક્યુલેટર

Premium-style Android calculator with Gujarati digit display (૦૧૨૩૪૫૬૭૮૯).

## Features
- Gujarati digits and Gujarati title
- Addition, subtraction, multiplication, division and modulo
- Decimal input, sign toggle, clear and backspace
- Dark premium gradient interface
- GitHub Actions workflow builds a debug APK automatically

## Build APK on GitHub
1. Create a new repository on GitHub.
2. Upload **all files and folders** from this project to the repository root.
3. Make sure the workflow file is at `.github/workflows/build-apk.yml`.
4. Push to the `main` branch (or open **Actions** and run **Build Gujarati Calculator APK** manually).
5. Open the repository's **Actions** tab.
6. Select the latest successful **Build Gujarati Calculator APK** run.
7. Under **Artifacts**, download `Gujarati-Calculator-APK.zip`.
8. Extract it. The APK inside is `app-debug.apk`.

## Build locally
Install Android Studio with Android SDK, then open this folder and build the `app` module.
