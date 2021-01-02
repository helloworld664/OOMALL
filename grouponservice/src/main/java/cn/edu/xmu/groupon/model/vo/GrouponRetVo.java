package cn.edu.xmu.groupon.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author XC 3304
 * Created at 2020-12-03 21:32
 * Modified at 2020-12-03 21:32
 */

@Data
public class GrouponRetVo {
    private Long id;

    private String name;

    private LocalDateTime beginTime;

    private LocalDateTime endTime;

    private Byte state;

    private Long shopId;

    private Long goodsSPUId;

    private String strategy;

    private LocalDateTime gmtCreated;

    private LocalDateTime gmtModified;
}
