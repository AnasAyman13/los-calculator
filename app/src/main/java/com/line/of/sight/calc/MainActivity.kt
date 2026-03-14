package com.line.of.sight.calc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.line.of.sight.calc.ui.nav.AppNavGraph
import com.line.of.sight.calc.ui.theme.LOSTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LOSTheme {
                AppNavGraph()
            }
        }
    }
}