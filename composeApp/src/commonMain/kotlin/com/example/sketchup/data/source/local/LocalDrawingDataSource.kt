package com.example.sketchup.data.source.local

import com.example.sketchup.domain.model.Drawing

/**
 * Local data source interface for drawings
 * Platform-specific implementations can use:
 * - SQLite/Room (Android)
 * - CoreData/Realm (iOS)  
 * - IndexedDB (Web)
 * - File system (Desktop)
 */
interface LocalDrawingDataSource {
    suspend fun saveDrawing(drawing: Drawing): Result<Unit>
    suspend fun loadDrawing(id: String): Result<Drawing>
    suspend fun getAllDrawings(): Result<List<Drawing>>
    suspend fun deleteDrawing(id: String): Result<Unit>
}

/**
 * Expect/Actual pattern for platform-specific data source
 */
expect class PlatformDrawingDataSource() : LocalDrawingDataSource

