package cn.edu.xmu.presale.model.vo;

import cn.edu.xmu.presale.model.po.PresalePo;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * @author XC 3304
 * Created at 2020-12-06 08:47
 * Modified at 2020-12-24 14:58
 */

@Data
public class CreatePresaleVo {

    private String name;
    @NotNull
    private LocalDateTime beginTime;
    @NotNull
    private LocalDateTime payTime;
    @NotNull
    private LocalDateTime endTime;
    @Min(value = 0,message = "为自然数")
    private Integer quantity;
    @Min(value = 0,message = "不应小于0")
    private Long advancePayPrice;
    @Min(value = 0,message = "不应小于0")
    private Long restPayPrice;


    public PresalePo createPresale() {
        PresalePo presalePo = new PresalePo();
        presalePo.setName(name);
        presalePo.setAdvancePayPrice(advancePayPrice);
        presalePo.setRestPayPrice(restPayPrice);
        presalePo.setQuantity(quantity);
        presalePo.setBeginTime(beginTime);
        presalePo.setPayTime(payTime);
        presalePo.setEndTime(endTime);
        return presalePo;
    }
}
