package com.example.taikaisensei.interfaz.pantallas// Importaciones necesarias para la interfaz y funcionalidad
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

// Resumen: PantallaLogin.kt es la interfaz de usuario de la pantalla de inicio de sesión de la aplicación. Utiliza Firebase para autenticar al usuario con su correo electrónico y contraseña.
// Si el inicio de sesión es exitoso, navega a la pantalla principal. Si hay un error, muestra mensajes de error debajo de los campos de correo o contraseña.

@Composable
fun PantallaLogin(
    navController: NavHostController,
    onLoginSuccess: (FirebaseUser) -> Unit,
    onLoginFailure: (String) -> Unit
) {
    val auth = FirebaseAuth.getInstance()

    // States para email, contraseña y si hay error
    val email = remember { mutableStateOf("") }  // Almacena el valor del campo de correo electrónico
    val password = remember { mutableStateOf("") }  // Almacena el valor del campo de contraseña
    val emailError = remember { mutableStateOf(false) }  // Almacena el estado de error para el correo
    val passwordError = remember { mutableStateOf(false) }  // Almacena el estado de error para la contraseña

    // Gradiente para el fondo de la pantalla
    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFA2A2A2), // Gris claro
            Color(0xFFFFFFFF), // Blanco
            Color(0xFFA2A2A2)  // Gris claro
        )
    )

    // Fuentes de interacción para manejar la animación del botón
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()  // Detecta si el botón está siendo presionado
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,  // Aplica animación de escala al botón cuando se presiona
        label = "BotónRebote"
    )
    val haptic = LocalHapticFeedback.current  // Feedback táctil

    // Animación de colores del botón
    val topColor by animateColorAsState(
        targetValue = if (isPressed) Color(0xFF4A4A4A) else Color(0xFF3A3A3A),  // Colores del botón cuando es presionado
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

    // Gradiente del botón con los colores animados
    val buttonGradient = Brush.verticalGradient(
        colors = listOf(topColor, centerColor, bottomColor)
    )

    // Caja principal con fondo gradiente
    Box(
        modifier = Modifier
            .fillMaxSize()  // La caja llena toda la pantalla
            .background(brush = backgroundGradient)  // Aplica el gradiente al fondo
    ) {
        // Contenedor de columnas con alineación centrada y espaciado
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,  // Alinea todo al centro horizontalmente
            verticalArrangement = Arrangement.spacedBy(24.dp),  // Espaciado entre elementos de la columna
            modifier = Modifier.align(Alignment.Center)  // Alinea la columna al centro de la pantalla
        ) {
            // LOGO
            Box(
                modifier = Modifier
                    .height(200.dp)  // Establece el alto del logo
                    .width(200.dp)  // Establece el ancho del logo
            ) {
                Image(
                    painter = painterResource(id = R.drawable.taikaisensei_logo),  // Carga el logo desde los recursos
                    contentDescription = "Logo TaikaiSensei",  // Descripción del contenido para accesibilidad
                    contentScale = ContentScale.Fit,  // Escala el logo para que se ajuste sin distorsión
                    modifier = Modifier.fillMaxSize()  // Hace que la imagen ocupe todo el espacio disponible en el box
                )
            }

            // Campo de correo electrónico
            TextField(
                value = email.value,  // Valor del campo de texto
                onValueChange = { email.value = it },  // Actualiza el valor del correo al escribir
                label = { Text("Correo electrónico") },  // Etiqueta del campo
                isError = emailError.value,  // Aplica un estilo de error si hay error en el correo
                modifier = Modifier
                    .fillMaxWidth()  // Hace que el campo ocupe todo el ancho disponible
                    .padding(horizontal = 24.dp),  // Aplica un padding horizontal
                singleLine = true  // Limita el campo a una sola línea
            )

            // Mensaje de error para el correo
            if (emailError.value) {
                Text(
                    text = "Correo no registrado",  // Mensaje de error si el correo no está registrado
                    color = MaterialTheme.colorScheme.error,  // Color de texto para el error
                    style = MaterialTheme.typography.bodySmall,  // Estilo pequeño para el mensaje de error
                    modifier = Modifier.padding(start = 24.dp)  // Aplica un padding a la izquierda
                )
            }

            // Campo de contraseña
            TextField(
                value = password.value,  // Valor del campo de texto
                onValueChange = { password.value = it },  // Actualiza el valor de la contraseña
                label = { Text("Contraseña") },  // Etiqueta del campo
                isError = passwordError.value,  // Aplica un estilo de error si hay error en la contraseña
                modifier = Modifier
                    .fillMaxWidth()  // Hace que el campo ocupe todo el ancho disponible
                    .padding(horizontal = 24.dp),  // Aplica un padding horizontal
                visualTransformation = PasswordVisualTransformation()  // Oculta el texto de la contraseña
            )

            // Mensaje de error para la contraseña
            if (passwordError.value) {
                Text(
                    text = "Contraseña incorrecta",  // Mensaje de error si la contraseña es incorrecta
                    color = MaterialTheme.colorScheme.error,  // Color de texto para el error
                    style = MaterialTheme.typography.bodySmall,  // Estilo pequeño para el mensaje de error
                    modifier = Modifier.padding(start = 24.dp)  // Aplica un padding a la izquierda
                )
            }

            // Botón de inicio de sesión
            Box(
                modifier = Modifier
                    .wrapContentWidth()  // El ancho del botón se ajusta al contenido
                    .height(60.dp)  // Establece la altura del botón
                    .offset(y = 6.dp)  // Añade un pequeño desplazamiento vertical
                    .scale(scale)  // Aplica animación de escala
                    .clip(RoundedCornerShape(40.dp))  // Aplica bordes redondeados
                    .background(brush = buttonGradient)  // Aplica el gradiente como fondo
                    .clickable(
                        interactionSource = interactionSource,  // Fuente de interacción para detectar presiones
                        indication = null  // No muestra indicación visual al presionar
                    ) {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)  // Feedback táctil al presionar
                        // Llamar a la función de inicio de sesión
                        loginWithFirebase(
                            email.value, password.value,
                            onLoginSuccess = {
                                // Si es exitoso, navegar a la pantalla de inicio
                                navController.navigate("pantalla_inicio") {
                                    popUpTo("pantalla_login") { inclusive = true }  // Vuelve atrás hasta la pantalla de login
                                }
                            },
                            onLoginFailure = { errorMessage ->  // Si falla, mostrar errores en los campos correspondientes
                                if (errorMessage.contains("email")) {
                                    emailError.value = true  // Error en el correo
                                } else {
                                    passwordError.value = true  // Error en la contraseña
                                }
                                onLoginFailure(errorMessage)  // Llama a la función de manejo de fallos
                            }
                        )
                    }
                    .padding(horizontal = 24.dp),  // Aplica padding horizontal al botón
                contentAlignment = Alignment.Center  // Centra el contenido dentro del botón
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,  // Alinea verticalmente al centro
                    horizontalArrangement = Arrangement.Center,  // Alinea horizontalmente al centro
                    modifier = Modifier.padding(horizontal = 16.dp)  // Aplica padding horizontal a la fila
                ) {
                    Icon(
                        imageVector = Icons.Filled.Person,  // Icono de persona (usado para el login)
                        contentDescription = "Iniciar sesión",  // Descripción para accesibilidad
                        tint = Color.White,  // Color blanco para el icono
                        modifier = Modifier.size(24.dp)  // Tamaño del icono
                    )
                    Spacer(modifier = Modifier.width(8.dp))  // Espaciado entre el icono y el texto
                    Text(
                        text = "Iniciar sesión",  // Texto del botón
                        color = Color.White,  // Color del texto
                        fontSize = 16.sp  // Tamaño de la fuente
                    )
                }
            }
        }
    }
}

// Lógica de inicio de sesión con Firebase
fun loginWithFirebase(
    email: String,
    password: String,
    onLoginSuccess: (FirebaseUser) -> Unit,
    onLoginFailure: (String) -> Unit
) {
    val auth = FirebaseAuth.getInstance()  // Obtiene una instancia de FirebaseAuth

    auth.signInWithEmailAndPassword(email, password)  // Intenta autenticar al usuario
        .addOnCompleteListener { task ->  // Añade un listener para el resultado de la autenticación
            if (task.isSuccessful) {
                val user = auth.currentUser  // Obtiene el usuario actual si la autenticación es exitosa
                if (user != null) {
                    // Si el inicio de sesión es exitoso, llamamos al callback
                    onLoginSuccess(user)
                }
            } else {
                // Si el inicio de sesión falla, mostramos un mensaje de error
                val errorMessage = task.exception?.localizedMessage ?: "Error desconocido"
                onLoginFailure(errorMessage)  // Llama al callback de error
            }
        }
}
