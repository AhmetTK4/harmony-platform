package com.atk.payment.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public record ShippingCreatedEvent(
        @JsonProperty("orderId") String orderId,
        @JsonProperty("status") String status,
        @JsonProperty("address") String address
) implements Serializable {}