package com.example.sketchup.presentation.drawing.helper

import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import com.example.sketchup.domain.model.DrawingPath

/**
 * Extension function to draw a DrawingPath on Canvas.
 * Handles both multi-point paths and single-point dots.
 */
fun DrawScope.drawPathCompat(drawingPath: DrawingPath) {
    if (drawingPath.points.size > 1) {
        val path = Path().apply {
            moveTo(drawingPath.points.first().x, drawingPath.points.first().y)
            for (i in 1 until drawingPath.points.size) {
                lineTo(drawingPath.points[i].x, drawingPath.points[i].y)
            }
        }
        drawPath(
            path = path,
            color = if (drawingPath.isEraser) Color.Transparent else drawingPath.color,
            style = Stroke(
                width = drawingPath.strokeWidth,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            ),
            alpha = drawingPath.opacity,
            blendMode = if (drawingPath.isEraser) BlendMode.Clear else BlendMode.SrcOver
        )
    } else if (drawingPath.points.size == 1) {
        drawPoints(
            points = drawingPath.points,
            pointMode = PointMode.Points,
            color = if (drawingPath.isEraser) Color.Transparent else drawingPath.color,
            strokeWidth = drawingPath.strokeWidth,
            alpha = drawingPath.opacity,
            cap = StrokeCap.Round,
            blendMode = if (drawingPath.isEraser) BlendMode.Clear else BlendMode.SrcOver
        )
    }
}
