package com.project.greatcloud13.ClimbingWith.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.retry.RepublishMessageRecoverer;
import org.springframework.amqp.support.converter.DefaultJackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.interceptor.RetryOperationsInterceptor;


@Configuration
public class RabbitMQConfig {

    public static final String QUEUE_NAME = "post.embedding.queue";
    public static final String EXCHANGE_NAME = "post.embedding.exchange";
    public static final String ROUTING_KEY = "post.embedding.key";

    // 재시도 소진 시 격리시킬 Dead Letter 큐
    public static final String DLX_EXCHANGE_NAME = "post.embedding.dlx";
    public static final String DLQ_NAME = "post.embedding.dlq";
    public static final String DLQ_ROUTING_KEY = "post.embedding.dlq.key";

    //  영속성 true
    @Bean
    Queue queue() {return new Queue(QUEUE_NAME, true);}

    @Bean
    DirectExchange exchange() {return new DirectExchange(EXCHANGE_NAME);}

    @Bean
    Binding binding(Queue queue, DirectExchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY);
    }

    @Bean
    Queue deadLetterQueue() { return new Queue(DLQ_NAME, true); }

    @Bean
    DirectExchange deadLetterExchange() { return new DirectExchange(DLX_EXCHANGE_NAME); }

    @Bean
    Binding deadLetterBinding(Queue deadLetterQueue, DirectExchange deadLetterExchange) {
        return BindingBuilder.bind(deadLetterQueue).to(deadLetterExchange).with(DLQ_ROUTING_KEY);
    }

    // 단일 MessageConverter 빈으로 등록해두면 Spring Boot가 자동구성한 RabbitTemplate(발행 쪽)에도
    // 동일하게 적용되어, 발행/구독 양쪽이 같은 JSON 포맷을 쓰게 됨
    @Bean
    public MessageConverter jsonMessageConverter() {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
        DefaultJackson2JavaTypeMapper typeMapper = new DefaultJackson2JavaTypeMapper();
        typeMapper.setTrustedPackages("com.project.greatcloud13.ClimbingWith.dto");
        converter.setJavaTypeMapper(typeMapper);
        return converter;
    }

    @Bean
    public RabbitListenerContainerFactory<?> rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory, RabbitTemplate rabbitTemplate, MessageConverter jsonMessageConverter) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jsonMessageConverter);

        // 컨버팅/처리 실패 시 최대 3회까지만 재시도, 그 이후엔 DLQ로 격리 (무한 재적재 방지)
        factory.setAdviceChain(retryInterceptor(rabbitTemplate));

        return factory;
    }

    private RetryOperationsInterceptor retryInterceptor(RabbitTemplate rabbitTemplate) {
        return RetryInterceptorBuilder.stateless()
                .maxAttempts(3)
                .backOffOptions(1000, 2.0, 10000) // 1s -> 2s -> 4s ... 최대 10s
                .recoverer(new RepublishMessageRecoverer(rabbitTemplate, DLX_EXCHANGE_NAME, DLQ_ROUTING_KEY))
                .build();
    }
}