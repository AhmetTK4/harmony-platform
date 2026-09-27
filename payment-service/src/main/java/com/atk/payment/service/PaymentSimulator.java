package com.atk.payment.service;

import org.springframework.stereotype.Service;

/** Learning-only simulation, isolated so both outcomes can be tested deterministically. */
@Service
public class PaymentSimulator {
    public boolean succeeds() {
        return Math.random() > 0.2;
    }
}
