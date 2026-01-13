package ddwu.com.mobile.wearly_frontend.login.ui

import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ddwu.com.mobile.wearly_frontend.databinding.FragmentFindEmailVerifCodeBinding

class FindEmailVerifCodeFragment: Fragment() {

    lateinit var binding: FragmentFindEmailVerifCodeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFindEmailVerifCodeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // (디자인) "다시 보내기" 밑줄
        binding.findEmailVcRetryTv.paintFlags = binding.findEmailVcRetryTv.paintFlags or Paint.UNDERLINE_TEXT_FLAG



        // 클릭리스너

        // Activity nextPage() 호출 요청
        binding.findEmailVcSubmitBtn.setOnClickListener {
            (activity as? FindEmailActivity)?.nextPage()
        }
    }
}