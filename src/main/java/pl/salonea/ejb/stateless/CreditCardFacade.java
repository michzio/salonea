package pl.salonea.ejb.stateless;

import pl.salonea.ejb.interfaces.CreditCardFacadeInterface;
import pl.salonea.entities.Client;
import pl.salonea.entities.Client_;
import pl.salonea.entities.CreditCard;
import pl.salonea.entities.CreditCard_;
import pl.salonea.entities.idclass.CreditCardId;
import pl.salonea.enums.CreditCardType;

import javax.ejb.LocalBean;
import javax.ejb.Stateful;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
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
    public CreditCard createForClient(Long clientId, CreditCard creditCard) {

        Client client = getEntityManager().find(Client.class, clientId);
        creditCard.setClient(client);
        return create(creditCard);
    }

    @Override
    public CreditCard update(CreditCardId creditCardId, CreditCard creditCard) {

        Client foundClient = getEntityManager().find(Client.class, creditCardId.getClient());
        creditCard.setClient(foundClient);
        creditCard.setCreditCardNumber(creditCardId.getCreditCardNumber());
        creditCard.setExpirationDate(creditCardId.getExpirationDate());

        return update(creditCard);
    }

    @Override
    public List<CreditCard> findByClient(Client client) {
        return findByClient(client, null, null);
    }

    @Override
    public List<CreditCard> findByClient(Client client, Integer start, Integer limit) {

        TypedQuery<CreditCard> query = getEntityManager().createNamedQuery(CreditCard.FIND_BY_CLIENT, CreditCard.class);
        query.setParameter("client", client);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<CreditCard> findByType(CreditCardType cardType) {
        return findByType(cardType, null, null);
    }

    @Override
    public List<CreditCard> findByType(CreditCardType cardType, Integer start, Integer limit) {

        TypedQuery<CreditCard> query = getEntityManager().createNamedQuery(CreditCard.FIND_BY_TYPE, CreditCard.class);
        query.setParameter("card_type", cardType);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<CreditCard> findByClientAndType(Client client, CreditCardType cardType) {
        return findByClientAndType(client, cardType, null, null);
    }

    @Override
    public List<CreditCard> findByClientAndType(Client client, CreditCardType cardType, Integer start, Integer limit) {

        TypedQuery<CreditCard> query = getEntityManager().createNamedQuery(CreditCard.FIND_BY_CLIENT_AND_TYPE, CreditCard.class);
        query.setParameter("client", client);
        query.setParameter("card_type", cardType);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<CreditCard> findExpired() {
       return findExpired(null, null);
    }

    @Override
    public List<CreditCard> findExpired(Integer start, Integer limit) {

        TypedQuery<CreditCard> query = getEntityManager().createNamedQuery(CreditCard.FIND_EXPIRED, CreditCard.class);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<CreditCard> findExpiredByClient(Client client) {
        return findExpiredByClient(client, null, null);
    }

    @Override
    public List<CreditCard> findExpiredByClient(Client client, Integer start, Integer limit) {

        TypedQuery<CreditCard> query = getEntityManager().createNamedQuery(CreditCard.FIND_EXPIRED_BY_CLIENT, CreditCard.class);
        query.setParameter("client", client);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<CreditCard> findNotExpired() {
        return findNotExpired(null, null);
    }

    @Override
    public List<CreditCard> findNotExpired(Integer start, Integer limit) {

        TypedQuery<CreditCard> query = getEntityManager().createNamedQuery(CreditCard.FIND_NOT_EXPIRED, CreditCard.class);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<CreditCard> findNotExpiredByClient(Client client) {
        return findNotExpiredByClient(client, null, null);
    }

    @Override
    public List<CreditCard> findNotExpiredByClient(Client client, Integer start, Integer limit) {

        TypedQuery<CreditCard> query = getEntityManager().createNamedQuery(CreditCard.FIND_NOT_EXPIRED_BY_CLIENT, CreditCard.class);
        query.setParameter("client", client);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<CreditCard> findExpirationDateAfter(Date date) {
        return findExpirationDateAfter(date, null, null);
    }

    @Override
    public List<CreditCard> findExpirationDateAfter(Date date, Integer start, Integer limit) {

        TypedQuery<CreditCard> query = getEntityManager().createNamedQuery(CreditCard.FIND_EXPIRATION_DATE_AFTER, CreditCard.class);
        query.setParameter("date", date);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<CreditCard> findExpirationDateAfterByClient(Date date, Client client) {
        return findExpirationDateAfterByClient(date, client, null, null);
    }

    @Override
    public List<CreditCard> findExpirationDateAfterByClient(Date date, Client client, Integer start, Integer limit) {

        TypedQuery<CreditCard> query = getEntityManager().createNamedQuery(CreditCard.FIND_EXPIRATION_DATE_AFTER_BY_CLIENT, CreditCard.class);
        query.setParameter("date", date);
        query.setParameter("client", client);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<CreditCard> findExpirationDateBefore(Date date) {
        return findExpirationDateBefore(date, null, null);
    }

    @Override
    public List<CreditCard> findExpirationDateBefore(Date date, Integer start, Integer limit) {

        TypedQuery<CreditCard> query = getEntityManager().createNamedQuery(CreditCard.FIND_EXPIRATION_DATE_BEFORE, CreditCard.class);
        query.setParameter("date", date);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<CreditCard> findExpirationDateBeforeByClient(Date date, Client client) {
        return findExpirationDateBeforeByClient(date, client, null, null);
    }

    @Override
    public List<CreditCard> findExpirationDateBeforeByClient(Date date, Client client, Integer start, Integer limit) {

        TypedQuery<CreditCard> query = getEntityManager().createNamedQuery(CreditCard.FIND_EXPIRATION_DATE_BEFORE_BY_CLIENT, CreditCard.class);
        query.setParameter("date", date);
        query.setParameter("client", client);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<CreditCard> findExpirationDateBetween(Date startDate, Date endDate) {
        return findExpirationDateBetween(startDate, endDate, null, null);
    }

    @Override
    public List<CreditCard> findExpirationDateBetween(Date startDate, Date endDate, Integer start, Integer limit) {

        TypedQuery<CreditCard> query = getEntityManager().createNamedQuery(CreditCard.FIND_EXPIRATION_DATE_BETWEEN, CreditCard.class);
        query.setParameter("start_date", startDate);
        query.setParameter("end_date", endDate);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<CreditCard> findExpirationDateBetweenByClient(Date startDate, Date endDate, Client client) {
        return findExpirationDateBetweenByClient(startDate, endDate, client, null, null);
    }

    @Override
    public List<CreditCard> findExpirationDateBetweenByClient(Date startDate, Date endDate, Client client, Integer start, Integer limit) {

        TypedQuery<CreditCard> query = getEntityManager().createNamedQuery(CreditCard.FIND_EXPIRATION_DATE_BETWEEN_BY_CLIENT, CreditCard.class);
        query.setParameter("start_date", startDate);
        query.setParameter("end_date", endDate);
        query.setParameter("client", client);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public Integer deleteForClient(Client client) {
        Query query = getEntityManager().createNamedQuery(CreditCard.DELETE_FOR_CLIENT);
        query.setParameter("client", client);
        return query.executeUpdate();
    }

    @Override
    public Integer deleteWithExpirationDateBefore(Date date) {

        Query query = getEntityManager().createNamedQuery(CreditCard.DELETE_WITH_EXPIRATION_DATE_BEFORE);
        query.setParameter("date", date);
        return query.executeUpdate();
    }

    @Override
    public Integer deleteWithExpirationDateBeforeForClient(Date date, Client client) {

        Query query = getEntityManager().createNamedQuery(CreditCard.DELETE_WITH_EXPIRATION_DATE_BEFORE_FOR_CLIENT);
        query.setParameter("client", client);
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
    public Integer deleteWithExpirationDateAfterForClient(Date date, Client client) {
        Query query = getEntityManager().createNamedQuery(CreditCard.DELETE_WITH_EXPIRATION_DATE_AFTER_FOR_CLIENT);
        query.setParameter("client", client);
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
    public Integer deleteWithExpirationDateBetweenForClient(Date startDate, Date endDate, Client client) {
        Query query = getEntityManager().createNamedQuery(CreditCard.DELETE_WITH_EXPIRATION_DATE_BETWEEN_FOR_CLIENT);
        query.setParameter("client", client);
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
    public Integer deleteExpiredForClient(Client client) {
        Query query = getEntityManager().createNamedQuery(CreditCard.DELETE_EXPIRED_FOR_CLIENT);
        query.setParameter("client", client);
        return query.executeUpdate();
    }

    @Override
    public Integer deleteWithType(CreditCardType cardType) {
        Query query = getEntityManager().createNamedQuery(CreditCard.DELETE_WITH_TYPE);
        query.setParameter("card_type", cardType);
        return query.executeUpdate();
    }

    @Override
    public Integer deleteWithTypeForClient(CreditCardType cardType, Client client) {
        Query query = getEntityManager().createNamedQuery(CreditCard.DELETE_WITH_TYPE_FOR_CLIENT);
        query.setParameter("client", client);
        query.setParameter("card_type", cardType);
        return query.executeUpdate();
    }

    @Override
    public Integer deleteById(CreditCardId creditCardId) {
        Query query = getEntityManager().createNamedQuery(CreditCard.DELETE_BY_ID);
        query.setParameter("client_id", creditCardId.getClient());
        query.setParameter("card_number", creditCardId.getCreditCardNumber());
        query.setParameter("expiration_date", creditCardId.getExpirationDate());
        return query.executeUpdate();
    }

    @Override
    public Integer countByClient(Client client) {
        TypedQuery<Integer> query = getEntityManager().createNamedQuery(CreditCard.COUNT_BY_CLIENT, Integer.class);
        query.setParameter("client", client);
        return query.getSingleResult();
    }

    @Override
    public List<CreditCard> findByMultipleCriteria(List<Client> clients, List<CreditCardType> cardTypes, String cardNumber, String cardHolder, Boolean expired, Date theEarliestExpirationDate, Date theLatestExpirationDate) {
        return findByMultipleCriteria(clients, cardTypes, cardNumber, cardHolder, expired, theEarliestExpirationDate, theLatestExpirationDate, null, null);
    }

    @Override
    public List<CreditCard> findByMultipleCriteria(List<Client> clients, List<CreditCardType> cardTypes, String cardNumber, String cardHolder, Boolean expired, Date theEarliestExpirationDate, Date theLatestExpirationDate, Integer start, Integer limit) {

        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<CreditCard> criteriaQuery = criteriaBuilder.createQuery(CreditCard.class);
        // FROM
        Root<CreditCard> creditCard = criteriaQuery.from(CreditCard.class);
        // SELECT
        criteriaQuery.select(creditCard);

        // INNER JOIN-s
        Join<CreditCard, Client> client = null;

        // WHERE PREDICATES
        List<Predicate> predicates = new ArrayList<>();

        if(clients != null && clients.size() > 0) {

            if(client == null) client = creditCard.join(CreditCard_.client);

            predicates.add( client.in(clients) );
        }

        if(cardTypes != null && cardTypes.size() > 0) {
            predicates.add( creditCard.get(CreditCard_.cardType).in(cardTypes) );
        }

        if(cardNumber != null) {
            predicates.add( criteriaBuilder.like(creditCard.get(CreditCard_.creditCardNumber), cardNumber) );
        }

        if(cardHolder != null) {
            predicates.add( criteriaBuilder.like(creditCard.get(CreditCard_.cardHolder), cardHolder) );
        }

        if(expired != null) {

            if(expired) {
                predicates.add( criteriaBuilder.lessThan(creditCard.get(CreditCard_.expirationDate), new Date()) );
            } else {
                predicates.add( criteriaBuilder.greaterThan(creditCard.get(CreditCard_.expirationDate), new Date()) );
            }
        }

        if(theEarliestExpirationDate != null) {
            predicates.add( criteriaBuilder.greaterThanOrEqualTo(creditCard.get(CreditCard_.expirationDate), theEarliestExpirationDate) );
        }

        if(theLatestExpirationDate != null) {
            predicates.add( criteriaBuilder.lessThanOrEqualTo(creditCard.get(CreditCard_.expirationDate), theLatestExpirationDate) );
        }

        // WHERE predicate1 AND predicate2 AND ... AND predicateN
        criteriaQuery.where(predicates.toArray(new Predicate[] {}));

        TypedQuery<CreditCard> query = getEntityManager().createQuery(criteriaQuery);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }
}