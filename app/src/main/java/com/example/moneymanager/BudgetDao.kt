package com.example.moneymanager

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface BudgetDao {

    @Insert
    suspend fun insertBudget(budget: Budget)

    @Query("SELECT * FROM budgets WHERE userID = :userID")
    fun getAllBudgetsByUser(userID: Long): List<Budget>


}