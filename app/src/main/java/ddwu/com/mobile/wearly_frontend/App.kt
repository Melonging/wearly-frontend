package ddwu.com.mobile.wearly_frontend

import android.app.Application

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        TokenManager.init(this)
    }
}