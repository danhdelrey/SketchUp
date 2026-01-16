package com.example.sketchup.view.features.drawing.screenModel

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.sketchup.data.model.DrawingPath
import com.example.sketchup.data.repository.DrawingRepository
import com.example.sketchup.platform.ImageSaver
import com.example.sketchup.view.features.drawing.event.DrawingEvent
import com.example.sketchup.view.features.drawing.state.DrawingState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Clock

/**
 * ScreenModel for the Drawing screen.
 * Manages drawing state, handles user events, and coordinates with repository and image saver.
 */
class DrawingScreenModel(
    private val repository: DrawingRepository,
    private val imageSaver: ImageSaver
) : ScreenModel {

    // UI State combined from Repository and local state (current color, current stroke)
    private val _currentPathPoints = MutableStateFlow<List<Offset>>(emptyList())
    private val _currentColor = MutableStateFlow(Color.Black)
    private val _currentWidth = MutableStateFlow(10f)
    private val _isEraseMode = MutableStateFlow(false)
    private val _currentTouchPosition = MutableStateFlow<Offset?>(null)
    val currentBrushSize = _currentWidth.asStateFlow()

    // Channel for sending messages to UI (Side Effects)
    // Channel.BUFFERED keeps messages if UI hasn't received them yet
    private val _messageChannel = Channel<String>(Channel.BUFFERED)
    val messageFlow = _messageChannel.receiveAsFlow()

    // Kết hợp các luồng dữ liệu thành một State duy nhất cho UI
    val state = combine(
        repository.paths,
        _currentPathPoints,
        _currentColor,
        _isEraseMode,
        _currentTouchPosition,
    ) { paths, currentPoints, color, isEraseMode, touchPosition ->
        DrawingState(
            paths = paths,
            currentDrawingPath = if (currentPoints.isNotEmpty()) DrawingPath(
                currentPoints,
                color,
                _currentWidth.value,
                isEraser = isEraseMode
            ) else null,
            selectedColor = color,
            brushSize = _currentWidth.value,
            isEraseMode = isEraseMode,
            currentTouchPosition = touchPosition
        )
    }.stateIn(screenModelScope, SharingStarted.WhileSubscribed(5000), DrawingState())

    fun onEvent(event: DrawingEvent) {
        when (event) {
            is DrawingEvent.StartDraw -> {
                _currentPathPoints.update { listOf(event.offset) }
                _currentTouchPosition.update { event.offset }
            }
            is DrawingEvent.UpdateDraw -> {
                _currentPathPoints.update { it + event.offset }
                _currentTouchPosition.update { event.offset }
            }
            is DrawingEvent.EndDraw -> {
                val points = _currentPathPoints.value
                if (points.isNotEmpty()) {
                    repository.addPath(DrawingPath(
                        points,
                        _currentColor.value,
                        _currentWidth.value,
                        isEraser = _isEraseMode.value
                    ))
                    _currentPathPoints.update { emptyList() }
                }
                _currentTouchPosition.update { null }
            }
            is DrawingEvent.Undo -> repository.undo()
            is DrawingEvent.Redo -> repository.redo()
            is DrawingEvent.PickColor -> _currentColor.update { event.color }
            is DrawingEvent.ChangeBrushSize -> _currentWidth.update { event.size }
            is DrawingEvent.SavePng -> saveImage(event.bytes)
            is DrawingEvent.ToggleEraseMode -> _isEraseMode.update { !it }
        }
    }

    private fun saveImage(bytes: ByteArray) {
        screenModelScope.launch {
            // Notify user that save is in progress
            _messageChannel.send("Saving image...")

            try {
                val success = imageSaver.saveImage(bytes, "sketch_${Clock.System.now().nanosecondsOfSecond}")
                if (success) {
                    _messageChannel.send("Image saved successfully! ✅")
                } else {
                    _messageChannel.send("Failed to save image ❌")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _messageChannel.send("Error: ${e.message}")
            }
        }
    }
}

