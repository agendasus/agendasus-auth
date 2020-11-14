package br.com.agendasus.auth.v1.domain.usecase.notification;

import br.com.agendasus.auth.v1.domain.usecase.notification.dto.AccessRecoveryEmailDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient("asnotification")
public interface NotificationClient {

    @RequestMapping(value = "/email/system/access-recovery", method = RequestMethod.POST)
    Boolean sendAccessRecoveryEmail(@RequestHeader("Authorization") String authHeader, @RequestBody AccessRecoveryEmailDTO emailDTO);

}
