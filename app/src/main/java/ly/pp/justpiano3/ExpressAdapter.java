package ly.pp.justpiano3;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.PopupWindow;

public final class ExpressAdapter extends BaseAdapter {
    PopupWindow popupWindow;
    ConnectionService cs;
    byte f6036e;
    byte f6037f;
    byte f6038g;
    private Context context;
    private Integer[] f6033b;

    ExpressAdapter(Context context, ConnectionService connectionService, Integer[] numArr, PopupWindow popupWindow, byte b, byte b2, byte b3) {
        this.context = context;
        f6033b = numArr;
        this.popupWindow = popupWindow;
        cs = connectionService;
        f6036e = b2;
        f6037f = b3;
        f6038g = b;
    }

    @Override
    public final int getCount() {
        return f6033b.length;
    }

    @Override
    public final Object getItem(int i) {
        return i;
    }

    @Override
    public final long getItemId(int i) {
        return i;
    }

    @Override
    public final View getView(int i, View view, ViewGroup viewGroup) {
        ImageView imageView = new ImageView(context);
        imageView.setPadding(0, 0, 0, 0);
        imageView.setImageResource(f6033b[i]);
        imageView.setOnClickListener(new ExpressClick(this, i));
        return imageView;
    }
}
