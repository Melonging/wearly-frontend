package ddwu.com.mobile.wearly_frontend.upload.ui.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import ddwu.com.mobile.wearly_frontend.R
import ddwu.com.mobile.wearly_frontend.databinding.FragmentUploadBinding
import ddwu.com.mobile.wearly_frontend.upload.data.SlotItem
import ddwu.com.mobile.wearly_frontend.upload.data.entity.ClothingDetail
import ddwu.com.mobile.wearly_frontend.upload.network.FileUtil
import ddwu.com.mobile.wearly_frontend.upload.ui.LoadingActivity
import ddwu.com.mobile.wearly_frontend.upload.ui.adapter.ClothingAdapter
class UploadFragment : Fragment() {

    // binding
    lateinit var binding: FragmentUploadBinding
    private val items = ArrayList<SlotItem>()
    private var currentPhotoUri: Uri? = null
    private lateinit var adapter: ClothingAdapter

    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                currentPhotoUri?.let { uri ->
                    openLoading(uri)
                }
            }
        }
    val loadingActivityLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val resultText = result.data?.getStringExtra("resultText")
                resultText?.let { showResultDialog(it) }
            }

            currentPhotoUri?.let { uri ->
                addPhotoToList(uri)
            }
        }

    private val cameraPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if(isGranted){
                openCameraInternal()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
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

        val type = arguments?.getString("containerType")
        val id = arguments?.getInt("containerId")
        val name = arguments?.getString("containerName")

        Log.d("UploadFragment", "type: $type, id: $id, name: $name")

        items.clear()
        val dummyList = listOf(
            R.drawable.cloth_01,
            R.drawable.cloth_02,
            R.drawable.cloth_03,
        )

        dummyList.forEach { items.add(SlotItem.Image(resId = it)) }
        items.add(SlotItem.Empty)

        val layoutManager = GridLayoutManager(requireContext(), 3)
        binding.itemRV.layoutManager = layoutManager

        adapter = ClothingAdapter(
            items,
            onAddClick = { openCamera() },
            onImageClick = { imageItem ->
                val detail = ClothingDetail(
                    uri = imageItem.uri?.toString(),
                    resId = imageItem.resId,
                    category = "아우터",
                    recommendedTemp = 22,
                    location = "행거1"
                )

                val bundle = Bundle().apply {
                    putParcelable("detail", detail)
                }

                findNavController().navigate(
                    R.id.action_uploadFragment_to_clothingDetailFragment,
                    bundle
                )
            }
        )
        binding.itemRV.adapter = adapter
    }

    private fun openCamera(){
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            openCameraInternal()
        } else {
            cameraPermissionLauncher.launch(android.Manifest.permission.CAMERA)
        }
    }

    private fun openCameraInternal(){
        val photoFile = FileUtil.createNewFile(requireContext())
        val uri = FileProvider.getUriForFile(
            requireContext(),
            "ddwu.com.mobile.wearly_frontend.fileprovider",
            photoFile
        )

        currentPhotoUri = uri
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
        cameraLauncher.launch(intent)
    }
    fun showResultDialog(resultText: String) {
        val dialog = AlertDialog.Builder(requireContext())
            .setMessage(resultText)
            .setCancelable(true)
            .create()

        dialog.show()
        Handler(Looper.getMainLooper()).postDelayed({
            if (dialog.isShowing) dialog.dismiss()
        }, 1500)
    }

    private fun openLoading(uri: Uri){
        val intent = Intent(requireContext(), LoadingActivity::class.java)
        intent.putExtra("photoUri", uri.toString())
        loadingActivityLauncher.launch(intent)
    }

    private fun addPhotoToList(uri: Uri) {
        val lastIndex = items.lastIndex
        if (lastIndex >= 0 && items[lastIndex] is SlotItem.Empty) {
            items.removeAt(lastIndex)
        }

        // 2) 이미지 아이템 추가 (String path에 uri.toString() 저장)
        items.add(SlotItem.Image(uri))

        // 3) Empty 다시 추가 (항상 마지막 칸에 +)
        items.add(SlotItem.Empty)

        // 4) 갱신
        adapter.notifyDataSetChanged()
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
                }
            }
    }
}