package com.atk.shipping.consumer;

import com.atk.shipping.model.ShippingCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ShippingConsumer {

    @RabbitListener(queues = "order.shipping.queue")
    public void handleShipping(ShippingCreatedEvent event){
        log.info("Shipping started for Order ID: {} to address: {}", event.orderId(), event.address());
        // Simulate shipping delay or success
        log.info("Shipping completed for Order ID: {}", event.orderId());
    }
}