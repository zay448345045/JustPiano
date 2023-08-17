package ly.pp.justpiano3.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * KeyboardSyncModeEnum
 * 键盘房间同步模式枚举
 * 编排模式：120毫秒同步一次音符
 * 协奏模式：一键一发
 *
 * @author Yzh
 * @since create(2023 / 8 / 3)
 **/
@Getter
@AllArgsConstructor
public enum KeyboardSyncModeEnum {

    ORCHESTRATE(1, "编排"),
    CONCERTO(2, "协奏"),

    ;

    private final int id;
    private final String desc;
}
