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
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
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
    JPProgressBar jpprogressBar;
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
    TextView titleTextView;
    JPApplication jpapplication;
    Cursor cursor;
    private Button sortButton;
    private ImageView menuListButton;
    private boolean firstLoadFocusFinish;
    private PopupWindow sortPopupwindow = null;
    private PopupWindow menuPopupwindow = null;
    private TextView f4237N;
    private int f4244U;
    private ListView songListView;
    private LocalSongsAdapter songListAdapter;
    public TestSQL testSQL;
    private int score;
    private final List<String> sortNamesList = new ArrayList<>();

    public final void mo2784a(Cursor cursor) {
        isFollowPlay.setChecked(false);
        if (songListAdapter == null) {
            songListAdapter = new LocalSongsAdapter(this, this, cursor);
            songListView.setAdapter(songListAdapter);
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
        songListAdapter.changeCursor(cursor2);
        songListAdapter.notifyDataSetChanged();
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
                sortButton.setText(sortNamesList.get(i));
                sortStr = Consts.sortSyntax[i];
                if (!f4231H.isEmpty() && songListAdapter != null && sqlitedatabase != null) {
                    mo2784a(sqlitedatabase.query("jp_data", null, "ishot = 0 and " + f4238O + " and item=?", new String[]{f4231H}, null, null, sortStr));
                }
                sortPopupwindow.dismiss();
                break;
            case 2:
                menuPopupwindow.dismiss();
                if (playSongs != null) {
                    playSongs.isPlayingSongs = false;
                    playSongs = null;
                }
                Intent intent = new Intent();
                switch (data.getInt("selIndex")) {
                    case 0:  // 参数设置
                        intent.setClass(this, SettingsMode.class);
                        startActivityForResult(intent, JPApplication.SETTING_MODE_CODE);
                        break;
                    case 1:  // 曲库同步
                        new SongSyncTask(this, OLMainMode.getMaxSongIdFromDatabase(testSQL)).execute();
                        break;
                    case 2:  // 数据导出
                        JPDialog jpdialog = new JPDialog(this);
                        jpdialog.setTitle("数据导入导出");
                        jpdialog.setMessage("此功能可将本地收藏夹、弹奏分数数据进行导入导出，导出路径为SD卡\\JustPiano\\local_data.db。导入文件后将清除当前本地曲谱分数及收藏数据，请谨慎操作");
                        jpdialog.setVisibleRadioGroup(true);
                        RadioButton radioButton = new RadioButton(this);
                        radioButton.setText("APP本地收藏夹、弹奏分数数据导出至SD卡\\JustPiano\\local_data.db");
                        radioButton.setTextSize(13);
                        radioButton.setTag(1);
                        radioButton.setHeight(100);
                        jpdialog.addRadioButton(radioButton);
                        radioButton = new RadioButton(this);
                        radioButton.setText("导入SD卡\\JustPiano\\local_data.db中的数据至APP(导入后将清除当前数据)");
                        radioButton.setTextSize(13);
                        radioButton.setTag(2);
                        radioButton.setHeight(100);
                        jpdialog.addRadioButton(radioButton);
                        jpdialog.setFirstButton("执行", (dialog, which) -> {
                            dialog.dismiss();
                            new LocalDataImportExportTask(this, jpdialog.getRadioGroupCheckedId()).execute();
                        });
                        jpdialog.setSecondButton("取消", new DialogDismissClick());
                        jpdialog.showDialog();
                        break;
                    case 3:  // 录音文件
                        intent.setClass(this, RecordFiles.class);
                        startActivity(intent);
                        break;
                }
                break;
            case 3:
                CharSequence format = SimpleDateFormat.getTimeInstance(3, Locale.CHINESE).format(new Date());
                if (f4237N != null) {
                    f4237N.setText(format);
                    break;
                }
                break;
            case 4:
                Cursor cursor = songListAdapter.getCursor();
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == JPApplication.SETTING_MODE_CODE) {
            jpapplication.setBackGround(this, "ground", findViewById(R.id.layout));
        }
    }

    @Override
    public void onBackPressed() {
        if (sqlitedatabase.isOpen()) {
            sqlitedatabase.close();
        }
        if (playSongs != null) {
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
                if (firstLoadFocusFinish) {
                    sortPopupwindow.showAsDropDown(sortButton);
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
            case R.id.menu_list_fast:
                if (firstLoadFocusFinish) {
                    menuPopupwindow.showAsDropDown(menuListButton);
                }
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
        jpprogressBar = new JPProgressBar(this, jpapplication);
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
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    if (inputMethodManager != null) {
                        return inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    }
                }
                return false;
            });
            jpapplication.setBackGround(this, "ground", linearLayout);
            Button f4230G = findViewById(R.id.search_button);
            f4230G.setOnClickListener(this);
            sortButton = findViewById(R.id.list_sort_b);
            sortButton.setOnClickListener(this);
            ListView f4245a = findViewById(R.id.f_list);
            songListView = findViewById(R.id.c_list);
            TextView f4247c = findViewById(R.id.all_mel);
            autoctv = findViewById(R.id.search_edit);
            ImageView searchFast = findViewById(R.id.search_fast);
            searchFast.setOnClickListener(this);
            menuListButton = findViewById(R.id.menu_list_fast);
            menuListButton.setOnClickListener(this);
            autoctv.addTextChangedListener(this);
            initAutoComplete(autoctv);
            isRecord = findViewById(R.id.check_record);
            if (f4244U == 10) {
                isRecord.setVisibility(View.GONE);
            } else {
                isRecord.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked && sharedPreferences.getBoolean("record_dialog", true)) {
                        mo2785a("选择后软件将在开始弹奏时启动录音，弹奏完成时结束录音并存储至文件。录音功能仅录制极品钢琴内弹奏的音频，不含其他后台音频及环境杂音，无需授予录音权限，但需确保授予文件存储权限", 0);
                    }
                });
            }
            isFollowPlay = findViewById(R.id.check_play);
            if (f4244U == 10) {
                isFollowPlay.setVisibility(View.GONE);
            } else {
                isFollowPlay.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked && sharedPreferences.getBoolean("play_dialog", true)) {
                        mo2785a("选择后软件将从当前播放的曲目开始依次播放试听", 1);
                    }
                });
            }
            isLeftHand = findViewById(R.id.check_hand);
            if (f4244U == 10) {
                isLeftHand.setVisibility(View.GONE);
            } else {
                isLeftHand.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked && sharedPreferences.getBoolean("hand_dialog", true)) {
                        mo2785a("选择后您将弹奏曲谱的左手和弦部分，软件将自动播放右手主旋律", 2);
                    }
                });
            }
            TextView f4248d = findViewById(R.id.totle_score_all);
            f4247c.setText("曲谱:" + f4257m);
            f4248d.setText("总分:" + score);
            f4245a.setAdapter(new LocalSongsItemAdapter(this));
            songListView.setCacheColorHint(0);
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
            titleTextView = findViewById(R.id.title_bar);
            titleTextView.setVisibility(View.GONE);
            f4237N = findViewById(R.id.time_text);
            if (f4255k) {
                titleTextView.setVisibility(View.VISIBLE);
            }
            f4249e = findViewById(R.id.show_button);
            f4249e.setVisibility(View.GONE);
            f4249e.setOnClickListener(v -> {
                if (f4255k) {
                    titleTextView.setVisibility(View.GONE);
                    f4255k = false;
                    f4249e.setText("显示标题");
                    return;
                }
                titleTextView.setVisibility(View.GONE);
                f4255k = true;
                f4249e.setText("隐藏标题");
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        sharedPreferences = getSharedPreferences("set", MODE_PRIVATE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
    }

    @Override
    protected void onDestroy() {
        if (playSongs != null) {
            playSongs.isPlayingSongs = false;
            playSongs = null;
        }
        handler = null;
        if (songListAdapter != null && songListAdapter.getCursor() != null) {
            songListAdapter.getCursor().close();
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
        while (!firstLoadFocusFinish) {
            handler = new Handler(this);
            sortNamesList.clear();
            Collections.addAll(sortNamesList, Consts.sortNames);
            View inflate = getLayoutInflater().inflate(R.layout.options, null);
            ListView listView = inflate.findViewById(R.id.list);
            PopupWindowSelectAdapter popupWindowSelectAdapter = new PopupWindowSelectAdapter(this, handler, sortNamesList, 1);
            listView.setAdapter(popupWindowSelectAdapter);
            sortPopupwindow = new PopupWindow(inflate, sortButton.getWidth() + 10, -2, true);
            sortPopupwindow.setOutsideTouchable(true);
            sortPopupwindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.filled_box));

            List<String> menuListNames = new ArrayList<>();
            Collections.addAll(menuListNames, Consts.localMenuListNames);
            inflate = getLayoutInflater().inflate(R.layout.options, null);
            listView = inflate.findViewById(R.id.list);
            popupWindowSelectAdapter = new PopupWindowSelectAdapter(this, handler, menuListNames, 2);
            listView.setAdapter(popupWindowSelectAdapter);
            menuPopupwindow = new PopupWindow(inflate, sortButton.getWidth() + 10, -2, true);
            menuPopupwindow.setOutsideTouchable(true);
            menuPopupwindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.filled_box));
            firstLoadFocusFinish = true;
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
        if (!autoctv.getText().toString().contains("'")) {
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
        str = str.replace("'", "''");
        cursor = sqlitedatabase.query("jp_data", null, "name like '%" + str + "%' AND " + f4238O, null, null, null, sortStr);
        return cursor;
    }
}

