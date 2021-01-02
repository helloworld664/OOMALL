package cn.edu.xmu.goods.model.vo;

import cn.edu.xmu.goods.model.po.CustomerPo;
import lombok.Data;

/**
 * @author XC, 3304
 * Created at 2021-01-02 03:30
 * Modified at 2021-01-02 03:30
 */

@Data
public class CustomerRetVo {
    Long id;
    String userName;
    String realName;
    public CustomerRetVo(CustomerPo customerPo)
    {
        this.id=customerPo.getId();
        this.realName=customerPo.getRealName();
        this.userName=customerPo.getUserName();
    }
}
