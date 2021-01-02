package cn.edu.xmu.outer.model.bo;

import lombok.Data;

import java.io.Serializable;

@Data
public class Aftersale implements Serializable {
    Long id;
    Long orderItemId;
    Long customerId;
    Long shopId;
    Byte type;
    String reason;
    String consignee;
    String conflusion;
    Long refund;
    Long quantility;
    Long regionId;
    String detail;
    String mobile;
    String customerLogSn;
    String shopLogSn;
    Byte state;
    Byte beDeleted;
}
