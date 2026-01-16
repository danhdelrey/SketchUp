package com.example.sketchup.domain.usecase

import com.example.sketchup.domain.repository.DrawingRepository

/**
 * Use case for undoing the last drawing action.
 */
class UndoDrawingUseCase(
    private val repository: DrawingRepository
) {
    operator fun invoke() {
        if (repository.canUndo()) {
            repository.undo()
        }
    }
}
