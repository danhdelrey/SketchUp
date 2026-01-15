package com.example.sketchup.view.features.drawing.screen

import ColorPicker
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Redo
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import com.example.sketchup.view.common.component.CustomIconButton
import com.example.sketchup.view.features.drawing.component.BrushSizeSlider
import com.example.sketchup.view.features.drawing.event.DrawingEvent
import com.example.sketchup.view.features.drawing.helper.drawPathCompat
import com.example.sketchup.view.features.drawing.screenModel.DrawingScreenModel

class DrawingScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        // Inject ScreenModel bằng Koin qua Voyager
        val screenModel = koinScreenModel<DrawingScreenModel>()
        val state by screenModel.state.collectAsState()
        val scope = rememberCoroutineScope()
        val brushSize by screenModel.currentBrushSize.collectAsState()

        // Tính năng mới của Compose 1.7+ để chụp ảnh màn hình
        val graphicsLayer = rememberGraphicsLayer()

        Scaffold { padding ->
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

                // Color and Brush Size Pickers at bottom left
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(10.dp)
                ) {
                    ColorPicker(
                        initialColor = state.selectedColor,
                        onColorSelected = { color ->
                            screenModel.onEvent(DrawingEvent.PickColor(color))
                        }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    BrushSizeSlider(
                        color = state.selectedColor,
                        size = brushSize,
                        onSizeChange = { size ->
                            screenModel.onEvent(DrawingEvent.ChangeBrushSize(size))
                        }
                    )
                }

                // Undo/Redo buttons at top right
                Row(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(10.dp)
                ) {
                    CustomIconButton(
                        icon = Icons.Default.Undo
                    ) {
                        screenModel.onEvent(DrawingEvent.Undo)
                    }
                    Spacer(Modifier.width(10.dp))

                    CustomIconButton(
                        icon = Icons.Default.Redo
                    ) {
                        screenModel.onEvent(DrawingEvent.Redo)
                    }
                }
            }
        }
    }


}
