package pl.salonea.ejb.stateless;

import pl.salonea.ejb.interfaces.TransactionFacadeInterface;
import pl.salonea.entities.*;
import pl.salonea.enums.CurrencyCode;
import pl.salonea.mapped_superclasses.AbstractTransaction_;
import pl.salonea.utils.Period;
import pl.salonea.utils.PriceRange;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import javax.persistence.metamodel.SetAttribute;
import java.util.ArrayList;
import java.util.Date;
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
    public List<Transaction> findByClient(Client client) {
        return findByClient(client, null, null);
    }

    @Override
    public List<Transaction> findByClient(Client client, Integer start, Integer offset) {

        TypedQuery<Transaction> query = getEntityManager().createNamedQuery(Transaction.FIND_BY_CLIENT, Transaction.class);
        query.setParameter("client", client);
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public List<Transaction> findByClientAndTransactionTime(Client client, Date startTime, Date endTime) {
        return findByClientAndTransactionTime(client, startTime, endTime, null, null);
    }

    @Override
    public List<Transaction> findByClientAndTransactionTime(Client client, Date startTime, Date endTime, Integer start, Integer offset) {

        TypedQuery<Transaction> query = getEntityManager().createNamedQuery(Transaction.FIND_BY_CLIENT_AND_TRANSACTION_TIME, Transaction.class);
        query.setParameter("client", client);
        query.setParameter("start_time", startTime);
        query.setParameter("end_time", endTime);
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public List<Transaction> findByClientAndBookedTime(Client client, Date startTime, Date endTime) {
        return findByClientAndBookedTime(client, startTime, endTime, null, null);
    }

    @Override
    public List<Transaction> findByClientAndBookedTime(Client client, Date startTime, Date endTime, Integer start, Integer offset) {

        TypedQuery<Transaction> query = getEntityManager().createNamedQuery(Transaction.FIND_BY_CLIENT_AND_BOOKED_TIME, Transaction.class);
        query.setParameter("client", client);
        query.setParameter("start_time", startTime);
        query.setParameter("end_time", endTime);
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public List<Transaction> findByClientOnlyPaid(Client client) {
        return findByClientOnlyPaid(client, null, null);
    }

    @Override
    public List<Transaction> findByClientOnlyPaid(Client client, Integer start, Integer offset) {

        TypedQuery<Transaction> query = getEntityManager().createNamedQuery(Transaction.FIND_BY_CLIENT_ONLY_PAID, Transaction.class);
        query.setParameter("client", client);
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public List<Transaction> findByClientOnlyUnpaid(Client client) {
        return findByClientOnlyUnpaid(client, null, null);
    }

    @Override
    public List<Transaction> findByClientOnlyUnpaid(Client client, Integer start, Integer offset) {

        TypedQuery<Transaction> query = getEntityManager().createNamedQuery(Transaction.FIND_BY_CLIENT_ONLY_UNPAID, Transaction.class);
        query.setParameter("client", client);
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public List<Transaction> findByClientAndPriceRange(Client client, Double minPrice, Double maxPrice) {
        return findByClientAndPriceRange(client, minPrice, maxPrice, null, null);
    }

    @Override
    public List<Transaction> findByClientAndPriceRange(Client client, Double minPrice, Double maxPrice, Integer start, Integer offset) {

        TypedQuery<Transaction> query = getEntityManager().createNamedQuery(Transaction.FIND_BY_CLIENT_AND_PRICE_RANGE, Transaction.class);
        query.setParameter("client", client);
        query.setParameter("min_price", minPrice);
        query.setParameter("max_price", maxPrice);
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public List<Transaction> findByClientAndCurrencyCode(Client client, CurrencyCode currencyCode) {
        return findByClientAndCurrencyCode(client, currencyCode, null, null);
    }

    @Override
    public List<Transaction> findByClientAndCurrencyCode(Client client, CurrencyCode currencyCode, Integer start, Integer offset) {

        TypedQuery<Transaction> query = getEntityManager().createNamedQuery(Transaction.FIND_BY_CLIENT_AND_CURRENCY_CODE, Transaction.class);
        query.setParameter("client", client);
        query.setParameter("currency_code", currencyCode);
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public List<Transaction> findByClientAndPriceRangeAndCurrencyCode(Client client, Double minPrice, Double maxPrice, CurrencyCode currencyCode) {
        return findByClientAndPriceRangeAndCurrencyCode(client, minPrice, maxPrice, currencyCode, null, null);
    }

    @Override
    public List<Transaction> findByClientAndPriceRangeAndCurrencyCode(Client client, Double minPrice, Double maxPrice, CurrencyCode currencyCode, Integer start, Integer offset) {

        TypedQuery<Transaction> query = getEntityManager().createNamedQuery(Transaction.FIND_BY_CLIENT_AND_PRICE_RANGE_AND_CURRENCY_CODE, Transaction.class);
        query.setParameter("client", client);
        query.setParameter("min_price", minPrice);
        query.setParameter("max_price", maxPrice);
        query.setParameter("currency_code", currencyCode);
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public List<Transaction> findByClientAndPaymentMethod(Client client, PaymentMethod paymentMethod) {
        return findByClientAndPaymentMethod(client, paymentMethod, null, null);
    }

    @Override
    public List<Transaction> findByClientAndPaymentMethod(Client client, PaymentMethod paymentMethod, Integer start, Integer offset) {

        TypedQuery<Transaction> query = getEntityManager().createNamedQuery(Transaction.FIND_BY_CLIENT_AND_PAYMENT_METHOD, Transaction.class);
        query.setParameter("client", client);
        query.setParameter("payment_method", paymentMethod);
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public List<Transaction> findByClientAndProvider(Client client, Provider provider) {
        return findByClientAndProvider(client, provider, null, null);
    }

    @Override
    public List<Transaction> findByClientAndProvider(Client client, Provider provider, Integer start, Integer offset) {

        TypedQuery<Transaction> query = getEntityManager().createNamedQuery(Transaction.FIND_BY_CLIENT_AND_PROVIDER, Transaction.class);
        query.setParameter("client", client);
        query.setParameter("provider", provider);
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public List<Transaction> findByClientAndService(Client client, Service service) {
        return findByClientAndService(client, service, null, null);
    }

    @Override
    public List<Transaction> findByClientAndService(Client client, Service service, Integer start, Integer offset) {

        TypedQuery<Transaction> query = getEntityManager().createNamedQuery(Transaction.FIND_BY_CLIENT_AND_SERVICE, Transaction.class);
        query.setParameter("client", client);
        query.setParameter("service", service);
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }

        return query.getResultList();
    }

    @Override
    public List<Transaction> findByClientAndEmployee(Client client, Employee employee) {
        return findByClientAndEmployee(client, employee, null, null);
    }

    @Override
    public List<Transaction> findByClientAndEmployee(Client client, Employee employee, Integer start, Integer offset) {

        TypedQuery<Transaction> query = getEntityManager().createNamedQuery(Transaction.FIND_BY_CLIENT_AND_EMPLOYEE, Transaction.class);
        query.setParameter("client", client);
        query.setParameter("employee", employee);
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public List<Transaction> findByClientAndWorkStation(Client client, WorkStation workStation) {
        return findByClientAndWorkStation(client, workStation, null, null);
    }

    @Override
    public List<Transaction> findByClientAndWorkStation(Client client, WorkStation workStation, Integer start, Integer offset) {

        TypedQuery<Transaction> query = getEntityManager().createNamedQuery(Transaction.FIND_BY_CLIENT_AND_WORK_STATION, Transaction.class);
        query.setParameter("client", client);
        query.setParameter("work_station", workStation);
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public List<Transaction> findByClientAndServicePoint(Client client, ServicePoint servicePoint) {
        return findByClientAndServicePoint(client, servicePoint, null, null);
    }

    @Override
    public List<Transaction> findByClientAndServicePoint(Client client, ServicePoint servicePoint, Integer start, Integer offset) {

        TypedQuery<Transaction> query = getEntityManager().createNamedQuery(Transaction.FIND_BY_CLIENT_AND_SERVICE_POINT, Transaction.class);
        query.setParameter("client", client);
        query.setParameter("service_point", servicePoint);
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public List<Transaction> findByTransactionTime(Date startTime, Date endTime) {
        return findByTransactionTime(startTime, endTime, null, null);
    }

    @Override
    public List<Transaction> findByTransactionTime(Date startTime, Date endTime, Integer start, Integer offset) {

        TypedQuery<Transaction> query = getEntityManager().createNamedQuery(Transaction.FIND_BY_TRANSACTION_TIME, Transaction.class);
        query.setParameter("start_time", startTime);
        query.setParameter("end_time", endTime);
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public List<Transaction> findByBookedTime(Date startTime, Date endTime) {
        return findByBookedTime(startTime, endTime, null, null);
    }

    @Override
    public List<Transaction> findByBookedTime(Date startTime, Date endTime, Integer start, Integer offset) {

        TypedQuery<Transaction> query = getEntityManager().createNamedQuery(Transaction.FIND_BY_BOOKED_TIME, Transaction.class);
        query.setParameter("start_time", startTime);
        query.setParameter("end_time", endTime);
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public List<Transaction> findOnlyPaid() {
        return findOnlyPaid(null, null);
    }

    @Override
    public List<Transaction> findOnlyPaid(Integer start, Integer offset) {

        TypedQuery<Transaction> query = getEntityManager().createNamedQuery(Transaction.FIND_ONLY_PAID, Transaction.class);
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public List<Transaction> findOnlyUnpaid() {
        return findOnlyUnpaid(null, null);
    }

    @Override
    public List<Transaction> findOnlyUnpaid(Integer start, Integer offset) {

        TypedQuery<Transaction> query = getEntityManager().createNamedQuery(Transaction.FIND_ONLY_UNPAID, Transaction.class);
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public List<Transaction> findByPriceRange(Double minPrice, Double maxPrice) {
        return findByPriceRange(minPrice, maxPrice, null, null);
    }

    @Override
    public List<Transaction> findByPriceRange(Double minPrice, Double maxPrice, Integer start, Integer offset) {

        TypedQuery<Transaction> query = getEntityManager().createNamedQuery(Transaction.FIND_BY_PRICE_RANGE, Transaction.class);
        query.setParameter("min_price", minPrice);
        query.setParameter("max_price", maxPrice);
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public List<Transaction> findByCurrencyCode(CurrencyCode currencyCode) {
        return findByCurrencyCode(currencyCode, null, null);
    }

    @Override
    public List<Transaction> findByCurrencyCode(CurrencyCode currencyCode, Integer start, Integer offset) {

        TypedQuery<Transaction> query = getEntityManager().createNamedQuery(Transaction.FIND_BY_CURRENCY_CODE, Transaction.class);
        query.setParameter("currency_code", currencyCode);
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public List<Transaction> findByPriceRangeAndCurrencyCode(Double minPrice, Double maxPrice, CurrencyCode currencyCode) {
        return findByPriceRangeAndCurrencyCode(minPrice, maxPrice, currencyCode, null, null);
    }

    @Override
    public List<Transaction> findByPriceRangeAndCurrencyCode(Double minPrice, Double maxPrice, CurrencyCode currencyCode, Integer start, Integer offset) {

        TypedQuery<Transaction> query = getEntityManager().createNamedQuery(Transaction.FIND_BY_PRICE_RANGE_AND_CURRENCY_CODE, Transaction.class);
        query.setParameter("min_price", minPrice);
        query.setParameter("max_price", maxPrice);
        query.setParameter("currency_code", currencyCode);
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public List<Transaction> findByPaymentMethod(PaymentMethod paymentMethod) {
        return findByPaymentMethod(paymentMethod, null, null);
    }

    @Override
    public List<Transaction> findByPaymentMethod(PaymentMethod paymentMethod, Integer start, Integer offset) {

        TypedQuery<Transaction> query = getEntityManager().createNamedQuery(Transaction.FIND_BY_PAYMENT_METHOD, Transaction.class);
        query.setParameter("payment_method", paymentMethod);
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public List<Transaction> findByProvider(Provider provider) {
        return findByProvider(provider, null, null);
    }

    @Override
    public List<Transaction> findByProvider(Provider provider, Integer start, Integer offset) {

        TypedQuery<Transaction> query = getEntityManager().createNamedQuery(Transaction.FIND_BY_PROVIDER, Transaction.class);
        query.setParameter("provider", provider);
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public List<Transaction> findByService(Service service) {
        return findByService(service, null, null);
    }

    @Override
    public List<Transaction> findByService(Service service, Integer start, Integer offset) {

        TypedQuery<Transaction> query = getEntityManager().createNamedQuery(Transaction.FIND_BY_SERVICE, Transaction.class);
        query.setParameter("service", service);
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public List<Transaction> findByEmployee(Employee employee) {
        return findByEmployee(employee, null, null);
    }

    @Override
    public List<Transaction> findByEmployee(Employee employee, Integer start, Integer offset) {

        TypedQuery<Transaction> query = getEntityManager().createNamedQuery(Transaction.FIND_BY_EMPLOYEE, Transaction.class);
        query.setParameter("employee", employee);
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public List<Transaction> findByWorkStation(WorkStation workStation) {
        return findByWorkStation(workStation, null, null);
    }

    @Override
    public List<Transaction> findByWorkStation(WorkStation workStation, Integer start, Integer offset) {

        TypedQuery<Transaction> query = getEntityManager().createNamedQuery(Transaction.FIND_BY_WORK_STATION, Transaction.class);
        query.setParameter("work_station", workStation);
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public List<Transaction> findByServicePoint(ServicePoint servicePoint) {
        return findByServicePoint(servicePoint, null, null);
    }

    @Override
    public List<Transaction> findByServicePoint(ServicePoint servicePoint, Integer start, Integer offset) {

        TypedQuery<Transaction> query = getEntityManager().createNamedQuery(Transaction.FIND_BY_SERVICE_POINT, Transaction.class);
        query.setParameter("service_point", servicePoint);
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public List<Transaction> findByMultipleCriteria(List<Client> clients, List<Provider> providers, List<Service> services, List<ServicePoint> servicePoints, List<WorkStation> workStations, List<Employee> employees, List<ProviderService> providerServices, Period transactionTimePeriod, Period bookedTimePeriod, PriceRange priceRange, List<CurrencyCode> currencyCodes, List<PaymentMethod> paymentMethods, Boolean paid) {
        return findByMultipleCriteria(clients, providers, services, servicePoints, workStations, employees, providerServices, transactionTimePeriod, bookedTimePeriod, priceRange, currencyCodes, paymentMethods, paid, null, null);
    }

    @Override
    public List<Transaction> findByMultipleCriteria(List<Client> clients, List<Provider> providers, List<Service> services, List<ServicePoint> servicePoints, List<WorkStation> workStations, List<Employee> employees, List<ProviderService> providerServices, Period transactionTimePeriod, Period bookedTimePeriod,
                                                    PriceRange priceRange, List<CurrencyCode> currencyCodes, List<PaymentMethod> paymentMethods, Boolean paid, Integer start, Integer offset) {

        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Transaction> criteriaQuery = criteriaBuilder.createQuery(Transaction.class);
        // FROM
        Root<Transaction> transaction = criteriaQuery.from(Transaction.class);
        // SELECT
        criteriaQuery.select(transaction).distinct(true);

        // INNER JOIN-s
        Join<Transaction, Client> client = null;

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

            List<Predicate> orPredicates = new ArrayList<>();
            for(Employee employee : employees) {
                orPredicates.add( criteriaBuilder.isMember(employee, transaction.get(Transaction_.employees)) );
            }

            predicates.add( criteriaBuilder.or(orPredicates.toArray(new Predicate[] {})) );
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

        // WHERE predicate1 AND predicate2 AND ... AND predicateN
        criteriaQuery.where(predicates.toArray(new Predicate[] { }));

        TypedQuery<Transaction> query = getEntityManager().createQuery(criteriaQuery);
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public Integer deleteByClient(Client client) {

        Query query = getEntityManager().createNamedQuery(Transaction.DELETE_BY_CLIENT);
        query.setParameter("client", client);
        return query.executeUpdate();
    }
}
