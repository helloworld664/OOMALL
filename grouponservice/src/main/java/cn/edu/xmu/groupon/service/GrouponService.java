package cn.edu.xmu.groupon.service;

import cn.edu.xmu.groupon.dao.GrouponDao;
import cn.edu.xmu.groupon.mapper.GrouponPoMapper;
import cn.edu.xmu.groupon.model.bo.Groupon;
import cn.edu.xmu.groupon.model.po.GrouponPo;
import cn.edu.xmu.groupon.model.vo.CreateGrouponVo;
import cn.edu.xmu.groupon.model.vo.GrouponSimpleRetVo;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.Common;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * @author XC
 */

@Service
public class GrouponService {
    @Autowired
    private GrouponDao grouponDao;

    private final Logger logger = LoggerFactory.getLogger(GrouponService.class);

    @Autowired
    private GrouponPoMapper grouponPoMapper;

    public GrouponService() {

    }

    /**
     * 获得团购活动所有状态
     * @return
     */
    public List getAllStates() {
        ReturnObject<List> listReturnObject = grouponDao.getAllStates();
        return listReturnObject.getData();
    }

    /**
     * 转换返回值类型
     * @param grouponPos
     * @param page
     * @param pageSize
     * @return
     */
    public ReturnObject<PageInfo<VoObject>> transfer(List<GrouponPo> grouponPos, int page, int pageSize) {
        List<VoObject> list = new ArrayList<>(grouponPos.size());
        for (GrouponPo grouponPo:grouponPos) {
            VoObject voObject = new Groupon(grouponPo).createSimpleVo();
            list.add(voObject);
        }
        PageHelper.startPage(page, pageSize);
        PageInfo<VoObject> pageInfo = PageInfo.of(list);
        return new ReturnObject<PageInfo<VoObject>>(pageInfo);
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
    public ReturnObject<PageInfo<VoObject>> getAllGroupons(Integer timeline, Long spuId, Long shopId, Integer page, Integer pageSize) {
        //ReturnObject<GrouponPo> returnObject = grouponDao.getAllGroupons(timeline, spuId, shopId, page, pageSize);
        List<GrouponPo> list = grouponDao.getAllGroupons(timeline, shopId, spuId, page, pageSize);
        return transfer(list, page, pageSize);
    }

    /**
     * 查询所有团购活动（包括下线的、删除的）
     * @param id
     * @param spuId
     * @param beginTime
     * @param endTime
     * @param state
     * @param page
     * @param pageSize
     * @return
     */
    public ReturnObject<PageInfo<VoObject>> getAllStateGroupons(Long id, Long spuId, String beginTime, String endTime, Byte state, Integer page, Integer pageSize) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime begin = null;
        LocalDateTime end = null;
        if (beginTime != null && endTime != null) {
            begin = LocalDateTime.parse(beginTime, dateTimeFormatter);
            end = LocalDateTime.parse(endTime, dateTimeFormatter);
        }
        List<GrouponPo> list = grouponDao.getAllStateGroupons(id, spuId, begin, end, state, page, pageSize);
        return transfer(list, page, pageSize);
    }

    /**
     * 新建团购活动
     * @param shopId
     * @param id
     * @param createGrouponVo
     * @return
     */
    public ReturnObject createGroupon(Long shopId, Long id, CreateGrouponVo createGrouponVo) {
        GrouponPo grouponPo = grouponDao.createGrouponById(shopId, id, createGrouponVo);
        if (grouponPo == null)
            return null;
        try {
            if (grouponPo != null)
                return new ReturnObject(new Groupon(grouponPo).createVo());
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
     * 修改团购活动
     * @param shopId
     * @param id
     * @param createGrouponVo
     * @return
     */
    public ReturnObject updateGroupon(Long shopId, Long id, CreateGrouponVo createGrouponVo) {
        try {
            if (check(id) == false)
                return new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST);
            GrouponPo grouponPo = grouponDao.selectGrouponById(id);
            if (grouponPo.getBeginTime().compareTo(LocalDateTime.now()) < 0 && grouponPo.getEndTime().compareTo(LocalDateTime.now()) > 0)
                return new ReturnObject(ResponseCode.GROUPON_STATENOTALLOW);
            grouponPo = grouponDao.updateGroupon(shopId, id, createGrouponVo);
            if (grouponPo == null)
                return null;
            grouponPoMapper.updateByPrimaryKeySelective(grouponPo);
            return new ReturnObject<>(new Groupon(grouponPo).createVo());
        } catch (DataAccessException e) {
            logger.error("Database Error: " + e.getMessage());
            return new ReturnObject(ResponseCode.INTERNAL_SERVER_ERR, String.format("Database Error: " + e.getMessage()));
        } catch (Exception e) {
            logger.error("Unknown Error: " + e.getMessage());
            return new ReturnObject(ResponseCode.INTERNAL_SERVER_ERR, String.format("Unknown Error: " + e.getMessage()));
        }
    }

    /**
     * 逻辑删除团购活动
     * @param shopId
     * @param id
     * @return
     */
    public ReturnObject deleteGroupon(Long shopId, Long id) {
        return grouponDao.updateState(shopId, id, Groupon.State.DELETED.getCode());
    }

    /**
     * 上线团购活动
     * @param shopId
     * @param id
     * @return
     */
    public ReturnObject grouponOnShelves(Long shopId, Long id) {
        return grouponDao.updateState(shopId, id, Groupon.State.ONSHELVES.getCode());
    }

    /**
     * 下线团购活动
     * @param shopId
     * @param id
     * @return
     */
    public ReturnObject grouponOffShelves(Long shopId, Long id) {
        return grouponDao.updateState(shopId, id, Groupon.State.OFFSHELVES.getCode());
    }

    private Boolean check(Long id) {
        GrouponPo grouponPo = grouponDao.selectGrouponById(id);
        if (grouponPo == null)
            return false;
        else
            return true;
    }

    /*
    public ReturnObject<GrouponPo> selectGrouponByShopId(Long shopId){
        ReturnObject<GrouponPo> returnObject = grouponDao.selectFirtGrouponByShopId(shopId);
        if(returnObject.getCode() != ResponseCode.OK){
            return new ReturnObject(returnObject.getCode(),returnObject.getErrmsg());
        }
        GrouponPo data = returnObject.getData();
        ///  TODO....
        return returnObject;


    }
     */

}
