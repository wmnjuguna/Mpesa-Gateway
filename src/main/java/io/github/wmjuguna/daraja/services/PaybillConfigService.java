package io.github.wmjuguna.daraja.services;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import io.github.wmjuguna.daraja.entities.PaybillConfig;
import io.github.wmjuguna.daraja.exceptions.ResourceNotFoundException;
import io.github.wmjuguna.daraja.repositories.PaybillConfigRepository;
import io.github.wmjuguna.daraja.utils.PaybillRegistrationStatus;

@Service
@RequiredArgsConstructor
public class PaybillConfigService {

    private final PaybillConfigRepository paybillConfigRepository;
    private final PaybillRegistrationAsyncService paybillRegistrationAsyncService;

    public PaybillConfig createPaybillConfiguration(PaybillConfig paybillConfig) {
        encodeSensitiveFields(paybillConfig);
        paybillConfig.setRegistrationStatus(PaybillRegistrationStatus.PENDING);
        paybillConfig.setRegistrationFailureReason(null);
        PaybillConfig saved = paybillConfigRepository.save(paybillConfig);
        paybillRegistrationAsyncService.submitToDaraja(saved.getUuid());
        return saved;
    }

    public PaybillConfig retrievePaybillConfiguration(String uuid, String finder) {
        if (finder.equalsIgnoreCase("uuid")) {
            return this.paybillConfigRepository.findByUuid(uuid)
                    .orElseThrow(() -> new ResourceNotFoundException("Paybill Could not be found"));
        } else {
            return this.paybillConfigRepository.findByPaybillNo(Integer.valueOf(uuid))
                    .orElseThrow(() -> new ResourceNotFoundException("Paybill Could not be found"));
        }
    }

    public PaybillConfig update(PaybillConfig paybillConfig) {
        return this.paybillConfigRepository.findByUuid(paybillConfig.getUuid())
                .map(paybillConfig1 -> {
                    paybillConfig1.setConfirmationUrl(Base64.getEncoder().encodeToString(paybillConfig.getConfirmationUrl().getBytes(StandardCharsets.UTF_8)));
                    paybillConfig1.setValidationUrl(Base64.getEncoder().encodeToString(paybillConfig.getValidationUrl().getBytes(StandardCharsets.UTF_8)));
                    paybillConfig1.setStkCallbackUrl(Base64.getEncoder().encodeToString(paybillConfig.getStkCallbackUrl().getBytes(StandardCharsets.UTF_8)));
                    paybillConfig1.setRegistrationStatus(PaybillRegistrationStatus.PENDING);
                    paybillConfig1.setRegistrationFailureReason(null);
                    PaybillConfig saved = this.paybillConfigRepository.save(paybillConfig1);
                    paybillRegistrationAsyncService.submitToDaraja(saved.getUuid());
                    return saved;
                })
                .orElseThrow(() -> new ResourceNotFoundException("Paybill could not be found"));
    }

    public PaybillConfig retryFailedSubmission(String uuid) {
        PaybillConfig paybillConfig = this.paybillConfigRepository.findByUuid(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("Paybill could not be found"));
        if (paybillConfig.getRegistrationStatus() != PaybillRegistrationStatus.FAILED) {
            throw new IllegalStateException("Only failed configurations can be retriggered.");
        }
        paybillConfig.setRegistrationStatus(PaybillRegistrationStatus.PENDING);
        paybillConfig.setRegistrationFailureReason(null);
        PaybillConfig saved = this.paybillConfigRepository.save(paybillConfig);
        paybillRegistrationAsyncService.submitToDaraja(saved.getUuid());
        return saved;
    }

    private void encodeSensitiveFields(PaybillConfig paybillConfig) {
        paybillConfig.setConsumerKey(Base64.getEncoder().encodeToString(paybillConfig.getConsumerKey()
                .getBytes(StandardCharsets.UTF_8)));
        paybillConfig.setConsumerSecret(Base64.getEncoder().encodeToString(paybillConfig
                .getConsumerSecret().getBytes(StandardCharsets.UTF_8)));
        paybillConfig.setPassKey(Base64.getEncoder().encodeToString(paybillConfig.getPassKey().getBytes(StandardCharsets.UTF_8)));
        paybillConfig.setConfirmationUrl(Base64.getEncoder().encodeToString(paybillConfig.getConfirmationUrl().getBytes(StandardCharsets.UTF_8)));
        paybillConfig.setValidationUrl(Base64.getEncoder().encodeToString(paybillConfig.getValidationUrl().getBytes(StandardCharsets.UTF_8)));
        paybillConfig.setStkCallbackUrl(Base64.getEncoder().encodeToString(paybillConfig.getStkCallbackUrl().getBytes(StandardCharsets.UTF_8)));
    }


    public List<PaybillConfig> getAll() {
        return this.paybillConfigRepository.findAll();
    }

    public Page<PaybillConfig> getAll(Pageable pageable) {
        return this.paybillConfigRepository.findAll(pageable);
    }
}
