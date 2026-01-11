package com.example.sketchup.platform

import androidx.compose.ui.graphics.ImageBitmap

// Interface định nghĩa việc lưu ảnh
interface ImageSaver {
    suspend fun saveImage(bitmap: ImageBitmap)
}

// Expect function để Koin có thể cung cấp implementation cho từng nền tảng
//expect fun provideImageSaver(): ImageSaver