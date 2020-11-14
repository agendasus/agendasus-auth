package br.com.agendasus.auth.v1.domain.usecase.notification.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccessRecoveryEmailDTO extends EmailDTO {

    private String requestIP;

    private String linkPasswordRecovery;

    private String linkInvalidateRecovery;

}
