package com.example.sketchup.core.di

import com.example.sketchup.platform.AndroidImageSaver
import com.example.sketchup.platform.ImageSaver
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * Android-specific Koin module.
 * Provides AndroidImageSaver implementation using injected Context.
 */
actual val imageSaverModule: Module = module {
    // get() will automatically retrieve Context from Koin container
    single<ImageSaver> { AndroidImageSaver(get()) }
}
