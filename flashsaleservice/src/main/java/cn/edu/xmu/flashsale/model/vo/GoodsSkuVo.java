package cn.edu.xmu.flashsale.model.vo;

import lombok.Data;

/**
 * @author XC
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
