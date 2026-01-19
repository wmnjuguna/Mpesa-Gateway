package io.github.wmjuguna.daraja.utils;

import io.github.wmjuguna.daraja.dtos.MpesaExpressRequestDTO;
import io.github.wmjuguna.daraja.dtos.PaymentConfirmationRequest;
import io.github.wmjuguna.daraja.dtos.ResponseTemplate;
import io.github.wmjuguna.daraja.dtos.Responses.AuthorizationResponse;
import io.github.wmjuguna.daraja.dtos.Responses.MpesaExpressResponseDTO;
import io.github.wmjuguna.daraja.dtos.Responses.URLRegistrationResponseDTO;
import io.github.wmjuguna.daraja.dtos.URLRegistrationRequestDTO;
import io.github.wmjuguna.daraja.exceptions.AuthenticationFailed;
import io.github.wmjuguna.daraja.exceptions.StkPushFailed;
import io.github.wmjuguna.daraja.utils.MpesaStaticStrings;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Objects;

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
        headers.setBearerAuth(response.accessToken());
        HttpEntity<MpesaExpressRequestDTO> requestEntity = new HttpEntity<>(request, headers);
        ResponseEntity<MpesaExpressResponseDTO> responseEntity = template.exchange(mpesaExpressUrl, HttpMethod.POST,
                requestEntity, MpesaExpressResponseDTO.class);
        if(!responseEntity.getStatusCode().is2xxSuccessful()){
            throw new StkPushFailed();
        }
        return responseEntity.getBody();
    }

    public void registerURl(@NonNull String consumerSecret, @NonNull String consumerKey, @NonNull String confirmationUrl,
                            @NonNull String validationUrl, int shortCode, @NonNull String responseType){
        AuthorizationResponse response =  authenticate(consumerSecret, consumerKey);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(response.accessToken());
        URLRegistrationRequestDTO request = new URLRegistrationRequestDTO(
                confirmationUrl,
                responseType,
                String.valueOf(shortCode),
                validationUrl
        );
        HttpEntity<URLRegistrationRequestDTO> requestEntity = new HttpEntity<>(request, headers);
        ResponseEntity<URLRegistrationResponseDTO> responseEntity = template.exchange(urlRegistrationUrl, HttpMethod.POST,
                requestEntity, URLRegistrationResponseDTO.class);
        responseEntity.getBody();
    }

    public void callBackWithConfirmationOrFailure( String paymentReference, double amount, String receiptNo, String callbackUrl, int resultCode){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        if(Objects.isNull(callbackUrl)) return;
        if(resultCode == 0){
            ResponseTemplate<PaymentConfirmationRequest> request = new ResponseTemplate<>(
                    new PaymentConfirmationRequest(
                            paymentReference,
                            receiptNo,
                            amount,
                            "MPESA"
                    ),
                    MpesaStaticStrings.PAYMENT_SUCCESSFUL,
                    null
            );

            HttpEntity<ResponseTemplate<PaymentConfirmationRequest>> requestHttpEntity = new HttpEntity<>(request, headers);
            template.exchange(callbackUrl, HttpMethod.POST, requestHttpEntity, Void.class);
        }
        else {
            ResponseTemplate<?> request = new ResponseTemplate<>(null, null, MpesaStaticStrings.PAYMENT_UNSUCCESSFUL);
            HttpEntity<ResponseTemplate<?>> requestHttpEntity = new HttpEntity<>(request, headers);
            template.exchange(callbackUrl, HttpMethod.POST, requestHttpEntity, Void.class);
        }
    }

    public void bulkDisbursement(){
        
    }
}
