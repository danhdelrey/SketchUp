package com.example.sketchup.domain.usecase

import com.example.sketchup.domain.repository.DrawingRepository

/**
 * Use case for undo/redo operations
 */
class UndoRedoUseCase(
    private val repository: DrawingRepository
) {
    suspend fun undo(): Boolean = repository.undo()
    
    suspend fun redo(): Boolean = repository.redo()
    
    fun canUndo() = repository.canUndo()
    
    fun canRedo() = repository.canRedo()
}

