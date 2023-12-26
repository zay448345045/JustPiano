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
import ly.pp.justpiano3.activity.local.RecordFiles;

public final class RecordFilesAdapter extends BaseAdapter {
    private List<Map<String, Object>> list;
    private final RecordFiles recordfiles;

    public RecordFilesAdapter(List<Map<String, Object>> list, RecordFiles recordFiles) {
        this.list = list;
        recordfiles = recordFiles;
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
    public View getView(int index, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(recordfiles).inflate(R.layout.chat_record_file_list, null);
        }
        String fileName = (String) list.get(index).get("filenames");
        String fileTime = (String) list.get(index).get("time");
        DocumentFile filePath = (DocumentFile) list.get(index).get("path");
        ImageView imageView = view.findViewById(R.id.deleteview);
        view.findViewById(R.id.showtxt).setVisibility(View.GONE);
        ((TextView) view.findViewById(R.id.txtview)).setText(fileName);
        ((TextView) view.findViewById(R.id.timeview)).setText(fileTime);
        view.findViewById(R.id.playview).setOnClickListener(v -> recordfiles.play(filePath));
        imageView.setOnClickListener(v -> recordfiles.delete(index, fileName, filePath));
        return view;
    }
}
