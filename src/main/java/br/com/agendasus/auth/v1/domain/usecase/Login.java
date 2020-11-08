package br.com.agendasus.auth.v1.domain.usecase;

import br.com.agendasus.auth.v1.infrastructure.persistence.UserLoginDAO;
import br.com.agendasus.auth.v1.infrastructure.persistence.redis.LoginAttemptDAO;
import br.com.agendasus.auth.v1.dto.UserLoginDTO;
import br.com.agendasus.auth.v1.domain.usecase.exceptions.AuthenticationException;
import br.com.agendasus.auth.v1.domain.usecase.exceptions.ResponseException;
import br.com.agendasus.auth.v1.domain.usecase.exceptions.AuthTokenException;
import br.com.agendasus.auth.v1.infrastructure.system.SystemProperties;
import br.com.agendasus.auth.v1.infrastructure.security.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import br.com.agendasus.auth.v1.domain.model.UserLogin;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static br.com.agendasus.auth.AgendaSUSApp.ADMIN_LOGIN;

@Service("login")
public class Login {

    private Logger logger = LoggerFactory.getLogger(Login.class);

    @Autowired
    private SystemProperties properties;

    @Autowired
    protected UserLoginDAO dao;

    @Autowired
    private LoginAttemptDAO loginAttemptDAO;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenProvider jwtTokenUtil;

    @Autowired
    private LoginAccountDetailsService userDetailsService;

    public Map<String, Object> login(UserLoginDTO userLoginDTO) {
        logger.info("Trying logging with user: {}", userLoginDTO.getUsername());
        checkLoginBlocked(userLoginDTO.getUsername());
        try {
            UserDetails user = userDetailsService.loadUserByUsername(userLoginDTO.getUsername());
            AgendaSUSUserSecurity agendaSUSUser = (AgendaSUSUserSecurity) user;
            final String authorities = agendaSUSUser.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.joining(","));
            final String token = jwtTokenUtil.generateToken(agendaSUSUser.getLogin(), authorities, properties.getTokenTimeoutMinutes());
            setUserToSpringContext(userLoginDTO, agendaSUSUser);
            loginAttemptDAO.del(userLoginDTO.getUsername());
            Map<String, Object> info = new HashMap<>();
            info.put("token", token);
            info.put("id", agendaSUSUser.getId());
            info.put("name", agendaSUSUser.getName());
            info.put("login", agendaSUSUser.getUsername());
            info.put("type", agendaSUSUser.getUser().getUserType().name());
            logger.info("User {} logged", userLoginDTO.getUsername());
            return info;
        } catch (BadCredentialsException e) {
            loginAttemptDAO.increment(userLoginDTO.getUsername());
            throw e;
        }
    }

    public void checkAuthorization(UserLogin usuarioRequest) {
        UserLogin loggedUser = getUserLogged();
        if (!loggedUser.isAdmin() && !loggedUser.equals(usuarioRequest)) {
            throw new ResponseException("error.generic.operation.not.authorized");
        }
    }

    public UserLogin getById(Long id) throws ResponseException {
        UserLogin user;
        if(id == -1) {
            user = SystemProperties.defaultUserLoginAdmin();
        } else {
            user = id != null ? dao.findById(id) : null;
            if (user == null) {
                throw new ResponseException("error.generic.not.found.male", "entity.user");
            }
        }
        return user;
    }

    private void setUserToSpringContext(UserLoginDTO userLoginDTO, AgendaSUSUserSecurity agendaSUSUser) {
        final Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        userLoginDTO.getUsername(),
                        userLoginDTO.getPassword(),
                        agendaSUSUser.getAuthorities()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private void checkLoginBlocked(String username) {
        Integer attemptCount = loginAttemptDAO.getAttempt(username);
        if (attemptCount != null && attemptCount >= 5) {
            throw new AuthenticationException("error.login.blocked", true);
        }
    }

    public Map<String, Object> getInfoUserLogged() {
        UserLogin logged = getUserLogged();
        return getInfoUser(logged);
    }

    private Map<String, Object> getInfoUser(UserLogin usuarioLogado) {
        Map<String, Object> info = new HashMap<>();
        info.put("id", usuarioLogado.getId());
        info.put("name", usuarioLogado.getName());
        info.put("login", usuarioLogado.getLogin());
        info.put("type", usuarioLogado.getUserType().name());
        return info;
    }

    public SystemUser checkHeaderToken() {
        Object objectUser = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (objectUser == null || objectUser.equals("anonymousUser") || !(objectUser instanceof AgendaSUSUserSecurity)) {
            throw new AuthTokenException("error.invalid.token");
        }
        AgendaSUSUserSecurity logged = (AgendaSUSUserSecurity) objectUser;
        return new SystemUser(logged);
    }

    public UserLogin getUserLogged() {
        Object objectUser = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (objectUser == null || objectUser.equals("anonymousUser")) {
            throw new ResponseException("error.generic.operation.not.authorized");
        }
        UserLogin loggedUser;
        AgendaSUSUserSecurity securityUser = (AgendaSUSUserSecurity) objectUser;
        if (ADMIN_LOGIN.equals(securityUser.getUsername())) {
            loggedUser = SystemProperties.defaultUserLoginAdmin();
        } else {
            loggedUser = dao.getUserLoginByLogin(securityUser.getUsername());
        }
        return loggedUser;
    }

}
