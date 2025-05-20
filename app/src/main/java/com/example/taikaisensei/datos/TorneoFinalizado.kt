package com.example.taikaisensei.datos

data class TorneoFinalizado(
    val nombreTorneo: String = "",
    val categoria: String = "",
    val campeon: Competidor = Competidor(),
    val subcampeon: Competidor = Competidor(),
    val timestamp: com.google.firebase.Timestamp? = null
)
