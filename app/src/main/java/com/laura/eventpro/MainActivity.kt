package com.laura.eventpro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.laura.eventpro.navigation.EventProApp
import com.laura.eventpro.ui.theme.EventProTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EventProTheme {
                EventProApp()
            }
        }
    }
}
