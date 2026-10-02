
package com.example.rieti.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.lifecycleScope
import com.example.rieti.ui.theme.RIETITheme
import com.example.rieti.ui.theme.ThemePreferences
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var themePreferences: ThemePreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        themePreferences = ThemePreferences(
            applicationContext
        )

        setContent {

            val darkTheme by themePreferences
                .darkTheme
                .collectAsState(initial = false)

            RIETITheme(
                darkTheme = darkTheme
            ) {
                AppNavigation(
                    darkTheme = darkTheme,
                    onThemeChange = {
                        lifecycleScope.launch {
                            themePreferences.saveDarkTheme(
                                !darkTheme
                            )
                        }
                    }
                )
            }
        }
    }
}
