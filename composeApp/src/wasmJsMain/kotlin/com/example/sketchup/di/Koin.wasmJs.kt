package com.example.sketchup.di

import com.example.sketchup.data.repository.PlatformImageExportRepository
import com.example.sketchup.domain.repository.ImageExportRepository
import org.koin.dsl.module

/**
 * WasmJS/Web platform-specific module
 */
actual val platformModule = module {
    single<ImageExportRepository> {
        PlatformImageExportRepository()
    }
}

