package br.com.agendasus.auth.v1.infrastructure.security;

/**
 * @author AgendaSUS - Vinícius Roggia Gomes
 * Contém todas as constantes necessárias para a autenticação JWT
 */
public class Constants {

    public static final String SIGNING_KEY = "AS;Secret.2020-Application;AS";
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    public static final String AUTHORITIES_KEY = "scopes";

}
