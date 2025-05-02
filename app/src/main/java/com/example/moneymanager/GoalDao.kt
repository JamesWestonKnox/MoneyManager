/**
 * GoalsDao.kt
 *
 *
 *
 *
 *  Assistance provided by ChatGPT, OpenAI (2025). https://chat.openai.com
 */

package com.example.moneymanager

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface GoalDao {
    @Insert
    suspend fun insertGoal(goal: Goal)

    @Query("SELECT * FROM goals")
    suspend fun getAllGoals(): List<Goal>
}

// ============================== End of file ==============================