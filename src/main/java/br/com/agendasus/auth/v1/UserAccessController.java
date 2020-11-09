package br.com.agendasus.auth.v1;

import br.com.agendasus.auth.v1.domain.usecase.UserManager;
import br.com.agendasus.auth.v1.dto.AccountDTO;
import br.com.agendasus.auth.v1.dto.UserDTO;
import br.com.agendasus.auth.v1.dto.PasswordDTO;
import br.com.agendasus.auth.v1.domain.model.UserLogin;
import br.com.agendasus.auth.v1.infrastructure.response.Response;
import br.com.agendasus.auth.v1.infrastructure.response.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController("userAccessController")
public class UserAccessController {

    @Autowired
    private UserManager business;

    @Autowired
    private ResponseUtils responseUtils;


    @Transactional(readOnly = true)
    @PreAuthorize("hasAuthority('MANAGE_USER') OR hasAuthority('ADMIN')")
    @RequestMapping(value = "/user/{userId}/userlogin/{id}", method = RequestMethod.GET)
    public Response get(@PathVariable("userId") Long userId, @PathVariable("id") Long id) {
        UserLogin user = business.getById(userId);
        return responseUtils.returnObject(new UserDTO(business.get(user, id)));
    }

    @Transactional
    @RequestMapping(value = "/user/userlogin", method = RequestMethod.POST)
    public Response create(@RequestBody @Valid UserDTO dto) {
        UserLogin user = dto.parse(new UserLogin());
        business.insertPatient(user);
        dto.setAttributes(user);
        return responseUtils.returnObject(dto, "success.generic.insert.female", "entity.subcontausuario");
    }

    @Transactional
    @PreAuthorize("hasAuthority('MANAGE_USER') OR hasAuthority('ADMIN')")
    @RequestMapping(value = "/user/{userId}/userlogin", method = RequestMethod.POST)
    public Response create(@PathVariable("userId") Long userId, @RequestBody @Valid UserDTO dto) {
        UserLogin userRequest = business.getById(userId);
        UserLogin user = dto.parse(new UserLogin());
        business.insert(userRequest, user);
        dto.setAttributes(user);
        return responseUtils.returnObject(dto, "success.generic.insert.female", "entity.subcontausuario");
    }

    @Transactional
    @PreAuthorize("hasAuthority('MANAGE_USER') OR hasAuthority('ADMIN')")
    @RequestMapping(value = "/user/{userId}/userlogin/{id}", method = RequestMethod.PUT)
    public Response edit(@PathVariable("userId") Long userId, @PathVariable("id") Long id, @RequestBody @Valid UserDTO dto) {
        UserLogin userRequest = business.getById(userId);
        UserLogin user = business.getById(id);
        UserLogin userLoginOld = user.getClone();
        dto.parse(user);
        business.update(userRequest, user, userLoginOld);
        dto.setAttributes(user);
        return responseUtils.returnObject(dto, "success.generic.update.female", "entity.subcontausuario");
    }

    @Transactional
    @PreAuthorize("hasAuthority('MANAGE_USER') OR hasAuthority('ADMIN')")
    @RequestMapping(value = "/user/{userId}/userlogin/{id}", method = RequestMethod.DELETE)
    public Response remove(@PathVariable("userId") Long userId, @PathVariable("id") Long id) {
        UserLogin userRequest = business.getById(userId);
        UserLogin user = business.getById(id);
        business.remove(userRequest, user);
        return responseUtils.returnMessage("success.generic.delete.female", "entity.subcontausuario");
    }

    @Transactional
    @PreAuthorize("hasAuthority('MANAGE_USER') OR hasAuthority('ADMIN')")
    @RequestMapping(value = "/user/{userId}/userlogin/{id}/senha", method = RequestMethod.PUT)
    public Response setPassword(@PathVariable("userId") Long userId, @PathVariable("id") Long id, @RequestBody PasswordDTO senhaConfig) {
        UserLogin userRequest = business.getById(userId);
        UserLogin user = business.getById(id);
        business.changePassword(userRequest, user, senhaConfig.getPassword(), senhaConfig.getCurrentPassword());
        return responseUtils.returnMessage("success.usuario.senha.update");
    }

    @Transactional
    @RequestMapping(value = "/user/{userId}/userlogin/conta", method = RequestMethod.PUT)
    public Response updateAccountBasicInfo(@PathVariable("userId") Long userId, @RequestBody @Valid AccountDTO dadosConta) {
        UserLogin userRequest = business.getById(userId);
        UserLogin userOld = userRequest.getClone();
        dadosConta.parse(userRequest);
        userRequest.setPassword(null);
        business.update(userRequest, userRequest, userOld);
        return responseUtils.returnMessage("success.usuario.dados.conta.update");
    }

}
