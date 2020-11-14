package br.com.agendasus.auth.v1.domain.usecase.notification.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public abstract class EmailDTO {

    private String subject;

    private String message;

    private List<ReceiverEmailDTO> receivers;


    public EmailDTO() {
        super();
        this.receivers = new ArrayList<>();
    }

}
