package ly.pp.justpiano3.activity;

import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ly.pp.justpiano3.R;
import ly.pp.justpiano3.adapter.RecordFilesAdapter;
import ly.pp.justpiano3.utils.DateUtil;
import ly.pp.justpiano3.utils.ImageLoadUtil;

public class RecordFiles extends BaseActivity {
    private List<Map<String, Object>> dataList;
    private ListView listView;
    private TextView tipsTextView;
    private RecordFilesAdapter recordFilesAdapter;

    private void loadRecordFiles(File file) {
        File[] recordFiles = file.listFiles();
        tipsTextView.setText("录音文件目录为:SD卡\\JustPiano\\Records");
        tipsTextView.setTextSize(20);
        dataList = new ArrayList<>();
        int i = 0;
        int j = recordFiles == null ? 0 : recordFiles.length;
        while (i < j) {
            Map<String, Object> hashMap = new HashMap<>();
            if (recordFiles[i].isFile() && recordFiles[i].getName().endsWith(".wav")) {
                hashMap.put("image", R.drawable._none);
                hashMap.put("path", recordFiles[i].getPath());
                hashMap.put("filenames", recordFiles[i].getName());
                hashMap.put("time", DateUtil.format(new Date(recordFiles[i].lastModified())));
                hashMap.put("timelong", recordFiles[i].lastModified());
                dataList.add(hashMap);
            }
            i++;
        }
        Collections.sort(dataList, (o1, o2) -> Long.compare((long) o2.get("timelong"), (long) o1.get("timelong")));
        recordFilesAdapter = new RecordFilesAdapter(dataList, this);
        listView.setAdapter(recordFilesAdapter);
    }

    public final void remove(int index, String fileName) {
        File file = new File(fileName);
        if (file.exists()) {
            file.delete();
        }
        dataList.remove(index);
        recordFilesAdapter.setDataList(dataList);
        recordFilesAdapter.notifyDataSetChanged();
    }

    public final void delete(int index, String fileName, String filePath) {
        Builder builder = new Builder(this);
        builder.setMessage("确认删除[" + fileName + "]吗?");
        builder.setTitle("提示");
        builder.setPositiveButton("确认", (dialog, which) -> {
            dialog.dismiss();
            remove(index, filePath);
        });
        builder.setNegativeButton("取消", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    public final void play(String str) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(str)), "audio");
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.record_list);
        ImageLoadUtil.setBackground(this);
        listView = findViewById(R.id.listFile);
        listView.setCacheColorHint(Color.TRANSPARENT);
        tipsTextView = findViewById(R.id.txt1);
        loadRecordFiles(new File(Environment.getExternalStorageDirectory() + "/JustPiano/Records/"));
    }
}
