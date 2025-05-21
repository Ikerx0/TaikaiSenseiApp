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
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.taikaisensei.datos.Competidor
import com.example.taikaisensei.datos.TorneoFinalizado
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun PantallaHistorial(navController: NavController) {
    // Lista que va a guardar todos los torneos que cargamos de Firestore
    var torneos by remember { mutableStateOf<List<TorneoFinalizado>>(emptyList()) }

    // Estado para saber si todavía estamos esperando que se cargue
    var cargando by remember { mutableStateOf(true) }

    // Variables para animaciones del botón de "Volver"
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(targetValue = if (isPressed) 0.95f else 1f, label = "scaleAnim")
    val haptic = LocalHapticFeedback.current

    // Animaciones de color para hacer que el botón tenga un gradiente bonito
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

    // Cogemos el ID del usuario que ha iniciado sesión
    val firebaseUser = FirebaseAuth.getInstance().currentUser
    val userId = firebaseUser?.uid

    // Cuando la pantalla se carga, hacemos una petición a Firestore para traer los torneos
    LaunchedEffect(userId) {
        if (userId == null) return@LaunchedEffect // Si no hay usuario, salimos

        val db = FirebaseFirestore.getInstance()

        db.collection("torneos")
            .whereEqualTo("usuarioId", userId) // Solo torneos del usuario actual
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING) // Más recientes primero
            .get()
            .addOnSuccessListener { result ->
                // Convertimos los documentos en objetos de Kotlin
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
                torneos = lista // Guardamos los torneos en el estado
                cargando = false // Ya no estamos cargando
            }
            .addOnFailureListener { exception ->
                // Si algo falla, lo mostramos por consola
                println("Error al cargar torneos: ${exception.message}")
                cargando = false
            }
    }

    // Comenzamos a dibujar la interfaz
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF101010)) // Fondo negro/gris oscuro
            .padding(16.dp)
    ) {
        // Título bonito
        Text(
            text = "Historial de Torneos",
            style = MaterialTheme.typography.headlineSmall,
            color = Color.White,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Si está cargando, mostramos un circulito de carga
        if (cargando) {
            CircularProgressIndicator(
                color = Color.White,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        } else {
            // Si no hay torneos, lo decimos
            if (torneos.isEmpty()) {
                Text("No hay torneos guardados.", color = Color.White)
            } else {
                // Mostramos cada torneo en una tarjetita
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(torneos) { torneo ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.DarkGray)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("🏆 ${torneo.nombreTorneo}", color = Color.White, style = MaterialTheme.typography.titleMedium)
                                Text("Categoría: ${torneo.categoria}", color = Color.LightGray)
                                Text("Campeón: ${torneo.campeon.nombre} (${torneo.campeon.club})", color = Color.White)
                                Text("Subcampeón: ${torneo.subcampeon.nombre} (${torneo.subcampeon.club})", color = Color.White)
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón para volver a la pantalla de inicio
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .scale(scale) // Escalado con animación si se pulsa
                .shadow(
                    elevation = 12.dp,
                    shape = RoundedCornerShape(40.dp),
                    ambientColor = Color(0xFFFEE37D),
                    spotColor = Color(0xFFFEE37D)
                )
                .clip(RoundedCornerShape(40.dp))
                .background(buttonGradient) // Fondo animado degradado
                .clickable(
                    interactionSource = interactionSource,
                    indication = null
                ) {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove) // Vibración al pulsar

                    // Navegamos a la pantalla de inicio y eliminamos esta del historial
                    navController.navigate("pantalla_inicio") {
                        popUpTo("pantalla_historial") { inclusive = true }
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Text("Volver al Inicio", color = Color.White)
        }
    }
}
