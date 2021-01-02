package cn.edu.xmu.goods.model.bo;

import cn.edu.xmu.goods.model.po.BrandPo;
import cn.edu.xmu.goods.model.vo.BrandInputVo;
import cn.edu.xmu.goods.model.vo.BrandVo;
import cn.edu.xmu.goods.model.vo.BrandRetVo;
import cn.edu.xmu.goods.model.vo.BrandSimpleRetVo;
import cn.edu.xmu.ooad.model.VoObject;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;


@Data
public class Brand implements VoObject, Serializable{

    private Long id;
    private String name;
    private String detail;
    private String imageUrl;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;

    public Brand() {
    }

    /**
     * 构造函数
     *
     * @param po 用PO构造
     * @return Brand
     */
    public Brand(BrandPo po) {
        this.id = po.getId();
        this.name = po.getName();
        this.detail = po.getDetail();
        this.imageUrl = po.getImageUrl();
        this.gmtCreate = po.getGmtCreate();
        this.gmtModified = po.getGmtModified();
    }

    @Override
    public Object createVo() {
            return new BrandRetVo(this);
    }

    @Override
    public BrandSimpleRetVo createSimpleVo() { return new BrandSimpleRetVo(this); }


    /**
     * 用vo对象创建更新po对象
     * @param brandInputVo vo对象
     * @return po对象
     */
    public BrandPo createUpdatePo(BrandInputVo brandInputVo) {
        String nameEnc = brandInputVo.getName() == null ? null : brandInputVo.getName();
        String detailEnc = brandInputVo.getDetail() == null ? null : brandInputVo.getDetail();
        BrandPo brandPo = new BrandPo();
        brandPo.setId(id);
        brandPo.setName(nameEnc);
        brandPo.setDetail(detailEnc);
        brandPo.setGmtModified(LocalDateTime.now());
        return brandPo;
    }


    /**
     * 用bo创建po
         * @return BrandPo
     */
    public BrandPo createBrandPo() {
        BrandPo po = new BrandPo();
        po.setId(this.getId());
        po.setName(this.getName());
        po.setDetail(this.getDetail());
        po.setImageUrl(this.getImageUrl());
        po.setGmtCreate(this.getGmtCreate());
        po.setGmtModified(this.getGmtModified());
        return po;
    }

    public BrandPo createAddPo(BrandInputVo brandVo) {
        BrandPo brandPo = new BrandPo();
        brandPo.setGmtCreate(LocalDateTime.now());
        brandPo.setName(brandVo.getName());
        brandPo.setDetail(brandVo.getDetail());
        return brandPo;
    }
}

