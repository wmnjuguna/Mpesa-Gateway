package app.fortuneconnect.payments.Utils.Const;

public class Constants {
    public static final String authenticationUrl = "https://api.safaricom.co.ke/oauth/v1/generate?grant_type=client_credentials";
    public static final String sandboxAuthenticationUrl = "https://sandbox.safaricom.co.ke/oauth/v1/generate?grant_type=client_credentials";
    public static final String c2bSimulationUrl = "https://sandbox.safaricom.co.ke/mpesa/c2b/v2/simulate";
    public static final String c2bUrlRegistrationUrl = "https://api.safaricom.co.ke/mpesa/c2b/v2/registerurl";
    public static final String B2C_TEST_INITIATOR_USERNAME = "GMUKOYA";
    public static final String B2C_TEST_INITIATOR_PASSWD = "sudoFortune#1";
    public static final String b2cSimulationUrl = "https://sandbox.safaricom.co.ke/mpesa/b2c/v1/paymentrequest";
    public static final String connectUrl = "https://api.connect.fortunecredit.co.ke/";
    public static final String username = "DARAJA";
    public static final String password = "mpesa@FortuneIntegration22";
    public static final String sandboxMpesaExpress = "https://sandbox.safaricom.co.ke/mpesa/stkpush/v1/processrequest";
}
