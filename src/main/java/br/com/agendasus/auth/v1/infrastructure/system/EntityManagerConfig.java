package br.com.agendasus.auth.v1.infrastructure.system;

import org.springframework.context.annotation.Configuration;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Configuration
public class EntityManagerConfig {

    public static EntityManager entityManager;

    @PersistenceContext
    public void setEntityManager(EntityManager em){
        entityManager = em;
    }

}
