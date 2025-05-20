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
    var torneos by remember { mutableStateOf<List<TorneoFinalizado>>(emptyList()) }
    var cargando by remember { mutableStateOf(true) }

    // Animaciones botón
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(targetValue = if (isPressed) 0.95f else 1f, label = "scaleAnim")
    val haptic = LocalHapticFeedback.current

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

    // Cargar torneos del usuario actual
    LaunchedEffect(Unit) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@LaunchedEffect
        val db = FirebaseFirestore.getInstance()

        db.collection("torneos")
            .whereEqualTo("usuarioId", userId)
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
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
            .addOnFailureListener {
                cargando = false
            }
    }

    // UI
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF101010))
            .padding(16.dp)
    ) {
        Text(
            text = "Historial de Torneos",
            style = MaterialTheme.typography.headlineSmall,
            color = Color.White,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (cargando) {
            CircularProgressIndicator(
                color = Color.White,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        } else {
            if (torneos.isEmpty()) {
                Text("No hay torneos guardados.", color = Color.White)
            } else {
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

        // Botón volver
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .scale(scale)
                .shadow(
                    elevation = 12.dp,
                    shape = RoundedCornerShape(40.dp),
                    ambientColor = Color(0xFFFEE37D),
                    spotColor = Color(0xFFFEE37D)
                )
                .clip(RoundedCornerShape(40.dp))
                .background(buttonGradient)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null
                ) {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
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
