package br.com.agendasus.auth.v1;

import br.com.agendasus.auth.v1.domain.usecase.Login;
import br.com.agendasus.auth.v1.dto.UserLoginDTO;
import br.com.agendasus.auth.v1.infrastructure.response.Response;
import br.com.agendasus.auth.v1.infrastructure.response.ResponseUtils;
import br.com.agendasus.auth.v1.infrastructure.security.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController("userLoginController")
public class LoginUserController {

    @Autowired
    private Login business;

    @Autowired
    private ResponseUtils responseUtils;

    @Transactional(readOnly = true)
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public Response login(@RequestBody @Valid UserLoginDTO userLoginDTO, HttpServletRequest request) {
        userLoginDTO.setUsername(userLoginDTO.getUsername().trim());
        return responseUtils.returnObject(business.login(userLoginDTO));
    }

    @Transactional(readOnly = true)
    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public Response getLoggedUser() {
        return responseUtils.returnObject(business.getInfoUserLogged());
    }

    @Transactional(readOnly = true)
    @RequestMapping(value = "/user/{id}", method = RequestMethod.GET)
    public SystemUser getUserById(@PathVariable("id") Long id) {
        br.com.agendasus.auth.v1.domain.model.UserLogin user = business.getById(id);
        business.checkAuthorization(user);
        return new SystemUser(user);
    }

    @Transactional(readOnly = true)
    @RequestMapping(value = "/check/token", method = RequestMethod.POST)
    public SystemUser checkToken() {
        return business.checkHeaderToken();
    }

}
