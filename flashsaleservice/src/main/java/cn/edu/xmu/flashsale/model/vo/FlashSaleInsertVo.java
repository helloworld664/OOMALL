package cn.edu.xmu.flashsale.model.vo;

import cn.edu.xmu.flashsale.model.bo.FlashSaleItem;
import cn.edu.xmu.flashsale.model.po.FlashSaleItemPo;
import cn.edu.xmu.flashsale.model.po.FlashSalePo;
import cn.edu.xmu.ooad.model.VoObject;
import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * @author XC
 */

@Data
@ApiModel
public class FlashSaleInsertVo implements VoObject {
    private Long skuId;

    private Long price;

    private Integer quantity;

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public FlashSaleItemPo flashSaleInsertVo() {
        FlashSaleItemPo flashSaleItemPo = new FlashSaleItemPo();
        flashSaleItemPo.setGoodsSkuId(skuId);
        flashSaleItemPo.setPrice(price);
        flashSaleItemPo.setQuantity(quantity);
        return flashSaleItemPo;
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
