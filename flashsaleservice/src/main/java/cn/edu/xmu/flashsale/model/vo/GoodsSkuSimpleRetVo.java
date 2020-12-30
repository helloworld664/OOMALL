package cn.edu.xmu.flashsale.model.vo;

import cn.edu.xmu.flashsale.model.bo.GoodsSku;
import lombok.Data;

import java.io.Serializable;

/**
 * @author XC
 */

@Data
public class GoodsSkuSimpleRetVo implements Serializable {
    private Long id;

    private String name;

    private String skuSn;

    private String imageUrl;

    private Integer inventory;

    private Long originalPrice;

    //private Long price;

    private Byte disabled;

    public GoodsSkuSimpleRetVo(GoodsSku goodsSku) {
        this.id = goodsSku.getId();
        this.name = goodsSku.getName();
        this.skuSn = goodsSku.getSkuSn();
        this.imageUrl = goodsSku.getImageUrl();
        this.inventory = goodsSku.getInventory();
        this.originalPrice = goodsSku.getOriginalPrice();
        this.disabled = goodsSku.getDisabled();
    }
}
