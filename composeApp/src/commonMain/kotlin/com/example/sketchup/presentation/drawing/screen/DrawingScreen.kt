package com.example.sketchup.presentation.drawing.screen

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
import com.example.sketchup.presentation.drawing.DrawingScreenModel
import com.example.sketchup.presentation.drawing.component.BrushSizeSlider
import com.example.sketchup.presentation.drawing.component.ColorPicker
import com.example.sketchup.presentation.drawing.helper.drawPathCompat
import com.example.sketchup.presentation.drawing.model.DrawingEffect
import com.example.sketchup.presentation.drawing.model.DrawingEvent
import com.example.sketchup.presentation.common.component.CustomIconButton
import com.example.sketchup.presentation.common.component.LoadingOverlay
import kotlinx.coroutines.launch

/**
 * Main drawing screen using Clean Architecture.
 * This screen is responsible only for UI rendering and user interaction.
 */
class DrawingScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val screenModel = koinScreenModel<DrawingScreenModel>()
        val state by screenModel.state.collectAsState()
        val brushSize by screenModel.currentBrushSize.collectAsState()
        val isLoading by screenModel.isLoading.collectAsState()

        val graphicsLayer = rememberGraphicsLayer()
        val coroutineScope = rememberCoroutineScope()

        val snackbarHostState = remember { SnackbarHostState() }

        // Handle side effects
        LaunchedEffect(Unit) {
            screenModel.effectFlow.collect { effect ->
                when (effect) {
                    is DrawingEffect.ShowMessage -> {
                        snackbarHostState.showSnackbar(effect.message)
                    }
                    is DrawingEffect.ShowError -> {
                        snackbarHostState.showSnackbar("Error: ${effect.error}")
                    }
                }
            }
        }

        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
        ) { padding ->
            Box {

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .background(Color(0xFFE0E0E0)),
                    contentAlignment = Alignment.Center
                ) {
                    // Canvas container
                    Box(
                        modifier = Modifier
                            .size(800.dp, 600.dp)
                            .background(Color.White)
                            .drawWithContent {
                                graphicsLayer.record {
                                    this@drawWithContent.drawContent()
                                }
                                drawLayer(graphicsLayer)
                            }
                    ) {
                        Canvas(
                            modifier = Modifier
                                .size(800.dp, 600.dp)
                                .graphicsLayer(alpha = 0.99f)
                                .pointerInput(Unit) {
                                    detectDragGestures(
                                        onDragStart = { offset ->
                                            val clampedOffset = Offset(
                                                x = offset.x.coerceIn(0f, size.width.toFloat()),
                                                y = offset.y.coerceIn(0f, size.height.toFloat())
                                            )
                                            screenModel.onEvent(DrawingEvent.StartDraw(clampedOffset))
                                        },
                                        onDrag = { change, _ ->
                                            val clampedPosition = Offset(
                                                x = change.position.x.coerceIn(
                                                    0f,
                                                    size.width.toFloat()
                                                ),
                                                y = change.position.y.coerceIn(
                                                    0f,
                                                    size.height.toFloat()
                                                )
                                            )
                                            screenModel.onEvent(
                                                DrawingEvent.UpdateDraw(
                                                    clampedPosition
                                                )
                                            )
                                        },
                                        onDragEnd = { screenModel.onEvent(DrawingEvent.EndDraw) }
                                    )
                                }
                        ) {
                            // Draw saved paths
                            state.paths.forEach { path ->
                                drawPathCompat(path)
                            }

                            // Draw current path being drawn
                            state.currentDrawingPath?.let { path ->
                                drawPathCompat(path)
                            }

                            // Draw erase indicator
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

                    // Color and Brush Size Pickers
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

                    // Action buttons
                    Row(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(10.dp)
                    ) {
                        CustomIconButton(
                            icon = Icons.Default.Save,
                            enabled = state.paths.isNotEmpty()
                        ) {
                            coroutineScope.launch {
                                try {
                                    val bitmap = graphicsLayer.toImageBitmap()
                                    val bytes = bitmap.toPngByteArray()
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
                            icon = Icons.Default.Undo,
                            enabled = state.canUndo
                        ) {
                            screenModel.onEvent(DrawingEvent.Undo)
                        }

                        Spacer(Modifier.width(10.dp))

                        CustomIconButton(
                            icon = Icons.Default.Redo,
                            enabled = state.canRedo
                        ) {
                            screenModel.onEvent(DrawingEvent.Redo)
                        }
                    }
                }
                LoadingOverlay(isLoading = isLoading)
            }
        }
    }
}
