
package com.example.rieti.view.components

import androidx.compose.foundation.ScrollState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Agrega un indicador visual de progreso de desplazamiento
 * al extremo derecho de un componente de Jetpack Compose.
 *
 * Esta función de extensión de Modifier utiliza
 * drawWithContent para dibujar el contenido original
 * y posteriormente representar una barra vertical
 * que indica la posición actual del desplazamiento.
 *
 * El indicador solamente se dibuja cuando existe
 * contenido desplazable, es decir, cuando
 * state.maxValue es mayor que cero.
 *
 * Su altura se calcula proporcionalmente al espacio
 * visible y al desplazamiento máximo disponible,
 * manteniendo una altura mínima de 28 dp.
 *
 * La posición vertical se actualiza de acuerdo
 * con el valor actual de ScrollState.
 *
 * El indicador tiene un ancho de 3 dp, esquinas
 * redondeadas y utiliza el color rosa de RIETI
 * con una opacidad de 0.65.
 *
 * Esta función proporciona únicamente una referencia
 * visual del desplazamiento y no implementa
 * interacción directa para arrastrar la barra.
 *
 * @receiver Modifier al que se agrega el indicador
 * visual de desplazamiento.
 *
 * @param state Estado de desplazamiento utilizado
 * para consultar la posición actual y el valor
 * máximo de desplazamiento del contenido.
 *
 * @return Modifier que conserva el dibujo del contenido
 * original e incorpora el indicador de desplazamiento
 * cuando existe contenido desplazable.
 *
 * @author Equipo de desarrollo RIETI:
 * Lorena Antonio Monraga,
 * Eduardo Márquez López,
 * Ángel Adrik Merino Cedeño,
 * Marco Polo Peña Rivera,
 * Said Valseca Figueroa.
 */
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
