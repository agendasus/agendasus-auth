package br.com.agendasus.auth.v1.infrastructure.response;

import lombok.Getter;

import java.io.Serializable;

@Getter
public class Error implements Serializable {

    private static final long serialVersionUID = -4575177619208148961L;

    private String input;
    private String message;

    public Error(String input, String message) {
        this.input = input;
        this.message = message;
    }

}
