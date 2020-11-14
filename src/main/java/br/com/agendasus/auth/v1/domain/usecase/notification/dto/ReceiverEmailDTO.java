package br.com.agendasus.auth.v1.domain.usecase.notification.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReceiverEmailDTO {

    private String name;

    private String address;


    public ReceiverEmailDTO() {
        super();
    }

    public ReceiverEmailDTO(String name, String address) {
        this.name = name;
        this.address = address;
    }

}
