package com.example.sketchup.core.utils

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asSkiaBitmap
import org.jetbrains.skia.EncodedImageFormat
import org.jetbrains.skia.Image

actual fun ImageBitmap.toPngByteArray(): ByteArray {
    // Chuyển đổi sang Skia Bitmap
    val skiaBitmap = this.asSkiaBitmap()
    // Tạo Image từ Bitmap và encode sang PNG
    val image = Image.makeFromBitmap(skiaBitmap)
    return image.encodeToData(EncodedImageFormat.PNG)?.bytes ?: ByteArray(0)
}