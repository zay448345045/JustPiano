package ly.pp.justpiano3.utils;

import android.app.Activity;

import java.util.Stack;

public final class JPStack {

    private static Stack<Activity> stack;

    public static void pop(Activity activity) {
        if (activity != null) {
            stack.remove(activity);
        }
    }

    public static Activity top() {
        return !stack.empty() ? stack.lastElement() : null;
    }

    public static void push(Activity activity) {
        if (activity != null) {
            if (stack == null) {
                stack = new Stack<>();
            }
            stack.add(activity);
        }
    }

    public static void clear() {
        while (stack != null && !stack.empty()) {
            JPStack.pop(JPStack.top());
        }
    }
}
