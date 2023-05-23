package app.fortuneconnect.payments.Models.StkLogs;

import app.fortuneconnect.payments.DTO.Responses.StkCallbackResponseDTO;
import app.fortuneconnect.payments.Utils.Const.MpesaStaticStrings;
import app.fortuneconnect.payments.Utils.MpesaActions;
import app.fortuneconnect.payments.Utils.StringToDateConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;

@Service @Transactional @Slf4j
public class StkLogService implements StkLogOperations{

    private  final StkLogRepository stkLogRepository;
    private final MpesaActions actions;

    public StkLogService(StkLogRepository stkLogRepository, MpesaActions mpesaActions){
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
        log.info("Updating {}", callback);
        StkLog stkLog = retriveByMerchantId(callback.getBody().getStkCallback().getMerchantRequestID());
        stkLog.setResultCode(callback.getBody().getStkCallback().getResultCode());
        log.info("Stk Log {} and  call back {} Result = {}", stkLog.getResultCode(), callback, callback.getBody().getStkCallback().getResultCode());
        if(callback.getBody().getStkCallback().getResultCode() == 0){
            log.info("I hae that I am fooling around");
            callback.getBody().getStkCallback().getCallbackMetadata().getItem().forEach(
                    item -> {
                        switch (item.getName()){
                            case MpesaStaticStrings.MPESA_RECEIPT_NO -> stkLog.getMpesaPayment().setMpesaTransactionNo(item.getValue().toString());
                            case MpesaStaticStrings.AMOUNT -> stkLog.getMpesaPayment().setTransactionAmount((Double) item.getValue());
                            case MpesaStaticStrings.TRANSACTION_DATE -> {
                                try {
                                    stkLog.getMpesaPayment().setTransactionTime(StringToDateConverter.parse(item.getValue().toString()));
                                } catch (ParseException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                            case MpesaStaticStrings.BALANCE -> {}
                            default -> stkLog.getMpesaPayment().setPhoneNumber(item.getValue().toString());
                        }
                    }
            );
            stkLog.getMpesaPayment().setTransactionStatus(true);
        }
        log.info("CallbackUrl {}",stkLog.getCallbackUrl());
        actions.callBackWithConfirmationOrFailure(stkLog.getMpesaPayment().getAccountNo(),stkLog.getMpesaPayment().getTransactionAmount(), stkLog.getMpesaPayment().getMpesaTransactionNo(),
                stkLog.getCallbackUrl(), stkLog.getResultCode());
        return stkLogRepository.save(stkLog);
    }

    @Override
    public StkLog retriveByMerchantId(String merchantRequestId) {
        return stkLogRepository.findByMerchantRequestID(merchantRequestId);
    }
}
