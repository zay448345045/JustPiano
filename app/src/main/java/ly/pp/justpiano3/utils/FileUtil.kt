package ly.pp.justpiano3.utils

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

object FileUtil {

    /**
     * 复制文件到应用目录
     */
    fun copyFileToAppFilesDir(context: Context, sf2File: File): String? {
        val cacheFile = File(context.filesDir, sf2File.name)
        try {
            FileInputStream(sf2File).use { inputStream ->
                FileOutputStream(cacheFile).use { outputStream ->
                    val buf = ByteArray(1024)
                    var len: Int
                    while (inputStream.read(buf).also { len = it } > 0) {
                        outputStream.write(buf, 0, len)
                    }
                }
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return cacheFile.absolutePath
    }

    /**
     * 移动文件到新文件的位置（拷贝流）
     *
     * @param src 源文件对象
     * @param des 目标文件对象
     */
    fun moveFile(src: File, des: File): Boolean {
        if (!src.exists()) {
            return false
        }
        if (des.exists()) {
            des.delete()
        }
        try {
            BufferedInputStream(FileInputStream(src)).use { reader ->
                BufferedOutputStream(
                    FileOutputStream(des)
                ).use { writer ->
                    val buffer = ByteArray(1024)
                    var length: Int
                    while (reader.read(buffer).also { length = it } != -1) {
                        writer.write(buffer, 0, length)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        } finally {
            src.delete()
        }
        return true
    }

    fun getUriInfo(context: Context, uri: Uri): UriInfo {
        val contentResolver = context.contentResolver
        val queryCursor = contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE), null, null, null)
        var displayName: String? = null
        var fileSize: Long? = null
        queryCursor.use { cursor ->
            if (cursor != null && cursor.moveToFirst()) {
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                if (nameIndex != -1) {
                    displayName = cursor.getString(nameIndex)
                }
                if (sizeIndex != -1 && !cursor.isNull(sizeIndex)) {
                    fileSize = cursor.getLong(sizeIndex)
                }
            }
        }
        return UriInfo(uri, displayName, fileSize)
    }

    data class UriInfo(val uri: Uri?, val fileName: String?, val fileSize: Long?)
}