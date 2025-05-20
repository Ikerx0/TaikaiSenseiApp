package com.example.taikaisensei.interfaz.pantallas

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
import com.google.firebase.firestore.FirebaseFirestore


// Pantalla principal para mostrar el diagrama del torneo
@Composable
fun PantallaDiagrama(
    competidoresIniciales: List<Competidor>, // Lista de competidores iniciales para el torneo
    navController: NavController, // Controlador de navegación para cambiar entre pantallas
    nombreTorneo: String,
    categoriaTorneo: String
) {
    // Estado que mantiene la lista de rondas, comenzando con la primera ronda generada
    var rondas by remember { mutableStateOf(listOf(generarPrimeraRonda(competidoresIniciales))) }

    // Estado que guarda el índice de la ronda actual
    var rondaActualIndex by remember { mutableStateOf(0) }
    val rondaActual = rondas[rondaActualIndex] // La ronda actual según el índice

    // Estado para guardar al campeón, inicialmente es nulo
    var campeon by remember { mutableStateOf<Competidor?>(null) }

    // Estado para controlar el scroll de la pantalla, para poder desplazarse
    val scrollState = rememberScrollState()

    // LaunchedEffect para reiniciar el scroll cuando cambie la ronda actual
    LaunchedEffect(rondaActualIndex) {
        scrollState.animateScrollTo(0) // Cuando la ronda cambia, el scroll vuelve al principio
    }

    // Estructura principal de la pantalla que contiene todos los elementos visuales
    Column(
        modifier = Modifier
            .fillMaxSize() // Hace que el contenedor ocupe toda la pantalla
            .background(Color(0xFF101010)) // Fondo oscuro para la pantalla
            .padding(16.dp), // Añade un poco de padding alrededor de todo
        horizontalAlignment = Alignment.CenterHorizontally // Centra todo horizontalmente
    ) {
        // Espaciado superior para separar de la parte superior de la pantalla
        Spacer(modifier = Modifier.height(32.dp))

        // Muestra el título de la ronda actual
        Text("Ronda ${rondaActualIndex + 1}", style = MaterialTheme.typography.titleLarge, color = Color.White)

        // Espacio entre el título y los enfrentamientos
        Spacer(modifier = Modifier.height(16.dp))

        // Contenedor que hace scroll vertical a los enfrentamientos de la ronda actual
        Column(
            modifier = Modifier
                .weight(1f) // Ocupa el espacio disponible
                .verticalScroll(scrollState) // Habilita el scroll
        ) {
            // Recorre cada enfrentamiento de la ronda actual y genera los botones
            rondaActual.forEach { enfrentamiento ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth() // Asegura que el enfrentamiento ocupe toda la pantalla
                        .padding(8.dp) // Padding alrededor del enfrentamiento
                        .border(1.dp, Color.White, RoundedCornerShape(8.dp)) // Borde con esquinas redondeadas
                        .padding(8.dp)
                ) {
                    // Muestra un texto indicando que se debe seleccionar al ganador
                    Text(
                        "Selecciona al ganador:",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White
                    )

                    // Fila que contiene los botones para seleccionar al ganador
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly // Espacio igual entre los botones
                    ) {
                        // Botón para seleccionar al primer competidor
                        Button(
                            onClick = { enfrentamiento.ganador = enfrentamiento.competidor1.nombre },
                            colors = ButtonDefaults.buttonColors(
                                // Cambia el color del botón si ya es el ganador
                                containerColor = if (enfrentamiento.ganador == enfrentamiento.competidor1.nombre)
                                    Color(0xFFB71C1C) else Color(0xFFEF9A9A)
                            )
                        ) {
                            // Muestra el nombre y club del primer competidor
                            Text("${enfrentamiento.competidor1.nombre} (${enfrentamiento.competidor1.club})")
                        }

                        // Botón para seleccionar al segundo competidor
                        Button(
                            onClick = { enfrentamiento.ganador = enfrentamiento.competidor2.nombre },
                            colors = ButtonDefaults.buttonColors(
                                // Cambia el color del botón si ya es el ganador
                                containerColor = if (enfrentamiento.ganador == enfrentamiento.competidor2.nombre)
                                    Color(0xFF0D47A1) else Color(0xFF90CAF9)
                            )
                        ) {
                            // Muestra el nombre y club del segundo competidor
                            Text("${enfrentamiento.competidor2.nombre} (${enfrentamiento.competidor2.club})")
                        }
                    }
                }
            }
        }

        // Espacio entre los enfrentamientos y el botón para avanzar a la siguiente ronda
        Spacer(modifier = Modifier.height(24.dp))

        // Si aún no hay campeón, muestra el botón para avanzar de ronda
        if (campeon == null) {
            Button(
                onClick = {
                    // Obtiene los ganadores de los enfrentamientos de esta ronda
                    val ganadores = rondaActual.mapNotNull { enfrentamiento ->
                        when (enfrentamiento.ganador) {
                            enfrentamiento.competidor1.nombre -> enfrentamiento.competidor1
                            enfrentamiento.competidor2.nombre -> enfrentamiento.competidor2
                            else -> null
                        }
                    }

                    // Si solo hay un ganador, se declara como campeón
                    if (ganadores.size == 1) {
                        campeon = ganadores.first()

                        // Subcampeón: perdedor del último enfrentamiento
                        val ultimoEnfrentamiento = rondaActual.last()
                        val subcampeon = when (campeon?.nombre) {
                            ultimoEnfrentamiento.competidor1.nombre -> ultimoEnfrentamiento.competidor2
                            else -> ultimoEnfrentamiento.competidor1
                        }

                        // Guardar en Firestore
                        val db = FirebaseFirestore.getInstance()
                        val finalistas = hashMapOf(
                            "campeon" to mapOf(
                                "nombre" to campeon!!.nombre,
                                "club" to campeon!!.club
                            ),
                            "subcampeon" to mapOf(
                                "nombre" to subcampeon.nombre,
                                "club" to subcampeon.club
                            )
                        )

                        db.collection("torneos")
                            .add(finalistas)
                            .addOnSuccessListener { /* éxito */ }
                            .addOnFailureListener { e -> e.printStackTrace() }

                    } else {
                            // Si hay más de un ganador, genera la siguiente ronda
                            val nuevaRonda = generarRonda(ganadores)
                            rondas = rondas + listOf(nuevaRonda) // Añade la nueva ronda a la lista de rondas
                            rondaActualIndex++ // Avanza al índice de la siguiente ronda
                        }
                    },
                    enabled = rondaActual.all { it.ganador != null }, // El botón solo se habilita si todos los enfrentamientos tienen un ganador
                    modifier = Modifier
                        .fillMaxWidth() // Hace que el botón ocupe toda la pantalla
                        .height(50.dp) // Altura del botón
                        .padding(horizontal = 16.dp) // Padding horizontal
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(Color(0xFF0D47A1), Color(0xFFB71C1C)) // Gradiente de color de fondo
                            ),
                            shape = RoundedCornerShape(12.dp) // Bordes redondeados
                        ),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    shape = RoundedCornerShape(12.dp) // Forma redondeada del botón
                ) {
                    // Texto dentro del botón
                    Text("Avanzar Ronda", color = Color.White)
                }
            }

        // Espacio antes de mostrar al campeón
        Spacer(modifier = Modifier.height(48.dp))

        // Si se ha declarado un campeón, muestra su nombre y un botón para reiniciar el torneo
        campeon?.let { campeonNoNulo ->
            Spacer(modifier = Modifier.height(16.dp)) // Espacio entre el nombre del campeón y el siguiente elemento
            Text("🏆 Campeón: ${campeonNoNulo.nombre} (${campeonNoNulo.club})", style = MaterialTheme.typography.titleMedium, color = Color.White)

            // Obtener el subcampeón
            val ultimoEnfrentamiento = rondas.last().last()
            val subcampeon = when (campeonNoNulo.nombre) {
                ultimoEnfrentamiento.competidor1.nombre -> ultimoEnfrentamiento.competidor2
                ultimoEnfrentamiento.competidor2.nombre -> ultimoEnfrentamiento.competidor1
                else -> null // Si no hay coincidencia, asignar null
            }

            subcampeon?.let {
                Text("🥈 Subcampeón: ${it.nombre} (${it.club})", style = MaterialTheme.typography.bodyLarge, color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

            // Botón para crear una nueva plantilla de competidores y reiniciar el torneo
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
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                shape = RoundedCornerShape(12.dp)
            ) {
                // Texto en el botón
                Text("Crear nueva plantilla", color = Color.White)
            }
        }
        Spacer(modifier = Modifier.height(48.dp)) // Espacio final en la parte inferior
    }

// Clase para representar un enfrentamiento entre dos competidores
class Enfrentamiento(
    val competidor1: Competidor, // Primer competidor del enfrentamiento
    val competidor2: Competidor, // Segundo competidor del enfrentamiento
    ganadorInicial: String? = null, // Nombre del ganador inicial, si lo hay
) {
    var ganador by mutableStateOf(ganadorInicial) // Estado mutable para el ganador
}

// Función para generar la primera ronda a partir de los competidores
fun generarPrimeraRonda(competidores: List<Competidor>): List<Enfrentamiento> {
    val emparejamientos = mutableListOf<Enfrentamiento>()
    for (i in competidores.indices step 2) {
        val c1 = competidores.getOrNull(i) // Competidor 1
        val c2 = competidores.getOrNull(i + 1) // Competidor 2
        if (c1 != null && c2 != null) {
            emparejamientos.add(Enfrentamiento(c1, c2)) // Añade el enfrentamiento a la lista
        }
    }
    return emparejamientos // Devuelve los enfrentamientos generados
}

// Función para generar las siguientes rondas con los ganadores
fun generarRonda(ganadores: List<Competidor>): List<Enfrentamiento> {
    val emparejamientos = mutableListOf<Enfrentamiento>()
    for (i in ganadores.indices step 2) {
        val c1 = ganadores.getOrNull(i) // Competidor 1
        val c2 = ganadores.getOrNull(i + 1) // Competidor 2
        if (c1 != null && c2 != null) {
            emparejamientos.add(Enfrentamiento(c1, c2)) // Añade el enfrentamiento con los ganadores
        }
    }
    return emparejamientos // Devuelve los nuevos enfrentamientos
}
