package ly.pp.justpiano3.entity

import android.content.Context
import android.preference.PreferenceManager

/**
 * 设置中的设置项
 */
data class Setting(
    /**
     * 音块变色
     */
    var changeNotesColor: Boolean = true,

    /**
     * 节拍比率
     */
    var tempSpeed: Float = 1f,

    /**
     * 音块消失
     */
    var noteDismiss: Boolean = false,

    /**
     * 音块大小
     */
    var noteSize: Float = 1f,

    /**
     * 音块速率
     */
    var notesDownSpeed: Int = 6,

    /**
     * MIDI键盘移调
     */
    var midiKeyboardTune: Int = 0,

    /**
     * 触摸键盘移调
     */
    var keyboardSoundTune: Int = 0,

    /**
     * 是否开启键盘模式动画
     */
    var keyboardAnim: Boolean = true,

    /**
     * 是否开启和弦
     */
    var isOpenChord: Boolean = true,

    /**
     * 和弦音量
     */
    var chordVolume: Float = 0.8f,

    /**
     * 弹奏时展示等级
     */
    var showTouchNotesLevel: Boolean = true,

    /**
     * 自动弹奏
     */
    var autoPlay: Boolean = true,

    /**
     * 展示判断线
     */
    var showLine: Boolean = true,

    /**
     * 显示键盘缩略图
     */
    var loadLongKeyboard: Boolean = false,

    /**
     * 动画帧率
     */
    var animFrame: Int = 4,

    /**
     * 按键效果
     */
    var keyboardPrefer: Boolean = true,

    /**
     * 判断线加粗类型
     */
    var roughLine: Int = 1,

    /**
     * 聊天音效
     */
    var chatSound: Boolean = false,

    /**
     * 聊天字体大小
     */
    var chatTextSize: Int = 15,

    /**
     * 瀑布流曲谱播放速度
     */
    var waterfallSongSpeed: Float = 1f,

    /**
     * 瀑布流音块下落速率
     */
    var waterfallDownSpeed: Float = 1f,
) {

    /**
     * 从sharedPreferences获取设置
     */
    fun loadSettings(context: Context?, online: Boolean) {
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
        animFrame = sharedPreferences.getString("anim_frame", "4")!!.toInt()
        keyboardPrefer = sharedPreferences.getBoolean("keyboard_prefer", true)
        showTouchNotesLevel = sharedPreferences.getBoolean("tishi_cj", true)
        showLine = sharedPreferences.getBoolean("show_line", true)
        loadLongKeyboard = sharedPreferences.getBoolean("open_long_key", false)
        roughLine = sharedPreferences.getString("rough_line", "1")!!.toInt()
        midiKeyboardTune = sharedPreferences.getString("midi_keyboard_tune", "0")!!.toInt()
        keyboardSoundTune = sharedPreferences.getString("keyboard_sound_tune", "0")!!.toInt()
        keyboardAnim = sharedPreferences.getBoolean("keyboard_anim", true)
        chatSound = sharedPreferences.getBoolean("chats_sound", false)
        notesDownSpeed = sharedPreferences.getString("down_speed", "6")!!.toInt()
        noteSize = sharedPreferences.getString("note_size", "1")!!.toFloat()
        noteDismiss = sharedPreferences.getBoolean("note_dismiss", false)
        changeNotesColor = sharedPreferences.getBoolean("change_color", true)
        chatTextSize = sharedPreferences.getString("chats_text_size", "15")!!.toInt()
        waterfallSongSpeed = sharedPreferences.getString("waterfall_song_speed", "1.0")!!.toFloat()
        waterfallDownSpeed = sharedPreferences.getString("waterfall_down_speed", "1.0")!!.toFloat()
    }

    /**
     * 写入设置到sharedPreferences
     */
    fun saveSettings(context: Context?) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val edit = sharedPreferences.edit()
        edit.putString("auto_play", autoPlay.toString())
        edit.putString("temp_speed", tempSpeed.toString())
        edit.putString("sound_check_box", isOpenChord.toString())
        edit.putString("b_s_vol", chordVolume.toString())
        edit.putString("anim_frame", animFrame.toString())
        edit.putString("keyboard_prefer", keyboardPrefer.toString())
        edit.putString("tishi_cj", showTouchNotesLevel.toString())
        edit.putString("show_line", showLine.toString())
        edit.putString("open_long_key", loadLongKeyboard.toString())
        edit.putString("rough_line", roughLine.toString())
        edit.putString("midi_keyboard_tune", midiKeyboardTune.toString())
        edit.putString("keyboard_sound_tune", keyboardSoundTune.toString())
        edit.putString("keyboard_anim", keyboardAnim.toString())
        edit.putString("chats_sound", chatSound.toString())
        edit.putString("down_speed", notesDownSpeed.toString())
        edit.putString("note_size", noteSize.toString())
        edit.putString("note_dismiss", noteDismiss.toString())
        edit.putString("change_color", changeNotesColor.toString())
        edit.putString("chats_text_size", chatTextSize.toString())
        edit.putString("waterfall_song_speed", waterfallSongSpeed.toString())
        edit.putString("waterfall_down_speed", waterfallDownSpeed.toString())
        edit.apply()
    }

    companion object {
        // activity区分是否是从设置界面返回的值
        const val SETTING_MODE_CODE = 122
    }
}