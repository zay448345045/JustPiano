package ly.pp.justpiano3.enums;

/**
 * 曲谱播放模式枚举
 *
 * @author as
 **/
public enum PlaySongsModeEnum {

    ONCE(0, "单次播放", "单次播放已开启"),
    RECYCLE(1, "单曲循环", "单曲循环已开启"),
    RANDOM(2, "曲库随机", "乐曲将随机播放"),
    FAVOR_RANDOM(3, "收藏随机", "乐曲将选择收藏夹内曲目随机播放"),
    FAVOR(4, "收藏顺序", "乐曲将按收藏夹内曲目顺序播放"),
    ;

    private final int code;
    private final String name;
    private final String desc;

    PlaySongsModeEnum(int code, String name, String desc) {
        this.code = code;
        this.name = name;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }
}
