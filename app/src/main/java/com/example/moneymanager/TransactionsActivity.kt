/**
 * Transactions.kt
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TransactionsActivity: AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TransactionAdapter
    private lateinit var db: AppDatabase



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.transactions_page)

        recyclerView = findViewById(R.id.rvTransactions)
        recyclerView.layoutManager = LinearLayoutManager(this)

        db = AppDatabase.getDatabase(this)

        loadTransactionData()

        //Add UserTransaction button functionality
        val btnAddTransaction = findViewById<View>(R.id.btnAddTransaction)
        btnAddTransaction.setOnClickListener {
            val intent = Intent(this, AddTransactionActivity::class.java)
            startActivity(intent)
        }

        //Nav bar functionality
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.selectedItemId = R.id.nav_transactions



        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_transactions -> {
                    true
                }

                R.id.nav_budgets -> {

                    startActivity(Intent(this, BudgetsActivity::class.java))
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

        }

        //Allowing the user to choose 2 dates and filter the transactions using those dates
        val btnFilter = findViewById<View>(R.id.filterButton)

        btnFilter.setOnClickListener{
            val calenderConstraints = CalendarConstraints.Builder()
                .setValidator(DateValidatorPointForward.from(System.currentTimeMillis()))
                .build()

            val datePicker = MaterialDatePicker.Builder.dateRangePicker()
                .setCalendarConstraints(calenderConstraints)
                .build()

            datePicker.show(supportFragmentManager, datePicker.toString())

            datePicker.addOnPositiveButtonClickListener { selection ->
                val startDate = selection.first
                val endDate = selection.second

                val startDateString = formatDate(startDate)
                val endDateString = formatDate(endDate)

                loadTransaction(startDateString, endDateString)

            }
        }



    }
    //Formatting the chosen dates to match the transaction date format
    private fun formatDate(timestamp: Long?): String{
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return format.format(Date(timestamp ?: 0))

    }
    //Refreshing the page with filtered transactions
    private fun loadTransaction(startDate: String, endDate: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val userId = getUserid()
            val transactions = db.transactionDao().getTransactionsByDate(userId, startDate, endDate)

            withContext(Dispatchers.Main) {
                adapter = TransactionAdapter(this@TransactionsActivity, transactions)
                recyclerView.adapter = adapter
            }
        }
    }


    private fun getUserid(): Long{
        val sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        return sharedPref.getLong("USER_ID", -1L)
    }

    override fun onResume(){
        super.onResume()

        loadTransactionData()
    }
    //Refreshing the page with all the transactions
    private fun loadTransactionData(){
        CoroutineScope(Dispatchers.IO).launch {
            val userId = getUserid()
            val transactions = db.transactionDao().getAllTransactionsByUser(userId)

            withContext(Dispatchers.Main){
                if (::adapter.isInitialized){
                    adapter = TransactionAdapter(this@TransactionsActivity, transactions)
                    recyclerView.adapter = adapter
                }else {
                    adapter = TransactionAdapter(this@TransactionsActivity, transactions)
                    recyclerView.adapter = adapter
                }

            }
        }
    }
}

// ============================== End of file ==============================