package br.com.agendasus.auth.v1;

import br.com.agendasus.auth.v1.domain.usecase.AccessRecovery;
import br.com.agendasus.auth.v1.dto.PasswordRecoveryDTO;
import br.com.agendasus.auth.v1.infrastructure.response.Response;
import br.com.agendasus.auth.v1.infrastructure.response.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController("accessRecoveryController")
public class AccessRecoveryController {

    @Autowired
    private AccessRecovery business;

    @Autowired
    private ResponseUtils responseUtils;


    @Transactional
    @RequestMapping(value = "/forgot-password", method = RequestMethod.POST)
    public Response forgotPassword(@RequestBody String login, HttpServletRequest request) {
        business.forgotPassword(login, request.getRemoteAddr());
        return responseUtils.returnMessage("success.user.sent.email.password.recovery");
    }

    @Transactional
    @RequestMapping(value = "/password-recovery", method = RequestMethod.POST)
    public Response passwordRecovery(@RequestBody PasswordRecoveryDTO passwordRecoveryDTO) {
        business.passwordRecovery(passwordRecoveryDTO.getHash(), passwordRecoveryDTO.getPassword());
        return responseUtils.returnMessage("success.user.updated.password");
    }

    @Transactional
    @RequestMapping(value = "/invalidate-recovery", method = RequestMethod.DELETE)
    public Response invalidateRecovery(@RequestBody String hash) {
        business.invalidateRecovery(hash);
        return responseUtils.returnMessage("success.invalidate.password.recovery");
    }

}
