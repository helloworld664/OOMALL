package cn.edu.xmu.groupon.dao;

import cn.edu.xmu.groupon.mapper.GrouponPoMapper;
import cn.edu.xmu.groupon.model.bo.Groupon;
import cn.edu.xmu.groupon.model.po.GrouponPo;
import cn.edu.xmu.groupon.model.po.GrouponPoExample;
import cn.edu.xmu.groupon.model.vo.CreateGrouponVo;
import cn.edu.xmu.groupon.model.vo.GrouponStateVo;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.Common;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ResponseUtil;
import cn.edu.xmu.ooad.util.ReturnObject;
import com.github.pagehelper.ISelect;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * @author XC 3304
 * Created at 2020-12-02 19:27
 * Modified at 2020-12-26 04:06
 */

@Repository
public class GrouponDao implements InitializingBean {
    private final Logger logger = LoggerFactory.getLogger(GrouponDao.class);

    @Autowired
    private GrouponPoMapper grouponPoMapper;

    /**
     * 获得团购活动所有状态
     * @return
     */
    public ReturnObject<List> getAllStates() {
        Groupon.State[] states = Groupon.State.class.getEnumConstants();
        List<GrouponStateVo> grouponStateVoList = new ArrayList<GrouponStateVo>();
        for (Integer i = 0; i < states.length; i++) {
            grouponStateVoList.add(new GrouponStateVo(states[i]));
            logger.debug("state " + i + ": " + states[i]);
        }
        return new ReturnObject<List>(grouponStateVoList);
    }

    /**
     * 查询团购活动
     * @param example
     * @param page
     * @param pageSize
     * @return
     */
    public List<GrouponPo> getExampleGroupons(GrouponPoExample example, Integer page, Integer pageSize) {
        PageHelper.startPage(page, pageSize);
        logger.debug("page = " + page + ", pageSize = " + pageSize);
        List<GrouponPo> list = null;
        try {
            list = grouponPoMapper.selectByExample(example);
        } catch (DataAccessException e) {
            logger.error("Database Error: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Other Error: " + e.getMessage());
        }
        return list;
    }

    /**
     * 查询所有团购活动
     * @param timeline
     * @param spuId
     * @param shopId
     * @param page
     * @param pageSize
     * @return
     */
    public List<GrouponPo> getAllGroupons(Integer timeline, Long spuId, Long shopId, Integer page, Integer pageSize) {
        GrouponPoExample example = new GrouponPoExample();
        GrouponPoExample.Criteria criteria = example.createCriteria();
        if (timeline != null)
            switch (timeline) {
                case 0:
                    criteria.andBeginTimeGreaterThan(LocalDateTime.now());
                    break;
                case 1:
                    LocalDateTime tomorrowBegin = LocalDateTime.of(LocalDate.now(), LocalTime.MIN).minusDays(-1L);
                    LocalDateTime tomorrowEnd = LocalDateTime.of(LocalDate.now(), LocalTime.MAX).minusDays(-1);
                    criteria.andBeginTimeBetween(tomorrowBegin, tomorrowEnd);
                    break;
                case 2:
                    criteria.andBeginTimeLessThanOrEqualTo(LocalDateTime.now());
                    criteria.andEndTimeGreaterThan(LocalDateTime.now());
                    break;
                case 3:
                    criteria.andEndTimeLessThanOrEqualTo(LocalDateTime.now());
                    break;
            }
        if (spuId != null)
            criteria.andGoodsSpuIdEqualTo(spuId);
        if (shopId != null)
            criteria.andShopIdEqualTo(shopId);
        criteria.andStateEqualTo(Groupon.State.ONSHELVES.getCode());
        return getExampleGroupons(example, page, pageSize);
    }

    /**
     * 查询所有状态的团购活动
     * @param id
     * @param beginTime
     * @param endTime
     * @param state
     * @param page
     * @param pageSize
     * @return
     */
    public List<GrouponPo> getAllStateGroupons(Long id, Long spuId, LocalDateTime beginTime, LocalDateTime endTime, Byte state, Integer page, Integer pageSize) {
        GrouponPoExample example = new GrouponPoExample();
        GrouponPoExample.Criteria criteria = example.createCriteria();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime begin = LocalDateTime.parse("1900-01-01 00:00:00", dateTimeFormatter);
        LocalDateTime end = LocalDateTime.parse("2999-12-31 23:59:59", dateTimeFormatter);
        if (id != null)
            criteria.andShopIdEqualTo(id);
        if (spuId != null)
            criteria.andGoodsSpuIdEqualTo(spuId);
        if (beginTime != null)
            criteria.andBeginTimeGreaterThanOrEqualTo(beginTime);
        else
            criteria.andBeginTimeGreaterThanOrEqualTo(begin);
        if (endTime != null)
            criteria.andEndTimeLessThanOrEqualTo(endTime);
        else
            criteria.andEndTimeLessThanOrEqualTo(end);
        if (state != null)
            criteria.andStateEqualTo(state);
        return getExampleGroupons(example, page, pageSize);
    }

    /**
     * 按照团购活动id主键查找
     * @param grouponId
     * @return
     */
    public GrouponPo selectGrouponById(Long grouponId){
        GrouponPo grouponPo = grouponPoMapper.selectByPrimaryKey(grouponId);
        return grouponPo;
    }

    /**
     * 创建团购活动并返回团购活动id
     * @param shopId
     * @param id
     * @param createGrouponVo
     * @return
     */
    public GrouponPo createGrouponById(Long shopId, Long id, CreateGrouponVo createGrouponVo) {
        if (createGrouponVo.getBeginTime().compareTo(LocalDateTime.now()) < 0)
            return null;
        if (createGrouponVo.getEndTime().compareTo(createGrouponVo.getBeginTime()) < 0)
            return null;
        GrouponPo grouponPo = createGrouponVo.createGroupon();
        grouponPo.setShopId(shopId);
        grouponPo.setGoodsSpuId(id);
        grouponPo.setGmtCreate(LocalDateTime.now());
        grouponPo.setState(Groupon.State.OFFSHELVES.getCode());
        grouponPoMapper.insertSelective(grouponPo);
        return grouponPo;
    }

    /**
     * 修改团购活动
     * @param shopId
     * @param id
     * @param createGrouponVo
     * @return
     */
    public GrouponPo updateGroupon(Long shopId, Long id, CreateGrouponVo createGrouponVo) {
        GrouponPo grouponPo = grouponPoMapper.selectByPrimaryKey(id);
        if (createGrouponVo.getBeginTime().compareTo(LocalDateTime.now()) < 0 || createGrouponVo.getEndTime().compareTo(createGrouponVo.getBeginTime()) < 0)
            return null;
        grouponPo.setBeginTime(createGrouponVo.getBeginTime());
        grouponPo.setEndTime(createGrouponVo.getEndTime());
        grouponPo.setStrategy(createGrouponVo.getStrategy());
        grouponPo.setGmtModified(LocalDateTime.now());
        return grouponPo;
    }

    /**
     * 通过修改团购状态实现对团购的逻辑删除、上线、下线
     * @param shopId
     * @param id
     * @param state
     * @return
     */
    public ReturnObject<Object> updateState(Long shopId, Long id, Byte state) {
        GrouponPo grouponPo = grouponPoMapper.selectByPrimaryKey(id);
        if (grouponPo == null) {
            logger.info("Groupon " + id + " is not exist.");
            return new ReturnObject<Object>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        if (grouponPo.getBeginTime().compareTo(LocalDateTime.now()) < 0 && grouponPo.getEndTime().compareTo(LocalDateTime.now()) > 0)
            return new ReturnObject<Object>(ResponseCode.GROUPON_STATENOTALLOW);
        if (grouponPo.getShopId() != shopId)
            return new ReturnObject<Object>(ResponseCode.RESOURCE_ID_NOTEXIST);
        if (grouponPo.getState() != state)
            return new ReturnObject<Object>(ResponseCode.GROUPON_STATENOTALLOW);
        if (state == Groupon.State.ONSHELVES.getCode() && checkIfSPUInGrouponActivity(grouponPo.getGoodsSpuId(), grouponPo.getBeginTime(), grouponPo.getEndTime()).getData() == true)
            return new ReturnObject<Object>(ResponseCode.TIMESEG_CONFLICT);
        grouponPo.setState(state);
        int flag;
        ReturnObject<Object> returnObject = new ReturnObject<Object>(ResponseCode.OK);
        try {
            flag = grouponPoMapper.updateByPrimaryKeySelective(grouponPo);
            if (flag == 0) {
                logger.info("Groupon " + id + " is not exist.");
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

    /**
     * 检查要上线的商品SPU是否已在该时间段中上线活动
     * @param spuId
     * @param beginTime
     * @param endTime
     * @return
     */
    public ReturnObject<Boolean> checkIfSPUInGrouponActivity(Long spuId, LocalDateTime beginTime, LocalDateTime endTime) {
        GrouponPoExample example = new GrouponPoExample();
        GrouponPoExample.Criteria criteria = example.createCriteria();
        criteria.andEndTimeGreaterThan(beginTime);
        criteria.andBeginTimeLessThan(endTime);
        criteria.andGoodsSpuIdEqualTo(spuId);
        criteria.andStateEqualTo(Groupon.State.ONSHELVES.getCode());
        List<GrouponPo> grouponPos = null;
        try {
            grouponPos = grouponPoMapper.selectByExample(example);
        } catch (DataAccessException e) {
            logger.error("Database Error: " + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("Database Error: " + e.getMessage()));
        } catch (Exception e) {
            logger.error("Unknown Error: " + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("Unknown Error: " + e.getMessage()));
        }
        if (grouponPos == null || grouponPos.size() == 0) {
            return new ReturnObject<>(false);
        } else {
            return new ReturnObject<>(true);
        }
    }

    /*
    public ReturnObject<GrouponPo> selectFirtGrouponByShopId(Long shopId){
        GrouponPoExample example = new GrouponPoExample();
        GrouponPoExample.Criteria criteria = example.createCriteria();
        criteria.andShopIdEqualTo(shopId);
        List<GrouponPo> grouponPos = null;
        try {
            grouponPos = grouponPoMapper.selectByExample(example);
        } catch (Exception e){
            return new ReturnObject(ResponseCode.INTERNAL_SERVER_ERR);
        }
        if(grouponPos.size() == 0){
            return new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        return new ReturnObject<GrouponPo>(grouponPos.get(0));
    }
     */

    @Override
    public void afterPropertiesSet() throws Exception {

    }
}
