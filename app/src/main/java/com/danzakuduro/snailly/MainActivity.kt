package com.danzakuduro.snailly

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.danzakuduro.snailly.ui.navigation.AppNavigation
import com.danzakuduro.snailly.ui.theme.SnaillyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SnaillyTheme {
                AppNavigation()
            }
        }
    }
}