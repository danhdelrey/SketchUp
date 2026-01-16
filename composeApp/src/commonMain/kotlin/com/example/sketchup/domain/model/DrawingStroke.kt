package com.example.sketchup.domain.model

import androidx.compose.ui.geometry.Offset

/**
 * Domain model cho một nét vẽ
 * Tách biệt khỏi UI concerns
 */
data class DrawingStroke(
    val id: String,
    val points: List<Point>,
    val brush: Brush,
    val timestamp: Long
) {
    data class Point(
        val x: Float,
        val y: Float,
        val pressure: Float = 1f
    ) {
        fun toOffset() = Offset(x, y)
    }
    
    companion object {
        fun fromOffsets(
            id: String,
            offsets: List<Offset>,
            brush: Brush,
            timestamp: Long = System.currentTimeMillis()
        ) = DrawingStroke(
            id = id,
            points = offsets.map { Point(it.x, it.y) },
            brush = brush,
            timestamp = timestamp
        )
    }
}

