/**
 * UserTransaction.kt
 *
 *
 *
 *
 *  Assistance provided by ChatGPT, OpenAI (2025). https://chat.openai.com
 */

package com.example.moneymanager



data class UserTransaction(
    val id: Long = 0L,
    val amount: Double = 0.0,
    val date: String = "",
    val category: String = "",
    val type: String = "",
    val description: String = "",
    val attachment: String = "",
    val userId: Long = 0L
)

// ============================== End of file ==============================