package com.example.sketchup.domain.model

/**
 * Domain model cho toàn bộ bức vẽ
 * Business logic model, không phụ thuộc vào platform
 */
data class Drawing(
    val id: String,
    val strokes: List<DrawingStroke>,
    val width: Int,
    val height: Int,
    val backgroundColor: androidx.compose.ui.graphics.Color,
    val createdAt: Long,
    val modifiedAt: Long
) {
    fun addStroke(stroke: DrawingStroke): Drawing {
        return copy(
            strokes = strokes + stroke,
            modifiedAt = System.currentTimeMillis()
        )
    }

    fun removeLastStroke(): Drawing? {
        if (strokes.isEmpty()) return null
        return copy(
            strokes = strokes.dropLast(1),
            modifiedAt = System.currentTimeMillis()
        )
    }

    companion object {
        fun empty(width: Int = 800, height: Int = 600) = Drawing(
            id = generateId(),
            strokes = emptyList(),
            width = width,
            height = height,
            backgroundColor = androidx.compose.ui.graphics.Color.White,
            createdAt = System.currentTimeMillis(),
            modifiedAt = System.currentTimeMillis()
        )

        private fun generateId() = "drawing_${System.currentTimeMillis()}"
    }
}

