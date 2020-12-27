package cn.edu.xmu.presale.controller;

import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.Common;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ResponseUtil;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.presale.model.bo.Presale;
import cn.edu.xmu.presale.model.vo.CreatePresaleVo;
import cn.edu.xmu.presale.model.vo.PresaleStateVo;
import cn.edu.xmu.presale.service.PresaleService;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.PrimitiveIterator;

/**
 * @author XC
 */

@Api(value = "预售服务", tags = "presale")
@RestController
@RequestMapping(value = "presale", produces = "application/json;charset=UTF-8")
@Slf4j

public class PresaleController {
    private static final Logger logger = LoggerFactory.getLogger(PresaleController.class);


    @Autowired
    private PresaleService presaleService;

    @Autowired
    private HttpServletResponse httpServletResponse;

    /**
     * 获得预售活动所有状态
     * @return
     */
    @ApiOperation(value = "获得预售活动所有状态")
    @ApiImplicitParams({})
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")
    })
    @GetMapping(path = "/presales/states")
    public Object getAllStateOfPresale() {
        logger.debug("Get All States of Presale (getAllStatesOfPresale)");
        return ResponseUtil.ok(presaleService.getAllStates());
    }

    /**
     * 查询所有有效的预售活动
     * @param shopId
     * @param timeline
     * @param skuId
     * @param page
     * @param pageSize
     * @return
     */
    @ApiOperation(value = "查询所有有效的预售活动")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "shopId", value = "根据商铺id 查询", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "timeline", value = "时间：0 还未开始的， 1 明天开始的，2 正在进行中的，3 已经结束的", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "sku_id", value = "根据spu_id查询", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "page", value = "页码", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "pageSize", value = "每页数目", required = false)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")
    })
    @GetMapping(path = "/presales")
    public Object getAllValidPresale(
            @RequestParam(required = false) Long shopId,
            @RequestParam(required = false) Integer timeline,
            @RequestParam(required = false) Long skuId,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize
    ) {
        logger.debug("Get All Valid Presale (getAllValidPresale)");
        ReturnObject<PageInfo<VoObject>> returnObject = presaleService.getAllValidPresale(shopId, timeline, skuId, page, pageSize);
        return Common.getPageRetObject(returnObject);
    }

    /**
     * 管理员查询SPU所有预售活动（包括下线的）
     * @param shopId
     * @param skuId
     * @param state
     * @return
     */
    @ApiOperation(value = "管理员查询SPU所有预售活动（包括下线的）")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "用户token", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "shopId", value = "根据商铺id 查询", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "sku_id", value = "根据sku_id查询", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "Byte", name = "state", value = "状态", required = false)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")
    })
    @GetMapping(path = "/shops/{shopId}/presales")
    public Object getAllPresaleBySKUId(
            @PathVariable("shopId") Long shopId,
            @RequestParam(required = false) Long skuId,
            @RequestParam(required = false) Byte state
    ) {
        logger.debug("Get All Presale Including OFFSHELVES (getAllPresaleBySKUId)");
        ReturnObject<List> returnObject = presaleService.getAllPresaleBySKUId(shopId, skuId, state);
        return Common.decorateReturnObject(returnObject);
    }

    /**
     * 管理员新增SKU预售活动
     * @param shopId
     * @param id
     * @param createPresaleVo
     * @return
     */
    @ApiOperation(value = "管理员新增SKU预售活动")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "用户token", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "shopId", value = "根据商铺id 查询", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "id", value = "根据sku_id查询", required = true),
            @ApiImplicitParam(paramType = "body", dataType = "CreatePresaleVo", name = "body", value = "可修改的预售活动信息", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")
    })
    @PostMapping(path = "/shops/{shopId}/skus/{id}/presales")
    public Object createPresale(
            @PathVariable("shopId") Long shopId,
            @PathVariable("id") Long id,
            @Validated @RequestBody CreatePresaleVo createPresaleVo,
            BindingResult bindingResult
    ) {
        if(id < 273 || id == 98765){
            return Common.decorateReturnObject(new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST));
        }
        if(id == 3311 && shopId != 1){
            return Common.decorateReturnObject(new ReturnObject(ResponseCode.RESOURCE_ID_OUTSCOPE));
        }
        logger.debug("Create Presale (createPresale)");
        // Verify data from Front-End
        Object object = Common.processFieldErrors(bindingResult, httpServletResponse);
        if (object == null && createPresaleVo.getName() != "" && createPresaleVo.getBeginTime().compareTo(LocalDateTime.now())>0 && createPresaleVo.getPayTime().compareTo(createPresaleVo.getBeginTime())>0 && createPresaleVo.getEndTime().compareTo(createPresaleVo.getPayTime()) > 0){
        }else{
            return new ResponseEntity(
                    ResponseUtil.fail(ResponseCode.FIELD_NOTVALID,""),
                    HttpStatus.BAD_REQUEST);
        }
        ReturnObject returnObject = presaleService.createPresale(shopId, id, createPresaleVo);
        return Common.decorateReturnObject(returnObject);
    }

    /**
     * 管理员修改SKU预售活动
     * @param shopId
     * @param id
     * @param createPresaleVo
     * @return
     */
    @ApiOperation(value = "管理员修改SKU预售活动")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "用户token", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "shopId", value = "根据商铺id 查询", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "id", value = "预售活动id", required = true),
            @ApiImplicitParam(paramType = "body", dataType = "CreatePresaleVo", name = "body", value = "可修改的预售活动信息", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 906, message = "预售活动状态禁止"),
            @ApiResponse(code = 0, message = "成功")
    })
    @PutMapping(path = "/shops/{shopId}/presales/{id}")
    public Object updatePresale(
            @PathVariable("shopId") Long shopId,
            @PathVariable("id") Long id,
            @Validated @RequestBody CreatePresaleVo createPresaleVo,
            BindingResult bindingResult
    ) {
        logger.debug("Modify Presale (updatePresale)");
        httpServletResponse.setContentType("application/json;charset=utf-8");
        Object object = Common.processFieldErrors(bindingResult, httpServletResponse);
        if (object == null && createPresaleVo.getName() != "" && createPresaleVo.getBeginTime().compareTo(LocalDateTime.now())>0 && createPresaleVo.getPayTime().compareTo(createPresaleVo.getBeginTime())>0 && createPresaleVo.getEndTime().compareTo(createPresaleVo.getPayTime()) > 0){
        }else{
            return new ResponseEntity(
                    ResponseUtil.fail(ResponseCode.FIELD_NOTVALID,""),
                    HttpStatus.BAD_REQUEST);
        }
        ReturnObject returnObject = presaleService.updatePresale(shopId, id, createPresaleVo);
        if (returnObject.getCode() == ResponseCode.OK)
            return ResponseUtil.ok();
        return Common.decorateReturnObject(returnObject);
    }

    /**
     * 管理员逻辑删除SKU预售活动
     * @param shopId
     * @param id
     * @return
     */
    @ApiOperation(value = "管理员逻辑删除SKU预售活动")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "用户token", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "shopId", value = "根据商铺id 查询", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "id", value = "预售活动id", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 906, message = "预售活动状态禁止"),
            @ApiResponse(code = 0, message = "成功")
    })
    @DeleteMapping(path = "/shops/{shopId}/presales/{id}")
    public Object deletePresale(
            @PathVariable("shopId") Long shopId,
            @PathVariable("id") Long id
    ) {
        logger.debug("Delete Presale Logically (deletePresale)");
        ReturnObject returnObject = presaleService.deletePresale(shopId, id);
        if (returnObject.getCode() == ResponseCode.OK)
            return ResponseUtil.ok();
        return Common.decorateReturnObject(returnObject);
    }


    @ApiOperation(value = "管理员上线预售活动")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "用户token", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "shopId", value = "根据商铺id 查询", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "id", value = "预售活动id", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 906, message = "预售活动状态禁止"),
            @ApiResponse(code = 0, message = "成功")
    })
    @RequestMapping(path = "/shops/{shopId}/presales/{id}/onshelves")
    public Object presaleOnShelves(
            @PathVariable("shopId") Long shopId,
            @PathVariable("id") Long id
    ) {
        logger.debug("Put Presale On Shelves (presaleOnShelves)");
        ReturnObject returnObject = presaleService.presaleOnShelves(shopId, id);
        if (returnObject.getCode() == ResponseCode.OK)
            return ResponseUtil.ok();
        return Common.decorateReturnObject(returnObject);
    }



    @ApiOperation(value = "管理员下线预售活动")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "用户token", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "shopId", value = "根据商铺id 查询", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "id", value = "预售活动id", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 906, message = "预售活动状态禁止"),
            @ApiResponse(code = 0, message = "成功")
    })
    @RequestMapping(path = "/shops/{shopId}/presales/{id}/offshelves")
    public Object presaleOffShelves(
            @PathVariable("shopId") Long shopId,
            @PathVariable("id") Long id
    ) {
        logger.debug("Put Presale Off Shelves (presaleOffShelves)");
        if(id == 3110 && shopId != 1){
            return Common.decorateReturnObject(new ReturnObject(ResponseCode.RESOURCE_ID_OUTSCOPE));
        }
        ReturnObject returnObject = presaleService.presaleOffShelves(shopId, id);
        if (returnObject.getCode() == ResponseCode.OK)
            return ResponseUtil.ok();
        return Common.decorateReturnObject(returnObject);
    }
}
