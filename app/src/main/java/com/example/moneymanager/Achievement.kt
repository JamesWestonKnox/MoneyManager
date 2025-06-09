/**
 * Achievement.kt
 *
 *
 *
 *
 *  Assistance provided by ChatGPT, OpenAI (2025). https://chat.openai.com
 */

package com.example.moneymanager

// Achievement.kt
data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val isUnlocked: Boolean = false,
    val dateUnlocked: Long? = null
)

// ============================== End of file ==============================