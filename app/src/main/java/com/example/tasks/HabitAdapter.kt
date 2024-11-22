package com.example.tasks

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class HabitAdapter(
    private val context: Context,
    private val habitsList: MutableList<Habit>, // Cambié List a MutableList para permitir modificaciones
    private val onHabitClick: (Habit) -> Unit // Pasamos la función que se ejecutará cuando se haga clic en un hábito
) : RecyclerView.Adapter<HabitAdapter.HabitViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.item_habit, parent, false)
        return HabitViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: HabitViewHolder, position: Int) {
        val habit = habitsList[position]
        holder.habitNameTextView.text = habit.name
        holder.habitDescriptionTextView.text = habit.description

        // Establecemos el clic sobre el hábito para editarlo
        holder.itemView.setOnClickListener {
            onHabitClick(habit) // Llama a la función que pasamos al adapter
        }
    }

    override fun getItemCount(): Int {
        return habitsList.size
    }

    inner class HabitViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val habitNameTextView: TextView = itemView.findViewById(R.id.habitName)
        val habitDescriptionTextView: TextView = itemView.findViewById(R.id.habitDescription)
    }
}
