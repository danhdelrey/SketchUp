# Migration Guide: From Old to New Architecture

## 📋 Tổng quan

Hướng dẫn này giúp bạn migrate từ kiến trúc cũ (giống Jetpack Compose thuần) sang kiến trúc mới (KMP Clean Architecture).

## 🔄 Các thay đổi chính

### 1. Package Structure

**Trước:**
```
com.example.sketchup/
├── data/
│   ├── model/DrawingPath.kt
│   └── repository/DrawingRepository.kt
├── view/
│   └── features/drawing/
│       ├── screenModel/DrawingScreenModel.kt
│       ├── screen/DrawingScreen.kt
│       └── component/
└── core/
    └── di/KoinModule.kt
```

**Sau:**
```
com.example.sketchup/
├── domain/              # NEW - Business logic layer
│   ├── model/
│   ├── repository/      # Interfaces only
│   ├── usecase/         # NEW - Business operations
│   ├── platform/        # NEW - Platform abstractions
│   └── storage/         # NEW - Storage abstractions
├── data/
│   ├── repository/      # Implementations
│   └── source/          # NEW - Data sources
├── presentation/        # NEW - Renamed from 'view'
│   └── drawing/
└── di/                  # Simplified DI
```

### 2. Models Migration

#### DrawingPath → DrawingStroke (Domain Model)

**Trước (Data Model):**
```kotlin
// data/model/DrawingPath.kt
data class DrawingPath(
    val points: List<Offset>,
    val color: Color,
    val strokeWidth: Float,
    val isEraser: Boolean = false
)
```

**Sau (Domain Model):**
```kotlin
// domain/model/DrawingStroke.kt
data class DrawingStroke(
    val id: String,                    // NEW - Unique identifier
    val points: List<Point>,           // NEW - Custom Point type
    val brush: Brush,                  // NEW - Brush model
    val timestamp: Long                // NEW - Timestamp
) {
    data class Point(
        val x: Float,
        val y: Float,
        val pressure: Float = 1f      // NEW - Pressure support
    )
}

// domain/model/Brush.kt (NEW)
data class Brush(
    val size: Float,
    val color: Color,
    val opacity: Float = 1f,
    val type: BrushType = BrushType.NORMAL
)
```

**Migration:**
```kotlin
// Old code
val path = DrawingPath(
    points = listOf(...),
    color = Color.Black,
    strokeWidth = 10f
)

// New code
val stroke = DrawingStroke.fromOffsets(
    id = "stroke_123",
    offsets = listOf(...),
    brush = Brush(
        size = 10f,
        color = Color.Black
    ),
    timestamp = System.currentTimeMillis()
)
```

### 3. Repository Migration

**Trước (Repository với implementation trộn lẫn):**
```kotlin
// data/repository/DrawingRepository.kt
interface DrawingRepository {
    val paths: StateFlow<List<DrawingPath>>
    fun addPath(path: DrawingPath)
    fun undo()
    fun redo()
}

class DrawingRepositoryImpl : DrawingRepository {
    private val _paths = MutableStateFlow<List<DrawingPath>>(emptyList())
    override val paths = _paths.asStateFlow()
    // ... implementation
}
```

**Sau (Domain interface + Data implementation):**
```kotlin
// domain/repository/DrawingRepository.kt (Interface)
interface DrawingRepository {
    fun observeCurrentDrawing(): Flow<Drawing>    // Better naming
    suspend fun addStroke(stroke: DrawingStroke)  // Suspend for async
    suspend fun undo(): Boolean                   // Return success
    suspend fun redo(): Boolean
    fun canUndo(): Flow<Boolean>                  // Reactive state
    fun canRedo(): Flow<Boolean>
}

// data/repository/InMemoryDrawingRepository.kt (Implementation)
class InMemoryDrawingRepository : DrawingRepository {
    private val _currentDrawing = MutableStateFlow(Drawing.empty())
    // ... implementation
}
```

### 4. ScreenModel → ViewModel Migration

**Trước (ScreenModel với repository dependency):**
```kotlin
// view/features/drawing/screenModel/DrawingScreenModel.kt
class DrawingScreenModel(
    private val repository: DrawingRepository,
    private val imageSaver: ImageSaver
) : ScreenModel {
    
    private val _currentColor = MutableStateFlow(Color.Black)
    private val _currentWidth = MutableStateFlow(10f)
    
    val state = combine(
        repository.paths,
        _currentColor,
        _currentWidth,
        // ...
    ) { paths, color, width ->
        DrawingState(...)
    }.stateIn(...)
    
    fun onEvent(event: DrawingEvent) {
        when (event) {
            is DrawingEvent.StartDraw -> { /* ... */ }
            is DrawingEvent.SavePng -> saveImage(event.bytes)
        }
    }
}
```

**Sau (ViewModel với use case dependencies):**
```kotlin
// presentation/drawing/DrawingViewModel.kt
class DrawingViewModel(
    private val addStrokeUseCase: AddStrokeUseCase,      // Use cases
    private val undoRedoUseCase: UndoRedoUseCase,
    private val exportDrawingUseCase: ExportDrawingUseCase
) : ScreenModel {
    
    private val _currentBrush = MutableStateFlow(Brush.DEFAULT)
    
    val uiState = combine(
        undoRedoUseCase.canUndo(),              // From use case
        undoRedoUseCase.canRedo(),
        _currentBrush,
        // ...
    ) { canUndo, canRedo, brush ->
        DrawingUiState(...)                     // Dedicated UI state
    }.stateIn(...)
    
    fun onEvent(event: DrawingUiEvent) {        // UI events
        when (event) {
            is DrawingUiEvent.OnTouchStart -> handleTouchStart(event.offset)
            is DrawingUiEvent.OnExportImage -> handleExportImage(event.imageData)
        }
    }
    
    private fun handleExportImage(imageData: ByteArray) {
        screenModelScope.launch {
            val result = exportDrawingUseCase(imageData, "sketch")
            result.fold(
                onSuccess = { /* ... */ },
                onFailure = { /* ... */ }
            )
        }
    }
}
```

### 5. Use Cases (NEW Concept)

**Trước:** Business logic nằm trong ScreenModel

**Sau:** Business logic trong Use Cases riêng biệt

```kotlin
// domain/usecase/AddStrokeUseCase.kt (NEW)
class AddStrokeUseCase(
    private val repository: DrawingRepository
) {
    suspend operator fun invoke(stroke: DrawingStroke) {
        // Business rule: validate stroke
        if (stroke.points.size < 2) {
            return // Don't add invalid strokes
        }
        repository.addStroke(stroke)
    }
}

// domain/usecase/ExportDrawingUseCase.kt (NEW)
class ExportDrawingUseCase(
    private val drawingRepository: DrawingRepository,
    private val imageExportRepository: ImageExportRepository
) {
    suspend operator fun invoke(imageData: ByteArray, fileName: String): Result<String> {
        // Business logic
        if (imageData.isEmpty()) {
            return Result.failure(IllegalArgumentException("Image data is empty"))
        }
        
        if (!imageExportRepository.isExportSupported()) {
            return Result.failure(UnsupportedOperationException("Export not supported"))
        }
        
        return imageExportRepository.exportImage(imageData, fileName)
    }
}
```

### 6. Dependency Injection Migration

**Trước:**
```kotlin
// core/di/KoinModule.kt
val appModule = module {
    includes(imageSaverModule)
    single<DrawingRepository> { DrawingRepositoryImpl() }
    factory { DrawingScreenModel(get(), get()) }
}

// Platform-specific
expect val imageSaverModule: Module
```

**Sau:**
```kotlin
// di/Koin.kt
val commonModule = module {
    // Repositories
    single<DrawingRepository> { InMemoryDrawingRepository() }
    
    // Use Cases (NEW)
    singleOf(::AddStrokeUseCase)
    singleOf(::UndoRedoUseCase)
    singleOf(::ExportDrawingUseCase)
    
    // ViewModels
    factoryOf(::DrawingViewModel)
}

expect val platformModule: Module  // Platform-specific repos
```

### 7. Platform-Specific Code

**Trước:** ImageSaver interface với platform implementations

**Sau:** Multiple platform abstractions

```kotlin
// NEW: Platform capabilities
// domain/platform/PlatformCapabilities.kt
expect class PlatformInfo() : PlatformCapabilities {
    fun supportsPressureSensitivity(): Boolean
    fun supportsHaptics(): Boolean
}

// NEW: Platform storage
// domain/storage/KeyValueStorage.kt
expect class PlatformKeyValueStorage() : KeyValueStorage {
    suspend fun putString(key: String, value: String)
    suspend fun getString(key: String): String?
}

// NEW: Platform image export
// data/repository/ImageExportRepositoryImpl.kt
expect class PlatformImageExportRepository() : ImageExportRepository {
    suspend fun exportImage(imageData: ByteArray, fileName: String): Result<String>
}
```

## 📝 Step-by-Step Migration

### Step 1: Create Domain Layer
```bash
# Create domain models
domain/model/Brush.kt
domain/model/Drawing.kt
domain/model/DrawingStroke.kt

# Create repository interfaces (no implementation)
domain/repository/DrawingRepository.kt
domain/repository/ImageExportRepository.kt

# Create use cases
domain/usecase/AddStrokeUseCase.kt
domain/usecase/UndoRedoUseCase.kt
domain/usecase/ExportDrawingUseCase.kt
```

### Step 2: Update Data Layer
```bash
# Move repository implementations to data/
data/repository/InMemoryDrawingRepository.kt

# Create platform-specific implementations
androidMain/data/repository/PlatformImageExportRepository.android.kt
iosMain/data/repository/PlatformImageExportRepository.ios.kt
wasmJsMain/data/repository/PlatformImageExportRepository.wasmJs.kt
```

### Step 3: Create Presentation Layer
```bash
# Rename view/ to presentation/
presentation/drawing/DrawingViewModel.kt
presentation/drawing/DrawingUiState.kt
presentation/drawing/DrawingUiEvent.kt
```

### Step 4: Update DI
```bash
# Update Koin modules
di/Koin.kt                    # Common
androidMain/di/Koin.android.kt
iosMain/di/Koin.ios.kt
wasmJsMain/di/Koin.wasmJs.kt
```

### Step 5: Update UI
```kotlin
// Update composables to use new ViewModel
@Composable
fun DrawingScreen() {
    val viewModel = koinScreenModel<DrawingViewModel>()  // Was DrawingScreenModel
    val uiState by viewModel.uiState.collectAsState()    // Was state
    
    // Update event handling
    viewModel.onEvent(DrawingUiEvent.OnTouchStart(...))  // Was DrawingEvent
}
```

## ✅ Checklist

- [ ] Create domain layer (models, repositories, use cases)
- [ ] Move data implementations to data layer
- [ ] Create platform-specific implementations
- [ ] Update ViewModel to use use cases
- [ ] Update DI configuration
- [ ] Update UI code to use new ViewModels
- [ ] Test each platform
- [ ] Remove old code
- [ ] Update documentation

## 🎯 Benefits After Migration

### Before (Old Architecture)
- ❌ Business logic mixed with UI logic
- ❌ Hard to test
- ❌ Platform code not well separated
- ❌ Limited code reuse

### After (New Architecture)
- ✅ Clear separation of concerns
- ✅ Highly testable (use cases, repositories)
- ✅ Platform-specific optimizations
- ✅ Maximum code reuse (domain layer 100% shared)
- ✅ Easy to add new features
- ✅ Easy to maintain

## 📚 Resources

- [Clean Architecture](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)
- [KMP Best Practices](https://kotlinlang.org/docs/multiplatform-mobile-best-practices.html)
- [Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/)

## ❓ FAQ

**Q: Có cần migrate toàn bộ cùng lúc không?**
A: Không. Có thể migrate từng feature một. Domain layer và Data layer có thể coexist với code cũ.

**Q: Use cases có bắt buộc không?**
A: Không bắt buộc nhưng strongly recommended. Use cases giúp tách business logic khỏi ViewModel.

**Q: Platform-specific code có phải viết lại hoàn toàn?**
A: Không. Sử dụng lại code cũ, chỉ cần wrap trong expect/actual pattern.

**Q: Testing có dễ hơn không?**
A: Có! Use cases và repositories giờ có thể test độc lập với UI và platform code.

