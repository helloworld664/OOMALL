package cn.edu.xmu.goods.model.vo;

import cn.edu.xmu.goods.model.bo.Shop;
import lombok.Data;

/**
 * @author XC, 3304
 * Created at 2021-01-01 20:44
 * Modified at 2021-01-01 20:44
 */

@Data
public class ShopStateVo {
    private int code;

    private String name;

    public ShopStateVo(Shop.State state) {
        this.code = state.getCode();
        this.name = state.getName();
    }
}
