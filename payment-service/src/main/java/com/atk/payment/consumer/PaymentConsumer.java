package com.atk.payment.consumer;

import com.atk.payment.config.RabbitMQConfig;
import com.atk.payment.model.OrderCreatedEvent;
import com.atk.payment.model.PaymentCompletedEvent;
import com.atk.payment.model.ShippingCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentConsumer {

    private final RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = "order.queue")
    public void handleOrderCreated(OrderCreatedEvent event) {
        log.info("✅ Payment Service: Received order: {}", event);

        boolean paymentResult = simulatePayment();

        if (paymentResult) {
            log.info("💳 Payment successful for Order ID: {}", event.orderId());

            // 1️⃣ Publish Payment Completed Event
            PaymentCompletedEvent completedEvent = new PaymentCompletedEvent(
                    event.orderId(), true
            );
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.PAYMENT_EXCHANGE,
                    RabbitMQConfig.PAYMENT_ROUTING_KEY,
                    completedEvent
            );

            // 2️⃣ Publish Shipping Created Event
            ShippingCreatedEvent shippingEvent = new ShippingCreatedEvent(
                    event.orderId(),
                    "SHIPPING_STARTED",
                    event.address()
            );
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.SHIPPING_EXCHANGE,
                    RabbitMQConfig.SHIPPING_ROUTING_KEY,
                    shippingEvent
            );

        } else {
            log.warn("❌ Payment failed for Order ID: {}", event.orderId());

            // Publish Rollback Event
            OrderCreatedEvent rollbackEvent = new OrderCreatedEvent(
                    event.orderId(),
                    event.userId(),
                    event.productId(),
                    event.quantity(),
                    "PAYMENT_FAILED",
                    event.address()
            );
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.ROLLBACK_EXCHANGE,
                    RabbitMQConfig.ROLLBACK_ROUTING_KEY,
                    rollbackEvent
            );
        }
    }

    private boolean simulatePayment() {
        return Math.random() > 0.2;
    }
}