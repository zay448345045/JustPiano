package ly.pp.justpiano3;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Message;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;

import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public final class PlayView extends SurfaceView implements Callback {
    static long serialID = 2825651233768L;
    Bitmap whiteKeyRightImage;
    Bitmap whiteKeyMiddleImage;
    Bitmap whiteKeyLeftImage;
    Bitmap blackKeyImage;
    Bitmap fireImage;
    Bitmap longKeyboardImage;
    int noteMod12 = 4;
    boolean startFirstNoteTouching;
    SurfaceHolder surfaceholder;
    PianoPlay pianoPlay;
    PlayNote currentPlayNote;
    int screenWidth;
    JPApplication jpapplication;
    boolean isTouchRightNote = true;
    Bitmap keyboardImage;
    Bitmap nullImage;
    byte[] noteArray;
    byte[] volumeArray;
    int arrayLength;
    Bitmap backgroundImage;
    Bitmap barImage;
    float posiAdd15AddAnim;
    int f4813n;
    Bitmap missImage;
    Bitmap perfectImage;
    Bitmap coolImage;
    Bitmap greatImage;
    Bitmap badImage;
    Bitmap noteImage;
    Bitmap blackNoteImage;
    Bitmap roughLineImage;
    String songsName;
    private Bitmap playNoteImage;
    private Bitmap practiceNoteImage;
    private int gameType;
    private Bitmap progressBarImage;
    private Bitmap progressBarBaseImage;
    private int volume0;
    private int score;
    private DownNotes downNotes;
    private LoadBackgrounds loadbackgrounds;
    private boolean hideNote;
    private boolean newNote = true;
    private PlayNote judgingNote;
    private int pm_2;
    private int position;
    private float lastPosition;
    private boolean hasTouched;
    private int tick;
    private int progressBarHight;
    private int progressBarWidth;
    private String songID;
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
    private ShowScoreAndLevels showScoreAndLevels;
    private double nandu;
    private double leftNandu;
    private int songsTime;
    private int screenHeight;
    private String songsPath;
    private Rect f4769bA;
    private Rect f4770bB;
    private RectF f4771bC;
    private RectF f4772bD;
    private Rect f4773bE;
    private RectF f4774bF;
    private int uploadTime;
    private List<Byte> uploadTouchStatusList;
    private byte[] uploadTimeArray;
    private int uploadNoteIndex;
    private final List<PlayNote> tempNotesArray = new ArrayList<>();
    private float halfHeightSub10;
    private int notesDownSpeed = 6;
    private final List<Integer> f4788bm = new ArrayList<>();
    private int handValue;
    private byte[] tickArray;
    private byte[] trackArray;
    private boolean f4713V = true;
    private ReadPm readpm;
    private PlayNote ln;
    private int noteCounts;
    private Rect f4801bz;
    private final Paint line = new Paint();
    private float widthDiv8;
    private float width2Div8;
    private float width4Div8;
    private float width5Div8;
    private float width6Div8;
    private float width8Div8;
    private int noteRightValue;
    private Bitmap scoreImage;
    private Bitmap scoreNumImage;
    private Bitmap xImage;
    private Bitmap maxImage;

    public PlayView(Context context) {
        super(context);
    }

    PlayView(JPApplication jPApplication, Context context, String str, PianoPlay pianoPlay, double d1, double d2, int i, int kind, int i3, int i4, int i5, int diao) {
        super(context);
        jpapplication = jPApplication;
        this.pianoPlay = pianoPlay;
        handValue = (i3 + 20) % 2;
        gameType = kind;
        uploadTime = i4;
        songsPath = str;
        nandu = d1;
        leftNandu = d2;
        score = i;
        songsTime = i5;
        loadParams();
        try {
            loadPm(context, str, diao);
            noteCounts = notesList.size();
        } catch (Exception e) {
            pianoPlay.finish();
        }
    }

    PlayView(JPApplication jPApplication, Context context, byte[] bArr, PianoPlay pianoPlay, double d, int i, int kind, int i3, String songId) {
        super(context);
        jpapplication = jPApplication;
        this.pianoPlay = pianoPlay;
        handValue = (i3 + 20) % 2;
        gameType = kind;
        songID = songId;
        nandu = d;
        score = i;
        loadParams();
        try {
            loadPm(jPApplication, bArr);
            noteCounts = notesList.size();
        } catch (Exception e) {
            pianoPlay.finish();
        }
    }

    private void loadImages() {
        Matrix matrix = new Matrix();
        barImage = jpapplication.loadImage("bar");
        keyboardImage = jpapplication.loadImage("key_board_hd");
        backgroundImage = jpapplication.loadImage("background_hd");
        perfectImage = jpapplication.loadImage("perfect_img");
        coolImage = jpapplication.loadImage("cool_img");
        greatImage = jpapplication.loadImage("great_img");
        badImage = jpapplication.loadImage("bad_img");
        missImage = jpapplication.loadImage("miss_img");
        maxImage = jpapplication.loadImage("max_img");
        xImage = jpapplication.loadImage("x_img");
        scoreImage = jpapplication.loadImage("score");
        scoreNumImage = jpapplication.loadImage("number");
        if (jpapplication.getHeightPixels() < 1080) {
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
        noteImage = jpapplication.loadImage("white_note_hd");
        blackNoteImage = jpapplication.loadImage("black_note_hd");
        playNoteImage = jpapplication.loadImage("play_note_hd");
        practiceNoteImage = jpapplication.loadImage("play_note_hd");
        whiteKeyRightImage = jpapplication.loadImage("white_r");
        whiteKeyMiddleImage = jpapplication.loadImage("white_m");
        whiteKeyLeftImage = jpapplication.loadImage("white_l");
        blackKeyImage = jpapplication.loadImage("black");
        fireImage = jpapplication.loadImage("fire");
        longKeyboardImage = jpapplication.loadImage("keyboard_long");
        nullImage = jpapplication.loadImage("null");
        switch (jpapplication.getRoughLine()) {
            case 1:
                roughLineImage = nullImage;
                break;
            case 2:
                roughLineImage = jpapplication.loadImage("rough_line1");
                break;
            case 3:
                roughLineImage = jpapplication.loadImage("rough_line");
                break;
        }
        progressBarImage = jpapplication.loadImage("progress_bar");
        progressBarBaseImage = jpapplication.loadImage("progress_bar_base");
        Bitmap bitmap = noteImage;
        float noteSize = jpapplication.getNoteSize();
        matrix.postScale(noteSize, noteSize);
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        noteImage = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        width = blackNoteImage.getWidth();
        height = blackNoteImage.getHeight();
        blackNoteImage = Bitmap.createBitmap(blackNoteImage, 0, 0, width, height, matrix, true);
        width = playNoteImage.getWidth();
        height = playNoteImage.getHeight();
        playNoteImage = Bitmap.createBitmap(playNoteImage, 0, 0, width, height, matrix, true);
        notesDownSpeed = jpapplication.getDownSpeed();
        jpapplication.setWidthDiv8(jpapplication.getWidthPixels() / 8f);
        jpapplication.setWhiteKeyHeight((int) (jpapplication.getHeightPixels() * 0.49));
        jpapplication.setHalfHeightSub20(jpapplication.getWhiteKeyHeight() - 20);
        jpapplication.setBlackKeyHeight(jpapplication.getHeightPixels() / 3.4f);
        jpapplication.setBlackKeyWidth(jpapplication.getWidthPixels() * 0.0413f);
        jpapplication.setHalfHeightSub10(jpapplication.getHeightPixels() * 0.5f - 10);
        jpapplication.setWhiteKeyHeightAdd90(jpapplication.getWhiteKeyHeight() + 90f);
    }

    private void loadPm(Context context, String str, int diao) {
        readpm = new ReadPm(context, str);
        tickArray = readpm.getTickArray();
        noteArray = readpm.getNoteArray();
        trackArray = readpm.getTrackArray();
        volumeArray = readpm.getVolumeArray();
        nandu = readpm.getNandu();
        songsName = readpm.getSongName();
        pm_2 = readpm.getPm_2();
        if (pm_2 <= 0) {
            pm_2 += 256;
        }
        int length = tickArray.length;
        if (diao != 0) {
            for (int i = 0; i < tickArray.length; i++) {
                noteArray[i] += diao;
            }
        }
        arrayLength = length;
        int i3 = 0;
        lastPosition = 0;
        length = 0;
        while (true) {
            int i4 = length;
            if (i4 < arrayLength) {
                tick += tickArray[i4];
                position = (int) (-tick * pm_2 / jpapplication.getTempSpeed() / notesDownSpeed);
                if (trackArray[i4] != handValue) {
                    ln = new PlayNote(jpapplication, this, noteArray[i4], trackArray[i4], volumeArray[i4], position, i3, hideNote, handValue);
                    notesList.add(ln);
                    hideNote = true;
                } else if (lastPosition < position) {
                    ln = new PlayNote(jpapplication, this, noteArray[i4], trackArray[i4], volumeArray[i4], position, i3, hideNote, handValue);
                    notesList.add(ln);
                    hideNote = true;
                } else {
                    if ((lastPosition - position) * notesDownSpeed >= 100 || i4 == 0) {
                        hideNote = false;
                    }
                    lastPosition = position;
                    if (noteArray[i4] < i3 * 12 || noteArray[i4] > i3 * 12 + 12) {
                        i3 = noteArray[i4] / 12;
                    }
                    if (noteArray[i4] == 110 + diao && volumeArray[i4] == 3) {
                        trackArray[i4] = 2;
                    }
                    ln = new PlayNote(jpapplication, this, noteArray[i4], trackArray[i4], volumeArray[i4], position, i3, hideNote, handValue);
                    notesList.add(ln);
                    hideNote = true;
                }
                length = i4 + 1;
            } else {
                return;
            }
        }
    }

    private void loadPm(JPApplication jPApplication, byte[] bArr) {
        readpm = new ReadPm(bArr);
        tickArray = readpm.getTickArray();
        noteArray = readpm.getNoteArray();
        trackArray = readpm.getTrackArray();
        volumeArray = readpm.getVolumeArray();
        nandu = readpm.getNandu();
        songsName = readpm.getSongName();
        pm_2 = readpm.getPm_2();
        arrayLength = tickArray.length;
        int i = 0;
        if (pm_2 <= 0) {
            pm_2 += 256;
        }
        lastPosition = 0;
        int i2 = 0;
        while (true) {
            int i3 = i2;
            if (i3 < arrayLength) {
                tick += tickArray[i3];
                position = (int) (-tick * pm_2 / jpapplication.getTempSpeed() / notesDownSpeed);
                if (trackArray[i3] != handValue) {
                    ln = new PlayNote(jPApplication, this, noteArray[i3], trackArray[i3], volumeArray[i3], position, i, hideNote, handValue);
                    notesList.add(ln);
                    hideNote = true;
                } else if (lastPosition < position) {
                    ln = new PlayNote(jPApplication, this, noteArray[i3], trackArray[i3], volumeArray[i3], position, i, hideNote, handValue);
                    notesList.add(ln);
                    hideNote = true;
                } else {
                    if ((lastPosition - position) * notesDownSpeed >= 100 || i3 == 0) {
                        hideNote = false;
                    }
                    lastPosition = position;
                    if (noteArray[i3] < i * 12 || noteArray[i3] > (i * 12) + 12) {
                        i = noteArray[i3] / 12;
                    }
                    if (noteArray[i3] == 110 && volumeArray[i3] == 3) {
                        trackArray[i3] = 2;
                    }
                    ln = new PlayNote(jPApplication, this, noteArray[i3], trackArray[i3], volumeArray[i3], position, i, hideNote, handValue);
                    notesList.add(ln);
                    hideNote = true;
                }
                i2 = i3 + 1;
            } else {
                return;
            }
        }
    }

    private void loadParams() {
        if (barImage == null) {
            loadImages();
        }
        perfectStandard = 200f / notesDownSpeed;
        coolStandard = 300f / notesDownSpeed;
        greatStandard = 400f / notesDownSpeed;
        missStandard = 500f / notesDownSpeed;
        jpapplication.setAnimPosition(0);
        line.setColor(Color.argb(178, 244, 255, 64));
        line.setStrokeWidth(3);
        screenWidth = jpapplication.getWidthPixels();
        screenHeight = jpapplication.getHeightPixels();
        widthDiv8 = jpapplication.getWidthPixels() / 8f;//每个琴键宽度
        width2Div8 = jpapplication.getWidthPixels() / 4f;
        width4Div8 = jpapplication.getWidthPixels() / 2f;
        width5Div8 = jpapplication.getWidthPixels() / 1.6f;
        width6Div8 = jpapplication.getWidthPixels() * 0.75f;
        width8Div8 = jpapplication.getWidthPixels();
        progressBarHight = progressBarImage.getHeight();
        progressBarWidth = progressBarImage.getWidth();
        halfHeightSub10 = jpapplication.getHalfHeightSub10();
        surfaceholder = getHolder();
        surfaceholder.addCallback(this);
        surfaceholder.setKeepScreenOn(true);
        f4801bz = new Rect();
        f4769bA = new Rect();
        f4772bD = new RectF();
        f4773bE = new Rect();
        f4774bF = new RectF();
        f4770bB = new Rect();
        f4771bC = new RectF();
        uploadTimeArray = new byte[uploadTime];
        uploadTouchStatusList = new ArrayList<>();
    }

    private int judgeTouchNote(int i, boolean isMidi) {
        int i2;
        int i3;
        boolean midiFlag = isMidi && i + 12 == noteRightValue;
        int returnNoteValue = midiFlag ? 12 : i % 12;
        if (i == noteRightValue || midiFlag) {
            int i4;
            isTouchRightNote = true;
            double abs;
            try {
                abs = jpapplication.getWhiteKeyHeight() - judgingNote.posiAdd15AddAnim;
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
            if (jpapplication.getNoteDismiss()) {
                judgingNote.noteImage = nullImage;
                int index = notesList.indexOf(judgingNote);
                for (int i1 = 0; i1 < notesList.size(); i1++) {
                    PlayNote note = notesList.get(i1);
                    if (note.trackValue == note.handValue && !note.hideNote && i1 > index) {
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
        showScoreAndLevels.computeScore(new ShowTouchNotesLevel(i2, this, comboNum, screenWidth, screenHeight), i3, comboNum);
        byte b = (byte) (i2 + 1);
        uploadTouchStatusList.add(b);
        if (gameType == 2) {
            uploadTimeArray[uploadNoteIndex] = b;
            if (uploadNoteIndex < uploadTime - 1) {
                uploadNoteIndex++;
                return returnNoteValue;
            }
            pianoPlay.sendMsg((byte) 25, pianoPlay.hallID, "", uploadTimeArray);
            uploadNoteIndex = 0;
        }
        return returnNoteValue;
    }

    final int eventPositionToTouchNoteNum(float f, float f2) {
        int i = 1;
        float w = jpapplication.getWhiteKeyHeight() + jpapplication.getBlackKeyHeight();
        if (f2 >= jpapplication.getWhiteKeyHeight()) {
            if (f2 <= w) {
                if (Math.abs(widthDiv8 - f) < jpapplication.getBlackKeyWidth()) {
                    return 1;
                }
                if (Math.abs(width2Div8 - f) < jpapplication.getBlackKeyWidth()) {
                    return 3;
                }
                if (Math.abs(width4Div8 - f) < jpapplication.getBlackKeyWidth()) {
                    return 6;
                }
                if (Math.abs(width5Div8 - f) < jpapplication.getBlackKeyWidth()) {
                    return 8;
                }
                if (Math.abs(width6Div8 - f) < jpapplication.getBlackKeyWidth()) {
                    return 10;
                }
                if (Math.abs(width8Div8 - f) < jpapplication.getBlackKeyWidth()) {
                    return 12;
                }
            }
            if (f2 >= jpapplication.getWhiteKeyHeight()) {
                while (i < 9) {
                    if (f >= ((float) (i - 1)) * jpapplication.getWidthDiv8() && f < ((float) i) * jpapplication.getWidthDiv8()) {
                        switch (i) {
                            case 1:
                                return 0;
                            case 2:
                                return 2;
                            case 3:
                                return 4;
                            case 4:
                                return 5;
                            case 5:
                                return 7;
                            case 6:
                                return 9;
                            case 7:
                                return 11;
                            case 8:
                                return 12;
                        }
                    }
                    i++;
                }
            }
        }
        return -1;
    }

    final void judgeAndPlaySound(int i) {
        int noteOctaveOffset = noteMod12 * 12;
        judgeTouchNote(i + noteOctaveOffset, false);
        if (i > -2) {
            jpapplication.playSound(i + noteOctaveOffset, volume0);
        }
    }

    final int midiJudgeAndPlaySound(int i) {
        int noteOctaveOffset = noteMod12 * 12;
        int trueNote = judgeTouchNote(i + noteOctaveOffset, true);
        jpapplication.playSound(trueNote + noteOctaveOffset, volume0);
        return trueNote;
    }

    final void mo2929a(Canvas canvas) {
        int size = (noteCounts - notesList.size()) * (jpapplication.getWidthPixels() - 2) / noteCounts;
        if (canvas != null) {
            f4773bE.set(0, 0, progressBarWidth, progressBarHight);
            f4774bF.set(0, 0, (float) jpapplication.getWidthPixels(), (float) progressBarHight);
            canvas.drawBitmap(progressBarBaseImage, null, f4774bF, null);
            f4774bF.set(0, 0, (float) size, (float) progressBarHight);
            canvas.drawBitmap(progressBarImage, f4773bE, f4774bF, null);
        }
        if (notesList.size() == 0) {
            pianoPlay.isPlayingStart = false;
            startFirstNoteTouching = false;
            JSONObject jSONObject = new JSONObject();
            pianoPlay.f4620k = true;
            int size2;
            byte[] bArr;
            Message message;
            switch (gameType) {
                case 4:
                    size2 = uploadTouchStatusList.size();
                    bArr = new byte[size2];
                    for (size = 0; size < size2; size++) {
                        bArr[size] = uploadTouchStatusList.get(size);
                    }
                    try {
                        jSONObject.put("S", GZIP.toZIP(new String(bArr, StandardCharsets.UTF_8)));
                        long x = pianoPlay.times * serialID;
                        long time = jpapplication.getServerTime();
                        long crypt = (time >>> 12 | time << 52) ^ x;
                        jSONObject.put("K", 4);
                        jSONObject.put("Z", crypt);
                        pianoPlay.sendMsg((byte) 16, (byte) 0, jSONObject.toString(), null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case 3:
                    size2 = uploadTouchStatusList.size();
                    bArr = new byte[size2];
                    for (size = 0; size < size2; size++) {
                        bArr[size] = uploadTouchStatusList.get(size);
                    }
                    try {
                        jSONObject.put("S", GZIP.toZIP(new String(bArr, StandardCharsets.UTF_8)));
                        jSONObject.put("T", 3);
                        long x = pianoPlay.times * serialID;
                        long time = jpapplication.getServerTime();
                        long crypt = (time >>> 12 | time << 52) ^ x;
                        jSONObject.put("Z", crypt);
                        pianoPlay.sendMsg((byte) 40, (byte) 0, jSONObject.toString(), null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case 2:
                    message = new Message();
                    message.what = 8;
                    pianoPlay.pianoPlayHandler.handleMessage(message);
                    size2 = uploadTouchStatusList.size();
                    bArr = new byte[size2];
                    for (size = 0; size < size2; size++) {
                        bArr[size] = uploadTouchStatusList.get(size);
                    }
                    try {
                        jSONObject.put("S", GZIP.toZIP(new String(bArr, StandardCharsets.UTF_8)));
                        long x = pianoPlay.roomBundle.getByte("ID") * serialID;
                        long time = jpapplication.getServerTime();
                        long crypt = (time >>> 12 | time << 52) ^ x;
                        jSONObject.put("Z", crypt);
                        pianoPlay.sendMsg((byte) 5, (byte) 0, jSONObject.toString(), null);
                    } catch (Exception e3) {
                        e3.printStackTrace();
                    }
                    break;
                default:
                    if (pianoPlay.isOpenRecord) {
                        message = new Message();
                        message.what = 22;
                        pianoPlay.pianoPlayHandler.handleMessage(message);
                    }
                    Intent intent = new Intent();
                    intent.setClass(pianoPlay, PlayFinish.class);
                    intent.putExtra("head", gameType);
                    intent.putExtra("topScore", score);
                    intent.putExtra("perf", perfectNum);
                    size2 = uploadTouchStatusList.size();
                    bArr = new byte[size2];
                    for (size = 0; size < size2; size++) {
                        bArr[size] = uploadTouchStatusList.get(size);
                    }
                    intent.putExtra("scoreArray", bArr);
                    intent.putExtra("totalScore", showScoreAndLevels.levelScore);
                    intent.putExtra("combo_scr", showScoreAndLevels.comboScore);
                    intent.putExtra("cool", coolNum);
                    intent.putExtra("great", greatNum);
                    intent.putExtra("bad", badNum);
                    intent.putExtra("miss", missNum);
                    intent.putExtra("name", songsName);
                    intent.putExtra("songID", songID);
                    intent.putExtra("path", songsPath);
                    intent.putExtra("nandu", nandu);
                    intent.putExtra("leftnandu", leftNandu);
                    intent.putExtra("songstime", songsTime);
                    intent.putExtra("hand", handValue);
                    intent.putExtra("top_combo", topComboNum);
                    pianoPlay.startActivityForResult(intent, size2);
                    break;
            }
        }
    }

    final void mo2930b(Canvas canvas) {
        int E;
        int i;
        int i2;
        int i3;
        tempNotesArray.clear();
        newNote = true;
        f4713V = true;
        for (PlayNote note : notesList) {
            currentPlayNote = note;
            if (currentPlayNote.posiAdd15AddAnim <= jpapplication.getHalfHeightSub20() + 60) {
                if (currentPlayNote.trackValue == currentPlayNote.handValue && currentPlayNote.posiAdd15AddAnim < jpapplication.getWhiteKeyHeight() && newNote) {
                    if (currentPlayNote.unPassed) {
                        hasTouched = false;
                        currentPlayNote.unPassed = false;
                    }
                    if (!currentPlayNote.noteImage.equals(nullImage)) {
                        judgingNote = currentPlayNote;
                        if (currentPlayNote.posiAdd15AddAnim > -15f) {
                            if (jpapplication.getIsShowLine() && canvas != null) {
                                canvas.drawLine(currentPlayNote.getHalfWidth() + currentPlayNote.noteXPosition, jpapplication.getWhiteKeyHeight(), currentPlayNote.getHalfWidth() + currentPlayNote.noteXPosition, 15 + currentPlayNote.posiAdd15AddAnim, line);
                            }
                            if (jpapplication.changeNotesColor) {
                                currentPlayNote.noteImage = playNoteImage;
                            } else if (currentPlayNote.posiAdd15AddAnim >= jpapplication.getHalfHeightSub20()) {
                                currentPlayNote.noteImage = playNoteImage;
                            }
                        }
                    }
                    if (jpapplication.getGameMode() == 2 && currentPlayNote.posiAdd15AddAnim < jpapplication.getWhiteKeyHeight() + 10 &&
                            currentPlayNote.posiAdd15AddAnim >= jpapplication.getHalfHeightSub20() && !hasTouched && !currentPlayNote.hideNote) {
                        isTouchRightNote = false;
                    }
                    noteRightValue = currentPlayNote.noteValue;
                    if (!currentPlayNote.hideNote) {
                        noteMod12 = currentPlayNote.noteDiv12;
                    }
                    volume0 = currentPlayNote.volumeValue;
                    newNote = false;
                }
                if (jpapplication.getIfLoadLongKeyboard() && currentPlayNote.posiAdd15AddAnim < jpapplication.getWhiteKeyHeight() && f4713V) {
                    f4813n = currentPlayNote.noteValue;
                    f4713V = false;
                }
                if (gameType > 0) {
                    if (currentPlayNote.mo3107b(canvas) < 0) {
                        break;
                    }
                } else if (currentPlayNote.mo3108c(canvas) < 0) {
                    break;
                }
            } else {
                tempNotesArray.add(currentPlayNote);
            }
        }
        notesList.removeAll(tempNotesArray);
        if (canvas != null && touchNotesList != null && jpapplication.getIfShowNotesLevel()) {
            try {
                for (ShowTouchNotesLevel showTouchNotesLevel : touchNotesList) {
                    Rect rect = f4770bB;
                    RectF rectF = f4771bC;
                    if (showTouchNotesLevel.levelImage != null) {
                        canvas.drawBitmap(showTouchNotesLevel.levelImage, (showTouchNotesLevel.screenWidth - (showTouchNotesLevel.playView.perfectImage.getWidth() / 2f)), showTouchNotesLevel.screenHeight, null);
                    }
                    if (showTouchNotesLevel.comboNum > 1 && showTouchNotesLevel.comboNum <= 9) {
                        canvas.drawBitmap(showTouchNotesLevel.playView.xImage, (showTouchNotesLevel.screenWidth + (showTouchNotesLevel.playView.perfectImage.getWidth() / 2f)), showTouchNotesLevel.screenHeight, null);
                        rect.set((showTouchNotesLevel.comboNum * showTouchNotesLevel.playView.scoreNumImage.getWidth()) / 10, 0, ((showTouchNotesLevel.comboNum + 1) * showTouchNotesLevel.playView.scoreNumImage.getWidth()) / 10, showTouchNotesLevel.playView.scoreNumImage.getHeight());
                        rectF.set((float) ((showTouchNotesLevel.screenWidth + (showTouchNotesLevel.playView.perfectImage.getWidth() / 2)) + showTouchNotesLevel.playView.xImage.getWidth()), (float) showTouchNotesLevel.screenHeight, (float) (((showTouchNotesLevel.screenWidth + (showTouchNotesLevel.playView.perfectImage.getWidth() / 2)) + showTouchNotesLevel.playView.xImage.getWidth()) + (showTouchNotesLevel.playView.scoreNumImage.getWidth() / 10)), (float) (showTouchNotesLevel.screenHeight + showTouchNotesLevel.playView.scoreNumImage.getHeight()));
                        canvas.drawBitmap(showTouchNotesLevel.playView.scoreNumImage, rect, rectF, null);
                    } else if (showTouchNotesLevel.comboNum > 9 && showTouchNotesLevel.comboNum <= 99) {
                        i = showTouchNotesLevel.comboNum % 10;
                        i2 = showTouchNotesLevel.comboNum / 10;
                        canvas.drawBitmap(showTouchNotesLevel.playView.xImage, (showTouchNotesLevel.screenWidth + (showTouchNotesLevel.playView.perfectImage.getWidth() / 2f)), showTouchNotesLevel.screenHeight, null);
                        rect.set((showTouchNotesLevel.playView.scoreNumImage.getWidth() * i2) / 10, 0, ((i2 + 1) * showTouchNotesLevel.playView.scoreNumImage.getWidth()) / 10, showTouchNotesLevel.playView.scoreNumImage.getHeight());
                        rectF.set((float) ((showTouchNotesLevel.screenWidth + (showTouchNotesLevel.playView.perfectImage.getWidth() / 2)) + showTouchNotesLevel.playView.xImage.getWidth()), (float) showTouchNotesLevel.screenHeight, (float) (((showTouchNotesLevel.screenWidth + (showTouchNotesLevel.playView.perfectImage.getWidth() / 2)) + showTouchNotesLevel.playView.xImage.getWidth()) + (showTouchNotesLevel.playView.scoreNumImage.getWidth() / 10)), (float) (showTouchNotesLevel.screenHeight + showTouchNotesLevel.playView.scoreNumImage.getHeight()));
                        canvas.drawBitmap(showTouchNotesLevel.playView.scoreNumImage, rect, rectF, null);
                        rect.set((showTouchNotesLevel.playView.scoreNumImage.getWidth() * i) / 10, 0, ((i + 1) * showTouchNotesLevel.playView.scoreNumImage.getWidth()) / 10, showTouchNotesLevel.playView.scoreNumImage.getHeight());
                        rectF.set((float) (((showTouchNotesLevel.screenWidth + (showTouchNotesLevel.playView.perfectImage.getWidth() / 2)) + showTouchNotesLevel.playView.xImage.getWidth()) + (showTouchNotesLevel.playView.scoreNumImage.getWidth() / 10)), (float) showTouchNotesLevel.screenHeight, (float) (((showTouchNotesLevel.screenWidth + (showTouchNotesLevel.playView.perfectImage.getWidth() / 2)) + showTouchNotesLevel.playView.xImage.getWidth()) + ((showTouchNotesLevel.playView.scoreNumImage.getWidth() / 10) * 2)), (float) (showTouchNotesLevel.screenHeight + showTouchNotesLevel.playView.scoreNumImage.getHeight()));
                        canvas.drawBitmap(showTouchNotesLevel.playView.scoreNumImage, rect, rectF, null);
                    } else if (showTouchNotesLevel.comboNum > 99 && showTouchNotesLevel.comboNum <= 999) {
                        i = showTouchNotesLevel.comboNum / 100;
                        i2 = (showTouchNotesLevel.comboNum - (i * 100)) / 10;
                        i3 = showTouchNotesLevel.comboNum % 10;
                        canvas.drawBitmap(showTouchNotesLevel.playView.xImage, (showTouchNotesLevel.screenWidth + (showTouchNotesLevel.playView.perfectImage.getWidth() / 2f)), showTouchNotesLevel.screenHeight, null);
                        rect.set((showTouchNotesLevel.playView.scoreNumImage.getWidth() * i) / 10, 0, ((i + 1) * showTouchNotesLevel.playView.scoreNumImage.getWidth()) / 10, showTouchNotesLevel.playView.scoreNumImage.getHeight());
                        rectF.set((float) ((showTouchNotesLevel.screenWidth + (showTouchNotesLevel.playView.perfectImage.getWidth() / 2)) + showTouchNotesLevel.playView.xImage.getWidth()), (float) showTouchNotesLevel.screenHeight, (float) (((showTouchNotesLevel.screenWidth + (showTouchNotesLevel.playView.perfectImage.getWidth() / 2)) + showTouchNotesLevel.playView.xImage.getWidth()) + (showTouchNotesLevel.playView.scoreNumImage.getWidth() / 10)), (float) (showTouchNotesLevel.screenHeight + showTouchNotesLevel.playView.scoreNumImage.getHeight()));
                        canvas.drawBitmap(showTouchNotesLevel.playView.scoreNumImage, rect, rectF, null);
                        rect.set((showTouchNotesLevel.playView.scoreNumImage.getWidth() * i2) / 10, 0, ((i2 + 1) * showTouchNotesLevel.playView.scoreNumImage.getWidth()) / 10, showTouchNotesLevel.playView.scoreNumImage.getHeight());
                        rectF.set((float) (((showTouchNotesLevel.screenWidth + (showTouchNotesLevel.playView.perfectImage.getWidth() / 2)) + showTouchNotesLevel.playView.xImage.getWidth()) + (showTouchNotesLevel.playView.scoreNumImage.getWidth() / 10)), (float) showTouchNotesLevel.screenHeight, (float) (((showTouchNotesLevel.screenWidth + (showTouchNotesLevel.playView.perfectImage.getWidth() / 2)) + showTouchNotesLevel.playView.xImage.getWidth()) + ((showTouchNotesLevel.playView.scoreNumImage.getWidth() / 10) * 2)), (float) (showTouchNotesLevel.screenHeight + showTouchNotesLevel.playView.scoreNumImage.getHeight()));
                        canvas.drawBitmap(showTouchNotesLevel.playView.scoreNumImage, rect, rectF, null);
                        rect.set((showTouchNotesLevel.playView.scoreNumImage.getWidth() * i3) / 10, 0, ((i3 + 1) * showTouchNotesLevel.playView.scoreNumImage.getWidth()) / 10, showTouchNotesLevel.playView.scoreNumImage.getHeight());
                        rectF.set((float) (((showTouchNotesLevel.screenWidth + (showTouchNotesLevel.playView.perfectImage.getWidth() / 2)) + showTouchNotesLevel.playView.xImage.getWidth()) + ((showTouchNotesLevel.playView.scoreNumImage.getWidth() / 10) * 2)), (float) showTouchNotesLevel.screenHeight, (float) (((showTouchNotesLevel.screenWidth + (showTouchNotesLevel.playView.perfectImage.getWidth() / 2)) + showTouchNotesLevel.playView.xImage.getWidth()) + ((showTouchNotesLevel.playView.scoreNumImage.getWidth() / 10) * 3)), (float) (showTouchNotesLevel.screenHeight + showTouchNotesLevel.playView.scoreNumImage.getHeight()));
                        canvas.drawBitmap(showTouchNotesLevel.playView.scoreNumImage, rect, rectF, null);
                    } else if (showTouchNotesLevel.comboNum > 999 && showTouchNotesLevel.comboNum <= 9999) {
                        i = showTouchNotesLevel.comboNum / 1000;
                        i2 = (showTouchNotesLevel.comboNum - (i * 1000)) / 100;
                        i3 = ((showTouchNotesLevel.comboNum - (i * 1000)) - (i2 * 100)) / 10;
                        int i4 = showTouchNotesLevel.comboNum % 10;
                        canvas.drawBitmap(showTouchNotesLevel.playView.xImage, (showTouchNotesLevel.screenWidth + (showTouchNotesLevel.playView.perfectImage.getWidth() / 2f)), showTouchNotesLevel.screenHeight, null);
                        rect.set((showTouchNotesLevel.playView.scoreNumImage.getWidth() * i) / 10, 0, ((i + 1) * showTouchNotesLevel.playView.scoreNumImage.getWidth()) / 10, showTouchNotesLevel.playView.scoreNumImage.getHeight());
                        rectF.set((float) ((showTouchNotesLevel.screenWidth + (showTouchNotesLevel.playView.perfectImage.getWidth() / 2)) + showTouchNotesLevel.playView.xImage.getWidth()), (float) showTouchNotesLevel.screenHeight, (float) (((showTouchNotesLevel.screenWidth + (showTouchNotesLevel.playView.perfectImage.getWidth() / 2)) + showTouchNotesLevel.playView.xImage.getWidth()) + (showTouchNotesLevel.playView.scoreNumImage.getWidth() / 10)), (float) (showTouchNotesLevel.screenHeight + showTouchNotesLevel.playView.scoreNumImage.getHeight()));
                        canvas.drawBitmap(showTouchNotesLevel.playView.scoreNumImage, rect, rectF, null);
                        rect.set((showTouchNotesLevel.playView.scoreNumImage.getWidth() * i2) / 10, 0, ((i2 + 1) * showTouchNotesLevel.playView.scoreNumImage.getWidth()) / 10, showTouchNotesLevel.playView.scoreNumImage.getHeight());
                        rectF.set((float) (((showTouchNotesLevel.screenWidth + (showTouchNotesLevel.playView.perfectImage.getWidth() / 2)) + showTouchNotesLevel.playView.xImage.getWidth()) + (showTouchNotesLevel.playView.scoreNumImage.getWidth() / 10)), (float) showTouchNotesLevel.screenHeight, (float) (((showTouchNotesLevel.screenWidth + (showTouchNotesLevel.playView.perfectImage.getWidth() / 2)) + showTouchNotesLevel.playView.xImage.getWidth()) + ((showTouchNotesLevel.playView.scoreNumImage.getWidth() / 10) * 2)), (float) (showTouchNotesLevel.screenHeight + showTouchNotesLevel.playView.scoreNumImage.getHeight()));
                        canvas.drawBitmap(showTouchNotesLevel.playView.scoreNumImage, rect, rectF, null);
                        rect.set((showTouchNotesLevel.playView.scoreNumImage.getWidth() * i3) / 10, 0, ((i3 + 1) * showTouchNotesLevel.playView.scoreNumImage.getWidth()) / 10, showTouchNotesLevel.playView.scoreNumImage.getHeight());
                        rectF.set((float) (((showTouchNotesLevel.screenWidth + (showTouchNotesLevel.playView.perfectImage.getWidth() / 2)) + showTouchNotesLevel.playView.xImage.getWidth()) + ((showTouchNotesLevel.playView.scoreNumImage.getWidth() / 10) * 2)), (float) showTouchNotesLevel.screenHeight, (float) (((showTouchNotesLevel.screenWidth + (showTouchNotesLevel.playView.perfectImage.getWidth() / 2)) + showTouchNotesLevel.playView.xImage.getWidth()) + ((showTouchNotesLevel.playView.scoreNumImage.getWidth() / 10) * 3)), (float) (showTouchNotesLevel.screenHeight + showTouchNotesLevel.playView.scoreNumImage.getHeight()));
                        canvas.drawBitmap(showTouchNotesLevel.playView.scoreNumImage, rect, rectF, null);
                        rect.set((showTouchNotesLevel.playView.scoreNumImage.getWidth() * i4) / 10, 0, ((i4 + 1) * showTouchNotesLevel.playView.scoreNumImage.getWidth()) / 10, showTouchNotesLevel.playView.scoreNumImage.getHeight());
                        rectF.set((float) (((showTouchNotesLevel.screenWidth + (showTouchNotesLevel.playView.perfectImage.getWidth() / 2)) + showTouchNotesLevel.playView.xImage.getWidth()) + ((showTouchNotesLevel.playView.scoreNumImage.getWidth() / 10) * 3)), (float) showTouchNotesLevel.screenHeight, (float) (((showTouchNotesLevel.screenWidth + (showTouchNotesLevel.playView.perfectImage.getWidth() / 2)) + showTouchNotesLevel.playView.xImage.getWidth()) + ((showTouchNotesLevel.playView.scoreNumImage.getWidth() / 10) * 4)), (float) (showTouchNotesLevel.screenHeight + showTouchNotesLevel.playView.scoreNumImage.getHeight()));
                        canvas.drawBitmap(showTouchNotesLevel.playView.scoreNumImage, rect, rectF, null);
                    } else if (showTouchNotesLevel.comboNum > 9999) {
                        canvas.drawBitmap(showTouchNotesLevel.playView.xImage, (showTouchNotesLevel.screenWidth + (showTouchNotesLevel.playView.perfectImage.getWidth() / 2f)), showTouchNotesLevel.screenHeight, null);
                        canvas.drawBitmap(showTouchNotesLevel.playView.maxImage, ((showTouchNotesLevel.screenWidth + (showTouchNotesLevel.playView.perfectImage.getWidth() / 2f)) + showTouchNotesLevel.playView.xImage.getWidth()), showTouchNotesLevel.screenHeight, null);
                    }
                }
            } catch (Exception ignored) {
            }
        }
        int i5 = showScoreAndLevels.levelScore;
        int i6 = 0;
        i = scoreImage.getWidth();
        i2 = scoreNumImage.getWidth() / 10;
        i3 = scoreNumImage.getHeight();
        f4788bm.clear();
        E = Math.max(i5, 0);
        for (i5 = 0; i5 < 5; i5++) {
            f4788bm.add(i5, (int) ((((double) E) % Math.pow(10, i5 + 1)) / Math.pow(10, i5)));
        }
        E = 0;
        while (true) {
            i5 = E;
            if (i5 >= 5) {
                f4772bD.set((float) ((jpapplication.getWidthPixels() - (i2 * 5)) - i), 0, (float) (jpapplication.getWidthPixels() - (i2 * 5)), scoreImage.getHeight());
                canvas.drawBitmap(scoreImage, null, f4772bD, null);
                return;
            }
            E = f4788bm.get(i5);
            f4801bz.set(E * i2, 0, (E + 1) * i2, i3);
            f4769bA.set(jpapplication.getWidthPixels() - ((i6 + 1) * i2), 0, jpapplication.getWidthPixels() - (i2 * i6), i3);
            canvas.drawBitmap(scoreNumImage, f4801bz, f4769bA, null);
            i6++;
            E = i5 + 1;
        }
    }

    final void mo2931c(Canvas canvas) {
        newNote = true;
        tempNotesArray.clear();
        for (PlayNote currentPlayNote : notesList) {
            if (currentPlayNote.posiAdd15AddAnim <= halfHeightSub10) {
                if (currentPlayNote.trackValue == currentPlayNote.handValue && currentPlayNote.posiAdd15AddAnim < jpapplication.getWhiteKeyHeight() && newNote) {
                    if (!currentPlayNote.hideNote) {
                        noteMod12 = currentPlayNote.noteDiv12;
                    }
                    volume0 = currentPlayNote.volumeValue;
                    newNote = false;
                }
                if (jpapplication.getIfLoadLongKeyboard() && currentPlayNote.posiAdd15AddAnim < jpapplication.getWhiteKeyHeight() && f4713V) {
                    f4813n = currentPlayNote.noteValue;
                    f4713V = false;
                }
                currentPlayNote.noCompatibleMode(canvas);
            } else {
                tempNotesArray.add(currentPlayNote);
            }
        }
        notesList.removeAll(tempNotesArray);
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        downNotes = new DownNotes(jpapplication, notesDownSpeed, pianoPlay);
        downNotes.start();
        loadbackgrounds = new LoadBackgrounds(jpapplication, this, pianoPlay);
        loadbackgrounds.start();
        showScoreAndLevels = new ShowScoreAndLevels(touchNotesList, pianoPlay);
        showScoreAndLevels.start();
        if (jpapplication.getGameMode() != 3) {
            setOnTouchListener(new TouchNotes(this));
            setAccessibilityDelegate(new View.AccessibilityDelegate() {
                @Override
                public boolean performAccessibilityAction(View host, int action, Bundle args) {
                    if (action == AccessibilityNodeInfo.ACTION_CLICK
                            || action == AccessibilityNodeInfo.ACTION_LONG_CLICK) {
                        pianoPlay.finish();
                        return true;
                    }
                    return super.performAccessibilityAction(host, action, args);
                }
            });
        }
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
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        if (pianoPlay.isBack) {
            if (downNotes != null) {
                while (downNotes.isAlive()) {
                    pianoPlay.isPlayingStart = false;
                    startFirstNoteTouching = false;
                }
            }
            if (loadbackgrounds != null) {
                while (loadbackgrounds.isAlive()) {
                    pianoPlay.isPlayingStart = false;
                }
            }
            if (showScoreAndLevels != null) {
                while (showScoreAndLevels.isAlive()) {
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
            practiceNoteImage.recycle();
            practiceNoteImage = null;
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
