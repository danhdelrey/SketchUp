package com.example.sketchup.platform

import kotlinx.browser.document
import org.khronos.webgl.Int8Array
import org.khronos.webgl.set
import org.w3c.dom.HTMLAnchorElement
import org.w3c.dom.url.URL
import org.w3c.files.Blob
import org.w3c.files.BlobPropertyBag

/**
 * Web/WASM implementation of ImageSaver.
 * Downloads the image as a file using the browser's download API.
 */
class WebImageSaver : ImageSaver {

    @OptIn(kotlin.js.ExperimentalJsExport::class)
    override suspend fun saveImage(bytes: ByteArray, fileName: String): Boolean {
        return try {
            // 1. Convert ByteArray (Kotlin) to Int8Array (JS TypedArray)
            val jsBytes = bytes.toJsInt8Array()

            // 2. Create JsArray (JavaScript array) to contain Int8Array
            // Blob requires an array of blob parts
            @Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
            val blobParts = js("[]") as JsArray<JsAny?>
            blobParts[0] = jsBytes

            // 3. Create Blob from JsArray
            val blob = Blob(blobParts, BlobPropertyBag(type = "image/png"))

            // 4. Create URL and anchor element for download
            val url = URL.createObjectURL(blob)
            val anchor = document.createElement("a") as HTMLAnchorElement
            anchor.href = url
            anchor.download = "$fileName.png"
            anchor.style.display = "none"

            document.body?.appendChild(anchor)
            anchor.click()
            document.body?.removeChild(anchor)

            URL.revokeObjectURL(url)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Helper function to convert ByteArray to Int8Array
     */
    private fun ByteArray.toJsInt8Array(): Int8Array {
        val result = Int8Array(this.size)
        for (i in this.indices) {
            result[i] = this[i]
        }
        return result
    }
}
