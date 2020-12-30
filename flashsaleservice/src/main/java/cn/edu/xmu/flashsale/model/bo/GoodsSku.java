package cn.edu.xmu.flashsale.model.bo;

import cn.edu.xmu.flashsale.model.po.FlashSalePo;
import cn.edu.xmu.flashsale.model.po.GoodsSkuPo;
import cn.edu.xmu.ooad.model.VoObject;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * @author XC
 */

@Data
public class GoodsSku implements VoObject {
    private Long id;

    private Long goodsSpuId;

    private String skuSn;

    private String name;

    private Long originalPrice;

    private String configuration;

    private Long weight;

    private String imageUrl;

    private Integer inventory;

    private String detail;

    private Byte disabled;

    private LocalDateTime gmtCreated;

    private LocalDateTime gmtModified;

    private Byte state;

    public enum State {
        OFFSHELVES(0, "未上架"),
        ONSHELVES(1, "已上线"),
        DELETED(6, "已删除");

        private int code;

        private String name;

        private static final Map<Integer, GoodsSku.State> stateMap;

        static { //由类加载机制，静态块初始加载对应的枚举属性到map中，而不用每次取属性时，遍历一次所有枚举值
            stateMap = new HashMap();
            for (GoodsSku.State stateEnum : values()) {
                stateMap.put(stateEnum.code, stateEnum);
            }
        }

        public static GoodsSku.State getTypeByCode(int code) {
            return stateMap.get(code);
        }

        public byte getCode() {
            return (byte) code;
        }

        public String getName() {
            return name;
        }

        public static Map<Integer, GoodsSku.State> getStateMap() {
            return stateMap;
        }

        State(int code, String name) {
            this.code = code;
            this.name = name;
        }
    }

    public GoodsSku(GoodsSkuPo goodsSkuPo) {
        id = goodsSkuPo.getId();
        goodsSpuId = goodsSkuPo.getGoodsSpuId();
        skuSn = goodsSkuPo.getSkuSn();
        name = goodsSkuPo.getName();
        originalPrice = goodsSkuPo.getOriginalPrice();
        configuration = goodsSkuPo.getConfiguration();
        weight = goodsSkuPo.getWeight();
        imageUrl = goodsSkuPo.getImageUrl();
        inventory = goodsSkuPo.getInventory();
        detail = goodsSkuPo.getDetail();
        disabled = goodsSkuPo.getDisabled();
        gmtCreated = goodsSkuPo.getGmtCreate();
        gmtModified = goodsSkuPo.getGmtModified();
        state = goodsSkuPo.getState();
    }

    @Override
    public Object createVo() {
        return null;
    }

    @Override
    public Object createSimpleVo() {
        return null;
    }
}
