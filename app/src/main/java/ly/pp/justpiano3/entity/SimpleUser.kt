package ly.pp.justpiano3.entity

import java.util.*

/**
 * @author as
 */
data class SimpleUser(
    /**
     * 用户性别
     */
    var gender: String,

    /**
     * 用户名称
     */
    var name: String,

    /**
     * 用户等级
     */
    var lv: Int,

    /**
     * 某些时间(临时时间存放变量)
     */
    var date: Date,
)