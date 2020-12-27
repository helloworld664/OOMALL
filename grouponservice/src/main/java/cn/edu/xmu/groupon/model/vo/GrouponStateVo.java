package cn.edu.xmu.groupon.model.vo;

import cn.edu.xmu.groupon.model.bo.Groupon;
import lombok.Data;

/**
 * @author XC
 */

@Data
public class GrouponStateVo {
    private int code;

    private String name;

    public GrouponStateVo(Groupon.State state) {
        this.code = state.getCode();
        this.name = state.getDescription();
    }
}
