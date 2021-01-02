package cn.edu.xmu.groupon.model.bo;

import cn.edu.xmu.groupon.model.po.GrouponPo;
import cn.edu.xmu.groupon.model.vo.GrouponVo;
import cn.edu.xmu.ooad.model.VoObject;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * @author XC 3304
 * Created at 2020-12-02 15:50
 * Modified at 2020-12-26 09:19
 */

@Data
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

        State(int code, String name) {
            this.code = code;
            this.name = name;
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
