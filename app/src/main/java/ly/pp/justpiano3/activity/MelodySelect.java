package ly.pp.justpiano3.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;
import androidx.activity.ComponentActivity;
import androidx.core.content.res.ResourcesCompat;
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
import ly.pp.justpiano3.entity.SongData;
import ly.pp.justpiano3.task.LocalDataImportExportTask;
import ly.pp.justpiano3.task.SongSyncTask;
import ly.pp.justpiano3.thread.SongPlay;
import ly.pp.justpiano3.utils.FilePickerUtil;
import ly.pp.justpiano3.utils.ImageLoadUtil;
import ly.pp.justpiano3.utils.SongUtil;
import ly.pp.justpiano3.view.JPDialogBuilder;
import ly.pp.justpiano3.view.JPProgressBar;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

public class MelodySelect extends ComponentActivity implements Callback, OnClickListener {
    public Handler handler;
    public SharedPreferences sharedPreferences;
    public LayoutInflater layoutInflater1;
    public LayoutInflater layoutInflater2;
    public JPProgressBar jpprogressBar;
    public String songsPath = "";
    public CheckBox isRecord;
    public CheckBox isLeftHand;
    public EditText songSearchEditText;
    public JPApplication jpapplication;
    private Button sortButton;
    private ImageView menuListButton;
    private boolean firstLoadFocusFinish;
    public int orderPosition;
    private int categoryPosition = -1;
    private PopupWindow sortPopupWindow;
    private PopupWindow menuPopupWindow;
    private TextView timeText;
    private TextView totalSongCountTextView;
    private TextView totalSongScoreTextView;
    private RecyclerView songsListView;
    private ListView categoryListView;
    public LiveData<PagedList<Song>> pagedListLiveData;
    private final MutableLiveData<SongDao.TotalSongInfo> totalSongInfoMutableLiveData = new MutableLiveData<>();

    protected final void buildDoNotShowDialogAndShow(String message, int i) {
        JPDialogBuilder jpDialogBuilder = new JPDialogBuilder(this);
        jpDialogBuilder.setTitle("提示");
        jpDialogBuilder.setMessage(message);
        jpDialogBuilder.setFirstButton("确定", ((dialog, which) -> dialog.dismiss()));
        jpDialogBuilder.setSecondButton("不再提示", (dialog, which) -> {
            SharedPreferences.Editor edit = sharedPreferences.edit();
            switch (i) {
                case 0:
                    edit.putBoolean("record_dialog", false);
                    break;
                case 1:
                    edit.putBoolean("hand_dialog", false);
                    break;
            }
            edit.apply();
            dialog.dismiss();
        });
        jpDialogBuilder.buildAndShowDialog();
    }

    @Override
    public boolean handleMessage(Message message) {
        Bundle data = message.getData();
        switch (message.what) {
            case 1:
                int orderPosition = data.getInt("selIndex");
                sortButton.setText(Consts.sortNames[orderPosition]);
                SongDao songDao = JPApplication.getSongDatabase().songDao();
                DataSource.Factory<Integer, Song> songsDataSource = songDao.getLocalSongsWithDataSource(categoryPosition, orderPosition);
                pagedListLiveData.removeObservers(this);
                pagedListLiveData = songDao.getPageListByDatasourceFactory(songsDataSource);
                pagedListLiveData.observe(this, ((LocalSongsAdapter) (Objects.requireNonNull(songsListView.getAdapter())))::submitList);
                sortPopupWindow.dismiss();
                this.orderPosition = orderPosition;
                break;
            case 2:
                menuPopupWindow.dismiss();
                SongPlay.INSTANCE.stopPlay();
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
                        JPDialogBuilder jpDialogBuilder = new JPDialogBuilder(this);
                        jpDialogBuilder.setTitle("数据导入导出");
                        jpDialogBuilder.setMessage("此功能可将本地收藏夹、弹奏分数数据进行导入导出，导出路径为SD卡\\JustPiano\\local_data.db。导入文件后将清除当前本地曲谱分数及收藏数据，请谨慎操作");
                        jpDialogBuilder.setVisibleRadioGroup(true);
                        RadioButton radioButton = new RadioButton(this);
                        radioButton.setText("APP本地收藏夹、弹奏分数数据导出至SD卡\\JustPiano\\local_data.db");
                        radioButton.setTextSize(13);
                        radioButton.setTag(1);
                        radioButton.setHeight(100);
                        jpDialogBuilder.addRadioButton(radioButton);
                        radioButton = new RadioButton(this);
                        radioButton.setText("导入SD卡\\JustPiano\\local_data.db数据至APP(导入会清除当前数据)");
                        radioButton.setTextSize(13);
                        radioButton.setTag(2);
                        radioButton.setHeight(100);
                        jpDialogBuilder.addRadioButton(radioButton);
                        jpDialogBuilder.setFirstButton("执行", (dialog, which) -> {
                            dialog.dismiss();
                            new LocalDataImportExportTask(this, jpDialogBuilder.getRadioGroupCheckedId()).execute();
                        });
                        jpDialogBuilder.setSecondButton("取消", ((dialog, which) -> dialog.dismiss()));
                        jpDialogBuilder.buildAndShowDialog();
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
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == JPApplication.SETTING_MODE_CODE) {
            ImageLoadUtil.setBackGround(this, "ground", findViewById(R.id.layout));
        } else if (requestCode == FilePickerUtil.PICK_FILE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            List<Song> insertSongList = new ArrayList<>();
            List<File> fileList = FilePickerUtil.getFilesFromIntent(this, data);
            if (fileList.isEmpty()) {
                return;
            }
            File midiFile = fileList.get(0);
            if (!midiFile.exists() || !(midiFile.getName().endsWith(".mid") || midiFile.getName().endsWith(".midi"))) {
                Toast.makeText(this, "请选择合法的midi格式文件", Toast.LENGTH_SHORT).show();
                return;
            }
            String songName = midiFile.getName().substring(0, midiFile.getName().indexOf('.'));
            if (midiFile.length() > 1048576) {
                Toast.makeText(this, "midi文件大小必须小于1M", Toast.LENGTH_SHORT).show();
                return;
            }
            File pmFile = new File(getFilesDir().getAbsolutePath() + "/Songs/" + songName + ".pm");
            if (pmFile.exists()) {
                Toast.makeText(this, "不能重复导入曲谱，请删除同名曲谱后再试", Toast.LENGTH_SHORT).show();
                return;
            }
            SongData songData = null;
            try {
                songData = SongUtil.midiFileToPmFile(midiFile, pmFile);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (!pmFile.exists() || songData == null) {
                Toast.makeText(this, "导入《" + songName + "》失败，请确认midi文件是否合法", Toast.LENGTH_SHORT).show();
                return;
            }
            insertSongList.add(new Song(null, songName, Consts.items[Consts.items.length - 1],
                    "songs/p/" + pmFile.getName(), 1, 0, 0, "",
                    0, 0, 0, songData.getDegree() / 10f, 0,
                    songData.getLeftDegree() / 10f, songData.getLength(), 0));
            JPApplication.getSongDatabase().songDao().insertSongs(insertSongList);
            Toast.makeText(this, "成功导入曲目《" + songName + "》，可点击\"本地导入\"分类查看", Toast.LENGTH_SHORT).show();
            categoryListView.performItemClick(categoryListView.getAdapter().getView(Consts.items.length - 1, null, null),
                    Consts.items.length - 1, categoryListView.getItemIdAtPosition(Consts.items.length - 1));
        }
    }

    @Override
    public void onBackPressed() {
        SongPlay.INSTANCE.stopPlay();
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
            case R.id.search_fast:
                SongDao songDao = JPApplication.getSongDatabase().songDao();
                DataSource.Factory<Integer, Song> dataSource = songDao.getSongsByNameKeywordsWithDataSource(songSearchEditText.getText().toString());
                pagedListLiveData.removeObservers(this);
                pagedListLiveData = songDao.getPageListByDatasourceFactory(dataSource);
                pagedListLiveData.observe(this, ((LocalSongsAdapter) (Objects.requireNonNull(songsListView.getAdapter())))::submitList);
                sortButton.setEnabled(false);
                return;
            case R.id.list_sort_b:
                if (firstLoadFocusFinish) {
                    sortPopupWindow.showAsDropDown(sortButton);
                }
                return;
            case R.id.local_import:
                FilePickerUtil.openFileManager(this, false);
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        jpapplication = (JPApplication) getApplication();
        jpprogressBar = new JPProgressBar(this, jpapplication);
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutInflater1 = LayoutInflater.from(this);
        layoutInflater2 = LayoutInflater.from(this);
        LinearLayout linearLayout = (LinearLayout) layoutInflater.inflate(R.layout.melodylist1, null);
        setContentView(linearLayout);
        ImageLoadUtil.setBackGround(this, "ground", linearLayout);
        sortButton = findViewById(R.id.list_sort_b);
        sortButton.setOnClickListener(this);
        findViewById(R.id.local_import).setOnClickListener(this);
        totalSongCountTextView = findViewById(R.id.all_mel);
        totalSongScoreTextView = findViewById(R.id.total_score_all);
        ImageView searchFastButton = findViewById(R.id.search_fast);
        searchFastButton.setOnClickListener(this);
        SongDao songDao = JPApplication.getSongDatabase().songDao();
        List<SongDao.TotalSongInfo> allSongsCountAndScore = songDao.getAllSongsCountAndScore();
        totalSongInfoMutableLiveData.setValue(allSongsCountAndScore.get(0));
        totalSongInfoMutableLiveData.observe(this, totalSongInfo -> {
            totalSongCountTextView.setText("曲谱:" + totalSongInfo.getTotalCount());
            totalSongScoreTextView.setText("总分:" + totalSongInfo.getTotalScore());
        });
        categoryListView = findViewById(R.id.f_list);
        songsListView = findViewById(R.id.c_list);
        songsListView.setLayoutManager(new LinearLayoutManager(this));
        LocalSongsAdapter localSongsAdapter = new LocalSongsAdapter(this, songsListView);
        songsListView.setAdapter(localSongsAdapter);
        DataSource.Factory<Integer, Song> allSongs = songDao.getLocalSongsWithDataSource(-1, 0);
        pagedListLiveData = songDao.getPageListByDatasourceFactory(allSongs);
        pagedListLiveData.observe(this, localSongsAdapter::submitList);
        songSearchEditText = findViewById(R.id.search_edit);
        menuListButton = findViewById(R.id.menu_list_fast);
        menuListButton.setOnClickListener(this);
        isRecord = findViewById(R.id.check_record);
        if (getIntent().getFlags() == (Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK)) {
            isRecord.setVisibility(View.GONE);
        } else {
            isRecord.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked && sharedPreferences.getBoolean("record_dialog", true)) {
                    buildDoNotShowDialogAndShow("选择后软件将在开始弹奏时启动录音，弹奏完成时结束录音并存储至文件。" +
                            "录音功能仅录制极品钢琴内弹奏的音频，不含其他后台音频及环境杂音，无需授予录音权限，但需确保授予文件存储权限", 0);
                }
            });
        }
        isLeftHand = findViewById(R.id.check_hand);
        if (getIntent().getFlags() == (Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK)) {
            isLeftHand.setVisibility(View.GONE);
        } else {
            isLeftHand.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked && sharedPreferences.getBoolean("hand_dialog", true)) {
                    buildDoNotShowDialogAndShow("选择后您将弹奏曲谱的左手和弦部分，软件将自动播放右手主旋律", 1);
                }
            });
        }
        categoryListView.setAdapter(new LocalSongsItemAdapter(this));
        categoryListView.setCacheColorHint(0);
        categoryListView.setOnItemClickListener((parent, view, position, id) -> {
            view.setSelected(true);
            if (position == Consts.items.length - 1) {
                // 加载本地导入的曲谱
                sortButton.setEnabled(false);
                DataSource.Factory<Integer, Song> dataSource = songDao.getLocalImportSongs();
                LiveData<PagedList<Song>> liveData = songDao.getPageListByDatasourceFactory(dataSource);
                liveData.observe(this, ((LocalSongsAdapter) (Objects.requireNonNull(songsListView.getAdapter())))::submitList);
            } else {
                sortButton.setEnabled(true);
                DataSource.Factory<Integer, Song> dataSource = songDao.getLocalSongsWithDataSource(position, orderPosition);
                LiveData<PagedList<Song>> liveData = songDao.getPageListByDatasourceFactory(dataSource);
                liveData.observe(this, ((LocalSongsAdapter) (Objects.requireNonNull(songsListView.getAdapter())))::submitList);
                categoryPosition = position;
            }
        });
        timeText = findViewById(R.id.time_text);
        sharedPreferences = getSharedPreferences("set", MODE_PRIVATE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
    }

    @Override
    protected void onDestroy() {
        SongPlay.INSTANCE.stopPlay();
        handler = null;
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
        isRecord.setChecked(false);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
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
            sortPopupWindow.setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.filled_box, getTheme()));
            List<String> menuListNames = new ArrayList<>();
            Collections.addAll(menuListNames, Consts.localMenuListNames);
            inflate = getLayoutInflater().inflate(R.layout.options, null);
            listView = inflate.findViewById(R.id.list);
            popupWindowSelectAdapter = new PopupWindowSelectAdapter(this, handler, menuListNames, 2);
            listView.setAdapter(popupWindowSelectAdapter);
            listView.setDivider(null);
            menuPopupWindow = new PopupWindow(inflate, sortButton.getWidth() + 20, -2, true);
            menuPopupWindow.setOutsideTouchable(true);
            menuPopupWindow.setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.filled_box, getTheme()));
            firstLoadFocusFinish = true;
        }
    }

    public MutableLiveData<SongDao.TotalSongInfo> getTotalSongInfoMutableLiveData() {
        return totalSongInfoMutableLiveData;
    }
}

