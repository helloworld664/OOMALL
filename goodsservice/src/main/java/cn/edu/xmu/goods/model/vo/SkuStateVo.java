package cn.edu.xmu.goods.model.vo;

import cn.edu.xmu.goods.model.bo.GoodsSku;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SkuStateVo {

    @ApiModelProperty("状态码")
    private Long code;
    
    private String name;

    public SkuStateVo(GoodsSku.State state) {
        code = Long.valueOf(state.getCode());
        name = state.getDescription();
    }
}
