package br.com.agendasus.auth.v1.infrastructure.persistence;

import br.com.agendasus.auth.v1.domain.model.UserLogin;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.NoResultException;
import javax.persistence.Query;

@Repository("userLoginDAO")
public class UserLoginDAO extends InstanceDAO<UserLogin> {

    public UserLogin getUserLoginByLogin(String login) {
        UserLogin user = null;
        try{
            StringBuilder hql = new StringBuilder();
            hql.append(" SELECT s FROM UserLogin s ");
            hql.append(" WHERE s.login = :login ");
            Query query = getEntityManager().createQuery(hql.toString());
            query.setParameter("login", login);
            user = (UserLogin) query.getSingleResult();
        } catch(NoResultException e){ }
        return user;
    }

    public UserLogin getUserByHashFromPasswordRecovery(String recoveryPasswordHash) {
        UserLogin user = null;
        try{
            StringBuilder hql = new StringBuilder();
            hql.append(" SELECT e FROM UserLogin e ");
            hql.append(" WHERE e.hashPasswordRecovery = :hashPasswordRecovery ");
            Query query = getEntityManager().createQuery(hql.toString());
            query.setParameter("hashPasswordRecovery", recoveryPasswordHash);
            user = (UserLogin) query.getSingleResult();
        } catch(NoResultException e){ }
        return user;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public boolean existLogin(UserLogin user) {
        StringBuilder hql = new StringBuilder();
        hql.append(" SELECT count(s) > 0 FROM UserLogin s ");
        hql.append(" WHERE s.login LIKE :login ");
        if (user.getId() != null){
            hql.append(" AND s.id != :id ");
        }
        Query query = getEntityManager().createQuery(hql.toString());
        query.setParameter("login", user.getLogin());
        if (user.getId() != null) {
            query.setParameter("id", user.getId());
        }
        return (Boolean) query.getSingleResult();
    }

    public void updateIsActiveFlag(UserLogin user) {
        StringBuilder hql = new StringBuilder();
        hql.append(" UPDATE UserLogin lu SET isActive = :flag");
        hql.append(" WHERE lu.usuarioMaster = :usuarioMaster ");
        Query query = getEntityManager().createQuery(hql.toString());
        query.setParameter("flag", user.getIsActive());
        query.setParameter("usuarioMaster", user);
        query.executeUpdate();
    }

}
