package cn.edu.xmu.goods.dao;

import cn.edu.xmu.external.service.IShareService;
import cn.edu.xmu.goods.mapper.*;
import cn.edu.xmu.goods.model.bo.*;
import cn.edu.xmu.goods.model.po.*;
import cn.edu.xmu.goods.model.vo.*;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.outer.model.bo.MyReturn;
//import cn.edu.xmu.outer.service.IFreightService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class GoodsDao {

    @Autowired
    GoodsSpuPoMapper goodsSpuPoMapper;

    @Autowired
    GoodsSkuPoMapper goodsSkuPoMapper;

    @Autowired
    FloatPricePoMapper floatPricePoMapper;

    @Autowired
    BrandPoMapper brandPoMapper;

    @Autowired
    GoodsCategoryPoMapper goodsCategoryPoMapper;

    @Autowired
    ShopPoMapper shopPoMapper;

    private static final Logger logger = LoggerFactory.getLogger(GoodsDao.class);

    /**
     * 已下架
     */
    public static final byte OFFSALE = 0;

    /**
     * 已上架
     */
    public static final byte ONSALE = 4;

    /**
     * 已删除
     */
    public static final byte DELETED = 6;

//    @Autowired
//    @DubboReference(version = "0.0.1", check = false)
//    private IFreightService iFreightService;

    @Autowired
    @DubboReference(version = "0.0.1", check = false)
    private IShareService iShareService;

    /**
     * 查找sku
     *
     */
    public GoodsSkuPo findGoodsSkuById(Long id) {
        GoodsSkuPoExample goodsSkuPoExample = new GoodsSkuPoExample();
        GoodsSkuPoExample.Criteria criteria = goodsSkuPoExample.createCriteria();
        criteria.andIdEqualTo(id);
        logger.debug("findGoodsSkuById : skuId=" + id);
        GoodsSkuPo goodsSkuPo = goodsSkuPoMapper.selectByPrimaryKey(id);
        return goodsSkuPo;
    }

    /**
     * 查找spu
     *
     */
    public GoodsSpuPo findGoodsSpuById(Long id) {
        logger.debug("findGoodsSpuById : spuId=" + id);
        GoodsSpuPo goodsSpuPo = goodsSpuPoMapper.selectByPrimaryKey(id);
        return goodsSpuPo;
    }

    /**
     * 上架商品(上架未上架未删除的商品)
     *
     */
    public ReturnObject<Object> putGoodsOnSaleById(Long shopId, Long skuId, Long code) {
        GoodsSkuPo goodsSkuPo = goodsSkuPoMapper.selectByPrimaryKey(skuId);
        if (goodsSkuPo == null || goodsSkuPo.getDisabled() != 0) {
            logger.debug("sku禁止访问");
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        Long shopid = findGoodsSpuById(goodsSkuPo.getGoodsSpuId()).getShopId();
        if (!shopid.equals(shopId)) {
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE);
        }
        if (goodsSkuPo.getState() == DELETED) {
            logger.debug("sku已删除");
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        if (goodsSkuPo.getState() == ONSALE) {
            logger.debug("sku已上架");
            return new ReturnObject<>(ResponseCode.STATE_NOCHANGE);
        }
        GoodsSku goodsSku = new GoodsSku(goodsSkuPo);
        GoodsSkuPo po = goodsSku.createUpdateStatePo(code);
        ReturnObject<Object> returnObject;
        try {
            int ret = goodsSkuPoMapper.updateByPrimaryKeySelective(po);
            // 检查更新有否成功
            if (ret == 0) {
                logger.info("商品不存在或已被删除：skuId = " + skuId);
                returnObject = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
            } else {
                logger.info("skuId = " + skuId + "已上架");
                returnObject = new ReturnObject<>();
            }
            return returnObject;
        } catch (Exception e) {
            logger.error("exception:" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误%s", e.getMessage()));
        }
    }

    /**
     * 下架商品
     *
     */
    public ReturnObject<Object> putOffGoodsOnSaleById(Long shopId, Long skuId, Long code) {
        GoodsSkuPo goodsSkuPo = goodsSkuPoMapper.selectByPrimaryKey(skuId);
        if (goodsSkuPo == null || goodsSkuPo.getDisabled() != 0) {
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        Long shopid = findGoodsSpuById(goodsSkuPo.getGoodsSpuId()).getShopId();
        if (!shopid.equals(shopId)) {
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE);
        }
        if (goodsSkuPo.getState() == DELETED) {
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        if (goodsSkuPo.getState() == OFFSALE) {
            return new ReturnObject<>(ResponseCode.STATE_NOCHANGE);
        }
        GoodsSku goodsSku = new GoodsSku(goodsSkuPo);
        GoodsSkuPo po = goodsSku.createUpdateStatePo(code);
        ReturnObject<Object> returnObject;
        int ret = goodsSkuPoMapper.updateByPrimaryKeySelective(po);
        // 检查更新有否成功
        if (ret == 0) {
            logger.info("商品不存在或已被删除：skuId = " + skuId);
            returnObject = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        } else {
            logger.info("skuId = " + skuId + "已下架");
            returnObject = new ReturnObject<>();
        }
        return returnObject;
    }

    /**
     * 逻辑删除(删除未上架，未删除)
     *
     */
    public ReturnObject<Object> deleteSkuById(Long shopId, Long skuId, Long code) {
        GoodsSkuPo goodsSkuPo = goodsSkuPoMapper.selectByPrimaryKey(skuId);
        if (goodsSkuPo == null || goodsSkuPo.getDisabled() != 0) {
            logger.debug("sku或禁止访问");
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        Long shopid = findGoodsSpuById(goodsSkuPo.getGoodsSpuId()).getShopId();
        if (!shopid.equals(shopId)) {
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE);
        }
        if (goodsSkuPo.getState() == DELETED) {
            logger.debug("sku已删除");
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        GoodsSku goodsSku = new GoodsSku(goodsSkuPo);
        GoodsSkuPo po = goodsSku.createUpdateStatePo(code);
        ReturnObject<Object> returnObject;
        try {
            int ret = goodsSkuPoMapper.updateByPrimaryKeySelective(po);
            // 检查更新有否成功
            if (ret == 0) {
                logger.info("商品不存在或已被删除：skuId = " + skuId);
                returnObject = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR);
            } else {
                logger.info("skuId = " + skuId + "已删除");
                returnObject = new ReturnObject<>(ResponseCode.OK);
            }
            return returnObject;
        } catch (Exception e) {
            logger.error("exception:" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误%s", e.getMessage()));
        }
    }

    /**
     * 查看所有sku
     *
     */
    public ReturnObject<PageInfo<VoObject>> findSkuSimple(Long shopId, Integer page, Integer
            pageSize, Long spuId, String skuSn, String spuSn) {

        GoodsSkuPoExample example = new GoodsSkuPoExample();
        GoodsSkuPoExample.Criteria criteria = example.createCriteria();
        //spu
        GoodsSpuPoExample spuPoExample = new GoodsSpuPoExample();
        GoodsSpuPoExample.Criteria spuCriteria = spuPoExample.createCriteria();
        //参数判断
        if (skuSn != null) {
            criteria.andSkuSnEqualTo(skuSn);
        }
        if (shopId != null) {
            spuCriteria.andShopIdEqualTo(shopId);
        }
        if (spuSn != null) {
            spuCriteria.andGoodsSnEqualTo(spuSn);
        }
        List<GoodsSpuPo> goodsSpuPos;
        PageHelper.startPage(page, pageSize);
        if (spuSn != null || shopId != null) {
            goodsSpuPos = goodsSpuPoMapper.selectByExample(spuPoExample);
            if (goodsSpuPos.size() == 0) {
                List<VoObject> empty = new ArrayList<>(goodsSpuPos.size());
                PageInfo<VoObject> rolePage1 = PageInfo.of(empty);
                rolePage1.setPages((PageInfo.of(goodsSpuPos).getPages()));
                rolePage1.setTotal((PageInfo.of(goodsSpuPos).getTotal()));
                rolePage1.setPageNum(page);
                rolePage1.setPageSize(pageSize);
                return new ReturnObject<>(rolePage1);
            }
        } else {
            goodsSpuPos = null;
        }
        Long spuFindId = null;

        if (goodsSpuPos != null) {
            spuFindId = goodsSpuPos.get(0).getId();
            criteria.andGoodsSpuIdEqualTo(spuFindId);
        }
        if (spuId != null) {
            if (!spuId.equals(spuFindId) && spuFindId != null) {
                return new ReturnObject<>(ResponseCode.FIELD_NOTVALID, "输入spuId和spu信息查询结果不符");
            }
            criteria.andGoodsSpuIdEqualTo(spuId);
        }
        List<GoodsSkuPo> goodsSkuPos;
        try {
            goodsSkuPos = goodsSkuPoMapper.selectByExample(example);
            List<VoObject> ret = new ArrayList<>(goodsSkuPos.size());
            for (GoodsSkuPo po : goodsSkuPos) {
                GoodsSku sku = new GoodsSku(po);
                FindSkuRet skuRet = new FindSkuRet(sku);
                skuRet.setPrice(getPrice(skuRet.getId()));
                FindSkuRetVo skuRetVo = new FindSkuRetVo(skuRet);
                ret.add(skuRetVo);
            }
            PageInfo<VoObject> rolePage = PageInfo.of(ret);
            rolePage.setPages((PageInfo.of(goodsSkuPos).getPages()));
            rolePage.setTotal((PageInfo.of(goodsSkuPos).getTotal()));
            rolePage.setPageNum(page);
            rolePage.setPageSize(pageSize);
            return new ReturnObject<>(rolePage);
        } catch (DataAccessException e) {
            logger.error("findSkuSimple: DataAccessException:" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR);
        } catch (Exception e) {
            logger.error("exception:" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误%s", e.getMessage()));
        }
    }

    /**
     * 修改sku
     *
     */
    public ReturnObject<Object> modifySkuById(Long shopId, Long skuId, SkuInputVo skuInputVo) {
        GoodsSkuPo goodsSkuPo;
        goodsSkuPo = goodsSkuPoMapper.selectByPrimaryKey(skuId);
        if (goodsSkuPo == null || goodsSkuPo.getDisabled() != 0) {
            logger.info("skuId = " + skuId + " 不存在");
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        Long shopid = findGoodsSpuById(goodsSkuPo.getGoodsSpuId()).getShopId();
        if (!shopid.equals(shopId)) {
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE);
        }

        GoodsSkuPoExample skuPoExample = new GoodsSkuPoExample();
        GoodsSkuPoExample.Criteria criteria = skuPoExample.createCriteria();
        criteria.andNameEqualTo(skuInputVo.getName());
        criteria.andOriginalPriceEqualTo(skuInputVo.getOriginalPrice());
        criteria.andConfigurationEqualTo(skuInputVo.getConfiguration());
        criteria.andInventoryEqualTo(skuInputVo.getInventory());
        criteria.andWeightEqualTo(skuInputVo.getWeight());
        criteria.andDetailEqualTo(skuInputVo.getDetail());
        List<GoodsSkuPo> goodsSkuPos = goodsSkuPoMapper.selectByExample(skuPoExample);
        if (!goodsSkuPos.isEmpty()) {
            return new ReturnObject<>(ResponseCode.SKUSN_SAME);
        }
        GoodsSku goodsSku = new GoodsSku(goodsSkuPo);
        GoodsSkuPo po = goodsSku.createUpdatePo(skuInputVo);
        goodsSkuPoMapper.updateByPrimaryKeySelective(po);
        return new ReturnObject<>();
    }

    /**
     * 失效商品价格浮动
     *
     */
    public ReturnObject<Object> invalidFloatPriceById(Long shopId, Long id, Long loginUserId) {
        FloatPricePo floatPricePo = floatPricePoMapper.selectByPrimaryKey(id);
        if (floatPricePo == null || floatPricePo.getValid() == 0) {
            logger.info("商品价格浮动不存在");
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        GoodsSkuPo goodsSkuPo = findGoodsSkuById(floatPricePo.getGoodsSkuId());
        if (goodsSkuPo == null) {
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, "商品价格浮动不存在");
        }
        Long goodsSpuId = goodsSkuPo.getGoodsSpuId();
        GoodsSpuPo goodsSpuPo = goodsSpuPoMapper.selectByPrimaryKey(goodsSpuId);
        Long shopid = goodsSpuPo.getShopId();
        if (shopid.equals(shopId)) {
            floatPricePo.setValid((byte) 0);
            floatPricePo.setGmtModified(LocalDateTime.now());
            floatPricePo.setInvalidBy(loginUserId);
            floatPricePoMapper.updateByPrimaryKeySelective(floatPricePo);
            logger.debug("invalidFloatPriceById : shopId = " + shopId + " floatPriceId = " + id + " invalidBy " + loginUserId);
            return new ReturnObject<>(ResponseCode.OK);
        } else {
            logger.debug("error");
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE);
        }
    }

    /**
     * 管理员新增商品类目
     *
     */
    public GoodsCategoryPo addCategoryById(Long id, CategoryInputVo categoryInputVo) {
        if (id != 0) {
            GoodsCategoryPo po = goodsCategoryPoMapper.selectByPrimaryKey(id);
            if (po == null) {
                logger.info("categoryId = " + id + "不存在");
                return null;
            }
            GoodsCategory goodsCategory = new GoodsCategory();
            GoodsCategoryPo goodsCategoryPo = goodsCategory.createAddPo(id, categoryInputVo);
            goodsCategoryPoMapper.insertSelective(goodsCategoryPo);
            return goodsCategoryPo;
        } else {
            GoodsCategory goodsCategory = new GoodsCategory();
            GoodsCategoryPo goodsCategoryPo = goodsCategory.createAddPo(id, categoryInputVo);
            int ret = goodsCategoryPoMapper.insertSelective(goodsCategoryPo);
            if (ret == 0) {
                goodsCategoryPo = null;
            } else {
                logger.info("categoryId = " + id + " 的信息已新增成功");
            }
            return goodsCategoryPo;
        }
    }

    /**
     * 修改商品类目
     *
     */
    public ReturnObject<Object> modifyCategoryById(Long id, CategoryInputVo categoryInputVo) {
        GoodsCategoryPo po = goodsCategoryPoMapper.selectByPrimaryKey(id);
        if (po == null) {
            logger.info("商品类目不存在或已被删除：categoryId = " + id);
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        if (categoryInputVo.getName() == null || categoryInputVo.getName().length() == 0) {
            return new ReturnObject<>(ResponseCode.FIELD_NOTVALID, "商品类目名称不能为空");
        }
        GoodsCategoryPoExample goodsCategoryPoExample = new GoodsCategoryPoExample();
        GoodsCategoryPoExample.Criteria criteria = goodsCategoryPoExample.createCriteria();
        criteria.andNameEqualTo(categoryInputVo.getName());
        List<GoodsCategoryPo> goodsCategoryPos = goodsCategoryPoMapper.selectByExample(goodsCategoryPoExample);
        if (!goodsCategoryPos.isEmpty()) {
            return new ReturnObject<>(ResponseCode.CATEGORY_NAME_SAME);
        }

        GoodsCategory goodsCategory = new GoodsCategory(po);
        GoodsCategoryPo goodsCategoryPo = goodsCategory.createUpdatePo(categoryInputVo);
        int ret = goodsCategoryPoMapper.updateByPrimaryKeySelective(goodsCategoryPo);
        ReturnObject<Object> returnObject;
        if (ret == 0) {
            //检查更新是否成功
            logger.info("商品类目不存在或已被删除：goodsCategoryId = " + id);
            returnObject = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR);
        } else {
            logger.info("categoryId = " + id + " 的信息已更新");
            returnObject = new ReturnObject<>();
        }
        return returnObject;
    }

    /**
     * 删除商品类目
     *
     */
    public ReturnObject<Object> deleteCategoryById(Long id) {
        GoodsCategoryPo po = goodsCategoryPoMapper.selectByPrimaryKey(id);
        if (po == null) {
            logger.info("商品类目不存在或已被删除：categoryId = " + id);
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        int ret = goodsCategoryPoMapper.deleteByPrimaryKey(id);
        ReturnObject<Object> returnObject;
        if (ret == 0) {
            logger.info("商品类目不存在或已被删除：goodsCategoryId = " + id);
            returnObject = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        } else {
            GoodsSpuPoExample goodsSpuPoExample = new GoodsSpuPoExample();
            GoodsSpuPoExample.Criteria criteria = goodsSpuPoExample.createCriteria();
            criteria.andCategoryIdEqualTo(id);
            List<GoodsSpuPo> goodsSpuPos = goodsSpuPoMapper.selectByExample(goodsSpuPoExample);
            for (GoodsSpuPo goodsSpuPo : goodsSpuPos) {
                goodsSpuPo.setCategoryId(0L);
                goodsSpuPoMapper.updateByPrimaryKeySelective(goodsSpuPo);
            }
            GoodsCategoryPoExample goodsCategoryPoExample = new GoodsCategoryPoExample();
            GoodsCategoryPoExample.Criteria criteria1 = goodsCategoryPoExample.createCriteria();
            criteria1.andPidEqualTo(id);
            List<GoodsCategoryPo> goodsCategoryPos = goodsCategoryPoMapper.selectByExample(goodsCategoryPoExample);
            for (GoodsCategoryPo goodsCategoryPo : goodsCategoryPos) {
                Long categoryId = goodsCategoryPo.getId();
                deleteCategoryById(categoryId);
            }
            returnObject = new ReturnObject<>();
        }
        return returnObject;
    }

    /**
     * 修改部分信息
     *
     */
    public ReturnObject modifySpuBySpuPoId(GoodsSpuPo goodsSpuPo) {
        ReturnObject returnObject;
        int ret = goodsSpuPoMapper.updateByPrimaryKeySelective(goodsSpuPo);
        if (ret == 0) {
            logger.info("spuId = " + goodsSpuPo.getId() + " 不存在");
            returnObject = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        } else {
            logger.info("spuId" + goodsSpuPo.getId() + "已修改");
            returnObject = new ReturnObject<>();
        }
        return returnObject;
    }

    /**
     * 修改全部信息，针对有些值要设置为null的时候修改部分信息不能将参数传进去修改
     *
     */
    public ReturnObject modifySpuBySpuPo(GoodsSpuPo goodsSpuPo) {
        ReturnObject returnObject = null;
        int ret = goodsSpuPoMapper.updateByPrimaryKey(goodsSpuPo);
        if (ret == 0) {
            logger.info("spuId = " + goodsSpuPo.getId() + " 不存在");
            returnObject = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        } else {
            logger.info("spuId" + goodsSpuPo.getId() + "已修改");
            returnObject = new ReturnObject<>();
        }
        return returnObject;
    }

    /**
     * 查询商品分类关系
     *
     */
    public ReturnObject<List> getCategoryByPid(Long id) {
        //查看是否有此分类
        if (id != 0) {
            GoodsCategoryPo categoryPo = goodsCategoryPoMapper.selectByPrimaryKey(id);
            if (categoryPo == null) {
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
            }
            GoodsCategoryPoExample example = new GoodsCategoryPoExample();
            GoodsCategoryPoExample.Criteria criteria = example.createCriteria();
            criteria.andPidEqualTo(id);
            List<GoodsCategoryPo> goodsCategoryPos = goodsCategoryPoMapper.selectByExample(example);
            List<GoodsCategory> goodsCategories = new ArrayList<>(goodsCategoryPos.size());
            for (GoodsCategoryPo po : goodsCategoryPos) {
                goodsCategories.add(new GoodsCategory(po));
            }
            return new ReturnObject<>(goodsCategories);
        } else {
            GoodsCategoryPoExample example = new GoodsCategoryPoExample();
            GoodsCategoryPoExample.Criteria criteria = example.createCriteria();
            criteria.andPidEqualTo(id);
            List<GoodsCategoryPo> goodsCategoryPos = goodsCategoryPoMapper.selectByExample(example);
            List<GoodsCategory> goodsCategories = new ArrayList<>(goodsCategoryPos.size());
            for (GoodsCategoryPo po : goodsCategoryPos) {
                goodsCategories.add(new GoodsCategory(po));
            }
            return new ReturnObject<>(goodsCategories);
        }
    }

    /**
     *
     */
    public GoodsCategoryPo getCategoryById(Long id) {
        return goodsCategoryPoMapper.selectByPrimaryKey(id);
    }

    /**
     * 查询所有品牌
     *
     */
    public ReturnObject<PageInfo<VoObject>> findAllBrand(Integer page, Integer pageSize) {
        BrandPoExample example = new BrandPoExample();
        BrandPoExample.Criteria criteria = example.createCriteria();
        List<BrandPo> brandPos = null;
        PageHelper.startPage(page, pageSize);
        brandPos = brandPoMapper.selectByExample(example);
        try {
            List<VoObject> ret = new ArrayList<>(brandPos.size());
            for (BrandPo po : brandPos) {
                Brand bran = new Brand(po);
                ret.add(bran);
            }
            PageInfo<VoObject> rolePage = PageInfo.of(ret);
            rolePage.setPages(PageInfo.of(brandPos).getPages());
            rolePage.setPageNum(page);
            rolePage.setPageSize(pageSize);
            rolePage.setTotal(PageInfo.of(brandPos).getTotal());
            return new ReturnObject<>(rolePage);
        } catch (DataAccessException e) {
            logger.error("findAllBrand: DataAccessException:" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR);
        }
    }

    /**
     * 删除品牌(物理删除)
     *
     */
    public ReturnObject<Object> deleteBrandById(Long id) {
        BrandPo brandPo = brandPoMapper.selectByPrimaryKey(id);
        if (brandPo == null) {
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        ReturnObject<Object> returnObject;
        int ret = brandPoMapper.deleteByPrimaryKey(id);
        if (ret == 0) {
            returnObject = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR);
        } else {
            GoodsSpuPoExample goodsSpuPoExample = new GoodsSpuPoExample();
            GoodsSpuPoExample.Criteria criteria = goodsSpuPoExample.createCriteria();
            criteria.andBrandIdEqualTo(id);
            List<GoodsSpuPo> goodsSpuPos = goodsSpuPoMapper.selectByExample(goodsSpuPoExample);
            for (GoodsSpuPo goodsSpuPo : goodsSpuPos) {
                goodsSpuPo.setBrandId(0L);
                goodsSpuPoMapper.updateByPrimaryKeySelective(goodsSpuPo);
            }
            returnObject = new ReturnObject<>(ResponseCode.OK);
        }
        return returnObject;
    }

    /**
     * 修改品牌信息
     *
     */
    public ReturnObject modifyBrandById(Long shopId, Long id, BrandInputVo brandInputVo) {
        BrandPo brandPo = brandPoMapper.selectByPrimaryKey(id);
        if (brandPo == null) {
            logger.info("brandId = " + id + " 不存在");
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        if (brandInputVo.getName() == null || brandInputVo.getName().length() == 0) {
            return new ReturnObject<>(ResponseCode.FIELD_NOTVALID, "品牌名称不能为空");
        }
        BrandPoExample brandPoExample = new BrandPoExample();
        BrandPoExample.Criteria criteria = brandPoExample.createCriteria();
        criteria.andNameEqualTo(brandInputVo.getName());
        List<BrandPo> brandPos = brandPoMapper.selectByExample(brandPoExample);
        if (!brandPos.isEmpty()) {
            return new ReturnObject<>(ResponseCode.BRAND_NAME_SAME);
        }
        Brand brand = new Brand(brandPo);
        BrandPo po = brand.createUpdatePo(brandInputVo);
        ReturnObject<Object> returnObject;
        int ret = brandPoMapper.updateByPrimaryKeySelective(po);
        // 检查更新有否成功
        if (ret == 0) {
            logger.info("brandId = " + id + " 不存在");
            returnObject = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR);
        } else {
            logger.info("brandId = " + id + " 的信息已更新");
            returnObject = new ReturnObject<>(ResponseCode.OK);
        }
        return returnObject;
    }

    /**
     * 管理员新增品牌
     *
     */
    public BrandPo addBrandById(BrandInputVo brandInputVo) {
        Brand brand = new Brand();
        try {
            if (brandInputVo.getName() == null) {
                return null;
            }
            BrandPo brandPo = brand.createAddPo(brandInputVo);
            int ret = brandPoMapper.insertSelective(brandPo);
            if (ret == 0) {
                //检查新增是否成功
                brandPo = null;
            } else {
                logger.info("品牌已新建成功");
            }
            return brandPo;
        } catch (Exception e) {
            logger.error("exception:" + e.getMessage());
            return null;
        }
    }

    /**
     *
     */
    public BrandPo findBrandById(Long id) {
        return brandPoMapper.selectByPrimaryKey(id);
    }

    /**
     *
     */
    public ReturnObject<GoodsSku> getGoodsSkuById(Long id) {
        GoodsSkuPo goodsSkuPo = goodsSkuPoMapper.selectByPrimaryKey(id);
        if (goodsSkuPo == null) {
            return new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        GoodsSku goodsSku = new GoodsSku(goodsSkuPo);
        return new ReturnObject<>(goodsSku);
    }

    /**
     * @param id
     * @return
     */
    public ReturnObject<GoodsSpu> getGoodsSpuById(Long id) {
        GoodsSpuPo goodsSpuPo = goodsSpuPoMapper.selectByPrimaryKey(id);
        if (goodsSpuPo == null) {
            return new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        GoodsSpu goodsSpu = new GoodsSpu(goodsSpuPo);
        return new ReturnObject<>(goodsSpu);
    }

    /**
     *
     */
    public ReturnObject<Brand> getBrandById(Long id) {
        BrandPo brandPo = brandPoMapper.selectByPrimaryKey(id);
        if (brandPo == null) {
            return new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        Brand brand = new Brand(brandPo);
        return new ReturnObject<>(brand);
    }

    /**
     * @param goodsSku
     * @return
     */
    public ReturnObject updateGoodsSkuImgUrl(GoodsSku goodsSku) {
        ReturnObject returnObject = new ReturnObject();
        GoodsSkuPo goodsSkuPo = new GoodsSkuPo();
        goodsSkuPo.setId(goodsSku.getId());
        goodsSkuPo.setImageUrl(goodsSku.getImageUrl());
        int ret = goodsSkuPoMapper.updateByPrimaryKeySelective(goodsSkuPo);
        if (ret == 0) {
            logger.debug("updateGoodsSkuImgUrl: update fail. sku id: " + goodsSku.getId());
            returnObject = new ReturnObject(ResponseCode.FIELD_NOTVALID);
        } else {
            logger.debug("updateGoodsSkuImgUrl: update sku success : " + goodsSku.toString());
            returnObject = new ReturnObject();
        }
        return returnObject;
    }

    /**
     * @param
     * @return
     */
    public ReturnObject updateGoodsSpuImgUrl(GoodsSpu goodsSpu) {
        ReturnObject returnObject = new ReturnObject();
        GoodsSpuPo goodsSpuPo = new GoodsSpuPo();
        goodsSpuPo.setId(goodsSpu.getId());
        goodsSpuPo.setImageUrl(goodsSpu.getImageUrl());
        int ret = goodsSpuPoMapper.updateByPrimaryKeySelective(goodsSpuPo);
        if (ret == 0) {
            logger.debug("updateGoodsSpuImgUrl: update fail. spu id: " + goodsSpu.getId());
            returnObject = new ReturnObject(ResponseCode.FIELD_NOTVALID);
        } else {
            logger.debug("updateGoodsSpuImgUrl: update sku success : " + goodsSpu.toString());
            returnObject = new ReturnObject();
        }
        return returnObject;
    }

    /**
     *
     *
     */
    public ReturnObject updateBrandImgUrl(Brand brand) {
        ReturnObject returnObject = new ReturnObject();
        BrandPo brandPo = new BrandPo();
        brandPo.setId(brand.getId());
        brandPo.setImageUrl(brand.getImageUrl());
        int ret = brandPoMapper.updateByPrimaryKeySelective(brandPo);
        if (ret == 0) {
            logger.debug("updateBrandImgUrl: update fail. brand id: " + brand.getId());
            returnObject = new ReturnObject(ResponseCode.FIELD_NOTVALID);
        } else {
            logger.debug("updateBrandSpuImgUrl: update brand success : " + brand.toString());
            returnObject = new ReturnObject();
        }
        return returnObject;
    }

    /**
     *
     *
     */
    public ReturnObject deleteSpuById(Long shopId, Long id) {
        try {
            GoodsSpuPo goodsSpuPo = goodsSpuPoMapper.selectByPrimaryKey(id);
            if (goodsSpuPo == null || goodsSpuPo.getDisabled() != 0) {
                if (logger.isDebugEnabled()) {
                    logger.debug("不存在或禁止访问");
                }
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
            }
            Long shopid = goodsSpuPo.getShopId();
            if (!shopid.equals(shopId)) {
                logger.debug("访问不合法");
                return new ReturnObject<>(ResponseCode.AUTH_NOT_ALLOW);
            }
            GoodsSkuPoExample example = new GoodsSkuPoExample();
            GoodsSkuPoExample.Criteria criteria = example.createCriteria();
            criteria.andGoodsSpuIdEqualTo(id);
            List<GoodsSkuPo> goodsSkuPos = goodsSkuPoMapper.selectByExample(example);
            if (goodsSkuPos.isEmpty()) {
                goodsSpuPoMapper.deleteByPrimaryKey(id);
                return new ReturnObject(ResponseCode.OK);
            } else {
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, "spu不合法");
            }
        } catch (Exception e) {
            logger.error("exception:" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误%s", e.getMessage()));
        }
    }

    /**
     * 新增商品价格浮动
     *
     */
    public ReturnObject addFloatPrice(Long shopId, Long id, FloatPriceInputVo floatPriceInputVo, Long userId) {
        GoodsSkuPo goodsSkuPo = goodsSkuPoMapper.selectByPrimaryKey(id);
        if (goodsSkuPo == null || goodsSkuPo.getDisabled() != 0) {
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        Long spuId = goodsSkuPo.getGoodsSpuId();
        GoodsSpuPo goodsSpuPo = goodsSpuPoMapper.selectByPrimaryKey(spuId);
        if (goodsSpuPo == null || goodsSpuPo.getDisabled() != 0) {
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        Long shopid = goodsSpuPo.getShopId();
        if (!shopid.equals(shopId)) {
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE);
        }
        LocalDateTime beginTime = floatPriceInputVo.getBeginTime();
        LocalDateTime endTime = floatPriceInputVo.getEndTime();
        if (beginTime == null) {
            return new ReturnObject(ResponseCode.FIELD_NOTVALID);
        } else if (endTime == null) {
            return new ReturnObject(ResponseCode.FIELD_NOTVALID);
        }
        if (beginTime.isBefore(LocalDateTime.now())) {
            return new ReturnObject(ResponseCode.FIELD_NOTVALID);
        }
        if (floatPriceInputVo.getQuantity() < 0) {
            return new ReturnObject(ResponseCode.FIELD_NOTVALID);
        }
        if (beginTime.isAfter(endTime)) {
            //开始时间不能比结束时间晚
            return new ReturnObject<>(ResponseCode.FIELD_NOTVALID);
        } else {
            //设置的库存不能大于总库存
            if (goodsSkuPo.getInventory() < floatPriceInputVo.getQuantity()) {
                logger.debug("库存数量不够");
                return new ReturnObject<>(ResponseCode.SKU_NOTENOUGH);
            } else {
                //一个商品只能有一个价格浮动在使用
                //查找当前时间段重合，生效，且skuId的商品价格浮动
                FloatPricePoExample floatPricePoExample = new FloatPricePoExample();
                FloatPricePoExample.Criteria criteria = floatPricePoExample.createCriteria();
                criteria.andGoodsSkuIdEqualTo(id);
                criteria.andEndTimeGreaterThan(beginTime);
                criteria.andValidEqualTo((byte) 1);
                List<FloatPricePo> floatPricePos = floatPricePoMapper.selectByExample(floatPricePoExample);
                //如果存在，则返回时间段冲突
                if (!floatPricePos.isEmpty()) {
                    logger.debug("时间段冲突");
                    return new ReturnObject<>(ResponseCode.SKUPRICE_CONFLICT);
                } else {
                    //时间段不冲突则新建一个商品价格浮动并且设为生效
                    FloatPricePo floatPricePo = new FloatPricePo();
                    floatPricePo.setGoodsSkuId(id);
                    floatPricePo.setActivityPrice(floatPriceInputVo.getActivityPrice());
                    floatPricePo.setBeginTime(beginTime);
                    floatPricePo.setEndTime(endTime);
                    floatPricePo.setCreatedBy(userId);
                    floatPricePo.setQuantity(floatPriceInputVo.getQuantity());
                    floatPricePo.setGmtCreate(LocalDateTime.now());
                    floatPricePo.setInvalidBy(userId);
                    floatPricePo.setValid((byte) 1);
                    floatPricePoMapper.insertSelective(floatPricePo);
                    return new ReturnObject(new FloatPriceRetVo(floatPricePo));
                }
            }
        }
    }

    /**
     * 新增商品spu
     *
     *
     */
    public ReturnObject addSpu(Long shopId, SpuInputVo spuInputVo) {
        GoodsSpu goodsSpu = new GoodsSpu();
        GoodsSpuPo goodsSpuPo = goodsSpu.createUpdatePo(shopId, spuInputVo);
        goodsSpuPoMapper.insertSelective(goodsSpuPo);
        SpuRetVo spuRetVo = new SpuRetVo();
        spuRetVo.setId(goodsSpuPo.getId());
        spuRetVo.setSpec(goodsSpuPo.getSpec());
        spuRetVo.setName(goodsSpuPo.getName());
        spuRetVo.setDetail(goodsSpuPo.getDetail());
        spuRetVo.setGoodsSn(goodsSpuPo.getGoodsSn());
        spuRetVo.setImageUrl(goodsSpuPo.getImageUrl());
        spuRetVo.setGmtCreate(goodsSpuPo.getGmtCreate());
        spuRetVo.setGmtModified(goodsSpuPo.getGmtModified());
        spuRetVo.setDisable(false);
        SimpleShopVo simpleShopVo = new SimpleShopVo();
        if (shopPoMapper.selectByPrimaryKey(shopId) != null) {
            simpleShopVo.setId(shopId);
            simpleShopVo.setName(shopPoMapper.selectByPrimaryKey(shopId).getName());
            spuRetVo.setShop(simpleShopVo);
        }
        spuRetVo.setSkuList(new ArrayList<>());
        return new ReturnObject(spuRetVo);
    }

    /**
     * 获得sku详细信息
     *
     *
     */
    public ReturnObject getSku(Long id, Long departId) {
        GoodsSkuPo goodsSkuPo = goodsSkuPoMapper.selectByPrimaryKey(id);
        //只能看到上架的商品
        if (departId == null || departId < 0) {
            if (goodsSkuPo == null || goodsSkuPo.getDisabled() != 0 || goodsSkuPo.getState() != 4) {
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
            }
            SkuReturnVo skuReturnVo = new SkuReturnVo();
            skuReturnVo.setId(id);
            skuReturnVo.setName(goodsSkuPo.getName());
            skuReturnVo.setSkuSn(goodsSkuPo.getSkuSn());
            skuReturnVo.setDetail(goodsSkuPo.getDetail());
            skuReturnVo.setImageUrl(goodsSkuPo.getImageUrl());
            skuReturnVo.setOriginalPrice(goodsSkuPo.getOriginalPrice());
            //获得现价
            Long price = getPrice(id);
            if (price == null) {
                skuReturnVo.setPrice(goodsSkuPo.getOriginalPrice());
            } else {
                skuReturnVo.setPrice(price);
            }
            skuReturnVo.setInventory(goodsSkuPo.getInventory());
            skuReturnVo.setState(goodsSkuPo.getState());
            skuReturnVo.setConfiguration(goodsSkuPo.getConfiguration());
            skuReturnVo.setWeight(goodsSkuPo.getWeight());
            skuReturnVo.setGmtCreate(goodsSkuPo.getGmtCreate());
            skuReturnVo.setGmtModified(goodsSkuPo.getGmtModified());
            skuReturnVo.setDisable(goodsSkuPo.getDisabled() != 0);
            SpuRetVo spuRetVo = getSpuRetVo(id);
            skuReturnVo.setSpu(spuRetVo);
            return new ReturnObject(skuReturnVo);
        } else {
            if (goodsSkuPo == null || goodsSkuPo.getDisabled() != 0) {
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
            }
            SkuReturnVo skuReturnVo = new SkuReturnVo();
            skuReturnVo.setId(id);
            skuReturnVo.setName(goodsSkuPo.getName());
            skuReturnVo.setSkuSn(goodsSkuPo.getSkuSn());
            skuReturnVo.setDetail(goodsSkuPo.getDetail());
            skuReturnVo.setImageUrl(goodsSkuPo.getImageUrl());
            skuReturnVo.setOriginalPrice(goodsSkuPo.getOriginalPrice());
            //获得现价
            Long price = getPrice(id);
            if (price == null) {
                skuReturnVo.setPrice(goodsSkuPo.getOriginalPrice());
            } else {
                skuReturnVo.setPrice(price);
            }
            skuReturnVo.setInventory(goodsSkuPo.getInventory());
            skuReturnVo.setState(goodsSkuPo.getState());
            skuReturnVo.setConfiguration(goodsSkuPo.getConfiguration());
            skuReturnVo.setWeight(goodsSkuPo.getWeight());
            skuReturnVo.setGmtCreate(goodsSkuPo.getGmtCreate());
            skuReturnVo.setGmtModified(goodsSkuPo.getGmtModified());
            skuReturnVo.setDisable(goodsSkuPo.getDisabled() != 0);
            SpuRetVo spuRetVo = getSpuRetVo(id);
            skuReturnVo.setSpu(spuRetVo);
            return new ReturnObject(skuReturnVo);
        }
    }


    /**
     * 获得spu的详细信息
     *
     *
     */
    public ReturnObject getSpu(Long id) {
        SpuRetVo spuRetVo = getSpuRetVo(id);
        if (spuRetVo == null) {
            return new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        return new ReturnObject(spuRetVo);
    }

    /**
     * 获得现价
     *
     *
     */
    public Long getPrice(Long id) {
        //查找有效的价格浮动，记录现价
        LocalDateTime localDateTime = LocalDateTime.now();
        FloatPricePoExample floatPricePoExample = new FloatPricePoExample();
        FloatPricePoExample.Criteria criteria = floatPricePoExample.createCriteria();
        criteria.andGoodsSkuIdEqualTo(id);
        criteria.andBeginTimeLessThanOrEqualTo(localDateTime);
        criteria.andEndTimeGreaterThanOrEqualTo(localDateTime);
        criteria.andValidEqualTo((byte) 1);
        List<FloatPricePo> floatPricePos = floatPricePoMapper.selectByExample(floatPricePoExample);
        //如果有合法的价格浮动，记录价格浮动的价格
        if (floatPricePos.isEmpty()) {
            return null;
        } else {
            return floatPricePos.get(0).getActivityPrice();
        }
    }

    /**
     *
     *
     */
    public SpuRetVo getSpuRetVo(Long id) {
        SpuRetVo spuRetVo = new SpuRetVo();
        GoodsSpuPo goodsSpuPo = goodsSpuPoMapper.selectByPrimaryKey(id);
        if (goodsSpuPo == null) {
            return null;
        }
        spuRetVo.setId(goodsSpuPo.getId());
        spuRetVo.setName(goodsSpuPo.getName());
        //记录品牌信息
        Long brandId = goodsSpuPo.getBrandId();
        if (brandId != null) {
            if (brandId != 0) {
                BrandPo brandPo = brandPoMapper.selectByPrimaryKey(brandId);
                if (brandPo != null) {
                    SimpleBrandVo simpleBrandVo = new SimpleBrandVo();
                    simpleBrandVo.setId(brandPo.getId());
                    simpleBrandVo.setName(brandPo.getName());
                    if (brandPo.getImageUrl() != null) {
                        simpleBrandVo.setImageUrl(brandPo.getImageUrl());
                    }
                    spuRetVo.setBrand(simpleBrandVo);
                }
            } else {
                SimpleBrandVo simpleBrandVo = new SimpleBrandVo();
                simpleBrandVo.setId(0L);
                spuRetVo.setBrand(simpleBrandVo);
            }
        }
        //记录分类信息
        Long categoryId = goodsSpuPo.getCategoryId();
        if (categoryId != null) {
            if (categoryId != 0) {
                GoodsCategoryPo goodsCategoryPo = goodsCategoryPoMapper.selectByPrimaryKey(categoryId);
                if (goodsCategoryPo != null) {
                    SimpleCategoryVo simpleCategoryVo = new SimpleCategoryVo();
                    simpleCategoryVo.setId(goodsCategoryPo.getId());
                    simpleCategoryVo.setName(goodsCategoryPo.getName());
                    spuRetVo.setCategory(simpleCategoryVo);
                }
            } else {
                SimpleCategoryVo simpleCategoryVo = new SimpleCategoryVo();
                simpleCategoryVo.setId(0L);
                spuRetVo.setCategory(simpleCategoryVo);
            }
        }
        //记录店铺信息
        Long shopId = goodsSpuPo.getShopId();
        if (shopId != null) {
            ShopPo shopPo = shopPoMapper.selectByPrimaryKey(shopId);
            if (shopPo != null) {
                SimpleShopVo simpleShopVo = new SimpleShopVo();
                simpleShopVo.setId(shopId);
                simpleShopVo.setName(shopPo.getName());
                spuRetVo.setShop(simpleShopVo);
            }
        }
        spuRetVo.setGoodsSn(goodsSpuPo.getGoodsSn());
        spuRetVo.setDetail(goodsSpuPo.getDetail());
        spuRetVo.setImageUrl(goodsSpuPo.getImageUrl());
        spuRetVo.setSpec(goodsSpuPo.getSpec());
        spuRetVo.setGmtCreate(goodsSpuPo.getGmtCreate());
        spuRetVo.setGmtModified(goodsSpuPo.getGmtModified());
        spuRetVo.setDisable(goodsSpuPo.getDisabled() != 0);
        //记录skuList信息
        GoodsSkuPoExample goodsSkuPoExample = new GoodsSkuPoExample();
        GoodsSkuPoExample.Criteria criteria1 = goodsSkuPoExample.createCriteria();
        criteria1.andGoodsSpuIdEqualTo(id);
        List<GoodsSkuPo> goodsSkuPos = goodsSkuPoMapper.selectByExample(goodsSkuPoExample);
        List<SimpleSkuVo> simpleSkuVos = new ArrayList<>();
        SimpleSkuVo simpleSkuVo = new SimpleSkuVo();
        for (GoodsSkuPo goodsSkuPo1 : goodsSkuPos) {
            simpleSkuVo.setId(goodsSkuPo1.getId());
            simpleSkuVo.setName(goodsSkuPo1.getName());
            simpleSkuVo.setInventory(goodsSkuPo1.getInventory());
            simpleSkuVo.setOriginalPrice(goodsSkuPo1.getOriginalPrice());
            simpleSkuVo.setImageUrl(goodsSkuPo1.getImageUrl());
            simpleSkuVo.setSkuSn(goodsSkuPo1.getSkuSn());
            Long price = getPrice(goodsSkuPo1.getId());
            if (price == null) {
                simpleSkuVo.setPrice(goodsSkuPo1.getOriginalPrice());
            } else {
                simpleSkuVo.setPrice(price);
            }
            simpleSkuVo.setDisable(goodsSkuPo1.getDisabled() != 0);
            simpleSkuVos.add(simpleSkuVo);
        }
        spuRetVo.setSkuList(simpleSkuVos);
        return spuRetVo;
    }

    /**
     * 新建sku
     *
     */
    public ReturnObject creatSku(Long spuId, Long shopId, SkuCreatVo skuCreatVo) {
        GoodsSpuPo goodsSpuPo = goodsSpuPoMapper.selectByPrimaryKey(spuId);
        if (goodsSpuPo == null || goodsSpuPo.getDisabled() != 0) {
            return new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        if (!goodsSpuPo.getShopId().equals(shopId)) {
            return new ReturnObject(ResponseCode.RESOURCE_ID_OUTSCOPE);
        }
        GoodsSkuPoExample goodsSkuPoExample = new GoodsSkuPoExample();
        GoodsSkuPoExample.Criteria criteria = goodsSkuPoExample.createCriteria();
        criteria.andSkuSnEqualTo(skuCreatVo.getSn());
        List<GoodsSkuPo> goodsSkuPos = goodsSkuPoMapper.selectByExample(goodsSkuPoExample);
        if (!goodsSkuPos.isEmpty()) {
            return new ReturnObject(ResponseCode.SKUSN_SAME);
        }
        GoodsSku goodsSku = new GoodsSku();
        GoodsSkuPo po = goodsSku.createPo(skuCreatVo, spuId);
        goodsSkuPoMapper.insertSelective(po);
        SkuOutputVo skuOutputVo = new SkuOutputVo(po);
        return new ReturnObject(skuOutputVo);
    }

    /**
     * 管理员新增品牌
     *
     */
    public ReturnObject<Object> addBrand(BrandInputVo brandInputVo) {
        if (brandInputVo.getName() == null || brandInputVo.getName().length() == 0) {
            return new ReturnObject<>(ResponseCode.FIELD_NOTVALID, "品牌名称不能为空");
        }
        String name = brandInputVo.getName();
        BrandPoExample brandPoExample = new BrandPoExample();
        BrandPoExample.Criteria criteria = brandPoExample.createCriteria();
        criteria.andNameEqualTo(name);
        List<BrandPo> brandPos = brandPoMapper.selectByExample(brandPoExample);
        if (!brandPos.isEmpty()) {
            return new ReturnObject<>(ResponseCode.BRAND_NAME_SAME);
        }
        Brand brand = new Brand();
        BrandPo brandPo = brand.createAddPo(brandInputVo);
        int ret = brandPoMapper.insertSelective(brandPo);
        if (ret == 0) {
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR);
        } else {
            logger.info("品牌已新建成功");
            return new ReturnObject(new Brand(brandPo));
        }
    }

    public ReturnObject getShare(Long sid, Long id, Long userId, Long departId) {
        cn.edu.xmu.external.model.MyReturn<Boolean> booleanMyReturn = iShareService.verifyShare(sid);
        Boolean bool = booleanMyReturn.getData();
        if (!bool) {
            return new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        GoodsSkuPo goodsSkuPo = goodsSkuPoMapper.selectByPrimaryKey(id);
        //只能看到上架的商品
        cn.edu.xmu.external.model.MyReturn<Boolean> booleanMyReturn1 = iShareService.verifyInfoByShareId(userId, id, sid);
        Boolean bool1 = booleanMyReturn1.getData();
        if (!bool1) {
            if (departId == null || departId < 0) {
                if (goodsSkuPo == null || goodsSkuPo.getDisabled() != 0 || goodsSkuPo.getState() != 4) {
                    return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
                }
                SkuReturnVo skuReturnVo = new SkuReturnVo();
                skuReturnVo.setId(id);
                skuReturnVo.setName(goodsSkuPo.getName());
                skuReturnVo.setSkuSn(goodsSkuPo.getSkuSn());
                skuReturnVo.setDetail(goodsSkuPo.getDetail());
                skuReturnVo.setImageUrl(goodsSkuPo.getImageUrl());
                skuReturnVo.setOriginalPrice(goodsSkuPo.getOriginalPrice());
                //获得现价
                Long price = getPrice(id);
                if (price == null) {
                    skuReturnVo.setPrice(goodsSkuPo.getOriginalPrice());
                } else {
                    skuReturnVo.setPrice(price);
                }
                skuReturnVo.setInventory(goodsSkuPo.getInventory());
                skuReturnVo.setState(goodsSkuPo.getState());
                skuReturnVo.setConfiguration(goodsSkuPo.getConfiguration());
                skuReturnVo.setWeight(goodsSkuPo.getWeight());
                skuReturnVo.setGmtCreate(goodsSkuPo.getGmtCreate());
                skuReturnVo.setGmtModified(goodsSkuPo.getGmtModified());
                skuReturnVo.setDisable(goodsSkuPo.getDisabled() != 0);
                SpuRetVo spuRetVo = getSpuRetVo(id);
                skuReturnVo.setSpu(spuRetVo);
                return new ReturnObject(skuReturnVo);
            } else {
                if (goodsSkuPo == null || goodsSkuPo.getDisabled() != 0) {
                    return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
                }
                SkuReturnVo skuReturnVo = new SkuReturnVo();
                skuReturnVo.setId(id);
                skuReturnVo.setName(goodsSkuPo.getName());
                skuReturnVo.setSkuSn(goodsSkuPo.getSkuSn());
                skuReturnVo.setDetail(goodsSkuPo.getDetail());
                skuReturnVo.setImageUrl(goodsSkuPo.getImageUrl());
                skuReturnVo.setOriginalPrice(goodsSkuPo.getOriginalPrice());
                //获得现价
                Long price = getPrice(id);
                if (price == null) {
                    skuReturnVo.setPrice(goodsSkuPo.getOriginalPrice());
                } else {
                    skuReturnVo.setPrice(price);
                }
                skuReturnVo.setInventory(goodsSkuPo.getInventory());
                skuReturnVo.setState(goodsSkuPo.getState());
                skuReturnVo.setConfiguration(goodsSkuPo.getConfiguration());
                skuReturnVo.setWeight(goodsSkuPo.getWeight());
                skuReturnVo.setGmtCreate(goodsSkuPo.getGmtCreate());
                skuReturnVo.setGmtModified(goodsSkuPo.getGmtModified());
                skuReturnVo.setDisable(goodsSkuPo.getDisabled() != 0);
                SpuRetVo spuRetVo = getSpuRetVo(id);
                skuReturnVo.setSpu(spuRetVo);
                return new ReturnObject(skuReturnVo);
            }
        } else {
            return new ReturnObject(ResponseCode.RESOURCE_ID_OUTSCOPE);
        }
    }
}