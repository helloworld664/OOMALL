package cn.edu.xmu.flashsale.service;

import cn.edu.xmu.flashsale.model.bo.GoodsSku;
import org.springframework.stereotype.Service;

/**
 * @author XC 3304
 * Created at 2020-12-27 00:38
 * Modified at 2020-12-27 00:38
 */

public interface GoodsService {
    GoodsSku getSkuById(Long id);
}
