package cn.edu.xmu.groupon.model.vo;

import cn.edu.xmu.groupon.model.bo.Groupon;
import lombok.Data;

/**
 * @author XC 3304
 * Created at 2020-12-04 09:48
 * Modified at 2020-12-18 20:08
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
