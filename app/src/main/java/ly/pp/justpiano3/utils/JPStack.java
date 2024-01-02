package ly.pp.justpiano3.utils;

import android.app.Activity;

import java.util.Stack;

public final class JPStack {

    private static Stack<Activity> jpStack;

    public static void pop(Activity activity) {
        if (activity != null) {
            jpStack.remove(activity);
        }
    }

    public static Activity top() {
        if (jpStack == null) {
            return null;
        }
        return !jpStack.empty() ? jpStack.lastElement() : null;
    }

    public static void push(Activity activity) {
        if (activity != null) {
            if (jpStack == null) {
                jpStack = new Stack<>();
            }
            jpStack.add(activity);
        }
    }

    public static void clear() {
        if (jpStack != null) {
            jpStack.clear();
        }
    }
}
