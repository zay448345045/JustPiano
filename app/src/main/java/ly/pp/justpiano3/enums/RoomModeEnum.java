package ly.pp.justpiano3.enums;

/**
 * 游戏模式枚举
 *
 * @author as
 **/
public enum RoomModeEnum {

    NORMAL(0, "普通模式"),
    TEAM(1, "组队模式"),
    COUPLE(2, "双人模式"),
    KEYBOARD(3, "键盘模式"),
    ;

    private final int code;
    private final String desc;

    RoomModeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static RoomModeEnum ofCode(int code, RoomModeEnum defaultValue) {
        for (RoomModeEnum value : values()) {
            if (value.code == code) {
                return value;
            }
        }
        return defaultValue;
    }
}
