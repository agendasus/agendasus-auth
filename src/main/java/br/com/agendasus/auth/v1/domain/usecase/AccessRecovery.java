package br.com.agendasus.auth.v1.domain.usecase;

import br.com.agendasus.auth.v1.domain.model.UserLogin;
import br.com.agendasus.auth.v1.domain.usecase.notification.NotificationService;
import br.com.agendasus.auth.v1.infrastructure.persistence.UserLoginDAO;
import br.com.agendasus.auth.v1.domain.usecase.exceptions.ResponseException;
import br.com.agendasus.auth.v1.infrastructure.system.EncriptionUtils;
import br.com.agendasus.auth.v1.infrastructure.system.SystemProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AccessRecovery {

    @Autowired
    private SystemProperties properties;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserLoginDAO dao;

    public void forgotPassword(String email, String requestIP) {
        UserLogin userLogin = dao.getUserLoginByLogin(email);
        if (userLogin != null) {
            String recoveryPasswordHash = UUID.randomUUID().toString();
            userLogin.setHashPasswordRecovery(recoveryPasswordHash);
            dao.merge(userLogin);
            String linkPasswordRecovery = properties.getSystemUrl() + "/password-recovery/" + recoveryPasswordHash;
            String linkInvalidateRecovery = properties.getSystemUrl() + "/invalidate-recovery/" + recoveryPasswordHash;
            notificationService.sendEmailToPasswordRecovery(userLogin, requestIP, linkPasswordRecovery, linkInvalidateRecovery);
        }
    }

    public void passwordRecovery(String hash, String password) {
        UserLogin user = dao.getUserByHashFromPasswordRecovery(hash);
        if (user == null) {
            throw new ResponseException("error.user.invalid.hash");
        }
        user.setHashPasswordRecovery(null);
        checkPassword(password);
        user.setPassword(EncriptionUtils.sha1Converter(password));
        dao.merge(user);
    }

    public void invalidateRecovery(String hash) {
        UserLogin user = dao.getUserByHashFromPasswordRecovery(hash);
        if (user == null) {
            throw new ResponseException("error.user.invalid.hash");
        }
        user.setHashPasswordRecovery(null);
        dao.merge(user);
    }

    private void checkPassword(String password) {
        if(password != null) {
            if(password.trim().length() < 6 || password.trim().length() > 30) {
                throw new ResponseException("error.generic.string.size", "label.senha", "6", "30");
            }
        } else {
            throw new ResponseException("error.generic.field.required", "label.senha");
        }
    }

}
