package com.example.rieti.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

/** Autor: Said Valseca Figueroa. Mensajes y cargas de RIETI. */
object RietiFeedback {
    var loading by mutableStateOf<String?>(null)

    var message by mutableStateOf<String?>(null)
        private set
    var isError by mutableStateOf(false)
        private set
    var version by mutableIntStateOf(0)
        private set

    fun show(text: String, error: Boolean = false) {
        message = text
        isError = error
        version++
    }

    fun dismiss() { message = null }
}

@Composable
fun RietiMessageOverlay(modifier: Modifier = Modifier) {
    val message = RietiFeedback.message
    val version = RietiFeedback.version
    LaunchedEffect(version) {
        if (message != null) {
            delay(4200)
            if (version == RietiFeedback.version) RietiFeedback.dismiss()
        }
    }
    if (message != null) {
        Box(modifier.fillMaxSize().padding(horizontal = 22.dp, vertical = 34.dp), contentAlignment = Alignment.BottomCenter) {
            Surface(shape = RoundedCornerShape(20.dp), shadowElevation = 10.dp,
                color = if (RietiFeedback.isError) Color(0xFFFFEDF3) else Color(0xFFE6F8F5)) {
                Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(if (RietiFeedback.isError) "!" else "OK", color = if (RietiFeedback.isError) Color(0xFFE60059) else Color(0xFF008F81), fontWeight = FontWeight.Bold)
                    Spacer(Modifier.width(14.dp))
                    Text(message, modifier = Modifier.weight(1f), color = Color(0xFF231624), fontFamily = FontFamily.Serif, fontSize = 15.sp)
                }
            }
        }
    }
}

@Composable
fun RietiLoadingOverlay(message: String, modifier: Modifier = Modifier) {
    Box(modifier.fillMaxSize().background(Color(0x99000000)), contentAlignment = Alignment.Center) {
        Surface(shape = RoundedCornerShape(24.dp), color = Color(0xFFFFF5F8), shadowElevation = 12.dp) {
            Column(Modifier.padding(horizontal = 36.dp, vertical = 30.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator(color = Color(0xFF16BFAF), strokeWidth = 3.dp)
                Spacer(Modifier.height(18.dp))
                Text(message, color = Color(0xFFB60050), fontFamily = FontFamily.Serif, fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
            }
        }
    }
}
