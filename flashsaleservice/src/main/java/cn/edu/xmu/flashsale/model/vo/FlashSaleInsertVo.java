package cn.edu.xmu.flashsale.model.vo;

import cn.edu.xmu.flashsale.model.bo.FlashSaleItem;
import cn.edu.xmu.flashsale.model.po.FlashSaleItemPo;
import cn.edu.xmu.flashsale.model.po.FlashSalePo;
import cn.edu.xmu.ooad.model.VoObject;
import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * @author XC 3304
 * Created at 2020-12-07 11:20
 * Modified at 2020-12-13 10:06
 */

@Data
@ApiModel
public class FlashSaleInsertVo implements VoObject {
    private Long skuId;

    private Long price;

    private Integer quantity;

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
