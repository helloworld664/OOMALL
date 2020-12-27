package cn.edu.xmu.flashsale.service;

import cn.edu.xmu.flashsale.dao.FlashSaleDao;
import cn.edu.xmu.flashsale.dao.FlashSaleItemDao;
import cn.edu.xmu.flashsale.mapper.FlashSalePoMapper;
import cn.edu.xmu.flashsale.model.bo.FlashSale;
import cn.edu.xmu.flashsale.model.bo.FlashSaleItem;
import cn.edu.xmu.flashsale.model.po.FlashSaleItemPo;
import cn.edu.xmu.flashsale.model.po.FlashSalePo;
import cn.edu.xmu.flashsale.model.vo.FlashSaleInsertVo;
import cn.edu.xmu.flashsale.model.vo.FlashSaleSimpleVo;
import cn.edu.xmu.flashsale.model.vo.TimeSegmentVo;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ResponseUtil;
import cn.edu.xmu.ooad.util.ReturnObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.CoreSubscriber;
import reactor.core.publisher.Flux;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;


/**
 * @author XC
 */

@Service
public class FlashSaleService {
    private static final Logger logger = LoggerFactory.getLogger(FlashSaleService.class);

    @Autowired
    private FlashSaleDao flashSaleDao;

    @Autowired
    private FlashSalePoMapper flashSalePoMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private FlashSaleItemDao flashSaleItemDao;

    //@Autowired
    //private ReactiveRedisTemplate<String, Serializable> reactiveRedisTemplate;

    public Flux<FlashSaleItem> getFlashSaleInDetail(Long segId) {
        String segIdStr = "seg_" + segId;
        // To be implement
        //return reactiveRedisTemplate.opsForSet().members(segIdStr).map(x -> (FlashSaleItem) x);
        return new Flux<FlashSaleItem>() {
            @Override
            public void subscribe(CoreSubscriber<? super FlashSaleItem> coreSubscriber) {

            }
        };
    }

    /**
     * 平台管理员在某个时段下新建秒杀
     * @param shopId
     * @param id
     * @param flashSaleSimpleVo
     * @return
     */
    public ReturnObject createFlashSaleByTimeSegId(Long shopId, Long id, FlashSaleSimpleVo flashSaleSimpleVo) {
        ReturnObject<Long> returnObject = flashSaleDao.createFlashSaleByTimeSegId(id, flashSaleSimpleVo);
        if (returnObject.getCode() != ResponseCode.OK)
            return new ReturnObject(returnObject.getCode(), returnObject.getErrmsg());
        FlashSalePo flashSalePo = flashSaleDao.getFlashSaleByFlashSaleId(returnObject.getData()).getData();
        FlashSale flashSale = new FlashSale(flashSalePo);
        return new ReturnObject(flashSale);
    }

    /**
     * 管理员修改秒杀活动
     * @param shopId
     * @param id
     * @param flashSaleSimpleVo
     * @return
     */
    public ReturnObject updateFlashSale(Long shopId, Long id, FlashSaleSimpleVo flashSaleSimpleVo) {
        FlashSalePo flashSalePo = flashSaleDao.getFlashSaleByFlashSaleId(id).getData();
        if (flashSalePo == null)
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        if (flashSalePo.getFlashDate().compareTo(LocalDateTime.now()) < 0)
            return new ReturnObject(ResponseCode.INTERNAL_SERVER_ERR);
        if (flashSalePo.getState() == FlashSale.State.ONSHELVES.getCode())
            return new ReturnObject(ResponseCode.INTERNAL_SERVER_ERR);
        flashSaleSimpleVo.setFlashDate(flashSaleDao.createRealTime(flashSaleSimpleVo.getFlashDate(), LocalDateTime.of(2020, 01, 01, 0, 0, 0)));
        ReturnObject<Boolean> checkResult = flashSaleDao.checkFlashSale(id, flashSaleSimpleVo.getFlashDate());
        if (checkResult.getData())
            return new ReturnObject(ResponseCode.FLASHSALE_OUTLIMIT);
        ReturnObject returnObject = flashSaleDao.updateFlashSale(id, flashSaleSimpleVo);
        if (returnObject.getCode() == ResponseCode.OK)
            return new ReturnObject();
        else
            return new ReturnObject(returnObject.getCode(), returnObject.getErrmsg());
    }

    /**
     * 平台管理员向秒杀活动添加商品SKU
     * @param shopId
     * @param id
     * @param flashSaleInsertVo
     * @return
     */
    public ReturnObject insertSKUIntoFlashSale(Long shopId, Long id, FlashSaleInsertVo flashSaleInsertVo) {
        ReturnObject<FlashSalePo> flashSale = flashSaleDao.getFlashSaleByFlashSaleId(id);
        if (flashSale == null) {
            return new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        ReturnObject<Boolean> booleanReturnObject = flashSaleItemDao.checkSKUInFlashSale(id, flashSaleInsertVo.getSkuId());
        if (booleanReturnObject.getCode() != ResponseCode.OK) {
            return new ReturnObject(booleanReturnObject.getCode(), booleanReturnObject.getErrmsg());
        }
        if (booleanReturnObject.getData() == true) {
            return new ReturnObject(ResponseCode.SKUPRICE_CONFLICT);
        }
        ReturnObject<FlashSaleItemPo> returnObject = flashSaleDao.insertSKUIntoFlashSale(id, flashSaleInsertVo);
        FlashSaleItemPo flashSaleItemPo = returnObject.getData();
        FlashSaleItem flashSaleItem = new FlashSaleItem(flashSaleItemPo);
        return new ReturnObject(flashSaleItem);
    }

    /**
     * 平台管理员在秒杀活动删除商品SKU
     * @param shopId
     * @param id
     * @param itemId
     * @return
     */
    public ReturnObject deleteSKUFromFlashSale(Long shopId, Long id, Long itemId) {
        ReturnObject<Boolean> booleanReturnObject = flashSaleItemDao.checkItem(itemId);
        if (booleanReturnObject.getCode() != ResponseCode.OK)
            return new ReturnObject(booleanReturnObject.getCode(), booleanReturnObject.getErrmsg());
        if (booleanReturnObject.getData()) {
            ReturnObject returnObject = flashSaleItemDao.deleteFlashSaleItem(itemId);
            return returnObject;
        } else
            return new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST);
    }

    /**
     * 管理员删除某个时段秒杀
     * @param did
     * @param id
     * @return
     */
    public ReturnObject deletaFlashSale(Long did, Long id) {
        ReturnObject returnObject = flashSaleDao.updateState(did, id, FlashSale.State.DELETED.getCode());
        if (returnObject == null)
            return new ReturnObject(ResponseCode.INTERNAL_SERVER_ERR);
        else
            return returnObject;
    }

    /**
     * 管理员上线秒杀活动
     * @param did
     * @param id
     * @return
     */
    public ReturnObject flashSaleOnShelves(Long did, Long id) {
        ReturnObject returnObject = flashSaleDao.updateState(did, id, FlashSale.State.ONSHELVES.getCode());
        if (returnObject == null)
            return new ReturnObject(ResponseCode.INTERNAL_SERVER_ERR, ResponseCode.IMG_FORMAT_ERROR.getMessage());
        else
            return returnObject;
    }

    /**
     * 管理员下线秒杀活动
     * @param did
     * @param id
     * @return
     */
    public ReturnObject flashSaleOffShelves(Long did, Long id) {
        ReturnObject returnObject = flashSaleDao.updateState(did, id, FlashSale.State.OFFSHELVES.getCode());
        if (returnObject == null)
            return new ReturnObject(ResponseCode.INTERNAL_SERVER_ERR, ResponseCode.IMG_FORMAT_ERROR.getMessage());
        else
            return returnObject;
    }
}
