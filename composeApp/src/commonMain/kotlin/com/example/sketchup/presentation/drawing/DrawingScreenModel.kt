package com.example.sketchup.presentation.drawing

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.sketchup.domain.model.DrawingPath
import com.example.sketchup.domain.repository.DrawingRepository
import com.example.sketchup.domain.usecase.*
import com.example.sketchup.presentation.drawing.model.DrawingEffect
import com.example.sketchup.presentation.drawing.model.DrawingEvent
import com.example.sketchup.presentation.drawing.model.DrawingState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ScreenModel for the Drawing screen following Clean Architecture principles.
 *
 * Responsibilities:
 * - Manages UI state
 * - Handles user events
 * - Coordinates use cases
 * - Provides side effects (messages, errors)
 */
class DrawingScreenModel(
    private val observeDrawingPathsUseCase: ObserveDrawingPathsUseCase,
    private val addDrawingPathUseCase: AddDrawingPathUseCase,
    private val undoDrawingUseCase: UndoDrawingUseCase,
    private val redoDrawingUseCase: RedoDrawingUseCase,
    private val clearDrawingUseCase: ClearDrawingUseCase,
    private val saveDrawingImageUseCase: SaveDrawingImageUseCase,
    private val drawingRepository: DrawingRepository
) : ScreenModel {

    // Local UI state
    private val _currentPathPoints = MutableStateFlow<List<Offset>>(emptyList())
    private val _currentColor = MutableStateFlow(Color.Black)
    private val _currentWidth = MutableStateFlow(10f)
    private val _isEraseMode = MutableStateFlow(false)
    private val _currentTouchPosition = MutableStateFlow<Offset?>(null)

    // Public observable brush size
    val currentBrushSize = _currentWidth.asStateFlow()

    // Side effects channel
    private val _effectChannel = Channel<DrawingEffect>(Channel.BUFFERED)
    val effectFlow = _effectChannel.receiveAsFlow()

    // Combined UI state
    val state = combine(
        observeDrawingPathsUseCase(),
        _currentPathPoints,
        _currentColor,
        _currentWidth,
        _isEraseMode,
        _currentTouchPosition,
    ) { flows ->
        val paths = flows[0] as List<DrawingPath>
        val currentPoints = flows[1] as List<Offset>
        val color = flows[2] as Color
        val width = flows[3] as Float
        val isEraseMode = flows[4] as Boolean
        val touchPosition = flows[5] as Offset?

        DrawingState(
            paths = paths,
            currentDrawingPath = if (currentPoints.isNotEmpty()) {
                DrawingPath(
                    points = currentPoints,
                    color = color,
                    strokeWidth = width,
                    isEraser = isEraseMode
                )
            } else null,
            selectedColor = color,
            brushSize = width,
            isEraseMode = isEraseMode,
            currentTouchPosition = touchPosition,
            canUndo = drawingRepository.canUndo(),
            canRedo = drawingRepository.canRedo()
        )
    }.stateIn(
        scope = screenModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DrawingState()
    )

    /**
     * Handles all user events from the UI.
     */
    fun onEvent(event: DrawingEvent) {
        when (event) {
            is DrawingEvent.StartDraw -> handleStartDraw(event.offset)
            is DrawingEvent.UpdateDraw -> handleUpdateDraw(event.offset)
            is DrawingEvent.EndDraw -> handleEndDraw()
            is DrawingEvent.Undo -> undoDrawingUseCase()
            is DrawingEvent.Redo -> redoDrawingUseCase()
            is DrawingEvent.PickColor -> handlePickColor(event.color)
            is DrawingEvent.ChangeBrushSize -> handleChangeBrushSize(event.size)
            is DrawingEvent.SavePng -> handleSaveImage(event.bytes)
            is DrawingEvent.ToggleEraseMode -> handleToggleEraseMode()
            is DrawingEvent.Clear -> handleClear()
        }
    }

    private fun handleStartDraw(offset: Offset) {
        _currentPathPoints.update { listOf(offset) }
        _currentTouchPosition.update { offset }
    }

    private fun handleUpdateDraw(offset: Offset) {
        _currentPathPoints.update { it + offset }
        _currentTouchPosition.update { offset }
    }

    private fun handleEndDraw() {
        val points = _currentPathPoints.value
        if (points.isNotEmpty()) {
            val path = DrawingPath(
                points = points,
                color = _currentColor.value,
                strokeWidth = _currentWidth.value,
                isEraser = _isEraseMode.value
            )
            addDrawingPathUseCase(path)
            _currentPathPoints.update { emptyList() }
        }
        _currentTouchPosition.update { null }
    }

    private fun handlePickColor(color: Color) {
        _currentColor.update { color }
        // Auto-disable erase mode when picking a color
        if (_isEraseMode.value) {
            _isEraseMode.update { false }
        }
    }

    private fun handleChangeBrushSize(size: Float) {
        _currentWidth.update { size }
    }

    private fun handleToggleEraseMode() {
        _isEraseMode.update { !it }
    }

    private fun handleClear() {
        clearDrawingUseCase()
        sendEffect(DrawingEffect.ShowMessage("Canvas cleared"))
    }

    private fun handleSaveImage(bytes: ByteArray) {
        screenModelScope.launch {
            sendEffect(DrawingEffect.ShowMessage("Saving image..."))

            val result = saveDrawingImageUseCase(bytes)

            result.fold(
                onSuccess = { message ->
                    sendEffect(DrawingEffect.ShowMessage(message))
                },
                onFailure = { error ->
                    sendEffect(DrawingEffect.ShowError(error.message ?: "Failed to save image"))
                }
            )
        }
    }

    private fun sendEffect(effect: DrawingEffect) {
        screenModelScope.launch {
            _effectChannel.send(effect)
        }
    }
}
