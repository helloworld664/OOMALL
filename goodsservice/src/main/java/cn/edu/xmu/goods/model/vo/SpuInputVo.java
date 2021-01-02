package cn.edu.xmu.goods.model.vo;

import cn.edu.xmu.ooad.model.VoObject;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel(description = "spu详细信息")
public class SpuInputVo implements VoObject {

    private String name;

    private String decription;

    private String specs;

    @Override
    public Object createVo() {
        return null;
    }

    @Override
    public Object createSimpleVo() {
        return null;
    }
}
