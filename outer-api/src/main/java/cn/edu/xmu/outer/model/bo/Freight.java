package cn.edu.xmu.outer.model.bo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class Freight implements Serializable {
    private Long id;
    private String name;
    private Byte type;
    private Integer unit;
    private Byte isDefault;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;

}
