package ddwu.com.mobile.wearly_frontend.setting.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import ddwu.com.mobile.wearly_frontend.R
import ddwu.com.mobile.wearly_frontend.databinding.ActivityPinEnabledBinding

class PinEnabledActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPinEnabledBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityPinEnabledBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        binding.cardChangePin.setOnClickListener {
            // PIN 변경 화면으로 이동
            startActivity(Intent(this, PinActivity::class.java))
        }

        binding.cardDisablePin.setOnClickListener {
            // PIN 해제 로직
        }
    }
}