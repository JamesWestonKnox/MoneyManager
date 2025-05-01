package com.example.moneymanager.com.example.moneymanager

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "goals")
data class Goal(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "goal_name") val name: String,
    @ColumnInfo(name = "goal_amount") val amount: Double,
    @ColumnInfo(name = "goal_date") val date: String
)