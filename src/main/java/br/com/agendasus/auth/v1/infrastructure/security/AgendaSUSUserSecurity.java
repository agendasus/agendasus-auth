package br.com.agendasus.auth.v1.infrastructure.security;

import br.com.agendasus.auth.v1.domain.model.UserLogin;
import lombok.Getter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.GrantedAuthority;
import java.util.Collection;

@Getter
public class AgendaSUSUserSecurity extends User {

	private static final long serialVersionUID = 8608059049820898197L;

	private UserLogin user;
	private Long id;
	private String login;
	private String name;

	public AgendaSUSUserSecurity(UserLogin user, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) {
		super(user.getLogin(), user.getPassword(), user.getIsActive(), accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
		this.user = user;
		this.id = user.getId();
		this.login = user.getLogin();
		this.name = user.getName();
	}

}
