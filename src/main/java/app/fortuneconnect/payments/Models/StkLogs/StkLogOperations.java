package app.fortuneconnect.payments.Models.StkLogs;


public interface StkLogOperations {
    StkLog createLog(StkLog log);
    StkLog retriveLog(String uid);
    StkLog updateLog(StkLog log);
    StkLog retriveByMerchantId(String merchantRequestId);
}
