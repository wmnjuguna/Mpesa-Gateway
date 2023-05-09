package app.fortuneconnect.payments.Exceptions;

public class StkPushFailed extends RuntimeException{
    public StkPushFailed(){
        super("Failed to trigger Stk Push");
    }
}
