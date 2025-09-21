package io.github.wmjuguna.daraja.controllers;

import io.github.wmjuguna.daraja.dtos.ClaimSTKPayment;
import io.github.wmjuguna.daraja.dtos.ResponseTemplate;
import io.github.wmjuguna.daraja.dtos.Responses.MpesaConfirmationOrValidationResponse;
import io.github.wmjuguna.daraja.dtos.Responses.StkCallbackResponseDTO;
import io.github.wmjuguna.daraja.dtos.ValidationResponse;
import io.github.wmjuguna.daraja.entities.PaybillConfig;
import io.github.wmjuguna.daraja.services.PaybillConfigService;
import io.github.wmjuguna.daraja.services.MpesaPaymentService;
import io.github.wmjuguna.daraja.services.StkLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController @Slf4j
@RequestMapping("mobile")
@Tag(name = "M-Pesa Payment Gateway", description = "Comprehensive M-Pesa payment processing APIs for STK Push, callbacks, confirmations, and merchant configuration")
public class PaymentsResource {

    private final MpesaPaymentService mpesaPaymentService;
    private final PaybillConfigService paybillConfigService;
    private final StkLogService stkLogService;

    public PaymentsResource(MpesaPaymentService mpesaPaymentService, PaybillConfigService paybillConfigService,
                            StkLogService stkLogService) {
        this.paybillConfigService = paybillConfigService;
        this.mpesaPaymentService = mpesaPaymentService;
        this.stkLogService = stkLogService;
    }

    @PostMapping("request-payment")
    @Operation(
            summary = "Initiate STK Push Payment",
            description = "Initiates an STK Push payment request to the customer's mobile device. The customer will receive a prompt on their phone to enter their M-Pesa PIN to complete the payment.",
            tags = {"STK Push"}
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "STK Push request initiated successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ResponseTemplate.class),
                            examples = @ExampleObject(
                                    name = "Successful STK Push",
                                    value = """
                                            {
                                              "data": {
                                                "merchantRequestId": "29115-34620561-1",
                                                "checkoutRequestId": "ws_CO_191220191020363925",
                                                "responseCode": "0",
                                                "responseDescription": "Success. Request accepted for processing",
                                                "customerMessage": "Success. Request accepted for processing"
                                              },
                                              "message": null,
                                              "error": null
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request parameters",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "Validation Error",
                                    value = """
                                            {
                                              "data": null,
                                              "message": null,
                                              "error": "Invalid phone number format"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "Server Error",
                                    value = """
                                            {
                                              "data": null,
                                              "message": null,
                                              "error": "Failed to process payment request"
                                            }
                                            """
                            )
                    )
            )
    })
    public ResponseEntity<ResponseTemplate<?>> stkPushPayment(
            @Parameter(
                    description = "STK Push payment request details",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = ClaimSTKPayment.class),
                            examples = @ExampleObject(
                                    name = "Payment Request",
                                    value = """
                                            {
                                              "phoneNo": "254712345678",
                                              "amount": 100.0,
                                              "paybill": 174379,
                                              "paymentReference": "ORDER123",
                                              "callbackUrl": "https://yourdomain.com/callback"
                                            }
                                            """
                            )
                    )
            )
            @RequestBody ClaimSTKPayment payment) {
        return ResponseEntity.ok().body(
                new ResponseTemplate<>(mpesaPaymentService.requestPayment(payment), null, null)
        );
    }

    @PostMapping("stk")
    @Operation(
            summary = "STK Push Callback Handler",
            description = "Handles callback responses from M-Pesa after an STK Push payment attempt. This endpoint is called by Safaricom to notify about payment status.",
            tags = {"STK Push"}
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Callback processed successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid callback data",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            )
    })
    public ResponseEntity<ResponseTemplate<?>> stkCallback(
            @Parameter(
                    description = "STK Push callback response from M-Pesa",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = StkCallbackResponseDTO.class),
                            examples = @ExampleObject(
                                    name = "Successful Payment Callback",
                                    value = """
                                            {
                                              "Body": {
                                                "stkCallback": {
                                                  "MerchantRequestID": "29115-34620561-1",
                                                  "CheckoutRequestID": "ws_CO_191220191020363925",
                                                  "ResultCode": 0,
                                                  "ResultDesc": "The service request is processed successfully.",
                                                  "CallbackMetadata": {
                                                    "Item": [
                                                      {
                                                        "Name": "Amount",
                                                        "Value": 100.00
                                                      },
                                                      {
                                                        "Name": "MpesaReceiptNumber",
                                                        "Value": "NLJ7RT61SV"
                                                      },
                                                      {
                                                        "Name": "TransactionDate",
                                                        "Value": 20191219102115
                                                      },
                                                      {
                                                        "Name": "PhoneNumber",
                                                        "Value": 254712345678
                                                      }
                                                    ]
                                                  }
                                                }
                                              }
                                            }
                                            """
                            )
                    )
            )
            @RequestBody StkCallbackResponseDTO callback) {
        stkLogService.updateLog(callback);
        return ResponseEntity.ok().body(null);
    }

    @PostMapping("confirm/payment")
    @Operation(
            summary = "Payment Confirmation Handler",
            description = "Handles payment confirmation callbacks from M-Pesa. This endpoint is called by M-Pesa to confirm successful payments.",
            tags = {"Payment Processing"}
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Payment confirmation processed successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid confirmation data",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            )
    })
    public ResponseEntity<ResponseTemplate<?>> confirm(
            @Parameter(
                    description = "Payment confirmation data from M-Pesa",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = MpesaConfirmationOrValidationResponse.class),
                            examples = @ExampleObject(
                                    name = "Payment Confirmation",
                                    value = """
                                            {
                                              "TransactionType": "Pay Bill",
                                              "TransID": "NLJ7RT61SV",
                                              "TransTime": "20191219102115",
                                              "TransAmount": "100.00",
                                              "BusinessShortCode": "174379",
                                              "BillRefNumber": "ORDER123",
                                              "InvoiceNumber": "",
                                              "OrgAccountBalance": "49197.00",
                                              "ThirdPartyTransID": "",
                                              "MSISDN": "254712345678",
                                              "FirstName": "John",
                                              "MiddleName": "",
                                              "LastName": "Doe"
                                            }
                                            """
                            )
                    )
            )
            @RequestBody MpesaConfirmationOrValidationResponse confirmationOrValidationResponse) {
        mpesaPaymentService.recordConfirmationPayment(confirmationOrValidationResponse);
        return ResponseEntity.ok().body(null);
    }

    @PostMapping("validate/payment")
    @Operation(
            summary = "Payment Validation Handler",
            description = "Validates incoming payment requests from M-Pesa before processing. Returns validation response to M-Pesa.",
            tags = {"Payment Processing"}
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Payment validation completed",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ValidationResponse.class),
                            examples = @ExampleObject(
                                    name = "Successful Validation",
                                    value = """
                                            {
                                              "ResultDesc": "Accepted",
                                              "ResultCode": "0"
                                            }
                                            """
                            )
                    )
            )
    })
    public ResponseEntity<?> validate(
            @Parameter(
                    description = "Payment validation request from M-Pesa",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = MpesaConfirmationOrValidationResponse.class)
                    )
            )
            @RequestBody MpesaConfirmationOrValidationResponse confirmationOrValidationResponse) {
        return ResponseEntity.ok().body(new ValidationResponse( "Accepted", "0"));
    }

    @PostMapping("configure-paybill")
    @Operation(
            summary = "Create Paybill Configuration",
            description = "Creates a new merchant paybill configuration for M-Pesa integration. This stores merchant credentials and settings.",
            tags = {"Configuration Management"}
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Paybill configuration created successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ResponseTemplate.class),
                            examples = @ExampleObject(
                                    name = "Configuration Created",
                                    value = """
                                            {
                                              "data": {
                                                "id": 1,
                                                "uuid": "550e8400-e29b-41d4-a716-446655440000",
                                                "paybill": "174379",
                                                "passkey": "***encrypted***",
                                                "consumerKey": "***encrypted***",
                                                "consumerSecret": "***encrypted***",
                                                "confirmationUrl": "https://yourdomain.com/confirm",
                                                "validationUrl": "https://yourdomain.com/validate"
                                              },
                                              "message": "Configuration created successfully",
                                              "error": null
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid configuration data",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            )
    })
    public ResponseEntity<ResponseTemplate<?>> configurePaybill(
            @Parameter(
                    description = "Paybill configuration details",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = PaybillConfig.class),
                            examples = @ExampleObject(
                                    name = "Paybill Configuration",
                                    value = """
                                            {
                                              "paybill": "174379",
                                              "passkey": "your-passkey-here",
                                              "consumerKey": "your-consumer-key",
                                              "consumerSecret": "your-consumer-secret",
                                              "confirmationUrl": "https://yourdomain.com/mobile/confirm/payment",
                                              "validationUrl": "https://yourdomain.com/mobile/validate/payment"
                                            }
                                            """
                            )
                    )
            )
            @RequestBody PaybillConfig paybillConfig) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        new ResponseTemplate<>(paybillConfigService.createPaybillConfiguration(paybillConfig), "Configuration created successfully", null)
                );
    }

    @GetMapping("configure-paybill")
    @Operation(
            summary = "Get All Paybill Configurations",
            description = "Retrieves all merchant paybill configurations with pagination support. Sensitive data like secrets are masked in the response.",
            tags = {"Configuration Management"}
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Configurations retrieved successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ResponseTemplate.class),
                            examples = @ExampleObject(
                                    name = "Configurations List",
                                    value = """
                                            {
                                              "data": [
                                                {
                                                  "id": 1,
                                                  "uuid": "550e8400-e29b-41d4-a716-446655440000",
                                                  "paybill": "174379",
                                                  "passkey": "***masked***",
                                                  "consumerKey": "***masked***",
                                                  "consumerSecret": "***masked***",
                                                  "confirmationUrl": "https://yourdomain.com/confirm",
                                                  "validationUrl": "https://yourdomain.com/validate"
                                                }
                                              ],
                                              "message": "Data retrieved Successfully",
                                              "error": null
                                            }
                                            """
                            )
                    )
            )
    })
    public ResponseEntity<ResponseTemplate<?>> allConfigurations(
            @Parameter(
                    description = "Page number for pagination (0-based)",
                    example = "0"
            )
            @RequestParam(defaultValue = "0") int page,
            @Parameter(
                    description = "Number of items per page",
                    example = "20"
            )
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PaybillConfig> paybillConfigPage = paybillConfigService.getAll(pageable);
        return ResponseEntity.ok().body(
                new ResponseTemplate<>(paybillConfigPage.getContent(), "Data retrieved Successfully", null)
        );
    }

    @PutMapping("configure-paybill/{uid}")
    @Operation(
            summary = "Update Paybill Configuration",
            description = "Updates an existing merchant paybill configuration identified by UUID. All configuration fields can be updated.",
            tags = {"Configuration Management"}
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Configuration updated successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ResponseTemplate.class),
                            examples = @ExampleObject(
                                    name = "Updated Configuration",
                                    value = """
                                            {
                                              "data": {
                                                "id": 1,
                                                "uuid": "550e8400-e29b-41d4-a716-446655440000",
                                                "paybill": "174379",
                                                "passkey": "***encrypted***",
                                                "consumerKey": "***encrypted***",
                                                "consumerSecret": "***encrypted***",
                                                "confirmationUrl": "https://yourdomain.com/confirm",
                                                "validationUrl": "https://yourdomain.com/validate"
                                              },
                                              "message": "Configurations Updated Successfully",
                                              "error": null
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Configuration not found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            )
    })
    public ResponseEntity<ResponseTemplate<?>> updatePaybillConfiguration(
            @Parameter(
                    description = "UUID of the configuration to update",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440000"
            )
            @PathVariable String uid,
            @Parameter(
                    description = "Updated paybill configuration data",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = PaybillConfig.class)
                    )
            )
            @RequestBody PaybillConfig paybillConfig) {
        return ResponseEntity.status(HttpStatus.OK.value()).body(
                new ResponseTemplate<>(paybillConfigService.update(paybillConfig), "Configurations Updated Successfully", null)
        );
    }

    @GetMapping("payments/all")
    @Operation(
            summary = "Get All Payments",
            description = "Retrieves all payment transactions processed through the system. Includes both successful and failed transactions.",
            tags = {"Payment Reports"}
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Payments retrieved successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ResponseTemplate.class),
                            examples = @ExampleObject(
                                    name = "Payments List",
                                    value = """
                                            {
                                              "data": [
                                                {
                                                  "id": 1,
                                                  "uuid": "550e8400-e29b-41d4-a716-446655440001",
                                                  "phoneNumber": "254712345678",
                                                  "amount": 100.0,
                                                  "paymentReference": "ORDER123",
                                                  "mpesaReceiptNumber": "NLJ7RT61SV",
                                                  "transactionDate": "2023-12-19T10:21:15",
                                                  "resultCode": "0",
                                                  "resultDescription": "The service request is processed successfully.",
                                                  "checkoutRequestId": "ws_CO_191220191020363925",
                                                  "merchantRequestId": "29115-34620561-1"
                                                }
                                              ],
                                              "message": "Payments Retrieved Successfully",
                                              "error": null
                                            }
                                            """
                            )
                    )
            )
    })
    public ResponseEntity<ResponseTemplate<?>> allPayments(){
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseTemplate<>(mpesaPaymentService.allPayments(), "Payments Retrieved Successfully", null)
        );
    }
}
