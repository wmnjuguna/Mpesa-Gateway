package app.fortuneconnect.payments.Utils;

import app.fortuneconnect.payments.DTO.MpesaExpressRequestDTO;
import app.fortuneconnect.payments.DTO.PaymentConfirmationRequest;
import app.fortuneconnect.payments.DTO.ResponseTemplate;
import app.fortuneconnect.payments.DTO.Responses.AuthorizationResponse;
import app.fortuneconnect.payments.DTO.Responses.MpesaExpressResponseDTO;
import app.fortuneconnect.payments.DTO.Responses.URLRegistrationResponseDTO;
import app.fortuneconnect.payments.DTO.URLRegistrationRequestDTO;
import app.fortuneconnect.payments.Exceptions.AuthenticationFailed;
import app.fortuneconnect.payments.Exceptions.StkPushFailed;
import app.fortuneconnect.payments.Utils.Const.MpesaStaticStrings;
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
        headers.setBearerAuth(response.getAccessToken());
        HttpEntity<MpesaExpressRequestDTO> requestEntity = new HttpEntity<>(request, headers);
        ResponseEntity<MpesaExpressResponseDTO> responseEntity = template.exchange(mpesaExpressUrl, HttpMethod.POST, requestEntity, MpesaExpressResponseDTO.class);
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
        headers.setBearerAuth(response.getAccessToken());
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

    public void callBackWithConfirmationOrFailure( String paymentReference, double amount, String receiptNo, String callbackUrl, int resultCode){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        if(Objects.isNull(callbackUrl)) return;
        if(resultCode == 0){
            ResponseTemplate<PaymentConfirmationRequest> request = ResponseTemplate.<PaymentConfirmationRequest>builder()
                    .data(PaymentConfirmationRequest.builder()
                            .billReference(paymentReference)
                            .paymentMethod("MPESA")
                            .amountReceived(amount)
                            .receiptNo(receiptNo)
                            .build())
                    .message(MpesaStaticStrings.PAYMENT_SUCCESSFUL)
                    .build();

            HttpEntity<ResponseTemplate<PaymentConfirmationRequest>> requestHttpEntity = new HttpEntity<>(request, headers);
            template.exchange(callbackUrl, HttpMethod.POST, requestHttpEntity, Void.class);
        }
        else {
            ResponseTemplate<?> request = ResponseTemplate.builder().error(MpesaStaticStrings.PAYMENT_UNSUCCESSFUL).build();
            HttpEntity<ResponseTemplate<?>> requestHttpEntity = new HttpEntity<>(request, headers);
            template.exchange(callbackUrl, HttpMethod.POST, requestHttpEntity, Void.class);
        }
    }

    public void bulkDisbursement(){
        
    }
}
