package com.example.rieti.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.rieti.ui.theme.RIETITheme
import com.example.rieti.view.AppNavigation

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            RIETITheme {

                AppNavigation()
            }
        }
    }
}