package cn.edu.xmu.flashsale.model.vo;

//import cn.edu.xmu.goods.model.vo.GoodsSkuSimpleRetVo;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author XC
 */

@Data
public class FlashSaleRetItemVo {
    private Long id;

    private Long goodsSkuId;

    private Long price;

    private Integer quantity;

    private LocalDateTime gmtCreated;

    private LocalDateTime gmtModified;
}
