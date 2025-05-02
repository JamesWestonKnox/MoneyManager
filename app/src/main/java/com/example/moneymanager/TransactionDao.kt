/**
 * TransactionDao.kt
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
interface TransactionDao {

    @Insert
    suspend fun insertTransaction(transaction: UserTransaction)

    @Query("SELECT * FROM transactions WHERE userId = :userId ORDER BY date DESC")
    fun getAllTransactionsByUser(userId: Long): List<UserTransaction>

    @Query("SELECT * FROM transactions WHERE userId = :userId AND date BETWEEN :startDate AND :endDate")
    fun getTransactionsByDate(userId: Long, startDate: String, endDate: String): List<UserTransaction>
}

// ============================== End of file ==============================