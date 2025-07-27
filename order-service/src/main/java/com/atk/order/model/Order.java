package com.atk.order.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public record Order(
        @JsonProperty("orderId") String orderId,
        @JsonProperty("userId") String userId,
        @JsonProperty("productId") String productId,
        @JsonProperty("quantity") int quantity,
        @JsonProperty("address") String address
) implements Serializable {}