package ly.pp.justpiano3;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.PopupWindow;

public final class ExpressAdapter extends BaseAdapter {
    PopupWindow popupWindow;
    ConnectionService connectionService;
    int messageType;
    private final Context context;
    private final Integer[] f6033b;

    ExpressAdapter(Context context, ConnectionService connectionService, Integer[] numArr, PopupWindow popupWindow, int b) {
        this.context = context;
        f6033b = numArr;
        this.popupWindow = popupWindow;
        this.connectionService = connectionService;
        messageType = b;
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
