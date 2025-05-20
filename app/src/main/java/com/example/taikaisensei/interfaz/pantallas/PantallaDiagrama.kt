package com.example.taikaisensei.interfaz.pantallas

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.taikaisensei.datos.Competidor
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun PantallaDiagrama(
    competidoresIniciales: List<Competidor>,
    navController: NavController,
    nombreTorneo: String,
    categoriaTorneo: String
) {
    var rondas by remember { mutableStateOf(listOf(generarPrimeraRonda(competidoresIniciales))) }
    var rondaActualIndex by remember { mutableStateOf(0) }
    val rondaActual = rondas[rondaActualIndex]
    var campeon by remember { mutableStateOf<Competidor?>(null) }
    val scrollState = rememberScrollState()

    LaunchedEffect(rondaActualIndex) {
        scrollState.animateScrollTo(0)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF101010))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        Text("Ronda ${rondaActualIndex + 1}", style = MaterialTheme.typography.titleLarge, color = Color.White)
        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState)
        ) {
            rondaActual.forEach { enfrentamiento ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .border(1.dp, Color.White, RoundedCornerShape(8.dp))
                        .padding(8.dp)
                ) {
                    Text("Selecciona al ganador:", style = MaterialTheme.typography.bodyLarge, color = Color.White)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(
                            onClick = { enfrentamiento.ganador = enfrentamiento.competidor1.nombre },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (enfrentamiento.ganador == enfrentamiento.competidor1.nombre)
                                    Color(0xFFB71C1C) else Color(0xFFEF9A9A),
                                contentColor = Color.White
                            )
                        ) {
                            Text("${enfrentamiento.competidor1.nombre} (${enfrentamiento.competidor1.club})")
                        }

                        Button(
                            onClick = { enfrentamiento.ganador = enfrentamiento.competidor2.nombre },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (enfrentamiento.ganador == enfrentamiento.competidor2.nombre)
                                    Color(0xFF0D47A1) else Color(0xFF90CAF9),
                                contentColor = Color.White
                            )
                        ) {
                            Text("${enfrentamiento.competidor2.nombre} (${enfrentamiento.competidor2.club})")
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (campeon == null) {
            Button(
                onClick = {
                    val ganadores = rondaActual.mapNotNull { enf ->
                        when (enf.ganador) {
                            enf.competidor1.nombre -> enf.competidor1
                            enf.competidor2.nombre -> enf.competidor2
                            else -> null
                        }
                    }

                    if (ganadores.size == 1) {
                        campeon = ganadores.first()
                        val ultimoEnfrentamiento = rondaActual.last()
                        val subcampeon = if (campeon!!.nombre == ultimoEnfrentamiento.competidor1.nombre)
                            ultimoEnfrentamiento.competidor2 else ultimoEnfrentamiento.competidor1

                        val db = FirebaseFirestore.getInstance()
                        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@Button

                        val torneoData = hashMapOf(
                            "usuarioId" to userId,
                            "nombreTorneo" to nombreTorneo,
                            "categoria" to categoriaTorneo,
                            "campeon" to mapOf("nombre" to campeon!!.nombre, "club" to campeon!!.club),
                            "subcampeon" to mapOf("nombre" to subcampeon.nombre, "club" to subcampeon.club),
                            "timestamp" to com.google.firebase.Timestamp.now()
                        )

                        db.collection("torneos")
                            .add(torneoData)
                            .addOnSuccessListener {
                                // Éxito
                                Log.d("FIREBASE", "Torneo guardado correctamente.")
                            }
                            .addOnFailureListener { e -> e.printStackTrace()
                                Log.e("FIREBASE", "Error al guardar torneo", e)}
                    } else {
                        val nuevaRonda = generarRonda(ganadores)
                        rondas = rondas + listOf(nuevaRonda)
                        rondaActualIndex++
                    }
                },
                enabled = rondaActual.all { it.ganador != null },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .padding(horizontal = 16.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(Color(0xFF0D47A1), Color(0xFFB71C1C))
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Avanzar Ronda")
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        campeon?.let { campeonNoNulo ->
            Spacer(modifier = Modifier.height(16.dp))
            Text("🏆 Campeón: ${campeonNoNulo.nombre} (${campeonNoNulo.club})", style = MaterialTheme.typography.titleMedium, color = Color.White)

            val ultimoEnfrentamiento = rondas.last().last()
            val subcampeon = if (campeonNoNulo.nombre == ultimoEnfrentamiento.competidor1.nombre)
                ultimoEnfrentamiento.competidor2 else ultimoEnfrentamiento.competidor1

            Text("🥈 Subcampeón: ${subcampeon.nombre} (${subcampeon.club})", style = MaterialTheme.typography.bodyLarge, color = Color.White)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { navController.navigate("pantalla_competidores") },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .padding(horizontal = 16.dp)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(Color(0xFF0D47A1), Color(0xFFB71C1C))
                    ),
                    shape = RoundedCornerShape(12.dp)
                ),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Crear nueva plantilla")
        }

        Spacer(modifier = Modifier.height(48.dp))
    }
}

// ---------- FUNCIONES Y CLASES AUXILIARES ----------

class Enfrentamiento(
    val competidor1: Competidor,
    val competidor2: Competidor,
    ganadorInicial: String? = null,
) {
    var ganador by mutableStateOf(ganadorInicial)
}

fun generarPrimeraRonda(competidores: List<Competidor>): List<Enfrentamiento> {
    val emparejamientos = mutableListOf<Enfrentamiento>()
    for (i in competidores.indices step 2) {
        val c1 = competidores.getOrNull(i)
        val c2 = competidores.getOrNull(i + 1)
        if (c1 != null && c2 != null) {
            emparejamientos.add(Enfrentamiento(c1, c2))
        }
    }
    return emparejamientos
}

fun generarRonda(ganadores: List<Competidor>): List<Enfrentamiento> {
    val emparejamientos = mutableListOf<Enfrentamiento>()
    for (i in ganadores.indices step 2) {
        val c1 = ganadores.getOrNull(i)
        val c2 = ganadores.getOrNull(i + 1)
        if (c1 != null && c2 != null) {
            emparejamientos.add(Enfrentamiento(c1, c2))
        }
    }
    return emparejamientos
}
