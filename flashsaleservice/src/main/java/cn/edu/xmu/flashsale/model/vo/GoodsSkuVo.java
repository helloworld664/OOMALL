package cn.edu.xmu.flashsale.model.vo;

import lombok.Data;

/**
 * @author XC 3304
 * Created at 2020-12-26 22:56
 * Modified at 2020-12-26 22:56
 */

@Data
public class GoodsSkuVo {
    private Long id;

    private String name;

    private Long originalPrice;

    private String configuration;

    private Long weight;

    private String imageUrl;

    private Integer inventory;

    private String detail;
}
