package ly.pp.justpiano3.activity.local;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import ly.pp.justpiano3.JPApplication;
import ly.pp.justpiano3.R;
import ly.pp.justpiano3.activity.BaseActivity;
import ly.pp.justpiano3.activity.settings.SettingsActivity;
import ly.pp.justpiano3.adapter.LocalSongsAdapter;
import ly.pp.justpiano3.adapter.LocalSongsItemAdapter;
import ly.pp.justpiano3.adapter.PopupWindowSelectAdapter;
import ly.pp.justpiano3.constant.Consts;
import ly.pp.justpiano3.database.dao.SongDao;
import ly.pp.justpiano3.database.entity.Song;
import ly.pp.justpiano3.entity.GlobalSetting;
import ly.pp.justpiano3.entity.SongData;
import ly.pp.justpiano3.enums.PlaySongsModeEnum;
import ly.pp.justpiano3.task.LocalDataImportExportTask;
import ly.pp.justpiano3.task.SongSyncTask;
import ly.pp.justpiano3.thread.SongPlay;
import ly.pp.justpiano3.utils.FilePickerUtil;
import ly.pp.justpiano3.utils.FileUtil;
import ly.pp.justpiano3.utils.ImageLoadUtil;
import ly.pp.justpiano3.utils.PmSongUtil;
import ly.pp.justpiano3.utils.ThreadPoolUtil;
import ly.pp.justpiano3.view.JPDialogBuilder;
import ly.pp.justpiano3.view.JPPopupWindow;
import ly.pp.justpiano3.view.JPProgressBar;

public final class MelodySelect extends BaseActivity implements Callback, OnClickListener {
    private SharedPreferences sharedPreferences;
    public JPProgressBar jpProgressBar;
    public String songsPath = "";
    public CheckBox isRecord;
    public CheckBox isLeftHand;
    public CheckBox isAutoPlayNext;
    public CheckBox isPractise;
    private EditText songSearchEditText;
    private Button sortButton;
    private ImageView menuListButton;
    private boolean firstLoadFocusFinish;
    public int orderPosition;
    private int categoryPosition = -1;
    private PopupWindow sortPopupWindow;
    private PopupWindow menuPopupWindow;
    private TextView totalSongCountTextView;
    private TextView totalSongScoreTextView;
    private RecyclerView songsListView;
    private ListView categoryListView;
    public LiveData<PagedList<Song>> pagedListLiveData;
    private final MutableLiveData<SongDao.TotalSongInfo> totalSongInfoMutableLiveData = new MutableLiveData<>();
    public int songsPositionInListView;

    private final ActivityResultLauncher<Intent> midiImportLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() != Activity.RESULT_OK || result.getData() == null) {
                    return;
                }
                List<FileUtil.UriInfo> uriInfoList = FilePickerUtil.getUriFromIntent(this, result.getData());
                if (uriInfoList.size() != 1) {
                    return;
                }
                FileUtil.UriInfo uriInfo = uriInfoList.get(0);
                if (uriInfo.getUri() == null || uriInfo.getDisplayName() == null
                        || !(uriInfo.getDisplayName().endsWith(".mid") || uriInfo.getDisplayName().endsWith(".midi"))) {
                    Toast.makeText(this, "请选择合法的midi格式文件", Toast.LENGTH_SHORT).show();
                    return;
                }
                String songName = uriInfo.getDisplayName().substring(0, uriInfo.getDisplayName().indexOf('.'));
                File pmFile = new File(getFilesDir().getAbsolutePath() + "/ImportSongs/" + songName + ".pm");
                if (pmFile.exists()) {
                    Toast.makeText(this, "不能重复导入曲谱，请删除同名曲谱后再试", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (pmFile.getParentFile() != null && !pmFile.getParentFile().exists()) {
                    pmFile.getParentFile().mkdirs();
                }
                if (uriInfo.getFileSize() != null && uriInfo.getFileSize() > 2 * 1024 * 1024) {
                    Toast.makeText(this, "不接受大小超过2M的midi文件", Toast.LENGTH_SHORT).show();
                    return;
                } else if (uriInfo.getFileSize() != null && uriInfo.getFileSize() > 256 * 1024) {
                    Toast.makeText(this, "midi文件建议小于256KB，文件过大可能导致加载过慢或出现异常", Toast.LENGTH_SHORT).show();
                }
                jpProgressBar.setCancelable(false);
                jpProgressBar.show();
                ThreadPoolUtil.execute(() -> importMidi(this, uriInfo, songName, pmFile));
            });

    private final ActivityResultLauncher<Intent> dbImportLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() != Activity.RESULT_OK || result.getData() == null) {
                    return;
                }
                List<FileUtil.UriInfo> uriInfoList = FilePickerUtil.getUriFromIntent(this, result.getData());
                if (uriInfoList.size() != 1) {
                    return;
                }
                FileUtil.UriInfo uriInfo = uriInfoList.get(0);
                if (uriInfo.getUri() == null || uriInfo.getDisplayName() == null || !uriInfo.getDisplayName().endsWith(".db")) {
                    Toast.makeText(this, "请选择合法的db格式文件", Toast.LENGTH_SHORT).show();
                    return;
                }
                new LocalDataImportExportTask(this, uriInfo.getUri(), false).execute();
            });

    private final ActivityResultLauncher<Intent> dbExportLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() != Activity.RESULT_OK || result.getData() == null) {
                    return;
                }
                FileUtil.UriInfo uriInfo = FileUtil.getFolderUriInfo(this, result.getData().getData());
                if (uriInfo.getUri() == null) {
                    Toast.makeText(this, "导出错误，请提交反馈或联系开发者", Toast.LENGTH_SHORT).show();
                    return;
                }
                new LocalDataImportExportTask(this, uriInfo.getUri(), true).execute();
            });

    private final ActivityResultLauncher<Intent> settingsLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result ->
                    ImageLoadUtil.setBackground(this, GlobalSetting.getBackgroundPic()));

    private void buildDoNotShowDialogAndShow(String message, int i) {
        JPDialogBuilder jpDialogBuilder = new JPDialogBuilder(this);
        jpDialogBuilder.setTitle("提示");
        jpDialogBuilder.setMessage(message);
        jpDialogBuilder.setFirstButton("确定", (dialog, which) -> dialog.dismiss());
        jpDialogBuilder.setSecondButton("不再提示", (dialog, which) -> {
            SharedPreferences.Editor edit = sharedPreferences.edit();
            switch (i) {
                case 0 -> edit.putBoolean("record_dialog", false);
                case 1 -> edit.putBoolean("hand_dialog", false);
                case 2 -> edit.putBoolean("auto_play_next_dialog", false);
                case 3 -> edit.putBoolean("practise_dialog", false);
            }
            edit.apply();
            dialog.dismiss();
        });
        jpDialogBuilder.buildAndShowDialog();
    }

    @Override
    public boolean handleMessage(Message message) {
        Bundle data = message.getData();
        if (message.what == 1) {
            int orderPosition = data.getInt("selIndex");
            SongDao songDao = JPApplication.getSongDatabase().songDao();
            DataSource.Factory<Integer, Song> songsDataSource = songDao.getLocalSongsWithDataSource(categoryPosition, orderPosition);
            pagedListLiveData.removeObservers(this);
            pagedListLiveData = songDao.getPageListByDatasourceFactory(songsDataSource);
            pagedListLiveData.observe(this, ((LocalSongsAdapter) (Objects.requireNonNull(songsListView.getAdapter())))::submitList);
            sortPopupWindow.dismiss();
            this.orderPosition = orderPosition;
        }
        return false;
    }

    private static void importMidi(MelodySelect melodySelect, FileUtil.UriInfo uriInfo, String songName, File pmFile) {
        if (uriInfo.getDisplayName() == null || uriInfo.getUri() == null) {
            return;
        }
        String message = "成功导入曲目《" + songName + "》，可点击“本地导入”分类查看";
        try {
            SongData songData = PmSongUtil.midiFileToPmFile(melodySelect, uriInfo.getDisplayName(), uriInfo.getUri(), pmFile);
            if (!pmFile.exists()) {
                throw new RuntimeException("导入《" + songName + "》失败，请确认midi文件是否合法");
            }
            List<Song> insertSongList = Collections.singletonList(new Song(null, songName, Consts.items[Consts.items.length - 1],
                    "songs/p/" + pmFile.getName(), 1, 0, 0, "",
                    0, 0, 0, songData.getRightHandDegree() / 10f, 0,
                    songData.getLeftHandDegree() / 10f, songData.getLength(), 0));
            JPApplication.getSongDatabase().songDao().insertSongs(insertSongList);
        } catch (Exception e) {
            message = e.getMessage();
            e.printStackTrace();
        } finally {
            String finalMessage = message;
            melodySelect.runOnUiThread(() -> {
                melodySelect.jpProgressBar.dismiss();
                Toast.makeText(melodySelect, finalMessage, Toast.LENGTH_SHORT).show();
                melodySelect.categoryListView.performItemClick(melodySelect.categoryListView.getAdapter()
                                .getView(Consts.items.length - 1, null, null),
                        Consts.items.length - 1, melodySelect.categoryListView.getItemIdAtPosition(Consts.items.length - 1));
            });
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        if (getIntent().getFlags() == (Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK)) {
            intent.setClass(this, MainMode.class);
        } else {
            intent.setClass(this, PlayModeSelect.class);
        }
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.search_fast) {
            SongDao songDao = JPApplication.getSongDatabase().songDao();
            DataSource.Factory<Integer, Song> dataSource = songDao.getSongsByNameKeywordsWithDataSource(songSearchEditText.getText().toString());
            pagedListLiveData.removeObservers(this);
            pagedListLiveData = songDao.getPageListByDatasourceFactory(dataSource);
            pagedListLiveData.observe(this, ((LocalSongsAdapter) (Objects.requireNonNull(songsListView.getAdapter())))::submitList);
            sortButton.setEnabled(false);
        } else if (id == R.id.list_sort_b) {
            PopupWindow sortPopupWindow = new JPPopupWindow(this);
            View songOrderView = LayoutInflater.from(this).inflate(R.layout.lo_songs_order, null);
            sortPopupWindow.setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable._none, getTheme()));
            sortPopupWindow.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
            sortPopupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
            songOrderView.findViewById(R.id.lo_songs_order_name_asc).setOnClickListener(this);
            songOrderView.findViewById(R.id.lo_songs_order_name_des).setOnClickListener(this);
            songOrderView.findViewById(R.id.lo_songs_order_new).setOnClickListener(this);
            songOrderView.findViewById(R.id.lo_songs_order_recent).setOnClickListener(this);
            songOrderView.findViewById(R.id.lo_songs_order_diff_asc).setOnClickListener(this);
            songOrderView.findViewById(R.id.lo_songs_order_diff_des).setOnClickListener(this);
            songOrderView.findViewById(R.id.lo_songs_order_score_asc).setOnClickListener(this);
            songOrderView.findViewById(R.id.lo_songs_order_score_des).setOnClickListener(this);
            songOrderView.findViewById(R.id.lo_songs_order_length_asc).setOnClickListener(this);
            songOrderView.findViewById(R.id.lo_songs_order_length_des).setOnClickListener(this);
            sortPopupWindow.setContentView(songOrderView);
            this.sortPopupWindow = sortPopupWindow;
            sortPopupWindow.showAsDropDown(sortButton, Gravity.CENTER, 0, 0);
        } else if (id == R.id.lo_songs_order_name_asc) {
            songsSort(0);
        } else if (id == R.id.lo_songs_order_name_des) {
            songsSort(1);
        } else if (id == R.id.lo_songs_order_new) {
            songsSort(2);
        } else if (id == R.id.lo_songs_order_recent) {
            songsSort(3);
        } else if (id == R.id.lo_songs_order_diff_asc) {
            songsSort(4);
        } else if (id == R.id.lo_songs_order_diff_des) {
            songsSort(5);
        } else if (id == R.id.lo_songs_order_score_asc) {
            songsSort(6);
        } else if (id == R.id.lo_songs_order_score_des) {
            songsSort(7);
        } else if (id == R.id.lo_songs_order_length_asc) {
            songsSort(8);
        } else if (id == R.id.lo_songs_order_length_des) {
            songsSort(9);
        } else if (id == R.id.menu_list_fast) {
            PopupWindow menuPopupWindow = new JPPopupWindow(this);
            View extraFunctionView = LayoutInflater.from(this).inflate(R.layout.lo_extra_func, null);
            menuPopupWindow.setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable._none, getTheme()));
            menuPopupWindow.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
            menuPopupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
            extraFunctionView.findViewById(R.id.lo_extra_func_settings).setOnClickListener(this);
            extraFunctionView.findViewById(R.id.lo_extra_func_sync).setOnClickListener(this);
            extraFunctionView.findViewById(R.id.lo_extra_func_midi_import).setOnClickListener(this);
            extraFunctionView.findViewById(R.id.lo_extra_func_data_export).setOnClickListener(this);
            menuPopupWindow.setContentView(extraFunctionView);
            this.menuPopupWindow = menuPopupWindow;
            menuPopupWindow.showAsDropDown(menuListButton, Gravity.CENTER, 0, 0);
        } else if (id == R.id.lo_extra_func_settings) {
            menuPopupWindow.dismiss();
            SongPlay.stopPlay();
            settingsLauncher.launch(new Intent(this, SettingsActivity.class));
        } else if (id == R.id.lo_extra_func_sync) {
            menuPopupWindow.dismiss();
            new SongSyncTask(this).execute();
        } else if (id == R.id.lo_extra_func_midi_import) {
            menuPopupWindow.dismiss();
            FilePickerUtil.openFilePicker(this, false, "midi_import", midiImportLauncher);
        } else if (id == R.id.lo_extra_func_data_export) {
            menuPopupWindow.dismiss();
            JPDialogBuilder jpDialogBuilder = new JPDialogBuilder(this);
            jpDialogBuilder.setWidth(500).setTitle("数据导入导出");
            jpDialogBuilder.setVisibleRadioGroup(true).setMessage(
                    "此功能可将本地收藏曲目及所有弹奏分数数据进行导入导出，导入操作会覆盖当前本地收藏及所有弹奏分数，请谨慎操作");
            RadioButton radioButton = new RadioButton(this);
            radioButton.setText("选择文件夹进行数据导出，导出数据文件名：" + LocalDataImportExportTask.EXPORT_FILE_NAME);
            radioButton.setTextSize(13);
            radioButton.setTag(1);
            radioButton.setHeight(150);
            jpDialogBuilder.addRadioButton(radioButton);
            radioButton = new RadioButton(this);
            radioButton.setText("【此操作会覆盖当前数据】选择文件，导入数据至APP");
            radioButton.setTextSize(13);
            radioButton.setTag(2);
            radioButton.setHeight(150);
            jpDialogBuilder.addRadioButton(radioButton).setFirstButton("执行", (dialog, which) -> {
                dialog.dismiss();
                if (jpDialogBuilder.getRadioGroupCheckedId() == 2) {
                    FilePickerUtil.openFilePicker(this, false, "db_import", dbImportLauncher);
                } else if (jpDialogBuilder.getRadioGroupCheckedId() == 1) {
                    FilePickerUtil.openFolderPicker("db_export", dbExportLauncher);
                }
            });
            jpDialogBuilder.setSecondButton("取消", (dialog, which) -> dialog.dismiss());
            jpDialogBuilder.buildAndShowDialog();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        setContentView(LayoutInflater.from(this).inflate(R.layout.lo_melody_list, null));
        SongPlay.setPlaySongsMode(PlaySongsModeEnum.ONCE);
        SongPlay.setCallBack(this::autoPlayNextSong);
        jpProgressBar = new JPProgressBar(this);
        sortButton = findViewById(R.id.list_sort_b);
        sortButton.setOnClickListener(this);
        totalSongCountTextView = findViewById(R.id.all_mel);
        totalSongScoreTextView = findViewById(R.id.total_score_all);
        findViewById(R.id.search_fast).setOnClickListener(this);
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
        isRecord.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked && sharedPreferences.getBoolean("record_dialog", true)) {
                buildDoNotShowDialogAndShow("选择后软件将在开始弹奏时启动内部录音(不含环境音)，弹奏完成时结束录音并存储至文件，存储位置可在设置中指定", 0);
            }
        });
        isLeftHand = findViewById(R.id.check_hand);
        isLeftHand.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked && sharedPreferences.getBoolean("hand_dialog", true)) {
                buildDoNotShowDialogAndShow("选择后您将弹奏曲谱的左手和弦部分，软件将自动播放右手主旋律", 1);
            }
        });
        isAutoPlayNext = findViewById(R.id.check_play_next);
        isAutoPlayNext.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked && sharedPreferences.getBoolean("auto_play_next_dialog", true)) {
                buildDoNotShowDialogAndShow("选择后曲谱将按界面列表中展示的顺序自动播放下一首", 2);
            }
        });
        isPractise = findViewById(R.id.check_practise);
        isPractise.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked && sharedPreferences.getBoolean("practise_dialog", true)) {
                buildDoNotShowDialogAndShow("选择后您在弹奏时，音块下落到判断线会自动暂停，帮助您练习弹奏曲谱", 3);
            }
        });
        categoryListView.setAdapter(new LocalSongsItemAdapter(this));
        categoryListView.setCacheColorHint(Color.TRANSPARENT);
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
        sharedPreferences = getSharedPreferences("set", MODE_PRIVATE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
    }

    private void autoPlayNextSong(String songPath) {
        if (isAutoPlayNext.isChecked()) {
            LocalSongsAdapter localSongsAdapter = (LocalSongsAdapter) (Objects.requireNonNull(songsListView.getAdapter()));
            if (songsPositionInListView >= 0 && songsPositionInListView < localSongsAdapter.getItemCount() - 1) {
                Song song = localSongsAdapter.getSongByPosition(songsPositionInListView + 1);
                if (song != null) {
                    songsPath = song.getFilePath();
                    songsPositionInListView++;
                    SongPlay.startPlay(this, song.getFilePath(), 0);
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        SongPlay.stopPlay();
        SongPlay.setCallBack(null);
        super.onDestroy();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        while (!firstLoadFocusFinish) {
            Handler handler = new Handler(this);
            List<String> sortNamesList = new ArrayList<>();
            Collections.addAll(sortNamesList, Consts.sortNames);
            View optionView = getLayoutInflater().inflate(R.layout.options, null);
            ListView listView = optionView.findViewById(R.id.list);
            listView.setAdapter(new PopupWindowSelectAdapter(this, handler, sortNamesList, 1));
            sortPopupWindow = new JPPopupWindow(optionView, sortButton.getWidth() + 100, ViewGroup.LayoutParams.WRAP_CONTENT, true);
            sortPopupWindow.setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.filled_box, getTheme()));
            List<String> menuListNames = new ArrayList<>();
            Collections.addAll(menuListNames, Consts.localMenuListNames);
            optionView = getLayoutInflater().inflate(R.layout.options, null);
            listView = optionView.findViewById(R.id.list);
            listView.setAdapter(new PopupWindowSelectAdapter(this, handler, menuListNames, 2));
            listView.setDivider(null);
            menuPopupWindow = new JPPopupWindow(optionView, sortButton.getWidth() + 50, ViewGroup.LayoutParams.WRAP_CONTENT, true);
            menuPopupWindow.setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.filled_box, getTheme()));
            firstLoadFocusFinish = true;
        }
    }

    public MutableLiveData<SongDao.TotalSongInfo> getTotalSongInfoMutableLiveData() {
        return totalSongInfoMutableLiveData;
    }

    private void songsSort(int order) {
        SongDao songDao = JPApplication.getSongDatabase().songDao();
        DataSource.Factory<Integer, Song> songsDataSource = songDao.getLocalSongsWithDataSource(categoryPosition, order);
        pagedListLiveData.removeObservers(this);
        pagedListLiveData = songDao.getPageListByDatasourceFactory(songsDataSource);
        pagedListLiveData.observe(this, ((LocalSongsAdapter) (Objects.requireNonNull(songsListView.getAdapter())))::submitList);
        sortPopupWindow.dismiss();
        this.orderPosition = order;
    }
}

