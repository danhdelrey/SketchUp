package com.example.sketchup.core.di

import AndroidImageSaver
import com.example.sketchup.platform.ImageSaver
import org.koin.core.module.Module
import org.koin.dsl.module

actual val imageSaverModule: Module = module {
    // get() sẽ tự động lấy Context từ Koin container
    single<ImageSaver> { AndroidImageSaver(get()) }
}