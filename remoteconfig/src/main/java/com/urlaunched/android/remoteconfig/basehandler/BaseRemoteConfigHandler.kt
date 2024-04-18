package com.urlaunched.android.remoteconfig.basehandler

import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.urlaunched.android.remoteconfig.data.RemoteConfig
import kotlinx.coroutines.tasks.await
import kotlinx.serialization.json.Json

abstract class BaseRemoteConfigHandler(private val isDebug: Boolean) {
    protected val firebaseRemoteConfig: FirebaseRemoteConfig by lazy {
        val config = Firebase.remoteConfig

        if (isDebug) {
            val configSettings = remoteConfigSettings {
                minimumFetchIntervalInSeconds = 0
            }
            config.setConfigSettingsAsync(configSettings)
        }
        config
    }

    fun fetchAndActivate(onSuccess: () -> Unit, onError: () -> Unit) {
        firebaseRemoteConfig.fetchAndActivate()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) onSuccess() else onError()
            }
    }

    suspend fun fetchAndActivateAsync(): Boolean = firebaseRemoteConfig.fetchAndActivate().await()

    protected inline fun <reified T> read(param: RemoteConfig): T? = try {
        when (T::class) {
            String::class -> firebaseRemoteConfig.getString(param.key)

            Boolean::class -> firebaseRemoteConfig.getBoolean(param.key)

            Long::class -> firebaseRemoteConfig.getLong(param.key)

            Int::class -> firebaseRemoteConfig.getLong(param.key).toInt()

            Double::class -> firebaseRemoteConfig.getDouble(param.key)

            Float::class -> firebaseRemoteConfig.getDouble(param.key).toFloat()

            else -> {
                val json = firebaseRemoteConfig.getString(param.key)
                json.takeIf { it.isNotBlank() }?.let { Json.decodeFromString(json) }
            }
        } as? T
    } catch (e: Exception) {
        null
    }
}