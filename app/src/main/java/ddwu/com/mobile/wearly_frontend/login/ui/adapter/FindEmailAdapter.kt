package ddwu.com.mobile.wearly_frontend.login.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import ddwu.com.mobile.wearly_frontend.login.ui.FindEmailFoundFragment
import ddwu.com.mobile.wearly_frontend.login.ui.FindEmailPhnoFragment
import ddwu.com.mobile.wearly_frontend.login.ui.FindEmailVerifCodeFragment

class FindEmailAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            // 첫 번째 - 휴대폰 번호 입력
            0 -> FindEmailPhnoFragment()

            // 두 번째 - 인증 번호 확인
            1 -> FindEmailVerifCodeFragment()

            // 세 번째 - 이메일 확인
            else -> FindEmailFoundFragment()
        }
    }
}