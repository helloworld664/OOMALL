package cn.edu.xmu.flashsale.model.bo;

import cn.edu.xmu.flashsale.model.po.FlashSalePo;
import cn.edu.xmu.flashsale.model.vo.FlashSaleSimpleVo;
import cn.edu.xmu.flashsale.model.vo.FlashSaleVo;
import cn.edu.xmu.flashsale.model.vo.TimeSegmentVo;
import cn.edu.xmu.ooad.model.VoObject;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * @author XC
 */
public class FlashSale implements VoObject {
    private Long id;

    private LocalDateTime flashDate;

    private Long timeSegId;

    private TimeSegmentVo timeSeg;

    private LocalDateTime gmtCreated;

    private LocalDateTime gmtModified;

    private Byte state;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getFlashDate() {
        return flashDate;
    }

    public void setFlashDate(LocalDateTime flashDate) {
        this.flashDate = flashDate;
    }

    public Long getTimeSegId() {
        return timeSegId;
    }

    public void setTimeSegId(Long timeSegId) {
        this.timeSegId = timeSegId;
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

    public Byte getState() {
        return state;
    }

    public void setState(Byte state) {
        this.state = state;
    }

    public enum State {
        OFFSHELVES(0, "已下线"),
        ONSHELVES(1, "已上线"),
        DELETED(2, "已删除"),
        SUCCESS(3, "成功"),
        FAILURE(4, "失败"),
        CREATED(5, "已创建");

        private int code;

        private String value;

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

        public String getValue() {
            return value;
        }

        public static Map<Integer, State> getStateMap() {
            return stateMap;
        }

        State(int code, String value) {
            this.code = code;
            this.value = value;
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
