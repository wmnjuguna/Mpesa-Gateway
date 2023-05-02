package app.fortuneconnect.payments.Models.Configuration;

import app.fortuneconnect.payments.Exceptions.ResourceNotFoundException;
import app.fortuneconnect.payments.Utils.MpesaActions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Service
public class PaybillConfigService {

    private final PaybillConfigRepository paybillConfigRepository;
    private final MpesaActions mpesaActions;

    public PaybillConfigService(PaybillConfigRepository paybillConfigRepository, MpesaActions mpesaActions){
        this.paybillConfigRepository = paybillConfigRepository;
        this.mpesaActions = mpesaActions;
    }

    @Transactional
    public PaybillConfig createPaybillConfiguration(PaybillConfig paybillConfig){
        this.mpesaActions.registerURl(paybillConfig.getConsumerSecret(), paybillConfig.getConsumerKey(), paybillConfig.getConfirmationUrl(), paybillConfig.getValidationUrl(), paybillConfig.getPaybillNo(), "Complete");
        paybillConfig.setPaybillUid(UUID.randomUUID().toString());
        paybillConfig.setConsumerKey(Base64.getEncoder().encodeToString(paybillConfig.getConsumerKey().getBytes(StandardCharsets.UTF_8)));
        paybillConfig.setConsumerSecret(Base64.getEncoder().encodeToString(paybillConfig.getConsumerSecret().getBytes(StandardCharsets.UTF_8)));
        paybillConfig.setPassKey(Base64.getEncoder().encodeToString(paybillConfig.getPassKey().getBytes(StandardCharsets.UTF_8)));
        paybillConfig.setConfirmationUrl(Base64.getEncoder().encodeToString(paybillConfig.getConfirmationUrl().getBytes(StandardCharsets.UTF_8)));
        paybillConfig.setValidationUrl(Base64.getEncoder().encodeToString(paybillConfig.getValidationUrl().getBytes(StandardCharsets.UTF_8)));
        paybillConfig.setStkCallbackUrl(Base64.getEncoder().encodeToString(paybillConfig.getStkCallbackUrl().getBytes(StandardCharsets.UTF_8)));
        return paybillConfigRepository.save(paybillConfig);
    }

    public PaybillConfig retrievePaybillConfiguration(String uid, String finder){
        if(finder.equalsIgnoreCase("uid")){
           return this.paybillConfigRepository.findByPaybillUid(uid)
                .orElseThrow(() -> new ResourceNotFoundException("Paybill Could not be found"));
        }else{
            return this.paybillConfigRepository.findByPaybillNo(uid)
                .orElseThrow(() -> new ResourceNotFoundException("Paybill Could not be found"));
        }
    }

    public PaybillConfig update(PaybillConfig paybillConfig){
        return this.paybillConfigRepository.findByPaybillUid(paybillConfig.getPaybillUid())
                .map(paybillConfig1 -> {
                    paybillConfig1.setConfirmationUrl(paybillConfig.getConfirmationUrl());
                    paybillConfig1.setValidationUrl(paybillConfig.getValidationUrl());
                    paybillConfig1.setStkCallbackUrl(paybillConfig.getStkCallbackUrl());
                    return this.paybillConfigRepository.save(paybillConfig1); // save the updated object
                })
                .orElseThrow(() -> new ResourceNotFoundException("Paybill could not be found"));
    }


    public List<PaybillConfig> getAll(){
        return this.paybillConfigRepository.findAll();
    }

    public Page<PaybillConfig> getAll(Pageable pageable){
        return this.paybillConfigRepository.findAll(pageable);
    }
}
