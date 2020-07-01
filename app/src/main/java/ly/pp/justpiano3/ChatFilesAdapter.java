package ly.pp.justpiano3;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

public final class ChatFilesAdapter extends BaseAdapter {
    private List<HashMap> list;
    private ChatFiles chatfiles;

    ChatFilesAdapter(List<HashMap> list, ChatFiles cs) {
        this.list = list;
        chatfiles = cs;
    }

    final void mo3422a(List<HashMap> list) {
        this.list = list;
    }

    @Override
    public final int getCount() {
        return list.size();
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
        if (view == null) {
            view = LayoutInflater.from(chatfiles).inflate(R.layout.fileimageandtext, null);
        }
        view.setKeepScreenOn(true);
        String str = (String) list.get(i).get("filenames");
        String str2 = (String) list.get(i).get("time");
        String str3 = (String) list.get(i).get("path");
        ImageView imageView = view.findViewById(R.id.deleteview);
        view.findViewById(R.id.playview).setVisibility(View.INVISIBLE);
        ((TextView) view.findViewById(R.id.txtview)).setText(str);
        ((TextView) view.findViewById(R.id.timeview)).setText(str2);
        view.findViewById(R.id.showtxt).setOnClickListener(v -> chatfiles.play(str3));
        imageView.setOnClickListener(v -> chatfiles.delete(i, str, str3));
        return view;
    }
}
