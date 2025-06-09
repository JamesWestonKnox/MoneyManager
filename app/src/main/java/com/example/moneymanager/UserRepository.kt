package com.example.moneymanager

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

class UserRepository {

    private val database: DatabaseReference = FirebaseDatabase.getInstance().getReference("users")

    suspend fun insertUser(user: User): Boolean {
        return try {
            val key = user.id.toString()
            database.child(key).setValue(user).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }


    suspend fun getUserByEmail(email: String): User? {
        return try {
            val snapshot = database.orderByChild("email").equalTo(email).get().await()
            if (snapshot.exists()) {
                snapshot.children.first().getValue(User::class.java)
            } else null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun getUserById(id: Long): User? {
        return try {
            val snapshot = database.child(id.toString()).get().await()
            snapshot.getValue(User::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun updateUser(user: User): Boolean {
        return try {
            val key = user.id.toString()
            database.child(key).setValue(user).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}