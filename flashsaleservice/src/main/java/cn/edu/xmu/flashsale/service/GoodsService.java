package cn.edu.xmu.flashsale.service;

import cn.edu.xmu.flashsale.model.bo.GoodsSku;
import org.springframework.stereotype.Service;

/**
 * @author XC
 */

public interface GoodsService {
    GoodsSku getSkuById(Long id);
}
