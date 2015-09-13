package pl.salonea.ejb.interfaces;

import pl.salonea.entities.*;
import pl.salonea.enums.CurrencyCode;
import pl.salonea.utils.Period;
import pl.salonea.utils.PriceRange;

import java.util.Date;
import java.util.List;

/**
 * Created by michzio on 23/08/2015.
 */
public interface TransactionFacadeInterface extends AbstractFacadeInterface<Transaction> {

    // concrete interface
    List<Transaction> findByClient(Client client);
    List<Transaction> findByClient(Client client, Integer start, Integer limit);
    List<Transaction> findByClientAndTransactionTime(Client client, Date startTime, Date endTime);
    List<Transaction> findByClientAndTransactionTime(Client client, Date startTime, Date endTime, Integer start, Integer limit);
    List<Transaction> findByClientAndBookedTime(Client client, Date startTime, Date endTime);
    List<Transaction> findByClientAndBookedTime(Client client, Date startTime, Date endTime, Integer start, Integer limit);
    List<Transaction> findByClientOnlyPaid(Client client);
    List<Transaction> findByClientOnlyPaid(Client client, Integer start, Integer limit);
    List<Transaction> findByClientOnlyUnpaid(Client client);
    List<Transaction> findByClientOnlyUnpaid(Client client, Integer start, Integer limit);
    List<Transaction> findByClientAndPriceRange(Client client, Double minPrice, Double maxPrice);
    List<Transaction> findByClientAndPriceRange(Client client, Double minPrice, Double maxPrice, Integer start, Integer limit);
    List<Transaction> findByClientAndCurrencyCode(Client client, CurrencyCode currencyCode);
    List<Transaction> findByClientAndCurrencyCode(Client client, CurrencyCode currencyCode, Integer start, Integer limit);
    List<Transaction> findByClientAndPriceRangeAndCurrencyCode(Client client, Double minPrice, Double maxPrice, CurrencyCode currencyCode);
    List<Transaction> findByClientAndPriceRangeAndCurrencyCode(Client client, Double minPrice, Double maxPrice, CurrencyCode currencyCode, Integer start, Integer limit);
    List<Transaction> findByClientAndPaymentMethod(Client client, PaymentMethod paymentMethod);
    List<Transaction> findByClientAndPaymentMethod(Client client, PaymentMethod paymentMethod, Integer start, Integer limit);
    List<Transaction> findByClientAndProvider(Client client, Provider provider);
    List<Transaction> findByClientAndProvider(Client client, Provider provider, Integer start, Integer limit);
    List<Transaction> findByClientAndService(Client client, Service service);
    List<Transaction> findByClientAndService(Client client, Service service, Integer start, Integer limit);
    List<Transaction> findByClientAndEmployee(Client client, Employee employee);
    List<Transaction> findByClientAndEmployee(Client client, Employee employee, Integer start, Integer limit);
    List<Transaction> findByClientAndWorkStation(Client client, WorkStation workStation);
    List<Transaction> findByClientAndWorkStation(Client client, WorkStation workStation, Integer start, Integer limit);
    List<Transaction> findByClientAndServicePoint(Client client, ServicePoint servicePoint);
    List<Transaction> findByClientAndServicePoint(Client client, ServicePoint servicePoint, Integer start, Integer limit);
    List<Transaction> findByTransactionTime(Date startTime, Date endTime);
    List<Transaction> findByTransactionTime(Date startTime, Date endTime, Integer start, Integer limit);
    List<Transaction> findByBookedTime(Date startTime, Date endTime);
    List<Transaction> findByBookedTime(Date startTime, Date endTime, Integer start, Integer limit);
    List<Transaction> findOnlyPaid();
    List<Transaction> findOnlyPaid(Integer start, Integer limit);
    List<Transaction> findOnlyUnpaid();
    List<Transaction> findOnlyUnpaid(Integer start, Integer limit);
    List<Transaction> findByPriceRange(Double minPrice, Double maxPrice);
    List<Transaction> findByPriceRange(Double minPrice, Double maxPrice, Integer start, Integer limit);
    List<Transaction> findByCurrencyCode(CurrencyCode currencyCode);
    List<Transaction> findByCurrencyCode(CurrencyCode currencyCode, Integer start, Integer limit);
    List<Transaction> findByPriceRangeAndCurrencyCode(Double minPrice, Double maxPrice, CurrencyCode currencyCode);
    List<Transaction> findByPriceRangeAndCurrencyCode(Double minPrice, Double maxPrice, CurrencyCode currencyCode, Integer start, Integer limit);
    List<Transaction> findByPaymentMethod(PaymentMethod paymentMethod);
    List<Transaction> findByPaymentMethod(PaymentMethod paymentMethod, Integer start, Integer limit);
    List<Transaction> findByProvider(Provider provider);
    List<Transaction> findByProvider(Provider provider, Integer start, Integer limit);
    List<Transaction> findByService(Service service);
    List<Transaction> findByService(Service service, Integer start, Integer limit);
    List<Transaction> findByEmployee(Employee employee);
    List<Transaction> findByEmployee(Employee employee, Integer start, Integer limit);
    List<Transaction> findByWorkStation(WorkStation workStation);
    List<Transaction> findByWorkStation(WorkStation workStation, Integer start, Integer limit);
    List<Transaction> findByServicePoint(ServicePoint servicePoint);
    List<Transaction> findByServicePoint(ServicePoint servicePoint, Integer start, Integer limit);
    List<Transaction> findByMultipleCriteria(List<Client> clients, List<Provider> providers, List<Service> services, List<ServicePoint> servicePoints, List<WorkStation> workStations,
                                             List<Employee> employees, List<ProviderService> providerServices, Period transactionTimePeriod, Period bookedTimePeriod,
                                             PriceRange priceRange, List<CurrencyCode> currencyCodes, List<PaymentMethod> paymentMethods,  Boolean paid);
    List<Transaction> findByMultipleCriteria(List<Client> clients, List<Provider> providers, List<Service> services, List<ServicePoint> servicePoints, List<WorkStation> workStations,
                                             List<Employee> employees, List<ProviderService> providerServices, Period transactionTimePeriod, Period bookedTimePeriod,
                                             PriceRange priceRange, List<CurrencyCode> currencyCodes, List<PaymentMethod> paymentMethods,  Boolean paid,
                                             Integer start, Integer limit);

    Integer deleteByClient(Client client);

    @javax.ejb.Local
    interface Local extends TransactionFacadeInterface { }

    @javax.ejb.Remote
    interface Remote extends TransactionFacadeInterface { }
}