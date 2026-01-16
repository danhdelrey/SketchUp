package com.example.sketchup.core.di

import com.example.sketchup.platform.WebImageSaver
import com.example.sketchup.platform.ImageSaver
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * WASM/Web-specific Koin module.
 * Provides WebImageSaver implementation for browser download.
 */
actual val imageSaverModule: Module = module {
    single<ImageSaver> { WebImageSaver() }
}
