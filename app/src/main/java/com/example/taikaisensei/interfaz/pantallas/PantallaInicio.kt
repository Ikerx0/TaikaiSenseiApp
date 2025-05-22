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
import androidx.compose.material.icons.filled.Info
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.taikaisensei.R

@Composable
fun PantallaInicio(
    onCrearDiagramaClick: () -> Unit,
    onVerHistorialClick: () -> Unit,
    onMarcadorClick: () -> Unit
) {
    // Fondo con degradado vertical
    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFA2A2A2),
            Color(0xFFFFFFFF),
            Color(0xFFA2A2A2)
        )
    )

    // Animaciones e interacción para el botón "Crear Nuevo Diagrama"
    val interactionSourceCrear = remember { MutableInteractionSource() }
    val isPressedCrear by interactionSourceCrear.collectIsPressedAsState()
    val scaleCrear by animateFloatAsState(targetValue = if (isPressedCrear) 0.95f else 1f, label = "BotonCrearRebote")
    val topColorCrear by animateColorAsState(targetValue = if (isPressedCrear) Color(0xFF4A4A4A) else Color(0xFF3A3A3A), label = "TopColorCrear")
    val centerColorCrear by animateColorAsState(targetValue = if (isPressedCrear) Color(0xFF1A1A1A) else Color(0xFF000000), label = "CenterColorCrear")
    val bottomColorCrear by animateColorAsState(targetValue = if (isPressedCrear) Color(0xFF4A4A4A) else Color(0xFF3A3A3A), label = "BottomColorCrear")
    val buttonGradientCrear = Brush.verticalGradient(colors = listOf(topColorCrear, centerColorCrear, bottomColorCrear))

    // Animaciones e interacción para el botón "Ver Historial"
    val interactionSourceHistorial = remember { MutableInteractionSource() }
    val isPressedHistorial by interactionSourceHistorial.collectIsPressedAsState()
    val scaleHistorial by animateFloatAsState(targetValue = if (isPressedHistorial) 0.95f else 1f, label = "BotonHistorialRebote")
    val topColorHistorial by animateColorAsState(targetValue = if (isPressedHistorial) Color(0xFF4A4A4A) else Color(0xFF3A3A3A), label = "TopColorHistorial")
    val centerColorHistorial by animateColorAsState(targetValue = if (isPressedHistorial) Color(0xFF1A1A1A) else Color(0xFF000000), label = "CenterColorHistorial")
    val bottomColorHistorial by animateColorAsState(targetValue = if (isPressedHistorial) Color(0xFF4A4A4A) else Color(0xFF3A3A3A), label = "BottomColorHistorial")
    val buttonGradientHistorial = Brush.verticalGradient(colors = listOf(topColorHistorial, centerColorHistorial, bottomColorHistorial))

    // Animaciones e interacción para el botón "Marcador Kumite"
    val interactionSourceMarcador = remember { MutableInteractionSource() }
    val isPressedMarcador by interactionSourceMarcador.collectIsPressedAsState()
    val scaleMarcador by animateFloatAsState(targetValue = if (isPressedMarcador) 0.95f else 1f, label = "BotonMarcadorRebote")
    val topColorMarcador by animateColorAsState(targetValue = if (isPressedMarcador) Color(0xFF4A4A4A) else Color(0xFF3A3A3A), label = "TopColorMarcador")
    val centerColorMarcador by animateColorAsState(targetValue = if (isPressedMarcador) Color(0xFF1A1A1A) else Color(0xFF000000), label = "CenterColorMarcador")
    val bottomColorMarcador by animateColorAsState(targetValue = if (isPressedMarcador) Color(0xFF4A4A4A) else Color(0xFF3A3A3A), label = "BottomColorMarcador")
    val buttonGradientMarcador = Brush.verticalGradient(colors = listOf(topColorMarcador, centerColorMarcador, bottomColorMarcador))

    // Contenedor principal de toda la pantalla
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
            // Logo de la aplicación
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

            // Botón para crear nuevo diagrama
            BotonInicio(
                texto = "Crear Nuevo Diagrama",
                icono = Icons.Default.Create,
                scale = scaleCrear,
                background = buttonGradientCrear,
                interactionSource = interactionSourceCrear
            ) {
                onCrearDiagramaClick()
            }

            // Botón para ver historial de torneos
            BotonInicio(
                texto = "Ver Historial de Torneos",
                icono = Icons.AutoMirrored.Filled.List,
                scale = scaleHistorial,
                background = buttonGradientHistorial,
                interactionSource = interactionSourceHistorial
            ) {
                onVerHistorialClick()
            }

            // Botón para acceder al marcador Kumite
            BotonInicio(
                texto = "Marcador Kumite",
                icono = Icons.Default.Info,
                scale = scaleMarcador,
                background = buttonGradientMarcador,
                interactionSource = interactionSourceMarcador
            ) {
                onMarcadorClick()
            }
        }
    }
}

@Composable
private fun BotonInicio(
    texto: String,
    icono: androidx.compose.ui.graphics.vector.ImageVector,
    scale: Float,
    background: Brush,
    interactionSource: MutableInteractionSource,
    onClick: () -> Unit
) {
    // Composable reutilizable para los botones principales de la pantalla
    Box(
        modifier = Modifier
            .wrapContentWidth()
            .height(60.dp)
            .offset(y = 6.dp)
            .scale(scale) // efecto rebote
            .shadow(
                elevation = 18.dp,
                shape = RoundedCornerShape(80.dp),
                ambientColor = Color(0xFFFEE37D),
                spotColor = Color(0xFFFEE37D)
            )
            .clip(RoundedCornerShape(40.dp))
            .background(brush = background) // degradado dinámico
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { onClick() }
            .padding(horizontal = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        // Contenido del botón con ícono y texto
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Icon(
                imageVector = icono,
                contentDescription = texto,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = texto, color = Color.White, fontSize = 16.sp)
        }
    }
}