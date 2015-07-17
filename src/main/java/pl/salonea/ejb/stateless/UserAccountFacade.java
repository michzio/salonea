package pl.salonea.ejb.stateless;

import pl.salonea.ejb.interfaces.UserAccountFacadeInterface;
import pl.salonea.entities.UserAccount;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.Date;
import java.util.List;

/**
 * Created by michzio on 15/07/2015.
 */
@Stateless
@LocalBean
public class UserAccountFacade extends AbstractFacade<UserAccount> implements UserAccountFacadeInterface.Remote, UserAccountFacadeInterface.Local {

    @Inject
    public EntityManager em;

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    public UserAccountFacade() {
        super(UserAccount.class);
    }

    public List<UserAccount> findAllNotActivated() {
        return findAllNotActivated(null, null);
    }

    public List<UserAccount> findAllNotActivated(Integer start, Integer offset) {

        TypedQuery<UserAccount> query = getEntityManager().createNamedQuery(UserAccount.FIND_ALL_NOT_ACTIVATED, UserAccount.class);
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    public List<UserAccount> findAllActivated() {
        return findAllActivated(null, null);
    }

    public List<UserAccount> findAllActivated(Integer start, Integer offset) {

        TypedQuery<UserAccount> query = getEntityManager().createNamedQuery(UserAccount.FIND_ALL_ACTIVATED, UserAccount.class);
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    public List<UserAccount> findByEmail(String email) {
        return findByEmail(email, null, null);
    }

    public List<UserAccount> findByEmail(String email, Integer start, Integer offset) {

        TypedQuery<UserAccount> query = getEntityManager().createNamedQuery(UserAccount.FIND_BY_EMAIL, UserAccount.class);
        query.setParameter("email", "%" + email + "%");
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    public List<UserAccount> findByLogin(String login) {
        return findByLogin(login, null, null);
    }

    public List<UserAccount> findByLogin(String login, Integer start, Integer offset) {

        TypedQuery<UserAccount> query = getEntityManager().createNamedQuery(UserAccount.FIND_BY_LOGIN, UserAccount.class);
        query.setParameter("login", "%" + login + "%");
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    public List<UserAccount> findCreatedBetween(Date startDate, Date endDate) {
        return findCreatedBetween(startDate, endDate, null, null);
    }

    public List<UserAccount> findCreatedBetween(Date startDate, Date endDate, Integer start, Integer offset) {

        TypedQuery<UserAccount> query = getEntityManager().createNamedQuery(UserAccount.FIND_CREATED_BETWEEN, UserAccount.class);
        query.setParameter("start_date", startDate);
        query.setParameter("end_date", endDate);
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    public List<UserAccount> findLastLoggedBetween(Date startDate, Date endDate) {
        return findLastLoggedBetween(startDate, endDate, null, null);
    }

    public List<UserAccount> findLastLoggedBetween(Date startDate, Date endDate, Integer start, Integer offset) {

        TypedQuery<UserAccount> query = getEntityManager().createNamedQuery(UserAccount.FIND_LAST_LOGGED_BETWEEN, UserAccount.class);
        query.setParameter("start_date", startDate);
        query.setParameter("end_date", endDate);
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return  query.getResultList();
    }

    public List<UserAccount> findLastFailedLoginBetween(Date startDate, Date endDate) {
        return findLastFailedLoginBetween(startDate, endDate, null, null);
    }

    public List<UserAccount> findLastFailedLoginBetween(Date startDate, Date endDate, Integer start, Integer offset) {

        TypedQuery<UserAccount> query = getEntityManager().createNamedQuery(UserAccount.FIND_LAST_FAILED_LOGIN_BETWEEN, UserAccount.class);
        query.setParameter("start_date", startDate);
        query.setParameter("end_date", endDate);
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    public Integer deleteOldNotActivated(Date oldestDate) {

        Query query = getEntityManager().createNamedQuery(UserAccount.DELETE_OLD_NOT_ACTIVATED);
        query.setParameter("oldest_date", oldestDate);
        return query.executeUpdate();
    }

    public Integer updateActivateAll() {

        Query query = getEntityManager().createNamedQuery(UserAccount.UPDATE_ACTIVATE_ALL);
        return query.executeUpdate();
    }
}
