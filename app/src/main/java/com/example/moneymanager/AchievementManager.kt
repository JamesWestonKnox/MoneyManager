package com.example.moneymanager

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class AchievementManager(private val context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("achievements", Context.MODE_PRIVATE)
    private val gson = Gson()

    private val achievements = mutableMapOf<AchievementType, Achievement>()

    init {
        initializeAchievements()
        loadUnlockedAchievements()
    }

    private fun initializeAchievements() {
        achievements[AchievementType.MONTHLY_BUDGET_HIT] = Achievement(
            id = "sharpshooter",
            title = "Sharpshooter",
            description = "Hit your monthly budget goal"

        )


        achievements[AchievementType.SAVINGS_GOAL_ACHIEVED] = Achievement(
            id = "goalsetter",
            title = "Goal Setter",
            description = "Achieve a savings goal"
        )

        achievements[AchievementType.FIRST_GOAL_CREATED] = Achievement(
            id = "firststep",
            title = "First Step",
            description = "Create your first goal"

        )

    }

    private fun loadUnlockedAchievements() {
        val unlockedJson = prefs.getString("unlocked_achievements", "[]")
        val type = object : TypeToken<List<String>>() {}.type
        val unlockedIds: List<String> = gson.fromJson(unlockedJson, type) ?: emptyList()

        achievements.forEach { (type, achievement) ->
            if (unlockedIds.contains(achievement.id)) {
                val dateUnlocked = prefs.getLong("${achievement.id}_date", System.currentTimeMillis())
                achievements[type] = achievement.copy(
                    isUnlocked = true,
                    dateUnlocked = dateUnlocked
                )
            }
        }
    }

    fun checkAndUnlockAchievement(type: AchievementType): Boolean {
        val achievement = achievements[type] ?: return false

        if (!achievement.isUnlocked) {
            unlockAchievement(type)
            return true
        }
        return false
    }

    private fun unlockAchievement(type: AchievementType) {
        val achievement = achievements[type] ?: return
        val currentTime = System.currentTimeMillis()

        // Update the achievement
        achievements[type] = achievement.copy(
            isUnlocked = true,
            dateUnlocked = currentTime
        )


        val unlockedJson = prefs.getString("unlocked_achievements", "[]")
        val typeToken = object : TypeToken<MutableList<String>>() {}.type
        val unlockedIds: MutableList<String> = gson.fromJson(unlockedJson, typeToken) ?: mutableListOf()

        if (!unlockedIds.contains(achievement.id)) {
            unlockedIds.add(achievement.id)
            prefs.edit()
                .putString("unlocked_achievements", gson.toJson(unlockedIds))
                .putLong("${achievement.id}_date", currentTime)
                .apply()
        }
    }

    fun getAllAchievements(): List<Achievement> {
        return achievements.values.sortedByDescending { it.dateUnlocked ?: 0 }
    }

    fun getUnlockedAchievements(): List<Achievement> {
        return achievements.values.filter { it.isUnlocked }
            .sortedByDescending { it.dateUnlocked ?: 0 }
    }
}
