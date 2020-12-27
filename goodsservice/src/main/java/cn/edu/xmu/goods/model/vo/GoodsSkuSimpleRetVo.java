package cn.edu.xmu.goods.model.vo;

import cn.edu.xmu.goods.model.bo.GoodsSku;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;

/**
 * @author XC
 */

@Data
@ApiModel
public class GoodsSkuSimpleRetVo implements Serializable {
    private Long id;

    private String skuSn;

    private String name;

    private Long originalPrice;

    private Integer price;

    private String imageUrl;

    private Integer inventory;

    private Byte disabled;

    public GoodsSkuSimpleRetVo(GoodsSku goodSku){
        this.setDisabled( goodSku.getDisabled());
        this.setId (goodSku.getId());
        this.setImageUrl(goodSku.getImageUrl());
        this.setInventory( goodSku.getInventory());
        this.setName(goodSku.getName());
        this.setOriginalPrice ( goodSku.getOriginalPrice());
        //this.price = goodSku.getPrice();
        this.setSkuSn ( goodSku.getSkuSn());
    }

    public GoodsSkuSimpleRetVo(){}
}