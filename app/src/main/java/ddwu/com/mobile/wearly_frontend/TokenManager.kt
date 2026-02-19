package ddwu.com.mobile.wearly_frontend

import android.content.Context
import android.content.SharedPreferences

object TokenManager {

    private const val PREF = "auth_prefs"
    private const val KEY = "access_token"
    private lateinit var prefs: SharedPreferences

    private const val KEY_NAME = "user_name"

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
    }

    fun saveToken(token: String) {
        prefs.edit().putString(KEY, token).apply()
    }

    fun getToken(): String? {
        return prefs.getString(KEY, null)
    }

    fun clearToken() {
        prefs.edit().remove(KEY).apply()
    }

    fun saveUserName(context: Context, name: String) {
        context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_NAME, name)
            .apply()
    }

    fun getUserName(context: Context): String? {
        return context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
            .getString(KEY_NAME, null)
    }

    fun clear(context: Context) {
        context.getSharedPreferences(PREF, Context.MODE_PRIVATE).edit().clear().apply()
    }
}


/*
class TokenManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    // 토큰 저장
    fun saveToken(token: String) {
        prefs.edit().putString("access_token", token).apply()
    }

    // 토큰 꺼내기
    fun getToken(): String? {
        return prefs.getString("access_token", null)
    }

    // 로그아웃 시 토큰 삭제
    fun clearToken() {
        prefs.edit().remove("access_token").apply()
    }
}

 */