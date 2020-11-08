package br.com.agendasus.auth.v1.domain.usecase.exceptions;

public class AuthTokenException extends RuntimeException {

    private Exception rootException;


    public AuthTokenException(String message) {
        super(message);
    }

    public AuthTokenException(Exception e, String message) {
        super(message);
        this.rootException = e;
    }


    public Exception getRootException() {
        return rootException;
    }

}