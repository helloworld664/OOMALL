package cn.edu.xmu.presale.model.vo;

import cn.edu.xmu.presale.model.bo.Presale;
import lombok.Data;

/**
 * @author XC 3304
 * Created at 2020-12-06 08:48
 * Modified at 2020-12-16 00:03
 */

@Data
public class PresaleStateVo {
    private int code;

    private String name;

    public PresaleStateVo(Presale.State state) {
        this.code = state.getCode();
        this.name = state.getValue();
    }
}
