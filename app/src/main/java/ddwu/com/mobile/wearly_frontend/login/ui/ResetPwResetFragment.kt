package ddwu.com.mobile.wearly_frontend.login.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ddwu.com.mobile.wearly_frontend.databinding.FragmentResetPwResetBinding

class ResetPwResetFragment: Fragment() {

    lateinit var binding: FragmentResetPwResetBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentResetPwResetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 클릭리스너

        // Activity nextPage() 호출 요청
        binding.resetPwRsSubmitBtn.setOnClickListener {
            (activity as? ResetPwActivity)?.nextPage()
        }
    }
}