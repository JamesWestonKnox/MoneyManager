package com.example.moneymanager

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.moneymanager.com.example.moneymanager.GoalsActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_page)

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
}