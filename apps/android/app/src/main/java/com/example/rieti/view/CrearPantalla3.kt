package com.example.rieti.view

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rieti.ui.theme.AppTheme
import com.example.rieti.ui.theme.RIETITheme

@Composable
fun CrearPantalla3(
    onLoginClick: () -> Unit,
    onCreateAccountClick: () -> Unit,
) {
    var darkTheme by remember {
        mutableStateOf(false)
    }

    var correo by remember {
        mutableStateOf("")
    }

    var contrasena by remember {
        mutableStateOf("")
    }

    var confirmarContrasena by remember {
        mutableStateOf("")
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
                    modifier = Modifier.fillMaxSize()
                ) {

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = colors.titles
                    ) {
                        Text(
                            text = "RIETI",
                            modifier = Modifier.padding(vertical = 50.dp),
                            fontSize = 55.sp,
                            fontFamily = FontFamily.Serif,
                            textAlign = TextAlign.Center,
                            color = colors.text
                        )
                    }

                    Spacer(
                        modifier = Modifier.height(14.dp)
                    )

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = colors.surface
                    ) {
                        Text(
                            text = "Crear Cuenta",
                            fontSize = 30.sp,
                            fontFamily = FontFamily.Serif,
                            textAlign = TextAlign.Center,
                            color = colors.titles
                        )
                    }

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    HorizontalDivider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 80.dp),
                        thickness = 2.dp,
                        color = colors.pinkPrincipal
                    )

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    Text(
                        text = "Correo",
                        modifier = Modifier.padding(
                            vertical = 8.dp,
                            horizontal = 14.dp
                        ),
                        fontSize = 16.sp,
                        fontFamily = FontFamily.Serif,
                        textAlign = TextAlign.Left,
                        color = colors.text
                    )

                    OutlinedTextField(
                        value = correo,
                        onValueChange = {
                            correo = it
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 25.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email
                        ),
                        colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                            focusedTextColor = colors.text,
                            unfocusedTextColor = colors.text,
                            cursorColor = colors.pinkPrincipal,
                            focusedBorderColor = colors.pinkPrincipal,
                            unfocusedBorderColor = colors.blueSecondary
                        )
                    )

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    Text(
                        text = "Contraseña",
                        modifier = Modifier.padding(
                            vertical = 8.dp,
                            horizontal = 14.dp
                        ),
                        fontSize = 16.sp,
                        fontFamily = FontFamily.Serif,
                        textAlign = TextAlign.Left,
                        color = colors.text
                    )

                    OutlinedTextField(
                        value = contrasena,
                        onValueChange = {
                            contrasena = it
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 25.dp),
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password
                        ),
                        colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                            focusedTextColor = colors.text,
                            unfocusedTextColor = colors.text,
                            cursorColor = colors.pinkPrincipal,
                            focusedBorderColor = colors.pinkPrincipal,
                            unfocusedBorderColor = colors.blueSecondary
                        )
                    )

                    Text(
                        text = "Confirmar Contraseña",
                        modifier = Modifier.padding(
                            vertical = 8.dp,
                            horizontal = 14.dp
                        ),
                        fontSize = 16.sp,
                        fontFamily = FontFamily.Serif,
                        textAlign = TextAlign.Left,
                        color = colors.text
                    )

                    OutlinedTextField(
                        value = confirmarContrasena,
                        onValueChange = {
                            confirmarContrasena = it
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 25.dp),
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password
                        ),
                        colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                            focusedTextColor = colors.text,
                            unfocusedTextColor = colors.text,
                            cursorColor = colors.pinkPrincipal,
                            focusedBorderColor = colors.pinkPrincipal,
                            unfocusedBorderColor = colors.blueSecondary
                        )
                    )

                    Spacer(
                        modifier = Modifier.height(28.dp)
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
                            text = "CREAR CUENTA",
                            fontSize = 30.sp,
                            fontFamily = FontFamily.Serif
                        )
                    }

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    Button(
                        onClick = onCreateAccountClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 50.dp)
                            .height(60.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colors.blueDetails,
                            contentColor = colors.textTyping
                        )
                    ) {
                        Text(
                            text = "INICIAR SESIÓN",
                            fontSize = 24.sp,
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

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = colors.surface
                    ) {
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
                            color = colors.text,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun CrearPantalla3DarkPreview() {
    RIETITheme(
        darkTheme = true
    ) {
        CrearPantalla3(
            onLoginClick = {},
            onCreateAccountClick = {}
        )
    }
}