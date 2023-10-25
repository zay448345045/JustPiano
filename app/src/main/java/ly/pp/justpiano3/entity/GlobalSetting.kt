package ly.pp.justpiano3.entity

import android.content.Context
import android.preference.PreferenceManager
import ly.pp.justpiano3.enums.LocalPlayModeEnum
import ly.pp.justpiano3.utils.SoundEngineUtil

/**
 * 设置项
 */
object GlobalSetting {

    /**
     * 本地模式
     */
    var localPlayMode = LocalPlayModeEnum.NORMAL

    /**
     * 音块变色
     */
    var changeNotesColor: Boolean = true

    /**
     * 节拍比率
     */
    var tempSpeed: Float = 1f

    /**
     * 音块消失
     */
    var noteDismiss: Boolean = false

    /**
     * 音块大小
     */
    var noteSize: Float = 1f

    /**
     * 音块速率
     */
    var notesDownSpeed: Float = 1f

    /**
     * MIDI键盘移调
     */
    var midiKeyboardTune: Int = 0

    /**
     * 触摸键盘移调
     */
    var keyboardSoundTune: Int = 0

    /**
     * 是否开启键盘模式动画
     */
    var keyboardAnim: Boolean = true

    /**
     * 是否开启和弦
     */
    var isOpenChord: Boolean = true

    /**
     * 和弦音量
     */
    var chordVolume: Float = 0.8f

    /**
     * 延音
     */
    var soundDelay: Int = 0

    /**
     * 混响
     */
    var soundReverb: Int = 0

    /**
     * 按键震动
     */
    var soundVibration: Boolean = false

    /**
     * 按键震动时长
     */
    var soundVibrationTime: Int = 10

    /**
     * 弹奏时展示等级
     */
    var showTouchNotesLevel: Boolean = true

    /**
     * 自动弹奏
     */
    var autoPlay: Boolean = true

    /**
     * 展示判断线
     */
    var showLine: Boolean = true

    /**
     * 显示键盘缩略图
     */
    var loadLongKeyboard: Boolean = false

    /**
     * 按键效果
     */
    var keyboardPrefer: Boolean = true

    /**
     * 判断线加粗类型
     */
    var roughLine: Int = 1

    /**
     * 聊天音效
     */
    var chatSound: Boolean = false

    /**
     * 聊天字体大小
     */
    var chatTextSize: Int = 15

    /**
     * 瀑布流曲谱播放速度
     */
    var waterfallSongSpeed: Float = 1f

    /**
     * 瀑布流音块下落速率
     */
    var waterfallDownSpeed: Float = 0.8f

    /**
     * 瀑布流左手音块颜色
     */
    var waterfallLeftHandColor: Int = 0xFF2BBBFB.toInt()

    /**
     * 瀑布流右手音块颜色
     */
    var waterfallRightHandColor: Int = 0xFFFF802D.toInt()

    /**
     * 瀑布流自由演奏音块颜色
     */
    var waterfallFreeStyleColor: Int = 0xFFFFFF00.toInt()

    /**
     * 是否存储聊天记录
     */
    var saveChatRecord: Boolean = false

    /**
     * 聊天是否展示时间
     */
    var showChatTime: Boolean = false

    /**
     * 曲谱播放时是否显示通知栏
     */
    var showNotification: Boolean = false

    /**
     * 钢琴键盘显示按键标签种类
     */
    var keyboardOctaveTagType: Int = 0

    /**
     * 联网键盘模式是否实时传输
     */
    var keyboardRealtime: Boolean = true

    /**
     * 从sharedPreferences获取设置
     */
    fun loadSettings(context: Context, online: Boolean) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        if (online) {
            tempSpeed = 1f
            autoPlay = true
        } else {
            tempSpeed = sharedPreferences.getString("temp_speed", "1.0")!!.toFloat()
            autoPlay = sharedPreferences.getBoolean("auto_play", true)
        }
        isOpenChord = sharedPreferences.getBoolean("sound_check_box", true)
        chordVolume = sharedPreferences.getString("b_s_vol", "0.8")!!.toFloat()
        soundDelay = sharedPreferences.getString("sound_delay", "0")!!.toInt()
        soundReverb = sharedPreferences.getString("sound_reverb", "0")!!.toInt()
        // 延音和混响：数值直接更新到C++层
        SoundEngineUtil.setDelay(soundDelay)
        SoundEngineUtil.setReverb(soundReverb)
        soundVibration = sharedPreferences.getBoolean("sound_vibration", false)
        soundVibrationTime = sharedPreferences.getString("sound_vibration_time", "10")!!.toInt()
        keyboardPrefer = sharedPreferences.getBoolean("keyboard_prefer", true)
        showTouchNotesLevel = sharedPreferences.getBoolean("tishi_cj", true)
        showLine = sharedPreferences.getBoolean("show_line", true)
        loadLongKeyboard = sharedPreferences.getBoolean("open_long_key", false)
        roughLine = sharedPreferences.getString("rough_line", "1")!!.toInt()
        midiKeyboardTune = sharedPreferences.getString("midi_keyboard_tune", "0")!!.toInt()
        keyboardSoundTune = sharedPreferences.getString("keyboard_sound_tune", "0")!!.toInt()
        keyboardAnim = sharedPreferences.getBoolean("keyboard_anim", true)
        chatSound = sharedPreferences.getBoolean("chats_sound", false)
        notesDownSpeed = 3 / (sharedPreferences.getString("down_speed", "1")!!.toFloat())
        noteSize = sharedPreferences.getString("note_size", "1")!!.toFloat()
        noteDismiss = sharedPreferences.getBoolean("note_dismiss", false)
        changeNotesColor = sharedPreferences.getBoolean("change_color", true)
        chatTextSize = sharedPreferences.getString("chats_text_size", "15")!!.toInt()
        waterfallSongSpeed = sharedPreferences.getString("waterfall_song_speed", "1.0")!!.toFloat()
        waterfallDownSpeed = sharedPreferences.getString("waterfall_down_speed", "0.8")!!.toFloat()
        waterfallLeftHandColor = sharedPreferences.getInt(
            "waterfall_left_hand_color",
            0xFF2BBBFB.toInt()
        )
        waterfallRightHandColor = sharedPreferences.getInt(
            "waterfall_right_hand_color",
            0xFFFF802D.toInt()
        )
        waterfallFreeStyleColor = sharedPreferences.getInt(
            "waterfall_free_style_color",
            0xFFFFFF00.toInt()
        )
        saveChatRecord = sharedPreferences.getBoolean("save_chats", false)
        showChatTime = sharedPreferences.getBoolean("chats_time_show", false)
        showNotification = sharedPreferences.getBoolean("show_notification", false)
        keyboardOctaveTagType = sharedPreferences.getString("octave_tag_type", "0")!!.toInt()
        keyboardRealtime = sharedPreferences.getBoolean("keyboard_realtime", true)
    }
}