package com.example.sketchup.data.source.local
}
    }
        return Result.failure(NotImplementedError("iOS persistence not yet implemented"))
    override suspend fun deleteDrawing(id: String): Result<Unit> {
    
    }
        return Result.success(emptyList())
    override suspend fun getAllDrawings(): Result<List<Drawing>> {
    
    }
        return Result.failure(NotImplementedError("iOS persistence not yet implemented"))
    override suspend fun loadDrawing(id: String): Result<Drawing> {
    
    }
        return Result.failure(NotImplementedError("iOS persistence not yet implemented"))
    override suspend fun saveDrawing(drawing: Drawing): Result<Unit> {
    // TODO: Implement using CoreData or file storage
    
actual class PlatformDrawingDataSource : LocalDrawingDataSource {
 */
 * For now, just a stub implementation
 * iOS implementation using CoreData or UserDefaults
/**

import com.example.sketchup.domain.model.Drawing


