package app.fortuneconnect.payments.Utils;

import app.fortuneconnect.payments.DTO.MpesaExpressRequestDTO;
import app.fortuneconnect.payments.DTO.Responses.AuthorizationResponse;
import app.fortuneconnect.payments.DTO.Responses.MpesaExpressResponseDTO;
import app.fortuneconnect.payments.DTO.URLRegistrationRequestDTO;
import app.fortuneconnect.payments.DTO.URLRegistrationResponseDTO;
import app.fortuneconnect.payments.Exceptions.AuthenticationFailed;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
@Slf4j
public class MpesaActions {
    @Autowired
    private RestTemplate template;

    @Value("${payments.mpesa.stk-push-url}")
    private String mpesaExpressUrl;

    @Value("${payments.mpesa.authentication-url}")
    private String authenticationUrl;

    @Value("${payments.mpesa.url-registration}")
    private String urlRegistrationUrl;


    public AuthorizationResponse authenticate(String consumerSecret, String consumerKey) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(consumerKey, consumerSecret);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<AuthorizationResponse> response = template.exchange(authenticationUrl, HttpMethod.GET, requestEntity, AuthorizationResponse.class);
        if(!response.getStatusCode().is2xxSuccessful()){
            throw new AuthenticationFailed();
        }
        return response.getBody();
    }

    public MpesaExpressResponseDTO lipaNaMpesaOnline(MpesaExpressRequestDTO request, String consumerSecret, String consumerKey){
        AuthorizationResponse response = authenticate(consumerSecret, consumerKey);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + response.getAccessToken());
        HttpEntity<MpesaExpressRequestDTO> requestEntity = new HttpEntity<>(request, headers);
        ResponseEntity<MpesaExpressResponseDTO> responseEntity = template.exchange(mpesaExpressUrl, HttpMethod.POST, requestEntity, MpesaExpressResponseDTO.class);
        if(!responseEntity.getStatusCode().is2xxSuccessful()){
            throw new AuthenticationFailed();
        }
        return responseEntity.getBody();
    }

    public void registerURl(@NonNull String consumerSecret, @NonNull String consumerKey, @NonNull String confirmationUrl,
                            @NonNull String validationUrl, int shortCode, @NonNull String responseType){
        AuthorizationResponse response =  authenticate(consumerSecret, consumerKey);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + response.getAccessToken());
        URLRegistrationRequestDTO request = URLRegistrationRequestDTO.builder()
                .confirmationURL(confirmationUrl)
                .validationURL(validationUrl)
                .shortCode(String.valueOf(shortCode))
                .responseType(responseType)
                .build();
        HttpEntity<URLRegistrationRequestDTO> requestEntity = new HttpEntity<>(request, headers);
        ResponseEntity<URLRegistrationResponseDTO> responseEntity = template.exchange(urlRegistrationUrl, HttpMethod.POST, requestEntity, URLRegistrationResponseDTO.class);
        responseEntity.getBody();
    }

    public void bulkDisbursement(){
        
    }
}
