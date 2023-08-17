package ly.pp.justpiano3.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 音符 缓存在队列用于传输
 */
@Data
@AllArgsConstructor
public class OLNote {

    /**
     * 时间
     */
    private long absoluteTime;

    /**
     * 音高
     */
    private int pitch;

    /**
     * 音量
     */
    private int volume;
}
