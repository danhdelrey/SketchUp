package com.example.sketchup.core.di

import com.example.sketchup.data.repository.DrawingRepository
import com.example.sketchup.data.repository.DrawingRepositoryImpl
import com.example.sketchup.view.features.drawing.screenModel.DrawingScreenModel
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

/**
 * Common Koin module shared across all platforms.
 * Contains shared dependencies like repositories and screen models.
 */
val appModule = module {
    includes(imageSaverModule)

    // Single: Repository shared across the entire app
    single<DrawingRepository> { DrawingRepositoryImpl() }

    // Factory: ScreenModel created for each screen instance
    factory { DrawingScreenModel(get(), get()) }
}

/**
 * Initializes Koin for dependency injection.
 * @param appDeclaration Platform-specific configuration (e.g., Android Context)
 */
fun initKoin(appDeclaration: KoinAppDeclaration = {}) = startKoin {
    appDeclaration()
    modules(appModule)
}

/**
 * Platform-specific Koin module for ImageSaver implementation.
 * Each platform (Android, iOS, Web) provides its own implementation.
 */
expect val imageSaverModule: Module