package cn.edu.xmu.flashsale.dao;

import cn.edu.xmu.flashsale.mapper.FlashSaleItemPoMapper;
import cn.edu.xmu.flashsale.model.po.FlashSaleItemPo;
import cn.edu.xmu.flashsale.model.po.FlashSaleItemPoExample;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author XC
 */

@Repository
public class FlashSaleItemDao implements InitializingBean {
    @Autowired
    private FlashSaleItemPoMapper flashSaleItemPoMapper;

    private static final Logger logger = LoggerFactory.getLogger(FlashSaleItemDao.class);

    /**
     *
     * @param saleId
     * @return
     */
    public List<FlashSaleItemPo> getFlashSaleItemPoFromSaleId(Long saleId) {
        FlashSaleItemPoExample example = new FlashSaleItemPoExample();
        FlashSaleItemPoExample.Criteria criteria = example.createCriteria();
        criteria.andSaleIdEqualTo(saleId);
        return flashSaleItemPoMapper.selectByExample(example);
    }

    /**
     * 检查商品SKU是否在秒杀活动中
     * @param flashSaleId
     * @param skuId
     * @return
     */
    public ReturnObject<Boolean> checkSKUInFlashSale(Long flashSaleId, Long skuId) {
        FlashSaleItemPoExample example = new FlashSaleItemPoExample();
        FlashSaleItemPoExample.Criteria criteria = example.createCriteria();
        criteria.andGoodsSkuIdEqualTo(skuId);
        criteria.andSaleIdEqualTo(flashSaleId);
        List<FlashSaleItemPo> flashSaleItemPos;
        try {
            flashSaleItemPos = flashSaleItemPoMapper.selectByExample(example);
        } catch (DataAccessException e) {
            logger.error("Database Error: " + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR,
                    String.format("Database Error: " + e.getMessage()));
        } catch (Exception e) {
            logger.error("Unknown Error: " + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR,
                    String.format("Unknown Error: " + e.getMessage()));
        }
        if (flashSaleItemPos.size() != 0)
            return new ReturnObject<Boolean>(true);
        else
            return new ReturnObject<Boolean>(false);
    }

    /**
     * 删除秒杀活动项
     * @param itemId
     * @return
     */
    public ReturnObject deleteFlashSaleItem(Long itemId) {
        FlashSaleItemPo flashSaleItemPo = flashSaleItemPoMapper.selectByPrimaryKey(itemId);
        try {
            flashSaleItemPoMapper.deleteByPrimaryKey(flashSaleItemPo.getId());
        } catch (DataAccessException e) {
            logger.error("Database Error: " + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("Database Error: " + e.getMessage()));
        } catch (Exception e) {
            logger.error("Unknown Error: " + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("Unknown Error: " + e.getMessage()));
        }
        return new ReturnObject(ResponseCode.OK);
    }

    /**
     * 按照id查询秒杀活动项
     * @param itemId
     * @return
     */
    public ReturnObject<Boolean> checkItem(Long itemId) {
        FlashSaleItemPo flashSaleItemPo = flashSaleItemPoMapper.selectByPrimaryKey(itemId);
        if(flashSaleItemPo != null)
            return new ReturnObject<>(true);
        else
            return new ReturnObject<>(false);
    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }
}
