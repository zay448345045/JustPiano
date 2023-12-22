package ly.pp.justpiano3.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import androidx.documentfile.provider.DocumentFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kotlin.Pair;
import ly.pp.justpiano3.R;
import ly.pp.justpiano3.adapter.ChatFilesAdapter;
import ly.pp.justpiano3.entity.GlobalSetting;
import ly.pp.justpiano3.utils.DateUtil;
import ly.pp.justpiano3.utils.FileUtil;
import ly.pp.justpiano3.view.JPDialogBuilder;

public class ChatFiles extends BaseActivity {
    private List<Map<String, Object>> dataList;
    private ListView listView;
    private TextView tipsTextView;
    private ChatFilesAdapter chatFilesAdapter;

    private void loadChatFiles(String chatsSaveUri) {
        Pair<DocumentFile, String> documentFile = FileUtil.INSTANCE.getDirectoryDocumentFile(this,
                chatsSaveUri, "Chats", "SD卡/Android/data/ly.pp.justpiano3/files/Chats");
        DocumentFile[] chatFiles = documentFile.component1().listFiles();
        tipsTextView.setText("聊天记录存储文件夹：" + documentFile.component2());
        dataList = new ArrayList<>();
        for (DocumentFile chatFile : chatFiles) {
            Map<String, Object> hashMap = new HashMap<>();
            if (chatFile.isFile() && chatFile.getName().endsWith(".txt")) {
                hashMap.put("path", chatFile);
                hashMap.put("filenames", chatFile.getName());
                hashMap.put("time", DateUtil.format(new Date(chatFile.lastModified())));
                hashMap.put("timelong", chatFile.lastModified());
                dataList.add(hashMap);
            }
        }
        Collections.sort(dataList, (o1, o2) -> Long.compare((long) o2.get("timelong"), (long) o1.get("timelong")));
        chatFilesAdapter = new ChatFilesAdapter(dataList, this);
        listView.setAdapter(chatFilesAdapter);
    }

    public final void remove(int index, DocumentFile documentFile) {
        if (documentFile.exists()) {
            documentFile.delete();
        }
        dataList.remove(index);
        chatFilesAdapter.setDataList(dataList);
        chatFilesAdapter.notifyDataSetChanged();
    }

    public final void delete(int index, String fileName, DocumentFile documentFile) {
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

    public final void open(DocumentFile documentFile) {
        if (documentFile == null) {
            return;
        }
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(documentFile.getUri(), "text/plain");
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.record_list);
        listView = findViewById(R.id.listFile);
        listView.setCacheColorHint(Color.TRANSPARENT);
        tipsTextView = findViewById(R.id.txt1);
        loadChatFiles(GlobalSetting.INSTANCE.getChatsSavePath());
    }
}
