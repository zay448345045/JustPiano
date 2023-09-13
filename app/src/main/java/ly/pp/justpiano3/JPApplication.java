package ly.pp.justpiano3;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Environment;
import android.os.IBinder;
import android.os.Looper;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.multidex.MultiDex;
import androidx.room.Room;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;
import io.netty.util.internal.StringUtil;
import ly.pp.justpiano3.activity.JustPiano;
import ly.pp.justpiano3.constant.Consts;
import ly.pp.justpiano3.database.SongDatabase;
import ly.pp.justpiano3.entity.GlobalSetting;
import ly.pp.justpiano3.entity.SimpleUser;
import ly.pp.justpiano3.entity.User;
import ly.pp.justpiano3.service.ConnectionService;
import ly.pp.justpiano3.task.FeedbackTask;
import ly.pp.justpiano3.thread.ThreadPoolUtils;
import ly.pp.justpiano3.utils.ChatBlackUserUtil;
import ly.pp.justpiano3.utils.ImageLoadUtil;
import ly.pp.justpiano3.utils.MidiUtil;
import ly.pp.justpiano3.view.PlayView;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.util.*;

public final class JPApplication extends Application {

    public static final int SETTING_MODE_CODE = 122;

    /**
     * app最小支持版本code 38对应4.32版本，4.32版本对于所有曲谱文件及数据库结构进行了重新生成
     * 此版本code前的app不再兼容数据库结构改变，必须卸载重装app才可正常运行曲谱数据库操作
     */
    private static final int MIN_APP_VERSION_CODE_SUPPORT = 38;

    private static SongDatabase songDatabase;

    public static String kitiName = "";
    public static SharedPreferences accountListSharedPreferences;
    public String title = "";
    public String f4072f = "";
    public String f4073g = "";
    public String f4074h = "";
    private ConnectionService connectionService;
    private int whiteKeyHeight;
    private float blackKeyHeight;
    private float blackKeyWidth;
    private boolean bindService;

    /**
     * 游戏模式
     */
    private int gameMode;

    private final Map<Byte, User> roomPlayerMap = new HashMap<>();
    private String accountName = "";
    private String password = "";
    private List<SimpleUser> chatBlackList;
    private String server = Consts.ONLINE_SERVER_URL;
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

    public Map<Byte, User> getRoomPlayerMap() {
        return roomPlayerMap;
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

    public String getKitiName() {
        if (StringUtil.isNullOrEmpty(kitiName)) {
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

    @Override
    public void onCreate() {
        super.onCreate();
        // 严苛模式开启
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().build());
        // 设置拦截app中未捕获的异常
        CrashHandler crashHandler = new CrashHandler();
        crashHandler.init();
        // 初始化一些图像缓存
        ImageLoadUtil.init(this);
        // 从app应用数据中加载设置
        GlobalSetting.INSTANCE.loadSettings(this, false);
        // 从app应用数据中加载账号信息
        accountListSharedPreferences = getSharedPreferences("account_list", MODE_PRIVATE);
        // 初始化数据库
        songDatabase = Room.databaseBuilder(this, SongDatabase.class, "data")
                .addMigrations(generateMigrations()).allowMainThreadQueries().build();
        // 初始化midi设备
        MidiUtil.initMidiDevice(this);
    }

    /**
     * 生成数据库迁移逻辑对象
     */
    private Migration[] generateMigrations() {
        Migration[] migrations = new Migration[BuildConfig.VERSION_CODE - MIN_APP_VERSION_CODE_SUPPORT];
        for (int i = MIN_APP_VERSION_CODE_SUPPORT; i < BuildConfig.VERSION_CODE; i++) {
            migrations[i - MIN_APP_VERSION_CODE_SUPPORT] = new Migration(i, BuildConfig.VERSION_CODE) {
                @Override
                public void migrate(@NonNull SupportSQLiteDatabase database) {
                    // 兼容4.32起到目前版本的所有旧版本，执行自定义迁移逻辑
                    sqliteVersionCompatible(this.startVersion, database);
                }
            };
        }
        return migrations;
    }

    /**
     * 兼容旧版本
     */
    private void sqliteVersionCompatible(int oldVersion, @NonNull SupportSQLiteDatabase database) {
        // 4.32版本极品钢琴对应的数据库版本为38，小于4.32版本时，停止对数据库兼容的支持，必须卸载重装
        if (oldVersion < MIN_APP_VERSION_CODE_SUPPORT) {
            Toast.makeText(this, "不支持过低的版本覆盖安装，请卸载重装应用", Toast.LENGTH_SHORT).show();
            return;
        }
        // 对于4.32版本以上，当前版本以下的情况，启动扫描本地曲谱文件来更新曲谱数据库
        if (oldVersion < BuildConfig.VERSION_CODE) {
            JustPiano.updateSQL = true;
        }
        // 兼容4.33版本设置
        if (oldVersion < 40) {
            // 强行初始化一些默认设置
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("down_speed", "6");
            editor.putBoolean("note_dismiss", false);
            editor.putString("note_size", "1");
            editor.putString("b_s_vol", "0.8");
            editor.putString("temp_speed", "1.0");
            editor.putString("sound_list", "original");
            editor.apply();

            // 4.33版本的音源为wav格式，不兼容后续的mp3格式，需要删掉4.33版本存储的音源
            File file = new File(Environment.getExternalStorageDirectory() + "/JustPiano/Sounds");
            if (file.isDirectory()) {
                File[] files = file.listFiles(pathname -> {
                    String filename = pathname.getName().toLowerCase();
                    return filename.endsWith(".ss");
                });
                if (files != null) {
                    for (File fileTemp : files) {
                        if (fileTemp.exists()) {
                            fileTemp.delete();
                        }
                    }
                }
            }
        }
        // 4.6版本 数据库版本号为44
        // 4.7版本后，数据库的版本号为app的versionCode
        if (oldVersion < 51) {
            // 兼容room框架，建立新表，修改数据库字段类型
            database.execSQL("CREATE TABLE IF NOT EXISTS `song` (`_id` INTEGER PRIMARY KEY AUTOINCREMENT, `name` TEXT NOT NULL DEFAULT '', " +
                    "`item` TEXT NOT NULL DEFAULT '经典乐章', `path` TEXT NOT NULL DEFAULT '', `isnew` INTEGER NOT NULL DEFAULT 1, " +
                    "`ishot` INTEGER NOT NULL DEFAULT 0, `isfavo` INTEGER NOT NULL DEFAULT 0, `player` TEXT NOT NULL DEFAULT '', " +
                    "`score` INTEGER NOT NULL DEFAULT 0, `date` INTEGER NOT NULL DEFAULT 0, `count` INTEGER NOT NULL DEFAULT 0, " +
                    "`diff` REAL NOT NULL DEFAULT 0, `online` INTEGER NOT NULL DEFAULT 1, `Ldiff` REAL NOT NULL DEFAULT 0, " +
                    "`length` INTEGER NOT NULL DEFAULT 0, `Lscore` INTEGER NOT NULL DEFAULT 0);");
            database.execSQL("INSERT INTO `song` SELECT * FROM jp_data;");
        }
    }

    private class CrashHandler implements Thread.UncaughtExceptionHandler {

        void init() {
            Thread.setDefaultUncaughtExceptionHandler(this);
        }

        @Override
        public void uncaughtException(@NotNull Thread thread, Throwable throwable) {
            ThreadPoolUtils.execute(() -> {
                Looper.prepare();
                Toast.makeText(getApplicationContext(), "很抱歉，极品钢琴出现异常，可至主界面提交问题反馈", Toast.LENGTH_LONG).show();
                Looper.loop();
            });
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            throwable.printStackTrace(new PrintStream(byteArrayOutputStream));
            new FeedbackTask(getApplicationContext(),
                    StringUtil.isNullOrEmpty(kitiName) ? "未知用户" : kitiName,
                    BuildConfig.VERSION_NAME + '\n' + byteArrayOutputStream).execute();

            if (connectionService != null) {
                connectionService.outLine();
            }
            if (bindService) {
                unbindService(serviceConnection);
                bindService = false;
            }
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        if (connectionService != null) {
            connectionService.outLine();
        }
        // 在应用程序终止时关闭数据库连接
        if (songDatabase != null) {
            songDatabase.close();
        }
        if (bindService) {
            unbindService(serviceConnection);
            bindService = false;
        }
    }

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        MultiDex.install(this);
    }

    /**
     * 重写 getResource 方法，防止系统字体影响
     */
    @Override
    public Resources getResources() {
        // 禁止app字体大小跟随系统字体大小调节
        Resources resources = super.getResources();
        if (resources != null && resources.getConfiguration().fontScale != 1.0f) {
            android.content.res.Configuration configuration = resources.getConfiguration();
            configuration.fontScale = 1.0f;
            resources.updateConfiguration(configuration, resources.getDisplayMetrics());
        }
        return resources;
    }

    public static SongDatabase getSongDatabase() {
        return songDatabase;
    }

    public ConnectionService getConnectionService() {
        return connectionService;
    }

    public void setConnectionService(ConnectionService connectionService) {
        this.connectionService = connectionService;
    }

    public int getWhiteKeyHeight() {
        return whiteKeyHeight;
    }

    public void setWhiteKeyHeight(int whiteKeyHeight) {
        this.whiteKeyHeight = whiteKeyHeight;
    }

    public float getBlackKeyHeight() {
        return blackKeyHeight;
    }

    public void setBlackKeyHeight(float blackKeyHeight) {
        this.blackKeyHeight = blackKeyHeight;
    }

    public float getBlackKeyWidth() {
        return blackKeyWidth;
    }

    public void setBlackKeyWidth(float blackKeyWidth) {
        this.blackKeyWidth = blackKeyWidth;
    }

    public boolean isBindService() {
        return bindService;
    }

    public void setBindService(boolean bindService) {
        this.bindService = bindService;
    }

    public int getGameMode() {
        return gameMode;
    }

    public void setGameMode(int gameMode) {
        this.gameMode = gameMode;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public int getWidthPixels() {
        return widthPixels;
    }

    public void setWidthPixels(int widthPixels) {
        this.widthPixels = widthPixels;
    }

    public int getHeightPixels() {
        return heightPixels;
    }

    public void setHeightPixels(int heightPixels) {
        this.heightPixels = heightPixels;
    }

    public int getAnimPosition() {
        return animPosition;
    }

    public void setAnimPosition(int animPosition) {
        this.animPosition = animPosition;
    }

    public float getWidthDiv8() {
        return widthDiv8;
    }

    public void setWidthDiv8(float widthDiv8) {
        this.widthDiv8 = widthDiv8;
    }

    public float getHalfHeightSub20() {
        return halfHeightSub20;
    }

    public void setHalfHeightSub20(float halfHeightSub20) {
        this.halfHeightSub20 = halfHeightSub20;
    }

    public float getHalfHeightSub10() {
        return halfHeightSub10;
    }

    public void setHalfHeightSub10(float halfHeightSub10) {
        this.halfHeightSub10 = halfHeightSub10;
    }

    public float getWhiteKeyHeightAdd90() {
        return whiteKeyHeightAdd90;
    }

    public void setWhiteKeyHeightAdd90(float whiteKeyHeightAdd90) {
        this.whiteKeyHeightAdd90 = whiteKeyHeightAdd90;
    }

    public ServiceConnection getServiceConnection() {
        return serviceConnection;
    }
}
