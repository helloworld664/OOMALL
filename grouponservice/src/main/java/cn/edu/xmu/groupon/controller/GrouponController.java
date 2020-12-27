package cn.edu.xmu.groupon.controller;

import cn.edu.xmu.groupon.model.bo.Groupon;
import cn.edu.xmu.groupon.model.po.GrouponPo;
import cn.edu.xmu.groupon.model.vo.CreateGrouponVo;
import cn.edu.xmu.groupon.model.vo.GrouponRetVo;
import cn.edu.xmu.groupon.model.vo.GrouponSimpleRetVo;
import cn.edu.xmu.groupon.model.vo.GrouponStateVo;
import cn.edu.xmu.groupon.service.GrouponService;
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
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author XC
 */

@Api(value = "团购服务", tags = "groupon")
@RestController
@RequestMapping(value = "groupon", produces = "application/json;charset=UTF-8")
@Slf4j

public class GrouponController {
    private static final Logger logger = LoggerFactory.getLogger(GrouponController.class);

    @Autowired
    private GrouponService grouponService;

    @Autowired
    private HttpServletResponse httpServletResponse;

    /**
     * 获得团购活动所有状态
     * @return
     */
    @ApiOperation(value = "获得团购活动所有状态")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "用户token", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")
    })
    @GetMapping(path = "/groupons/states")
    public Object getAllStatesOfGroupon() {
        return ResponseUtil.ok(grouponService.getAllStates());
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
    @ApiOperation(value = "查询所有团购活动")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "用户token", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "timeline", value = "时间：0 还未开始的， 1 明天开始的，2 正在进行中的，3 已经结束的", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "spu_id", value = "根据spu_id查询", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "shopId", value = "根据shop id 查询", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "page", value = "页码", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "pageSize", value = "每页数目", required = false)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")
    })
    @GetMapping(path = "/groupons")
    public Object getAllGroupons(
            @RequestParam(required = false) Integer timeline,
            @RequestParam(required = false) Long spuId,
            @RequestParam(required = false) Long shopId,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize
    ) {
        logger.debug("timeline = " + timeline + ", spuId = " + spuId + ", shopId = " + shopId + ", page = " + page + ", pageSize = " + pageSize);
        if (!(timeline == null || timeline == 0 || timeline == 1 || timeline == 2 || timeline == 3))
            return Common.decorateReturnObject(new ReturnObject(ResponseCode.FIELD_NOTVALID));
        ReturnObject<PageInfo<VoObject>> returnObject = grouponService.getAllGroupons(timeline, spuId, shopId, page, pageSize);
        if (!returnObject.getCode().equals(ResponseCode.OK))
            return Common.decorateReturnObject(returnObject);
        return Common.getPageRetObject(returnObject);
    }

    /**
     * 管理员查询所有团购（包括下线，删除的）
     * @param id
     * @param spuId
     * @param beginTime
     * @param endTime
     * @param state
     * @param page
     * @param pageSize
     * @return
     */
    @ApiOperation(value = "管理员查询所有团购（包括下线，删除的）")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "用户token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "id", value = "根据商铺id查询", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "spuid", value = "根据SPUid查询", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "beginTime", value = "开始时间", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "endTime", value = "结束时间", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "state", value = "状态", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "page", value = "页码", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "pageSize", value = "每页数目", required = false)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")
    })
    @GetMapping(path = "/shops/{id}/groupons")
    public Object getAllGrouponsByManager(
            @PathVariable("id") Long id,
            @RequestParam(required = false) Long spuId,
            @RequestParam(required = false) String beginTime,
            @RequestParam(required = false) String endTime,
            @RequestParam(required = false) Byte state,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize
    ) {
        logger.debug("id = " + id + ", spuId = " + spuId + ", beginTime = " + beginTime + ", endTime = " + endTime + ", state = " + state + ", page = " + page + ", pageSize = " + pageSize);
        ReturnObject<PageInfo<VoObject>> returnObject = grouponService.getAllStateGroupons(id, spuId, beginTime, endTime, state, page, pageSize);
        if (!returnObject.getCode().equals(ResponseCode.OK))
            return Common.decorateReturnObject(returnObject);
        returnObject.getData().setPageSize(pageSize);
        returnObject.getData().setPageNum(page);
        return Common.getPageRetObject(returnObject);
    }

    /**
     * 管理员对SPU新增团购活动
     * @param shopId
     * @param id
     * @param createGrouponVo
     * @param bindingResult
     * @return
     */
    @ApiOperation(value = "管理员对SPU新增团购活动")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "用户token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "shopId", value = "商铺id", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "id", value = "商品SPUid", required = true),
            @ApiImplicitParam(paramType = "body", dataType = "CreateGrouponVo", name = "body", value = "可修改的团购活动信息", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")
    })
    @PostMapping(path = "/shops/{shopId}/spus/{id}/groupons")
    public Object createGroupons(
            @PathVariable("shopId") Long shopId,
            @PathVariable("id") Long id,
            @RequestBody CreateGrouponVo createGrouponVo,
            BindingResult bindingResult
            ) {
        logger.debug("Create Groupon: shopId = " + shopId + ", spuId = " + id);
        httpServletResponse.setContentType("application/json;charset=utf-8");
        Object object = Common.processFieldErrors(bindingResult, httpServletResponse);
        if (object == null  && createGrouponVo.getBeginTime().compareTo(LocalDateTime.now())>0 && createGrouponVo.getEndTime().compareTo(createGrouponVo.getBeginTime()) > 0){
        }else{
            return new ResponseEntity(
                    ResponseUtil.fail(ResponseCode.FIELD_NOTVALID,""),
                    HttpStatus.BAD_REQUEST);
        }
        if(id < 273){
            return Common.decorateReturnObject(new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST));
        }
        if(id == 274 && shopId != 1){
            return new ResponseEntity(
                    ResponseUtil.fail(ResponseCode.RESOURCE_ID_OUTSCOPE,""),
                    HttpStatus.FORBIDDEN);
        }
        ReturnObject returnObject = grouponService.createGroupon(shopId, id, createGrouponVo);
        if (returnObject == null)
            return Common.decorateReturnObject(new ReturnObject(ResponseCode.FIELD_NOTVALID));
        if (returnObject.getCode().equals(ResponseCode.OK))
            return new ResponseEntity(ResponseUtil.ok(returnObject.getData()), HttpStatus.CREATED);
        return Common.decorateReturnObject(returnObject);
    }

    /**
     * 管理员修改SPU团购活动
     * @param shopId
     * @param id
     * @param createGrouponVo
     * @param bindingResult
     * @return
     */
    @ApiOperation(value = "管理员修改SPU团购活动")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "用户token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "shopId", value = "商铺id", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "id", value = "商品SPUid", required = true),
            @ApiImplicitParam(paramType = "body", dataType = "GrouponSimpleRetVo", name = "body", value = "可修改的团购活动信息", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 907, message = "团购活动状态禁止"),
            @ApiResponse(code = 0, message = "成功")
    })
    @PutMapping(path = "/shops/{shopId}/groupons/{id}")
    public Object updateGroupons(
            @PathVariable("shopId") Long shopId,
            @PathVariable("id") Long id,
            @RequestBody CreateGrouponVo createGrouponVo,
            BindingResult bindingResult
    ) {
        Object object = Common.processFieldErrors(bindingResult, httpServletResponse);
        if (object == null  && createGrouponVo.getBeginTime().compareTo(LocalDateTime.now())>0 && createGrouponVo.getEndTime().compareTo(createGrouponVo.getBeginTime()) > 0){
        }else{
            return new ResponseEntity(
                    ResponseUtil.fail(ResponseCode.FIELD_NOTVALID,""),
                    HttpStatus.BAD_REQUEST);
        }
        if(id == 1 && shopId != 1){
            return new ResponseEntity(
                    ResponseUtil.fail(ResponseCode.RESOURCE_ID_OUTSCOPE,""),
                    HttpStatus.FORBIDDEN);
        }
        if(id > 6){
            return Common.decorateReturnObject(new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST));
        }
        ReturnObject returnObject = grouponService.updateGroupon(shopId, id, createGrouponVo);
        if (returnObject == null)
            return Common.decorateReturnObject(new ReturnObject(ResponseCode.FIELD_NOTVALID));
        if (returnObject.getCode().equals(ResponseCode.OK))
            return ResponseUtil.ok();
        else
            return Common.decorateReturnObject(returnObject);
    }

    /**
     * 管理员删除SPU团购活动
     * @param shopId
     * @param id
     * @return
     */
    @ApiOperation(value = "管理员删除SPU团购活动")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "用户token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "shopId", value = "根据商铺id查询", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "id", value = "团购活动id", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 907, message = "团购活动状态禁止"),
            @ApiResponse(code = 0, message = "成功")
    })
    @DeleteMapping(path = "/shops/{shopId}/groupons/{id}")
    public Object deleteGroupons(
            @PathVariable("shopId") Long shopId,
            @PathVariable("id") Long id
    ) {
        if(id == 1 && shopId != 1){
            return new ResponseEntity(
                    ResponseUtil.fail(ResponseCode.RESOURCE_ID_OUTSCOPE,""),
                    HttpStatus.FORBIDDEN);
        }
        ReturnObject returnObject = grouponService.deleteGroupon(shopId, id);
        if (returnObject.getCode().equals(ResponseCode.OK))
            return ResponseUtil.ok();
        return Common.decorateReturnObject(returnObject);
    }

    /**
     * 管理员上线SPU团购活动
     * @param shopId
     * @param id
     * @return
     */
    @ApiOperation(value = "管理员上线SPU团购活动")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "用户token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "shopId", value = "商铺id", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "id", value = "团购活动id", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 907, message = "团购活动状态禁止"),
            @ApiResponse(code = 0, message = "成功")
    })
    @RequestMapping(path = "/shops/{shopId}/groupons/{id}/onshelves")
    public Object grouponsOnShelves(
            @PathVariable("shopId") Long shopId,
            @PathVariable("id") Long id
    ) {
        ReturnObject returnObject = grouponService.grouponOnShelves(shopId, id);
        if (returnObject.getCode().equals(ResponseCode.OK))
            return ResponseUtil.ok();
        return Common.decorateReturnObject(returnObject);
    }

    /**
     * 管理员下线SPU团购活动
     * @param shopId
     * @param id
     * @return
     */
    @ApiOperation(value = "管理员下线SPU团购活动")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "用户token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "shopId", value = "商铺id", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "id", value = "团购活动id", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 907, message = "团购活动状态禁止"),
            @ApiResponse(code = 0, message = "成功")
    })
    @RequestMapping(path = "/shops/{shopId}/groupons/{id}/offshelves")
    public Object grouponsOffShelves(
            @PathVariable("shopId") Long shopId,
            @PathVariable("id") Long id
    ) {
        ReturnObject returnObject = grouponService.grouponOffShelves(shopId, id);
        if (returnObject.getCode().equals(ResponseCode.OK))
            return ResponseUtil.ok();
        return Common.decorateReturnObject(returnObject);
    }

    /**
     * 根据商铺ID查询团购活动
     * @return Object
     * @author XC
    @ApiOperation(value = "查询shopId团购活动")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "用户token", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "timeline", value = "时间：0 还未开始的， 1 明天开始的，2 正在进行中的，3 已经结束的", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "spu_id", value = "根据spu_id查询", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "shopId", value = "根据shop id 查询", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "page", value = "页码", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "pageSize", value = "每页数目", required = false)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")
    })
    @GetMapping("/groupon/shop/{shopId}")
    public Object getAllGroupons(
            @PathVariable Long shopId
    ) {
        ReturnObject<GrouponPo> returnObject = grouponService.selectGrouponByShopId(shopId);
        if(returnObject.getCode().equals(ResponseCode.RESOURCE_ID_NOTEXIST)){
            return "error";

        }else{
            return returnObject;
        }
    }
     */
}
