package cn.edu.xmu.flashsale.model.bo;

import cn.edu.xmu.flashsale.model.po.FlashSaleItemPo;
import cn.edu.xmu.flashsale.model.vo.FlashSaleRetItemVo;
import cn.edu.xmu.flashsale.model.vo.GoodsSkuSimpleRetVo;
import cn.edu.xmu.flashsale.model.vo.GoodsSkuVo;
import cn.edu.xmu.ooad.model.VoObject;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author XC 3304
 * Created at 2020-12-07 10:51
 * Modified at 2020-12-07 10:51
 */

@Data
public class FlashSaleItem implements VoObject, Serializable {
    private Long id;

    private Long saleId;

    private Long goodsSkuId;

    private Long goodsSKuId;

    private GoodsSkuVo goodsSku;

    private Long price;

    private Integer quantity;

    private LocalDateTime gmtCreated;

    private LocalDateTime gmtModified;

    public FlashSaleItem(FlashSaleItemPo flashSaleItemPo, GoodsSku goodsSku) {
        this.id = flashSaleItemPo.getId();
        this.saleId = flashSaleItemPo.getSaleId();
        this.gmtCreated = flashSaleItemPo.getGmtCreate();
        this.goodsSku = null;
        this.gmtModified = flashSaleItemPo.getGmtModified();
        this.price = flashSaleItemPo.getPrice();
        this.quantity = flashSaleItemPo.getQuantity();
    }

    @Override
    public Object createVo() {
        FlashSaleRetItemVo flashSaleRetItemVo = new FlashSaleRetItemVo();
        flashSaleRetItemVo.setId(this.id);
        flashSaleRetItemVo.setGmtCreated(this.gmtCreated);
        flashSaleRetItemVo.setGmtModified(this.gmtModified);
        flashSaleRetItemVo.setPrice(this.price);
        flashSaleRetItemVo.setQuantity(this.quantity);
        flashSaleRetItemVo.setGoodsSku(this.goodsSku);
        return flashSaleRetItemVo;
    }

    @Override
    public Object createSimpleVo() {
        return null;
    }
}
