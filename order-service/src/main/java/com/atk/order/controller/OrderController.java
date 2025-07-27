package com.atk.order.controller;

import com.atk.order.model.Order;
import com.atk.order.producer.OrderProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderProducer orderProducer;

    @PostMapping
    public ResponseEntity<String> placeOrder(@RequestBody Order order) {
        try {
            orderProducer.sendOrder(order);
            return ResponseEntity.ok("Order sent to RabbitMQ!");
        } catch (Exception e) {
            e.printStackTrace(); // console’a hata bas
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error while processing order: " + e.getMessage());
        }
    }
}