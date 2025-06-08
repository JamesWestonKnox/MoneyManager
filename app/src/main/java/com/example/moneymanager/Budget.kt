/**
 * Budget.kt
 *
 *
 *
 *
 *  Assistance provided by ChatGPT, OpenAI (2025). https://chat.openai.com
 */

package com.example.moneymanager



data class Budget(

    var id: String = "",
    val name: String = "",
    val userID: Double = 0.0,
    val budgetMaxLimit: Double = 0.0,
    var budgetAmount: Double = 0.0,
) {

    val budgetRemaining: Double
        get() = budgetMaxLimit - budgetAmount
}


// ============================== End of file ==============================