package cn.edu.xmu.groupon.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.map.repository.config.EnableMapRepositories;

import java.time.LocalDateTime;

/**
 * @author XC
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
