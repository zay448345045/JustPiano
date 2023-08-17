package ly.pp.justpiano3.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ly.pp.justpiano3.thread.PlaySongs;

/**
 * 曲谱播放模式枚举
 *
 * @author as
 **/
@Getter
@AllArgsConstructor
public enum PlaySongsModeEnum {

    ONCE(0, "单次播放", "单次播放已开启"),
    RECYCLE(1, "单曲循环", "单曲循环已开启"),
    RANDOM(2, "曲库随机", "乐曲将随机播放"),
    FAVOR_RANDOM(3, "收藏随机", "乐曲将选择收藏夹内曲目随机播放"),
    ;

    private final int code;
    private final String name;
    private final String desc;

    public static PlaySongsModeEnum ofCode(int code, PlaySongsModeEnum defaultValue) {
        for (PlaySongsModeEnum value : values()) {
            if (value.code == code) {
                return value;
            }
        }
        return defaultValue;
    }
}
