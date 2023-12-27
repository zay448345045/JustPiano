package ly.pp.justpiano3.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

public final class GZIPUtil {

    public static String toZIP(String str) {
        if (str == null || str.length() == 0) {
            return str;
        }
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            GZIPOutputStream gZIPOutputStream = new GZIPOutputStream(byteArrayOutputStream);
            gZIPOutputStream.write(str.getBytes(StandardCharsets.UTF_8));
            gZIPOutputStream.close();
            str = byteArrayOutputStream.toString("ISO-8859-1");
            byteArrayOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static List<File> ZIPFileTo(File file, String str) {
        List<File> unZipFileList = new ArrayList<>();
        File file2 = new File(str);
        if (!file2.exists()) {
            file2.mkdirs();
        }
        try {
            ZipFile zipFile;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                zipFile = new ZipFile(file, Charset.forName("GBK"));
            } else {
                zipFile = new ZipFile(file);
            }
            Enumeration<?> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry zipEntry = (ZipEntry) entries.nextElement();
                InputStream inputStream = zipFile.getInputStream(zipEntry);
                file2 = new File(new String((str + File.separator + zipEntry.getName()).getBytes(StandardCharsets.ISO_8859_1), "GB2312"));
                if (!file2.exists()) {
                    File parentFile = file2.getParentFile();
                    if (!parentFile.exists()) {
                        parentFile.mkdirs();
                    }
                    file2.createNewFile();
                }
                OutputStream fileOutputStream = new FileOutputStream(file2);
                byte[] bArr = new byte[256];
                while (true) {
                    int read = inputStream.read(bArr);
                    if (read <= 0) {
                        break;
                    }
                    fileOutputStream.write(bArr, 0, read);
                }
                inputStream.close();
                fileOutputStream.close();
                unZipFileList.add(file2);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return unZipFileList;
    }

    public static List<File> unzipFromUri(Context context, Uri zipFileUri, String outputDirPath) {
        List<File> unzippedFileList = new ArrayList<>();
        File outputDir = new File(outputDirPath);
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }
        ZipInputStream zipInputStream = null;
        try {
            ContentResolver resolver = context.getContentResolver();
            InputStream inputStream = resolver.openInputStream(zipFileUri);
            if (inputStream == null) {
                return unzippedFileList;
            }
            zipInputStream = new ZipInputStream(inputStream);

            ZipEntry zipEntry;
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                String zipEntryName = new String(zipEntry.getName().getBytes(StandardCharsets.ISO_8859_1), "GBK");
                File outputFile = new File(outputDir, zipEntryName);
                if (zipEntry.isDirectory()) {
                    outputFile.mkdirs();
                } else {
                    if (!outputFile.getParentFile().exists()) {
                        outputFile.getParentFile().mkdirs();
                    }
                    try (OutputStream outputStream = new FileOutputStream(outputFile)) {
                        byte[] buffer = new byte[1024];
                        int count;
                        while ((count = zipInputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, count);
                        }
                    }
                }
                zipInputStream.closeEntry();
                unzippedFileList.add(outputFile);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (zipInputStream != null) {
                try {
                    zipInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return unzippedFileList;
    }

    public static String ZIPTo(String str) {
        if (str == null || str.length() == 0) {
            return str;
        }
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            InputStream byteArrayInputStream = new ByteArrayInputStream(str.getBytes(StandardCharsets.ISO_8859_1));
            GZIPInputStream gZIPInputStream = new GZIPInputStream(byteArrayInputStream, 256);
            byte[] bArr = new byte[256];
            while (true) {
                int read = gZIPInputStream.read(bArr);
                if (read < 0) {
                    str = byteArrayOutputStream.toString();
                    byteArrayOutputStream.close();
                    byteArrayInputStream.close();
                    gZIPInputStream.close();
                    return str;
                }
                byteArrayOutputStream.write(bArr, 0, read);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static String arrayToZIP(byte[] arr) {//byte数组压缩
        String str = null;
        if (arr == null) {
            return "";
        }
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            GZIPOutputStream gZIPOutputStream = new GZIPOutputStream(byteArrayOutputStream);
            gZIPOutputStream.write(arr);
            gZIPOutputStream.close();
            str = byteArrayOutputStream.toString("ISO-8859-1");
            byteArrayOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static byte[] ZIPToArray(String str) {
        if (str == null || str.length() == 0) {
            return null;
        }
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            InputStream byteArrayInputStream = new ByteArrayInputStream(str.getBytes(StandardCharsets.ISO_8859_1));
            GZIPInputStream gZIPInputStream = new GZIPInputStream(byteArrayInputStream, 256);
            byte[] bArr = new byte[256];
            while (true) {
                int read = gZIPInputStream.read(bArr);
                if (read < 0) {
                    bArr = byteArrayOutputStream.toByteArray();
                    byteArrayOutputStream.close();
                    byteArrayInputStream.close();
                    gZIPInputStream.close();
                    return bArr;
                }
                byteArrayOutputStream.write(bArr, 0, read);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
