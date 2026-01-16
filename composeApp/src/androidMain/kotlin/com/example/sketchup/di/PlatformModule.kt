package com.example.sketchup.di

import com.example.sketchup.data.source.local.ImageStorageDataSource
import com.example.sketchup.platform.AndroidImageStorageDataSource
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * Android-specific platform module.
 * Provides Android implementations of platform-specific interfaces.
 */
actual val platformModule: Module = module {
    single<ImageStorageDataSource> { AndroidImageStorageDataSource(get()) }
}
