package com.example.moneymanager

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

class BudgetRepository {

    private val database: DatabaseReference = FirebaseDatabase.getInstance().getReference("budgets")


    suspend fun insertBudget(budget: Budget): Boolean {
        return try {
            val key = if (budget.id.isBlank()) {
                database.push().key ?: return false
            } else {
                budget.id
            }
            val budgetToSave = budget.copy(id = key)

            database.child(key).setValue(budgetToSave).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun getAllBudgetsByUser(userID: Long): List<Budget> {
        return try {
            val snapshot = database.orderByChild("userID").equalTo(userID.toDouble()).get().await()
            if (snapshot.exists()) {
                snapshot.children.mapNotNull { it.getValue(Budget::class.java) }
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun getBudgetMaxByCategory(userId: Long): Map<String, Double> {
        return try {
            val snapshot = database.orderByChild("userID").equalTo(userId.toDouble()).get().await()
            val budgets = snapshot.children.mapNotNull { it.getValue(Budget::class.java) }
            budgets.associateBy({ it.name }, { it.budgetMaxLimit })
        } catch (e: Exception) {
            e.printStackTrace()
            emptyMap()
        }
    }

}