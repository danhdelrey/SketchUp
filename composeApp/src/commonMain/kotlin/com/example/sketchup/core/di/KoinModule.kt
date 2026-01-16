package com.example.sketchup.core.di
import com.example.sketchup.data.repository.DrawingRepository
import com.example.sketchup.data.repository.DrawingRepositoryImpl
import com.example.sketchup.view.features.drawing.screenModel.DrawingScreenModel
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module


val appModule = module {
    includes(imageSaverModule)
    // Single: Repo dùng chung cho toàn app
    single<DrawingRepository> { DrawingRepositoryImpl() }

    // Factory: Platform specific Image Saver
//    factory { provideImageSaver() }

    // Factory: ScreenModel được tạo mỗi khi màn hình cần
    factory { DrawingScreenModel(get(),get()) }
}

fun initKoin(appDeclaration: KoinAppDeclaration = {}) = startKoin {
    appDeclaration() // Chạy config riêng của từng platform
    modules(appModule) // Load module chung
}
expect val imageSaverModule: Module