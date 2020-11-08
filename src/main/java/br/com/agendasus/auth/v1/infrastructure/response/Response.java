package br.com.agendasus.auth.v1.infrastructure.response;

import br.com.agendasus.auth.v1.infrastructure.enumeration.StatusReturn;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Setter
@Getter
public class Response implements Serializable {

	private static final long serialVersionUID = -4369130483321841391L;

	private StatusReturn status;
	private String message;
	private String[] messageParams;
	private List<java.lang.Error> errors;
	private Object response;
	
}
