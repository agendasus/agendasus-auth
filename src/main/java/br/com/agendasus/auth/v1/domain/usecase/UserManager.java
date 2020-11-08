package br.com.agendasus.auth.v1.domain.usecase;
import br.com.agendasus.auth.v1.domain.model.UserLogin;
import br.com.agendasus.auth.v1.infrastructure.persistence.UserLoginDAO;
import br.com.agendasus.auth.v1.infrastructure.enumeration.UserType;
import br.com.agendasus.auth.v1.domain.usecase.exceptions.ResponseException;
import br.com.agendasus.auth.v1.infrastructure.system.EncriptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static br.com.agendasus.auth.AgendaSUSApp.ADMIN_LOGIN;
import static br.com.agendasus.auth.v1.infrastructure.enumeration.UserType.ADMIN;

@Service("userManager")
public class UserManager {

    @Autowired
    protected UserLoginDAO dao;

    @Autowired
    private Login login;

    public UserLogin getById(Long id) throws ResponseException {
        UserLogin user;
        if(id == -1) {
            user = createUserAgendaSUSAdmin();
        } else {
            user = id != null ? dao.findById(id) : null;
            if (user == null) {
                throw new ResponseException("error.generic.not.found.male", "entity.user");
            }
        }
        return user;
    }

    public void checkAuthorization(UserLogin usuarioRequest) {
        UserLogin loggedUser = login.getUserLogged();
        if(!loggedUser.isAdmin()) {
            if(!loggedUser.equals(usuarioRequest)) {
                throw new ResponseException("error.generic.operation.not.authorized");
            }
        }
    }

    public UserLogin get(UserLogin usuarioRequest, Long id) {
        UserLogin user = getById(id);
        checkAuthorization(usuarioRequest);
        return user;
    }

    public void insert(UserLogin usuarioRequest, UserLogin user) {
        try {
            checkAuthorization(usuarioRequest);
            setDefaultAttributeToNewLoginUsuario(user);
            checkPassword(user.getPassword());
            checkExists(user);
            user.setPassword(EncriptionUtils.sha1Converter(user.getPassword()));
            dao.persist(user);
        } catch (ResponseException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseException("error.generic.insert", "entity.subcontausuario");
        }
    }

    private void setDefaultAttributeToNewLoginUsuario(UserLogin loginUsuario) {
        UserType userType = loginUsuario.getUserType();
        loginUsuario.setUserType(userType);
        loginUsuario.setIsActive(true);
    }

    public void update(UserLogin usuarioRequest, UserLogin user, UserLogin userOld) {
        try {
            checkAuthorization(usuarioRequest);
            checkPasswordOnUpdate(user, userOld);
            checkExists(user);
            dao.merge(user);
        } catch (ResponseException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseException("error.generic.update", "entity.subcontausuario");
        }
    }

    private void checkPasswordOnUpdate(UserLogin loginUsuarioNew, UserLogin loginUsuarioOld) {
        if(loginUsuarioNew.getPassword() != null && !loginUsuarioNew.getPassword().isEmpty()) {
            checkPassword(loginUsuarioNew.getPassword());
            loginUsuarioNew.setPassword(EncriptionUtils.sha1Converter(loginUsuarioNew.getPassword()));
        } else {
            loginUsuarioNew.setPassword(loginUsuarioOld.getPassword());
        }
    }

    public void remove(UserLogin userRequest, UserLogin user) {
        try {
            checkAuthorization(userRequest);
            dao.remove(user);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseException("error.generic.delete", "entity.subcontausuario");
        }
    }

    private void checkExists(UserLogin loginUsuario) {
        if(dao.existLogin(loginUsuario)) {
            throw new ResponseException("error.generic.exist.unique.male", "label.login", loginUsuario.getLogin(), "label.usuario");
        }
    }

    private void checkPassword(String password) {
        if(password != null) {
            if(password.trim().length() < 6 || password.trim().length() > 30) {
                throw new ResponseException("error.generic.string.size", "label.senha", "6", "30");
            }
        } else {
            throw new ResponseException("crud.validation.required", "label.senha");
        }
    }

    public void changePassword(UserLogin usuarioRequest, UserLogin loginUsuario, String newPassword, String currentPassword) {
        try {
            checkAuthorization(usuarioRequest);
            checkPassword(newPassword);
            UserLogin logged = login.getUserLogged();
            if(!UserType.ADMIN.equals(logged.getUserType())) {
                if(logged.equals(loginUsuario)) {
                    if (currentPassword == null) {
                        throw new ResponseException("error.usuario.senha.obrigatorio");
                    }
                    if (!EncriptionUtils.checkSha1Password(currentPassword, loginUsuario.getPassword())) {
                        throw new ResponseException("error.usuario.senha.antiga.incorreta");
                    }
                }
            }
            loginUsuario.setPassword(EncriptionUtils.sha1Converter(newPassword));
            dao.merge(loginUsuario);
        } catch (ResponseException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseException("error.usuario.alterar.senha");
        }
    }

    public void setStatus(UserLogin user) {
        dao.updateIsActiveFlag(user);
    }

    private static UserLogin createUserAgendaSUSAdmin() {
        UserLogin user = new UserLogin();
        user.setId(-1L);
        user.setLogin(ADMIN_LOGIN);
        user.setName(ADMIN_LOGIN);
        user.setIsActive(true);
        user.setUserType(ADMIN);
        return user;
    }

}
