package app.fortuneconnect.payments.config.rabbit.queues;

import app.fortuneconnect.payments.config.rabbit.RabbitMQConfiguration;
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PaymentsQueus {
    public static final String PAYMENTS_QUEUE = "payments.queue";
    public static final String PAYMENTS_ROUTING_KEY = "payments";
    public static final String PAYMENTS_DEAD_LETTER_QUEUE = "payments-dead-letter.queue";
    public static final String PAYMENTS_DEAD_LETTER_ROUTING_KEY = "payments-dead-letter";

    @Bean
    Queue paymentsQueue() {
        return QueueBuilder.durable(PAYMENTS_QUEUE).withArgument("x-dead-letter-exchange", RabbitMQConfiguration.DEAD_LETTER_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", PAYMENTS_DEAD_LETTER_ROUTING_KEY).build();
    }

    @Bean
    Binding paymentsBinding(@Qualifier("paymentsQueue") Queue queue, @Qualifier("exchange") DirectExchange directExchange) {
        return BindingBuilder.bind(queue).to(directExchange).with(PAYMENTS_ROUTING_KEY);
    }

    @Bean
    Queue paymentsDeadLetterQueue(){
        return QueueBuilder.durable(PAYMENTS_DEAD_LETTER_QUEUE).build();
    }

    @Bean
    Binding paymentsDeadLetterQueueBinding(@Qualifier("paymentsDeadLetterQueue") Queue queue, @Qualifier("deadLetterExchange") DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange)
                .with(PAYMENTS_DEAD_LETTER_ROUTING_KEY);
    }
}
