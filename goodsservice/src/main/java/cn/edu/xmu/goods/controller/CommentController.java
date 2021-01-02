package cn.edu.xmu.goods.controller;

import cn.edu.xmu.goods.model.bo.Comment;
import cn.edu.xmu.goods.model.vo.CommentVo;
import cn.edu.xmu.goods.model.vo.ConclusionVo;
import cn.edu.xmu.goods.util.CommentStatus;
import cn.edu.xmu.ooad.annotation.Audit;
import cn.edu.xmu.ooad.annotation.Depart;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.Common;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ResponseUtil;
import cn.edu.xmu.ooad.util.ReturnObject;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import cn.edu.xmu.goods.model.vo.CommentStatusVo;
import cn.edu.xmu.goods.service.CommentService;
import springfox.documentation.annotations.ApiIgnore;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletResponse;

@Api(value = "评论服务", tags = "comment")
@RestController /*Restful的Controller对象*/
@RequestMapping(value = "", produces = "application/json;charset=UTF-8")
@Slf4j

public class CommentController {
    private static final Logger logger = LoggerFactory.getLogger(CommentController.class);

    @Autowired
    private CommentService commentService;

    @Autowired
    private HttpServletResponse httpServletResponse;

    /**
    * 获得评论所有状态
    * createdBy hxy
    * @return Object
    *
    */
    @ApiOperation(value = "获得评论的所有状态", produces = "application/json")
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")
    })
    @GetMapping("/comments/states")
    public Object listStates(){
        CommentStatus[] commentStatuses=CommentStatus.class.getEnumConstants();
        List<CommentStatusVo> commentStatusVos=new ArrayList<CommentStatusVo>();
        for(int i=0;i<commentStatuses.length;i++){
            commentStatusVos.add(new CommentStatusVo(commentStatuses[i]));
        }
        logger.debug(commentStatusVos.toString());
        return ResponseUtil.ok(new ReturnObject<List>(commentStatusVos).getData());
    }

    /**
     * 买家新增sku的评论
     *
     *
     */
    @ApiOperation(value = "买家新增sku的评论", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "id", value = "订单明细id", required = true),
            @ApiImplicitParam(paramType = "body", dataType = "CommentModelVo", name = "body", value = "评价信息", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 903, message = "用户没有购买此商品")
    })
    @Audit
    @PostMapping("/orderitems/{id}/comments")
    @ResponseBody
    public Object insertSKUCommentByOrder(@Depart Long orderId,
                                          @Validated @RequestBody CommentVo vo,
                                          @ApiIgnore @RequestParam(required = false) Long userId,
                                          BindingResult bindingResult) {
        logger.debug("order id = " + orderId + ", vo = " + vo +", user id="+userId);
        Object returnObject = Common.processFieldErrors(bindingResult, httpServletResponse);
        if(userId == null){
            returnObject = Common.getNullRetObj(new ReturnObject<>(ResponseCode.AUTH_NEED_LOGIN), httpServletResponse);
            return returnObject;
        }
        if (null != returnObject) {
            return returnObject;
        }
        Comment comment = vo.createComment();
        comment.setCustomerId(userId);
        comment.setGmtCreate(LocalDateTime.now());
        comment.setState(Comment.State.NOT_EXAMINE.getCode());
        ReturnObject retObject = commentService.createCommonWithSku(orderId,comment);
        if (retObject.getData() != null) {
            if (!retObject.getCode().equals(ResponseCode.USER_NOTBUY)) {
                httpServletResponse.setStatus(HttpStatus.OK.value());
                return Common.getPageRetObject(retObject);
            }
            else{
                return Common.getNullRetObj(new ReturnObject<>(retObject.getCode(), retObject.getErrmsg()), httpServletResponse);
            }
        } else {
            return Common.getNullRetObj(new ReturnObject<>(retObject.getCode(), retObject.getErrmsg()), httpServletResponse);
        }
    }


    /**
     *查看sku的评论列表
     *
     *
     *
     */
    @ApiOperation(value = "查看sku的评论列表", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "id", value = "skuID", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "page", value = "页码",required = false),
            @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "pageSize", value = "每页数目",required = false)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")
    })
    @GetMapping("/skus/{id}/comments")
    public Object QuerySKUCommentList(@PathVariable("id") Long skuId,
                                      @RequestParam(required = false, defaultValue = "1") Integer page,
                                      @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        logger.debug("skuId = " + skuId + ", page = "+ page +", pageSize ="+pageSize);
        ReturnObject<PageInfo<VoObject>> retObject = commentService.findAllComments(skuId, page, pageSize);
        if (!retObject.getCode().equals(ResponseCode.RESOURCE_ID_NOTEXIST)) {
            httpServletResponse.setStatus(HttpStatus.OK.value());
            return Common.getPageRetObject(retObject);
        } else {
            return Common.getNullRetObj(new ReturnObject<>(retObject.getCode(), retObject.getErrmsg()), httpServletResponse);
        }

    }

    /**
     * 管理员审核评论
     *
     */
    @ApiOperation(value = "管理员审核评论", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "did", value = "店铺ID",required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "id", value = "评论ID",required = true),
            @ApiImplicitParam(paramType = "body", dataType = "CommentModelVo", name = "body", value = "可修改的评论信息",required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")
    })
    @PutMapping("/shops/{did}/comments/{id}/confirm")
    @ResponseBody
    public Object ExamineCommentByManager(@PathVariable("did") Long did,
                                          @PathVariable("id") Long id,
                                          @Validated @RequestBody ConclusionVo vo,
                                          BindingResult bindingResult){
        ReturnObject returnObject = commentService.examineComment(did, id, vo);
        if (returnObject.getCode().equals(ResponseCode.OK))
            return ResponseUtil.ok();
        return Common.decorateReturnObject(returnObject);

    }

    /**
     * 买家查看自己的评价记录
     *
     */
    @ApiOperation(value = "买家查看自己的评论记录", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "page", value = "页码",required = false),
            @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "pageSize", value = "每页数目",required = false)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")
    })
    @GetMapping("/comments")
    public Object QueryOwnCommentsList(@RequestParam(required = false, defaultValue = "1") Integer page,
                                       @RequestParam(required = false, defaultValue = "10") Integer pageSize,
                                       @ApiIgnore @RequestParam(required = false) Long userId) {
        logger.debug("page = " + page + ", pageSize = "+ pageSize +", userId ="+userId);
        ReturnObject<PageInfo<VoObject>> retObject = commentService.findAllCommentsByCustomer(page,pageSize,userId);
        if (!retObject.getCode().equals(ResponseCode.RESOURCE_ID_NOTEXIST)) {
            httpServletResponse.setStatus(HttpStatus.OK.value());
            return Common.getPageRetObject(retObject);
        } else {
            return Common.getNullRetObj(new ReturnObject<>(retObject.getCode(), retObject.getErrmsg()), httpServletResponse);
        }
    }

    /**
     * 管理员查看未审核/已审核评论列表
     */
    @ApiOperation(value = "管理员查看未审核/已审核评论列表", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "id", value = "店铺ID",required = true),
            @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "state", value = "评论状态",required = false),
            @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "page", value = "页码",required = false),
            @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "pageSize", value = "每页数目",required = false)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")
    })
    @Audit
    @GetMapping("/shops/{id}/comments/all")
    public Object QueryCommentsStateList(@PathVariable("did") Long did,
                                         @RequestParam(required = false) Byte state,
                                         @RequestParam(required = false, defaultValue = "1") Integer page,
                                         @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        if(did.equals(0)){
            page = (page == null) ? 1 : page;
            pageSize = (pageSize == null) ? 10 : pageSize;
            ReturnObject<PageInfo<VoObject>> returnObject = commentService.getCommentListByState(state,page,pageSize);
            if (returnObject.getData() != null) {
                return ResponseUtil.ok(returnObject.getData());
            } else {
                return Common.getNullRetObj(new ReturnObject<>(returnObject.getCode(), returnObject.getErrmsg()), httpServletResponse);
            }
        }
        else {
            return ResponseCode.AUTH_NOT_ALLOW;
        }
    }

}





