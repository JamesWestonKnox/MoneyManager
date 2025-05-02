package com.example.moneymanager.com.example.moneymanager

import android.animation.ValueAnimator
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
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.animation.addListener


class GoalsActivity : AppCompatActivity() {

    private lateinit var goalTitleText: TextView
    private lateinit var goalProgressBar: ProgressBar
    private lateinit var goalPercentText: TextView

    private var savedGoalName: String = "New Goal"
    private var savedGoalAmount: Double = 0.0
    private var currentProgressAmount: Double = 0.0 // You can tie this to actual savings data later.

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.goals_page)

        val expandButton1 = findViewById<Button>(R.id.expandButton1)
        val expandableLayout1 = findViewById<LinearLayout>(R.id.expandableLayout1)

        val progressBar = findViewById<ProgressBar>(R.id.newGoalProgressBar)
        val percentText = findViewById<TextView>(R.id.newGoalPercentText)

        expandButton1.setOnClickListener {
            if (expandableLayout1.visibility == View.GONE) {
                // Expand with animation
                expandableLayout1.measure(
                    View.MeasureSpec.makeMeasureSpec((expandableLayout1.parent as View).width, View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.UNSPECIFIED
                )
                val targetHeight = expandableLayout1.measuredHeight

                expandableLayout1.layoutParams.height = 0
                expandableLayout1.visibility = View.VISIBLE
                progressBar.visibility = View.INVISIBLE
                percentText.visibility = View.INVISIBLE

                val animator = ValueAnimator.ofInt(0, targetHeight)
                animator.addUpdateListener { valueAnimator ->
                    val layoutParams = expandableLayout1.layoutParams
                    layoutParams.height = valueAnimator.animatedValue as Int
                    expandableLayout1.layoutParams = layoutParams
                }
                animator.duration = 300
                animator.interpolator = AccelerateDecelerateInterpolator()
                animator.start()

                expandButton1.text = "Collapse"

            } else {
                // Collapse with animation
                val initialHeight = expandableLayout1.measuredHeight

                val animator = ValueAnimator.ofInt(initialHeight, 0)
                animator.addUpdateListener { valueAnimator ->
                    val layoutParams = expandableLayout1.layoutParams
                    layoutParams.height = valueAnimator.animatedValue as Int
                    expandableLayout1.layoutParams = layoutParams
                }

                animator.duration = 300
                animator.interpolator = AccelerateDecelerateInterpolator()
                animator.start()

                animator.addListener(onEnd = {
                    expandableLayout1.visibility = View.GONE
                    progressBar.visibility = View.VISIBLE
                    percentText.visibility = View.VISIBLE
                })

                expandButton1.text = "Expand"
            }
        }



        goalTitleText = findViewById(R.id.newGoalTitleText)
        goalProgressBar = findViewById(R.id.newGoalProgressBar)
        goalPercentText = findViewById(R.id.newGoalPercentText)

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
                val amountText = goalAmount.text.toString()
                val date = goalDate.text.toString()

                if (name.isNotEmpty() && amountText.isNotEmpty() && date.isNotEmpty()) {
                    savedGoalName = name
                    savedGoalAmount = amountText.toDoubleOrNull() ?: 0.0

                    updateGoalCard()

                    Toast.makeText(this, "Goal saved: $name", Toast.LENGTH_SHORT).show()
                    bottomSheetDialog.dismiss()
                } else {
                    Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                }
            }

            bottomSheetDialog.show()
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

    private fun updateGoalCard() {
        goalTitleText.text = savedGoalName

        val percent = if (savedGoalAmount == 0.0) 0 else (currentProgressAmount / savedGoalAmount * 100).toInt()
        goalProgressBar.progress = percent
        goalPercentText.text = "$percent%"
    }
}

