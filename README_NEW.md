# SketchUp - Kotlin Multiplatform Drawing App

## 🎨 Giới thiệu

SketchUp là ứng dụng vẽ đa nền tảng được xây dựng bằng **Kotlin Multiplatform (KMP)** và **Compose Multiplatform**, chạy trên:
- 📱 Android
- 🍎 iOS
- 🌐 Web (WASM)
- 💻 Desktop (JVM) - coming soon

## ✨ Điểm nổi bật

### 1. Kiến trúc Clean Architecture
Project được tổ chức theo **Clean Architecture** với 3 layer rõ ràng:

```
┌─────────────────────────────────────┐
│   Presentation Layer (UI)           │
│   - ViewModels                      │
│   - UI States & Events              │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│   Domain Layer (Business Logic)     │
│   - Use Cases                       │
│   - Domain Models                   │
│   - Repository Interfaces           │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│   Data Layer (Implementation)       │
│   - Repository Implementations      │
│   - Platform-specific Data Sources  │
│   - Native APIs Integration         │
└─────────────────────────────────────┘
```

### 2. Tận dụng Platform-Specific APIs

#### Android
- ✅ MediaStore API cho image export
- ✅ SharedPreferences cho settings
- 🔜 Room database cho persistence
- 🔜 Android Share API

#### iOS
- ✅ Photos Framework cho image export
- ✅ UserDefaults cho settings
- 🔜 CoreData cho persistence
- 🔜 UIActivityViewController cho share

#### Web
- ✅ Blob/Download API cho image export
- ✅ localStorage cho settings
- 🔜 IndexedDB cho persistence
- 🔜 Web Share API

### 3. Shared Business Logic (100% Code Reuse)

Tất cả business logic được viết một lần trong `commonMain`:

```kotlin
// Domain Use Case - Shared across all platforms
class AddStrokeUseCase(
    private val repository: DrawingRepository
) {
    suspend operator fun invoke(stroke: DrawingStroke) {
        // Business rule: stroke must have at least 2 points
        if (stroke.points.size < 2) return
        repository.addStroke(stroke)
    }
}
```

### 4. Expect/Actual Pattern

Platform-specific implementation sử dụng `expect/actual`:

```kotlin
// commonMain - Interface
expect class PlatformImageExportRepository() : ImageExportRepository

// androidMain - Android implementation
actual class PlatformImageExportRepository(
    private val context: Context
) : ImageExportRepository {
    override suspend fun exportImage(...) {
        // Use Android MediaStore
    }
}

// iosMain - iOS implementation  
actual class PlatformImageExportRepository : ImageExportRepository {
    override suspend fun exportImage(...) {
        // Use iOS Photos Framework
    }
}
```

## 🏗️ Cấu trúc Project

```
composeApp/src/
├── commonMain/kotlin/
│   ├── domain/              # Business logic (100% shared)
│   │   ├── model/          # Domain entities
│   │   ├── repository/     # Repository interfaces
│   │   ├── usecase/        # Business use cases
│   │   ├── platform/       # Platform capabilities
│   │   └── storage/        # Storage abstractions
│   ├── data/               # Data layer (mostly shared)
│   │   ├── repository/     # Repository implementations
│   │   └── source/         # Data source interfaces
│   ├── presentation/       # Presentation logic (shared)
│   │   └── drawing/        # Drawing feature
│   └── di/                 # Dependency injection
│
├── androidMain/kotlin/     # Android-specific
│   ├── data/
│   │   ├── repository/     # Android MediaStore
│   │   └── source/         # SharedPreferences/Room
│   ├── domain/
│   │   ├── platform/       # Android capabilities
│   │   └── storage/        # Android storage
│   └── di/                 # Android DI module
│
├── iosMain/kotlin/         # iOS-specific
│   ├── data/
│   │   ├── repository/     # Photos Framework
│   │   └── source/         # UserDefaults/CoreData
│   ├── domain/
│   │   ├── platform/       # iOS capabilities
│   │   └── storage/        # iOS storage
│   └── di/                 # iOS DI module
│
└── wasmJsMain/kotlin/      # Web-specific
    ├── data/
    │   ├── repository/     # Blob/Download API
    │   └── source/         # localStorage/IndexedDB
    ├── domain/
    │   ├── platform/       # Web capabilities
    │   └── storage/        # Web storage
    └── di/                 # Web DI module
```

## 🚀 Features

### Hiện tại
- ✅ Vẽ tự do với nhiều màu sắc
- ✅ Điều chỉnh kích thước cọ
- ✅ Undo/Redo
- ✅ Export PNG (platform-specific)
- ✅ Chế độ tẩy
- ✅ Clean Architecture
- ✅ Platform-specific optimizations

### Sắp tới
- 🔜 Lưu/Load bức vẽ
- 🔜 Persistence với database
- 🔜 Share drawing
- 🔜 Brush types (marker, highlighter)
- 🔜 Pressure sensitivity
- 🔜 Cloud sync
- 🔜 Collaborative drawing

## 📦 Dependencies

### Common
- Kotlin Multiplatform
- Compose Multiplatform
- Kotlinx Coroutines
- Koin (Dependency Injection)
- Voyager (Navigation)

### Platform-specific
- **Android**: Material3, MediaStore API
- **iOS**: UIKit, Photos Framework
- **Web**: Kotlin/JS, Browser APIs

## 🛠️ Build & Run

### Android
```bash
./gradlew :composeApp:assembleDebug
```

### iOS
```bash
cd iosApp
xcodebuild -project iosApp.xcodeproj
```

### Web
```bash
./gradlew wasmJsBrowserDevelopmentRun
```

## 📖 Tài liệu

- [KMP Architecture Guide](./KMP_ARCHITECTURE.md) - Chi tiết về kiến trúc
- [PNG Export Feature](./PNG_EXPORT_FEATURE.md) - Export feature implementation

## 🎯 Học được gì từ project này?

### 1. Kotlin Multiplatform
- Cách tổ chức code để share tối đa
- Expect/Actual pattern
- Platform-specific implementations

### 2. Clean Architecture
- Separation of concerns
- Dependency inversion
- Use case pattern

### 3. Platform APIs
- Android MediaStore
- iOS Photos Framework  
- Web Blob/Download API
- localStorage, UserDefaults, SharedPreferences

### 4. Compose Multiplatform
- Shared UI code
- Platform-specific UI when needed
- State management

## 🤝 Contributing

Contributions are welcome! Đặc biệt:
- Implement persistence layer (Room, CoreData, IndexedDB)
- Add more brush types
- Implement sharing functionality
- Add tests

## 📄 License

MIT License

## 👨‍💻 Author

Built with ❤️ using Kotlin Multiplatform

