package cn.edu.xmu.outer.service;

import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.outer.model.bo.Freight;
import cn.edu.xmu.outer.model.bo.MyReturn;

/**
 * Create by 王薪蕾
 * on 2020/12/15
 */
public interface IFreightService {
    //商品模块集成

    /**
     * 获取运费模板信息
     *
     * @param freightId
     * @return
     */
    MyReturn<Freight> getFreightById(Long freightId);

}
