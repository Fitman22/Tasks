package com.example.tasks

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class JsonSave(private val context: Context) {  // Recibe el contexto como parámetro

    // Usamos el contexto para obtener SharedPreferences
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("HabitApp", Context.MODE_PRIVATE)
    private val gson = Gson()

    // Método para guardar hábitos
    fun saveHabits(habits: List<Habit>) {
        try {
            val json = gson.toJson(habits)
            sharedPreferences.edit().putString("habit_list", json).apply()
        } catch (e: Exception) {
            e.printStackTrace()  // Capturar cualquier error
        }
    }

    // Método para cargar hábitos
    fun loadHabits(): MutableList<Habit> {
        try {
            val json = sharedPreferences.getString("habit_list", null)
            return if (json != null) {
                val type = object : TypeToken<MutableList<Habit>>() {}.type
                gson.fromJson(json, type)
            } else {
                mutableListOf()
            }
        } catch (e: Exception) {
            e.printStackTrace()  // Capturar cualquier error
            return mutableListOf()  // Devolver una lista vacía en caso de error
        }
    }

}
