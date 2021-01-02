package cn.edu.xmu.goods.controller;

import cn.edu.xmu.goods.model.vo.ConclusionVo;
import cn.edu.xmu.goods.model.vo.ShopModelVo;
import cn.edu.xmu.goods.service.ShopService;
import cn.edu.xmu.ooad.annotation.Audit;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;

@Api(value = "店铺服务", tags = "shop")
@RestController /*Restful的Controller对象*/
@RequestMapping(value = "shop", produces = "application/json;charset=UTF-8")
@Slf4j

public class ShopController {
    private static final Logger logger = LoggerFactory.getLogger(ShopController.class);

    @Autowired
    private ShopService shopService;

    @Autowired
    private HttpServletResponse httpServletResponse;

    /**
     * 获得店铺的所有状态
     */
    @ApiOperation(value = "获得店铺的所有状态", produces = "application/json")
    @ApiImplicitParams({})
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")
    })
    @GetMapping("/shops/states")
    public Object listStates(){
        return ResponseUtil.ok(shopService.getAllStates());
    }

    /**
     * 店家申请店铺
     */
    @ApiOperation(value = "店家申请店铺", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "token", required = true),
            @ApiImplicitParam(paramType = "body", dataType = "ShopModelVo", name = "body", value = "店铺信息", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 903, message = "用户已拥有店铺")
    })
    @PostMapping("/shops")
    public Object ApplyShopBySeller(
            @Validated @RequestBody ShopModelVo shopModelVo,
            BindingResult bindingResult
    ){
        logger.debug("Apply Shop (ApplyShopBySeller)");
        httpServletResponse.setContentType("application/json;charset=utf-8");
        Object object = Common.processFieldErrors(bindingResult, httpServletResponse);
        if (object == null) {
        } else {
            return new ResponseEntity(
                    ResponseUtil.fail(ResponseCode.FIELD_NOTVALID,""),
                    HttpStatus.BAD_REQUEST);
        }
        ReturnObject returnObject = shopService.applyShopBySeller(shopModelVo);
        if (returnObject == null)
            return Common.decorateReturnObject(new ReturnObject(ResponseCode.FIELD_NOTVALID));
        if (returnObject.getCode().equals(ResponseCode.OK))
            return new ResponseEntity(ResponseUtil.ok(returnObject.getData()), HttpStatus.CREATED);
        return Common.decorateReturnObject(returnObject);
    }

    /**
     * 店家修改店铺信息
     */
    @ApiOperation(value = "店家修改店铺信息", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "id", value = "店铺ID", required = true),
            @ApiImplicitParam(paramType = "body", dataType = "ShopModelVo", name = "body", value = "店铺信息", required = true)

    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")
    })
    @Audit
    @PostMapping("/shops/{id}")
    @ResponseBody
    public Object ModifyShopBySeller(
            @PathVariable("id") Long id,
            @Validated @RequestBody ShopModelVo shopModelVo,
            BindingResult bindingResult
    ){
        Object object = Common.processFieldErrors(bindingResult, httpServletResponse);
        if (object == null) {
        } else {
            return new ResponseEntity(
                    ResponseUtil.fail(ResponseCode.FIELD_NOTVALID,""),
                    HttpStatus.BAD_REQUEST);
        }
        ReturnObject returnObject = shopService.modifyShopBySeller(id, shopModelVo);
        if (returnObject == null)
            return Common.decorateReturnObject(new ReturnObject(ResponseCode.FIELD_NOTVALID));
        if (returnObject.getCode().equals(ResponseCode.OK))
            return ResponseUtil.ok();
        else
            return Common.decorateReturnObject(returnObject);
    }

    /**
     * 管理员或店家关闭店铺
     */
    @ApiOperation(value = "管理员或店家关闭店铺", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "id", value = "店铺ID", required = true)

    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")
    })
    @Audit
    @DeleteMapping("/shops/{id}")
    @ResponseBody
    public Object ModifyShopBySellerOrManager(
            @PathVariable("id") Long id
    ){
        ReturnObject returnObject = shopService.closeShop(id);
        if (returnObject.getCode().equals(ResponseCode.OK))
            return ResponseUtil.ok();
        return Common.decorateReturnObject(returnObject);
    }

    /**
     * 平台管理员审核店铺信息
     */
    @ApiOperation(value = "平台管理员审核店铺信息", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "shopID", value = "店铺ID", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "id", value = "新店ID", required = true),
            @ApiImplicitParam(paramType = "body", dataType = "Integer", name = "ShopExamineModelVo", value = "店铺审核", required = true)

    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")
    })
    @PutMapping("/shops/{shopId}/newshops/{id}/audit")
    public Object ExamineShopByManager(
            @PathVariable("shopId") Long shopId,
            @PathVariable("id") Long id,
            @Validated @RequestBody ConclusionVo conclusionVo
    ){
        ReturnObject returnObject = shopService.newShop(shopId, id, conclusionVo);
        if (returnObject.getCode().equals(ResponseCode.OK))
            return ResponseUtil.ok();
        return Common.decorateReturnObject(returnObject);
    }

    /**
     * 管理员上线店铺
     */
    @ApiOperation(value = "管理员上线店铺", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "id", value = "店铺ID", required = true)

    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")
    })
    @PutMapping("/shops/{id}/onshelves")
    public Object ONLINEShopByManager(
            @PathVariable("id") Long id
    ){
        ReturnObject returnObject = shopService.shopOnShelves(id);
        if (returnObject.getCode().equals(ResponseCode.OK))
            return ResponseUtil.ok();
        return Common.decorateReturnObject(returnObject);
    }

    /**
     * 管理员下线店铺
     */
    @ApiOperation(value = "管理员下线店铺", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "id", value = "店铺ID", required = true)

    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")
    })
    @PutMapping("/shops/{id}/offshelves")

    public Object OFFLINEShopByManager(
            @PathVariable("id") Long id
    ){
        ReturnObject returnObject = shopService.shopOffShelves(id);
        if (returnObject.getCode().equals(ResponseCode.OK))
            return ResponseUtil.ok();
        return Common.decorateReturnObject(returnObject);
    }
}
