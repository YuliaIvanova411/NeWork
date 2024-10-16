package com.example.nework.auth

import android.content.Context
import androidx.core.content.edit
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.example.nework.api.ApiService
import com.example.nework.model.AuthModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppAuth @Inject constructor(
    @ApplicationContext
    private val context: Context,
) {
    private val prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
    private val ID_KEY = "ID_KEY"
    private val TOKEN_KEY = "TOKEN_KEY"

    private val _data: MutableStateFlow<AuthModel?>

    init {
        val token = prefs.getString(TOKEN_KEY, null)
        val id = prefs.getInt(ID_KEY, 0)

        if (token == null || id == 0) {
            _data = MutableStateFlow(null)

            prefs.edit { clear() }
        } else {
            _data = MutableStateFlow(AuthModel(id, token))
        }
    }

    val data = _data.asStateFlow()

    @InstallIn(SingletonComponent::class)
    @EntryPoint
    interface AppAuthEntryPoint {
        fun apiService(): ApiService
    }

    fun getToken(): String? {
        return prefs.getString(TOKEN_KEY, null)
    }

    fun getId(): Int {
        return prefs.getInt(ID_KEY, 0)
    }

    @Synchronized
    fun setAuth(id: Int, token: String) {
        _data.value = AuthModel(id, token)
        prefs.edit {
            putInt(ID_KEY, id)
            putString(TOKEN_KEY, token)
        }
    }

    @Synchronized
    fun removeAuth() {
        _data.value = null
        prefs.edit { clear() }
    }
}
