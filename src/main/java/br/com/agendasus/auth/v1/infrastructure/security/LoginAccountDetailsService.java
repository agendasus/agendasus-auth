package br.com.agendasus.auth.v1.infrastructure.security;

import br.com.agendasus.auth.v1.infrastructure.system.SystemProperties;
import br.com.agendasus.auth.v1.domain.model.UserLogin;
import br.com.agendasus.auth.v1.infrastructure.enumeration.SystemPermission;
import br.com.agendasus.auth.v1.infrastructure.persistence.UserLoginDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static br.com.agendasus.auth.AgendaSUSApp.ADMIN_LOGIN;

@Service("loginAccountDetailsService")
@Transactional(rollbackFor = {Exception.class, Throwable.class}, timeout = 120)
public class LoginAccountDetailsService implements UserDetailsService {

    @Autowired
    protected UserLoginDAO userLoginDAO;

    @Override
    public AgendaSUSUserSecurity loadUserByUsername(String login) throws UsernameNotFoundException {
        AgendaSUSUserSecurity user = login(login);
        if(user == null) {
            throw new UsernameNotFoundException("");
        }
        return user;
    }

    private AgendaSUSUserSecurity login(String login) {
        AgendaSUSUserSecurity userSecurity = null;
        if(login.equals(ADMIN_LOGIN)) {
            userSecurity = SystemProperties.defaultUserSecurityAdmin();
        } else {
            UserLogin user = this.userLoginDAO.getUserLoginByLogin(login);
            if(user != null) {
                List roles = SystemPermission.getSimpleGrantedAuthorityPermissions(user);
                userSecurity = new AgendaSUSUserSecurity(user, true, true, user.getIsActive(), roles);
            }
        }
        return userSecurity;
    }

}
