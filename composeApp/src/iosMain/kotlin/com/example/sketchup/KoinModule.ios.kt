package com.example.sketchup.core.di


import IosImageSaver
import com.example.sketchup.platform.ImageSaver
import org.koin.core.module.Module
import org.koin.dsl.module

actual val imageSaverModule: Module = module {
    single<ImageSaver> { IosImageSaver() }
}
