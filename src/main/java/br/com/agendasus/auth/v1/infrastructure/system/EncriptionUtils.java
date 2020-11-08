package br.com.agendasus.auth.v1.infrastructure.system;

import br.com.agendasus.auth.v1.domain.usecase.exceptions.ResponseException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class EncriptionUtils {

	public static String sha1Converter(String password) {
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		return passwordEncoder.encode(password);
	}
	
	public static boolean checkSha1Password(String passwordPlaintext, String storedHash) {
		boolean password_verified = false;

		if(null == storedHash || !storedHash.startsWith("$2a$")){
			throw new IllegalArgumentException("Invalid hash provided for comparison");
		}
		password_verified = BCrypt.checkpw(passwordPlaintext, storedHash);

		return(password_verified);
	}

	public static String convertMd5(String value) {
		String resultado = null;
		try {
			MessageDigest m = MessageDigest.getInstance("MD5");
			m.update(value.getBytes(), 0, value.length());
			resultado = new BigInteger(1, m.digest()).toString(16);
			while (resultado.length() < 32) {
				resultado = "0" + resultado;
			}
		} catch (NoSuchAlgorithmException e) {
			throw new ResponseException("utils.erro.criar.md5");
		}
		return resultado;
	}
	
	public static String convertMd5(byte[] value) {
		String resultado = null;
		try {
			MessageDigest m = MessageDigest.getInstance("MD5");
			m.update(value, 0, value.length);
			resultado = new BigInteger(1, m.digest()).toString(16);
			while (resultado.length() < 32) {
				resultado = "0" + resultado;
			}
		} catch (NoSuchAlgorithmException e) {
			throw new ResponseException("utils.erro.criar.md5");
		}
		return resultado;
	}

}
