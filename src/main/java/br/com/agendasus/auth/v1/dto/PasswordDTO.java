package br.com.agendasus.auth.v1.dto;

import br.com.agendasus.auth.v1.infrastructure.validators.NotNullAndNotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class PasswordDTO implements Serializable {

    private static final long serialVersionUID = -1270861899515919128L;

    @NotNullAndNotEmpty(message = "crud.validation.required#{label.senha}")
    private String password;

    private String currentPassword;

}
