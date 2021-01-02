package cn.edu.xmu.goods.dao;

import cn.edu.xmu.goods.mapper.ShopPoMapper;
import cn.edu.xmu.goods.model.bo.Shop;
import cn.edu.xmu.goods.model.po.ShopPo;
import cn.edu.xmu.goods.model.vo.ShopModelVo;
import cn.edu.xmu.goods.model.vo.ShopStateVo;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import javax.swing.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author XC, 3304
 * Created at 2021-01-01 20:41
 * Modified at 2021-01-01 20:41
 */

@Repository
public class ShopDao implements InitializingBean {
    private final Logger logger = LoggerFactory.getLogger(ShopDao.class);

    @Autowired
    private ShopPoMapper shopPoMapper;

    /**
     * 获得店铺所有状态
     * @return
     */
    public ReturnObject<List> getAllStates() {
        Shop.State[] states = Shop.State.class.getEnumConstants();
        List<ShopStateVo> grouponStateVoList = new ArrayList<ShopStateVo>();
        for (Integer i = 0; i < states.length; i++) {
            grouponStateVoList.add(new ShopStateVo(states[i]));
            logger.debug("state " + i + ": " + states[i]);
        }
        return new ReturnObject<List>(grouponStateVoList);
    }

    /**
     *
     * @param shopModelVo
     * @return
     */
    public ShopPo applyShopBySeller(ShopModelVo shopModelVo) {
        ShopPo shopPo = shopModelVo.modifiedShop();
        if (shopModelVo.getName() == null)
            return null;
        shopPo.setName(shopModelVo.getName());
        shopPo.setGmtCreate(LocalDateTime.now());
        shopPo.setState(Shop.State.NOT_EXAMINE.getCode());
        shopPoMapper.insertSelective(shopPo);
        return shopPo;
    }

    /**
     *
     * @param id
     * @param shopModelVo
     * @return
     */
    public ShopPo modifyShopBySeller(Long id, ShopModelVo shopModelVo) {
        ShopPo shopPo = shopPoMapper.selectByPrimaryKey(id);
        if (shopModelVo.getName() == null)
            return null;
        shopPo.setName(shopModelVo.getName());
        shopPo.setGmtModified(LocalDateTime.now());
        return shopPo;
    }

    /**
     *
     * @param shopId
     * @param shopId
     * @param state
     * @return
     */
    public ReturnObject<Object> updateState(Long shopId, Byte state) {
        ShopPo shopPo = shopPoMapper.selectByPrimaryKey(shopId);
        if (shopPo == null) {
            logger.info("Groupon " + shopId + " is not exist.");
            return new ReturnObject<Object>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        if (shopPo.getId() != shopId)
            return new ReturnObject<Object>(ResponseCode.RESOURCE_ID_NOTEXIST);
        if (shopPo.getState() != state)
            return new ReturnObject<Object>(ResponseCode.GROUPON_STATENOTALLOW);
        if (state == Shop.State.ONSHELVES.getCode())
            return new ReturnObject<Object>(ResponseCode.TIMESEG_CONFLICT);
        shopPo.setState(state);
        int flag;
        ReturnObject<Object> returnObject = new ReturnObject<Object>(ResponseCode.OK);
        try {
            flag = shopPoMapper.updateByPrimaryKeySelective(shopPo);
            if (flag == 0) {
                logger.info("Groupon " + shopId + " is not exist.");
                return new ReturnObject<Object>(ResponseCode.RESOURCE_ID_NOTEXIST);
            }
        } catch (DataAccessException e) {
            logger.error("Database Error: " + e.getMessage());
            return new ReturnObject<Object>(ResponseCode.INTERNAL_SERVER_ERR, String.format("Database Error: " + e.getMessage()));
        } catch (Exception e) {
            logger.error("Unknown Error: " + e.getMessage());
            return new ReturnObject<Object>(ResponseCode.INTERNAL_SERVER_ERR, String.format("Unknown Error: " + e.getMessage()));
        }
        return returnObject;
    }

    public ShopPo selectShopById(Long shopId){
        ShopPo shopPo = shopPoMapper.selectByPrimaryKey(shopId);
        return shopPo;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }
}
