package cn.edu.xmu.goods.model.vo;

import cn.edu.xmu.goods.util.CommentStatus;
import lombok.Data;

@Data
public class CommentStatusVo {
    private int Code;

    private String name;
    public CommentStatusVo(CommentStatus status){
        Code=Integer.valueOf(status.getCode());
        name=status.getDescription();
    }
}
