package ly.pp.justpiano3.activity;

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
import android.widget.*;
import ly.pp.justpiano3.*;
import ly.pp.justpiano3.adapter.OLMelodySelectAdapter2;
import ly.pp.justpiano3.adapter.OLMelodySelectTypeAdapter;
import ly.pp.justpiano3.adapter.PopupWindowSelectAdapter;
import ly.pp.justpiano3.constant.Consts;
import ly.pp.justpiano3.entity.GlobalSetting;
import ly.pp.justpiano3.enums.GameModeEnum;
import ly.pp.justpiano3.task.OLMelodySelectTask;
import ly.pp.justpiano3.utils.SkinImageLoadUtil;
import ly.pp.justpiano3.view.JPProgressBar;
import org.json.JSONArray;
import org.json.JSONException;

import java.text.Collator;
import java.util.*;

public class OLMelodySelect extends Activity implements Callback, OnClickListener {
    public static byte[] songBytes = null;
    public static String songID;
    public JPApplication jpapplication;
    public double degree;
    public int topScore;
    public Button pageButton;
    public int index;
    public int pageNum = 1;
    public PopupWindowSelectAdapter popupWindowSelectAdapter = null;
    public int f4314a;
    public String f4315b = "";
    public int f4316c;
    public String f4317e = "";
    public String songName = "";
    public JPProgressBar jpprogressBar;
    public int f4322k;
    public LayoutInflater layoutInflater1;
    public LayoutInflater layoutInflater2;
    public ListView f4327p;
    public TextView f4328q;
    public boolean f4330s = true;
    public boolean f4331t = true;
    public boolean f4332u = true;
    public boolean f4333v = true;
    public boolean f4334w = true;
    public boolean f4335x = true;
    public Button showTitleButton;
    private PopupWindow popupWindow = null;
    private final List<String> pageList = new ArrayList<>();
    private Handler handler;
    private boolean firstLoadFocusFinish = false;
    private List<HashMap> songList = null;

    public static final class SongsComparator implements Comparator<HashMap> {
        private final OLMelodySelect olMelodySelect;
        private final int type;
        private final Comparator comparator = Collator.getInstance(Locale.CHINA);

        public SongsComparator(OLMelodySelect oLMelodySelect, int i) {
            olMelodySelect = oLMelodySelect;
            type = i;
        }

        @Override
        public int compare(HashMap obj, HashMap obj2) {
            switch (type) {
                case 0:
                    return olMelodySelect.f4331t ? 0 - ((String) obj.get("update")).compareTo((String) obj2.get("update")) : ((String) obj.get("update")).compareTo((String) obj2.get("update"));
                case 1:
                    return olMelodySelect.f4332u ? ((Double) obj.get("degree")).compareTo((Double) obj2.get("degree")) : 0 - ((Double) obj.get("degree")).compareTo((Double) obj2.get("degree"));
                case 2:
                    return olMelodySelect.f4333v ? comparator.compare(obj.get("songName"), obj2.get("songName")) : 0 - comparator.compare(obj.get("songName"), obj2.get("songName"));
                case 3:
                    return olMelodySelect.f4334w ? comparator.compare(obj.get("items"), obj2.get("items")) : 0 - comparator.compare(obj.get("items"), obj2.get("items"));
                case 4:
                    return olMelodySelect.f4335x ? 0 - comparator.compare(obj.get("playCount"), obj2.get("playCount")) : comparator.compare(obj.get("playCount"), obj2.get("playCount"));
                default:
                    return 0;
            }
        }
    }


    public void m3643a(int i) {
        pageList.clear();
        for (int i2 = 1; i2 <= i; i2++) {
            pageList.add(i2 - 1, "第" + i2 + "页");
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

    public final void mo2808a(ListView listView, int i, int i2) {
        List<HashMap> list = songList;
        if (list != null && !list.isEmpty()) {
            Collections.sort(list, new SongsComparator(this, i));
        }
        listView.setAdapter(new OLMelodySelectAdapter2(this, i2, list));
    }

    public final void mo2809a(String str) {
        songList = m3648b(str);
    }

    @Override
    public boolean handleMessage(Message message) {
        Bundle data = message.getData();
        switch (message.what) {
            case 1:
                int i = data.getInt("selIndex");
                pageButton.setText(" " + pageList.get(i) + " ");
                index = i;
                popupWindow.dismiss();
                new OLMelodySelectTask(this).execute();
                break;
            case 2:
                pageList.remove(data.getInt("delIndex"));
                popupWindowSelectAdapter.notifyDataSetChanged();
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
                if (firstLoadFocusFinish) {
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
        jPApplication.setGameMode(GameModeEnum.NORMAL.getCode());
        try {
            GlobalSetting.INSTANCE.loadSettings(this, true);
            GlobalSetting.INSTANCE.setTempSpeed(1f);
            layoutInflater1 = LayoutInflater.from(this);
            layoutInflater2 = LayoutInflater.from(this);
            new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Consts.sortNames).setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            setContentView(LayoutInflater.from(this).inflate(R.layout.olmelodylist, null));
            SkinImageLoadUtil.setBackGround(this, "ground", findViewById(R.id.layout));
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
                index = 0;
                pageButton.setText(" 第1页 ");
                new OLMelodySelectTask(OLMelodySelect.this).execute();
            });
            f4328q = findViewById(R.id.title_bar);
            if (!f4330s) {
                f4328q.setVisibility(View.VISIBLE);
            }
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
            jpprogressBar = new JPProgressBar(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
    }

    @Override
    protected void onDestroy() {
        handler = null;
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        songBytes = null;
        super.onResume();
    }

    @Override
    public void onWindowFocusChanged(boolean z) {
        while (!firstLoadFocusFinish) {
            handler = new Handler(this);
            int width = pageButton.getWidth() + 40;
            m3643a(pageNum);
            View inflate = getLayoutInflater().inflate(R.layout.options, null);
            ListView listView = inflate.findViewById(R.id.list);
            popupWindowSelectAdapter = new PopupWindowSelectAdapter(this, handler, pageList, 1);
            listView.setAdapter(popupWindowSelectAdapter);
            popupWindow = new PopupWindow(inflate, width, -2, true);
            popupWindow.setOutsideTouchable(true);
            popupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.filled_face));
            firstLoadFocusFinish = true;
        }
    }
}
