package ly.pp.justpiano3.activity;

import android.app.Activity;
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

public class RecordFiles extends Activity {
    private List<Map<String, Object>> dataList;
    private ListView f4919d;
    private TextView f4921f;
    private RecordFilesAdapter recordFilesAdapter;

    private void m3824a(File file) {
        File[] f4924i = file.listFiles();
        f4921f.setText("录音文件目录为:SD卡\\JustPiano\\Records");
        f4921f.setTextSize(20);
        dataList = new ArrayList<>();
        int i = 0;
        int j;
        try {
            j = f4924i.length;
        } catch (NullPointerException e) {
            e.printStackTrace();
            j = 0;
        }
        while (i < j) {
            Map<String, Object> hashMap = new HashMap<>();
            if (f4924i[i].isFile() && f4924i[i].getName().endsWith(".wav")) {
                hashMap.put("image", R.drawable._none);
                hashMap.put("path", f4924i[i].getPath());
                hashMap.put("filenames", f4924i[i].getName());
                hashMap.put("time", DateUtil.format(new Date(f4924i[i].lastModified())));
                hashMap.put("timelong", f4924i[i].lastModified());
                dataList.add(hashMap);
            }
            i++;
        }
        Collections.sort(dataList, (o1, o2) -> Long.compare((long) o2.get("timelong"), (long) o1.get("timelong")));
        recordFilesAdapter = new RecordFilesAdapter(dataList, this);
        f4919d.setAdapter(recordFilesAdapter);
    }

    public final void remove(int i, String str) {
        File file = new File(str);
        if (file.exists()) {
            file.delete();
        }
        dataList.remove(i);
        recordFilesAdapter.mo3422a(dataList);
        recordFilesAdapter.notifyDataSetChanged();
    }

    public final void delete(int i, String str, String str2) {
        Builder builder = new Builder(this);
        builder.setMessage("确认删除[" + str + "]吗?");
        builder.setTitle("提示");
        builder.setPositiveButton("确认", (dialog, which) -> {
            dialog.dismiss();
            remove(i, str2);
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
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.record_list);
        ImageLoadUtil.setBackground(this, "ground", findViewById(R.id.layout));
        f4919d = findViewById(R.id.listFile);
        f4919d.setCacheColorHint(Color.TRANSPARENT);
        f4921f = findViewById(R.id.txt1);
        m3824a(new File(Environment.getExternalStorageDirectory() + "/JustPiano/Records/"));
    }
}
