package ly.pp.justpiano3.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import ly.pp.justpiano3.JPApplication;
import ly.pp.justpiano3.R;
import ly.pp.justpiano3.adapter.OLMelodySelectAdapter;
import ly.pp.justpiano3.adapter.OLMelodySelectTypeAdapter;
import ly.pp.justpiano3.adapter.PopupWindowSelectAdapter;
import ly.pp.justpiano3.constant.Consts;
import ly.pp.justpiano3.entity.GlobalSetting;
import ly.pp.justpiano3.enums.LocalPlayModeEnum;
import ly.pp.justpiano3.task.OLMelodySelectTask;
import ly.pp.justpiano3.view.JPPopupWindow;
import ly.pp.justpiano3.view.JPProgressBar;

public final class OLMelodySelect extends BaseActivity implements Callback, OnClickListener {
    public static byte[] songBytes;
    public static String songID;
    public JPApplication jpApplication;
    public double degree;
    public int topScore;
    private Button pageButton;
    public int index;
    public int pageNum = 1;
    public PopupWindowSelectAdapter popupWindowSelectAdapter;
    public int orderByType;
    public String type = "";
    public String songName = "";
    public JPProgressBar jpprogressBar;
    public int songCount;
    public LayoutInflater layoutInflater1;
    public LayoutInflater layoutInflater2;
    public ListView itemListView;
    private boolean updateOrderByReverse = true;
    private boolean degreeOrderByReverse = true;
    private boolean songNameOrderByReverse = true;
    private boolean itemOrderByReverse = true;
    private boolean playCountOrderByReverse = true;
    private PopupWindow popupWindow;
    private final List<String> pageList = new ArrayList<>();
    private boolean firstLoadFocusFinish;
    public List<Map<String, Object>> songList;

    public static final class SongsComparator implements Comparator<Map<String, Object>> {
        private final OLMelodySelect olMelodySelect;
        private final int type;
        private final Collator comparator = Collator.getInstance(Locale.CHINA);

        public SongsComparator(OLMelodySelect oLMelodySelect, int i) {
            olMelodySelect = oLMelodySelect;
            type = i;
        }

        @Override
        public int compare(Map<String, Object> obj, Map<String, Object> obj2) {
            return switch (type) {
                case 0 ->
                        olMelodySelect.updateOrderByReverse ? -((String) obj.get("update")).compareTo((String) obj2.get("update")) : ((String) obj.get("update")).compareTo((String) obj2.get("update"));
                case 1 ->
                        olMelodySelect.degreeOrderByReverse ? ((Double) obj.get("degree")).compareTo((Double) obj2.get("degree")) : -((Double) obj.get("degree")).compareTo((Double) obj2.get("degree"));
                case 2 ->
                        olMelodySelect.songNameOrderByReverse ? comparator.compare(obj.get("songName"), obj2.get("songName")) : -comparator.compare(obj.get("songName"), obj2.get("songName"));
                case 3 ->
                        olMelodySelect.itemOrderByReverse ? comparator.compare(obj.get("items"), obj2.get("items")) : -comparator.compare(obj.get("items"), obj2.get("items"));
                case 4 ->
                        olMelodySelect.playCountOrderByReverse ? -comparator.compare(obj.get("playCount"), obj2.get("playCount")) : comparator.compare(obj.get("playCount"), obj2.get("playCount"));
                default -> 0;
            };
        }
    }


    public void fillPageList(int i) {
        pageList.clear();
        for (int i2 = 1; i2 <= i; i2++) {
            pageList.add(i2 - 1, "第" + i2 + "页");
        }
    }

    public List<Map<String, Object>> songListHandle(String str) {
        JSONArray jSONArray;
        List<Map<String, Object>> arrayList = new ArrayList<>();
        try {
            jSONArray = new JSONArray(str);
        } catch (JSONException e) {
            e.printStackTrace();
            return arrayList;
        }
        songCount = jSONArray.length();
        for (int i = 0; i < songCount; i++) {
            try {
                Map<String, Object> songInfoMap = new HashMap<>();
                songInfoMap.put("songID", jSONArray.getJSONObject(i).get("SI").toString());
                songInfoMap.put("songName", jSONArray.getJSONObject(i).get("SN").toString());
                songInfoMap.put("degree", Double.valueOf(jSONArray.getJSONObject(i).get("DG").toString()));
                songInfoMap.put("items", jSONArray.getJSONObject(i).get("AR").toString());
                songInfoMap.put("topUser", jSONArray.getJSONObject(i).get("TU").toString());
                songInfoMap.put("topScore", Integer.valueOf(jSONArray.getJSONObject(i).get("TS").toString()));
                songInfoMap.put("update", jSONArray.getJSONObject(i).get("UP").toString());
                songInfoMap.put("length", jSONArray.getJSONObject(i).get("LE").toString());
                songInfoMap.put("playCount", jSONArray.getJSONObject(i).get("PC").toString());
                arrayList.add(songInfoMap);
            } catch (JSONException e2) {
                e2.printStackTrace();
            }
        }
        return arrayList;
    }

    public void bindAdapter(ListView listView, int i, int i2) {
        List<Map<String, Object>> list = songList;
        if (list != null && !list.isEmpty()) {
            Collections.sort(list, new SongsComparator(this, i));
        }
        listView.setAdapter(new OLMelodySelectAdapter(this, i2, list));
    }

    @Override
    public boolean handleMessage(Message message) {
        Bundle data = message.getData();
        switch (message.what) {
            case 1 -> {
                int i = data.getInt("selIndex");
                pageButton.setText(" " + pageList.get(i) + " ");
                index = i;
                popupWindow.dismiss();
                new OLMelodySelectTask(this).execute();
            }
            case 2 -> {
                pageList.remove(data.getInt("delIndex"));
                popupWindowSelectAdapter.notifyDataSetChanged();
            }
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
        int id = view.getId();
        if (id == R.id.ol_date_b) {
            if (updateOrderByReverse) {
                z2 = false;
            }
            updateOrderByReverse = z2;
            orderByType = 0;
            bindAdapter(itemListView, orderByType, songCount);
        } else if (id == R.id.ol_degree_b) {
            if (!degreeOrderByReverse) {
                z = true;
            }
            degreeOrderByReverse = z;
            orderByType = 1;
            bindAdapter(itemListView, orderByType, songCount);
        } else if (id == R.id.ol_name_b) {
            if (!songNameOrderByReverse) {
                z = true;
            }
            songNameOrderByReverse = z;
            orderByType = 2;
            bindAdapter(itemListView, orderByType, songCount);
        } else if (id == R.id.ol_items_b) {
            if (!itemOrderByReverse) {
                z = true;
            }
            itemOrderByReverse = z;
            orderByType = 3;
            bindAdapter(itemListView, orderByType, songCount);
        } else if (id == R.id.ol_hot_b) {
            if (!playCountOrderByReverse) {
                z = true;
            }
            playCountOrderByReverse = z;
            orderByType = 4;
            bindAdapter(itemListView, orderByType, songCount);
        } else if (id == R.id.ol_search_button) {
            Intent intent = new Intent();
            intent.putExtra("head", 1);
            intent.setClass(this, SearchSongs.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.ol_top_next) {
            if (firstLoadFocusFinish) {
                popupWindow.showAsDropDown(pageButton);
                return;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        jpApplication = (JPApplication) getApplication();
        GlobalSetting.INSTANCE.setLocalPlayMode(LocalPlayModeEnum.NORMAL);
        try {
            GlobalSetting.INSTANCE.loadSettings(this, true);
            layoutInflater1 = LayoutInflater.from(this);
            layoutInflater2 = LayoutInflater.from(this);
            new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Consts.sortNames).setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            setContentView(LayoutInflater.from(this).inflate(R.layout.ol_melody_list, null));
            LinearLayout linearLayout = findViewById(R.id.sup_view);
            findViewById(R.id.ol_search_button).setOnClickListener(this);
            ListView f4326o = findViewById(R.id.ol_f_list);
            itemListView = findViewById(R.id.ol_c_list);
            f4326o.setAdapter(new OLMelodySelectTypeAdapter(this));
            f4326o.setCacheColorHint(Color.TRANSPARENT);
            f4326o.setOnItemClickListener((parent, view, position, id) -> {
                linearLayout.setVisibility(View.GONE);
                type = Consts.items[position + 1];
                index = 0;
                pageButton.setText(" 第1页 ");
                new OLMelodySelectTask(OLMelodySelect.this).execute();
            });
            TextView f4328q = findViewById(R.id.title_bar);
            boolean f4330s = true;
            if (!f4330s) {
                f4328q.setVisibility(View.VISIBLE);
            }
            findViewById(R.id.ol_date_b).setOnClickListener(this);
            findViewById(R.id.ol_degree_b).setOnClickListener(this);
            findViewById(R.id.ol_name_b).setOnClickListener(this);
            findViewById(R.id.ol_hot_b).setOnClickListener(this);
            findViewById(R.id.ol_items_b).setOnClickListener(this);
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
    protected void onResume() {
        songBytes = null;
        super.onResume();
    }

    @Override
    public void onWindowFocusChanged(boolean z) {
        while (!firstLoadFocusFinish) {
            Handler handler = new Handler(this);
            int width = pageButton.getWidth() + 40;
            fillPageList(pageNum);
            View inflate = getLayoutInflater().inflate(R.layout.options, null);
            ListView listView = inflate.findViewById(R.id.list);
            popupWindowSelectAdapter = new PopupWindowSelectAdapter(this, handler, pageList, 1);
            listView.setAdapter(popupWindowSelectAdapter);
            popupWindow = new JPPopupWindow(inflate, width, ViewGroup.LayoutParams.WRAP_CONTENT, true);
            popupWindow.setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.filled_face, getTheme()));
            firstLoadFocusFinish = true;
        }
    }
}
