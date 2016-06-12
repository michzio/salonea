package pl.salonea.ejb.stateless;

import pl.salonea.ejb.interfaces.HistoricalTransactionFacadeInterface;
import pl.salonea.entities.*;
import pl.salonea.entities.idclass.ProviderServiceId;
import pl.salonea.entities.idclass.ServicePointId;
import pl.salonea.entities.idclass.TransactionId;
import pl.salonea.entities.idclass.WorkStationId;
import pl.salonea.enums.CurrencyCode;
import pl.salonea.enums.TransactionCompletionStatus;
import pl.salonea.utils.Period;
import pl.salonea.utils.PriceRange;
import pl.salonea.utils.RatingRange;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.*;
import javax.persistence.criteria.*;
import javax.ws.rs.QueryParam;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * Created by michzio on 25/08/2015.
 */
@Stateless
@LocalBean
public class HistoricalTransactionFacade extends AbstractFacade<HistoricalTransaction>
    implements HistoricalTransactionFacadeInterface.Local, HistoricalTransactionFacadeInterface.Remote {

    @Inject
    private EntityManager em;

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    public HistoricalTransactionFacade() {
        super(HistoricalTransaction.class);
    }

    @Override
    public HistoricalTransaction createForClient(Long clientId, HistoricalTransaction historicalTransaction) {

        Client foundClient = getEntityManager().find(Client.class, clientId);
        historicalTransaction.setClient(foundClient);

        // fetch and set entities based on detached ids
        ProviderServiceId providerServiceId = new ProviderServiceId(historicalTransaction.getProviderService().getProvider().getUserId(),
                                                                    historicalTransaction.getProviderService().getService().getServiceId());
        ProviderService providerService = getEntityManager().find(ProviderService.class, providerServiceId);
        ServicePointId servicePointId = new ServicePointId(historicalTransaction.getServicePoint().getProvider().getUserId(),
                                                           historicalTransaction.getServicePoint().getServicePointNumber());
        ServicePoint servicePoint = getEntityManager().find(ServicePoint.class, servicePointId);
        WorkStationId workStationId = new WorkStationId(historicalTransaction.getWorkStation().getServicePoint().getProvider().getUserId(),
                                                        historicalTransaction.getWorkStation().getServicePoint().getServicePointNumber(),
                                                        historicalTransaction.getWorkStation().getWorkStationNumber());
        WorkStation workStation = getEntityManager().find(WorkStation.class, workStationId);
        PaymentMethod paymentMethod = getEntityManager().find(PaymentMethod.class, historicalTransaction.getPaymentMethod().getId());
        Term term = getEntityManager().find(Term.class, historicalTransaction.getTerm().getTermId());

        // ... and set ...
        historicalTransaction.setProviderService(providerService);
        historicalTransaction.setServicePoint(servicePoint);
        historicalTransaction.setWorkStation(workStation);
        historicalTransaction.setPaymentMethod(paymentMethod);
        historicalTransaction.setTerm(term);

        // ... and set employees ...
        if(historicalTransaction.getEmployeeIds() != null && historicalTransaction.getEmployeeIds().size() > 0) {
            historicalTransaction.setEmployees(new LinkedHashSet<>());

            for (Long employeeId : historicalTransaction.getEmployeeIds()) {
                Employee employee = getEntityManager().find(Employee.class, employeeId);
                historicalTransaction.getEmployees().add(employee);
            }
        }


        return create(historicalTransaction);
    }

    @Override
    public HistoricalTransaction update(TransactionId transactionId, HistoricalTransaction historicalTransaction) {

        return update(transactionId, historicalTransaction, true);
    }

    @Override
    public HistoricalTransaction update(TransactionId transactionId, HistoricalTransaction historicalTransaction, Boolean retainTransientFields) {

        Client foundClient = getEntityManager().find(Client.class, transactionId.getClient());
        historicalTransaction.setClient(foundClient);
        historicalTransaction.setTransactionNumber(transactionId.getTransactionNumber());

        HistoricalTransaction currentHistoricalTransaction = null;

        if(retainTransientFields) {
            // keep current collection attributes of resource (and other marked @XmlTransient)
            currentHistoricalTransaction = findByIdEagerly(transactionId);
            if(currentHistoricalTransaction != null) {
                historicalTransaction.setEmployees(currentHistoricalTransaction.getEmployees());
            }
        }

        // fetch and set entities based on detached ids
        ProviderServiceId providerServiceId = new ProviderServiceId(historicalTransaction.getProviderService().getProvider().getUserId(),
                                                                    historicalTransaction.getProviderService().getService().getServiceId());
        ProviderService providerService = null;
        if(currentHistoricalTransaction != null
                && currentHistoricalTransaction.getProviderService().getProvider().getUserId() == providerServiceId.getProvider()
                && currentHistoricalTransaction.getProviderService().getService().getServiceId() == providerServiceId.getService() ) {
            // if the same provider service is already set on current historical transaction
            providerService = currentHistoricalTransaction.getProviderService();
        } else {
            providerService = getEntityManager().find(ProviderService.class, providerServiceId);
        }

        ServicePointId servicePointId = new ServicePointId(historicalTransaction.getServicePoint().getProvider().getUserId(),
                                                           historicalTransaction.getServicePoint().getServicePointNumber());
        ServicePoint servicePoint = null;
        if(currentHistoricalTransaction != null
                && currentHistoricalTransaction.getServicePoint().getProvider().getUserId() == servicePointId.getProvider()
                && currentHistoricalTransaction.getServicePoint().getServicePointNumber() == servicePointId.getServicePointNumber()) {
            // if the same service point is already set on current historical transaction
            servicePoint = currentHistoricalTransaction.getServicePoint();
        } else {
            servicePoint = getEntityManager().find(ServicePoint.class, servicePointId);
        }

        WorkStationId workStationId = new WorkStationId(historicalTransaction.getWorkStation().getServicePoint().getProvider().getUserId(),
                                                        historicalTransaction.getWorkStation().getServicePoint().getServicePointNumber(),
                                                        historicalTransaction.getWorkStation().getWorkStationNumber());
        WorkStation workStation = null;
        if(currentHistoricalTransaction != null
                && currentHistoricalTransaction.getWorkStation().getServicePoint().getProvider().getUserId() == workStationId.getServicePoint().getProvider()
                && currentHistoricalTransaction.getWorkStation().getServicePoint().getServicePointNumber() == workStationId.getServicePoint().getServicePointNumber()
                && currentHistoricalTransaction.getWorkStation().getWorkStationNumber() == workStationId.getWorkStationNumber()) {
            // if the same work station is already set on current historical transaction
            workStation = currentHistoricalTransaction.getWorkStation();
        } else {
            workStation = getEntityManager().find(WorkStation.class, workStationId);
        }

        PaymentMethod paymentMethod = null;
        if(currentHistoricalTransaction != null
                && currentHistoricalTransaction.getPaymentMethod().getId() == historicalTransaction.getPaymentMethod().getId()) {
            // if the same payment method is already set on current historical transaction
            paymentMethod = currentHistoricalTransaction.getPaymentMethod();
        } else {
            paymentMethod = getEntityManager().find(PaymentMethod.class, historicalTransaction.getPaymentMethod().getId());
        }

        Term term = null;
        if(currentHistoricalTransaction != null
                && currentHistoricalTransaction.getTerm().getTermId() == historicalTransaction.getTerm().getTermId()) {
            // if the same term is already set on current historical transaction
            term = currentHistoricalTransaction.getTerm();
        } else {
            term = getEntityManager().find(Term.class, historicalTransaction.getTerm().getTermId());
        }

        // ... and set ...
        historicalTransaction.setProviderService(providerService);
        historicalTransaction.setServicePoint(servicePoint);
        historicalTransaction.setWorkStation(workStation);
        historicalTransaction.setPaymentMethod(paymentMethod);
        historicalTransaction.setTerm(term);

        // ... and set employees ...
        if(historicalTransaction.getEmployeeIds() != null && historicalTransaction.getEmployeeIds().size() > 0) {
            historicalTransaction.setEmployees(new LinkedHashSet<>());

            for (Long employeeId : historicalTransaction.getEmployeeIds()) {
                Employee employee = getEntityManager().find(Employee.class, employeeId);
                historicalTransaction.getEmployees().add(employee);
            }
        }

        return update(historicalTransaction);
    }

    @Override
    public List<HistoricalTransaction> findAllEagerly() {
        return findAllEagerly(null, null);
    }

    @Override
    public List<HistoricalTransaction> findAllEagerly(Integer start, Integer limit) {

        TypedQuery<HistoricalTransaction> query = getEntityManager().createNamedQuery(HistoricalTransaction.FIND_ALL_EAGERLY, HistoricalTransaction.class);
        if (start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public HistoricalTransaction findByIdEagerly(TransactionId transactionId) {

        TypedQuery<HistoricalTransaction> query = getEntityManager().createNamedQuery(HistoricalTransaction.FIND_BY_ID_EAGERLY, HistoricalTransaction.class);
        query.setParameter("clientId", transactionId.getClient());
        query.setParameter("transaction_number", transactionId.getTransactionNumber());
        try {
            return query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            return null;
        }
    }

    @Override
    public List<HistoricalTransaction> findByTransactionTime(Date startTime, Date endTime) {
        return findByTransactionTime(startTime, endTime, null, null);
    }

    @Override
    public List<HistoricalTransaction> findByTransactionTime(Date startTime, Date endTime, Integer start, Integer limit) {

        TypedQuery<HistoricalTransaction> query = getEntityManager().createNamedQuery(HistoricalTransaction.FIND_BY_TRANSACTION_TIME, HistoricalTransaction.class);
        query.setParameter("start_time", startTime);
        query.setParameter("end_time", endTime);
        if (start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<HistoricalTransaction> findByBookedTime(Date startTime, Date endTime) {
        return findByBookedTime(startTime, endTime, null, null);
    }

    @Override
    public List<HistoricalTransaction> findByBookedTime(Date startTime, Date endTime, Integer start, Integer limit) {

        TypedQuery<HistoricalTransaction> query = getEntityManager().createNamedQuery(HistoricalTransaction.FIND_BY_BOOKED_TIME, HistoricalTransaction.class);
        query.setParameter("start_time", startTime);
        query.setParameter("end_time", endTime);
        if (start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<HistoricalTransaction> findOnlyPaid() {
        return findOnlyPaid(null, null);
    }

    @Override
    public List<HistoricalTransaction> findOnlyPaid(Integer start, Integer limit) {

        TypedQuery<HistoricalTransaction> query = getEntityManager().createNamedQuery(HistoricalTransaction.FIND_ONLY_PAID, HistoricalTransaction.class);
        if (start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<HistoricalTransaction> findOnlyUnpaid() {
        return findOnlyUnpaid(null, null);
    }

    @Override
    public List<HistoricalTransaction> findOnlyUnpaid(Integer start, Integer limit) {

        TypedQuery<HistoricalTransaction> query = getEntityManager().createNamedQuery(HistoricalTransaction.FIND_ONLY_UNPAID, HistoricalTransaction.class);
        if (start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<HistoricalTransaction> findByPriceRange(Double minPrice, Double maxPrice) {
        return findByPriceRange(minPrice, maxPrice, null, null);
    }

    @Override
    public List<HistoricalTransaction> findByPriceRange(Double minPrice, Double maxPrice, Integer start, Integer limit) {

        TypedQuery<HistoricalTransaction> query = getEntityManager().createNamedQuery(HistoricalTransaction.FIND_BY_PRICE_RANGE, HistoricalTransaction.class);
        query.setParameter("min_price", minPrice);
        query.setParameter("max_price", maxPrice);
        if (start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<HistoricalTransaction> findByCurrencyCode(CurrencyCode currencyCode) {
        return findByCurrencyCode(currencyCode, null, null);
    }

    @Override
    public List<HistoricalTransaction> findByCurrencyCode(CurrencyCode currencyCode, Integer start, Integer limit) {

        TypedQuery<HistoricalTransaction> query = getEntityManager().createNamedQuery(HistoricalTransaction.FIND_BY_CURRENCY_CODE, HistoricalTransaction.class);
        query.setParameter("currency_code", currencyCode);
        if (start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<HistoricalTransaction> findByPriceRangeAndCurrencyCode(Double minPrice, Double maxPrice, CurrencyCode currencyCode) {
        return findByPriceRangeAndCurrencyCode(minPrice, maxPrice, currencyCode, null, null);
    }

    @Override
    public List<HistoricalTransaction> findByPriceRangeAndCurrencyCode(Double minPrice, Double maxPrice, CurrencyCode currencyCode, Integer start, Integer limit) {

        TypedQuery<HistoricalTransaction> query = getEntityManager().createNamedQuery(HistoricalTransaction.FIND_BY_PRICE_RANGE_AND_CURRENCY_CODE, HistoricalTransaction.class);
        query.setParameter("min_price", minPrice);
        query.setParameter("max_price", maxPrice);
        query.setParameter("currency_code", currencyCode);
        if (start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<HistoricalTransaction> findByCompletionStatus(TransactionCompletionStatus completionStatus) {
        return findByCompletionStatus(completionStatus, null, null);
    }

    @Override
    public List<HistoricalTransaction> findByCompletionStatus(TransactionCompletionStatus completionStatus, Integer start, Integer limit) {

        TypedQuery<HistoricalTransaction> query = getEntityManager().createNamedQuery(HistoricalTransaction.FIND_BY_COMPLETION_STATUS, HistoricalTransaction.class);
        query.setParameter("completion_status", completionStatus);
        if (start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<HistoricalTransaction> findByClientRatingRange(Short minRating, Short maxRating) {
        return findByClientRatingRange(minRating, maxRating, null, null);
    }

    @Override
    public List<HistoricalTransaction> findByClientRatingRange(Short minRating, Short maxRating, Integer start, Integer limit) {

        TypedQuery<HistoricalTransaction> query = getEntityManager().createNamedQuery(HistoricalTransaction.FIND_BY_CLIENT_RATING_RANGE, HistoricalTransaction.class);
        query.setParameter("min_rating", minRating);
        query.setParameter("max_rating", maxRating);
        if (start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<HistoricalTransaction> findByClientComment(String clientComment) {
        return findByClientComment(clientComment, null, null);
    }

    @Override
    public List<HistoricalTransaction> findByClientComment(String clientComment, Integer start, Integer limit) {

        TypedQuery<HistoricalTransaction> query = getEntityManager().createNamedQuery(HistoricalTransaction.FIND_BY_CLIENT_COMMENT, HistoricalTransaction.class);
        query.setParameter("client_comment", "%" + clientComment + "%");
        if (start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<HistoricalTransaction> findByProviderRatingRange(Short minRating, Short maxRating) {
        return findByProviderRatingRange(minRating, maxRating, null, null);
    }

    @Override
    public List<HistoricalTransaction> findByProviderRatingRange(Short minRating, Short maxRating, Integer start, Integer limit) {

        TypedQuery<HistoricalTransaction> query = getEntityManager().createNamedQuery(HistoricalTransaction.FIND_BY_PROVIDER_RATING_RANGE, HistoricalTransaction.class);
        query.setParameter("min_rating", minRating);
        query.setParameter("max_rating", maxRating);
        if (start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<HistoricalTransaction> findByProviderDementi(String providerDementi) {
        return findByProviderDementi(providerDementi, null, null);
    }

    @Override
    public List<HistoricalTransaction> findByProviderDementi(String providerDementi, Integer start, Integer limit) {

        TypedQuery<HistoricalTransaction> query = getEntityManager().createNamedQuery(HistoricalTransaction.FIND_BY_PROVIDER_DEMENTI, HistoricalTransaction.class);
        query.setParameter("provider_dementi", "%" + providerDementi + "%");
        if (start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<HistoricalTransaction> findByClient(Client client) {
        return findByClient(client, null, null);
    }

    @Override
    public List<HistoricalTransaction> findByClient(Client client, Integer start, Integer limit) {

        TypedQuery<HistoricalTransaction> query = getEntityManager().createNamedQuery(HistoricalTransaction.FIND_BY_CLIENT, HistoricalTransaction.class);
        query.setParameter("client", client);
        if (start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<HistoricalTransaction> findByClientEagerly(Client client) {
        return findByClientEagerly(client, null, null);
    }

    @Override
    public List<HistoricalTransaction> findByClientEagerly(Client client, Integer start, Integer limit) {

        TypedQuery<HistoricalTransaction> query = getEntityManager().createNamedQuery(HistoricalTransaction.FIND_BY_CLIENT_EAGERLY, HistoricalTransaction.class);
        query.setParameter("client", client);
        if (start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<HistoricalTransaction> findByClientAndTransactionTime(Client client, Date startTime, Date endTime) {
        return findByClientAndTransactionTime(client, startTime, endTime, null, null);
    }

    @Override
    public List<HistoricalTransaction> findByClientAndTransactionTime(Client client, Date startTime, Date endTime, Integer start, Integer limit) {

        TypedQuery<HistoricalTransaction> query = getEntityManager().createNamedQuery(HistoricalTransaction.FIND_BY_CLIENT_AND_TRANSACTION_TIME, HistoricalTransaction.class);
        query.setParameter("client", client);
        query.setParameter("start_time", startTime);
        query.setParameter("end_time", endTime);
        if (start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<HistoricalTransaction> findByClientAndBookedTime(Client client, Date startTime, Date endTime) {
        return findByClientAndBookedTime(client, startTime, endTime, null, null);
    }

    @Override
    public List<HistoricalTransaction> findByClientAndBookedTime(Client client, Date startTime, Date endTime, Integer start, Integer limit) {

        TypedQuery<HistoricalTransaction> query = getEntityManager().createNamedQuery(HistoricalTransaction.FIND_BY_CLIENT_AND_BOOKED_TIME, HistoricalTransaction.class);
        query.setParameter("client", client);
        query.setParameter("start_time", startTime);
        query.setParameter("end_time", endTime);
        if (start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<HistoricalTransaction> findByClientOnlyPaid(Client client) {
        return findByClientOnlyPaid(client, null, null);
    }

    @Override
    public List<HistoricalTransaction> findByClientOnlyPaid(Client client, Integer start, Integer limit) {

        TypedQuery<HistoricalTransaction> query = getEntityManager().createNamedQuery(HistoricalTransaction.FIND_BY_CLIENT_ONLY_PAID, HistoricalTransaction.class);
        query.setParameter("client", client);
        if (start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<HistoricalTransaction> findByClientOnlyUnpaid(Client client) {
        return findByClientOnlyUnpaid(client, null, null);
    }

    @Override
    public List<HistoricalTransaction> findByClientOnlyUnpaid(Client client, Integer start, Integer limit) {

        TypedQuery<HistoricalTransaction> query = getEntityManager().createNamedQuery(HistoricalTransaction.FIND_BY_CLIENT_ONLY_UNPAID, HistoricalTransaction.class);
        query.setParameter("client", client);
        if (start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<HistoricalTransaction> findByClientAndPriceRange(Client client, Double minPrice, Double maxPrice) {
        return findByClientAndPriceRange(client, minPrice, maxPrice, null, null);
    }

    @Override
    public List<HistoricalTransaction> findByClientAndPriceRange(Client client, Double minPrice, Double maxPrice, Integer start, Integer limit) {

        TypedQuery<HistoricalTransaction> query = getEntityManager().createNamedQuery(HistoricalTransaction.FIND_BY_CLIENT_AND_PRICE_RANGE, HistoricalTransaction.class);
        query.setParameter("client", client);
        query.setParameter("min_price", minPrice);
        query.setParameter("max_price", maxPrice);
        if (start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<HistoricalTransaction> findByClientAndCurrencyCode(Client client, CurrencyCode currencyCode) {
        return findByClientAndCurrencyCode(client, currencyCode, null, null);
    }

    @Override
    public List<HistoricalTransaction> findByClientAndCurrencyCode(Client client, CurrencyCode currencyCode, Integer start, Integer limit) {

        TypedQuery<HistoricalTransaction> query = getEntityManager().createNamedQuery(HistoricalTransaction.FIND_BY_CLIENT_AND_CURRENCY_CODE, HistoricalTransaction.class);
        query.setParameter("client", client);
        query.setParameter("currency_code", currencyCode);
        if (start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<HistoricalTransaction> findByClientAndPriceRangeAndCurrencyCode(Client client, Double minPrice, Double maxPrice, CurrencyCode currencyCode) {
        return findByClientAndPriceRangeAndCurrencyCode(client, minPrice, maxPrice, currencyCode, null, null);
    }

    @Override
    public List<HistoricalTransaction> findByClientAndPriceRangeAndCurrencyCode(Client client, Double minPrice, Double maxPrice, CurrencyCode currencyCode, Integer start, Integer limit) {

        TypedQuery<HistoricalTransaction> query = getEntityManager().createNamedQuery(HistoricalTransaction.FIND_BY_CLIENT_AND_PRICE_RANGE_AND_CURRENCY_CODE, HistoricalTransaction.class);
        query.setParameter("client", client);
        query.setParameter("min_price", minPrice);
        query.setParameter("max_price", maxPrice);
        query.setParameter("currency_code", currencyCode);
        if (start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<HistoricalTransaction> findByClientAndCompletionStatus(Client client, TransactionCompletionStatus completionStatus) {
        return findByClientAndCompletionStatus(client, completionStatus, null, null);
    }

    @Override
    public List<HistoricalTransaction> findByClientAndCompletionStatus(Client client, TransactionCompletionStatus completionStatus, Integer start, Integer limit) {

        TypedQuery<HistoricalTransaction> query = getEntityManager().createNamedQuery(HistoricalTransaction.FIND_BY_CLIENT_AND_COMPLETION_STATUS, HistoricalTransaction.class);
        query.setParameter("client", client);
        query.setParameter("completion_status", completionStatus);
        if (start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<HistoricalTransaction> findByClientAndClientRatingRange(Client client, Short minRating, Short maxRating) {
        return findByClientAndClientRatingRange(client, minRating, maxRating, null, null);
    }

    @Override
    public List<HistoricalTransaction> findByClientAndClientRatingRange(Client client, Short minRating, Short maxRating, Integer start, Integer limit) {

        TypedQuery<HistoricalTransaction> query = getEntityManager().createNamedQuery(HistoricalTransaction.FIND_BY_CLIENT_AND_CLIENT_RATING_RANGE, HistoricalTransaction.class);
        query.setParameter("client", client);
        query.setParameter("min_rating", minRating);
        query.setParameter("max_rating", maxRating);
        if (start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<HistoricalTransaction> findByClientAndClientComment(Client client, String clientComment) {
        return findByClientAndClientComment(client, clientComment, null, null);
    }

    @Override
    public List<HistoricalTransaction> findByClientAndClientComment(Client client, String clientComment, Integer start, Integer limit) {

        TypedQuery<HistoricalTransaction> query = getEntityManager().createNamedQuery(HistoricalTransaction.FIND_BY_CLIENT_AND_CLIENT_COMMENT, HistoricalTransaction.class);
        query.setParameter("client", client);
        query.setParameter("client_comment", "%" + clientComment + "%");
        if (start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<HistoricalTransaction> findByClientAndProviderRatingRange(Client client, Short minRating, Short maxRating) {
        return findByClientAndProviderRatingRange(client, minRating, maxRating, null, null);
    }

    @Override
    public List<HistoricalTransaction> findByClientAndProviderRatingRange(Client client, Short minRating, Short maxRating, Integer start, Integer limit) {

        TypedQuery<HistoricalTransaction> query = getEntityManager().createNamedQuery(HistoricalTransaction.FIND_BY_CLIENT_AND_PROVIDER_RATING_RANGE, HistoricalTransaction.class);
        query.setParameter("client", client);
        query.setParameter("min_rating", minRating);
        query.setParameter("max_rating", maxRating);
        if (start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<HistoricalTransaction> findByClientAndProviderDementi(Client client, String providerDementi) {
        return findByClientAndProviderDementi(client, providerDementi, null, null);
    }

    @Override
    public List<HistoricalTransaction> findByClientAndProviderDementi(Client client, String providerDementi, Integer start, Integer limit) {

        TypedQuery<HistoricalTransaction> query = getEntityManager().createNamedQuery(HistoricalTransaction.FIND_BY_CLIENT_AND_PROVIDER_DEMENTI, HistoricalTransaction.class);
        query.setParameter("client", client);
        query.setParameter("provider_dementi", "%" + providerDementi + "%");
        if (start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<HistoricalTransaction> findByPaymentMethod(PaymentMethod paymentMethod) {
        return findByPaymentMethod(paymentMethod, null, null);
    }

    @Override
    public List<HistoricalTransaction> findByPaymentMethod(PaymentMethod paymentMethod, Integer start, Integer limit) {

        TypedQuery<HistoricalTransaction> query = getEntityManager().createNamedQuery(HistoricalTransaction.FIND_BY_PAYMENT_METHOD, HistoricalTransaction.class);
        query.setParameter("payment_method", paymentMethod);
        if (start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<HistoricalTransaction> findByPaymentMethodEagerly(PaymentMethod paymentMethod) {
        return findByPaymentMethodEagerly(paymentMethod, null, null);
    }

    @Override
    public List<HistoricalTransaction> findByPaymentMethodEagerly(PaymentMethod paymentMethod, Integer start, Integer limit) {

        TypedQuery<HistoricalTransaction> query = getEntityManager().createNamedQuery(HistoricalTransaction.FIND_BY_PAYMENT_METHOD_EAGERLY, HistoricalTransaction.class);
        query.setParameter("payment_method", paymentMethod);
        if (start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<HistoricalTransaction> findByProvider(Provider provider) {
        return findByProvider(provider, null, null);
    }

    @Override
    public List<HistoricalTransaction> findByProvider(Provider provider, Integer start, Integer limit) {

        TypedQuery<HistoricalTransaction> query = getEntityManager().createNamedQuery(HistoricalTransaction.FIND_BY_PROVIDER, HistoricalTransaction.class);
        query.setParameter("provider", provider);
        if (start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<HistoricalTransaction> findByProviderEagerly(Provider provider) {
        return findByProviderEagerly(provider, null, null);
    }

    @Override
    public List<HistoricalTransaction> findByProviderEagerly(Provider provider, Integer start, Integer limit) {

        TypedQuery<HistoricalTransaction> query = getEntityManager().createNamedQuery(HistoricalTransaction.FIND_BY_PROVIDER_EAGERLY, HistoricalTransaction.class);
        query.setParameter("provider", provider);
        if (start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<HistoricalTransaction> findByService(Service service) {
        return findByService(service, null, null);
    }

    @Override
    public List<HistoricalTransaction> findByService(Service service, Integer start, Integer limit) {

        TypedQuery<HistoricalTransaction> query = getEntityManager().createNamedQuery(HistoricalTransaction.FIND_BY_SERVICE, HistoricalTransaction.class);
        query.setParameter("service", service);
        if (start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<HistoricalTransaction> findByServiceEagerly(Service service) {
        return findByServiceEagerly(service, null, null);
    }

    @Override
    public List<HistoricalTransaction> findByServiceEagerly(Service service, Integer start, Integer limit) {

        TypedQuery<HistoricalTransaction> query = getEntityManager().createNamedQuery(HistoricalTransaction.FIND_BY_SERVICE_EAGERLY, HistoricalTransaction.class);
        query.setParameter("service", service);
        if (start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<HistoricalTransaction> findByProviderService(ProviderService providerService) {
        return findByProviderService(providerService, null, null);
    }


    @Override
    public List<HistoricalTransaction> findByProviderService(ProviderService providerService, Integer start, Integer limit) {

        TypedQuery<HistoricalTransaction> query = getEntityManager().createNamedQuery(HistoricalTransaction.FIND_BY_PROVIDER_SERVICE, HistoricalTransaction.class);
        query.setParameter("provider_service", providerService);
        if (start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<HistoricalTransaction> findByProviderServiceEagerly(ProviderService providerService) {
        return findByProviderServiceEagerly(providerService, null, null);
    }

    @Override
    public List<HistoricalTransaction> findByProviderServiceEagerly(ProviderService providerService, Integer start, Integer limit) {

        TypedQuery<HistoricalTransaction> query = getEntityManager().createNamedQuery(HistoricalTransaction.FIND_BY_PROVIDER_SERVICE_EAGERLY, HistoricalTransaction.class);
        query.setParameter("provider_service", providerService);
        if (start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<HistoricalTransaction> findByEmployee(Employee employee) {
        return findByEmployee(employee, null, null);
    }

    @Override
    public List<HistoricalTransaction> findByEmployee(Employee employee, Integer start, Integer limit) {

        TypedQuery<HistoricalTransaction> query = getEntityManager().createNamedQuery(HistoricalTransaction.FIND_BY_EMPLOYEE, HistoricalTransaction.class);
        query.setParameter("employee", employee);
        if (start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<HistoricalTransaction> findByEmployeeEagerly(Employee employee) {
        return findByEmployeeEagerly(employee, null, null);
    }

    @Override
    public List<HistoricalTransaction> findByEmployeeEagerly(Employee employee, Integer start, Integer limit) {

        TypedQuery<HistoricalTransaction> query = getEntityManager().createNamedQuery(HistoricalTransaction.FIND_BY_EMPLOYEE_EAGERLY, HistoricalTransaction.class);
        query.setParameter("employee", employee);
        if (start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<HistoricalTransaction> findByWorkStation(WorkStation workStation) {
        return findByWorkStation(workStation, null, null);
    }

    @Override
    public List<HistoricalTransaction> findByWorkStation(WorkStation workStation, Integer start, Integer limit) {

        TypedQuery<HistoricalTransaction> query = getEntityManager().createNamedQuery(HistoricalTransaction.FIND_BY_WORK_STATION, HistoricalTransaction.class);
        query.setParameter("work_station", workStation);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<HistoricalTransaction> findByWorkStationEagerly(WorkStation workStation) {
        return findByWorkStationEagerly(workStation, null, null);
    }

    @Override
    public List<HistoricalTransaction> findByWorkStationEagerly(WorkStation workStation, Integer start, Integer limit) {

        TypedQuery<HistoricalTransaction> query = getEntityManager().createNamedQuery(HistoricalTransaction.FIND_BY_WORK_STATION_EAGERLY, HistoricalTransaction.class);
        query.setParameter("work_station", workStation);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<HistoricalTransaction> findByServicePoint(ServicePoint servicePoint) {
        return findByServicePoint(servicePoint, null, null);
    }

    @Override
    public List<HistoricalTransaction> findByServicePoint(ServicePoint servicePoint, Integer start, Integer limit) {

        TypedQuery<HistoricalTransaction> query = getEntityManager().createNamedQuery(HistoricalTransaction.FIND_BY_SERVICE_POINT, HistoricalTransaction.class);
        query.setParameter("service_point", servicePoint);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<HistoricalTransaction> findByServicePointEagerly(ServicePoint servicePoint) {
        return findByServicePointEagerly(servicePoint, null, null);
    }

    @Override
    public List<HistoricalTransaction> findByServicePointEagerly(ServicePoint servicePoint, Integer start, Integer limit) {

        TypedQuery<HistoricalTransaction> query = getEntityManager().createNamedQuery(HistoricalTransaction.FIND_BY_SERVICE_POINT_EAGERLY, HistoricalTransaction.class);
        query.setParameter("service_point", servicePoint);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<HistoricalTransaction> findByTerm(Term term) {
        return findByTerm(term, null, null);
    }

    @Override
    public List<HistoricalTransaction> findByTerm(Term term, Integer start, Integer limit) {

        TypedQuery<HistoricalTransaction> query = getEntityManager().createNamedQuery(HistoricalTransaction.FIND_BY_TERM, HistoricalTransaction.class);
        query.setParameter("term", term);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<HistoricalTransaction> findByTermEagerly(Term term) {
        return findByTermEagerly(term, null, null);
    }

    @Override
    public List<HistoricalTransaction> findByTermEagerly(Term term, Integer start, Integer limit) {

        TypedQuery<HistoricalTransaction> query = getEntityManager().createNamedQuery(HistoricalTransaction.FIND_BY_TERM_EAGERLY, HistoricalTransaction.class);
        query.setParameter("term", term);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<HistoricalTransaction> findByClientAndPaymentMethod(Client client, PaymentMethod paymentMethod) {
        return findByClientAndPaymentMethod(client, paymentMethod, null, null);
    }

    @Override
    public List<HistoricalTransaction> findByClientAndPaymentMethod(Client client, PaymentMethod paymentMethod, Integer start, Integer limit) {

        TypedQuery<HistoricalTransaction> query = getEntityManager().createNamedQuery(HistoricalTransaction.FIND_BY_CLIENT_AND_PAYMENT_METHOD, HistoricalTransaction.class);
        query.setParameter("client", client);
        query.setParameter("payment_method", paymentMethod);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<HistoricalTransaction> findByClientAndProvider(Client client, Provider provider) {
        return findByClientAndProvider(client, provider, null, null);
    }

    @Override
    public List<HistoricalTransaction> findByClientAndProvider(Client client, Provider provider, Integer start, Integer limit) {

        TypedQuery<HistoricalTransaction> query = getEntityManager().createNamedQuery(HistoricalTransaction.FIND_BY_CLIENT_AND_PROVIDER, HistoricalTransaction.class);
        query.setParameter("client", client);
        query.setParameter("provider", provider);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<HistoricalTransaction> findByClientAndService(Client client, Service service) {
        return findByClientAndService(client, service, null, null);
    }

    @Override
    public List<HistoricalTransaction> findByClientAndService(Client client, Service service, Integer start, Integer limit) {

        TypedQuery<HistoricalTransaction> query = getEntityManager().createNamedQuery(HistoricalTransaction.FIND_BY_CLIENT_AND_SERVICE, HistoricalTransaction.class);
        query.setParameter("client", client);
        query.setParameter("service", service);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<HistoricalTransaction> findByClientAndProviderService(Client client, ProviderService providerService) {
        return findByClientAndProviderService(client, providerService, null, null);
    }

    @Override
    public List<HistoricalTransaction> findByClientAndProviderService(Client client, ProviderService providerService, Integer start, Integer limit) {

        TypedQuery<HistoricalTransaction> query = getEntityManager().createNamedQuery(HistoricalTransaction.FIND_BY_CLIENT_AND_PROVIDER_SERVICE, HistoricalTransaction.class);
        query.setParameter("client", client);
        query.setParameter("provider_service", providerService);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<HistoricalTransaction> findByClientAndEmployee(Client client, Employee employee) {
        return findByClientAndEmployee(client, employee, null, null);
    }

    @Override
    public List<HistoricalTransaction> findByClientAndEmployee(Client client, Employee employee, Integer start, Integer limit) {

        TypedQuery<HistoricalTransaction> query = getEntityManager().createNamedQuery(HistoricalTransaction.FIND_BY_CLIENT_AND_EMPLOYEE, HistoricalTransaction.class);
        query.setParameter("client", client);
        query.setParameter("employee", employee);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<HistoricalTransaction> findByClientAndWorkStation(Client client, WorkStation workStation) {
        return findByClientAndWorkStation(client, workStation, null, null);
    }

    @Override
    public List<HistoricalTransaction> findByClientAndWorkStation(Client client, WorkStation workStation, Integer start, Integer limit) {

        TypedQuery<HistoricalTransaction> query = getEntityManager().createNamedQuery(HistoricalTransaction.FIND_BY_CLIENT_AND_WORK_STATION, HistoricalTransaction.class);
        query.setParameter("client", client);
        query.setParameter("work_station", workStation);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<HistoricalTransaction> findByClientAndServicePoint(Client client, ServicePoint servicePoint) {
        return findByClientAndServicePoint(client, servicePoint, null, null);
    }

    @Override
    public List<HistoricalTransaction> findByClientAndServicePoint(Client client, ServicePoint servicePoint, Integer start, Integer limit) {

        TypedQuery<HistoricalTransaction> query = getEntityManager().createNamedQuery(HistoricalTransaction.FIND_BY_CLIENT_AND_SERVICE_POINT, HistoricalTransaction.class);
        query.setParameter("client", client);
        query.setParameter("service_point", servicePoint);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<HistoricalTransaction> findByClientAndTerm(Client client, Term term) {
        return findByClientAndTerm(client, term, null, null);
    }

    @Override
    public List<HistoricalTransaction> findByClientAndTerm(Client client, Term term, Integer start, Integer limit) {

        TypedQuery<HistoricalTransaction> query = getEntityManager().createNamedQuery(HistoricalTransaction.FIND_BY_CLIENT_AND_TERM, HistoricalTransaction.class);
        query.setParameter("client", client);
        query.setParameter("term", term);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<HistoricalTransaction> findByMultipleCriteria(List<Client> clients, List<Provider> providers, List<Service> services, List<ServicePoint> servicePoints, List<WorkStation> workStations,
                                                              List<Employee> employees, List<ProviderService> providerServices, Period transactionTimePeriod, Period bookedTimePeriod, List<Term> terms,
                                                              PriceRange priceRange, List<CurrencyCode> currencyCodes, List<PaymentMethod> paymentMethods, Boolean paid,
                                                              List<TransactionCompletionStatus> completionStatuses, RatingRange clientRatingRange, List<String> clientComments, RatingRange providerRatingRange, List<String> providerDementis) {
        return findByMultipleCriteria(clients, providers, services, servicePoints, workStations,
                employees, providerServices, transactionTimePeriod, bookedTimePeriod, terms,
                priceRange, currencyCodes, paymentMethods, paid,
                completionStatuses, clientRatingRange, clientComments, providerRatingRange, providerDementis, null, null);
    }


    @Override
    public List<HistoricalTransaction> findByMultipleCriteria(List<Client> clients, List<Provider> providers, List<Service> services, List<ServicePoint> servicePoints, List<WorkStation> workStations,
                                                              List<Employee> employees, List<ProviderService> providerServices, Period transactionTimePeriod, Period bookedTimePeriod, List<Term> terms,
                                                              PriceRange priceRange, List<CurrencyCode> currencyCodes, List<PaymentMethod> paymentMethods, Boolean paid,
                                                              List<TransactionCompletionStatus> completionStatuses, RatingRange clientRatingRange, List<String> clientComments, RatingRange providerRatingRange, List<String> providerDementis,
                                                              Integer start, Integer limit) {
        return findByMultipleCriteria(clients, providers, services, servicePoints, workStations,
                employees, providerServices, transactionTimePeriod, bookedTimePeriod, terms,
                priceRange, currencyCodes, paymentMethods, paid,
                completionStatuses, clientRatingRange, clientComments, providerRatingRange, providerDementis, false, start, limit);
    }

    @Override
    public List<HistoricalTransaction> findByMultipleCriteriaEagerly(List<Client> clients, List<Provider> providers, List<Service> services, List<ServicePoint> servicePoints, List<WorkStation> workStations,
                                                                     List<Employee> employees, List<ProviderService> providerServices, Period transactionTimePeriod, Period bookedTimePeriod, List<Term> terms,
                                                                     PriceRange priceRange, List<CurrencyCode> currencyCodes, List<PaymentMethod> paymentMethods, Boolean paid,
                                                                     List<TransactionCompletionStatus> completionStatuses, RatingRange clientRatingRange, List<String> clientComments, RatingRange providerRatingRange, List<String> providerDementis) {
        return findByMultipleCriteriaEagerly(clients, providers, services, servicePoints, workStations,
                employees, providerServices, transactionTimePeriod, bookedTimePeriod, terms,
                priceRange, currencyCodes, paymentMethods, paid,
                completionStatuses, clientRatingRange, clientComments, providerRatingRange, providerDementis, null, null);
    }


    @Override
    public List<HistoricalTransaction> findByMultipleCriteriaEagerly(List<Client> clients, List<Provider> providers, List<Service> services, List<ServicePoint> servicePoints, List<WorkStation> workStations,
                                                                     List<Employee> employees, List<ProviderService> providerServices, Period transactionTimePeriod, Period bookedTimePeriod, List<Term> terms,
                                                                     PriceRange priceRange, List<CurrencyCode> currencyCodes, List<PaymentMethod> paymentMethods, Boolean paid,
                                                                     List<TransactionCompletionStatus> completionStatuses, RatingRange clientRatingRange, List<String> clientComments, RatingRange providerRatingRange, List<String> providerDementis,
                                                                     Integer start, Integer limit) {
        return findByMultipleCriteria(clients, providers, services, servicePoints, workStations,
                employees, providerServices, transactionTimePeriod, bookedTimePeriod, terms,
                priceRange, currencyCodes, paymentMethods, paid,
                completionStatuses, clientRatingRange, clientComments, providerRatingRange, providerDementis, true, start, limit);
    }

    // TODO below method
    private List<HistoricalTransaction> findByMultipleCriteria(List<Client> clients, List<Provider> providers, List<Service> services, List<ServicePoint> servicePoints, List<WorkStation> workStations,
                                                               List<Employee> employees, List<ProviderService> providerServices, Period transactionTimePeriod, Period bookedTimePeriod, List<Term> terms,
                                                               PriceRange priceRange, List<CurrencyCode> currencyCodes, List<PaymentMethod> paymentMethods, Boolean paid,
                                                               List<TransactionCompletionStatus> completionStatuses, RatingRange clientRatingRange, List<String> clientComments, RatingRange providerRatingRange, List<String> providerDementis,
                                                               Boolean eagerly, Integer start, Integer limit) {

        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<HistoricalTransaction> criteriaQuery = criteriaBuilder.createQuery(HistoricalTransaction.class);
        // FROM
        Root<HistoricalTransaction> historicalTransaction = criteriaQuery.from(HistoricalTransaction.class);
        // SELECT
        criteriaQuery.select(historicalTransaction).distinct(true);

        // INNER JOIN-s
        Join<HistoricalTransaction, Client> client = null;
        Join<HistoricalTransaction, Employee> employee = null;

        // WHERE PREDICATES
        List<Predicate> predicates = new ArrayList<>();

        if(clients != null && clients.size() > 0) {

            if(client == null) client = historicalTransaction.join(HistoricalTransaction_.client);

            predicates.add( client.in(clients) );
        }

        if(providers != null && providers.size() > 0) {

            predicates.add( historicalTransaction.get(HistoricalTransaction_.provider).in(providers) );
        }

        if(services != null && services.size() > 0) {

            predicates.add( historicalTransaction.get(HistoricalTransaction_.service).in(services) );
        }

        if(providerServices != null && providerServices.size() > 0) {

            predicates.add( historicalTransaction.get(HistoricalTransaction_.providerService).in(providerServices) );
        }

        if(servicePoints != null && servicePoints.size() > 0) {

            predicates.add( historicalTransaction.get(HistoricalTransaction_.servicePoint).in(servicePoints) );
        }

        if(workStations != null && workStations.size() > 0) {

            predicates.add( historicalTransaction.get(HistoricalTransaction_.workStation).in(workStations));
        }

        if(employees != null && employees.size() > 0) {

            if(employee == null) employee = historicalTransaction.join(Transaction_.employees);

            predicates.add( employee.in(employees) );
        }

        if(transactionTimePeriod != null) {

            if(transactionTimePeriod.getStartTime() != null)
                predicates.add( criteriaBuilder.greaterThanOrEqualTo( historicalTransaction.get(HistoricalTransaction_.transactionTime),
                        transactionTimePeriod.getStartTime()) );
            if(transactionTimePeriod.getEndTime() != null)
                predicates.add( criteriaBuilder.lessThanOrEqualTo( historicalTransaction.get(HistoricalTransaction_.transactionTime),
                        transactionTimePeriod.getEndTime()) );
        }

        if(bookedTimePeriod != null) {

            if(bookedTimePeriod.getStartTime() != null)
                predicates.add( criteriaBuilder.greaterThanOrEqualTo( historicalTransaction.get(HistoricalTransaction_.bookedTime),
                        bookedTimePeriod.getStartTime()) );

            if(bookedTimePeriod.getEndTime() != null)
                predicates.add( criteriaBuilder.lessThanOrEqualTo( historicalTransaction.get(HistoricalTransaction_.bookedTime),
                        bookedTimePeriod.getEndTime()) );
        }

        if(terms != null && terms.size() > 0) {

            predicates.add( historicalTransaction.get(HistoricalTransaction_.term).in(terms) );
        }

        if(priceRange != null) {

            if(priceRange.getMinPrice() != null)
                predicates.add( criteriaBuilder.greaterThanOrEqualTo( historicalTransaction.get(HistoricalTransaction_.price),
                        priceRange.getMinPrice()) );

            if(priceRange.getMaxPrice() != null)
                predicates.add( criteriaBuilder.lessThanOrEqualTo( historicalTransaction.get(HistoricalTransaction_.price),
                        priceRange.getMaxPrice()) );
        }

        if(currencyCodes != null && currencyCodes.size() > 0) {

            predicates.add( historicalTransaction.get(HistoricalTransaction_.priceCurrencyCode).in(currencyCodes) );
        }

        if(paymentMethods != null && paymentMethods.size() > 0) {

            predicates.add( historicalTransaction.get(HistoricalTransaction_.paymentMethod).in(paymentMethods) );
        }

        if(paid != null) {

            predicates.add( criteriaBuilder.equal(historicalTransaction.get(HistoricalTransaction_.paid), paid) );
        }

        if(completionStatuses != null && completionStatuses.size() > 0) {

            predicates.add( historicalTransaction.get(HistoricalTransaction_.completionStatus).in(completionStatuses) );
        }

        if(clientRatingRange != null) {

            if(clientRatingRange.getMinRating() != null)
                predicates.add( criteriaBuilder.greaterThanOrEqualTo( historicalTransaction.get(HistoricalTransaction_.clientRating),
                        clientRatingRange.getMinRating()) );

            if(clientRatingRange.getMaxRating() != null)
                predicates.add( criteriaBuilder.lessThanOrEqualTo( historicalTransaction.get(HistoricalTransaction_.clientRating),
                        clientRatingRange.getMaxRating()) );
        }

        if(clientComments != null && clientComments.size() > 0) {

            List<Predicate> orPredicates = new ArrayList<>();
            for(String clientComment : clientComments) {
                orPredicates.add( criteriaBuilder.like( historicalTransaction.get(HistoricalTransaction_.clientComment), "%" + clientComment + "%") );
            }

            predicates.add( criteriaBuilder.or(orPredicates.toArray(new Predicate[]{})) );
        }

        if(providerRatingRange != null) {

            if(providerRatingRange.getMinRating() != null)
                predicates.add( criteriaBuilder.greaterThanOrEqualTo( historicalTransaction.get(HistoricalTransaction_.providerRating),
                        providerRatingRange.getMinRating()) );

            if(providerRatingRange.getMaxRating() != null)
                predicates.add( criteriaBuilder.lessThanOrEqualTo( historicalTransaction.get(HistoricalTransaction_.providerRating),
                        providerRatingRange.getMaxRating()) );
        }

        if(providerDementis != null && providerDementis.size() > 0) {

            List<Predicate> orPredicates = new ArrayList<>();
            for(String providerDementi : providerDementis) {
                orPredicates.add( criteriaBuilder.like( historicalTransaction.get(HistoricalTransaction_.providerDementi), "%" + providerDementi + "%") );
            }

            predicates.add( criteriaBuilder.or(orPredicates.toArray(new Predicate[]{})) );
        }

        if(eagerly) {
            if(employee != null) {
                historicalTransaction.fetch("employees", JoinType.INNER);
            } else {
                historicalTransaction.fetch("employees", JoinType.LEFT);
            }
        }

        // WHERE predicate1 AND predicate2 AND ... AND predicateN
        criteriaQuery.where(predicates.toArray(new Predicate[] { }));

        TypedQuery<HistoricalTransaction> query = getEntityManager().createQuery(criteriaQuery);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public Long countByClient(Client client) {

        TypedQuery<Long> query = getEntityManager().createNamedQuery(HistoricalTransaction.COUNT_BY_CLIENT, Long.class);
        query.setParameter("client", client);
        return query.getSingleResult();
    }

    @Override
    public Long countByPaymentMethod(PaymentMethod paymentMethod) {

        TypedQuery<Long> query = getEntityManager().createNamedQuery(HistoricalTransaction.COUNT_BY_PAYMENT_METHOD, Long.class);
        query.setParameter("payment_method", paymentMethod);
        return query.getSingleResult();
    }

    @Override
    public Long countByProvider(Provider provider) {

        TypedQuery<Long> query = getEntityManager().createNamedQuery(HistoricalTransaction.COUNT_BY_PROVIDER, Long.class);
        query.setParameter("provider", provider);
        return query.getSingleResult();
    }

    @Override
    public Long countByService(Service service) {

        TypedQuery<Long> query = getEntityManager().createNamedQuery(HistoricalTransaction.COUNT_BY_SERVICE, Long.class);
        query.setParameter("service", service);
        return query.getSingleResult();
    }

    @Override
    public Long countByProviderService(ProviderService providerService) {

        TypedQuery<Long> query = getEntityManager().createNamedQuery(HistoricalTransaction.COUNT_BY_PROVIDER_SERVICE, Long.class);
        query.setParameter("provider_service", providerService);
        return query.getSingleResult();
    }

    @Override
    public Long countByEmployee(Employee employee) {

        TypedQuery<Long> query = getEntityManager().createNamedQuery(HistoricalTransaction.COUNT_BY_EMPLOYEE, Long.class);
        query.setParameter("employee", employee);
        return query.getSingleResult();
    }

    @Override
    public Long countByWorkStation(WorkStation workStation) {

        TypedQuery<Long> query = getEntityManager().createNamedQuery(HistoricalTransaction.COUNT_BY_WORK_STATION, Long.class);
        query.setParameter("work_station", workStation);
        return query.getSingleResult();
    }

    @Override
    public Long countByServicePoint(ServicePoint servicePoint) {

        TypedQuery<Long> query = getEntityManager().createNamedQuery(HistoricalTransaction.COUNT_BY_SERVICE_POINT, Long.class);
        query.setParameter("service_point", servicePoint);
        return query.getSingleResult();
    }

    @Override
    public Long countByTerm(Term term) {

        TypedQuery<Long> query = getEntityManager().createNamedQuery(HistoricalTransaction.COUNT_BY_TERM, Long.class);
        query.setParameter("term", term);
        return query.getSingleResult();
    }

    @Override
    public Integer deleteByClient(Client client) {

        Query query = getEntityManager().createNamedQuery(HistoricalTransaction.DELETE_BY_CLIENT);
        query.setParameter("client", client);
        return query.executeUpdate();
    }

    @Override
    public Integer deleteById(TransactionId transactionId) {

        Query query = getEntityManager().createNamedQuery(HistoricalTransaction.DELETE_BY_ID);
        query.setParameter("clientId", transactionId.getClient());
        query.setParameter("transaction_number", transactionId.getTransactionNumber());
        return query.executeUpdate();
    }
}
