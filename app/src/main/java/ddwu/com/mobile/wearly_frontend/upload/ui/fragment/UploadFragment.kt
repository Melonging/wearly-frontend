package ddwu.com.mobile.wearly_frontend.upload.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import ddwu.com.mobile.wearly_frontend.R
import ddwu.com.mobile.wearly_frontend.databinding.FragmentUploadBinding
import ddwu.com.mobile.wearly_frontend.upload.data.slot.SlotItem
import ddwu.com.mobile.wearly_frontend.upload.ui.adapter.ClothingAdapter

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [UploadFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class UploadFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    // binding
    lateinit var binding: FragmentUploadBinding

    private val items = ArrayList<SlotItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUploadBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        items.add(SlotItem.Empty)

        val layoutManager = GridLayoutManager(requireContext(), 3)
        binding.itemRV.layoutManager = layoutManager

        // 원본 val adapter = ClothingAdapter(requireContext(), items)
        val adapter = ClothingAdapter(
                list = items,
        onAddClick = {
            // 더하기(+) 버튼 클릭 시 실행할 코드 (예: 카메라 열기)
            // 지금 당장 구현할 게 없다면 비워두세요.
        },
        onImageClick = { imageItem ->
            // 이미지를 클릭했을 때 실행할 코드
            // imageItem.imageUrl 등을 사용 가능합니다.
        }
        )
        binding.itemRV.adapter = adapter

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment UploadFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            UploadFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}