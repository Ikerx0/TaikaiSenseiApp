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
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.*
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

// Pantalla de inicio con un botón para crear un nuevo diagrama
@Composable
fun PantallaInicio(
    onCrearDiagramaClick: () -> Unit  // Función que se llama cuando el botón es presionado
) {
    // Gradiente de fondo que va de un gris claro a blanco
    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFA2A2A2), // Gris claro
            Color(0xFFFFFFFF), // Blanco
            Color(0xFFA2A2A2)  // Gris claro
        )
    )

    // Fuente de interacción para manejar la interacción con el botón
    val interactionSource = remember { MutableInteractionSource() }
    // Detecta si el botón está siendo presionado
    val isPressed by interactionSource.collectIsPressedAsState()
    // Escala animada para el efecto de rebote al presionar el botón
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,  // Reduce el tamaño del botón al presionar
        label = "BotónRebote"
    )
    val haptic = LocalHapticFeedback.current  // Feedback táctil

    // Animación para los colores del botón
    val topColor by animateColorAsState(
        targetValue = if (isPressed) Color(0xFF4A4A4A) else Color(0xFF3A3A3A),
        label = "TopColor"
    )
    val centerColor by animateColorAsState(
        targetValue = if (isPressed) Color(0xFF1A1A1A) else Color(0xFF000000),
        label = "CenterColor"
    )
    val bottomColor by animateColorAsState(
        targetValue = if (isPressed) Color(0xFF4A4A4A) else Color(0xFF3A3A3A),
        label = "BottomColor"
    )

    // Gradiente del botón que cambia dinámicamente con el color
    val buttonGradient = Brush.verticalGradient(
        colors = listOf(topColor, centerColor, bottomColor)
    )

    // Caja principal que ocupa toda la pantalla y aplica el gradiente de fondo
    Box(
        modifier = Modifier
            .fillMaxSize()  // La caja llena toda la pantalla
            .background(brush = backgroundGradient)  // Aplica el fondo con gradiente
    ) {
        // Contenedor para la columna con alineación centrada
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,  // Alinea todo al centro horizontalmente
            verticalArrangement = Arrangement.spacedBy(24.dp),  // Espaciado entre elementos
            modifier = Modifier.align(Alignment.Center)  // Alinea la columna al centro de la pantalla
        ) {
            // LOGO grande (imagen central)
            Box(
                modifier = Modifier
                    .height(400.dp)  // Establece el alto del logo
                    .width(400.dp)  // Establece el ancho del logo
            ) {
                Image(
                    painter = painterResource(id = R.drawable.taikaisensei_logo),  // Carga el logo desde los recursos
                    contentDescription = "Logo TaikaiSensei",  // Descripción accesible
                    contentScale = ContentScale.Fit,  // Ajusta la imagen sin distorsionarla
                    modifier = Modifier.fillMaxSize()  // Hace que la imagen ocupe todo el espacio
                )
            }

            // BOTÓN animado con sombra y color de fondo dinámico
            Box(
                modifier = Modifier
                    .wrapContentWidth()  // El ancho del botón se ajusta al contenido
                    .height(60.dp)  // Establece la altura del botón
                    .offset(y = 6.dp)  // Añade un pequeño desplazamiento vertical
                    .scale(scale)  // Aplica la animación de escala
                    .shadow(
                        elevation = 18.dp,  // Sombra con elevación
                        shape = RoundedCornerShape(80.dp),  // Bordes redondeados
                        ambientColor = Color(0xFFFEE37D),  // Color de la sombra
                        spotColor = Color(0xFFFEE37D)  // Color de la luz puntual de la sombra
                    )
                    .clip(RoundedCornerShape(40.dp))  // Bordes redondeados para el botón
                    .background(brush = buttonGradient)  // Aplica el gradiente dinámico al fondo
                    .clickable(
                        interactionSource = interactionSource,  // Detecta si el botón es presionado
                        indication = null  // No muestra indicaciones visuales
                    ) {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)  // Feedback táctil
                        onCrearDiagramaClick()  // Llama a la función para crear un nuevo diagrama
                    }
                    .padding(horizontal = 24.dp),  // Padding horizontal en el botón
                contentAlignment = Alignment.Center  // Centra el contenido dentro del botón
            ) {
                // Fila con el icono y texto del botón
                Row(
                    verticalAlignment = Alignment.CenterVertically,  // Alinea verticalmente el contenido
                    horizontalArrangement = Arrangement.Center,  // Alinea horizontalmente el contenido
                    modifier = Modifier.padding(horizontal = 16.dp)  // Padding horizontal
                ) {
                    Icon(
                        imageVector = Icons.Default.Create,  // Icono de creación
                        contentDescription = "Crear",  // Descripción accesible
                        tint = Color.White,  // Color del icono
                        modifier = Modifier.size(24.dp)  // Tamaño del icono
                    )
                    Spacer(modifier = Modifier.width(8.dp))  // Espacio entre el icono y el texto
                    Text(
                        text = "Crear Nuevo Diagrama",  // Texto que aparece en el botón
                        color = Color.White,  // Color del texto
                        fontSize = 16.sp  // Tamaño de la fuente
                    )
                }
            }
        }
    }
}
