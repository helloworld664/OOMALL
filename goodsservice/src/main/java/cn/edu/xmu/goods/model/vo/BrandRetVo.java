package cn.edu.xmu.goods.model.vo;


import cn.edu.xmu.goods.model.bo.Brand;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@ApiModel(description = "品牌视图对象")
public class BrandRetVo {
    @ApiModelProperty(value = "品牌id")
    private Long id;

    @ApiModelProperty(value = "品牌名")
    private String name;

    @ApiModelProperty(value = "品牌描述")
    private String detail;

    @ApiModelProperty(value = "URL链接")
    private String imageUrl;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime gmtCreate;

    @ApiModelProperty(value = "修改时间")
    private LocalDateTime gmtModified;

    public BrandRetVo(Brand brand) {
        this.id = brand.getId();
        this.name = brand.getName();
        this.detail = brand.getDetail();
        this.imageUrl = brand.getImageUrl();
        this.gmtCreate = brand.getGmtCreate();
        this.gmtModified = brand.getGmtModified();
    }
}
