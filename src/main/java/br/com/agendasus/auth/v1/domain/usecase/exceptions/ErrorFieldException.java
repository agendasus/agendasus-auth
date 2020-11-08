package br.com.agendasus.auth.v1.domain.usecase.exceptions;

import java.util.Arrays;
import java.util.Objects;

public class ErrorFieldException extends RuntimeException {

	private String field;
	private String message;
	private String[] paramsMessage;


	public ErrorFieldException(String field, String message, String... params) {
		super();
		this.field = field;
		this.message = message;
		this.paramsMessage = params;
	}

	public String getField() {
		return field;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String[] getParamsMessage() {
		return paramsMessage;
	}

	public void setParamsMessage(String[] paramsMessage) {
		this.paramsMessage = paramsMessage;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ErrorFieldException errorFieldException = (ErrorFieldException) o;
		return Objects.equals(field, errorFieldException.field) &&
				Objects.equals(message, errorFieldException.message) &&
				Arrays.equals(paramsMessage, errorFieldException.paramsMessage);
	}

	@Override
	public int hashCode() {
		int result = Objects.hash(field, message);
		result = 31 * result + Arrays.hashCode(paramsMessage);
		return result;
	}

}
