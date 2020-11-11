package br.com.agendasus.auth.v1.infrastructure.system;

import br.com.agendasus.auth.v1.domain.model.UserLogin;
import br.com.agendasus.auth.v1.infrastructure.security.AgendaSUSUserSecurity;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static br.com.agendasus.auth.AgendaSUSApp.ADMIN_LOGIN;
import static br.com.agendasus.auth.AgendaSUSApp.ADMIN_SENHA;
import static br.com.agendasus.auth.v1.infrastructure.enumeration.UserType.ADMIN;

@Getter
@Component
public class SystemProperties {

    @Value("${server.port}") private String appPort;
    @Value("${agendasus.system-url}") private String systemUrl;
    @Value("${agendasus.token-timeout-minutes}") private Integer tokenTimeoutMinutes;
    @Value("${spring.datasource.server}") private String databaseServer;
    @Value("${spring.datasource.port}") private String databasePort;
    @Value("${spring.datasource.name}") private String databaseName;
    @Value("${spring.datasource.username}") private String databaseUser;
    @Value("${spring.datasource.password}") private String databasePassword;
    @Value("${spring.redis.host}") private String redisHost;
    @Value("${spring.redis.port}") private Integer redisPort;
    @Value("${spring.redis.password}") private String redisPassword;

    public static AgendaSUSUserSecurity defaultUserSecurityAdmin(){
        UserLogin user = new UserLogin();
        user.setId(-1L);
        user.setLogin(ADMIN_LOGIN);
        user.setIsActive(true);
        user.setPassword(EncriptionUtils.sha1Converter(ADMIN_SENHA));
        user.setUserType(ADMIN);
        List roles = new ArrayList<SimpleGrantedAuthority>();
        roles.add(new SimpleGrantedAuthority(ADMIN.name()));
        return new AgendaSUSUserSecurity(user, true, true, true, roles);
    }

    private static UserLogin defaultMasterUserAdmin() {
        UserLogin master = new UserLogin();
        master.setId(0L);
        master.setName("Administrador");
        master.setIsActive(true);
        master.setUserType(ADMIN);
        return master;
    }

    public static UserLogin defaultUserLoginAdmin() {
        UserLogin userMaster = new UserLogin();
        userMaster.setUserType(ADMIN);
        userMaster.setName(ADMIN_LOGIN);
        userMaster.setId(-1L);
        UserLogin user = new UserLogin();
        user.setId(-1L);
        user.setLogin(ADMIN_LOGIN);
        user.setName(ADMIN_LOGIN);
        user.setIsActive(true);
        user.setUserType(ADMIN);
        return user;
    }

}
