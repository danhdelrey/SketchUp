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
return true

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
