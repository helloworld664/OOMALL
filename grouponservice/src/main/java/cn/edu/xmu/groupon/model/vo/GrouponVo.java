package cn.edu.xmu.groupon.model.vo;

import cn.edu.xmu.ooad.model.VoObject;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author XC 3304
 * Created at 2020-12-03 20:34
 * Modified at 2020-12-21 00:26
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
