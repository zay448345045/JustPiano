package ly.pp.justpiano3.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.documentfile.provider.DocumentFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kotlin.Pair;
import ly.pp.justpiano3.R;
import ly.pp.justpiano3.adapter.RecordFilesAdapter;
import ly.pp.justpiano3.entity.GlobalSetting;
import ly.pp.justpiano3.utils.DateUtil;
import ly.pp.justpiano3.utils.FileUtil;
import ly.pp.justpiano3.view.JPDialogBuilder;

public final class RecordFiles extends BaseActivity {
    private List<Map<String, Object>> dataList;
    private ListView listView;
    private TextView tipsTextView;
    private RecordFilesAdapter recordFilesAdapter;

    private void loadRecordFiles(String recordsSaveUri) {
        Pair<DocumentFile, String> documentFile = FileUtil.INSTANCE.getDirectoryDocumentFile(this,
                recordsSaveUri, "Records", "（默认）SD卡/Android/data/ly.pp.justpiano3/files/Records");
        DocumentFile[] recordFiles = documentFile.component1().listFiles();
        tipsTextView.setText("录音文件存储文件夹：" + documentFile.component2() + "\n【警告】若您未设置其他存储位置，则所有使用默认位置存储的文件将在APP卸载时删除");
        dataList = new ArrayList<>();
        for (DocumentFile recordFile : recordFiles) {
            Map<String, Object> hashMap = new HashMap<>();
            if (recordFile.isFile() && recordFile.getName().endsWith(".wav")) {
                hashMap.put("path", recordFile);
                hashMap.put("filenames", recordFile.getName());
                hashMap.put("time", DateUtil.format(new Date(recordFile.lastModified())));
                hashMap.put("timelong", recordFile.lastModified());
                dataList.add(hashMap);
            }
        }
        Collections.sort(dataList, (o1, o2) -> Long.compare((long) o2.get("timelong"), (long) o1.get("timelong")));
        recordFilesAdapter = new RecordFilesAdapter(dataList, this);
        listView.setAdapter(recordFilesAdapter);
    }

    public void remove(int index, DocumentFile documentFile) {
        if (documentFile.exists()) {
            documentFile.delete();
        }
        dataList.remove(index);
        recordFilesAdapter.setDataList(dataList);
        recordFilesAdapter.notifyDataSetChanged();
    }

    public void delete(int index, String fileName, DocumentFile documentFile) {
        JPDialogBuilder jpDialogBuilder = new JPDialogBuilder(this);
        jpDialogBuilder.setMessage("确认删除[" + fileName + "]吗?");
        jpDialogBuilder.setTitle("提示");
        jpDialogBuilder.setFirstButton("确认", (dialog, which) -> {
            dialog.dismiss();
            remove(index, documentFile);
        });
        jpDialogBuilder.setSecondButton("取消", (dialog, which) -> dialog.dismiss());
        jpDialogBuilder.buildAndShowDialog();
    }

    public void play(DocumentFile documentFile) {
        if (documentFile == null) {
            return;
        }
        try {
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setAction(Intent.ACTION_VIEW);
            intent.setDataAndType(documentFile.getUri(), "audio/*");
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "设备无支持的应用，无法打开文件", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.record_list);
        listView = findViewById(R.id.listFile);
        listView.setCacheColorHint(Color.TRANSPARENT);
        tipsTextView = findViewById(R.id.txt1);
        loadRecordFiles(GlobalSetting.INSTANCE.getRecordsSavePath());
    }
}
