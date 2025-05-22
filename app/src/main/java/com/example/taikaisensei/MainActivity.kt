package com.example.taikaisensei

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.navigation.compose.*
import com.example.taikaisensei.datos.Competidor
import com.example.taikaisensei.interfaz.pantallas.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Controlador de navegación para manejar las pantallas
            val navController = rememberNavController()

            // Estado para guardar datos compartidos entre pantallas
            var competidoresSeleccionados by remember { mutableStateOf<List<Competidor>>(emptyList()) }
            var nombreTorneo by remember { mutableStateOf("") }
            var categoriaTorneo by remember { mutableStateOf("") }

            Surface {
                // Definición del grafo de navegación y pantallas disponibles
                NavHost(navController = navController, startDestination = "pantalla_login") {

                    // Pantalla de Login: entrada al sistema
                    composable("pantalla_login") {
                        PantallaLogin(
                            navController = navController,
                            onLoginSuccess = {
                                // Navegar a la pantalla de inicio y eliminar login del back stack
                                navController.navigate("pantalla_inicio") {
                                    popUpTo("pantalla_login") { inclusive = true }
                                }
                            },
                            onLoginFailure = { errorMessage ->
                                // Aquí se puede manejar errores de login (no implementado)
                            }
                        )
                    }

                    // Pantalla de Inicio: menú principal con opciones
                    composable("pantalla_inicio") {
                        PantallaInicio(
                            onCrearDiagramaClick = {
                                // Navegar a la pantalla para elegir competidores
                                navController.navigate("pantalla_competidores")
                            },
                            onVerHistorialClick = {
                                // Navegar a la pantalla de historial de torneos
                                navController.navigate("pantalla_historial")
                            },
                            onMarcadorClick = {
                                // Navegar a la pantalla del marcador en tiempo real
                                navController.navigate("pantalla_marcador")
                            }
                        )
                    }

                    // Pantalla para seleccionar y configurar competidores
                    composable("pantalla_competidores") {
                        PantallaCompetidores(
                            onFinalizarClick = { competidores, nombre, categoria ->
                                // Guardar datos y avanzar a la pantalla del diagrama
                                competidoresSeleccionados = competidores
                                nombreTorneo = nombre
                                categoriaTorneo = categoria
                                navController.navigate("pantalla_diagrama") {
                                    popUpTo("pantalla_competidores") { inclusive = true }
                                }
                            },
                            onVolverInicio = {
                                // Volver al menú principal
                                navController.navigate("pantalla_inicio") {
                                    popUpTo("pantalla_inicio") { inclusive = true }
                                }
                            }
                        )
                    }

                    // Pantalla del diagrama con los competidores
                    composable("pantalla_diagrama") {
                        PantallaDiagrama(
                            competidoresIniciales = competidoresSeleccionados,
                            nombreTorneo = nombreTorneo,
                            categoriaTorneo = categoriaTorneo,
                            navController = navController
                        )
                    }

                    // Pantalla del historial
                    composable("pantalla_historial") {
                        PantallaHistorial(navController = navController)
                    }

                    // Pantalla del marcador
                    composable("pantalla_marcador") {
                        PantallaMarcador(navController = navController)
                    }
                }
            }
        }
    }
}
