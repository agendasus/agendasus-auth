package br.com.agendasus.auth.v1.domain.usecase;

import br.com.agendasus.auth.v1.domain.model.UserLogin;
import br.com.agendasus.auth.v1.domain.usecase.exceptions.ResponseException;
import br.com.agendasus.auth.v1.infrastructure.enumeration.UserType;
import br.com.agendasus.auth.v1.infrastructure.persistence.UserLoginDAO;
import br.com.agendasus.auth.v1.infrastructure.security.AgendaSUSUserSecurity;
import br.com.agendasus.auth.v1.infrastructure.system.EncriptionUtils;
import org.junit.Assert;
import static br.com.agendasus.auth.v1.infrastructure.enumeration.UserType.PATIENT;
import static org.mockito.Mockito.when;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.List;

import static br.com.agendasus.auth.AgendaSUSApp.ADMIN_LOGIN;
import static br.com.agendasus.auth.v1.infrastructure.enumeration.UserType.ADMIN;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class UserManagerTest {

    @InjectMocks
    private UserManager userManager;

    @Mock
    private Login login;

    private SecurityContext securityContext;

    @Mock
    private UserLoginDAO userLoginDAO;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    public void getByIdAdmin() {
        UserLogin user = userManager.getById(-1L);
        assertEquals(Long.valueOf(-1), user.getId());
        assertEquals(ADMIN_LOGIN, user.getLogin());
        assertEquals(ADMIN_LOGIN, user.getName());
        assertEquals(ADMIN, user.getUserType());
        assertTrue(user.getIsActive());
    }

    @Test
    public void getByIdPatientUser() {
        when(userLoginDAO.findById(123456L)).thenReturn(getMockedUser("joao@gmail.com", PATIENT));
        UserLogin user = userManager.getById(123456L);
        assertEquals(Long.valueOf(123456), user.getId());
        assertEquals("joao@gmail.com", user.getLogin());
        assertEquals("Joao", user.getName());
        assertEquals(PATIENT, user.getUserType());
        assertTrue(user.getIsActive());
    }

    @Test
    public void getByIdInexistentUser() {
        expectedException.expect(ResponseException.class);
        expectedException.expectMessage("error.generic.not.found.male");
        when(userLoginDAO.findById(1L)).thenReturn(null);
        UserLogin user = userManager.getById(1L);

    }

    @Test
    public void checkAuthorizationWhenUserIsNotAdminAndIsNotTheSameUserRequest() {
        expectedException.expect(ResponseException.class);
        expectedException.expectMessage("error.generic.operation.not.authorized");
        when(login.getUserLogged()).thenReturn(getMockedUser("joao@gmail.com", PATIENT));
        userManager.checkAuthorization(new UserLogin());
    }

    @Test
    public void checkAuthorization() {
        UserLogin mockedUser = getMockedUser(ADMIN_LOGIN, ADMIN);
        when(login.getUserLogged()).thenReturn(mockedUser);
        userManager.checkAuthorization(mockedUser);
    }

    @Test
    public void get() {
        UserLogin mockedUser = getMockedUser(ADMIN_LOGIN, ADMIN);
        when(login.getUserLogged()).thenReturn(mockedUser);
        when(userLoginDAO.findById(123456L)).thenReturn(mockedUser);
        UserLogin user = userManager.get(getMockedUser(ADMIN_LOGIN, ADMIN), 123456L);
        assertEquals(Long.valueOf(123456L), user.getId());
        assertEquals(ADMIN_LOGIN, user.getLogin());
        assertEquals("Joao", user.getName());
        assertEquals(ADMIN, user.getUserType());
        assertTrue(user.getIsActive());
    }

    @Test
    public void insertPatientSuccessfuly() {
        userManager.insertPatient(getMockedUser(ADMIN_LOGIN, ADMIN));
    }

    @Test
    public void insertPatientWithPasswordNull() {
        expectedException.expect(ResponseException.class);
        expectedException.expectMessage("crud.validation.required");

        UserLogin userLogin = getMockedUser(ADMIN_LOGIN, ADMIN);
        userLogin.setPassword(null);
        userManager.insertPatient(userLogin);
    }

    @Test
    public void insertPatientWithPasswordLessThan6Letters() {
        expectedException.expect(ResponseException.class);
        expectedException.expectMessage("error.generic.string.size");

        UserLogin userLogin = getMockedUser(ADMIN_LOGIN, ADMIN);
        userLogin.setPassword("12345");
        userManager.insertPatient(userLogin);
    }

    @Test
    public void insertPatientWithPasswordMoreThan30Letters() {
        expectedException.expect(ResponseException.class);
        expectedException.expectMessage("error.generic.string.size");

        UserLogin userLogin = getMockedUser(ADMIN_LOGIN, ADMIN);
        userLogin.setPassword("1234567890123456789012345678901");
        userManager.insertPatient(userLogin);
    }

    @Test
    public void insertPatientAlreadyExistent() {
        expectedException.expect(ResponseException.class);
        expectedException.expectMessage("error.generic.exist.unique.male");

        UserLogin userLogin = getMockedUser(ADMIN_LOGIN, ADMIN);
        when(userLoginDAO.existLogin(userLogin)).thenReturn(true);

        userManager.insertPatient(userLogin);
    }

    @Test
    public void insertSuccessfuly() {
        UserLogin requestUser = getMockedUser(ADMIN_LOGIN, ADMIN);
        UserLogin newUser = getMockedUser("Joao", PATIENT);
        when(login.getUserLogged()).thenReturn(requestUser);

        userManager.insert(requestUser, newUser);
    }

    @Test
    public void insertWithoutPermission() {
        expectedException.expect(ResponseException.class);
        expectedException.expectMessage("error.generic.operation.not.authorized");

        UserLogin requestUser = getMockedUser("Fernando", PATIENT);
        UserLogin newUser = getMockedUser("Joao", PATIENT);
        when(login.getUserLogged()).thenReturn(newUser);

        userManager.insert(requestUser, newUser);
    }

    @Test
    public void insertWithPasswordNull() {
        expectedException.expect(ResponseException.class);
        expectedException.expectMessage("crud.validation.required");

        UserLogin requestUser = getMockedUser(ADMIN_LOGIN, ADMIN);
        UserLogin newUser = getMockedUser("Joao", PATIENT);
        newUser.setPassword(null);
        when(login.getUserLogged()).thenReturn(requestUser);

        userManager.insert(requestUser, newUser);
    }

    @Test
    public void insertWithPasswordLessThan6Letters() {
        expectedException.expect(ResponseException.class);
        expectedException.expectMessage("error.generic.string.size");

        UserLogin requestUser = getMockedUser(ADMIN_LOGIN, ADMIN);
        UserLogin newUser = getMockedUser("Joao", PATIENT);
        newUser.setPassword("12345");
        when(login.getUserLogged()).thenReturn(requestUser);

        userManager.insert(requestUser, newUser);
    }

    @Test
    public void insertWithPasswordMoreThan30Letters() {
        expectedException.expect(ResponseException.class);
        expectedException.expectMessage("error.generic.string.size");

        UserLogin requestUser = getMockedUser(ADMIN_LOGIN, ADMIN);
        UserLogin newUser = getMockedUser("Joao", PATIENT);
        newUser.setPassword("1234567890123456789012345678901");
        when(login.getUserLogged()).thenReturn(requestUser);

        userManager.insert(requestUser, newUser);
    }

    @Test
    public void insertAlreadyExistent() {
        expectedException.expect(ResponseException.class);
        expectedException.expectMessage("error.generic.exist.unique.male");

        UserLogin requestUser = getMockedUser(ADMIN_LOGIN, ADMIN);
        UserLogin newUser = getMockedUser("Joao", PATIENT);
        when(login.getUserLogged()).thenReturn(requestUser);
        when(userLoginDAO.existLogin(newUser)).thenReturn(true);

        userManager.insert(requestUser, newUser);
    }

    @Test
    public void updateSuccessfuly() {
        UserLogin requestUser = getMockedUser(ADMIN_LOGIN, ADMIN);
        UserLogin userOld = getMockedUser("Johny", PATIENT);
        UserLogin userUpdated = getMockedUser("Joao", PATIENT);
        when(login.getUserLogged()).thenReturn(requestUser);

        userManager.update(requestUser, userUpdated, userOld);
    }

    @Test
    public void updateWithoutPermission() {
        expectedException.expect(ResponseException.class);
        expectedException.expectMessage("error.generic.operation.not.authorized");

        UserLogin requestUser = getMockedUser("Fernando", PATIENT);
        UserLogin userOld = getMockedUser("Johny", PATIENT);
        UserLogin userUpdated = getMockedUser("Joao", PATIENT);
        when(login.getUserLogged()).thenReturn(userUpdated);

        userManager.update(requestUser, userUpdated, userOld);
    }

    @Test
    public void updateWithPasswordLessThan6Letters() {
        expectedException.expect(ResponseException.class);
        expectedException.expectMessage("error.generic.string.size");

        UserLogin requestUser = getMockedUser(ADMIN_LOGIN, ADMIN);
        UserLogin userOld = getMockedUser("Johny", PATIENT);
        UserLogin userUpdated = getMockedUser("Joao", PATIENT);
        userUpdated.setPassword("12345");
        when(login.getUserLogged()).thenReturn(requestUser);

        userManager.update(requestUser, userUpdated, userOld);
    }

    @Test
    public void updateWithPasswordMoreThan30Letters() {
        expectedException.expect(ResponseException.class);
        expectedException.expectMessage("error.generic.string.size");

        UserLogin requestUser = getMockedUser(ADMIN_LOGIN, ADMIN);
        UserLogin userOld = getMockedUser("Johny", PATIENT);
        UserLogin userUpdated = getMockedUser("Joao", PATIENT);
        userUpdated.setPassword("1234567890123456789012345678901");
        when(login.getUserLogged()).thenReturn(requestUser);

        userManager.update(requestUser, userUpdated, userOld);
    }

    @Test
    public void updateAlreadyExistent() {
        expectedException.expect(ResponseException.class);
        expectedException.expectMessage("error.generic.exist.unique.male");

        UserLogin requestUser = getMockedUser(ADMIN_LOGIN, ADMIN);
        UserLogin userOld = getMockedUser("Johny", PATIENT);
        UserLogin userUpdated = getMockedUser("Joao", PATIENT);
        when(login.getUserLogged()).thenReturn(requestUser);
        when(userLoginDAO.existLogin(userUpdated)).thenReturn(true);

        userManager.update(requestUser, userUpdated, userOld);
    }

    @Test
    public void removeSuccessfuly() {
        UserLogin requestUser = getMockedUser(ADMIN_LOGIN, ADMIN);
        UserLogin userRemoved = getMockedUser("Joao", PATIENT);
        when(login.getUserLogged()).thenReturn(requestUser);

        userManager.remove(requestUser, userRemoved);
    }

    @Test
    public void removeWithoutPermission() {
        expectedException.expect(ResponseException.class);
        expectedException.expectMessage("error.generic.operation.not.authorized");

        UserLogin requestUser = getMockedUser("Fernando", PATIENT);
        UserLogin userRemoved = getMockedUser("Joao", PATIENT);
        when(login.getUserLogged()).thenReturn(userRemoved);

        userManager.remove(requestUser, userRemoved);
    }

    @Test
    public void changePasswordSuccessfulyByAdmin() {
        UserLogin requestUser = getMockedUser(ADMIN_LOGIN, ADMIN);
        UserLogin userUpdated = getMockedUser("Joao", PATIENT);
        userUpdated.setPassword(EncriptionUtils.sha1Converter(userUpdated.getPassword()));
        String currentPassword = "123456";
        String newPassword = "654321";
        when(login.getUserLogged()).thenReturn(requestUser);

        userManager.changePassword(requestUser, userUpdated, newPassword, currentPassword);
    }

    @Test
    public void changePasswordSuccessfulyByUser() {
        UserLogin userUpdated = getMockedUser("Joao", PATIENT);
        userUpdated.setPassword(EncriptionUtils.sha1Converter(userUpdated.getPassword()));
        String currentPassword = "123456";
        String newPassword = "654321";
        when(login.getUserLogged()).thenReturn(userUpdated);

        userManager.changePassword(userUpdated, userUpdated, newPassword, currentPassword);
    }

    @Test
    public void changePasswordWhenCurrentPasswordNull() {
        expectedException.expect(ResponseException.class);
        expectedException.expectMessage("error.usuario.senha.obrigatorio");

        UserLogin userUpdated = getMockedUser("Joao", PATIENT);
        userUpdated.setPassword(EncriptionUtils.sha1Converter(userUpdated.getPassword()));
        String currentPassword = null;
        String newPassword = "654321";
        when(login.getUserLogged()).thenReturn(userUpdated);

        userManager.changePassword(userUpdated, userUpdated, newPassword, currentPassword);
    }

    @Test
    public void changePasswordWhenCurrentPasswordIsWrong() {
        expectedException.expect(ResponseException.class);
        expectedException.expectMessage("error.usuario.senha.antiga.incorreta");

        UserLogin userUpdated = getMockedUser("Joao", PATIENT);
        userUpdated.setPassword(EncriptionUtils.sha1Converter(userUpdated.getPassword()));
        String currentPassword = "0246810";
        String newPassword = "654321";
        when(login.getUserLogged()).thenReturn(userUpdated);

        userManager.changePassword(userUpdated, userUpdated, newPassword, currentPassword);
    }

    @Test
    public void changePasswordWhenNewPasswordIsNull() {
        expectedException.expect(ResponseException.class);
        expectedException.expectMessage("crud.validation.required");

        UserLogin userUpdated = getMockedUser("Joao", PATIENT);
        userUpdated.setPassword(EncriptionUtils.sha1Converter(userUpdated.getPassword()));
        String currentPassword = "123456";
        String newPassword = null;
        when(login.getUserLogged()).thenReturn(userUpdated);

        userManager.changePassword(userUpdated, userUpdated, newPassword, currentPassword);
    }

    @Test
    public void changePasswordWhenNewPasswordHasLessThan6Letters() {
        expectedException.expect(ResponseException.class);
        expectedException.expectMessage("error.generic.string.size");

        UserLogin userUpdated = getMockedUser("Joao", PATIENT);
        userUpdated.setPassword(EncriptionUtils.sha1Converter(userUpdated.getPassword()));
        String currentPassword = "123456";
        String newPassword = "12345";
        when(login.getUserLogged()).thenReturn(userUpdated);

        userManager.changePassword(userUpdated, userUpdated, newPassword, currentPassword);
    }

    @Test
    public void changePasswordWhenNewPasswordHasMoreThan30Letters() {
        expectedException.expect(ResponseException.class);
        expectedException.expectMessage("error.generic.string.size");

        UserLogin userUpdated = getMockedUser("Joao", PATIENT);
        userUpdated.setPassword(EncriptionUtils.sha1Converter(userUpdated.getPassword()));
        String currentPassword = "123456";
        String newPassword = "1234567890123456789012345678901";
        when(login.getUserLogged()).thenReturn(userUpdated);

        userManager.changePassword(userUpdated, userUpdated, newPassword, currentPassword);
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
