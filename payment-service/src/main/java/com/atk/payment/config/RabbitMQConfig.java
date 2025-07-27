package com.atk.payment.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {


    @Bean
    public Queue orderQueue(){
        return new Queue("order.queue");
    }


    public static final String PAYMENT_EXCHANGE = "payment.exchange";
    public static final String PAYMENT_ROUTING_KEY = "payment.completed";

    @Bean
    public TopicExchange paymentExchange(){
        return new TopicExchange(PAYMENT_EXCHANGE);
    }

    @Bean
    public Queue paymentQueue(){
        return new Queue("payment.queue");
    }

    @Bean
    public Binding paymentBinding(){
        return BindingBuilder
                .bind(paymentQueue())
                .to(paymentExchange())
                .with(PAYMENT_ROUTING_KEY);
    }

    public static final String SHIPPING_EXCHANGE = "shipping.exchange";
    public static final String SHIPPING_ROUTING_KEY = "order.shipping";

    @Bean
    public TopicExchange shippingExchange() {
        return new TopicExchange(SHIPPING_EXCHANGE);
    }


    public static final String ROLLBACK_EXCHANGE = "order.rollback.exchange";
    public static final String ROLLBACK_ROUTING_KEY = "order.rollback";

    @Bean
    public DirectExchange rollbackExchange() {
        return new DirectExchange(ROLLBACK_EXCHANGE);
    }

}