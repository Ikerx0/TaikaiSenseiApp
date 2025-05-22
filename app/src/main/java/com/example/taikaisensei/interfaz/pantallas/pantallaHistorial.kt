package com.example.taikaisensei.interfaz.pantallas

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.taikaisensei.datos.Competidor
import com.example.taikaisensei.datos.TorneoFinalizado
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun PantallaHistorial(navController: NavController) {
    // Aquí guardamos la lista de torneos finalizados que se mostrarán en pantalla
    var torneos by remember { mutableStateOf<List<TorneoFinalizado>>(emptyList()) }

    // Flag para mostrar el progreso de carga mientras traemos los datos de Firebase
    var cargando by remember { mutableStateOf(true) }

    // Configuraciones para el botón de "Volver", incluyendo animación de escala y colores
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(targetValue = if (isPressed) 0.95f else 1f, label = "scaleAnim")
    val haptic = LocalHapticFeedback.current

    // Animación de colores para el fondo del botón
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
    val buttonGradient = Brush.verticalGradient(colors = listOf(topColor, centerColor, bottomColor))

    // Obtenemos el usuario que ha iniciado sesión
    val firebaseUser = FirebaseAuth.getInstance().currentUser
    val userId = firebaseUser?.uid

    // Al cargar la pantalla, se consultan los torneos finalizados desde Firestore
    LaunchedEffect(userId) {
        if (userId == null) return@LaunchedEffect // Si no hay usuario, salimos

        val db = FirebaseFirestore.getInstance()

        db.collection("torneos")
            .whereEqualTo("usuarioId", userId) // Solo torneos creados por este usuario
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                // Convertimos los datos del documento en objetos de nuestra app
                val lista = result.map { doc ->
                    val campeonMap = doc.get("campeon") as? Map<*, *>
                    val subcampeonMap = doc.get("subcampeon") as? Map<*, *>

                    TorneoFinalizado(
                        nombreTorneo = doc.getString("nombreTorneo") ?: "",
                        categoria = doc.getString("categoria") ?: "",
                        campeon = Competidor(
                            nombre = campeonMap?.get("nombre") as? String ?: "",
                            club = campeonMap?.get("club") as? String ?: ""
                        ),
                        subcampeon = Competidor(
                            nombre = subcampeonMap?.get("nombre") as? String ?: "",
                            club = subcampeonMap?.get("club") as? String ?: ""
                        ),
                        timestamp = doc.getTimestamp("timestamp")
                    )
                }
                torneos = lista
                cargando = false
            }
            .addOnFailureListener { exception ->
                println("Error al cargar torneos: ${exception.message}")
                cargando = false
            }
    }

    // Composición principal de la pantalla de historial
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF101010)) // Fondo oscuro sobrio
            .padding(16.dp)
    ) {
        // Título de la pantalla
        Text(
            text = "Historial de Torneos",
            style = MaterialTheme.typography.headlineSmall,
            color = Color.White,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Mientras se están cargando los datos, mostramos un indicador de carga
        if (cargando) {
            // Aquí iría un CircularProgressIndicator o algo similar
        } else {
            // Mostramos la lista de torneos una vez cargados
            LazyColumn {
                items(torneos) { torneo ->
                    // Aquí podrías componer una tarjeta o un ítem bonito por torneo
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón para volver a la pantalla anterior
        Box(
            modifier = Modifier
                .scale(scale)
                .clip(RoundedCornerShape(12.dp))
                .background(brush = buttonGradient)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null
                ) {
                    haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                    navController.popBackStack()
                }
                .padding(horizontal = 24.dp, vertical = 12.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            Text(
                text = "Volver",
                color = Color.White,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}
