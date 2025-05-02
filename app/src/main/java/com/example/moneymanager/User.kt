/**
 * User.kt
 *
 *
 *
 *
 *  Assistance provided by ChatGPT, OpenAI (2025). https://chat.openai.com
 */

package com.example.moneymanager

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true ) val id: Long = 0,
    val firstName: String,
    val surname: String,
    val email: String,
    val password: String
)

// ============================== End of file ==============================