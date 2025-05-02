/**
 * UserTransaction.kt
 *
 *
 *
 *
 *  Assistance provided by ChatGPT, OpenAI (2025). https://chat.openai.com
 */

package com.example.moneymanager

import androidx.room.PrimaryKey
import androidx.room.Entity

@Entity(tableName = "transactions")
data class UserTransaction(
    @PrimaryKey(autoGenerate = true ) val id: Long = 0,
    val amount: Double,
    val date: String,
    val category: String,
    val type: String,
    val description: String,
    val attachment: String,
    val userId: Long
)

// ============================== End of file ==============================