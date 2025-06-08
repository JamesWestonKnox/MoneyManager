/**
 * BudgetsActivity.kt
 *
 *
 *
 *
 *  Assistance provided by ChatGPT, OpenAI (2025). https://chat.openai.com
 */

package com.example.moneymanager

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BudgetsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: BudgetAdapter
    private val budgetRepository = BudgetRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.budgets_page)

        //Using recyclerViews to display the budgets
        recyclerView = findViewById(R.id.budgetRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        //Method to retrieve budgets
        loadBudgetData()

        //AddBudget button functionality
        val btnAddBudget = findViewById<View>(R.id.fabAddBudget)

        btnAddBudget.setOnClickListener {
            val intent = Intent(this, AddBudgetActivity::class.java)
            startActivity(intent)
        }

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.selectedItemId = R.id.nav_budgets

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_budgets -> {
                    true
                }

                R.id.nav_home -> {

                    startActivity(Intent(this, HomeActivity::class.java))
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

                R.id.nav_goals -> {

                    startActivity(Intent(this, GoalsActivity::class.java))
                    overridePendingTransition(0, 0)
                    finish()

                    true
                }


                else -> false

            }
    }}

    private fun getUserid(): Long {
        val sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        return sharedPref.getLong("USER_ID", -1L)
    }

    override fun onResume(){
        super.onResume()

        loadBudgetData()
    }

    private fun loadBudgetData(){
        CoroutineScope(Dispatchers.IO).launch {
            val userId = getUserid()

            val budgets = budgetRepository.getAllBudgetsByUser(userId)
            val transactionRepo = TransactionRepository()
            val transactions = transactionRepo.getAllTransactionsByUser(userId)

            withContext(Dispatchers.Main) {
                adapter = BudgetAdapter(this@BudgetsActivity, budgets, transactions)
                recyclerView.adapter = adapter
            }

        }
    }
}

// ============================== End of file ==============================