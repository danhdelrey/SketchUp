# KMP Architecture Refactoring

## 🎯 Tổng quan

Project đã được refactor hoàn toàn để tận dụng sức mạnh của Kotlin Multiplatform (KMP) theo kiến trúc **Clean Architecture**.

## 🏗️ Kiến trúc mới

### 1. Domain Layer (commonMain) - Core Business Logic
Lớp này chứa business logic thuần túy, **KHÔNG phụ thuộc vào platform hay framework**.

```
domain/
├── model/              # Domain entities
│   ├── Brush.kt       # Mô hình cọ vẽ
│   ├── Drawing.kt     # Mô hình bức vẽ
│   └── DrawingStroke.kt # Mô hình nét vẽ
├── repository/         # Interfaces (không có implementation)
│   ├── DrawingRepository.kt
│   └── ImageExportRepository.kt
└── usecase/            # Business logic use cases
    ├── AddStrokeUseCase.kt
    ├── UndoRedoUseCase.kt
    └── ExportDrawingUseCase.kt
```

**Lợi ích:**
- Business logic có thể test dễ dàng (pure Kotlin)
- Tái sử dụng trên mọi platform
- Không bị ràng buộc bởi UI framework

### 2. Data Layer (commonMain + platform-specific)
Lớp này implement các interfaces từ Domain layer.

#### Common (commonMain)
```
data/
├── repository/
│   ├── InMemoryDrawingRepository.kt  # Implementation chung
│   └── ImageExportRepositoryImpl.kt  # Base + expect/actual
└── source/
    └── local/
        └── LocalDrawingDataSource.kt  # expect/actual
```

#### Platform-specific (androidMain, iosMain, wasmJsMain)
```
androidMain/
├── data/
│   ├── repository/
│   │   └── PlatformImageExportRepository.android.kt  # Android MediaStore
│   └── source/
│       └── PlatformDrawingDataSource.android.kt      # Room/SharedPrefs

iosMain/
├── data/
│   ├── repository/
│   │   └── PlatformImageExportRepository.ios.kt      # iOS Photos Framework
│   └── source/
│       └── PlatformDrawingDataSource.ios.kt          # CoreData/UserDefaults

wasmJsMain/
├── data/
│   ├── repository/
│   │   └── PlatformImageExportRepository.wasmJs.kt   # Browser Download API
│   └── source/
│       └── PlatformDrawingDataSource.wasmJs.kt       # IndexedDB/localStorage
```

**Lợi ích:**
- Mỗi platform sử dụng native APIs tốt nhất
- Android: MediaStore, Room
- iOS: Photos Framework, CoreData
- Web: Blob/Download API, IndexedDB
- Code chung được tái sử dụng tối đa

### 3. Presentation Layer (commonMain)
UI logic và state management.

```
presentation/
└── drawing/
    ├── DrawingViewModel.kt    # Screen model với use cases
    ├── DrawingUiState.kt      # UI state
    └── DrawingUiEvent.kt      # UI events
```

**Lợi ích:**
- ViewModel phụ thuộc vào Use Cases (domain), không phụ thuộc repositories
- UI state rõ ràng, dễ test
- Separation of concerns tốt

### 4. Dependency Injection (Koin)

#### Common Module (commonMain)
```kotlin
val commonModule = module {
    // Repositories
    single<DrawingRepository> { InMemoryDrawingRepository() }
    
    // Use Cases
    singleOf(::AddStrokeUseCase)
    singleOf(::UndoRedoUseCase)
    singleOf(::ExportDrawingUseCase)
    
    // ViewModels
    factoryOf(::DrawingViewModel)
}
```

#### Platform Modules
```kotlin
// Android
actual val platformModule = module {
    single<ImageExportRepository> { 
        PlatformImageExportRepository(get<Context>()) 
    }
}

// iOS
actual val platformModule = module {
    single<ImageExportRepository> { 
        PlatformImageExportRepository() 
    }
}

// Web
actual val platformModule = module {
    single<ImageExportRepository> { 
        PlatformImageExportRepository() 
    }
}
```

## 🔄 Data Flow

```
UI (Compose)
    ↓ User Action
ViewModel (Presentation Layer)
    ↓ Business Event
Use Cases (Domain Layer)
    ↓ Domain Operations
Repository Interface (Domain Layer)
    ↓ Implementation
Repository Impl (Data Layer)
    ↓ Platform-specific
Native Platform APIs
```

## ✨ Điểm khác biệt so với cũ

### Trước (Giống Jetpack Compose thuần):
```kotlin
// Tất cả logic trộn lẫn
class DrawingScreenModel(
    private val repository: DrawingRepository,
    private val imageSaver: ImageSaver
) {
    // Logic vẽ, lưu ảnh, undo/redo tất cả ở đây
    fun onEvent(event: DrawingEvent) { ... }
}
```

### Sau (KMP Clean Architecture):
```kotlin
// Domain: Business logic thuần túy
class AddStrokeUseCase(private val repository: DrawingRepository) {
    suspend operator fun invoke(stroke: DrawingStroke) {
        if (stroke.points.size < 2) return
        repository.addStroke(stroke)
    }
}

// Presentation: UI logic riêng biệt
class DrawingViewModel(
    private val addStrokeUseCase: AddStrokeUseCase,
    private val undoRedoUseCase: UndoRedoUseCase,
    private val exportDrawingUseCase: ExportDrawingUseCase
) {
    fun onEvent(event: DrawingUiEvent) { ... }
}

// Data: Platform-specific implementation
actual class PlatformImageExportRepository(
    private val context: Context  // Android specific
) : ImageExportRepository {
    override suspend fun exportImage(...) {
        // Sử dụng Android MediaStore API
    }
}
```

## 🎁 Lợi ích của kiến trúc mới

### 1. **Tách biệt concerns rõ ràng**
- Domain: Business rules (không biết gì về Android/iOS/Web)
- Data: Platform implementation
- Presentation: UI logic

### 2. **Testability cao**
```kotlin
// Test use case dễ dàng
class AddStrokeUseCaseTest {
    @Test
    fun `should not add stroke with less than 2 points`() {
        val mockRepo = MockDrawingRepository()
        val useCase = AddStrokeUseCase(mockRepo)
        
        useCase(stroke with 1 point)
        
        verify(mockRepo, never()).addStroke(any())
    }
}
```

### 3. **Platform-specific optimizations**
- **Android**: Dùng MediaStore (scoped storage), Room database
- **iOS**: Dùng Photos framework, CoreData
- **Web**: Dùng Blob API, IndexedDB
- Mỗi platform tận dụng native APIs tốt nhất

### 4. **Reusability**
- Business logic được chia sẻ 100% (domain layer)
- Data logic được chia sẻ phần lớn (common repo)
- Chỉ platform-specific code mới viết riêng

### 5. **Maintainability**
- Dễ thêm tính năng mới (thêm use case)
- Dễ thay đổi implementation (swap repository)
- Dễ debug (từng layer rõ ràng)

## 📝 Migration Guide

### Để sử dụng kiến trúc mới:

1. **Update Android App**:
```kotlin
// AndroidApp.kt
class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@MyApp)
        }
    }
}
```

2. **Update iOS App**:
```swift
// iOSApp.swift
@main
struct iOSApp: App {
    init() {
        KoinKt.doInitKoin()
    }
}
```

3. **Update ViewModel usage**:
```kotlin
// DrawingScreen.kt
@Composable
fun DrawingScreen() {
    val viewModel = koinScreenModel<DrawingViewModel>()
    val uiState by viewModel.uiState.collectAsState()
    
    // UI code
}
```

## 🚀 Next Steps

1. **Implement persistence**:
   - Android: Room database
   - iOS: CoreData
   - Web: IndexedDB

2. **Add more features**:
   - Load/Save drawings
   - Share functionality
   - Cloud sync

3. **Optimize**:
   - Add caching layer
   - Implement offline-first
   - Add analytics

## 📚 Tài liệu tham khảo

- [Clean Architecture](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)
- [Kotlin Multiplatform](https://kotlinlang.org/docs/multiplatform.html)
- [KMP Best Practices](https://kotlinlang.org/docs/multiplatform-mobile-best-practices.html)

