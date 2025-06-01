package com.example.app.utils

fun String.toSafePriceDouble(): Double {
    return this
        .replace("[^\\d.,]".toRegex(), "")  // Elimina caracteres no numéricos, pero mantiene comas y puntos
        .replace(".", "")                    // Elimina puntos de miles
        .replace(",", ".")                   // Cambia coma por punto decimal
        .toDoubleOrNull() ?: 0.0             // Si falla, devuelve 0.0
}