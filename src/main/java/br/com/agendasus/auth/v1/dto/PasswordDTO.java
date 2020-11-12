package br.com.agendasus.auth.v1.dto;

import br.com.agendasus.auth.v1.infrastructure.validators.NotNullAndNotEmpty;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class PasswordDTO implements Serializable {

    private static final long serialVersionUID = -1270861899515919128L;

    @NotNullAndNotEmpty(message = "crud.validation.required#{label.senha}")
    @JsonInclude(Include.NON_NULL)
    private String password;

    @JsonInclude(Include.NON_NULL)
    private String currentPassword;

}
