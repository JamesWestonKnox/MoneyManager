package com.example.moneymanager

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query


@Dao
interface TransactionDao {

    @Insert
    suspend fun insertTransaction(transaction: UserTransaction)

    @Query("SELECT * FROM transactions WHERE userId = :userId ORDER BY date DESC")
    fun getAllTransactionsByUser(userId: Long): List<UserTransaction>

    @Query("SELECT * FROM transactions WHERE userId = :userId AND date BETWEEN :startDate AND :endDate")
    fun getTransactionsByDate(userId: Long, startDate: String, endDate: String): List<UserTransaction>

    @Query("SELECT SUM(amount) FROM transactions WHERE userId = :userId AND amount < 0")
    suspend fun getTotalExpenses(userId: Long): Double
}