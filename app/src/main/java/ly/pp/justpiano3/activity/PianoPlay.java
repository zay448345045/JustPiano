package ly.pp.justpiano3.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.google.protobuf.MessageLite;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;

import ly.pp.justpiano3.R;
import ly.pp.justpiano3.adapter.FinishScoreAdapter;
import ly.pp.justpiano3.adapter.MiniScoreAdapter;
import ly.pp.justpiano3.constant.OnlineProtocolType;
import ly.pp.justpiano3.entity.GlobalSetting;
import ly.pp.justpiano3.handler.android.PianoPlayHandler;
import ly.pp.justpiano3.listener.ShowOrHideMiniGradeClick;
import ly.pp.justpiano3.midi.MidiUtil;
import ly.pp.justpiano3.thread.StartPlayTimerTask;
import ly.pp.justpiano3.utils.FileUtil;
import ly.pp.justpiano3.utils.MidiDeviceUtil;
import ly.pp.justpiano3.utils.OnlineUtil;
import ly.pp.justpiano3.utils.PmSongUtil;
import ly.pp.justpiano3.utils.ShareUtil;
import ly.pp.justpiano3.utils.SoundEngineUtil;
import ly.pp.justpiano3.view.HorizontalListView;
import ly.pp.justpiano3.view.JPDialogBuilder;
import ly.pp.justpiano3.view.PlayKeyBoardView;
import ly.pp.justpiano3.view.PlayView;
import protobuf.dto.OnlineChallengeDTO;
import protobuf.dto.OnlineClTestDTO;
import protobuf.dto.OnlineLoadPlayUserDTO;
import protobuf.dto.OnlineQuitRoomDTO;

public final class PianoPlay extends OLBaseActivity implements MidiDeviceUtil.MidiDeviceListener {
    public HorizontalListView horizontalListView;
    public TextView showHideGrade;
    private boolean isOpenRecord;
    public PianoPlayHandler pianoPlayHandler = new PianoPlayHandler(this);
    public View pausedPlay;
    public int times;
    public String songsName;
    public TextView songName;
    public TextView rightHandDegreeTextView;
    public TextView highScoreTextView;
    public ListView gradeListView;
    public TextView finishSongName;
    public List<Bundle> gradeList = new ArrayList<>();
    private LayoutInflater layoutinflater;
    public PlayKeyBoardView playKeyBoardView;
    public boolean isShowingSongsInfo;
    public Bundle roomBundle;
    public boolean isPlayingStart;
    public boolean isBack;
    public boolean isPlaying;
    public ImageButton startPlayButton;
    public PlayView playView;
    private double onlineRightHandDegree;
    private int score;
    public View finishView;
    private View miniScoreView;
    public LayoutParams layoutParams2;
    private ProgressBar progressbar;
    private Bundle hallBundle;
    private boolean recordStart;
    private String recordWavPath;
    private int roomMode;
    private LayoutParams layoutparams;
    private double localRightHandDegree;
    private double localLeftHandDegree;
    private int localSongsTime;
    public int playKind;
    private final Map<Byte, Pair<Byte, Long>> playingPitchMap = new ConcurrentHashMap<>();

    private List<Bundle> sortByField(List<Bundle> list, String field) {
        if (list != null && !list.isEmpty()) {
            Collections.sort(list, (o1, o2) -> Integer.valueOf((String) o2.get(field)).compareTo(Integer.valueOf((String) o1.get(field))));
        }
        return list;
    }

    public void playTypeAndShowHandle(int i, boolean z) {
        if (z) {  // 仅显示对话框就行了，其他不做不分析，用于按下后退键时
            if (i == 0) {
                if (rightHandDegreeTextView == null) {
                    rightHandDegreeTextView = findViewById(R.id.m_nandu);
                }
                rightHandDegreeTextView.setVisibility(View.VISIBLE);
            } else {
                rightHandDegreeTextView = findViewById(R.id.m_nandu);
                rightHandDegreeTextView.setVisibility(View.GONE);
            }
            highScoreTextView.setVisibility(View.VISIBLE);
            startPlayButton.setVisibility(View.VISIBLE);
            songName.setText(songsName);
            return;
        }
        isShowingSongsInfo = true;
        if (pausedPlay.getParent() != null) {
            ((ViewGroup) (pausedPlay.getParent())).removeView(pausedPlay);
        }
        addContentView(pausedPlay, layoutParams2);
        songName = findViewById(R.id.m_name);
        progressbar = findViewById(R.id.m_progress);
        rightHandDegreeTextView = findViewById(R.id.m_nandu);
        TextView leftHandDegreeTextView = findViewById(R.id.l_nandu);
        TextView songLengthTextView = findViewById(R.id.time_mid);
        highScoreTextView = findViewById(R.id.m_score);
        highScoreTextView.setText("最高纪录:" + score);
        startPlayButton = findViewById(R.id.p_start);
        songName.setText(songsName);
        switch (i) {
            case 0:    // 本地
                startPlayButton.setOnClickListener(v -> {
                    rightHandDegreeTextView.setVisibility(View.GONE);
                    highScoreTextView.setVisibility(View.GONE);
                    startPlayButton.setVisibility(View.GONE);
                    playStartHandle(false);
                });
                rightHandDegreeTextView.setText("右手难度:" + localRightHandDegree);
                leftHandDegreeTextView.setText("左手难度:" + localLeftHandDegree);
                String str1 = localSongsTime / 60 >= 10 ? "" + localSongsTime / 60 : "0" + localSongsTime / 60;
                String str2 = localSongsTime % 60 >= 10 ? "" + localSongsTime % 60 : "0" + localSongsTime % 60;
                songLengthTextView.setText("曲目时长:" + str1 + ":" + str2);
                return;
            case 1:    // 在线曲库
                startPlayButton.setOnClickListener(v -> {
                    rightHandDegreeTextView.setVisibility(View.GONE);
                    highScoreTextView.setVisibility(View.GONE);
                    startPlayButton.setVisibility(View.GONE);
                    playStartHandle(false);
                });
                songLengthTextView.setText("难度:" + onlineRightHandDegree);
                rightHandDegreeTextView.setVisibility(View.GONE);
                leftHandDegreeTextView.setVisibility(View.GONE);
                return;
            case 2:    // 联网对战
                songName.setText("请稍后...");
                progressbar.setVisibility(View.VISIBLE);
                rightHandDegreeTextView.setVisibility(View.GONE);
                leftHandDegreeTextView.setVisibility(View.GONE);
                highScoreTextView.setVisibility(View.GONE);
                startPlayButton.setVisibility(View.GONE);
                songLengthTextView.setVisibility(View.GONE);
                leftHandDegreeTextView.setVisibility(View.GONE);
                if (miniScoreView.getParent() != null) {
                    ((ViewGroup) (miniScoreView.getParent())).removeView(miniScoreView);
                }
                addContentView(miniScoreView, layoutparams);
                miniScoreView.setVisibility(View.VISIBLE);
                if (finishView.getParent() != null) {
                    ((ViewGroup) (finishView.getParent())).removeView(finishView);
                }
                addContentView(finishView, layoutParams2);
                finishView.findViewById(R.id.ol_ok).setOnClickListener(v -> {
                    Intent intent = new Intent(PianoPlay.this, OLPlayRoom.class);
                    intent.putExtras(roomBundle);
                    startActivity(intent);
                    finish();
                });
                finishView.findViewById(R.id.ol_share).setOnClickListener(v -> ShareUtil.share(this));
                finishView.setVisibility(View.GONE);
                sendMsg(OnlineProtocolType.LOAD_PLAY_USER, OnlineLoadPlayUserDTO.getDefaultInstance());
                songName.setOnClickListener(v -> sendMsg(OnlineProtocolType.LOAD_PLAY_USER, OnlineLoadPlayUserDTO.getDefaultInstance()));
                return;
            case 3:    // 考级
                songName.setText("请稍后...");
                progressbar.setVisibility(View.VISIBLE);
                rightHandDegreeTextView.setVisibility(View.GONE);
                highScoreTextView.setVisibility(View.GONE);
                startPlayButton.setVisibility(View.GONE);
                songLengthTextView.setVisibility(View.GONE);
                leftHandDegreeTextView.setVisibility(View.GONE);
                if (miniScoreView.getParent() != null) {
                    ((ViewGroup) (miniScoreView.getParent())).removeView(miniScoreView);
                }
                addContentView(miniScoreView, layoutparams);
                miniScoreView.setVisibility(View.VISIBLE);
                OnlineClTestDTO.Builder builder = OnlineClTestDTO.newBuilder();
                builder.setType(2);
                sendMsg(OnlineProtocolType.CL_TEST, builder.build());
                return;
            case 4:    // 挑战
                songName.setText("请稍后...");
                progressbar.setVisibility(View.VISIBLE);
                rightHandDegreeTextView.setVisibility(View.GONE);
                highScoreTextView.setVisibility(View.GONE);
                startPlayButton.setVisibility(View.GONE);
                songLengthTextView.setVisibility(View.GONE);
                leftHandDegreeTextView.setVisibility(View.GONE);
                if (miniScoreView.getParent() != null) {
                    ((ViewGroup) (miniScoreView.getParent())).removeView(miniScoreView);
                }
                addContentView(miniScoreView, layoutparams);
                miniScoreView.setVisibility(View.VISIBLE);
                OnlineChallengeDTO.Builder builder1 = OnlineChallengeDTO.newBuilder();
                builder1.setType(3);
                sendMsg(OnlineProtocolType.CHALLENGE, builder1.build());
                return;
            default:
        }
    }

    public void sendMsg(int type, MessageLite msg) {
        if (OnlineUtil.getConnectionService() != null) {
            OnlineUtil.getConnectionService().writeData(type, msg);
        } else {
            Toast.makeText(this, "连接已断开，请重新登录", Toast.LENGTH_SHORT).show();
        }
    }

    public void updateMiniScore(HorizontalListView listView, List<Bundle> list) {
        MiniScoreAdapter miniScoreAdapter = (MiniScoreAdapter) listView.getAdapter();
        List<Bundle> bundleList = sortByField(list, "M");
        if (miniScoreAdapter == null) {
            listView.setAdapter(new MiniScoreAdapter(bundleList, layoutinflater, roomMode));
            return;
        }
        miniScoreAdapter.changeList(bundleList);
        miniScoreAdapter.notifyDataSetChanged();
    }

    public void playStartHandle(boolean hasTimer) {
        progressbar.setVisibility(View.GONE);
        Message obtainMessage = pianoPlayHandler.obtainMessage();
        if (hasTimer) {  // 联网模式发321倒计时器
            obtainMessage.what = 7;
            Timer timer = new Timer();
            timer.schedule(new StartPlayTimerTask(this, obtainMessage, timer), 0, 1000);
        } else {  // 本地模式直接开始
            obtainMessage.what = 7;
            obtainMessage.arg1 = 0;
            pianoPlayHandler.handleMessage(obtainMessage);
        }
        if (isOpenRecord && !recordStart) {
            recordStart = true;
            SoundEngineUtil.setRecordFilePath(recordWavPath);
            SoundEngineUtil.setRecord(true);
            Toast.makeText(this, "开始录音...", Toast.LENGTH_SHORT).show();
        }
    }

    public void bindAdapter(ListView listView, List<Bundle> list) {
        String str = "SC";
        if (roomMode > 0) {
            str = "E";
        }
        listView.setAdapter(new FinishScoreAdapter(sortByField(list, str), layoutinflater, roomMode));
    }

    public void recordFinish() {
        if (recordStart) {
            isOpenRecord = false;
            recordStart = false;
            SoundEngineUtil.setRecord(false);
            File srcFile = new File(recordWavPath.replace(".raw", ".wav"));
            Uri desUri = FileUtil.INSTANCE.getOrCreateFileByUriFolder(this,
                    GlobalSetting.INSTANCE.getRecordsSavePath(), "Records", songsName + ".wav");
            if (FileUtil.INSTANCE.moveFileToUri(this, srcFile, desUri)) {
                Toast.makeText(this, "录音完毕，文件已存储", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "录音文件存储失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int i, int i2, Intent intent) {
        if (i2 == -1) {
            if (isOpenRecord) {
                recordFinish();
            }
            finish();
            super.onActivityResult(i, i2, intent);
        }
    }

    @Override
    public void onBackPressed() {
        JPDialogBuilder jpDialogBuilder = new JPDialogBuilder(this);
        switch (playKind) {
            case 0:
                playTypeAndShowHandle(playKind, true);
                if (!isShowingSongsInfo) {
                    playView.startFirstNoteTouching = false;
                    isShowingSongsInfo = true;
                    pausedPlay.setVisibility(View.VISIBLE);
                    return;
                } else {
                    isShowingSongsInfo = false;
                    pausedPlay.setVisibility(View.GONE);
                    playView.startFirstNoteTouching = false;
                    isPlayingStart = false;
                    isBack = true;
                    finish();
                }
                return;
            case 1:
                playTypeAndShowHandle(playKind, true);
                if (!isShowingSongsInfo) {
                    playView.startFirstNoteTouching = false;
                    isShowingSongsInfo = true;
                    pausedPlay.setVisibility(View.VISIBLE);
                } else {
                    isShowingSongsInfo = false;
                    pausedPlay.setVisibility(View.GONE);
                    playView.startFirstNoteTouching = false;
                    isPlayingStart = false;
                    isBack = true;
                    finish();
                }
                return;
            case 2:
                jpDialogBuilder.setTitle("提示");
                jpDialogBuilder.setMessage("退出弹奏并返回大厅?");
                jpDialogBuilder.setFirstButton("确定", (dialog, which) -> {
                    isShowingSongsInfo = false;
                    playView.startFirstNoteTouching = false;
                    isPlayingStart = false;
                    isBack = true;
                    dialog.dismiss();
                    finish();
                });
                jpDialogBuilder.setSecondButton("取消", (dialog, which) -> dialog.dismiss());
                jpDialogBuilder.setCancelableFalse();
                jpDialogBuilder.buildAndShowDialog();
                return;
            case 3:
                jpDialogBuilder.setTitle("提示");
                jpDialogBuilder.setMessage("退出考级并返回大厅?");
                jpDialogBuilder.setFirstButton("确定", (dialog, which) -> {
                    isShowingSongsInfo = false;
                    playView.startFirstNoteTouching = false;
                    isPlayingStart = false;
                    isBack = true;
                    dialog.dismiss();
                    finish();
                });
                jpDialogBuilder.setSecondButton("取消", (dialog, which) -> dialog.dismiss());
                jpDialogBuilder.setCancelableFalse();
                jpDialogBuilder.buildAndShowDialog();
                return;
            case 4:
                jpDialogBuilder.setTitle("提示");
                jpDialogBuilder.setMessage("您将损失一次挑战机会!退出挑战并返回大厅?");
                jpDialogBuilder.setFirstButton("确定", (dialog, which) -> {
                    isShowingSongsInfo = false;
                    playView.startFirstNoteTouching = false;
                    isPlayingStart = false;
                    isBack = true;
                    dialog.dismiss();
                    finish();
                });
                jpDialogBuilder.setSecondButton("取消", (dialog, which) -> dialog.dismiss());
                jpDialogBuilder.setCancelableFalse();
                jpDialogBuilder.buildAndShowDialog();
                return;
            default:
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkAnJian();
        layoutinflater = LayoutInflater.from(this);
        isShowingSongsInfo = false;
        isBack = false;
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        GlobalSetting.INSTANCE.loadSettings(this, true);
        pausedPlay = LayoutInflater.from(this).inflate(R.layout.paused_play, null);
        layoutParams2 = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams2.gravity = android.view.Gravity.CENTER;
        layoutparams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutparams.topMargin = 0;
        layoutparams.leftMargin = 0;
        Bundle extras = getIntent().getExtras();
        playKind = extras.getInt("head");
        switch (playKind) {
            case 0:    //本地模式
                String songsPath = extras.getString("path");
                songsName = extras.getString("name");
                localRightHandDegree = BigDecimal.valueOf(extras.getDouble("nandu")).setScale(1, RoundingMode.HALF_UP).doubleValue();
                localLeftHandDegree = BigDecimal.valueOf(extras.getDouble("leftnandu")).setScale(1, RoundingMode.HALF_UP).doubleValue();
                localSongsTime = extras.getInt("songstime");
                score = extras.getInt("score");
                isOpenRecord = extras.getBoolean("isrecord");
                int hand = extras.getInt("hand");
                playView = new PlayView(this, songsPath, this, localRightHandDegree, localLeftHandDegree, score, playKind, hand, 30, localSongsTime, 0);
                break;
            case 1:    //在线曲库
                songsName = extras.getString("songName");
                onlineRightHandDegree = BigDecimal.valueOf(extras.getDouble("degree")).setScale(1, RoundingMode.HALF_UP).doubleValue();
                score = extras.getInt("topScore");
                String songId = extras.getString("songID");
                byte[] byteArray = extras.getByteArray("songBytes");
                playView = new PlayView(this, byteArray, this, onlineRightHandDegree, score, 1, 0, songId);
                break;
            case 2:    //房间对战
                roomBundle = extras.getBundle("bundle");
                hallBundle = extras.getBundle("bundleHall");
                songsPath = extras.getString("path");
                songsName = extras.getString("name");
                int tune = extras.getInt("diao");
                hand = extras.getInt("hand");
                roomMode = extras.getInt("roomMode");
                miniScoreView = layoutinflater.inflate(R.layout.ol_play_score_ranking, null);
                horizontalListView = miniScoreView.findViewById(R.id.ol_score_list);
                showHideGrade = miniScoreView.findViewById(R.id.ol_score_button);
                int visibility = horizontalListView.getVisibility();
                if (visibility == View.VISIBLE) {
                    showHideGrade.setText("隐藏成绩");
                } else if (visibility == View.GONE) {
                    showHideGrade.setText("显示成绩");
                }
                showHideGrade.setOnClickListener(new ShowOrHideMiniGradeClick(this));
                gradeList.clear();
                finishView = layoutinflater.inflate(R.layout.ol_room_play_finish, null);
                finishSongName = finishView.findViewById(R.id.ol_song_name);
                gradeListView = finishView.findViewById(R.id.ol_finish_list);
                gradeListView.setCacheColorHint(Color.TRANSPARENT);
                playView = new PlayView(this, songsPath, this, onlineRightHandDegree, onlineRightHandDegree, score, playKind, hand, 30, 0, tune);
                break;
            case 3:    //大厅考级
            case 4:    //挑战
                roomBundle = extras.getBundle("bundle");
                hallBundle = extras.getBundle("bundleHall");
                songsName = extras.getString("name");
                times = extras.getInt("times");
                hand = extras.getInt("hand");
                miniScoreView = layoutinflater.inflate(R.layout.ol_play_score_ranking, null);
                horizontalListView = miniScoreView.findViewById(R.id.ol_score_list);
                showHideGrade = miniScoreView.findViewById(R.id.ol_score_button);
                showHideGrade.setText("");
                showHideGrade.setOnClickListener(new ShowOrHideMiniGradeClick(this));
                gradeList.clear();
                String songBytes = extras.getString("songBytes");
                playView = new PlayView(this, songBytes.getBytes(), this, onlineRightHandDegree, score, playKind, hand, "");
                break;
        }
        if (isOpenRecord) {
            recordWavPath = getFilesDir().getAbsolutePath() + "/Records/" + songsName + ".raw";
        }
    }

    private void checkAnJian() {
        final PackageManager packageManager = getPackageManager();
        final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        final List<ResolveInfo> apps = packageManager.queryIntentActivities(mainIntent, 0);
        for (ResolveInfo app : apps) {
            String name = app.activityInfo.packageName;
            if (name.contains("nknpngmlmnmhmpmh") || name.contains("mobileanjian") || name.contains("Touchelper")) {
                finish();
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (isOpenRecord) {
            recordFinish();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && getPackageManager().hasSystemFeature(PackageManager.FEATURE_MIDI)) {
            MidiDeviceUtil.removeMidiConnectionListener();
        }
        SoundEngineUtil.stopPlayAllSounds();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Bundle bundle = new Bundle();
        Intent intent;
        switch (playKind) {
            case 0:
            case 1:
                isShowingSongsInfo = false;
                if (playView != null) {
                    playView.startFirstNoteTouching = false;
                }
                isPlayingStart = false;
                if (!isPlaying) {
                    finish();
                    return;
                }
                return;
            case 2:
                if (!isPlaying) {
                    isShowingSongsInfo = false;
                    if (playView != null) {
                        playView.startFirstNoteTouching = false;
                    }
                    isPlayingStart = false;
                    isBack = true;
                    sendMsg(OnlineProtocolType.QUIT_ROOM, OnlineQuitRoomDTO.getDefaultInstance());
                    if (isOnline()) {
                        intent = new Intent(this, OLPlayHall.class);
                        bundle.putString("hallName", hallBundle.getString("hallName"));
                        bundle.putByte("hallID", hallBundle.getByte("hallID"));
                        intent.putExtras(bundle);
                        startActivity(intent);
                        return;
                    }
                    return;
                }
                return;
            case 3:
                isShowingSongsInfo = false;
                if (playView != null) {
                    playView.startFirstNoteTouching = false;
                }
                isPlayingStart = false;
                isBack = true;
                if (isOnline()) {
                    intent = new Intent(this, OLPlayHall.class);
                    bundle.putString("hallName", hallBundle.getString("hallName"));
                    bundle.putByte("hallID", hallBundle.getByte("hallID"));
                    intent.putExtras(bundle);
                    startActivity(intent);
                    return;
                }
                return;
            case 4:
                isShowingSongsInfo = false;
                if (playView != null) {
                    playView.startFirstNoteTouching = false;
                }
                isPlayingStart = false;
                isBack = true;
                if (isOnline()) {
                    intent = new Intent(this, OLChallenge.class);
                    bundle.putString("hallName", hallBundle.getString("hallName"));
                    bundle.putByte("hallID", hallBundle.getByte("hallID"));
                    intent.putExtras(bundle);
                    startActivity(intent);
                    return;
                }
                return;
            default:
        }
    }

    @Override
    protected void onResume() {
        if (isBack) {
            finish();
            isBack = false;
        }
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isBack) {
            isBack = false;
            isPlayingStart = false;
            isPlaying = false;
            finish();
        }
    }

    @Override
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void onMidiMessageReceive(byte pitch, byte volume) {
        int trueNote = pitch % 12 + 12 == playView.noteRightValue ? 12 : pitch % 12;
        if (volume > 0) {
            SoundEngineUtil.playSound((byte) (trueNote + playView.noteMod12 * 12), playView.volume0);
            playView.judgeTouchNote(pitch % 12 + playView.noteMod12 * 12, true);
            if (playView.currentPlayNote != null) {
                playView.positionAdd15AddAnim = playView.currentPlayNote.posiAdd15AddAnim;
            }
            playKeyBoardView.touchNoteMap.put(trueNote, 0);
            updateKeyboardPrefer();
        } else {
            SoundEngineUtil.stopPlaySound((byte) (trueNote + playView.noteMod12 * 12));
            if (pitch % 12 == 0) {
                playKeyBoardView.touchNoteMap.remove(12);
            }
            playKeyBoardView.touchNoteMap.remove(pitch % 12);
            updateKeyboardPrefer();
        }
    }

    public void updateKeyboardPrefer() {
        if (GlobalSetting.INSTANCE.getKeyboardPrefer()) {
            Message obtainMessage = pianoPlayHandler.obtainMessage();
            obtainMessage.what = 4;
            pianoPlayHandler.handleMessage(obtainMessage);
        }
    }

    public void playNoteSoundHandle(byte track, byte pitch, byte volume) {
        if (pitch < MidiUtil.MIN_PIANO_MIDI_PITCH || pitch > MidiUtil.MAX_PIANO_MIDI_PITCH) {
            return;
        }
        Iterator<Map.Entry<Byte, Pair<Byte, Long>>> iterator = playingPitchMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Byte, Pair<Byte, Long>> entry = iterator.next();
            if (entry.getValue().first == track && System.currentTimeMillis() - entry.getValue().second >= PmSongUtil.PM_GLOBAL_SPEED) {
                SoundEngineUtil.stopPlaySound(entry.getKey());
                iterator.remove();
            }
        }
        SoundEngineUtil.playSound(pitch, volume);
        playingPitchMap.put(pitch, Pair.create(track, System.currentTimeMillis()));
    }
}
