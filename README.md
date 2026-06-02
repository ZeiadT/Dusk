# 🌙 Dusk - Weather Application

A feature-rich Android weather application built with Kotlin and Jetpack Compose, providing real-time weather information, location management, weather alerts, and multi-language support.

## 📋 Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Tech Stack](#tech-stack)
- [Installation Guide](#installation-guide)
- [Configuration](#configuration)
- [Usage Instructions](#usage-instructions)
- [Project Structure](#project-structure)
- [Architecture](#architecture)
- [Contributing](#contributing)

## 🎯 Overview

Dusk is a modern weather application that delivers accurate weather forecasts with an intuitive user interface. The app leverages Mapbox for location selection, provides personalized weather alerts, and supports multiple languages (English and Arabic) with customizable temperature and wind speed units.

**Target:** Android 8.0+ (API Level 26)  
**Compiled:** Android 15 (API Level 36)

## ✨ Features

### Core Features
- 🌍 **Real-time Weather Data**: Current weather conditions with comprehensive details
- 📍 **Location Management**: Save multiple locations with map-based selection
- 🗺️ **Interactive Maps**: Mapbox integration for location search and selection
- 📊 **Forecasts**: Hourly and daily weather forecasts
- 🔔 **Weather Alerts**: Create custom weather alerts with notifications and alarms
- 🌐 **Multi-language Support**: English and Arabic localization
- ⚙️ **User Preferences**: Customize temperature units (Celsius/Fahrenheit) and wind speed units (metric/imperial)
- 🎨 **Modern UI**: Built with Jetpack Compose using Material Design 3
- 🔄 **Pull-to-Refresh**: Easy data synchronization
- ⏰ **Background Services**: WorkManager and foreground services for alerts and notifications

### Advanced Features
- 📍 **GPS Location Tracking**: Automatic current location detection
- 🔐 **Secure Data Storage**: Room database for persistent data
- 🎯 **Alarm Management**: AlarmManager for precise alert scheduling
- 🎵 **Audio Alerts**: Sound notifications for weather alarms
- 💾 **User Settings**: DataStore for preference persistence
- 🧪 **Unit Tests**: MockK and Coroutines testing support

## 🛠️ Tech Stack

### Architecture & Design Patterns
- **MVVM** (Model-View-ViewModel)
- **Repository Pattern** for data abstraction
- **Dependency Injection** using Hilt
- **Clean Architecture** principles

### Android Framework
- **Jetpack Compose**: Modern declarative UI framework
- **Jetpack Navigation**: Fragment-based and Compose navigation
- **Room Database**: Local data persistence (v4)
- **DataStore**: Preferences storage for user settings
- **WorkManager**: Background task scheduling
- **AlarmManager**: Precise alert scheduling
- **Services**: Foreground services for high-priority alerts

### Libraries & Dependencies
- **Networking**
  - Retrofit: HTTP client
  - OkHttp: HTTP interceptor and logging
  - Kotlinx Serialization: JSON parsing
  
- **Location & Maps**
  - Play Services Location: GPS and Fused Location Provider
  - Mapbox Maps: Interactive map rendering
  - Mapbox Search: Location geocoding and search
  - Mapbox Search UI: Pre-built search components

- **UI & Imaging**
  - Material 3: Design system components
  - Coil: Image loading and caching
  - Compose Material Icons Extended: Icon library
  - Google Fonts for Compose: Font library support

- **Utilities**
  - Clarity: Encryption and security
  - Dotenv: Environment variable loading
  - Hilt Navigation Compose: ViewModel injection in Compose

- **Testing**
  - JUnit: Unit testing framework
  - MockK: Mocking library
  - Kotlinx Coroutines Test: Coroutine testing support
  - Espresso: UI testing
  - Compose UI Test: Compose-specific testing

## 📦 Installation Guide

### Prerequisites
- Android Studio (2024.1 or later)
- Java Development Kit (JDK 11+)
- Gradle 8.0+
- Android SDK (API Level 26+)
- Mapbox Account with API Token

### Step 1: Clone the Repository

```bash
git clone https://github.com/ZeiadT/Dusk.git
cd Dusk
```

### Step 2: Set Up Mapbox API Token

Create a `.env` file in the project root directory:

```bash
MAPBOX_PUBLIC_TOKEN=your_mapbox_public_token_here
MAPBOX_DOWNLOADS_TOKEN=your_mapbox_downloads_token_here
```

Or add your tokens to the `gradle.properties` file:

```properties
MAPBOX_DOWNLOADS_TOKEN=your_mapbox_downloads_token_here
```

You can obtain these tokens from the [Mapbox Dashboard](https://account.mapbox.com/tokens/).

### Step 3: Build the Project

Using Android Studio:
1. Open the project in Android Studio
2. Let Gradle sync complete
3. Build the project via **Build** → **Make Project**

Or using the command line:

```bash
# On macOS/Linux
./gradlew build

# On Windows
gradlew.bat build
```

### Step 4: Run the Application

Using Android Studio:
1. Connect a physical device or start an emulator
2. Click **Run** → **Run 'app'**

Or using the command line:

```bash
# On macOS/Linux
./gradlew installDebug

# On Windows
gradlew.bat installDebug
```

## ⚙️ Configuration

### Gradle Properties

The project uses the following Gradle properties (modify in `gradle.properties`):

```properties
# JVM Configuration
org.gradle.jvmargs=-Xmx2048m -Dfile.encoding=UTF-8

# AndroidX
android.useAndroidX=true
android.nonTransitiveRClass=true

# Kotlin Code Style
kotlin.code.style=official

# Gradle Configuration Cache
org.gradle.configuration-cache=true
```

### Android Manifest

Key manifest configurations:
- **Package**: `iti.mad.dusk`
- **Permissions**: Location, Internet, Vibrate (for alerts)
- **WorkManager Initialization**: Custom initialization via `InitializationProvider`

### Build Configuration

- **Minimum SDK**: 26 (Android 8.0)
- **Target SDK**: 36 (Android 15)
- **Compile SDK**: 36
- **Java Compatibility**: 11

## 📱 Usage Instructions

### Getting Started

1. **Launch the App**: Open Dusk on your device
2. **Grant Permissions**: Allow location and notification permissions
3. **View Weather**: The home screen displays current weather and forecasts for your location

### Managing Locations

1. **Add Location**:
   - Tap the Floating Action Button (FAB) on the home screen
   - Search for a location on the map or use the search bar
   - Select a location on the map
   - Save the location

2. **View Saved Locations**:
   - Navigate to the **Locations** screen via the bottom navigation bar
   - Tap any location to view its weather

3. **Set Default Location**:
   - Long-press or access the location menu in the Locations screen
   - Select "Set as Default"

4. **Delete Location**:
   - Swipe or access the location menu
   - Select "Delete" (cannot delete current location)

### Weather Alerts

1. **Create Alert**:
   - Navigate to the **Alerts** screen
   - Tap the Floating Action Button (FAB)
   - Select temperature threshold, alert type, and time window
   - Confirm to create the alert

2. **Manage Alerts**:
   - View all active alerts on the Alerts screen
   - Edit or delete existing alerts as needed
   - Alerts trigger with notifications and optional audio alarms

### Customizing Settings

1. **Access Settings**:
   - Navigate to the **Settings** screen via the bottom navigation bar

2. **Temperature Unit**:
   - Toggle between **Celsius** and **Fahrenheit**

3. **Wind Speed Unit**:
   - Toggle between **Metric** (m/s) and **Imperial** (mph)

4. **Language**:
   - Switch between **English** and **العربية** (Arabic)
   - The app restarts to apply language changes

### Refresh Weather Data

- **Pull-to-Refresh**: Swipe down on the Home screen to fetch latest weather
- **Manual Refresh**: Tap the refresh chip displaying "Updated: X minutes ago"

## 🏗️ Project Structure

```
Dusk/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/iti/mad/dusk/
│   │   │   │   ├── data/
│   │   │   │   │   ├── local/
│   │   │   │   │   │   ├── database/
│   │   │   │   │   │   ├── datastore/
│   │   │   │   │   │   └── dao/
│   │   │   │   │   ├── remote/
│   │   │   │   │   ├── repository/
│   │   │   │   │   └── mapper/
│   │   │   │   ├── domain/
│   │   │   │   │   ├── model/
│   │   │   │   │   └── repository/
│   │   │   │   ├── presentation/
│   │   │   │   │   ├── screen/
│   │   │   │   │   ├── component/
│   │   │   │   │   ├── viewmodel/
│   │   │   │   │   └── theme/
│   │   │   │   ├── di/
│   │   │   │   ├── util/
│   │   │   │   └── DuskApplication.kt
│   │   │   ├── res/
│   │   │   │   ├── values/
│   │   │   │   ├── values-ar/
│   │   │   │   └── layout/
│   │   │   └── AndroidManifest.xml
│   │   └── test/
│   ├── build.gradle.kts
│   └── proguard-rules.pro
├── gradle/
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
└── README.md
```

### Key Directories

- **data**: Data layer with Room entities, DAOs, repositories, and API services
- **domain**: Domain models and repository interfaces
- **presentation**: UI layer with Compose screens, ViewModels, and themes
- **di**: Dependency injection modules using Hilt
- **util**: Utility functions and extensions (UnitConverter, LocationManager, etc.)

## 🏛️ Architecture

### Layered Architecture

```
┌─────────────────────────────┐
│   Presentation Layer        │
│  (Screens, ViewModels)      │
├─────────────────────────────┤
│   Domain Layer              │
│  (Models, Interfaces)       │
├─────────────────────────────┤
│   Data Layer                │
│  (Repositories, Sources)    │
├─────────────────────────────┤
│   Local & Remote Sources    │
│  (Room, API, Services)      │
└─────────────────────────────┘
```

### Data Flow

1. **UI Events**: User interactions trigger ViewModel methods
2. **Repository Layer**: ViewModels request data from repositories
3. **Data Sources**: Repositories fetch from local (Room) or remote (API) sources
4. **Observable Streams**: Data flows back to UI via StateFlow/Flow
5. **UI Rendering**: Compose recomposes based on state changes

### Key Components

- **ViewModel**: Manages UI state and business logic (HomeViewModel, AlertViewModel, SettingsViewModel)
- **Repository**: Abstracts data sources (WeatherRepository, LocationRepository, AlertRepository)
- **DataSource**: Direct access to data (LocalDataSource, RemoteDataSource)
- **Mapper**: Transforms DTOs to domain models and vice versa
- **UseCase**: Optional business logic wrapper (can be added for specific operations)

## 🧪 Testing

### Running Tests

```bash
# Unit tests
./gradlew test

# Instrumented tests
./gradlew connectedAndroidTest

# Specific test class
./gradlew test --tests=com.example.TestClass
```

### Test Coverage

- **SettingsViewModel**: Settings state management
- **AlertViewModel**: Alert creation and management
- **AlertLocalDataSource**: Database operations for alerts
- **SettingsRepository**: Settings data access
- **SettingsDataStore**: Preference persistence

## 🚀 Getting Started with Development

### Adding a New Feature

1. **Create Domain Model**: Define the data structure in `domain/model/`
2. **Create Entity & DAO**: Add database layer in `data/local/`
3. **Implement Repository**: Create repository interface in `domain/` and implementation in `data/`
4. **Create ViewModel**: Implement business logic in `presentation/viewmodel/`
5. **Build UI Screen**: Create Compose screen in `presentation/screen/`
6. **Add Navigation**: Register route in navigation graph

### Database Migrations

Database version is managed in `DuskDatabase`. When schema changes:

1. Increment `@Database(version = X)`
2. Add migration if upgrading existing schemas
3. Update corresponding entity models

Current Version: **4** (includes CurrentWeather, Forecast, Locations, and Alerts tables)

## 📝 Notes

- The app uses **Hilt** for dependency injection. Ensure all injectable dependencies are properly scoped.
- **Coroutines** are used extensively for async operations. Always use `viewModelScope` to prevent memory leaks.
- **Compose** recomposition is optimized through stable data classes and proper state management.
- **Room** database uses coordinates rounding for unique location IDs to prevent duplicates.

## 🤝 Contributing

Contributions are welcome! Please follow these guidelines:

1. Create a feature branch from `main`
2. Write clean, well-documented code
3. Add unit tests for new features
4. Submit a pull request with a clear description

## 📄 License

This project is open source. Check the LICENSE file for details.

## 📧 Support

For issues or questions, please open an issue on the GitHub repository.

---

**Developed with ❤️ by [ZEIAD](https://github.com/ZeiadT)**
