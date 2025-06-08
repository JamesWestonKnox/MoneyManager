/**
 * GoalsActivity.kt
 *
 *
 *
 *
 *  Assistance provided by ChatGPT, OpenAI (2025). https://chat.openai.com
 */

package com.example.moneymanager


import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.launch


class GoalsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var goalAdapter: GoalAdapter
    private val goalList = mutableListOf<Goal>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.goals_page)
        //Using a recyclerView to display all the goals to the user
        recyclerView = findViewById(R.id.rvGoals)
        goalAdapter = GoalAdapter(goalList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = goalAdapter

        //val dbGoal = AppDatabase.getDatabase(this)
        lifecycleScope.launch {
            //val existingGoals = dbGoal.goalDao().getAllGoalsByUser(getUserid())
            //goalList.addAll(existingGoals)
            goalAdapter.notifyDataSetChanged()
        }
        //AddGoal functionality
        val btnAddGoal = findViewById<Button>(R.id.btnNewGoal)
        btnAddGoal.setOnClickListener {
            val bottomSheetDialog = BottomSheetDialog(this)
            val sheetView = layoutInflater.inflate(R.layout.bottom_sheet_new_goal, null)
            bottomSheetDialog.setContentView(sheetView)
            bottomSheetDialog.show()

            val etGoalName = sheetView.findViewById<EditText>(R.id.etGoalName)
            val etGoalAmount = sheetView.findViewById<EditText>(R.id.etGoalAmount)
            val btnSaveGoal = sheetView.findViewById<Button>(R.id.btnSaveGoal)

            btnSaveGoal.setOnClickListener {
                val goalName = etGoalName.text.toString()
                val amount = etGoalAmount.text.toString().toDoubleOrNull()
                if (goalName.isBlank() || amount == null) {
                    Toast.makeText(this, "Please fill in all fields correctly", Toast.LENGTH_SHORT)
                        .show()
                    return@setOnClickListener
                }
                val newGoal = Goal(
                    goalName = goalName,
                    amount = amount,
                    userID = getUserid()
                )
                lifecycleScope.launch {
                   // dbGoal.goalDao().insertGoal(newGoal)
                   // val updatedGoals = dbGoal.goalDao().getAllGoalsByUser(getUserid())
                    runOnUiThread {
                        goalList.clear()
                       // goalList.addAll(updatedGoals)
                        goalAdapter.notifyDataSetChanged()

                        Toast.makeText(
                            this@GoalsActivity,
                            "Goal saved successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                        bottomSheetDialog.dismiss() }
                }
            }
        }
            // Navbar functionality
            val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
            bottomNav.selectedItemId = R.id.nav_goals

            bottomNav.setOnItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.nav_goals -> true
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
            }



    }
    private fun getUserid(): Long {
        val sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        return sharedPref.getLong("USER_ID", -1L)
    }}

