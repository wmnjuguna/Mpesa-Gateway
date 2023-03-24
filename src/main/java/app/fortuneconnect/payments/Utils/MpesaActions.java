package app.fortuneconnect.payments.Utils;

import app.fortuneconnect.payments.DTO.MpesaExpressRequestDTO;
import app.fortuneconnect.payments.DTO.Responses.AuthorizationResponse;
import app.fortuneconnect.payments.DTO.Responses.MpesaExpressResponseDTO;
import app.fortuneconnect.payments.Utils.Const.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.Objects;

@Component
@Slf4j
public class MpesaActions {
    @Autowired
    private RestTemplate template;

    public AuthorizationResponse authenticate(String consumerSecret, String consumerKey) {
        String appKeySecret = consumerKey + ":" + consumerSecret;
        byte[] bytes = appKeySecret.getBytes(StandardCharsets.ISO_8859_1);
        String encoded = Base64.getEncoder().encodeToString(bytes);
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(consumerSecret, consumerKey);
        headers.set("Authorization", "Basic " + encoded);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<AuthorizationResponse> response = template.exchange(Constants.sandboxAuthenticationUrl, HttpMethod.GET, requestEntity, AuthorizationResponse.class);
        if(response.getStatusCode().isError()){
            throw new RuntimeException("Could not authenticate with M-PESA");
        }
        return response.getBody();
    }

    public MpesaExpressResponseDTO lipaNaMpesaOnline(MpesaExpressRequestDTO request, String consumerSecret, String consumerKey){
        AuthorizationResponse response = authenticate(consumerSecret, consumerKey);
        if (Objects.isNull(response)) {
            log.error("M-PESA Authorization could not be done");
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + response.getAccessToken());
        HttpEntity<MpesaExpressRequestDTO> requestEntity = new HttpEntity<>(request, headers);
        ResponseEntity<MpesaExpressResponseDTO> responseEntity = template.exchange(Constants.sandboxMpesaExpress, HttpMethod.POST, requestEntity, MpesaExpressResponseDTO.class);
        return responseEntity.getBody();
    }

    public void bulkDisbursement(){
        
    }
}
