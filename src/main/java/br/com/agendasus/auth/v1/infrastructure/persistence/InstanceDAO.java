package br.com.agendasus.auth.v1.infrastructure.persistence;

import br.com.agendasus.auth.v1.infrastructure.system.EntityManagerConfig;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.lang.reflect.ParameterizedType;

public abstract class InstanceDAO<T> {

	public EntityManager getEntityManager() {
		return EntityManagerConfig.entityManager;
	}

	@SuppressWarnings("unchecked")
	public Class<T> getClazz() {
		return (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
	}

	public T findById(Object id) {
		return getEntityManager().find(getClazz(), id);
	}

	@SuppressWarnings("hiding")
	public <T> T findById(Object id, Class<T> clazz) {
		return getEntityManager().find(clazz, id);
	}
	
	
	public T persist(T t) {
		getEntityManager().persist(t);
		return t;
	}

	public void remove(Object obj) {
		obj = getEntityManager().merge(obj);
		getEntityManager().remove(obj);
		getEntityManager().flush();
	}
	
	@Transactional(rollbackFor = {Exception.class, Throwable.class}, timeout = 120, propagation = Propagation.REQUIRED)
	public T merge(T t) {
		return getEntityManager().merge(t);
	}

}
