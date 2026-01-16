package com.example.sketchup.platform

interface ImageSaver {
    // Hàm này nhận vào mảng byte của ảnh và tên file muốn lưu
    suspend fun saveImage(bytes: ByteArray, fileName: String): Boolean
}