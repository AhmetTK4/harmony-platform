package com.atk.payment;

import com.atk.payment.consumer.PaymentConsumer;
import com.atk.payment.config.RabbitMQConfig;
import com.atk.payment.model.OrderCreatedEvent;
import com.atk.payment.model.PaymentCompletedEvent;
import com.atk.payment.model.ShippingCreatedEvent;
import com.atk.payment.service.PaymentSimulator;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import static org.mockito.Mockito.*;

class PaymentConsumerTest {
    private final RabbitTemplate rabbit = mock(RabbitTemplate.class);
    private final PaymentSimulator simulator = mock(PaymentSimulator.class);
    private final PaymentConsumer consumer = new PaymentConsumer(rabbit, simulator);
    private final OrderCreatedEvent order = new OrderCreatedEvent("order-1", "user-1", "product-1", 2, "CREATED", "demo-address");

    @Test void successfulPaymentPublishesPaymentAndShipping() {
        when(simulator.succeeds()).thenReturn(true);
        consumer.handleOrderCreated(order);
        verify(rabbit).convertAndSend(RabbitMQConfig.PAYMENT_EXCHANGE, RabbitMQConfig.PAYMENT_ROUTING_KEY,
                new PaymentCompletedEvent("order-1", true));
        verify(rabbit).convertAndSend(RabbitMQConfig.SHIPPING_EXCHANGE, RabbitMQConfig.SHIPPING_ROUTING_KEY,
                new ShippingCreatedEvent("order-1", "SHIPPING_STARTED", "demo-address"));
        verifyNoMoreInteractions(rabbit);
    }

    @Test void failedPaymentPublishesRollbackWithoutShipping() {
        when(simulator.succeeds()).thenReturn(false);
        consumer.handleOrderCreated(order);
        verify(rabbit).convertAndSend(RabbitMQConfig.ROLLBACK_EXCHANGE, RabbitMQConfig.ROLLBACK_ROUTING_KEY,
                new OrderCreatedEvent("order-1", "user-1", "product-1", 2, "PAYMENT_FAILED", "demo-address"));
        verifyNoMoreInteractions(rabbit);
    }
}
