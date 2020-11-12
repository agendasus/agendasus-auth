package br.com.agendasus.auth.v1;

import br.com.agendasus.auth.v1.domain.usecase.Login;
import br.com.agendasus.auth.v1.dto.UserLoginDTO;
import br.com.agendasus.auth.v1.infrastructure.response.Response;
import br.com.agendasus.auth.v1.infrastructure.response.ResponseUtils;
import br.com.agendasus.auth.v1.infrastructure.security.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Api(value = "User Login")
@RestController("userLoginController")
public class LoginUserController {

    @Autowired
    private Login business;

    @Autowired
    private ResponseUtils responseUtils;

    @ApiOperation(value = "Login the user")
    @Transactional(readOnly = true)
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public Response login(@RequestBody @Valid UserLoginDTO userLoginDTO, HttpServletRequest request) {
        userLoginDTO.setUsername(userLoginDTO.getUsername().trim());
        return responseUtils.returnObject(business.login(userLoginDTO));
    }

    @ApiOperation(value = "Return information from logged user")
    @Transactional(readOnly = true)
    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public Response getLoggedUser() {
        return responseUtils.returnObject(business.getInfoUserLogged());
    }

    @ApiOperation(value = "Return information from some user through id")
    @Transactional(readOnly = true)
    @RequestMapping(value = "/user/{id}", method = RequestMethod.GET)
    public SystemUser getUserById(@PathVariable("id") Long id) {
        br.com.agendasus.auth.v1.domain.model.UserLogin user = business.getById(id);
        business.checkAuthorization(user);
        return new SystemUser(user);
    }

    @ApiOperation(value = "Check if the header token is valid")
    @Transactional(readOnly = true)
    @RequestMapping(value = "/check/token", method = RequestMethod.POST)
    public SystemUser checkToken() {
        return business.checkHeaderToken();
    }

}
