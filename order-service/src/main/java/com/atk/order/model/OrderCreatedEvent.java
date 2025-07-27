package com.atk.order.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public record OrderCreatedEvent(
        @JsonProperty("orderId") String orderId,
        @JsonProperty("userId") String userId,
        @JsonProperty("productId") String productId,
        @JsonProperty("quantity") int quantity,
        @JsonProperty("status") String status,
        @JsonProperty("address") String address
) implements Serializable {}