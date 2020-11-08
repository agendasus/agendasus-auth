package br.com.agendasus.auth.v1.domain.model;

import br.com.agendasus.auth.v1.infrastructure.enumeration.UserType;
import br.com.agendasus.auth.v1.infrastructure.system.StringJsonUserType;
import org.hibernate.annotations.Type;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@Entity(name = "UserLogin")
@TypeDefs( {@TypeDef( name= "StringJsonObject", typeClass = StringJsonUserType.class)})
public class UserLogin implements Serializable, Cloneable {

	private static final long serialVersionUID = 7074328024759037929L;

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "name", length = 250, nullable = false)
	private String name;

	@Column(name = "login", nullable = false, unique = true, length = 150, columnDefinition = "varchar(150) default ' '")
	private String login;

	@Column(name = "password", nullable = false, length = 150, columnDefinition = "varchar(150) default ' '")
	private String password;

	@Column(name = "user_type", nullable = false, columnDefinition = "varchar(20) default 'ESTABLISHMENT'")
	@Enumerated(value = EnumType.STRING)
	private UserType userType;

	@Column(name = "is_active", nullable = false)
	private Boolean isActive;

	@Column(name = "hash_password_recovery", length = 200)
	private String hashPasswordRecovery;

	@Column(name = "permissions", columnDefinition = "jsonb")
	@Type(type = "StringJsonObject")
	private String permissions;

	public boolean isPatient() {
		return UserType.PATIENT.equals(userType);
	}

	public boolean isEstablishment() {
		return UserType.ESTABLISHMENT.equals(userType);
	}

	public boolean isAdmin() {
		return UserType.ADMIN.equals(userType);
	}

	public UserLogin getClone() {
		try {
			return (UserLogin) super.clone();
		} catch (CloneNotSupportedException e) {
			System.out.println("Cloning not allowed.");
			return this;
		}
	}

}
