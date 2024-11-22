package com.example.tasks

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import android.content.Intent

class AddHabitActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.popup)

        val habitNameEditText: EditText = findViewById(R.id.etHabitName)
        val habitDescriptionEditText: EditText = findViewById(R.id.ehabitDescription)
        val saveButton: Button = findViewById(R.id.btnSaveHabit)

        // Obtener datos si estamos editando un hábito
        val habitId = intent.getIntExtra("habitId", -1)
        val habitName = intent.getStringExtra("habitName")
        val habitDescription = intent.getStringExtra("habitDescription")

        habitName?.let { habitNameEditText.setText(it) }
        habitDescription?.let { habitDescriptionEditText.setText(it) }

        val closeButton: Button = findViewById(R.id.popupCloseButton)
        closeButton.setOnClickListener {
            finish()
        }

        saveButton.setOnClickListener {
            val name = habitNameEditText.text.toString()
            val description = habitDescriptionEditText.text.toString()

            if (name.isNotEmpty() && description.isNotEmpty()) {
                val resultIntent = Intent()
                resultIntent.putExtra("habitId", habitId)  // Pasa el id
                resultIntent.putExtra("newHabitName", name)
                resultIntent.putExtra("newHabitDescription", description)
                setResult(RESULT_OK, resultIntent)
                finish()
            } else {
                Toast.makeText(this, "Por favor, ingresa un nombre y descripción", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
