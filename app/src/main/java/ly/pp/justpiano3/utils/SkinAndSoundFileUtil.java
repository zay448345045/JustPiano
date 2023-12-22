package ly.pp.justpiano3.utils;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public final class SkinAndSoundFileUtil {

    public static List<File> getLocalSkinList(File file) {
        List<File> linkedList = new LinkedList<>();
        File[] listFiles = file.listFiles();
        if (listFiles != null) {
            int i = 0;
            while (i < listFiles.length) {
                if (listFiles[i].isFile() && listFiles[i].getName().endsWith(".ps")) {
                    linkedList.add(listFiles[i]);
                }
                i++;
            }
        }
        return linkedList;
    }

    public static List<File> getLocalSoundList(File file) {
        List<File> linkedList = new LinkedList<>();
        File[] listFiles = file.listFiles();
        if (listFiles != null) {
            int i = 0;
            while (i < listFiles.length) {
                if (listFiles[i].isFile()
                        && (listFiles[i].getName().endsWith(".ss")
                        || listFiles[i].getName().endsWith(".sf2"))) {
                    linkedList.add(listFiles[i]);
                }
                i++;
            }
        }
        return linkedList;
    }
}
