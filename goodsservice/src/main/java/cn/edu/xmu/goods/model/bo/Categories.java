package cn.edu.xmu.goods.model.bo;

import cn.edu.xmu.goods.model.po.GoodsCategoryPo;
import cn.edu.xmu.goods.model.vo.CategoriesVo;
import cn.edu.xmu.ooad.model.VoObject;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;


@Data
public class Categories implements VoObject, Serializable{
    private Long id;
    private String name;
    private Long pid;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;

    public Categories() {
    }

    /**
     * 构造函数
     *
     * @param po 用PO构造
     * @return Categories
     */
    public Categories(GoodsCategoryPo po) {
        this.id = po.getId();
        this.name = po.getName();
        this.pid = po.getPid();
        this.gmtCreate = po.getGmtCreate();
        this.gmtModified = po.getGmtModified();
    }

    /**@Override
    public Object createVo() {
        return new CategoriesRetVo(this);
    }

    @Override
    public CategoriesSimpleRetVo createSimpleVo() { return new CategoriesSimpleRetVo(this); }

    /**
     * 用vo对象创建更新po对象
     * @param vo vo对象
     * @return po对象
     */
    public GoodsCategoryPo createUpdatePo(CategoriesVo vo){
        GoodsCategoryPo po = new GoodsCategoryPo();
        po.setId(this.getId());
        po.setName(vo.getName());
        po.setPid(null);
        po.setGmtCreate(null);
        po.setGmtModified(LocalDateTime.now());
        return po;
    }


    /**
     * 用bo创建po
     * @return GoodsCategoryPo
     */
    public GoodsCategoryPo createCategoriesPo() {
        GoodsCategoryPo po = new GoodsCategoryPo();
        po.setId(this.getId());
        po.setName(this.getName());
        po.setPid(this.getPid());
        po.setGmtCreate(this.getGmtCreate());
        po.setGmtModified(this.getGmtModified());
        return po;
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
