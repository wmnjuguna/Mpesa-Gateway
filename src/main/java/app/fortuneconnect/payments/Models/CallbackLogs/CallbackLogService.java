package app.fortuneconnect.payments.Models.CallbackLogs;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CallbackLogService {

    private final CallbackLogRepository callbackLogRepository;

    public CallbackLogService(CallbackLogRepository callbackLogRepository){
        this.callbackLogRepository = callbackLogRepository;
    }

    public CallbackLog createCallBackLog(CallbackLog callbackLog){
        callbackLog.setLogUid(UUID.randomUUID().toString());
        return callbackLogRepository.save(callbackLog);
    }

    public List<CallbackLog> callbackLogList(){
        return callbackLogRepository.findAll();
    }
}
