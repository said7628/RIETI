package com.example.rieti.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rieti.ui.theme.AppTheme

/**
 * Aviso integral de la App RIETI: transcripcion fiel del documento
 * "Aviso de privacidad integral App RIETI.pdf" (15/10/2026).
 *
 * El documento original es un BORRADOR para revision del socio formador:
 * contiene campos [pendientes] que deben validarse con la institucion.
 * Mostrar esta pantalla NO constituye consentimiento automatico.
 */
@Composable
fun RietiPrivacyNoticeScreen(onBackClick: () -> Unit) {
    val colors = AppTheme.colors
    val paragraphColor = colors.text
    Scaffold(containerColor = colors.surface) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            TextButton(onClick = onBackClick) {
                Text("← Volver al registro", color = colors.pinkPrincipal)
            }
            Spacer(Modifier.height(12.dp))
            Text(
                "Aviso de privacidad integral: App RIETI",
                color = paragraphColor,
                fontSize = 28.sp,
                lineHeight = 35.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(12.dp))
            Text(
                "BORRADOR PARA REVISIÓN DEL SOCIO FORMADOR · NO APROBADO PARA PUBLICACIÓN DEFINITIVA",
                color = colors.pinkPrincipal,
                fontSize = 13.sp,
                lineHeight = 19.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(12.dp))
            PrivacyParagraph("Borrador para revisión del socio formador · Proyecto RIETI · 15 de octubre de 2026")
            PrivacyParagraph("Este aviso describe cómo la app RIETI recaba, usa y protege los datos de quienes reportan posibles casos de trabajo infantil. Los campos entre [corchetes] deben ser completados o confirmados por la institución responsable.")
            PrivacyParagraph("Última actualización: 15 de octubre de 2026")

            PrivacySection(
                "1. ¿Quién es el responsable?",
                "[Nombre de la institución responsable], con domicilio en [domicilio de la institución], es responsable del tratamiento de sus datos personales, con fundamento en [la ley de protección de datos personales que le aplique]."
            )
            PrivacySection(
                "2. ¿Qué datos recabamos?",
                "• Del reporte: número y edades aproximadas de los niños, tipo y horario de trabajo, descripción de los hechos, dirección completa (municipio, colonia y código postal), ubicación geográfica exacta y fotografías o archivos.",
                "• Si elige \"con seguimiento\": correo electrónico y contraseña. La contraseña se guarda cifrada.",
                "• Si reporta sin cuenta o de forma anónima: no se solicita ni se guarda ningún dato que lo identifique.",
                "Datos sensibles: los datos y fotografías de menores se tratan como sensibles. Le pedimos no escribir nombres de menores o familiares en la descripción. Al subir fotografías, eliminamos sus metadatos (ubicación y modelo del dispositivo)."
            )
            PrivacySection(
                "3. ¿Para qué usamos sus datos?",
                "• Recibir, registrar y canalizar su reporte a las autoridades competentes.",
                "• Dar seguimiento al caso y notificarle avances (solo con cuenta).",
                "• Generar estadísticas y mapas de calor con datos agregados, sin datos personales ni fotografías.",
                "No los usaremos para fines distintos sin avisarle antes."
            )
            PrivacySection(
                "4. Consentimiento",
                "Al marcar la casilla \"He leído el aviso de privacidad\" y enviar el reporte, usted autoriza el tratamiento de los datos descritos, incluidos los sensibles."
            )
            PrivacySection(
                "5. ¿A quién se comparten?",
                "• Municipio correspondiente, Procuraduría Estatal de Protección de NNA y SIPINNA, para atender el caso.",
                "• Proveedores que almacenan o procesan datos: Amazon Web Services [país de los servidores por confirmar] y Google (mapas y ubicación).",
                "• Autoridades que lo requieran por mandato legal."
            )
            PrivacySection(
                "6. ¿Cuánto tiempo los conservamos?",
                "Hasta que el menor cumpla la mayoría de edad, calculado con la edad menor indicada y la fecha del reporte. Después, los datos se eliminan o anonimizan [plazo por definir]."
            )
            PrivacySection(
                "7. Derechos ARCO",
                "Puede solicitar acceso, rectificación, cancelación u oposición al correo [correo de contacto de la institución] o en el domicilio del punto 1. Responderemos en [plazo que marque la ley aplicable].",
                "• Los reportes anónimos no pueden vincularse a una persona, por lo que no es posible atender solicitudes sobre ellos.",
                "• La cancelación puede limitarse cuando la ley exija conservar los datos para proteger a los menores."
            )
            PrivacySection(
                "8. Limitar el uso o la divulgación",
                "Escríbanos al mismo correo para solicitarlo."
            )
            PrivacySection(
                "9. Seguridad",
                "Cifrado en las comunicaciones, acceso por roles, registro de actividad y fotografías almacenadas cifradas."
            )
            PrivacySection(
                "10. Cambios al aviso",
                "Publicaremos cualquier cambio en la app con su fecha. El aviso completo estará disponible en [enlace por definir]."
            )
            PrivacySection(
                "Puntos por confirmar antes de publicar",
                "Antes de usar este aviso con reportes reales, la institución debe:",
                "• Completar los campos marcados entre [corchetes].",
                "• Confirmar la ley aplicable, según sea una entidad pública o privada.",
                "• Revisar el texto con su área jurídica, en especial el consentimiento y los plazos.",
                "• Verificar que la app cumpla lo descrito, por ejemplo la eliminación de metadatos de las fotografías."
            )
            Spacer(Modifier.height(12.dp))
            Button(
                onClick = onBackClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = colors.pinkPrincipal)
            ) {
                Text("Volver al registro")
            }
            Spacer(Modifier.height(20.dp))
        }
    }
}

@Composable
private fun PrivacySection(title: String, vararg paragraphs: String) {
    val colors = AppTheme.colors
    Spacer(Modifier.height(22.dp))
    Text(
        text = title,
        color = colors.text,
        fontSize = 19.sp,
        fontWeight = FontWeight.Bold,
        lineHeight = 26.sp
    )
    Spacer(Modifier.height(10.dp))
    paragraphs.forEach { paragraph -> PrivacyParagraph(paragraph) }
}

@Composable
private fun PrivacyParagraph(text: String) {
    Text(
        text = text,
        color = AppTheme.colors.text,
        fontSize = 15.sp,
        lineHeight = 23.sp,
        modifier = Modifier.fillMaxWidth().padding(bottom = 9.dp)
    )
}
