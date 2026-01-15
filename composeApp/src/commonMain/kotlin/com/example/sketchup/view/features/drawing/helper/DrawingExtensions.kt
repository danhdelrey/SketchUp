package com.example.sketchup.view.features.drawing.helper

import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import com.example.sketchup.data.model.DrawingPath

// Extension function để vẽ DrawingPath lên Canvas
fun DrawScope.drawPathCompat(drawingPath: DrawingPath) {
    if (drawingPath.points.size > 1) {
        val path = androidx.compose.ui.graphics.Path().apply {
            moveTo(drawingPath.points.first().x, drawingPath.points.first().y)
            for (i in 1 until drawingPath.points.size) {
                lineTo(drawingPath.points[i].x, drawingPath.points[i].y)
            }
        }
        drawPath(
            path = path,
            color = drawingPath.color,
            style = Stroke(
                width = drawingPath.strokeWidth,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )
    } else if (drawingPath.points.size == 1) {
        drawPoints(
            points = drawingPath.points,
            pointMode = androidx.compose.ui.graphics.PointMode.Points,
            color = drawingPath.color,
            strokeWidth = drawingPath.strokeWidth,
            cap = StrokeCap.Round
        )
    }
}