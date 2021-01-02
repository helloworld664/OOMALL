package cn.edu.xmu.goods.util;

/**
 * 评论状态码
 */
public enum ShopStatus {

    UNEXAMINE(0,"待审核"),
    UNPASS(1,"审核未通过"),
    PASS(2,"审核通过"),
    SHOP_DELETE(3,"被删除"),
    SHOP_ONLINE(4,"已上线"),
    SHOP_OFFLINE(5,"已下线");

    private int code;
    private String description;
    ShopStatus(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode(){
        return this.code;
    }

    public String getDescription() {
        return description;
    }
}
