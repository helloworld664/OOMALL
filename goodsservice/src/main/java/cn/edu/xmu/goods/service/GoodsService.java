package cn.edu.xmu.goods.service;

import cn.edu.xmu.goods.dao.GoodsDao;
import cn.edu.xmu.goods.mapper.GoodsCategoryPoMapper;
import cn.edu.xmu.goods.model.bo.Brand;
import cn.edu.xmu.goods.model.bo.GoodsCategory;
import cn.edu.xmu.goods.model.bo.GoodsSku;
import cn.edu.xmu.goods.model.bo.GoodsSpu;
import cn.edu.xmu.goods.model.po.*;
import cn.edu.xmu.goods.model.vo.*;
import cn.edu.xmu.ininterface.service.Ingoodservice;
import cn.edu.xmu.ininterface.service.model.vo.*;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.ImgHelper;
import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import com.github.pagehelper.PageInfo;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@DubboService(version = "0.0.1")
public class GoodsService implements Ingoodservice {

    @Autowired
    GoodsDao goodsDao;

    @Resource
    RocketMQTemplate rocketMQTemplate;

    @Autowired
    GoodsCategoryPoMapper goodsCategoryPoMapper;

    @Value("${goodsservice.dav.username}")
    private String davUsername;

    @Value("${goodsservice.dav.password}")
    private String davPassword;

    @Value("${goodsservice.dav.baseUrl}")
    private String baseUrl;

    private static final Logger logger = LoggerFactory.getLogger(GoodsService.class);

    public ReturnObject<PageInfo<VoObject>> findAllBrand(Integer page, Integer pageSize) {
        return goodsDao.findAllBrand(page, pageSize);
    }

    @Override
    public SkuToPresaleVo presaleFindSku(Long id) {
        GoodsSkuPo goodsSkuPo = goodsDao.findGoodsSkuById(id);
        if (goodsSkuPo == null || !goodsSkuPo.getDisabled().equals((byte) 0)) {
            return null;
        }
        SkuToPresaleVo skuToPresaleVo = new SkuToPresaleVo(goodsSkuPo.getId(), goodsSkuPo.getName(), goodsSkuPo.getSkuSn(), goodsSkuPo.getImageUrl(), goodsSkuPo.getInventory(), goodsSkuPo.getOriginalPrice(),
                goodsDao.getPrice(id) == null ? goodsSkuPo.getOriginalPrice() : goodsDao.getPrice(id), goodsSkuPo.getDisabled() != 0);
        return skuToPresaleVo;
    }

    @Override
    public SpuToGrouponVo grouponFindSpu(Long id) {
        GoodsSpuPo goodsSpuPo = goodsDao.findGoodsSpuById(id);
        if (goodsSpuPo == null) {
            return null;
        }
        SpuGrouponVo spuGrouponVo = new SpuGrouponVo(goodsSpuPo);
        SpuToGrouponVo spuToGrouponVo = new SpuToGrouponVo();
        spuToGrouponVo.setId(spuGrouponVo.getId());
        spuToGrouponVo.setName(spuGrouponVo.getName());
        spuToGrouponVo.setGoodsSn(spuGrouponVo.getGoodsSn());
        spuToGrouponVo.setImageUrl(spuGrouponVo.getImageUrl());
        spuToGrouponVo.setGmtCreate(spuGrouponVo.getGmtCreate());
        spuToGrouponVo.setGmtModified(spuGrouponVo.getGmtModified());
        return spuToGrouponVo;
    }

    @Override
    public SkuToFlashSaleVo flashFindSku(Long id) {
        try {
            GoodsSkuPo goodsSkuPo = goodsDao.findGoodsSkuById(id);
            if (goodsSkuPo == null || !goodsSkuPo.getDisabled().equals((byte) 0)) {
                return null;
            }
            SkuToFlashSaleVo skuToFlashSaleVo = new SkuToFlashSaleVo(goodsSkuPo.getId(), goodsSkuPo.getName(), goodsSkuPo.getSkuSn(), goodsSkuPo.getImageUrl(), goodsSkuPo.getInventory(), goodsSkuPo.getOriginalPrice(),
                    goodsDao.getPrice(id) == null ? goodsSkuPo.getOriginalPrice() : goodsDao.getPrice(id), goodsSkuPo.getDisabled() != 0);
            return skuToFlashSaleVo;
        } catch (Exception e) {
            logger.error("findAllBrand: DataAccessException:" + e.getMessage());
            return null;
        }
    }

    @Override
    public boolean skuExitOrNot(Long skuId) {
        GoodsSkuPo po = goodsDao.findGoodsSkuById(skuId);
        return po != null;
    }

    @Override
    public boolean skuInShopOrNot(Long shopId, Long id) {
        GoodsSkuPo goodsSkuPo = goodsDao.findGoodsSkuById(id);
        GoodsSpuPo goodsSpuPo = goodsDao.findGoodsSpuById(goodsSkuPo.getGoodsSpuId());
        return shopId.equals(goodsSpuPo.getShopId());
    }

    @Override
    public boolean spuInShopOrNot(Long shopId, Long id) {
        GoodsSpuPo goodsSpuPo = goodsDao.findGoodsSpuById(id);
        if (goodsSpuPo == null) {
            return false;
        }
        return shopId.equals(goodsSpuPo.getShopId());
    }

    @Override
    public SkuToCouponVo couponActivityFindSku(Long id) {
        GoodsSkuPo goodsSkuPo = goodsDao.findGoodsSkuById(id);
        if (goodsSkuPo == null) {
            return null;
        }
        SkuCouponVo skuCouponVo = new SkuCouponVo(goodsSkuPo);

        SkuToCouponVo skuToCouponVo = new SkuToCouponVo();

        skuToCouponVo.setDisable(skuCouponVo.getDisable());
        skuToCouponVo.setGoodsSn(skuCouponVo.getGoodsSn());
        skuToCouponVo.setId(skuCouponVo.getId());
        skuToCouponVo.setImageUrl(skuCouponVo.getImageUrl());
        skuToCouponVo.setInventory(skuCouponVo.getInventory());
        skuToCouponVo.setOriginalPrice(skuCouponVo.getOriginalPrice());
        skuToCouponVo.setName(skuCouponVo.getName());
        return skuToCouponVo;
    }

    public ReturnObject deleteBrandById(Long id) {
        return goodsDao.deleteBrandById(id);
    }

    public ReturnObject modifyBrandInfo(Long shopId, Long id, BrandInputVo brandInputVo) {
        return goodsDao.modifyBrandById(shopId, id, brandInputVo);
    }

    public ReturnObject<Object> addBrand(BrandInputVo brandInputVo) {
        return goodsDao.addBrand(brandInputVo);
    }

    @Transactional
    public ReturnObject<Object> modifySpuInfo(Long shopId, Long spuId, SpuInputVo spuInputVo) {
        GoodsSpuPo goodsSpuPo = goodsDao.findGoodsSpuById(spuId);
        if (goodsSpuPo == null) {
            logger.debug("修改spu信息中，spuId不存在");
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        if (goodsSpuPo.getDisabled() != 0) {
            logger.debug("修改spu信息中，禁止访问");
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        ReturnObject returnObject;
        if (!shopId.equals(goodsSpuPo.getShopId())) {
            logger.debug("修改spu信息中，spu里shopId和路径上的shopId不一致");
            returnObject = new ReturnObject<>(ResponseCode.FIELD_NOTVALID);
        } else {
            goodsSpuPo.setId(spuId);
            goodsSpuPo.setGmtModified(LocalDateTime.now());
            goodsSpuPo.setSpec(spuInputVo.getSpecs());
            goodsSpuPo.setName(spuInputVo.getName());
            goodsSpuPo.setDetail(spuInputVo.getDecription());
            returnObject = goodsDao.modifySpuBySpuPoId(goodsSpuPo);
        }
        return returnObject;
    }

    public ReturnObject putGoodsOnSaleById(Long shopId, Long skuId) {
        return goodsDao.putGoodsOnSaleById(shopId, skuId, 4L);
    }

    public ReturnObject putOffGoodsOnSaleById(Long shopId, Long skuId) {
        return goodsDao.putOffGoodsOnSaleById(shopId, skuId, 0L);
    }

    public ReturnObject deleteSkuById(Long shopId, Long skuId) {
        return goodsDao.deleteSkuById(shopId, skuId, 6L);
    }

    public ReturnObject modifySkuInfo(Long shopId, Long id, SkuInputVo skuInputVo) {
        return goodsDao.modifySkuById(shopId, id, skuInputVo);
    }

    public ReturnObject<PageInfo<VoObject>> findSkuSimple(Long shopId, Integer page, Integer
            pageSize, Long spuId, String skuSn, String spuSn) {
        ReturnObject<PageInfo<VoObject>> returnObject = goodsDao.findSkuSimple(shopId, page, pageSize, spuId, skuSn, spuSn);
        return returnObject;
    }

    public ReturnObject invalidFloatPriceById(Long shopId, Long id, Long loginUserId) {
        return goodsDao.invalidFloatPriceById(shopId, id, loginUserId);
    }

    public ReturnObject<Object> addCategory(Long id, CategoryInputVo categoryInputVo) {
        if (goodsCategoryPoMapper.selectByPrimaryKey(id) == null && id != 0) {
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        if (categoryInputVo.getName() == null || categoryInputVo.getName().length() == 0) {
            return new ReturnObject<>(ResponseCode.FIELD_NOTVALID, "商品类目名称不能为空");
        }
        GoodsCategoryPoExample goodsCategoryPo = new GoodsCategoryPoExample();
        GoodsCategoryPoExample.Criteria criteria = goodsCategoryPo.createCriteria();
        criteria.andNameEqualTo(categoryInputVo.getName());
        List<GoodsCategoryPo> goodsCategoryPos = goodsCategoryPoMapper.selectByExample(goodsCategoryPo);
        if (!goodsCategoryPos.isEmpty()) {
            return new ReturnObject<>(ResponseCode.CATEGORY_NAME_SAME);
        }

        GoodsCategoryPo goodsCategoryPo1 = goodsDao.addCategoryById(id, categoryInputVo);
        if (goodsCategoryPo1 != null) {
            return new ReturnObject(new GoodsCategory(goodsCategoryPo1));
        } else {
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
    }

    public ReturnObject<Object> modifyCategory(Long id, CategoryInputVo categoryInputVo) {
        return goodsDao.modifyCategoryById(id, categoryInputVo);
    }

    public ReturnObject<Object> deleteCategoryById(Long id) {
        return goodsDao.deleteCategoryById(id);
    }

    public ReturnObject spuAddBrand(Long shopId, Long spuId, Long id) {
        ReturnObject returnObject;
        GoodsSpuPo tempSpu = goodsDao.findGoodsSpuById(spuId);
        if (tempSpu == null || tempSpu.getDisabled() == 0) {
            logger.debug("findGoodsSkuById : Not Found!");
            returnObject = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        } else {
            if (tempSpu.getShopId().equals(shopId)) {
                BrandPo brandPo = goodsDao.findBrandById(id);
                if (brandPo != null) {
                    GoodsSpuPo goodsSpuPo = new GoodsSpuPo();
                    goodsSpuPo.setBrandId(id);
                    goodsSpuPo.setId(spuId);
                    goodsSpuPo.setGmtModified(LocalDateTime.now());
                    returnObject = goodsDao.modifySpuBySpuPoId(goodsSpuPo);
                } else {
                    logger.debug("findBrandById : Not Found!");
                    returnObject = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
                }
            } else {
                logger.debug("spuAddBrand shopId和这个spu的里的shopId不一致");
                returnObject = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
            }
        }
        return returnObject;
    }

    public ReturnObject<List> selectCategories(Long id) {
        ReturnObject<List> ret = goodsDao.getCategoryByPid(id);
        return ret;
    }

    @Transactional
    public ReturnObject uploadSkuImg(Long shopId, Long id, MultipartFile multipartFile) {
        ReturnObject<GoodsSku> goodsSkuReturnObject = goodsDao.getGoodsSkuById(id);
        if (goodsSkuReturnObject.getCode() == ResponseCode.RESOURCE_ID_NOTEXIST || goodsSkuReturnObject.getData().getDisabled()) {
            logger.debug("uploadSkuImg : failed");
            return goodsSkuReturnObject;
        }
        Long shopid = goodsDao.findGoodsSpuById(goodsDao.findGoodsSkuById(id).getGoodsSpuId()).getShopId();
        if (shopid.equals(shopId)) {
            if (goodsSkuReturnObject.getCode() == ResponseCode.RESOURCE_ID_NOTEXIST) {
                return goodsSkuReturnObject;
            }
            GoodsSku goodsSku = goodsSkuReturnObject.getData();
            ReturnObject returnObject = new ReturnObject();
            try {
                returnObject = ImgHelper.remoteSaveImg(multipartFile, 2, davUsername, davPassword, baseUrl);
                //文件上传错误
                if (returnObject.getCode() != ResponseCode.OK) {
                    logger.debug(returnObject.getErrmsg());
                    return returnObject;
                }
                String oldFilename = null;
                if (goodsSku.getImageUrl() != null) {
                    int baseUrlIndex = goodsSku.getImageUrl().lastIndexOf("/");
                    oldFilename = goodsSku.getImageUrl().substring(baseUrlIndex + 1);
                }
                goodsSku.setImageUrl(baseUrl + returnObject.getData().toString());
                ReturnObject updateReturnObject = goodsDao.updateGoodsSkuImgUrl(goodsSku);

                //数据库更新失败，需删除新增的图片
                if (updateReturnObject.getCode() == ResponseCode.FIELD_NOTVALID) {
                    ImgHelper.deleteRemoteImg(returnObject.getData().toString(), davUsername, davPassword, baseUrl);
                    return updateReturnObject;
                }

                //数据库更新成功需删除旧图片，未设置则不删除
                if (oldFilename != null) {
                    ImgHelper.deleteRemoteImg(oldFilename, davUsername, davPassword, baseUrl);
                }
            } catch (IOException e) {
                logger.debug("uploadImg: I/O Error:" + baseUrl);
                return new ReturnObject(ResponseCode.FILE_NO_WRITE_PERMISSION);
            }
            return returnObject;
        } else {
            return new ReturnObject(ResponseCode.AUTH_NOT_ALLOW);
        }
    }

    @Transactional
    public ReturnObject uploadSpuImg(Long shopId, Long id, MultipartFile multipartFile) {
        ReturnObject<GoodsSpu> goodsSpuReturnObject = goodsDao.getGoodsSpuById(id);
        if (goodsSpuReturnObject.getCode() == ResponseCode.RESOURCE_ID_NOTEXIST || goodsSpuReturnObject.getData().getDisabled()) {
            logger.debug("uploadSpuImg : failed");
            return goodsSpuReturnObject;
        }
        Long shopid = goodsDao.findGoodsSpuById(id).getShopId();
        if (shopid.equals(shopId)) {
            if (goodsSpuReturnObject.getCode() == ResponseCode.RESOURCE_ID_NOTEXIST) {
                return goodsSpuReturnObject;
            }
            GoodsSpu goodsSpu = goodsSpuReturnObject.getData();
            ReturnObject returnObject = new ReturnObject();
            try {
                returnObject = ImgHelper.remoteSaveImg(multipartFile, 2, davUsername, davPassword, baseUrl);
                //文件上传错误
                if (returnObject.getCode() != ResponseCode.OK) {
                    logger.debug(returnObject.getErrmsg());
                    return returnObject;
                }
                String oldFilename = null;
                if (goodsSpu.getImageUrl() != null) {
                    int baseUrlIndex = goodsSpu.getImageUrl().lastIndexOf("/");
                    oldFilename = goodsSpu.getImageUrl().substring(baseUrlIndex + 1);
                }
                goodsSpu.setImageUrl(baseUrl + returnObject.getData().toString());
                ReturnObject updateReturnObject = goodsDao.updateGoodsSpuImgUrl(goodsSpu);

                //数据库更新失败，需删除新增的图片
                if (updateReturnObject.getCode() == ResponseCode.FIELD_NOTVALID) {
                    ImgHelper.deleteRemoteImg(returnObject.getData().toString(), davUsername, davPassword, baseUrl);
                    return updateReturnObject;
                }

                //数据库更新成功需删除旧图片，未设置则不删除
                if (oldFilename != null) {
                    ImgHelper.deleteRemoteImg(oldFilename, davUsername, davPassword, baseUrl);
                }
            } catch (IOException e) {
                logger.debug("uploadImg: I/O Error:" + baseUrl);
                return new ReturnObject(ResponseCode.FILE_NO_WRITE_PERMISSION);
            }
            return returnObject;
        } else {
            return new ReturnObject(ResponseCode.AUTH_NOT_ALLOW);
        }
    }

    @Transactional
    public ReturnObject uploadBrandImg(Long shopId, Long id, MultipartFile multipartFile) {
        ReturnObject<Brand> brandReturnObject = goodsDao.getBrandById(id);
        if (shopId == 0) {
            if (brandReturnObject.getCode() == ResponseCode.RESOURCE_ID_NOTEXIST) {
                return brandReturnObject;
            }
            Brand brand = brandReturnObject.getData();
            ReturnObject returnObject = new ReturnObject();
            try {
                returnObject = ImgHelper.remoteSaveImg(multipartFile, 2, davUsername, davPassword, baseUrl);
                //文件上传错误
                if (returnObject.getCode() != ResponseCode.OK) {
                    logger.debug(returnObject.getErrmsg());
                    return returnObject;
                }
                String oldFilename = null;
                if (brand.getImageUrl() != null) {
                    int baseUrlIndex = brand.getImageUrl().lastIndexOf("/");
                    oldFilename = brand.getImageUrl().substring(baseUrlIndex + 1);
                }
                brand.setImageUrl(baseUrl + returnObject.getData().toString());
                ReturnObject updateReturnObject = goodsDao.updateBrandImgUrl(brand);

                //数据库更新失败，需删除新增的图片
                if (updateReturnObject.getCode() == ResponseCode.FIELD_NOTVALID) {
                    ImgHelper.deleteRemoteImg(returnObject.getData().toString(), davUsername, davPassword, baseUrl);
                    return updateReturnObject;
                }
                //数据库更新成功需删除旧图片，未设置则不删除
                if (oldFilename != null) {
                    ImgHelper.deleteRemoteImg(oldFilename, davUsername, davPassword, baseUrl);
                }
            } catch (IOException e) {
                logger.debug("uploadImg: I/O Error:" + baseUrl);
                return new ReturnObject(ResponseCode.FILE_NO_WRITE_PERMISSION);
            }
            return returnObject;
        } else {
            return new ReturnObject(ResponseCode.AUTH_NOT_ALLOW);
        }
    }

    public ReturnObject spuAddCategories(Long shopId, Long spuId, Long id) {
        ReturnObject returnObject;
        GoodsSpuPo goodsSpuPo = goodsDao.findGoodsSpuById(spuId);
        if (goodsSpuPo == null || goodsSpuPo.getDisabled() == 0) {
            logger.debug("findGoodsSPuById : Not Found!");
            returnObject = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, "SpuId不存在");
        } else {
            if (!shopId.equals(goodsSpuPo.getShopId())) {
                logger.debug("spu增加种类的时候，shopid不一致");
                returnObject = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, "spuAddCategories，shopid不一致");
            } else {
                GoodsCategoryPo goodsCategoryPo = goodsDao.getCategoryById(id);
                if (goodsCategoryPo == null) {
                    logger.debug("spu增加种类的时候，CategoriesId不存在");
                    returnObject = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, "spuAddCategories，shopid不一致");
                } else {
                    //当spu没有种类的时候，直接添加种类
                    if (goodsSpuPo.getCategoryId() == null) {
                        goodsSpuPo.setGmtModified(LocalDateTime.now());
                        goodsSpuPo.setCategoryId(id);
                        returnObject = goodsDao.modifySpuBySpuPoId(goodsSpuPo);
                    }
                    //spu有种类了
                    else {
                        if (goodsCategoryPo.getPid() == null) {
                            returnObject = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, "spuAddCategories，CategoriesId不是二级种类");
                        } else {
                            if (!goodsCategoryPo.getPid().equals(goodsSpuPo.getCategoryId())) {
                                //不是二级种类，不予添加
                                logger.debug("spu增加种类的时候，CategoriesId不是二级种类");
                                returnObject = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, "spuAddCategories，CategoriesId不是二级种类");
                            } else {
                                //是二级目录，更改数据库
                                goodsSpuPo.setGmtModified(LocalDateTime.now());
                                goodsSpuPo.setCategoryId(id);
                                returnObject = goodsDao.modifySpuBySpuPoId(goodsSpuPo);
                            }
                        }
                    }
                }
            }
        }
        return returnObject;
    }

    public ReturnObject spuDeleteBrand(Long shopId, Long spuId, Long id) {
        ReturnObject returnObject;
        GoodsSpuPo tempSpu = goodsDao.findGoodsSpuById(spuId);
        if (tempSpu == null || tempSpu.getDisabled() == 0) {
            logger.debug("findGoodsSPuById : Not Found!");
            returnObject = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, "SpuId不存在");
        } else {
            if (tempSpu.getShopId().equals(shopId) && tempSpu.getBrandId().equals(id)) {
                tempSpu.setGmtModified(LocalDateTime.now());
                tempSpu.setBrandId(null);
                returnObject = goodsDao.modifySpuBySpuPo(tempSpu);
            } else {
                logger.debug("spuDeleteBrand shopId和这个spu的里的shopId不一致");
                returnObject = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, "spuDeleteBrand shopId和这个spu的里的shopId不一致");
            }
        }
        return returnObject;
    }

    public ReturnObject spuDeleteCategories(Long shopId, Long spuId, Long id) {
        ReturnObject returnObject;
        GoodsSpuPo tempSpu = goodsDao.findGoodsSpuById(spuId);
        if (tempSpu == null || tempSpu.getDisabled() == 0) {
            logger.debug("findGoodsSPuById : Not Found!");
            returnObject = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, "SpuId不存在");
        } else {
            if (tempSpu.getShopId().equals(shopId) && tempSpu.getCategoryId().equals(id)) {
                tempSpu.setGmtModified(LocalDateTime.now());
                tempSpu.setCategoryId(null);
                returnObject = goodsDao.modifySpuBySpuPo(tempSpu);
            } else {
                logger.debug("spuDeleteBrand shopId和这个spu的里的shopId不一致");
                returnObject = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, "spuDeleteBrand shopId和这个spu的里的shopId不一致");
            }
        }
        return returnObject;
    }

    public ReturnObject deleteSpuById(Long shopId, Long id) {
        return goodsDao.deleteSpuById(shopId, id);
    }

    public ReturnObject<Object> addFloatPrice(Long shopId, Long id, FloatPriceInputVo floatPriceInputVo, Long userId) {
        return goodsDao.addFloatPrice(shopId, id, floatPriceInputVo, userId);
    }

    public ReturnObject addSpu(Long shopId, SpuInputVo spuInputVo) {
        return goodsDao.addSpu(shopId, spuInputVo);
    }

    public ReturnObject getSku(Long id, Long userId, Long departId) {
//        if (userId != null) {
//            System.out.println(userId);
//            MessageVo messageVo = new MessageVo();
//            messageVo.setGoodsSkuId(id);
//            messageVo.setCustomerId(userId);
//            sendMessage(messageVo);
//        }
        return goodsDao.getSku(id, departId);
    }


    private void sendMessage(MessageVo messageVo) {
        String json = JacksonUtil.toJson(messageVo);
        Message message = MessageBuilder.withPayload(json).build();
        rocketMQTemplate.sendOneWay("goods-foot-topic", message);
        logger.debug(json);
    }

    public ReturnObject getSpu(Long id) {
        return goodsDao.getSpu(id);
    }

    public ReturnObject<Object> creatSku(Long id, Long shopId, SkuCreatVo skuCreatVo) {
        return goodsDao.creatSku(id, shopId, skuCreatVo);
    }

    public ReturnObject getShare(Long sid, Long id, Long userId, Long departId) {
        return goodsDao.getShare(sid, id, userId, departId);
    }
}
