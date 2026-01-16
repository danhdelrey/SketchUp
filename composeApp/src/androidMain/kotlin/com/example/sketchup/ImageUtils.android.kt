package com.example.sketchup.core.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import java.io.ByteArrayOutputStream

@RequiresApi(Build.VERSION_CODES.O)
actual fun ImageBitmap.toPngByteArray(): ByteArray {
    // 1. Lấy Bitmap từ Compose
    val androidBitmap = this.asAndroidBitmap()

    // 2. [QUAN TRỌNG] Xử lý Hardware Bitmap
    // Nếu là Hardware Bitmap, copy sang ARGB_8888 (Software) để có thể thao tác bằng Canvas
    val sourceBitmap = if (androidBitmap.config == Bitmap.Config.HARDWARE) {
        androidBitmap.copy(Bitmap.Config.ARGB_8888, false)
    } else {
        androidBitmap
    }

    // 3. Tạo Bitmap mới nền trắng
    val newBitmap = Bitmap.createBitmap(
        sourceBitmap.width,
        sourceBitmap.height,
        Bitmap.Config.ARGB_8888
    )

    // 4. Vẽ nền trắng và đè ảnh gốc lên
    val canvas = Canvas(newBitmap)
    canvas.drawColor(Color.WHITE) // Nền trắng
    canvas.drawBitmap(sourceBitmap, 0f, 0f, null) // Ảnh vẽ

    // 5. Nén ra PNG
    val stream = ByteArrayOutputStream()
    newBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)

    // 6. Dọn dẹp bộ nhớ (Tránh tràn bộ nhớ vì tạo nhiều Bitmap copy)
    // Nếu sourceBitmap là bản copy (do Hardware), thì recycle nó
    if (sourceBitmap != androidBitmap) {
        sourceBitmap.recycle()
    }
    // Recycle luôn bitmap đích sau khi đã nén xong
    newBitmap.recycle()

    return stream.toByteArray()
}