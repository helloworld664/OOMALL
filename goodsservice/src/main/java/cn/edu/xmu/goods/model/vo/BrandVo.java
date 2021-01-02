package cn.edu.xmu.goods.model.vo;

import cn.edu.xmu.goods.model.bo.Brand;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
@ApiModel(description = "商铺模板视图对象")
public class BrandVo {

    @ApiModelProperty(value="品牌名称")
    private String name;

    @ApiModelProperty(value="品牌描述")
    private String detail;

    @ApiModelProperty(value="图片链接")
    private String imageUrl;

    public Brand createBrand(){
        Brand bo = new Brand();
        bo.setName(this.name);
        bo.setDetail(this.detail);
        bo.setImageUrl(this.imageUrl);
        return bo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
