package cn.edu.xmu.flashsale.model.vo;

import cn.edu.xmu.flashsale.model.po.FlashSalePo;
import cn.edu.xmu.ooad.model.VoObject;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import org.springframework.boot.jdbc.metadata.DataSourcePoolMetadata;

import java.time.LocalDateTime;

/**
 * @author XC
 */

@Data
@ApiModel
public class FlashSaleSimpleVo implements VoObject {
    private LocalDateTime flashDate;

    public LocalDateTime getFlashDate() {
        return flashDate;
    }

    public void setFlashDate(LocalDateTime flashDate) {
        this.flashDate = flashDate;
    }

    public FlashSalePo createFlashSale() {
        FlashSalePo flashSalePo = new FlashSalePo();
        flashSalePo.setFlashDate(flashDate);
        return flashSalePo;
    }

    @Override
    public VoObject createVo() {
        return null;
    }

    @Override
    public VoObject createSimpleVo() {
        return null;
    }
}
