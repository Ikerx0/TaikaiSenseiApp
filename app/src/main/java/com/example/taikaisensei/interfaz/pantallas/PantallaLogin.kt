package com.example.taikaisensei.interfaz.pantallas

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.taikaisensei.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

// Pantalla de inicio de sesión con campos para ingresar credenciales y botón para iniciar sesión.

@Composable
fun PantallaLogin(
    navController: NavHostController,
    onLoginSuccess: (FirebaseUser) -> Unit,
    onLoginFailure: (String) -> Unit
) {
    val auth = FirebaseAuth.getInstance()

    // Variables para manejar los datos ingresados y estados de error
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val emailError = remember { mutableStateOf(false) }
    val passwordError = remember { mutableStateOf(false) }

    // Gradiente para el fondo de la pantalla
    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFA2A2A2),
            Color(0xFFFFFFFF),
            Color(0xFFA2A2A2)
        )
    )

    // Manejo de la interacción táctil y animación del botón
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        label = "BotónRebote"
    )
    val haptic = LocalHapticFeedback.current

    // Animación del color del botón según estado de interacción
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

    val buttonGradient = Brush.verticalGradient(
        colors = listOf(topColor, centerColor, bottomColor)
    )

    // Contenedor principal de la pantalla con fondo
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = backgroundGradient)
    ) {
        // Organización vertical centrada de elementos
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp),
            modifier = Modifier.align(Alignment.Center)
        ) {
            // Logo o imagen principal
            Box(
                modifier = Modifier
                    .height(200.dp)
                    .width(200.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.taikaisensei_logo),
                    contentDescription = "Logo",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Campo para ingresar correo o usuario
            TextField(
                value = email.value,
                onValueChange = { email.value = it },
                label = { Text("Correo electrónico") },
                isError = emailError.value,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                singleLine = true
            )

            // Mensaje de error para correo
            if (emailError.value) {
                Text(
                    text = "Correo no registrado",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 24.dp)
                )
            }

            // Campo para ingresar contraseña
            TextField(
                value = password.value,
                onValueChange = { password.value = it },
                label = { Text("Contraseña") },
                isError = passwordError.value,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                visualTransformation = PasswordVisualTransformation()
            )

            // Mensaje de error para contraseña
            if (passwordError.value) {
                Text(
                    text = "Contraseña incorrecta",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 24.dp)
                )
            }

            // Botón para iniciar sesión
            Box(
                modifier = Modifier
                    .wrapContentWidth()
                    .height(60.dp)
                    .offset(y = 6.dp)
                    .scale(scale)
                    .clip(RoundedCornerShape(40.dp))
                    .background(brush = buttonGradient)
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null
                    ) {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        loginWithFirebase(
                            email.value, password.value,
                            onLoginSuccess = {
                                navController.navigate("pantalla_inicio") {
                                    popUpTo("pantalla_login") { inclusive = true }
                                }
                            },
                            onLoginFailure = { errorMessage ->
                                if (errorMessage.contains("email")) {
                                    emailError.value = true
                                } else {
                                    passwordError.value = true
                                }
                                onLoginFailure(errorMessage)
                            }
                        )
                    }
                    .padding(horizontal = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = "Iniciar sesión",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Iniciar sesión",
                        color = Color.White,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

// Función para autenticación usando Firebase
fun loginWithFirebase(
    email: String,
    password: String,
    onLoginSuccess: (FirebaseUser) -> Unit,
    onLoginFailure: (String) -> Unit
) {
    val auth = FirebaseAuth.getInstance()

    auth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = auth.currentUser
                if (user != null) {
                    onLoginSuccess(user)
                }
            } else {
                val errorMessage = task.exception?.localizedMessage ?: "Error desconocido"
                onLoginFailure(errorMessage)
            }
        }
}
