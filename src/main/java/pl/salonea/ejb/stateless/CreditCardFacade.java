package pl.salonea.ejb.stateless;

import pl.salonea.ejb.interfaces.CreditCardFacadeInterface;
import pl.salonea.entities.CreditCard;
import pl.salonea.enums.CreditCardType;

import javax.ejb.LocalBean;
import javax.ejb.Stateful;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.Date;
import java.util.List;

/**
 * Created by michzio on 18/07/2015.
 */
@Stateless
@LocalBean
public class CreditCardFacade extends AbstractFacade<CreditCard> implements CreditCardFacadeInterface.Local, CreditCardFacadeInterface.Remote {

    @Inject
    private EntityManager em;

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    public CreditCardFacade() {
        super(CreditCard.class);
    }


    @Override
    public List<CreditCard> findByType(CreditCardType cardType) {
        return findByType(cardType, null, null);
    }

    @Override
    public List<CreditCard> findByType(CreditCardType cardType, Integer start, Integer offset) {

        TypedQuery<CreditCard> query = getEntityManager().createNamedQuery(CreditCard.FIND_BY_TYPE, CreditCard.class);
        query.setParameter("card_type", cardType);
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public List<CreditCard> findExpired() {
       return findExpired(null, null);
    }

    @Override
    public List<CreditCard> findExpired(Integer start, Integer offset) {

        TypedQuery<CreditCard> query = getEntityManager().createNamedQuery(CreditCard.FIND_EXPIRED, CreditCard.class);
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public List<CreditCard> findNotExpired() {
        return findNotExpired(null, null);
    }

    @Override
    public List<CreditCard> findNotExpired(Integer start, Integer offset) {

        TypedQuery<CreditCard> query = getEntityManager().createNamedQuery(CreditCard.FIND_NOT_EXPIRED, CreditCard.class);
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public List<CreditCard> findExpirationDateAfter(Date date) {
        return findExpirationDateAfter(date, null, null);
    }

    @Override
    public List<CreditCard> findExpirationDateAfter(Date date, Integer start, Integer offset) {

        TypedQuery<CreditCard> query = getEntityManager().createNamedQuery(CreditCard.FIND_EXPIRATION_DATE_AFTER, CreditCard.class);
        query.setParameter("date", date);
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public List<CreditCard> findExpirationDateBefore(Date date) {
        return findExpirationDateBefore(date, null, null);
    }

    @Override
    public List<CreditCard> findExpirationDateBefore(Date date, Integer start, Integer offset) {

        TypedQuery<CreditCard> query = getEntityManager().createNamedQuery(CreditCard.FIND_EXPIRATION_DATE_BEFORE, CreditCard.class);
        query.setParameter("date", date);
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public List<CreditCard> findExpirationDateBetween(Date startDate, Date endDate) {
        return findExpirationDateBetween(startDate, endDate, null, null);
    }

    @Override
    public List<CreditCard> findExpirationDateBetween(Date startDate, Date endDate, Integer start, Integer offset) {

        TypedQuery<CreditCard> query = getEntityManager().createNamedQuery(CreditCard.FIND_EXPIRATION_DATE_BETWEEN, CreditCard.class);
        query.setParameter("start_date", startDate);
        query.setParameter("end_date", endDate);
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public Integer deleteWithExpirationDateBefore(Date date) {

        Query query = getEntityManager().createNamedQuery(CreditCard.DELETE_WITH_EXPIRATION_DATE_BEFORE);
        query.setParameter("date", date);
        return query.executeUpdate();
    }

    @Override
    public Integer deleteWithExpirationDateAfter(Date date) {

        Query query = getEntityManager().createNamedQuery(CreditCard.DELETE_WITH_EXPIRATION_DATE_AFTER);
        query.setParameter("date", date);
        return query.executeUpdate();
    }

    @Override
    public Integer deleteWithExpirationDateBetween(Date startDate, Date endDate) {

        Query query = getEntityManager().createNamedQuery(CreditCard.DELETE_WITH_EXPIRATION_DATE_BETWEEN);
        query.setParameter("start_date", startDate);
        query.setParameter("end_date", endDate);
        return query.executeUpdate();
    }

    @Override
    public Integer deleteExpired() {

        Query query = getEntityManager().createNamedQuery(CreditCard.DELETE_EXPIRED);
        return query.executeUpdate();
    }

    @Override
    public Integer deleteWithType(CreditCardType cardType) {

        Query query = getEntityManager().createNamedQuery(CreditCard.DELETE_WITH_TYPE);
        query.setParameter("card_type", cardType);
        return query.executeUpdate();
    }
}