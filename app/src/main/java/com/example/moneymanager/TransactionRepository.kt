package com.example.moneymanager

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class TransactionRepository {

    private val database: DatabaseReference = FirebaseDatabase.getInstance().getReference("transactions")

    suspend fun insertTransaction(transaction: UserTransaction): Boolean {
        return try {
            database.push().setValue(transaction).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun getAllTransactionsByUser(userId: Long): List<UserTransaction> {
        return try {
            val snapshot = database.orderByChild("userId").equalTo(userId.toDouble()).get().await()
            snapshot.children.mapNotNull { it.getValue(UserTransaction::class.java) }
                .sortedByDescending { it.date }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun getTransactionsByDate(userId: Long, startDate: String, endDate: String): List<UserTransaction> {
        return try {
            val snapshot = database.orderByChild("userId").equalTo(userId.toDouble()).get().await()
            snapshot.children.mapNotNull { it.getValue(UserTransaction::class.java) }
                .filter { it.date >= startDate && it.date <= endDate }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun getTotalExpenses(userId: Long): Double {
        return try {
            val snapshot = database.orderByChild("userId").equalTo(userId.toDouble()).get().await()
            snapshot.children.mapNotNull { it.getValue(UserTransaction::class.java) }
                .filter { it.type == "Expense" }
                .sumOf { it.amount }
        } catch (e: Exception) {
            e.printStackTrace()
            0.0
        }
    }

    suspend fun getExpenseTotalsByCategory(userId: Long, startDate: String, endDate: String): Map<String, Double> {
        return try {
            val snapshot = database.orderByChild("userId").equalTo(userId.toDouble()).get().await()
            val transactions = snapshot.children.mapNotNull { it.getValue(UserTransaction::class.java) }
                .filter { it.date >= startDate && it.date <= endDate && it.type == "Expense" }

            transactions.groupBy { it.category }
                .mapValues { entry -> entry.value.sumOf { kotlin.math.abs(it.amount) } }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyMap()
        }
    }





}