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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddBudgetActivity: AppCompatActivity() {

    private var budgetName: String = ""
    private var budgetMaxLimit: Double = 0.00

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.add_budget_page)

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

        btnCreate.setOnClickListener {
            budgetName = etName.text.toString()
            budgetMaxLimit = etMaxLimit.text.toString().toDoubleOrNull() ?: 0.0

            if (budgetName.isNotEmpty() && budgetMaxLimit > 0.0) {
                val userId = getUserid()
                val budget = Budget(userID = userId, name = budgetName, budgetMaxLimit = budgetMaxLimit)

                val db = AppDatabase.getDatabase(this)
                CoroutineScope(Dispatchers.IO).launch {
                    db.budgetDao().insertBudget(budget)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@AddBudgetActivity, "Budget saved!", Toast.LENGTH_SHORT)
                            .show()
                        finish()
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

    private fun getUserid(): Long{
        val sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        return sharedPref.getLong("USER_ID", -1L)
    }
}

// ============================== End of file ==============================
