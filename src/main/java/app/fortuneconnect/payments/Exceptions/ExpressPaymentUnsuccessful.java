package app.fortuneconnect.payments.Exceptions;

public class ExpressPaymentUnsuccessful extends RuntimeException{
    public ExpressPaymentUnsuccessful(String message){
        super(message);
    }
}
