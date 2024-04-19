package com.urlaunched.android.common.files

import android.content.Context
import java.io.File

object FileHelper {
    private const val TEMP_FILE_PREFIX = "temp_"
    private const val CAMERA_PICKER_CACHE_FOLDER = "image_pick"
    private const val VIDEO_DOWNLOAD_FOLDER = "video_download"

    fun deleteFileByName(context: Context, directory: String, name: String): Boolean =
        deleteFileByName(filesDir = context.filesDir, directory = directory, name = name)

    fun getFileByName(context: Context, directory: String, name: String): File? =
        getFileByName(filesDir = context.filesDir, directory = directory, name = name)

    fun clearDirectory(context: Context, directory: String): Boolean = clearDirectory(
        filesDir = context.filesDir,
        directory = directory
    )

    fun getFileByName(filesDir: File, directory: String, name: String): File? {
        val files = File(filesDir, directory).listFiles()
        if (files != null) {
            for (file in files) {
                if (file.nameWithoutExtension == name) {
                    return file
                }
            }
        }
        return null
    }

    fun clearDirectory(filesDir: File, directory: String): Boolean {
        val dir = File(filesDir, directory)
        if (dir.exists() && dir.isDirectory) {
            val files = dir.listFiles()
            if (files != null) {
                for (file in files) {
                    file.delete()
                }
                return true
            }
        }
        return false
    }

    fun deleteFileByName(filesDir: File, directory: String, name: String): Boolean {
        val files = File(filesDir, directory).listFiles()
        if (files != null) {
            for (file in files) {
                if (file.nameWithoutExtension == name) {
                    return file.delete()
                }
            }
        }
        return false
    }

    fun deleteTempFilesFromCache(context: Context) {
        val cacheDir = context.cacheDir
        val tempFiles = cacheDir.listFiles { _, name -> name.contains(TEMP_FILE_PREFIX) }

        if (tempFiles != null) {
            for (file in tempFiles) {
                file.delete()
            }
        }
    }

    fun createTempFile(context: Context, fileExtension: String): File =
        File.createTempFile(TEMP_FILE_PREFIX, fileExtension, context.cacheDir)

    fun createTempCameraPickFile(context: Context, fileExtension: String): File {
        val pickDir = File(context.cacheDir, CAMERA_PICKER_CACHE_FOLDER)
        if (!pickDir.exists()) {
            pickDir.mkdir()
        }

        return File.createTempFile(TEMP_FILE_PREFIX, fileExtension, pickDir)
    }

    fun getVideDownloadFolderPath(context: Context): String {
        val file = File(context.cacheDir, VIDEO_DOWNLOAD_FOLDER)
        if (!file.exists()) {
            file.mkdir()
        }
        return file.absolutePath
    }
}