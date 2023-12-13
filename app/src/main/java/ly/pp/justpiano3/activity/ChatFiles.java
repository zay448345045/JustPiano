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
import ly.pp.justpiano3.adapter.ChatFilesAdapter;
import ly.pp.justpiano3.utils.DateUtil;

public class ChatFiles extends BaseActivity {
    public List<Map<String, Object>> dataList = null;
    private ListView listView;
    private TextView tipsTextView;
    private ChatFilesAdapter chatFilesAdapter;

    private void loadChatFiles(File file) {
        File[] chatFiles = file.listFiles();
        tipsTextView.setText("聊天记录存储目录为:SD卡\\JustPiano\\Chats");
        tipsTextView.setTextSize(20);
        dataList = new ArrayList<>();
        int i = 0;
        int j = chatFiles == null ? 0 : chatFiles.length;
        while (i < j) {
            Map<String, Object> hashMap = new HashMap<>();
            if (chatFiles[i].isFile() && chatFiles[i].getName().endsWith(".txt")) {
                hashMap.put("image", R.drawable._none);
                hashMap.put("path", chatFiles[i].getPath());
                hashMap.put("filenames", chatFiles[i].getName());
                hashMap.put("time", DateUtil.format(new Date(chatFiles[i].lastModified())));
                hashMap.put("timelong", chatFiles[i].lastModified());
                dataList.add(hashMap);
            }
            i++;
        }
        Collections.sort(dataList, (o1, o2) -> Long.compare((long) o2.get("timelong"), (long) o1.get("timelong")));
        chatFilesAdapter = new ChatFilesAdapter(dataList, this);
        listView.setAdapter(chatFilesAdapter);
    }

    public final void remove(int index, String fileName) {
        File file = new File(fileName);
        if (file.exists()) {
            file.delete();
        }
        dataList.remove(index);
        chatFilesAdapter.setDataList(dataList);
        chatFilesAdapter.notifyDataSetChanged();
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

    public final void open(String fileName) {
        File file = new File(fileName);
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.fromFile(file), "text/plain");
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.record_list);
        listView = findViewById(R.id.listFile);
        listView.setCacheColorHint(Color.TRANSPARENT);
        tipsTextView = findViewById(R.id.txt1);
        loadChatFiles(new File(Environment.getExternalStorageDirectory() + "/JustPiano/Chats/"));
    }
}
