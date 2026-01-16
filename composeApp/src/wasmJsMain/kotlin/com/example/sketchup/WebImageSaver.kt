import com.example.sketchup.platform.ImageSaver
import kotlinx.browser.document
import org.khronos.webgl.Int8Array
import org.khronos.webgl.set
import org.w3c.dom.HTMLAnchorElement
import org.w3c.dom.url.URL
import org.w3c.files.Blob
import org.w3c.files.BlobPropertyBag

class WebImageSaver : ImageSaver {

    override suspend fun saveImage(bytes: ByteArray, fileName: String): Boolean {
        return try {
            // 1. Chuyển đổi ByteArray (Kotlin) sang Int8Array (JS TypedArray)
            val jsBytes = bytes.toJsInt8Array()

            // 2. Tạo JsArray (mảng JavaScript) để chứa Int8Array
            // Blob yêu cầu một mảng các phần tử (blob parts)
            val blobParts = JsArray<JsAny?>()
            blobParts[0] = jsBytes

            // 3. Tạo Blob từ JsArray
            val blob = Blob(blobParts, BlobPropertyBag(type = "image/png"))

            // 4. Tạo URL và thẻ <a> để tải
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

    // Hàm tiện ích chuyển ByteArray sang Int8Array
    private fun ByteArray.toJsInt8Array(): Int8Array {
        val result = Int8Array(this.size)
        for (i in this.indices) {
            result[i] = this[i]
        }
        return result
    }
}