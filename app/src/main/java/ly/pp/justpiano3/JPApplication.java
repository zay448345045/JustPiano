package ly.pp.justpiano3;

import android.app.Application;
import android.content.*;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetFileDescriptor;
import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;
import android.media.midi.MidiDeviceInfo;
import android.media.midi.MidiManager;
import android.media.midi.MidiOutputPort;
import android.os.*;
import android.preference.PreferenceManager;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.multidex.MultiDex;
import javazoom.jl.converter.Converter;
import ly.pp.justpiano3.entity.SimpleUser;
import ly.pp.justpiano3.entity.User;
import ly.pp.justpiano3.service.ConnectionService;
import ly.pp.justpiano3.utils.ChatBlackUserUtil;
import ly.pp.justpiano3.utils.EncryptUtil;

import java.io.*;
import java.security.KeyPair;
import java.security.PublicKey;
import java.util.*;

public final class JPApplication extends Application {

    /**
     * 官网地址
     */
    public static final String WEBSITE_URL = "justpiano.fun";

    /**
     * apk资源下载地址
     */
    public static final String RESOURCE_WEBSITE_URL = "i.justpiano.fun";

    /**
     * 对战服务器地址
     */
    public static final String ONLINE_SERVER_URL = "server.justpiano.fun";

    /**
     * 测试对战服务器地址
     */
    public static final String TEST_ONLINE_SERVER_URL = "test.justpiano.fun";

    private List<MidiConnectionListener> midiConnectionListeners;
    public static String kitiName = "";
    public static SharedPreferences accountListSharedPreferences;
    public static int SETTING_MODE_CODE = 122;

    static {
        System.loadLibrary("soundengine");
    }

    public String title = "";
    public String f4072f = "";
    public String f4073g = "";
    public String f4074h = "";
    boolean changeNotesColor = true;
    private ConnectionService connectionService;
    private int whiteKeyHeight;
    private float blackKeyHeight;
    private float blackKeyWidth;
    private float chordVolume = 0.8f;
    private int notesDownSpeed = 6;
    private boolean isBindService;
    private int gameMode;
    private int midiKeyboardTune;
    private int keyboardSoundTune;
    private boolean keyboardAnim;
    private boolean isOpenChord = true;
    private boolean showTouchNotesLevel = true;
    private boolean autoPlay = true;
    private boolean showLine = true;
    private boolean loadLongKeyboard;
    private boolean isShowDialog;
    private boolean noteDismiss = true;
    private boolean chatSound;
    private int playSongsMode;
    private float noteSize = 1;
    private final Map<Byte, User> hashMap = new HashMap<>();
    private String accountName = "";
    private String password = "";
    private List<SimpleUser> chatBlackList;
    private String nowSongsName = "";
    private String server = ONLINE_SERVER_URL;
    private boolean keyboardPrefer;
    private final ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            setConnectionService(((ConnectionService.JPBinder) service).getConnectionService());
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            setConnectionService(null);
        }
    };
    private int widthPixels;
    private int heightPixels;
    private int animPosition;
    private int animFrame = 8;
    private float tempSpeed = 1;
    private float widthDiv8;
    private float halfHeightSub20;
    private float halfHeightSub10;
    private float whiteKeyHeightAdd90;
    private int roughLine;
    private MidiManager mMidiManager;
    public MidiOutputPort midiOutputPort;

    /**
     * RSA客户端生成密钥存储
     */
    private KeyPair keyPair;

    /**
     * 服务端公钥存储
     */
    private PublicKey publicKey;

    /**
     * 服务端时间
     */
    public long serverTimeInterval;

    /**
     * 获取客户端密钥
     *
     * @return
     */
    public KeyPair getDeviceKeyPair() {
        return keyPair;
    }

    /**
     * 更新客户端密钥
     */
    public void updateDeviceKeyPair() {
        keyPair = EncryptUtil.generateRSAKeyPair();
    }

    public void setServerPublicKey(String publicKeyStr) {
        this.publicKey = EncryptUtil.generatePublicKey(publicKeyStr);
    }

    public PublicKey getServerPublicKey() {
        return publicKey;
    }

    public static native void setupAudioStreamNative(int var1, int var2);

    public static native void teardownAudioStreamNative();

    public static native void loadWavAssetNative(byte[] var1, int var2, float var3);

    public static native void unloadWavAssetsNative();

    public static native void trigger(int var1, int var2);

    public static native void setRecord(boolean record);

    public static native void setRecordFilePath(String recordFilePath);

    public static void preloadSounds(Context context, int i) {
        try {
            Converter converter = new Converter();
            converter.convert(context.getFilesDir().getAbsolutePath() + "/Sounds/" + i + ".mp3", context.getFilesDir().getAbsolutePath() + "/Sounds/" + i + ".wav");
            loadWavInputStreamByIndex(context, i);
        } catch (Exception e1) {
            try {
                AssetFileDescriptor assetFD = context.getResources().getAssets().openFd("sound/" + i + ".mp3");
                Converter converter = new Converter();
                converter.convert(assetFD.createInputStream(), context.getFilesDir().getAbsolutePath() + "/Sounds/" + i + ".wav", null, null);
                loadWavInputStreamByIndex(context, i);
                assetFD.close();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }

    private static void loadWavInputStreamByIndex(Context context, int index) throws IOException {
        FileInputStream dataStream = new FileInputStream(context.getFilesDir().getAbsolutePath() + "/Sounds/" + index + ".wav");
        int dataLen = dataStream.available();
        byte[] dataBytes = new byte[dataLen];
        dataStream.read(dataBytes, 0, dataLen);
        loadWavAssetNative(dataBytes, index, 0);
        dataStream.close();
    }

    private static void loadChatWav(Context context) throws IOException {
        AssetFileDescriptor assetFD = context.getResources().getAssets().openFd("chat_b5.wav");
        FileInputStream dataStream = assetFD.createInputStream();
        int dataLen = dataStream.available();
        byte[] dataBytes = new byte[dataLen];
        dataStream.read(dataBytes, 0, dataLen);
        loadWavAssetNative(dataBytes, 0, 0);
        assetFD.close();
        dataStream.close();
    }

    /**
     * 移动文件到新文件的位置（拷贝流）
     *
     * @param src 源文件对象
     * @param des 目标文件对象
     */
    public static boolean moveFile(File src, File des) {
        if (!src.exists()) {
            return false;
        }
        if (des.exists()) {
            des.delete();
        }
        try (BufferedInputStream reader = new BufferedInputStream(new FileInputStream(src)); BufferedOutputStream writer = new BufferedOutputStream(new FileOutputStream(des))) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, length);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            src.delete();
        }
        return true;
    }

    public static void reLoadOriginalSounds(Context context) {
        File dir = new File(context.getFilesDir(), "Sounds");
        if (dir.isDirectory()) {
            File[] listFiles = dir.listFiles();
            if (listFiles != null && listFiles.length > 0) {
                for (File delete : listFiles) {
                    delete.delete();
                }
            }
        }
        teardownAudioStreamNative();
        unloadWavAssetsNative();
        for (int i = 108; i >= 24; i--) {
            preloadSounds(context, i);
        }
        confirmLoadSounds(context);
        SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(context).edit();
        edit.putString("sound_list", "original");
        edit.apply();
    }

    public static void confirmLoadSounds(Context context) {
        try {
            loadChatWav(context);
        } catch (IOException e) {
            e.printStackTrace();
        }
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean compatibleSound = sharedPreferences.getBoolean("compatible_sound", true);
        setupAudioStreamNative(compatibleSound ? 2 : 4, 44100);
    }

    public static void initSettings(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("down_speed", "6");
        editor.putString("anim_frame", "4");
        editor.putBoolean("note_dismiss", false);
        editor.putString("note_size", "1");
        editor.putString("b_s_vol", "0.8");
        editor.putString("temp_speed", "1.0");
        editor.putString("sound_list", "original");
        editor.apply();
    }

    public void m3520a(Canvas canvas, Rect rect, Rect rect2, PlayView playView, int i) {
        switch (i) {
            case 0:
            case 12:
            case 5:
                canvas.drawBitmap(playView.fireImage, null, rect, null);
                canvas.drawBitmap(playView.whiteKeyRightImage, null, rect2, null);
                return;
            case 1:
            case 10:
            case 8:
            case 6:
            case 3:
                canvas.drawBitmap(playView.fireImage, null, rect, null);
                canvas.drawBitmap(playView.blackKeyImage, null, rect2, null);
                return;
            case 2:
            case 9:
            case 7:
                canvas.drawBitmap(playView.fireImage, null, rect, null);
                canvas.drawBitmap(playView.whiteKeyMiddleImage, null, rect2, null);
                return;
            case 4:
            case 11:
                canvas.drawBitmap(playView.fireImage, null, rect, null);
                canvas.drawBitmap(playView.whiteKeyLeftImage, null, rect2, null);
                return;
            default:
        }
    }

    public int getGameMode() {
        return gameMode;
    }

    public void setGameMode(int i) {
        gameMode = i;  //0为普通模式 1为自由模式 2为练习模式 3为欣赏模式
    }

    public List<Rect> getKeyRectArray() {
        List<Rect> arrayList = new ArrayList<>();
        arrayList.add(new Rect(0, whiteKeyHeight, (int) widthDiv8, heightPixels));
        arrayList.add(new Rect((int) (widthDiv8 - blackKeyWidth), whiteKeyHeight, (int) (widthDiv8 + blackKeyWidth), (int) (whiteKeyHeight + blackKeyHeight + 5)));
        arrayList.add(new Rect((int) widthDiv8, whiteKeyHeight, (int) (widthDiv8 * 2), heightPixels));
        arrayList.add(new Rect((int) (widthDiv8 * 2 - blackKeyWidth), whiteKeyHeight, (int) (widthDiv8 * 2 + blackKeyWidth), (int) (whiteKeyHeight + blackKeyHeight + 5)));
        arrayList.add(new Rect((int) (widthDiv8 * 2), whiteKeyHeight, (int) (widthDiv8 * 3), heightPixels));
        arrayList.add(new Rect((int) (widthDiv8 * 3), whiteKeyHeight, (int) (widthDiv8 * 4), heightPixels));
        arrayList.add(new Rect((int) (widthDiv8 * 4 - blackKeyWidth), whiteKeyHeight, (int) (widthDiv8 * 4 + blackKeyWidth), (int) (whiteKeyHeight + blackKeyHeight + 5)));
        arrayList.add(new Rect((int) (widthDiv8 * 4), whiteKeyHeight, (int) (widthDiv8 * 5), heightPixels));
        arrayList.add(new Rect((int) ((widthDiv8 * 5) - blackKeyWidth), whiteKeyHeight, (int) (widthDiv8 * 5 + blackKeyWidth), (int) (whiteKeyHeight + blackKeyHeight + 5)));
        arrayList.add(new Rect((int) (widthDiv8 * 5), whiteKeyHeight, (int) (widthDiv8 * 6), heightPixels));
        arrayList.add(new Rect((int) (widthDiv8 * 6 - blackKeyWidth), whiteKeyHeight, (int) (widthDiv8 * 6 + blackKeyWidth), (int) (whiteKeyHeight + blackKeyHeight + 5)));
        arrayList.add(new Rect((int) (widthDiv8 * 6), whiteKeyHeight, (int) (widthDiv8 * 7), heightPixels));
        arrayList.add(new Rect((int) (widthDiv8 * 7), whiteKeyHeight, (int) (widthDiv8 * 8), heightPixels));
        return arrayList;
    }

    public int getHeightPixels() {
        return heightPixels;
    }

    public void setHeightPixels(int i) {
        heightPixels = i;
    }

    public int getWidthPixels() {
        return widthPixels;
    }

    public void setWidthPixels(int i) {
        widthPixels = i;
    }

    public boolean hasKeyboardPerfer() {
        return keyboardPrefer;
    }

    public String getVersion() {
        String str = "";
        try {
            return getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            return str;
        }
    }

    public ServiceConnection getServiceConnection() {
        return serviceConnection;
    }

    public ConnectionService getConnectionService() {
        return connectionService;
    }

    public void setConnectionService(ConnectionService connectionService) {
        this.connectionService = connectionService;
    }

    public boolean getIsBindService() {
        return isBindService;
    }

    public void setIsBindService(boolean z) {
        isBindService = z;
    }

    public float getNoteSize() {
        return noteSize;
    }

    public Map<Byte, User> getHashmap() {
        return hashMap;
    }

    public String getAccountName() {
        if (accountName.isEmpty()) {
            accountName = accountListSharedPreferences.getString("name", "");
        }
        return accountName;
    }

    public void setAccountName(String str) {
        accountName = str;
    }

    public String getPassword() {
        if (password.isEmpty()) {
            password = accountListSharedPreferences.getString("password", "");
        }
        return password;
    }

    public void setPassword(String str) {
        password = str;
    }

    public String getNowSongsName() {
        return nowSongsName;
    }

    public void setNowSongsName(String str) {
        nowSongsName = str;
    }

    public String getKitiName() {
        if (kitiName.isEmpty()) {
            kitiName = accountListSharedPreferences.getString("userKitiName", "");
        }
        return kitiName;
    }

    public List<SimpleUser> getChatBlackList() {
        if (chatBlackList == null) {
            chatBlackList = ChatBlackUserUtil.getStoredChatBlackList(accountListSharedPreferences);
        }
        return chatBlackList;
    }

    public void chatBlackListAddUser(SimpleUser simpleUser) {
        if (chatBlackList == null) {
            chatBlackList = ChatBlackUserUtil.getStoredChatBlackList(accountListSharedPreferences);
        }
        chatBlackList.add(simpleUser);
        ChatBlackUserUtil.saveChatBlackList(accountListSharedPreferences, chatBlackList);
    }

    public void chatBlackListRemoveUser(String userName) {
        if (chatBlackList == null) {
            chatBlackList = ChatBlackUserUtil.getStoredChatBlackList(accountListSharedPreferences);
        }
        List<SimpleUser> simpleUserList = new ArrayList<>();
        for (SimpleUser simpleUser : chatBlackList) {
            if (!Objects.equals(simpleUser.getName(), userName)) {
                simpleUserList.add(simpleUser);
            }
        }
        chatBlackList = simpleUserList;
        ChatBlackUserUtil.saveChatBlackList(accountListSharedPreferences, chatBlackList);
    }

    public void setKitiName(String str) {
        kitiName = str;
    }

    public List<Rect> getFireRectArray(PlayView playView) {
        List<Rect> arrayList = new ArrayList<>();
        arrayList.add(new Rect(0, (int) (halfHeightSub20 - ((float) playView.fireImage.getHeight())), (int) widthDiv8, (int) halfHeightSub20));
        arrayList.add(new Rect((int) (widthDiv8 - blackKeyWidth), (int) (halfHeightSub20 - ((float) playView.fireImage.getHeight())), (int) (widthDiv8 + blackKeyWidth), (int) halfHeightSub20));
        arrayList.add(new Rect((int) widthDiv8, (int) (halfHeightSub20 - ((float) playView.fireImage.getHeight())), (int) (widthDiv8 * 2), (int) halfHeightSub20));
        arrayList.add(new Rect((int) (widthDiv8 * 2 - blackKeyWidth), (int) (halfHeightSub20 - ((float) playView.fireImage.getHeight())), (int) (widthDiv8 * 2 + blackKeyWidth), (int) halfHeightSub20));
        arrayList.add(new Rect((int) (widthDiv8 * 2), ((int) halfHeightSub20) - playView.fireImage.getHeight(), (int) (widthDiv8 * 3), (int) halfHeightSub20));
        arrayList.add(new Rect((int) (widthDiv8 * 3), ((int) halfHeightSub20) - playView.fireImage.getHeight(), (int) (widthDiv8 * 4), (int) halfHeightSub20));
        arrayList.add(new Rect((int) (widthDiv8 * 4 - blackKeyWidth), (int) (halfHeightSub20 - ((float) playView.fireImage.getHeight())), (int) (widthDiv8 * 4 + blackKeyWidth), (int) halfHeightSub20));
        arrayList.add(new Rect((int) (widthDiv8 * 4), ((int) halfHeightSub20) - playView.fireImage.getHeight(), (int) (widthDiv8 * 5), (int) halfHeightSub20));
        arrayList.add(new Rect((int) (widthDiv8 * 5 - blackKeyWidth), (int) (halfHeightSub20 - ((float) playView.fireImage.getHeight())), (int) (widthDiv8 * 5 + blackKeyWidth), (int) halfHeightSub20));
        arrayList.add(new Rect((int) (widthDiv8 * 5), ((int) halfHeightSub20) - playView.fireImage.getHeight(), (int) (widthDiv8 * 6), (int) halfHeightSub20));
        arrayList.add(new Rect((int) (widthDiv8 * 6 - blackKeyWidth), (int) (halfHeightSub20 - ((float) playView.fireImage.getHeight())), (int) (widthDiv8 * 6 + blackKeyWidth), (int) halfHeightSub20));
        arrayList.add(new Rect((int) (widthDiv8 * 6), ((int) halfHeightSub20) - playView.fireImage.getHeight(), (int) (widthDiv8 * 7), (int) halfHeightSub20));
        arrayList.add(new Rect((int) (widthDiv8 * 7), ((int) halfHeightSub20) - playView.fireImage.getHeight(), (int) (widthDiv8 * 8), (int) halfHeightSub20));
        return arrayList;
    }

    public void drawFire(PlayView playView, Canvas canvas, int i) {
        switch (i) {
            case 0:
                canvas.drawBitmap(playView.fireImage, null, new RectF(0, halfHeightSub20 - playView.fireImage.getHeight(), widthDiv8, halfHeightSub20), null);
                return;
            case 1:
                canvas.drawBitmap(playView.fireImage, null, new RectF((widthDiv8 - blackKeyWidth), halfHeightSub20 - playView.fireImage.getHeight(), widthDiv8 + blackKeyWidth, halfHeightSub20), null);
                return;
            case 2:
                canvas.drawBitmap(playView.fireImage, null, new RectF(widthDiv8, halfHeightSub20 - playView.fireImage.getHeight(), widthDiv8 * 2, halfHeightSub20), null);
                return;
            case 3:
                canvas.drawBitmap(playView.fireImage, null, new RectF((widthDiv8 * 2 - blackKeyWidth), halfHeightSub20 - playView.fireImage.getHeight(), (widthDiv8 * 2 + blackKeyWidth), halfHeightSub20), null);
                return;
            case 4:
                canvas.drawBitmap(playView.fireImage, null, new RectF(widthDiv8 * 2, halfHeightSub20 - playView.fireImage.getHeight(), widthDiv8 * 3, halfHeightSub20), null);
                return;
            case 5:
                canvas.drawBitmap(playView.fireImage, null, new RectF(widthDiv8 * 3, halfHeightSub20 - playView.fireImage.getHeight(), widthDiv8 * 4, halfHeightSub20), null);
                return;
            case 6:
                canvas.drawBitmap(playView.fireImage, null, new RectF((widthDiv8 * 4 - blackKeyWidth), halfHeightSub20 - playView.fireImage.getHeight(), (widthDiv8 * 4 + blackKeyWidth), halfHeightSub20), null);
                return;
            case 7:
                canvas.drawBitmap(playView.fireImage, null, new RectF(widthDiv8 * 4, halfHeightSub20 - playView.fireImage.getHeight(), widthDiv8 * 5, halfHeightSub20), null);
                return;
            case 8:
                canvas.drawBitmap(playView.fireImage, null, new RectF((widthDiv8 * 5 - blackKeyWidth), halfHeightSub20 - playView.fireImage.getHeight(), (widthDiv8 * 5 + blackKeyWidth), halfHeightSub20), null);
                return;
            case 9:
                canvas.drawBitmap(playView.fireImage, null, new RectF(widthDiv8 * 5, halfHeightSub20 - playView.fireImage.getHeight(), widthDiv8 * 6, halfHeightSub20), null);
                return;
            case 10:
                canvas.drawBitmap(playView.fireImage, null, new RectF((widthDiv8 * 6 - blackKeyWidth), halfHeightSub20 - playView.fireImage.getHeight(), widthDiv8 * 6 + blackKeyWidth, halfHeightSub20), null);
                return;
            case 11:
                canvas.drawBitmap(playView.fireImage, null, new RectF(widthDiv8 * 6, halfHeightSub20 - playView.fireImage.getHeight(), widthDiv8 * 7, halfHeightSub20), null);
                return;
            case 12:
                canvas.drawBitmap(playView.fireImage, null, new RectF(widthDiv8 * 7, halfHeightSub20 - playView.fireImage.getHeight(), widthDiv8 * 8, halfHeightSub20), null);
                return;
            default:
        }
    }

    public void setBackGround(Context context, String str, ViewGroup viewGroup) {
        if (viewGroup == null) {
            return;
        }
        if (!PreferenceManager.getDefaultSharedPreferences(context).getString("skin_list", "original").equals("original")) {
            Bitmap bitmap = null;
            try {
                bitmap = BitmapFactory.decodeFile(context.getDir("Skin", Context.MODE_PRIVATE) + "/" + str + ".jpg");
            } catch (Exception ignored) {
            }
            if (bitmap == null) {
                try {
                    bitmap = BitmapFactory.decodeFile(context.getDir("Skin", Context.MODE_PRIVATE) + "/" + str + ".png");
                } catch (Exception ignored) {
                }
            }
            if (bitmap != null) {
                viewGroup.setBackground(new BitmapDrawable(getResources(), bitmap));
            }
        } else {
            viewGroup.setBackgroundResource(R.drawable.ground);
        }
    }

    public Bitmap loadImage(String str) {
        Bitmap bitmap = null;
        if (!PreferenceManager.getDefaultSharedPreferences(this).getString("skin_list", "original").equals("original")) {
            try {
                bitmap = BitmapFactory.decodeFile(getDir("Skin", Context.MODE_PRIVATE) + "/" + str + ".png");
            } catch (Exception e) {
                try {
                    return BitmapFactory.decodeStream(getResources().getAssets().open("drawable/" + str + ".png"));
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        if (bitmap != null) {
            return bitmap;
        }
        try {
            return BitmapFactory.decodeStream(getResources().getAssets().open("drawable/" + str + ".png"));
        } catch (IOException e2) {
            e2.printStackTrace();
            return null;
        }
    }

    public int getAnimPosition() {
        return animPosition;
    }

    public void setAnimPosition(int f) {
        animPosition = f;
    }

    public void downNote() {
        animPosition += animFrame;
    }

    public void setBlackKeyHeight(float f) {
        blackKeyHeight = f;
    }

    public void setServerTimeInterval(long serverTime) {
        this.serverTimeInterval = serverTime - System.currentTimeMillis();
    }

    public long getServerTime() {
        return serverTimeInterval + System.currentTimeMillis();
    }

    public void loadSettings(boolean online) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (online) {
            tempSpeed = 1;
            autoPlay = true;
        } else {
            autoPlay = sharedPreferences.getBoolean("auto_play", true);
            tempSpeed = Float.parseFloat(sharedPreferences.getString("temp_speed", "1.0"));
        }
        isOpenChord = sharedPreferences.getBoolean("sound_check_box", true);
        chordVolume = Float.parseFloat(sharedPreferences.getString("b_s_vol", "0.8"));
        animFrame = Integer.parseInt(sharedPreferences.getString("anim_frame", "4"));
        keyboardPrefer = sharedPreferences.getBoolean("keyboard_perfer", true);
        showTouchNotesLevel = sharedPreferences.getBoolean("tishi_cj", true);
        showLine = sharedPreferences.getBoolean("show_line", true);
        loadLongKeyboard = sharedPreferences.getBoolean("open_long_key", false);
        roughLine = Integer.parseInt(sharedPreferences.getString("rough_line", "1"));
        midiKeyboardTune = Integer.parseInt(sharedPreferences.getString("midi_keyboard_tune", "0"));
        keyboardSoundTune = Integer.parseInt(sharedPreferences.getString("keyboard_sound_tune", "0"));
        keyboardAnim = sharedPreferences.getBoolean("keyboard_anim", true);
        chatSound = sharedPreferences.getBoolean("chats_sound", false);
        notesDownSpeed = Integer.parseInt(sharedPreferences.getString("down_speed", "6"));
        noteSize = Float.parseFloat(sharedPreferences.getString("note_size", "1"));
        noteDismiss = sharedPreferences.getBoolean("note_dismiss", false);
        changeNotesColor = sharedPreferences.getBoolean("change_color", true);
    }

    public void setTempSpeed() {
        tempSpeed = 1;
    }

    public float getTempSpeed() {
        return tempSpeed;
    }

    public boolean getNoteDismiss() {
        return noteDismiss;
    }

    public int getDownSpeed() {
        return notesDownSpeed;
    }

    public void setDownSpeed(int speed) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.edit().putString("down_speed", String.valueOf(speed)).apply();
        notesDownSpeed = speed;
    }

    public boolean getIfLoadLongKeyboard() {
        return loadLongKeyboard;
    }

    public int getRoughLine() {
        return roughLine;
    }

    public boolean getIsShowLine() {
        return showLine;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        CrashHandler crashHandler = new CrashHandler();
        crashHandler.init();
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        accountListSharedPreferences = getSharedPreferences("account_list", MODE_PRIVATE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_MIDI)) {
                midiConnectionListeners = new ArrayList<>();
                mMidiManager = (MidiManager) getSystemService(MIDI_SERVICE);
                mMidiManager.registerDeviceCallback(new MidiManager.DeviceCallback() {
                    @Override
                    public void onDeviceAdded(MidiDeviceInfo info) {
                        mMidiManager.openDevice(info, device -> {
                            if (device != null) {
                                MidiDeviceInfo.PortInfo[] ports = device.getInfo().getPorts();
                                for (MidiDeviceInfo.PortInfo port : ports) {
                                    if (port.getType() == MidiDeviceInfo.PortInfo.TYPE_OUTPUT) {
                                        midiOutputPort = device.openOutputPort(port.getPortNumber());
                                        for (MidiConnectionListener midiConnectionListener : midiConnectionListeners) {
                                            midiConnectionListener.onMidiConnect();
                                        }
                                        break;
                                    }
                                }
                                Toast.makeText(getApplicationContext(), "MIDI设备已连接", Toast.LENGTH_SHORT).show();
                            }
                        }, new Handler(Looper.getMainLooper()));
                    }

                    @Override
                    public void onDeviceRemoved(MidiDeviceInfo info) {
                        for (MidiConnectionListener midiConnectionListener : midiConnectionListeners) {
                            midiConnectionListener.onMidiDisconnect();
                        }
                        try {
                            if (midiOutputPort != null) {
                                midiOutputPort.close();
                                Toast.makeText(getApplicationContext(), "MIDI设备已断开", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "请重新连接MIDI设备", Toast.LENGTH_SHORT).show();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            midiOutputPort = null;
                        }
                    }
                }, new Handler(Looper.getMainLooper()));
                for (MidiDeviceInfo info : mMidiManager.getDevices()) {
                    mMidiManager.openDevice(info, device -> {
                        if (device != null) {
                            MidiDeviceInfo.PortInfo[] ports = device.getInfo().getPorts();
                            for (MidiDeviceInfo.PortInfo port : ports) {
                                if (port.getType() == MidiDeviceInfo.PortInfo.TYPE_OUTPUT) {
                                    midiOutputPort = device.openOutputPort(port.getPortNumber());
                                    for (MidiConnectionListener midiConnectionListener : midiConnectionListeners) {
                                        midiConnectionListener.onMidiConnect();
                                    }
                                    break;
                                }
                            }
                            Toast.makeText(getApplicationContext(), "MIDI设备已连接", Toast.LENGTH_SHORT).show();
                        }
                    }, new Handler(Looper.getMainLooper()));
                }
            }
        }
    }

    public void addMidiConnectionListener(MidiConnectionListener midiConnectionListener) {
        midiConnectionListeners.add(midiConnectionListener);
    }

    public void removeMidiConnectionStart(MidiConnectionListener midiConnectionListener) {
        midiConnectionListeners.remove(midiConnectionListener);
    }

    public int getAnimFrame() {
        return animFrame;
    }

    public boolean getIfShowNotesLevel() {
        return showTouchNotesLevel;
    }


    public int getMidiKeyboardTune() {
        return midiKeyboardTune;
    }

    public void setMidiKeyboardTune(int midiKeyboardTune) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.edit().putString("midi_keyboard_tune", String.valueOf(midiKeyboardTune)).apply();
        this.midiKeyboardTune = midiKeyboardTune;
    }

    public int getKeyboardSoundTune() {
        return keyboardSoundTune;
    }

    public void setKeyboardSoundTune(int keyboardSoundTune) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.edit().putString("keyboard_sound_tune", String.valueOf(keyboardSoundTune)).apply();
        this.keyboardSoundTune = keyboardSoundTune;
    }

    public boolean isKeyboardAnim() {
        return keyboardAnim;
    }

    public void setKeyboardAnim(boolean keyboardAnim) {
        this.keyboardAnim = keyboardAnim;
    }

    public int getPlaySongsMode() {
        return playSongsMode;
    }

    public void setPlaySongsMode(int n) {
        playSongsMode = n;
    }

    public boolean getAutoPlay() {
        return autoPlay;
    }

    public float getChordVolume() {
        return chordVolume;
    }

    public boolean getOpenChord() {
        return isOpenChord;
    }

    public float getWidthDiv8() {
        return widthDiv8;
    }

    public void setWidthDiv8(float f) {
        widthDiv8 = f;
    }

    public float getHalfHeightSub20() {
        return halfHeightSub20;
    }

    public void setHalfHeightSub20(float f) {
        halfHeightSub20 = f;
    }

    public int getWhiteKeyHeight() {
        return whiteKeyHeight;
    }

    public void setWhiteKeyHeight(int f) {
        whiteKeyHeight = f;
    }

    public float getBlackKeyHeight() {
        return blackKeyHeight;
    }

    public float getBlackKeyWidth() {
        return blackKeyWidth;
    }

    public void setBlackKeyWidth(float f) {
        blackKeyWidth = f;
    }

    public float getHalfHeightSub10() {
        return halfHeightSub10;
    }

    public void setHalfHeightSub10(float f) {
        halfHeightSub10 = f;
    }

    public float getWhiteKeyHeightAdd90() {
        return whiteKeyHeightAdd90;
    }

    public void setWhiteKeyHeightAdd90(float f) {
        whiteKeyHeightAdd90 = f;
    }

    public boolean isChatSound() {
        return chatSound;
    }

    public void setChatSound(boolean chatSound) {
        this.chatSound = chatSound;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public boolean getIsShowDialog() {
        return isShowDialog;
    }

    public void setIsShowDialog(boolean isShowDialog) {
        this.isShowDialog = isShowDialog;
    }

    public int playSound(int note, int volume) {
        if (note >= 24 && note <= 108 && volume > 3) {
            trigger(108 - note, volume);
            return note;
        }
        return 0;
    }

    public void stopSongs(int i) {
        // nothing
    }

    public void playChatSound() {
        trigger(85, 127);
    }

    private class CrashHandler implements Thread.UncaughtExceptionHandler {

        void init() {
            Thread.setDefaultUncaughtExceptionHandler(this);
        }

        @Override
        public void uncaughtException(Thread t, Throwable e) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(byteArrayOutputStream));
            final String errorLog = byteArrayOutputStream.toString();
            ClipboardManager myClipboard = (ClipboardManager) getApplicationContext().getSystemService(CLIPBOARD_SERVICE);
            ClipData myClip = ClipData.newPlainText("errorLog", errorLog);
            myClipboard.setPrimaryClip(myClip);
            if (connectionService != null) {
                connectionService.outLine();
            }
            if (isBindService) {
                unbindService(serviceConnection);
                setIsBindService(false);
            }
            new Thread() {
                @Override
                public void run() {
                    Looper.prepare();
                    Toast.makeText(getApplicationContext(), "很抱歉，极品钢琴出现异常，错误信息已复制，可粘贴至主界面问题反馈并发送", Toast.LENGTH_LONG).show();
                    Looper.loop();
                }
            }.start();
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        if (connectionService != null) {
            connectionService.outLine();
        }
        if (isBindService) {
            unbindService(serviceConnection);
            setIsBindService(false);
        }
    }

    @Override
    protected void attachBaseContext(Context context){
        super.attachBaseContext(context);
        MultiDex.install(this);
    }
}
