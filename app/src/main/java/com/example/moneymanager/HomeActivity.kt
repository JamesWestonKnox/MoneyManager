/**
 * HomeActivity.kt
 *
 *
 *
 *
 *  Assistance provided by ChatGPT, OpenAI (2025). https://chat.openai.com
 */

package com.example.moneymanager

import GoalHomeAdapter
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeActivity : AppCompatActivity() {
    private lateinit var goalRepository: GoalRepository
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: GoalHomeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_page)


        val welcomeMessage = findViewById<TextView>(R.id.tvWelcomeMessage)
        val userId = getUserid()

        goalRepository = GoalRepository()
        recyclerView = findViewById(R.id.rv_goalHome)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.isNestedScrollingEnabled = false




        lifecycleScope.launch {
            val goalList = goalRepository.getAllGoalsByUser(userId)
            adapter = GoalHomeAdapter(goalList)
            recyclerView.adapter = adapter
        }




        //Allowing user to navigate to the account page
        val imageView3 = findViewById<View>(R.id.imageView4)
        imageView3.setOnClickListener {
            val intent = Intent(this, AccountActivity::class.java)
            startActivity(intent)
        }

        //Making navBar functional
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.selectedItemId = R.id.nav_home

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
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

                R.id.nav_goals -> {

                        startActivity(Intent(this, GoalsActivity::class.java))
                        overridePendingTransition(0, 0)
                        finish()

                    true
                }


                else -> false

            }

        }

    }

    private fun getUserid(): Long{
        val sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        return sharedPref.getLong("USER_ID", -1L)
    }

}

// ============================== End of file ==============================