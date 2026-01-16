package com.example.sketchup.core.di


import WebImageSaver
import com.example.sketchup.platform.ImageSaver
import org.koin.core.module.Module
import org.koin.dsl.module

actual val imageSaverModule: Module = module {
    single<ImageSaver> { WebImageSaver() }
}