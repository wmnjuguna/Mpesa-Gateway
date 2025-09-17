package io.github.wmjuguna.daraja.services;

import io.github.wmjuguna.daraja.dtos.Responses.StkCallbackResponseDTO;
import io.github.wmjuguna.daraja.entities.StkLog;
import io.github.wmjuguna.daraja.repositories.StkLogOperations;
import io.github.wmjuguna.daraja.repositories.StkLogRepository;
import io.github.wmjuguna.daraja.utils.Const.MpesaStaticStrings;
import io.github.wmjuguna.daraja.utils.MpesaActions;
import io.github.wmjuguna.daraja.utils.StringToDateConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;

@Service
@Transactional
@Slf4j
public class StkLogService implements StkLogOperations {

    private final StkLogRepository stkLogRepository;
    private final MpesaActions actions;

    public StkLogService(StkLogRepository stkLogRepository, MpesaActions mpesaActions) {
        this.stkLogRepository = stkLogRepository;
        this.actions = mpesaActions;
    }

    @Override
    public StkLog createLog(StkLog log) {
        return stkLogRepository.save(log);
    }

    @Override
    public StkLog retriveLog(String uid) {
        return stkLogRepository.findByStkLogUid(uid);
    }

    @Override
    public StkLog updateLog(StkCallbackResponseDTO callback) {
        StkLog stkLog = retriveByMerchantId(callback.body().stkCallback().merchantRequestID());
        stkLog.setResultCode(callback.body().stkCallback().resultCode());
        if (callback.body().stkCallback().resultCode() == 0) {
            callback.body().stkCallback().callbackMetadata().item().forEach(
                    item -> {
                        switch (item.name()) {
                            case MpesaStaticStrings.MPESA_RECEIPT_NO ->
                                    stkLog.getMpesaPayment().setMpesaTransactionNo(item.value().toString());
                            case MpesaStaticStrings.AMOUNT ->
                                    stkLog.getMpesaPayment().setTransactionAmount((Double) item.value());
                            case MpesaStaticStrings.TRANSACTION_DATE -> {
                                try {
                                    stkLog.getMpesaPayment().setTransactionTime(StringToDateConverter.parse(item.value().toString()));
                                } catch (ParseException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                            case MpesaStaticStrings.BALANCE -> {
                            }
                            default -> stkLog.getMpesaPayment().setPhoneNumber(item.value().toString());
                        }
                    }
            );
            stkLog.getMpesaPayment().setTransactionStatus(true);
        }
        ;
        actions.callBackWithConfirmationOrFailure(stkLog.getMpesaPayment().getAccountNo(),
                stkLog.getMpesaPayment().getTransactionAmount(),
                stkLog.getMpesaPayment().getMpesaTransactionNo(),
                stkLog.getCallbackUrl(), stkLog.getResultCode());
        return stkLogRepository.save(stkLog);
    }

    @Override
    public StkLog retriveByMerchantId(String merchantRequestId) {
        return stkLogRepository.findByMerchantRequestID(merchantRequestId);
    }
}
