package ly.pp.justpiano3;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ShowTopInfo extends Activity implements Callback, OnClickListener {
    public JPApplication jpapplication;
    List<HashMap> f4985a = null;
    LayoutInflater layoutInflater;
    int f4987c = 0;
    String f4988d = "";
    ListView f4989e;
    JPProgressBar jpprogressBar;
    int head = 0;
    int f4996l = 0;
    int f4997m = 0;
    String f4999o = "";
    PictureHandle f5001q;
    Handler handler;
    private TextView f4993i;
    private final int f4998n = 20;
    private Bitmap f5003s = null;

    public Bitmap m3874a(Context context) {
        try {
            if (f5003s == null) {
                f5003s = BitmapFactory.decodeStream(context.getResources().getAssets().open("drawable/nailface.jpg"));
            }
        } catch (IOException ignored) {
        }
        return f5003s;
    }

    public List<HashMap> m3877a(String str) {
        JSONArray jSONArray;
        List<HashMap> arrayList = new ArrayList<>();
        try {
            jSONArray = new JSONArray(str);
        } catch (JSONException e) {
            e.printStackTrace();
            jSONArray = null;
        }
        f4987c = jSONArray.length();
        for (int i = 0; i < f4987c; i++) {
            try {
                HashMap hashMap = new HashMap();
                hashMap.put("userID", Integer.valueOf(jSONArray.getJSONObject(i).get("I").toString()));
                hashMap.put("userName", jSONArray.getJSONObject(i).get("K").toString());
                hashMap.put("faceID", jSONArray.getJSONObject(i).get("F").toString());
                hashMap.put("userScore", Integer.valueOf(jSONArray.getJSONObject(i).get("S").toString()));
                hashMap.put("userNuns", Integer.valueOf(jSONArray.getJSONObject(i).get("T").toString()));
                hashMap.put("userSex", jSONArray.getJSONObject(i).get("X").toString());
                arrayList.add(hashMap);
            } catch (JSONException e2) {
                e2.printStackTrace();
            }
        }
        return arrayList;
    }

    @Override
    public boolean handleMessage(Message message) {
        return false;
    }

    @Override
    public void onBackPressed() {
        jpprogressBar.dismiss();
        Intent intent = new Intent();
        intent.setClass(this, OLTopUser.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ol_top_next:
                f4996l++;
                f4997m = (f4996l * f4998n) + 1;
                f4993i.setText("-" + (f4996l + 1) + "-");
                new ShowTopInfoTask(this).execute();
                return;
            case R.id.ol_top_before:
                f4996l--;
                if (f4996l < 0) {
                    f4996l = 0;
                    return;
                }
                f4997m = (f4996l * f4998n) + 1;
                f4993i.setText("-" + (f4996l + 1) + "-");
                new ShowTopInfoTask(this).execute();
                return;
            default:
        }
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        jpapplication = (JPApplication) getApplication();
        setContentView(R.layout.showtopinfo);
        jpapplication.setBackGround(this, "ground", findViewById(R.id.layout));
        layoutInflater = LayoutInflater.from(this);
        jpprogressBar = new JPProgressBar(this);
        f4989e = findViewById(R.id.ol_top_list);
        TextView f4992h = findViewById(R.id.ol_top_title);
        Button f4994j = findViewById(R.id.ol_top_before);
        Button f4995k = findViewById(R.id.ol_top_next);
        f4997m = (f4996l * f4998n) + 1;
        Bundle extras = getIntent().getExtras();
        head = extras.getInt("head");
        f4988d = extras.getString("keywords");
        if (f4988d.equals("C")) {
            f4999o = "";
        } else {
            f4999o = extras.getString("address");
        }
        f4994j.setOnClickListener(this);
        f4995k.setOnClickListener(this);
        f4993i = findViewById(R.id.ol_top_page);
        f4993i.setText("-" + (f4996l + 1) + "-");
        switch (head) {
            case 0:
                f4992h.setText("-" + f4999o + "冠军数量榜-");
                break;
            case 1:
                f4992h.setText("-" + f4999o + "个人总分榜-");
                break;
            case 2:
                f4992h.setText("-" + f4999o + "达人榜-");
                break;
            case 3:
                f4992h.setText("-" + f4999o + "新秀榜-");
                break;
            case 4:
                f4992h.setText("-" + f4999o + "个人等级榜-");
                break;
            case 5:
                f4992h.setText("-导出-");
                f4994j.setVisibility(View.GONE);
                f4995k.setVisibility(View.GONE);
                f4993i.setVisibility(View.GONE);
                break;
            case 7:
                f4992h.setText("-" + f4999o + "搭档祝福榜-");
                break;
            case 8:
                f4992h.setText("-" + f4999o + "总分周榜-");
                break;
            case 9:
                f4992h.setText("-" + f4999o + "家族贡献榜-");
                break;
            case 10:
                f4992h.setText("-" + f4999o + "个人考级榜-");
                break;
        }
        new ShowTopInfoTask(this).execute();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
    }

    @Override
    protected void onDestroy() {
        if (f5001q != null) {
            f5001q.mo3026a();
        }
        super.onDestroy();
    }
}
