package com.example.moneymanager

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface GoalDao {
    @Insert
    suspend fun insertGoal(goal: Goal)

    @Query("SELECT * FROM goals WHERE userID = :userID")
    suspend fun getAllGoalsByUser(userID: Long): List<Goal>

    @Query("SELECT * FROM goals WHERE id = :goalID AND userID =:userID")
    suspend fun getGoalByIdandUserId(goalID: Long, userID: Long): Goal?

    @Update
    suspend fun updateGoal(goal: Goal)

    @Delete
    suspend fun deleteGoal(goal: Goal)
}
