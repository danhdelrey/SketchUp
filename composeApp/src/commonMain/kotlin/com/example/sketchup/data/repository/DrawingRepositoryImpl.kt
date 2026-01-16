package com.example.sketchup.data.repository

import com.example.sketchup.domain.model.DrawingPath
import com.example.sketchup.domain.repository.DrawingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Implementation of DrawingRepository.
 * Manages drawing paths with undo/redo functionality using in-memory storage.
 */
class DrawingRepositoryImpl : DrawingRepository {
    // Main drawing stack
    private val _paths = MutableStateFlow<List<DrawingPath>>(emptyList())
    override val paths: StateFlow<List<DrawingPath>> = _paths.asStateFlow()

    // Redo stack for undone operations
    private val redoStack = ArrayDeque<DrawingPath>()

    override fun addPath(path: DrawingPath) {
        _paths.update { it + path }
        redoStack.clear() // Clear redo history when new path is added
    }

    override fun undo() {
        _paths.value.lastOrNull()?.let { lastPath ->
            redoStack.addLast(lastPath)
            _paths.update { it.dropLast(1) }
        }
    }

    override fun redo() {
        redoStack.removeLastOrNull()?.let { path ->
            _paths.update { it + path }
        }
    }

    override fun clear() {
        _paths.update { emptyList() }
        redoStack.clear()
    }

    override fun canUndo(): Boolean = _paths.value.isNotEmpty()

    override fun canRedo(): Boolean = redoStack.isNotEmpty()
}
