package br.com.agendasus.auth.v1.dto;

import br.com.agendasus.auth.v1.domain.model.UserLogin;
import br.com.agendasus.auth.v1.infrastructure.validators.NotNullAndNotEmpty;
import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import java.io.Serializable;

@Getter
@Setter
public class AccountDTO implements Serializable {

    private static final long serialVersionUID = -3890909285163759053L;

    @NotNullAndNotEmpty(message = "crud.validation.required#{label.name}")
    @JsonInclude(Include.NON_NULL)
    private String name;

    @NotNullAndNotEmpty(message = "crud.validation.required#{label.login}")
    @JsonInclude(Include.NON_NULL)
    private String login;

    public AccountDTO() {
        super();
    }

    public AccountDTO(UserLogin user) {
        setAttributes(user);
    }


    public void parse(UserLogin user) {
        user.setName(name);
        user.setLogin(login);
    }

    public void setAttributes(UserLogin user) {
        this.name = user.getName();
        this.login = user.getLogin();
    }

}
