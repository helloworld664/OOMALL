package cn.edu.xmu.goods.model.bo;

import cn.edu.xmu.goods.model.po.GoodsSpuPo;
import cn.edu.xmu.goods.model.vo.SpuInputVo;
import cn.edu.xmu.ooad.model.VoObject;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GoodsSpu implements VoObject {

    private Long id;

    private String name;

    private Long brandId;

    private Long categoryId;

    private Long freightId;

    private Long shopId;

    private String goodsSn;

    private String detail;

    private String imageUrl;

    private String spec;

    private Boolean disabled;

    private LocalDateTime gmtCreated;

    private LocalDateTime gmtModified;

    public GoodsSpu(GoodsSpuPo goodsSpuPo) {
        this.id = goodsSpuPo.getId();
        this.name = goodsSpuPo.getName();
        this.brandId = goodsSpuPo.getBrandId();
        this.categoryId = goodsSpuPo.getCategoryId();
        this.freightId = goodsSpuPo.getFreightId();
        this.shopId = goodsSpuPo.getShopId();
        this.goodsSn = goodsSpuPo.getGoodsSn();
        this.detail = goodsSpuPo.getDetail();
        this.imageUrl = goodsSpuPo.getImageUrl();
        this.spec = goodsSpuPo.getSpec();
        this.disabled = goodsSpuPo.getDisabled() != 0;
        this.gmtCreated = goodsSpuPo.getGmtCreate();
        this.gmtModified = goodsSpuPo.getGmtModified();
    }

    public GoodsSpu() {
    }

    public GoodsSpuPo createUpdateStatePo(Long code) {
        GoodsSpuPo goodsSpuPo = new GoodsSpuPo();
        goodsSpuPo.setId(id);
        goodsSpuPo.setGmtModified(LocalDateTime.now());
        return goodsSpuPo;
    }

    @Override
    public Object createVo() {
        return null;
    }

    @Override
    public Object createSimpleVo() {
        return null;
    }


    public GoodsSpuPo createUpdatePo(Long shopId, SpuInputVo spuInputVo) {
        //在数据库中插入新建的商品spu的各项信息
        GoodsSpuPo goodsSpuPo = new GoodsSpuPo();
        goodsSpuPo.setId(id);
        goodsSpuPo.setName(spuInputVo.getName());
        goodsSpuPo.setDetail(spuInputVo.getDecription());
        goodsSpuPo.setSpec(spuInputVo.getSpecs());
        goodsSpuPo.setShopId(shopId);
        goodsSpuPo.setDisabled((byte) 1);
        goodsSpuPo.setGmtModified(LocalDateTime.now());
        goodsSpuPo.setGmtCreate(LocalDateTime.now());
        return goodsSpuPo;
    }
}