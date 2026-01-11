package com.example.sketchup.data.repository

import com.example.sketchup.data.model.DrawingPath
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

interface DrawingRepository {
    val paths: StateFlow<List<DrawingPath>>
    fun addPath(path: DrawingPath)
    fun undo()
    fun redo()
    fun clear()
}

class DrawingRepositoryImpl : DrawingRepository {
    // Stack chính để vẽ
    private val _paths = MutableStateFlow<List<DrawingPath>>(emptyList())
    override val paths: StateFlow<List<DrawingPath>> = _paths.asStateFlow()

    // Stack để Redo
    private val redoStack = ArrayDeque<DrawingPath>()

    override fun addPath(path: DrawingPath) {
        _paths.update { it + path }
        redoStack.clear() // Khi vẽ mới thì mất lịch sử redo
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
}