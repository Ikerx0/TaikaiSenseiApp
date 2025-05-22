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
    // Lista inicial de competidores
    competidoresIniciales: List<Competidor>,
    // Controlador para navegar entre pantallas
    navController: NavController,
    // Nombre del torneo actual
    nombreTorneo: String,
    // Categoría del torneo actual
    categoriaTorneo: String
) {
    // Estado que guarda las rondas, iniciando con la primera generada a partir de competidores
    var rondas by remember { mutableStateOf(listOf(generarPrimeraRonda(competidoresIniciales))) }
    // Índice para saber qué ronda se está mostrando
    var rondaActualIndex by remember { mutableStateOf(0) }
    val rondaActual = rondas[rondaActualIndex]  // Ronda actualmente visible
    // Estado para guardar el campeón cuando se determine
    var campeon by remember { mutableStateOf<Competidor?>(null) }
    val scrollState = rememberScrollState()     // Estado para el scroll vertical

    // Cada vez que cambia la ronda actual, hace scroll al inicio para mejor UX
    LaunchedEffect(rondaActualIndex) {
        scrollState.animateScrollTo(0)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF101010))  // Fondo oscuro para mejor contraste
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        // Título con número de ronda
        Text("Ronda ${rondaActualIndex + 1}", style = MaterialTheme.typography.titleLarge, color = Color.White)
        Spacer(modifier = Modifier.height(16.dp))

        // Contenedor scrollable para los enfrentamientos de la ronda actual
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
                        .border(1.dp, Color.White, RoundedCornerShape(8.dp)) // Borde para delimitar
                        .padding(8.dp)
                ) {
                    // Indicador para que el usuario seleccione ganador
                    Text("Selecciona al ganador:", style = MaterialTheme.typography.bodyLarge, color = Color.White)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        // Botón para seleccionar competidor 1 como ganador
                        Button(
                            onClick = { enfrentamiento.ganador = enfrentamiento.competidor1.nombre },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (enfrentamiento.ganador == enfrentamiento.competidor1.nombre)
                                    Color(0xFFB71C1C) else Color(0xFFEF9A9A),  // Color distinto si está seleccionado
                                contentColor = Color.White
                            )
                        ) {
                            Text("${enfrentamiento.competidor1.nombre} (${enfrentamiento.competidor1.club})")
                        }

                        // Botón para seleccionar competidor 2 como ganador
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

        // Botón para avanzar a la siguiente ronda, solo activo si todos los enfrentamientos tienen ganador
        if (campeon == null) {
            Button(
                onClick = {
                    // Recopila los ganadores seleccionados en la ronda actual
                    val ganadores = rondaActual.mapNotNull { enf ->
                        when (enf.ganador) {
                            enf.competidor1.nombre -> enf.competidor1
                            enf.competidor2.nombre -> enf.competidor2
                            else -> null
                        }
                    }

                    if (ganadores.size == 1) {
                        // Si solo queda un ganador, se declara campeón y se guarda torneo en Firestore
                        campeon = ganadores.first()
                        val ultimoEnfrentamiento = rondaActual.last()
                        val subcampeon = if (campeon!!.nombre == ultimoEnfrentamiento.competidor1.nombre)
                            ultimoEnfrentamiento.competidor2 else ultimoEnfrentamiento.competidor1

                        // Referencia a Firestore y usuario autenticado
                        val db = FirebaseFirestore.getInstance()
                        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@Button

                        // Datos que se guardarán del torneo
                        val torneoData = hashMapOf(
                            "usuarioId" to userId,
                            "nombreTorneo" to nombreTorneo,
                            "categoria" to categoriaTorneo,
                            "campeon" to mapOf("nombre" to campeon!!.nombre, "club" to campeon!!.club),
                            "subcampeon" to mapOf("nombre" to subcampeon.nombre, "club" to subcampeon.club),
                            "timestamp" to com.google.firebase.Timestamp.now()
                        )

                        // Guardado en Firestore con logging para éxito o fallo
                        db.collection("torneos")
                            .add(torneoData)
                            .addOnSuccessListener {
                                Log.d("FIREBASE", "Torneo guardado correctamente.")
                            }
                            .addOnFailureListener { e ->
                                e.printStackTrace()
                                Log.e("FIREBASE", "Error al guardar torneo", e)
                            }
                    } else {
                        // Si quedan varios ganadores, genera una nueva ronda y avanza el índice
                        val nuevaRonda = generarRonda(ganadores)
                        rondas = rondas + listOf(nuevaRonda)
                        rondaActualIndex++
                    }
                },
                enabled = rondaActual.all { it.ganador != null }, // Habilitado solo si todos los ganadores están definidos
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .padding(horizontal = 16.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(Color(0xFF0D47A1), Color(0xFFB71C1C)) // Degradado visual atractivo
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

        // Si hay campeón, se muestra en pantalla junto con el subcampeón
        campeon?.let { campeonNoNulo ->
            Spacer(modifier = Modifier.height(16.dp))
            Text("🏆 Campeón: ${campeonNoNulo.nombre} (${campeonNoNulo.club})", style = MaterialTheme.typography.titleMedium, color = Color.White)

            val ultimoEnfrentamiento = rondas.last().last()
            val subcampeon = if (campeonNoNulo.nombre == ultimoEnfrentamiento.competidor1.nombre)
                ultimoEnfrentamiento.competidor2 else ultimoEnfrentamiento.competidor1

            Text("🥈 Subcampeón: ${subcampeon.nombre} (${subcampeon.club})", style = MaterialTheme.typography.bodyLarge, color = Color.White)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón para volver a la pantalla de creación de plantilla de competidores
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

// Clase para representar un enfrentamiento entre dos competidores
class Enfrentamiento(
    val competidor1: Competidor,
    val competidor2: Competidor,
    ganadorInicial: String? = null,
) {
    // Estado mutable para guardar el ganador seleccionado (puede ser null si no se ha elegido)
    var ganador by mutableStateOf(ganadorInicial)
}

// Genera la primera ronda a partir de la lista inicial de competidores
fun generarPrimeraRonda(competidores: List<Competidor>): List<Enfrentamiento> {
    val emparejamientos = mutableListOf<Enfrentamiento>()
    // Empareja competidores de dos en dos de forma secuencial
    for (i in competidores.indices step 2) {
        val c1 = competidores.getOrNull(i)
        val c2 = competidores.getOrNull(i + 1)
        if (c1 != null && c2 != null) {
            emparejamientos.add(Enfrentamiento(c1, c2))
        }
    }
    return emparejamientos
}

// Genera una ronda a partir de la lista de ganadores de la ronda anterior
fun generarRonda(ganadores: List<Competidor>): List<Enfrentamiento> {
    val emparejamientos = mutableListOf<Enfrentamiento>()
    // Misma lógica de emparejamiento para la nueva ronda
    for (i in ganadores.indices step 2) {
        val c1 = ganadores.getOrNull(i)
        val c2 = ganadores.getOrNull(i + 1)
        if (c1 != null && c2 != null) {
            emparejamientos.add(Enfrentamiento(c1, c2))
        }
    }
    return emparejamientos
}