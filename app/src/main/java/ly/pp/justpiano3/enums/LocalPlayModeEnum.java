package ly.pp.justpiano3.enums;

/**
 * 本地弹奏模式枚举
 *
 * @author as
 **/
public enum LocalPlayModeEnum {

    NORMAL(0, "普通模式"),
    PRACTISE(2, "练习模式"),
    ;

    private final int code;
    private final String desc;

    LocalPlayModeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
