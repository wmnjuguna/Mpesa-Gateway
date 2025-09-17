package io.github.wmjuguna.daraja.exceptions;

public class AuthenticationFailed extends RuntimeException{
    public AuthenticationFailed(){
        super("Authentication Failed");
    }
}
