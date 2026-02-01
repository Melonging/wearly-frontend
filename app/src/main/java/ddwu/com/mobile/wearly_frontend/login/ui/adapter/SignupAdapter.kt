package ddwu.com.mobile.wearly_frontend.login.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import ddwu.com.mobile.wearly_frontend.login.ui.ResetPwPhnoFragment
import ddwu.com.mobile.wearly_frontend.login.ui.ResetPwResetFragment
import ddwu.com.mobile.wearly_frontend.login.ui.ResetPwVerifCodeFragment
import ddwu.com.mobile.wearly_frontend.login.ui.SignupInfoFragment
import ddwu.com.mobile.wearly_frontend.login.ui.SignupVerifCodeFragment

class SignupAdapter(fa: FragmentActivity): FragmentStateAdapter(fa) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            // 첫 번째 - 정보 입력
            0 -> SignupInfoFragment()

            // 두 번째 - 이메일 인증
            else -> SignupVerifCodeFragment()
        }
    }
}