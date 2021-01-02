package cn.edu.xmu.goods.model.vo;


import cn.edu.xmu.goods.model.bo.Brand;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "品牌视图对象")
public class BrandSimpleRetVo {
    @ApiModelProperty(value = "品牌id")
    private Long id;

    @ApiModelProperty(value = "品牌名")
    private String name;

    public BrandSimpleRetVo(Brand brand) {
        this.id = brand.getId();
        this.name = brand.getName();
    }
}
