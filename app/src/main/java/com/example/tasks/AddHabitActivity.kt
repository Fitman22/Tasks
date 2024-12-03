package com.example.tasks

import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import android.content.Intent
import android.view.View
import android.widget.AdapterView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.CheckBox
import java.util.Calendar

class AddHabitActivity : ComponentActivity() {
    val selectedTime = Calendar.getInstance()
    var HourSelect = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.popup)

        val habitNameEditText: EditText = findViewById(R.id.etHabitName)
        val habitDescriptionEditText: EditText = findViewById(R.id.ehabitDescription)
        val saveButton: Button = findViewById(R.id.btnSaveHabit)
        val spinnerFrequency: Spinner = findViewById(R.id.spinnerFrequency)
        val weekDaysLayout: LinearLayout = findViewById(R.id.weekDaysLayout)
        val timeButton: Button = findViewById(R.id.btnSelectTime)
        val textTime: TextView = findViewById(R.id.timeTextView)

        // Checkboxes for days of the week
        val checkBoxes = listOf(
            findViewById<CheckBox>(R.id.checkboxMonday),
            findViewById<CheckBox>(R.id.checkboxTuesday),
            findViewById<CheckBox>(R.id.checkboxWednesday),
            findViewById<CheckBox>(R.id.checkboxThursday),
            findViewById<CheckBox>(R.id.checkboxFriday),
            findViewById<CheckBox>(R.id.checkboxSaturday),
            findViewById<CheckBox>(R.id.checkboxSunday)
        )


        // Obtener datos si estamos editando un hábito
        val habitId = intent.getIntExtra("habitId", -1)
        val habitName = intent.getStringExtra("habitName")
        val habitDescription = intent.getStringExtra("habitDescription")
        val FrequencySelect = intent.getStringExtra("frequency")
        val DaysSelect = intent.getBooleanArrayExtra("selectedDays")
        val TextTimeTx = intent.getLongExtra("reminderHour",-1L)
        habitName?.let { habitNameEditText.setText(it) }
        habitDescription?.let { habitDescriptionEditText.setText(it) }
        FrequencySelect?.let {
            val options = resources.getStringArray(R.array.frequency_options)
            val position = options.indexOf(it)
            spinnerFrequency.setSelection(position) }
        DaysSelect?.forEachIndexed{ i, value ->
            checkBoxes[i].isChecked=value

        }
        if (TextTimeTx != -1L) {
            HourSelect=true
            val calendar = Calendar.getInstance().apply {
                timeInMillis = TextTimeTx
            }
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)
            selectedTime.apply {
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
            }
            // Puedes acceder a la hora y minutos de este Calendar
            textTime.text = String.format("%02d:%02d", hour, minute)
        }
        else{
            textTime.text = "Seleccione una Hora"
        }
        val closeButton: Button = findViewById(R.id.popupCloseButton)
        closeButton.setOnClickListener {
            finish()
        }

        saveButton.setOnClickListener {
            val name = habitNameEditText.text.toString()
            val description = habitDescriptionEditText.text.toString()
            val frequency = spinnerFrequency.selectedItem.toString()

            // Obtener los días seleccionados

            val selectedDays = checkBoxes.map { it.isChecked }
            // Obtener la hora seleccionada
            val reminderHour = selectedTime.timeInMillis
            if (name.isEmpty()) {
                Toast.makeText(this, "Por favor, ingresa un nombre", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (description.isEmpty()) {
                Toast.makeText(this, "Por favor, ingresa una descripción", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (!HourSelect) {
                Toast.makeText(this, "Por favor, ingresa una Hora", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
                val resultIntent = Intent()
                resultIntent.putExtra("habitId", habitId)  // Pasa el id
                resultIntent.putExtra("newHabitName", name)
                resultIntent.putExtra("newHabitDescription", description)
                resultIntent.putExtra("frequency", frequency)  // Frecuencia seleccionada
                resultIntent.putExtra("selectedDays", selectedDays.toBooleanArray())  // Días seleccionados
                resultIntent.putExtra("reminderHour", reminderHour)  // Hora seleccionada
                setResult(RESULT_OK, resultIntent)
                finish()

        }

        spinnerFrequency.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (position == 1) { // "Semanualmente"
                    weekDaysLayout.visibility = View.VISIBLE
                } else { // "Diariamente"
                    weekDaysLayout.visibility = View.GONE
                }
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
                // Aquí puedes poner una lógica si es necesario
            }
        }

        timeButton.setOnClickListener {
            // Obtenemos la hora y minuto actual
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            // Creamos un TimePickerDialog
            val timePickerDialog = TimePickerDialog(
                this,  // Contexto
                { _, selectedHour, selectedMinute ->
                    // Formateamos la hora seleccionada y la mostramos en el TextView
                    val formattedTime = String.format("%02d:%02d", selectedHour, selectedMinute)
                    textTime.text = "$formattedTime"

                    // Guardamos la hora seleccionada en un Calendar
                     selectedTime.apply {
                        set(Calendar.HOUR_OF_DAY, selectedHour)
                        set(Calendar.MINUTE, selectedMinute)
                    }
                    HourSelect=true

                },
                hour, minute, true  // true para formato 24 horas
            )

            // Mostramos el TimePickerDialog
            timePickerDialog.show()
        }



    }
}
