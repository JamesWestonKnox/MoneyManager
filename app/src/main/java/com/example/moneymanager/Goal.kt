/**
 * Goal.kt
 *
 *
 *
 *
 *  Assistance provided by ChatGPT, OpenAI (2025). https://chat.openai.com
 */

package com.example.moneymanager



data class Goal(

    var id: Long = 0L,
    val goalName: String = "",
    val amount: Double = 0.0,
    val totalAmount: Double = 0.0,
    val userID: Long = 0L
)