package com.example.modus_system.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

class FirebaseAuthManager {

    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance("https://modus-system-e7905-default-rtdb.firebaseio.com/").reference

    val currentUser: FirebaseUser? get() = auth.currentUser

    // Sync a transaction to the cloud
    suspend fun syncTransaction(userId: String, transaction: Transaction) {
        try {
            database.child("users").child(userId)
                .child("transactions").child(transaction.id.toString())
                .setValue(transaction).await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Delete a transaction from the cloud
    suspend fun deleteCloudTransaction(userId: String, transactionId: Int) {
        try {
            database.child("users").child(userId)
                .child("transactions").child(transactionId.toString())
                .removeValue().await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Register with email and password
    suspend fun register(
        name: String,
        email: String,
        phone: String,
        password: String
    ): Result<FirebaseUser> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user!!

            // Save user profile to Realtime Database
            val userProfile = mapOf(
                "name" to name,
                "email" to email,
                "phone" to phone,
                "userId" to user.uid,
                "createdAt" to System.currentTimeMillis()
            )
            database.child("users").child(user.uid).setValue(userProfile).await()

            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Login with email and password
    suspend fun login(
        email: String,
        password: String
    ): Result<FirebaseUser> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            Result.success(result.user!!)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Send password reset email
    suspend fun sendPasswordReset(email: String): Result<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Logout
    fun logout() {
        auth.signOut()
    }

    // Check if user is logged in
    fun isLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    // Get user profile from database
    suspend fun getUserProfile(userId: String): Map<String, Any>? {
        return try {
            val snapshot = database.child("users").child(userId).get().await()
            snapshot.value as? Map<String, Any>
        } catch (e: Exception) {
            null
        }
    }
}