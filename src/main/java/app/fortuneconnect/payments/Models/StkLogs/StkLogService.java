package app.fortuneconnect.payments.Models.StkLogs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StkLogService implements StkLogOperations{
    @Autowired
    private StkLogRepository repository;

    @Override
    public StkLog createLog(StkLog log) {
        return repository.save(log);
    }

    @Override
    public StkLog retriveLog(String uid) {
        return repository.findByStkLogUid(uid);
    }

    @Override
    public StkLog updateLog(StkLog log) {
        return repository.save(log);
    }

    @Override
    public StkLog retriveByMerchantId(String merchantRequestId) {
        return repository.findByMerchantRequestID(merchantRequestId);
    }
}
