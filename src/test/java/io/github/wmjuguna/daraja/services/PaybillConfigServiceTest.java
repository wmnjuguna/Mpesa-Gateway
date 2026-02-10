package io.github.wmjuguna.daraja.services;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.github.wmjuguna.daraja.entities.PaybillConfig;
import io.github.wmjuguna.daraja.repositories.PaybillConfigRepository;
import io.github.wmjuguna.daraja.utils.MpesaResponseType;
import io.github.wmjuguna.daraja.utils.PaybillRegistrationStatus;

@ExtendWith(MockitoExtension.class)
class PaybillConfigServiceTest {

    @Mock
    private PaybillConfigRepository paybillConfigRepository;

    @Mock
    private PaybillRegistrationAsyncService paybillRegistrationAsyncService;

    @InjectMocks
    private PaybillConfigService paybillConfigService;

    @Test
    void createPaybillConfigurationStoresRecordFirstThenQueuesAsyncSubmission() {
        PaybillConfig payload = sampleConfig();
        when(paybillConfigRepository.save(any(PaybillConfig.class))).thenAnswer(invocation -> {
            PaybillConfig saved = invocation.getArgument(0);
            saved.setUuid("cfg-uuid-1");
            return saved;
        });

        PaybillConfig created = paybillConfigService.createPaybillConfiguration(payload);

        ArgumentCaptor<PaybillConfig> savedCaptor = ArgumentCaptor.forClass(PaybillConfig.class);
        verify(paybillConfigRepository).save(savedCaptor.capture());
        PaybillConfig savedConfig = savedCaptor.getValue();
        assertEquals(PaybillRegistrationStatus.PENDING, savedConfig.getRegistrationStatus());
        assertNull(savedConfig.getRegistrationFailureReason());
        assertNotEquals("consumer-key", savedConfig.getConsumerKey());
        assertEquals("consumer-key", new String(Base64.getDecoder().decode(savedConfig.getConsumerKey()), StandardCharsets.UTF_8));
        verify(paybillRegistrationAsyncService).submitToDaraja(eq("cfg-uuid-1"));
        assertEquals(PaybillRegistrationStatus.PENDING, created.getRegistrationStatus());
    }

    @Test
    void retryFailedSubmissionQueuesAsyncSubmission() {
        PaybillConfig existing = sampleConfig();
        existing.setUuid("cfg-uuid-2");
        existing.setRegistrationStatus(PaybillRegistrationStatus.FAILED);
        existing.setRegistrationFailureReason("Authentication failed");

        when(paybillConfigRepository.findByUuid("cfg-uuid-2")).thenReturn(Optional.of(existing));
        when(paybillConfigRepository.save(any(PaybillConfig.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PaybillConfig retried = paybillConfigService.retryFailedSubmission("cfg-uuid-2");

        assertEquals(PaybillRegistrationStatus.PENDING, retried.getRegistrationStatus());
        assertNull(retried.getRegistrationFailureReason());
        verify(paybillRegistrationAsyncService).submitToDaraja("cfg-uuid-2");
    }

    @Test
    void retryFailedSubmissionRejectsNonFailedConfigurations() {
        PaybillConfig existing = sampleConfig();
        existing.setUuid("cfg-uuid-3");
        existing.setRegistrationStatus(PaybillRegistrationStatus.SUCCESS);

        when(paybillConfigRepository.findByUuid("cfg-uuid-3")).thenReturn(Optional.of(existing));

        assertThrows(IllegalStateException.class, () -> paybillConfigService.retryFailedSubmission("cfg-uuid-3"));
        verify(paybillConfigRepository, never()).save(any(PaybillConfig.class));
        verify(paybillRegistrationAsyncService, never()).submitToDaraja(any(String.class));
    }

    private PaybillConfig sampleConfig() {
        PaybillConfig paybillConfig = new PaybillConfig();
        paybillConfig.setPaybillNo(174379);
        paybillConfig.setOrganisationName("Example Ltd");
        paybillConfig.setConsumerSecret("consumer-secret");
        paybillConfig.setConsumerKey("consumer-key");
        paybillConfig.setPassKey("pass-key");
        paybillConfig.setConfirmationUrl("https://example.com/mobile/confirm/payment");
        paybillConfig.setValidationUrl("https://example.com/mobile/validate/payment");
        paybillConfig.setStkCallbackUrl("https://example.com/mobile/stk");
        paybillConfig.setResponseType(MpesaResponseType.COMPLETED);
        return paybillConfig;
    }
}
