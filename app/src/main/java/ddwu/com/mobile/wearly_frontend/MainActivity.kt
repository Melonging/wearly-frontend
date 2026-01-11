package ddwu.com.mobile.wearly_frontend

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import ddwu.com.mobile.wearly_frontend.databinding.ActivityMainBinding
<<<<<<< HEAD
import ddwu.com.mobile.wearly_frontend.ui.fragment.ClosetCardFragment
=======
>>>>>>> 675c41e70625676b644f8ab488023362e5410094

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
<<<<<<< HEAD

=======
>>>>>>> 675c41e70625676b644f8ab488023362e5410094
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //ClosetCardFragment연결
        supportFragmentManager.beginTransaction()
            .replace(R.id.main, ClosetCardFragment())
            .commit()

    }
}