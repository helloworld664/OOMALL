package cn.edu.xmu.goods.model.vo;

import cn.edu.xmu.goods.model.po.ShopPo;
import cn.edu.xmu.ooad.model.VoObject;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "商铺模板视图对象")
public class ShopVo implements VoObject {
    @ApiModelProperty(value="商铺名")
    private String name;

    public ShopPo createShop(){
        ShopPo shopPo = new ShopPo();
        shopPo.setName(this.name);
        return shopPo;
    }

    @Override
    public Object createVo() {
        return null;
    }

    @Override
    public Object createSimpleVo() {
        return null;
    }
}
