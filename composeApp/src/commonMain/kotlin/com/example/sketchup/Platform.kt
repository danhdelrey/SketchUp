package com.example.sketchup

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform