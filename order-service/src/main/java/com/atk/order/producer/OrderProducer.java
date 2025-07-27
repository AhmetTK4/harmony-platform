package com.atk.order.producer;


import com.atk.order.model.Order;
import com.atk.order.model.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
@Service
@RequiredArgsConstructor
public class OrderProducer {

    private final RabbitTemplate rabbitTemplate;

    public void sendOrder(Order order) {
        OrderCreatedEvent event = new OrderCreatedEvent(
                order.orderId(),
                order.userId(),
                order.productId(),
                order.quantity(),
                "CREATED",
                order.address()

        );
        rabbitTemplate.convertAndSend("order.exchange", "order.created", event);
    }
}