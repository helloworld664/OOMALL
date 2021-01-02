package cn.edu.xmu.flashsale.service;

import cn.edu.xmu.flashsale.dao.FlashSaleDao;
import cn.edu.xmu.flashsale.dao.FlashSaleItemDao;
import cn.edu.xmu.flashsale.mapper.FlashSalePoMapper;
import cn.edu.xmu.flashsale.mapper.GoodsSkuPoMapper;
import cn.edu.xmu.flashsale.model.bo.FlashSale;
import cn.edu.xmu.flashsale.model.bo.FlashSaleItem;
import cn.edu.xmu.flashsale.model.bo.GoodsSku;
import cn.edu.xmu.flashsale.model.po.FlashSaleItemPo;
import cn.edu.xmu.flashsale.model.po.FlashSalePo;
import cn.edu.xmu.flashsale.model.vo.FlashSaleInsertVo;
import cn.edu.xmu.flashsale.model.vo.FlashSaleSimpleVo;
import cn.edu.xmu.flashsale.model.vo.TimeSegmentVo;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ResponseUtil;
import cn.edu.xmu.ooad.util.ReturnObject;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.CoreSubscriber;
import reactor.core.publisher.Flux;

import javax.annotation.Resource;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;


/**
 * @author XC 3304
 * Created at 2020-12-07 10:37
 * Modified at 2021-01-01 03:03
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

    @Resource
    private ReactiveRedisTemplate<String, Serializable> reactiveRedisTemplate;

    @Autowired
    private GoodsSkuPoMapper goodsSkuPoMapper;

    @DubboReference(check = false, version = "2.2.0")
    GoodsService goodsService;

    public Flux<FlashSaleItem> getFlashSaleInDetail(Long segId) {
        String segIdStr = "seg_" + segId;
        if (redisTemplate.opsForSet().size(segIdStr) == 0) {
            List<FlashSalePo> flashSalePo = flashSaleDao.getFlashSaleByTimeSegmentId(segId);
            if (flashSalePo.size() != 0) {
                List<FlashSaleItemPo> flashSaleItemPos = flashSaleItemDao.getFlashSaleItemPoFromSaleId(flashSalePo.get(0).getId());
                for (FlashSaleItemPo flashSaleItemPo : flashSaleItemPos) {
                    GoodsSku goodsSku = goodsService.getSkuById(flashSaleItemPo.getGoodsSkuId());
                    FlashSaleItem flashSaleItem = new FlashSaleItem(flashSaleItemPo, goodsSku);
                    redisTemplate.opsForSet().add(segIdStr, flashSaleItem);
                }
            }
        }
        return reactiveRedisTemplate.opsForSet().members(segIdStr).map(x -> (FlashSaleItem) x);
    }

    @Transactional
    public Flux<FlashSaleItem> getCurrentFlashSale(LocalDateTime localDateTime) {
        String currentNow = "flashSaleNow_" + localDateTime.toString();
        if (redisTemplate.opsForSet().size(currentNow) == 0) {
            TimeSegmentVo timeSegmentPo = new TimeSegmentVo();
            timeSegmentPo.setId(9L);
            if (timeSegmentPo != null) {
                List<FlashSalePo> flashSalePos = flashSaleDao.getFlashSaleByTimeSegmentId(timeSegmentPo.getId());
                if (flashSalePos.size() != 0) {
                    FlashSalePo flashSalePo = flashSalePos.get(1);
                    List<FlashSaleItemPo> flashSaleItemPoFromSaleId = flashSaleItemDao.getFlashSaleItemPoFromSaleId(flashSalePo.getId());
                    for (FlashSaleItemPo flashSaleItemPo : flashSaleItemPoFromSaleId) {
                        //GoodsSku goodsSku = goodsService.getSkuById(flashSaleItemPo.getGoodsSkuId());
                        FlashSaleItem flashSaleItem = new FlashSaleItem(flashSaleItemPo, null);
                        redisTemplate.opsForSet().add(currentNow, flashSaleItem);
                    }
                }
            }
        }
        return reactiveRedisTemplate.opsForSet().members(currentNow).map(x -> (FlashSaleItem) x);
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
            return new ReturnObject(ResponseCode.ACTIVITYALTER_INVALID);
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
        //GoodsSku goodsSku = goodsService.getSkuById(flashSaleItemPo.getGoodsSkuId());
        FlashSaleItem flashSaleItem = new FlashSaleItem(flashSaleItemPo, null);
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
    public ReturnObject deleteFlashSale(Long did, Long id) {
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
            return returnObject;
    }
}
