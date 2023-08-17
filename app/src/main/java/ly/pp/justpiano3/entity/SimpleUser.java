package ly.pp.justpiano3.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

/**
 * @author as
 */
@Data
@AllArgsConstructor
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

    /**
     * 某些时间(临时时间存放变量)
     */
    private Date date;

    public SimpleUser(String gender, String name, Integer lv) {
        this.gender = gender;
        this.name = name;
        this.lv = lv;
    }
}
