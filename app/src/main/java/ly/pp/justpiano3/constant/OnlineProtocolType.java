package ly.pp.justpiano3.constant;

/**
 * online模块协议常量
 *
 * @author as2134u
 * @since 2021-11-10
 */
public class OnlineProtocolType {

    /**
     * 查看用户个人资料对话框
     */
    public static final int USER_INFO_DIALOG = 2;

    /**
     * 开始弹奏
     */
    public static final int PLAY_START = 3;

    /**
     * 准备和取消准备
     */
    public static final int CHANGE_ROOM_USER_STATUS = 4;

    /**
     * 弹奏成绩判定
     */
    public static final int PLAY_FINISH = 5;

    /**
     * 创建房间
     */
    public static final int CREATE_ROOM = 6;

    /**
     * 进入房间
     */
    public static final int ENTER_ROOM = 7;

    /**
     * 退出房间
     */
    public static final int QUIT_ROOM = 8;

    /**
     * 被踢出房间
     */
    public static final int KICKED_QUIT_ROOM = 9;

    /**
     * 用户登录对战
     */
    public static final int LOGIN = 10;

    /**
     * 大厅聊天
     */
    public static final int HALL_CHAT = 12;

    /**
     * 房间聊天
     */
    public static final int ROOM_CHAT = 13;

    /**
     * 更换房名或密码
     */
    public static final int CHANGE_ROOM_INFO = 14;

    /**
     * 播放曲谱
     */
    public static final int PLAY_SONG = 15;

    /**
     * 每日挑战
     */
    public static final int CHALLENGE = 16;

    /**
     * 系统播报消息
     */
    public static final int BROADCAST = 17;

    /**
     * 家族
     */
    public static final int FAMILY = 18;

    /**
     * 进入大厅后加载房间列表
     */
    public static final int LOAD_ROOM_LIST = 19;

    /**
     * 有人改变房间状态，只传回
     */
    public static final int CHANGE_ROOM_POSITION = 20;

    /**
     * 房间内初次加载所有人物数据
     */
    public static final int LOAD_ROOM_POSITION = 21;

    /**
     * 大厅中加载变化的房间列表，只传回
     */
    public static final int CHANGE_ROOM_LIST = 22;

    /**
     * 房间开始弹奏后加载用户
     */
    public static final int LOAD_PLAY_USER = 23;

    /**
     * 更新弹奏界面左上角成绩条
     */
    public static final int MINI_GRADE = 25;

    /**
     * 商店
     */
    public static final int SHOP = 26;

    /**
     * 推荐曲谱，只传回
     */
    public static final int RECOMMEND_SONG = 27;

    /**
     * 主界面加载用户信息及大厅列表
     */
    public static final int LOAD_USER = 28;

    /**
     * 进入大厅
     */
    public static final int ENTER_HALL = 29;

    /**
     * 退出大厅
     */
    public static final int QUIT_HALL = 30;

    /**
     * 增删好友、解除搭档、设置祝语
     */
    public static final int SET_USER_INFO = 31;

    /**
     * 显示/隐藏弹奏界面左上角成绩条
     */
    public static final int SET_MINI_GRADE = 32;

    /**
     * 更换服装
     */
    public static final int CHANGE_CLOTHES = 33;

    /**
     * 查看好友、私信和主界面查看搭档
     */
    public static final int LOAD_USER_INFO = 34;

    /**
     * 发送私信
     */
    public static final int SEND_MAIL = 35;

    /**
     * 大厅加载用户列表或房间加载邀请列表
     */
    public static final int LOAD_USER_LIST = 36;

    /**
     * 找Ta或邀请等任何对话框提示
     */
    public static final int DIALOG = 37;

    /**
     * 每日签到等每日奖励相关
     */
    public static final int DAILY = 38;

    /**
     * 键盘模式传输音符
     */
    public static final int KEYBOARD = 39;

    /**
     * 等级考试
     */
    public static final int CL_TEST = 40;

    /**
     * 心跳
     */
    public static final int HEART_BEAT = 41;

    /**
     * 打开/关闭空位
     */
    public static final int CHANGE_ROOM_DOOR = 42;

    /**
     * 显示房间内成员信息
     */
    public static final int LOAD_ROOM_USER_LIST = 43;

    /**
     * 改变左右手
     */
    public static final int CHANGE_ROOM_HAND = 44;

    /**
     * 搭档、祝福
     */
    public static final int COUPLE = 45;

    /**
     * 聊天消息类型
     */
    public static class MsgType {

        /**
         * 曲谱推荐消息
         */
        public static final int SONG_RECOMMEND_MESSAGE = 0;

        /**
         * 公聊消息
         */
        public static final int PUBLIC_MESSAGE = 1;

        /**
         * 私聊消息
         */
        public static final int PRIVATE_MESSAGE = 2;

        /**
         * 系统消息
         */
        public static final int SYSTEM_MESSAGE = 3;

        /**
         * 全服消息
         */
        public static final int ALL_SERVER_MESSAGE = 18;

        /**
         * 流消息
         */
        public static final int STREAM_MESSAGE = 19;

        /**
         * 流消息协议
         */
        public static class StreamMsg {

            /**
             * 协议：开始
             */
            public static final String START = "S:";

            /**
             * 协议：数据
             */
            public static final String DATA = "D:";

            /**
             * 协议：结束
             */
            public static final String END = "E:";

            /**
             * 参数名字 消息id
             */
            public static final String PARAM_ID = "STREAM_ID";

            /**
             * 参数名字 消息状态
             */
            public static final String PARAM_STATUS = "STREAM_STATUS";
        }
    }
}
