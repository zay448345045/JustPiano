package ly.pp.justpiano3.view;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;

import ly.pp.justpiano3.entity.GlobalSetting;
import ly.pp.justpiano3.utils.WindowUtil;

public class JPPopupWindow extends PopupWindow {

    private final Context context;

    public JPPopupWindow(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public JPPopupWindow(View contentView, int width, int height, boolean focusable) {
        super(contentView, width, height, focusable);
        this.context = contentView.getContext();
        init();
    }

    private void init() {
        setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        setFocusable(true);
        setTouchable(true);
        setOutsideTouchable(true);
    }

    @Override
    public void showAsDropDown(View anchor) {
        super.showAsDropDown(anchor);
        if (GlobalSetting.getAllFullScreenShow()) {
            WindowUtil.fullScreenHandle(((Activity) context).getWindow());
        } else {
            WindowUtil.exitFullScreenHandle(((Activity) context).getWindow());
        }
    }

    @Override
    public void showAsDropDown(View anchor, int xoff, int yoff) {
        super.showAsDropDown(anchor, xoff, yoff);
        if (GlobalSetting.getAllFullScreenShow()) {
            WindowUtil.fullScreenHandle(((Activity) context).getWindow());
        } else {
            WindowUtil.exitFullScreenHandle(((Activity) context).getWindow());
        }
    }

    @Override
    public void showAsDropDown(View anchor, int xoff, int yoff, int gravity) {
        super.showAsDropDown(anchor, xoff, yoff, gravity);
        if (GlobalSetting.getAllFullScreenShow()) {
            WindowUtil.fullScreenHandle(((Activity) context).getWindow());
        } else {
            WindowUtil.exitFullScreenHandle(((Activity) context).getWindow());
        }
    }

    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
        super.showAtLocation(parent, gravity, x, y);
        if (GlobalSetting.getAllFullScreenShow()) {
            WindowUtil.fullScreenHandle(((Activity) context).getWindow());
        } else {
            WindowUtil.exitFullScreenHandle(((Activity) context).getWindow());
        }
    }
}
