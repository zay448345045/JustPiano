package ly.pp.justpiano3.utils

import android.content.Context
import android.net.Uri
import android.provider.DocumentsContract
import android.provider.OpenableColumns
import androidx.core.content.FileProvider
import androidx.core.util.Consumer
import androidx.documentfile.provider.DocumentFile
import okhttp3.Request
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.OutputStream
import java.net.URLDecoder


object FileUtil {

    data class UriInfo(
        val uri: Uri?,
        val displayName: String?,
        val fileSize: Long?,
        val modifiedTime: Long?
    )

    /**
     * 复制文件到应用目录
     */
    @JvmStatic
    fun copyDocumentFileToAppFilesDir(context: Context, sf2DocumentFile: DocumentFile): String? {
        val cacheFile = File(context.filesDir, sf2DocumentFile.name ?: return null)
        try {
            context.contentResolver.openInputStream(sf2DocumentFile.uri).use { inputStream ->
                FileOutputStream(cacheFile).use { outputStream ->
                    val buf = ByteArray(1024)
                    var len: Int
                    while (inputStream?.read(buf).also { len = it ?: -1 }!! > 0) {
                        outputStream.write(buf, 0, len)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
        return cacheFile.absolutePath
    }

    @JvmStatic
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

    @JvmStatic
    fun getUriInfo(context: Context, uri: Uri?): UriInfo {
        return if (uri == null) {
            UriInfo(null, null, null, null)
        } else if ("content".equals(uri.scheme, ignoreCase = true)) {
            getUriInfoFromContent(context, uri)
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            getUriInfoFromFile(uri)
        } else {
            UriInfo(uri, null, null, null)
        }
    }

    private fun getUriInfoFromFile(uri: Uri): UriInfo {
        val file = File(uri.path!!)
        val displayName = file.name
        val fileSize = if (file.isFile) file.length() else null
        val modifiedTime = if (file.isFile) file.lastModified() else null
        return UriInfo(uri, displayName, fileSize, modifiedTime)
    }

    private fun getUriInfoFromContent(context: Context, uri: Uri): UriInfo {
        try {
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
        } catch (e: Exception) {
            return UriInfo(null, null, null, null)
        }
    }

    @JvmStatic
    fun getFolderUriInfo(context: Context, uri: Uri?): UriInfo {
        if (uri == null) {
            return UriInfo(null, null, null, null)
        }
        val documentFile = DocumentFile.fromTreeUri(context, uri)
        if (documentFile != null && documentFile.isDirectory) {
            return UriInfo(uri, getRelativePath(context, uri), 0L, documentFile.lastModified())
        }
        return UriInfo(null, null, null, null)
    }

    @JvmStatic
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
                        if (file == null || (!file.exists() && !file.createNewFile())) {
                            fail.run()
                            return
                        }
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

    @JvmStatic
    fun getOrCreateFileByUriFolder(
        context: Context,
        directoryUriString: String?,
        defaultFolder: String,
        fileName: String
    ): Uri? {
        if (directoryUriString.isNullOrEmpty()) {
            // 目录URI字符串为空，直接使用应用的外部文件目录
            return getOrCreateFileInAppExternalDir(context, defaultFolder, fileName)?.uri
        }
        return try {
            // 尝试用原始URI操作
            val directoryUri = Uri.parse(directoryUriString)
            getOrCreateDocumentFile(context, directoryUri, fileName)?.uri
        } catch (e: Exception) {
            // URI失效或其它异常，使用应用的外部文件目录
            getOrCreateFileInAppExternalDir(context, defaultFolder, fileName)?.uri
        }
    }

    private fun getOrCreateFileInAppExternalDir(
        context: Context,
        defaultFolder: String,
        fileName: String
    ): DocumentFile? {
        val filesDir = File(context.getExternalFilesDir(null), defaultFolder)
        if (!filesDir.exists()) {
            filesDir.mkdirs()
        }
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

    @JvmStatic
    fun getDirectoryDocumentFile(
        context: Context,
        directoryUriString: String?,
        defaultFolder: String,
        defaultShow: String
    ): Pair<DocumentFile?, String?> {
        if (directoryUriString.isNullOrEmpty()) {
            // 目录URI字符串为空，直接使用应用的外部文件目录
            return Pair(getExternalFilesDir(context, defaultFolder), defaultShow)
        }
        return try {
            // 尝试用原始URI操作
            val directoryUri = Uri.parse(directoryUriString)
            val directoryDocumentFile = DocumentFile.fromTreeUri(context, directoryUri)
            if (directoryDocumentFile != null && directoryDocumentFile.exists() && directoryDocumentFile.isDirectory) {
                Pair(directoryDocumentFile, getRelativePath(context, directoryUri))
            } else {
                Pair(getExternalFilesDir(context, defaultFolder), defaultShow)
            }
        } catch (e: Exception) {
            // 解析URI异常或其它错误，使用应用的外部文件目录
            Pair(getExternalFilesDir(context, defaultFolder), defaultShow)
        }
    }

    private fun getRelativePath(context: Context?, uri: Uri): String? {
        // 尝试解析URI以获取相对路径
        val pathSegment = uri.lastPathSegment
        if (pathSegment != null) {
            try {
                val decodedPathSegment = URLDecoder.decode(pathSegment, "UTF-8")
                if (decodedPathSegment.startsWith("primary:")) {
                    return "SD卡/" + decodedPathSegment.substring("primary:".length)
                }
            } catch (e: Exception) {
                // nothing
            }
        }
        // 作为备选方案，使用`DocumentFile`的`getName()`
        val documentFile = DocumentFile.fromTreeUri(context!!, uri)
        return if (documentFile != null && documentFile.name != null) {
            documentFile.name
        } else null
    }

    private fun getExternalFilesDir(context: Context, defaultFolder: String): DocumentFile? {
        return context.getExternalFilesDir(null)
            ?.let { DocumentFile.fromFile(File(it, defaultFolder)) }
    }

    @JvmStatic
    fun deleteFileUsingUri(context: Context, uri: Uri): Boolean {
        return when (uri.scheme) {
            "file" -> {
                // 如果是file:// URI，使用File API删除文件
                val file = uri.path?.let { File(it) }
                file?.delete() ?: false
            }

            "content" -> {
                // 如果是content:// URI，通过ContentResolver来删除文件
                try {
                    val contentResolver = context.contentResolver
                    DocumentsContract.deleteDocument(contentResolver, uri)
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                    false
                }
            }

            else -> {
                // 如果是其他类型的URI，则无法处理
                false
            }
        }
    }

    @JvmStatic
    fun uriToDocumentFile(context: Context, uri: Uri): DocumentFile? {
        return when (uri.scheme) {
            "content" -> {
                DocumentFile.fromSingleUri(context, uri)
            }

            "file" -> {
                val file = File(uri.path ?: return null)
                if (file.exists()) {
                    DocumentFile.fromFile(file)
                } else {
                    null
                }
            }

            else -> null
        }
    }

    @JvmStatic
    fun getContentUriFromDocumentFile(context: Context, documentFile: DocumentFile): Uri? {
        if (documentFile.uri.scheme == "content") {
            return documentFile.uri
        } else {
            return try {
                documentFile.uri.path?.let { File(it) }?.let {
                    FileProvider.getUriForFile(
                        context,
                        "${context.applicationContext.packageName}.fileProvider",
                        it
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}