package cn.edu.xmu.presale.service;

import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ResponseUtil;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.presale.dao.PresaleDao;
import cn.edu.xmu.presale.mapper.PresalePoMapper;
import cn.edu.xmu.presale.model.bo.Presale;
import cn.edu.xmu.presale.model.po.PresalePo;
import cn.edu.xmu.presale.model.po.PresalePoExample;
import cn.edu.xmu.presale.model.vo.CreatePresaleVo;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.repository.reactive.RxJava2CrudRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author XC 3304
 * Created at 2020-12-06 08:46
 * Modified at 2020-12-27 07:10
 */

@Service
public class PresaleService {
    private final Logger logger = LoggerFactory.getLogger(PresaleService.class);

    @Autowired
    private PresaleDao presaleDao;

    @Autowired
    private PresalePoMapper presalePoMapper;

    public PresaleService() {

    }

    /**
     * 获得预售活动所有状态
     * @return
     */
    public List getAllStates() {
        ReturnObject<List> listReturnObject = presaleDao.getAllState();
        return listReturnObject.getData();
    }

    /**
     * 转换返回值对象
     * @param presalePos
     * @param page
     * @param pageSize
     * @return
     */
    public ReturnObject<PageInfo<VoObject>> transfer(List<PresalePo> presalePos, int page, int pageSize) {
        List<VoObject> list = new ArrayList<>(presalePos.size());
        for (PresalePo presalePo:presalePos) {
            VoObject voObject = (VoObject) new Presale(presalePo).createSimpleVo();
            list.add(voObject);
        }
        PageHelper.startPage(page, pageSize);
        PageInfo<VoObject> pageInfo = PageInfo.of(list);
        return new ReturnObject<PageInfo<VoObject>>(pageInfo);
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
        ReturnObject<PageInfo<VoObject>> returnObject = presaleDao.getAllValidPresale(shopId, timeline, skuId, page, pageSize);
        return returnObject;
    }

    /**
     * 查询SPU的所有预售活动
     * @param shopId
     * @param skuId
     * @param state
     * @return
     */
    public ReturnObject<List> getAllPresaleBySKUId(Long shopId, Long skuId, Byte state) {
        ReturnObject<List> returnObject = presaleDao.getAllPresaleBySKUId(shopId, skuId, state);
        if (returnObject == null)
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR);
        return returnObject;
    }

    ///此处还需调用商品部分和商铺部分
    /**
     * 新增SKU预售活动
     * @param shopId
     * @param id
     * @param createPresaleVo
     * @return
     */
    public ReturnObject createPresale(Long shopId, Long id, CreatePresaleVo createPresaleVo) {
        if (createPresaleVo.getBeginTime().compareTo(LocalDateTime.now()) < 0 || createPresaleVo.getEndTime().compareTo(createPresaleVo.getBeginTime()) <= 0
                || createPresaleVo.getPayTime().compareTo(createPresaleVo.getBeginTime()) < 0 || createPresaleVo.getPayTime().compareTo(createPresaleVo.getEndTime()) > 0)
            return new ReturnObject(ResponseCode.FIELD_NOTVALID);
        if (presaleDao.checkIfSKUInActivities(id, createPresaleVo.getBeginTime(), createPresaleVo.getEndTime()).getData() == true)
            return new ReturnObject(ResponseCode.PRESALE_STATENOTALLOW);
        PresalePo presalePo = presaleDao.createPresale(shopId, id, createPresaleVo);
        if (presalePo == null)
            return new ReturnObject(ResponseCode.INTERNAL_SERVER_ERR);
        return new ReturnObject(presalePo);
    }

    /**
     * 修改SKU预售活动
     * @param shopId
     * @param id
     * @param createPresaleVo
     * @return
     */
    public ReturnObject updatePresale(Long shopId, Long id, CreatePresaleVo createPresaleVo) {
        ReturnObject<PresalePo> presalePoReturnObject = presaleDao.updatePresale(shopId, id, createPresaleVo);
        //PresalePo presalePo = presalePoReturnObject.getData();
        //ReturnObject returnObject = presaleDao.checkIfCanModifyState(presalePo, shopId, Presale.State.OFFSHELVES.getCode());
/*        if (returnObject.getCode() != ResponseCode.OK)
            return returnObject;*/
        return presalePoReturnObject.getCode()==ResponseCode.OK?new ReturnObject(presalePoReturnObject.getData())
                :new ReturnObject(presalePoReturnObject.getCode(),presalePoReturnObject.getErrmsg());
    }

    /**
     * 管理员逻辑删除预售活动
     * @param shopId
     * @param id
     * @return
     */
    public ReturnObject deletePresale(Long shopId, Long id) {
        ReturnObject returnObject = presaleDao.updateState(shopId, id, Presale.State.DELETED.getCode());
        if(returnObject.getCode() == ResponseCode.OK){
            return new ReturnObject(returnObject.getData());
        }else{
            return new ReturnObject(returnObject.getCode(),returnObject.getErrmsg());
        }
    }

    /**
     * 管理员上线预售活动
     * @param shopId
     * @param id
     * @return
     */
    public ReturnObject presaleOnShelves(Long shopId, Long id) {
        return presaleDao.updateState(shopId, id, Presale.State.ONSHELVES.getCode());
    }

    /**
     * 管理员下线预售活动
     * @param shopId
     * @param id
     * @return
     */
    public ReturnObject presaleOffShelves(Long shopId, Long id) {
        return presaleDao.updateState(shopId, id, Presale.State.OFFSHELVES.getCode());
    }
}
