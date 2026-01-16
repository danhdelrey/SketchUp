package com.example.sketchup.platform

import com.example.sketchup.data.source.local.ImageStorageDataSource
import kotlinx.browser.document
import org.khronos.webgl.Int8Array
import org.khronos.webgl.set
import org.w3c.dom.HTMLAnchorElement
import org.w3c.dom.url.URL
import org.w3c.files.Blob
import org.w3c.files.BlobPropertyBag

/**
 * Web/WASM implementation of ImageStorageDataSource.
 * Downloads the image as a file using the browser's download API.
 */
class WebImageStorageDataSource : ImageStorageDataSource {

    @OptIn(kotlin.js.ExperimentalJsExport::class)
    override suspend fun saveImage(imageData: ByteArray, fileName: String): Boolean {
        return try {
            val jsBytes = imageData.toJsInt8Array()

            @Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
            val blobParts = js("[]") as JsArray<JsAny?>
            blobParts[0] = jsBytes

            val blob = Blob(blobParts, BlobPropertyBag(type = "image/png"))

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

    private fun ByteArray.toJsInt8Array(): Int8Array {
        val result = Int8Array(this.size)
        for (i in this.indices) {
            result[i] = this[i]
        }
        return result
    }
}
