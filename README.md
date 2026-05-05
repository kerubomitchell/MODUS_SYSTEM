# Modus App 💰

A personal finance Android app built with Kotlin + Jetpack Compose that helps you manage money through two powerful paths.

## 🛡️ Iron Shield Path
Track your **essential survival expenses** — rent, food, bills, transport. These are non-negotiable expenses you must protect.

## ✨ Golden Path
Track your **growth investments** — stocks, savings, education, side hustles. These build your wealth over time.

## Features
- ✅ Add transactions to either path
- ✅ Delete transactions with confirmation
- ✅ Dashboard with spending breakdown
- ✅ Split bar showing Iron Shield vs Golden Path %
- ✅ Multi-currency support (KES, USD, EUR, GBP, UGX, TZS, NGN, ZAR)
- ✅ Notes on each transaction
- ✅ System default theme (light/dark)
- ✅ Settings screen with currency selector
- ✅ Room database for offline storage
- ✅ Recent transactions on home screen

## Tech Stack
- **Language:** Kotlin
- **UI:** Jetpack Compose + Material3
- **Database:** Room
- **Navigation:** Navigation Compose
- **Architecture:** MVVM (ViewModel + StateFlow)
- **Storage:** DataStore Preferences (currency setting)

## Setup Instructions

### Prerequisites
- Android Studio Hedgehog or newer
- JDK 17
- Android SDK 35

### Clone and Run
```bash
git clone https://github.com/YOUR_USERNAME/ModusApp.git
cd ModusApp
./gradlew assembleDebug
```

### Java Setup (Linux)
```bash
sudo apt install openjdk-17-jdk -y
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
```

## Project Structure
```
com.example.modus_system/
├── data/
│   ├── Transaction.kt          # Room Entity
│   ├── TransactionDao.kt       # Database queries
│   ├── TransactionDatabase.kt  # Room Database
│   └── CurrencyPreferences.kt  # DataStore
├── viewmodel/
│   └── TransactionViewModel.kt # MVVM ViewModel
├── ui/
│   ├── Screen.kt               # Navigation routes
│   ├── Navigation.kt           # NavHost setup
│   ├── theme/
│   │   ├── Theme.kt
│   │   └── Typography.kt
│   └── screens/
│       ├── HomeScreen.kt       # Dashboard
│       ├── IronShieldScreen.kt # Essential expenses
│       ├── GoldenPathScreen.kt # Growth investments
│       └── SettingsScreen.kt   # Currency & about
└── MainActivity.kt
```
