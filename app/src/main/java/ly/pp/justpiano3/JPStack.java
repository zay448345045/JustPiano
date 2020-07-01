package ly.pp.justpiano3;

import android.app.Activity;

import java.util.Stack;

public final class JPStack {
    private static Stack<Activity> stack;
    private static JPStack jpStack;

    public static JPStack create() {
        if (jpStack == null) {
            jpStack = new JPStack();
        }
        return jpStack;
    }

    static void pop(Activity activity) {
        if (activity != null) {
            stack.remove(activity);
        }
    }

    static Activity top() {
        return !stack.empty() ? stack.lastElement() : null;
    }

    static void push(Activity activity) {
        if (activity != null) {
            if (stack == null) {
                stack = new Stack<>();
            }
            stack.add(activity);
        }
    }

    static void clear() {
        while (stack != null && !stack.empty()) {
            JPStack.pop(JPStack.top());
        }
    }
}
