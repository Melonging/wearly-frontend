package ddwu.com.mobile.wearly_frontend

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts // 💡 중요: 계약 객체 임포트 추가
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import ddwu.com.mobile.wearly_frontend.closet.ui.fragment.ClosetCardFragment
import ddwu.com.mobile.wearly_frontend.databinding.ActivityMainBinding
import ddwu.com.mobile.wearly_frontend.upload.TestActivity


class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    // 위치 권한 요청 비서
    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions.entries.all { it.value }
        if (granted) { }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        checkAndRequestPermissions()

        // Toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // Nav
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host) as NavHostFragment

        val navController = navHostFragment.navController

        binding.bottomNav.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.settingFragment -> {
                    binding.toolbar.visibility = View.GONE
                    binding.bottomNav.visibility = View.GONE
                }

                // 코디 일기 옷 선택 프레그먼트
                R.id.codiSelectCategoryFrament -> {
                    binding.bottomNav.visibility = View.GONE
                }

                // 코디 일기 작석 프레그먼트
                R.id.diaryWriteFragment -> {
                    binding.bottomNav.visibility = View.GONE
                }

                else -> {
                    binding.toolbar.visibility = View.VISIBLE
                    binding.bottomNav.visibility = View.VISIBLE
                }
            }
    }


        binding.bottomNav.setOnItemReselectedListener { item ->
            when (item.itemId) {
                R.id.homeFragment -> {
                }
                R.id.closetFragment -> {
                    //dispatchReselectToCurrentFragment { (it as? ProfileFragment)?.refresh() }

                }
            }
        }

    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        getMenuInflater().inflate(R.menu.toolbar_menu, menu)
        return true
    }

    /**
     * 위치 권한 요청 팝업
     */
    private fun checkAndRequestPermissions() {
        val permissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        locationPermissionLauncher.launch(permissions)
    }

    // 임시 코드, 없애야 함.
    /*
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.btn_profile -> {
                /*val navController = findNavController(R.id.nav_host)
                if (navController.currentDestination?.id != R.id.settingFragment) {
                    navController.navigate(R.id.settingFragment)
                }

                 */
                val intent = Intent(this, TestActivity::class.java)
                startActivity(intent)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

     */
}