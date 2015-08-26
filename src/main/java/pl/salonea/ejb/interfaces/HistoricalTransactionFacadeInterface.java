package pl.salonea.ejb.interfaces;

import org.omg.CORBA.CompletionStatus;
import pl.salonea.entities.*;
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
    List<HistoricalTransaction> findByClient(Client client);
    List<HistoricalTransaction> findByClient(Client client, Integer start, Integer limit);
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
    List<HistoricalTransaction> findByClientAndPaymentMethod(Client client, PaymentMethod paymentMethod);
    List<HistoricalTransaction> findByClientAndPaymentMethod(Client client, PaymentMethod paymentMethod, Integer start, Integer limit);
    List<HistoricalTransaction> findByClientAndProvider(Client client, Provider provider);
    List<HistoricalTransaction> findByClientAndProvider(Client client, Provider provider, Integer start, Integer limit);
    List<HistoricalTransaction> findByClientAndService(Client client, Service service);
    List<HistoricalTransaction> findByClientAndService(Client client, Service service, Integer start, Integer limit);
    List<HistoricalTransaction> findByClientAndEmployee(Client client, Employee employee);
    List<HistoricalTransaction> findByClientAndEmployee(Client client, Employee employee, Integer start, Integer limit);
    List<HistoricalTransaction> findByClientAndWorkStation(Client client, WorkStation workStation);
    List<HistoricalTransaction> findByClientAndWorkStation(Client client, WorkStation workStation, Integer start, Integer limit);
    List<HistoricalTransaction> findByClientAndServicePoint(Client client, ServicePoint servicePoint);
    List<HistoricalTransaction> findByClientAndServicePoint(Client client, ServicePoint servicePoint, Integer start, Integer limit);
    List<HistoricalTransaction> findByClientAndCompletionStatus(Client client, TransactionCompletionStatus completionStatus);
    List<HistoricalTransaction> findByClientAndCompletionStatus(Client client, TransactionCompletionStatus completionStatus, Integer strat, Integer limit);
    List<HistoricalTransaction> findByClientAndClientRatingRange(Client client, Short minRating, Short maxRating);
    List<HistoricalTransaction> findByClientAndClientRatingRange(Client client, Short minRating, Short maxRating, Integer start, Integer limit);
    List<HistoricalTransaction> findByClientAndProviderRatingRange(Client client, Short minRating, Short maxRating);
    List<HistoricalTransaction> findByClientAndProviderRatingRange(Client client, Short minRating, Short maxRating, Integer start, Integer limit);
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
    List<HistoricalTransaction> findByPaymentMethod(PaymentMethod paymentMethod);
    List<HistoricalTransaction> findByPaymentMethod(PaymentMethod paymentMethod, Integer start, Integer limit);
    List<HistoricalTransaction> findByProvider(Provider provider);
    List<HistoricalTransaction> findByProvider(Provider provider, Integer start, Integer limit);
    List<HistoricalTransaction> findByService(Service service);
    List<HistoricalTransaction> findByService(Service service, Integer start, Integer limit);
    List<HistoricalTransaction> findByEmployee(Employee employee);
    List<HistoricalTransaction> findByEmployee(Employee employee, Integer start, Integer limit);
    List<HistoricalTransaction> findByWorkStation(WorkStation workStation);
    List<HistoricalTransaction> findByWorkStation(WorkStation workStation, Integer start, Integer limit);
    List<HistoricalTransaction> findByServicePoint(ServicePoint servicePoint);
    List<HistoricalTransaction> findByServicePoint(ServicePoint servicePoint, Integer start, Integer limit);
    List<HistoricalTransaction> findByCompletionStatus(TransactionCompletionStatus completionStatus);
    List<HistoricalTransaction> findByCompletionStatus(TransactionCompletionStatus completionStatus, Integer start, Integer limit);
    List<HistoricalTransaction> findByClientRatingRange(Short minRating, Short maxRating);
    List<HistoricalTransaction> findByClientRatingRange(Short minRating, Short maxRating, Integer start, Integer limit);
    List<HistoricalTransaction> findByProviderRatingRange(Short minRating, Short maxRating);
    List<HistoricalTransaction> findByProviderRatingRange(Short minRating, Short maxRating, Integer start, Integer limit);
    List<HistoricalTransaction> findByMultipleCriteria(List<Client> clients, List<Provider> providers, List<Service> services, List<ServicePoint> servicePoints, List<WorkStation> workStations,
                                             List<Employee> employees, List<ProviderService> providerServices, Period transactionTimePeriod, Period bookedTimePeriod,
                                             PriceRange priceRange, List<CurrencyCode> currencyCodes, List<PaymentMethod> paymentMethods,  Boolean paid,
                                             List<TransactionCompletionStatus> completionStatuses, RatingRange clientRatingRange, RatingRange providerRatingRange);
    List<HistoricalTransaction> findByMultipleCriteria(List<Client> clients, List<Provider> providers, List<Service> services, List<ServicePoint> servicePoints, List<WorkStation> workStations,
                                             List<Employee> employees, List<ProviderService> providerServices, Period transactionTimePeriod, Period bookedTimePeriod,
                                             PriceRange priceRange, List<CurrencyCode> currencyCodes, List<PaymentMethod> paymentMethods,  Boolean paid,
                                             List<TransactionCompletionStatus> completionStatuses, RatingRange clientRatingRange, RatingRange providerRatingRange, Integer start, Integer limit);

    Integer deleteByClient(Client client);

    @javax.ejb.Local
    interface Local extends HistoricalTransactionFacadeInterface { }

    @javax.ejb.Remote
    interface Remote extends HistoricalTransactionFacadeInterface { }
}
