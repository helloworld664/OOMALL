package cn.edu.xmu.flashsale.model.vo;

//import cn.edu.xmu.goods.model.vo.GoodsSkuSimpleRetVo;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author XC 3304
 * Created at 2020-12-10 19:56
 * Modified at 2020-12-10 19:56
 */

@Data
public class FlashSaleRetItemVo {
    private Long id;

    private Long goodsSkuId;

    private GoodsSkuVo goodsSku;

    private Long price;

    private Integer quantity;

    private LocalDateTime gmtCreated;

    private LocalDateTime gmtModified;
}
