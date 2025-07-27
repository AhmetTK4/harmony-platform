package com.atk.payment.producer;

import com.atk.payment.model.ShippingCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShippingProducer {

    private final RabbitTemplate rabbitTemplate;

    public void sendShippingEvent(ShippingCreatedEvent event) {
        rabbitTemplate.convertAndSend("order.shipping.queue", event);
    }
}