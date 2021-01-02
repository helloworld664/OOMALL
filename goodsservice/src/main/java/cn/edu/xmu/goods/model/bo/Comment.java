package cn.edu.xmu.goods.model.bo;

import cn.edu.xmu.goods.model.po.CommentPo;
import cn.edu.xmu.goods.model.vo.CommentRetVo;
import cn.edu.xmu.goods.model.vo.CommentVo;
import cn.edu.xmu.goods.model.vo.CommentStatusVo;
import cn.edu.xmu.ooad.model.VoObject;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;


@Data
public class Comment implements VoObject, Serializable{
    private Long id;
    private Long customerId;
    private Long goodsSkuId;
    
    private Long orderItemId;
    
    private Byte type;
    private String content;
    private Byte state;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;

    public Comment() {
    }

    public enum State {
        NOT_EXAMINE(0, "未审核"),
        SUCCESS(4, "评论成功"),
        NOT_PASS(6, "未通过");

        private static final Map<Integer, State> stateMap;

        static { //由类加载机制，静态块初始加载对应的枚举属性到map中，而不用每次取属性时，遍历一次所有枚举值
            stateMap = new HashMap();
            for (Comment.State enum1 : values()) {
                stateMap.put(enum1.code, enum1);
            }
        }

        private int code;
        private String description;

        State(int code, String description) {
            this.code = code;
            this.description = description;
        }

        public static Comment.State getTypeByCode(Integer code) {
            return stateMap.get(code);
        }

        public Byte getCode() {
            return (byte) code;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * 构造函数
     *
     * @param po 用PO构造
     * @return Comment
     */
    public Comment(CommentPo po) {
        this.id = po.getId();
        this.customerId = po.getCustomerId();
        this.goodsSkuId = po.getGoodsSkuId();
        this.type=po.getType();
        this.content=po.getContent();
        this.state=po.getState();
        this.gmtCreate = po.getGmtCreate();
        this.gmtModified = po.getGmtModified();
    }

   /** @Override
    public Object createVo() {
        return new CommentRetVo(this);
    }

    @Override
    public CommentSimpleRetVo createSimpleVo() { return new CommentSimpleRetVo(this); }


    /**
     * 用vo对象创建更新po对象
     * @param vo vo对象
     * @return po对象
     */
    public CommentPo createUpdatePo(CommentVo vo){
        CommentPo po = new CommentPo();
        po.setId(this.getId());
        po.setCustomerId(null);
        po.setGoodsSkuId(null);
        po.setType(vo.getType());
        po.setContent(vo.getContent());
        po.setState(null);
        po.setGmtCreate(null);
        po.setGmtModified(LocalDateTime.now());
        return po;
    }

    public CommentPo createUpdatePo(CommentStatusVo vo){
        CommentPo po = new CommentPo();
        po.setId(this.getId());
        po.setCustomerId(null);
        po.setGoodsSkuId(null);
        po.setType((byte) vo.getCode());
        po.setContent(vo.getName());
        po.setState(null);
        po.setGmtCreate(null);
        po.setGmtModified(LocalDateTime.now());
        return po;
    }

    /**
     * 用bo创建po
     * @return CommentPo
     */
    public CommentPo createCommentPo() {
        CommentPo po = new CommentPo();
        po.setId(this.getId());
        po.setCustomerId(this.getCustomerId());
        po.setGoodsSkuId(this.getGoodsSkuId());
        po.setType(this.getType());
        po.setContent(this.getContent());
        po.setState(this.getState());
        po.setGmtCreate(this.getGmtCreate());
        po.setGmtModified(this.getGmtModified());
        return po;
    }

    @Override
    public Object createVo() {
        return null;
    }

    public VoObject createRetVo() {
        return new CommentRetVo(this);
    }

    @Override
    public VoObject createSimpleVo() {
        return createRetVo();
    }
}

