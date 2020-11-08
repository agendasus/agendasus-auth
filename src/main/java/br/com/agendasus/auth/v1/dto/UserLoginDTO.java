package br.com.agendasus.auth.v1.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class UserLoginDTO {

    @NotNull(message = "crud.validation.required#{label.login}")
    @NotEmpty(message = "crud.validation.required#{label.login}")
    private String username;

    @NotNull(message = "crud.validation.required#{label.login}")
    @NotEmpty(message = "crud.validation.required#{label.login}")
    private String password;

}
