package com.example.moneymanager

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity (tableName = "budgets" )
data class Budget(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val userID: Long,
    val budgetMaxLimit: Double,
    var budgetAmount: Double = 0.00,
    val budgetRemaining: Double = budgetMaxLimit - budgetAmount
)
