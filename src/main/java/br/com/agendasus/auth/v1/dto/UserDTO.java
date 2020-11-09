package br.com.agendasus.auth.v1.dto;

import br.com.agendasus.auth.v1.domain.model.UserLogin;
import br.com.agendasus.auth.v1.infrastructure.enumeration.UserType;
import br.com.agendasus.auth.v1.infrastructure.validators.NotNullAndNotEmpty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Email;

@Getter
@Setter
public class UserDTO {

    private Long id;

    @NotNullAndNotEmpty(message = "crud.validation.required#{label.name}")
    private String name;

    @NotNullAndNotEmpty(message = "crud.validation.required#{label.login}")
    @Email(message = "error.generic.field.email#{label.login}")
    private String login;

    private String password;

    private String userType;

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
