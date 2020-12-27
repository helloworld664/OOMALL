package cn.edu.xmu.groupon.model.bo;

import cn.edu.xmu.groupon.model.po.GrouponPo;
import cn.edu.xmu.groupon.model.vo.GrouponVo;
import cn.edu.xmu.ooad.model.VoObject;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * @author XC
 */

public class Groupon implements VoObject {
    public Long id;

    public String name;

    public LocalDateTime beginTime;

    public LocalDateTime endTime;

    public Integer state;

    public Long shopId;

    public Long goodsSPUId;

    public String strategy;

    public LocalDateTime gmtCreated;

    public LocalDateTime gmtModified;

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

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Long getShopId() {
        return shopId;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }

    public Long getGoodsSPUId() {
        return goodsSPUId;
    }

    public void setGoodsSPUId(Long goodsSPUId) {
        this.goodsSPUId = goodsSPUId;
    }

    public String getStrategy() {
        return strategy;
    }

    public void setStrategy(String strategy) {
        this.strategy = strategy;
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

    public enum State {
        OFFSHELVES(0, "已下线"),
        ONSHELVES(1, "已上线"),
        DELETED(2, "已删除");

        private static final Map<Integer, Groupon.State> stateMap;

        static { //由类加载机制，静态块初始加载对应的枚举属性到map中，而不用每次取属性时，遍历一次所有枚举值
            stateMap = new HashMap();
            for (Groupon.State stateEnum : values()) {
                stateMap.put(stateEnum.code, stateEnum);
            }
        }

        private int code;

        private String name;

        State(int code, String value) {
            this.code = code;
            this.name = value;
        }

        public static Groupon.State getTypeByCode(int code) {
            return stateMap.get(code);
        }

        public byte getCode() {
            return (byte) code;
        }

        public String getDescription() {
            return name;
        }

    }

    public Groupon(GrouponPo po) {
        this.id = po.getId();
        this.name = po.getName();
        this.beginTime = po.getBeginTime();
        this.endTime = po.getEndTime();
        this.state = Integer.valueOf(po.getState());
        this.shopId = po.getShopId();
        this.goodsSPUId = po.getGoodsSpuId();
        this.strategy = po.getStrategy();
        this.gmtCreated = po.getGmtCreate();
        this.gmtModified = po.getGmtModified();
    }

    @Override
    public VoObject createVo() {
        GrouponVo grouponVo = new GrouponVo();
        grouponVo.setId(id);
        grouponVo.setName(name);
        grouponVo.setBeginTime(beginTime);
        grouponVo.setEndTime(endTime);
        grouponVo.setState(state);
        grouponVo.setShopId(shopId);
        grouponVo.setGoodsSPUId(goodsSPUId);
        grouponVo.setStrategy(strategy);
        grouponVo.setGmtCreated(gmtCreated);
        grouponVo.setGmtModified(gmtModified);

        return grouponVo;
    }

    @Override
    public VoObject createSimpleVo() {
        GrouponVo grouponVo = new GrouponVo();
        grouponVo.setStrategy(strategy);
        grouponVo.setBeginTime(beginTime);
        grouponVo.setEndTime(endTime);

        return grouponVo;
    }
}
