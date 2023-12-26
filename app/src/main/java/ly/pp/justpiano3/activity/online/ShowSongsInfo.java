package ly.pp.justpiano3.activity.online;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ly.pp.justpiano3.R;
import ly.pp.justpiano3.activity.BaseActivity;
import ly.pp.justpiano3.adapter.ShowSongsInfoAdapter;
import ly.pp.justpiano3.task.ShowSongsInfoTask;
import ly.pp.justpiano3.utils.GZIPUtil;
import ly.pp.justpiano3.view.JPProgressBar;

public final class ShowSongsInfo extends BaseActivity implements OnClickListener {
    public LayoutInflater layoutInflater;
    public String keywords = "";
    public String songName = "";
    public String songID;
    public ListView songsListView;
    public JPProgressBar jpprogressBar;
    public double degree;
    public int score;
    public String head = "1";
    public int page;
    private TextView pageText;
    private ShowSongsInfoAdapter showSongsInfoAdapter;
    private List<Map<String, Object>> songsList;

    private List<Map<String, Object>> songListHandle(String str) {
        JSONArray jSONArray;
        List<Map<String, Object>> arrayList = new ArrayList<>();
        try {
            jSONArray = new JSONArray(str);
        } catch (JSONException e) {
            e.printStackTrace();
            jSONArray = null;
        }
        for (int i = 0; i < jSONArray.length(); i++) {
            try {
                Map<String, Object> hashMap = new HashMap<>();
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

    public void updateSongInfo(int i) {
        if (showSongsInfoAdapter != null && songsList != null) {
            songsList.remove(i);
            showSongsInfoAdapter.mo3500a(songsList);
            showSongsInfoAdapter.notifyDataSetChanged();
        }
    }

    public void handleResultAndBindAdapter(String str, ListView listView) {
        try {
            songsList = songListHandle(GZIPUtil.ZIPTo(new JSONObject(str).getString("L")));
            showSongsInfoAdapter = new ShowSongsInfoAdapter(this, songsList);
            if (listView != null) {
                listView.setAdapter(showSongsInfoAdapter);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        if (jpprogressBar != null) {
            jpprogressBar.dismiss();
        }
        Intent intent = new Intent();
        intent.setClass(this, OLSongsPage.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View view) {
        if (keywords.equals("H") || keywords.equals("N") || keywords.equals("T") || keywords.equals("M")) {
            int id = view.getId();
            if (id == R.id.ol_top_next) {
                page++;
            } else if (id == R.id.ol_top_before) {
                page--;
                if (page < 0) {
                    page = 0;
                }
            }
            pageText.setText("-" + (page + 1) + "-");
            new ShowSongsInfoTask(this).execute();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        head = extras.getString("head");
        keywords = extras.getString("keywords");
        setContentView(R.layout.ol_top_info);
        layoutInflater = LayoutInflater.from(this);
        findViewById(R.id.ol_top_before).setOnClickListener(this);
        findViewById(R.id.ol_top_next).setOnClickListener(this);
        pageText = findViewById(R.id.ol_top_page);
        pageText.setText("-" + (page + 1) + "-");
        songsListView = findViewById(R.id.ol_top_list);
        jpprogressBar = new JPProgressBar(this);
        TextView topTitleTextView = findViewById(R.id.ol_top_title);
        switch (keywords) {
            case "N" -> topTitleTextView.setText("-最新曲目-");
            case "H" -> topTitleTextView.setText("-热门曲目-");
            case "F" -> topTitleTextView.setText("-曲谱收藏-");
            case "T" -> {
                topTitleTextView.setText("-新晋冠军-");
                songsListView.setCacheColorHint(Color.TRANSPARENT);
            }
            case "M" -> topTitleTextView.setText("-上传测试-");
        }
        new ShowSongsInfoTask(this).execute();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
    }
}
