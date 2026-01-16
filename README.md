# SketchUp - Kotlin Multiplatform Drawing App

A cross-platform drawing application built with Kotlin Multiplatform (KMP) and Compose Multiplatform, demonstrating the real strengths of KMP architecture.

## 🎯 Supported Platforms

- **Android** - Native Android app
- **iOS** - Native iOS app via Compose Multiplatform
- **Web** - WebAssembly (WASM) browser app

## 🏗️ Architecture

This project demonstrates best practices for KMP development:

### Project Structure

```
composeApp/src/
├── commonMain/           # Shared code across all platforms
│   └── kotlin/com/example/sketchup/
│       ├── App.kt                    # Main Compose app entry
│       ├── core/
│       │   ├── di/                   # Dependency injection (Koin)
│       │   │   └── KoinModule.kt     # Common DI module + expect declaration
│       │   ├── theme/                # Material 3 theming
│       │   └── utils/
│       │       └── ImageUtils.kt     # expect fun for image processing
│       ├── data/
│       │   ├── model/                # Domain models
│       │   └── repository/           # Data layer abstractions
│       ├── platform/
│       │   └── ImageSaver.kt         # Platform interface for saving images
│       └── view/
│           ├── common/component/     # Reusable UI components
│           └── features/drawing/     # Drawing feature (MVI pattern)
│               ├── component/        # Feature-specific components
│               ├── event/            # UI events
│               ├── helper/           # Extension functions
│               ├── screen/           # Compose screens
│               ├── screenModel/      # ViewModels (Voyager)
│               └── state/            # UI state
│
├── androidMain/          # Android-specific implementations
│   └── kotlin/com/example/sketchup/
│       ├── core/
│       │   ├── di/KoinModule.android.kt     # actual Koin module
│       │   └── utils/ImageUtils.android.kt  # actual ImageBitmap->PNG
│       ├── platform/
│       │   └── AndroidImageSaver.kt         # MediaStore API
│       ├── AndroidApp.kt
│       └── MainActivity.kt
│
├── iosMain/              # iOS-specific implementations
│   └── kotlin/com/example/sketchup/
│       ├── core/
│       │   ├── di/KoinModule.ios.kt        # actual Koin module
│       │   └── utils/ImageUtils.ios.kt     # actual using Skia
│       ├── platform/
│       │   └── IosImageSaver.kt            # PHPhotoLibrary API
│       └── MainViewController.kt
│
└── wasmJsMain/           # WebAssembly-specific implementations
    └── kotlin/com/example/sketchup/
        ├── core/
        │   ├── di/KoinModule.wasmJs.kt     # actual Koin module
        │   └── utils/ImageUtils.wasmJs.kt  # actual using Skia
        └── platform/
            └── WebImageSaver.kt            # Browser download API
```

## 🔑 Key KMP Patterns Demonstrated

### 1. Expect/Actual Declarations
Platform-specific implementations with compile-time verification:

```kotlin
// commonMain: ImageUtils.kt
expect fun ImageBitmap.toPngByteArray(): ByteArray

// androidMain: ImageUtils.android.kt
actual fun ImageBitmap.toPngByteArray(): ByteArray {
    val androidBitmap = this.asAndroidBitmap()
    // Android-specific bitmap processing
}

// iosMain: ImageUtils.ios.kt
actual fun ImageBitmap.toPngByteArray(): ByteArray {
    val skiaBitmap = this.asSkiaBitmap()
    // Skia-based processing for iOS
}
```

### 2. Interface-Based Platform Abstraction
Clean separation with dependency injection:

```kotlin
// commonMain: ImageSaver.kt
interface ImageSaver {
    suspend fun saveImage(bytes: ByteArray, fileName: String): Boolean
}

// Platform implementations injected via Koin
// Android: MediaStore API
// iOS: PHPhotoLibrary
// Web: Browser download
```

### 3. Shared UI with Compose Multiplatform
100% shared UI code in `commonMain`:
- Material 3 theming
- Custom components (ColorPicker, BrushSizePicker)
- Navigation with Voyager

### 4. MVI Architecture Pattern
Clean unidirectional data flow:
- `DrawingState` - immutable UI state
- `DrawingEvent` - sealed interface for user actions
- `DrawingScreenModel` - processes events, updates state

### 5. Dependency Injection with Koin
Platform-aware DI setup:

```kotlin
// commonMain: shared modules + expect declaration
expect val imageSaverModule: Module

// Each platform provides actual implementation
actual val imageSaverModule: Module = module {
    single<ImageSaver> { PlatformImageSaver() }
}
```

## 🚀 Getting Started

### Prerequisites
- JDK 11+
- Android Studio with KMP plugin
- Xcode (for iOS builds)

### Build & Run

**Android:**
```shell
./gradlew :composeApp:assembleDebug
```

**iOS:**
Open `iosApp/iosApp.xcodeproj` in Xcode and run.

**Web (WASM):**
```shell
./gradlew :composeApp:wasmJsBrowserDevelopmentRun
```

## 📦 Dependencies

- **Compose Multiplatform** - Shared UI framework
- **Voyager** - Navigation & ScreenModel
- **Koin** - Dependency injection
- **Kotlinx Coroutines** - Async operations
- **Skia** - Image processing (iOS, Web)

## ✨ Features

- 🎨 Freehand drawing with smooth paths
- 🖌️ Customizable brush size and color
- 🌈 Full color picker with HSV selection
- ↩️ Undo/Redo support
- 🧽 Eraser mode
- 💾 Save drawings as PNG to device

## 📄 License

This project is open source and available under the MIT License.
