package pl.salonea.ejb.stateless;

import pl.salonea.ejb.interfaces.UserAccountFacadeInterface;
import pl.salonea.entities.UserAccount;
import pl.salonea.entities.UserAccount_;
import pl.salonea.utils.Period;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
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

    @Override
    public List<UserAccount> findAllNotActivated() {
        return findAllNotActivated(null, null);
    }

    @Override
    public List<UserAccount> findAllNotActivated(Integer start, Integer limit) {

        TypedQuery<UserAccount> query = getEntityManager().createNamedQuery(UserAccount.FIND_ALL_NOT_ACTIVATED, UserAccount.class);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<UserAccount> findAllActivated() {
        return findAllActivated(null, null);
    }

    @Override
    public List<UserAccount> findAllActivated(Integer start, Integer limit) {

        TypedQuery<UserAccount> query = getEntityManager().createNamedQuery(UserAccount.FIND_ALL_ACTIVATED, UserAccount.class);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<UserAccount> findByEmail(String email) {
        return findByEmail(email, null, null);
    }

    @Override
    public List<UserAccount> findByEmail(String email, Integer start, Integer limit) {

        TypedQuery<UserAccount> query = getEntityManager().createNamedQuery(UserAccount.FIND_BY_EMAIL, UserAccount.class);
        query.setParameter("email", "%" + email + "%");
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<UserAccount> findByLogin(String login) {
        return findByLogin(login, null, null);
    }

    @Override
    public List<UserAccount> findByLogin(String login, Integer start, Integer limit) {

        TypedQuery<UserAccount> query = getEntityManager().createNamedQuery(UserAccount.FIND_BY_LOGIN, UserAccount.class);
        query.setParameter("login", "%" + login + "%");
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<UserAccount> findByAccountType(String accountType) {
        return findByAccountType(accountType, null, null);
    }

    @Override
    public List<UserAccount> findByAccountType(String accountType, Integer start, Integer limit) {

        TypedQuery<UserAccount> query = getEntityManager().createNamedQuery(UserAccount.FIND_BY_ACCOUNT_TYPE, UserAccount.class);
        query.setParameter("account_type", accountType);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    public List<UserAccount> findCreatedBetween(Date startDate, Date endDate) {
        return findCreatedBetween(startDate, endDate, null, null);
    }

    @Override
    public List<UserAccount> findCreatedBetween(Date startDate, Date endDate, Integer start, Integer limit) {

        TypedQuery<UserAccount> query = getEntityManager().createNamedQuery(UserAccount.FIND_CREATED_BETWEEN, UserAccount.class);
        query.setParameter("start_date", startDate);
        query.setParameter("end_date", endDate);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<UserAccount> findLastLoggedBetween(Date startDate, Date endDate) {
        return findLastLoggedBetween(startDate, endDate, null, null);
    }

    @Override
    public List<UserAccount> findLastLoggedBetween(Date startDate, Date endDate, Integer start, Integer limit) {

        TypedQuery<UserAccount> query = getEntityManager().createNamedQuery(UserAccount.FIND_LAST_LOGGED_BETWEEN, UserAccount.class);
        query.setParameter("start_date", startDate);
        query.setParameter("end_date", endDate);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return  query.getResultList();
    }

    @Override
    public List<UserAccount> findLastFailedLoginBetween(Date startDate, Date endDate) {
        return findLastFailedLoginBetween(startDate, endDate, null, null);
    }

    @Override
    public List<UserAccount> findLastFailedLoginBetween(Date startDate, Date endDate, Integer start, Integer limit) {

        TypedQuery<UserAccount> query = getEntityManager().createNamedQuery(UserAccount.FIND_LAST_FAILED_LOGIN_BETWEEN, UserAccount.class);
        query.setParameter("start_date", startDate);
        query.setParameter("end_date", endDate);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<UserAccount> findByMultipleCriteria(String login, String email, Boolean activated, Period createdBetween, Period lastLoggedBetween, Period lastFailedLoginBetween) {
        return findByMultipleCriteria(login, email, activated, createdBetween, lastLoggedBetween, lastFailedLoginBetween, null, null);
    }

    @Override
    public List<UserAccount> findByMultipleCriteria(String login, String email, Boolean activated, Period createdBetween, Period lastLoggedBetween, Period lastFailedLoginBetween, Integer start, Integer limit) {

        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<UserAccount> criteriaQuery = criteriaBuilder.createQuery(UserAccount.class);
        // FROM
        Root<UserAccount> userAccount = criteriaQuery.from(UserAccount.class);
        // SELECT
        criteriaQuery.select(userAccount);

        // WHERE PREDICATES
        List<Predicate> predicates = new java.util.ArrayList<>();

        if(login != null) {
            predicates.add( criteriaBuilder.like(userAccount.get(UserAccount_.login), "%" + login + "%") );
        }

        if(email != null) {
            predicates.add( criteriaBuilder.like(userAccount.get(UserAccount_.email), "%" + email + "%") );
        }

        if(activated != null) {
            if(activated) {
                // get only activated user accounts i.e. activationCode is null
                predicates.add( criteriaBuilder.isNull(userAccount.get(UserAccount_.activationCode)) );
            } else {
                // get only not activated user accounts i.e. activationCode is not null
                predicates.add( criteriaBuilder.isNotNull(userAccount.get(UserAccount_.activationCode)) );
            }
        } // otherwise get both activated and not activated user accounts i.e. no predicate is needed

        if(createdBetween != null) {
            // limit user accounts to registration date between specified period of time
            if(createdBetween.getStartTime() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(userAccount.<Date>get(UserAccount_.registrationDate), new Date(createdBetween.getStartTime().getTime())) );
            }

            if(createdBetween.getEndTime() != null)
                predicates.add( criteriaBuilder.lessThanOrEqualTo(userAccount.<Date>get(UserAccount_.registrationDate), new Date(createdBetween.getEndTime().getTime())) );
        }

        if(lastLoggedBetween != null) {
            // limit user accounts to last logged between specified period of time
            if(lastLoggedBetween.getStartTime() != null)
                predicates.add( criteriaBuilder.greaterThanOrEqualTo(userAccount.get(UserAccount_.lastLogged), new Date(lastLoggedBetween.getStartTime().getTime())) );

            if(lastLoggedBetween.getEndTime() != null)
                predicates.add( criteriaBuilder.lessThanOrEqualTo(userAccount.get(UserAccount_.lastLogged), new Date(lastLoggedBetween.getEndTime().getTime())) );
        }

        if(lastFailedLoginBetween != null) {
            // limit user accounts to last failed login between specified period of time
            if(lastFailedLoginBetween.getStartTime() != null)
                predicates.add( criteriaBuilder.greaterThanOrEqualTo(userAccount.get(UserAccount_.lastFailedLogin), new Date(lastFailedLoginBetween.getStartTime().getTime())) );

            if(lastFailedLoginBetween.getEndTime() != null)
                predicates.add( criteriaBuilder.lessThanOrEqualTo(userAccount.get(UserAccount_.lastFailedLogin), new Date(lastFailedLoginBetween.getEndTime().getTime())) );
        }

        // WHERE predicate1 AND predicate2 AND ... AND predicateN
        criteriaQuery.where(predicates.toArray(new Predicate[] { }));

        TypedQuery<UserAccount> query = getEntityManager().createQuery(criteriaQuery);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }

        return query.getResultList();
    }

    @Override
    public Integer deleteOldNotActivated(Date youngestDate) {

        Query query = getEntityManager().createNamedQuery(UserAccount.DELETE_OLD_NOT_ACTIVATED);
        query.setParameter("youngest_date", youngestDate);
        return query.executeUpdate();
    }

    @Override
    public Integer updateActivateAll() {

        Query query = getEntityManager().createNamedQuery(UserAccount.UPDATE_ACTIVATE_ALL);
        return query.executeUpdate();
    }
}
