package com.example.taikaisensei.interfaz.pantallas

import android.app.Activity
import android.content.pm.ActivityInfo
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import kotlinx.coroutines.delay

@Composable
fun PantallaMarcador(navController: NavController) {
    val context = LocalContext.current

    // Para que la pantalla siempre esté en horizontal, así el marcador queda bien amplio
    LaunchedEffect(Unit) {
        (context as? Activity)?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    }
    // Cuando salgamos de aquí, volvemos a la orientación normal (vertical)
    DisposableEffect(Unit) {
        onDispose {
            (context as? Activity)?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }

    // Guardamos el tiempo que se selecciona y el que va quedando en el cronómetro
    var tiempoSeleccionado by remember { mutableStateOf(90_00) }  // 1:30 en centésimas
    var tiempoRestante by remember { mutableStateOf(tiempoSeleccionado) }
    var isRunning by remember { mutableStateOf(false) }
    var mostrarDialogoTiempo by remember { mutableStateOf(true) } // para mostrar el selector al iniciar

    // Puntos, penalizaciones y senshu de los competidores (AKA y AO)
    var puntosAka by remember { mutableStateOf(0) }
    var puntosAo by remember { mutableStateOf(0) }
    var penalizacionesAka by remember { mutableStateOf(0) }
    var penalizacionesAo by remember { mutableStateOf(0) }
    var senshuAka by remember { mutableStateOf(false) }
    var senshuAo by remember { mutableStateOf(false) }

    // Cronómetro descendente: mientras esté corriendo, resta tiempo cada 10 ms
    LaunchedEffect(isRunning) {
        while (isRunning && tiempoRestante > 0) {
            delay(10)
            tiempoRestante--
        }
        if (tiempoRestante == 0) isRunning = false // para cuando se acaba el tiempo
    }

    // Diálogo para que el usuario elija la duración del combate
    if (mostrarDialogoTiempo) {
        AlertDialog(
            onDismissRequest = {}, // no se puede cerrar tocando fuera, para evitar líos
            confirmButton = {},    // sin botón de confirmar, solo elegir directo
            title = { Text("Selecciona el tiempo") },
            text = {
                Column {
                    // Opciones para elegir 1:30, 2:00 o 3:00 minutos
                    listOf(90_00 to "1:30", 120_00 to "2:00", 180_00 to "3:00").forEach { (valor, texto) ->
                        Button(
                            onClick = {
                                tiempoSeleccionado = valor
                                tiempoRestante = valor
                                mostrarDialogoTiempo = false
                            },
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                        ) { Text(texto) }
                    }
                }
            }
        )
    }

    // Convertimos el tiempo para mostrarlo bonito en formato mm:ss.cc
    val minutos = tiempoRestante / 6000
    val segundos = (tiempoRestante / 100) % 60
    val centesimas = tiempoRestante % 100
    val formatoTiempo = String.format("%02d:%02d.%02d", minutos, segundos, centesimas)

    Box(modifier = Modifier.fillMaxSize()) {
        // Fondo dividido en rojo y azul para AKA y AO
        Row(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.weight(1f).fillMaxHeight().background(Color.Red))
            Box(modifier = Modifier.weight(1f).fillMaxHeight().background(Color.Blue))
        }

        // Aquí van los controles encima del fondo
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Controles para AKA (rojo)
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                CompetidorLayout(
                    nombre = "AKA",
                    color = Color.White,
                    puntos = puntosAka,
                    penalizaciones = penalizacionesAka,
                    senshu = senshuAka,
                    onPunto = { puntosAka++ },
                    onQuitarPunto = { if (puntosAka > 0) puntosAka-- },
                    onPenalizacion = { if (penalizacionesAka < 5) penalizacionesAka++ },
                    onQuitarPenalizacion = { if (penalizacionesAka > 0) penalizacionesAka-- },
                    onSenshu = {
                        // Solo puede haber uno con senshu activo, así que si pulsas aquí
                        // se activa para AKA y se desactiva para AO, o se quita si ya estaba activo
                        if (senshuAka) senshuAka = false
                        else {
                            senshuAka = true
                            senshuAo = false
                        }
                    }
                )
            }

            // Parte central: cronómetro y botones de control
            Column(
                modifier = Modifier.width(220.dp).fillMaxHeight(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = formatoTiempo,
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ControlButton(text = if (isRunning) "Parar" else "Iniciar") {
                        isRunning = !isRunning
                    }
                    ControlButton("Reiniciar") {
                        isRunning = false
                        tiempoRestante = tiempoSeleccionado
                    }
                }
            }

            // Controles para AO (azul), igual que AKA pero en azul
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                CompetidorLayout(
                    nombre = "AO",
                    color = Color.White,
                    puntos = puntosAo,
                    penalizaciones = penalizacionesAo,
                    senshu = senshuAo,
                    onPunto = { puntosAo++ },
                    onQuitarPunto = { if (puntosAo > 0) puntosAo-- },
                    onPenalizacion = { if (penalizacionesAo < 5) penalizacionesAo++ },
                    onQuitarPenalizacion = { if (penalizacionesAo > 0) penalizacionesAo-- },
                    onSenshu = {
                        if (senshuAo) senshuAo = false
                        else {
                            senshuAo = true
                            senshuAka = false
                        }
                    }
                )
            }
        }

        // Botón para salir de la pantalla y volver al menú principal
        Box(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp).align(Alignment.BottomCenter),
            contentAlignment = Alignment.Center
        ) {
            Button(
                onClick = {
                    navController.navigate("pantalla_inicio") {
                        popUpTo("pantalla_marcador") { inclusive = true }
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White.copy(alpha = 0.15f),
                    contentColor = Color.White
                )
            ) {
                Text("Salir")
            }
        }
    }
}

// Componente para mostrar los controles de cada competidor (AKA o AO)
@Composable
fun CompetidorLayout(
    nombre: String,
    color: Color,
    puntos: Int,
    penalizaciones: Int,
    senshu: Boolean,
    onPunto: () -> Unit,
    onQuitarPunto: () -> Unit,
    onPenalizacion: () -> Unit,
    onQuitarPenalizacion: () -> Unit,
    onSenshu: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(nombre, fontSize = 36.sp, fontWeight = FontWeight.Bold, color = color)
        Text("$puntos", fontSize = 60.sp, fontWeight = FontWeight.ExtraBold, color = color)

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ControlButton("+", onPunto)
            ControlButton("-", onQuitarPunto)
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text("Chui", fontSize = 24.sp, color = color)

        // Penalizaciones en círculos amarillos (hasta 5)
        Row {
            repeat(5) { index ->
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .padding(2.dp)
                        .background(
                            color = if (index < penalizaciones) Color.Yellow else Color.LightGray,
                            shape = CircleShape
                        )
                )
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ControlButton("+", onPenalizacion)
            ControlButton("-", onQuitarPenalizacion)
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Botón de senshu, se pone amarillo si está activo
        Button(
            onClick = onSenshu,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (senshu) Color.Yellow else Color.DarkGray,
                contentColor = Color.Black
            ),
            modifier = Modifier.size(40.dp)
        ) {
            Text("Senshu", color = Color.Black)
        }
    }
}

// Botón simple para todos los controles (+, -, iniciar, parar, etc)
@Composable
fun ControlButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.height(40.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White.copy(alpha = 0.15f),
            contentColor = Color.White
        )
    ) {
        Text(text = text, color = Color.White)
    }
}
