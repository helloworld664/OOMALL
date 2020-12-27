package cn.edu.xmu.flashsale.model.bo;

import cn.edu.xmu.flashsale.model.po.FlashSaleItemPo;
import cn.edu.xmu.flashsale.model.vo.FlashSaleRetItemVo;
import cn.edu.xmu.ooad.model.VoObject;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author XC
 */
public class FlashSaleItem implements VoObject, Serializable {
    private Long id;

    private Long saleId;

    private Long goodsSkuId;

    //private GoodsSkuSimpleRetVo goodsSku;

    private Long price;

    private Integer quantity;

    private LocalDateTime gmtCreated;

    private LocalDateTime gmtModified;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSaleId() {
        return saleId;
    }

    public void setSaleId(Long saleId) {
        this.saleId = saleId;
    }

    public Long getGoodsSkuId() {
        return goodsSkuId;
    }

    public void setGoodsSkuId(Long goodsSkuId) {
        this.goodsSkuId = goodsSkuId;
    }

    /**
    public GoodsSkuSimpleRetVo getGoodsSku() {
        return goodsSku;
    }

    public void setGoodsSku(GoodsSkuSimpleRetVo goodsSku) {
        this.goodsSku = goodsSku;
    }
     */

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

    public LocalDateTime getGmtCreated() {
        return gmtCreated;
    }

    public void setGmtCreated(LocalDateTime gmtCreated) {
        this.gmtCreated = gmtCreated;
    }

    public LocalDateTime getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(LocalDateTime gmtModified) {
        this.gmtModified = gmtModified;
    }

    public FlashSaleItem(FlashSaleItemPo flashSaleItemPo) {
        this.id = flashSaleItemPo.getId();
        this.saleId = flashSaleItemPo.getSaleId();
        this.gmtCreated = flashSaleItemPo.getGmtCreate();
        this.goodsSkuId = flashSaleItemPo.getGoodsSkuId();
        this.gmtModified = flashSaleItemPo.getGmtModified();
        this.price = flashSaleItemPo.getPrice();
        this.quantity = flashSaleItemPo.getQuantity();
    }

    @Override
    public Object createVo() {
        FlashSaleRetItemVo flashSaleItemVo = new FlashSaleRetItemVo();
        flashSaleItemVo.setId(this.id);
        flashSaleItemVo.setGmtCreated(this.gmtCreated);
        flashSaleItemVo.setGmtModified(this.gmtModified);
        flashSaleItemVo.setPrice(this.price);
        flashSaleItemVo.setQuantity(this.quantity);
        flashSaleItemVo.setGoodsSkuId(this.goodsSkuId);
        return flashSaleItemVo;
    }

    @Override
    public Object createSimpleVo() {
        return null;
    }
}
