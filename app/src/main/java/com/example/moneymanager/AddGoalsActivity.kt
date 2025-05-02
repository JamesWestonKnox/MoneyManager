/**
 * AddGoalsActivity.kt
 *
 *
 *
 *
 *  Assistance provided by ChatGPT, OpenAI (2025). https://chat.openai.com
 */

package com.example.moneymanager

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class AddGoalsActivity : AppCompatActivity() {

    private lateinit var etGoalName: EditText
    private lateinit var etAmount: EditText
    private lateinit var btnSaveGoal: Button
    private lateinit var goalDB: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.bottom_sheet_new_goal)

        etGoalName = findViewById(R.id.etGoalName)
        etAmount = findViewById(R.id.etGoalAmount)
        btnSaveGoal = findViewById(R.id.btnSaveGoal)
        goalDB = AppDatabase.getDatabase(this)
        btnSaveGoal.setOnClickListener {
            saveGoal()
        }
    }

    private fun getUserid(): Long{
        val sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        return sharedPref.getLong("USER_ID", -1L)
    }

    private fun saveGoal() {

        val goalName = etGoalName.text.toString()
        val amount = etAmount.text.toString().toDoubleOrNull()
        if (goalName.isBlank() || amount == null) {
            Toast.makeText(this, "Please fill in all fields correctly", Toast.LENGTH_SHORT).show()
            return
        }
        val newGoal = Goal(
            goalName = goalName,
            amount = amount,
            userID = getUserid()
        )
        lifecycleScope.launch {
            goalDB.goalDao().insertGoal(newGoal)
            runOnUiThread {
                Toast.makeText(this@AddGoalsActivity, "Goal saved successfully", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}
