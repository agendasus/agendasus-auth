package br.com.agendasus.auth.v1.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordRecoveryDTO {

    @JsonInclude(Include.NON_NULL) private String hash;
    @JsonInclude(Include.NON_NULL) private String password;

}
