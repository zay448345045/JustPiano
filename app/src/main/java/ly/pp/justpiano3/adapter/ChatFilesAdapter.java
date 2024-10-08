package ly.pp.justpiano3.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.documentfile.provider.DocumentFile;

import java.util.List;
import java.util.Map;

import ly.pp.justpiano3.R;
import ly.pp.justpiano3.activity.local.ChatFiles;

public final class ChatFilesAdapter extends BaseAdapter {
    private List<Map<String, Object>> list;
    private final ChatFiles chatFiles;

    public ChatFilesAdapter(List<Map<String, Object>> list, ChatFiles chatFiles) {
        this.list = list;
        this.chatFiles = chatFiles;
    }

    public void setDataList(List<Map<String, Object>> list) {
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(chatFiles).inflate(R.layout.chat_record_file_list, null);
        }
        String fileName = (String) list.get(i).get("filenames");
        String fileTime = (String) list.get(i).get("time");
        DocumentFile documentFile = (DocumentFile) list.get(i).get("path");
        ImageView imageView = view.findViewById(R.id.deleteview);
        view.findViewById(R.id.playview).setVisibility(View.INVISIBLE);
        ((TextView) view.findViewById(R.id.txtview)).setText(fileName);
        ((TextView) view.findViewById(R.id.timeview)).setText(fileTime);
        view.findViewById(R.id.showtxt).setOnClickListener(v -> chatFiles.open(documentFile));
        imageView.setOnClickListener(v -> chatFiles.delete(i, fileName, documentFile));
        return view;
    }
}
