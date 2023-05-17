package app.fortuneconnect.payments.Models.StkLogs;


import app.fortuneconnect.payments.DTO.Responses.StkCallbackResponseBody;
import org.springframework.transaction.annotation.Transactional;

public interface StkLogOperations {
    @Transactional
    StkLog createLog(StkLog log);
    StkLog retriveLog(String uid);

    @Transactional
    StkLog updateLog(StkCallbackResponseBody callback);

    StkLog retriveByMerchantId(String merchantRequestId);
}
