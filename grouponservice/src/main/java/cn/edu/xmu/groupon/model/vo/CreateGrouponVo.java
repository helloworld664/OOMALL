package cn.edu.xmu.groupon.model.vo;

import cn.edu.xmu.groupon.model.po.GrouponPo;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author XC 3304
 * Created at 2020-12-04 11:24
 * Modified at 2020-12-14 23:16
 */

@Data
public class CreateGrouponVo {
    private String strategy;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime beginTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    public GrouponPo createGroupon() {
        GrouponPo grouponPo = new GrouponPo();
        grouponPo.setStrategy(strategy);
        grouponPo.setBeginTime(beginTime);
        grouponPo.setBeginTime(endTime);
        return grouponPo;
    }
}
