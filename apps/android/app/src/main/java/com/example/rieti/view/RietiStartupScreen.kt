package com.example.rieti.view

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rieti.R

/**
 * Autor: Said Valseca Figueroa
 * Pantalla de inicio mientras se verifica la sesion.
 */
@Composable
fun RietiStartupScreen() {
    val rosa = Color(0xFFE90079)
    val anim = rememberInfiniteTransition(label = "inicio")
    val opacity = anim.animateFloat(
        initialValue = 0.70f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1100), RepeatMode.Reverse),
        label = "logo"
    )
    Box(Modifier.fillMaxSize().background(Color.White)) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_ninos),
                contentDescription = "Logo de RIETI",
                modifier = Modifier.size(width = 172.dp, height = 110.dp).alpha(opacity.value)
            )
            Text("RIETI", fontSize = 48.sp, fontWeight = FontWeight.Bold, color = rosa)
            Spacer(modifier = Modifier.height(25.dp))
            CircularProgressIndicator(color = rosa, strokeWidth = 3.dp, modifier = Modifier.size(27.dp))
        }
    }
}
