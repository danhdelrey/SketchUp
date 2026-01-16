# Migration Guide: Old Structure → Clean Architecture

## ✅ Hoàn thành tái cấu trúc theo Clean Architecture

Dự án đã được tái cấu trúc hoàn toàn từ cấu trúc "Jetpack Compose style" sang **Clean Architecture** chuẩn cho Kotlin Multiplatform.

## 📊 Thay đổi cấu trúc

### CŨ (View-based Structure)
```
src/commonMain/kotlin/com/example/sketchup/
├── view/
│   └── features/drawing/
├── data/
│   ├── model/
│   └── repository/
├── platform/
└── core/
```

### MỚI (Clean Architecture)
```
src/commonMain/kotlin/com/example/sketchup/
├── domain/              # ⭐ Business Logic Layer
│   ├── model/          # Domain entities
│   ├── repository/     # Repository interfaces
│   └── usecase/        # Business use cases
├── data/               # Data Layer
│   ├── repository/     # Repository implementations
│   └── source/         # Data sources
├── presentation/       # ⭐ UI Layer  
│   ├── drawing/
│   │   ├── DrawingScreenModel.kt
│   │   ├── model/
│   │   ├── screen/
│   │   ├── component/
│   │   └── helper/
│   └── common/
├── di/                 # ⭐ Dependency Injection
│   └── AppModule.kt
├── platform/          # Platform interfaces
└── core/              # Shared utilities
```

## 🔄 File Mapping

| Cũ | Mới | Ghi chú |
|---|---|---|
| `data/model/DrawingPath.kt` | `domain/model/DrawingPath.kt` | Di chuyển sang domain |
| `data/repository/DrawingRepository.kt` | `domain/repository/DrawingRepository.kt` | Interface sang domain |
| `data/repository/DrawingRepositoryImpl.kt` | `data/repository/DrawingRepositoryImpl.kt` | Implementation ở data |
| `view/features/drawing/screenModel/` | `presentation/drawing/DrawingScreenModel.kt` | Refactor hoàn toàn |
| `view/features/drawing/screen/` | `presentation/drawing/screen/` | Di chuyển sang presentation |
| `view/features/drawing/component/` | `presentation/drawing/component/` | Di chuyển sang presentation |
| `platform/ImageSaver.kt` | `data/source/local/ImageStorageDataSource.kt` | Rename & restructure |
| `core/di/KoinModule.kt` | `di/AppModule.kt` | Tổ chức lại DI |

## 🆕 File mới được tạo

### Domain Layer
- `domain/model/BrushSettings.kt`
- `domain/repository/ImageStorageRepository.kt`
- `domain/usecase/AddDrawingPathUseCase.kt`
- `domain/usecase/UndoDrawingUseCase.kt`
- `domain/usecase/RedoDrawingUseCase.kt`
- `domain/usecase/ClearDrawingUseCase.kt`
- `domain/usecase/ObserveDrawingPathsUseCase.kt`
- `domain/usecase/SaveDrawingImageUseCase.kt`

### Data Layer
- `data/repository/ImageStorageRepositoryImpl.kt`
- `data/source/local/ImageStorageDataSource.kt`

### Presentation Layer
- `presentation/drawing/model/DrawingEffect.kt` (Side effects)
- `presentation/drawing/model/DrawingEvent.kt` (Updated)
- `presentation/drawing/model/DrawingState.kt` (Updated with canUndo/canRedo)

### Platform Implementations
- `androidMain/di/PlatformModule.kt`
- `androidMain/platform/AndroidImageStorageDataSource.kt`
- `iosMain/di/PlatformModule.kt`
- `iosMain/platform/IosImageStorageDataSource.kt`
- `wasmJsMain/di/PlatformModule.kt`
- `wasmJsMain/platform/WebImageStorageDataSource.kt`

## 🎯 Cải tiến chính

### 1. **Separation of Concerns**
- Domain logic hoàn toàn tách biệt khỏi UI và framework
- Data layer không phụ thuộc vào presentation
- UI chỉ gọi use cases, không trực tiếp gọi repository

### 2. **Use Cases**
Mỗi business operation có use case riêng:
```kotlin
// Thay vì:
repository.addPath(path)

// Bây giờ:
addDrawingPathUseCase(path)
```

### 3. **Side Effects Management**
```kotlin
// Mới: Proper side effects với Channel
sealed interface DrawingEffect {
    data class ShowMessage(val message: String) : DrawingEffect
    data class ShowError(val error: String) : DrawingEffect
}
```

### 4. **Enhanced State**
```kotlin
data class DrawingState(
    // ...existing fields...
    val canUndo: Boolean = false,  // ⭐ New
    val canRedo: Boolean = false   // ⭐ New
)
```

### 5. **Platform Module Pattern**
```kotlin
// commonMain
expect val platformModule: Module

// androidMain
actual val platformModule: Module = module {
    single<ImageStorageDataSource> { AndroidImageStorageDataSource(get()) }
}
```

## 🧪 Testing Benefits

Với cấu trúc mới, testing dễ dàng hơn:

```kotlin
// Test Use Case (pure business logic)
class AddDrawingPathUseCaseTest {
    @Test
    fun `should add path to repository`() {
        val mockRepository = mockk<DrawingRepository>()
        val useCase = AddDrawingPathUseCase(mockRepository)
        
        useCase(testPath)
        
        verify { mockRepository.addPath(testPath) }
    }
}

// Test ScreenModel với mock use cases
class DrawingScreenModelTest {
    @Test
    fun `should call use case when event received`() {
        val mockUseCase = mockk<AddDrawingPathUseCase>()
        val screenModel = DrawingScreenModel(/* inject mocks */)
        
        screenModel.onEvent(DrawingEvent.EndDraw)
        
        verify { mockUseCase(any()) }
    }
}
```

## 📦 Dependency Flow

```
Presentation Layer (UI)
       ↓ depends on
  Domain Layer (Business Logic)
       ↑ implemented by
   Data Layer (Data Access)
       ↓ depends on
Platform Layer (Platform-specific)
```

## 🚀 Next Steps

1. **Add More Features**: Thêm features mới theo cùng pattern
   - Tạo domain models & use cases trước
   - Implement data layer
   - Build UI cuối cùng

2. **Add Unit Tests**: Test từng layer độc lập

3. **Add Feature Modules**: Mở rộng với modules như:
   - `gallery` - Xem lại drawings đã lưu
   - `export` - Export to PDF, SVG
   - `share` - Chia sẻ drawings

4. **Optimize Performance**: 
   - Add caching layer
   - Implement pagination cho large drawings

## 📚 Documentation

- Xem `ARCHITECTURE.md` cho chi tiết về architecture
- Mỗi file có documentation comments đầy đủ
- Use cases có examples trong docstrings

## ✨ Key Takeaways

- ✅ **Domain-first approach**: Business logic không phụ thuộc framework
- ✅ **Single Responsibility**: Mỗi class/use case có 1 nhiệm vụ
- ✅ **Dependency Inversion**: High-level không phụ thuộc low-level
- ✅ **Platform Independence**: Dễ dàng add platform mới
- ✅ **Testability**: Mọi layer đều test được độc lập
- ✅ **Maintainability**: Dễ maintain và mở rộng

---

**Migration Status**: ✅ HOÀN THÀNH

Dự án đã sẵn sàng cho development và scalability!
