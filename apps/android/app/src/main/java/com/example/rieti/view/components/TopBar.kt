
package com.example.rieti.view.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rieti.R
import com.example.rieti.ui.theme.AppTheme


// =====================================================================
// TOP BAR
// =====================================================================

/**
 * Componente de barra superior de la aplicación RIETI.
 *
 * Presenta la identidad visual de la aplicación
 * mediante el logotipo institucional y el texto "RIETI".
 *
 * Utiliza componentes de Jetpack Compose para organizar
 * horizontalmente el logotipo y el nombre de la aplicación
 * dentro de un contenedor de 72 dp de altura.
 *
 * Obtiene los colores desde AppTheme para adaptar
 * el fondo y el texto al tema visual seleccionado.
 *
 * Incorpora statusBarsPadding para respetar el espacio
 * ocupado por la barra de estado del sistema Android.
 *
 * El logotipo se obtiene desde los recursos gráficos
 * de la aplicación y se presenta con una altura de 45 dp.
 *
 * El nombre de RIETI utiliza una tipografía Serif,
 * tamaño de 24 sp y peso Medium.
 *
 * @param modifier Modificador opcional utilizado
 * para personalizar el contenedor de la barra superior.
 *
 * @author Equipo de desarrollo RIETI:
 * Lorena Antonio Monraga,
 * Eduardo Márquez López,
 * Ángel Adrik Merino Cedeño,
 * Marco Polo Peña Rivera,
 * Said Valseca Figueroa.
 */
@Composable
fun TopBar(
    modifier: Modifier = Modifier
) {

    val colors = AppTheme.colors

    val logoResource =
        R.drawable.logo_ninos


    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.surface)
            .statusBarsPadding()
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .background(colors.surface)
        ) {

            Row(
                modifier = Modifier
                    .fillMaxSize()

                    // =================================================
                    // POSICIÓN HORIZONTAL DEL CONTENIDO
                    //
                    // start controla qué tan lejos está el logo
                    // del borde izquierdo.
                    // =================================================

                    .padding(
                        start = 5.dp,
                        end = 20.dp
                    ),
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                // =====================================================
                // LOGO
                // =====================================================

                Image(
                    painter = painterResource(
                        id = logoResource
                    ),
                    contentDescription =
                        "Logo RIETI",
                    contentScale =
                        ContentScale.Fit,
                    modifier = Modifier
                        .height(45.dp)
                )


                // =====================================================
                // NOMBRE RIETI
                // =====================================================

                Text(
                    text = "RIETI",
                    color =
                        colors.text,
                    fontSize =
                        24.sp,
                    fontFamily =
                        FontFamily.Serif,
                    fontWeight =
                        FontWeight.Medium
                )


                Spacer(
                    modifier =
                        Modifier.weight(1f)
                )
            }
        }
    }
}
