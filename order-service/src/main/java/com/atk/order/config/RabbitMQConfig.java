package com.atk.order.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    //Normal
    public static final String EXCHANGE = "order.exchange";
    public static final String QUEUE = "order.queue";
    public static final String ROUTING_KEY = "order.created";
    //Rollback
    public static final String ROLLBACK_EXCHANGE = "order.rollback.exchange";
    public static final String ROLLBACK_QUEUE = "order.rollback.queue";
    public static final String ROLLBACK_ROUTING_KEY = "order.callback";

    //Normal
    @Bean
    public TopicExchange exchange(){
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Queue queue(){
        return new Queue(QUEUE);
    }

    @Bean
    public Binding binding() {
        return BindingBuilder.bind(queue()).to(exchange()).with(ROUTING_KEY);
    }
    //Rollback
    @Bean
    public DirectExchange rollbackExchange() {
        return new DirectExchange(ROLLBACK_EXCHANGE);
    }

    @Bean
    public Queue rollbackQueue() {
        return new Queue(ROLLBACK_QUEUE, true);
    }

    @Bean
    public Binding rollbackBinding(Queue rollbackQueue, DirectExchange rollbackExchange) {
        return BindingBuilder
                .bind(rollbackQueue)
                .to(rollbackExchange)
                .with(ROLLBACK_ROUTING_KEY);
    }
}
