package ddwu.com.mobile.wearly_frontend.upload.ui.fragment

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import ddwu.com.mobile.wearly_frontend.R
import ddwu.com.mobile.wearly_frontend.databinding.FragmentClothingDetailBinding
import ddwu.com.mobile.wearly_frontend.upload.data.entity.ClothingDetail

class ClothingDetailFragment : Fragment() {

    private lateinit var binding: FragmentClothingDetailBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentClothingDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val detail = arguments?.getParcelable<ClothingDetail>("detail")
            ?: return

        when {
            detail?.uri != null -> {
                Glide.with(this)
                    .load(Uri.parse(detail.uri))
                    .into(binding.clothingImage)
            }
            detail?.resId != null -> {
                binding.clothingImage.setImageResource(detail.resId)
            }
        }

        binding.categoryText.text = detail?.category
        binding.tempText.text = "추천 날씨: ${detail?.recommendedTemp}도"
        binding.locationText.text = detail?.location

        binding.btnEdit.setOnClickListener {
            // TODO: 수정 화면/다이얼로그 열기
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ClothingDetailFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ClothingDetailFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}