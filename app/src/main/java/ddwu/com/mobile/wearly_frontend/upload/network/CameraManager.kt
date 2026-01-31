package ddwu.com.mobile.wearly_frontend.upload.network

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.core.content.FileProvider
import java.io.File

class CameraManager {

    fun createImageFile(context: Context): File {
        return FileUtil.createNewFile(context)
    }

    fun createCameraIntent(
        context: Context,
        file: File
    ): Pair<Intent, Uri> {

        val uri = FileProvider.getUriForFile(
            context,
            "ddwu.com.mobile.a01_20211442.fileprovider",
            file
        )

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)

        return intent to uri
    }
}
