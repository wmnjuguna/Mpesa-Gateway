package io.github.wmjuguna.daraja.utils;

import io.github.wmjuguna.daraja.dtos.MpesaExpressRequestDTO;
import io.github.wmjuguna.daraja.dtos.PaymentConfirmationRequest;
import io.github.wmjuguna.daraja.dtos.ResponseTemplate;
import io.github.wmjuguna.daraja.dtos.Responses.AuthorizationResponse;
import io.github.wmjuguna.daraja.dtos.Responses.MpesaExpressResponseDTO;
import io.github.wmjuguna.daraja.dtos.URLRegistrationRequestDTO;
import io.github.wmjuguna.daraja.exceptions.AuthenticationFailed;
import io.github.wmjuguna.daraja.exceptions.StkPushFailed;
import io.github.wmjuguna.daraja.integrations.DarajaApiClient;
import lombok.NonNull;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;

@Component
public class MpesaActions {
    private final DarajaApiClient darajaApiClient;
    private final RestClient restClient;

    public MpesaActions(DarajaApiClient darajaApiClient, RestClient.Builder restClientBuilder) {
        this.darajaApiClient = darajaApiClient;
        this.restClient = restClientBuilder.build();
    }


    public AuthorizationResponse authenticate(String consumerSecret, String consumerKey) {
        try {
            AuthorizationResponse response = darajaApiClient.authenticate(
                    basicAuthorizationHeader(consumerKey, consumerSecret)
            );
            if (Objects.isNull(response) || Objects.isNull(response.accessToken())) {
                throw new AuthenticationFailed();
            }
            return response;
        } catch (RestClientException ex) {
            throw new AuthenticationFailed();
        }
    }

    public MpesaExpressResponseDTO lipaNaMpesaOnline(MpesaExpressRequestDTO request, String consumerSecret, String consumerKey){
        AuthorizationResponse response = authenticate(consumerSecret, consumerKey);
        try {
            MpesaExpressResponseDTO paymentResponse = darajaApiClient.lipaNaMpesaOnline(
                    bearerAuthorizationHeader(response.accessToken()),
                    request
            );
            if (Objects.isNull(paymentResponse)) {
                throw new StkPushFailed();
            }
            return paymentResponse;
        } catch (RestClientException ex) {
            throw new StkPushFailed();
        }
    }

    public void registerURl(@NonNull String consumerSecret, @NonNull String consumerKey, @NonNull String confirmationUrl,
                            @NonNull String validationUrl, int shortCode, @NonNull String responseType){
        AuthorizationResponse response =  authenticate(consumerSecret, consumerKey);
        URLRegistrationRequestDTO request = new URLRegistrationRequestDTO(
                confirmationUrl,
                responseType,
                String.valueOf(shortCode),
                validationUrl
        );
        darajaApiClient.registerUrl(
                bearerAuthorizationHeader(response.accessToken()),
                request
        );
    }

    public void callBackWithConfirmationOrFailure( String paymentReference, double amount, String receiptNo, String callbackUrl, int resultCode){
        if(Objects.isNull(callbackUrl)) return;
        if(resultCode == 0){
            ResponseTemplate<PaymentConfirmationRequest> requestPayload = new ResponseTemplate<>(
                    new PaymentConfirmationRequest(
                            paymentReference,
                            receiptNo,
                            amount,
                            "MPESA"
                    ),
                    MpesaStaticStrings.PAYMENT_SUCCESSFUL,
                    null
            );
            restClient.post()
                    .uri(callbackUrl)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .body(requestPayload)
                    .retrieve()
                    .toBodilessEntity();
        }
        else {
            ResponseTemplate<?> requestPayload = new ResponseTemplate<>(null, null, MpesaStaticStrings.PAYMENT_UNSUCCESSFUL);
            restClient.post()
                    .uri(callbackUrl)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .body(requestPayload)
                    .retrieve()
                    .toBodilessEntity();
        }
    }

    public void bulkDisbursement(){
        
    }

    private String basicAuthorizationHeader(String consumerKey, String consumerSecret) {
        String credentials = consumerKey + ":" + consumerSecret;
        String encodedCredentials = Base64.getEncoder()
                .encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
        return "Basic " + encodedCredentials;
    }

    private String bearerAuthorizationHeader(String accessToken) {
        return "Bearer " + accessToken;
    }
}
