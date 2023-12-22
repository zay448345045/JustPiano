package ly.pp.justpiano3.utils

import android.content.Context
import android.net.Uri
import android.provider.DocumentsContract
import android.provider.OpenableColumns
import androidx.core.util.Consumer
import androidx.documentfile.provider.DocumentFile
import okhttp3.Request
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.OutputStream

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

    fun moveFileToUri(context: Context, sourceFile: File, destinationUri: Uri?): Boolean {
        if (destinationUri == null) {
            return false
        }
        var input: FileInputStream? = null
        var output: OutputStream? = null
        var moveSuccess = false
        try {
            val contentResolver = context.contentResolver
            input = FileInputStream(sourceFile)
            output = contentResolver.openOutputStream(destinationUri, "w")
            output?.let { outStream ->
                input.copyTo(outStream)
            }
            moveSuccess = sourceFile.delete()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                input?.close()
                output?.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return moveSuccess
    }

    fun getUriInfo(context: Context, uri: Uri): UriInfo {
        val contentResolver = context.contentResolver
        val queryCursor = contentResolver.query(
            uri,
            arrayOf(
                OpenableColumns.DISPLAY_NAME,
                OpenableColumns.SIZE,
                DocumentsContract.Document.COLUMN_MIME_TYPE,
                DocumentsContract.Document.COLUMN_LAST_MODIFIED
            ),
            null,
            null,
            null
        )
        var displayName: String? = null
        var fileSize: Long? = null
        var modifiedTime: Long? = null
        queryCursor.use { cursor ->
            if (cursor != null && cursor.moveToFirst()) {
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                val mimeTypeIndex =
                    cursor.getColumnIndex(DocumentsContract.Document.COLUMN_MIME_TYPE)
                if (nameIndex != -1) {
                    displayName = cursor.getString(nameIndex)
                }
                // Check if the document is a directory by MIME type
                if (mimeTypeIndex != -1 && DocumentsContract.Document.MIME_TYPE_DIR == cursor.getString(
                        mimeTypeIndex
                    )
                ) {
                    fileSize = 0L
                } else if (sizeIndex != -1 && !cursor.isNull(sizeIndex)) {
                    fileSize = cursor.getLong(sizeIndex)
                }
                val modifiedIndex =
                    cursor.getColumnIndex(DocumentsContract.Document.COLUMN_LAST_MODIFIED)
                if (modifiedIndex != -1) {
                    modifiedTime = cursor.getLong(modifiedIndex)
                }
            }
        }
        return UriInfo(uri, displayName, fileSize, modifiedTime)
    }

    data class UriInfo(
        val uri: Uri?,
        val displayName: String?,
        val fileSize: Long?,
        val modifiedTime: Long?
    )

    fun downloadFile(
        url: String,
        file: File?,
        progress: Consumer<Int?>,
        success: Runnable,
        fail: Runnable
    ) {
        val request: Request = Request.Builder().url(url).build()
        try {
            OkHttpUtil.client().newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    var start = System.currentTimeMillis()
                    val length = response.body!!.contentLength()
                    // 下面从返回的输入流中读取字节数据并保存为本地文件
                    try {
                        response.body!!.byteStream().use { inputStream ->
                            FileOutputStream(file).use { fileOutputStream ->
                                val buf = ByteArray(100 * 1024)
                                var sum = 0
                                var len: Int
                                while (inputStream.read(buf).also { len = it } != -1) {
                                    fileOutputStream.write(buf, 0, len)
                                    sum += len
                                    if (System.currentTimeMillis() - start > 200) {
                                        start = System.currentTimeMillis()
                                        progress.accept((sum * 1.0f / length * 100).toInt())
                                    }
                                }
                                success.run()
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        fail.run()
                    }
                } else {
                    fail.run()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            fail.run()
        }
    }

    fun getOrCreateFileByUriFolder(
        context: Context,
        directoryUriString: String?,
        fileName: String
    ): Uri? {
        if (directoryUriString.isNullOrEmpty()) {
            // 目录URI字符串为空，直接使用应用的外部文件目录
            return getOrCreateFileInAppExternalDir(context, fileName)?.uri
        }
        return try {
            // 尝试用原始URI操作
            val directoryUri = Uri.parse(directoryUriString)
            getOrCreateDocumentFile(context, directoryUri, fileName)?.uri
        } catch (e: Exception) {
            // URI失效或其它异常，使用应用的外部文件目录
            getOrCreateFileInAppExternalDir(context, fileName)?.uri
        }
    }

    private fun getOrCreateFileInAppExternalDir(
        context: Context,
        fileName: String
    ): DocumentFile? {
        val filesDir = context.getExternalFilesDir(null)
        val newFile = File(filesDir, fileName)
        return if (newFile.exists()) {
            DocumentFile.fromFile(newFile)
        } else {
            try {
                if (newFile.createNewFile()) {
                    DocumentFile.fromFile(newFile)
                } else {
                    null
                }
            } catch (e: Exception) {
                null
            }
        }
    }

    private fun getOrCreateDocumentFile(
        context: Context,
        directoryUri: Uri,
        fileName: String
    ): DocumentFile? {
        val directoryDocumentFile = DocumentFile.fromTreeUri(context, directoryUri)
        val fileDocument = directoryDocumentFile?.findFile(fileName)
        return when {
            fileDocument != null && fileDocument.isFile -> fileDocument
            directoryDocumentFile?.canWrite() == true -> {
                directoryDocumentFile.createFile("*/*", fileName)
            }

            else -> null
        }
    }

    fun getDirectoryDocumentFile(
        context: Context,
        directoryUriString: String?,
        defaultPath: String?
    ): Pair<DocumentFile?, String?> {
        if (directoryUriString.isNullOrEmpty()) {
            // 目录URI字符串为空，直接使用应用的外部文件目录
            return Pair(getExternalFilesDir(context), defaultPath)
        }
        return try {
            // 尝试用原始URI操作
            val directoryUri = Uri.parse(directoryUriString)
            val directoryDocumentFile = DocumentFile.fromTreeUri(context, directoryUri)
            if (directoryDocumentFile != null && directoryDocumentFile.exists() && directoryDocumentFile.isDirectory) {
                Pair(directoryDocumentFile, directoryDocumentFile.name)
            } else {
                Pair(getExternalFilesDir(context), defaultPath)
            }
        } catch (e: Exception) {
            // 解析URI异常或其它错误，使用应用的外部文件目录
            Pair(getExternalFilesDir(context), defaultPath)
        }
    }

    private fun getExternalFilesDir(context: Context): DocumentFile? {
        val filesDir = context.getExternalFilesDir(null)
        return filesDir?.let {
            DocumentFile.fromTreeUri(context, Uri.fromFile(it))
        }
    }
}