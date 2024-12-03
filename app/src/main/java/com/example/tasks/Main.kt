package com.example.tasks

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

class Main : ComponentActivity() {

    private val habitsList = mutableListOf<Habit>()
    private lateinit var adapter: HabitAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.index)

        val jsonSave = JsonSave(this)
       habitsList.addAll(jsonSave.loadHabits())

        val habitsRecyclerView: RecyclerView = findViewById(R.id.habitsRecyclerView)
        val showPopupButton: Button = findViewById(R.id.addHabitButton)

        adapter = HabitAdapter(this, habitsList) { habit ->
            val intent = Intent(this, AddHabitActivity::class.java)
            intent.putExtra("habitId", habit.id)  // Pasar el id
            intent.putExtra("habitName", habit.name)
            intent.putExtra("habitDescription", habit.description)
            intent.putExtra("frequency", habit.frequency)
            // Validar días seleccionados
            intent.putExtra("selectedDays", habit.selectedDays.toBooleanArray())
            intent.putExtra("reminderHour", habit.reminderHour)

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
                    saveHabitList(habitsList, this@Main)
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
            val habitId = data.getIntExtra("habitId", -1)
            val frequency = data?.getStringExtra("frequency") ?: return
            val selectedDays = data?.getBooleanArrayExtra("selectedDays")?.toList() ?: return
            val reminder= data.getLongExtra("reminderHour",-1L)
            //val reminderHour = data?.getSerializableExtra("reminderHour") as? Calendar // Hora de recordatorio
            //Toast.makeText(applicationContext, (reminderHour.toString() + ":M:"+reminder), Toast.LENGTH_SHORT).show()



            // Si la frecuencia es "Diariamente", asignar todos los días
            val daysToSet = if (frequency == "Diariamente") {
                listOf(true, true, true, true, true, true, true) // Todos los días de la semana
            } else {
                selectedDays // Los días seleccionados por el usuario
            }

            if (requestCode == REQUEST_ADD_HABIT) {
                val newHabit = Habit(
                    name = habitName,
                    description = habitDescription,
                    selectedDays = daysToSet,
                    reminderHour =reminder,
                    frequency = frequency // Agregar la frecuencia
                )

                CoroutineScope(Dispatchers.IO).launch {
                    withContext(Dispatchers.Main) {
                        habitsList.add(newHabit)
                        adapter.notifyItemInserted(habitsList.size - 1)
                        saveHabitList(habitsList, this@Main)
                    }
                }
            } else if (requestCode == REQUEST_EDIT_HABIT && habitId != -1) {
                val updatedHabit = Habit(
                    id = habitId,
                    name = habitName,
                    description = habitDescription,
                    selectedDays = daysToSet,
                    reminderHour = reminder,
                    frequency = frequency // Actualizar la frecuencia
                )

                CoroutineScope(Dispatchers.IO).launch {
                    withContext(Dispatchers.Main) {
                        val position = habitsList.indexOfFirst { it.id == habitId }
                        if (position >= 0) {
                            habitsList[position] = updatedHabit
                            adapter.notifyItemChanged(position)
                            saveHabitList(habitsList, this@Main)
                        }
                    }
                }
            }
        }
    }



    // Método para guardar la lista de hábitos
    fun saveHabitList(habits: List<Habit>, context: Context) {
        val jsonSave = JsonSave(context)
        jsonSave.saveHabits(habits)
    }

    companion object {
        const val REQUEST_ADD_HABIT = 1
        const val REQUEST_EDIT_HABIT = 2
    }
}
