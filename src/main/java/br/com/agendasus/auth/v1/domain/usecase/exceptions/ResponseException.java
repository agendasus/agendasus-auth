package br.com.agendasus.auth.v1.domain.usecase.exceptions;

import lombok.Getter;

import java.util.List;

@Getter
public class ResponseException extends RuntimeException {

	private static final long serialVersionUID = 5242151978325562763L;

	private String message;
	private String[] messageParams;
	private List<ErrorFieldException> errorFieldExceptions;


	public ResponseException(String message, String... params) {
		this.message = message;
		this.messageParams = params;
	}

	public ResponseException(List<ErrorFieldException> errorFieldExceptions) {
		this.message = "validacao";
		this.errorFieldExceptions = errorFieldExceptions;
	}

}
