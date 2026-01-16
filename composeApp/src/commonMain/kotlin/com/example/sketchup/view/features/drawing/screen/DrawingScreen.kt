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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Redo
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import com.example.sketchup.core.utils.toPngByteArray
import com.example.sketchup.view.common.component.CustomIconButton
import com.example.sketchup.view.features.drawing.component.BrushSizeSlider
import com.example.sketchup.view.features.drawing.event.DrawingEvent
import com.example.sketchup.view.features.drawing.helper.drawPathCompat
import com.example.sketchup.view.features.drawing.screenModel.DrawingScreenModel
import kotlinx.coroutines.launch

class DrawingScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        // Inject ScreenModel bằng Koin qua Voyager
        val screenModel = koinScreenModel<DrawingScreenModel>()
        val state by screenModel.state.collectAsState()
        val brushSize by screenModel.currentBrushSize.collectAsState()

        // Tính năng mới của Compose 1.7+ để chụp ảnh màn hình
        val graphicsLayer = rememberGraphicsLayer()
        val coroutineScope = rememberCoroutineScope()

        // 1. Tạo State cho Snackbar
        val snackbarHostState = remember { SnackbarHostState() }

        // 2. Lắng nghe thông báo từ ViewModel
        LaunchedEffect(Unit) {
            screenModel.messageFlow.collect { message ->
                // Hiển thị snackbar (hàm này suspend, nó sẽ xếp hàng nếu có nhiều tin nhắn)
                snackbarHostState.showSnackbar(message)
            }
        }



        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
        ) { padding ->
            // Outer container that fills the screen with a gray background
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(Color(0xFFE0E0E0)), // Light gray background
                contentAlignment = Alignment.Center
            ) {
                // Fixed-size canvas container
                Box(
                    modifier = Modifier
                        .size(800.dp, 600.dp) // Fixed canvas size: 800x600
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
                            .size(800.dp, 600.dp) // Match the container size
                            .graphicsLayer(alpha = 0.99f) // Enable alpha compositing for BlendMode.Clear
                            .pointerInput(Unit) {
                                detectDragGestures(
                                    onDragStart = { offset ->
                                        // Clamp the offset to canvas bounds
                                        val clampedOffset = Offset(
                                            x = offset.x.coerceIn(0f, size.width.toFloat()),
                                            y = offset.y.coerceIn(0f, size.height.toFloat())
                                        )
                                        screenModel.onEvent(DrawingEvent.StartDraw(clampedOffset))
                                    },
                                    onDrag = { change, _ ->
                                        // Clamp the position to canvas bounds
                                        val clampedPosition = Offset(
                                            x = change.position.x.coerceIn(0f, size.width.toFloat()),
                                            y = change.position.y.coerceIn(0f, size.height.toFloat())
                                        )
                                        screenModel.onEvent(DrawingEvent.UpdateDraw(clampedPosition))
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

                    // Vẽ vòng tròn chỉ thị vùng xóa khi ở chế độ erase và đang chạm
                    if (state.isEraseMode && state.currentTouchPosition != null) {
                        drawCircle(
                            color = Color.Gray.copy(alpha = 0.5f),
                            radius = brushSize / 2f,
                            center = state.currentTouchPosition!!,
                            style = Stroke(width = 2f)
                        )
                    }
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
                        icon = Icons.Default.Save
                    ) {
                        coroutineScope.launch {
                            try {
                                // 1. Chụp ảnh từ graphicsLayer
                                val bitmap = graphicsLayer.toImageBitmap()

                                // 2. Chuyển thành ByteArray (dùng hàm tiện ích ở Bước 1)
                                val bytes = bitmap.toPngByteArray()

                                // 3. Gửi xuống ViewModel
                                screenModel.onEvent(DrawingEvent.SavePng(bytes))
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                    Spacer(Modifier.width(10.dp))
                    CustomIconButton(
                        icon = if (state.isEraseMode) Icons.Default.Create else Icons.Default.Delete
                    ) {
                        screenModel.onEvent(DrawingEvent.ToggleEraseMode)
                    }
                    Spacer(Modifier.width(10.dp))

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
