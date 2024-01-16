package ly.pp.justpiano3;

import android.app.Application;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.os.Process;
import android.os.StrictMode;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;
import androidx.room.Room;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.babyte.breakpad.BaByteBreakpad;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import kotlin.Unit;
import ly.pp.justpiano3.activity.JustPiano;
import ly.pp.justpiano3.activity.online.OLBaseActivity;
import ly.pp.justpiano3.database.SongDatabase;
import ly.pp.justpiano3.entity.GlobalSetting;
import ly.pp.justpiano3.task.FeedbackTask;
import ly.pp.justpiano3.utils.DeviceUtil;
import ly.pp.justpiano3.utils.ImageLoadUtil;
import ly.pp.justpiano3.utils.MidiDeviceUtil;
import ly.pp.justpiano3.utils.OnlineUtil;
import ly.pp.justpiano3.utils.SoundEngineUtil;

public final class JPApplication extends Application {

    /**
     * app最小支持版本code 38对应4.32版本，4.32版本对于所有曲谱文件及数据库结构进行了重新生成
     * 此版本code前的app不再兼容数据库结构改变，必须卸载重装app才可正常运行曲谱数据库操作
     */
    private static final int MIN_APP_VERSION_CODE_SUPPORT = 38;

    private static SongDatabase songDatabase;

    private boolean appInException;

    @Override
    public void onCreate() {
        super.onCreate();
        // debug下，严苛模式开启
        if (BuildConfig.DEBUG) {
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().build());
        }
        // 设置拦截app中未捕获的异常
        new CrashHandler().init();
        // 从app应用数据中加载设置
        SoundEngineUtil.init(this);
        GlobalSetting.loadSettings(this, false);
        // 初始化一些图像缓存
        ImageLoadUtil.init(this);
        // 从app应用数据中加载账号信息
        OLBaseActivity.accountListSharedPreferences = getSharedPreferences("account_list", MODE_PRIVATE);
        // 初始化数据库
        songDatabase = Room.databaseBuilder(this, SongDatabase.class, "data")
                .addMigrations(generateMigrations()).allowMainThreadQueries().build();
        // 支持midi设备功能时，初始化midi设备
        if (MidiDeviceUtil.isSupportMidiDevice(this)) {
            MidiDeviceUtil.initMidiDevice(this);
        }
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
            editor.putString("sound_select", "original");
            editor.apply();
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
            // 监听native异常
            BaByteBreakpad.INSTANCE.initBreakpad(crashInfo -> {
                uncaughtException(Thread.currentThread(), new Throwable(crashInfo.toString()));
                return Unit.INSTANCE;
            });
        }

        @Override
        public void uncaughtException(@NotNull Thread thread, @NonNull Throwable throwable) {
            if (appInException) {
                return;
            }
            appInException = true;
            new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(getApplicationContext(),
                    "很抱歉，极品钢琴出现异常，可至主界面提交问题反馈", Toast.LENGTH_LONG).show());
            // 上传崩溃日志
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            throwable.printStackTrace();
            throwable.printStackTrace(new PrintStream(byteArrayOutputStream));
            new FeedbackTask(getApplicationContext(),
                    TextUtils.isEmpty(OLBaseActivity.kitiName) ? "未知用户" : OLBaseActivity.kitiName,
                    DeviceUtil.getAppAndDeviceInfo() + '\n' + byteArrayOutputStream).execute();
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            onTerminate();
            System.exit(1);
            Process.killProcess(Process.myPid());
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        // 在应用程序终止时关闭数据库连接
        if (songDatabase != null) {
            songDatabase.close();
        }
        // 关闭TCP网络连接服务
        OnlineUtil.outlineConnectionService(this);
        // 关闭fluidsynth
        SoundEngineUtil.closeFluidSynth();
    }

    public static SongDatabase getSongDatabase() {
        return songDatabase;
    }
}
