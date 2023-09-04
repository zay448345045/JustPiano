package ly.pp.justpiano3.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.*;
import androidx.activity.ComponentActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ly.pp.justpiano3.JPApplication;
import ly.pp.justpiano3.R;
import ly.pp.justpiano3.adapter.LocalSongsAdapter;
import ly.pp.justpiano3.adapter.LocalSongsItemAdapter;
import ly.pp.justpiano3.adapter.PopupWindowSelectAdapter;
import ly.pp.justpiano3.constant.Consts;
import ly.pp.justpiano3.database.dao.SongDao;
import ly.pp.justpiano3.database.entity.Song;
import ly.pp.justpiano3.listener.DialogDismissClick;
import ly.pp.justpiano3.listener.DoNotShowDialogClick;
import ly.pp.justpiano3.task.LocalDataImportExportTask;
import ly.pp.justpiano3.task.SongSyncTask;
import ly.pp.justpiano3.utils.SkinImageLoadUtil;
import ly.pp.justpiano3.view.JPDialog;
import ly.pp.justpiano3.view.JPProgressBar;

import java.text.SimpleDateFormat;
import java.util.*;

public class MelodySelect extends ComponentActivity implements Callback, TextWatcher, OnClickListener {
    public Handler handler;
    public SharedPreferences sharedPreferences;
    public LayoutInflater layoutInflater1;
    public LayoutInflater layoutInflater2;
    public JPProgressBar jpprogressBar;
    public String songsPath = "";
    public CheckBox isRecord;
    public CheckBox isFollowPlay;
    public CheckBox isLeftHand;
    public EditText songSearchEditText;
    public JPApplication jpapplication;
    private Button sortButton;
    private ImageView menuListButton;
    private boolean firstLoadFocusFinish;
    private int orderPosition;
    private int categoryPosition;
    private PopupWindow sortPopupWindow;
    private PopupWindow menuPopupWindow;
    private TextView timeText;
    private TextView totalSongCountTextView;
    private TextView totalSongScoreTextView;
    private RecyclerView songsListView;
    private LiveData<PagedList<Song>> pagedListLiveData;
    private final MutableLiveData<SongDao.TotalSongInfo> totalSongInfoMutableLiveData = new MutableLiveData<>();

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
                sortButton.setText(Consts.sortNames[i]);
                SongDao songDao = JPApplication.getSongDatabase().songDao();
                DataSource.Factory<Integer, Song> songsDataSource = songDao.getOrderedSongsByCategoryWithDataSource(Consts.items[categoryPosition], i);
                pagedListLiveData = songDao.getPageListByDatasourceFactory(songsDataSource);
                pagedListLiveData.observe(this, ((LocalSongsAdapter) (Objects.requireNonNull(songsListView.getAdapter())))::submitList);
                sortPopupWindow.dismiss();
                orderPosition = i;
                break;
            case 2:
                menuPopupWindow.dismiss();
                jpapplication.stopPlaySong();
                Intent intent = new Intent();
                switch (data.getInt("selIndex")) {
                    case 0:  // 参数设置
                        intent.setClass(this, SettingsMode.class);
                        startActivityForResult(intent, JPApplication.SETTING_MODE_CODE);
                        break;
                    case 1:  // 曲库同步
                        new SongSyncTask(this, OLMainMode.getMaxSongIdFromDatabase()).execute();
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
                // 本地连续播放功能暂时取消
//                Cursor cursor = songListAdapter.getCursor();
//                int i2 = data.getInt("position") + 1;
//                songsPath = "";
//                if (cursor.moveToPosition(i2)) {
//                    String string = cursor.getString(cursor.getColumnIndexOrThrow("path"));
//                    String string2 = cursor.getString(cursor.getColumnIndexOrThrow("name"));
//                    jpapplication.startPlaySongLocal(string, this);
//                    Toast.makeText(this, "正在播放:《" + string2 + "》", Toast.LENGTH_SHORT).show();
//                    break;
//                }
                break;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == JPApplication.SETTING_MODE_CODE) {
            SkinImageLoadUtil.setBackGround(this, "ground", findViewById(R.id.layout));
        }
    }

    @Override
    public void onBackPressed() {
        jpapplication.stopPlaySong();
        Intent intent;
        if (getIntent().getFlags() == (Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK)) {
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
                    sortPopupWindow.showAsDropDown(sortButton);
                }
                return;
            case R.id.menu_list_fast:
                if (firstLoadFocusFinish) {
                    menuPopupWindow.showAsDropDown(menuListButton);
                }
                return;
            default:
        }
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        jpapplication = (JPApplication) getApplication();
        jpprogressBar = new JPProgressBar(this, jpapplication);
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutInflater1 = LayoutInflater.from(this);
        layoutInflater2 = LayoutInflater.from(this);
        LinearLayout linearLayout = (LinearLayout) layoutInflater.inflate(R.layout.melodylist1, null);
        setContentView(linearLayout);
        SkinImageLoadUtil.setBackGround(this, "ground", linearLayout);
        sortButton = findViewById(R.id.list_sort_b);
        sortButton.setOnClickListener(this);
        sortButton.setEnabled(false);
        totalSongCountTextView = findViewById(R.id.all_mel);
        totalSongScoreTextView = findViewById(R.id.total_score_all);
        List<SongDao.TotalSongInfo> allSongsCountAndScore = JPApplication.getSongDatabase().songDao().getAllSongsCountAndScore();
        totalSongInfoMutableLiveData.setValue(allSongsCountAndScore.get(0));
        totalSongInfoMutableLiveData.observe(this, totalSongInfo -> {
            totalSongCountTextView.setText("曲谱:" + totalSongInfo.getTotalCount());
            totalSongScoreTextView.setText("总分:" + totalSongInfo.getTotalScore());
        });
        ListView categoryListView = findViewById(R.id.f_list);
        songsListView = findViewById(R.id.c_list);
        songsListView.setLayoutManager(new LinearLayoutManager(this));
        LocalSongsAdapter localSongsAdapter = new LocalSongsAdapter(this);
        songsListView.setAdapter(localSongsAdapter);
        SongDao songDao = JPApplication.getSongDatabase().songDao();
        DataSource.Factory<Integer, Song> allSongs = songDao.getAllSongsWithDataSource();
        pagedListLiveData = songDao.getPageListByDatasourceFactory(allSongs);
        pagedListLiveData.observe(this, localSongsAdapter::submitList);
        songSearchEditText = findViewById(R.id.search_edit);
        songSearchEditText.addTextChangedListener(this);
        menuListButton = findViewById(R.id.menu_list_fast);
        menuListButton.setOnClickListener(this);
        isRecord = findViewById(R.id.check_record);
        if (getIntent().getFlags() == (Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK)) {
            isRecord.setVisibility(View.GONE);
        } else {
            isRecord.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked && sharedPreferences.getBoolean("record_dialog", true)) {
                    mo2785a("选择后软件将在开始弹奏时启动录音，弹奏完成时结束录音并存储至文件。" +
                            "录音功能仅录制极品钢琴内弹奏的音频，不含其他后台音频及环境杂音，无需授予录音权限，但需确保授予文件存储权限", 0);
                }
            });
        }
        isFollowPlay = findViewById(R.id.check_play);
        if (getIntent().getFlags() == (Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK)) {
            isFollowPlay.setVisibility(View.GONE);
        } else {
            isFollowPlay.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked && sharedPreferences.getBoolean("play_dialog", true)) {
                    mo2785a("抱歉，连续播放功能暂时取消", 1);
                }
            });
        }
        isLeftHand = findViewById(R.id.check_hand);
        if (getIntent().getFlags() == (Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK)) {
            isLeftHand.setVisibility(View.GONE);
        } else {
            isLeftHand.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked && sharedPreferences.getBoolean("hand_dialog", true)) {
                    mo2785a("选择后您将弹奏曲谱的左手和弦部分，软件将自动播放右手主旋律", 2);
                }
            });
        }
        categoryListView.setAdapter(new LocalSongsItemAdapter(this));
        categoryListView.setCacheColorHint(0);
        categoryListView.setOnItemClickListener((parent, view, position, id) -> {
            view.setSelected(true);
            sortButton.setEnabled(position != 0);
            if (position == 0) {
                DataSource.Factory<Integer, Song> favoriteSongsWithDataSource = songDao.getFavoriteSongsWithDataSource();
                pagedListLiveData = songDao.getPageListByDatasourceFactory(favoriteSongsWithDataSource);
            } else {
                DataSource.Factory<Integer, Song> dataSource = songDao.getOrderedSongsByCategoryWithDataSource(Consts.items[position], orderPosition);
                pagedListLiveData = songDao.getPageListByDatasourceFactory(dataSource);
            }
            pagedListLiveData.observe(this, ((LocalSongsAdapter) (Objects.requireNonNull(songsListView.getAdapter())))::submitList);
            categoryPosition = position;
        });
        timeText = findViewById(R.id.time_text);
        sharedPreferences = getSharedPreferences("set", MODE_PRIVATE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
    }

    @Override
    protected void onDestroy() {
        jpapplication.stopPlaySong();
        handler = null;
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
            List<String> sortNamesList = new ArrayList<>();
            Collections.addAll(sortNamesList, Consts.sortNames);
            View inflate = getLayoutInflater().inflate(R.layout.options, null);
            ListView listView = inflate.findViewById(R.id.list);
            PopupWindowSelectAdapter popupWindowSelectAdapter = new PopupWindowSelectAdapter(this, handler, sortNamesList, 1);
            listView.setAdapter(popupWindowSelectAdapter);
            sortPopupWindow = new PopupWindow(inflate, sortButton.getWidth() + 20, -2, true);
            sortPopupWindow.setOutsideTouchable(true);
            sortPopupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.filled_box));
            List<String> menuListNames = new ArrayList<>();
            Collections.addAll(menuListNames, Consts.localMenuListNames);
            inflate = getLayoutInflater().inflate(R.layout.options, null);
            listView = inflate.findViewById(R.id.list);
            popupWindowSelectAdapter = new PopupWindowSelectAdapter(this, handler, menuListNames, 2);
            listView.setAdapter(popupWindowSelectAdapter);
            listView.setDivider(null);
            menuPopupWindow = new PopupWindow(inflate, sortButton.getWidth() + 20, -2, true);
            menuPopupWindow.setOutsideTouchable(true);
            menuPopupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.filled_box));
            firstLoadFocusFinish = true;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        SongDao songDao = JPApplication.getSongDatabase().songDao();
        DataSource.Factory<Integer, Song> dataSource = songDao.getSongsByNameKeywordsWithDataSource(s.toString());
        pagedListLiveData = songDao.getPageListByDatasourceFactory(dataSource);
        pagedListLiveData.observe(this, ((LocalSongsAdapter) (Objects.requireNonNull(songsListView.getAdapter())))::submitList);
    }

    public MutableLiveData<SongDao.TotalSongInfo> getTotalSongInfoMutableLiveData() {
        return totalSongInfoMutableLiveData;
    }
}

