package com.example.rieti.ui.screens



import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rieti.ui.theme.BrandPink
import com.example.rieti.ui.theme.LightBlueBoxes
import com.example.rieti.ui.theme.LightBlueDetails
import com.example.rieti.ui.theme.LightPinkPrincipal



@Composable
fun InicioPantalla1(
    onIniciarSesion: () -> Unit,
    onCrearCuenta: () -> Unit
){
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = LightPinkPrincipal
        ) {
            Text(
                text = "RIETI",
                modifier = Modifier.padding(vertical = 24.dp),
                fontSize = 36.sp,
                textAlign = TextAlign.Center
            )
        }
        Spacer(
            modifier = Modifier.height(25.dp)
        )

        Text(
            text = "Reporta cualquier forma\nde trabajo infantil",
            fontSize = 23.sp,
            textAlign = TextAlign.Center
        )

        HorizontalDivider(
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = 17.dp),
            thickness = 2.dp,
            color = BrandPink
        )

        Spacer(
            modifier = Modifier.height(35.dp)
        )

        Button(
            onClick = onIniciarSesion,
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = 25.dp)
                .height(70.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = LightBlueBoxes
        )

        ) {
            Text(
                text = "INICIAR SESIÓN",
                fontSize = 22.sp
            )
        }

        Spacer(
            modifier = Modifier.height(38.dp)
        )
        Button(
            onClick = onCrearCuenta,
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = 25.dp)
                .height(70.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = LightBlueDetails
            )

        ) {
            Text(
                text = "CREAR CUENTA",
                fontSize = 22.sp
            )
        }

        Spacer(
            modifier = Modifier.weight(1f)
        )

        HorizontalDivider(
            thickness = 2.dp,
            color = BrandPink
        )
        Text(
            text = "Modo oscuro",
            modifier = Modifier.padding(vertical = 18.dp),
            fontSize = 20.sp
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun InicioPantalla1Preview() {
    InicioPantalla1(
        onIniciarSesion = {},
        onCrearCuenta = {}
    )
}