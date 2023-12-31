package ly.pp.justpiano3.activity.online;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ly.pp.justpiano3.R;
import ly.pp.justpiano3.activity.BaseActivity;
import ly.pp.justpiano3.task.ShowTopInfoTask;
import ly.pp.justpiano3.thread.PictureHandle;
import ly.pp.justpiano3.view.JPProgressBar;

public final class ShowTopInfo extends BaseActivity implements Handler.Callback, OnClickListener {
    public List<Map<String, Object>> dataList;
    public LayoutInflater layoutInflater;
    public int size;
    public ListView listView;
    public JPProgressBar jpprogressBar;
    public int head;
    public int pageNum;
    public int position;
    public PictureHandle pictureHandle;
    public Handler handler;
    private TextView pageNumTextView;
    private final int pageSize = 20;
    private Bitmap nailFace;

    public Bitmap setDefaultAvatar(Context context) {
        try {
            if (nailFace == null) {
                nailFace = BitmapFactory.decodeStream(context.getResources().getAssets().open("drawable/nailface.webp"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return nailFace;
    }

    public List<Map<String, Object>> userListHandle(String str) {
        JSONArray jSONArray;
        List<Map<String, Object>> arrayList = new ArrayList<>();
        try {
            jSONArray = new JSONArray(str);
        } catch (JSONException e) {
            e.printStackTrace();
            jSONArray = null;
        }
        size = jSONArray.length();
        for (int i = 0; i < size; i++) {
            try {
                Map<String, Object> hashMap = new HashMap<>();
                hashMap.put("userID", Integer.valueOf(jSONArray.getJSONObject(i).get("I").toString()));
                hashMap.put("userName", jSONArray.getJSONObject(i).get("K").toString());
                hashMap.put("faceID", jSONArray.getJSONObject(i).get("F").toString());
                hashMap.put("userScore", Integer.valueOf(jSONArray.getJSONObject(i).get("S").toString()));
                hashMap.put("userNuns", Integer.valueOf(jSONArray.getJSONObject(i).get("T").toString()));
                hashMap.put("userSex", jSONArray.getJSONObject(i).get("X").toString());
                arrayList.add(hashMap);
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return arrayList;
    }

    @Override
    public void onBackPressed() {
        jpprogressBar.dismiss();
        startActivity(new Intent(this, OLTopUser.class));
        finish();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.ol_top_next) {
            pageNum++;
            position = (pageNum * pageSize) + 1;
            pageNumTextView.setText("-" + (pageNum + 1) + "-");
            new ShowTopInfoTask(this).execute();
        } else if (id == R.id.ol_top_before) {
            pageNum--;
            if (pageNum < 0) {
                pageNum = 0;
                return;
            }
            position = (pageNum * pageSize) + 1;
            pageNumTextView.setText("-" + (pageNum + 1) + "-");
            new ShowTopInfoTask(this).execute();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ol_top_info);
        layoutInflater = LayoutInflater.from(this);
        jpprogressBar = new JPProgressBar(this);
        listView = findViewById(R.id.ol_top_list);
        position = (pageNum * pageSize) + 1;
        head = getIntent().getExtras().getInt("head");
        findViewById(R.id.ol_top_before).setOnClickListener(this);
        findViewById(R.id.ol_top_next).setOnClickListener(this);
        pageNumTextView = findViewById(R.id.ol_top_page);
        pageNumTextView.setText("-" + (pageNum + 1) + "-");
        TextView titleTextView = findViewById(R.id.ol_top_title);
        switch (head) {
            case 0 -> titleTextView.setText("-冠军数量榜-");
            case 1 -> titleTextView.setText("-个人总分榜-");
            case 4 -> titleTextView.setText("-个人等级榜-");
            case 7 -> titleTextView.setText("-搭档祝福榜-");
            case 9 -> titleTextView.setText("-家族贡献榜-");
            case 10 -> titleTextView.setText("-个人考级榜-");
        }
        new ShowTopInfoTask(this).execute();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
    }

    @Override
    protected void onDestroy() {
        if (pictureHandle != null) {
            pictureHandle.clear();
        }
        super.onDestroy();
    }

    @Override
    public boolean handleMessage(@NonNull Message message) {
        return false;
    }
}
