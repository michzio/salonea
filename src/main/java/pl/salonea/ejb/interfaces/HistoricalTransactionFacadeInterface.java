package pl.salonea.ejb.interfaces;

import org.omg.CORBA.CompletionStatus;
import pl.salonea.entities.*;
import pl.salonea.entities.idclass.TransactionId;
import pl.salonea.enums.CurrencyCode;
import pl.salonea.enums.TransactionCompletionStatus;
import pl.salonea.utils.Period;
import pl.salonea.utils.PriceRange;
import pl.salonea.utils.RatingRange;

import java.util.Date;
import java.util.List;

/**
 * Created by michzio on 25/08/2015.
 */
public interface HistoricalTransactionFacadeInterface extends AbstractFacadeInterface<HistoricalTransaction> {

    // concrete interface
    HistoricalTransaction createForClient(Long clientId, HistoricalTransaction historicalTransaction);
    HistoricalTransaction update(TransactionId transactionId, HistoricalTransaction historicalTransaction);
    HistoricalTransaction update(TransactionId transactionId, HistoricalTransaction historicalTransaction, Boolean retainTransientFields);
    List<HistoricalTransaction> findAllEagerly();
    List<HistoricalTransaction> findAllEagerly(Integer start, Integer limit);
    HistoricalTransaction findByIdEagerly(TransactionId transactionId);

    List<HistoricalTransaction> findByTransactionTime(Date startTime, Date endTime);
    List<HistoricalTransaction> findByTransactionTime(Date startTime, Date endTime, Integer start, Integer limit);
    List<HistoricalTransaction> findByBookedTime(Date startTime, Date endTime);
    List<HistoricalTransaction> findByBookedTime(Date startTime, Date endTime, Integer start, Integer limit);

    List<HistoricalTransaction> findOnlyPaid();
    List<HistoricalTransaction> findOnlyPaid(Integer start, Integer limit);
    List<HistoricalTransaction> findOnlyUnpaid();
    List<HistoricalTransaction> findOnlyUnpaid(Integer start, Integer limit);

    List<HistoricalTransaction> findByPriceRange(Double minPrice, Double maxPrice);
    List<HistoricalTransaction> findByPriceRange(Double minPrice, Double maxPrice, Integer start, Integer limit);
    List<HistoricalTransaction> findByCurrencyCode(CurrencyCode currencyCode);
    List<HistoricalTransaction> findByCurrencyCode(CurrencyCode currencyCode, Integer start, Integer limit);
    List<HistoricalTransaction> findByPriceRangeAndCurrencyCode(Double minPrice, Double maxPrice, CurrencyCode currencyCode);
    List<HistoricalTransaction> findByPriceRangeAndCurrencyCode(Double minPrice, Double maxPrice, CurrencyCode currencyCode, Integer start, Integer limit);

    List<HistoricalTransaction> findByCompletionStatus(TransactionCompletionStatus completionStatus);
    List<HistoricalTransaction> findByCompletionStatus(TransactionCompletionStatus completionStatus, Integer start, Integer limit);
    List<HistoricalTransaction> findByClientRatingRange(Short minRating, Short maxRating);
    List<HistoricalTransaction> findByClientRatingRange(Short minRating, Short maxRating, Integer start, Integer limit);
    List<HistoricalTransaction> findByClientComment(String clientComment);
    List<HistoricalTransaction> findByClientComment(String clientComment, Integer start, Integer limit);
    List<HistoricalTransaction> findByProviderRatingRange(Short minRating, Short maxRating);
    List<HistoricalTransaction> findByProviderRatingRange(Short minRating, Short maxRating, Integer start, Integer limit);
    List<HistoricalTransaction> findByProviderDementi(String providerDementi);
    List<HistoricalTransaction> findByProviderDementi(String providerDementi, Integer start, Integer limit);

    // client
    List<HistoricalTransaction> findByClient(Client client);
    List<HistoricalTransaction> findByClient(Client client, Integer start, Integer limit);
    List<HistoricalTransaction> findByClientEagerly(Client client);
    List<HistoricalTransaction> findByClientEagerly(Client client, Integer start, Integer limit);
    List<HistoricalTransaction> findByClientAndTransactionTime(Client client, Date startTime, Date endTime);
    List<HistoricalTransaction> findByClientAndTransactionTime(Client client, Date startTime, Date endTime, Integer start, Integer limit);
    List<HistoricalTransaction> findByClientAndBookedTime(Client client, Date startTime, Date endTime);
    List<HistoricalTransaction> findByClientAndBookedTime(Client client, Date startTime, Date endTime, Integer start, Integer limit);
    List<HistoricalTransaction> findByClientOnlyPaid(Client client);
    List<HistoricalTransaction> findByClientOnlyPaid(Client client, Integer start, Integer limit);
    List<HistoricalTransaction> findByClientOnlyUnpaid(Client client);
    List<HistoricalTransaction> findByClientOnlyUnpaid(Client client, Integer start, Integer limit);
    List<HistoricalTransaction> findByClientAndPriceRange(Client client, Double minPrice, Double maxPrice);
    List<HistoricalTransaction> findByClientAndPriceRange(Client client, Double minPrice, Double maxPrice, Integer start, Integer limit);
    List<HistoricalTransaction> findByClientAndCurrencyCode(Client client, CurrencyCode currencyCode);
    List<HistoricalTransaction> findByClientAndCurrencyCode(Client client, CurrencyCode currencyCode, Integer start, Integer limit);
    List<HistoricalTransaction> findByClientAndPriceRangeAndCurrencyCode(Client client, Double minPrice, Double maxPrice, CurrencyCode currencyCode);
    List<HistoricalTransaction> findByClientAndPriceRangeAndCurrencyCode(Client client, Double minPrice, Double maxPrice, CurrencyCode currencyCode, Integer start, Integer limit);
    List<HistoricalTransaction> findByClientAndCompletionStatus(Client client, TransactionCompletionStatus completionStatus);
    List<HistoricalTransaction> findByClientAndCompletionStatus(Client client, TransactionCompletionStatus completionStatus, Integer start, Integer limit);
    List<HistoricalTransaction> findByClientAndClientRatingRange(Client client, Short minRating, Short maxRating);
    List<HistoricalTransaction> findByClientAndClientRatingRange(Client client, Short minRating, Short maxRating, Integer start, Integer limit);
    List<HistoricalTransaction> findByClientAndClientComment(Client client, String clientComment);
    List<HistoricalTransaction> findByClientAndClientComment(Client client, String clientComment, Integer start, Integer limit);
    List<HistoricalTransaction> findByClientAndProviderRatingRange(Client client, Short minRating, Short maxRating);
    List<HistoricalTransaction> findByClientAndProviderRatingRange(Client client, Short minRating, Short maxRating, Integer start, Integer limit);
    List<HistoricalTransaction> findByClientAndProviderDementi(Client client, String providerDementi);
    List<HistoricalTransaction> findByClientAndProviderDementi(Client client, String providerDementi, Integer start, Integer limit);

    // payment method
    List<HistoricalTransaction> findByPaymentMethod(PaymentMethod paymentMethod);
    List<HistoricalTransaction> findByPaymentMethod(PaymentMethod paymentMethod, Integer start, Integer limit);
    List<HistoricalTransaction> findByPaymentMethodEagerly(PaymentMethod paymentMethod);
    List<HistoricalTransaction> findByPaymentMethodEagerly(PaymentMethod paymentMethod, Integer start, Integer limit);

    // provider
    List<HistoricalTransaction> findByProvider(Provider provider);
    List<HistoricalTransaction> findByProvider(Provider provider, Integer start, Integer limit);
    List<HistoricalTransaction> findByProviderEagerly(Provider provider);
    List<HistoricalTransaction> findByProviderEagerly(Provider provider, Integer start, Integer limit);

    // service
    List<HistoricalTransaction> findByService(Service service);
    List<HistoricalTransaction> findByService(Service service, Integer start, Integer limit);
    List<HistoricalTransaction> findByServiceEagerly(Service service);
    List<HistoricalTransaction> findByServiceEagerly(Service service, Integer start, Integer limit);

    // provider service
    List<HistoricalTransaction> findByProviderService(ProviderService providerService);
    List<HistoricalTransaction> findByProviderService(ProviderService providerService, Integer start, Integer limit);
    List<HistoricalTransaction> findByProviderServiceEagerly(ProviderService providerService);
    List<HistoricalTransaction> findByProviderServiceEagerly(ProviderService providerService, Integer start, Integer limit);

    // employee
    List<HistoricalTransaction> findByEmployee(Employee employee);
    List<HistoricalTransaction> findByEmployee(Employee employee, Integer start, Integer limit);
    List<HistoricalTransaction> findByEmployeeEagerly(Employee employee);
    List<HistoricalTransaction> findByEmployeeEagerly(Employee employee, Integer start, Integer limit);

    // work station
    List<HistoricalTransaction> findByWorkStation(WorkStation workStation);
    List<HistoricalTransaction> findByWorkStation(WorkStation workStation, Integer start, Integer limit);
    List<HistoricalTransaction> findByWorkStationEagerly(WorkStation workStation);
    List<HistoricalTransaction> findByWorkStationEagerly(WorkStation workStation, Integer start, Integer limit);

    // service point
    List<HistoricalTransaction> findByServicePoint(ServicePoint servicePoint);
    List<HistoricalTransaction> findByServicePoint(ServicePoint servicePoint, Integer start, Integer limit);
    List<HistoricalTransaction> findByServicePointEagerly(ServicePoint servicePoint);
    List<HistoricalTransaction> findByServicePointEagerly(ServicePoint servicePoint, Integer start, Integer limit);

    // term
    List<HistoricalTransaction> findByTerm(Term term);
    List<HistoricalTransaction> findByTerm(Term term, Integer start, Integer limit);
    List<HistoricalTransaction> findByTermEagerly(Term term);
    List<HistoricalTransaction> findByTermEagerly(Term term, Integer start, Integer limit);

    // other
    List<HistoricalTransaction> findByClientAndPaymentMethod(Client client, PaymentMethod paymentMethod);
    List<HistoricalTransaction> findByClientAndPaymentMethod(Client client, PaymentMethod paymentMethod, Integer start, Integer limit);
    List<HistoricalTransaction> findByClientAndProvider(Client client, Provider provider);
    List<HistoricalTransaction> findByClientAndProvider(Client client, Provider provider, Integer start, Integer limit);
    List<HistoricalTransaction> findByClientAndService(Client client, Service service);
    List<HistoricalTransaction> findByClientAndService(Client client, Service service, Integer start, Integer limit);
    List<HistoricalTransaction> findByClientAndProviderService(Client client, ProviderService providerService);
    List<HistoricalTransaction> findByClientAndProviderService(Client client, ProviderService providerService, Integer start, Integer limit);
    List<HistoricalTransaction> findByClientAndEmployee(Client client, Employee employee);
    List<HistoricalTransaction> findByClientAndEmployee(Client client, Employee employee, Integer start, Integer limit);
    List<HistoricalTransaction> findByClientAndWorkStation(Client client, WorkStation workStation);
    List<HistoricalTransaction> findByClientAndWorkStation(Client client, WorkStation workStation, Integer start, Integer limit);
    List<HistoricalTransaction> findByClientAndServicePoint(Client client, ServicePoint servicePoint);
    List<HistoricalTransaction> findByClientAndServicePoint(Client client, ServicePoint servicePoint, Integer start, Integer limit);
    List<HistoricalTransaction> findByClientAndTerm(Client client, Term term);
    List<HistoricalTransaction> findByClientAndTerm(Client client, Term term, Integer start, Integer limit);

    // multiple criteria
    List<HistoricalTransaction> findByMultipleCriteria(List<Client> clients, List<Provider> providers, List<Service> services, List<ServicePoint> servicePoints, List<WorkStation> workStations,
                                                       List<Employee> employees, List<ProviderService> providerServices, Period transactionTimePeriod, Period bookedTimePeriod, List<Term> terms,
                                                       PriceRange priceRange, List<CurrencyCode> currencyCodes, List<PaymentMethod> paymentMethods,  Boolean paid,
                                                       List<TransactionCompletionStatus> completionStatuses, RatingRange clientRatingRange, List<String> clientComments, RatingRange providerRatingRange, List<String> providerDementis);
    List<HistoricalTransaction> findByMultipleCriteria(List<Client> clients, List<Provider> providers, List<Service> services, List<ServicePoint> servicePoints, List<WorkStation> workStations,
                                                       List<Employee> employees, List<ProviderService> providerServices, Period transactionTimePeriod, Period bookedTimePeriod, List<Term> terms,
                                                       PriceRange priceRange, List<CurrencyCode> currencyCodes, List<PaymentMethod> paymentMethods,  Boolean paid,
                                                       List<TransactionCompletionStatus> completionStatuses, RatingRange clientRatingRange, List<String> clientComments, RatingRange providerRatingRange, List<String> providerDementis,
                                                       Integer start, Integer limit);
    List<HistoricalTransaction> findByMultipleCriteriaEagerly(List<Client> clients, List<Provider> providers, List<Service> services, List<ServicePoint> servicePoints, List<WorkStation> workStations,
                                                       List<Employee> employees, List<ProviderService> providerServices, Period transactionTimePeriod, Period bookedTimePeriod, List<Term> terms,
                                                       PriceRange priceRange, List<CurrencyCode> currencyCodes, List<PaymentMethod> paymentMethods,  Boolean paid,
                                                       List<TransactionCompletionStatus> completionStatuses, RatingRange clientRatingRange, List<String> clientComments, RatingRange providerRatingRange, List<String> providerDementis);
    List<HistoricalTransaction> findByMultipleCriteriaEagerly(List<Client> clients, List<Provider> providers, List<Service> services, List<ServicePoint> servicePoints, List<WorkStation> workStations,
                                                       List<Employee> employees, List<ProviderService> providerServices, Period transactionTimePeriod, Period bookedTimePeriod, List<Term> terms,
                                                       PriceRange priceRange, List<CurrencyCode> currencyCodes, List<PaymentMethod> paymentMethods,  Boolean paid,
                                                       List<TransactionCompletionStatus> completionStatuses, RatingRange clientRatingRange, List<String> clientComments, RatingRange providerRatingRange, List<String> providerDementis,
                                                       Integer start, Integer limit);

    Long countByClient(Client client);
    Long countByPaymentMethod(PaymentMethod paymentMethod);
    Long countByProvider(Provider provider);
    Long countByService(Service service);
    Long countByProviderService(ProviderService providerService);
    Long countByEmployee(Employee employee);
    Long countByWorkStation(WorkStation workStation);
    Long countByServicePoint(ServicePoint servicePoint);
    Long countByTerm(Term term);

    Integer deleteByClient(Client client);
    Integer deleteById(TransactionId transactionId);

    @javax.ejb.Local
    interface Local extends HistoricalTransactionFacadeInterface { }

    @javax.ejb.Remote
    interface Remote extends HistoricalTransactionFacadeInterface { }
}
