package ly.pp.justpiano3.enums;

/**
 * 游戏模式枚举
 *
 * @author as
 **/
public enum FamilyPositionEnum {

    LEADER(0, "族长"),
    VICE_LEADER(1, "副族长"),
    MEMBER(2, "族员"),
    NOT_IN_FAMILY(3, "非族员"),
    ;

    private final int code;
    private final String desc;

    FamilyPositionEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static FamilyPositionEnum ofCode(String code, FamilyPositionEnum defaultValue) {
        for (FamilyPositionEnum value : values()) {
            if (String.valueOf(value.code).equals(code)) {
                return value;
            }
        }
        return defaultValue;
    }
}
