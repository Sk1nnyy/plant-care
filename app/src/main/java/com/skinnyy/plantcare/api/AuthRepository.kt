package com.skinnyy.plantcare.api

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.functions.functions
import com.skinnyy.plantcare.data.TokenResponse
import kotlinx.coroutines.tasks.await
import kotlin.collections.get

class AuthRepository {
    private var activeToken: TokenResponse? = null

    suspend fun getOrRefreshToken(): String {
        if (activeToken == null) { // Add expiration date
            try {
                val result =
                    Firebase
                        .functions("europe-west1")
                        .getHttpsCallable("getClientToken")
                        .call(null)
                        .await()

                val raw = result.data as Map<*, *>
                val token = raw["token"] as? String
                val expiration = raw["expiration"] as? String

                val tokenResponse =
                    TokenResponse(
                        token = token ?: "",
                        expirationDate = expiration ?: "",
                    )
                activeToken = tokenResponse
            } catch (e: Exception) {
                Log.e("Debugger", "Error getting token", e)
                throw e
            }
        }

        return activeToken?.token.orEmpty()
    }

    suspend fun signInAnonymously(): Boolean {
        val result = FirebaseAuth.getInstance().signInAnonymously().await()
        return result.user != null
    }

    suspend fun signInWithEmailAndPassword(
        email: String,
        password: String,
    ): Boolean {
        val result = FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).await()
        return result.user != null
    }

    fun isUserLoggedIn() = FirebaseAuth.getInstance().currentUser != null
}
