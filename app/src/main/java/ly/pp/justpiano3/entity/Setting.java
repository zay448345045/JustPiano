package ly.pp.justpiano3.entity;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import lombok.Data;

/**
 * 设置中的设置项
 */
@Data
public class Setting {

    public static int SETTING_MODE_CODE = 122;

    /**
     * 音块变色
     */
    private Boolean changeNotesColor;

    /**
     * 节拍比率
     */
    public Float tempSpeed;

    /**
     * 音块消失
     */
    private Boolean noteDismiss;

    /**
     * 聊天音效
     */
    private Boolean chatSound;

    /**
     * 音块大小
     */
    private Float noteSize;

    /**
     * 音块速率
     */
    private Integer notesDownSpeed;

    /**
     * MIDI键盘移调
     */
    private Integer midiKeyboardTune;

    /**
     * 触摸键盘移调
     */
    private Integer keyboardSoundTune;

    /**
     * 是否开启键盘模式动画
     */
    private Boolean keyboardAnim;

    /**
     * 是否开启和弦
     */
    private Boolean isOpenChord;

    /**
     * 和弦音量
     */
    private Float chordVolume;

    /**
     * 弹奏时展示等级
     */
    private Boolean showTouchNotesLevel;

    /**
     * 自动弹奏
     */
    private Boolean autoPlay;

    /**
     * 展示判断线
     */
    private Boolean showLine;

    /**
     * 显示键盘缩略图
     */
    private Boolean loadLongKeyboard;

    /**
     * 动画帧率
     */
    private Integer animFrame;

    /**
     * 按键效果
     */
    private Boolean keyboardPrefer;

    /**
     * 判断线加粗类型
     */
    private Integer roughLine;

    /**
     * 聊天字体大小
     */
    private Integer chatTextSize;

    /**
     * 从sharedPreferences获取设置
     */
    public void loadSettings(Context context, boolean online) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (online) {
            setTempSpeed(1f);
            setAutoPlay(true);
        } else {
            setAutoPlay(sharedPreferences.getBoolean("auto_play", true));
            setTempSpeed(Float.parseFloat(sharedPreferences.getString("temp_speed", "1.0")));
        }
        setIsOpenChord(sharedPreferences.getBoolean("sound_check_box", true));
        setChordVolume(Float.parseFloat(sharedPreferences.getString("b_s_vol", "0.8")));
        setAnimFrame(Integer.parseInt(sharedPreferences.getString("anim_frame", "4")));
        setKeyboardPrefer(sharedPreferences.getBoolean("keyboard_perfer", true));
        setShowTouchNotesLevel(sharedPreferences.getBoolean("tishi_cj", true));
        setShowLine(sharedPreferences.getBoolean("show_line", true));
        setLoadLongKeyboard(sharedPreferences.getBoolean("open_long_key", false));
        setRoughLine(Integer.parseInt(sharedPreferences.getString("rough_line", "1")));
        setMidiKeyboardTune(Integer.parseInt(sharedPreferences.getString("midi_keyboard_tune", "0")));
        setKeyboardSoundTune(Integer.parseInt(sharedPreferences.getString("keyboard_sound_tune", "0")));
        setKeyboardAnim(sharedPreferences.getBoolean("keyboard_anim", true));
        setChatSound(sharedPreferences.getBoolean("chats_sound", false));
        setNotesDownSpeed(Integer.parseInt(sharedPreferences.getString("down_speed", "6")));
        setNoteSize(Float.parseFloat(sharedPreferences.getString("note_size", "1")));
        setNoteDismiss(sharedPreferences.getBoolean("note_dismiss", false));
        setChangeNotesColor(sharedPreferences.getBoolean("change_color", true));
        setChatTextSize(Integer.parseInt(sharedPreferences.getString("msg_font_size", "15")));
    }

    /**
     * 写入设置到sharedPreferences
     */
    public void saveSettings(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString("keyboard_sound_tune", String.valueOf(keyboardSoundTune));
        edit.putString("midi_keyboard_tune", String.valueOf(midiKeyboardTune));
        edit.putString("down_speed", String.valueOf(notesDownSpeed));
        edit.apply();
    }
}
