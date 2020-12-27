package cn.edu.xmu.flashsale.model.vo;

import cn.edu.xmu.ooad.model.VoObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;

import java.time.LocalDateTime;

/**
 * @author XC
 */

@Api
@ApiModel
public class FlashSaleVo implements VoObject {
    private Long id;

    private LocalDateTime flashDate;

    private Long timeSegId;

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

    @Override
    public Object createVo() {
        return null;
    }

    @Override
    public Object createSimpleVo() {
        return null;
    }
}
