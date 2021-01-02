package cn.edu.xmu.ininterface.service;

import cn.edu.xmu.ininterface.service.model.vo.ShopToAllVo;


public interface InShopService {

    ShopToAllVo presaleFindShop(Long id);

    boolean shopExitOrNot(Long shopId);
}
