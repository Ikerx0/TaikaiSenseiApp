package com.example.taikaisensei.interfaz.pantallas

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.taikaisensei.R

@Composable
fun PantallaInicio(
    onCrearDiagramaClick: () -> Unit,
    onVerHistorialClick: () -> Unit
) {
    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFA2A2A2),
            Color(0xFFFFFFFF),
            Color(0xFFA2A2A2)
        )
    )

    // Botón Crear Nuevo Diagrama interaction
    val interactionSourceCrear = remember { MutableInteractionSource() }
    val isPressedCrear by interactionSourceCrear.collectIsPressedAsState()
    val scaleCrear by animateFloatAsState(targetValue = if (isPressedCrear) 0.95f else 1f, label = "BotonCrearRebote")
    val topColorCrear by animateColorAsState(targetValue = if (isPressedCrear) Color(0xFF4A4A4A) else Color(0xFF3A3A3A), label = "TopColorCrear")
    val centerColorCrear by animateColorAsState(targetValue = if (isPressedCrear) Color(0xFF1A1A1A) else Color(0xFF000000), label = "CenterColorCrear")
    val bottomColorCrear by animateColorAsState(targetValue = if (isPressedCrear) Color(0xFF4A4A4A) else Color(0xFF3A3A3A), label = "BottomColorCrear")
    val buttonGradientCrear = Brush.verticalGradient(colors = listOf(topColorCrear, centerColorCrear, bottomColorCrear))

    // Botón Ver Historial interaction
    val interactionSourceHistorial = remember { MutableInteractionSource() }
    val isPressedHistorial by interactionSourceHistorial.collectIsPressedAsState()
    val scaleHistorial by animateFloatAsState(targetValue = if (isPressedHistorial) 0.95f else 1f, label = "BotonHistorialRebote")
    val topColorHistorial by animateColorAsState(targetValue = if (isPressedHistorial) Color(0xFF4A4A4A) else Color(0xFF3A3A3A), label = "TopColorHistorial")
    val centerColorHistorial by animateColorAsState(targetValue = if (isPressedHistorial) Color(0xFF1A1A1A) else Color(0xFF000000), label = "CenterColorHistorial")
    val bottomColorHistorial by animateColorAsState(targetValue = if (isPressedHistorial) Color(0xFF4A4A4A) else Color(0xFF3A3A3A), label = "BottomColorHistorial")
    val buttonGradientHistorial = Brush.verticalGradient(colors = listOf(topColorHistorial, centerColorHistorial, bottomColorHistorial))

    val haptic = LocalHapticFeedback.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = backgroundGradient)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Box(
                modifier = Modifier
                    .height(400.dp)
                    .width(400.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.taikaisensei_logo),
                    contentDescription = "Logo TaikaiSensei",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Botón Crear Nuevo Diagrama
            Box(
                modifier = Modifier
                    .wrapContentWidth()
                    .height(60.dp)
                    .offset(y = 6.dp)
                    .scale(scaleCrear)
                    .shadow(
                        elevation = 18.dp,
                        shape = RoundedCornerShape(80.dp),
                        ambientColor = Color(0xFFFEE37D),
                        spotColor = Color(0xFFFEE37D)
                    )
                    .clip(RoundedCornerShape(40.dp))
                    .background(brush = buttonGradientCrear)
                    .clickable(
                        interactionSource = interactionSourceCrear,
                        indication = null
                    ) {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onCrearDiagramaClick()
                    }
                    .padding(horizontal = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Create,
                        contentDescription = "Crear",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Crear Nuevo Diagrama",
                        color = Color.White,
                        fontSize = 16.sp
                    )
                }
            }

            // Botón Ver Historial de Torneos
            Box(
                modifier = Modifier
                    .wrapContentWidth()
                    .height(60.dp)
                    .offset(y = 6.dp)
                    .scale(scaleHistorial)
                    .shadow(
                        elevation = 18.dp,
                        shape = RoundedCornerShape(80.dp),
                        ambientColor = Color(0xFFFEE37D),
                        spotColor = Color(0xFFFEE37D)
                    )
                    .clip(RoundedCornerShape(40.dp))
                    .background(brush = buttonGradientHistorial)
                    .clickable(
                        interactionSource = interactionSourceHistorial,
                        indication = null
                    ) {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onVerHistorialClick()
                    }
                    .padding(horizontal = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.List,
                        contentDescription = "Historial",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Ver Historial de Torneos",
                        color = Color.White,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}
