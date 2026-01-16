package com.example.sketchup.di

import com.example.sketchup.data.repository.DrawingRepositoryImpl
import com.example.sketchup.data.repository.ImageStorageRepositoryImpl
import com.example.sketchup.domain.repository.DrawingRepository
import com.example.sketchup.domain.repository.ImageStorageRepository
import com.example.sketchup.domain.usecase.*
import com.example.sketchup.presentation.drawing.DrawingScreenModel
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

/**
 * Domain layer module - Use cases
 */
val domainModule = module {
    // Use Cases
    factory { ObserveDrawingPathsUseCase(get()) }
    factory { AddDrawingPathUseCase(get()) }
    factory { UndoDrawingUseCase(get()) }
    factory { RedoDrawingUseCase(get()) }
    factory { ClearDrawingUseCase(get()) }
    factory { SaveDrawingImageUseCase(get()) }
}

/**
 * Data layer module - Repositories
 */
val dataModule = module {
    // Repositories
    single<DrawingRepository> { DrawingRepositoryImpl() }
    single<ImageStorageRepository> { ImageStorageRepositoryImpl(get()) }
}

/**
 * Presentation layer module - ViewModels/ScreenModels
 */
val presentationModule = module {
    // ScreenModels
    factory {
        DrawingScreenModel(
            observeDrawingPathsUseCase = get(),
            addDrawingPathUseCase = get(),
            undoDrawingUseCase = get(),
            redoDrawingUseCase = get(),
            clearDrawingUseCase = get(),
            saveDrawingImageUseCase = get(),
            drawingRepository = get()
        )
    }
}

/**
 * Common Koin module that combines all layers.
 */
val appModule = module {
    includes(
        domainModule,
        dataModule,
        presentationModule,
        platformModule // Platform-specific implementations
    )
}

/**
 * Initializes Koin for dependency injection.
 * @param appDeclaration Platform-specific configuration
 */
fun initKoin(appDeclaration: KoinAppDeclaration = {}) = startKoin {
    appDeclaration()
    modules(appModule)
}

/**
 * Platform-specific module for platform implementations.
 * Each platform (Android, iOS, Web) provides its own implementation.
 */
expect val platformModule: Module
