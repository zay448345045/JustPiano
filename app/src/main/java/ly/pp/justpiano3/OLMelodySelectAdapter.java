package ly.pp.justpiano3;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public final class OLMelodySelectAdapter extends BaseAdapter {
    Handler handler;
    private List<String> list;
    private Activity activity;

    OLMelodySelectAdapter(Activity activity, Handler handler, List<String> arrayList) {
        this.activity = activity;
        this.handler = handler;
        list = arrayList;
    }

    @Override
    public final int getCount() {
        return list.size();
    }

    @Override
    public final Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public final long getItemId(int i) {
        return i;
    }

    @Override
    public final View getView(int i, View view, ViewGroup viewGroup) {
        TextView f6012a;
        if (view == null) {
            view = LayoutInflater.from(activity).inflate(R.layout.page_list, null);
            f6012a = view.findViewById(R.id.page_item);
            view.setTag(f6012a);
        } else {
            f6012a = (TextView) view.getTag();
        }
        f6012a.setText(list.get(i));
        f6012a.setOnClickListener(v -> {
            Message message = new Message();
            Bundle bundle = new Bundle();
            bundle.putInt("selIndex", i);
            message.setData(bundle);
            message.what = 1;
            handler.sendMessage(message);
        });
        return view;
    }
}
