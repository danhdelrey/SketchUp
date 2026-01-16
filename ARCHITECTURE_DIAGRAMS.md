# Architecture Diagrams

## 🏗️ Overall Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                         UI Layer                                 │
│                    (Compose Multiplatform)                       │
│  ┌───────────┐  ┌───────────┐  ┌───────────┐                   │
│  │ DrawingUI │  │ BrushUI   │  │ ToolbarUI │                   │
│  └─────┬─────┘  └─────┬─────┘  └─────┬─────┘                   │
└────────┼──────────────┼──────────────┼───────────────────────────┘
         │              │              │
         └──────────────┴──────────────┘
                        │
         ┌──────────────▼──────────────┐
         │     DrawingViewModel        │
         │   (Presentation Layer)      │
         │  - UI State Management      │
         │  - Event Handling           │
         └──────────────┬──────────────┘
                        │
         ┌──────────────▼──────────────────────────┐
         │          Use Cases                       │
         │        (Domain Layer)                    │
         │  ┌────────────┐  ┌──────────────┐      │
         │  │AddStroke   │  │UndoRedo      │      │
         │  │UseCase     │  │UseCase       │      │
         │  └─────┬──────┘  └──────┬───────┘      │
         │  ┌─────▼────────────────▼───────┐      │
         │  │  ExportDrawingUseCase        │      │
         │  └──────────────┬────────────────┘      │
         └─────────────────┼─────────────────────┘
                          │
         ┌────────────────▼─────────────────────┐
         │     Repository Interfaces            │
         │       (Domain Layer)                 │
         │  ┌────────────┐  ┌────────────────┐ │
         │  │Drawing     │  │ImageExport     │ │
         │  │Repository  │  │Repository      │ │
         │  └─────┬──────┘  └────────┬───────┘ │
         └────────┼───────────────────┼─────────┘
                  │                   │
         ┌────────▼───────────────────▼─────────┐
         │   Repository Implementations          │
         │        (Data Layer)                   │
         │  ┌─────────────────────────────────┐ │
         │  │  InMemoryDrawingRepository      │ │
         │  └─────────────────────────────────┘ │
         │  ┌─────────────────────────────────┐ │
         │  │ PlatformImageExportRepository   │ │
         │  │    (expect/actual)              │ │
         │  └──────────┬──────────────────────┘ │
         └─────────────┼────────────────────────┘
                       │
         ┌─────────────▼────────────────────────┐
         │    Platform-Specific Implementations  │
         ├───────────────────────────────────────┤
         │  Android  │   iOS    │     Web        │
         ├───────────┼──────────┼────────────────┤
         │MediaStore │ Photos   │ Blob/Download  │
         │SharedPrefs│UserDef   │ localStorage   │
         │  Room     │CoreData  │  IndexedDB     │
         └───────────────────────────────────────┘
```

## 🔄 Data Flow Example: Adding a Stroke

```
User draws on canvas
        │
        ▼
┌───────────────────┐
│  DrawingScreen    │ onTouchMove(offset)
│  (UI Component)   │
└────────┬──────────┘
         │
         ▼
┌───────────────────┐
│ DrawingViewModel  │ onEvent(OnTouchMove(offset))
└────────┬──────────┘
         │
         │ points collected
         ▼
┌───────────────────┐
│ DrawingViewModel  │ onEvent(OnTouchEnd)
└────────┬──────────┘
         │
         │ create DrawingStroke
         ▼
┌───────────────────┐
│ AddStrokeUseCase  │ invoke(stroke)
└────────┬──────────┘
         │
         │ validate (points >= 2)
         ▼
┌───────────────────┐
│DrawingRepository  │ addStroke(stroke)
└────────┬──────────┘
         │
         │ update drawing
         ▼
┌───────────────────┐
│  MutableStateFlow │ emit new Drawing
│  <Drawing>        │
└────────┬──────────┘
         │
         │ collect
         ▼
┌───────────────────┐
│ DrawingViewModel  │ uiState updated
└────────┬──────────┘
         │
         │ observe
         ▼
┌───────────────────┐
│  DrawingScreen    │ UI recomposes
│  (UI Component)   │ shows new stroke
└───────────────────┘
```

## 🌐 Platform-Specific Flow: Export Image

```
User clicks Export
        │
        ▼
┌──────────────────────┐
│  DrawingViewModel    │ onEvent(OnExportImage(bytes))
└───────────┬──────────┘
            │
            ▼
┌──────────────────────┐
│ExportDrawingUseCase  │ invoke(imageData, fileName)
└───────────┬──────────┘
            │
            │ validate imageData
            │ check platform support
            ▼
┌──────────────────────┐
│ImageExportRepository │ exportImage(bytes, name)
│    (interface)       │
└───────────┬──────────┘
            │
            │ expect/actual dispatches to:
            │
    ┌───────┴────────┬────────────────┐
    │                │                │
    ▼                ▼                ▼
┌─────────┐    ┌─────────┐    ┌──────────┐
│ Android │    │   iOS   │    │   Web    │
└────┬────┘    └────┬────┘    └────┬─────┘
     │              │              │
     ▼              ▼              ▼
┌─────────┐    ┌─────────┐    ┌──────────┐
│MediaStore    │Photos   │    │Blob API  │
│  API    │    │Framework│    │Download  │
└────┬────┘    └────┬────┘    └────┬─────┘
     │              │              │
     │ Save to      │ Save to      │ Trigger
     │ Pictures/    │ Photo        │ browser
     │ SketchUp     │ Library      │ download
     │              │              │
     └──────────────┴──────────────┘
                    │
                    ▼
         Result<String> (file path)
                    │
                    ▼
         ┌──────────────────────┐
         │ ExportDrawingUseCase │ return Result
         └──────────┬───────────┘
                    │
                    ▼
         ┌──────────────────────┐
         │  DrawingViewModel    │ handle result
         │  - success: show msg │
         │  - failure: show err │
         └──────────┬───────────┘
                    │
                    ▼
         ┌──────────────────────┐
         │  DrawingScreen       │ display Snackbar
         │  (UI)                │
         └──────────────────────┘
```

## 🧩 Dependency Graph

```
                    ┌─────────────┐
                    │     UI      │
                    │ (DrawingUI) │
                    └──────┬──────┘
                           │
                    ┌──────▼─────────┐
                    │  ViewModel     │
                    │(Presentation)  │
                    └──────┬─────────┘
                           │
        ┌──────────────────┼──────────────────┐
        │                  │                  │
   ┌────▼────┐      ┌─────▼──────┐    ┌─────▼────────┐
   │AddStroke│      │ UndoRedo   │    │ExportDrawing │
   │UseCase  │      │ UseCase    │    │  UseCase     │
   └────┬────┘      └─────┬──────┘    └─────┬────────┘
        │                 │                  │
        └────────┬────────┴─────────┬────────┘
                 │                  │
        ┌────────▼──────┐    ┌──────▼─────────┐
        │  Drawing      │    │  ImageExport   │
        │  Repository   │    │  Repository    │
        │  (Interface)  │    │  (Interface)   │
        └────────┬──────┘    └──────┬─────────┘
                 │                  │
        ┌────────▼──────┐    ┌──────▼─────────┐
        │  InMemory     │    │  Platform      │
        │  Drawing      │    │  ImageExport   │
        │  Repository   │    │  (expect/actual)│
        └───────────────┘    └──────┬─────────┘
                                    │
                     ┌──────────────┼──────────────┐
                     │              │              │
              ┌──────▼──────┐ ┌────▼─────┐ ┌─────▼────┐
              │  Android    │ │   iOS    │ │   Web    │
              │Implementation│ │Implement │ │Implement │
              └─────────────┘ └──────────┘ └──────────┘
```

## 📦 Module Dependencies

```
┌─────────────────────────────────────────┐
│           composeApp                     │
├─────────────────────────────────────────┤
│                                         │
│  ┌─────────────────────────────────┐   │
│  │       commonMain                 │   │
│  │  ┌────────────┐  ┌────────────┐ │   │
│  │  │  domain    │  │presentation│ │   │
│  │  │(no deps)   │  │  (domain)  │ │   │
│  │  └────────────┘  └────────────┘ │   │
│  │  ┌────────────┐                 │   │
│  │  │   data     │                 │   │
│  │  │ (domain)   │                 │   │
│  │  └────────────┘                 │   │
│  └─────────────────────────────────┘   │
│                                         │
│  ┌───────────┐ ┌─────────┐ ┌────────┐ │
│  │androidMain│ │iosMain  │ │wasmJs  │ │
│  │(common)   │ │(common) │ │Main    │ │
│  │           │ │         │ │(common)│ │
│  └───────────┘ └─────────┘ └────────┘ │
└─────────────────────────────────────────┘
```

## 🎯 Principle: Dependency Rule

```
┌──────────────────────────────────────┐
│  Presentation Layer                  │
│  (UI, ViewModels, UI State)          │
│                                      │
│  Dependencies: ↓ Domain              │
└──────────────┬───────────────────────┘
               │
┌──────────────▼───────────────────────┐
│  Domain Layer                        │
│  (Use Cases, Entities, Interfaces)   │
│                                      │
│  Dependencies: NONE (Pure Kotlin)    │
└──────────────▲───────────────────────┘
               │
┌──────────────┴───────────────────────┐
│  Data Layer                          │
│  (Repositories, Data Sources)        │
│                                      │
│  Dependencies: ↑ Domain (interfaces) │
└──────────────────────────────────────┘
```

**Key Points:**
- Domain layer has NO dependencies (pure business logic)
- Presentation depends on Domain (uses use cases)
- Data depends on Domain (implements interfaces)
- **Dependencies point INWARD** (toward domain)

## 🔧 Testing Strategy

```
┌──────────────────────────────────────┐
│        Unit Tests                    │
│  ┌────────────────────────────────┐ │
│  │  Use Cases (Pure Kotlin)       │ │ ← Easy to test
│  │  - Mock repositories           │ │
│  │  - Test business logic         │ │
│  └────────────────────────────────┘ │
│  ┌────────────────────────────────┐ │
│  │  Repositories                  │ │ ← Easy to test
│  │  - Mock data sources           │ │
│  │  - Test data transformations   │ │
│  └────────────────────────────────┘ │
└──────────────────────────────────────┘

┌──────────────────────────────────────┐
│     Integration Tests                │
│  ┌────────────────────────────────┐ │
│  │  ViewModel + Use Cases         │ │
│  │  - Test complete flows         │ │
│  └────────────────────────────────┘ │
└──────────────────────────────────────┘

┌──────────────────────────────────────┐
│        UI Tests                      │
│  ┌────────────────────────────────┐ │
│  │  Compose UI Tests              │ │
│  │  - Screenshot tests            │ │
│  │  - User interaction tests      │ │
│  └────────────────────────────────┘ │
└──────────────────────────────────────┘
```

## 📊 Code Sharing Metrics

```
                   Code Sharing by Layer
                   
Domain Layer:       ████████████████████  100%
Use Cases:          ████████████████████  100%
Presentation:       ██████████████████░░   90%
Data (Common):      ████████████░░░░░░░░   60%
Data (Platform):    ░░░░░░░░░░░░░░░░░░░░    0%
UI Components:      ██████████████████░░   90%
                    
Overall:            ███████████████░░░░░   75%
```

**Breakdown:**
- **Domain Layer**: 100% shared (pure Kotlin, no platform deps)
- **Use Cases**: 100% shared (business logic)
- **Presentation**: 90% shared (ViewModels, some platform-specific UI)
- **Data (Common)**: 60% shared (interfaces, base implementations)
- **Data (Platform)**: 0% shared (platform-specific by design)
- **UI Components**: 90% shared (Compose Multiplatform)

## 🚀 Benefits Visualization

```
Before (Jetpack Compose style):
┌─────────────────────────────┐
│     All Code Mixed          │
│                             │
│  ┌───────────────────────┐  │
│  │ UI + Logic + Data     │  │
│  │ Platform Code Mixed   │  │
│  │ Hard to Test          │  │
│  │ Low Reusability       │  │
│  └───────────────────────┘  │
└─────────────────────────────┘

After (KMP Clean Architecture):
┌─────────────────────────────┐
│   Clear Separation          │
│                             │
│  ┌───────────────────────┐  │
│  │ UI (90% shared)       │  │
│  └───────────────────────┘  │
│  ┌───────────────────────┐  │
│  │ Logic (100% shared)   │  │ ← Highly testable
│  └───────────────────────┘  │
│  ┌───────────────────────┐  │
│  │ Data (Platform opt)   │  │ ← Native APIs
│  └───────────────────────┘  │
└─────────────────────────────┘
```

