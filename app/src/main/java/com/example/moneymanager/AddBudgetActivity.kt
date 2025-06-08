/**
 * AddBudgetActivity.kt
 *
 *
 *
 *
 *  Assistance provided by ChatGPT, OpenAI (2025). https://chat.openai.com
 */


package com.example.moneymanager

import android.os.Bundle
import android.view.Gravity
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.launch

class AddBudgetActivity: AppCompatActivity() {

    private var budgetName: String = ""
    private var budgetMaxLimit: Double = 0.00

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.add_budget_page)

        //Creating a pop up window and styling it
        val window = window
        val layoutParams = window.attributes

        layoutParams.width = (resources.displayMetrics.widthPixels * 1)
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
        layoutParams.gravity = Gravity.CENTER
        layoutParams.y = -100

        window.attributes = layoutParams
        window.setBackgroundDrawableResource(android.R.color.transparent)

        val etName = findViewById<EditText>(R.id.etBudgetName)
        val etMaxLimit = findViewById<EditText>(R.id.etMaxLimit)
        val btnCreate = findViewById<Button>(R.id.btnCreateBudget)

        //Create button functionality
        btnCreate.setOnClickListener {
            budgetName = etName.text.toString()
            budgetMaxLimit = etMaxLimit.text.toString().toDoubleOrNull() ?: 0.0

            if (budgetName.isNotEmpty() && budgetMaxLimit > 0.0) {
                val userId = getUserid()
                val budget = Budget(id = "", userID = userId.toDouble(), name = budgetName, budgetMaxLimit = budgetMaxLimit)

                // Use coroutine to call suspend function
                lifecycleScope.launch {
                    val repository = BudgetRepository()
                    val success = repository.insertBudget(budget)
                    if (success) {
                        runOnUiThread {
                            Toast.makeText(this@AddBudgetActivity, "Budget saved!", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                    } else {
                        runOnUiThread {
                            Toast.makeText(this@AddBudgetActivity, "Failed to save budget", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            } else {
                Toast.makeText(this, "Please enter a valid name and amount", Toast.LENGTH_SHORT).show()
            }
        }

        val btnCancel = findViewById<Button>(R.id.btnCancelBudget)
        btnCancel.setOnClickListener{
            finish()
        }
    }
    //Method to retrieve userID
    private fun getUserid(): Long{
        val sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        return sharedPref.getLong("USER_ID", -1L)
    }
}

// ============================== End of file ==============================
