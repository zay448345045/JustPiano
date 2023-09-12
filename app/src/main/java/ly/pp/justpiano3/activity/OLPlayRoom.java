package ly.pp.justpiano3.activity;

import android.content.pm.ActivityInfo;
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
import android.view.WindowManager;
import android.widget.*;
import android.widget.TabHost.TabSpec;
import androidx.lifecycle.LiveData;
import androidx.paging.DataSource;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.netty.util.internal.StringUtil;
import ly.pp.justpiano3.JPApplication;
import ly.pp.justpiano3.R;
import ly.pp.justpiano3.adapter.OLRoomSongsAdapter;
import ly.pp.justpiano3.adapter.PlayerImageAdapter;
import ly.pp.justpiano3.constant.Consts;
import ly.pp.justpiano3.constant.OnlineProtocolType;
import ly.pp.justpiano3.database.dao.SongDao;
import ly.pp.justpiano3.database.entity.Song;
import ly.pp.justpiano3.entity.GlobalSetting;
import ly.pp.justpiano3.entity.User;
import ly.pp.justpiano3.enums.PlaySongsModeEnum;
import ly.pp.justpiano3.enums.RoomModeEnum;
import ly.pp.justpiano3.handler.android.OLPlayRoomHandler;
import ly.pp.justpiano3.listener.CpRequestClick;
import ly.pp.justpiano3.listener.DialogDismissClick;
import ly.pp.justpiano3.thread.SongPlay;
import ly.pp.justpiano3.utils.JPStack;
import ly.pp.justpiano3.view.JPDialog;
import ly.pp.justpiano3.view.ScrollText;
import org.jetbrains.annotations.NotNull;
import protobuf.dto.OnlineChangeRoomHandDTO;
import protobuf.dto.OnlineChangeRoomUserStatusDTO;
import protobuf.dto.OnlinePlaySongDTO;
import protobuf.dto.OnlinePlayStartDTO;

import java.util.*;

public final class OLPlayRoom extends OLPlayRoomActivity {
    public OLPlayRoomHandler olPlayRoomHandler = new OLPlayRoomHandler(this);
    public ScrollText songNameText;
    public PopupWindow commonModeGroup;
    // 防止横竖屏切换时，玩家前后台状态错误
    public boolean isChangeScreen;
    public int roomMode;
    public User user;
    public Button groupButton;
    public Button playButton;
    public int currentHand;
    private int tune;
    private Button playSongsModeButton;
    private TextView searchText;
    private PopupWindow moreSongs;
    private PopupWindow groupModeGroup;
    private PopupWindow coupleModeGroup;
    private PopupWindow playSongsMode;
    private RecyclerView songsListView;
    public LiveData<PagedList<Song>> pagedListLiveData;
    public String currentPlaySongPath;

    private void playSongByDegreeRandom(int startDegree, int endDegree) {
        List<Song> songs = JPApplication.getSongDatabase().songDao().getSongByRightHandDegreeWithRandom(startDegree, endDegree);
        String songFilePath = songs.isEmpty() ? "" : songs.get(0).getFilePath();
        currentPlaySongPath = songFilePath;
        OnlinePlaySongDTO.Builder builder = OnlinePlaySongDTO.newBuilder();
        builder.setTune(tune);
        builder.setSongPath(songFilePath.substring(6, songFilePath.length() - 3));
        sendMsg(OnlineProtocolType.PLAY_SONG, builder.build());
        if (moreSongs != null) {
            moreSongs.dismiss();
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
        pagedListLiveData.observe(this, ((OLRoomSongsAdapter) (Objects.requireNonNull(songsListView.getAdapter())))::submitList);
        moreSongs.dismiss();
    }

    private void randomSongBetweenTwoDegree(int i, int j) {
        roomTabs.setCurrentTab(2);
        SongDao songDao = JPApplication.getSongDatabase().songDao();
        DataSource.Factory<Integer, Song> songsByCategoryWithDataSource = songDao.getSongsByCategoriesWithDataSource(Consts.items[i + 1], Consts.items[j + 1]);
        pagedListLiveData.removeObservers(this);
        pagedListLiveData = songDao.getPageListByDatasourceFactory(songsByCategoryWithDataSource);
        pagedListLiveData.observe(this, ((OLRoomSongsAdapter) (Objects.requireNonNull(songsListView.getAdapter())))::submitList);
        moreSongs.dismiss();
    }

    public void mo2860a(int i, String str, int i2, byte b) {
        String str2 = "";
        String str3 = "";
        String str4 = "";
        String str5 = "情意绵绵的情侣";
        switch (i2) {
            case 0:
                return;
            case 1:
                str5 = "情意绵绵的情侣";
                break;
            case 2:
                str5 = "基情四射的基友";
                break;
            case 3:
                str5 = "百年好合的百合";
                break;
        }
        if (i == 1) {
            str3 = "搭档请求";
            str4 = "邀请";
            str = "您与“" + str + "”在双人模式中最高连击总和超过200,是否邀请对方结为一对" + str5 + "?";
        } else if (i == 2) {
            str3 = "搭档请求";
            str4 = "同意";
            str = str + "请求与您结为一对" + str5 + ",是否同意?";
        } else if (i == 3) {
            str3 = "喜告";
            str4 = "确定";
            str = "祝贺" + str + "成为一对" + str5 + "!";
        } else if (i == 4) {
            showCpDialog(str5.substring(str5.length() - 2) + "证书", str);
            return;
        } else if (i == 5) {
            str3 = "提示";
            str4 = "确定";
        } else {
            str = str4;
            str4 = str3;
            str3 = str2;
        }
        JPDialog jpdialog = new JPDialog(this);
        jpdialog.setCancelableFalse();
        jpdialog.setTitle(str3).setMessage(str).setFirstButton(str4, new CpRequestClick(this, i, b, i2)).setSecondButton("取消", new DialogDismissClick()).showDialog();
    }

    public void mo2861a(GridView gridView, Bundle bundle) {
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

    public String[] querySongNameAndDiffByPath(String str) {
        String[] strArr = new String[3];
        List<Song> songByFilePath = JPApplication.getSongDatabase().songDao().getSongByFilePath(str);
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
        return playerKind;
    }

    public int getMode() {
        return roomMode;
    }

    public void setdiao(int i) {
        tune = i;
    }

    public int getdiao() {
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
        switch (view.getId()) {
            case R.id.favor:
                roomTabs.setCurrentTab(2);
                DataSource.Factory<Integer, Song> favoriteSongList = songDao.getFavoriteSongsWithDataSource();
                pagedListLiveData.removeObservers(this);
                pagedListLiveData = songDao.getPageListByDatasourceFactory(favoriteSongList);
                pagedListLiveData.observe(this, ((OLRoomSongsAdapter) (Objects.requireNonNull(songsListView.getAdapter())))::submitList);
                moreSongs.dismiss();
                return;
            case R.id.couple_1:
                setGroupOrHand(1, coupleModeGroup);
                return;
            case R.id.couple_2:
                setGroupOrHand(2, coupleModeGroup);
                return;
            case R.id.couple_6:
                setGroupOrHand(6, coupleModeGroup);
                return;
            case R.id.couple_4:
                setGroupOrHand(4, coupleModeGroup);
                return;
            case R.id.couple_5:
                setGroupOrHand(5, coupleModeGroup);
                return;
            case R.id.couple_3:
                setGroupOrHand(3, coupleModeGroup);
                return;
            case R.id.group_1:
                setGroupOrHand(1, groupModeGroup);
                return;
            case R.id.group_2:
                setGroupOrHand(2, groupModeGroup);
                return;
            case R.id.group_3:
                setGroupOrHand(3, groupModeGroup);
                return;
            case R.id.left_hand:
                currentHand = 1;
                setGroupOrHand(1, commonModeGroup);
                groupButton.setText("左" + groupButton.getText().toString().substring(1));
                return;
            case R.id.right_hand:
                currentHand = 0;
                setGroupOrHand(0, commonModeGroup);
                groupButton.setText("右" + groupButton.getText().toString().substring(1));
                return;
            case R.id.changeScreenOrientation:
                changeScreenOrientation();
                return;
            case R.id.rand_0:
                playSongByDegreeRandom(2, 4);
                return;
            case R.id.rand_all:
                playSongByDegreeRandom(0, 2);
                return;
            case R.id.rand_5:
                playSongByDegreeRandom(6, 8);
                return;
            case R.id.rand_3:
                playSongByDegreeRandom(4, 6);
                return;
            case R.id.rand_7:
                playSongByDegreeRandom(8, 10);
                return;
            case R.id.add_favor:
                if (!StringUtil.isNullOrEmpty(currentPlaySongPath)) {
                    if (JPApplication.getSongDatabase().songDao().updateFavoriteSong(currentPlaySongPath, 1) > 0) {
                        Toast.makeText(this, String.format("已将曲目《%s》加入本地收藏", songNameText.getText()), Toast.LENGTH_SHORT).show();
                    }
                }
                moreSongs.dismiss();
                return;
            case R.id.type_l:
                randomSongBetweenTwoDegree(1, 2);
                return;
            case R.id.type_j:
                randomSongWithDegree(0);
                return;
            case R.id.type_e:
                randomSongBetweenTwoDegree(3, 6);
                return;
            case R.id.type_d:
                randomSongBetweenTwoDegree(4, 5);
                return;
            case R.id.type_h:
                randomSongWithDegree(7);
                return;
            case R.id.ol_search_b:
                String keywords = String.valueOf(searchText.getText());
                if (keywords.isEmpty()) {
                    return;
                }
                searchText.setText("");
                DataSource.Factory<Integer, Song> songByNameKeywords = songDao.getSongsByNameKeywordsWithDataSource(keywords);
                pagedListLiveData.removeObservers(this);
                pagedListLiveData = songDao.getPageListByDatasourceFactory(songByNameKeywords);
                pagedListLiveData.observe(this, ((OLRoomSongsAdapter) (Objects.requireNonNull(songsListView.getAdapter())))::submitList);
                return;
            case R.id.ol_soundstop:
                SongPlay.INSTANCE.stopPlay();
                return;
            case R.id.onetimeplay:
                if (playerKind.equals("H")) {
                    SongPlay.INSTANCE.setPlaySongsMode(PlaySongsModeEnum.ONCE);
                    Toast.makeText(this, PlaySongsModeEnum.ONCE.getDesc(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "您不是房主，不能设置播放模式!", Toast.LENGTH_SHORT).show();
                }
                playSongsMode.dismiss();
                return;
            case R.id.circulateplay:
                if (playerKind.equals("H")) {
                    SongPlay.INSTANCE.setPlaySongsMode(PlaySongsModeEnum.RECYCLE);
                    Toast.makeText(this, PlaySongsModeEnum.RECYCLE.getDesc(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "您不是房主，不能设置播放模式!", Toast.LENGTH_SHORT).show();
                }
                playSongsMode.dismiss();
                return;
            case R.id.randomplay:
                if (playerKind.equals("H")) {
                    SongPlay.INSTANCE.setPlaySongsMode(PlaySongsModeEnum.RANDOM);
                    Toast.makeText(this, PlaySongsModeEnum.RANDOM.getDesc(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "您不是房主，不能设置播放模式!", Toast.LENGTH_SHORT).show();
                }
                playSongsMode.dismiss();
                return;
            case R.id.favorplay:
                if (playerKind.equals("H")) {
                    SongPlay.INSTANCE.setPlaySongsMode(PlaySongsModeEnum.FAVOR_RANDOM);
                    Toast.makeText(this, PlaySongsModeEnum.FAVOR_RANDOM.getDesc(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "您不是房主，不能设置播放模式!", Toast.LENGTH_SHORT).show();
                }
                playSongsMode.dismiss();
                return;
            case R.id.favorlist:
                if (playerKind.equals("H")) {
                    SongPlay.INSTANCE.setPlaySongsMode(PlaySongsModeEnum.FAVOR);
                    Toast.makeText(this, PlaySongsModeEnum.FAVOR.getDesc(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "您不是房主，不能设置播放模式!", Toast.LENGTH_SHORT).show();
                }
                playSongsMode.dismiss();
                return;
            case R.id.ol_ready_b:
                if (playerKind.equals("G")) {
                    OnlineChangeRoomUserStatusDTO.Builder builder1 = OnlineChangeRoomUserStatusDTO.newBuilder();
                    builder1.setStatus("R");
                    sendMsg(OnlineProtocolType.CHANGE_ROOM_USER_STATUS, builder1.build());
                } else {
                    sendMsg(OnlineProtocolType.PLAY_START, OnlinePlayStartDTO.getDefaultInstance());
                }
                return;
            case R.id.ol_songlist_b:
                moreSongs.showAtLocation(playSongsModeButton, Gravity.CENTER, 0, 0);
                return;
            case R.id.ol_group_b:
                if (roomMode == RoomModeEnum.NORMAL.getCode()) {
                    PopupWindow popupWindow2 = new PopupWindow(this);
                    View inflate2 = LayoutInflater.from(this).inflate(R.layout.ol_hand_list, null);
                    popupWindow2.setBackgroundDrawable(getResources().getDrawable(R.drawable.filled_box));
                    popupWindow2.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
                    popupWindow2.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
                    inflate2.findViewById(R.id.left_hand).setOnClickListener(this);
                    inflate2.findViewById(R.id.right_hand).setOnClickListener(this);
                    inflate2.findViewById(R.id.shengdiao).setOnClickListener(this);
                    inflate2.findViewById(R.id.jiangdiao).setOnClickListener(this);
                    inflate2.findViewById(R.id.changeScreenOrientation).setOnClickListener(this);
                    popupWindow2.setFocusable(true);
                    popupWindow2.setTouchable(true);
                    popupWindow2.setOutsideTouchable(true);
                    popupWindow2.setContentView(inflate2);
                    commonModeGroup = popupWindow2;
                    popupWindow2.showAtLocation(groupButton, Gravity.CENTER, 0, 0);
                    return;
                } else if (roomMode == RoomModeEnum.TEAM.getCode() && groupModeGroup != null) {
                    groupModeGroup.showAtLocation(groupButton, Gravity.CENTER, 0, 0);
                    return;
                } else if (roomMode == RoomModeEnum.COUPLE.getCode() && coupleModeGroup != null) {
                    coupleModeGroup.showAtLocation(groupButton, Gravity.CENTER, 0, 0);
                    return;
                }
                return;
            case R.id.ol_more_b:
                if (playSongsMode != null) {
                    int[] iArr = new int[2];
                    playSongsModeButton.getLocationOnScreen(iArr);
                    playSongsMode.showAtLocation(songNameText, Gravity.TOP | Gravity.START, iArr[0], (int) (iArr[1] * 0.43f));
                }
                return;
            case R.id.shengdiao:
                if (playerKind.equals("H") && !StringUtil.isNullOrEmpty(currentPlaySongPath)) {
                    tune = Math.min(6, tune + 1);
                    updateNewSongPlay(currentPlaySongPath);
                } else {
                    Toast.makeText(this, "您不是房主或您未选择曲目，操作无效!", Toast.LENGTH_LONG).show();
                }
                if (commonModeGroup != null) {
                    commonModeGroup.dismiss();
                }
                return;
            case R.id.jiangdiao:
                if (playerKind.equals("H") && !StringUtil.isNullOrEmpty(currentPlaySongPath)) {
                    tune = Math.max(-6, tune - 1);
                    updateNewSongPlay(currentPlaySongPath);
                } else {
                    Toast.makeText(this, "您不是房主或您未选择曲目，操作无效!", Toast.LENGTH_LONG).show();
                }
                if (commonModeGroup != null) {
                    commonModeGroup.dismiss();
                }
                return;
            default:
        }
    }

    private void changeScreenOrientation() {
        isChangeScreen = true;
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        JPStack.push(this);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        SongPlay.INSTANCE.setCallBack(this::updateNewSongPlay);
        setContentView(R.layout.olplayroom);
        if (savedInstanceState != null) {
            msgList = savedInstanceState.getParcelableArrayList("msgList");
            isChangeScreen = savedInstanceState.getBoolean("isChangeScreen");
            bindMsgListView(GlobalSetting.INSTANCE.getShowChatTime());
        }
        initRoomActivity();
        Button olSearchButton = findViewById(R.id.ol_search_b);
        olSearchButton.setOnClickListener(this);
        playButton = findViewById(R.id.ol_ready_b);
        playButton.setOnClickListener(this);
        playSongsModeButton = findViewById(R.id.ol_more_b);
        playSongsModeButton.setOnClickListener(this);
        groupButton = findViewById(R.id.ol_group_b);
        groupButton.setOnClickListener(this);
        if (playerKind.equals("H")) {
            playButton.setText("开始");
        } else {
            playButton.setText("准备");
        }
        searchText = findViewById(R.id.ol_search_text);
        ImageView soundStopButton = findViewById(R.id.ol_soundstop);
        soundStopButton.setOnClickListener(this);
        songsListView = findViewById(R.id.ol_song_list);
        songsListView.setLayoutManager(new LinearLayoutManager(this));
        OLRoomSongsAdapter olRoomSongsAdapter = new OLRoomSongsAdapter(this, songsListView);
        songsListView.setAdapter(olRoomSongsAdapter);
        SongDao songDao = JPApplication.getSongDatabase().songDao();
        DataSource.Factory<Integer, Song> allSongs = songDao.getAllSongsWithDataSource();
        pagedListLiveData = songDao.getPageListByDatasourceFactory(allSongs);
        pagedListLiveData.observe(this, olRoomSongsAdapter::submitList);
        songNameText = findViewById(R.id.ol_songlist_b);
        songNameText.setSingleLine(true);
        songNameText.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        songNameText.setMarqueeRepeatLimit(-1);
        songNameText.setSelected(true);
        songNameText.setFocusable(true);
        songNameText.setFocusableInTouchMode(true);
        songNameText.setOnClickListener(this);
        PopupWindow popupWindow2 = new PopupWindow(this);
        View inflate2 = LayoutInflater.from(this).inflate(R.layout.ol_songpop_list, null);
        popupWindow2.setContentView(inflate2);
        inflate2.findViewById(R.id.rand_all).setOnClickListener(this);
        inflate2.findViewById(R.id.add_favor).setOnClickListener(this);
        inflate2.findViewById(R.id.favor).setOnClickListener(this);
        inflate2.findViewById(R.id.rand_0).setOnClickListener(this);
        inflate2.findViewById(R.id.rand_3).setOnClickListener(this);
        inflate2.findViewById(R.id.rand_5).setOnClickListener(this);
        inflate2.findViewById(R.id.rand_7).setOnClickListener(this);
        inflate2.findViewById(R.id.type_j).setOnClickListener(this);
        inflate2.findViewById(R.id.type_l).setOnClickListener(this);
        inflate2.findViewById(R.id.type_d).setOnClickListener(this);
        inflate2.findViewById(R.id.type_e).setOnClickListener(this);
        inflate2.findViewById(R.id.type_h).setOnClickListener(this);
        popupWindow2.setBackgroundDrawable(getResources().getDrawable(R.drawable.filled_box));
        popupWindow2.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow2.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow2.setFocusable(true);
        popupWindow2.setTouchable(true);
        popupWindow2.setOutsideTouchable(true);
        moreSongs = popupWindow2;
        switch (RoomModeEnum.ofCode(roomMode, RoomModeEnum.NORMAL)) {
            case NORMAL:
                groupButton.setText("右00");
                break;
            case TEAM:
                popupWindow2 = new PopupWindow(this);
                inflate2 = LayoutInflater.from(this).inflate(R.layout.ol_group_list, null);
                popupWindow2.setContentView(inflate2);
                popupWindow2.setBackgroundDrawable(getResources().getDrawable(R.drawable.filled_box));
                popupWindow2.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
                popupWindow2.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
                inflate2.findViewById(R.id.group_1).setOnClickListener(this);
                inflate2.findViewById(R.id.group_2).setOnClickListener(this);
                inflate2.findViewById(R.id.group_3).setOnClickListener(this);
                popupWindow2.setFocusable(true);
                popupWindow2.setTouchable(true);
                popupWindow2.setOutsideTouchable(true);
                groupModeGroup = popupWindow2;
                break;
            case COUPLE:
                popupWindow2 = new PopupWindow(this);
                inflate2 = LayoutInflater.from(this).inflate(R.layout.ol_couple_list, null);
                popupWindow2.setContentView(inflate2);
                popupWindow2.setBackgroundDrawable(getResources().getDrawable(R.drawable.filled_box));
                popupWindow2.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
                popupWindow2.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
                inflate2.findViewById(R.id.couple_1).setOnClickListener(this);
                inflate2.findViewById(R.id.couple_2).setOnClickListener(this);
                inflate2.findViewById(R.id.couple_3).setOnClickListener(this);
                inflate2.findViewById(R.id.couple_4).setOnClickListener(this);
                inflate2.findViewById(R.id.couple_5).setOnClickListener(this);
                inflate2.findViewById(R.id.couple_6).setOnClickListener(this);
                popupWindow2.setFocusable(true);
                popupWindow2.setTouchable(true);
                popupWindow2.setOutsideTouchable(true);
                coupleModeGroup = popupWindow2;
                break;
        }
        PopupWindow popupWindow4 = new PopupWindow(this);
        View inflate4 = LayoutInflater.from(this).inflate(R.layout.ol_playsongsmode, null);
        popupWindow4.setContentView(inflate4);
        popupWindow4.setBackgroundDrawable(getResources().getDrawable(R.drawable.filled_box));
        popupWindow4.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow4.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        inflate4.findViewById(R.id.onetimeplay).setOnClickListener(this);
        inflate4.findViewById(R.id.circulateplay).setOnClickListener(this);
        inflate4.findViewById(R.id.randomplay).setOnClickListener(this);
        inflate4.findViewById(R.id.favorplay).setOnClickListener(this);
        inflate4.findViewById(R.id.favorlist).setOnClickListener(this);
        popupWindow4.setFocusable(true);
        popupWindow4.setTouchable(true);
        popupWindow4.setOutsideTouchable(true);
        playSongsMode = popupWindow4;
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
                if (px2dp(this, displayMetrics.widthPixels) <= 360) {
                    int height = sp2px(this, 20) + dp2px(this, 224) + dm.widthPixels / 3;
                    RelativeLayout rs = this.findViewById(R.id.RelativeLayout1);
                    rs.setLayoutParams(new LinearLayout.LayoutParams(dm.widthPixels, height));
                    playerGrid.setNumColumns(3);
                } else {
                    int height = sp2px(this, 20) + dp2px(this, 76) + dm.widthPixels / 6;
                    RelativeLayout rs = this.findViewById(R.id.RelativeLayout1);
                    rs.setLayoutParams(new LinearLayout.LayoutParams(dm.widthPixels, height));
                    playerGrid.setNumColumns(6);
                }
            } else {
                roomTabs.getTabWidget().getChildTabViewAt(i).getLayoutParams().height = (displayMetrics.heightPixels * 45) / 480;
            }
            setTabTitleViewLayout(i);
        }
        roomTabs.setCurrentTab(1);
    }

    @Override
    protected void onDestroy() {
        if (!isChangeScreen) {
            SongPlay.INSTANCE.stopPlay();
        }
        timeUpdateRunning = false;
        try {
            timeUpdateThread.interrupt();
        } catch (Exception ignored) {
        }
        msgList.clear();
        playerList.clear();
        invitePlayerList.clear();
        friendPlayerList.clear();
        JPStack.pop(this);
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!isOnStart) {
            OnlineChangeRoomUserStatusDTO.Builder builder = OnlineChangeRoomUserStatusDTO.newBuilder();
            builder.setStatus("N");
            sendMsg(OnlineProtocolType.CHANGE_ROOM_USER_STATUS, builder.build());
        }
        isOnStart = true;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (!isOnStart) {
            OnlineChangeRoomUserStatusDTO.Builder builder = OnlineChangeRoomUserStatusDTO.newBuilder();
            builder.setStatus("N");
            sendMsg(OnlineProtocolType.CHANGE_ROOM_USER_STATUS, builder.build());
            roomTabs.setCurrentTab(1);
            if (msgListView != null && msgListView.getAdapter() != null) {
                msgListView.setSelection(msgListView.getAdapter().getCount() - 1);
            }
        }
        isOnStart = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        // 确定从前台切换到后台才会调用下面的代码，防止玩家手动横竖屏切换，导致后台状态错乱
        if (isOnStart && !isChangeScreen) {
            isOnStart = false;
            OnlineChangeRoomUserStatusDTO.Builder builder = OnlineChangeRoomUserStatusDTO.newBuilder();
            builder.setStatus("B");
            sendMsg(OnlineProtocolType.CHANGE_ROOM_USER_STATUS, builder.build());
        }
    }

    @Override
    protected void onSaveInstanceState(@NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("msgList", new ArrayList<>(msgList));
        outState.putBoolean("isChangeScreen", isChangeScreen);
    }

    /**
     * 根据曲谱名称进行发消息播放
     */
    public void updateNewSongPlay(String songFilePath) {
        OnlinePlaySongDTO.Builder playSongBuilder = OnlinePlaySongDTO.newBuilder();
        playSongBuilder.setTune(tune);
        playSongBuilder.setSongPath(songFilePath.substring(6, songFilePath.length() - 3));
        sendMsg(OnlineProtocolType.PLAY_SONG, playSongBuilder.build());
        Message obtainMessage = olPlayRoomHandler.obtainMessage();
        obtainMessage.what = 12;
        olPlayRoomHandler.handleMessage(obtainMessage);
    }
}
