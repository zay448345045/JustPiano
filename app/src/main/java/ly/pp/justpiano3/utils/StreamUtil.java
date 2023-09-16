package ly.pp.justpiano3.utils;

import java.io.*;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("unchecked")
public class StreamUtil {

    /**
     * 序列化,List
     */
    public static <T> boolean writeObject(List<T> list, File file) {
        T[] array = (T[]) list.toArray();
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
            out.writeObject(array);
            out.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 反序列化,List
     */
    public static <E> List<E> readObjectForList(File file) {
        E[] object;
        try (ObjectInputStream out = new ObjectInputStream(new FileInputStream(file))) {
            object = (E[]) out.readObject();
            return Arrays.asList(object);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}