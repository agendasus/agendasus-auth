package br.com.agendasus.auth.v1.dto;

import br.com.agendasus.auth.v1.domain.model.UserLogin;
import br.com.agendasus.auth.v1.infrastructure.enumeration.UserType;
import br.com.agendasus.auth.v1.infrastructure.validators.NotNullAndNotEmpty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Email;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@Getter
@Setter
public class UserDTO {

    private Long id;

    @NotNullAndNotEmpty(message = "crud.validation.required#{label.name}")
    @JsonInclude(Include.NON_NULL)
    private String name;

    @NotNullAndNotEmpty(message = "crud.validation.required#{label.login}")
    @Email(message = "error.generic.field.dto#{label.login}")
    @JsonInclude(Include.NON_NULL)
    private String login;

    @JsonInclude(Include.NON_NULL)
    private String password;

    @JsonInclude(Include.NON_NULL)
    private String userType;

    @JsonInclude(Include.NON_NULL)
    private String permissions;


    public UserDTO() {
        super();
    }

    public UserDTO(UserLogin user) {
        setAttributes(user);
    }


    public UserLogin parse(UserLogin user) {
        user.setName(this.name);
        user.setLogin(this.login);
        user.setPassword(this.password);
        user.setUserType(this.userType != null ? UserType.valueOf(this.userType) : null);
        return user;
    }

    public void setAttributes(UserLogin user) {
        this.id = user.getId();
        this.name = user.getName();
        this.login = user.getLogin();
        this.userType = user.getUserType().name();
    }

}
