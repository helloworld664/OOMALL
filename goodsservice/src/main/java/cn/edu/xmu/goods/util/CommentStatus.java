package cn.edu.xmu.goods.util;

/**
 * 评论状态码
 */
public enum CommentStatus {

    UNEXAMINE(0,"未审核"),
    SUCCESS(1,"评论成功"),
    UNPASS(2,"未通过");

    private int code;
    private String description;
    CommentStatus(int code, String description) {
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
