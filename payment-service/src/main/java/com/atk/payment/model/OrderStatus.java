package com.atk.payment.model;

public enum OrderStatus {
    CREATED,
    PAYMENT_FAILED,
    PAYMENT_SUCCESS,
    SHIPPING_STARTED,
    SHIPPING_FAILED,
    COMPLETED
}