package ddwu.com.mobile.wearly_frontend.closet.ui.fragment

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ddwu.com.mobile.wearly_frontend.R
import ddwu.com.mobile.wearly_frontend.closet.ui.fragment.PlusClosetDialogFragment.WardrobeType
import ddwu.com.mobile.wearly_frontend.databinding.DialogEditClosetBinding


class EditClosetDialogFragment : BottomSheetDialogFragment() {

    enum class WardrobeType { CLOSET, DRAWER, SHOES }

    interface OnWardrobeEditedListener {
        fun onWardrobeEdited(type: WardrobeType, name: String)
    }

    lateinit var binding : DialogEditClosetBinding
    var currentType: WardrobeType = WardrobeType.CLOSET
    var currentName: String? = null
    var listener: OnWardrobeEditedListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.AppBottomSheetDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogEditClosetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.closeIc.setOnClickListener { dismiss() }

        //전달받은 값 세팅
        binding.nameEt.setText(currentName)
        updateTypeSelection(currentType)

        binding.typeClosetTv.setOnClickListener {
            currentType = WardrobeType.CLOSET
            updateTypeSelection(currentType)
        }
        binding.typeDrawerTv.setOnClickListener {
            currentType = WardrobeType.DRAWER
            updateTypeSelection(currentType)
        }
        binding.typeShoesTv.setOnClickListener {
            currentType = WardrobeType.SHOES
            updateTypeSelection(currentType)
        }

        binding.confirmBtn.setOnClickListener {
            val name = binding.nameEt.text?.toString()?.trim().orEmpty()
            if (name.isNotEmpty()) {
                listener?.onWardrobeEdited(currentType, name)
                dismiss()
            }
        }
    }

    private fun updateTypeSelection(type: WardrobeType) {
        binding.typeClosetTv.isSelected = (type == WardrobeType.CLOSET)
        binding.typeDrawerTv.isSelected = (type == WardrobeType.DRAWER)
        binding.typeShoesTv.isSelected = (type == WardrobeType.SHOES)

        val views = listOf(binding.typeClosetTv, binding.typeDrawerTv, binding.typeShoesTv)
        val types = listOf(WardrobeType.CLOSET, WardrobeType.DRAWER, WardrobeType.SHOES)

        views.forEachIndexed { index, textView ->
            if (types[index] == type) {
                textView.setTypeface(null, android.graphics.Typeface.BOLD)
            } else {
                textView.setTypeface(null, android.graphics.Typeface.NORMAL)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.let { window ->
            window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val params = window.attributes
            params.width = WindowManager.LayoutParams.MATCH_PARENT
            window.attributes = params
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}