package com.project.greatcloud13.ClimbingWith.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;


@Configuration
public class RabbitMQConfig {

    public static final String QUEUE_NAME = "post.embedding.queue";
    public static final String EXCHANGE_NAME ="post.embedding.exchange";
    public static final String ROUTING_KEY = "post.embedding.key";

//  영속성 true
    @Bean
    Queue queue() {return new Queue(QUEUE_NAME, true);}

//
    @Bean
    DirectExchange exchange() {return new DirectExchange(EXCHANGE_NAME);}

    @Bean
    Binding binding(Queue queue, DirectExchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY);
    }

    @Bean
    public RabbitListenerContainerFactory<?> rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);

        // 메시지 컨버터 설정
        SimpleMessageConverter converter = new SimpleMessageConverter();
        // DTO 패키지를 신뢰 리스트에 추가
        converter.setAllowedListPatterns(List.of("com.project.greatcloud13.ClimbingWith.dto.*", "java.util.*"));

        factory.setMessageConverter(converter);
        return factory;
    }
}
