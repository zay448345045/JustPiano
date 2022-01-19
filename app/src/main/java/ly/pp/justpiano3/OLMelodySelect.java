package ly.pp.justpiano3;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class OLMelodySelect extends Activity implements Callback, OnClickListener {
    static byte[] songBytes = null;
    static String songID;
    public JPApplication jpapplication;
    double degree;
    int topScore;
    Button pageButton;
    int f4303H;
    int f4305J = 1;
    OLMelodySelectAdapter olMelodySelectAdapter = null;
    int f4314a;
    String f4315b = "";
    int f4316c;
    String f4317e = "";
    String songName = "";
    JPProgressBar jpprogressBar;
    int f4322k;
    LayoutInflater layoutInflater1;
    LayoutInflater layoutInflater2;
    ListView f4327p;
    TextView f4328q;
    boolean f4330s = true;
    boolean f4331t = true;
    boolean f4332u = true;
    boolean f4333v = true;
    boolean f4334w = true;
    boolean f4335x = true;
    Button f4336y;
    private PopupWindow popupWindow = null;
    private List<String> f4308M = new ArrayList<>();
    private Handler f4311P;
    private boolean f4312Q = false;
    private List<HashMap> songList = null;

    void m3643a(int i) {
        f4308M.clear();
        for (int i2 = 1; i2 <= i; i2++) {
            f4308M.add(i2 - 1, "第" + i2 + "页");
        }
    }

    private List<HashMap> m3648b(String str) {
        JSONArray jSONArray;
        List<HashMap> arrayList = new ArrayList<>();
        try {
            jSONArray = new JSONArray(str);
        } catch (JSONException e) {
            e.printStackTrace();
            jSONArray = null;
        }
        f4322k = jSONArray.length();
        for (int i = 0; i < f4322k; i++) {
            try {
                HashMap hashMap = new HashMap();
                hashMap.put("songID", jSONArray.getJSONObject(i).get("SI").toString());
                hashMap.put("songName", jSONArray.getJSONObject(i).get("SN").toString());
                hashMap.put("degree", Double.valueOf(jSONArray.getJSONObject(i).get("DG").toString()));
                hashMap.put("items", jSONArray.getJSONObject(i).get("AR").toString());
                hashMap.put("topUser", jSONArray.getJSONObject(i).get("TU").toString());
                hashMap.put("topScore", Integer.valueOf(jSONArray.getJSONObject(i).get("TS").toString()));
                hashMap.put("update", jSONArray.getJSONObject(i).get("UP").toString());
                hashMap.put("length", jSONArray.getJSONObject(i).get("LE").toString());
                hashMap.put("playCount", jSONArray.getJSONObject(i).get("PC").toString());
                arrayList.add(hashMap);
            } catch (JSONException e2) {
                e2.printStackTrace();
            }
        }
        return arrayList;
    }

    final void mo2808a(ListView listView, int i, int i2) {
        List<HashMap> list = songList;
        if (list != null && !list.isEmpty()) {
            Collections.sort(list, new SongsComparator(this, i));
        }
        listView.setAdapter(new OLMelodySelectAdapter2(this, i2, list));
    }

    final void mo2809a(String str) {
        songList = m3648b(str);
    }

    @Override
    public boolean handleMessage(Message message) {
        Bundle data = message.getData();
        switch (message.what) {
            case 1:
                int i = data.getInt("selIndex");
                pageButton.setText(" " + f4308M.get(i) + " ");
                f4303H = i;
                popupWindow.dismiss();
                new OLMelodySelectTask(this).execute();
                break;
            case 2:
                f4308M.remove(data.getInt("delIndex"));
                olMelodySelectAdapter.notifyDataSetChanged();
                break;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        jpprogressBar.dismiss();
        Intent intent = new Intent();
        intent.setClass(this, OLSongsPage.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View view) {
        boolean z = false;
        boolean z2 = true;
        switch (view.getId()) {
            case R.id.ol_date_b:
                if (f4331t) {
                    z2 = false;
                }
                f4331t = z2;
                f4316c = 0;
                mo2808a(f4327p, f4316c, f4322k);
                return;
            case R.id.ol_degree_b:
                if (!f4332u) {
                    z = true;
                }
                f4332u = z;
                f4316c = 1;
                mo2808a(f4327p, f4316c, f4322k);
                return;
            case R.id.ol_name_b:
                if (!f4333v) {
                    z = true;
                }
                f4333v = z;
                f4316c = 2;
                mo2808a(f4327p, f4316c, f4322k);
                return;
            case R.id.ol_items_b:
                if (!f4334w) {
                    z = true;
                }
                f4334w = z;
                f4316c = 3;
                mo2808a(f4327p, f4316c, f4322k);
                return;
            case R.id.ol_hot_b:
                if (!f4335x) {
                    z = true;
                }
                f4335x = z;
                f4316c = 3;
                f4316c = 4;
                mo2808a(f4327p, f4316c, f4322k);
                return;
            case R.id.ol_search_button:
                Intent intent = new Intent();
                intent.putExtra("head", 1);
                intent.setClass(this, SearchSongs.class);
                startActivity(intent);
                finish();
                return;
            case R.id.ol_top_next:
                if (f4312Q) {
                    popupWindow.showAsDropDown(pageButton);
                    return;
                }
                return;
            default:
        }
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        jpapplication = (JPApplication) getApplication();
        JPApplication jPApplication = jpapplication;
        jPApplication.setGameMode(0);
        try {
            jpapplication.loadSettings(1);
            jpapplication.setTempSpeed();
            layoutInflater1 = LayoutInflater.from(this);
            layoutInflater2 = LayoutInflater.from(this);
            new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Consts.sortNames).setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            setContentView(LayoutInflater.from(this).inflate(R.layout.olmelodylist, null));
            jpapplication.setBackGround(this, "ground", findViewById(R.id.layout));
            LinearLayout linearLayout = findViewById(R.id.sup_view);
            Button f4325n = findViewById(R.id.ol_search_button);
            f4325n.setOnClickListener(this);
            ListView f4326o = findViewById(R.id.ol_f_list);
            f4327p = findViewById(R.id.ol_c_list);
            f4326o.setAdapter(new OLMelodySelectTypeAdapter(this));
            f4326o.setCacheColorHint(0);
            f4326o.setOnItemClickListener((parent, view, position, id) -> {
                linearLayout.setVisibility(View.GONE);
                f4314a = position;
                f4315b = Consts.items[position + 1];
                f4317e = Consts.items[position + 1];
                f4303H = 0;
                pageButton.setText(" 第1页 ");
                new OLMelodySelectTask(OLMelodySelect.this).execute();
            });
            f4328q = findViewById(R.id.title_bar);
            if (!f4330s) {
                f4328q.setVisibility(View.VISIBLE);
            }
            f4336y = findViewById(R.id.show_button);
            Button f4337z = findViewById(R.id.ol_date_b);
            f4337z.setOnClickListener(this);
            Button f4296A = findViewById(R.id.ol_degree_b);
            f4296A.setOnClickListener(this);
            Button f4297B = findViewById(R.id.ol_name_b);
            f4297B.setOnClickListener(this);
            Button f4299D = findViewById(R.id.ol_hot_b);
            f4299D.setOnClickListener(this);
            Button f4298C = findViewById(R.id.ol_items_b);
            f4298C.setOnClickListener(this);
            pageButton = findViewById(R.id.ol_top_next);
            pageButton.setOnClickListener(this);
            f4336y.setOnClickListener(new ShowTitleClick(this));
            jpprogressBar = new JPProgressBar(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
    }

    @Override
    protected void onDestroy() {
        f4311P = null;
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        songBytes = null;
        super.onResume();
    }

    @Override
    public void onWindowFocusChanged(boolean z) {
        while (!f4312Q) {
            f4311P = new Handler(this);
            int f4309N = pageButton.getWidth();
            m3643a(f4305J);
            View inflate = getLayoutInflater().inflate(R.layout.options, null);
            ListView f4310O = inflate.findViewById(R.id.list);
            olMelodySelectAdapter = new OLMelodySelectAdapter(this, f4311P, f4308M);
            f4310O.setAdapter(olMelodySelectAdapter);
            popupWindow = new PopupWindow(inflate, f4309N, -2, true);
            popupWindow.setOutsideTouchable(true);
            popupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.filled_face));
            f4312Q = true;
        }
    }
}
