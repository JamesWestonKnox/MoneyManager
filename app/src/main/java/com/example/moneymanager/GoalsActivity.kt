
/**
 * GoalsActivity.kt
 *
 *
 *
 *
 *  Assistance provided by ChatGPT, OpenAI (2025). https://chat.openai.com
 */

package com.example.moneymanager


import com.example.moneymanager.AchievementAdapter
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
    private lateinit var achievementAdapter: AchievementAdapter
    private lateinit var achievementManager: AchievementManager

    private val goalList = mutableListOf<Goal>()
    private val goalRepository = GoalRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.goals_page)


        achievementManager = AchievementManager(this)

        // Setup RecyclerViews
        setupGoalsRecyclerView()
        setupAchievementsRecyclerView()

        // Load existing goals
        loadGoals()

        // Setup new goal button
        setupNewGoalButton()

        // Setup navigation
        setupBottomNavigation()
    }

    private fun setupGoalsRecyclerView() {
        recyclerView = findViewById(R.id.rvGoals)
        goalAdapter = GoalAdapter(goalList) { goal, position, amountToAdd ->
            handleAddToGoal(goal, position, amountToAdd)
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = goalAdapter
    }

    private fun setupAchievementsRecyclerView() {
        val rvAchievements = findViewById<RecyclerView>(R.id.rvAchievements)
        achievementAdapter = AchievementAdapter(achievementManager.getAllAchievements())
        rvAchievements.layoutManager = LinearLayoutManager(this)
        rvAchievements.adapter = achievementAdapter
    }

    private fun loadGoals() {
        lifecycleScope.launch {
            val existingGoals = goalRepository.getAllGoalsByUser(getUserid())
            goalList.clear()
            goalList.addAll(existingGoals)
            goalAdapter.notifyDataSetChanged()
        }
    }

    private fun setupNewGoalButton() {
        val btnAddGoal = findViewById<Button>(R.id.btnNewGoal)
        btnAddGoal.setOnClickListener {
            showNewGoalBottomSheet()
        }
    }

    private fun showNewGoalBottomSheet() {
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
                Toast.makeText(this, "Please fill in all fields correctly", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val newGoal = Goal(
                goalName = goalName,
                amount = amount,
                userID = getUserid().toDouble()
            )

            lifecycleScope.launch {
                val success = goalRepository.insertGoal(newGoal)
                runOnUiThread {
                    if (success) {
                        checkFirstGoalAchievement()
                        loadGoals()
                        Toast.makeText(this@GoalsActivity, "Goal saved successfully", Toast.LENGTH_SHORT).show()
                        bottomSheetDialog.dismiss()
                    } else {
                        Toast.makeText(this@GoalsActivity, "Failed to save goal", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun handleAddToGoal(goal: Goal, position: Int, amountToAdd: Double?) {
        if (amountToAdd == null || amountToAdd <= 0) {
            Toast.makeText(this, "Please enter a valid amount", Toast.LENGTH_SHORT).show()
            return
        }

        val newTotalAmount = goal.totalAmount + amountToAdd

        if (newTotalAmount > goal.amount) {
            Toast.makeText(
                this,
                "Amount exceeds goal target. Max allowed: R${String.format("%.2f", goal.amount - goal.totalAmount)}",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        val updatedGoal = goal.copy(totalAmount = newTotalAmount)
        val wasGoalCompleted = newTotalAmount >= goal.amount

        lifecycleScope.launch {
            val success = goalRepository.updateGoal(updatedGoal)
            runOnUiThread {
                if (success) {
                    goalAdapter.updateGoal(position, updatedGoal)
                    if (wasGoalCompleted) {
                        checkGoalCompletionAchievement()
                    }

                    Toast.makeText(
                        this@GoalsActivity,
                        if (wasGoalCompleted) "🎉 You've reached your goal!" else "Amount added successfully!",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(this@GoalsActivity, "Failed to update goal", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun checkFirstGoalAchievement() {
        lifecycleScope.launch {
            val goals = goalRepository.getAllGoalsByUser(getUserid())

            if (goals.isNotEmpty() && !achievementManager.getUnlockedAchievements().any { it.id == "firststep" }) {
                val unlocked = achievementManager.checkAndUnlockAchievement(AchievementType.FIRST_GOAL_CREATED)
                if (unlocked) {
                    runOnUiThread {
                        showAchievementDialog("First Step", "You've created your first goal!")
                        refreshAchievements()
                    }
                }
            }
        }
    }

    private fun checkGoalCompletionAchievement() {
        val unlocked = achievementManager.checkAndUnlockAchievement(AchievementType.SAVINGS_GOAL_ACHIEVED)
        if (unlocked) {
            showAchievementDialog("Goal Setter", "You achieved your savings goal!")
            refreshAchievements()
        }
    }

    fun checkBudgetAchievements() {
        lifecycleScope.launch {
            val budgetRepo = BudgetRepository()
            val budgets = budgetRepo.getAllBudgetsByUser(getUserid())

            val hitBudgetGoal = budgets.any { it.budgetRemaining >= 0 }
            if (hitBudgetGoal) {
                val unlocked = achievementManager.checkAndUnlockAchievement(AchievementType.MONTHLY_BUDGET_HIT)
                if (unlocked) {
                    runOnUiThread {
                        showAchievementDialog("Sharpshooter", "You hit your monthly budget goal!")
                        refreshAchievements()
                    }
                }
            }

            val stayedWithinBudget = budgets.any { it.budgetRemaining > 0 }
            if (stayedWithinBudget) {
                val unlocked = achievementManager.checkAndUnlockAchievement(AchievementType.BUDGET_LIMIT_MONTH)
                if (unlocked) {
                    runOnUiThread {
                        showAchievementDialog("Big Saver", "You stayed within budget for a month!")
                        refreshAchievements()
                    }
                }
            }
        }
    }

    private fun showAchievementDialog(title: String, description: String) {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("🎉 Achievement Unlocked!")
            .setMessage("$title: $description")
            .setPositiveButton("Awesome!") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun refreshAchievements() {
        achievementAdapter.updateAchievements(achievementManager.getAllAchievements())
    }

    private fun setupBottomNavigation() {
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
    }
}

/* ============================================END OF FILE=================================================// */