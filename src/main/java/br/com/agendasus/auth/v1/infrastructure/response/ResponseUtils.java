package br.com.agendasus.auth.v1.infrastructure.response;

import br.com.agendasus.auth.v1.infrastructure.enumeration.StatusReturn;
import br.com.agendasus.auth.v1.infrastructure.system.MessageSystem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class ResponseUtils {

	@Autowired
	private MessageSystem messageSystem;

	public Response returnObject(Object object) {
		Response response = new Response();
		response.setStatus(StatusReturn.SUCCESS);
		response.setResponse(object);
		response.setMessage("");
		return response;
	}
	
	public Response returnObject(Object object, String message, String... params) {
		Response response = new Response();
		response.setStatus(StatusReturn.SUCCESS);
		response.setResponse(object);
		response.setMessage(messageSystem.formatMessage(message, params));
		return response;
	}
	
	public Response returnMessage(String message, String... params) {
		Response response = new Response();
		response.setStatus(StatusReturn.SUCCESS);
		response.setMessage(messageSystem.formatMessage(message, params));
		return response;
	}

}
