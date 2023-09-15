package ly.pp.justpiano3.entity

import java.io.File

/**
 * 曲谱数据
 */
data class SongData(
    /**
     * 曲谱pm文件
     */
    var pmFile: File? = null,

    /**
     * 曲谱midi文件
     */
    var midiFile: File? = null,

    /**
     * 曲谱右手明键满分
     */
    var fullScore: Int? = null,

    /**
     * 曲谱右手含隐藏键满分
     */
    var fullAllScore: Int? = null,

    /**
     * 曲谱左手明键满分
     */
    var leftFullScore: Int? = null,

    /**
     * 曲谱左手含隐藏键满分
     */
    var leftFullAllScore: Int? = null,

    /**
     * 曲谱时长（秒）
     */
    var length: Int? = null,

    /**
     * 曲谱右手难度（*10）
     */
    var degree: Int? = null,

    /**
     * 曲谱左手难度（*10）
     */
    var leftDegree: Int? = null
)
