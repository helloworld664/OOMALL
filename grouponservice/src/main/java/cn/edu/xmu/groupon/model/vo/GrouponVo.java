package cn.edu.xmu.groupon.model.vo;

import cn.edu.xmu.ooad.model.VoObject;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author XC
 */

@Data
public class GrouponVo implements VoObject {
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

    @Override
    public Object createVo() {
        return null;
    }

    @Override
    public Object createSimpleVo() {
        return null;
    }
}
