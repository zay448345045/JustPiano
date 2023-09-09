package ly.pp.justpiano3.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import ly.pp.justpiano3.JPApplication;
import ly.pp.justpiano3.R;
import ly.pp.justpiano3.adapter.SearchSongsAdapter;
import ly.pp.justpiano3.task.SearchSongsTask;
import ly.pp.justpiano3.thread.PictureHandle;
import ly.pp.justpiano3.utils.GZIPUtil;
import ly.pp.justpiano3.utils.SkinImageLoadUtil;
import ly.pp.justpiano3.view.JPProgressBar;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SearchSongs extends Activity implements Callback, OnClickListener {
    public JPApplication jpapplication;
    public LayoutInflater layoutinflater;
    public int length = 0;
    public String f4948c = "";
    public String f4949d = "";
    public String songID;
    public ListView songsListView;
    public JPProgressBar jpprogressBar;
    public double f4953h;
    public int f4954i;
    public int headType = 0;
    public PictureHandle pictureHandle;
    public Handler searchSongsHandler;
    private TextView keywords;
    private Bitmap nailface = null;

    public Bitmap m3831a(Context context) {
        try {
            if (nailface == null) {
                nailface = BitmapFactory.decodeStream(context.getResources().getAssets().open("drawable/nailface.jpg"));
            }
        } catch (IOException ignored) {
        }
        return nailface;
    }

    private List<HashMap> m3834a(String str) {
        JSONArray jSONArray;
        List<HashMap> arrayList = new ArrayList<>();
        try {
            jSONArray = new JSONArray(str);
        } catch (JSONException e) {
            e.printStackTrace();
            jSONArray = null;
        }
        length = jSONArray.length();
        for (int i = 0; i < length; i++) {
            try {
                HashMap hashMap = new HashMap();
                hashMap.put("songID", jSONArray.getJSONObject(i).get("SI").toString());
                hashMap.put("songName", jSONArray.getJSONObject(i).get("SN").toString());
                hashMap.put("degree", Double.valueOf(jSONArray.getJSONObject(i).get("DG").toString()));
                hashMap.put("items", jSONArray.getJSONObject(i).get("AR").toString());
                hashMap.put("topUser", jSONArray.getJSONObject(i).get("TU").toString());
                hashMap.put("topScore", jSONArray.getJSONObject(i).get("TS").toString());
                hashMap.put("playCount", Integer.valueOf(jSONArray.getJSONObject(i).get("PC").toString()));
                hashMap.put("length", jSONArray.getJSONObject(i).get("LE").toString());
                hashMap.put("update", jSONArray.getJSONObject(i).get("UP").toString());
                arrayList.add(hashMap);
            } catch (JSONException e2) {
                e2.printStackTrace();
            }
        }
        return arrayList;
    }

    public List<HashMap> m3841b(String str) {
        JSONArray jSONArray;
        List<HashMap> arrayList = new ArrayList<>();
        try {
            jSONArray = new JSONArray(str);
        } catch (JSONException e) {
            e.printStackTrace();
            jSONArray = null;
        }
        length = jSONArray.length();
        for (int i = 0; i < length; i++) {
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

    public final void mo2963a(String str, ListView listView) {
        try {
            List<HashMap> songsList = m3834a(GZIPUtil.ZIPTo(new JSONObject(str).getString("L")));
            if (listView != null) {
                SearchSongsAdapter searchSongsAdapter = new SearchSongsAdapter(this, length, songsList);
                listView.setAdapter(searchSongsAdapter);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean handleMessage(Message message) {
        return false;
    }

    @Override
    public void onBackPressed() {
        jpprogressBar.dismiss();
        Intent intent = new Intent();
        switch (headType) {
            case 0:
                intent.setClass(this, OLSongsPage.class);
                break;
            case 1:
                intent.setClass(this, OLMelodySelect.class);
                break;
            case 6:
                intent.setClass(this, OLMainMode.class);
                break;
        }
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.ol_search_b) {
            f4948c = keywords.getText().toString();
            if (f4948c.isEmpty()) {
                Toast.makeText(this, "请输入需要搜索的关键词!", Toast.LENGTH_SHORT).show();
                return;
            }
            new SearchSongsTask(this).execute();
        }
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        jpapplication = (JPApplication) getApplication();
        headType = getIntent().getExtras().getInt("head");
        setContentView(R.layout.searchsongs);
        SkinImageLoadUtil.setBackGround(this, "ground", findViewById(R.id.layout));
        layoutinflater = LayoutInflater.from(this);
        keywords = findViewById(R.id.ol_keywords);
        Button f4957l = findViewById(R.id.ol_search_b);
        f4957l.setOnClickListener(this);
        jpprogressBar = new JPProgressBar(this);
        songsListView = findViewById(R.id.ol_search_list);
        TextView f4956k = findViewById(R.id.title_bar);
        if (headType == 6) {
            f4956k.setText("查找用户:");
        }
    }

    @Override
    protected void onDestroy() {
        if (pictureHandle != null) {
            pictureHandle.mo3026a();
        }
        super.onDestroy();
    }
}
