package cn.edu.xmu.goods.controller;

import cn.edu.xmu.goods.model.bo.GoodsSku;
import cn.edu.xmu.goods.model.vo.*;
import cn.edu.xmu.goods.service.GoodsService;
import cn.edu.xmu.ooad.annotation.Audit;
import cn.edu.xmu.ooad.annotation.Depart;
import cn.edu.xmu.ooad.annotation.LoginUser;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.Common;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ResponseUtil;
import cn.edu.xmu.ooad.util.ReturnObject;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;


@Api(value = "商品服务", tags = "goods")
@RestController
@RequestMapping(value = "/goods", produces = "application/json;charset=UTF-8")
public class GoodsController {

    private static final Logger logger = LoggerFactory.getLogger(GoodsController.class);

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private HttpServletResponse httpServletResponse;

    @Autowired
    private HttpServletRequest httpServletRequest;

    /**
     * 查看商品sku的所有状态
     *
     */
    @ApiOperation(value = "获得商品sku的所有状态")
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")
    })
    @GetMapping("/skus/states")
    public Object getGoodsSkuState() {
        GoodsSku.State[] states = GoodsSku.State.class.getEnumConstants();
        List<SkuStateVo> skuStateVos = new ArrayList<>();
        for (GoodsSku.State state : states) {
            skuStateVos.add(new SkuStateVo(state));
        }
        return ResponseUtil.ok(new ReturnObject<List<?>>(skuStateVos).getData());
    }

    /**
     * 店家或管理员删除商品spu
     *
     */
    @ApiOperation(value = "店家删除商品spu")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Long", name = "shopId", value = "店铺id", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Long", name = "id", value = "spuId", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")
    })
    @Audit
    @DeleteMapping("/shops/{shopId}/spus/{id}")
    public Object deleteGoodsSpu(@PathVariable Long shopId, @PathVariable Long id) {
        if (logger.isDebugEnabled()) {
            logger.debug("deleteGoodsSpu : shopId = " + shopId + " spuId = " + id);
        }
        ReturnObject<?> returnObject = goodsService.deleteSpuById(shopId, id);
        return Common.decorateReturnObject(returnObject);
    }

    /**
     * 店家商品上架
     *
     */
    @ApiOperation(value = "店家商品上架")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Long", name = "shopId", value = "店铺id", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Long", name = "id", value = "skuId", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")
    })
    @Audit
    @PutMapping("/shops/{shopId}/skus/{id}/onshelves")
    public Object putGoodsOnSales(@PathVariable Long shopId, @PathVariable Long id) {
        if (logger.isDebugEnabled()) {
            logger.debug("putGoodsOnSales : shopId = " + shopId + " skuId = " + id);
        }
        ReturnObject returnObj = goodsService.putGoodsOnSaleById(shopId, id);
        if (returnObj.getCode() == ResponseCode.RESOURCE_ID_OUTSCOPE) {
            httpServletResponse.setStatus(403);
        }
        return Common.decorateReturnObject(returnObj);
    }

    /**
     * 店家商品下架
     *
     */
    @ApiOperation(value = "店家商品下架")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Long", name = "shopId", value = "店铺id", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Long", name = "id", value = "spuId", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")
    })
    @Audit
    @PutMapping("/shops/{shopId}/skus/{id}/offshelves")
    public Object putOffGoodsOnSales(@PathVariable Long shopId, @PathVariable Long id) {
        if (logger.isDebugEnabled()) {
            logger.debug("putGoodsOnSales : shopId = " + shopId + " skuId = " + id);
        }
        ReturnObject returnObj = goodsService.putOffGoodsOnSaleById(shopId, id);
        if (returnObj.getCode() == ResponseCode.RESOURCE_ID_OUTSCOPE) {
            httpServletResponse.setStatus(403);
        }
        return Common.decorateReturnObject(returnObj);
    }

    /**
     * 管理员或店家逻辑删除sku
     *
     */
    @ApiOperation(value = "管理员或店家逻辑删除sku")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Long", name = "shopId", value = "店铺id", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Long", name = "id", value = "skuId", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")
    })
    @Audit
    @DeleteMapping("/shops/{shopId}/skus/{id}")
    public Object deleteGoodsSku(@PathVariable Long shopId, @PathVariable Long id) {
        if (logger.isDebugEnabled()) {
            logger.debug("deleteGoodsSku : shopId = " + shopId + " skuId = " + id);
        }
        ReturnObject returnObj = goodsService.deleteSkuById(shopId, id);
        httpServletResponse.setContentType("application/json;charset=utf-8");
        if (returnObj.getCode() == ResponseCode.RESOURCE_ID_OUTSCOPE) {
            httpServletResponse.setStatus(403);
        }
        return Common.decorateReturnObject(returnObj);
    }

    /**
     * 查看所有品牌
     *
     */
    @ApiOperation(value = "查看所有品牌")
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")
    })
    @GetMapping("/brands")
    public Object getAllBrand(@RequestParam(required = false) Integer page, @RequestParam(required = false) Integer pageSize) {
        logger.debug("getAllBrand: page = " + page + "  pageSize =" + pageSize);
        page = (page == null) ? 1 : page;
        pageSize = (pageSize == null) ? 10 : pageSize;
        if (page <= 0 || pageSize <= 0) {
            return new ReturnObject<>(ResponseCode.FIELD_NOTVALID, "页数或页大小必须大于0");
        }
        logger.debug("getAllBrand: page = " + page + "  pageSize =" + pageSize);
        ReturnObject<PageInfo<VoObject>> returnObject = goodsService.findAllBrand(page, pageSize);
        return Common.getPageRetObject(returnObject);
    }

    /**
     * 管理员或店家修改商品sku
     *
     */
    @ApiOperation(value = "店家修改商品sku")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Long", name = "shopId", value = "店铺id", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Long", name = "id", value = "skuId", required = true),
            @ApiImplicitParam(paramType = "body", dataType = "SkuInputVo", name = "skuInputVo", value = "可修改的sku信息", required = true)
    })
    @Audit
    @PutMapping("/shops/{shopId}/skus/{id}")
    public Object modifySku(@PathVariable Long shopId, @PathVariable Long id, @RequestBody SkuInputVo skuInputVo) {
        if (logger.isDebugEnabled()) {
            logger.debug("modifyGoodsSku : shopId = " + shopId + " skuId = " + id + " vo = " + skuInputVo);
        }
        ReturnObject returnObj = goodsService.modifySkuInfo(shopId, id, skuInputVo);
        if (returnObj.getCode() == ResponseCode.RESOURCE_ID_OUTSCOPE) {
            httpServletResponse.setStatus(403);
        }
        return Common.decorateReturnObject(returnObj);
    }

    /**
     * 查看所有sku
     *
     */
    @ApiOperation(value = "查询sku")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "shopId", value = "店铺id", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "skuSn", value = "skuSn", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "Long", name = "spuId", value = "spuId", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "spuSn", value = "spuSn", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "page", value = "页码", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "pageSize", value = "每页数目", required = false)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")
    })
    @GetMapping("/skus")
    public Object getSkuList(
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long shopId,
            @RequestParam(required = false) String skuSn,
            @RequestParam(required = false) Long spuId,
            @RequestParam(required = false) String spuSn
    ) {
        Object object;
        if (page <= 0 || pageSize <= 0) {
            object = Common.getNullRetObj(new ReturnObject<>(ResponseCode.FIELD_NOTVALID), httpServletResponse);
        } else {
            ReturnObject<PageInfo<VoObject>> returnObject = goodsService.findSkuSimple(shopId, page, pageSize, spuId, skuSn, spuSn);
            logger.debug("getSkuSimple = " + returnObject);
            object = Common.getPageRetObject(returnObject);
        }
        return object;
    }

    /**
     * 管理员失效商品价格浮动
     *
     */
    @ApiOperation(value = "管理员失效商品价格浮动")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Long", name = "shopId", value = "店铺id", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Long", name = "floatPriceId", value = "价格浮动id", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")
    })
    @Audit
    @DeleteMapping("/shops/{shopId}/floatPrices/{id}")
    public Object invalidFloatPrice(@PathVariable Long id, @PathVariable Long shopId, @LoginUser Long loginUserId) {
        if (logger.isDebugEnabled()) {
            logger.debug("invalidFloatPrice : shopId = " + shopId + " floatPriceId = " + id);
        }
        httpServletResponse.setContentType("application/json;charset=UTF-8");
        ReturnObject returnObj = goodsService.invalidFloatPriceById(shopId, id, loginUserId);
        return Common.decorateReturnObject(returnObj);
    }

    /**
     * 管理员修改品牌
     *
     */
    @ApiOperation(value = "管理员修改品牌")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Long", name = "shopId", value = "店铺id", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Long", name = "id", value = "spuId", required = true),
            @ApiImplicitParam(paramType = "body", dataType = "BrandInputVo", name = "brandInputVo", value = "可修改的品牌信息", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 500, message = "服务器内部错误"),
            @ApiResponse(code = 503, message = "品牌名称不能为空"),
            @ApiResponse(code = 900, message = "品牌名称已存在"),
            @ApiResponse(code = 504, message = "操作的资源id不存在"),
            @ApiResponse(code = 705, message = "无权限访问")
    })
    @Audit
    @PutMapping("/shops/{shopId}/brands/{id}")
    public Object modifyBrand(@PathVariable Long shopId, @PathVariable Long id, @Validated @RequestBody BrandInputVo brandInputVo, BindingResult bindingResult) {
        if (logger.isDebugEnabled()) {
            logger.debug("modifyBrand : shopId = " + shopId + " brandId = " + id + " vo = " + brandInputVo);
        }
        Object returnObject = Common.processFieldErrors(bindingResult, httpServletResponse);
        if (returnObject != null) {
            logger.info("incorrect data received while modifyBrand shopId = " + shopId + " skuId = " + id);
            return returnObject;
        }
        if (shopId == 0) {
            ReturnObject returnObj = goodsService.modifyBrandInfo(shopId, id, brandInputVo);
            return Common.decorateReturnObject(returnObj);
        } else {
            return Common.decorateReturnObject(new ReturnObject(ResponseCode.AUTH_NOT_ALLOW));
        }
    }

    /**
     * 管理员删除品牌(平台管理员)
     *
     */
    @ApiOperation(value = "管理员删除品牌")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Long", name = "shopId", value = "店铺id", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Long", name = "brandId", value = "品牌id", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 500, message = "服务器内部错误"),
            @ApiResponse(code = 504, message = "操作的资源id不存在"),
            @ApiResponse(code = 705, message = "无权限访问")
    })
    @Audit
    @DeleteMapping("/shops/{shopId}/brands/{id}")
    public Object deleteBrand(@PathVariable Long id, @PathVariable Long shopId) {
        if (logger.isDebugEnabled()) {
            logger.debug("deleteBrand : shopId = " + shopId + " brandId = " + id);
        }
        if (shopId == 0) {
            ReturnObject returnObj = goodsService.deleteBrandById(id);
            return Common.decorateReturnObject(returnObj);
        } else {
            return Common.decorateReturnObject(new ReturnObject<>(ResponseCode.AUTH_NOT_ALLOW));
        }
    }

    /**
     * 管理员新增商品类目(只有管理员可以)
     *
     */
    @ApiOperation(value = "管理员新增商品类目")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Long", name = "id", value = "种类 id", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Long", name = "shopId", value = "店铺 id", required = true),
            @ApiImplicitParam(paramType = "body", dataType = "CategoryInputVo", name = "categoryInputVo", value = "类目详细信息", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 500, message = "服务器内部错误"),
            @ApiResponse(code = 991, message = "商品类目已存在"),
            @ApiResponse(code = 503, message = "商品类目名字不能重复"),
            @ApiResponse(code = 504, message = "操作的资源id不存在"),
            @ApiResponse(code = 705, message = "无权限访问")
    })
    @Audit
    @PostMapping("/shops/{shopId}/categories/{id}/subcategories")
    public Object addCategory(@PathVariable Long id, @Validated @RequestBody CategoryInputVo categoryInputVo, @PathVariable Long shopId, HttpServletResponse response) {
        if (logger.isDebugEnabled()) {
            logger.debug("addCategory: CategoryId = " + id);
        }
        if (shopId == 0) {
            ReturnObject goodsCategory = goodsService.addCategory(id, categoryInputVo);
            if (goodsCategory.getCode() == ResponseCode.OK) {
                response.setStatus(201);
            } else if (goodsCategory.getCode() == ResponseCode.FIELD_NOTVALID) {
                response.setStatus(400);
            }
            return Common.decorateReturnObject(goodsCategory);
        } else {
            return Common.decorateReturnObject(new ReturnObject<>(ResponseCode.AUTH_NOT_ALLOW));
        }
    }

    /**
     * 管理员修改商品类目信息
     *
     */
    @ApiOperation(value = "管理员修改商品类目信息")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Long", name = "id", value = "种类 id", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Long", name = "shopId", value = "店铺 id", required = true),
            @ApiImplicitParam(paramType = "body", dataType = "CategoryInputVo", name = "CategoryInputVo", value = "类目详细信息", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 500, message = "服务器内部错误"),
            @ApiResponse(code = 503, message = "商品类目名称不能为空"),
            @ApiResponse(code = 991, message = "商品类目名称已存在"),
            @ApiResponse(code = 504, message = "操作的资源id不存在"),
            @ApiResponse(code = 705, message = "无权限访问")
    })
    @Audit
    @PutMapping("/shops/{shopId}/categories/{id}")
    public Object modifyGoodsType(@PathVariable Long id, @Validated @RequestBody CategoryInputVo categoryInputVo, @PathVariable Long shopId, @Depart Long shopid) {
        if (logger.isDebugEnabled()) {
            logger.debug("modifyGoodsType: CategoryId = " + id);
        }
        if (shopId == 0) {
            ReturnObject returnObj = goodsService.modifyCategory(id, categoryInputVo);
            return Common.decorateReturnObject(returnObj);
        } else {
            return Common.decorateReturnObject(new ReturnObject<>(ResponseCode.AUTH_NOT_ALLOW));
        }

    }

    /**
     * 管理员删除商品类目信息
     *
     */
    @ApiOperation(value = "管理员删除商品类目信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "Token", required = true, dataType = "String", paramType = "header"),
            @ApiImplicitParam(name = "id", required = true, dataType = "Integer", paramType = "path")
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 504, message = "操作的资源id不存在"),
            @ApiResponse(code = 705, message = "无权限访问")
    })
    @Audit
    @DeleteMapping("/shops/{shopId}/categories/{id}")
    public Object delCategory(@PathVariable Long id, @PathVariable Long shopId, @Depart Long shopid) {
        if (logger.isDebugEnabled()) {
            logger.debug("deleteCategory: CategoryId = " + id);
        }
        if (shopid == 0 && shopId == 0) {
            ReturnObject returnObject = goodsService.deleteCategoryById(id);
            return Common.decorateReturnObject(returnObject);
        } else {
            return Common.decorateReturnObject(new ReturnObject<>(ResponseCode.AUTH_NOT_ALLOW));
        }
    }

    /**
     * 将SPU加入品牌
     *
     * @param id
     * @param shopId
     * @return
     */
    @ApiOperation(value = "将SPU加入品牌")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "shopId", value = "店铺id", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "spuId", value = "spuId", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "id", value = "品牌id", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 504, message = "操作的资源id不存在"),
            @ApiResponse(code = 705, message = "无权限访问")
    })
    @Audit
    @PostMapping("/shops/{shopId}/spus/{spuId}/brands/{id}")
    public Object spuAddBrand(@PathVariable Long shopId, @PathVariable Long spuId, @PathVariable Long id, @Depart Long shopid) {
        if (shopid == 0) {
            ReturnObject returnObject = goodsService.spuAddBrand(shopId, spuId, id);
            return Common.decorateReturnObject(returnObject);
        } else {
            return Common.decorateReturnObject(new ReturnObject<>(ResponseCode.AUTH_NOT_ALLOW));
        }
    }

    /**
     * 查询商品分类关系(用户无需登录)
     *
     * @param id 种类id
     * @return ReturnObject<List>
     */
    @ApiOperation(value = "查询商品分类关系")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", required = true, dataType = "Integer", paramType = "path")
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @GetMapping("/categories/{id}/subcategories")
    public Object queryType(@PathVariable Long id) {
        ReturnObject<List> returnObject = goodsService.selectCategories(id);
        return Common.decorateReturnObject(returnObject);
    }

    /**
     * sku上传图片
     *
     */
    @ApiOperation(value = "sku上传图片")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Long", name = "shopId", value = "店铺 id", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Long", name = "skuId", value = "sku id", required = true),
            @ApiImplicitParam(paramType = "formData", dataType = "file", name = "img", value = "文件", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 506, message = "该目录文件夹没有写入的权限"),
            @ApiResponse(code = 508, message = "图片格式不正确"),
            @ApiResponse(code = 509, message = "图片大小超限")
    })
    @Audit
    @PostMapping("/shops/{shopId}/skus/{id}/uploadImg")
    public Object uploadSkuImg(@PathVariable Long shopId, @PathVariable Long id, @RequestParam("img") MultipartFile multipartFile) {
        logger.debug("uploadSkuImg: shopId = " + shopId + " skuId = " + id + " img :" + multipartFile.getOriginalFilename());
        ReturnObject returnObject = goodsService.uploadSkuImg(shopId, id, multipartFile);
        return Common.getNullRetObj(returnObject, httpServletResponse);
    }

    /**
     * spu上传图片
     *
     */
    @ApiOperation(value = "spu上传图片")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Long", name = "shopId", value = "店铺 id", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Long", name = "spuId", value = "spu id", required = true),
            @ApiImplicitParam(paramType = "formData", dataType = "file", name = "img", value = "文件", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 506, message = "该目录文件夹没有写入的权限"),
            @ApiResponse(code = 508, message = "图片格式不正确"),
            @ApiResponse(code = 509, message = "图片大小超限")
    })
    @Audit
    @PostMapping("/shops/{shopId}/spus/{id}/uploadImg")
    public Object uploadSpuImg(@PathVariable Long shopId, @PathVariable Long id, @RequestParam("img") MultipartFile multipartFile) {
        logger.debug("uploadSpuImg: shopId = " + shopId + " spuId = " + id + " img :" + multipartFile.getOriginalFilename());
        ReturnObject returnObject = goodsService.uploadSpuImg(shopId, id, multipartFile);
        return Common.getNullRetObj(returnObject, httpServletResponse);
    }

    /**
     * 品牌上传图片(只有管理员可以)
     *
     */
    @ApiOperation(value = "品牌上传图片")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Long", name = "shopId", value = "店铺 id", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Long", name = "brandId", value = "brand id", required = true),
            @ApiImplicitParam(paramType = "formData", dataType = "file", name = "img", value = "文件", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 506, message = "该目录文件夹没有写入的权限"),
            @ApiResponse(code = 508, message = "图片格式不正确"),
            @ApiResponse(code = 509, message = "图片大小超限")
    })
    @Audit
    @PostMapping("/shops/{shopId}/brands/{id}/uploadImg")
    public Object uploadBrandImg(@PathVariable Long shopId, @PathVariable Long id, @RequestParam("img") MultipartFile multipartFile, @Depart Long shopid) {
        logger.debug("uploadBrandImg: shopId = " + shopId + " brandId = " + id + " img :" + multipartFile.getOriginalFilename());
        if (shopid == 0) {
            ReturnObject returnObject = goodsService.uploadBrandImg(shopId, id, multipartFile);
            return Common.getNullRetObj(returnObject, httpServletResponse);
        } else {
            return new ReturnObject(ResponseCode.RESOURCE_ID_OUTSCOPE);
        }
    }

    /**
     * 将SPU加入种类
     *
     */
    @ApiOperation(value = "将SPU加入种类")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "shopId", value = "店铺id", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "spuId", value = "spuId", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "id", value = "分类id", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 504, message = "操作的资源id不存在"),
            @ApiResponse(code = 705, message = "无权限访问")
    })
    @Audit
    @PostMapping("/shops/{shopId}/spus/{spuId}/categories/{id}")
    public Object spuAddCategories(@PathVariable Long shopId, @PathVariable Long spuId, @PathVariable Long id, @Depart Long shopid) {
        if (shopid == 0) {
            ReturnObject returnObject = goodsService.spuAddCategories(shopId, spuId, id);
            return Common.decorateReturnObject(returnObject);
        } else {
            return Common.decorateReturnObject(new ReturnObject<>(ResponseCode.AUTH_NOT_ALLOW));
        }
    }

    /**
     * spu删除种类
     *
     */
    @ApiOperation(value = "将SPU删除种类")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "shopId", value = "店铺id", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "spuId", value = "spuId", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "id", value = "分类id", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 504, message = "操作的资源id不存在"),
            @ApiResponse(code = 705, message = "无权限访问")
    })
    @Audit
    @DeleteMapping("/shops/{shopId}/spus/{spuId}/categories/{id}")
    public Object spuDeleteCategories(@PathVariable Long shopId, @PathVariable Long spuId, @PathVariable Long id, @Depart Long shopid) {
        if (shopid == 0) {
            ReturnObject returnObject = goodsService.spuDeleteCategories(shopId, spuId, id);
            return Common.decorateReturnObject(returnObject);
        } else {
            return Common.decorateReturnObject(new ReturnObject<>(ResponseCode.AUTH_NOT_ALLOW));
        }
    }

    /**
     * 将SPU删除品牌
     *
     */
    @ApiOperation(value = "将SPU删除品牌")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "shopId", value = "店铺id", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "spuId", value = "spuId", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "id", value = "品牌id", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 504, message = "操作的资源id不存在"),
            @ApiResponse(code = 705, message = "无权限访问")
    })
    @Audit
    @DeleteMapping("/shops/{shopId}/spus/{spuId}/brands/{id}")
    public Object spuDeleteBrand(@PathVariable Long shopId, @PathVariable Long spuId, @PathVariable Long id, @Depart Long shopid) {
        if (shopid == 0) {
            ReturnObject returnObject = goodsService.spuDeleteBrand(shopId, spuId, id);
            return Common.decorateReturnObject(returnObject);
        } else {
            return Common.decorateReturnObject(new ReturnObject(ResponseCode.AUTH_NOT_ALLOW));
        }
    }

    /**
     * 管理员新增品牌(平台管理员)
     *
     */
    @ApiOperation(value = "管理员新增品牌")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", dataType = "Long", name = "id", value = "店铺id", required = true),
            @ApiImplicitParam(paramType = "body", dataType = "BrandInputVo", name = "brandInputVo", value = "品牌详细信息", required = true),
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 500, message = "服务器内部错误"),
            @ApiResponse(code = 503, message = "品牌名字不能为空"),
            @ApiResponse(code = 990, message = "品牌名称已存在"),
            @ApiResponse(code = 705, message = "无权限访问")
    })
    @Audit
    @PostMapping("/shops/{id}/brands")
    public Object addBrand(@PathVariable Long id, @Validated @RequestBody BrandInputVo brandInputVo, HttpServletResponse response) {
        if (logger.isDebugEnabled()) {
            logger.debug("addBrands: BrandId = " + id);
        }
        if (id == 0) {
            ReturnObject brandCategory = goodsService.addBrand(brandInputVo);
            if (brandCategory.getCode() == ResponseCode.OK) {
                response.setStatus(201);
            } else if (brandCategory.getCode() == ResponseCode.FIELD_NOTVALID) {
                response.setStatus(400);
            }
            return Common.decorateReturnObject(brandCategory);
        } else {
            return Common.decorateReturnObject(new ReturnObject<>(ResponseCode.AUTH_NOT_ALLOW));
        }
    }

    /**
     * 管理员新增商品价格浮动
     *
     */
    @ApiOperation(value = "管理员新增商品价格浮动")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Long", name = "shopId", value = "店铺id", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Long", name = "skuId", value = "skuId", required = true),
            @ApiImplicitParam(paramType = "body", dataType = "FloatPriceInputVo", name = "floatPriceInputVo", value = "可修改的信息", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 504, message = "操作的资源id不存在"),
            @ApiResponse(code = 705, message = "无权限访问"),
            @ApiResponse(code = 902, message = "商品价格浮动时间冲突"),
            @ApiResponse(code = 610, message = "开始时间大于结束时间"),
            @ApiResponse(code = 611, message = "开始时间不能为空"),
            @ApiResponse(code = 622, message = "结束时间不能为空"),
            @ApiResponse(code = 900, message = "商品规格库存不够"),
            @ApiResponse(code = 705, message = "无权限访问")
    })
    @Audit
    @PostMapping("/shops/{shopId}/skus/{id}/floatPrices")
    public Object addFloatingPrice(@PathVariable Long shopId, @PathVariable Long id, @Validated @RequestBody FloatPriceInputVo floatPriceInputVo, @LoginUser Long userId, HttpServletResponse response) {
        if (logger.isDebugEnabled()) {
            logger.debug("addFloatingPrice: shopId = " + shopId + " skuId = " + id);
        }
        ReturnObject floatPrice = goodsService.addFloatPrice(shopId, id, floatPriceInputVo, userId);
        if (floatPrice.getCode() == ResponseCode.OK) {
            response.setStatus(201);
        } else if (floatPrice.getCode() == ResponseCode.RESOURCE_ID_OUTSCOPE) {
            response.setStatus(403);
        } else if (floatPrice.getCode() == ResponseCode.Log_Bigger) {
            response.setStatus(400);
        } else if (floatPrice.getCode() == ResponseCode.SKU_NOTENOUGH) {
            response.setStatus(200);
        } else if (floatPrice.getCode() == ResponseCode.FIELD_NOTVALID) {
            response.setStatus(400);
        }
        return Common.decorateReturnObject(floatPrice);
    }

    /**
     * 店家或管理员修改商品spu
     *
     */
    @ApiOperation(value = "店家修改商品spu")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Long", name = "shopId", value = "店铺id", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Long", name = "id", value = "spuId", required = true),
            @ApiImplicitParam(paramType = "body", dataType = "SpuInputVo", name = "spuInputVo", value = "可修改的用户信息", required = true),

    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")
    })
    @Audit
    @PutMapping("/shops/{shopId}/spus/{id}")
    public Object modifyGoodsSpu(@PathVariable Long shopId, @PathVariable Long id, @Validated @RequestBody SpuInputVo spuInputVo) {
        if (logger.isDebugEnabled()) {
            logger.debug("modifyGoodsSpu : shopId = " + shopId + " spuId = " + id + " vo = " + spuInputVo);
        }
        ReturnObject returnObj = goodsService.modifySpuInfo(shopId, id, spuInputVo);
        return Common.decorateReturnObject(returnObj);
    }

    /**
     * 店家或管理员新建商品spu
     *
     */
    @ApiOperation(value = "店家新建商品spu")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Long", name = "shopId", value = "店铺id", required = true),
            @ApiImplicitParam(paramType = "body", dataType = "SpuInputVo", name = "spuInputVo", value = "可修改的用户信息", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")
    })
    @Audit
    @PostMapping("/shops/{shopId}/spus")
    public Object addSpu(@PathVariable Long shopId, @Validated @RequestBody SpuInputVo spuInputVo, @Depart Long departId) {
        if (logger.isDebugEnabled()) {
            logger.debug("addSpu : shopId = " + shopId + " vo = " + spuInputVo);
        }
        if (shopId.equals(departId) || departId == 0) {
            ReturnObject returnObj = goodsService.addSpu(shopId, spuInputVo);
            if (returnObj.getCode() == ResponseCode.OK) {
                httpServletResponse.setStatus(201);
            }
            return Common.decorateReturnObject(returnObj);
        } else {
            httpServletResponse.setStatus(403);
            return Common.decorateReturnObject(new ReturnObject(ResponseCode.RESOURCE_ID_OUTSCOPE));
        }
    }

    /**
     * 获得sku的详细信息
     *
     */
    @ApiOperation(value = "获得sku的详细信息")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = false),
            @ApiImplicitParam(paramType = "path", dataType = "Long", name = "id", value = "skuId", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")
    })
    @Audit
    @GetMapping("/skus/{id}")
    public Object getSku(@PathVariable Long id, @LoginUser Long userId, @Depart Long departId) {
        if (logger.isDebugEnabled()) {
            logger.debug("getSku : skuId = " + id);
        }
        ReturnObject returnObj = goodsService.getSku(id, userId, departId);
        return Common.decorateReturnObject(returnObj);
    }

    /**
     * 查看一条商品SPU的详细信息
     *
     */
    @ApiOperation(value = "查看一条商品spu的详细信息")
    @ApiImplicitParam(name = "id", required = true, dataType = "Integer", paramType = "path", value = "spuId")
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @GetMapping("/spus/{id}")
    public Object getSpu(@PathVariable Long id) {
        if (logger.isDebugEnabled()) {
            logger.debug("getSku");
        }
        ReturnObject returnObj = goodsService.getSpu(id);
        return Common.decorateReturnObject(returnObj);
    }

    /**
     * 管理员添加新的SKU到SPU里
     *
     */
    @ApiOperation(value = "管理员添加新的SKU到SPU里")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Long", name = "shopId", value = "店铺id", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Long", name = "id", value = "spuId", required = true),
            @ApiImplicitParam(paramType = "body", dataType = "SkuCreatVo", name = "skuCreatVo", value = "新建需要的SKU信息", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 901, message = "商品规格重复")
    })
    @Audit
    @PostMapping("/shops/{shopId}/spus/{id}/skus")
    public Object createSku(@PathVariable Long shopId, @PathVariable Long id, @RequestBody SkuCreatVo skuCreatVo, @LoginUser Long userId) {
        if (logger.isDebugEnabled()) {
            logger.debug("createSKU : shopId = " + shopId + " skuId = " + id + " vo = " + skuCreatVo);
        }
        if (userId == null) {
            ReturnObject returnObj = goodsService.creatSku(id, shopId, skuCreatVo);
            if (returnObj.getCode() == ResponseCode.OK) {
                httpServletResponse.setStatus(201);
            } else if (returnObj.getCode() == ResponseCode.RESOURCE_ID_OUTSCOPE) {
                httpServletResponse.setStatus(403);
            }
            return Common.decorateReturnObject(returnObj);
        } else {
            return new ReturnObject<>(ResponseCode.AUTH_NEED_LOGIN);
        }

    }

    /**
     * 查看一条分享sku的详细信息
     *
     */
    @ApiOperation(value = "查看一条分享sku的详细信息")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Long", name = "sid", value = "分享id", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Long", name = "id", value = "skuId", required = true),
    })
    @Audit
    @GetMapping("/share/{sid}/skus/{id}")
    public Object getShare(@PathVariable Long sid, @PathVariable Long id, @LoginUser Long userId, @Depart Long departId) {
        ReturnObject returnObject = goodsService.getShare(sid, id, userId, departId);
        return Common.decorateReturnObject(returnObject);
    }
}