package app.fortuneconnect.payments.Exceptions;

public class AuthenticationFailed extends RuntimeException{
    public AuthenticationFailed(){
        super("Authentication Failed");
    }
}
