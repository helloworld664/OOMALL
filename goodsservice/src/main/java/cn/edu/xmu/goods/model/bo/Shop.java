package cn.edu.xmu.goods.model.bo;

import cn.edu.xmu.goods.model.po.ShopPo;
import cn.edu.xmu.goods.model.vo.ShopVo;
import cn.edu.xmu.ooad.model.VoObject;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;


@Data
public class Shop implements VoObject, Serializable{
    private Long id;
    private String name;
    private Byte state;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;

    public enum State {
        NOT_EXAMINE(0, "未审核"),
        OFFSHELVES(1, "未上线"),
        ONSHELVES(2, "上线"),
        CLOSE(3, "关闭"),
        NOT_PASS(4, "审核未通过");

        private int code;

        private String name;

        private static final Map<Integer, Shop.State> stateMap;

        static { //由类加载机制，静态块初始加载对应的枚举属性到map中，而不用每次取属性时，遍历一次所有枚举值
            stateMap = new HashMap();
            for (Shop.State stateEnum : values()) {
                stateMap.put(stateEnum.code, stateEnum);
            }
        }

        public static Shop.State getTypeByCode(int code) {
            return stateMap.get(code);
        }

        public byte getCode() {
            return (byte) code;
        }

        public String getName() {
            return name;
        }

        public static Map<Integer, State> getStateMap() {
            return stateMap;
        }

        State(int code, String name) {
            this.code = code;
            this.name = name;
        }
    }

    public Shop() {
    }

    /**
     * 构造函数
     *
     * @param po 用PO构造
     * @return Shop
     */
    public Shop(ShopPo po) {
        this.id = po.getId();
        this.name = po.getName();
        this.state = po.getState();
        this.gmtCreate = po.getGmtCreate();
        this.gmtModified = po.getGmtModified();
    }

   /** @Override
    public Object createVo() {
        return new ShopRetVo(this);
    }

    @Override
    public ShopSimpleRetVo createSimpleVo() { return new ShopSimpleRetVo(this); }


    /**
     * 用vo对象创建更新po对象
     * @param vo vo对象
     * @return po对象
     */
    public ShopPo createUpdatePo(ShopVo vo){
        ShopPo po = new ShopPo();
        po.setId(this.getId());
        po.setName(vo.getName());
        po.setState(null);
        po.setGmtCreate(null);
        po.setGmtModified(LocalDateTime.now());
        return po;
    }


    /**
     * 用bo创建po
     * @return ShopPo
     */
    public ShopPo createShopPo() {
        ShopPo po = new ShopPo();
        po.setId(this.getId());
        po.setName(this.getName());
        po.setState(this.getState());
        po.setGmtCreate(this.getGmtCreate());
        po.setGmtModified(this.getGmtModified());
        return po;
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

