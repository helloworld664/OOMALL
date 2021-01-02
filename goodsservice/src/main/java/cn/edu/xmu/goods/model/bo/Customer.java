package cn.edu.xmu.goods.model.bo;

import cn.edu.xmu.goods.model.po.CustomerPo;
import cn.edu.xmu.ooad.model.VoObject;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * @author XC, 3304
 * Created at 2021-01-02 14:30
 * Modified at 2021-01-02 14:30
 */

@Data
public class Customer implements VoObject, Serializable {
    private Long id;

    private String userName;

    private String password;

    private String realName;

    private Byte gender;

    private LocalDate birthday;

    private Integer point;

    private Byte state;

    private String email;

    private String mobile;

    private Byte beDeleted;

    private LocalDateTime gmtCreated;

    private LocalDateTime gmtModified;

    public enum State {
        BACKEND(0, "后台用户"),
        NORMAL(4, "正常用户"),
        FORBIDDEN(6, "被封禁用户");

        private static final Map<Integer, Customer.State> stateMap;

        static { //由类加载机制，静态块初始加载对应的枚举属性到map中，而不用每次取属性时，遍历一次所有枚举值
            stateMap = new HashMap();
            for (Customer.State stateEnum : values()) {
                stateMap.put(stateEnum.code, stateEnum);
            }
        }

        private int code;

        private String name;

        State(int code, String name) {
            this.code = code;
            this.name = name;
        }

        public static Customer.State getTypeByCode(int code) {
            return stateMap.get(code);
        }

        public byte getCode() {
            return (byte) code;
        }

        public String getDescription() {
            return name;
        }

    }

    public Customer(CustomerPo customerPo) {
        customerPo.setId(this.id);
        customerPo.setUserName(this.userName);
        customerPo.setRealName(this.realName);
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
