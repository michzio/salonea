package pl.salonea.ejb.stateless;

import pl.salonea.ejb.interfaces.TransactionFacadeInterface;
import pl.salonea.entities.*;
import pl.salonea.entities.idclass.ProviderServiceId;
import pl.salonea.entities.idclass.ServicePointId;
import pl.salonea.entities.idclass.TransactionId;
import pl.salonea.entities.idclass.WorkStationId;
import pl.salonea.enums.CurrencyCode;
import pl.salonea.utils.Period;
import pl.salonea.utils.PriceRange;

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
 * Created by michzio on 23/08/2015.
 */
@Stateless
@LocalBean
public class TransactionFacade extends AbstractFacade<Transaction>
        implements TransactionFacadeInterface.Local, TransactionFacadeInterface.Remote {

    @Inject
    private EntityManager em;

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    public TransactionFacade() {
        super(Transaction.class);
    }

    @Override
    public Transaction createForClient(Long clientId, Transaction transaction) {

        Client foundClient = getEntityManager().find(Client.class, clientId);
        transaction.setClient(foundClient);

        // fetch and set entities based on detached ids
        ProviderServiceId providerServiceId = new ProviderServiceId(transaction.getProviderService().getProvider().getUserId(),
                                                                    transaction.getProviderService().getService().getServiceId());
        ProviderService providerService = getEntityManager().find(ProviderService.class, providerServiceId);
        ServicePointId servicePointId = new ServicePointId(transaction.getServicePoint().getProvider().getUserId(),
                                                           transaction.getServicePoint().getServicePointNumber());
        ServicePoint servicePoint = getEntityManager().find(ServicePoint.class, servicePointId);
        WorkStationId workStationId = new WorkStationId(transaction.getWorkStation().getServicePoint().getProvider().getUserId(),
                                                        transaction.getWorkStation().getServicePoint().getServicePointNumber(),
                                                        transaction.getWorkStation().getWorkStationNumber());
        WorkStation workStation = getEntityManager().find(WorkStation.class, workStationId);
        PaymentMethod paymentMethod = getEntityManager().find(PaymentMethod.class, transaction.getPaymentMethod().getId());
        Term term = getEntityManager().find(Term.class, transaction.getTerm().getTermId());

        // ... and set ...
        transaction.setProviderService(providerService);
        transaction.setServicePoint(servicePoint);
        transaction.setWorkStation(workStation);
        transaction.setPaymentMethod(paymentMethod);
        transaction.setTerm(term);

        // ... and set employees ...
        if(transaction.getEmployeeIds() != null && transaction.getEmployeeIds().size() > 0) {
            transaction.setEmployees(new LinkedHashSet<>());

            for (Long employeeId : transaction.getEmployeeIds()) {
                Employee employee = getEntityManager().find(Employee.class, employeeId);
                transaction.getEmployees().add(employee);
            }
        }

        return create(transaction);
    }

    @Override
    public Transaction update(TransactionId transactionId, Transaction transaction) {

        return update(transactionId, transaction, true);
    }

    @Override
    public Transaction update(TransactionId transactionId, Transaction transaction, Boolean retainTransientFields) {

        Client foundClient = getEntityManager().find(Client.class, transactionId.getClient());
        transaction.setClient(foundClient);
        transaction.setTransactionNumber(transactionId.getTransactionNumber());

        Transaction currentTransaction = null;

        if(retainTransientFields) {
            // keep current collection attributes of resource (and other marked @XmlTransient)
            currentTransaction = findByIdEagerly(transactionId);
            if (currentTransaction != null) {
                transaction.setEmployees(currentTransaction.getEmployees());
            }
        }

        // fetch and set entities based on detached ids
        ProviderServiceId providerServiceId = new ProviderServiceId(transaction.getProviderService().getProvider().getUserId(),
                                                                    transaction.getProviderService().getService().getServiceId());
        ProviderService providerService = null;
        if(currentTransaction != null
                && currentTransaction.getProviderService().getProvider().getUserId() == providerServiceId.getProvider()
                && currentTransaction.getProviderService().getService().getServiceId() == providerServiceId.getService() ) {
            // if the same provider service is already set on current transaction
            providerService = currentTransaction.getProviderService();
        } else {
            providerService = getEntityManager().find(ProviderService.class, providerServiceId);
        }

        ServicePointId servicePointId = new ServicePointId(transaction.getServicePoint().getProvider().getUserId(),
                                                           transaction.getServicePoint().getServicePointNumber());
        ServicePoint servicePoint = null;
        if(currentTransaction != null
                && currentTransaction.getServicePoint().getProvider().getUserId() == servicePointId.getProvider()
                && currentTransaction.getServicePoint().getServicePointNumber() == servicePointId.getServicePointNumber()) {
            // if the same service point is already set on current transaction
            servicePoint = currentTransaction.getServicePoint();
        } else {
            servicePoint = getEntityManager().find(ServicePoint.class, servicePointId);
        }

        WorkStationId workStationId = new WorkStationId(transaction.getWorkStation().getServicePoint().getProvider().getUserId(),
                                                        transaction.getWorkStation().getServicePoint().getServicePointNumber(),
                                                        transaction.getWorkStation().getWorkStationNumber());

        WorkStation workStation = null;
        if(currentTransaction != null
                && currentTransaction.getWorkStation().getServicePoint().getProvider().getUserId() == workStationId.getServicePoint().getProvider()
                && currentTransaction.getWorkStation().getServicePoint().getServicePointNumber() == workStationId.getServicePoint().getServicePointNumber()
                && currentTransaction.getWorkStation().getWorkStationNumber() == workStationId.getWorkStationNumber()) {
            // if the same work station is already set on current transaction
            workStation = currentTransaction.getWorkStation();
        } else {
            workStation = getEntityManager().find(WorkStation.class, workStationId);
        }

        PaymentMethod paymentMethod = null;
        if(currentTransaction != null
                && currentTransaction.getPaymentMethod().getId() == transaction.getPaymentMethod().getId()) {
            // if the same payment method is already set on current transaction
            paymentMethod = currentTransaction.getPaymentMethod();
        } else {
            paymentMethod = getEntityManager().find(PaymentMethod.class, transaction.getPaymentMethod().getId());
        }

        Term term = null;
        if(currentTransaction != null
                && currentTransaction.getTerm().getTermId() == transaction.getTerm().getTermId()) {
            // if the same term is already set on current transaction
            term = currentTransaction.getTerm();
        } else {
            term = getEntityManager().find(Term.class, transaction.getTerm().getTermId());
        }

        // ... and set ...
        transaction.setProviderService(providerService);
        transaction.setServicePoint(servicePoint);
        transaction.setWorkStation(workStation);
        transaction.setPaymentMethod(paymentMethod);
        transaction.setTerm(term);

        // ... and set employees ...
        if(transaction.getEmployeeIds() != null && transaction.getEmployeeIds().size() > 0) {
            transaction.setEmployees(new LinkedHashSet<>());

            for (Long employeeId : transaction.getEmployeeIds()) {
                Employee employee = getEntityManager().find(Employee.class, employeeId);
                transaction.getEmployees().add(employee);
            }
        }

        return update(transaction);
    }

    @Override
    public List<Transaction> findAllEagerly() {
        return findAllEagerly(null, null);
    }

    @Override
    public List<Transaction> findAllEagerly(Integer start, Integer limit) {

        TypedQuery<Transaction> query = getEntityManager().createNamedQuery(Transaction.FIND_ALL_EAGERLY, Transaction.class);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public Transaction findByIdEagerly(TransactionId transactionId) {

        TypedQuery<Transaction> query = getEntityManager().createNamedQuery(Transaction.FIND_BY_ID_EAGERLY, Transaction.class);
        query.setParameter("clientId", transactionId.getClient());
        query.setParameter("transaction_number", transactionId.getTransactionNumber());
        try {
            return query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            return null;
        }
    }

    @Override
    public List<Transaction> findByTransactionTime(Date startTime, Date endTime) {
        return findByTransactionTime(startTime, endTime, null, null);
    }

    @Override
    public List<Transaction> findByTransactionTime(Date startTime, Date endTime, Integer start, Integer limit) {

        TypedQuery<Transaction> query = getEntityManager().createNamedQuery(Transaction.FIND_BY_TRANSACTION_TIME, Transaction.class);
        query.setParameter("start_time", startTime);
        query.setParameter("end_time", endTime);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<Transaction> findByBookedTime(Date startTime, Date endTime) {
        return findByBookedTime(startTime, endTime, null, null);
    }

    @Override
    public List<Transaction> findByBookedTime(Date startTime, Date endTime, Integer start, Integer limit) {

        TypedQuery<Transaction> query = getEntityManager().createNamedQuery(Transaction.FIND_BY_BOOKED_TIME, Transaction.class);
        query.setParameter("start_time", startTime);
        query.setParameter("end_time", endTime);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<Transaction> findOnlyPaid() {
        return findOnlyPaid(null, null);
    }

    @Override
    public List<Transaction> findOnlyPaid(Integer start, Integer limit) {

        TypedQuery<Transaction> query = getEntityManager().createNamedQuery(Transaction.FIND_ONLY_PAID, Transaction.class);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<Transaction> findOnlyUnpaid() {
        return findOnlyUnpaid(null, null);
    }

    @Override
    public List<Transaction> findOnlyUnpaid(Integer start, Integer limit) {

        TypedQuery<Transaction> query = getEntityManager().createNamedQuery(Transaction.FIND_ONLY_UNPAID, Transaction.class);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<Transaction> findByPriceRange(Double minPrice, Double maxPrice) {
        return findByPriceRange(minPrice, maxPrice, null, null);
    }

    @Override
    public List<Transaction> findByPriceRange(Double minPrice, Double maxPrice, Integer start, Integer limit) {

        TypedQuery<Transaction> query = getEntityManager().createNamedQuery(Transaction.FIND_BY_PRICE_RANGE, Transaction.class);
        query.setParameter("min_price", minPrice);
        query.setParameter("max_price", maxPrice);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<Transaction> findByCurrencyCode(CurrencyCode currencyCode) {
        return findByCurrencyCode(currencyCode, null, null);
    }

    @Override
    public List<Transaction> findByCurrencyCode(CurrencyCode currencyCode, Integer start, Integer limit) {

        TypedQuery<Transaction> query = getEntityManager().createNamedQuery(Transaction.FIND_BY_CURRENCY_CODE, Transaction.class);
        query.setParameter("currency_code", currencyCode);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<Transaction> findByPriceRangeAndCurrencyCode(Double minPrice, Double maxPrice, CurrencyCode currencyCode) {
        return findByPriceRangeAndCurrencyCode(minPrice, maxPrice, currencyCode, null, null);
    }

    @Override
    public List<Transaction> findByPriceRangeAndCurrencyCode(Double minPrice, Double maxPrice, CurrencyCode currencyCode, Integer start, Integer limit) {

        TypedQuery<Transaction> query = getEntityManager().createNamedQuery(Transaction.FIND_BY_PRICE_RANGE_AND_CURRENCY_CODE, Transaction.class);
        query.setParameter("min_price", minPrice);
        query.setParameter("max_price", maxPrice);
        query.setParameter("currency_code", currencyCode);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<Transaction> findByClient(Client client) {
        return findByClient(client, null, null);
    }

    @Override
    public List<Transaction> findByClient(Client client, Integer start, Integer limit) {

        TypedQuery<Transaction> query = getEntityManager().createNamedQuery(Transaction.FIND_BY_CLIENT, Transaction.class);
        query.setParameter("client", client);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<Transaction> findByClientEagerly(Client client) {
        return findByClientEagerly(client, null, null);
    }

    @Override
    public List<Transaction> findByClientEagerly(Client client, Integer start, Integer limit) {

        TypedQuery<Transaction> query = getEntityManager().createNamedQuery(Transaction.FIND_BY_CLIENT_EAGERLY, Transaction.class);
        query.setParameter("client", client);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<Transaction> findByClientAndTransactionTime(Client client, Date startTime, Date endTime) {
        return findByClientAndTransactionTime(client, startTime, endTime, null, null);
    }

    @Override
    public List<Transaction> findByClientAndTransactionTime(Client client, Date startTime, Date endTime, Integer start, Integer limit) {

        TypedQuery<Transaction> query = getEntityManager().createNamedQuery(Transaction.FIND_BY_CLIENT_AND_TRANSACTION_TIME, Transaction.class);
        query.setParameter("client", client);
        query.setParameter("start_time", startTime);
        query.setParameter("end_time", endTime);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<Transaction> findByClientAndBookedTime(Client client, Date startTime, Date endTime) {
        return findByClientAndBookedTime(client, startTime, endTime, null, null);
    }

    @Override
    public List<Transaction> findByClientAndBookedTime(Client client, Date startTime, Date endTime, Integer start, Integer limit) {

        TypedQuery<Transaction> query = getEntityManager().createNamedQuery(Transaction.FIND_BY_CLIENT_AND_BOOKED_TIME, Transaction.class);
        query.setParameter("client", client);
        query.setParameter("start_time", startTime);
        query.setParameter("end_time", endTime);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<Transaction> findByClientOnlyPaid(Client client) {
        return findByClientOnlyPaid(client, null, null);
    }

    @Override
    public List<Transaction> findByClientOnlyPaid(Client client, Integer start, Integer limit) {

        TypedQuery<Transaction> query = getEntityManager().createNamedQuery(Transaction.FIND_BY_CLIENT_ONLY_PAID, Transaction.class);
        query.setParameter("client", client);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<Transaction> findByClientOnlyUnpaid(Client client) {
        return findByClientOnlyUnpaid(client, null, null);
    }

    @Override
    public List<Transaction> findByClientOnlyUnpaid(Client client, Integer start, Integer limit) {

        TypedQuery<Transaction> query = getEntityManager().createNamedQuery(Transaction.FIND_BY_CLIENT_ONLY_UNPAID, Transaction.class);
        query.setParameter("client", client);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<Transaction> findByClientAndPriceRange(Client client, Double minPrice, Double maxPrice) {
        return findByClientAndPriceRange(client, minPrice, maxPrice, null, null);
    }

    @Override
    public List<Transaction> findByClientAndPriceRange(Client client, Double minPrice, Double maxPrice, Integer start, Integer limit) {

        TypedQuery<Transaction> query = getEntityManager().createNamedQuery(Transaction.FIND_BY_CLIENT_AND_PRICE_RANGE, Transaction.class);
        query.setParameter("client", client);
        query.setParameter("min_price", minPrice);
        query.setParameter("max_price", maxPrice);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<Transaction> findByClientAndCurrencyCode(Client client, CurrencyCode currencyCode) {
        return findByClientAndCurrencyCode(client, currencyCode, null, null);
    }

    @Override
    public List<Transaction> findByClientAndCurrencyCode(Client client, CurrencyCode currencyCode, Integer start, Integer limit) {

        TypedQuery<Transaction> query = getEntityManager().createNamedQuery(Transaction.FIND_BY_CLIENT_AND_CURRENCY_CODE, Transaction.class);
        query.setParameter("client", client);
        query.setParameter("currency_code", currencyCode);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<Transaction> findByClientAndPriceRangeAndCurrencyCode(Client client, Double minPrice, Double maxPrice, CurrencyCode currencyCode) {
        return findByClientAndPriceRangeAndCurrencyCode(client, minPrice, maxPrice, currencyCode, null, null);
    }

    @Override
    public List<Transaction> findByClientAndPriceRangeAndCurrencyCode(Client client, Double minPrice, Double maxPrice, CurrencyCode currencyCode, Integer start, Integer limit) {

        TypedQuery<Transaction> query = getEntityManager().createNamedQuery(Transaction.FIND_BY_CLIENT_AND_PRICE_RANGE_AND_CURRENCY_CODE, Transaction.class);
        query.setParameter("client", client);
        query.setParameter("min_price", minPrice);
        query.setParameter("max_price", maxPrice);
        query.setParameter("currency_code", currencyCode);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<Transaction> findByPaymentMethod(PaymentMethod paymentMethod) {
        return findByPaymentMethod(paymentMethod, null, null);
    }

    @Override
    public List<Transaction> findByPaymentMethod(PaymentMethod paymentMethod, Integer start, Integer limit) {

        TypedQuery<Transaction> query = getEntityManager().createNamedQuery(Transaction.FIND_BY_PAYMENT_METHOD, Transaction.class);
        query.setParameter("payment_method", paymentMethod);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<Transaction> findByPaymentMethodEagerly(PaymentMethod paymentMethod) {
        return findByPaymentMethodEagerly(paymentMethod, null, null);
    }

    @Override
    public List<Transaction> findByPaymentMethodEagerly(PaymentMethod paymentMethod, Integer start, Integer limit) {

        TypedQuery<Transaction> query = getEntityManager().createNamedQuery(Transaction.FIND_BY_PAYMENT_METHOD_EAGERLY, Transaction.class);
        query.setParameter("payment_method", paymentMethod);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<Transaction> findByProvider(Provider provider) {
        return findByProvider(provider, null, null);
    }

    @Override
    public List<Transaction> findByProvider(Provider provider, Integer start, Integer limit) {

        TypedQuery<Transaction> query = getEntityManager().createNamedQuery(Transaction.FIND_BY_PROVIDER, Transaction.class);
        query.setParameter("provider", provider);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<Transaction> findByProviderEagerly(Provider provider) {
        return findByProviderEagerly(provider, null, null);
    }

    @Override
    public List<Transaction> findByProviderEagerly(Provider provider, Integer start, Integer limit) {

        TypedQuery<Transaction> query = getEntityManager().createNamedQuery(Transaction.FIND_BY_PROVIDER_EAGERLY, Transaction.class);
        query.setParameter("provider", provider);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<Transaction> findByService(Service service) {
        return findByService(service, null, null);
    }

    @Override
    public List<Transaction> findByService(Service service, Integer start, Integer limit) {

        TypedQuery<Transaction> query = getEntityManager().createNamedQuery(Transaction.FIND_BY_SERVICE, Transaction.class);
        query.setParameter("service", service);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<Transaction> findByServiceEagerly(Service service) {
        return findByServiceEagerly(service, null, null);
    }

    @Override
    public List<Transaction> findByServiceEagerly(Service service, Integer start, Integer limit) {

        TypedQuery<Transaction> query = getEntityManager().createNamedQuery(Transaction.FIND_BY_SERVICE_EAGERLY, Transaction.class);
        query.setParameter("service", service);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<Transaction> findByProviderService(ProviderService providerService) {
        return findByProviderService(providerService, null, null);
    }

    @Override
    public List<Transaction> findByProviderService(ProviderService providerService, Integer start, Integer limit) {

        TypedQuery<Transaction> query = getEntityManager().createNamedQuery(Transaction.FIND_BY_PROVIDER_SERVICE, Transaction.class);
        query.setParameter("provider_service", providerService);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<Transaction> findByProviderServiceEagerly(ProviderService providerService) {
        return findByProviderServiceEagerly(providerService, null, null);
    }

    @Override
    public List<Transaction> findByProviderServiceEagerly(ProviderService providerService, Integer start, Integer limit) {

        TypedQuery<Transaction> query = getEntityManager().createNamedQuery(Transaction.FIND_BY_PROVIDER_SERVICE_EAGERLY, Transaction.class);
        query.setParameter("provider_service", providerService);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<Transaction> findByEmployee(Employee employee) {
        return findByEmployee(employee, null, null);
    }

    @Override
    public List<Transaction> findByEmployee(Employee employee, Integer start, Integer limit) {

        TypedQuery<Transaction> query = getEntityManager().createNamedQuery(Transaction.FIND_BY_EMPLOYEE, Transaction.class);
        query.setParameter("employee", employee);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<Transaction> findByEmployeeEagerly(Employee employee) {
        return findByEmployeeEagerly(employee, null, null);
    }

    @Override
    public List<Transaction> findByEmployeeEagerly(Employee employee, Integer start, Integer limit) {

        TypedQuery<Transaction> query = getEntityManager().createNamedQuery(Transaction.FIND_BY_EMPLOYEE_EAGERLY, Transaction.class);
        query.setParameter("employee", employee);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<Transaction> findByWorkStation(WorkStation workStation) {
        return findByWorkStation(workStation, null, null);
    }

    @Override
    public List<Transaction> findByWorkStation(WorkStation workStation, Integer start, Integer limit) {

        TypedQuery<Transaction> query = getEntityManager().createNamedQuery(Transaction.FIND_BY_WORK_STATION, Transaction.class);
        query.setParameter("work_station", workStation);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<Transaction> findByWorkStationEagerly(WorkStation workStation) {
        return findByWorkStationEagerly(workStation, null, null);
    }

    @Override
    public List<Transaction> findByWorkStationEagerly(WorkStation workStation, Integer start, Integer limit) {

        TypedQuery<Transaction> query = getEntityManager().createNamedQuery(Transaction.FIND_BY_WORK_STATION_EAGERLY, Transaction.class);
        query.setParameter("work_station", workStation);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<Transaction> findByServicePoint(ServicePoint servicePoint) {
        return findByServicePoint(servicePoint, null, null);
    }

    @Override
    public List<Transaction> findByServicePoint(ServicePoint servicePoint, Integer start, Integer limit) {

        TypedQuery<Transaction> query = getEntityManager().createNamedQuery(Transaction.FIND_BY_SERVICE_POINT, Transaction.class);
        query.setParameter("service_point", servicePoint);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<Transaction> findByServicePointEagerly(ServicePoint servicePoint) {
        return findByServicePointEagerly(servicePoint, null, null);
    }

    @Override
    public List<Transaction> findByServicePointEagerly(ServicePoint servicePoint, Integer start, Integer limit) {

        TypedQuery<Transaction> query = getEntityManager().createNamedQuery(Transaction.FIND_BY_SERVICE_POINT_EAGERLY, Transaction.class);
        query.setParameter("service_point", servicePoint);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<Transaction> findByTerm(Term term) {
        return findByTerm(term, null, null);
    }

    @Override
    public List<Transaction> findByTerm(Term term, Integer start, Integer limit) {

        TypedQuery<Transaction> query = getEntityManager().createNamedQuery(Transaction.FIND_BY_TERM, Transaction.class);
        query.setParameter("term", term);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<Transaction> findByTermEagerly(Term term) {
        return findByTermEagerly(term, null, null);
    }

    @Override
    public List<Transaction> findByTermEagerly(Term term, Integer start, Integer limit) {

        TypedQuery<Transaction> query = getEntityManager().createNamedQuery(Transaction.FIND_BY_TERM_EAGERLY, Transaction.class);
        query.setParameter("term", term);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    // other
    @Override
    public List<Transaction> findByClientAndPaymentMethod(Client client, PaymentMethod paymentMethod) {
        return findByClientAndPaymentMethod(client, paymentMethod, null, null);
    }

    @Override
    public List<Transaction> findByClientAndPaymentMethod(Client client, PaymentMethod paymentMethod, Integer start, Integer limit) {

        TypedQuery<Transaction> query = getEntityManager().createNamedQuery(Transaction.FIND_BY_CLIENT_AND_PAYMENT_METHOD, Transaction.class);
        query.setParameter("client", client);
        query.setParameter("payment_method", paymentMethod);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<Transaction> findByClientAndProvider(Client client, Provider provider) {
        return findByClientAndProvider(client, provider, null, null);
    }

    @Override
    public List<Transaction> findByClientAndProvider(Client client, Provider provider, Integer start, Integer limit) {

        TypedQuery<Transaction> query = getEntityManager().createNamedQuery(Transaction.FIND_BY_CLIENT_AND_PROVIDER, Transaction.class);
        query.setParameter("client", client);
        query.setParameter("provider", provider);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<Transaction> findByClientAndService(Client client, Service service) {
        return findByClientAndService(client, service, null, null);
    }

    @Override
    public List<Transaction> findByClientAndService(Client client, Service service, Integer start, Integer limit) {

        TypedQuery<Transaction> query = getEntityManager().createNamedQuery(Transaction.FIND_BY_CLIENT_AND_SERVICE, Transaction.class);
        query.setParameter("client", client);
        query.setParameter("service", service);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }

        return query.getResultList();
    }

    @Override
    public List<Transaction> findByClientAndProviderService(Client client, ProviderService providerService) {
        return findByClientAndProviderService(client, providerService, null, null);
    }

    @Override
    public List<Transaction> findByClientAndProviderService(Client client, ProviderService providerService, Integer start, Integer limit) {

        TypedQuery<Transaction> query = getEntityManager().createNamedQuery(Transaction.FIND_BY_CLIENT_AND_PROVIDER_SERVICE, Transaction.class);
        query.setParameter("client", client);
        query.setParameter("provider_service", providerService);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<Transaction> findByClientAndEmployee(Client client, Employee employee) {
        return findByClientAndEmployee(client, employee, null, null);
    }

    @Override
    public List<Transaction> findByClientAndEmployee(Client client, Employee employee, Integer start, Integer limit) {

        TypedQuery<Transaction> query = getEntityManager().createNamedQuery(Transaction.FIND_BY_CLIENT_AND_EMPLOYEE, Transaction.class);
        query.setParameter("client", client);
        query.setParameter("employee", employee);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<Transaction> findByClientAndWorkStation(Client client, WorkStation workStation) {
        return findByClientAndWorkStation(client, workStation, null, null);
    }

    @Override
    public List<Transaction> findByClientAndWorkStation(Client client, WorkStation workStation, Integer start, Integer limit) {

        TypedQuery<Transaction> query = getEntityManager().createNamedQuery(Transaction.FIND_BY_CLIENT_AND_WORK_STATION, Transaction.class);
        query.setParameter("client", client);
        query.setParameter("work_station", workStation);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<Transaction> findByClientAndServicePoint(Client client, ServicePoint servicePoint) {
        return findByClientAndServicePoint(client, servicePoint, null, null);
    }

    @Override
    public List<Transaction> findByClientAndServicePoint(Client client, ServicePoint servicePoint, Integer start, Integer limit) {

        TypedQuery<Transaction> query = getEntityManager().createNamedQuery(Transaction.FIND_BY_CLIENT_AND_SERVICE_POINT, Transaction.class);
        query.setParameter("client", client);
        query.setParameter("service_point", servicePoint);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<Transaction> findByClientAndTerm(Client client, Term term) {
        return findByClientAndTerm(client, term, null, null);
    }

    @Override
    public List<Transaction> findByClientAndTerm(Client client, Term term, Integer start, Integer limit) {

        TypedQuery<Transaction> query = getEntityManager().createNamedQuery(Transaction.FIND_BY_CLIENT_AND_TERM, Transaction.class);
        query.setParameter("client", client);
        query.setParameter("term", term);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<Transaction> findByMultipleCriteria(List<Client> clients, List<Provider> providers, List<Service> services,
                                                    List<ServicePoint> servicePoints, List<WorkStation> workStations, List<Employee> employees,
                                                    List<ProviderService> providerServices, Period transactionTimePeriod, Period bookedTimePeriod,
                                                    List<Term> terms, PriceRange priceRange, List<CurrencyCode> currencyCodes,
                                                    List<PaymentMethod> paymentMethods, Boolean paid) {

        return findByMultipleCriteria(clients, providers, services, servicePoints, workStations, employees, providerServices, transactionTimePeriod, bookedTimePeriod, terms, priceRange, currencyCodes, paymentMethods, paid, null, null);
    }

    @Override
    public List<Transaction> findByMultipleCriteria(List<Client> clients, List<Provider> providers, List<Service> services,
                                                    List<ServicePoint> servicePoints, List<WorkStation> workStations, List<Employee> employees,
                                                    List<ProviderService> providerServices, Period transactionTimePeriod, Period bookedTimePeriod,
                                                    List<Term> terms, PriceRange priceRange, List<CurrencyCode> currencyCodes,
                                                    List<PaymentMethod> paymentMethods, Boolean paid, Integer start, Integer limit) {

        return findByMultipleCriteria(clients, providers, services, servicePoints, workStations, employees, providerServices, transactionTimePeriod, bookedTimePeriod, terms, priceRange, currencyCodes, paymentMethods, paid, false, start, limit);
    }

    @Override
    public List<Transaction> findByMultipleCriteriaEagerly(List<Client> clients, List<Provider> providers, List<Service> services,
                                                           List<ServicePoint> servicePoints, List<WorkStation> workStations, List<Employee> employees,
                                                           List<ProviderService> providerServices, Period transactionTimePeriod, Period bookedTimePeriod,
                                                           List<Term> terms, PriceRange priceRange, List<CurrencyCode> currencyCodes,
                                                           List<PaymentMethod> paymentMethods, Boolean paid) {

        return findByMultipleCriteriaEagerly(clients, providers, services, servicePoints, workStations, employees, providerServices, transactionTimePeriod, bookedTimePeriod, terms, priceRange, currencyCodes, paymentMethods, paid, null, null);
    }

    @Override
    public List<Transaction> findByMultipleCriteriaEagerly(List<Client> clients, List<Provider> providers, List<Service> services,
                                                           List<ServicePoint> servicePoints, List<WorkStation> workStations, List<Employee> employees,
                                                           List<ProviderService> providerServices, Period transactionTimePeriod, Period bookedTimePeriod,
                                                           List<Term> terms, PriceRange priceRange, List<CurrencyCode> currencyCodes,
                                                           List<PaymentMethod> paymentMethods, Boolean paid, Integer start, Integer limit) {

        return findByMultipleCriteria(clients, providers, services, servicePoints, workStations, employees, providerServices, transactionTimePeriod, bookedTimePeriod, terms, priceRange, currencyCodes, paymentMethods, paid, true, start, limit);
    }

    private List<Transaction> findByMultipleCriteria(List<Client> clients, List<Provider> providers, List<Service> services,
                                                     List<ServicePoint> servicePoints, List<WorkStation> workStations, List<Employee> employees,
                                                     List<ProviderService> providerServices, Period transactionTimePeriod, Period bookedTimePeriod,
                                                     List<Term> terms, PriceRange priceRange, List<CurrencyCode> currencyCodes,
                                                     List<PaymentMethod> paymentMethods, Boolean paid, Boolean eagerly, Integer start, Integer limit) {

        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Transaction> criteriaQuery = criteriaBuilder.createQuery(Transaction.class);
        // FROM
        Root<Transaction> transaction = criteriaQuery.from(Transaction.class);
        // SELECT
        criteriaQuery.select(transaction).distinct(true);

        // INNER JOIN-s
        Join<Transaction, Client> client = null;
        Join<Transaction, Employee> employee = null;

        // WHERE PREDICATES
        List<Predicate> predicates = new ArrayList<>();

        if(clients != null && clients.size() > 0) {

            if(client == null) client = transaction.join(Transaction_.client);

            predicates.add( client.in(clients) );
        }

        if(providers != null && providers.size() > 0) {

            predicates.add( transaction.get(Transaction_.provider).in(providers) );
        }

        if(services != null && services.size() > 0) {

            predicates.add( transaction.get(Transaction_.service).in(services) );
        }

        if(providerServices != null && providerServices.size() > 0) {

            predicates.add( transaction.get(Transaction_.providerService).in(providerServices) );
        }

        if(servicePoints != null && servicePoints.size() > 0) {

            predicates.add( transaction.get(Transaction_.servicePoint).in(servicePoints) );
        }

        if(workStations != null && workStations.size() > 0) {

            predicates.add(transaction.get(Transaction_.workStation).in(workStations));
        }

        if(employees != null && employees.size() > 0) {

            if(employee == null) employee = transaction.join(Transaction_.employees);

            predicates.add( employee.in(employees) );
        }

        if(transactionTimePeriod != null) {

            if(transactionTimePeriod.getStartTime() != null)
                predicates.add( criteriaBuilder.greaterThanOrEqualTo( transaction.get(Transaction_.transactionTime),
                                                                      transactionTimePeriod.getStartTime()) );
            if(transactionTimePeriod.getEndTime() != null)
                predicates.add( criteriaBuilder.lessThanOrEqualTo( transaction.get(Transaction_.transactionTime),
                                                                   transactionTimePeriod.getEndTime()) );
        }

        if(bookedTimePeriod != null) {

            if(bookedTimePeriod.getStartTime() != null)
                predicates.add( criteriaBuilder.greaterThanOrEqualTo( transaction.get(Transaction_.bookedTime),
                                                                      bookedTimePeriod.getStartTime()) );

            if(bookedTimePeriod.getEndTime() != null)
                predicates.add( criteriaBuilder.lessThanOrEqualTo( transaction.get(Transaction_.bookedTime),
                                                                    bookedTimePeriod.getEndTime()) );
        }

        if(terms != null && terms.size() > 0) {

            predicates.add( transaction.get(Transaction_.term).in(terms) );
        }

        if(priceRange != null) {

            if(priceRange.getMinPrice() != null)
                predicates.add( criteriaBuilder.greaterThanOrEqualTo( transaction.get(Transaction_.price),
                                                                      priceRange.getMinPrice()) );

            if(priceRange.getMaxPrice() != null)
                predicates.add( criteriaBuilder.lessThanOrEqualTo( transaction.get(Transaction_.price),
                                                                    priceRange.getMaxPrice()) );
        }

        if(currencyCodes != null && currencyCodes.size() > 0) {

            predicates.add( transaction.get(Transaction_.priceCurrencyCode).in(currencyCodes) );
        }

        if(paymentMethods != null && paymentMethods.size() > 0) {

            predicates.add( transaction.get(Transaction_.paymentMethod).in(paymentMethods) );
        }

        if(paid != null) {

            predicates.add( criteriaBuilder.equal(transaction.get(Transaction_.paid), paid) );
        }

        if(eagerly) {
            if(employee != null) {
                transaction.fetch("employees", JoinType.INNER);
            } else {
                transaction.fetch("employees", JoinType.LEFT);
            }
        }

        // WHERE predicate1 AND predicate2 AND ... AND predicateN
        criteriaQuery.where(predicates.toArray(new Predicate[] { }));

        TypedQuery<Transaction> query = getEntityManager().createQuery(criteriaQuery);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public Long countByClient(Client client) {

        TypedQuery<Long> query = getEntityManager().createNamedQuery(Transaction.COUNT_BY_CLIENT, Long.class);
        query.setParameter("client", client);
        return query.getSingleResult();
    }

    @Override
    public Long countByPaymentMethod(PaymentMethod paymentMethod) {

        TypedQuery<Long> query = getEntityManager().createNamedQuery(Transaction.COUNT_BY_PAYMENT_METHOD, Long.class);
        query.setParameter("payment_method", paymentMethod);
        return query.getSingleResult();
    }

    @Override
    public Long countByProvider(Provider provider) {

        TypedQuery<Long> query = getEntityManager().createNamedQuery(Transaction.COUNT_BY_PROVIDER, Long.class);
        query.setParameter("provider", provider);
        return query.getSingleResult();
    }

    @Override
    public Long countByService(Service service) {

        TypedQuery<Long> query = getEntityManager().createNamedQuery(Transaction.COUNT_BY_SERVICE, Long.class);
        query.setParameter("service", service);
        return query.getSingleResult();
    }

    @Override
    public Long countByProviderService(ProviderService providerService) {

        TypedQuery<Long> query = getEntityManager().createNamedQuery(Transaction.COUNT_BY_PROVIDER_SERVICE, Long.class);
        query.setParameter("provider_service", providerService);
        return query.getSingleResult();
    }

    @Override
    public Long countByEmployee(Employee employee) {

        TypedQuery<Long> query = getEntityManager().createNamedQuery(Transaction.COUNT_BY_EMPLOYEE, Long.class);
        query.setParameter("employee", employee);
        return query.getSingleResult();
    }

    @Override
    public Long countByWorkStation(WorkStation workStation) {

        TypedQuery<Long> query = getEntityManager().createNamedQuery(Transaction.COUNT_BY_WORK_STATION, Long.class);
        query.setParameter("work_station", workStation);
        return query.getSingleResult();
    }

    @Override
    public Long countByServicePoint(ServicePoint servicePoint) {

        TypedQuery<Long> query = getEntityManager().createNamedQuery(Transaction.COUNT_BY_SERVICE_POINT, Long.class);
        query.setParameter("service_point", servicePoint);
        return query.getSingleResult();
    }

    @Override
    public Long countByTerm(Term term) {

        TypedQuery<Long> query = getEntityManager().createNamedQuery(Transaction.COUNT_BY_TERM, Long.class);
        query.setParameter("term", term);
        return query.getSingleResult();
    }

    @Override
    public Integer deleteByClient(Client client) {

        Query query = getEntityManager().createNamedQuery(Transaction.DELETE_BY_CLIENT);
        query.setParameter("client", client);
        return query.executeUpdate();
    }

    @Override
    public Integer deleteById(TransactionId transactionId) {

        Query query = getEntityManager().createNamedQuery(Transaction.DELETE_BY_ID);
        query.setParameter("clientId", transactionId.getClient());
        query.setParameter("transaction_number", transactionId.getTransactionNumber());
        return query.executeUpdate();
    }
}
