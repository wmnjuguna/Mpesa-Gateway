package io.github.wmjuguna.daraja.integrations;

import io.github.wmjuguna.daraja.dtos.MpesaExpressRequestDTO;
import io.github.wmjuguna.daraja.dtos.Responses.AuthorizationResponse;
import io.github.wmjuguna.daraja.dtos.Responses.MpesaExpressResponseDTO;
import io.github.wmjuguna.daraja.dtos.Responses.URLRegistrationResponseDTO;
import io.github.wmjuguna.daraja.dtos.URLRegistrationRequestDTO;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

@HttpExchange(accept = MediaType.APPLICATION_JSON_VALUE)
public interface DarajaApiClient {

    @GetExchange("/oauth/v1/generate?grant_type=client_credentials")
    AuthorizationResponse authenticate(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader
    );

    @PostExchange(
            url = "/mpesa/stkpush/v1/processrequest",
            contentType = MediaType.APPLICATION_JSON_VALUE
    )
    MpesaExpressResponseDTO lipaNaMpesaOnline(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
            @RequestBody MpesaExpressRequestDTO request
    );

    @PostExchange(
            url = "/mpesa/c2b/v1/registerurl",
            contentType = MediaType.APPLICATION_JSON_VALUE
    )
    URLRegistrationResponseDTO registerUrl(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
            @RequestBody URLRegistrationRequestDTO request
    );
}
