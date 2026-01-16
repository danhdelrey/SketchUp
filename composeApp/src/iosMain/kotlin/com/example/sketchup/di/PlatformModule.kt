package com.example.sketchup.di

import com.example.sketchup.data.source.local.ImageStorageDataSource
import com.example.sketchup.platform.IosImageStorageDataSource
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * iOS-specific platform module.
 * Provides iOS implementations of platform-specific interfaces.
 */
actual val platformModule: Module = module {
    single<ImageStorageDataSource> { IosImageStorageDataSource() }
}
