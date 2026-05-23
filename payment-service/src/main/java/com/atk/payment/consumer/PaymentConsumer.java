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
    public void handleOrderCreated(OrderCreatedEvent orderEvent) {

        log.info("Payment service order bilgisini aldı. Order id: {}", orderEvent.orderId());

        boolean isPaymentSuccess = makePayment();

        if (isPaymentSuccess) {

            log.info("Ödeme başarılı. Order id: {}", orderEvent.orderId());

            PaymentCompletedEvent paymentCompletedEvent = new PaymentCompletedEvent(
                    orderEvent.orderId(),
                    true
            );

            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.PAYMENT_EXCHANGE,
                    RabbitMQConfig.PAYMENT_ROUTING_KEY,
                    paymentCompletedEvent
            );

            log.info("Payment completed event gönderildi. Order id: {}", orderEvent.orderId());

            ShippingCreatedEvent shippingCreatedEvent = new ShippingCreatedEvent(
                    orderEvent.orderId(),
                    "SHIPPING_STARTED",
                    orderEvent.address()
            );

            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.SHIPPING_EXCHANGE,
                    RabbitMQConfig.SHIPPING_ROUTING_KEY,
                    shippingCreatedEvent
            );

            log.info("Shipping event gönderildi. Order id: {}", orderEvent.orderId());

        } else {

            log.warn("Ödeme başarısız oldu. Order id: {}", orderEvent.orderId());

            OrderCreatedEvent failedOrderEvent = new OrderCreatedEvent(
                    orderEvent.orderId(),
                    orderEvent.userId(),
                    orderEvent.productId(),
                    orderEvent.quantity(),
                    "PAYMENT_FAILED",
                    orderEvent.address()
            );

            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.ROLLBACK_EXCHANGE,
                    RabbitMQConfig.ROLLBACK_ROUTING_KEY,
                    failedOrderEvent
            );

            log.info("Rollback event gönderildi. Order id: {}", orderEvent.orderId());
        }
    }

    private boolean makePayment() {
        double randomValue = Math.random();

        if (randomValue > 0.2) {
            return true;
        } else {
            return false;
        }
    }
}