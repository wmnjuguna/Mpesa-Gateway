package app.fortuneconnect.payments.kafka.service;

import app.fortuneconnect.payments.DTO.Responses.PaymentCompletionResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.transaction.KafkaTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor @Slf4j
public class PaymentsProducerService {
    private final ObjectMapper objectMapper;
    private final KafkaTransactionManager<String, Object> kafkaTransactionManager;
    private static final String TOPIC = "daraja-incoming-payments-queue";

    private final KafkaTemplate<String, Object> kafkaMessageTemplate;

    @Transactional
    public void sendMessage(PaymentCompletionResponse completionResponse) {
        try{
            String json = objectMapper.writeValueAsString(completionResponse);
            kafkaMessageTemplate.send(TOPIC, json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
