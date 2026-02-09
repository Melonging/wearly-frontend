package ddwu.com.mobile.wearly_frontend.codidiary.ui.fragment

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ddwu.com.mobile.wearly_frontend.R
import ddwu.com.mobile.wearly_frontend.databinding.FragmentRecentCodiDialogBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [RecentCodiDialogFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RecentCodiBottomSheet : BottomSheetDialogFragment() {

    lateinit var  binding : FragmentRecentCodiDialogBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRecentCodiDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 바텀 시트 설정
        val bottomSheet = dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        val behavior = BottomSheetBehavior.from(bottomSheet!!)

        behavior.apply {
            val peekHeightPx = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 450f, resources.displayMetrics
            ).toInt()
            peekHeight = peekHeightPx

            maxHeight = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 700f, resources.displayMetrics
            ).toInt()

            state = BottomSheetBehavior.STATE_COLLAPSED
            isFitToContents = false
        }

        // 닫기 버튼 클릭 시
       binding.closeIv.setOnClickListener {
            dismiss()
        }

        // RecyclerView 어댑터 연결


    }
}