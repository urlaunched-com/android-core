package com.urlaunched.android.common.coil

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

object DownloadImageHelper {
    suspend fun downloadImage(mediaLink: String, context: Context): File? = withContext(Dispatchers.IO) {
        try {
            val url = URL(mediaLink)
            val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val inputStream: InputStream = connection.inputStream

            val cacheDir = context.cacheDir
            val directory = File(cacheDir.toString())
            val outputFile = File.createTempFile("temp_cached_image", ".jpg", directory)

            val outputStream = FileOutputStream(outputFile)
            val buffer = ByteArray(1024)
            var bytesRead: Int
            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                outputStream.write(buffer, 0, bytesRead)
            }
            outputStream.flush()
            outputStream.close()
            inputStream.close()

            return@withContext outputFile
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext null
        }
    }
}