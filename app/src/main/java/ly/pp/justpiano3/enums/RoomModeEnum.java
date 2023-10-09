package ly.pp.justpiano3.enums;

/**
 * 游戏模式枚举
 *
 * @author as
 **/
public enum RoomModeEnum {

    NORMAL(0, "普通模式", "普通"),
    TEAM(1, "组队模式", "组队"),
    COUPLE(2, "双人模式", "双人"),
    KEYBOARD(3, "键盘模式", "键盘"),
    ;

    private final int code;
    private final String desc;
    private final String simpleName;

    RoomModeEnum(int code, String desc, String simpleName) {
        this.code = code;
        this.desc = desc;
        this.simpleName = simpleName;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public String getSimpleName() {
        return simpleName;
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
