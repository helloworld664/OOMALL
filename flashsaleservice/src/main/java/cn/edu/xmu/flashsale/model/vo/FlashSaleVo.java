package cn.edu.xmu.flashsale.model.vo;

import cn.edu.xmu.ooad.model.VoObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author XC 3304
 * Created at 2020-12-07 11:21
 * Modified at 2020-12-24 20:13
 */

@Api
@ApiModel
@Data
public class FlashSaleVo implements VoObject {
    private Long id;

    private LocalDateTime flashDate;

    private Long timeSegId;

    private LocalDateTime gmtCreated;

    private LocalDateTime gmtModified;

    private Byte state;

    @Override
    public Object createVo() {
        return null;
    }

    @Override
    public Object createSimpleVo() {
        return null;
    }
}
