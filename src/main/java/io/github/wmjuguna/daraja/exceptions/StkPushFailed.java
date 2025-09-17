package io.github.wmjuguna.daraja.exceptions;

public class StkPushFailed extends RuntimeException{
    public StkPushFailed(){
        super("Failed to trigger Stk Push");
    }
}
