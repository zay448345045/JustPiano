package ly.pp.justpiano3.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 商店商品
 */
@Data
@AllArgsConstructor
public class ShopProduct {

    /**
     * 商品id
     */
    private int id;

    /**
     * 商品名称
     */
    private String name;

    /**
     * 商品图片（字符串，传递给安卓端后安卓端按字符串反射图片资源id进行加载）
     */
    private String picture;

    /**
     * 商品价格（音符币）
     */
    private int price;

    /**
     * 商品描述
     */
    private String description;
}
