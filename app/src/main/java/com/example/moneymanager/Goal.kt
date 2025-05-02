/**
 * Goal.kt
 *
 *
 *
 *
 *  Assistance provided by ChatGPT, OpenAI (2025). https://chat.openai.com
 */

package com.example.moneymanager

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "goals")
data class Goal(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val goalName: String,
    val amount: Double,
    val totalAmount: Double = 0.0,
    val userID: Long
)