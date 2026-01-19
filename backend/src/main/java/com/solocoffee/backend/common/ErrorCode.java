package com.solocoffee.backend.common;

import lombok.Getter;

@Getter
public enum ErrorCode {

    // 系统错误
    SYSTEM_ERROR(50001, "系统内部错误"),
    DATABASE_ERROR(50002, "数据库错误"),
    THIRD_PARTY_ERROR(50003, "第三方服务错误"),

    // 业务错误
    PARAMETER_ERROR(40001, "请求参数错误"),
    VALIDATION_ERROR(40002, "验证失败"),
    ORDER_NOT_FOUND(40401, "订单不存在"),
    INVALID_ORDER_STATUS(40003, "无效的订单状态流转"),
    INSUFFICIENT_INVENTORY(40004, "库存不足"),
    PAYMENT_FAILED(40005, "支付失败"),
    CANCEL_FAILED(40006, "取消失败"),
    REFUND_FAILED(40007, "退款失败"),

    // 认证授权错误
    UNAUTHORIZED(40101, "未授权"),
    TOKEN_EXPIRED(40102, "令牌过期"),
    TOKEN_INVALID(40103, "令牌无效"),
    FORBIDDEN(40301, "禁止访问"),

    // 资源错误
    RESOURCE_NOT_FOUND(40401, "资源不存在"),
    ADDRESS_NOT_FOUND(40402, "地址不存在"),
    PRODUCT_NOT_FOUND(40403, "商品不存在"),
    STORE_NOT_FOUND(40404, "店铺不存在"),
    CUSTOMER_NOT_FOUND(40405, "客户不存在"),

    // 成功
    SUCCESS(200, "请求成功");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
