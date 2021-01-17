package br.com.agendasus.auth.v1.domain.usecase;

import br.com.agendasus.auth.v1.domain.model.UserLogin;
import br.com.agendasus.auth.v1.domain.usecase.exceptions.AuthTokenException;
import br.com.agendasus.auth.v1.domain.usecase.exceptions.ResponseException;
import br.com.agendasus.auth.v1.dto.UserLoginDTO;
import br.com.agendasus.auth.v1.infrastructure.enumeration.UserType;
import br.com.agendasus.auth.v1.infrastructure.persistence.UserLoginDAO;
import br.com.agendasus.auth.v1.infrastructure.persistence.redis.LoginAttemptDAO;
import br.com.agendasus.auth.v1.infrastructure.security.AgendaSUSUserSecurity;
import br.com.agendasus.auth.v1.infrastructure.security.LoginAccountDetailsService;
import br.com.agendasus.auth.v1.infrastructure.security.SystemUser;
import br.com.agendasus.auth.v1.infrastructure.security.TokenProvider;
import br.com.agendasus.auth.v1.infrastructure.system.SystemProperties;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static br.com.agendasus.auth.AgendaSUSApp.ADMIN_LOGIN;
import static br.com.agendasus.auth.v1.infrastructure.enumeration.UserType.ADMIN;
import static br.com.agendasus.auth.v1.infrastructure.enumeration.UserType.PATIENT;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class LoginTest {

    @InjectMocks
    private Login login;

    @Mock
    private LoginAccountDetailsService userDetailsService;

    @Mock
    private UserLoginDAO userLoginDAO;

    @Mock
    private LoginAttemptDAO loginAttemptDAO;

    @Mock
    private TokenProvider jwtTokenUtil;

    @Mock
    private SystemProperties properties;

    @Mock
    private AuthenticationManager authenticationManager;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private SecurityContext securityContext;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    public void login() {
        UserLoginDTO dto = new UserLoginDTO();
        dto.setUsername("admin");
        List roles = new ArrayList<SimpleGrantedAuthority>();
        roles.add(new SimpleGrantedAuthority(ADMIN.name()));
        UserDetails userDetails = new AgendaSUSUserSecurity(getMockedUser(ADMIN_LOGIN, ADMIN), true, true, true, roles);

        when(properties.getTokenTimeoutMinutes()).thenReturn(200);
        when(loginAttemptDAO.getAttempt("admin")).thenReturn(null);
        when(userDetailsService.loadUserByUsername("admin")).thenReturn((AgendaSUSUserSecurity) userDetails);
        when(authenticationManager.authenticate(null)).thenReturn(null);

        Map<String, Object> info = login.login(dto);

        assertNull(info.get("token"));
        assertEquals(Long.valueOf(123456L), info.get("id"));
        assertEquals("Joao", info.get("name"));
        assertEquals("admin", info.get("login"));
        assertEquals("ADMIN", info.get("type"));
    }


    @Test
    public void checkAuthorizationWhenUserIsNotAdminAndIsNotTheSameUserRequest() {
        expectedException.expect(ResponseException.class);
        expectedException.expectMessage("error.generic.operation.not.authorized");

        List roles = new ArrayList<SimpleGrantedAuthority>();
        roles.add(new SimpleGrantedAuthority(PATIENT.name()));
        AgendaSUSUserSecurity applicationUser = new AgendaSUSUserSecurity(getMockedUser("joao@gmail.com", PATIENT), true, true, true, roles);
        Authentication authentication = mock(Authentication.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(securityContext.getAuthentication().getPrincipal()).thenReturn(applicationUser);
        SecurityContextHolder.setContext(securityContext);
        when(userLoginDAO.getUserLoginByLogin("joao@gmail.com")).thenReturn(getMockedUser("joao@gmail.com", PATIENT));

        login.checkAuthorization(new UserLogin());
    }

    @Test
    public void checkAuthorization() {
        List roles = new ArrayList<SimpleGrantedAuthority>();
        roles.add(new SimpleGrantedAuthority(ADMIN.name()));
        UserLogin mockedUser = getMockedUser(ADMIN_LOGIN, ADMIN);
        AgendaSUSUserSecurity applicationUser = new AgendaSUSUserSecurity(mockedUser, true, true, true, roles);
        Authentication authentication = mock(Authentication.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(securityContext.getAuthentication().getPrincipal()).thenReturn(applicationUser);
        SecurityContextHolder.setContext(securityContext);
        when(userLoginDAO.getUserLoginByLogin(ADMIN_LOGIN)).thenReturn(mockedUser);

        login.checkAuthorization(mockedUser);
    }

    @Test
    public void getByIdReturnAnResponseExceptionBecauseIsNull() {
        expectedException.expect(ResponseException.class);
        expectedException.expectMessage("error.generic.not.found.male");
        when(userLoginDAO.findById(1L)).thenReturn(null);

        UserLogin user = login.getById(1L);
    }

    @Test
    public void getByIdAnPatient() {
        when(userLoginDAO.findById(1L)).thenReturn(getMockedUser("joao@gmail.com", PATIENT));

        UserLogin user = login.getById(1L);

        assertEquals(Long.valueOf(123456L), user.getId());
        assertEquals("joao@gmail.com", user.getLogin());
        assertEquals("Joao", user.getName());
        assertTrue(user.getIsActive());
        assertEquals(PATIENT, user.getUserType());
    }

    @Test
    public void getByIdAnAdminUser() {
        UserLogin user = login.getById(-1L);

        assertEquals(Long.valueOf(-1), user.getId());
        assertEquals(ADMIN_LOGIN, user.getLogin());
        assertEquals(ADMIN_LOGIN, user.getName());
        assertTrue(user.getIsActive());
        assertEquals(ADMIN, user.getUserType());
    }

    @Test
    public void getInfoUserLogged() {
        List roles = new ArrayList<SimpleGrantedAuthority>();
        roles.add(new SimpleGrantedAuthority(PATIENT.name()));
        AgendaSUSUserSecurity applicationUser = new AgendaSUSUserSecurity(getMockedUser("joao@gmail.com", PATIENT), true, true, true, roles);
        Authentication authentication = mock(Authentication.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(securityContext.getAuthentication().getPrincipal()).thenReturn(applicationUser);
        SecurityContextHolder.setContext(securityContext);
        when(userLoginDAO.getUserLoginByLogin("joao@gmail.com")).thenReturn(getMockedUser("joao@gmail.com", PATIENT));

        Map<String, Object> infoUser = login.getInfoUserLogged();

        assertEquals(Long.valueOf(123456L), infoUser.get("id"));
        assertEquals("joao@gmail.com", infoUser.get("login"));
        assertEquals("Joao", infoUser.get("name"));
        assertEquals("PATIENT", infoUser.get("type"));
    }

    @Test
    public void checkHeaderTokenReturnNullPointerBeacauseDoesntHaveSecurityContext() {
        expectedException.expect(NullPointerException.class);

        SystemUser user = login.checkHeaderToken();
    }

    @Test
    public void checkHeaderTokenReturnNullPointerExceptionWhenAuthenticationIsNull() {
        expectedException.expect(NullPointerException.class);

        when(securityContext.getAuthentication()).thenReturn(null);
        SecurityContextHolder.setContext(securityContext);

        SystemUser user = login.checkHeaderToken();
    }

    @Test
    public void checkHeaderTokenReturnResponseExceptionWhenPrincipalIsNull() {
        expectedException.expect(AuthTokenException.class);
        expectedException.expectMessage("error.invalid.token");

        Authentication authentication = mock(Authentication.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(securityContext.getAuthentication().getPrincipal()).thenReturn(null);
        SecurityContextHolder.setContext(securityContext);

        SystemUser user = login.checkHeaderToken();
    }

    @Test
    public void checkHeaderTokenWhenPatient() {
        List roles = new ArrayList<SimpleGrantedAuthority>();
        roles.add(new SimpleGrantedAuthority(PATIENT.name()));
        AgendaSUSUserSecurity applicationUser = new AgendaSUSUserSecurity(getMockedUser("joao@gmail.com", PATIENT), true, true, true, roles);
        Authentication authentication = mock(Authentication.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(securityContext.getAuthentication().getPrincipal()).thenReturn(applicationUser);
        SecurityContextHolder.setContext(securityContext);
        SystemProperties systemProperties = mock(SystemProperties.class);

        SystemUser user = login.checkHeaderToken();

        assertEquals(Long.valueOf(123456), user.getId());
        assertEquals("joao@gmail.com", user.getLogin());
        assertEquals("joao@gmail.com", user.getName());
        assertEquals(PATIENT, user.getUserType());
        assertEquals(Arrays.asList("PATIENT"), user.getPermissions());
    }

    @Test
    public void getUserLoggedReturnNullPointerBeacauseDoesntHaveSecurityContext() {
        expectedException.expect(NullPointerException.class);

        UserLogin user = login.getUserLogged();
    }

    @Test
    public void getUserLoggedReturnNullPointerExceptionWhenAuthenticationIsNull() {
        expectedException.expect(NullPointerException.class);

        when(securityContext.getAuthentication()).thenReturn(null);
        SecurityContextHolder.setContext(securityContext);

        UserLogin user = login.getUserLogged();
    }

    @Test
    public void getUserLoggedReturnResponseExceptionWhenPrincipalIsNull() {
        expectedException.expect(ResponseException.class);
        expectedException.expectMessage("error.generic.operation.not.authorized");

        Authentication authentication = mock(Authentication.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(securityContext.getAuthentication().getPrincipal()).thenReturn(null);
        SecurityContextHolder.setContext(securityContext);

        UserLogin user = login.getUserLogged();
    }

    @Test
    public void getUserLoggedWhenAdmin() {
        List roles = new ArrayList<SimpleGrantedAuthority>();
        roles.add(new SimpleGrantedAuthority(ADMIN.name()));
        AgendaSUSUserSecurity applicationUser = new AgendaSUSUserSecurity(getMockedUser(ADMIN_LOGIN, ADMIN), true, true, true, roles);
        Authentication authentication = mock(Authentication.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(securityContext.getAuthentication().getPrincipal()).thenReturn(applicationUser);
        SecurityContextHolder.setContext(securityContext);
        SystemProperties systemProperties = mock(SystemProperties.class);

        UserLogin user = login.getUserLogged();

        assertEquals(Long.valueOf(-1), user.getId());
        assertEquals(ADMIN_LOGIN, user.getLogin());
        assertEquals(ADMIN_LOGIN, user.getName());
        assertTrue(user.getIsActive());
        assertEquals(ADMIN, user.getUserType());
    }

    @Test
    public void getUserLoggedwhenIsNotAdmin() {
        List roles = new ArrayList<SimpleGrantedAuthority>();
        roles.add(new SimpleGrantedAuthority(PATIENT.name()));
        AgendaSUSUserSecurity applicationUser = new AgendaSUSUserSecurity(getMockedUser("joao@gmail.com", PATIENT), true, true, true, roles);
        Authentication authentication = mock(Authentication.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(securityContext.getAuthentication().getPrincipal()).thenReturn(applicationUser);
        SecurityContextHolder.setContext(securityContext);
        when(userLoginDAO.getUserLoginByLogin("joao@gmail.com")).thenReturn(getMockedUser("joao@gmail.com", PATIENT));

        UserLogin user = login.getUserLogged();

        assertEquals(Long.valueOf(123456L), user.getId());
        assertEquals("joao@gmail.com", user.getLogin());
        assertEquals("Joao", user.getName());
        assertTrue(user.getIsActive());
        assertEquals(PATIENT, user.getUserType());
    }

    private UserLogin getMockedUser(String userName, UserType type) {
        UserLogin mockedUser = new UserLogin();
        mockedUser.setLogin(userName);
        mockedUser.setPassword("123456");
        mockedUser.setIsActive(true);
        mockedUser.setId(123456L);
        mockedUser.setName("Joao");
        mockedUser.setUserType(type);
        return mockedUser;
    }

}
