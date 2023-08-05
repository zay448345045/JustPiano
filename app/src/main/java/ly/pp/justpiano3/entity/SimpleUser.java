package ly.pp.justpiano3.entity;

/**
 * @author as
 */
public class SimpleUser {

    /**
     * 用户性别
     */
    private String gender;

    /**
     * 用户名称
     */
    private String name;

    /**
     * 用户等级
     */
    private Integer lv;

    public SimpleUser(String gender, String name, Integer lv) {
        this.gender = gender;
        this.name = name;
        this.lv = lv;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getLv() {
        return lv;
    }

    public void setLv(Integer lv) {
        this.lv = lv;
    }
}
