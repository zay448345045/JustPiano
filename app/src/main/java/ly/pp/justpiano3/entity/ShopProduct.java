package ly.pp.justpiano3.entity;

/**
 * 商店商品
 */
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

    public ShopProduct(int id, String name, String picture, int price, String description) {
        this.id = id;
        this.name = name;
        this.picture = picture;
        this.price = price;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
