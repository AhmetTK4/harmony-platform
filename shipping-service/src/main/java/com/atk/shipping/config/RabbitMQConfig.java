package com.atk.shipping.config;


import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String SHIPPING_EXCHANGE = "shipping.exchange";
    public static final String SHIPPING_QUEUE = "order.shipping.queue";
    public static final String SHIPPING_ROUTING_KEY = "order.shipping";

    @Bean
    public Queue shippingQueue() {
        return new Queue(SHIPPING_QUEUE, true);
    }

    @Bean
    public org.springframework.amqp.core.TopicExchange shippingExchange() {
        return new org.springframework.amqp.core.TopicExchange(SHIPPING_EXCHANGE);
    }

    @Bean
    public org.springframework.amqp.core.Binding shippingBinding() {
        return org.springframework.amqp.core.BindingBuilder
                .bind(shippingQueue())
                .to(shippingExchange())
                .with(SHIPPING_ROUTING_KEY);
    }
}