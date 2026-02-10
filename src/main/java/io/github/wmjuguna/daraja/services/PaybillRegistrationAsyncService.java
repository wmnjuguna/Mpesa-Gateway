package io.github.wmjuguna.daraja.services;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.wmjuguna.daraja.entities.PaybillConfig;
import io.github.wmjuguna.daraja.repositories.PaybillConfigRepository;
import io.github.wmjuguna.daraja.utils.MpesaActions;
import io.github.wmjuguna.daraja.utils.PaybillRegistrationStatus;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaybillRegistrationAsyncService {

    private final PaybillConfigRepository paybillConfigRepository;
    private final MpesaActions mpesaActions;

    @Async
    @Transactional
    public void submitToDaraja(String configUuid) {
        paybillConfigRepository.findByUuid(configUuid).ifPresentOrElse(
                this::submitRegistration,
                () -> log.error("Paybill registration skipped. Configuration not found for uuid={}", configUuid)
        );
    }

    private void submitRegistration(PaybillConfig paybillConfig) {
        try {
            mpesaActions.registerURl(
                    decodeIfBase64(paybillConfig.getConsumerSecret()),
                    decodeIfBase64(paybillConfig.getConsumerKey()),
                    decodeIfBase64(paybillConfig.getConfirmationUrl()),
                    decodeIfBase64(paybillConfig.getValidationUrl()),
                    paybillConfig.getPaybillNo(),
                    paybillConfig.getResponseType().getResponse()
            );
            paybillConfig.setRegistrationStatus(PaybillRegistrationStatus.SUCCESS);
            paybillConfig.setRegistrationFailureReason(null);
        } catch (Exception ex) {
            log.error("Daraja paybill registration failed for uuid={}", paybillConfig.getUuid(), ex);
            paybillConfig.setRegistrationStatus(PaybillRegistrationStatus.FAILED);
            paybillConfig.setRegistrationFailureReason(trimFailureReason(ex.getMessage()));
        }
        paybillConfigRepository.save(paybillConfig);
    }

    private String trimFailureReason(String reason) {
        if (reason == null || reason.isBlank()) {
            return "Registration failed without additional details.";
        }
        return reason.length() > 1000 ? reason.substring(0, 1000) : reason;
    }

    private String decodeIfBase64(String value) {
        if (value == null) {
            return null;
        }
        try {
            byte[] decoded = Base64.getDecoder().decode(value);
            String decodedValue = new String(decoded, StandardCharsets.UTF_8);
            String encodedAgain = Base64.getEncoder().encodeToString(decodedValue.getBytes(StandardCharsets.UTF_8));
            return encodedAgain.equals(value) ? decodedValue : value;
        } catch (IllegalArgumentException ex) {
            return value;
        }
    }
}
