package com.example.app.model

import android.content.Context
import android.net.Uri
import okhttp3.ResponseBody
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

object FileUtils {
    fun getFileFromUri(context: Context, uri: Uri): File? {
        try {
            val contentResolver = context.contentResolver
            val inputStream: InputStream? = contentResolver.openInputStream(uri)
            val fileName = getFileName(context, uri) ?: "temp_file"

            // Tạo file tạm trong thư mục cache của app
            val file = File(context.cacheDir, fileName)
            val outputStream = FileOutputStream(file)

            inputStream?.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }
            return file
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    // Hàm lấy tên file (Optional, có thể đặt tên cứng nếu muốn)
    private fun getFileName(context: Context, uri: Uri): String? {
        // Logic lấy tên file từ ContentResolver (lược giản để tập trung vào logic chính)
        return "upload_temp_${System.currentTimeMillis()}"
    }
    fun saveSongToStorage(
        context: Context,
        body: ResponseBody,
        fileName: String,
        onProgress: (Float) -> Unit
    ): File? {
        try {
            val file = File(context.getExternalFilesDir(null), "$fileName.mp3")
            val inputStream = body.byteStream()
            val outputStream = FileOutputStream(file)
            val totalBytes = body.contentLength()
            val data = ByteArray(4096)
            var bytesRead: Int
            var downloadedBytes: Long = 0

            while (inputStream.read(data).also { bytesRead = it } != -1) {
                outputStream.write(data, 0, bytesRead)
                downloadedBytes += bytesRead
                if (totalBytes > 0) {
                    onProgress(downloadedBytes.toFloat() / totalBytes)
                }
            }
            outputStream.flush()
            return file
        } catch (e: Exception) {
            return null
        }
    }
}