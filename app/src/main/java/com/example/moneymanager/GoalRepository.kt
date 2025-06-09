package com.example.moneymanager

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

class GoalRepository {
    private val database: DatabaseReference = FirebaseDatabase.getInstance().getReference("goals")

    suspend fun insertGoal(goal: Goal): Boolean {
        return try {
            val key = if (goal.id.isBlank()) {
                database.push().key ?: return false
            } else {
                goal.id
            }
            val goalToSave = goal.copy(id = key)

            database.child(key).setValue(goalToSave).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun getAllGoalsByUser(userID: Long): List<Goal> {
        return try {
            val snapshot = database.orderByChild("userID").equalTo(userID.toDouble()).get().await()
            if (snapshot.exists()) {
                snapshot.children.mapNotNull { it.getValue(Goal::class.java) }
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun getGoalByIdandUserId(goalID: String, userID: Long): Goal? {
        return try {
            val snapshot = database.child(goalID).get().await()
            if (snapshot.exists()) {
                val goal = snapshot.getValue(Goal::class.java)

                if (goal != null && goal.userID == userID.toDouble()) {
                    goal
                } else {
                    null
                }
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun updateGoal(goal: Goal): Boolean {
        return try {
            if (goal.id.isNotBlank()) {
                database.child(goal.id).setValue(goal).await()
                true
            } else {
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun deleteGoal(goal: Goal): Boolean {
        return try {
            if (goal.id.isNotBlank()) {
                database.child(goal.id).removeValue().await()
                true
            } else {
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
