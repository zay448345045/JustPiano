package ly.pp.justpiano3.entity

import android.content.Context
import androidx.preference.PreferenceManager
import ly.pp.justpiano3.enums.LocalPlayModeEnum
import ly.pp.justpiano3.utils.SoundEngineUtil

/**
 * 设置项
 */
object GlobalSetting {

    /**
     * 本地模式
     */
    @JvmStatic
    var localPlayMode = LocalPlayModeEnum.NORMAL

    /**
     * 全面屏适配开关
     */
    @JvmStatic
    var allFullScreenShow: Boolean = true
        private set

    /**
     * 背景图
     */
    @JvmStatic
    var backgroundPic: String = ""
        private set

    /**
     * 当前皮肤名称
     */
    @JvmStatic
    var skin: String = "original"
        private set

    /**
     * 当前音源名称
     */
    @JvmStatic
    var sound: String = "original"
        private set

    /**
     * 音块变色
     */
    @JvmStatic
    var changeNotesColor: Boolean = true
        private set

    /**
     * 节拍比率
     */
    @JvmStatic
    var tempSpeed: Float = 1f
        private set

    /**
     * 音块消失
     */
    @JvmStatic
    var noteDismiss: Boolean = false
        private set

    /**
     * 音块大小
     */
    @JvmStatic
    var noteSize: Float = 1f
        private set

    /**
     * 音块速率
     */
    @JvmStatic
    var notesDownSpeed: Float = 1f
        private set

    /**
     * MIDI键盘移调
     */
    @JvmStatic
    var midiKeyboardTune: Int = 0
        private set

    /**
     * 触摸键盘移调
     */
    @JvmStatic
    var keyboardSoundTune: Int = 0
        private set

    /**
     * 是否开启和弦
     */
    @JvmStatic
    var isOpenChord: Boolean = true
        private set

    /**
     * 和弦音量
     */
    @JvmStatic
    var chordVolume: Float = 0.8f
        private set

    /**
     * 按键震动
     */
    @JvmStatic
    var soundVibration: Boolean = false
        private set

    /**
     * 按键震动时长
     */
    @JvmStatic
    var soundVibrationTime: Int = 10
        private set

    /**
     * 弹奏时展示等级
     */
    @JvmStatic
    var showTouchNotesLevel: Boolean = true
        private set

    /**
     * 自动弹奏
     */
    @JvmStatic
    var autoPlay: Boolean = true
        private set

    /**
     * 展示判断线
     */
    @JvmStatic
    var showLine: Boolean = true
        private set

    /**
     * 按键效果
     */
    @JvmStatic
    var keyboardPrefer: Boolean = true
        private set

    /**
     * 判断线加粗类型
     */
    @JvmStatic
    var roughLine: Int = 1
        private set

    /**
     * 是否开启聊天音效
     */
    @JvmStatic
    var chatsSound: Boolean = false
        private set

    /**
     * 聊天音效
     */
    @JvmStatic
    var chatsSoundFile: String = ""
        private set

    /**
     * 聊天字体大小
     */
    @JvmStatic
    var chatTextSize: Int = 15
        private set

    /**
     * 瀑布流曲谱播放速度
     */
    @JvmStatic
    var waterfallSongSpeed: Float = 1f
        private set

    /**
     * 瀑布流音块下落速率
     */
    @JvmStatic
    var waterfallDownSpeed: Float = 0.8f
        private set

    /**
     * 瀑布流曲谱播放移调
     */
    @JvmStatic
    var waterfallTune: Int = 0
        private set

    /**
     * 瀑布流左手音块颜色
     */
    @JvmStatic
    var waterfallLeftHandColor: Int = 0xFF2BBBFB.toInt()
        private set

    /**
     * 瀑布流右手音块颜色
     */
    @JvmStatic
    var waterfallRightHandColor: Int = 0xFFFF802D.toInt()
        private set

    /**
     * 瀑布流自由演奏音块颜色
     */
    @JvmStatic
    var waterfallFreeStyleColor: Int = 0xFFFFFF00.toInt()
        private set

    /**
     * 是否存储聊天记录
     */
    @JvmStatic
    var saveChatRecord: Boolean = false
        private set

    /**
     * 聊天记录存储位置
     */
    @JvmStatic
    var chatsSavePath: String = ""
        private set

    /**
     * 录音文件存储位置
     */
    @JvmStatic
    var recordsSavePath: String = ""
        private set

    /**
     * 聊天是否展示时间
     */
    @JvmStatic
    var showChatTime: Boolean = false
        private set

    /**
     * 聊天是否展示时间
     */
    @JvmStatic
    var showChatTimeModes: String = "MM:ss"
        private set

    /**
     * 曲谱播放时是否显示通知栏，目前未使用
     */
    @JvmStatic
    var showNotification: Boolean = false
        private set

    /**
     * 钢琴键盘显示按键标签种类
     */
    @JvmStatic
    var keyboardOctaveTagType: Int = 0
        private set

    /**
     * 联网键盘模式是否实时传输
     */
    @JvmStatic
    var keyboardRealtime: Boolean = true
        private set

    /**
     * 联网键盘模式瀑布流透明度
     */
    @JvmStatic
    var waterfallOnlineAlpha: Int = 100
        private set

    /**
     * 是否强制开启延音踏板
     */
    @JvmStatic
    var forceEnableSustainPedal: Boolean = false

    /**
     * 是否显示瀑布流八度虚线
     */
    @JvmStatic
    var waterfallOctaveLine: Boolean = true
        private set

    /**
     * 瀑布流背景图
     */
    @JvmStatic
    var waterfallBackgroundPic: String = ""
        private set

    /**
     * 唤醒锁
     */
    @JvmStatic
    var wakeLock: Boolean = false
        private set

    /**
     * 从sharedPreferences获取设置
     */
    @JvmStatic
    fun loadSettings(context: Context, online: Boolean) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        allFullScreenShow = sharedPreferences.getBoolean("all_full_screen_show", true)
        backgroundPic = sharedPreferences.getString("background_pic", "")!!
        skin = sharedPreferences.getString("skin_select", "original").toString()
        sound = sharedPreferences.getString("sound_select", "original").toString()
        if (online) {
            tempSpeed = 1f
            autoPlay = true
        } else {
            tempSpeed = sharedPreferences.getString("temp_speed", "1.0")!!.toFloat()
            autoPlay = sharedPreferences.getBoolean("auto_play", true)
        }
        isOpenChord = sharedPreferences.getBoolean("sound_check_box", true)
        chordVolume = sharedPreferences.getString("b_s_vol", "0.8")!!.toFloat()
        // 延音和混响：数值直接更新到C++层即可
        SoundEngineUtil.setDelayValue(sharedPreferences.getString("sound_delay", "10")!!.toInt())
        SoundEngineUtil.setReverbValue(sharedPreferences.getString("sound_reverb", "10")!!.toInt())
        soundVibration = sharedPreferences.getBoolean("sound_vibration", false)
        soundVibrationTime = sharedPreferences.getString("sound_vibration_time", "10")!!.toInt()
        keyboardPrefer = sharedPreferences.getBoolean("keyboard_prefer", true)
        showTouchNotesLevel = sharedPreferences.getBoolean("tishi_cj", true)
        showLine = sharedPreferences.getBoolean("show_line", true)
        roughLine = sharedPreferences.getString("rough_line", "1")!!.toInt()
        midiKeyboardTune = sharedPreferences.getString("midi_keyboard_tune", "0")!!.toInt()
        keyboardSoundTune = sharedPreferences.getString("keyboard_sound_tune", "0")!!.toInt()
        chatsSound = sharedPreferences.getBoolean("chats_sound", false)
        chatsSoundFile = sharedPreferences.getString("chats_sound_file", "")!!
        notesDownSpeed = 3 / (sharedPreferences.getString("down_speed", "1")!!.toFloat())
        noteSize = sharedPreferences.getString("note_size", "1")!!.toFloat()
        noteDismiss = sharedPreferences.getBoolean("note_dismiss", false)
        changeNotesColor = sharedPreferences.getBoolean("change_color", true)
        chatTextSize = sharedPreferences.getString("chats_text_size", "15")!!.toInt()
        waterfallSongSpeed = sharedPreferences.getString("waterfall_song_speed", "1.0")!!.toFloat()
        waterfallDownSpeed = sharedPreferences.getString("waterfall_down_speed", "0.8")!!.toFloat()
        waterfallTune = sharedPreferences.getString("waterfall_tune", "0")!!.toInt()
        waterfallLeftHandColor =
            sharedPreferences.getInt("waterfall_left_hand_color", 0xFF2BBBFB.toInt())
        waterfallRightHandColor =
            sharedPreferences.getInt("waterfall_right_hand_color", 0xFFFF802D.toInt())
        waterfallFreeStyleColor =
            sharedPreferences.getInt("waterfall_free_style_color", 0xFFFFFF00.toInt())
        saveChatRecord = sharedPreferences.getBoolean("save_chats", false)
        chatsSavePath = sharedPreferences.getString("chats_save_path", "")!!
        recordsSavePath = sharedPreferences.getString("records_save_path", "")!!
        showChatTime = sharedPreferences.getBoolean("chats_time_show", false)
        showChatTimeModes = sharedPreferences.getString("chats_time_show_modes", "HH:mm")!!
        showNotification = sharedPreferences.getBoolean("show_notification", false)
        keyboardOctaveTagType = sharedPreferences.getString("octave_tag_type", "0")!!.toInt()
        keyboardRealtime = sharedPreferences.getBoolean("keyboard_realtime", true)
        waterfallOnlineAlpha =
            sharedPreferences.getString("online_waterfall_alpha", "100")!!.toInt()
        forceEnableSustainPedal = sharedPreferences.getBoolean("force_enable_sustain_pedal", false)
        waterfallOctaveLine = sharedPreferences.getBoolean("waterfall_octave_line", true)
        waterfallBackgroundPic = sharedPreferences.getString("waterfall_background_pic", "")!!
        wakeLock = sharedPreferences.getBoolean("wake_lock", false)
    }
}