# SketchUp - Kotlin Multiplatform Drawing App

A cross-platform drawing application built with Kotlin Multiplatform, Compose Multiplatform, and Clean Architecture.

## 🏗️ Architecture

This project follows **Clean Architecture** principles with clear separation of concerns:

```
composeApp/src/commonMain/kotlin/com/example/sketchup/
├── domain/                    # Business Logic Layer (Platform Independent)
│   ├── model/                # Domain entities
│   │   ├── DrawingPath.kt
│   │   └── BrushSettings.kt
│   ├── repository/           # Repository interfaces
│   │   ├── DrawingRepository.kt
│   │   └── ImageStorageRepository.kt
│   └── usecase/              # Business use cases
│       ├── AddDrawingPathUseCase.kt
│       ├── UndoDrawingUseCase.kt
│       ├── RedoDrawingUseCase.kt
│       ├── ClearDrawingUseCase.kt
│       ├── ObserveDrawingPathsUseCase.kt
│       └── SaveDrawingImageUseCase.kt
│
├── data/                      # Data Layer
│   ├── repository/           # Repository implementations
│   │   ├── DrawingRepositoryImpl.kt
│   │   └── ImageStorageRepositoryImpl.kt
│   └── source/               # Data sources
│       └── local/
│           └── ImageStorageDataSource.kt
│
├── presentation/             # Presentation Layer (UI)
│   ├── drawing/
│   │   ├── DrawingScreenModel.kt
│   │   ├── model/
│   │   │   ├── DrawingEvent.kt
│   │   │   ├── DrawingState.kt
│   │   │   └── DrawingEffect.kt
│   │   ├── screen/
│   │   │   └── DrawingScreen.kt
│   │   ├── component/
│   │   │   ├── ColorPicker.kt
│   │   │   └── BrushSizeSlider.kt
│   │   └── helper/
│   │       └── DrawingExtensions.kt
│   └── common/
│       └── component/
│           └── CustomIconButton.kt
│
├── di/                        # Dependency Injection
│   └── AppModule.kt
│
├── platform/                  # Platform-specific interfaces
│   └── [Platform implementations in platform-specific folders]
│
└── core/                      # Shared core utilities
    ├── theme/
    └── utils/
```

## 📱 Platform-Specific Implementations

Each platform provides its own implementation of platform-specific features:

### Android (`androidMain/`)
- `AndroidImageStorageDataSource.kt` - Uses MediaStore API
- `PlatformModule.kt` - Android DI module

### iOS (`iosMain/`)
- `IosImageStorageDataSource.kt` - Uses PHPhotoLibrary
- `PlatformModule.kt` - iOS DI module

### Web/WASM (`wasmJsMain/`)
- `WebImageStorageDataSource.kt` - Browser download API
- `PlatformModule.kt` - Web DI module

## 🎯 Layer Responsibilities

### Domain Layer
- **Pure business logic** - no framework dependencies
- **Entities**: Core business models (DrawingPath, BrushSettings)
- **Use Cases**: Single-responsibility business operations
- **Repository Interfaces**: Contracts for data access

### Data Layer
- **Repository Implementations**: Concrete data access logic
- **Data Sources**: Platform-specific data handling
- Manages data flow between domain and external sources

### Presentation Layer
- **ScreenModels**: UI state management (similar to ViewModels)
- **UI Components**: Compose UI screens and reusable components
- **Models**: UI-specific models (State, Events, Effects)
- Handles user interaction and displays data

### DI Layer
- **Dependency Injection**: Koin modules for each layer
- **Platform Modules**: Platform-specific implementations

## 🚀 Key Features

- ✏️ **Multi-touch drawing** with custom brush sizes and colors
- 🎨 **Advanced color picker** with HSV gradient selection
- ↩️ **Undo/Redo** functionality with proper state management
- 🧹 **Eraser mode** with visual indicator
- 💾 **Save drawings** as PNG images (platform-specific implementation)
- 🌈 **Custom brush colors** and sizes
- 📱 **Cross-platform** - Android, iOS, and Web

## 🛠️ Tech Stack

- **Kotlin Multiplatform** - Share code across platforms
- **Compose Multiplatform** - Declarative UI framework
- **Voyager** - Navigation and ScreenModel management
- **Koin** - Dependency injection
- **Coroutines & Flow** - Asynchronous programming
- **kotlinx-datetime** - Date/time utilities

## 🏃 Getting Started

### Prerequisites
- JDK 17 or higher
- Android Studio (for Android development)
- Xcode (for iOS development)
- Kotlin 2.0+

### Building

#### Android
```bash
./gradlew :composeApp:assembleDebug
```

#### iOS
Open `iosApp/iosApp.xcodeproj` in Xcode and build

#### Web/WASM
```bash
./gradlew :composeApp:wasmJsBrowserDevelopmentRun
```

## 🧪 Testing

The clean architecture makes testing easier:
- **Domain Layer**: Pure business logic, easy to unit test
- **Data Layer**: Mock repositories for testing
- **Presentation Layer**: Test ScreenModels independently

## 📝 Benefits of This Architecture

1. **Separation of Concerns**: Each layer has a clear responsibility
2. **Testability**: Easy to write unit tests for each layer
3. **Maintainability**: Changes in one layer don't affect others
4. **Scalability**: Easy to add new features without breaking existing code
5. **Platform Independence**: Business logic is shared, platform details are isolated
6. **Reusability**: Use cases and domain models can be reused across features

## 🔄 Data Flow

```
UI (DrawingScreen) 
    ↓ (User Action)
ScreenModel (DrawingScreenModel)
    ↓ (Calls Use Case)
Use Case (AddDrawingPathUseCase, etc.)
    ↓ (Uses Repository Interface)
Repository Implementation (DrawingRepositoryImpl)
    ↓ (Accesses Data Source if needed)
Data Source (Platform-specific)
```

## 🤝 Contributing

When adding new features:
1. Start with the **Domain Layer** (entities, use cases, repository interfaces)
2. Implement **Data Layer** (repository implementations, data sources)
3. Update **DI** to wire dependencies
4. Build **Presentation Layer** (ScreenModels, UI components)
5. Add platform-specific implementations if needed

## 📄 License

[Your License Here]

## 👥 Authors

[Your Name/Team]

---

Built with ❤️ using Kotlin Multiplatform and Clean Architecture
