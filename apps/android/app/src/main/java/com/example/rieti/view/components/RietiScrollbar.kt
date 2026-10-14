package com.example.rieti.view.components

import androidx.compose.foundation.ScrollState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/** Indicador fino de progreso de desplazamiento a la derecha del formulario. */
fun Modifier.rietiScrollbar(state: ScrollState): Modifier = this.drawWithContent {
    drawContent()
    if (state.maxValue > 0) {
        val trackHeight = size.height
        val thumbHeight = (trackHeight * trackHeight / (trackHeight + state.maxValue))
            .coerceIn(28.dp.toPx(), trackHeight)
        val top = (state.value.toFloat() / state.maxValue) * (trackHeight - thumbHeight)
        val width = 3.dp.toPx()
        drawRoundRect(
            color = Color(0xFFD9165D).copy(alpha = 0.65f),
            topLeft = Offset(size.width - width - 2.dp.toPx(), top),
            size = Size(width, thumbHeight),
            cornerRadius = CornerRadius(width / 2)
        )
    }
}
