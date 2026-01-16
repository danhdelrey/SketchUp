package com.example.sketchup

import android.app.Application
import android.content.Context
import com.example.sketchup.di.initKoin
import org.koin.android.ext.koin.androidContext

class AndroidApp : Application() {
    override fun onCreate() {
        super.onCreate()
        appContext = this

        // Initialize Koin with Android context
        initKoin {
            androidContext(this@AndroidApp)
        }
    }

    companion object {
        lateinit var appContext: Context
    }
}