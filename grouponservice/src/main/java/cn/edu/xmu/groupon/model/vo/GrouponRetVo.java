package cn.edu.xmu.groupon.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author XC
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
