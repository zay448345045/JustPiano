package ly.pp.justpiano3.entity;

import android.graphics.RectF;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 瀑布流音符
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class WaterfallNote {

    /**
     * 绘制区域左边界坐标
     */
    private float left;

    /**
     * 绘制区域右边界坐标
     */
    private float right;

    /**
     * 音符开始时间，以曲谱开始时间为0开始累计计算的值，毫秒
     */
    private int startTime;

    /**
     * 音符开始时间，以曲谱开始时间为0开始累计计算的值，毫秒
     */
    private int endTime;

    /**
     * 是否为左手
     */
    private boolean leftHand;

    /**
     * 音高
     */
    private int pitch;

    /**
     * 音量
     */
    private int volume;

    public int interval() {
        return endTime - startTime;
    }
}
