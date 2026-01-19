package io.github.wmjuguna.daraja.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.wmjuguna.daraja.dtos.Responses.StkCallbackResponseDTO;
import io.github.wmjuguna.daraja.entities.StkLog;
import io.github.wmjuguna.daraja.repositories.StkLogOperations;
import io.github.wmjuguna.daraja.repositories.StkLogRepository;
import io.github.wmjuguna.daraja.utils.MpesaActions;
import io.github.wmjuguna.daraja.utils.StringToDateConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

@Service
@Transactional
@Slf4j
public class StkLogService implements StkLogOperations {

    private final StkLogRepository stkLogRepository;
    private final MpesaActions actions;
    private final ObjectMapper objectMapper;

    public StkLogService(StkLogRepository stkLogRepository, MpesaActions mpesaActions, ObjectMapper objectMapper) {
        this.stkLogRepository = stkLogRepository;
        this.actions = mpesaActions;
        this.objectMapper = objectMapper;
    }

    @Override
    public StkLog createLog(StkLog log) {
        return stkLogRepository.save(log);
    }

    @Override
    public StkLog retriveLog(String uid) {
        return stkLogRepository.findByUuid(uid);
    }

    @Override
    public StkLog updateLog(StkCallbackResponseDTO callback, String rawPayload) {
        String merchantRequestId = callback.body().stkCallback().merchantRequestID();
        StkLog stkLog = retriveByMerchantId(merchantRequestId);
        stkLog.setCallbackPayload(rawPayload);
        Integer resultCode = callback.body().stkCallback().resultCode();
        if (resultCode != null && resultCode == 0 && stkLog.getCallbackUrl() != null) {
            ParsedCallbackData parsedData = parseCallbackData(rawPayload);
            actions.callBackWithConfirmationOrFailure(
                    parsedData.paymentReference,
                    parsedData.amount,
                    parsedData.receiptNo,
                    stkLog.getCallbackUrl(),
                    resultCode
            );
        }
        return stkLogRepository.save(stkLog);
    }

    @Override
    public StkLog retriveByMerchantId(String merchantRequestId) {
        return stkLogRepository.findByMerchantRequestId(merchantRequestId);
    }

    private ParsedCallbackData parseCallbackData(String rawPayload) {
        if (rawPayload == null || rawPayload.isBlank()) {
            return ParsedCallbackData.empty();
        }
        try {
            Map<String, Object> payload = objectMapper.readValue(rawPayload, new TypeReference<>() {});
            Object body = payload.get("Body");
            if (!(body instanceof Map<?, ?> bodyMap)) {
                return ParsedCallbackData.empty();
            }
            Object stkCallback = bodyMap.get("stkCallback");
            if (!(stkCallback instanceof Map<?, ?> stkCallbackMap)) {
                return ParsedCallbackData.empty();
            }
            Object callbackMetadata = stkCallbackMap.get("CallbackMetadata");
            if (!(callbackMetadata instanceof Map<?, ?> callbackMetadataMap)) {
                return ParsedCallbackData.empty();
            }
            Object items = callbackMetadataMap.get("Item");
            if (!(items instanceof List<?> itemList)) {
                return ParsedCallbackData.empty();
            }
            double amount = 0.0;
            String receiptNo = null;
            String phoneNumber = null;
            String transactionDate = null;
            for (Object item : itemList) {
                if (!(item instanceof Map<?, ?> itemMap)) {
                    continue;
                }
                Object name = itemMap.get("Name");
                Object value = itemMap.get("Value");
                if (!(name instanceof String itemName)) {
                    continue;
                }
                switch (itemName) {
                    case "Amount" -> amount = parseDouble(value);
                    case "MpesaReceiptNumber" -> receiptNo = value != null ? value.toString() : null;
                    case "PhoneNumber" -> phoneNumber = value != null ? value.toString() : null;
                    case "TransactionDate" -> transactionDate = value != null ? value.toString() : null;
                    default -> {
                    }
                }
            }
            if (transactionDate != null) {
                try {
                    StringToDateConverter.parse(transactionDate);
                } catch (ParseException e) {
                    log.info("Failed to parse transaction date {}", transactionDate);
                }
            }
            return new ParsedCallbackData(phoneNumber, amount, receiptNo);
        } catch (IOException e) {
            log.info("Failed to parse STK callback payload", e);
            return ParsedCallbackData.empty();
        }
    }

    private double parseDouble(Object value) {
        if (value instanceof Number number) {
            return number.doubleValue();
        }
        if (value == null) {
            return 0.0;
        }
        try {
            return Double.parseDouble(value.toString());
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    private record ParsedCallbackData(String paymentReference, double amount, String receiptNo) {
        private static ParsedCallbackData empty() {
            return new ParsedCallbackData(null, 0.0, null);
        }
    }
}
