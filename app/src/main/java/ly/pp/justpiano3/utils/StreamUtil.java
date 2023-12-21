package ly.pp.justpiano3.utils;

import android.content.Context;
import android.net.Uri;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("unchecked")
public class StreamUtil {

    /**
     * 序列化,List
     */
    public static <T> boolean writeObject(List<T> list, Context context, Uri uri) {
        T[] array = (T[]) list.toArray();
        try (ObjectOutputStream out = new ObjectOutputStream(context.getContentResolver().openOutputStream(uri))) {
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
    public static <E> List<E> readObjectForList(Context context, Uri uri) {
        E[] object;
        try (ObjectInputStream out = new ObjectInputStream(context.getContentResolver().openInputStream(uri))) {
            object = (E[]) out.readObject();
            return Arrays.asList(object);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}