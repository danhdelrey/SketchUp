package com.example.sketchup.core.di

import com.example.sketchup.platform.IosImageSaver
import com.example.sketchup.platform.ImageSaver
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * iOS-specific Koin module.
 * Provides IosImageSaver implementation.
 */
actual val imageSaverModule: Module = module {
    single<ImageSaver> { IosImageSaver() }
}
