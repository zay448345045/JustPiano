package ly.pp.justpiano3;

import android.app.Application;
import android.content.*;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;
import android.os.IBinder;
import android.os.Looper;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.multidex.MultiDex;
import lombok.Getter;
import lombok.Setter;
import ly.pp.justpiano3.activity.MelodySelect;
import ly.pp.justpiano3.activity.OLPlayRoom;
import ly.pp.justpiano3.constant.Consts;
import ly.pp.justpiano3.entity.Setting;
import ly.pp.justpiano3.entity.SimpleUser;
import ly.pp.justpiano3.entity.User;
import ly.pp.justpiano3.service.ConnectionService;
import ly.pp.justpiano3.thread.PlaySongs;
import ly.pp.justpiano3.utils.ChatBlackUserUtil;
import ly.pp.justpiano3.utils.MidiUtil;
import ly.pp.justpiano3.view.PlayView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.*;

public final class JPApplication extends Application {

    public static String kitiName = "";
    public static SharedPreferences accountListSharedPreferences;
    public static int SETTING_MODE_CODE = 122;
    public String title = "";
    public String f4072f = "";
    public String f4073g = "";
    public String f4074h = "";
    private Setting setting;
    private ConnectionService connectionService;
    private int whiteKeyHeight;
    private float blackKeyHeight;
    private float blackKeyWidth;
    private boolean isBindService;

    /**
     * 游戏模式
     */
    @Setter
    @Getter
    private int gameMode;

    /**
     * 是否已经显示对话框，防止对话框重复显示
     */
    @Setter
    @Getter
    private boolean isShowDialog;
    private final Map<Byte, User> hashMap = new HashMap<>();
    private String accountName = "";
    private String password = "";
    private List<SimpleUser> chatBlackList;
    private String nowSongsName = "";
    private String server = Consts.ONLINE_SERVER_URL;

    /**
     * 目前正在播放的歌曲
     */
    @Getter
    private PlaySongs playSongs;
    private int widthPixels;
    private int heightPixels;
    private int animPosition;
    private float widthDiv8;
    private float halfHeightSub20;
    private float halfHeightSub10;
    private float whiteKeyHeightAdd90;
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

    public void startPlaySongLocal(String songPath, MelodySelect melodySelect, int position) {
        stopPlaySong();
        this.playSongs = new PlaySongs(this, songPath, melodySelect, null, position, 0);
    }

    public void startPlaySongOnline(String songPath, OLPlayRoom olPlayRoom, int tune) {
        stopPlaySong();
        this.playSongs = new PlaySongs(this, songPath, null, olPlayRoom, 0, tune);
    }

    public boolean stopPlaySong() {
        if (this.playSongs != null) {
            this.playSongs.setPlayingSongs(false);
            this.playSongs = null;
            return true;
        }
        return false;
    }

    public boolean isPlayingSong() {
        return this.playSongs != null && this.playSongs.isPlayingSongs();
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
        animPosition += setting.getAnimFrame();
    }

    public void setBlackKeyHeight(float f) {
        blackKeyHeight = f;
    }

    public void loadSettings(boolean online) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        setting = Setting.loadSettings(sharedPreferences, online);
    }

    public Setting getSetting() {
        return setting;
    }

    public void setDownSpeed(int speed) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.edit().putString("down_speed", String.valueOf(speed)).apply();
        setting.setNotesDownSpeed(speed);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        loadSettings(false);
        CrashHandler crashHandler = new CrashHandler();
        crashHandler.init();
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().build());
        accountListSharedPreferences = getSharedPreferences("account_list", MODE_PRIVATE);
        MidiUtil.initMidiDevice(this);
    }

    public void setMidiKeyboardTune(int midiKeyboardTune) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.edit().putString("midi_keyboard_tune", String.valueOf(midiKeyboardTune)).apply();
        setting.setMidiKeyboardTune(midiKeyboardTune);
    }

    public void setKeyboardSoundTune(int keyboardSoundTune) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.edit().putString("keyboard_sound_tune", String.valueOf(keyboardSoundTune)).apply();
        setting.setKeyboardSoundTune(keyboardSoundTune);
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

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public void stopSongs(int i) {
        // nothing
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

    /**
     * 重写 getResource 方法，防止系统字体影响
     */
    @Override
    public Resources getResources() {//禁止app字体大小跟随系统字体大小调节
        Resources resources = super.getResources();
        if (resources != null && resources.getConfiguration().fontScale != 1.0f) {
            android.content.res.Configuration configuration = resources.getConfiguration();
            configuration.fontScale = 1.0f;
            resources.updateConfiguration(configuration, resources.getDisplayMetrics());
        }
        return resources;
    }
}
