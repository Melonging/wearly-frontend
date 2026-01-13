package ddwu.com.mobile.wearly_frontend.login.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ddwu.com.mobile.wearly_frontend.databinding.FragmentFindEmailPhnoBinding

class FindEmailPhnoFragment: Fragment(){

    lateinit var binding: FragmentFindEmailPhnoBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFindEmailPhnoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 클릭리스너

        // Activity nextPage() 호출 요청
        binding.findEmailPhnoSubmitBtn.setOnClickListener {
            (activity as? FindEmailActivity)?.nextPage()
        }

    }
}