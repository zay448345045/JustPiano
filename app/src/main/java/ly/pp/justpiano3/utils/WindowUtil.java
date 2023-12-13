package ly.pp.justpiano3.utils;

import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;

/**
 * window相关工具
 */
public class WindowUtil {

    /**
     * 设置适配全面屏，隐藏状态栏、导航栏等
     */
    public static void fullScreenHandle(Window window) {
        if (window == null) {
            return;
        }
        // 设置全面屏显示
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.TRANSPARENT);
        window.setNavigationBarColor(Color.TRANSPARENT);
        // 适配刘海屏等异形屏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            layoutParams.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            window.setAttributes(layoutParams);
        }
        // 内容扩展到状态栏和导航栏下面
        View decorView = window.getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        // API 31后，隐藏导航栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            WindowInsetsController controller = decorView.getWindowInsetsController();
            if (controller != null) {
                // 隐藏状态栏和导航栏
                controller.hide(WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
                // 设置为沉浸式模式
                controller.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
            }
        }
    }

    /**
     * 恢复默认的屏幕显示，显示状态栏、导航栏等
     */
    public static void exitFullScreenHandle(Window window) {
        if (window == null) {
            return;
        }
        // 移除全面屏显示设置
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        // 如果之前适配了刘海屏等异形屏，恢复默认设置
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            layoutParams.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_DEFAULT;
            window.setAttributes(layoutParams);
        }
        // 内容不再扩展到状态栏和导航栏下面
        View decorView = window.getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_FULLSCREEN);
        // API 31后，恢复导航栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            WindowInsetsController controller = decorView.getWindowInsetsController();
            if (controller != null) {
                // 显示状态栏和导航栏
                controller.show(WindowInsets.Type.navigationBars());
                // 恢复默认系统栏行为
                controller.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_DEFAULT);
            }
        }
    }
}
