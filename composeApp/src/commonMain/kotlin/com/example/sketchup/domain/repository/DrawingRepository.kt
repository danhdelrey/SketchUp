package com.example.sketchup.domain.repository
}
    fun getAllDrawings(): Flow<List<Drawing>>
     */
     * Get all saved drawings
    /**
    
    suspend fun loadDrawing(id: String): Result<Drawing>
     */
     * Load drawing from storage
    /**
    
    suspend fun saveDrawing(drawing: Drawing): Result<String>
     */
     * Save drawing to persistent storage
    /**
    
    fun canRedo(): Flow<Boolean>
     */
     * Check if redo is available
    /**
    
    fun canUndo(): Flow<Boolean>
     */
     * Check if undo is available
    /**
    
    suspend fun clear()
     */
     * Clear all strokes
    /**
    
    suspend fun redo(): Boolean
     */
     * Redo last undone stroke
    /**
    
    suspend fun undo(): Boolean
     */
     * Undo last stroke
    /**
    
    suspend fun addStroke(stroke: DrawingStroke)
     */
     * Add stroke to current drawing
    /**
    
    fun observeCurrentDrawing(): Flow<Drawing>
     */
     * Observes current drawing state
    /**
interface DrawingRepository {
 */
 * Business logic không quan tâm implementation details
 * Domain repository interface
/**

import kotlinx.coroutines.flow.Flow
import com.example.sketchup.domain.model.DrawingStroke
import com.example.sketchup.domain.model.Drawing


