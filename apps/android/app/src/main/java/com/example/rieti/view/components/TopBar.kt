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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.rieti.R
import com.example.rieti.ui.theme.AppTheme


@Composable
fun TopBar(
    onMenuClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    val colors = AppTheme.colors

    val logoResource = R.drawable.logo_ninos


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
                    .padding(horizontal = 14.dp),

                verticalAlignment = Alignment.CenterVertically
            ) {

                Image(
                    painter = painterResource(
                        id = logoResource
                    ),
                    contentDescription = "Logo RIETI",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.height(45.dp)
                )


                Text(
                    text = "RIETI",
                    color = colors.text,
                    fontSize = 24.sp,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Medium
                )


                Spacer(
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}