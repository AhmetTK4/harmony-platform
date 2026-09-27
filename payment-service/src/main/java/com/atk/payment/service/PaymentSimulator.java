package com.atk.payment.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

/** Learning-only simulation, isolated so both outcomes can be tested deterministically. */
@Service
public class PaymentSimulator {
    private final double successRate;

    public PaymentSimulator(@Value("${payment.simulation.success-rate:0.8}") double successRate) {
        if (!Double.isFinite(successRate) || successRate < 0 || successRate > 1) {
            throw new IllegalArgumentException("Payment simulation success rate must be between 0 and 1");
        }
        this.successRate = successRate;
    }

    public boolean succeeds() {
        return Math.random() < successRate;
    }
}
