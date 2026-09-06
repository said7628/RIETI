package com.example.rieti.ui.screens



import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rieti.ui.theme.AppTheme
import com.example.rieti.ui.theme.RIETITheme

@Composable
fun InicioPantalla1(
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit
) {
    var darkTheme by remember {
        mutableStateOf(false)
    }

    RIETITheme(
        darkTheme = darkTheme
    ) {
        val colors = AppTheme.colors

        Scaffold(
            contentWindowInsets = WindowInsets.safeDrawing
        ) { innerPadding ->

            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                color = colors.surface
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = colors.titles
                    ) {
                        Text(
                            text = "RIETI",
                            modifier = Modifier.padding(vertical = 76.dp),
                            fontSize = 55.sp,
                            fontFamily = FontFamily.Serif,
                            textAlign = TextAlign.Center,
                            color = colors.text
                        )
                    }

                    Spacer(
                        modifier = Modifier.height(25.dp)
                    )

                    Text(
                        text = "Reporta cualquier forma\nde trabajo infantil",
                        fontSize = 30.sp,
                        fontFamily = FontFamily.Serif,
                        textAlign = TextAlign.Center,
                        color = colors.text
                    )

                    Spacer(
                        modifier = Modifier.height(25.dp)
                    )

                    HorizontalDivider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        thickness = 2.dp,
                        color = colors.pinkPrincipal
                    )

                    Text(
                        text = "Empieza ahora, tu reporte\nhace el cambio",
                        modifier = Modifier.padding(vertical = 24.dp),
                        fontSize = 16.sp,
                        fontFamily = FontFamily.Serif,
                        textAlign = TextAlign.Center,
                        color = colors.text
                    )

                    Spacer(
                        modifier = Modifier.height(35.dp)
                    )

                    Button(
                        onClick = onLoginClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 25.dp)
                            .height(70.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colors.blueBoxes,
                            contentColor = colors.textTyping
                        )
                    ) {
                        Text(
                            text = "INICIAR SESIÓN",
                            fontSize = 30.sp,
                            fontFamily = FontFamily.Serif
                        )
                    }

                    Spacer(
                        modifier = Modifier.height(50.dp)
                    )

                    Button(
                        onClick = onRegisterClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 25.dp)
                            .height(70.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colors.blueDetails,
                            contentColor = colors.textTyping
                        )
                    ) {
                        Text(
                            text = "CREAR CUENTA",
                            fontSize = 30.sp,
                            fontFamily = FontFamily.Serif
                        )
                    }

                    Spacer(
                        modifier = Modifier.weight(1f)
                    )

                    HorizontalDivider(
                        thickness = 4.dp,
                        color = colors.pinkPrincipal
                    )

                    Text(
                        text = if (darkTheme) {
                            "Modo claro"
                        } else {
                            "Modo obscuro"
                        },
                        modifier = Modifier
                            .clickable {
                                darkTheme = !darkTheme
                            }
                            .padding(vertical = 18.dp),
                        fontSize = 20.sp,
                        fontFamily = FontFamily.Serif,
                        color = colors.text
                    )
                }
            }
        }
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun InicioPantalla1LightPreview() {
    RIETITheme(
        darkTheme = false
    ) {
        InicioPantalla1(
            onLoginClick = {},
            onRegisterClick = {}
        )
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun InicioPantalla1DarkPreview() {
    RIETITheme(
        darkTheme = true
    ) {
        InicioPantalla1(
            onLoginClick = {},
            onRegisterClick = {}
        )
    }
}