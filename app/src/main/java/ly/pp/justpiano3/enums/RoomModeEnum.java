package ly.pp.justpiano3.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 游戏模式枚举
 *
 * @author as
 **/
@Getter
@AllArgsConstructor
public enum RoomModeEnum {

    NORMAL(0, "普通模式"),
    TEAM(1, "组队模式"),
    COUPLE(2, "双人模式"),
    KEYBOARD(3, "键盘模式"),
    ;

    private final int code;
    private final String desc;

    public static RoomModeEnum ofCode(int code, RoomModeEnum defaultValue) {
        for (RoomModeEnum value : values()) {
            if (value.code == code) {
                return value;
            }
        }
        return defaultValue;
    }
}
