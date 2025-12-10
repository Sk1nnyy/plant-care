package com.skinnyy.plantcare.api

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.functions.functions
import com.skinnyy.plantcare.data.TokenResponse
import kotlinx.coroutines.tasks.await
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import kotlin.collections.get

class AuthRepository {
    private var activeToken: TokenResponse? = null

    suspend fun getOrRefreshToken(): String {
        if (activeToken == null) { // Add expiration date
            try {
                val ip = getPublicIp()
                val result =
                    Firebase
                        .functions("europe-west1")
                        .getHttpsCallable("getClientToken")
                        .call(mapOf("ip" to ip))
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
                throw IllegalArgumentException()
            }
        }

        return activeToken?.token.orEmpty()
    }

    suspend fun getPublicIp(): String {
        val client = OkHttpClient()
        val request =
            Request
                .Builder()
                .url("https://api.ipify.org?format=json")
                .build()
        val response = client.newCall(request).execute()
        val json = JSONObject(response.body!!.string())
        return json.getString("ip")
    }
}
