package ly.pp.justpiano3.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "song")
data class Song(
    /**
     * 自增主键
     * 注意客户端的自增主键与服务端的曲谱id不同，取服务端的曲谱id请根据pm文件名（数据库的filePath字段）
     */
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    var id: Int?,

    /**
     * 曲谱名称
     */
    @ColumnInfo(name = "name", defaultValue = "")
    var name: String,

    /**
     * 曲谱分类
     */
    @ColumnInfo(name = "item", defaultValue = "经典乐章")
    var category: String,

    /**
     * 曲谱文件路径
     */
    @ColumnInfo(name = "path", defaultValue = "")
    var filePath: String,

    /**
     * 曲谱是否为新同步的曲谱
     */
    @ColumnInfo(name = "isnew", defaultValue = "1")
    var isNew: Int,

    /**
     * 曲谱是否为热门曲谱，预留字段
     */
    @ColumnInfo(name = "ishot", defaultValue = "0")
    var isHot: Int,

    /**
     * 曲谱是否加入了收藏
     */
    @ColumnInfo(name = "isfavo", defaultValue = "0")
    var isFavorite: Int,

    /**
     * 冠军用户，预留字段
     */
    @ColumnInfo(name = "player", defaultValue = "")
    var winner: String,

    /**
     * 本地弹奏右手最高分数
     */
    @ColumnInfo(name = "score", defaultValue = "0")
    var rightHandHighScore: Int,

    /**
     * 本地弹奏最高分数时产生最高记录的时间戳
     */
    @ColumnInfo(name = "date", defaultValue = "0")
    var highScoreDate: Long,

    /**
     * 曲谱文件版本，表示曲谱被(在线曲库同步)更新了多少次，用于做标记-删除曲谱
     * 更新和插入的曲谱会得到更新，然后把未更新也未插入的（就是新版没有这个pm）的曲子删掉，也就是在客户端曲子删库用
     */
    @ColumnInfo(name = "count", defaultValue = "0")
    var fileVersion: Int,

    /**
     * 曲谱右手难度
     */
    @ColumnInfo(name = "diff", defaultValue = "0")
    var rightHandDegree: Float,

    /**
     * 在线曲库是否可见，预留字段
     */
    @ColumnInfo(name = "online", defaultValue = "1")
    var isOnline: Int,

    /**
     * 曲谱左手难度
     */
    @ColumnInfo(name = "Ldiff", defaultValue = "0")
    var leftHandDegree: Float,

    /**
     * 曲谱时长
     */
    @ColumnInfo(name = "length", defaultValue = "0")
    var length: Int,

    /**
     * 本地弹奏左手最高分数
     */
    @ColumnInfo(name = "Lscore", defaultValue = "0")
    var leftHandHighScore: Int,
)
