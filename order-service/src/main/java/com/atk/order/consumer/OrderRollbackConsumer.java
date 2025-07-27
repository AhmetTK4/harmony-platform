package com.atk.order.consumer;

import com.atk.order.model.OrderCreatedEvent;
import lombok.extern.slf4j.Slf4j;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OrderRollbackConsumer {

    @RabbitListener(queues = "order.rollback.queue")
    public void handleRollback(OrderCreatedEvent event){
        log.warn("Rollback for Order Received: {}", event);
        // TODO: Burada DB'den ilgili order'ı iptal etme, statü güncelleme gibi işlemler yapılabilir.
        log.info("Rollback complated for Order ID {}", event.orderId());
    }
}
