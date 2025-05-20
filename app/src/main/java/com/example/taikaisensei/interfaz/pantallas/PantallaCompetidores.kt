package com.example.taikaisensei.interfaz.pantallas

// Importaciones necesarias para la UI y estado en Jetpack Compose
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.taikaisensei.R
import com.example.taikaisensei.datos.Competidor

@Composable
fun PantallaCompetidores(
    onFinalizarClick: (List<Competidor>, String, String) -> Unit = { _, _, _ -> },
    onVolverInicio: () -> Unit = {} // NUEVA LAMBDA para volver al inicio
) {
    var nombreTorneo by remember { mutableStateOf("") }
    var categoriaTorneo by remember { mutableStateOf("") }
    var mostrarFormularioInicial by remember { mutableStateOf(true) }

    var numeroCompetidores by remember { mutableStateOf(0) }
    var faseInicializada by remember { mutableStateOf(false) }
    val competidores = remember { mutableStateListOf<Competidor>() }
    var expanded by remember { mutableStateOf(false) }
    var faseSeleccionada by remember { mutableStateOf("") }
    var competidoresAsignados by remember { mutableStateOf(0) }

    val opcionesFase = listOf(
        "Semifinales" to 4,
        "Cuartos de Final" to 8,
        "Octavos de Final" to 16,
        "Dieciseisavos de Final" to 32
    )

    val backgroundGradient = Brush.verticalGradient(
        listOf(Color(0xFF1A1A1A), Color(0xFF121212), Color(0xFF1A1A1A))
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGradient)
            .padding(horizontal = 16.dp, vertical = 45.dp)
    ) {
        when {
            mostrarFormularioInicial -> {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 80.dp)
                ) {
                    Text(
                        text = "Ingresa el nombre del torneo:",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White
                    )

                    TextField(
                        value = nombreTorneo,
                        onValueChange = { nombreTorneo = it },
                        label = { Text("Nombre del torneo") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.LightGray,
                            focusedContainerColor = Color.DarkGray,
                            unfocusedContainerColor = Color.Gray,
                            focusedLabelColor = Color.White,
                            unfocusedLabelColor = Color.LightGray,
                            cursorColor = Color.White
                        )
                    )

                    Text(
                        text = "Ingresa la categoría:",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White
                    )

                    TextField(
                        value = categoriaTorneo,
                        onValueChange = { categoriaTorneo = it },
                        label = { Text("Categoría") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.LightGray,
                            focusedContainerColor = Color.DarkGray,
                            unfocusedContainerColor = Color.Gray,
                            focusedLabelColor = Color.White,
                            unfocusedLabelColor = Color.LightGray,
                            cursorColor = Color.White
                        )
                    )
                }

                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    BotonDegradadoRojoAzul(texto = "Siguiente") {
                        if (nombreTorneo.isNotBlank() && categoriaTorneo.isNotBlank()) {
                            mostrarFormularioInicial = false
                        }
                    }

                    // BOTÓN PARA VOLVER A PANTALLA DE INICIO
                    BotonDegradadoRojoAzul(texto = "Volver al inicio") {
                        onVolverInicio()
                    }
                }
            }

            !faseInicializada -> {
                Column(
                    verticalArrangement = Arrangement.spacedBy(24.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 80.dp)
                ) {
                    Text(
                        text = "¿En qué fase quieres comenzar tu diagrama?",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White
                    )

                    Box {
                        OutlinedButton(
                            onClick = { expanded = true },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, Color.White, shape = RoundedCornerShape(8.dp))
                        ) {
                            Text(text = faseSeleccionada.ifEmpty { "Seleccionar fase" })
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(Icons.Filled.ArrowDropDown, contentDescription = "Desplegar")
                        }

                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier.background(Color.Black)
                        ) {
                            opcionesFase.forEach { (fase, cantidad) ->
                                DropdownMenuItem(
                                    text = { Text(fase, color = Color.White) },
                                    onClick = {
                                        faseSeleccionada = fase
                                        competidoresAsignados = cantidad
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.CenterHorizontally)
                            .padding(vertical = 24.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.sr_miyagi_sin_fondo),
                            contentDescription = "Logo TaikaiSensei",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(700.dp)
                                .align(Alignment.Center)
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                ) {
                    BotonDegradadoRojoAzul(texto = "Continuar") {
                        if (competidoresAsignados > 0) {
                            numeroCompetidores = competidoresAsignados
                            faseInicializada = true
                            competidores.clear()
                            repeat(numeroCompetidores) {
                                competidores.add(Competidor())
                            }
                        }
                    }
                }
            }

            else -> {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    LazyColumn(
                        modifier = Modifier.weight(1f)
                    ) {
                        items(competidores.indices.toList()) { index ->
                            CompetidorEditor(
                                competidor = competidores[index],
                                onCompetidorChange = { nuevoCompetidor ->
                                    competidores[index] = nuevoCompetidor
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    BotonDegradadoRojoAzul(texto = "Crear Diagrama") {
                        onFinalizarClick(competidores, nombreTorneo, categoriaTorneo)
                    }
                }
            }
        }
    }
}

@Composable
fun CompetidorEditor(
    competidor: Competidor,
    onCompetidorChange: (Competidor) -> Unit
) {
    var nombre by remember { mutableStateOf(competidor.nombre) }
    var club by remember { mutableStateOf(competidor.club) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2A2A2A))
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            TextField(
                value = nombre,
                onValueChange = {
                    nombre = it
                    onCompetidorChange(Competidor(nombre, club))
                },
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.LightGray,
                    focusedContainerColor = Color.DarkGray,
                    unfocusedContainerColor = Color.Gray,
                    focusedLabelColor = Color.White,
                    unfocusedLabelColor = Color.LightGray,
                    cursorColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            TextField(
                value = club,
                onValueChange = {
                    club = it
                    onCompetidorChange(Competidor(nombre, club))
                },
                label = { Text("Club") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.LightGray,
                    focusedContainerColor = Color.DarkGray,
                    unfocusedContainerColor = Color.Gray,
                    focusedLabelColor = Color.White,
                    unfocusedLabelColor = Color.LightGray,
                    cursorColor = Color.White
                )
            )
        }
    }
}

@Composable
fun BotonDegradadoRojoAzul(texto: String, onClick: () -> Unit) {
    val gradientBrush = Brush.horizontalGradient(
        colors = listOf(Color(0xFFCC0000), Color(0xFF0000CC))
    )

    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .height(56.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        shape = RoundedCornerShape(50),
        contentPadding = PaddingValues()
    ) {
        Box(
            modifier = Modifier
                .background(brush = gradientBrush, shape = RoundedCornerShape(50))
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(text = texto, color = Color.White, fontSize = 18.sp)
        }
    }
}
