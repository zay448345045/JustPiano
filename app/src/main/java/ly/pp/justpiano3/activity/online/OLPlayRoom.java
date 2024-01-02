package ly.pp.justpiano3.activity.online;

import android.content.Intent;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.LiveData;
import androidx.paging.DataSource;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import ly.pp.justpiano3.JPApplication;
import ly.pp.justpiano3.R;
import ly.pp.justpiano3.activity.local.SettingsMode;
import ly.pp.justpiano3.adapter.OLRoomSongsAdapter;
import ly.pp.justpiano3.adapter.PlayerImageAdapter;
import ly.pp.justpiano3.constant.Consts;
import ly.pp.justpiano3.constant.OnlineProtocolType;
import ly.pp.justpiano3.database.dao.SongDao;
import ly.pp.justpiano3.database.entity.Song;
import ly.pp.justpiano3.enums.PlaySongsModeEnum;
import ly.pp.justpiano3.enums.RoomModeEnum;
import ly.pp.justpiano3.handler.android.OLPlayRoomHandler;
import ly.pp.justpiano3.thread.SongPlay;
import ly.pp.justpiano3.utils.ImageLoadUtil;
import ly.pp.justpiano3.utils.UnitConvertUtil;
import ly.pp.justpiano3.view.JPDialogBuilder;
import ly.pp.justpiano3.view.JPPopupWindow;
import ly.pp.justpiano3.view.ScrollText;
import protobuf.dto.OnlineChangeRoomHandDTO;
import protobuf.dto.OnlineChangeRoomUserStatusDTO;
import protobuf.dto.OnlineCoupleDTO;
import protobuf.dto.OnlineLoadRoomPositionDTO;
import protobuf.dto.OnlinePlaySongDTO;
import protobuf.dto.OnlinePlayStartDTO;

public final class OLPlayRoom extends OLRoomActivity {
    public OLPlayRoomHandler olPlayRoomHandler = new OLPlayRoomHandler(this);
    public LiveData<PagedList<Song>> pagedListLiveData;
    public String currentPlaySongPath;
    public ScrollText songNameScrollText;
    public Button settingButton;
    public Button playButton;
    public int currentHand;
    private int tune;
    private Button playSongsModeButton;
    private TextView searchTextView;
    private PopupWindow normalModePopupWindow;
    private PopupWindow moreSongsPopupWindow;
    private PopupWindow groupModePopupWindow;
    private PopupWindow coupleModePopupWindow;
    private PopupWindow playSongsModePopupWindow;
    private RecyclerView songsRecyclerView;

    private void playSongByDegreeRandom(int startDegree, int endDegree) {
        List<Song> songs = JPApplication.getSongDatabase().songDao().getSongByRightHandDegreeWithRandom(startDegree, endDegree);
        String songFilePath = songs.isEmpty() ? "" : songs.get(0).getFilePath();
        currentPlaySongPath = songFilePath;
        OnlinePlaySongDTO.Builder builder = OnlinePlaySongDTO.newBuilder();
        builder.setTune(tune);
        builder.setSongPath(songFilePath.substring(6, songFilePath.length() - 3));
        sendMsg(OnlineProtocolType.PLAY_SONG, builder.build());
        if (moreSongsPopupWindow != null) {
            moreSongsPopupWindow.dismiss();
        }
    }

    public void setGroupOrHand(int i, PopupWindow popupWindow) {
        OnlineChangeRoomHandDTO.Builder builder = OnlineChangeRoomHandDTO.newBuilder();
        builder.setHand(i);
        sendMsg(OnlineProtocolType.CHANGE_ROOM_HAND, builder.build());
        if (popupWindow != null) {
            popupWindow.dismiss();
        }
    }

    private void randomSongWithDegree(int i) {
        roomTabs.setCurrentTab(2);
        SongDao songDao = JPApplication.getSongDatabase().songDao();
        DataSource.Factory<Integer, Song> songsByCategoryWithDataSource = songDao.getSongsByCategoriesWithDataSource(Consts.items[i + 1]);
        pagedListLiveData.removeObservers(this);
        pagedListLiveData = songDao.getPageListByDatasourceFactory(songsByCategoryWithDataSource);
        pagedListLiveData.observe(this, ((OLRoomSongsAdapter) (Objects.requireNonNull(songsRecyclerView.getAdapter())))::submitList);
        moreSongsPopupWindow.dismiss();
    }

    private void randomSongBetweenTwoDegree(int i, int j) {
        roomTabs.setCurrentTab(2);
        SongDao songDao = JPApplication.getSongDatabase().songDao();
        DataSource.Factory<Integer, Song> songsByCategoryWithDataSource = songDao.getSongsByCategoriesWithDataSource(Consts.items[i + 1], Consts.items[j + 1]);
        pagedListLiveData.removeObservers(this);
        pagedListLiveData = songDao.getPageListByDatasourceFactory(songsByCategoryWithDataSource);
        pagedListLiveData.observe(this, ((OLRoomSongsAdapter) (Objects.requireNonNull(songsRecyclerView.getAdapter())))::submitList);
        moreSongsPopupWindow.dismiss();
    }

    public void buildNewCoupleDialog(int dialogType, String message, int coupleType, byte couplePosition) {
        String title;
        String buttonText;
        String coupleTypeText = "情意绵绵的情侣";
        switch (coupleType) {
            case 0 -> {
                return;
            }
            case 1 -> coupleTypeText = "情意绵绵的情侣";
            case 2 -> coupleTypeText = "基情四射的基友";
            case 3 -> coupleTypeText = "百年好合的百合";
        }
        if (dialogType == 1) {
            title = "搭档请求";
            buttonText = "邀请";
            message = "您与[" + message + "]在双人模式中最高连击总和超过200,是否邀请对方结为一对" + coupleTypeText + '?';
        } else if (dialogType == 2) {
            title = "搭档请求";
            buttonText = "同意";
            message = message + "请求与您结为一对" + coupleTypeText + ",是否同意?";
        } else if (dialogType == 3) {
            title = "喜告";
            buttonText = "确定";
            message = "祝贺" + message + "成为一对" + coupleTypeText + '!';
        } else if (dialogType == 4) {
            showCpDialog(coupleTypeText.substring(coupleTypeText.length() - 2) + "证书", message);
            return;
        } else if (dialogType == 5) {
            title = "提示";
            buttonText = "确定";
        } else {
            return;
        }
        JPDialogBuilder jpDialogBuilder = new JPDialogBuilder(this);
        jpDialogBuilder.setCancelableFalse();
        jpDialogBuilder.setTitle(title).setMessage(message).setFirstButton(buttonText, (dialog, which) -> {
            if (dialogType == 1 || dialogType == 2) {
                OnlineCoupleDTO.Builder builder = OnlineCoupleDTO.newBuilder();
                builder.setType(dialogType + 1);
                builder.setCoupleType(coupleType);
                builder.setCoupleRoomPosition(couplePosition);
                sendMsg(OnlineProtocolType.COUPLE, builder.build());
            }
            dialog.dismiss();
        }).setSecondButton("取消", (dialog, which) -> dialog.dismiss()).buildAndShowDialog();
    }

    public void updatePlayerList(GridView gridView, Bundle bundle) {
        playerList.clear();
        if (bundle != null) {
            int size = bundle.size() - 6;
            for (int i = 0; i < size; i++) {
                playerList.add(bundle.getBundle(String.valueOf(i)));
            }
            List<Bundle> list = playerList;
            if (!list.isEmpty()) {
                Collections.sort(list, (o1, o2) -> Integer.compare(o1.getByte("PI"), o2.getByte("PI")));
            }
            gridView.setAdapter(new PlayerImageAdapter(list, this));
        }
    }

    public String[] querySongNameAndDiffByPath(String songFilePath) {
        String[] strArr = new String[3];
        List<Song> songByFilePath = JPApplication.getSongDatabase().songDao().getSongByFilePath(songFilePath);
        for (Song song : songByFilePath) {
            strArr[0] = song.getName();
            strArr[1] = String.format(Locale.getDefault(), "%.1f", song.getRightHandDegree());
            strArr[2] = String.format(Locale.getDefault(), "%.1f", song.getLeftHandDegree());
        }
        return strArr;
    }

    public Handler getHandler() {
        return handler;
    }

    public String getPlayerKind() {
        return positionStatus;
    }

    public int getMode() {
        return roomMode;
    }

    public void setTune(int tune) {
        this.tune = tune;
    }

    public int getTune() {
        return tune;
    }

    @Override
    public boolean handleMessage(Message message) {
        super.handleMessage(message);
        Bundle data = message.getData();
        if (message.what == 1) {
            OnlinePlaySongDTO.Builder builder = OnlinePlaySongDTO.newBuilder();
            builder.setTune(tune);
            builder.setSongPath(data.getString("S"));
            sendMsg(OnlineProtocolType.PLAY_SONG, builder.build());
        }
        return false;
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        SongDao songDao = JPApplication.getSongDatabase().songDao();
        int id = view.getId();
        if (id == R.id.favor) {
            roomTabs.setCurrentTab(2);
            DataSource.Factory<Integer, Song> favoriteSongList = songDao.getFavoriteSongsWithDataSource();
            pagedListLiveData.removeObservers(this);
            pagedListLiveData = songDao.getPageListByDatasourceFactory(favoriteSongList);
            pagedListLiveData.observe(this, ((OLRoomSongsAdapter) (Objects.requireNonNull(songsRecyclerView.getAdapter())))::submitList);
            moreSongsPopupWindow.dismiss();
        } else if (id == R.id.couple_1) {
            setGroupOrHand(1, coupleModePopupWindow);
        } else if (id == R.id.couple_2) {
            setGroupOrHand(2, coupleModePopupWindow);
        } else if (id == R.id.couple_6) {
            setGroupOrHand(6, coupleModePopupWindow);
        } else if (id == R.id.couple_4) {
            setGroupOrHand(4, coupleModePopupWindow);
        } else if (id == R.id.couple_5) {
            setGroupOrHand(5, coupleModePopupWindow);
        } else if (id == R.id.couple_3) {
            setGroupOrHand(3, coupleModePopupWindow);
        } else if (id == R.id.group_1) {
            setGroupOrHand(1, groupModePopupWindow);
        } else if (id == R.id.group_2) {
            setGroupOrHand(2, groupModePopupWindow);
        } else if (id == R.id.group_3) {
            setGroupOrHand(3, groupModePopupWindow);
        } else if (id == R.id.left_hand) {
            currentHand = 1;
            setGroupOrHand(1, normalModePopupWindow);
            this.roomInfoBundle.putInt("myHand", currentHand);
            settingButton.setText("左" + settingButton.getText().toString().substring(1));
        } else if (id == R.id.right_hand) {
            currentHand = 0;
            setGroupOrHand(0, normalModePopupWindow);
            this.roomInfoBundle.putInt("myHand", currentHand);
            settingButton.setText("右" + settingButton.getText().toString().substring(1));
        } else if (id == R.id.changeScreenOrientation) {
            changeScreenOrientation();
        } else if (id == R.id.ol_more_settings) {
            if (normalModePopupWindow != null) {
                normalModePopupWindow.dismiss();
            }
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result ->
                    ImageLoadUtil.setBackground(this)).launch(new Intent(this, SettingsMode.class));
        } else if (id == R.id.rand_0) {
            playSongByDegreeRandom(2, 4);
        } else if (id == R.id.rand_all) {
            playSongByDegreeRandom(0, 2);
        } else if (id == R.id.rand_5) {
            playSongByDegreeRandom(6, 8);
        } else if (id == R.id.rand_3) {
            playSongByDegreeRandom(4, 6);
        } else if (id == R.id.rand_7) {
            playSongByDegreeRandom(8, 10);
        } else if (id == R.id.add_favor) {
            if (!TextUtils.isEmpty(currentPlaySongPath)) {
                if (songDao.updateFavoriteSong(currentPlaySongPath, 1) > 0) {
                    Toast.makeText(this, String.format("已将曲目《%s》加入本地收藏", songNameScrollText.getText()), Toast.LENGTH_SHORT).show();
                }
            }
            moreSongsPopupWindow.dismiss();
        } else if (id == R.id.type_l) {
            randomSongBetweenTwoDegree(1, 2);
        } else if (id == R.id.type_j) {
            randomSongWithDegree(0);
        } else if (id == R.id.type_e) {
            randomSongBetweenTwoDegree(3, 6);
        } else if (id == R.id.type_d) {
            randomSongBetweenTwoDegree(4, 5);
        } else if (id == R.id.type_h) {
            randomSongWithDegree(7);
        } else if (id == R.id.ol_search_b) {
            String keywords = String.valueOf(searchTextView.getText());
            if (keywords.isEmpty()) {
                return;
            }
            searchTextView.setText("");
            DataSource.Factory<Integer, Song> songByNameKeywords = songDao.getSongsByNameKeywordsWithDataSource(keywords);
            pagedListLiveData.removeObservers(this);
            pagedListLiveData = songDao.getPageListByDatasourceFactory(songByNameKeywords);
            pagedListLiveData.observe(this, (pagedList) -> {
                Toast.makeText(this, "搜索到" +
                        (pagedList.getLoadedCount() >= SongDao.PAGE_SIZE ? SongDao.PAGE_SIZE + "+" : String.valueOf(pagedList.getLoadedCount()))
                        + "首与" + keywords + " 有关的曲目!", Toast.LENGTH_SHORT).show();
                ((OLRoomSongsAdapter) (Objects.requireNonNull(songsRecyclerView.getAdapter()))).submitList(pagedList);
            });
        } else if (id == R.id.ol_soundstop) {
            SongPlay.stopPlay();
        } else if (id == R.id.onetimeplay) {
            if (positionStatus.equals("H")) {
                SongPlay.setPlaySongsMode(PlaySongsModeEnum.ONCE);
                Toast.makeText(this, PlaySongsModeEnum.ONCE.getDesc(), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "您不是房主，不能设置播放模式!", Toast.LENGTH_SHORT).show();
            }
            playSongsModePopupWindow.dismiss();
        } else if (id == R.id.circulateplay) {
            if (positionStatus.equals("H")) {
                SongPlay.setPlaySongsMode(PlaySongsModeEnum.RECYCLE);
                Toast.makeText(this, PlaySongsModeEnum.RECYCLE.getDesc(), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "您不是房主，不能设置播放模式!", Toast.LENGTH_SHORT).show();
            }
            playSongsModePopupWindow.dismiss();
        } else if (id == R.id.randomplay) {
            if (positionStatus.equals("H")) {
                SongPlay.setPlaySongsMode(PlaySongsModeEnum.RANDOM);
                Toast.makeText(this, PlaySongsModeEnum.RANDOM.getDesc(), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "您不是房主，不能设置播放模式!", Toast.LENGTH_SHORT).show();
            }
            playSongsModePopupWindow.dismiss();
        } else if (id == R.id.favorplay) {
            if (positionStatus.equals("H")) {
                SongPlay.setPlaySongsMode(PlaySongsModeEnum.FAVOR_RANDOM);
                Toast.makeText(this, PlaySongsModeEnum.FAVOR_RANDOM.getDesc(), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "您不是房主，不能设置播放模式!", Toast.LENGTH_SHORT).show();
            }
            playSongsModePopupWindow.dismiss();
        } else if (id == R.id.favorlist) {
            if (positionStatus.equals("H")) {
                SongPlay.setPlaySongsMode(PlaySongsModeEnum.FAVOR);
                Toast.makeText(this, PlaySongsModeEnum.FAVOR.getDesc(), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "您不是房主，不能设置播放模式!", Toast.LENGTH_SHORT).show();
            }
            playSongsModePopupWindow.dismiss();
        } else if (id == R.id.ol_ready_b) {
            if (positionStatus.equals("G")) {
                String status = Objects.equals("准备", playButton.getText().toString()) ? "N" : "R";
                sendMsg(OnlineProtocolType.CHANGE_ROOM_USER_STATUS, OnlineChangeRoomUserStatusDTO.newBuilder().setStatus(status).build());
            } else {
                sendMsg(OnlineProtocolType.PLAY_START, OnlinePlayStartDTO.getDefaultInstance());
            }
        } else if (id == R.id.ol_songlist_b) {
            moreSongsPopupWindow.showAtLocation(playSongsModeButton, Gravity.CENTER, 0, 0);
        } else if (id == R.id.ol_group_b) {
            if (roomMode == RoomModeEnum.NORMAL.getCode()) {
                PopupWindow normalModePopupWindow = new JPPopupWindow(this);
                View roomHandSelectView = LayoutInflater.from(this).inflate(R.layout.ol_room_hand_select, null);
                normalModePopupWindow.setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.filled_box, getTheme()));
                roomHandSelectView.findViewById(R.id.left_hand).setOnClickListener(this);
                roomHandSelectView.findViewById(R.id.right_hand).setOnClickListener(this);
                roomHandSelectView.findViewById(R.id.shengdiao).setOnClickListener(this);
                roomHandSelectView.findViewById(R.id.jiangdiao).setOnClickListener(this);
                roomHandSelectView.findViewById(R.id.changeScreenOrientation).setOnClickListener(this);
                roomHandSelectView.findViewById(R.id.ol_more_settings).setOnClickListener(this);
                normalModePopupWindow.setContentView(roomHandSelectView);
                this.normalModePopupWindow = normalModePopupWindow;
                normalModePopupWindow.showAtLocation(settingButton, Gravity.CENTER, 0, 0);
            } else if (roomMode == RoomModeEnum.TEAM.getCode() && groupModePopupWindow != null) {
                groupModePopupWindow.showAtLocation(settingButton, Gravity.CENTER, 0, 0);
            } else if (roomMode == RoomModeEnum.COUPLE.getCode() && coupleModePopupWindow != null) {
                coupleModePopupWindow.showAtLocation(settingButton, Gravity.CENTER, 0, 0);
            }
        } else if (id == R.id.ol_more_b) {
            if (playSongsModePopupWindow != null) {
                int[] iArr = new int[2];
                playSongsModeButton.getLocationOnScreen(iArr);
                playSongsModePopupWindow.showAtLocation(songNameScrollText, Gravity.TOP | Gravity.START, iArr[0], (int) (iArr[1] * 0.43f));
            }
        } else if (id == R.id.shengdiao) {
            if (positionStatus.equals("H") && !TextUtils.isEmpty(currentPlaySongPath)) {
                tune = Math.min(6, tune + 1);
                updateNewSongPlay(currentPlaySongPath);
            } else {
                Toast.makeText(this, "您不是房主或您未选择曲目，操作无效!", Toast.LENGTH_LONG).show();
            }
            if (normalModePopupWindow != null) {
                normalModePopupWindow.dismiss();
            }
        } else if (id == R.id.jiangdiao) {
            if (positionStatus.equals("H") && !TextUtils.isEmpty(currentPlaySongPath)) {
                tune = Math.max(-6, tune - 1);
                updateNewSongPlay(currentPlaySongPath);
            } else {
                Toast.makeText(this, "您不是房主或您未选择曲目，操作无效!", Toast.LENGTH_LONG).show();
            }
            if (normalModePopupWindow != null) {
                normalModePopupWindow.dismiss();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        SongPlay.setCallBack(this::updateNewSongPlay);
        setContentView(R.layout.ol_play_room);
        initRoomActivity(savedInstanceState);
        findViewById(R.id.ol_search_b).setOnClickListener(this);
        playButton = findViewById(R.id.ol_ready_b);
        playButton.setOnClickListener(this);
        playSongsModeButton = findViewById(R.id.ol_more_b);
        playSongsModeButton.setOnClickListener(this);
        settingButton = findViewById(R.id.ol_group_b);
        settingButton.setOnClickListener(this);
        playButton.setText(positionStatus.equals("H") ? "开始" : "准备");
        searchTextView = findViewById(R.id.ol_search_text);
        findViewById(R.id.ol_soundstop).setOnClickListener(this);
        songsRecyclerView = findViewById(R.id.ol_song_list);
        songsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        OLRoomSongsAdapter olRoomSongsAdapter = new OLRoomSongsAdapter(this, songsRecyclerView);
        songsRecyclerView.setAdapter(olRoomSongsAdapter);
        SongDao songDao = JPApplication.getSongDatabase().songDao();
        DataSource.Factory<Integer, Song> allSongs = songDao.getAllSongsWithDataSource();
        pagedListLiveData = songDao.getPageListByDatasourceFactory(allSongs);
        pagedListLiveData.observe(this, olRoomSongsAdapter::submitList);
        songNameScrollText = findViewById(R.id.ol_songlist_b);
        songNameScrollText.setSingleLine(true);
        songNameScrollText.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        songNameScrollText.setMarqueeRepeatLimit(-1);
        songNameScrollText.setSelected(true);
        songNameScrollText.setFocusable(true);
        songNameScrollText.setFocusableInTouchMode(true);
        songNameScrollText.setOnClickListener(this);
        PopupWindow moreSongsPopupWindow = new JPPopupWindow(this);
        View roomSongRandView = LayoutInflater.from(this).inflate(R.layout.ol_room_song_rand, null);
        moreSongsPopupWindow.setContentView(roomSongRandView);
        roomSongRandView.findViewById(R.id.rand_all).setOnClickListener(this);
        roomSongRandView.findViewById(R.id.add_favor).setOnClickListener(this);
        roomSongRandView.findViewById(R.id.favor).setOnClickListener(this);
        roomSongRandView.findViewById(R.id.rand_0).setOnClickListener(this);
        roomSongRandView.findViewById(R.id.rand_3).setOnClickListener(this);
        roomSongRandView.findViewById(R.id.rand_5).setOnClickListener(this);
        roomSongRandView.findViewById(R.id.rand_7).setOnClickListener(this);
        roomSongRandView.findViewById(R.id.type_j).setOnClickListener(this);
        roomSongRandView.findViewById(R.id.type_l).setOnClickListener(this);
        roomSongRandView.findViewById(R.id.type_d).setOnClickListener(this);
        roomSongRandView.findViewById(R.id.type_e).setOnClickListener(this);
        roomSongRandView.findViewById(R.id.type_h).setOnClickListener(this);
        moreSongsPopupWindow.setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.filled_box, getTheme()));
        this.moreSongsPopupWindow = moreSongsPopupWindow;
        switch (RoomModeEnum.ofCode(roomMode, RoomModeEnum.NORMAL)) {
            case NORMAL -> {
                currentHand = this.roomInfoBundle.getInt("myHand");
                settingButton.setText((currentHand == 0 ? "右" : "左") + "00");
            }
            case TEAM -> {
                PopupWindow groupModePopupWindow = new JPPopupWindow(this);
                View roomGroupView = LayoutInflater.from(this).inflate(R.layout.ol_room_group_select, null);
                groupModePopupWindow.setContentView(roomGroupView);
                groupModePopupWindow.setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.filled_box, getTheme()));
                roomGroupView.findViewById(R.id.group_1).setOnClickListener(this);
                roomGroupView.findViewById(R.id.group_2).setOnClickListener(this);
                roomGroupView.findViewById(R.id.group_3).setOnClickListener(this);
                this.groupModePopupWindow = groupModePopupWindow;
            }
            case COUPLE -> {
                PopupWindow coupleModePopupWindow = new JPPopupWindow(this);
                View coupleGroupView = LayoutInflater.from(this).inflate(R.layout.ol_room_couple_select, null);
                coupleModePopupWindow.setContentView(coupleGroupView);
                coupleModePopupWindow.setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.filled_box, getTheme()));
                coupleGroupView.findViewById(R.id.couple_1).setOnClickListener(this);
                coupleGroupView.findViewById(R.id.couple_2).setOnClickListener(this);
                coupleGroupView.findViewById(R.id.couple_3).setOnClickListener(this);
                coupleGroupView.findViewById(R.id.couple_4).setOnClickListener(this);
                coupleGroupView.findViewById(R.id.couple_5).setOnClickListener(this);
                coupleGroupView.findViewById(R.id.couple_6).setOnClickListener(this);
                this.coupleModePopupWindow = coupleModePopupWindow;
            }
        }
        PopupWindow playSongsModePopupWindow = new JPPopupWindow(this);
        View playSongsModeView = LayoutInflater.from(this).inflate(R.layout.ol_playsongsmode, null);
        playSongsModePopupWindow.setContentView(playSongsModeView);
        playSongsModePopupWindow.setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.filled_box, getTheme()));
        playSongsModeView.findViewById(R.id.onetimeplay).setOnClickListener(this);
        playSongsModeView.findViewById(R.id.circulateplay).setOnClickListener(this);
        playSongsModeView.findViewById(R.id.randomplay).setOnClickListener(this);
        playSongsModeView.findViewById(R.id.favorplay).setOnClickListener(this);
        playSongsModeView.findViewById(R.id.favorlist).setOnClickListener(this);
        this.playSongsModePopupWindow = playSongsModePopupWindow;
        TabSpec newTabSpec = roomTabs.newTabSpec("tab3");
        newTabSpec.setContent(R.id.songs_tab);
        newTabSpec.setIndicator("曲目");
        roomTabs.addTab(newTabSpec);
        newTabSpec = roomTabs.newTabSpec("tab4");
        newTabSpec.setContent(R.id.players_tab);
        newTabSpec.setIndicator("邀请");
        roomTabs.addTab(newTabSpec);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        for (int i = 0; i < 4; i++) {
            DisplayMetrics dm = this.getResources().getDisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                roomTabs.getTabWidget().getChildTabViewAt(i).getLayoutParams().height = (displayMetrics.heightPixels * 45) / 960;
                if (UnitConvertUtil.px2dp(this, displayMetrics.widthPixels) <= 360) {
                    int height = UnitConvertUtil.sp2px(this, 20) + UnitConvertUtil.dp2px(this, 224) + dm.widthPixels / 3;
                    RelativeLayout relativeLayout = findViewById(R.id.RelativeLayout1);
                    relativeLayout.setLayoutParams(new LinearLayout.LayoutParams(dm.widthPixels, height));
                    playerGrid.setNumColumns(3);
                } else {
                    int height = UnitConvertUtil.sp2px(this, 20) + UnitConvertUtil.dp2px(this, 76) + dm.widthPixels / 6;
                    RelativeLayout relativeLayout = findViewById(R.id.RelativeLayout1);
                    relativeLayout.setLayoutParams(new LinearLayout.LayoutParams(dm.widthPixels, height));
                    playerGrid.setNumColumns(6);
                }
            } else {
                roomTabs.getTabWidget().getChildTabViewAt(i).getLayoutParams().height = (displayMetrics.heightPixels * 45) / 480;
                roomNameView.getLayoutParams().height = (displayMetrics.heightPixels * 45) / 480;
                timeTextView.getLayoutParams().height = (displayMetrics.heightPixels * 45) / 480;
            }
            setTabTitleViewLayout(i);
        }
        roomTabs.setCurrentTab(1);
        // 注意这里在向服务端发消息
        sendMsg(OnlineProtocolType.LOAD_ROOM_POSITION, OnlineLoadRoomPositionDTO.getDefaultInstance());
    }

    @Override
    protected void onDestroy() {
        SongPlay.setCallBack(null);
        super.onDestroy();
    }

    /**
     * 根据曲谱名称进行发消息播放
     */
    public void updateNewSongPlay(String songFilePath) {
        if (TextUtils.isEmpty(songFilePath)) {
            return;
        }
        OnlinePlaySongDTO.Builder playSongBuilder = OnlinePlaySongDTO.newBuilder();
        playSongBuilder.setTune(tune);
        playSongBuilder.setSongPath(songFilePath.substring(6, songFilePath.length() - 3));
        sendMsg(OnlineProtocolType.PLAY_SONG, playSongBuilder.build());
        Message obtainMessage = olPlayRoomHandler.obtainMessage();
        obtainMessage.what = 12;
        olPlayRoomHandler.handleMessage(obtainMessage);
    }
}
