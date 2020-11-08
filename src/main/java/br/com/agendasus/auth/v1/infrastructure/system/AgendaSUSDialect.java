package br.com.agendasus.auth.v1.infrastructure.system;

import org.hibernate.dialect.PostgreSQL94Dialect;
import org.hibernate.type.StandardBasicTypes;

import java.sql.Types;

public class AgendaSUSDialect extends PostgreSQL94Dialect {

    public AgendaSUSDialect() {
        registerColumnType(Types.JAVA_OBJECT, "json");
        registerHibernateType(Types.BIGINT, StandardBasicTypes.LONG.getName() );
    }

}
