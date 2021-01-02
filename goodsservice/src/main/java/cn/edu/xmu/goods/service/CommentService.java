package cn.edu.xmu.goods.service;

import cn.edu.xmu.goods.dao.CommentDao;
import cn.edu.xmu.goods.model.bo.Comment;
import cn.edu.xmu.goods.model.bo.Shop;
import cn.edu.xmu.goods.model.vo.ConclusionVo;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CommentService {
    @Autowired
    private CommentDao commentDao;

    private Logger logger = LoggerFactory.getLogger(CommentService.class);

    @Transactional
    public ReturnObject createCommonWithSku(Long orderId, Comment comment){
        ReturnObject returnObject = commentDao.createCommonWithSku(orderId,comment);
        return returnObject;
    }

    public ReturnObject<PageInfo<VoObject>> findAllComments(Long skuId,Integer page,Integer pageSize){
        ReturnObject<PageInfo<VoObject>> returnObject = commentDao.findAllComments(skuId, page, pageSize);
        return returnObject;
    }

    public ReturnObject examineComment(Long did, Long id, ConclusionVo conclusionVo) {
        if (conclusionVo.getConclusion() == true)
            return commentDao.updateState(did, Shop.State.OFFSHELVES.getCode());
        else
            return commentDao.updateState(did, Shop.State.NOT_PASS.getCode());
    }

    @Transactional
    public ReturnObject modifyCommentByManager(Long did,Long id, Comment bo){
        ReturnObject returnObject = commentDao.modifyCommentByManager(did,id,bo);
        return returnObject;
    }

    public ReturnObject<PageInfo<VoObject>> findAllCommentsByCustomer(Integer page, Integer pageSize, Long userId){
        ReturnObject<PageInfo<VoObject>> returnObject = commentDao.findAllCommentsByCustomer( page, pageSize,userId);
        return returnObject;
    }

    public ReturnObject<PageInfo<VoObject>> getCommentListByState(Byte state, Integer page, Integer pageSize) {
        PageInfo<VoObject> retObj=null;
        try {
            return new ReturnObject<>(commentDao.getCommentListByState(state,page,pageSize));
        } catch (Exception e) {
            logger.error("发生了严重的服务器内部错误ha：" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR);
        }
    }
}
