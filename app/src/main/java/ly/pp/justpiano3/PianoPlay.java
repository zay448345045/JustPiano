package ly.pp.justpiano3;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.media.AudioManager;
import android.media.midi.MidiReceiver;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Timer;

public final class PianoPlay extends BaseActivity implements MidiConnectionListener {
    public byte hallID;
    public TextView l_nandu;
    public TextView time_mid;
    HorizontalListView horizontalListView;
    TextView showHideGrade;
    Map userMap = null;
    boolean isOpenRecord;
    PianoPlayHandler pianoPlayHandler = new PianoPlayHandler(this);
    View f4591J;
    int times;
    String songsName;
    TextView songName;
    TextView f4596O;
    TextView f4597P;
    ListView gradeListView;
    TextView finishSongName;
    List<Bundle> gradeList = new ArrayList<>();
    LayoutInflater layoutinflater;
    KeyBoardView keyboardview;
    boolean isShowingSongsInfo;
    Bundle roomBundle;
    boolean isPlayingStart;
    boolean isBack;
    boolean f4620k;
    JPProgressBar jpprogressbar;
    ImageButton startPlayButton;
    PlayView playView;
    double nandu;
    int score;
    JPApplication jpapplication;
    MidiReceiver midiFramer;
    View finishView;
    private View f4592K;
    private LayoutParams layoutparams2;
    private ConnectionService connectionService;
    private ProgressBar progressbar;
    private Bundle hallBundle;
    private boolean recordStart;
    private String recordWavPath;
    private int roomMode = 0;
    private LayoutParams layoutparams;
    private double localRNandu;
    private double localLNandu;
    private int localSongsTime;
    private int playKind;

    private List<Bundle> m3783a(List<Bundle> list, String str) {
        if (list != null && !list.isEmpty()) {
            Collections.sort(list, (o1, o2) -> Integer.valueOf((String) o2.get(str)).compareTo(Integer.valueOf((String) o1.get(str))));
        }
        return list;
    }

    private void m3785a(int i, boolean z) {
        if (z) {  //仅显示对话框就行了，其他不做不分析，用于按下后退键时
            if (i == 0) {
                f4596O.setVisibility(View.VISIBLE);
            } else {
                f4596O = findViewById(R.id.m_nandu);
                f4596O.setVisibility(View.GONE);
            }
            f4597P.setVisibility(View.VISIBLE);
            startPlayButton.setVisibility(View.VISIBLE);
            songName.setText(songsName);
            return;
        }
        isShowingSongsInfo = true;
        addContentView(f4591J, layoutparams2);
        songName = findViewById(R.id.m_name);
        progressbar = findViewById(R.id.m_progress);
        f4596O = findViewById(R.id.m_nandu);
        l_nandu = findViewById(R.id.l_nandu);
        time_mid = findViewById(R.id.time_mid);
        f4597P = findViewById(R.id.m_score);
        f4597P.setText("最高纪录:" + score);
        startPlayButton = findViewById(R.id.p_start);
        songName.setText(songsName);
        switch (i) {
            case 0:    //本地
                startPlayButton.setOnClickListener(v -> {
                    f4596O.setVisibility(View.GONE);
                    f4597P.setVisibility(View.GONE);
                    startPlayButton.setVisibility(View.GONE);
                    mo2906a(false);
                });
                f4596O.setText("右手难度:" + localRNandu);
                l_nandu.setText("左手难度:" + localLNandu);
                String str1 = localSongsTime / 60 >= 10 ? "" + localSongsTime / 60 : "0" + localSongsTime / 60;
                String str2 = localSongsTime % 60 >= 10 ? "" + localSongsTime % 60 : "0" + localSongsTime % 60;
                time_mid.setText("曲目时长:" + str1 + ":" + str2);
                return;
            case 1:    //在线曲库
                startPlayButton.setOnClickListener(v -> {
                    f4596O.setVisibility(View.GONE);
                    f4597P.setVisibility(View.GONE);
                    startPlayButton.setVisibility(View.GONE);
                    mo2906a(false);
                });
                time_mid.setText("难度:" + nandu);
                f4596O.setVisibility(View.GONE);
                l_nandu.setVisibility(View.GONE);
                return;
            case 2:    //联网对战
                songName.setText("请稍后...");
                progressbar.setVisibility(View.VISIBLE);
                f4596O.setVisibility(View.GONE);
                l_nandu.setVisibility(View.GONE);
                f4597P.setVisibility(View.GONE);
                startPlayButton.setVisibility(View.GONE);
                time_mid.setVisibility(View.GONE);
                l_nandu.setVisibility(View.GONE);
                addContentView(f4592K, layoutparams);
                f4592K.setVisibility(View.VISIBLE);
                addContentView(finishView, layoutparams2);
                ImageButton f4582A = finishView.findViewById(R.id.ol_ok);
                finishView.findViewById(R.id.share_score);
                f4582A.setOnClickListener(v -> {
                    Intent intent = new Intent(PianoPlay.this, OLPlayRoom.class);
                    intent.putExtras(roomBundle);
                    startActivity(intent);
                    finish();
                });
                finishView.setVisibility(View.GONE);
                sendMsg((byte) 23, (byte) 0, "", null);
                songName.setOnClickListener(v -> sendMsg((byte) 23, (byte) 0, "", null));
                return;
            case 3:    //考级
                songName.setText("请稍后...");
                progressbar.setVisibility(View.VISIBLE);
                f4596O.setVisibility(View.GONE);
                f4597P.setVisibility(View.GONE);
                startPlayButton.setVisibility(View.GONE);
                time_mid.setVisibility(View.GONE);
                l_nandu.setVisibility(View.GONE);
                addContentView(f4592K, layoutparams);
                f4592K.setVisibility(View.VISIBLE);
                JSONObject jSONObject = new JSONObject();
                try {
                    jSONObject.put("T", 2);
                    sendMsg((byte) 40, (byte) 0, jSONObject.toString(), null);
                    return;
                } catch (JSONException e) {
                    e.printStackTrace();
                    return;
                }
            case 4:    //挑战
                songName.setText("请稍后...");
                progressbar.setVisibility(View.VISIBLE);
                f4596O.setVisibility(View.GONE);
                f4597P.setVisibility(View.GONE);
                startPlayButton.setVisibility(View.GONE);
                time_mid.setVisibility(View.GONE);
                l_nandu.setVisibility(View.GONE);
                addContentView(f4592K, layoutparams);
                f4592K.setVisibility(View.VISIBLE);
                try {
                    jSONObject = new JSONObject();
                    jSONObject.put("K", 3);
                    sendMsg((byte) 16, (byte) 0, jSONObject.toString(), null);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return;
            default:
        }
    }

    public void m3794f() {
        f4591J = LayoutInflater.from(this).inflate(R.layout.pusedplay, null);
        layoutparams2 = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutparams2.gravity = android.view.Gravity.CENTER;
        layoutparams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutparams.topMargin = 0;
        layoutparams.leftMargin = 0;
        Bundle extras = getIntent().getExtras();
        playKind = extras.getInt("head");
        String str;
        switch (playKind) {
            case 0:    //本地模式
                String songsPath = extras.getString("path");
                songsName = extras.getString("name");
                localRNandu = BigDecimal.valueOf(extras.getDouble("nandu")).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
                localLNandu = BigDecimal.valueOf(extras.getDouble("leftnandu")).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
                localSongsTime = extras.getInt("songstime");
                score = extras.getInt("score");
                isOpenRecord = extras.getBoolean("isrecord");
                int hand = extras.getInt("hand");
                str = songsPath;
                playView = new PlayView(jpapplication, this, str, this, localRNandu, localLNandu, score, playKind, hand, 30, localSongsTime, 0);
                break;
            case 1:    //在线曲库
                songsName = extras.getString("songName");
                nandu = BigDecimal.valueOf(extras.getDouble("degree")).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
                score = extras.getInt("topScore");
                String songId = extras.getString("songID");
                byte[] byteArray = extras.getByteArray("songBytes");
                playView = new PlayView(jpapplication, this, byteArray, this, nandu, score, 1, 0, songId);
                break;
            case 2:    //房间对战
                connectionService = jpapplication.getConnectionService();
                roomBundle = extras.getBundle("bundle");
                hallBundle = extras.getBundle("bundleHall");
                hallID = hallBundle.getByte("hallID");
                userMap = jpapplication.getHashmap();
                songsPath = extras.getString("path");
                songsName = extras.getString("name");
                int diao = extras.getInt("diao");
                hand = extras.getInt("hand");
                roomMode = extras.getInt("roomMode");
                f4592K = layoutinflater.inflate(R.layout.ol_score_view, null);
                horizontalListView = f4592K.findViewById(R.id.ol_score_list);
                showHideGrade = f4592K.findViewById(R.id.ol_score_button);
                int visibility = horizontalListView.getVisibility();
                if (visibility == View.VISIBLE) {
                    showHideGrade.setText("隐藏成绩");
                } else if (visibility == View.GONE) {
                    showHideGrade.setText("显示成绩");
                }
                showHideGrade.setOnClickListener(new ShowOrHideMiniGradeClick(this));
                gradeList.clear();
                finishView = layoutinflater.inflate(R.layout.ol_finish_view, null);
                finishSongName = finishView.findViewById(R.id.ol_song_name);
                gradeListView = finishView.findViewById(R.id.ol_finish_list);
                gradeListView.setCacheColorHint(0);
                str = songsPath;
                playView = new PlayView(jpapplication, this, str, this, nandu, nandu, score, playKind, hand, 30, 0, diao);
                break;
            case 3:    //大厅考级
            case 4:    //挑战
                connectionService = jpapplication.getConnectionService();
                roomBundle = extras.getBundle("bundle");
                hallBundle = extras.getBundle("bundleHall");
                hallID = hallBundle.getByte("hallID");
                userMap = jpapplication.getHashmap();
                String songBytes = extras.getString("songBytes");
                songsName = extras.getString("name");
                times = extras.getInt("times");
                hand = extras.getInt("hand");
                f4592K = layoutinflater.inflate(R.layout.ol_score_view, null);
                horizontalListView = f4592K.findViewById(R.id.ol_score_list);
                showHideGrade = f4592K.findViewById(R.id.ol_score_button);
                showHideGrade.setText("");
                showHideGrade.setOnClickListener(new ShowOrHideMiniGradeClick(this));
                gradeList.clear();
                playView = new PlayView(jpapplication, this, songBytes.getBytes(), this, nandu, score, playKind, hand, "");
                break;
        }
        if (isOpenRecord) {
            recordWavPath = Environment.getExternalStorageDirectory() + "/JustPiano/Record/" + songsName + ".wav";
        }
        setContentView(playView);
        keyboardview = new KeyBoardView(this, playView);
        addContentView(keyboardview, layoutparams2);
        m3785a(playKind, false);
        isPlayingStart = true;
    }

    void m3802m() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        jpapplication.setHeightPixels(displayMetrics.heightPixels);
        jpapplication.setWidthPixels(displayMetrics.widthPixels);
        jpapplication.loadSettings(true);
        JPApplication.teardownAudioStreamNative();
        JPApplication.unloadWavAssetsNative();
        for (int i = 108; i >= 24; i--) {
            JPApplication.preloadSounds(i);
        }
        JPApplication.confirmLoadSounds();
    }

    public final void sendMsg(byte b, byte b2, String str, byte[] bArr) {
        if (connectionService != null) {
            connectionService.writeData(b, (byte) 0, b2, str, bArr);
        } else {
            Toast.makeText(this, "连接已断开", Toast.LENGTH_SHORT).show();
        }
    }

    public final void mo2905a(HorizontalListView listView, List<Bundle> list) {
        MiniScoreAdapter c1209io = (MiniScoreAdapter) listView.getAdapter();
        List<Bundle> a = m3783a(list, "M");
        if (c1209io == null) {
            ListAdapter c1209io2 = new MiniScoreAdapter(a, layoutinflater, roomMode);
            listView.setAdapter(c1209io2);
            return;
        }
        c1209io.mo3332a(a);
        c1209io.notifyDataSetChanged();
    }

    public final void mo2906a(boolean hasTimer) {
        progressbar.setVisibility(View.GONE);
        Message obtainMessage = pianoPlayHandler.obtainMessage();
        if (hasTimer) {  //联网模式发321倒计时器
            obtainMessage.what = 7;
            Timer timer = new Timer();
            timer.schedule(new StartPlayTimer(this, obtainMessage, timer), 0, 1000);
        } else {  //本地模式直接开始
            obtainMessage.what = 7;
            obtainMessage.arg1 = 0;
            pianoPlayHandler.handleMessage(obtainMessage);
        }
        if (isOpenRecord && !recordStart) {
            recordStart = true;
            JPApplication.setRecordFilePath(recordWavPath);
            JPApplication.setRecord(true);
            Toast.makeText(this, "开始录音...", Toast.LENGTH_SHORT).show();
        }
    }

    public final void mo2907b(ListView listView, List<Bundle> list) {
        String str = "SC";
        if (roomMode > 0) {
            str = "E";
        }
        listView.setAdapter(new FinishScoreAdapter(m3783a(list, str), layoutinflater, roomMode));
    }

    final void recordFinish() {
        if (recordStart) {
            isOpenRecord = false;
            JPApplication.setRecord(false);
            Toast.makeText(this, "录音完毕，文件已存储至SD卡\\JustPiano\\Record中", Toast.LENGTH_SHORT).show();
            recordStart = false;
        }
    }

    @Override
    protected void onActivityResult(int i, int i2, Intent intent) {
        if (i2 == -1) {
            finish();
            super.onActivityResult(i, i2, intent);
        }
    }

    @Override
    public void onBackPressed() {
        JPDialog jpdialog = new JPDialog(this);
        switch (playKind) {
            case 0:
                m3785a(playKind, true);
                if (!isShowingSongsInfo) {
                    playView.startFirstNoteTouching = false;
                    isShowingSongsInfo = true;
                    f4591J.setVisibility(View.VISIBLE);
                    return;
                } else {
                    isShowingSongsInfo = false;
                    f4591J.setVisibility(View.GONE);
                    playView.startFirstNoteTouching = false;
                    isPlayingStart = false;
                    isBack = true;
                    finish();
                }
                return;
            case 1:
                m3785a(playKind, true);
                if (!isShowingSongsInfo) {
                    playView.startFirstNoteTouching = false;
                    isShowingSongsInfo = true;
                    f4591J.setVisibility(View.VISIBLE);
                } else {
                    isShowingSongsInfo = false;
                    f4591J.setVisibility(View.GONE);
                    playView.startFirstNoteTouching = false;
                    isPlayingStart = false;
                    isBack = true;
                    finish();
                }
                return;
            case 2:
                jpdialog.setTitle("提示");
                jpdialog.setMessage("退出弹奏并返回大厅?");
                jpdialog.setFirstButton("确定", (dialog, which) -> {
                    isShowingSongsInfo = false;
                    playView.startFirstNoteTouching = false;
                    isPlayingStart = false;
                    isBack = true;
                    dialog.dismiss();
                    finish();
                });
                jpdialog.setSecondButton("取消", new DialogDismissClick());
                jpdialog.setCancelableFalse();
                jpdialog.showDialog();
                return;
            case 3:
                jpdialog.setTitle("提示");
                jpdialog.setMessage("退出考级并返回大厅?");
                jpdialog.setFirstButton("确定", (dialog, which) -> {
                    isShowingSongsInfo = false;
                    playView.startFirstNoteTouching = false;
                    isPlayingStart = false;
                    isBack = true;
                    dialog.dismiss();
                    finish();
                });
                jpdialog.setSecondButton("取消", new DialogDismissClick());
                jpdialog.setCancelableFalse();
                jpdialog.showDialog();
                return;
            case 4:
                jpdialog.setTitle("提示");
                jpdialog.setMessage("您将损失一次挑战机会!退出挑战并返回大厅?");
                jpdialog.setFirstButton("确定", (dialog, which) -> {
                    isShowingSongsInfo = false;
                    playView.startFirstNoteTouching = false;
                    isPlayingStart = false;
                    isBack = true;
                    dialog.dismiss();
                    finish();
                });
                jpdialog.setSecondButton("取消", new DialogDismissClick());
                jpdialog.setCancelableFalse();
                jpdialog.showDialog();
                return;
            default:
        }
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        activityNum = 5;
        JPStack.push(this);
        jpapplication = (JPApplication) getApplication();
        checkAnJian();
        layoutinflater = LayoutInflater.from(this);
        isShowingSongsInfo = false;
        isBack = false;
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        if (jpapplication.getHeightPixels() == 0) {
            jpprogressbar = new JPProgressBar(this);
            new PianoPlayTask(this).execute();
        } else {
            m3794f();
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
        JPStack.create();
        JPStack.pop(this);
        if (isOpenRecord) {
            recordFinish();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_MIDI)) {
                if (jpapplication.midiOutputPort != null) {
                    if (midiFramer != null) {
                        jpapplication.midiOutputPort.disconnect(midiFramer);
                        midiFramer = null;
                    }
                    jpapplication.removeMidiConnectionStart(this);
                }
            }
        }
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
                if (!f4620k) {
                    finish();
                    return;
                }
                return;
            case 2:
                if (!f4620k) {
                    isShowingSongsInfo = false;
                    if (playView != null) {
                        playView.startFirstNoteTouching = false;
                    }
                    isPlayingStart = false;
                    isBack = true;
                    sendMsg((byte) 8, (byte) 0, "", null);
                    if (!f3995a) {
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
                if (!f3995a) {
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
                if (!f3995a) {
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
            f4620k = false;
            finish();
        }
    }

    @Override
    public void onMidiConnect() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_MIDI)) {
                if (jpapplication.midiOutputPort != null) {
                    if (midiFramer == null) {
                        midiFramer = new MidiFramer(new MidiReceiver() {
                            @Override
                            public void onSend(byte[] data, int offset, int count, long timestamp) {
                                midiConnectHandle(data);
                            }
                        });
                    }
                    jpapplication.midiOutputPort.connect(midiFramer);
                }
            }
        }
    }

    @Override
    public void onMidiDisconnect() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_MIDI)) {
                if (midiFramer != null) {
                    jpapplication.midiOutputPort.disconnect(midiFramer);
                    midiFramer = null;
                }
            }
        }
    }

    public void midiConnectHandle(byte[] data) {
        byte command = (byte) (data[0] & MidiConstants.STATUS_COMMAND_MASK);
        int touchNoteNum = data[1] % 12;
        if (command == MidiConstants.STATUS_NOTE_ON && data[2] > 0) {
            if (playView.currentPlayNote != null) {
                playView.posiAdd15AddAnim = playView.currentPlayNote.posiAdd15AddAnim;
            }
            int trueNote = playView.midiJudgeAndPlaySound(touchNoteNum);
            keyboardview.touchNoteSet.put(trueNote, 0);
            updateKeyboardPrefer();
        } else if (command == MidiConstants.STATUS_NOTE_OFF
                || (command == MidiConstants.STATUS_NOTE_ON && data[2] == 0)) {
            if (touchNoteNum == 0) {
                keyboardview.touchNoteSet.remove(12);
            }
            keyboardview.touchNoteSet.remove(touchNoteNum);
            updateKeyboardPrefer();
        }
    }

    public void updateKeyboardPrefer() {
        if (jpapplication.hasKeyboardPerfer()) {
            Message obtainMessage = pianoPlayHandler.obtainMessage();
            obtainMessage.what = 4;
            pianoPlayHandler.handleMessage(obtainMessage);
        }
    }
}
