package br.com.agendasus.auth.v1.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordRecoveryDTO {

    private String hash;
    private String password;

}
