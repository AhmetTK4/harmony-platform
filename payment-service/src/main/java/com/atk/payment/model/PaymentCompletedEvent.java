package com.atk.payment.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public record PaymentCompletedEvent(
        @JsonProperty("orderId") String orderId,
        @JsonProperty("paymentSuccess") boolean paymentSuccess
)implements Serializable {}