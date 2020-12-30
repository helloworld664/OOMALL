package cn.edu.xmu.flashsale.dao;

import cn.edu.xmu.flashsale.mapper.FlashSaleItemPoMapper;
import cn.edu.xmu.flashsale.mapper.FlashSalePoMapper;
import cn.edu.xmu.flashsale.mapper.FloatPricePoMapper;
import cn.edu.xmu.flashsale.mapper.TimeSegmentPoMapper;
import cn.edu.xmu.flashsale.model.bo.FlashSale;
import cn.edu.xmu.flashsale.model.bo.FlashSaleItem;
import cn.edu.xmu.flashsale.model.po.*;
import cn.edu.xmu.flashsale.model.vo.FlashSaleInsertVo;
import cn.edu.xmu.flashsale.model.vo.FlashSaleSimpleVo;
import cn.edu.xmu.flashsale.model.vo.TimeSegmentVo;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ResponseUtil;
import cn.edu.xmu.ooad.util.ReturnObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author XC
 */

@Repository
public class FlashSaleDao implements InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(FlashSaleDao.class);

    @Autowired
    private FlashSalePoMapper flashSalePoMapper;

    @Autowired
    private FlashSaleItemPoMapper flashSaleItemPoMapper;

    @Autowired
    private FloatPricePoMapper floatPricePoMapper;

    @Autowired
    private TimeSegmentPoMapper timeSegmentPoMapper;

    public FlashSalePo selectById(Long id) {
        FlashSalePo flashSalePo = flashSalePoMapper.selectByPrimaryKey(id);
        return flashSalePo;
    }

    public List<FlashSalePo> getFlashSaleByTimeSegmentId(Long timeSegmentId) {
        FlashSalePoExample flashSalePoExample = new FlashSalePoExample();
        FlashSalePoExample.Criteria criteria = flashSalePoExample.createCriteria();
        criteria.andTimeSegIdEqualTo(timeSegmentId);
        return flashSalePoMapper.selectByExample(flashSalePoExample);
    }

    /**
     * 平台管理员在某个时段下新建秒杀
     *
     * @param id
     * @param flashSaleSimpleVo
     * @return
     */
    public ReturnObject createFlashSaleByTimeSegId(Long id, FlashSaleSimpleVo flashSaleSimpleVo) {
        FlashSalePo flashSalePo = null;
        LocalDateTime today = LocalDateTime.now();
        if (flashSaleSimpleVo.getFlashDate().compareTo(LocalDateTime.now()) < 0
                || flashSaleSimpleVo.getFlashDate().compareTo(LocalDateTime.of(today.getYear(), today.getMonth(), today.getDayOfMonth(), 23, 59, 59)) < 0)
            return new ReturnObject(ResponseCode.FIELD_NOTVALID);
        TimeSegmentPo timeSegmentPo = timeSegmentPoMapper.selectByPrimaryKey(id);
        if (timeSegmentPo == null)
            return new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST);
        flashSaleSimpleVo.setFlashDate(createRealTime(flashSaleSimpleVo.getFlashDate(), LocalDateTime.of(2020, 01, 01, 0, 0, 0)));
        ReturnObject<Boolean> check = checkFlashSale(id, flashSaleSimpleVo.getFlashDate());
        if (check.getCode() != ResponseCode.OK)
            return new ReturnObject(check.getCode(), check.getErrmsg());
        if (check.getData())
            return new ReturnObject(ResponseCode.TIMESEG_CONFLICT);
        flashSalePo = new FlashSalePo();
        flashSalePo.setFlashDate(flashSaleSimpleVo.getFlashDate());
        flashSalePo.setGmtCreate(LocalDateTime.now());
        flashSalePo.setState(FlashSale.State.OFFSHELVES.getCode());
        flashSalePo.setTimeSegId(id);
        try {
            int flag = flashSalePoMapper.insertSelective(flashSalePo);
            if (flag == 1) {
                return new ReturnObject<Long>(flashSalePo.getId());
            } else {
                return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, "Created Failed");
            }
        } catch (DataAccessException e) {
            logger.error("Database Error: " + e.getMessage());
            return new ReturnObject(ResponseCode.INTERNAL_SERVER_ERR, String.format("Database Error: " + e.getMessage()));
        } catch (Exception e) {
            logger.error("Unknown Error: " + e.getMessage());
            return new ReturnObject(ResponseCode.INTERNAL_SERVER_ERR, String.format("Unknown Error: " + e.getMessage()));
        }
    }

    /**
     * 管理员修改秒杀活动
     *
     * @param id
     * @param flashSaleSimpleVo
     * @return
     */
    public ReturnObject updateFlashSale(Long id, FlashSaleSimpleVo flashSaleSimpleVo) {
        FlashSalePo flashSalePo = flashSalePoMapper.selectByPrimaryKey(id);
        TimeSegmentPo timeSegmentPo = timeSegmentPoMapper.selectByPrimaryKey(flashSalePo.getTimeSegId());
        LocalDateTime today = LocalDateTime.now();
        if (flashSalePo == null) {
            logger.info("FlashSale " + id + " is not exist.");
            return new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        if (timeSegmentPo.getBeginTime().compareTo(LocalDateTime.now()) < 0 && timeSegmentPo.getEndTime().compareTo(LocalDateTime.now()) > 0) {
            logger.info("FlashSale is in progress.");
            return new ReturnObject(ResponseCode.FIELD_NOTVALID);
        }
        if (flashSaleSimpleVo.getFlashDate().compareTo(LocalDateTime.now()) < 0
                || flashSaleSimpleVo.getFlashDate().compareTo(LocalDateTime.of(today.getYear(), today.getMonth(), today.getDayOfMonth(), 23, 59, 59)) < 0)
            return new ReturnObject(ResponseCode.FIELD_NOTVALID);
        flashSalePo.setFlashDate(flashSaleSimpleVo.getFlashDate());
        flashSalePo.setGmtModified(LocalDateTime.now());
        //if (flashSalePoMapper.updateByPrimaryKeySelective(flashSalePo) == 0)
        //return new ReturnObject(ResponseCode.INTERNAL_SERVER_ERR);
        int flag;
        flag = flashSalePoMapper.updateByPrimaryKeySelective(flashSalePo);
        if (flag == 0) {
            logger.info("FlashSale " + id + " is not exist.");
            return new ReturnObject(ResponseCode.INTERNAL_SERVER_ERR);
        }
        return new ReturnObject();
    }

    /**
     * 平台管理员向秒杀活动添加商品SKU
     *
     * @param id
     * @param flashSaleInsertVo
     * @return
     */
    public ReturnObject<FlashSaleItemPo> insertSKUIntoFlashSale(Long id, FlashSaleInsertVo flashSaleInsertVo) {
        FlashSaleItemPo flashSaleItemPo = flashSaleInsertVo.flashSaleInsertVo();
        flashSaleItemPo.setSaleId(id);
        flashSaleItemPo.setGmtCreate(LocalDateTime.now());
        int flag;
        try {
            flag = flashSaleItemPoMapper.insert(flashSaleItemPo);
            ;
            if (flag == 0) {
                logger.info("FlashSale " + id + " is not exist.");
                return new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST);
            }

        } catch (DataAccessException e) {
            logger.error("Database Error: " + e.getMessage());
            return new ReturnObject(ResponseCode.INTERNAL_SERVER_ERR, String.format("Database Error: " + e.getMessage()));
        } catch (Exception e) {
            logger.error("Unknown Error: " + e.getMessage());
            return new ReturnObject(ResponseCode.INTERNAL_SERVER_ERR, String.format("Unknown Error: " + e.getMessage()));
        }
        return new ReturnObject<>(flashSaleItemPo);
    }

    /**
     * 平台管理员在秒杀活动删除商品SKU
     *
     * @param itemId
     * @return
     */
    public FlashSaleItem deleteSKUFromFlashSale(Long itemId) {
        flashSaleItemPoMapper.deleteByPrimaryKey(itemId);
        return (FlashSaleItem) ResponseUtil.ok();
    }

    /**
     * 通过修改预售活动的状态实现对秒杀的删除、上线、下线
     *
     * @param did
     * @param id
     * @param state
     * @return
     */
    public ReturnObject updateState(Long did, Long id, Byte state) {
        ReturnObject<FlashSalePo> returnObjectFlashSalePo = getFlashSaleByFlashSaleId(id);
        if (returnObjectFlashSalePo.getCode() != ResponseCode.OK)
            return new ReturnObject(returnObjectFlashSalePo.getCode(), returnObjectFlashSalePo.getErrmsg());
        if (returnObjectFlashSalePo.getData() == null)
            return new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST);
        FlashSalePo flashSalePo = returnObjectFlashSalePo.getData();
        TimeSegmentPo timeSegmentPo = timeSegmentPoMapper.selectByPrimaryKey(flashSalePo.getTimeSegId());
        if (flashSalePo == null || timeSegmentPo == null) {
            logger.info("FlashSale " + id + " is not exist.");
            return new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        if (timeSegmentPo.getBeginTime().compareTo(LocalDateTime.now()) < 0 && timeSegmentPo.getEndTime().compareTo(LocalDateTime.now()) > 0) {
            logger.info("FlashSale is in progress.");
            return new ReturnObject(ResponseCode.FIELD_NOTVALID);
        }
        Byte stateExpected = null;
        if (state == FlashSale.State.ONSHELVES.getCode() || state == FlashSale.State.DELETED.getCode())
            stateExpected = FlashSale.State.OFFSHELVES.getCode();
        else
            stateExpected = FlashSale.State.ONSHELVES.getCode();
        ReturnObject check = checkFlashSaleId(flashSalePo, stateExpected);
        if (check.getCode() != ResponseCode.OK)
            return check;
        if (returnObjectFlashSalePo.getData().getState() == state)
            return new ReturnObject(ResponseCode.FIELD_NOTVALID);
        FlashSalePo flashSalePo1 = new FlashSalePo();
        flashSalePo1.setState(state);
        flashSalePo1.setId(id);
        int flag;
        ReturnObject returnObject = new ReturnObject(ResponseCode.OK);
        try {
            flag = flashSalePoMapper.updateByPrimaryKeySelective(flashSalePo1);
            if (flag == 0) {
                logger.info("FlashSale " + id + " is not exist.");
                return new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST);
            }
        } catch (DataAccessException e) {
            logger.error("Database Error: " + e.getMessage());
            return new ReturnObject(ResponseCode.INTERNAL_SERVER_ERR, String.format("Database Error: " + e.getMessage()));
        } catch (Exception e) {
            logger.error("Unknown Error: " + e.getMessage());
            return new ReturnObject(ResponseCode.INTERNAL_SERVER_ERR, String.format("Unknown Error: " + e.getMessage()));
        }
        return returnObject;
    }

    /**
     * 分别获得日期和时间
     *
     * @param flashDate
     * @param flashTime
     * @return
     */
    public LocalDateTime createRealTime(LocalDateTime flashDate, LocalDateTime flashTime) {
        return LocalDateTime.of(flashDate.getYear(), flashDate.getMonth(), flashDate.getDayOfMonth(),
                flashTime.getHour(), flashTime.getMinute(), flashTime.getSecond());
    }

    /**
     * 查看当前秒杀
     *
     * @param id
     * @param flashDate
     * @return
     */
    public ReturnObject<Boolean> checkFlashSale(Long id, LocalDateTime flashDate) {
        FlashSalePoExample example = new FlashSalePoExample();
        FlashSalePoExample.Criteria criteria = example.createCriteria();
        criteria.andTimeSegIdEqualTo(id);
        criteria.andFlashDateEqualTo(flashDate);
        criteria.andStateNotEqualTo(FlashSale.State.DELETED.getCode());

        logger.debug("Time Segment Id = " + id);

        List<FlashSalePo> flashSalePos = null;
        try {
            flashSalePos = flashSalePoMapper.selectByExample(example);
        } catch (DataAccessException e) {
            logger.error("Database Error: " + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR,
                    String.format("Database Error: " + e.getMessage()));
        } catch (Exception e) {
            logger.error("Unknown Error: " + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR,
                    String.format("Unknown Error: " + e.getMessage()));
        }
        if (flashSalePos == null || flashSalePos.size() == 0)
            return new ReturnObject<Boolean>(false);
        return new ReturnObject<Boolean>(true);
    }

    /**
     * 按照id查询秒杀活动
     *
     * @param flashSaleId
     * @return
     */
    public ReturnObject<FlashSalePo> getFlashSaleByFlashSaleId(Long flashSaleId) {
        try {
            FlashSalePo flashSalePo = flashSalePoMapper.selectByPrimaryKey(flashSaleId);
            if (flashSalePo == null || flashSalePo.getState() == FlashSale.State.DELETED.getCode())
                return new ReturnObject();
            else
                return new ReturnObject<>(flashSalePo);
        } catch (DataAccessException e) {
            logger.error("Database Error: " + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("Database Error: " + e.getMessage()));
        } catch (Exception e) {
            logger.error("Unknown Error: " + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("Unknown Error: " + e.getMessage()));
        }
    }

    public ReturnObject checkFlashSaleId(FlashSalePo flashSalePo, Byte expectState) {
        if (flashSalePo == null)
            return new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST);
        if (expectState != null && flashSalePo.getState() != expectState)
            return new ReturnObject(ResponseCode.ACTIVITYALTER_INVALID);
        return new ReturnObject(ResponseCode.OK);
    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }
}
