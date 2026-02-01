package ddwu.com.mobile.wearly_frontend.upload.network

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date

class FileUtil {
    companion object{
        private const val TAG = "Upload_FileUtil"
        private const val FILENAME_FORMAT = "yyyyMMdd_HHmmss"

        private fun getFileName(context: Context) : String {
            val timeStamp: String = SimpleDateFormat(FILENAME_FORMAT).format(Date())
            val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            return "${storageDir?.path}/${timeStamp}.jpg"
        }

        @Throws(IOException::class)
        fun createNewFile(context: Context): File {
            val file = File(getFileName(context))
            return file
        }

        /* 갤러리 사진 저장 */
        fun saveFileToExtstorage(context: Context, sourceUri: Uri?) : String? {
            if(sourceUri == null){
                Log.e(TAG, "sourceUri is null")
                return null
            }

            val saveTargetFile = File(getFileName(context))

            return try{
                context.contentResolver.openInputStream(sourceUri)?.use { input ->
                    FileOutputStream(saveTargetFile).use { output ->
                        input.copyTo(output)
                    }
                } ?: null
                Log.d(TAG, "사진 저장 완료: ${saveTargetFile.absolutePath}")
                saveTargetFile.absolutePath
            } catch (e: Exception) {
                Log.e(TAG, "사진 저장 실패")
                return null
            }
        }
        @Throws(IOException::class)
        fun deleteFile(filePath: String?) : Boolean {
            if(filePath.isNullOrEmpty())
                return false
            val file = File(filePath)
            return if(file.exists()){
                val deleted = file.delete()
                Log.d(TAG, "파일 삭제 성공: $deleted")
                deleted
            } else {
                Log.e(TAG, "파일 삭제 실패")
                false
            }
        }
    }
}