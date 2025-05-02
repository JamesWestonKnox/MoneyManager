package com.example.moneymanager

import android.net.Uri
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
