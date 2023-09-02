package ly.pp.justpiano3.entity

/**
 * 商店商品
 */
data class ShopProduct(
    /**
     * 商品id
     */
    var id: Int,

    /**
     * 商品名称
     */
    var name: String,

    /**
     * 商品图片（字符串，传递给安卓端后安卓端按字符串反射图片资源id进行加载）
     */
    var picture: String,

    /**
     * 商品价格（音符币）
     */
    var price: Int,

    /**
     * 商品描述
     */
    var description: String,
)