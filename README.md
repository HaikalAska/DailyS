# DailyS 🖋️

A minimalist, privacy-focused daily activity logger and timeline tracker built with modern Android development practices (Kotlin + Jetpack Compose + Room).

Unlike traditional to-do list managers with checklists and deadlines, **DailyS** focuses on logging what you actually did throughout your day in a clean chronological timeline, giving you clear insights into where your time went.

---

## ✨ Features

- **Interactive Day Pager (120 FPS):** Swipe left or right with 1:1 real-time finger tracking to inspect past days and upcoming plans.
- **Chronological Activity Timeline:** Borderless visual timeline connecting activities from morning to night.
- **Dynamic Daily Insights:** Battery-style category breakdown showing total tracked time and dominant focus areas (Work, Study, Health, etc.).
- **Expandable Obsidian Calendar:** Toggle between a quick 1-week horizontal strip and a full monthly overview with active task dots.
- **Dynamic Two-Tone Theming:** Choose from built-in presets (*Obsidian, Pure Dark, Sakura Pink, Pastel Rose, Midnight Navy*) or pick your own header and body colors with automated contrast and status bar luminance adaptation.
- **Offline & Private:** All data is stored locally in an on-device SQLite database via Room. No accounts, no telemetry, no background tracking.
- **Optimized APK Size:** Pre-configured ProGuard/R8 rules strip unused resources, keeping the compiled release APK lightweight (~5-8 MB).

---

## 🛠️ Tech Stack & Architecture

- **Language:** [Kotlin](https://kotlinlang.org/) (100%)
- **UI Toolkit:** [Jetpack Compose](https://developer.android.com/jetpack/compose) with Material 3
- **Architecture:** MVVM (Model-View-ViewModel) + Clean Unidirectional Data Flow (UDF)
- **Local Persistence:** [Room Database](https://developer.android.com/training/data-storage/room) (SQLite ORM) with reactive Kotlin `Flow` queries
- **Preferences:** [Jetpack DataStore](https://developer.android.com/topic/libraries/architecture/datastore) (Preferences DataStore)
- **Concurrency:** Kotlin Coroutines & `StateFlow`
- **Build System:** Gradle Kotlin DSL (`build.gradle.kts`) with R8 / ProGuard optimization

---

## 🏗️ Getting Started

### Prerequisites
- Android Studio Hedgehog (2023.1.1) or newer
- JDK 17
- Android SDK (API 26+ / Android 8.0 Oreo minimum, Target API 34)

### Building from Source

1. Clone the repository:
   ```bash
   git clone https://github.com/HaikalAska/DailyS.git
   cd DailyS
   ```

2. Open the project in Android Studio or build via terminal:

   **Debug APK:**
   ```bash
   ./gradlew assembleDebug
   ```
   *The APK will be generated at `app/build/outputs/apk/debug/app-debug.apk`.*

   **Release APK (Minified & Optimized):**
   ```bash
   ./gradlew assembleRelease
   ```
   *The optimized APK will be generated at `app/build/outputs/apk/release/app-release.apk`.*

---

## 📁 Project Structure

```text
com.example.dailytask/
├── data/
│   ├── local/          # Room DB, DAOs, and SQLite Helpers
│   ├── model/          # TaskEntity, Category definitions
│   ├── preferences/    # DataStore User Preferences (Themes, Profile)
│   └── repository/     # Data repository layer
├── ui/
│   ├── components/     # Compose UI blocks (Calendar Header, Cards, Settings)
│   ├── screens/        # Main & Today screen layouts
│   ├── theme/          # Material 3 Color palette, Typography, Theme engine
│   └── viewmodel/      # DailyTaskViewModel (StateFlow & business logic)
├── util/               # DateUtils, formatters, and calculation helpers
└── worker/             # Background reminder scheduling
```

---

## 📄 License

Distributed under the MIT License. See `LICENSE` for more information.
