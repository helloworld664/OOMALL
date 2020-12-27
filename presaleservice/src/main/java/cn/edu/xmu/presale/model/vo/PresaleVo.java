package cn.edu.xmu.presale.model.vo;

import cn.edu.xmu.ooad.model.VoObject;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author XC
 */

@Data
public class PresaleVo implements VoObject {
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

    @Override
    public Object createVo() {
        return null;
    }

    @Override
    public Object createSimpleVo() {
        return null;
    }
}
