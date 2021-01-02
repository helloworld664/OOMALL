package cn.edu.xmu.goods.model.vo;

import cn.edu.xmu.goods.model.po.ShopPo;
import lombok.Data;

/**
 * @author XC, 3304
 * Created at 2021-01-01 21:06
 * Modified at 2021-01-01 21:06
 */

@Data
public class ShopModelVo {
    private String name;

    public ShopPo modifiedShop() {
        ShopPo shopPo = new ShopPo();
        shopPo.setName(name);
        return shopPo;
    }
}
