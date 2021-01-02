package cn.edu.xmu.goods.service;

import cn.edu.xmu.goods.dao.ShopDao;
import cn.edu.xmu.goods.mapper.ShopPoMapper;
import cn.edu.xmu.goods.model.bo.Shop;
import cn.edu.xmu.goods.model.po.ShopPo;
import cn.edu.xmu.goods.model.vo.ConclusionVo;
import cn.edu.xmu.goods.model.vo.ShopModelVo;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author XC, 3304
 * Created at 2021-01-01 20:16
 * Modified at 2021-01-01 20:16
 */

@Service
public class ShopService {
    private final Logger logger = LoggerFactory.getLogger(ShopService.class);

    @Autowired
    private ShopDao shopDao;

    @Autowired
    private ShopPoMapper shopPoMapper;

    /**
     * 获得店铺所有状态
     * @return
     */
    public List getAllStates() {
        ReturnObject<List> listReturnObject = shopDao.getAllStates();
        return listReturnObject.getData();
    }

    /**
     *
     * @param shopModelVo
     * @return
     */
    public ReturnObject applyShopBySeller(ShopModelVo shopModelVo) {
        ShopPo shopPo = shopDao.applyShopBySeller(shopModelVo);
        if (shopPo == null)
            return null;
        try {
            if (shopPo != null)
                return new ReturnObject(new Shop(shopPo).createVo());
            else
                return new ReturnObject(ResponseCode.INTERNAL_SERVER_ERR);
        } catch (DataAccessException e) {
            logger.error("Database Error: " + e.getMessage());
            return new ReturnObject(ResponseCode.INTERNAL_SERVER_ERR, String.format("Database Error: " + e.getMessage()));
        } catch (Exception e) {
            logger.error("Unknown Error: " + e.getMessage());
            return new ReturnObject(ResponseCode.INTERNAL_SERVER_ERR, String.format("Unknown Error: " + e.getMessage()));
        }
    }

    /**
     *
     * @param id
     * @param shopModelVo
     * @return
     */
    public ReturnObject modifyShopBySeller(Long id, ShopModelVo shopModelVo) {
        try {
            if (check(id) == false)
                return new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST);
            ShopPo shopPo = shopDao.selectShopById(id);
            shopPo = shopDao.modifyShopBySeller(id, shopModelVo);
            if (shopPo == null)
                return null;
            shopPoMapper.updateByPrimaryKeySelective(shopPo);
            return new ReturnObject<>(new Shop(shopPo).createVo());
        } catch (DataAccessException e) {
            logger.error("Database Error: " + e.getMessage());
            return new ReturnObject(ResponseCode.INTERNAL_SERVER_ERR, String.format("Database Error: " + e.getMessage()));
        } catch (Exception e) {
            logger.error("Unknown Error: " + e.getMessage());
            return new ReturnObject(ResponseCode.INTERNAL_SERVER_ERR, String.format("Unknown Error: " + e.getMessage()));
        }
    }

    /**
     *
     * @param shopId
     * @return
     */
    public ReturnObject closeShop(Long shopId) {
        return shopDao.updateState(shopId, Shop.State.CLOSE.getCode());
    }

    /**
     *
     * @param shopId
     * @param id
     * @param conclusionVo
     * @return
     */
    public ReturnObject newShop(Long shopId, Long id, ConclusionVo conclusionVo) {
        if (conclusionVo.getConclusion() == true)
            return shopDao.updateState(shopId, Shop.State.OFFSHELVES.getCode());
        else
            return shopDao.updateState(shopId, Shop.State.NOT_PASS.getCode());
    }

    /**
     *
     * @param shopId
     * @return
     */
    public ReturnObject shopOnShelves(Long shopId) {
        return shopDao.updateState(shopId, Shop.State.ONSHELVES.getCode());
    }

    /**
     *
     * @param shopId
     * @return
     */
    public ReturnObject shopOffShelves(Long shopId) {
        return shopDao.updateState(shopId, Shop.State.OFFSHELVES.getCode());
    }

    private Boolean check(Long shopId) {
        ShopPo shopPo = shopDao.selectShopById(shopId);
        if (shopPo == null)
            return false;
        else
            return true;
    }
}
