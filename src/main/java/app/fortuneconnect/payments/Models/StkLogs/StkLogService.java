package app.fortuneconnect.payments.Models.StkLogs;

import app.fortuneconnect.payments.DTO.Responses.StkCallbackResponseDTO;
import app.fortuneconnect.payments.Utils.Const.MpesaStaticStrings;
import app.fortuneconnect.payments.Utils.MpesaActions;
import app.fortuneconnect.payments.Utils.StringToDateConverter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;

@Service @Transactional
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
        StkLog log = retriveByMerchantId(callback.getBody().getStkCallback().getMerchantRequestID());
        log.setResultCode(callback.getBody().getStkCallback().getResultCode());
        if(callback.getBody().getStkCallback().getResultCode() == 0){
            callback.getBody().getStkCallback().getCallbackMetadata().getItem().forEach(
                    item -> {
                        switch (item.getName()){
                            case MpesaStaticStrings.MPESA_RECEIPT_NO -> log.getMpesaPayment().setMpesaTransactionNo(item.getValue().toString());
                            case MpesaStaticStrings.AMOUNT -> log.getMpesaPayment().setTransactionAmount((Double) item.getValue());
                            case MpesaStaticStrings.TRANSACTION_DATE -> {
                                try {
                                    log.getMpesaPayment().setTransactionTime(StringToDateConverter.parse(item.getValue().toString()));
                                } catch (ParseException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                            case MpesaStaticStrings.BALANCE -> {}
                            default -> log.getMpesaPayment().setPhoneNumber(item.getValue().toString());
                        }
                    }
            );
            log.getMpesaPayment().setTransactionStatus(true);
        }
        actions.callBackWithConfirmationOrFailure(log.getMpesaPayment().getAccountNo(),log.getMpesaPayment().getTransactionAmount(), log.getMpesaPayment().getMpesaTransactionNo(),
                log.getCallbackUrl(), log.getResultCode());
        return stkLogRepository.save(log);
    }

    @Override
    public StkLog retriveByMerchantId(String merchantRequestId) {
        return stkLogRepository.findByMerchantRequestID(merchantRequestId);
    }
}
