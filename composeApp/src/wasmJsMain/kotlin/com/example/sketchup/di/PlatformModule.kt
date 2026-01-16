package com.example.sketchup.di

import com.example.sketchup.data.source.local.ImageStorageDataSource
import com.example.sketchup.platform.WebImageStorageDataSource
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * Web/WASM-specific platform module.
 * Provides Web implementations of platform-specific interfaces.
 */
actual val platformModule: Module = module {
    single<ImageStorageDataSource> { WebImageStorageDataSource() }
}
