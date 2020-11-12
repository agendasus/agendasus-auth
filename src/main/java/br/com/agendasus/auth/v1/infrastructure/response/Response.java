package br.com.agendasus.auth.v1.infrastructure.response;

import br.com.agendasus.auth.v1.infrastructure.enumeration.StatusReturn;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@Setter
@Getter
public class Response implements Serializable {

	private static final long serialVersionUID = -4369130483321841391L;

	@JsonInclude(Include.NON_NULL) private StatusReturn status;
	@JsonInclude(Include.NON_NULL) private String message;
	@JsonInclude(Include.NON_NULL) private String[] messageParams;
	@JsonInclude(Include.NON_NULL) private List<java.lang.Error> errors;
	@JsonInclude(Include.NON_NULL) private Object response;
	
}
