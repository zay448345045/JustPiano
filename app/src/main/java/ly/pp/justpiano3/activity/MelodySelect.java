package ly.pp.justpiano3.activity;

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
import android.widget.*;
import ly.pp.justpiano3.*;
import ly.pp.justpiano3.adapter.LocalSongsAdapter;
import ly.pp.justpiano3.adapter.LocalSongsItemAdapter;
import ly.pp.justpiano3.adapter.MelodySelectAdapter;
import ly.pp.justpiano3.adapter.PopupWindowSelectAdapter;
import ly.pp.justpiano3.constant.Consts;
import ly.pp.justpiano3.helper.SQLiteHelper;
import ly.pp.justpiano3.listener.DialogDismissClick;
import ly.pp.justpiano3.listener.DoNotShowDialogClick;
import ly.pp.justpiano3.task.LocalDataImportExportTask;
import ly.pp.justpiano3.task.SongSyncTask;
import ly.pp.justpiano3.view.JPDialog;
import ly.pp.justpiano3.view.JPProgressBar;
import ly.pp.justpiano3.wrapper.JustPianoCursorWrapper;

import java.text.SimpleDateFormat;
import java.util.*;

public class MelodySelect extends Activity implements Callback, TextWatcher, OnClickListener {
    public final String onlineCondition = "online = 1";

    public Handler handler;
    public SharedPreferences sharedPreferences;
    public LayoutInflater layoutInflater1;
    public LayoutInflater layoutInflater2;
    public JPProgressBar jpprogressBar;
    public ImageView isNewImageView;
    public String songItem = "";
    public String songsPath = "";
    public CheckBox isRecord;
    public CheckBox isFollowPlay;
    public CheckBox isLeftHand;
    public AutoCompleteTextView autoctv;
    public Button showOrHideTitleButton;
    public SQLiteDatabase sqlitedatabase;
    public boolean showTitle;
    public String sortStr = null;
    public String f4263s = "";
    public int f4264t;
    public TextView titleTextView;
    public JPApplication jpapplication;
    public Cursor cursor;
    private Button sortButton;
    private ImageView menuListButton;
    private boolean firstLoadFocusFinish;
    private PopupWindow sortPopupwindow = null;
    private PopupWindow menuPopupwindow = null;
    private TextView timeText;
    private int intentFlag;
    private ListView songListView;
    private LocalSongsAdapter songListAdapter;
    public ly.pp.justpiano3.helper.SQLiteHelper SQLiteHelper;
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

    public final boolean getIsFollowPlay() {
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
                if (!songItem.isEmpty() && songListAdapter != null && sqlitedatabase != null) {
                    mo2784a(sqlitedatabase.query("jp_data", null, "ishot = 0 and " + onlineCondition + " and item=?", new String[]{songItem}, null, null, sortStr));
                }
                sortPopupwindow.dismiss();
                break;
            case 2:
                menuPopupwindow.dismiss();
                jpapplication.stopPlaySong();
                Intent intent = new Intent();
                switch (data.getInt("selIndex")) {
                    case 0:  // 参数设置
                        intent.setClass(this, SettingsMode.class);
                        startActivityForResult(intent, JPApplication.SETTING_MODE_CODE);
                        break;
                    case 1:  // 曲库同步
                        new SongSyncTask(this, OLMainMode.getMaxSongIdFromDatabase(SQLiteHelper)).execute();
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
                        radioButton.setText("导入SD卡\\JustPiano\\local_data.db数据至APP(导入会清除当前数据)");
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
                if (timeText != null) {
                    timeText.setText(format);
                    break;
                }
                break;
            case 4:
                Cursor cursor = songListAdapter.getCursor();
                int i2 = data.getInt("position") + 1;
                songsPath = "";
                if (cursor.moveToPosition(i2)) {
                    String string = cursor.getString(cursor.getColumnIndexOrThrow("path"));
                    String string2 = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                    jpapplication.startPlaySongLocal(string, this, i2);
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
        jpapplication.stopPlaySong();
        Intent intent;
        if (intentFlag == 10) {
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
            case R.id.search_fast:
                autoctv.clearFocus();
                cursor = sqlitedatabase.query("jp_data", null, "name like '%" + autoctv.getText().toString() + "%' AND " + onlineCondition, null, null, null, sortStr);
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
        intentFlag = getIntent().getFlags();
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        sortStr = Consts.sortSyntax[0];
        jpapplication = (JPApplication) getApplication();
        jpprogressBar = new JPProgressBar(this, jpapplication);
        try {
            LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            layoutInflater1 = LayoutInflater.from(this);
            layoutInflater2 = LayoutInflater.from(this);
            SQLiteHelper = new SQLiteHelper(this, "data");
            sqlitedatabase = SQLiteHelper.getWritableDatabase();
            cursor = sqlitedatabase.query("jp_data", null, onlineCondition, null, null, null, null);
            while (cursor.moveToNext()) {
                score += cursor.getInt(cursor.getColumnIndexOrThrow("score"));
            }
            int f4257m = cursor.getCount();
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
            LinearLayout linearLayout = (LinearLayout) layoutInflater.inflate(R.layout.melodylist1, null);
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
            if (intentFlag == 10) {
                isRecord.setVisibility(View.GONE);
            } else {
                isRecord.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked && sharedPreferences.getBoolean("record_dialog", true)) {
                        mo2785a("选择后软件将在开始弹奏时启动录音，弹奏完成时结束录音并存储至文件。录音功能仅录制极品钢琴内弹奏的音频，不含其他后台音频及环境杂音，无需授予录音权限，但需确保授予文件存储权限", 0);
                    }
                });
            }
            isFollowPlay = findViewById(R.id.check_play);
            if (intentFlag == 10) {
                isFollowPlay.setVisibility(View.GONE);
            } else {
                isFollowPlay.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked && sharedPreferences.getBoolean("play_dialog", true)) {
                        mo2785a("选择后软件将从当前播放的曲目开始依次播放试听", 1);
                    }
                });
            }
            isLeftHand = findViewById(R.id.check_hand);
            if (intentFlag == 10) {
                isLeftHand.setVisibility(View.GONE);
            } else {
                isLeftHand.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked && sharedPreferences.getBoolean("hand_dialog", true)) {
                        mo2785a("选择后您将弹奏曲谱的左手和弦部分，软件将自动播放右手主旋律", 2);
                    }
                });
            }
            TextView f4248d = findViewById(R.id.total_score_all);
            f4247c.setText("曲谱:" + f4257m);
            f4248d.setText("总分:" + score);
            f4245a.setAdapter(new LocalSongsItemAdapter(this));
            songListView.setCacheColorHint(0);
            f4245a.setCacheColorHint(0);
            f4245a.setOnItemClickListener((parent, view, position, id) -> {
                Cursor query;
                f4264t = position;
                view.setSelected(true);
                if (position == 0) {
                    songItem = "";
                    query = sqlitedatabase.query("jp_data", null, "isfavo = 1 AND " + onlineCondition, null, null, null, sortStr);
                } else {
                    songItem = Consts.items[position];
                    query = sqlitedatabase.query("jp_data", null, "ishot = 0 AND " + onlineCondition + " AND item=?", new String[]{songItem}, null, null, sortStr);
                }
                mo2784a(query);
                f4263s = Consts.items[position];
            });
            titleTextView = findViewById(R.id.title_bar);
            titleTextView.setVisibility(View.GONE);
            timeText = findViewById(R.id.time_text);
            if (showTitle) {
                titleTextView.setVisibility(View.VISIBLE);
            }
            showOrHideTitleButton.setOnClickListener(v -> {
                if (showTitle) {
                    titleTextView.setVisibility(View.GONE);
                    showTitle = false;
                    showOrHideTitleButton.setText("显示标题");
                    return;
                }
                titleTextView.setVisibility(View.GONE);
                showTitle = true;
                showOrHideTitleButton.setText("隐藏标题");
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
        jpapplication.stopPlaySong();
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
            SQLiteHelper.close();
            SQLiteHelper = null;
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
            sortPopupwindow = new PopupWindow(inflate, sortButton.getWidth() + 20, -2, true);
            sortPopupwindow.setOutsideTouchable(true);
            sortPopupwindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.filled_box));
            List<String> menuListNames = new ArrayList<>();
            Collections.addAll(menuListNames, Consts.localMenuListNames);
            inflate = getLayoutInflater().inflate(R.layout.options, null);
            listView = inflate.findViewById(R.id.list);
            popupWindowSelectAdapter = new PopupWindowSelectAdapter(this, handler, menuListNames, 2);
            listView.setAdapter(popupWindowSelectAdapter);
            listView.setDivider(null);
            menuPopupwindow = new PopupWindow(inflate, sortButton.getWidth() + 20, -2, true);
            menuPopupwindow.setOutsideTouchable(true);
            menuPopupwindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.filled_box));
            firstLoadFocusFinish = true;
        }
    }

    private void initAutoComplete(AutoCompleteTextView autoCompleteTextView) {
        String valueOf = autoCompleteTextView.getText().toString();
        cursor = sqlitedatabase.query("jp_data", null, "name like '%" + valueOf.replace("'", "''") + "%' AND " + onlineCondition, null, null, null, sortStr);
        autoCompleteTextHandle(autoCompleteTextView);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        cursor = sqlitedatabase.query("jp_data", null, "name like '%" + s.toString().replace("'", "''") + "%' AND " + onlineCondition, null, null, null, sortStr);
        autoCompleteTextHandle(autoctv);
    }

    private void autoCompleteTextHandle(AutoCompleteTextView autoctv) {
        MelodySelectAdapter adapter = new MelodySelectAdapter(this, cursor, this);
        autoctv.setAdapter(adapter);
        autoctv.setOnFocusChangeListener((v, hasFocus) -> {
            AutoCompleteTextView view = (AutoCompleteTextView) v;
            if (hasFocus) {
                try {
                    view.showDropDown();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public Cursor search(String str) {
        str = str.replace("'", "''");
        cursor = sqlitedatabase.query("jp_data", null, "name like '%" + str + "%' AND " + onlineCondition, null, null, null, sortStr);
        return cursor;
    }
}

