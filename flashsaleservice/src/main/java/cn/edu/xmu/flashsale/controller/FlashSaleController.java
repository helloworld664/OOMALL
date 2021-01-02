package cn.edu.xmu.flashsale.controller;

import cn.edu.xmu.flashsale.model.vo.FlashSaleInsertVo;
import cn.edu.xmu.flashsale.model.vo.FlashSaleRetItemVo;
import cn.edu.xmu.flashsale.model.vo.FlashSaleSimpleVo;
import cn.edu.xmu.flashsale.service.FlashSaleService;
import cn.edu.xmu.ooad.util.Common;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ResponseUtil;
import cn.edu.xmu.ooad.util.ReturnObject;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.time.LocalDateTime;

/**
 * @author XC 3304
 * Created at 2020-12-07 10:37
 * Modified at 2021-01-01 02:51
 */

@Api(value = "秒杀服务", tags = "flashsale")
@RestController
@RequestMapping(value = "flashsale", produces = "application/json;charset=UTF-8")
@Slf4j
public class FlashSaleController {
    private static final Logger logger = LoggerFactory.getLogger(FlashSaleController.class);

    @Autowired
    private FlashSaleService flashSaleService;

    @Autowired
    private HttpServletResponse httpServletResponse;

    /**
     * 查询某一时段秒杀活动详情
     * @param id
     * @return
     */
    @ApiOperation(value = "查询某一时段秒杀活动详情")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "id", value = "时间段id", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")
    })
    @GetMapping(path = "/timesegments/{id}/flashsales")
    public Object getFlashSaleDetails(
            @PathVariable("id") Long id
    ) {
        // To be implement
        logger.debug("Get Flash Sale in Details (getFlashSaleDetails)");
        return flashSaleService.getFlashSaleInDetail(id).map(x -> (FlashSaleRetItemVo) x.createVo());
    }

    /**
     * 平台管理员在某个时段下新建秒杀
     * @param did
     * @param id
     * @param flashSaleSimpleVo
     * @return
     */
    @ApiOperation(value = "平台管理员在某个时段下新建秒杀")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "用户token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "did", value = "店铺id", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "String", name = "id", value = "秒杀时间段id", required = true),
            @ApiImplicitParam(paramType = "body", dataType = "FlashSaleSimpleVo", name = "skuid", value = "", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")
    })
    @PostMapping(path = "/shops/{did}/timesegments/{id}/flashsales")
    public Object createFlashSaleByTimeSegId(
            @PathVariable("did") Long did,
            @PathVariable("id") String id,
            @RequestBody @Valid FlashSaleSimpleVo flashSaleSimpleVo,
            BindingResult bindingResult
    ) {
        logger.debug("Create Flash Sale in a Time Segment (createFlashSaleByTimeSegId)");
        httpServletResponse.setContentType("application/json;charset=utf-8");
        Object object = Common.processFieldErrors(bindingResult, httpServletResponse);
        if (object != null)
            return object;
        ReturnObject returnObject = flashSaleService.createFlashSaleByTimeSegId(did, Long.parseLong(id), flashSaleSimpleVo);
        return Common.decorateReturnObject(returnObject);
    }

    /**
     * 获取当前时段秒杀列表，响应式API，会多次返回
     * @return
     */
    @ApiOperation(value = "获取当前时段秒杀列表，响应式API，会多次返回")
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")
    })
    @GetMapping(path = "/flashsales/current")
    public Flux<FlashSaleRetItemVo> getCurrentFlashSale() {
        logger.debug("Get Flash Sale in Details (getFlashSaleDetails)");
        return flashSaleService.getCurrentFlashSale(LocalDateTime.now()).map(x -> (FlashSaleRetItemVo) x.createVo());
    }

    /**
     * 平台管理员删除某个时段秒杀
     * @param did
     * @param id
     * @return
     */
    @ApiOperation(value = "平台管理员删除某个时段秒杀")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "用户token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "did", value = "店铺id", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "id", value = "秒杀id", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")
    })
    @DeleteMapping(path = "/shops/{did}/flashsales/{id}")
    public Object deleteFlashSale(
            @PathVariable("did") Long did,
            @PathVariable("id") Long id
    ) {
        ReturnObject returnObject = flashSaleService.deleteFlashSale(did, id);
        if (returnObject.getCode() == ResponseCode.OK)
            return ResponseUtil.ok();
        return Common.decorateReturnObject(returnObject);
    }

    /**
     * 管理员修改秒杀活动
     * @param did
     * @param id
     * @param flashSaleSimpleVo
     * @return
     */
    @ApiOperation(value = "管理员修改秒杀活动")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "用户token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "did", value = "店铺id", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "id", value = "秒杀id", required = true),
            @ApiImplicitParam(paramType = "body", dataType = "FlashSaleSimpleVo", name = "body", value = "可修改的信息", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")
    })
    @PutMapping(path = "/shops/{did}/flashsales/{id}")
    public Object updateFlashSale(
            @PathVariable("did") Long did,
            @PathVariable("id") Long id,
            @RequestBody @Valid FlashSaleSimpleVo flashSaleSimpleVo,
            BindingResult bindingResult
    ) {
        logger.debug("Update Flash Sale (updateFlashSale)");
        Object object = Common.processFieldErrors(bindingResult, httpServletResponse);
        if (object != null)
            return object;
        ReturnObject returnObject = flashSaleService.updateFlashSale(did, id, flashSaleSimpleVo);
        if (returnObject.getCode() == ResponseCode.OK)
            return ResponseUtil.ok();
        return Common.decorateReturnObject(returnObject);
    }

    /**
     * 管理员上线秒杀活动
     * @param did
     * @param id
     * @return
     */
    @ApiOperation(value = "管理员上线秒杀活动")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "用户token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "did", value = "店铺id", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "id", value = "秒杀id", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")
    })
    @PutMapping(path = "/shops/{did}/flashsales/{id}/onshelves")
    public Object flashSaleOnSheleves(
            @PathVariable("did") Long did,
            @PathVariable("id") Long id
    ) {
        ReturnObject returnObject = flashSaleService.flashSaleOnShelves(did, id);
        if (returnObject.getCode() == ResponseCode.OK)
            return ResponseUtil.ok();
        return Common.decorateReturnObject(returnObject);
    }

    /**
     * 管理员下线秒杀活动
     * @param did
     * @param id
     * @return
     */
    @ApiOperation(value = "管理员下线秒杀活动")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "用户token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "did", value = "店铺id", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "id", value = "秒杀id", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")
    })
    @PutMapping(path = "/shops/{did}/flashsales/{id}/offshelves")
    public Object flashSaleOffSheleves(
            @PathVariable("did") Long did,
            @PathVariable("id") Long id
    ) {
        ReturnObject returnObject = flashSaleService.flashSaleOffShelves(did, id);
        if (returnObject.getCode() == ResponseCode.OK)
            return ResponseUtil.ok();
        return Common.decorateReturnObject(returnObject);
    }

    /**
     * 平台管理员向秒杀活动添加商品SKU
     * @param did
     * @param id
     * @param flashSaleInsertVo
     * @return
     */
    @ApiOperation(value = "平台管理员向秒杀活动添加商品SKU")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "用户token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "did", value = "店铺id", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "id", value = "秒杀活动id", required = true),
            @ApiImplicitParam(paramType = "body", dataType = "FlashSaleInsertVo", name = "body", value = "向秒杀活动添加的SKU信息", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")
    })
    @PostMapping(path = "/shops/{did}/flashsales/{id}/flashitems")
    public Object insertSKUIntoFlashSale(
            @PathVariable("did") Long did,
            @PathVariable("id") Long id,
            @RequestBody @Valid FlashSaleInsertVo flashSaleInsertVo,
            BindingResult bindingResult
    ) {
        logger.debug("Insert SKU into Flash Sale (insertSKUIntoFlashSale)");
        Object object = Common.processFieldErrors(bindingResult, httpServletResponse);
        if (null != object)
            return object;
        ReturnObject returnObject = flashSaleService.insertSKUIntoFlashSale(did, id, flashSaleInsertVo);
        if (returnObject.getCode() == ResponseCode.OK)
            return new ResponseEntity(ResponseUtil.ok(returnObject.getData()), HttpStatus.CREATED);
        return Common.decorateReturnObject(returnObject);
    }

    /**
     * 平台管理员在秒杀活动删除商品SKU
     * @param did
     * @param fid
     * @param id
     * @return
     */
    @ApiOperation(value = "平台管理员在秒杀活动删除商品SKU")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "用户token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "did", value = "店铺id", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "fid", value = "秒杀活动id", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "id", value = "秒杀活动项id", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")
    })
    @DeleteMapping(path = "/shops/{did}/flashsales/{fid}/flashitems/{id}")
    public Object deleteSKUFromFlashSale(
            @PathVariable("did") Long did,
            @PathVariable("fid") Long fid,
            @PathVariable("id") Long id
    ) {
        logger.debug("Delete SKU from Flash Sale (insertSKUIntoFlashSale)");
        ReturnObject returnObject = flashSaleService.deleteSKUFromFlashSale(did, fid, id);
        if (returnObject.getCode() == ResponseCode.OK)
            return new ResponseEntity(ResponseUtil.ok(returnObject.getData()), HttpStatus.CREATED);
        return Common.decorateReturnObject(returnObject);
    }
}
