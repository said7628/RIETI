
package com.example.rieti.view

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rieti.data.local.ReportDatabaseProvider
import com.example.rieti.data.local.ReportDraftEntity
import com.example.rieti.ui.theme.AppTheme
import com.example.rieti.ui.theme.RIETITheme
import com.example.rieti.view.components.BottomBar
import com.example.rieti.view.components.TopBar
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

// =================================================================
// TIPO DE REPORTE SELECCIONADO
// =================================================================

/**
 * Representa las modalidades de reporte disponibles
 * para su selección desde la pantalla principal.
 *
 * ANONYMOUS identifica el reporte anónimo.
 * TRACKING identifica el reporte con seguimiento.
 *
 * Se utiliza como estado interno del modal
 * de selección de modalidad.
 */
private enum class HomeReportType {
    ANONYMOUS,
    TRACKING
}

// =================================================================
// HOME
// =================================================================

/**
 * Presenta la pantalla principal de la aplicación RIETI.
 *
 * Proporciona accesos para realizar reportes,
 * consultar su seguimiento, obtener orientación
 * y navegar hacia otras secciones de la aplicación.
 *
 * Consulta el borrador más reciente almacenado
 * mediante Room y, cuando existe, muestra una opción
 * para continuar su elaboración.
 *
 * Utiliza LocalInspectionMode para evitar consultar
 * la base de datos durante las vistas previas
 * de Android Studio.
 *
 * Integra un ModalBottomSheet que permite seleccionar
 * entre reporte anónimo y reporte con seguimiento.
 *
 * Adapta los colores de la interfaz al tema visual
 * activo mediante AppTheme.
 *
 * La navegación se delega a las funciones recibidas
 * como parámetros.
 *
 * @param onAnonymousReportClick Función ejecutada
 * cuando se elige la modalidad de reporte anónimo.
 * @param onReportClick Función ejecutada cuando
 * se elige la modalidad de reporte con seguimiento.
 * @param onAboutUsClick Función que permite navegar
 * hacia la sección "Quiénes somos".
 * @param onTrackingClick Función que permite navegar
 * hacia la consulta de seguimiento de reportes.
 * @param onOrientationClick Función que permite navegar
 * hacia la sección de orientación.
 * @param onSettingsClick Función que permite navegar
 * hacia la sección de configuración.
 * @param onContinueDraftClick Función que recibe
 * el identificador y la modalidad del borrador
 * seleccionado para continuar su elaboración.
 *
 * @author Equipo de desarrollo RIETI:
 * Lorena Antonio Monraga,
 * Eduardo Márquez López,
 * Ángel Adrik Merino Cedeño,
 * Marco Polo Peña Rivera,
 * Said Valseca Figueroa.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onAnonymousReportClick: () -> Unit,
    onReportClick: () -> Unit,
    onAboutUsClick: () -> Unit,
    onTrackingClick: () -> Unit,
    onOrientationClick: () -> Unit,
    onSettingsClick: () -> Unit,

    // TODO(NAVIGATION):
    // Conectar con la ruta que recupera un borrador.
    onContinueDraftClick: (String, String) -> Unit = { _, _ -> }
) {

    val colors = AppTheme.colors
    val isDarkTheme = AppTheme.isDarkTheme

    // =============================================================
    // BORRADORES GUARDADOS EN ROOM
    // =============================================================

    val context = LocalContext.current
    val isPreview = LocalInspectionMode.current

    val draftFlow: Flow<ReportDraftEntity?> =
        remember(context, isPreview) {
            if (isPreview) {
                flowOf(null)
            } else {
                ReportDatabaseProvider
                    .getDatabase(context.applicationContext)
                    .reportDraftDao()
                    .observeLatestDraft()
            }
        }

    val latestDraft by draftFlow.collectAsState(
        initial = null
    )

    // =============================================================
    // ESTADO DEL MODAL
    // =============================================================

    var showReportTypeSheet by remember {
        mutableStateOf(false)
    }

    val reportTypeSheetState =
        rememberModalBottomSheetState(
            skipPartiallyExpanded = true
        )

    // =============================================================
    // COLORES
    // =============================================================

    val mainPink =
        if (isDarkTheme) {
            Color(0xFFD71964)
        } else {
            Color(0xFFD9165D)
        }

    val secondaryCard =
        if (isDarkTheme) {
            Color(0xFF211D20)
        } else {
            Color(0xFFF9F9F9)
        }

    val secondaryBorder =
        if (isDarkTheme) {
            Color(0xFF4B4448)
        } else {
            Color(0xFFE1E1E1)
        }

    val primaryText =
        if (isDarkTheme) {
            Color(0xFFF8F5F6)
        } else {
            Color(0xFF123E5B)
        }

    val secondaryText =
        if (isDarkTheme) {
            Color(0xFFC8BEC3)
        } else {
            Color(0xFF587081)
        }

    val sheetBackground =
        if (isDarkTheme) {
            Color(0xFF171416)
        } else {
            Color(0xFFFFFBFC)
        }

    // =============================================================
    // PANTALLA
    // =============================================================

    Scaffold(
        containerColor = colors.surface,

        topBar = {
            TopBar()
        },

        bottomBar = {
            BottomBar(
                onHomeClick = {},
                onAboutUsClick = onAboutUsClick,
                onSettingsClick = onSettingsClick
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(colors.surface)
                .verticalScroll(rememberScrollState())
        ) {

            // =====================================================
            // PRESENTACIÓN
            // =====================================================

            HomeHeroSection(
                isDarkTheme = isDarkTheme,
                primaryText = primaryText,
                secondaryText = secondaryText
            )

            Spacer(
                modifier = Modifier.height(15.dp)
            )

            // =====================================================
            // CONTINUAR REPORTE SIN TERMINAR
            // =====================================================

            latestDraft?.let { draft ->

                val draftModeText =
                    when (draft.mode) {
                        "ANONYMOUS" -> "Reporte anónimo"
                        "TRACKING" -> "Reporte con seguimiento"
                        else -> "Reporte"
                    }

                HomeActionCard(
                    icon = Icons.Default.EditNote,

                    title = "Continuar reporte",

                    subtitle =
                        "$draftModeText · Paso ${draft.currentStep} de 4. " +
                                "Tienes un reporte sin terminar.",

                    backgroundColor = secondaryCard,
                    borderColor = mainPink,

                    iconColor = mainPink,
                    titleColor = primaryText,
                    subtitleColor = secondaryText,
                    arrowColor = mainPink,

                    onClick = {
                        onContinueDraftClick(
                            draft.id,
                            draft.mode
                        )
                    }
                )

                Spacer(
                    modifier = Modifier.height(20.dp)
                )
            }

            // =====================================================
            // REALIZAR REPORTE
            // =====================================================

            HomeActionCard(
                icon = Icons.Default.Description,

                title = "Realizar reporte",

                subtitle =
                    "Proporciona información sobre una posible situación de trabajo infantil.",

                backgroundColor = mainPink,
                borderColor = mainPink,

                iconColor = Color.White,
                titleColor = Color.White,
                subtitleColor = Color.White.copy(
                    alpha = 0.92f
                ),
                arrowColor = Color.White,

                onClick = {
                    showReportTypeSheet = true
                }
            )

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            // =====================================================
            // SEGUIMIENTO
            // =====================================================

            HomeActionCard(
                icon = Icons.Default.AccessTime,

                title = "Seguimiento",

                subtitle =
                    "Consulta el estado general de tu reporte.",

                backgroundColor = secondaryCard,
                borderColor = secondaryBorder,

                iconColor = primaryText,
                titleColor = primaryText,
                subtitleColor = secondaryText,
                arrowColor = primaryText,

                onClick = onTrackingClick
            )

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            // =====================================================
            // ORIENTACIÓN
            // =====================================================

            HomeActionCard(
                icon = Icons.AutoMirrored.Filled.MenuBook,

                title = "Orientación",

                subtitle =
                    "Conoce información útil sobre reportes y trabajo infantil.",

                backgroundColor = secondaryCard,
                borderColor = secondaryBorder,

                iconColor = primaryText,
                titleColor = primaryText,
                subtitleColor = secondaryText,
                arrowColor = primaryText,

                onClick = onOrientationClick
            )

            Spacer(
                modifier = Modifier.height(14.dp)
            )
        }
    }

    // =============================================================
    // MODAL PARA ELEGIR TIPO DE REPORTE
    // =============================================================

    if (showReportTypeSheet) {

        ModalBottomSheet(
            onDismissRequest = {
                showReportTypeSheet = false
            },

            sheetState = reportTypeSheetState,
            containerColor = sheetBackground,

            dragHandle = {
                Box(
                    modifier = Modifier
                        .padding(
                            top = 10.dp,
                            bottom = 4.dp
                        )
                        .width(34.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(50))
                        .background(
                            if (isDarkTheme) {
                                Color(0xFF8A8185)
                            } else {
                                Color(0xFFB7BEC4)
                            }
                        )
                )
            }
        ) {

            ReportTypeContent(
                isDarkTheme = isDarkTheme,

                onClose = {
                    showReportTypeSheet = false
                },

                onContinue = { selectedType ->

                    showReportTypeSheet = false

                    when (selectedType) {

                        HomeReportType.ANONYMOUS -> {
                            onAnonymousReportClick()
                        }

                        HomeReportType.TRACKING -> {
                            onReportClick()
                        }
                    }
                }
            )
        }
    }
}

// =================================================================
// PRESENTACIÓN SUPERIOR
// =================================================================

// =================================================================
// PRESENTACIÓN SUPERIOR
// =================================================================

/**
 * Construye la sección de presentación superior
 * de la pantalla principal de RIETI.
 *
 * Muestra el mensaje institucional "Por entornos seguros"
 * acompañado de un texto informativo sobre el propósito
 * de la aplicación.
 *
 * Incorpora elementos decorativos con transparencias
 * que se adaptan al tema visual seleccionado.
 *
 * @param isDarkTheme Indica si está activo el modo oscuro.
 * @param primaryText Color utilizado para el texto principal.
 * @param secondaryText Color utilizado para el texto descriptivo.
 */
@Composable
private fun HomeHeroSection(
    isDarkTheme: Boolean,
    primaryText: Color,
    secondaryText: Color
) {

    val pink =
        if (isDarkTheme) {
            Color(0xFFFF4A88)
        } else {
            Color(0xFFD9165D)
        }

    val teal = Color(0xFF16BFAF)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(238.dp)
    ) {

        // =========================================================
        // CUADRADOS DECORATIVOS TRANSPARENTES
        // =========================================================

        // Rosa superior
        Box(
            modifier = Modifier
                .size(155.dp)
                .align(Alignment.TopEnd)
                .offset(
                    x = 30.dp,
                    y = (-30).dp
                )
                .background(
                    color = pink.copy(
                        alpha = if (isDarkTheme) {
                            0.10f
                        } else {
                            0.07f
                        }
                    )
                )
        )

        // Rosa secundario
        Box(
            modifier = Modifier
                .size(102.dp)
                .align(Alignment.CenterEnd)
                .offset(
                    x = 20.dp,
                    y = (-14).dp
                )
                .background(
                    color = pink.copy(
                        alpha = if (isDarkTheme) {
                            0.07f
                        } else {
                            0.045f
                        }
                    )
                )
        )

        // Turquesa inferior
        Box(
            modifier = Modifier
                .size(120.dp)
                .align(Alignment.BottomEnd)
                .offset(
                    x = 32.dp,
                    y = 15.dp
                )
                .background(
                    color = teal.copy(
                        alpha = if (isDarkTheme) {
                            0.10f
                        } else {
                            0.07f
                        }
                    )
                )
        )

        // Turquesa pequeño superpuesto
        Box(
            modifier = Modifier
                .size(80.dp)
                .align(Alignment.BottomEnd)
                .offset(
                    x = (-35).dp,
                    y = 28.dp
                )
                .background(
                    color = teal.copy(
                        alpha = if (isDarkTheme) {
                            0.06f
                        } else {
                            0.04f
                        }
                    )
                )
        )

        // =========================================================
        // CONTENIDO
        // =========================================================

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = 24.dp,
                    end = 24.dp,
                    top = 24.dp
                )
        ) {

            Text(
                text = "Por entornos",
                color = primaryText,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 34.sp,
                lineHeight = 37.sp
            )

            Text(
                text = "seguros",
                color = pink,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 37.sp,
                lineHeight = 40.sp
            )

            Spacer(
                modifier = Modifier.height(9.dp)
            )

            Box(
                modifier = Modifier
                    .width(42.dp)
                    .height(3.dp)
                    .background(pink)
            )

            Spacer(
                modifier = Modifier.height(15.dp)
            )

            Text(
                text =
                    "Informa sobre una posible situación de trabajo infantil de forma rápida y segura.",
                color = secondaryText,
                fontFamily = FontFamily.Serif,
                fontSize = 17.sp,
                lineHeight = 21.sp,
                modifier = Modifier.width(300.dp)
            )
        }
    }
}

// =================================================================
// TARJETAS DEL HOME
// =================================================================

/**
 * Presenta una tarjeta de acción de la pantalla principal.
 *
 * Muestra un ícono, un título, una descripción y un indicador
 * visual de navegación.
 *
 * Permite personalizar los colores del fondo, borde,
 * textos e íconos según la acción representada.
 *
 * Ejecuta la función onClick cuando el usuario
 * selecciona la tarjeta.
 *
 * @param icon Ícono representativo de la acción.
 * @param title Título principal de la tarjeta.
 * @param subtitle Texto descriptivo de la acción.
 * @param backgroundColor Color de fondo de la tarjeta.
 * @param borderColor Color del borde de la tarjeta.
 * @param iconColor Color aplicado al ícono.
 * @param titleColor Color del título.
 * @param subtitleColor Color del texto descriptivo.
 * @param arrowColor Color del indicador de navegación.
 * @param onClick Función ejecutada al seleccionar la tarjeta.
 */
@Composable
private fun HomeActionCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    backgroundColor: Color,
    borderColor: Color,
    iconColor: Color,
    titleColor: Color,
    subtitleColor: Color,
    arrowColor: Color,
    onClick: () -> Unit
) {

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .height(102.dp)
            .clickable(onClick = onClick),

        shape = RoundedCornerShape(14.dp),

        color = backgroundColor,

        border = BorderStroke(
            width = 1.dp,
            color = borderColor
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = 20.dp,
                    end = 18.dp
                ),

            verticalAlignment = Alignment.CenterVertically
        ) {

            // =====================================================
            // ICONO
            // =====================================================

            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(40.dp)
            )

            Spacer(
                modifier = Modifier.width(18.dp)
            )

            // =====================================================
            // TEXTO
            // =====================================================

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {

                Text(
                    text = title,
                    color = titleColor,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 21.sp
                )

                Spacer(
                    modifier = Modifier.height(6.dp)
                )

                Text(
                    text = subtitle,
                    color = subtitleColor,
                    fontFamily = FontFamily.Serif,
                    fontSize = 13.sp,
                    lineHeight = 16.sp
                )
            }

            Spacer(
                modifier = Modifier.width(10.dp)
            )

            Text(
                text = "›",
                color = arrowColor,
                fontFamily = FontFamily.Serif,
                fontSize = 36.sp
            )
        }
    }
}

// =================================================================
// CONTENIDO DEL MODAL
// =================================================================

/**
 * Presenta el contenido del modal de selección
 * de modalidad de reporte de RIETI.
 *
 * Muestra las opciones de reporte anónimo
 * y reporte con seguimiento.
 *
 * Mantiene temporalmente la modalidad seleccionada
 * mediante un estado local de Jetpack Compose.
 *
 * El botón Continuar permanece deshabilitado
 * mientras no exista una modalidad seleccionada.
 *
 * Permite cancelar la selección o comunicar
 * la modalidad elegida al componente principal.
 *
 * @param isDarkTheme Indica si está activo el modo oscuro.
 * @param onClose Función ejecutada al cancelar la selección.
 * @param onContinue Función que recibe la modalidad
 * seleccionada cuando se pulsa Continuar.
 */
@Composable
private fun ReportTypeContent(
    isDarkTheme: Boolean,
    onClose: () -> Unit,
    onContinue: (HomeReportType) -> Unit
) {

    var selectedType by remember {
        mutableStateOf<HomeReportType?>(null)
    }

    val primaryText =
        if (isDarkTheme) {
            Color(0xFFF8F5F6)
        } else {
            Color(0xFF123E5B)
        }

    val secondaryText =
        if (isDarkTheme) {
            Color(0xFFC8BEC3)
        } else {
            Color(0xFF587081)
        }

    val cardBackground =
        if (isDarkTheme) {
            Color(0xFF211D20)
        } else {
            Color(0xFFFFFDFD)
        }

    val cardBorder =
        if (isDarkTheme) {
            Color(0xFF4B4448)
        } else {
            Color(0xFFE3E3E3)
        }

    val pink = Color(0xFFFF0060)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = 16.dp,
                end = 16.dp,
                bottom = 22.dp
            ),

        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // =========================================================
        // INDICADOR SUPERIOR
        // =========================================================

        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(
                    pink.copy(alpha = 0.09f)
                ),

            contentAlignment = Alignment.Center
        ) {

            Text(
                text = "▤",
                color = pink,
                fontSize = 25.sp
            )
        }

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        // =========================================================
        // TÍTULO
        // =========================================================

        Text(
            text = "¿Qué tipo de reporte\ndeseas realizar?",
            color = primaryText,
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold,
            fontSize = 21.sp,
            lineHeight = 23.sp,
            textAlign = TextAlign.Center
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Text(
            text =
                "Selecciona la opción que mejor se adapte a tu situación. Puedes consultar los detalles de cada modalidad.",
            color = secondaryText,
            fontFamily = FontFamily.Serif,
            fontSize = 12.sp,
            lineHeight = 16.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 22.dp)
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        // =========================================================
        // REPORTE ANÓNIMO
        // =========================================================

        ReportTypeOption(
            title = "Reporte anónimo",

            subtitle =
                "Tu identidad se mantendrá confidencial.",

            selected =
                selectedType == HomeReportType.ANONYMOUS,

            backgroundColor = cardBackground,
            borderColor = cardBorder,
            selectedColor = pink,
            titleColor = primaryText,
            subtitleColor = secondaryText,

            onClick = {
                selectedType = HomeReportType.ANONYMOUS
            }
        )

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        // =========================================================
        // REPORTE CON SEGUIMIENTO
        // =========================================================

        ReportTypeOption(
            title = "Reporte con seguimiento",

            subtitle =
                "Podrás dar seguimiento al estado de tu reporte con un folio.",

            selected =
                selectedType == HomeReportType.TRACKING,

            backgroundColor = cardBackground,
            borderColor = cardBorder,
            selectedColor = pink,
            titleColor = primaryText,
            subtitleColor = secondaryText,

            onClick = {
                selectedType = HomeReportType.TRACKING
            }
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        // =========================================================
        // CONTINUAR
        // =========================================================

        ContinueReportButton(
            enabled = selectedType != null,
            pink = pink,

            onClick = {
                selectedType?.let { type ->
                    onContinue(type)
                }
            }
        )

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        // =========================================================
        // CANCELAR
        // =========================================================

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(46.dp)
                .clickable(onClick = onClose),

            color = Color.Transparent,
            shape = RoundedCornerShape(8.dp),

            border = BorderStroke(
                width = 1.dp,
                color = pink
            )
        ) {

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = "Cancelar",
                    color = pink,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
        }
    }
}

// =================================================================
// OPCIÓN DEL MODAL
// =================================================================

/**
 * Presenta una opción seleccionable dentro del modal
 * de modalidades de reporte.
 *
 * Muestra el título y la descripción de la modalidad,
 * acompañados de un círculo que indica su selección.
 *
 * Modifica únicamente su representación visual
 * en función del valor de selected.
 *
 * La acción de selección se delega al callback onClick.
 *
 * @param title Nombre de la modalidad mostrada.
 * @param subtitle Descripción de la modalidad.
 * @param selected Indica si la opción está seleccionada.
 * @param backgroundColor Color de fondo sin selección.
 * @param borderColor Color del borde sin selección.
 * @param selectedColor Color utilizado para destacar la selección.
 * @param titleColor Color del título.
 * @param subtitleColor Color de la descripción.
 * @param onClick Función ejecutada al seleccionar la opción.
 */
@Composable
private fun ReportTypeOption(
    title: String,
    subtitle: String,
    selected: Boolean,
    backgroundColor: Color,
    borderColor: Color,
    selectedColor: Color,
    titleColor: Color,
    subtitleColor: Color,
    onClick: () -> Unit
) {

    val finalBorderColor =
        if (selected) {
            selectedColor
        } else {
            borderColor
        }

    val finalBackgroundColor =
        if (selected) {
            selectedColor.copy(
                alpha = 0.07f
            )
        } else {
            backgroundColor
        }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(82.dp)
            .clickable(onClick = onClick),

        color = finalBackgroundColor,

        shape = RoundedCornerShape(12.dp),

        border = BorderStroke(
            width = if (selected) {
                1.5.dp
            } else {
                1.dp
            },

            color = finalBorderColor
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = 18.dp,
                    end = 18.dp,
                    top = 12.dp,
                    bottom = 12.dp
                ),

            verticalAlignment = Alignment.CenterVertically
        ) {

            // =====================================================
            // TEXTO DE LA OPCIÓN
            // =====================================================

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {

                Text(
                    text = title,
                    color = titleColor,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )

                Spacer(
                    modifier = Modifier.height(6.dp)
                )

                Text(
                    text = subtitle,
                    color = subtitleColor,
                    fontFamily = FontFamily.Serif,
                    fontSize = 11.sp,
                    lineHeight = 14.sp
                )
            }

            Spacer(
                modifier = Modifier.width(16.dp)
            )

            // =====================================================
            // CÍRCULO DE SELECCIÓN
            // =====================================================

            Box(
                modifier = Modifier
                    .size(21.dp)
                    .border(
                        width = if (selected) {
                            2.dp
                        } else {
                            1.5.dp
                        },

                        color = if (selected) {
                            selectedColor
                        } else {
                            Color(0xFFB5C7D1)
                        },

                        shape = CircleShape
                    ),

                contentAlignment = Alignment.Center
            ) {

                if (selected) {

                    Box(
                        modifier = Modifier
                            .size(11.dp)
                            .clip(CircleShape)
                            .background(selectedColor)
                    )
                }
            }
        }
    }
}

// =================================================================
// BOTÓN CONTINUAR DEL MODAL
// =================================================================

/**
 * Presenta el botón Continuar del modal de selección
 * de modalidad de reporte.
 *
 * Adapta el color del botón y del texto en función
 * del estado enabled.
 *
 * La interacción se deshabilita cuando todavía
 * no se ha seleccionado una modalidad.
 *
 * @param enabled Indica si el botón permite interacción.
 * @param pink Color principal utilizado para el botón.
 * @param onClick Función ejecutada cuando el botón
 * está habilitado y el usuario lo selecciona.
 */
@Composable
private fun ContinueReportButton(
    enabled: Boolean,
    pink: Color,
    onClick: () -> Unit
) {

    val backgroundColor =
        if (enabled) {
            pink
        } else {
            pink.copy(
                alpha = 0.25f
            )
        }

    val textColor =
        if (enabled) {
            Color.White
        } else {
            Color.White.copy(
                alpha = 0.70f
            )
        }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clickable(
                enabled = enabled,
                onClick = onClick
            ),

        color = backgroundColor,
        shape = RoundedCornerShape(8.dp)
    ) {

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {

            Text(
                text = "Continuar",
                color = textColor,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
        }
    }
}

// =================================================================
// PREVIEW DARK
// =================================================================

/**
 * Genera una vista previa de HomeScreen
 * utilizando el tema oscuro de RIETI.
 *
 * Permite revisar la apariencia de la pantalla
 * principal desde Android Studio.
 *
 * Configura RIETITheme en modo oscuro y utiliza
 * callbacks vacíos para las acciones de navegación.
 */
@Preview(
    name = "Home - Dark",
    showBackground = true,
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun HomeScreenDarkPreview() {

    RIETITheme(
        darkTheme = true
    ) {

        HomeScreen(
            onAnonymousReportClick = {},
            onReportClick = {},
            onAboutUsClick = {},
            onTrackingClick = {},
            onOrientationClick = {},
            onSettingsClick = {},
            onContinueDraftClick = { _, _ -> }
        )
    }
}

// =================================================================
// PREVIEW LIGHT
// =================================================================

/**
 * Genera una vista previa de HomeScreen
 * utilizando el tema claro de RIETI.
 *
 * Permite revisar la presentación de las tarjetas,
 * los elementos decorativos y las opciones de navegación.
 *
 * Configura RIETITheme en modo claro y utiliza
 * callbacks vacíos para las acciones disponibles.
 */
@Preview(
    name = "Home - Light",
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun HomeScreenLightPreview() {

    RIETITheme(
        darkTheme = false
    ) {

        HomeScreen(
            onAnonymousReportClick = {},
            onReportClick = {},
            onAboutUsClick = {},
            onTrackingClick = {},
            onOrientationClick = {},
            onSettingsClick = {},
            onContinueDraftClick = { _, _ -> }
        )
    }
}
