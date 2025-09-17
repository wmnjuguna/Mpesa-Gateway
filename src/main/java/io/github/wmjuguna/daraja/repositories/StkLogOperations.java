package io.github.wmjuguna.daraja.repositories;


import io.github.wmjuguna.daraja.dtos.Responses.StkCallbackResponseDTO;
import io.github.wmjuguna.daraja.entities.StkLog;
import org.springframework.transaction.annotation.Transactional;

public interface StkLogOperations {
    @Transactional
    StkLog createLog(StkLog log);
    StkLog retriveLog(String uid);

    @Transactional
    StkLog updateLog(StkCallbackResponseDTO callback);

    StkLog retriveByMerchantId(String merchantRequestId);
}
