package cn.edu.xmu.goods.model.vo;

import cn.edu.xmu.goods.model.bo.Comment;
import cn.edu.xmu.ooad.model.VoObject;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author XC, 3304
 * Created at 2021-01-02 03:29
 * Modified at 2021-01-02 03:29
 */

@Data
public class CommentRetVo implements VoObject {
    Long id;
    CustomerRetVo customerRetVo;
    Long goodsSkuId;
    Byte type;
    String content;
    Byte state;
    LocalDateTime gmtCreate;
    LocalDateTime gmtModified;

    public CommentRetVo(Comment comment) {
        this.goodsSkuId=comment.getGoodsSkuId();
    }

    @Override
    public Object createVo() {
        return null;
    }

    @Override
    public Object createSimpleVo() {
        return null;
    }
}
