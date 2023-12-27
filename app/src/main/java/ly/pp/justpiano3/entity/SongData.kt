package ly.pp.justpiano3.entity

import java.io.File

/**
 * 曲谱数据
 */
data class SongData(

    /**
     * 曲谱时长（秒）
     */
    var length: Int,

    /**
     * 曲谱右手难度（*10）
     */
    var rightHandDegree: Int,

    /**
     * 曲谱左手难度（*10）
     */
    var leftHandDegree: Int,

    /**
     * 曲谱右手明键满分
     */
    var fullScore: Int,

    /**
     * 曲谱左手明键满分
     */
    var leftFullScore: Int,

    /**
     * 曲谱右手含隐藏键满分
     */
    var fullAllScore: Int,

    /**
     * 曲谱左手含隐藏键满分
     */
    var leftFullAllScore: Int,

    /**
     * 曲谱pm文件
     */
    var pmFile: File,
)
