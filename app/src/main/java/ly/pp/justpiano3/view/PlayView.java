package ly.pp.justpiano3.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Message;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import ly.pp.justpiano3.JPApplication;
import ly.pp.justpiano3.activity.local.PianoPlay;
import ly.pp.justpiano3.activity.local.PlayFinish;
import ly.pp.justpiano3.constant.OnlineProtocolType;
import ly.pp.justpiano3.database.entity.Song;
import ly.pp.justpiano3.entity.GlobalSetting;
import ly.pp.justpiano3.entity.PmSongData;
import ly.pp.justpiano3.enums.LocalPlayModeEnum;
import ly.pp.justpiano3.listener.touch.TouchNotes;
import ly.pp.justpiano3.thread.LoadBackgroundsThread;
import ly.pp.justpiano3.thread.ShowScoreAndLevelsThread;
import ly.pp.justpiano3.utils.EncryptUtil;
import ly.pp.justpiano3.utils.GZIPUtil;
import ly.pp.justpiano3.utils.ImageLoadUtil;
import ly.pp.justpiano3.utils.PmSongUtil;
import ly.pp.justpiano3.utils.ThreadPoolUtil;
import ly.pp.justpiano3.utils.ViewUtil;
import ly.pp.justpiano3.view.play.PlayNote;
import ly.pp.justpiano3.view.play.ShowTouchNotesLevel;
import protobuf.dto.OnlineChallengeDTO;
import protobuf.dto.OnlineClTestDTO;
import protobuf.dto.OnlineMiniGradeDTO;
import protobuf.dto.OnlinePlayFinishDTO;

/**
 * 陈年屎山代码，谨慎修改
 */
public final class PlayView extends SurfaceView implements Callback {
    /**
     * 整体进度
     */
    public int progress;
    public int whiteKeyHeight;
    public float blackKeyWidth;
    public float blackKeyHeight;
    public float halfHeightSub20;
    public float halfHeightSub10;
    public float whiteKeyHeightAdd90;
    // 总分显示向左偏移的数量，解决曲面屏右上角总分显示不全的问题
    private static final int TOTAL_SCORE_SHOW_OFFSET = 50;
    public Bitmap whiteKeyRightImage;
    public Bitmap whiteKeyMiddleImage;
    public Bitmap whiteKeyLeftImage;
    public Bitmap blackKeyImage;
    public Bitmap fireImage;
    public Bitmap longKeyboardImage;
    public int noteMod12 = 4;
    public boolean startFirstNoteTouching;
    public SurfaceHolder surfaceholder;
    public PianoPlay pianoPlay;
    public PlayNote currentPlayNote;
    public int screenWidth;
    public boolean isTouchRightNote = true;
    public Bitmap keyboardImage;
    public Bitmap nullImage;
    public byte[] noteArray;
    public byte[] volumeArray;
    public Bitmap backgroundImage;
    public Bitmap barImage;
    public float positionAdd15AddAnim;
    public Bitmap missImage;
    public Bitmap perfectImage;
    public Bitmap coolImage;
    public Bitmap greatImage;
    public Bitmap badImage;
    public Bitmap noteImage;
    public Bitmap blackNoteImage;
    public Bitmap roughLineImage;
    public String songsName;
    private Bitmap playNoteImage;
    private Bitmap practiseNoteImage;
    private int gameType;
    private Bitmap progressBarImage;
    private Bitmap progressBarBaseImage;
    public byte volume0;
    private int score;
    private LoadBackgroundsThread loadBackgroundsThread;
    private PlayNote judgingNote;
    private int pm_2;
    private int position;
    private boolean hasTouched;
    private int progressBarHeight;
    private int progressBarWidth;
    private String songId;
    private float perfectStandard;
    private float coolStandard;
    private float greatStandard;
    private float missStandard;
    private int comboNum;
    private int topComboNum;
    private int perfectNum;
    private int coolNum;
    private int greatNum;
    private int badNum;
    private int missNum;
    private final List<PlayNote> notesList = new CopyOnWriteArrayList<>();
    private final List<ShowTouchNotesLevel> touchNotesList = new ArrayList<>();
    private ShowScoreAndLevelsThread showScoreAndLevelsThread;
    private double rightHandDegree;
    private double leftHandDegree;
    private int songsTime;
    private int screenHeight;
    private String songsPath;
    private Rect f4769bA;
    private Rect f4770bB;
    private RectF f4771bC;
    private RectF scoreRectF;
    private Rect f4773bE;
    private RectF f4774bF;
    private int uploadTime;
    private List<Byte> uploadTouchStatusList;
    private byte[] uploadTimeArray;
    private int uploadNoteIndex;
    private final List<PlayNote> deleteNotesArray = new ArrayList<>();
    private final List<Integer> currentTotalScoreNumberList = new ArrayList<>();
    private int handValue;
    private byte[] tickArray;
    private byte[] trackArray;
    private Rect f4801bz;
    private final Paint line = new Paint();
    public float widthDiv8;
    private float width2Div8;
    private float width4Div8;
    private float width5Div8;
    private float width6Div8;
    private float width8Div8;
    public int noteRightValue;
    private Bitmap scoreImage;
    private Bitmap scoreNumImage;
    private Bitmap xImage;
    private Bitmap maxImage;
    private int tune;

    public PlayView(Context context) {
        super(context);
    }

    /**
     * 本地弹奏或房间对战-初始化弹奏view
     */
    public PlayView(Context context, String str, PianoPlay pianoPlay, double d1, double d2, int i, int kind, int i3, int i4, int i5, int tune) {
        super(context);
        this.pianoPlay = pianoPlay;
        handValue = (i3 + 20) % 2;
        gameType = kind;
        uploadTime = i4;
        songsPath = str;
        rightHandDegree = d1;
        leftHandDegree = d2;
        score = i;
        songsTime = i5;
        this.tune = tune;
        try {
            loadPm(context, str, tune);
        } catch (Exception e) {
            e.printStackTrace();
            pianoPlay.finish();
        }
    }

    /**
     * 在线曲库弹奏或大厅考级或挑战-初始化弹奏view
     */
    public PlayView(Context context, byte[] bArr, PianoPlay pianoPlay, double d, int i, int kind, int i3, String songId) {
        super(context);
        this.pianoPlay = pianoPlay;
        handValue = (i3 + 20) % 2;
        gameType = kind;
        this.songId = songId;
        rightHandDegree = d;
        score = i;
        try {
            loadPm(bArr);
        } catch (Exception e) {
            e.printStackTrace();
            pianoPlay.finish();
        }
    }

    private void loadImages() {
        Matrix matrix = new Matrix();
        barImage = ImageLoadUtil.loadSkinImage(pianoPlay, "bar");
        keyboardImage = ImageLoadUtil.loadSkinImage(pianoPlay, "key_board_hd");
        backgroundImage = ImageLoadUtil.loadSkinImage(pianoPlay, "background_hd");
        perfectImage = ImageLoadUtil.loadSkinImage(pianoPlay, "perfect_img");
        coolImage = ImageLoadUtil.loadSkinImage(pianoPlay, "cool_img");
        greatImage = ImageLoadUtil.loadSkinImage(pianoPlay, "great_img");
        badImage = ImageLoadUtil.loadSkinImage(pianoPlay, "bad_img");
        missImage = ImageLoadUtil.loadSkinImage(pianoPlay, "miss_img");
        maxImage = ImageLoadUtil.loadSkinImage(pianoPlay, "max_img");
        xImage = ImageLoadUtil.loadSkinImage(pianoPlay, "x_img");
        scoreImage = ImageLoadUtil.loadSkinImage(pianoPlay, "score");
        scoreNumImage = ImageLoadUtil.loadSkinImage(pianoPlay, "number");
        if (getHeight() < 1080) {
            matrix.postScale(0.7f, 0.7f);
            perfectImage = Bitmap.createBitmap(perfectImage, 0, 0, perfectImage.getWidth(), perfectImage.getHeight(), matrix, true);
            coolImage = Bitmap.createBitmap(coolImage, 0, 0, coolImage.getWidth(), coolImage.getHeight(), matrix, true);
            greatImage = Bitmap.createBitmap(greatImage, 0, 0, greatImage.getWidth(), greatImage.getHeight(), matrix, true);
            badImage = Bitmap.createBitmap(badImage, 0, 0, badImage.getWidth(), badImage.getHeight(), matrix, true);
            missImage = Bitmap.createBitmap(missImage, 0, 0, missImage.getWidth(), missImage.getHeight(), matrix, true);
            xImage = Bitmap.createBitmap(xImage, 0, 0, xImage.getWidth(), xImage.getHeight(), matrix, true);
            scoreImage = Bitmap.createBitmap(scoreImage, 0, 0, scoreImage.getWidth(), scoreImage.getHeight(), matrix, true);
            scoreNumImage = Bitmap.createBitmap(scoreNumImage, 0, 0, scoreNumImage.getWidth(), scoreNumImage.getHeight(), matrix, true);
        }
        noteImage = ImageLoadUtil.loadSkinImage(pianoPlay, "white_note_hd");
        blackNoteImage = ImageLoadUtil.loadSkinImage(pianoPlay, "black_note_hd");
        playNoteImage = ImageLoadUtil.loadSkinImage(pianoPlay, "play_note_hd");
        practiseNoteImage = ImageLoadUtil.loadSkinImage(pianoPlay, "play_note_hd");
        whiteKeyRightImage = ImageLoadUtil.loadSkinImage(pianoPlay, "white_r");
        whiteKeyMiddleImage = ImageLoadUtil.loadSkinImage(pianoPlay, "white_m");
        whiteKeyLeftImage = ImageLoadUtil.loadSkinImage(pianoPlay, "white_l");
        blackKeyImage = ImageLoadUtil.loadSkinImage(pianoPlay, "black");
        fireImage = ImageLoadUtil.loadSkinImage(pianoPlay, "fire");
        longKeyboardImage = ImageLoadUtil.loadSkinImage(pianoPlay, "keyboard_long");
        nullImage = ImageLoadUtil.loadSkinImage(pianoPlay, "null");
        switch (GlobalSetting.getRoughLine()) {
            case 1 -> roughLineImage = nullImage;
            case 2 -> roughLineImage = ImageLoadUtil.loadSkinImage(pianoPlay, "rough_line1");
            case 3 -> roughLineImage = ImageLoadUtil.loadSkinImage(pianoPlay, "rough_line");
        }
        progressBarImage = ImageLoadUtil.loadSkinImage(pianoPlay, "progress_bar");
        progressBarBaseImage = ImageLoadUtil.loadSkinImage(pianoPlay, "progress_bar_base");
        Bitmap bitmap = noteImage;
        matrix.postScale(GlobalSetting.getNoteSize(), GlobalSetting.getNoteSize());
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        noteImage = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        width = blackNoteImage.getWidth();
        height = blackNoteImage.getHeight();
        blackNoteImage = Bitmap.createBitmap(blackNoteImage, 0, 0, width, height, matrix, true);
        width = playNoteImage.getWidth();
        height = playNoteImage.getHeight();
        playNoteImage = Bitmap.createBitmap(playNoteImage, 0, 0, width, height, matrix, true);
        whiteKeyHeight = (int) (getHeight() * 0.49);
        halfHeightSub20 = whiteKeyHeight - 20;
        blackKeyHeight = getHeight() / 3.4f;
        blackKeyWidth = getWidth() * 0.0413f;
        halfHeightSub10 = getHeight() * 0.5f - 10;
        whiteKeyHeightAdd90 = whiteKeyHeight + 90f;
    }

    private void loadPm(Context context, String songFilePath, int tune) {
        PmSongData pmSongData = PmSongUtil.parsePmDataByFilePath(context, songFilePath);
        if (pmSongData == null) {
            return;
        }
        tickArray = pmSongData.getTickArray();
        noteArray = pmSongData.getPitchArray();
        trackArray = pmSongData.getTrackArray();
        volumeArray = pmSongData.getVolumeArray();
        rightHandDegree = pmSongData.getRightHandDegree();
        songsName = pmSongData.getSongName();
        pm_2 = pmSongData.getGlobalSpeed();
        if (tune != 0) {
            for (int i = 0; i < tickArray.length; i++) {
                noteArray[i] += tune;
            }
        }
        setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        pianoPlay.setContentView(this);
    }

    private void loadPm(byte[] bArr) {
        PmSongData pmSongData = PmSongUtil.parsePmDataByBytes(bArr);
        if (pmSongData == null) {
            return;
        }
        tickArray = pmSongData.getTickArray();
        noteArray = pmSongData.getPitchArray();
        trackArray = pmSongData.getTrackArray();
        volumeArray = pmSongData.getVolumeArray();
        rightHandDegree = pmSongData.getRightHandDegree();
        songsName = pmSongData.getSongName();
        pm_2 = pmSongData.getGlobalSpeed();
        setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        pianoPlay.setContentView(this);
    }

    private void buildNoteByPmResult(int tune) {
        notesList.clear();
        boolean hideNote = false;
        float lastPosition = 0;
        int tick = 0;
        int i3 = 0;
        int actualTime;
        int lastActualTime = 0;
        int length = 0;
        while (true) {
            int i4 = length;
            if (i4 < tickArray.length) {
                tick += tickArray[i4];
                actualTime = -tick * pm_2;
                position = (int) (-tick * pm_2 / GlobalSetting.getTempSpeed() / GlobalSetting.getNotesDownSpeed());
                if (trackArray[i4] != handValue) {
                    notesList.add(new PlayNote(this, noteArray[i4], trackArray[i4],
                            (byte) (volumeArray[i4] * Byte.MAX_VALUE / 100f), position, i3, hideNote, handValue));
                    hideNote = true;
                } else if (lastPosition < position) {
                    notesList.add(new PlayNote(this, noteArray[i4], trackArray[i4],
                            (byte) (volumeArray[i4] * Byte.MAX_VALUE / 100f), position, i3, hideNote, handValue));
                    hideNote = true;
                } else {
                    if (lastActualTime - actualTime >= 100 || i4 == 0) {
                        hideNote = false;
                    }
                    lastPosition = position;
                    lastActualTime = actualTime;
                    if (noteArray[i4] < i3 * 12 || noteArray[i4] > i3 * 12 + 12) {
                        i3 = noteArray[i4] / 12;
                    }
                    if (noteArray[i4] == 110 + tune && volumeArray[i4] == 3) {
                        trackArray[i4] = 2;
                    }
                    notesList.add(new PlayNote(this, noteArray[i4], trackArray[i4],
                            (byte) (volumeArray[i4] * Byte.MAX_VALUE / 100f), position, i3, hideNote, handValue));
                    hideNote = true;
                }
                length = i4 + 1;
            } else {
                return;
            }
        }
    }

    public void loadParams() {
        if (barImage == null) {
            loadImages();
        }
        perfectStandard = 200f / GlobalSetting.getNotesDownSpeed();
        coolStandard = 300f / GlobalSetting.getNotesDownSpeed();
        greatStandard = 400f / GlobalSetting.getNotesDownSpeed();
        missStandard = 500f / GlobalSetting.getNotesDownSpeed();
        line.setColor(Color.argb(178, 244, 255, 64));
        line.setStrokeWidth(3);
        screenWidth = getWidth();
        screenHeight = getHeight();
        widthDiv8 = getWidth() / 8f;// 每个琴键宽度
        width2Div8 = getWidth() / 4f;
        width4Div8 = getWidth() / 2f;
        width5Div8 = getWidth() / 1.6f;
        width6Div8 = getWidth() * 0.75f;
        width8Div8 = getWidth();
        progressBarHeight = progressBarImage.getHeight();
        progressBarWidth = progressBarImage.getWidth();
        f4801bz = new Rect();
        f4769bA = new Rect();
        scoreRectF = new RectF();
        f4773bE = new Rect();
        f4774bF = new RectF();
        f4770bB = new Rect();
        f4771bC = new RectF();
        uploadTimeArray = new byte[uploadTime];
        uploadTouchStatusList = new ArrayList<>();
        if (pianoPlay.playKeyBoardView != null) {
            if (pianoPlay.playKeyBoardView.getParent() != null) {
                ((ViewGroup) (pianoPlay.playKeyBoardView.getParent())).removeView(pianoPlay.playKeyBoardView);
            }
        }
        pianoPlay.playKeyBoardView = new PlayKeyBoardView(pianoPlay, this);
        pianoPlay.addContentView(pianoPlay.playKeyBoardView, pianoPlay.layoutParams2);
        pianoPlay.playTypeAndShowHandle(pianoPlay.playType, false);
        pianoPlay.isPlayingStart = true;
        if (surfaceholder == null) {
            surfaceholder = getHolder();
            surfaceholder.addCallback(this);
        }
    }

    public List<Rect> getKeyRectArray() {
        List<Rect> arrayList = new ArrayList<>();
        arrayList.add(new Rect(0, whiteKeyHeight, (int) widthDiv8, getHeight()));
        arrayList.add(new Rect((int) (widthDiv8 - blackKeyWidth), whiteKeyHeight, (int) (widthDiv8 + blackKeyWidth), (int) (whiteKeyHeight + blackKeyHeight + 5)));
        arrayList.add(new Rect((int) widthDiv8, whiteKeyHeight, (int) (widthDiv8 * 2), getHeight()));
        arrayList.add(new Rect((int) (widthDiv8 * 2 - blackKeyWidth), whiteKeyHeight, (int) (widthDiv8 * 2 + blackKeyWidth), (int) (whiteKeyHeight + blackKeyHeight + 5)));
        arrayList.add(new Rect((int) (widthDiv8 * 2), whiteKeyHeight, (int) (widthDiv8 * 3), getHeight()));
        arrayList.add(new Rect((int) (widthDiv8 * 3), whiteKeyHeight, (int) (widthDiv8 * 4), getHeight()));
        arrayList.add(new Rect((int) (widthDiv8 * 4 - blackKeyWidth), whiteKeyHeight, (int) (widthDiv8 * 4 + blackKeyWidth), (int) (whiteKeyHeight + blackKeyHeight + 5)));
        arrayList.add(new Rect((int) (widthDiv8 * 4), whiteKeyHeight, (int) (widthDiv8 * 5), getHeight()));
        arrayList.add(new Rect((int) ((widthDiv8 * 5) - blackKeyWidth), whiteKeyHeight, (int) (widthDiv8 * 5 + blackKeyWidth), (int) (whiteKeyHeight + blackKeyHeight + 5)));
        arrayList.add(new Rect((int) (widthDiv8 * 5), whiteKeyHeight, (int) (widthDiv8 * 6), getHeight()));
        arrayList.add(new Rect((int) (widthDiv8 * 6 - blackKeyWidth), whiteKeyHeight, (int) (widthDiv8 * 6 + blackKeyWidth), (int) (whiteKeyHeight + blackKeyHeight + 5)));
        arrayList.add(new Rect((int) (widthDiv8 * 6), whiteKeyHeight, (int) (widthDiv8 * 7), getHeight()));
        arrayList.add(new Rect((int) (widthDiv8 * 7), whiteKeyHeight, (int) (widthDiv8 * 8), getHeight()));
        return arrayList;
    }

    public List<Rect> getFireRectArray() {
        List<Rect> arrayList = new ArrayList<>();
        arrayList.add(new Rect(0, (int) (halfHeightSub20 - ((float) fireImage.getHeight())), (int) widthDiv8, (int) halfHeightSub20));
        arrayList.add(new Rect((int) (widthDiv8 - blackKeyWidth), (int) (halfHeightSub20 - ((float) fireImage.getHeight())), (int) (widthDiv8 + blackKeyWidth), (int) halfHeightSub20));
        arrayList.add(new Rect((int) widthDiv8, (int) (halfHeightSub20 - ((float) fireImage.getHeight())), (int) (widthDiv8 * 2), (int) halfHeightSub20));
        arrayList.add(new Rect((int) (widthDiv8 * 2 - blackKeyWidth), (int) (halfHeightSub20 - ((float) fireImage.getHeight())), (int) (widthDiv8 * 2 + blackKeyWidth), (int) halfHeightSub20));
        arrayList.add(new Rect((int) (widthDiv8 * 2), ((int) halfHeightSub20) - fireImage.getHeight(), (int) (widthDiv8 * 3), (int) halfHeightSub20));
        arrayList.add(new Rect((int) (widthDiv8 * 3), ((int) halfHeightSub20) - fireImage.getHeight(), (int) (widthDiv8 * 4), (int) halfHeightSub20));
        arrayList.add(new Rect((int) (widthDiv8 * 4 - blackKeyWidth), (int) (halfHeightSub20 - ((float) fireImage.getHeight())), (int) (widthDiv8 * 4 + blackKeyWidth), (int) halfHeightSub20));
        arrayList.add(new Rect((int) (widthDiv8 * 4), ((int) halfHeightSub20) - fireImage.getHeight(), (int) (widthDiv8 * 5), (int) halfHeightSub20));
        arrayList.add(new Rect((int) (widthDiv8 * 5 - blackKeyWidth), (int) (halfHeightSub20 - ((float) fireImage.getHeight())), (int) (widthDiv8 * 5 + blackKeyWidth), (int) halfHeightSub20));
        arrayList.add(new Rect((int) (widthDiv8 * 5), ((int) halfHeightSub20) - fireImage.getHeight(), (int) (widthDiv8 * 6), (int) halfHeightSub20));
        arrayList.add(new Rect((int) (widthDiv8 * 6 - blackKeyWidth), (int) (halfHeightSub20 - ((float) fireImage.getHeight())), (int) (widthDiv8 * 6 + blackKeyWidth), (int) halfHeightSub20));
        arrayList.add(new Rect((int) (widthDiv8 * 6), ((int) halfHeightSub20) - fireImage.getHeight(), (int) (widthDiv8 * 7), (int) halfHeightSub20));
        arrayList.add(new Rect((int) (widthDiv8 * 7), ((int) halfHeightSub20) - fireImage.getHeight(), (int) (widthDiv8 * 8), (int) halfHeightSub20));
        return arrayList;
    }

    public void judgeTouchNote(int i, boolean isMidi) {
        int i2;
        int i3;
        if (i == noteRightValue || (isMidi && i + 12 == noteRightValue)) {
            int i4;
            isTouchRightNote = true;
            double abs;
            try {
                abs = Math.abs(whiteKeyHeight - judgingNote.posiAdd15AddAnim);
            } catch (Exception e) {
                abs = 0;
            }
            if (hasTouched) {
                comboNum = 0;
                missNum++;
                i2 = -1;
                i4 = -5;
            } else if (abs <= perfectStandard) {
                comboNum++;
                perfectNum++;
                i2 = 1;
                i4 = 10;
            } else if (abs <= coolStandard) {
                comboNum = 0;
                coolNum++;
                i2 = 2;
                i4 = 8;
            } else if (abs <= greatStandard) {
                comboNum = 0;
                i4 = 5;
                i2 = 3;
                greatNum++;
            } else if (abs > missStandard) {
                comboNum = 0;
                missNum++;
                i4 = -5;
                i2 = -1;
            } else {
                comboNum = 0;
                badNum++;
                i4 = 2;
                i2 = 5;
            }
            hasTouched = true;
            i3 = i4;
            if (GlobalSetting.getNoteDismiss()) {
                judgingNote.image = nullImage;
                int index = notesList.indexOf(judgingNote);
                for (int i1 = 0; i1 < notesList.size(); i1++) {
                    PlayNote note = notesList.get(i1);
                    if (note.track == note.hand && !note.hide && i1 > index) {
                        judgingNote = note;
                        break;
                    }
                }
            }
        } else {
            comboNum = 0;
            missNum++;
            i2 = -1;
            i3 = -5;
        }
        if (comboNum - 1 > topComboNum) {
            topComboNum = comboNum - 1;
        }
        showScoreAndLevelsThread.computeScore(new ShowTouchNotesLevel(i2, this, comboNum, screenWidth, screenHeight), i3, comboNum);
        byte b = (byte) (i2 + 1);
        uploadTouchStatusList.add(b);
        if (gameType == 2) {
            uploadTimeArray[uploadNoteIndex] = b;
            if (uploadNoteIndex < uploadTime - 1) {
                uploadNoteIndex++;
                return;
            }
            OnlineMiniGradeDTO.Builder builder = OnlineMiniGradeDTO.newBuilder();
            builder.setStatusArray(GZIPUtil.arrayToZIP(uploadTimeArray));
            pianoPlay.sendMsg(OnlineProtocolType.MINI_GRADE, builder.build());
            uploadNoteIndex = 0;
        }
    }

    public int eventPositionToTouchNoteNum(float f, float f2) {
        int i = 1;
        float w = whiteKeyHeight + blackKeyHeight;
        if (f2 >= whiteKeyHeight) {
            if (f2 <= w) {
                if (Math.abs(widthDiv8 - f) < blackKeyWidth) {
                    return 1;
                }
                if (Math.abs(width2Div8 - f) < blackKeyWidth) {
                    return 3;
                }
                if (Math.abs(width4Div8 - f) < blackKeyWidth) {
                    return 6;
                }
                if (Math.abs(width5Div8 - f) < blackKeyWidth) {
                    return 8;
                }
                if (Math.abs(width6Div8 - f) < blackKeyWidth) {
                    return 10;
                }
                if (Math.abs(width8Div8 - f) < blackKeyWidth) {
                    return 12;
                }
            }
            if (f2 >= whiteKeyHeight) {
                while (i < 9) {
                    if (f >= ((float) (i - 1)) * widthDiv8 && f < ((float) i) * widthDiv8) {
                        switch (i) {
                            case 1 -> {
                                return 0;
                            }
                            case 2 -> {
                                return 2;
                            }
                            case 3 -> {
                                return 4;
                            }
                            case 4 -> {
                                return 5;
                            }
                            case 5 -> {
                                return 7;
                            }
                            case 6 -> {
                                return 9;
                            }
                            case 7 -> {
                                return 11;
                            }
                            case 8 -> {
                                return 12;
                            }
                        }
                    }
                    i++;
                }
            }
        }
        return -1;
    }

    public void drawProgressAndFinish(int progress, Canvas canvas) {
        float size = (float) progress / (getHeight() - whiteKeyHeight - position) * getWidth();
        if (canvas != null) {
            f4773bE.set(0, 0, progressBarWidth, progressBarHeight);
            f4774bF.set(0, 0, (float) getWidth(), (float) progressBarHeight);
            canvas.drawBitmap(progressBarBaseImage, null, f4774bF, null);
            f4774bF.set(0, 0, size, (float) progressBarHeight);
            canvas.drawBitmap(progressBarImage, f4773bE, f4774bF, null);
        }
        if (notesList.size() == 0) {
            pianoPlay.isPlayingStart = false;
            startFirstNoteTouching = false;
            pianoPlay.isPlaying = true;
            int size2;
            byte[] bArr;
            Message message;
            long serialID = 2825651233768L;
            switch (gameType) {
                case 4 -> {
                    size2 = uploadTouchStatusList.size();
                    bArr = new byte[size2];
                    for (int i = 0; i < size2; i++) {
                        bArr[i] = uploadTouchStatusList.get(i);
                    }
                    OnlineChallengeDTO.Builder builder1 = OnlineChallengeDTO.newBuilder();
                    builder1.setType(4);
                    builder1.setStatusArray(GZIPUtil.arrayToZIP(bArr));
                    long x = pianoPlay.times * serialID;
                    long time = EncryptUtil.getServerTime();
                    long crypt = (time >>> 12 | time << 52) ^ x;
                    builder1.setCode(crypt);
                    pianoPlay.sendMsg(OnlineProtocolType.CHALLENGE, builder1.build());
                }
                case 3 -> {
                    size2 = uploadTouchStatusList.size();
                    bArr = new byte[size2];
                    for (int i = 0; i < size2; i++) {
                        bArr[i] = uploadTouchStatusList.get(i);
                    }
                    OnlineClTestDTO.Builder builder = OnlineClTestDTO.newBuilder();
                    builder.setType(3);
                    builder.setStatusArray(GZIPUtil.toZIP(new String(bArr, StandardCharsets.UTF_8)));
                    long x = pianoPlay.times * serialID;
                    long time = EncryptUtil.getServerTime();
                    long crypt = (time >>> 12 | time << 52) ^ x;
                    builder.setCode(crypt);
                    pianoPlay.sendMsg(OnlineProtocolType.CL_TEST, builder.build());
                }
                case 2 -> {
                    // 增加弹奏结果到本地数据库
                    ThreadPoolUtil.execute(() -> {
                        List<Song> songByPath = JPApplication.getSongDatabase().songDao().getSongByFilePath(songsPath);
                        for (Song song : songByPath) {
                            int topScore = handValue == 0 ? song.getRightHandHighScore() : song.getLeftHandHighScore();
                            if (topScore <= showScoreAndLevelsThread.levelScore) {
                                if (handValue == 0) {
                                    song.setRightHandHighScore(showScoreAndLevelsThread.levelScore);
                                } else {
                                    song.setLeftHandHighScore(showScoreAndLevelsThread.levelScore);
                                }
                                song.setHighScoreDate(System.currentTimeMillis());
                                song.setNew(0);
                                JPApplication.getSongDatabase().songDao().updateSongs(Collections.singletonList(song));
                            }
                        }
                    });
                    message = Message.obtain(pianoPlay.pianoPlayHandler);
                    message.what = 8;
                    pianoPlay.pianoPlayHandler.handleMessage(message);
                    size2 = uploadTouchStatusList.size();
                    bArr = new byte[size2];
                    for (int i = 0; i < size2; i++) {
                        bArr[i] = uploadTouchStatusList.get(i);
                    }
                    OnlinePlayFinishDTO.Builder builder2 = OnlinePlayFinishDTO.newBuilder();
                    builder2.setStatusArray(GZIPUtil.arrayToZIP(bArr));
                    long x = pianoPlay.roomBundle.getByte("ID") * serialID;
                    long time = EncryptUtil.getServerTime();
                    long crypt = (time >>> 12 | time << 52) ^ x;
                    builder2.setCode(crypt);
                    pianoPlay.sendMsg(OnlineProtocolType.PLAY_FINISH, builder2.build());
                }
                default -> {
                    Intent intent = new Intent(pianoPlay, PlayFinish.class);
                    intent.putExtra("head", gameType);
                    intent.putExtra("topScore", score);
                    intent.putExtra("perf", perfectNum);
                    size2 = uploadTouchStatusList.size();
                    bArr = new byte[size2];
                    for (int i = 0; i < size2; i++) {
                        bArr[i] = uploadTouchStatusList.get(i);
                    }
                    intent.putExtra("scoreArray", bArr);
                    intent.putExtra("totalScore", showScoreAndLevelsThread.levelScore);
                    intent.putExtra("combo_scr", showScoreAndLevelsThread.comboScore);
                    intent.putExtra("cool", coolNum);
                    intent.putExtra("great", greatNum);
                    intent.putExtra("bad", badNum);
                    intent.putExtra("miss", missNum);
                    intent.putExtra("name", songsName);
                    intent.putExtra("songID", songId);
                    intent.putExtra("path", songsPath);
                    intent.putExtra("rightHandDegree", rightHandDegree);
                    intent.putExtra("leftHandDegree", leftHandDegree);
                    intent.putExtra("songsTime", songsTime);
                    intent.putExtra("hand", handValue);
                    intent.putExtra("top_combo", topComboNum);
                    pianoPlay.playFinishLauncher.launch(intent);
                }
            }
        }
    }

    public void drawLineAndNotes(Canvas canvas) {
        deleteNotesArray.clear();
        boolean newNote = true;
        for (PlayNote note : notesList) {
            currentPlayNote = note;
            if (currentPlayNote.posiAdd15AddAnim < halfHeightSub20 + 60) {
                if (currentPlayNote.track == currentPlayNote.hand && currentPlayNote.posiAdd15AddAnim < whiteKeyHeight && newNote) {
                    if (currentPlayNote.unPassed) {
                        hasTouched = false;
                        currentPlayNote.unPassed = false;
                    }
                    if (!currentPlayNote.image.equals(nullImage)) {
                        judgingNote = currentPlayNote;
                        if (currentPlayNote.posiAdd15AddAnim > -15f) {
                            if (GlobalSetting.getShowLine() && canvas != null) {
                                canvas.drawLine(currentPlayNote.getHalfWidth() + currentPlayNote.positionX, whiteKeyHeight, currentPlayNote.getHalfWidth() + currentPlayNote.positionX, 15 + currentPlayNote.posiAdd15AddAnim, line);
                            }
                            if (GlobalSetting.getChangeNotesColor()) {
                                currentPlayNote.image = playNoteImage;
                            } else if (currentPlayNote.posiAdd15AddAnim >= halfHeightSub20) {
                                currentPlayNote.image = playNoteImage;
                            }
                        }
                    }
                    if (GlobalSetting.getLocalPlayMode() == LocalPlayModeEnum.PRACTISE && !hasTouched && !currentPlayNote.hide
                            && currentPlayNote.posiAdd15AddAnim > whiteKeyHeight - 100 / GlobalSetting.getNotesDownSpeed()) {
                        isTouchRightNote = false;
                    }
                    noteRightValue = currentPlayNote.pitch;
                    if (!currentPlayNote.hide) {
                        noteMod12 = currentPlayNote.octave;
                    }
                    volume0 = currentPlayNote.volume;
                    newNote = false;
                }
                if (gameType > 0) {
                    if (currentPlayNote.onlineNotePlayHandle(canvas) < 0) {
                        break;
                    }
                } else if (currentPlayNote.localNotePlayHandle(canvas) < 0) {
                    break;
                }
            } else {
                deleteNotesArray.add(currentPlayNote);
            }
        }
        notesList.removeAll(deleteNotesArray);
        int i;
        int i2;
        int i3;
        if (canvas != null && touchNotesList != null && GlobalSetting.getShowTouchNotesLevel()) {
            try {
                for (ShowTouchNotesLevel showTouchNotesLevel : touchNotesList) {
                    Rect rect = f4770bB;
                    RectF rectF = f4771bC;
                    if (showTouchNotesLevel.levelImage != null) {
                        canvas.drawBitmap(showTouchNotesLevel.levelImage, (showTouchNotesLevel.width - (showTouchNotesLevel.playView.perfectImage.getWidth() / 2f)), showTouchNotesLevel.height, null);
                    }
                    if (showTouchNotesLevel.comboNum > 1 && showTouchNotesLevel.comboNum <= 9) {
                        canvas.drawBitmap(showTouchNotesLevel.playView.xImage, (showTouchNotesLevel.width + (showTouchNotesLevel.playView.perfectImage.getWidth() / 2f)), showTouchNotesLevel.height, null);
                        rect.set((showTouchNotesLevel.comboNum * showTouchNotesLevel.playView.scoreNumImage.getWidth()) / 10, 0, ((showTouchNotesLevel.comboNum + 1) * showTouchNotesLevel.playView.scoreNumImage.getWidth()) / 10, showTouchNotesLevel.playView.scoreNumImage.getHeight());
                        rectF.set((float) ((showTouchNotesLevel.width + (showTouchNotesLevel.playView.perfectImage.getWidth() / 2)) + showTouchNotesLevel.playView.xImage.getWidth()), (float) showTouchNotesLevel.height, (float) (((showTouchNotesLevel.width + (showTouchNotesLevel.playView.perfectImage.getWidth() / 2)) + showTouchNotesLevel.playView.xImage.getWidth()) + (showTouchNotesLevel.playView.scoreNumImage.getWidth() / 10)), (float) (showTouchNotesLevel.height + showTouchNotesLevel.playView.scoreNumImage.getHeight()));
                        canvas.drawBitmap(showTouchNotesLevel.playView.scoreNumImage, rect, rectF, null);
                    } else if (showTouchNotesLevel.comboNum > 9 && showTouchNotesLevel.comboNum <= 99) {
                        i = showTouchNotesLevel.comboNum % 10;
                        i2 = showTouchNotesLevel.comboNum / 10;
                        canvas.drawBitmap(showTouchNotesLevel.playView.xImage, (showTouchNotesLevel.width + (showTouchNotesLevel.playView.perfectImage.getWidth() / 2f)), showTouchNotesLevel.height, null);
                        rect.set((showTouchNotesLevel.playView.scoreNumImage.getWidth() * i2) / 10, 0, ((i2 + 1) * showTouchNotesLevel.playView.scoreNumImage.getWidth()) / 10, showTouchNotesLevel.playView.scoreNumImage.getHeight());
                        rectF.set((float) ((showTouchNotesLevel.width + (showTouchNotesLevel.playView.perfectImage.getWidth() / 2)) + showTouchNotesLevel.playView.xImage.getWidth()), (float) showTouchNotesLevel.height, (float) (((showTouchNotesLevel.width + (showTouchNotesLevel.playView.perfectImage.getWidth() / 2)) + showTouchNotesLevel.playView.xImage.getWidth()) + (showTouchNotesLevel.playView.scoreNumImage.getWidth() / 10)), (float) (showTouchNotesLevel.height + showTouchNotesLevel.playView.scoreNumImage.getHeight()));
                        canvas.drawBitmap(showTouchNotesLevel.playView.scoreNumImage, rect, rectF, null);
                        rect.set((showTouchNotesLevel.playView.scoreNumImage.getWidth() * i) / 10, 0, ((i + 1) * showTouchNotesLevel.playView.scoreNumImage.getWidth()) / 10, showTouchNotesLevel.playView.scoreNumImage.getHeight());
                        rectF.set((float) (((showTouchNotesLevel.width + (showTouchNotesLevel.playView.perfectImage.getWidth() / 2)) + showTouchNotesLevel.playView.xImage.getWidth()) + (showTouchNotesLevel.playView.scoreNumImage.getWidth() / 10)), (float) showTouchNotesLevel.height, (float) (((showTouchNotesLevel.width + (showTouchNotesLevel.playView.perfectImage.getWidth() / 2)) + showTouchNotesLevel.playView.xImage.getWidth()) + ((showTouchNotesLevel.playView.scoreNumImage.getWidth() / 10) * 2)), (float) (showTouchNotesLevel.height + showTouchNotesLevel.playView.scoreNumImage.getHeight()));
                        canvas.drawBitmap(showTouchNotesLevel.playView.scoreNumImage, rect, rectF, null);
                    } else if (showTouchNotesLevel.comboNum > 99 && showTouchNotesLevel.comboNum <= 999) {
                        i = showTouchNotesLevel.comboNum / 100;
                        i2 = (showTouchNotesLevel.comboNum - (i * 100)) / 10;
                        i3 = showTouchNotesLevel.comboNum % 10;
                        canvas.drawBitmap(showTouchNotesLevel.playView.xImage, (showTouchNotesLevel.width + (showTouchNotesLevel.playView.perfectImage.getWidth() / 2f)), showTouchNotesLevel.height, null);
                        rect.set((showTouchNotesLevel.playView.scoreNumImage.getWidth() * i) / 10, 0, ((i + 1) * showTouchNotesLevel.playView.scoreNumImage.getWidth()) / 10, showTouchNotesLevel.playView.scoreNumImage.getHeight());
                        rectF.set((float) ((showTouchNotesLevel.width + (showTouchNotesLevel.playView.perfectImage.getWidth() / 2)) + showTouchNotesLevel.playView.xImage.getWidth()), (float) showTouchNotesLevel.height, (float) (((showTouchNotesLevel.width + (showTouchNotesLevel.playView.perfectImage.getWidth() / 2)) + showTouchNotesLevel.playView.xImage.getWidth()) + (showTouchNotesLevel.playView.scoreNumImage.getWidth() / 10)), (float) (showTouchNotesLevel.height + showTouchNotesLevel.playView.scoreNumImage.getHeight()));
                        canvas.drawBitmap(showTouchNotesLevel.playView.scoreNumImage, rect, rectF, null);
                        rect.set((showTouchNotesLevel.playView.scoreNumImage.getWidth() * i2) / 10, 0, ((i2 + 1) * showTouchNotesLevel.playView.scoreNumImage.getWidth()) / 10, showTouchNotesLevel.playView.scoreNumImage.getHeight());
                        rectF.set((float) (((showTouchNotesLevel.width + (showTouchNotesLevel.playView.perfectImage.getWidth() / 2)) + showTouchNotesLevel.playView.xImage.getWidth()) + (showTouchNotesLevel.playView.scoreNumImage.getWidth() / 10)), (float) showTouchNotesLevel.height, (float) (((showTouchNotesLevel.width + (showTouchNotesLevel.playView.perfectImage.getWidth() / 2)) + showTouchNotesLevel.playView.xImage.getWidth()) + ((showTouchNotesLevel.playView.scoreNumImage.getWidth() / 10) * 2)), (float) (showTouchNotesLevel.height + showTouchNotesLevel.playView.scoreNumImage.getHeight()));
                        canvas.drawBitmap(showTouchNotesLevel.playView.scoreNumImage, rect, rectF, null);
                        rect.set((showTouchNotesLevel.playView.scoreNumImage.getWidth() * i3) / 10, 0, ((i3 + 1) * showTouchNotesLevel.playView.scoreNumImage.getWidth()) / 10, showTouchNotesLevel.playView.scoreNumImage.getHeight());
                        rectF.set((float) (((showTouchNotesLevel.width + (showTouchNotesLevel.playView.perfectImage.getWidth() / 2)) + showTouchNotesLevel.playView.xImage.getWidth()) + ((showTouchNotesLevel.playView.scoreNumImage.getWidth() / 10) * 2)), (float) showTouchNotesLevel.height, (float) (((showTouchNotesLevel.width + (showTouchNotesLevel.playView.perfectImage.getWidth() / 2)) + showTouchNotesLevel.playView.xImage.getWidth()) + ((showTouchNotesLevel.playView.scoreNumImage.getWidth() / 10) * 3)), (float) (showTouchNotesLevel.height + showTouchNotesLevel.playView.scoreNumImage.getHeight()));
                        canvas.drawBitmap(showTouchNotesLevel.playView.scoreNumImage, rect, rectF, null);
                    } else if (showTouchNotesLevel.comboNum > 999 && showTouchNotesLevel.comboNum <= 9999) {
                        i = showTouchNotesLevel.comboNum / 1000;
                        i2 = (showTouchNotesLevel.comboNum - (i * 1000)) / 100;
                        i3 = ((showTouchNotesLevel.comboNum - (i * 1000)) - (i2 * 100)) / 10;
                        int i4 = showTouchNotesLevel.comboNum % 10;
                        canvas.drawBitmap(showTouchNotesLevel.playView.xImage, (showTouchNotesLevel.width + (showTouchNotesLevel.playView.perfectImage.getWidth() / 2f)), showTouchNotesLevel.height, null);
                        rect.set((showTouchNotesLevel.playView.scoreNumImage.getWidth() * i) / 10, 0, ((i + 1) * showTouchNotesLevel.playView.scoreNumImage.getWidth()) / 10, showTouchNotesLevel.playView.scoreNumImage.getHeight());
                        rectF.set((float) ((showTouchNotesLevel.width + (showTouchNotesLevel.playView.perfectImage.getWidth() / 2)) + showTouchNotesLevel.playView.xImage.getWidth()), (float) showTouchNotesLevel.height, (float) (((showTouchNotesLevel.width + (showTouchNotesLevel.playView.perfectImage.getWidth() / 2)) + showTouchNotesLevel.playView.xImage.getWidth()) + (showTouchNotesLevel.playView.scoreNumImage.getWidth() / 10)), (float) (showTouchNotesLevel.height + showTouchNotesLevel.playView.scoreNumImage.getHeight()));
                        canvas.drawBitmap(showTouchNotesLevel.playView.scoreNumImage, rect, rectF, null);
                        rect.set((showTouchNotesLevel.playView.scoreNumImage.getWidth() * i2) / 10, 0, ((i2 + 1) * showTouchNotesLevel.playView.scoreNumImage.getWidth()) / 10, showTouchNotesLevel.playView.scoreNumImage.getHeight());
                        rectF.set((float) (((showTouchNotesLevel.width + (showTouchNotesLevel.playView.perfectImage.getWidth() / 2)) + showTouchNotesLevel.playView.xImage.getWidth()) + (showTouchNotesLevel.playView.scoreNumImage.getWidth() / 10)), (float) showTouchNotesLevel.height, (float) (((showTouchNotesLevel.width + (showTouchNotesLevel.playView.perfectImage.getWidth() / 2)) + showTouchNotesLevel.playView.xImage.getWidth()) + ((showTouchNotesLevel.playView.scoreNumImage.getWidth() / 10) * 2)), (float) (showTouchNotesLevel.height + showTouchNotesLevel.playView.scoreNumImage.getHeight()));
                        canvas.drawBitmap(showTouchNotesLevel.playView.scoreNumImage, rect, rectF, null);
                        rect.set((showTouchNotesLevel.playView.scoreNumImage.getWidth() * i3) / 10, 0, ((i3 + 1) * showTouchNotesLevel.playView.scoreNumImage.getWidth()) / 10, showTouchNotesLevel.playView.scoreNumImage.getHeight());
                        rectF.set((float) (((showTouchNotesLevel.width + (showTouchNotesLevel.playView.perfectImage.getWidth() / 2)) + showTouchNotesLevel.playView.xImage.getWidth()) + ((showTouchNotesLevel.playView.scoreNumImage.getWidth() / 10) * 2)), (float) showTouchNotesLevel.height, (float) (((showTouchNotesLevel.width + (showTouchNotesLevel.playView.perfectImage.getWidth() / 2)) + showTouchNotesLevel.playView.xImage.getWidth()) + ((showTouchNotesLevel.playView.scoreNumImage.getWidth() / 10) * 3)), (float) (showTouchNotesLevel.height + showTouchNotesLevel.playView.scoreNumImage.getHeight()));
                        canvas.drawBitmap(showTouchNotesLevel.playView.scoreNumImage, rect, rectF, null);
                        rect.set((showTouchNotesLevel.playView.scoreNumImage.getWidth() * i4) / 10, 0, ((i4 + 1) * showTouchNotesLevel.playView.scoreNumImage.getWidth()) / 10, showTouchNotesLevel.playView.scoreNumImage.getHeight());
                        rectF.set((float) (((showTouchNotesLevel.width + (showTouchNotesLevel.playView.perfectImage.getWidth() / 2)) + showTouchNotesLevel.playView.xImage.getWidth()) + ((showTouchNotesLevel.playView.scoreNumImage.getWidth() / 10) * 3)), (float) showTouchNotesLevel.height, (float) (((showTouchNotesLevel.width + (showTouchNotesLevel.playView.perfectImage.getWidth() / 2)) + showTouchNotesLevel.playView.xImage.getWidth()) + ((showTouchNotesLevel.playView.scoreNumImage.getWidth() / 10) * 4)), (float) (showTouchNotesLevel.height + showTouchNotesLevel.playView.scoreNumImage.getHeight()));
                        canvas.drawBitmap(showTouchNotesLevel.playView.scoreNumImage, rect, rectF, null);
                    } else if (showTouchNotesLevel.comboNum > 9999) {
                        canvas.drawBitmap(showTouchNotesLevel.playView.xImage, (showTouchNotesLevel.width + (showTouchNotesLevel.playView.perfectImage.getWidth() / 2f)), showTouchNotesLevel.height, null);
                        canvas.drawBitmap(showTouchNotesLevel.playView.maxImage, ((showTouchNotesLevel.width + (showTouchNotesLevel.playView.perfectImage.getWidth() / 2f)) + showTouchNotesLevel.playView.xImage.getWidth()), showTouchNotesLevel.height, null);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (canvas != null) {
            int i5 = showScoreAndLevelsThread.levelScore;
            int i6 = 0;
            i = scoreImage.getWidth();
            i2 = scoreNumImage.getWidth() / 10;
            i3 = scoreNumImage.getHeight();
            currentTotalScoreNumberList.clear();
            int e = Math.max(i5, 0);
            for (i5 = 0; i5 < 6; i5++) {
                currentTotalScoreNumberList.add(i5, (int) ((((double) e) % Math.pow(10, i5 + 1)) / Math.pow(10, i5)));
            }
            e = 0;
            while (true) {
                i5 = e;
                if (i5 >= 6) {
                    scoreRectF.set((float) ((getWidth() - (i2 * 6)) - i - TOTAL_SCORE_SHOW_OFFSET), 0,
                            (float) (getWidth() - (i2 * 6) - TOTAL_SCORE_SHOW_OFFSET), scoreImage.getHeight());
                    canvas.drawBitmap(scoreImage, null, scoreRectF, null);
                    return;
                }
                e = currentTotalScoreNumberList.get(i5);
                f4801bz.set(e * i2, 0, (e + 1) * i2, i3);
                f4769bA.set(getWidth() - ((i6 + 1) * i2) - TOTAL_SCORE_SHOW_OFFSET, 0,
                        getWidth() - (i2 * i6) - TOTAL_SCORE_SHOW_OFFSET, i3);
                canvas.drawBitmap(scoreNumImage, f4801bz, f4769bA, null);
                i6++;
                e = i5 + 1;
            }
        }
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i2, int i3) {
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
        loadBackgroundsThread = new LoadBackgroundsThread(this, pianoPlay);
        loadBackgroundsThread.start();
        showScoreAndLevelsThread = new ShowScoreAndLevelsThread(touchNotesList, pianoPlay);
        showScoreAndLevelsThread.start();
        setOnTouchListener(new TouchNotes(this));
    }

    @Override
    public void sendAccessibilityEvent(int eventType) {
        pianoPlay.finish();
    }

    @Override
    public void findViewsWithText(ArrayList<View> outViews, CharSequence searched, int flags) {
        outViews.remove(this);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w != oldw || h != oldh) {
            ViewUtil.registerViewLayoutObserver(this, () -> {
                loadParams();
                ThreadPoolUtil.execute(() -> buildNoteByPmResult(tune));
            });
        }
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {
        if (pianoPlay.isBack) {
            if (loadBackgroundsThread != null) {
                while (loadBackgroundsThread.isAlive()) {
                    pianoPlay.isPlayingStart = false;
                    startFirstNoteTouching = false;
                }
            }
            if (showScoreAndLevelsThread != null) {
                while (showScoreAndLevelsThread.isAlive()) {
                    pianoPlay.isPlayingStart = false;
                }
            }
            barImage.recycle();
            barImage = null;
            backgroundImage.recycle();
            backgroundImage = null;
            perfectImage.recycle();
            perfectImage = null;
            coolImage.recycle();
            coolImage = null;
            greatImage.recycle();
            greatImage = null;
            badImage.recycle();
            badImage = null;
            missImage.recycle();
            missImage = null;
            scoreNumImage.recycle();
            scoreNumImage = null;
            scoreImage.recycle();
            scoreImage = null;
            startFirstNoteTouching = false;
            noteImage.recycle();
            noteImage = null;
            blackNoteImage.recycle();
            blackNoteImage = null;
            playNoteImage.recycle();
            playNoteImage = null;
            practiseNoteImage.recycle();
            practiseNoteImage = null;
            whiteKeyRightImage.recycle();
            whiteKeyRightImage = null;
            whiteKeyMiddleImage.recycle();
            whiteKeyMiddleImage = null;
            whiteKeyLeftImage.recycle();
            whiteKeyLeftImage = null;
            blackKeyImage.recycle();
            blackKeyImage = null;
            fireImage.recycle();
            fireImage = null;
            roughLineImage.recycle();
            roughLineImage = null;
        }
    }
}
