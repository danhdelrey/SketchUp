package com.example.sketchup.core.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import java.io.ByteArrayOutputStream

/**
 * Converts an ImageBitmap to PNG byte array for Android.
 * Handles Hardware Bitmap conversion and adds white background.
 */
@RequiresApi(Build.VERSION_CODES.O)
actual fun ImageBitmap.toPngByteArray(): ByteArray {
    // 1. Get Bitmap from Compose
    val androidBitmap = this.asAndroidBitmap()

    // 2. Handle Hardware Bitmap
    // If it's a Hardware Bitmap, copy to ARGB_8888 (Software) for Canvas operations
    val sourceBitmap = if (androidBitmap.config == Bitmap.Config.HARDWARE) {
        androidBitmap.copy(Bitmap.Config.ARGB_8888, false)
    } else {
        androidBitmap
    }

    // 3. Create new Bitmap with white background
    val newBitmap = Bitmap.createBitmap(
        sourceBitmap.width,
        sourceBitmap.height,
        Bitmap.Config.ARGB_8888
    )

    // 4. Draw white background and overlay original image
    val canvas = Canvas(newBitmap)
    canvas.drawColor(Color.WHITE)
    canvas.drawBitmap(sourceBitmap, 0f, 0f, null)

    // 5. Compress to PNG
    val stream = ByteArrayOutputStream()
    newBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)

    // 6. Clean up memory to avoid leaks
    if (sourceBitmap != androidBitmap) {
        sourceBitmap.recycle()
    }
    newBitmap.recycle()

    return stream.toByteArray()
}
