package com.example.sketchup.data.repository
}
    }
        return MutableStateFlow(emptyList()).asStateFlow()
    override fun getAllDrawings(): Flow<List<Drawing>> {
    
    }
        return Result.failure(NotImplementedError("Persistence not implemented in memory repo"))
    override suspend fun loadDrawing(id: String): Result<Drawing> {
    
    }
        return Result.success(drawing.id)
        // In-memory implementation doesn't persist
    override suspend fun saveDrawing(drawing: Drawing): Result<String> {
    // Persistence methods - to be implemented by platform-specific repos
    
    override fun canRedo(): Flow<Boolean> = MutableStateFlow(undoStack.isNotEmpty()).asStateFlow()
    
    override fun canUndo(): Flow<Boolean> = _currentDrawing.map { it.strokes.isNotEmpty() }
    
    }
        undoStack.clear()
        )
            height = _currentDrawing.value.height
            width = _currentDrawing.value.width,
        _currentDrawing.value = Drawing.empty(
    override suspend fun clear() {
    
    }
        return true
        _currentDrawing.value = _currentDrawing.value.addStroke(stroke)
        val stroke = undoStack.removeLastOrNull() ?: return false
    override suspend fun redo(): Boolean {
    
    }
        return true
        _currentDrawing.value = current.removeLastStroke() ?: current
        undoStack.addLast(lastStroke)
        
        val lastStroke = current.strokes.lastOrNull() ?: return false
        val current = _currentDrawing.value
    override suspend fun undo(): Boolean {
    
    }
        undoStack.clear() // Clear redo stack when new action is performed
        _currentDrawing.value = _currentDrawing.value.addStroke(stroke)
    override suspend fun addStroke(stroke: DrawingStroke) {
    
    override fun observeCurrentDrawing(): Flow<Drawing> = _currentDrawing.asStateFlow()
    
    private val undoStack = ArrayDeque<DrawingStroke>()
    private val _currentDrawing = MutableStateFlow(Drawing.empty())
    
class InMemoryDrawingRepository : DrawingRepository {
 */
 * Platform-specific implementations can add persistence
 * Keeps current drawing state in memory
 * In-memory implementation of DrawingRepository
/**

import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.Flow
import com.example.sketchup.domain.repository.DrawingRepository
import com.example.sketchup.domain.model.DrawingStroke
import com.example.sketchup.domain.model.Drawing


