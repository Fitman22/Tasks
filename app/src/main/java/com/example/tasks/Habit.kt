package com.example.tasks


data class Habit(
    val id: Int = generateId(),
    val name: String,
    val description: String
) {
    companion object {
        private var idCounter: Int = 0

        private fun generateId(): Int {
            idCounter += 1
            return idCounter
        }
    }
}

