package cn.edu.xmu.goods.model.bo;

import cn.edu.xmu.goods.model.po.GoodsCategoryPo;
import cn.edu.xmu.goods.model.vo.CategoryInputVo;
import cn.edu.xmu.ooad.model.VoObject;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GoodsCategory implements VoObject {

    private Long id;

    private Long pid;

    private String name;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;

    public GoodsCategory() {
    }

    public GoodsCategory(GoodsCategoryPo po) {
        this.id = po.getId();
        this.pid = po.getPid();
        this.name = po.getName();
        this.gmtCreate = po.getGmtCreate();
        this.gmtModified = po.getGmtModified();
    }

    public GoodsCategoryPo createUpdatePo(CategoryInputVo categoryInputVo) {
        GoodsCategoryPo goodsCategoryPo = new GoodsCategoryPo();
        String nameModify = categoryInputVo.getName() == null ? null : categoryInputVo.getName();
        goodsCategoryPo.setId(id);
        goodsCategoryPo.setGmtModified(LocalDateTime.now());
        goodsCategoryPo.setName(nameModify);
        return goodsCategoryPo;
    }

    public GoodsCategoryPo createAddPo(Long id, CategoryInputVo categoryInputVo) {
        GoodsCategoryPo goodsCategoryPo = new GoodsCategoryPo();
        goodsCategoryPo.setGmtCreate(LocalDateTime.now());
        goodsCategoryPo.setName(categoryInputVo.getName());
        goodsCategoryPo.setPid(id);
        return goodsCategoryPo;
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
