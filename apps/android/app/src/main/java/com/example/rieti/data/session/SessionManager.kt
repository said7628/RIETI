package com.example.rieti.data.session

import android.content.Context
import com.example.rieti.data.network.LoginResponse
import com.example.rieti.data.network.RefreshResponse
import com.example.rieti.data.network.UserDto

class SessionManager(
    context: Context
) {

    private val prefs =
        context.getSharedPreferences(
            "rieti_session",
            Context.MODE_PRIVATE
        )

    fun saveLogin(
        response: LoginResponse
    ) {

        saveSession(
            response.accessToken,
            response.refreshToken,
            response.user
        )
    }

    fun saveRefresh(
        response: RefreshResponse
    ) {

        saveSession(
            response.accessToken,
            response.refreshToken,
            response.user
        )
    }

    fun saveSession(
        accessToken: String,
        refreshToken: String,
        user: UserDto
    ) {

        prefs.edit()
            .putString(
                "access_token",
                accessToken
            )
            .putString(
                "refresh_token",
                refreshToken
            )
            .putLong(
                "user_id",
                user.id
            )
            .putString(
                "name",
                user.name
            )
            .putString(
                "email",
                user.email
            )
            .putString(
                "role",
                user.roleCode
            )
            .apply()
    }

    fun saveRecoveryEmail(
        email: String
    ) {
        prefs.edit()
            .putString(
                "recovery_email",
                email.trim().lowercase()
            )
            .apply()
    }

    fun recoveryEmail(): String? =
        prefs.getString(
            "recovery_email",
            null
        )

    fun accessToken(): String? =
        prefs.getString(
            "access_token",
            null
        )

    fun refreshToken(): String? =
        prefs.getString(
            "refresh_token",
            null
        )

    fun userEmail(): String? =
        prefs.getString(
            "email",
            null
        )

    fun userName(): String? =
        prefs.getString(
            "name",
            null
        )

    fun roleCode(): String? =
        prefs.getString(
            "role",
            null
        )

    fun hasSession(): Boolean =
        !refreshToken().isNullOrBlank()

    fun clear() {
        prefs.edit()
            .clear()
            .apply()
    }
}
