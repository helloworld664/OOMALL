package cn.edu.xmu.presale.dao;

import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ResponseUtil;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.presale.mapper.PresalePoMapper;
import cn.edu.xmu.presale.model.bo.Presale;
import cn.edu.xmu.presale.model.po.PresalePo;
import cn.edu.xmu.presale.model.po.PresalePoExample;
import cn.edu.xmu.presale.model.vo.CreatePresaleVo;
import cn.edu.xmu.presale.model.vo.PresaleStateVo;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author XC 3304
 * Created at 2020-12-06 08:47
 * Modified at 2020-12-27 11:51
 */

@Repository
public class PresaleDao implements InitializingBean {
    private Logger logger = LoggerFactory.getLogger(PresaleDao.class);

    @Autowired
    private PresalePoMapper presalePoMapper;

    /**
     * 获得预售活动所有状态
     * @return
     */
    public ReturnObject<List> getAllState() {
        Presale.State[] states = Presale.State.class.getEnumConstants();
        List<PresaleStateVo> presaleStateVoList = new ArrayList<PresaleStateVo>();
        for (int i = 0; i < states.length; i++) {
            presaleStateVoList.add(new PresaleStateVo(states[i]));
            logger.debug("state " + i + ": " + states[i]);
        }
        return new ReturnObject<List>(presaleStateVoList);
    }

    /**
     * 查询预售活动
     * @param presalePoExample
     * @param page
     * @param pageSize
     * @return
     */
    public List<PresalePo> getExamplePresale(PresalePoExample presalePoExample, Integer page, Integer pageSize) {
        PageHelper.startPage(page, pageSize);
        logger.debug("page = " + page + ", pageSize = " + pageSize);
        List<PresalePo> presalePos = null;
        try {
            presalePos = presalePoMapper.selectByExample(presalePoExample);
        } catch (DataAccessException e) {
            logger.error("Database Error: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Unknown Error: " + e.getMessage());
        }
        return presalePos;
    }

    /**
     * 查询有效的预售活动
     * @param shopId
     * @param timeline
     * @param skuId
     * @param page
     * @param pageSize
     * @return
     */
    public ReturnObject<PageInfo<VoObject>> getAllValidPresale(Long shopId, Integer timeline, Long skuId, Integer page, Integer pageSize) {
        PresalePoExample presalePoExample = new PresalePoExample();
        PresalePoExample.Criteria criteria = presalePoExample.createCriteria();
        //Byte state = Presale.State.ONSHELVES.getCode();
        //List<PresalePo> presalePo = presalePoMapper.selectByExample(presalePoExample);
        LocalDateTime tomorrow;
        if (timeline != null)
            switch (timeline) {
                case 0:
                    criteria.andBeginTimeGreaterThan(LocalDateTime.now());
                    break;
                case 1:
                    tomorrow = LocalDateTime.of(LocalDateTime.now().toLocalDate(), LocalTime.MIN).minusDays(-1);
                    criteria.andBeginTimeBetween(tomorrow, tomorrow.minusDays(-1));
                    break;
                case 2:
                    criteria.andBeginTimeLessThanOrEqualTo(LocalDateTime.now());
                    criteria.andEndTimeGreaterThan(LocalDateTime.now());
                    break;
                case 3:
                    criteria.andEndTimeLessThanOrEqualTo(LocalDateTime.now());
                    break;
                default:
                    return new ReturnObject(ResponseCode.FIELD_NOTVALID);
            }
        if (shopId != null)
            criteria.andShopIdEqualTo(shopId);
        if (skuId != null)
            criteria.andGoodsSkuIdEqualTo(skuId);
        criteria.andStateEqualTo(Presale.State.ONSHELVES.getCode());
        PageHelper.startPage(page, pageSize);
        logger.debug("page = " + page + ", pageSize = " + pageSize);
        try {
            List<PresalePo> presalePos = presalePoMapper.selectByExample(presalePoExample);
            return new ReturnObject(PageInfo.of(presalePos));
        } catch (DataAccessException e) {
            logger.error("Database Error: " + e.getMessage());
            return new ReturnObject(ResponseCode.INTERNAL_SERVER_ERR, String.format("Database Error: " + e.getMessage()));
        } catch (Exception e) {
            logger.error("Unknown Error: " + e.getMessage());
            return new ReturnObject(ResponseCode.INTERNAL_SERVER_ERR, String.format("Unknown Error: " + e.getMessage()));
        }
    }

    /**
     * 查询SPU的所有预售活动
     * @param shopId
     * @param skuId
     * @param state
     * @return
     */
    public ReturnObject<List> getAllPresaleBySKUId(Long shopId, Long skuId, Byte state) {
        PresalePoExample presalePoExample = new PresalePoExample();
        PresalePoExample.Criteria criteria = presalePoExample.createCriteria();
        criteria.andShopIdEqualTo(shopId);
        criteria.andStateNotEqualTo(Presale.State.DELETED.getCode());
        if (shopId != null)
            criteria.andShopIdEqualTo(shopId);
        if (skuId != null)
            criteria.andGoodsSkuIdEqualTo(skuId);
        if (state != null)
            criteria.andStateEqualTo(state);
        try {
            List<PresalePo> presalePos = presalePoMapper.selectByExample(presalePoExample);
            ReturnObject<List> returnObject = new ReturnObject<List>(presalePos);
            return returnObject;
        } catch (DataAccessException e) {
            logger.debug("Database Error: " + e.getMessage());
            return null;
        } catch (Exception e) {
            logger.debug("Unknown Error: " + e.getMessage());
            return null;
        }
    }

    /**
     * 管理员新增SKU预售活动
     * @param shopId
     * @param id
     * @param createPresaleVo
     * @return
     */
    public PresalePo createPresale(Long shopId, Long id, CreatePresaleVo createPresaleVo) {
        PresalePo presalePo = new PresalePo();
        try {
            createPresaleVo.createPresale();
            presalePo.setGoodsSkuId(id);
            presalePo.setShopId(shopId);
            presalePo.setState(Presale.State.OFFSHELVES.getCode());
            presalePo.setGmtCreate(LocalDateTime.now());
            presalePoMapper.insert(presalePo);
            return presalePoMapper.selectByPrimaryKey(presalePo.getId());
        } catch (DataAccessException e) {
            logger.debug("Database Error: " + e.getMessage());
            return null;
        } catch (Exception e) {
            logger.debug("Unknown Error: " + e.getMessage());
            return null;
        }
    }

    /**
     * 修改SKU预售活动
     * @param shopId
     * @param id
     * @param createPresaleVo
     * @return
     */
    public ReturnObject<PresalePo> updatePresale(Long shopId, Long id, CreatePresaleVo createPresaleVo) {
        ReturnObject returnObject = checkIfCanModifyState(presalePoMapper.selectByPrimaryKey(id), shopId, Presale.State.OFFSHELVES.getCode());
        if(returnObject.getCode() != ResponseCode.OK){
            return new ReturnObject<>(returnObject.getCode(),returnObject.getErrmsg());
        }

        PresalePo presalePo = presalePoMapper.selectByPrimaryKey(id);
        try {
            if (presalePo == null) {
                logger.info("Presale " + id + " is not exist.");
                return new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST);
            }
            if (presalePo.getBeginTime().compareTo(LocalDateTime.now()) < 0 && presalePo.getEndTime().compareTo(LocalDateTime.now()) > 0)
                return new ReturnObject(ResponseCode.PRESALE_STATENOTALLOW);
            if (createPresaleVo.getBeginTime().compareTo(LocalDateTime.now()) < 0 || createPresaleVo.getEndTime().compareTo(createPresaleVo.getBeginTime()) < 0
                    || createPresaleVo.getPayTime().compareTo(createPresaleVo.getBeginTime()) < 0 || createPresaleVo.getPayTime().compareTo(createPresaleVo.getEndTime()) > 0)
                return new ReturnObject<>(ResponseCode.FIELD_NOTVALID);
            presalePo = createPresaleVo.createPresale();
            presalePo.setGmtModified(LocalDateTime.now());
            presalePoMapper.updateByPrimaryKeySelective(presalePo);
            return new ReturnObject(presalePo);
        } catch (DataAccessException e) {
            logger.debug("Database Error: " + e.getMessage());
            return new ReturnObject(ResponseCode.INTERNAL_SERVER_ERR);
        } catch (Exception e) {
            logger.debug("Unknown Error: " + e.getMessage());
            return new ReturnObject(ResponseCode.INTERNAL_SERVER_ERR);
        }
    }

    /**
     * 通过修改团购状态实现对预售的逻辑删除、上线、下线
     * @param shopId
     * @param id
     * @param state
     * @return
     */
    public ReturnObject updateState(Long shopId, Long id, Byte state) {
        PresalePo presalePo = presalePoMapper.selectByPrimaryKey(id);
        if (presalePo == null) {
            logger.info("Presale " + id + " is not exist or deleted.");
            return new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        if (presalePo.getBeginTime().compareTo(LocalDateTime.now()) < 0 && presalePo.getEndTime().compareTo(LocalDateTime.now()) > 0) {
            logger.info("Presale is in progress.");
            return new ReturnObject(ResponseCode.PRESALE_STATENOTALLOW);
        }
        Byte stateExpected;
        if (state == Presale.State.DELETED.getCode() || state == Presale.State.ONSHELVES.getCode())
            stateExpected = Presale.State.OFFSHELVES.getCode();
        else
            stateExpected = Presale.State.ONSHELVES.getCode();
        ReturnObject checkState = checkIfCanModifyState(presalePo, shopId, stateExpected);
        if (checkState.getCode() != ResponseCode.OK)
            return new ReturnObject(checkState.getCode(),checkState.getErrmsg());
        presalePo.setState(state);
        int flag;
        ReturnObject returnObject = new ReturnObject(ResponseCode.OK);
        try {
            if (state == Presale.State.ONSHELVES.getCode()) {
                ReturnObject<Boolean> booleanReturnObject = checkIfSKUInActivities(presalePo.getGoodsSkuId(), presalePo.getBeginTime(), presalePo.getEndTime());
                if (booleanReturnObject.getCode() != ResponseCode.OK || booleanReturnObject.getData().equals(false))
                    return new ReturnObject(ResponseCode.PRESALE_STATENOTALLOW);
            }
            flag = presalePoMapper.updateByPrimaryKeySelective(presalePo);
            if (flag == 0) {
                logger.info("Presale " + id + " is not exist.");
                return new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST);
            }
            //return new ReturnObject(ResponseUtil.ok());
        } catch (DataAccessException e) {
            logger.error("Database Error: " + e.getMessage());
            return new ReturnObject(ResponseCode.INTERNAL_SERVER_ERR, String.format("Database Error: " + e.getMessage()));
        } catch (Exception e) {
            logger.error("Unknown Error: " + e.getMessage());
            return new ReturnObject(ResponseCode.INTERNAL_SERVER_ERR, String.format("Unknown Error: " + e.getMessage()));
        }
        return returnObject;
    }

    public ReturnObject checkIfCanModifyState(PresalePo presalePo, Long shopId, Byte stateExpected) {

        if (presalePo == null)
            return new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST);
        if (!presalePo.getShopId().equals(shopId))
            return new ReturnObject(ResponseCode.RESOURCE_ID_OUTSCOPE);

        if (!presalePo.getState().equals(stateExpected))
            return new ReturnObject(ResponseCode.PRESALE_STATENOTALLOW);


        return new ReturnObject(ResponseCode.OK);
    }

    /**
     * 检查商品SKU是否在该时段参加了活动
     * @param goodsSkuId
     * @param beginTime
     * @param endTime
     * @return
     */
    public ReturnObject<Boolean> checkIfSKUInActivities(Long goodsSkuId, LocalDateTime beginTime, LocalDateTime endTime) {
        PresalePoExample example = new PresalePoExample();
        PresalePoExample.Criteria criteria1 = example.createCriteria();

        criteria1.andEndTimeGreaterThan(beginTime);
        criteria1.andBeginTimeLessThan(endTime);
        criteria1.andGoodsSkuIdEqualTo(goodsSkuId);
        criteria1.andStateEqualTo(Presale.State.ONSHELVES.getCode());
        List<PresalePo> presalePos = null;
        try {
            presalePos = presalePoMapper.selectByExample(example);
        } catch (DataAccessException e) {
            logger.debug("Database Error: " + e.getMessage());
            return new ReturnObject(ResponseCode.INTERNAL_SERVER_ERR, ResponseCode.INTERNAL_SERVER_ERR.getMessage());
        } catch (Exception e) {
            logger.debug("Unknown Error: " + e.getMessage());
            return new ReturnObject(ResponseCode.INTERNAL_SERVER_ERR, ResponseCode.INTERNAL_SERVER_ERR.getMessage());
        }
        if (presalePos.size() == 0)
            return new ReturnObject<Boolean>(false);
        else
            return new ReturnObject<Boolean>(true);
    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }
}
