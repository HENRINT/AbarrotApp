package com.example.abarrotapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.abarrotapp.ui.navigation.AbarrotesNavGraph
import com.example.abarrotapp.ui.theme.AbarrotAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AbarrotAppTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AbarrotesNavGraph()
                }
            }
        }
    }
}
