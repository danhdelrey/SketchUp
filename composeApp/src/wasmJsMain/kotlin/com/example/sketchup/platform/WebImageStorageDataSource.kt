import com.example.sketchup.data.source.local.ImageStorageDataSource
import kotlinx.browser.document
import org.w3c.dom.HTMLAnchorElement
import org.w3c.dom.url.URL
import org.w3c.files.Blob
import org.w3c.files.BlobPropertyBag
import org.khronos.webgl.Uint8Array      // ĐỔI SANG Uint8Array
import org.khronos.webgl.get
import org.khronos.webgl.set
import kotlin.js.JsAny
import kotlin.js.JsArray
import kotlin.js.set

class WebImageStorageDataSource : ImageStorageDataSource {

    @OptIn(kotlin.js.ExperimentalJsExport::class)
    override suspend fun saveImage(imageData: ByteArray, fileName: String): Boolean {
        return try {
            // 1. Chuyển ByteArray sang Uint8Array (BẮT BUỘC cho hình ảnh)
            val jsBytes = imageData.toJsUint8Array()

            // 2. Đảm bảo tên file có đuôi .png nếu chưa có
            val finalFileName = if (!fileName.contains(".")) {
                "$fileName.png"
            } else {
                fileName
            }

            // 3. Xác định MIME type
            val mimeType = when {
                finalFileName.endsWith(".png", true) -> "image/png"
                finalFileName.endsWith(".jpg", true) || finalFileName.endsWith(".jpeg", true) -> "image/jpeg"
                else -> "application/octet-stream"
            }

            // 4. Tạo JsArray cho Blob
            val blobParts = JsArray<JsAny?>()
            blobParts[0] = jsBytes

            val blob = Blob(blobParts, BlobPropertyBag(type = mimeType))

            // 5. Tải xuống
            val url = URL.createObjectURL(blob)
            val anchor = document.createElement("a") as HTMLAnchorElement
            anchor.href = url
            anchor.download = finalFileName // Tên file lúc này chắc chắn có đuôi .png
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

    // --- SỬA HÀM NÀY SANG Uint8Array ---
    private fun ByteArray.toJsUint8Array(): Uint8Array {
        val result = Uint8Array(this.size)
        for (i in this.indices) {
            // Chuyển đổi an toàn từ Byte (có dấu) của Kotlin sang (không dấu) của JS
            result[i] = this[i]
        }
        return result
    }
}