package ly.pp.justpiano3.utils

import android.annotation.SuppressLint
import android.content.*
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.*
import java.io.*

object FileUtil {

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

    fun getPathByUri(context: Context, uri: Uri): String? {
        // 以 file:// 开头的使用第三方应用打开 (open with third-party applications starting with file://)
        if (ContentResolver.SCHEME_FILE.equals(
                uri.scheme,
                ignoreCase = true
            )
        ) return getDataColumn(context, uri)

        @SuppressLint("ObsoleteSdkInt")
        val isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT

        // Before 4.4 , API 19 content:// 开头, 比如 content://media/external/images/media/123
        if (!isKitKat && ContentResolver.SCHEME_CONTENT.equals(uri.scheme, true)) {
            if (isGooglePhotosUri(uri)) return uri.lastPathSegment
            return getDataColumn(context, uri)
        }

        // After 4.4 , API 19
        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":").toTypedArray()
                val type = split[0]
                if ("primary".equals(type, ignoreCase = true)) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        val file = File(
                            context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
                                .toString() + File.separator + split[1]
                        )
                        return if (file.exists()) {
                            file.absolutePath
                        } else {
                            Environment.getExternalStorageDirectory()
                                .toString() + File.separator + split[1]
                        }
                    } else {
                        @Suppress("DEPRECATION")
                        return Environment.getExternalStorageDirectory()
                            .toString() + File.separator + split[1]
                    }
                } else if ("home".equals(type, ignoreCase = true)) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        return context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
                            .toString() + File.separator + "documents" + File.separator + split[1]
                    } else {
                        @Suppress("DEPRECATION")
                        return Environment.getExternalStorageDirectory()
                            .toString() + File.separator + "documents" + File.separator + split[1]
                    }
                } else {
                    @Suppress("DEPRECATION")
                    val sdcardPath =
                        Environment.getExternalStorageDirectory()
                            .toString() + File.separator + "documents" + File.separator + split[1]
                    return if (sdcardPath.startsWith("file://")) {
                        sdcardPath.replace("file://", "")
                    } else {
                        sdcardPath
                    }
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
                val id = DocumentsContract.getDocumentId(uri)
                if (id != null && id.startsWith("raw:")) {
                    return id.substring(4)
                }
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                    val contentUriPrefixesToTry = arrayOf(
                        "content://downloads/public_downloads",
                        "content://downloads/my_downloads",
                        "content://downloads/all_downloads"
                    )
                    for (contentUriPrefix in contentUriPrefixesToTry) {
                        val contentUri =
                            ContentUris.withAppendedId(Uri.parse(contentUriPrefix), id.toLong())
                        try {
                            val path = getDataColumn(context, contentUri)
                            if (!path.isNullOrBlank()) return path
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                } else {
                    //testPath(uri)
                    return getDataColumn(context, uri)
                }
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":").toTypedArray()
                val contentUri: Uri? = when (split[0]) {
                    "image" -> MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    "video" -> MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                    "audio" -> MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                    "download" -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        MediaStore.Downloads.EXTERNAL_CONTENT_URI
                    } else null

                    else -> null
                }
                val selectionArgs = arrayOf(split[1])
                return getDataColumn(context, contentUri, "_id=?", selectionArgs)
            }

            //GoogleDriveProvider
            else if (isGoogleDriveUri(uri)) {
                return getGoogleDriveFilePath(uri, context)
            }
        }
        // MediaStore (and general)
        else if ("content".equals(uri.scheme, ignoreCase = true)) {
            // Return the remote address
            if (isGooglePhotosUri(uri)) {
                return uri.lastPathSegment
            }
            // Google drive legacy provider
            else if (isGoogleDriveUri(uri)) {
                return getGoogleDriveFilePath(uri, context)
            }
            // Huawei
            else if (isHuaWeiUri(uri)) {
                val uriPath = getDataColumn(context, uri) ?: uri.toString()
                //content://com.huawei.hidisk.fileprovider/root/storage/emulated/0/Android/data/com.xxx.xxx/
                if (uriPath.startsWith("/root")) {
                    return uriPath.replace("/root".toRegex(), "")
                }
            }
            return getDataColumn(context, uri)
        }
        return getDataColumn(context, uri)
    }

    private fun getGoogleDriveFilePath(uri: Uri, context: Context): String? {
        context.contentResolver.query(uri, null, null, null, null)?.use { c: Cursor ->
            /*
             Get the column indexes of the data in the Cursor,
             move to the first row in the Cursor, get the data, and display it.
             */
            val nameIndex: Int = c.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            //val sizeIndex: Int = c.getColumnIndex(OpenableColumns.SIZE)
            if (!c.moveToFirst()) {
                return uri.toString()
            }
            val name: String = c.getString(nameIndex)
            //val size = c.getLong(sizeIndex).toString()
            val file = File(context.cacheDir, name)

            var inputStream: InputStream? = null
            var outputStream: FileOutputStream? = null
            try {
                inputStream = context.contentResolver.openInputStream(uri)
                outputStream = FileOutputStream(file)
                var read = 0
                val maxBufferSize = 1 * 1024 * 1024
                val bytesAvailable: Int = inputStream?.available() ?: 0
                val bufferSize = bytesAvailable.coerceAtMost(maxBufferSize)
                val buffers = ByteArray(bufferSize)
                while (inputStream?.read(buffers)?.also { read = it } != -1) {
                    outputStream.write(buffers, 0, read)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                inputStream?.close()
                outputStream?.close()
            }
            return file.path
        }
        return uri.toString()
    }

    private fun isMediaDocument(uri: Uri?): Boolean {
        return "com.android.providers.media.documents".equals(uri?.authority, true)
    }

    private fun isExternalStorageDocument(uri: Uri?): Boolean {
        return "com.android.externalstorage.documents".equals(uri?.authority, true)
    }

    private fun isDownloadsDocument(uri: Uri?): Boolean {
        return "com.android.providers.downloads.documents".equals(uri?.authority, true)
    }

    private fun isGooglePhotosUri(uri: Uri?): Boolean {
        return "com.google.android.apps.photos.content".equals(uri?.authority, true)
    }

    private fun isGoogleDriveUri(uri: Uri?): Boolean {
        return "com.google.android.apps.docs.storage.legacy" == uri?.authority || "com.google.android.apps.docs.storage" == uri?.authority
    }

    private fun isHuaWeiUri(uri: Uri?): Boolean {
        return "com.huawei.hidisk.fileprovider".equals(uri?.authority, true)
    }

    private fun getDataColumn(
        context: Context,
        uri: Uri?,
        selection: String? = null,
        selectionArgs: Array<String>? = null
    ): String? {
        @Suppress("DEPRECATION")
        val column = MediaStore.Files.FileColumns.DATA
        val projection = arrayOf(column)
        try {
            context.contentResolver.query(
                uri ?: return null,
                projection,
                selection,
                selectionArgs,
                null
            )?.use { c: Cursor ->
                if (c.moveToFirst()) {
                    val columnIndex = c.getColumnIndex(column)
                    return c.getString(columnIndex)
                }
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }
        return null
    }
}