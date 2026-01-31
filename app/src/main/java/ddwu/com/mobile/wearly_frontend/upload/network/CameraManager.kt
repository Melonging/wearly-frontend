package ddwu.com.mobile.wearly_frontend.upload.network

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.os.postDelayed
import androidx.fragment.app.Fragment
import ddwu.com.mobile.wearly_frontend.upload.ui.LoadingActivity
import java.io.File
import java.io.IOException
import java.util.logging.Handler

class CameraManager(private val fragment: Fragment) {

    var callback: ((uri: Uri) -> Unit)? = null
    var currentPhotoUri: Uri? = null
    var currentPhotoPath: String? = null

    fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(fragment.requireContext(), Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED) {
            dispatchTakePictureIntent()
        } else {
            fragment.requestPermissions(arrayOf(Manifest.permission.CAMERA), 101)
        }
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(fragment.requireActivity().packageManager) != null) {
            val photoFile: File? = try {
                val file = FileUtil.createNewFile(fragment.requireContext())
                currentPhotoPath = file.absolutePath
                file
            } catch (e: IOException) {
                Log.e("CameraManager", "이미지 파일 생성 오류", e)
                null
            }
            photoFile?.also {
                val photoURI: Uri = FileProvider.getUriForFile(
                    fragment.requireContext(),
                    "ddwu.com.mobile.a01_20211442.fileprovider",
                    it
                )
                currentPhotoUri = photoURI
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                fragment.startActivityForResult(takePictureIntent, 102)
            }
        }
    }

    // 촬영 완료 후 Fragment에서 호출
    fun onPhotoCaptured() {
        currentPhotoUri?.let { callback?.invoke(it) }
    }
}