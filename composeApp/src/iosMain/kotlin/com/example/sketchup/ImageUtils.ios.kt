package com.example.sketchup.core.utils

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asSkiaBitmap
import org.jetbrains.skia.Color
import org.jetbrains.skia.EncodedImageFormat
import org.jetbrains.skia.Image
import org.jetbrains.skia.Surface

actual fun ImageBitmap.toPngByteArray(): ByteArray {
    // 1. Chuyển ImageBitmap của Compose thành Skia Bitmap
    val originalBitmap = this.asSkiaBitmap()

    // 2. Tạo một Surface (bề mặt vẽ) mới
    // makeRasterN32Premul: Tạo bề mặt xử lý pixel tiêu chuẩn
    val surface = Surface.makeRasterN32Premul(originalBitmap.width, originalBitmap.height)
    val canvas = surface.canvas

    // 3. Tô màu trắng cho toàn bộ bề mặt
    canvas.clear(Color.WHITE)

    // 4. Vẽ ảnh gốc (có vùng trong suốt) đè lên
    val originalImage = Image.makeFromBitmap(originalBitmap)
    canvas.drawImage(originalImage, 0f, 0f)

    // 5. Chụp lại kết quả từ Surface thành Image mới
    val finalImage = surface.makeImageSnapshot()

    // 6. Xuất ra byte PNG
    return finalImage.encodeToData(EncodedImageFormat.PNG)?.bytes ?: ByteArray(0)
}