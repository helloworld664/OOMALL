package cn.edu.xmu.presale.model.vo;

import cn.edu.xmu.presale.model.bo.Presale;
import lombok.Data;

/**
 * @author XC
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
