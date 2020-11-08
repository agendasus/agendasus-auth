package br.com.agendasus.auth.v1.infrastructure.system;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service("messageSystem")
public class MessageSystem {
	
	@Cacheable("getMessage" )
	public String getMessage(String message, String... arg){
		String messageTranslated = null;
		try {
			ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
			messageSource.setBasenames("messages/messages");
			messageSource.setDefaultEncoding("UTF-8");
			messageSource.setCacheSeconds(1);
			Locale locale = LocaleContextHolder.getLocale();
			messageTranslated = messageSource.getMessage(message, arg, locale);
		} catch (NoSuchMessageException e) {
			messageTranslated = message;
		}
		return messageTranslated;
	}
	
	@Cacheable("formatMessage")
	public String formatMessage(String error){
		if(error == null) {
			return null;
		}
		if(error.contains("#")){
			String[] args = error.split("#");
			String message = "";
			String[] attrs = new String[args.length-1];
			boolean first = true;
			for(int i=0;i<args.length;i++) {
				if(first){
					message = args[i];
					first = false;
				} else {
					attrs[i-1] = checkMessageParams(args[i]);
				}
			}
			return getMessage(message, attrs);
		} else {
			return getMessage(error);
		}
	}

	@Cacheable("formatMessageWithParams")
	public String formatMessage(String message, String... params){
		if(params == null || params.length == 0) {
			return getMessage(message);
		}
		String[] paramLocale = new String[params.length];
		for(int i=0; i<params.length; i++){
			paramLocale[i] = getMessage(params[i]);
		}
		return getMessage(message, paramLocale);
	}
	
	private String checkMessageParams(String variavel){
		if(variavel.startsWith("{") && variavel.endsWith("}")){
			variavel = variavel.substring(1, variavel.length()-1);
			return getMessage(variavel);
		} else {
			return variavel;
		}
	}
	
}
