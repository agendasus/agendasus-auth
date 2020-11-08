package br.com.agendasus.auth.v1.domain.usecase.exceptions;

import br.com.agendasus.auth.v1.infrastructure.response.Response;
import br.com.agendasus.auth.v1.infrastructure.system.MessageSystem;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import io.jsonwebtoken.MalformedJwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.CannotCreateTransactionException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletRequest;

import static br.com.agendasus.auth.v1.infrastructure.enumeration.StatusReturn.ERROR;
import static org.springframework.http.HttpStatus.*;

@RestController
@ControllerAdvice
public class ResponseExceptionHandler {

	private Logger logger = LoggerFactory.getLogger(ResponseExceptionHandler.class);

	@Autowired
	private MessageSystem messageSystem;


	@ResponseStatus(BAD_REQUEST)
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public Response handleNotReadableException(RuntimeException e, WebRequest request) {
		logger.error("HTTP Message Readable", e);
		HttpMessageNotReadableException exception = (HttpMessageNotReadableException) e;
		Response response = new Response();
		response.setStatus(ERROR);
		if (exception.getCause() instanceof InvalidFormatException) {
			InvalidFormatException invalid = (InvalidFormatException) exception.getCause();
			String value = invalid.getValue().toString();
			response.setMessage(messageSystem.getMessage("system.error.converter.value", value));
		} else {
			response.setMessage(messageSystem.getMessage("system.error.converter.value"));
		}
		return response;
	}

	@ResponseStatus(BAD_REQUEST)
	@ExceptionHandler({CannotCreateTransactionException.class})
	public Response handleCannotCreateTransactionException(WebRequest request, RuntimeException e) {
		logger.error("Cannot create transaction", e);
		Response response = new Response();
		response.setStatus(ERROR);
		response.setMessage(messageSystem.getMessage("system.time.is.over"));
		response.setResponse(e);
		return response;
	}

	@ResponseStatus(FORBIDDEN)
	@ExceptionHandler({AccessDeniedException.class, AuthenticationServiceException.class})
	public Response handleAccessDeniedException(HttpServletRequest req, RuntimeException e, WebRequest request) {
		logger.error("System deny access", e);
		Response response = new Response();
		response.setStatus(ERROR);
		response.setMessage(messageSystem.getMessage("system.deny.access"));
		return response;
	}

	@ResponseStatus(UNAUTHORIZED)
	@ExceptionHandler({AuthenticationException.class, BadCredentialsException.class, UsernameNotFoundException.class})
	public Response handleLoginFailureException(HttpServletRequest req, RuntimeException e, WebRequest request) {
		Response response = new Response();
		response.setStatus(ERROR);
		if(e instanceof UsernameNotFoundException || e instanceof BadCredentialsException) {
			response.setMessage(messageSystem.getMessage("error.wrong.username.or.password"));
		} else {
			AuthenticationException exception = (AuthenticationException) e;
			if(exception.getReturnGenericMessage()) {
				logger.error(exception.getMessage(), e);
				response.setMessage(messageSystem.getMessage("error.authentication.user"));
			} else {
				response.setMessage(messageSystem.getMessage(e.getMessage()));
			}
		}
		return response;
	}

	@ResponseStatus(BAD_REQUEST)
	@ExceptionHandler({MalformedJwtException.class, AuthTokenException.class})
	public Response handleMalformedJwtException(HttpServletRequest req, RuntimeException e, WebRequest request) {
		if(e instanceof AuthTokenException) {
			logger.warn(e.getMessage());
		} else {
			logger.warn("Invalid JWT token");
		}
		Response response = new Response();
		response.setStatus(ERROR);
		response.setMessage(messageSystem.getMessage("error.invalid.token"));
		return response;
	}

	@ResponseStatus(INTERNAL_SERVER_ERROR)
	@ExceptionHandler({RuntimeException.class, Exception.class})
	public Response handleGenericException(HttpServletRequest req, RuntimeException e, WebRequest request) {
		logger.error("Internal server error", e);
		Response response = new Response();
		response.setStatus(ERROR);
		response.setMessage(messageSystem.getMessage("system.internal.error"));
		response.setResponse(e);
		return response;
	}

}
