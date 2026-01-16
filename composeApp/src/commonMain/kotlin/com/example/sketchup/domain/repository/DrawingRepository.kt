package com.example.sketchup.domain.repository

import com.example.sketchup.domain.model.DrawingPath
import kotlinx.coroutines.flow.StateFlow

/**
 * Repository interface for drawing operations.
 * This is part of the domain layer and defines the contract
 * that data layer implementations must follow.
 */
interface DrawingRepository {
    /**
     * Observable list of all drawing paths.
     */
    val paths: StateFlow<List<DrawingPath>>

    /**
     * Adds a new path to the drawing.
     */
    fun addPath(path: DrawingPath)

    /**
     * Undoes the last drawing action.
     */
    fun undo()

    /**
     * Redoes the last undone action.
     */
    fun redo()

    /**
     * Clears all drawing paths.
     */
    fun clear()

    /**
     * Checks if undo is available.
     */
    fun canUndo(): Boolean

    /**
     * Checks if redo is available.
     */
    fun canRedo(): Boolean
}
