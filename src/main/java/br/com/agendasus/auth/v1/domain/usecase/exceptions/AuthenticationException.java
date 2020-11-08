package br.com.agendasus.auth.v1.domain.usecase.exceptions;

import lombok.Getter;

@Getter
public class AuthenticationException extends RuntimeException {

    private Boolean returnGenericMessage;

    public AuthenticationException(String message, Boolean returnGenericMessage) {
        super(message);
        this.returnGenericMessage = returnGenericMessage;
    }

}