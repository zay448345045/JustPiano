package ly.pp.justpiano3.entity

import android.content.Context
import android.preference.PreferenceManager

/**
 * 设置项
 */
object GlobalSetting{
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
    var notesDownSpeed: Float = 6f
    var mofa: Float = 0f

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
    var waterfallDownSpeed: Float = 1f

    /**
     * 是否兼容模式播放
     */
    var compatiblePlaySound: Boolean = true

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
        keyboardPrefer = sharedPreferences.getBoolean("keyboard_prefer", true)
        showTouchNotesLevel = sharedPreferences.getBoolean("tishi_cj", true)
        showLine = sharedPreferences.getBoolean("show_line", true)
        loadLongKeyboard = sharedPreferences.getBoolean("open_long_key", false)
        roughLine = sharedPreferences.getString("rough_line", "1")!!.toInt()
        midiKeyboardTune = sharedPreferences.getString("midi_keyboard_tune", "0")!!.toInt()
        keyboardSoundTune = sharedPreferences.getString("keyboard_sound_tune", "0")!!.toInt()
        keyboardAnim = sharedPreferences.getBoolean("keyboard_anim", true)
        chatSound = sharedPreferences.getBoolean("chats_sound", false)
        notesDownSpeed = sharedPreferences.getString("down_speed", "6")!!.toFloat()
        mofa = sharedPreferences.getString("mofa", "0")!!.toFloat()
        noteSize = sharedPreferences.getString("note_size", "1")!!.toFloat()
        noteDismiss = sharedPreferences.getBoolean("note_dismiss", false)
        changeNotesColor = sharedPreferences.getBoolean("change_color", true)
        chatTextSize = sharedPreferences.getString("chats_text_size", "15")!!.toInt()
        waterfallSongSpeed = sharedPreferences.getString("waterfall_song_speed", "1.0")!!.toFloat()
        waterfallDownSpeed = sharedPreferences.getString("waterfall_down_speed", "1.0")!!.toFloat()
        compatiblePlaySound = sharedPreferences.getBoolean("compatible_sound",true)
        saveChatRecord = sharedPreferences.getBoolean("save_chats",false)
        showChatTime = sharedPreferences.getBoolean("chats_time_show",false)
        showNotification = sharedPreferences.getBoolean("show_notification",false)
    }

    /**
     * 写入设置到sharedPreferences
     * 部分值存了，不是全部都存了
     */
    fun saveSettings(context: Context) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val edit = sharedPreferences.edit()
        edit.putBoolean("sound_check_box", isOpenChord)
        edit.putString("b_s_vol", chordVolume.toString())
        edit.putBoolean("keyboard_prefer", keyboardPrefer)
        edit.putBoolean("tishi_cj", showTouchNotesLevel)
        edit.putBoolean("show_line", showLine)
        edit.putBoolean("open_long_key", loadLongKeyboard)
        edit.putString("rough_line", roughLine.toString())
        edit.putString("midi_keyboard_tune", midiKeyboardTune.toString())
        edit.putString("keyboard_sound_tune", keyboardSoundTune.toString())
        edit.putBoolean("keyboard_anim", keyboardAnim)
        edit.putBoolean("chats_sound", chatSound)
        edit.putString("down_speed", notesDownSpeed.toString())
        edit.putString("note_size", noteSize.toString())
        edit.putBoolean("note_dismiss", noteDismiss)
        edit.putBoolean("change_color", changeNotesColor)
        edit.putString("chats_text_size", chatTextSize.toString())
        edit.putString("waterfall_song_speed", waterfallSongSpeed.toString())
        edit.putString("waterfall_down_speed", waterfallDownSpeed.toString())
        edit.putBoolean("compatible_sound", compatiblePlaySound)
        edit.putBoolean("save_chats", saveChatRecord)
        edit.putBoolean("chats_time_show", showChatTime)
        edit.putBoolean("show_notification", showNotification)
        edit.apply()
    }
}