package ddwu.com.mobile.wearly_frontend.closet.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialog
import ddwu.com.mobile.wearly_frontend.databinding.FragmentClosetCardBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ClosetCardFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ClosetCardFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    lateinit var binding : FragmentClosetCardBinding

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
        binding = FragmentClosetCardBinding.inflate(inflater, container, false)

        // Setup icon click listeners
        binding.addIconIv.setOnClickListener {
            val dialog = PlusClosetDialogFragment().apply {
                currentType = PlusClosetDialogFragment.WardrobeType.CLOSET
                currentName = "옷장1"
                listener = object : PlusClosetDialogFragment.OnWardrobeEditedListener {
                    override fun onWardrobeEdited(
                        type: PlusClosetDialogFragment.WardrobeType,
                        name: String
                    ) {
                        // TODO: 편집 결과 반영
                    }
                }
            }
            dialog.show(childFragmentManager, "PlusWardrobeDialog")
        }

        binding.moreOptionsIv.setOnClickListener {

            val dialog = EditClosetDialogFragment().apply {
                currentType = EditClosetDialogFragment.WardrobeType.CLOSET
                currentName = "옷장1"
                listener = object : EditClosetDialogFragment.OnWardrobeEditedListener {
                    override fun onWardrobeEdited(
                        type: EditClosetDialogFragment.WardrobeType,
                        name: String
                    ) {
                        // TODO: 편집 결과 반영
                    }
                }
            }
            dialog.show(childFragmentManager, "EditWardrobeDialog")
        }

        binding.viewLayoutIv.setOnClickListener {
            // 아이콘 ic_closet_view_card로 아이콘 변경
            //리스트뷰 형태의 fragement로 변경

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
         * @return A new instance of fragment ClosetCardFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ClosetCardFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}

