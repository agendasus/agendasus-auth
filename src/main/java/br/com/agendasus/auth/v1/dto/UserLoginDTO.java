package br.com.agendasus.auth.v1.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@Getter
@Setter
public class UserLoginDTO {

    @NotNull(message = "crud.validation.required#{label.login}")
    @NotEmpty(message = "crud.validation.required#{label.login}")
    @JsonInclude(Include.NON_NULL)
    private String username;

    @NotNull(message = "crud.validation.required#{label.login}")
    @NotEmpty(message = "crud.validation.required#{label.login}")
    @JsonInclude(Include.NON_NULL)
    private String password;

}
