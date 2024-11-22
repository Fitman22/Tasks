package com.example.tasks


import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class Main : ComponentActivity() {

    private val habitsList = mutableListOf<Habit>()
    private lateinit var adapter: HabitAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.index)

        val habitsRecyclerView: RecyclerView = findViewById(R.id.habitsRecyclerView)
        val showPopupButton: Button = findViewById(R.id.addHabitButton)

        adapter = HabitAdapter(this, habitsList) { habit ->
            // Este es el evento que ocurre cuando se hace clic en un hábito
            val intent = Intent(this, AddHabitActivity::class.java)
            intent.putExtra("habitId", habit.id)  // Pasar el id
            intent.putExtra("habitName", habit.name)
            intent.putExtra("habitDescription", habit.description)
            startActivityForResult(intent, REQUEST_EDIT_HABIT)
        }

        habitsRecyclerView.adapter = adapter
        habitsRecyclerView.layoutManager = LinearLayoutManager(this)

        // Llamamos al Intent para agregar un nuevo hábito
        showPopupButton.setOnClickListener {
            val habitId = habitsList.size + 1 // Un id simple basado en el tamaño de la lista
            val intent = Intent(this, AddHabitActivity::class.java)
            intent.putExtra("habitId", habitId)  // Pasa el id al formulario de agregar
            startActivityForResult(intent, REQUEST_ADD_HABIT)
        }

        // Configuramos el swipe para eliminar
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            0, ItemTouchHelper.LEFT
        ) {
            override fun onMove(
                recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    habitsList.removeAt(position)
                    adapter.notifyItemRemoved(position)
                    Toast.makeText(applicationContext, "Hábito eliminado", Toast.LENGTH_SHORT).show()
                }
            }
        })

        itemTouchHelper.attachToRecyclerView(habitsRecyclerView)
    }

    // Método para recibir el resultado de la actividad AddHabitActivity (ya sea al agregar o editar)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            val habitName = data?.getStringExtra("newHabitName") ?: return
            val habitDescription = data.getStringExtra("newHabitDescription") ?: return
            val habitId = data.getIntExtra("habitId", -1) // Asegúrate de obtener el id también

            if (habitId != -1) {
                if (requestCode == REQUEST_ADD_HABIT) {
                    // Agregar un nuevo hábito
                    habitsList.add(Habit(habitId, habitName, habitDescription))
                    adapter.notifyItemInserted(habitsList.size - 1)
                } else if (requestCode == REQUEST_EDIT_HABIT) {
                    // Buscar el hábito por id y actualizarlo
                    val habitPosition = habitsList.indexOfFirst { it.id == habitId }
                    if (habitPosition >= 0) {
                        habitsList[habitPosition] = Habit(habitId, habitName, habitDescription)
                        adapter.notifyItemChanged(habitPosition)
                    }
                }
            }
        }
    }


    companion object {
        const val REQUEST_ADD_HABIT = 1
        const val REQUEST_EDIT_HABIT = 2
    }
}
