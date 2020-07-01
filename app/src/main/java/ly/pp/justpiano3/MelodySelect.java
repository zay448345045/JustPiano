package ly.pp.justpiano3;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MelodySelect extends Activity implements Callback, TextWatcher, OnClickListener {
    public Handler handler;
    SharedPreferences sharedPreferences;
    LayoutInflater layoutInflater1;
    LayoutInflater layoutInflater2;
    ImageView f4228E;
    String f4231H = "";
    String f4238O = "online = 1";
    String songsPath = "";
    CheckBox isRecord;
    CheckBox isFollowPlay;
    CheckBox isLeftHand;
    AutoCompleteTextView autoctv;
    Button f4249e;
    SQLiteDatabase sqlitedatabase;
    boolean f4255k;
    String sortStr = null;
    View f4261q;
    PlaySongs playSongs;
    String f4263s = "";
    int f4264t;
    TextView f4266v;
    JPApplication jpapplication;
    Cursor cursor;
    private Button f4229F;
    private boolean f4234K;
    private PopupWindow popupwindow = null;
    private TextView f4237N;
    private int f4244U;
    private ListView f4246b;
    private LocalSongsAdapter f4250f;
    private TestSQL testSQL;
    private int score;
    private List<String> f4269y = new ArrayList<>();

    private void m3611b() {
        f4269y.clear();
        Collections.addAll(f4269y, Consts.sortNames);
    }

    public final void mo2784a(Cursor cursor) {
        isFollowPlay.setChecked(false);
        if (f4250f == null) {
            f4250f = new LocalSongsAdapter(this, this, cursor);
            f4246b.setAdapter(f4250f);
            return;
        }
        Cursor cursor2;
        switch (sortStr) {
            case "name desc":
                cursor2 = new JustPianoCursorWrapper(cursor, "name", false);
                break;
            case "isnew desc":
                cursor2 = new JustPianoCursorWrapper(cursor, "isnew", false);
                break;
            case "date desc":
                cursor2 = new JustPianoCursorWrapper(cursor, "date", false);
                break;
            case "diff asc":
                cursor2 = new JustPianoCursorWrapper(cursor, "diff", true);
                break;
            case "diff desc":
                cursor2 = new JustPianoCursorWrapper(cursor, "diff", false);
                break;
            case "score asc":
                cursor2 = new JustPianoCursorWrapper(cursor, "score", true);
                break;
            case "score desc":
                cursor2 = new JustPianoCursorWrapper(cursor, "score", false);
                break;
            case "length asc":
                cursor2 = new JustPianoCursorWrapper(cursor, "length", true);
                break;
            case "length desc":
                cursor2 = new JustPianoCursorWrapper(cursor, "length", false);
                break;
            default:
                cursor2 = new JustPianoCursorWrapper(cursor, "name", true);
        }
        f4250f.changeCursor(cursor2);
        f4250f.notifyDataSetChanged();
    }

    protected final void mo2785a(String str2, int i) {
        JPDialog jpdialog = new JPDialog(this);
        jpdialog.setTitle("提示");
        jpdialog.setMessage(str2);
        jpdialog.setFirstButton("确定", new DialogDismissClick());
        jpdialog.setSecondButton("不再提示", new DoNotShowDialogClick(this, i));
        jpdialog.showDialog();
    }

    final boolean getIsFollowPlay() {
        return isFollowPlay.isChecked();
    }

    @Override
    public boolean handleMessage(Message message) {
        Bundle data = message.getData();
        switch (message.what) {
            case 1:
                int i = data.getInt("selIndex");
                f4229F.setText(f4269y.get(i));
                sortStr = Consts.sortSyntax[i];
                if (!f4231H.isEmpty() && f4250f != null && sqlitedatabase != null) {
                    mo2784a(sqlitedatabase.query("jp_data", null, "ishot = 0 and " + f4238O + " and item=?", new String[]{f4231H}, null, null, sortStr));
                }
                popupwindow.dismiss();
                break;
            case 3:
                CharSequence format = SimpleDateFormat.getTimeInstance(3, Locale.CHINESE).format(new Date());
                if (f4237N != null) {
                    f4237N.setText(format);
                    break;
                }
                break;
            case 4:
                Cursor cursor = f4250f.getCursor();
                int i2 = data.getInt("position") + 1;
                playSongs = null;
                songsPath = "";
                if (cursor.moveToPosition(i2)) {
                    String string = cursor.getString(cursor.getColumnIndexOrThrow("path"));
                    String string2 = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                    playSongs = new PlaySongs(jpapplication, string, this, null, i2, 0);
                    Toast.makeText(this, "正在播放:《" + string2 + "》", Toast.LENGTH_SHORT).show();
                    break;
                }
                break;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if (sqlitedatabase.isOpen()) {
            sqlitedatabase.close();
        }
        if (playSongs != null) {
            jpapplication.stopMusic();
            playSongs.isPlayingSongs = false;
            playSongs = null;
        }
        Intent intent;
        if (f4244U == 10) {
            intent = new Intent();
            intent.setClass(this, MainMode.class);
            startActivity(intent);
        } else {
            intent = new Intent();
            intent.setClass(this, PlayModeSelect.class);
            startActivity(intent);
        }
        finish();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.list_sort_b:
                if (f4234K) {
                    popupwindow.showAsDropDown(f4229F);
                }
                return;
            case R.id.search_button:
                View inflate = getLayoutInflater().inflate(R.layout.message_send, findViewById(R.id.dialog));
                TextView textView = inflate.findViewById(R.id.text_1);
                TextView textView2 = inflate.findViewById(R.id.title_1);
                TextView textView3 = inflate.findViewById(R.id.title_2);
                inflate.findViewById(R.id.text_2).setVisibility(View.GONE);
                textView3.setVisibility(View.GONE);
                textView2.setText("关键词:");
                new JPDialog(this).setTitle("搜索曲谱").loadInflate(inflate).setFirstButton("搜索", new SearchSongsClick(this, textView.getText().toString())).setSecondButton("取消", new DialogDismissClick()).showDialog();
                return;
            case R.id.search_fast:
                autoctv.clearFocus();
                cursor = sqlitedatabase.query("jp_data", null, "name like '%" + autoctv.getText().toString() + "%' AND " + f4238O, null, null, null, sortStr);
                mo2784a(cursor);
                try {
                    ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return;
            case R.id.setting_fast:
                Intent intent = new Intent();
                intent.setClass(this, SettingsMode.class);
                startActivity(intent);
                return;
            default:
        }
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        f4244U = getIntent().getFlags();
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        sortStr = Consts.sortSyntax[0];
        jpapplication = (JPApplication) getApplication();
        try {
            LayoutInflater f4265u = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            layoutInflater1 = LayoutInflater.from(this);
            layoutInflater2 = LayoutInflater.from(this);
            testSQL = new TestSQL(this, "data");
            sqlitedatabase = testSQL.getWritableDatabase();
            cursor = sqlitedatabase.query("jp_data", null, f4238O, null, null, null, null);
            while (cursor.moveToNext()) {
                score += cursor.getInt(cursor.getColumnIndexOrThrow("score"));
            }
            int f4257m = cursor.getCount();
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
            LinearLayout linearLayout = (LinearLayout) f4265u.inflate(R.layout.melodylist1, null);
            setContentView(linearLayout);
            linearLayout.setOnTouchListener((v, event) -> {
                if (null != getCurrentFocus()) {
                    InputMethodManager mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    if (mInputMethodManager != null) {
                        return mInputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    }
                }
                return false;
            });
            jpapplication.setBackGround(this, "ground", linearLayout);
            Button f4230G = findViewById(R.id.search_button);
            f4230G.setOnClickListener(this);
            f4229F = findViewById(R.id.list_sort_b);
            f4229F.setOnClickListener(this);
            ListView f4245a = findViewById(R.id.f_list);
            f4246b = findViewById(R.id.c_list);
            TextView f4247c = findViewById(R.id.all_mel);
            autoctv = findViewById(R.id.search_edit);
            ImageView searchFast = findViewById(R.id.search_fast);
            searchFast.setOnClickListener(this);
            ImageView settingFast = findViewById(R.id.setting_fast);
            settingFast.setOnClickListener(this);
            autoctv.addTextChangedListener(this);
            initAutoComplete(autoctv);
            isRecord = findViewById(R.id.check_record);
            if (f4244U == 10) {
                isRecord.setVisibility(View.GONE);
            } else {
                isRecord.setOnCheckedChangeListener(new RecordCheckChange(this));
            }
            isFollowPlay = findViewById(R.id.check_play);
            if (f4244U == 10) {
                isFollowPlay.setVisibility(View.GONE);
            } else {
                isFollowPlay.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked && sharedPreferences.getBoolean("play_dialog", true)) {
                        mo2785a("选择后软件将从当前播放的曲目开始依次播放试听。", 1);
                    }
                });
            }
            isLeftHand = findViewById(R.id.check_hand);
            if (f4244U == 10) {
                isLeftHand.setVisibility(View.GONE);
            } else {
                isLeftHand.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked && sharedPreferences.getBoolean("hand_dialog", true)) {
                        mo2785a("选择后您将弹奏曲谱的和弦，软件将自动播放主旋律。", 2);
                    }
                });
            }
            TextView f4248d = findViewById(R.id.totle_score_all);
            f4247c.setText("曲谱:" + f4257m);
            f4248d.setText("总分:" + score);
            f4245a.setAdapter(new LocalSongsItemAdapter(this));
            f4246b.setCacheColorHint(0);
            f4245a.setCacheColorHint(0);
            f4245a.setOnItemClickListener((parent, view, position, id) -> {
                Cursor query;
                f4264t = position;
                if (f4261q != null) {
                    f4261q.setBackgroundResource(R.color.top_background);
                }
                view.setBackgroundResource(R.color.translent);
                if (position == 0) {
                    f4231H = "";
                    query = sqlitedatabase.query("jp_data", null, "isfavo = 1 AND " + f4238O, null, null, null, sortStr);
                } else {
                    f4231H = Consts.items[position];
                    query = sqlitedatabase.query("jp_data", null, "ishot = 0 AND " + f4238O + " AND item=?", new String[]{f4231H}, null, null, sortStr);
                }
                mo2784a(query);
                f4263s = Consts.items[position];
                f4261q = view;
            });
            f4266v = findViewById(R.id.title_bar);
            f4266v.setVisibility(View.GONE);
            f4237N = findViewById(R.id.time_text);
            if (f4255k) {
                f4266v.setVisibility(View.VISIBLE);
            }
            f4249e = findViewById(R.id.show_button);
            f4249e.setVisibility(View.GONE);
            f4249e.setOnClickListener(v -> {
                if (f4255k) {
                    f4266v.setVisibility(View.GONE);
                    f4255k = false;
                    f4249e.setText("显示标题");
                    return;
                }
                f4266v.setVisibility(View.GONE);
                f4255k = true;
                f4249e.setText("隐藏标题");
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        sharedPreferences = getSharedPreferences("set", 0);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
    }

    @Override
    protected void onDestroy() {
        if (playSongs != null) {
            jpapplication.stopMusic();
            playSongs.isPlayingSongs = false;
            playSongs = null;
        }
        handler = null;
        if (f4250f != null && f4250f.getCursor() != null) {
            f4250f.getCursor().close();
        }
        if (cursor != null) {
            cursor.close();
            cursor = null;
        }
        if (sqlitedatabase.isOpen()) {
            sqlitedatabase.close();
            sqlitedatabase = null;
            testSQL.close();
            testSQL = null;
        }
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
        isRecord.setChecked(false);
    }

    @Override
    public void onWindowFocusChanged(boolean z) {
        super.onWindowFocusChanged(z);
        while (!f4234K) {
            handler = new Handler(this);
            int f4232I = f4229F.getWidth() + 10;
            m3611b();
            View inflate = getLayoutInflater().inflate(R.layout.options, null);
            ListView f4233J = inflate.findViewById(R.id.list);
            OLMelodySelectAdapter f4236M = new OLMelodySelectAdapter(this, handler, f4269y);
            f4233J.setAdapter(f4236M);
            popupwindow = new PopupWindow(inflate, f4232I, -2, true);
            popupwindow.setOutsideTouchable(true);
            popupwindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.filled_box));
            f4234K = true;
        }
    }

    private void initAutoComplete(AutoCompleteTextView autoCompleteTextView) {
        String valueOf = autoCompleteTextView.getText().toString();
        cursor = sqlitedatabase.query("jp_data", null, "name like '%" + valueOf + "%' AND " + f4238O, null, null, null, sortStr);
        MelodySelectAdapter adapter = new MelodySelectAdapter(this, cursor, this);
        autoCompleteTextView.setAdapter(adapter);
        autoCompleteTextView.setOnFocusChangeListener((v, hasFocus) -> {
            AutoCompleteTextView view = (AutoCompleteTextView) v;
            if (hasFocus) {
                view.showDropDown();
            }
        });
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (!autoctv.getText().toString().contains("\'")) {
            cursor = sqlitedatabase.query("jp_data", null, "name like '%" + s.toString() + "%' AND " + f4238O, null, null, null, sortStr);
            MelodySelectAdapter adapter = new MelodySelectAdapter(this, cursor, this);
            autoctv.setAdapter(adapter);
            autoctv.setOnFocusChangeListener((v, hasFocus) -> {
                AutoCompleteTextView view = (AutoCompleteTextView) v;
                if (hasFocus) {
                    view.showDropDown();
                }
            });
        }
    }

    public Cursor search(String str) {
        str = str.replace("\'", "\'\'");
        cursor = sqlitedatabase.query("jp_data", null, "name like '%" + str + "%' AND " + f4238O, null, null, null, sortStr);
        return cursor;
    }
}

