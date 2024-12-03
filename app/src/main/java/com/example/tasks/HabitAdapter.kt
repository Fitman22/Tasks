package com.example.tasks

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import java.util.Calendar

class HabitAdapter(
    private val context: Context,
    private val habitsList: MutableList<Habit>,
    private val onHabitClick: (Habit) -> Unit
) : RecyclerView.Adapter<HabitAdapter.HabitViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.habit_item, parent, false)
        return HabitViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: HabitViewHolder, position: Int) {
        val habit = habitsList[position]

        holder.habitNameTextView.text = habit.name
        holder.habitDescriptionTextView.text = habit.description

        // Mostrar los días seleccionados (si es "Diariamente", mostrar todos los días)
        val daysText = if (habit.frequency == "Diariamente") {
            "Todos los días"
        } else {
            holder.habitDaysTextView.visibility = View.VISIBLE
            val daysOfWeek = listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo")

            val selectedDays = habit.selectedDays.mapIndexed { index, isSelected ->
                if (isSelected) daysOfWeek.getOrNull(index) else null
            }.filterNotNull().joinToString(", ")

            if (selectedDays.isNotEmpty()) {
                "Días seleccionados: $selectedDays"
            } else {
                "No se ha seleccionado ningún día"
            }
        }
        holder.habitFrequencyTextView.text = "Frecuencia: ${habit.frequency}"
        holder.habitDaysTextView.text = daysText

        // Mostrar la hora de recordatorio
        val calendar = Calendar.getInstance().apply {
            timeInMillis =  habit.reminderHour
        }

        // Puedes acceder a la hora y minutos de este Calendar
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
            val timeText = String.format("%02d:%02d", hour, minute)
            holder.habitTimeTextView.text = "Hora: $timeText"

        holder.itemView.setOnClickListener {
            onHabitClick(habit)
        }
    }



    override fun getItemCount(): Int {
        return habitsList.size
    }

    inner class HabitViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val habitNameTextView: TextView = itemView.findViewById(R.id.habitName)
        val habitDescriptionTextView: TextView = itemView.findViewById(R.id.habitDescription)
        val habitFrequencyTextView: TextView = itemView.findViewById(R.id.habitFrequency)
        val habitDaysTextView: TextView = itemView.findViewById(R.id.habitSelectedDays)
        val habitTimeTextView: TextView = itemView.findViewById(R.id.habitTime)
    }
}
