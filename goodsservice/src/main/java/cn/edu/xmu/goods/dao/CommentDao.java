package cn.edu.xmu.goods.dao;

import cn.edu.xmu.goods.mapper.CommentPoMapper;
import cn.edu.xmu.goods.mapper.OrdersPoMapper;
import cn.edu.xmu.goods.model.bo.Shop;
import cn.edu.xmu.goods.model.po.CommentPo;
import cn.edu.xmu.goods.model.po.OrdersPo;
import cn.edu.xmu.goods.model.po.ShopPo;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ResponseUtil;
import cn.edu.xmu.ooad.util.ReturnObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import cn.edu.xmu.goods.model.bo.Comment;
import cn.edu.xmu.goods.model.po.CommentPoExample;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class CommentDao implements InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(Comment.class);

    @Autowired
    private CommentPoMapper commentPoMapper;

    @Autowired
    private OrdersPoMapper orderPoMapper;

    public ReturnObject createCommonWithSku(Long orderId, Comment comment){
        CommentPo commentPo=comment.createCommentPo();
        ReturnObject returnObject;
        OrdersPo orderPo=orderPoMapper.selectByPrimaryKey(orderId);
        try{

            if(orderPo.getCustomerId()!=comment.getCustomerId()){ //用户没有购买此商品
                 returnObject = new ReturnObject<>(ResponseCode.USER_NOTBUY, String.format("用户未购买该商品：" +orderPo.getId()));
            }
            else{
                int ret =commentPoMapper.insertSelective(commentPo);
                returnObject =new ReturnObject() ;
            }

        }
        catch (DataAccessException e) {
            // 其他数据库错误
            logger.debug("other sql exception : " + e.getMessage());
            returnObject= new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        }
        catch (Exception e) {
            // 其他Exception错误
            logger.error("other exception : " + e.getMessage());
            returnObject= new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误：%s", e.getMessage()));
        }
        return returnObject;

    }

    public ReturnObject<PageInfo<VoObject>> findAllComments(Long skuId,Integer page,Integer pageSize) {
        CommentPoExample example = new CommentPoExample();
        CommentPoExample.Criteria criteria = example.createCriteria();
        criteria.andGoodsSkuIdEqualTo(skuId);
        List<CommentPo> commentPos = null;
        PageHelper.startPage(page, pageSize);
        logger.debug("page = " + page + "pageSize = " + pageSize);
        try {
            //不加限定条件查询所有
            commentPos = commentPoMapper.selectByExample(example);
            List<VoObject> ret = new ArrayList<>(commentPos.size());
            for (CommentPo po : commentPos) {
                Comment comment = new Comment(po);
                ret.add(comment);
            }
            PageInfo<VoObject> commentPage = PageInfo.of(ret);
            return new ReturnObject<>(commentPage);
        } catch (DataAccessException e) {
            logger.error("findAllComments: DataAccessException:" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        } catch (Exception e) {
            // 其他Exception错误
            logger.error("other exception : " + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误：%s", e.getMessage()));
        }
    }

    public ReturnObject modifyCommentByManager(Long did,Long id, Comment bo){
        CommentPo commentPo = commentPoMapper.selectByPrimaryKey(id);
        commentPo.setGmtModified(LocalDateTime.now());
        return new ReturnObject();
    }

    public ReturnObject<Object> updateState(Long shopId, Byte state) {
        CommentPo commentPo = commentPoMapper.selectByPrimaryKey(shopId);
        if (commentPo == null) {
            logger.info("Groupon " + shopId + " is not exist.");
            return new ReturnObject<Object>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        if (commentPo.getId() != shopId)
            return new ReturnObject<Object>(ResponseCode.RESOURCE_ID_NOTEXIST);
        if (commentPo.getState() != state)
            return new ReturnObject<Object>(ResponseCode.GROUPON_STATENOTALLOW);
        if (state == Shop.State.ONSHELVES.getCode())
            return new ReturnObject<Object>(ResponseCode.TIMESEG_CONFLICT);
        commentPo.setState(state);
        int flag;
        ReturnObject<Object> returnObject = new ReturnObject<Object>(ResponseCode.OK);
        try {
            flag = commentPoMapper.updateByPrimaryKeySelective(commentPo);
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

    public ReturnObject<PageInfo<VoObject>> findAllCommentsByCustomer(Integer page, Integer pageSize, Long userId){
        CommentPoExample example = new CommentPoExample();
        CommentPoExample.Criteria criteria = example.createCriteria();
        criteria.andCustomerIdEqualTo(userId);
        List<CommentPo> commentPos = null ;
        //分页查询
        PageHelper.startPage(page, pageSize);
        logger.debug("page = " + page + "pageSize = " + pageSize);
        try {
            //不加限定条件查询所有
            commentPos = commentPoMapper.selectByExample(example);
            List<VoObject> ret = new ArrayList<>(commentPos.size());
            for (CommentPo po : commentPos) {
                Comment comment = new Comment(po);
                ret.add(comment);
            }
            PageInfo<VoObject> commentPage = PageInfo.of(ret);
            return new ReturnObject<>(commentPage);
        }
        catch (DataAccessException e){
            logger.error("findAllBrandsByUser: DataAccessException:" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        }
        catch (Exception e) {
            // 其他Exception错误
            logger.error("other exception : " + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误：%s", e.getMessage()));
        }
    }

    public PageInfo<VoObject> getCommentListByState(Byte state,Integer page,Integer pageSize){
        PageHelper.startPage(page,pageSize);
        CommentPoExample example=new CommentPoExample();
        CommentPoExample.Criteria criteria=example.createCriteria();
        criteria.andStateEqualTo(state.byteValue());
        List<CommentPo> commentPos=commentPoMapper.selectByExample(example);
        List<VoObject> commmentVos=commentPos.stream().map(po->new Comment(po).createSimpleVo()).collect(Collectors.toList());
        return new PageInfo<>(commmentVos);
    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }
}
