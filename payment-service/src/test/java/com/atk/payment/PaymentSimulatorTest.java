package com.atk.payment;

import com.atk.payment.service.PaymentSimulator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PaymentSimulatorTest {
    @Test
    void zeroAlwaysFails() {
        assertFalse(new PaymentSimulator(0).succeeds());
    }

    @Test
    void oneAlwaysSucceeds() {
        assertTrue(new PaymentSimulator(1).succeeds());
    }

    @Test
    void rejectsInvalidRates() {
        for (double rate : new double[]{-0.1, 1.1, Double.NaN, Double.POSITIVE_INFINITY}) {
            assertThrows(IllegalArgumentException.class, () -> new PaymentSimulator(rate));
        }
    }
}
