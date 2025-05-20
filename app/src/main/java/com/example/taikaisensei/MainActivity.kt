package com.example.taikaisensei

import PantallaLogin
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
            val navController = rememberNavController()
            var competidoresSeleccionados by remember { mutableStateOf<List<Competidor>>(emptyList()) }
            var nombreTorneo by remember { mutableStateOf("") }
            var categoriaTorneo by remember { mutableStateOf("") }

            Surface {
                NavHost(navController = navController, startDestination = "pantalla_login") {
                    composable("pantalla_login") {
                        PantallaLogin(
                            navController = navController,
                            onLoginSuccess = {
                                navController.navigate("pantalla_inicio") {
                                    popUpTo("pantalla_login") { inclusive = true }
                                }
                            },
                            onLoginFailure = { errorMessage ->
                                // Manejo de error (opcional)
                            }
                        )
                    }

                    composable("pantalla_inicio") {
                        PantallaInicio(
                            onCrearDiagramaClick = {
                                navController.navigate("pantalla_competidores")
                            }
                        )
                    }

                    composable("pantalla_competidores") {
                        PantallaCompetidores(
                            onFinalizarClick = { competidores, nombre, categoria ->
                                competidoresSeleccionados = competidores
                                nombreTorneo = nombre
                                categoriaTorneo = categoria
                                navController.navigate("pantalla_diagrama") {
                                    popUpTo("pantalla_competidores") { inclusive = true }
                                }
                            }
                        )
                    }

                    composable("pantalla_diagrama") {
                        PantallaDiagrama(
                            competidoresIniciales = competidoresSeleccionados,
                            nombreTorneo = nombreTorneo,
                            categoriaTorneo = categoriaTorneo,
                            navController = navController
                        )
                    }
                }
            }
        }
    }
}
