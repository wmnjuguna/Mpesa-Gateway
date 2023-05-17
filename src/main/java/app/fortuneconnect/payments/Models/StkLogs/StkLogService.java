package app.fortuneconnect.payments.Models.StkLogs;

import app.fortuneconnect.payments.DTO.Responses.StkCallbackResponseBody;
import app.fortuneconnect.payments.Utils.Const.MpesaStaticStrings;
import app.fortuneconnect.payments.Utils.StringToDateConverter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;

@Service
public class StkLogService implements StkLogOperations{

    private  final StkLogRepository stkLogRepository;

    public StkLogService(StkLogRepository stkLogRepository){
        this.stkLogRepository = stkLogRepository;
    }

    @Override
    public StkLog createLog(StkLog log) {
        return stkLogRepository.save(log);
    }

    @Override
    public StkLog retriveLog(String uid) {
        return stkLogRepository.findByStkLogUid(uid);
    }

    @Transactional
    @Override
    public StkLog updateLog(StkCallbackResponseBody callback) {
        StkLog log = retriveByMerchantId(callback.getStkCallback().getMerchantRequestID());
        if(callback.getStkCallback().getResultCode() == 0){
            callback.getStkCallback().getCallbackMetadata().getItem().forEach(
                    item -> {
                        switch (item.getName()){
                            case MpesaStaticStrings.MPESA_RECEIPT_NO -> log.getMpesaPayment().setMpesaTransactionNo((String) item.getValue());
                            case MpesaStaticStrings.AMOUNT -> log.getMpesaPayment().setTransactionAmount((Double) item.getValue());
                            case MpesaStaticStrings.TRANSACTION_DATE -> {
                                try {
                                    log.getMpesaPayment().setTransactionTime(StringToDateConverter.parse((String) item.getValue()));
                                } catch (ParseException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                            case MpesaStaticStrings.BALANCE -> {}
                            default -> log.getMpesaPayment().setPhoneNumber((String) item.getValue());
                        }
                    }
            );
            log.getMpesaPayment().setTransactionStatus(true);
        }
        return stkLogRepository.save(log);
    }

    @Override
    public StkLog retriveByMerchantId(String merchantRequestId) {
        return stkLogRepository.findByMerchantRequestID(merchantRequestId);
    }
}
