package com.example.moneymanager.com.example.moneymanager

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.moneymanager.BudgetsActivity
import com.example.moneymanager.HomeActivity
import com.example.moneymanager.R
import com.example.moneymanager.ReportsActivity
import com.example.moneymanager.TransactionsActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.util.*

class GoalsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.goals_page)

        val btnNewGoal = findViewById<Button>(R.id.btnNewGoal)

        btnNewGoal.setOnClickListener {
            val bottomSheetDialog = BottomSheetDialog(this)
            val sheetView = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_new_goal, null)
            bottomSheetDialog.setContentView(sheetView)

            val goalName = sheetView.findViewById<EditText>(R.id.editGoalName)
            val goalAmount = sheetView.findViewById<EditText>(R.id.editGoalAmount)
            val goalDate = sheetView.findViewById<TextView>(R.id.tvGoalDate)
            val btnSaveGoal = sheetView.findViewById<Button>(R.id.btnSaveGoal)

            goalDate.setOnClickListener {
                val calendar = Calendar.getInstance()
                val year = calendar.get(Calendar.YEAR)
                val month = calendar.get(Calendar.MONTH)
                val day = calendar.get(Calendar.DAY_OF_MONTH)

                val datePickerDialog = DatePickerDialog(this,
                    { _, selectedYear, selectedMonth, selectedDay ->
                        goalDate.text = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                    }, year, month, day
                )
                datePickerDialog.show()
            }

            btnSaveGoal.setOnClickListener {
                val name = goalName.text.toString()
                val amount = goalAmount.text.toString()
                val date = goalDate.text.toString()

                if (name.isNotEmpty() && amount.isNotEmpty() && date.isNotEmpty()) {
                    Toast.makeText(this, "Goal saved: $name", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                }

                bottomSheetDialog.dismiss()
            }

            bottomSheetDialog.show()
        }


        //Navbar functionality
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.selectedItemId = R.id.nav_goals

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_goals -> {
                    true
                }

                R.id.nav_budgets -> {

                    startActivity(Intent(this, BudgetsActivity::class.java))
                    overridePendingTransition(0, 0)
                    finish()

                    true
                }
                R.id.nav_transactions -> {

                    startActivity(Intent(this, TransactionsActivity::class.java))
                    overridePendingTransition(0, 0)
                    finish()

                    true
                }

                R.id.nav_reports -> {

                    startActivity(Intent(this, ReportsActivity::class.java))
                    overridePendingTransition(0, 0)
                    finish()

                    true
                }

                R.id.nav_home -> {

                    startActivity(Intent(this, HomeActivity::class.java))
                    overridePendingTransition(0, 0)
                    finish()

                    true
                }


                else -> false

            }

    }}
}

