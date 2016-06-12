package pl.salonea.entities;

import pl.salonea.constraints.BookedTimeInFuture;
import pl.salonea.enums.CurrencyCode;
import pl.salonea.jaxrs.utils.hateoas.Link;
import pl.salonea.mapped_superclasses.AbstractTransaction;

import javax.persistence.*;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.Date;
import java.util.LinkedHashSet;

@XmlRootElement(name = "transaction")
@XmlAccessorType(XmlAccessType.PROPERTY)

@Entity
@Table(name = "transaction")
@BookedTimeInFuture
@Access(AccessType.PROPERTY)
@NamedQueries({
        @NamedQuery(name = Transaction.FIND_ALL_EAGERLY, query = "SELECT DISTINCT tx FROM Transaction tx LEFT JOIN FETCH tx.employees"),
        @NamedQuery(name = Transaction.FIND_BY_ID_EAGERLY, query = "SELECT tx FROM Transaction tx LEFT JOIN FETCH tx.employees WHERE tx.client.clientId = :clientId AND tx.transactionNumber = :transaction_number"),
        @NamedQuery(name = Transaction.FIND_BY_TRANSACTION_TIME, query = "SELECT tx FROM Transaction tx WHERE tx.transactionTime >= :start_time AND tx.transactionTime <= :end_time"),
        @NamedQuery(name = Transaction.FIND_BY_BOOKED_TIME, query = "SELECT tx FROM Transaction tx WHERE tx.bookedTime >= :start_time AND tx.bookedTime <= :end_time"),
        @NamedQuery(name = Transaction.FIND_ONLY_PAID, query = "SELECT tx FROM Transaction tx WHERE tx.paid = TRUE"),
        @NamedQuery(name = Transaction.FIND_ONLY_UNPAID, query = "SELECT tx FROM Transaction tx WHERE tx.paid = FALSE"),
        @NamedQuery(name = Transaction.FIND_BY_PRICE_RANGE, query = "SELECT tx FROM Transaction tx WHERE tx.price >= :min_price AND tx.price <= :max_price"),
        @NamedQuery(name = Transaction.FIND_BY_CURRENCY_CODE, query = "SELECT tx FROM Transaction tx WHERE tx.priceCurrencyCode = :currency_code"),
        @NamedQuery(name = Transaction.FIND_BY_PRICE_RANGE_AND_CURRENCY_CODE, query = "SELECT tx FROM Transaction tx WHERE tx.price >= :min_price AND tx.price <= :max_price AND tx.priceCurrencyCode = :currency_code"),
        @NamedQuery(name = Transaction.FIND_BY_CLIENT, query = "SELECT tx FROM Transaction tx WHERE tx.client = :client"),
        @NamedQuery(name = Transaction.FIND_BY_CLIENT_EAGERLY, query = "SELECT DISTINCT tx FROM Transaction tx LEFT JOIN FETCH tx.employees WHERE tx.client = :client"),
        @NamedQuery(name = Transaction.FIND_BY_CLIENT_AND_TRANSACTION_TIME, query = "SELECT tx FROM Transaction tx WHERE tx.client = :client " +
                "AND tx.transactionTime >= :start_time AND tx.transactionTime <= :end_time"),
        @NamedQuery(name = Transaction.FIND_BY_CLIENT_AND_BOOKED_TIME, query = "SELECT tx FROM Transaction tx WHERE tx.client = :client " +
                "AND tx.bookedTime >= :start_time AND tx.bookedTime <= :end_time"),
        @NamedQuery(name = Transaction.FIND_BY_CLIENT_ONLY_PAID, query = "SELECT tx FROM Transaction tx WHERE tx.client = :client AND tx.paid = TRUE"),
        @NamedQuery(name = Transaction.FIND_BY_CLIENT_ONLY_UNPAID, query = "SELECT tx FROM Transaction tx WHERE tx.client = :client AND tx.paid = FALSE"),
        @NamedQuery(name = Transaction.FIND_BY_CLIENT_AND_PRICE_RANGE, query = "SELECT tx FROM Transaction tx WHERE tx.client = :client AND " +
                " tx.price >= :min_price AND tx.price <= :max_price"),
        @NamedQuery(name = Transaction.FIND_BY_CLIENT_AND_CURRENCY_CODE, query = "SELECT tx FROM Transaction tx WHERE tx.client = :client AND " +
                " tx.priceCurrencyCode = :currency_code"),
        @NamedQuery(name = Transaction.FIND_BY_CLIENT_AND_PRICE_RANGE_AND_CURRENCY_CODE, query = "SELECT tx FROM Transaction tx WHERE tx.client = :client AND " +
                " tx.price >= :min_price AND tx.price <= :max_price AND tx.priceCurrencyCode = :currency_code"),
        @NamedQuery(name = Transaction.FIND_BY_PAYMENT_METHOD, query = "SELECT tx FROM Transaction tx WHERE tx.paymentMethod = :payment_method"),
        @NamedQuery(name = Transaction.FIND_BY_PAYMENT_METHOD_EAGERLY, query = "SELECT DISTINCT tx FROM Transaction tx LEFT JOIN FETCH tx.employees WHERE tx.paymentMethod = :payment_method"),
        @NamedQuery(name = Transaction.FIND_BY_PROVIDER, query = "SELECT tx FROM Transaction tx WHERE tx.provider = :provider"),
        @NamedQuery(name = Transaction.FIND_BY_PROVIDER_EAGERLY, query = "SELECT DISTINCT tx FROM Transaction tx LEFT JOIN FETCH tx.employees WHERE tx.provider = :provider"),
        @NamedQuery(name = Transaction.FIND_BY_SERVICE, query = "SELECT tx FROM Transaction tx WHERE tx.service = :service"),
        @NamedQuery(name = Transaction.FIND_BY_SERVICE_EAGERLY, query = "SELECT DISTINCT tx FROM Transaction tx LEFT JOIN FETCH tx.employees WHERE tx.service = :service"),
        @NamedQuery(name = Transaction.FIND_BY_PROVIDER_SERVICE, query = "SELECT tx FROM Transaction tx WHERE tx.providerService = :provider_service"),
        @NamedQuery(name = Transaction.FIND_BY_PROVIDER_SERVICE_EAGERLY, query = "SELECT DISTINCT tx FROM Transaction tx LEFT JOIN FETCH tx.employees WHERE tx.providerService = :provider_service"),
        @NamedQuery(name = Transaction.FIND_BY_EMPLOYEE, query = "SELECT tx FROM Transaction tx WHERE :employee MEMBER OF tx.employees"),
        @NamedQuery(name = Transaction.FIND_BY_EMPLOYEE_EAGERLY, query = "SELECT DISTINCT tx FROM Transaction tx INNER JOIN FETCH tx.employees e WHERE e = :employee"),
        @NamedQuery(name = Transaction.FIND_BY_WORK_STATION, query = "SELECT tx FROM Transaction tx WHERE tx.workStation = :work_station"),
        @NamedQuery(name = Transaction.FIND_BY_WORK_STATION_EAGERLY, query = "SELECT DISTINCT tx FROM Transaction tx LEFT JOIN FETCH tx.employees WHERE tx.workStation = :work_station"),
        @NamedQuery(name = Transaction.FIND_BY_SERVICE_POINT, query = "SELECT tx FROM Transaction tx WHERE tx.servicePoint = :service_point"),
        @NamedQuery(name = Transaction.FIND_BY_SERVICE_POINT_EAGERLY, query = "SELECT DISTINCT tx FROM Transaction tx LEFT JOIN FETCH tx.employees WHERE tx.servicePoint = :service_point"),
        @NamedQuery(name = Transaction.FIND_BY_TERM, query = "SELECT tx FROM Transaction tx WHERE tx.term = :term"),
        @NamedQuery(name = Transaction.FIND_BY_TERM_EAGERLY, query = "SELECT DISTINCT tx FROM Transaction tx LEFT JOIN FETCH tx.employees WHERE tx.term = :term"),
        @NamedQuery(name = Transaction.FIND_BY_CLIENT_AND_PAYMENT_METHOD, query = "SELECT tx FROM Transaction tx WHERE tx.client = :client AND tx.paymentMethod = :payment_method"),
        @NamedQuery(name = Transaction.FIND_BY_CLIENT_AND_PROVIDER, query = "SELECT tx FROM Transaction tx WHERE tx.client = :client AND tx.provider = :provider"),
        @NamedQuery(name = Transaction.FIND_BY_CLIENT_AND_SERVICE, query = "SELECT tx FROM Transaction tx WHERE tx.client = :client AND tx.service = :service"),
        @NamedQuery(name = Transaction.FIND_BY_CLIENT_AND_PROVIDER_SERVICE, query = "SELECT tx FROM Transaction tx WHERE tx.client = :client AND tx.providerService = :provider_service"),
        @NamedQuery(name = Transaction.FIND_BY_CLIENT_AND_EMPLOYEE, query = "SELECT tx FROM Transaction tx WHERE tx.client = :client AND :employee MEMBER OF tx.employees"),
        @NamedQuery(name = Transaction.FIND_BY_CLIENT_AND_WORK_STATION, query = "SELECT tx FROM Transaction tx WHERE tx.client = :client AND tx.workStation = :work_station"),
        @NamedQuery(name = Transaction.FIND_BY_CLIENT_AND_SERVICE_POINT, query = "SELECT tx FROM Transaction tx WHERE tx.client = :client AND tx.servicePoint = :service_point"),
        @NamedQuery(name = Transaction.FIND_BY_CLIENT_AND_TERM, query = "SELECT tx FROM Transaction tx WHERE tx.client = :client AND tx.term = :term"),
        @NamedQuery(name = Transaction.COUNT_BY_CLIENT, query = "SELECT COUNT(tx) FROM Transaction tx WHERE tx.client = :client"),
        @NamedQuery(name = Transaction.COUNT_BY_PAYMENT_METHOD, query = "SELECT COUNT(tx) FROM Transaction tx WHERE tx.paymentMethod = :payment_method"),
        @NamedQuery(name = Transaction.COUNT_BY_PROVIDER, query = "SELECT COUNT(tx) FROM Transaction tx WHERE tx.provider = :provider"),
        @NamedQuery(name = Transaction.COUNT_BY_SERVICE, query = "SELECT COUNT(tx) FROM Transaction tx WHERE tx.service = :service"),
        @NamedQuery(name = Transaction.COUNT_BY_PROVIDER_SERVICE, query = "SELECT COUNT(tx) FROM Transaction tx WHERE tx.providerService = :provider_service"),
        @NamedQuery(name = Transaction.COUNT_BY_EMPLOYEE, query = "SELECT COUNT(tx) FROM Transaction tx WHERE :employee MEMBER OF tx.employees"),
        @NamedQuery(name = Transaction.COUNT_BY_WORK_STATION, query = "SELECT COUNT(tx) FROM Transaction tx WHERE tx.workStation = :work_station"),
        @NamedQuery(name = Transaction.COUNT_BY_SERVICE_POINT, query = "SELECT COUNT(tx) FROM Transaction tx WHERE tx.servicePoint = :service_point"),
        @NamedQuery(name = Transaction.COUNT_BY_TERM, query = "SELECT COUNT(tx) FROM Transaction tx WHERE tx.term = :term"),
        @NamedQuery(name = Transaction.DELETE_BY_CLIENT, query = "DELETE FROM Transaction tx WHERE tx.client = :client"),
        @NamedQuery(name = Transaction.DELETE_BY_ID, query = "DELETE FROM Transaction tx WHERE tx.client.clientId = :clientId AND tx.transactionNumber = :transaction_number"),
})
@NamedNativeQueries({
        @NamedNativeQuery(name = Transaction.DELETE_EMPLOYEES_ASSOCIATIONS_BY_TRANSACTION, query = "DELETE FROM transaction_executed_by WHERE client_id = :clientId AND transaction_no = :transaction_number"),
        @NamedNativeQuery(name = Transaction.DELETE_EMPLOYEES_ASSOCIATIONS_BY_CLIENT, query = "DELETE FROM transaction_executed_by WHERE client_id = :clientId"),
})


public class Transaction extends AbstractTransaction implements Serializable {

    public static final String FIND_ALL_EAGERLY = "Transaction.findAllEagerly";
    public static final String FIND_BY_ID_EAGERLY = "Transaction.findByIdEagerly";
    public static final String FIND_BY_TRANSACTION_TIME = "Transaction.findByTransactionTime";
    public static final String FIND_BY_BOOKED_TIME = "Transaction.findByBookedTime";
    public static final String FIND_ONLY_PAID = "Transaction.findOnlyPaid";
    public static final String FIND_ONLY_UNPAID = "Transaction.findOnlyUnpaid";
    public static final String FIND_BY_PRICE_RANGE = "Transaction.findByPriceRange";
    public static final String FIND_BY_CURRENCY_CODE = "Transaction.findByCurrencyCode";
    public static final String FIND_BY_PRICE_RANGE_AND_CURRENCY_CODE = "Transaction.findByPriceRangeAndCurrencyCode";
    public static final String FIND_BY_CLIENT = "Transaction.findByClient";
    public static final String FIND_BY_CLIENT_EAGERLY = "Transaction.findByClientEagerly";
    public static final String FIND_BY_CLIENT_AND_TRANSACTION_TIME = "Transaction.findByClientAndTransactionTime";
    public static final String FIND_BY_CLIENT_AND_BOOKED_TIME = "Transaction.findByClientAndBookedTime";
    public static final String FIND_BY_CLIENT_ONLY_PAID = "Transaction.findByClientOnlyPaid";
    public static final String FIND_BY_CLIENT_ONLY_UNPAID = "Transaction.findByClientOnlyUnpaid";
    public static final String FIND_BY_CLIENT_AND_PRICE_RANGE = "Transaction.findByClientAndPriceRange";
    public static final String FIND_BY_CLIENT_AND_CURRENCY_CODE = "Transaction.findByClientAndCurrencyCode";
    public static final String FIND_BY_CLIENT_AND_PRICE_RANGE_AND_CURRENCY_CODE = "Transaction.findByClientAndPriceRangeAndCurrencyCode";
    public static final String FIND_BY_PAYMENT_METHOD = "Transaction.findByPaymentMethod";
    public static final String FIND_BY_PAYMENT_METHOD_EAGERLY = "Transaction.findByPaymentMethodEagerly";
    public static final String FIND_BY_PROVIDER = "Transaction.findByProvider";
    public static final String FIND_BY_PROVIDER_EAGERLY = "Transaction.findByProviderEagerly";
    public static final String FIND_BY_SERVICE = "Transaction.findByService";
    public static final String FIND_BY_SERVICE_EAGERLY = "Transaction.findByServiceEagerly";
    public static final String FIND_BY_PROVIDER_SERVICE = "Transaction.findByProviderService";
    public static final String FIND_BY_PROVIDER_SERVICE_EAGERLY = "Transaction.findByProviderServiceEagerly";
    public static final String FIND_BY_EMPLOYEE = "Transaction.findByEmployee";
    public static final String FIND_BY_EMPLOYEE_EAGERLY = "Transaction.findByEmployeeEagerly";
    public static final String FIND_BY_WORK_STATION = "Transaction.findByWorkStation";
    public static final String FIND_BY_WORK_STATION_EAGERLY = "Transaction.findByWorkStationEagerly";
    public static final String FIND_BY_SERVICE_POINT = "Transaction.findByServicePoint";
    public static final String FIND_BY_SERVICE_POINT_EAGERLY = "Transaction.findByServicePointEagerly";
    public static final String FIND_BY_TERM = "Transaction.findByTerm";
    public static final String FIND_BY_TERM_EAGERLY = "Transaction.findByTermEagerly";
    public static final String FIND_BY_CLIENT_AND_PAYMENT_METHOD = "Transaction.findByClientAndPaymentMethod";
    public static final String FIND_BY_CLIENT_AND_PROVIDER = "Transaction.findByClientAndProvider";
    public static final String FIND_BY_CLIENT_AND_SERVICE = "Transaction.findByClientAndService";
    public static final String FIND_BY_CLIENT_AND_PROVIDER_SERVICE = "Transaction.findByClientAndProviderService";
    public static final String FIND_BY_CLIENT_AND_EMPLOYEE = "Transaction.findByClientAndEmployee";
    public static final String FIND_BY_CLIENT_AND_WORK_STATION = "Transaction.findByClientAndWorkStation";
    public static final String FIND_BY_CLIENT_AND_SERVICE_POINT = "Transaction.findByClientAndServicePoint";
    public static final String FIND_BY_CLIENT_AND_TERM = "Transaction.findByClientAndTerm";
    public static final String COUNT_BY_CLIENT = "Transaction.countByClient";
    public static final String COUNT_BY_PAYMENT_METHOD = "Transaction.countByPaymentMethod";
    public static final String COUNT_BY_PROVIDER = "Transaction.countByProvider";
    public static final String COUNT_BY_SERVICE = "Transaction.countByService";
    public static final String COUNT_BY_PROVIDER_SERVICE = "Transaction.countByProviderService";
    public static final String COUNT_BY_EMPLOYEE = "Transaction.countByEmployee";
    public static final String COUNT_BY_WORK_STATION = "Transaction.countByWorkStation";
    public static final String COUNT_BY_SERVICE_POINT = "Transaction.countByServicePoint";
    public static final String COUNT_BY_TERM = "Transaction.countByTerm";
    public static final String DELETE_BY_CLIENT = "Transaction.deleteByClient";
    public static final String DELETE_BY_ID = "Transaction.deleteById";
    public static final String DELETE_EMPLOYEES_ASSOCIATIONS_BY_TRANSACTION = "Transaction.deleteEmployeesAssociationsByTransaction";
    public static final String DELETE_EMPLOYEES_ASSOCIATIONS_BY_CLIENT = "Transaction.deleteEmployeesAssociationsByClient";

    public Transaction() {
    }

    public Transaction(Client client, Integer transactionNumber) {
        super(client, transactionNumber);
    }

    public Transaction(Client client, Integer transactionNumber, Double price, CurrencyCode priceCurrencyCode, Date transactionTime, Date bookedTime, Boolean paid, Service service, WorkStation workStation, PaymentMethod paymentMethod, Term term) {
        super(client, transactionNumber, price, priceCurrencyCode, transactionTime, bookedTime, paid, service, workStation, paymentMethod, term);
    }
}
