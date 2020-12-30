package cn.edu.xmu.flashsale.model.bo;

import cn.edu.xmu.flashsale.model.po.FlashSalePo;
import cn.edu.xmu.flashsale.model.vo.FlashSaleSimpleVo;
import cn.edu.xmu.flashsale.model.vo.FlashSaleVo;
import cn.edu.xmu.flashsale.model.vo.TimeSegmentVo;
import cn.edu.xmu.ooad.model.VoObject;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * @author XC
 */

@Data
public class FlashSale implements VoObject {
    private Long id;

    private LocalDateTime flashDate;

    private Long timeSegId;

    private TimeSegmentVo timeSeg;

    private LocalDateTime gmtCreated;

    private LocalDateTime gmtModified;

    private Byte state;

    public enum State {
        OFFSHELVES(0, "已下线"),
        ONSHELVES(1, "已上线"),
        DELETED(2, "已删除");

        private int code;

        private String name;

        private static final Map<Integer, FlashSale.State> stateMap;

        static { //由类加载机制，静态块初始加载对应的枚举属性到map中，而不用每次取属性时，遍历一次所有枚举值
            stateMap = new HashMap();
            for (FlashSale.State stateEnum : values()) {
                stateMap.put(stateEnum.code, stateEnum);
            }
        }

        public static FlashSale.State getTypeByCode(int code) {
            return stateMap.get(code);
        }

        public byte getCode() {
            return (byte) code;
        }

        public String getName() {
            return name;
        }

        public static Map<Integer, State> getStateMap() {
            return stateMap;
        }

        State(int code, String name) {
            this.code = code;
            this.name = name;
        }
    }

    public FlashSale(FlashSalePo flashSalePo) {
        id = flashSalePo.getId();
        flashDate = flashSalePo.getFlashDate();
        timeSegId = flashSalePo.getTimeSegId();
        gmtCreated = flashSalePo.getGmtCreate();
        gmtModified = flashSalePo.getGmtModified();
        state = flashSalePo.getState();
    }

    @Override
    public VoObject createVo() {
        FlashSaleVo flashSaleVo = new FlashSaleVo();
        flashSaleVo.setId(id);
        flashSaleVo.setFlashDate(flashDate);
        flashSaleVo.setTimeSegId(timeSegId);
        flashSaleVo.setGmtCreated(gmtCreated);
        flashSaleVo.setGmtModified(gmtModified);
        return flashSaleVo;
    }

    @Override
    public VoObject createSimpleVo() {
        FlashSaleSimpleVo flashSaleSimpleVo = new FlashSaleSimpleVo();
        flashSaleSimpleVo.setFlashDate(flashDate);
        return flashSaleSimpleVo;
    }
}
