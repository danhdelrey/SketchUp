package com.example.sketchup.domain.model
}
    MARKER
    HIGHLIGHTER,
    ERASER,
    NORMAL,
enum class BrushType {

}
    }
        )
            type = BrushType.NORMAL
            opacity = 1f,
            color = Color.Black,
            size = 10f,
        val DEFAULT = Brush(
    companion object {
) {
    val type: BrushType = BrushType.NORMAL
    val opacity: Float = 1f,
    val color: Color,
    val size: Float,
data class Brush(
 */
 * Đây là business entity, không phụ thuộc vào UI framework
 * Domain model cho Brush - đại diện cho cọ vẽ
/**

import androidx.compose.ui.graphics.Color


