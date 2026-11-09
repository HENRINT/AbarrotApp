package com.example.abarrotapp

import android.app.Application
import com.example.abarrotapp.di.AppContainer
import com.example.abarrotapp.di.DefaultAppContainer

class AbarrotesApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }
}
