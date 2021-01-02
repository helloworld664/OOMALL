package cn.edu.xmu.goods.model.vo;

import cn.edu.xmu.goods.model.bo.Categories;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "商品类别模板视图对象")
public class CategoriesVo {

    @ApiModelProperty(value="种类名称")
    private String name;

    public Categories createCategories(){
        Categories bo = new Categories();
        bo.setName(this.name);
        return bo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
