package ddwu.com.mobile.wearly_frontend.setting.ui

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import ddwu.com.mobile.wearly_frontend.R
import ddwu.com.mobile.wearly_frontend.TokenManager
import ddwu.com.mobile.wearly_frontend.databinding.FragmentSettingBinding


/**
 * A simple [Fragment] subclass.
 * Use the [SettingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SettingFragment : Fragment() {
    private lateinit var binding: FragmentSettingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = FragmentSettingBinding.inflate(inflater, container, false)

        binding.switchRemind.setOnCheckedChangeListener { _, isChecked ->
            binding.layoutRemindDetail.visibility =
                if (isChecked) View.VISIBLE else View.GONE
        }
        val times = listOf(
            "오전 12시","오전 1시","오전 2시","오전 3시",
            "오전 4시","오전 5시","오전 6시","오전 7시",
            "오전 8시","오전 9시","오전 10시","오전 11시",
            "오후 12시","오후 1시","오후 2시","오후 3시",
            "오후 4시","오후 5시","오후 6시","오후 7시",
            "오후 8시","오후 9시","오후 10시","오후 11시"
        )

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1, times
        )

        binding.actRemindTime.setAdapter(adapter)
        binding.actRemindTime.setText(times[0], false)

        binding.chipGroupDays.setOnCheckedStateChangeListener { group, checkedIds ->


        }

        val name = TokenManager.getUserName(requireContext()) ?: "사용자"
        binding.tvName.text = name

        binding.rowAppLock.setOnClickListener {
            val intent = Intent(requireContext(), PinActivity::class.java)
            startActivity(intent)

        }


        return binding.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SettingFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SettingFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}