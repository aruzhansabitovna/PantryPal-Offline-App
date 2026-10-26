# PantryPal — Offline Android App
Native Kotlin Android UI. Contains the same 50 recipe names, ingredients, categories, and preparation times from the original PantryPal Task 3 `index.html`.

Search for recipes by entering comma-separated ingredients. Tap a recipe for details. The app does not need internet access.

**Note:** The original dataset does not include step-by-step cooking instructions, so they are not claimed or invented here. Recipe photos from the website are also not bundled offline.

## Build without Android Studio
Upload the **contents** of this folder to a new GitHub repository preserving all paths (including `.github/workflows/build.yml`). Under Actions, run **Build PantryPal Offline APK**. Download artifact **PantryPal-Offline-APK** and unzip it to get `app-debug.apk`. Android only; cannot install on iPhone.
