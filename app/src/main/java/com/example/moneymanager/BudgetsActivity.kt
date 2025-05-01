package com.example.moneymanager

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.moneymanager.com.example.moneymanager.GoalsActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class BudgetsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.budgets_page)



        //Making navBar functional
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
}