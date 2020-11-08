package br.com.agendasus.auth.v1.infrastructure.security;

import br.com.agendasus.auth.v1.domain.model.UserLogin;
import br.com.agendasus.auth.v1.infrastructure.enumeration.UserType;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

public class SystemUser implements Serializable {

    private static final long serialVersionUID = -4064471055430886033L;

    private Long id;
    private String name;
    private String login;
    private UserType userType;
    private List<String> permissions;


    public SystemUser() {
        super();
    }

    public SystemUser(AgendaSUSUserSecurity userSecurity) {
        this.id = userSecurity.getId();
        this.name = userSecurity.getUsername();
        this.login = userSecurity.getLogin();
        this.userType = userSecurity.getUser().getUserType();
        this.permissions = userSecurity.getAuthorities().stream().map(grantedAuthority -> grantedAuthority.getAuthority()).collect(Collectors.toList());
    }

    public SystemUser(UserLogin user) {
        this.id = user.getId();
        this.name = user.getName();
        this.login = user.getLogin();
        this.userType = user.getUserType();
        this.permissions = null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }

}
