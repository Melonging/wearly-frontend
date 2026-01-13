package ddwu.com.mobile.wearly_frontend.login.ui

import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ddwu.com.mobile.wearly_frontend.databinding.FragmentFindEmailFoundBinding

class FindEmailFoundFragment: Fragment() {

    lateinit var binding: FragmentFindEmailFoundBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFindEmailFoundBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // (디자인) "로그인하러 가기" 밑줄
        binding.findEmailFoundPwTv.paintFlags = binding.findEmailFoundPwTv.paintFlags or Paint.UNDERLINE_TEXT_FLAG


        // 클릭리스너

        // 다음 페이지
        binding.findEmailFoundSubmitBtn.setOnClickListener {
            (activity as? FindEmailActivity)?.nextPage()
        }

        // 비밀번호 재설정
        binding.findEmailFoundPwTv.setOnClickListener {
            val intent = Intent(requireContext(), ResetPwActivity::class.java)
            startActivity(intent)

            activity?.finish()
        }
    }
}