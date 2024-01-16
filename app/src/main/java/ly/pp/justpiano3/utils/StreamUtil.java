package ly.pp.justpiano3.utils;

import android.content.Context;
import android.net.Uri;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("unchecked")
public final class StreamUtil {

    /**
     * 序列化List
     */
    public static <T> boolean writeObject(List<T> list, Context context, Uri uri) {
        try (ObjectOutputStream out = new ObjectOutputStream(context.getContentResolver().openOutputStream(uri, "w"))) {
            out.writeObject((T[]) list.toArray());
            out.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 反序列化List
     */
    public static <T> List<T> readObjectForList(Context context, Uri uri) {
        try (ObjectInputStream out = new ObjectInputStream(context.getContentResolver().openInputStream(uri))) {
            return Arrays.asList((T[]) out.readObject());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}