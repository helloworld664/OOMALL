package cn.edu.xmu.groupon.model.vo;

import cn.edu.xmu.groupon.model.po.GrouponPo;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author XC
 */

public class CreateGrouponVo {
    private String strategy;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime beginTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    public String getStrategy() {
        return strategy;
    }

    public void setStrategy(String strategy) {
        this.strategy = strategy;
    }

    public LocalDateTime getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(LocalDateTime beginTime) {
        this.beginTime = beginTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public GrouponPo createGroupon() {
        GrouponPo grouponPo = new GrouponPo();
        grouponPo.setStrategy(strategy);
        grouponPo.setBeginTime(beginTime);
        grouponPo.setBeginTime(endTime);
        return grouponPo;
    }
}
