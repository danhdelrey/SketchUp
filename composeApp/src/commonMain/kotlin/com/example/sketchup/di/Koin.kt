package com.example.sketchup.di

import com.example.sketchup.data.repository.InMemoryDrawingRepository
import com.example.sketchup.data.repository.PlatformImageExportRepository
import com.example.sketchup.domain.repository.DrawingRepository
import com.example.sketchup.domain.repository.ImageExportRepository
import com.example.sketchup.domain.usecase.AddStrokeUseCase
import com.example.sketchup.domain.usecase.ExportDrawingUseCase
import com.example.sketchup.domain.usecase.UndoRedoUseCase
import com.example.sketchup.presentation.drawing.DrawingViewModel
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

/**
 * Common DI module - works on all platforms
 */
val commonModule = module {
    // Repositories (Data Layer)
    single<DrawingRepository> { InMemoryDrawingRepository() }

    // Use Cases (Domain Layer)
    singleOf(::AddStrokeUseCase)
    singleOf(::UndoRedoUseCase)
    singleOf(::ExportDrawingUseCase)

    // ViewModels (Presentation Layer)
    factoryOf(::DrawingViewModel)
}

/**
 * Platform-specific module
 * Each platform provides its own implementation
 */
expect val platformModule: Module

/**
 * Initialize Koin with all modules
 */
fun initKoin(config: KoinAppDeclaration = {}) = startKoin {
    config()
    modules(commonModule, platformModule)
}

