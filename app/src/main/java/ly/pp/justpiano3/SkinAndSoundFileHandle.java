package ly.pp.justpiano3;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

final class SkinAndSoundFileHandle {

    static List<File> getLocalSkinList(String str) {
        List<File> linkedList = new LinkedList<>();
        File[] listFiles = new File(str).listFiles();
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

    static List<File> getLocalSoundList(String str) {
        List<File> linkedList = new LinkedList<>();
        File[] listFiles = new File(str).listFiles();
        if (listFiles != null) {
            int i = 0;
            while (i < listFiles.length) {
                if (listFiles[i].isFile() && listFiles[i].getName().endsWith(".ss")) {
                    linkedList.add(listFiles[i]);
                }
                i++;
            }
        }
        return linkedList;
    }
}
