package cn.edu.xmu.groupon.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.map.repository.config.EnableMapRepositories;

import java.time.LocalDateTime;

/**
 * @author XC 3304
 * Created at 2020-12-03 16:49
 * Modified at 2020-12-21 01:00
 */

@Data
@ApiModel
public class GrouponSimpleRetVo {
    @ApiModelProperty(name = "strategy", value = "团购规则JSON")
    private String strategy;

    @ApiModelProperty(name = "beginTime", value = "开始时间")
    private LocalDateTime beginTime;

    @ApiModelProperty(name = "endTime", value = "结束时间")
    private LocalDateTime endTime;

    public GrouponSimpleRetVo() {

    }
}
