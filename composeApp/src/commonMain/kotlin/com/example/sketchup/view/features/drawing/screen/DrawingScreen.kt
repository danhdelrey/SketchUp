package com.example.sketchup.view.features.drawing.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import com.example.sketchup.data.model.DrawingPath
import com.example.sketchup.view.features.drawing.event.DrawingEvent
import com.example.sketchup.view.features.drawing.screenModel.DrawingScreenModel
import kotlinx.coroutines.launch

class DrawingScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        // Inject ScreenModel bằng Koin qua Voyager
        val screenModel = getScreenModel<DrawingScreenModel>()
        val state by screenModel.state.collectAsState()
        val scope = rememberCoroutineScope()

        // Tính năng mới của Compose 1.7+ để chụp ảnh màn hình
        val graphicsLayer = rememberGraphicsLayer()

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("KMP Drawing") },
                    actions = {
                        IconButton(onClick = { screenModel.onEvent(DrawingEvent.Undo) }) {
                            Icon(Icons.Default.ArrowBack, "Undo")
                        }
                        IconButton(onClick = { screenModel.onEvent(DrawingEvent.Redo) }) {
                            Icon(Icons.Default.ArrowForward, "Redo")
                        }
                        IconButton(onClick = {
                            scope.launch {
                                // Capture bitmap từ graphicsLayer
                                val bitmap = graphicsLayer.toImageBitmap()
                                screenModel.onEvent(DrawingEvent.SavePng(bitmap))
                            }
                        }) {
                            Icon(Icons.Default.Share, "Save PNG")
                        }
                    }
                )
            },
            bottomBar = {
                ColorPicker(
                    selectedColor = state.selectedColor,
                    onColorSelected = { screenModel.onEvent(DrawingEvent.PickColor(it)) }
                )
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(Color.White)
                    // Gắn graphicsLayer vào container chứa Canvas
                    .drawWithContent {
                        graphicsLayer.record {
                            this@drawWithContent.drawContent()
                        }
                        drawLayer(graphicsLayer)
                    }
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) {
                            detectDragGestures(
                                onDragStart = { screenModel.onEvent(DrawingEvent.StartDraw(it)) },
                                onDrag = { change, _ ->
                                    screenModel.onEvent(DrawingEvent.UpdateDraw(change.position))
                                },
                                onDragEnd = { screenModel.onEvent(DrawingEvent.EndDraw) }
                            )
                        }
                ) {
                    // Vẽ các đường đã lưu
                    state.paths.forEach { path ->
                        drawPathCompat(path)
                    }
                    // Vẽ đường đang kéo (preview)
                    state.currentDrawingPath?.let { path ->
                        drawPathCompat(path)
                    }
                }
            }
        }
    }

    // Extension function để vẽ DrawingPath lên Canvas
    private fun DrawScope.drawPathCompat(drawingPath: DrawingPath) {
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
}

@Composable
fun ColorPicker(selectedColor: Color, onColorSelected: (Color) -> Unit) {
    val colors = listOf(Color.Black, Color.Red, Color.Blue, Color.Green, Color.Yellow)
    Row(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        colors.forEach { color ->
            Button(
                onClick = { onColorSelected(color) },
                modifier = Modifier.size(40.dp),
                border = if (color == selectedColor) BorderStroke(2.dp, Color.Gray) else null,
                colors = ButtonDefaults.buttonColors(
                    containerColor = color
                )
            ) {}
        }
    }
}