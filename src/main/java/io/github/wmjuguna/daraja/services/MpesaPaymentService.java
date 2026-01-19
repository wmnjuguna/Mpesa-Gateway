package io.github.wmjuguna.daraja.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.wmjuguna.daraja.dtos.ClaimSTKPayment;
import io.github.wmjuguna.daraja.dtos.MpesaExpressRequestDTO;
import io.github.wmjuguna.daraja.dtos.Responses.MpesaConfirmationOrValidationResponse;
import io.github.wmjuguna.daraja.dtos.Responses.MpesaExpressResponseDTO;
import io.github.wmjuguna.daraja.dtos.UpdatePay;
import io.github.wmjuguna.daraja.entities.MpesaPayment;
import io.github.wmjuguna.daraja.entities.MpesaValidationLog;
import io.github.wmjuguna.daraja.entities.PaybillConfig;
import io.github.wmjuguna.daraja.entities.StkLog;
import io.github.wmjuguna.daraja.repositories.MpesaPaymentRepository;
import io.github.wmjuguna.daraja.repositories.MpesaValidationLogRepository;
import io.github.wmjuguna.daraja.utils.MpesaActions;
import io.github.wmjuguna.daraja.utils.TransactionTypeEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class MpesaPaymentService implements MpesaPaymentOperations {
    private final MpesaPaymentRepository mpesaPaymentRepository;
    private final MpesaValidationLogRepository mpesaValidationLogRepository;
    private final StkLogService stkLogService;
    private final MpesaActions actions;
    private final PaybillConfigService paybillConfigService;
    private final ObjectMapper objectMapper;

    @Transactional
    @Override
    public StkLog requestPayment(ClaimSTKPayment stkPayment){
        String timeStamp = parseDate(LocalDateTime.now());
        PaybillConfig config = this.paybillConfigService.retrievePaybillConfiguration(stkPayment.paybill().toString(), "no");
        String password = stkPayment.paybill() + new String(Base64.getDecoder().decode(config.getPassKey())) + timeStamp;

        MpesaPayment payment = MpesaPayment.builder().build();

        MpesaExpressResponseDTO responseDTO = actions.lipaNaMpesaOnline(new MpesaExpressRequestDTO(
                TransactionTypeEnum.CustomerPaybillOnline.getTransactioType(),
                stkPayment.amount(),
                new String(Base64.getDecoder().decode(config.getStkCallbackUrl())),
                stkPayment.phoneNo(),
                stkPayment.phoneNo(),
                stkPayment.paybill(),
                (!Objects.isNull(stkPayment.paymentReference())) ? stkPayment.paymentReference() : stkPayment.phoneNo(),
                stkPayment.paybill() + " /REF " + stkPayment.phoneNo(),
                stkPayment.paybill(),
                timeStamp,
                Base64.getEncoder().encodeToString(password.getBytes(StandardCharsets.ISO_8859_1))
        ), new String(Base64.getDecoder().decode(config.getConsumerSecret())),
                new String(Base64.getDecoder().decode(config.getConsumerKey())));

        String responsePayload = toJsonPayload(responseDTO);

        return stkLogService.createLog(StkLog.builder()
                .mpesaPayment(payment)
                .requestPayload(responsePayload)
                .callbackUrl(stkPayment.callbackUrl())
                .build());
    }

    @Override
    public void recordConfirmationPayment(MpesaConfirmationOrValidationResponse confirmationOrValidationResponse, String rawPayload) {
        if (mpesaPaymentRepository.existsByTransId(confirmationOrValidationResponse.transID())) {
            return;
        }
        MpesaPayment payment = MpesaPayment.builder()
                .confirmationPayload(rawPayload)
                .build();
        actions.callBackWithConfirmationOrFailure(confirmationOrValidationResponse.billRefNumber(), confirmationOrValidationResponse.transAmount(),
                confirmationOrValidationResponse.transID(), null, 0);
        mpesaPaymentRepository.save(payment);
        log.info("Payment confirmed: TransactionID={}, Amount={}, Reference={}",
                confirmationOrValidationResponse.transID(), confirmationOrValidationResponse.transAmount(),
                confirmationOrValidationResponse.billRefNumber());
    }

    public List<io.github.wmjuguna.daraja.dtos.MpesaPayment> allPayments(){
        return mpesaPaymentRepository.findAll()
                .parallelStream()
                .map(payment -> summaryFromPayload(payment.getUuid(), payment.getConfirmationPayload()))
                .collect(Collectors.toList());
    }

    public void updatePay(List<UpdatePay> payList){
        List<MpesaPayment> mpesaPayments = new java.util.ArrayList<>();
        for (UpdatePay updatePay : payList) {
            MpesaPayment payment = mpesaPaymentRepository.findById(updatePay.id()).orElse(null);
            if (payment == null) {
                log.info("Payment Missing with ID {}", updatePay.id());
                continue;
            }
            payment.setConfirmationPayload(updatePayload(payment.getConfirmationPayload(), updatePay));
            mpesaPayments.add(payment);
        }
        mpesaPaymentRepository.saveAll(mpesaPayments);
    }

    public void recordValidationPayload(String rawPayload) {
        MpesaValidationLog validationLog = new MpesaValidationLog();
        validationLog.setValidationPayload(rawPayload);
        mpesaValidationLogRepository.save(validationLog);
    }

    private io.github.wmjuguna.daraja.dtos.MpesaPayment summaryFromPayload(String uuid, String payload) {
        if (payload == null || payload.isBlank()) {
            return new io.github.wmjuguna.daraja.dtos.MpesaPayment(uuid, null, 0.0, null, null);
        }
        try {
            Map<String, Object> data = objectMapper.readValue(payload, new TypeReference<>() {});
            String receiptNo = getString(data.get("TransID"));
            double amount = getDouble(data.get("TransAmount"));
            String accountNo = getString(data.get("BillRefNumber"));
            String tranTime = getString(data.get("TransTime"));
            return new io.github.wmjuguna.daraja.dtos.MpesaPayment(uuid, receiptNo, amount, accountNo, tranTime);
        } catch (IOException e) {
            log.info("Failed to parse confirmation payload for summary", e);
            return new io.github.wmjuguna.daraja.dtos.MpesaPayment(uuid, null, 0.0, null, null);
        }
    }

    private String updatePayload(String payload, UpdatePay updatePay) {
        Map<String, Object> data;
        try {
            data = payload == null || payload.isBlank()
                    ? new java.util.HashMap<>()
                    : objectMapper.readValue(payload, new TypeReference<>() {});
        } catch (IOException e) {
            data = new java.util.HashMap<>();
        }
        if (updatePay.customerName() != null) {
            data.put("FirstName", updatePay.customerName());
        }
        if (updatePay.receiptNo() != null) {
            data.put("TransID", updatePay.receiptNo());
        }
        return toJsonPayload(data);
    }

    private String toJsonPayload(Object payload) {
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (IOException e) {
            log.info("Failed to serialize payload", e);
            return null;
        }
    }

    private String getString(Object value) {
        return value == null ? null : value.toString();
    }

    private double getDouble(Object value) {
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

    private String parseDate(LocalDateTime date){
        ZoneId zoneId = ZoneId.of("Africa/Nairobi");
        ZonedDateTime zonedDateTime = date.atZone(zoneId);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss").withZone(zoneId);
        return zonedDateTime.format(formatter);
    }
}
