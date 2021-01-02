package cn.edu.xmu.goods.model.vo;

import cn.edu.xmu.goods.model.bo.Comment;
import io.lettuce.core.StrAlgoArgs;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "评论模板视图对象")
public class CommentVo {

    @ApiModelProperty(value="评论类型：0好评1中评2差评")
    private Byte type;

    @ApiModelProperty(value="评论内容")
    private String content;

    public Comment createComment(){
        Comment bo = new Comment();
        bo.setType(this.type);
        bo.setContent(this.content);
        return bo;
    }
}
