package com.example.moneymanager

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.moneymanager.com.example.moneymanager.GoalsActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.animation.ValueAnimator
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.TextView
import androidx.core.animation.addListener
import com.google.android.material.progressindicator.CircularProgressIndicator

class BudgetsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.budgets_page)

        val expandButton = findViewById<Button>(R.id.button)

        val expandedLayout = findViewById<LinearLayout>(R.id.expandedLayout)

        val progressBar = findViewById<CircularProgressIndicator>(R.id.progressBarSmall)
        val tvSpent = findViewById<TextView>(R.id.tvSpentSmall)
        val tvRemaining = findViewById<TextView>(R.id.tvRemainingSmall)

        expandButton.setOnClickListener {
            if (expandedLayout.visibility == View.GONE) {

                expandedLayout.measure(
                    View.MeasureSpec.makeMeasureSpec((expandedLayout.parent as View).width, View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.UNSPECIFIED
                )
                val targetHeight = expandedLayout.measuredHeight

                expandedLayout.layoutParams.height = 0
                expandedLayout.visibility = View.VISIBLE
                progressBar.visibility = View.INVISIBLE
                tvSpent.visibility = View.GONE
                tvRemaining.visibility = View.GONE

                val animator = ValueAnimator.ofInt(0, targetHeight)
                animator.addUpdateListener { valueAnimator ->
                    val layoutParams = expandedLayout.layoutParams
                    layoutParams.height = valueAnimator.animatedValue as Int
                    expandedLayout.layoutParams = layoutParams
                }
                animator.duration = 300
                animator.interpolator = AccelerateDecelerateInterpolator()
                animator.start()

                expandButton.text = "Collapse"

            } else {
                // Collapse with animation
                val initialHeight = expandedLayout.measuredHeight

                val animator = ValueAnimator.ofInt(initialHeight, 0)
                animator.addUpdateListener { valueAnimator ->
                    val layoutParams = expandedLayout.layoutParams
                    layoutParams.height = valueAnimator.animatedValue as Int
                    expandedLayout.layoutParams = layoutParams
                }

                animator.duration = 300
                animator.interpolator = AccelerateDecelerateInterpolator()
                animator.start()

                animator.addListener(onEnd = {
                    expandedLayout.visibility = View.GONE
                    progressBar.visibility = View.VISIBLE
                    tvSpent.visibility = View.VISIBLE
                    tvRemaining.visibility = View.VISIBLE
                })

                expandButton.text = "Expand"
            }
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
}