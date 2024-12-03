package com.example.tasks

import android.app.DatePickerDialog
import android.widget.TextView
import java.util.*

data class Habit(
    val id: Int = generateId(),
    val name: String,
    val description: String,
    val selectedDays: List<Boolean> = listOf(false, false, false, false, false, false, false), // Lista de días seleccionados
    val reminderHour: Long, // Hora del recordatorio
    val frequency: String = "Diariamente"
) {
    companion object {
        private var idCounter: Int = 0

        private fun generateId(): Int {
            idCounter += 1
            return idCounter
        }
    }

}
