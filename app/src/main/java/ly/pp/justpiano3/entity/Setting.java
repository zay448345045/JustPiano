package ly.pp.justpiano3.entity;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import lombok.Data;

/**
 * 设置中的设置项
 */
@Data
public class Setting {

    /**
     * 音块变色
     */
    public Boolean changeNotesColor;

    /**
     * 节拍比率
     */
    private Float tempSpeed;

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

    public static Setting loadSettings(SharedPreferences sharedPreferences, boolean online) {
        Setting setting = new Setting();
        if (online) {
            setting.setTempSpeed(1f);
            setting.setAutoPlay(true);
        } else {
            setting.setAutoPlay(sharedPreferences.getBoolean("auto_play", true));
            setting.setTempSpeed(Float.parseFloat(sharedPreferences.getString("temp_speed", "1.0")));
        }
        setting.setIsOpenChord(sharedPreferences.getBoolean("sound_check_box", true));
        setting.setChordVolume(Float.parseFloat(sharedPreferences.getString("b_s_vol", "0.8")));
        setting.setAnimFrame(Integer.parseInt(sharedPreferences.getString("anim_frame", "4")));
        setting.setKeyboardPrefer(sharedPreferences.getBoolean("keyboard_perfer", true));
        setting.setShowTouchNotesLevel(sharedPreferences.getBoolean("tishi_cj", true));
        setting.setShowLine(sharedPreferences.getBoolean("show_line", true));
        setting.setLoadLongKeyboard(sharedPreferences.getBoolean("open_long_key", false));
        setting.setRoughLine(Integer.parseInt(sharedPreferences.getString("rough_line", "1")));
        setting.setMidiKeyboardTune(Integer.parseInt(sharedPreferences.getString("midi_keyboard_tune", "0")));
        setting.setKeyboardSoundTune(Integer.parseInt(sharedPreferences.getString("keyboard_sound_tune", "0")));
        setting.setKeyboardAnim(sharedPreferences.getBoolean("keyboard_anim", true));
        setting.setChatSound(sharedPreferences.getBoolean("chats_sound", false));
        setting.setNotesDownSpeed(Integer.parseInt(sharedPreferences.getString("down_speed", "6")));
        setting.setNoteSize(Float.parseFloat(sharedPreferences.getString("note_size", "1")));
        setting.setNoteDismiss(sharedPreferences.getBoolean("note_dismiss", false));
        setting.setChangeNotesColor(sharedPreferences.getBoolean("change_color", true));
        setting.setChatTextSize(Integer.parseInt(sharedPreferences.getString("msg_font_size", "15")));
        return setting;
    }
}
