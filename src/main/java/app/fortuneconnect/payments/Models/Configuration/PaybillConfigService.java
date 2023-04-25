package app.fortuneconnect.payments.Models.Configuration;

import app.fortuneconnect.payments.Exceptions.ResourceNotFoundException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.awt.print.Pageable;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Service
public class PaybillConfigService {

    private final PaybillConfigRepository paybillConfigRepository;

    public PaybillConfigService(PaybillConfigRepository paybillConfigRepository){
        this.paybillConfigRepository = paybillConfigRepository;
    }

    public PaybillConfig createPaybillConfiguration(PaybillConfig paybillConfig){
        paybillConfig.setPaybillUid(UUID.randomUUID().toString());
        paybillConfig.setConsumerKey(Base64.getEncoder().encodeToString(paybillConfig.getConsumerKey().getBytes(StandardCharsets.UTF_8)));
        paybillConfig.setConsumerSecret(Base64.getEncoder().encodeToString(paybillConfig.getConsumerSecret().getBytes(StandardCharsets.UTF_8)));
        paybillConfig.setPassKey(Base64.getEncoder().encodeToString(paybillConfig.getPassKey().getBytes(StandardCharsets.UTF_8)));
        paybillConfig.setConfirmationUrl(Base64.getEncoder().encodeToString(paybillConfig.getConfirmationUrl().getBytes(StandardCharsets.UTF_8)));
        paybillConfig.setValidationUrl(Base64.getEncoder().encodeToString(paybillConfig.getValidationUrl().getBytes(StandardCharsets.UTF_8)));
        paybillConfig.setStkCallbackUrl(Base64.getEncoder().encodeToString(paybillConfig.getStkCallbackUrl().getBytes(StandardCharsets.UTF_8)));
        return paybillConfigRepository.save(paybillConfig);
    }

    public PaybillConfig retrievePaybillConfiguration(String paybillNO){
        return this.paybillConfigRepository.findByPaybillNo(paybillNO)
                .orElseThrow(() -> new ResourceNotFoundException("Paybill Could not be found"));
    }

    public List<PaybillConfig> allConfigurations(){
        return this.paybillConfigRepository.findAll();
    }

    public List<PaybillConfig> allConfigurations(Pageable pageable){
        return this.paybillConfigRepository.findAll((Sort) pageable);
    }
}
