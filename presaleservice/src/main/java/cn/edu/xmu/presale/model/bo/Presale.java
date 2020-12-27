package cn.edu.xmu.presale.model.bo;

import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.presale.model.po.PresalePo;
import cn.edu.xmu.presale.model.vo.PresaleVo;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * @author XC
 */

public class Presale implements VoObject {
    private Long id;

    private String name;

    private LocalDateTime beginTime;

    private LocalDateTime payTime;

    private LocalDateTime endTime;

    private Byte state;

    private Long shopId;

    private Long goodsSKUId;

    private Integer quantity;

    private Long advancePayPrice;

    private Long restPayPrice;

    private LocalDateTime gmtCreated;

    private LocalDateTime gmtModified;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(LocalDateTime beginTime) {
        this.beginTime = beginTime;
    }

    public LocalDateTime getPayTime() {
        return payTime;
    }

    public void setPayTime(LocalDateTime payTime) {
        this.payTime = payTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public Byte getState() {
        return state;
    }

    public void setState(Byte state) {
        this.state = state;
    }

    public Long getShopId() {
        return shopId;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }

    public Long getGoodsSKUId() {
        return goodsSKUId;
    }

    public void setGoodsSKUId(Long goodsSKUId) {
        this.goodsSKUId = goodsSKUId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Long getAdvancePayPrice() {
        return advancePayPrice;
    }

    public void setAdvancePayPrice(Long advancePayPrice) {
        this.advancePayPrice = advancePayPrice;
    }

    public Long getRestPayPrice() {
        return restPayPrice;
    }

    public void setRestPayPrice(Long restPayPrice) {
        this.restPayPrice = restPayPrice;
    }

    public LocalDateTime getGmtCreated() {
        return gmtCreated;
    }

    public void setGmtCreated(LocalDateTime gmtCreated) {
        this.gmtCreated = gmtCreated;
    }

    public LocalDateTime getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(LocalDateTime gmtModified) {
        this.gmtModified = gmtModified;
    }

    @Override
    public Object createVo() {
        PresaleVo presaleVo = new PresaleVo();
        presaleVo.setId(id);
        presaleVo.setName(name);
        presaleVo.setBeginTime(beginTime);
        presaleVo.setPayTime(payTime);
        presaleVo.setEndTime(endTime);
        presaleVo.setState(state);
        presaleVo.setShopId(shopId);
        presaleVo.setGoodsSKUId(goodsSKUId);
        presaleVo.setQuantity(quantity);
        presaleVo.setAdvancePayPrice(advancePayPrice);
        presaleVo.setRestPayPrice(restPayPrice);
        presaleVo.setGmtCreated(gmtCreated);
        presaleVo.setGmtModified(gmtModified);
        return presaleVo;
    }

    @Override
    public Object createSimpleVo() {
        PresaleVo presaleVo = new PresaleVo();
        presaleVo.setName(name);
        presaleVo.setAdvancePayPrice(advancePayPrice);
        presaleVo.setRestPayPrice(restPayPrice);
        presaleVo.setQuantity(quantity);
        presaleVo.setBeginTime(beginTime);
        presaleVo.setPayTime(payTime);
        presaleVo.setEndTime(endTime);
        return null;
    }

    public enum State {
        OFFSHELVES(0, "已下线"),
        ONSHELVES(1, "已上线"),
        DELETED(2, "已删除");

        private static final Map<Integer, Presale.State> stateMap;

        static { //由类加载机制，静态块初始加载对应的枚举属性到map中，而不用每次取属性时，遍历一次所有枚举值
            stateMap = new HashMap();
            for (Presale.State stateEnum : values()) {
                stateMap.put(stateEnum.code, stateEnum);
            }
        }

        private int code;

        private String name;

        State(int code, String name) {
            this.code = code;
            this.name = name;
        }

        public byte getCode() {
            return (byte) code;
        }

        public String getValue() {
            return name;
        }
    }

    public Presale(PresalePo po) {
        this.id = po.getId();
        this.name = po.getName();
        this.beginTime = po.getBeginTime();
        this.payTime = po.getPayTime();
        this.endTime = po.getEndTime();
        this.state = po.getState();
        this.shopId = po.getShopId();
        this.goodsSKUId = po.getGoodsSkuId();
        this.quantity = po.getQuantity();
        this.advancePayPrice = po.getAdvancePayPrice();
        this.restPayPrice = po.getRestPayPrice();
        this.gmtCreated = po.getGmtCreate();
        this.gmtModified = po.getGmtModified();
    }
}
