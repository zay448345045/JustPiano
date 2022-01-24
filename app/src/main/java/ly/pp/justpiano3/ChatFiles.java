package ly.pp.justpiano3;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class ChatFiles extends Activity {
    List<HashMap> f4917b = null;
    private ListView f4919d;
    private TextView f4921f;
    private ChatFilesAdapter chatFilesAdapter;

    private void m3824a(File file) {
        File[] f4924i = file.listFiles();
        f4921f.setText("聊天记录存储目录为:SD卡\\JustPiano\\Chats");
        f4921f.setTextSize(20);
        f4917b = new ArrayList<>();
        int i = 0;
        int j;
        try {
            j = f4924i.length;
        } catch (NullPointerException e) {
            e.printStackTrace();
            j = 0;
        }
        while (i < j) {
            HashMap hashMap = new HashMap();
            if (f4924i[i].isFile() && f4924i[i].getName().endsWith(".txt")) {
                hashMap.put("image", R.drawable._none);
                hashMap.put("path", f4924i[i].getPath());
                hashMap.put("filenames", f4924i[i].getName());
                hashMap.put("time", new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.CHINESE).format(new Date(f4924i[i].lastModified())));
                hashMap.put("timelong", f4924i[i].lastModified());
                f4917b.add(hashMap);
            }
            i++;
        }
        Collections.sort(f4917b, (o1, o2) -> (int) ((long) (o2.get("timelong")) - (long) (o1.get("timelong"))));
        chatFilesAdapter = new ChatFilesAdapter(f4917b, this);
        f4919d.setAdapter(chatFilesAdapter);
        if (f4917b.size() == 0) {
            Toast.makeText(this, "没有聊天记录!", Toast.LENGTH_SHORT).show();
        }
    }

    public final void remove(int i, String str) {
        File file = new File(str);
        if (file.exists()) {
            file.delete();
        }
        f4917b.remove(i);
        chatFilesAdapter.mo3422a(f4917b);
        chatFilesAdapter.notifyDataSetChanged();
    }

    protected final void delete(int i, String str, String str2) {
        Builder builder = new Builder(this);
        builder.setMessage("确认删除[" + str + "]吗?");
        builder.setTitle("提示");
        builder.setPositiveButton("确认", (dialog, which) -> {
            dialog.dismiss();
            remove(i, str2);
        });
        builder.setNegativeButton("取消", new DialogDismissClick());
        builder.create().show();
    }

    public final void play(String str) {
        File file = new File(str);
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(file);
        intent.setDataAndType(uri, "text/plain");
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        JPApplication jpApplication = (JPApplication) getApplication();
        setContentView(R.layout.record_list);
        jpApplication.setBackGround(this, "ground", findViewById(R.id.layout));
        f4919d = findViewById(R.id.listFile);
        f4919d.setCacheColorHint(0);
        f4921f = findViewById(R.id.txt1);
        m3824a(new File(Environment.getExternalStorageDirectory() + "/JustPiano/Chats/"));
    }
}
