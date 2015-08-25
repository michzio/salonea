package pl.salonea.entities;

import pl.salonea.enums.CurrencyCode;
import pl.salonea.mapped_superclasses.AbstractTransaction;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "transaction")
@Access(AccessType.PROPERTY)
@NamedQueries({
        @NamedQuery(name = Transaction.FIND_BY_CLIENT, query = "SELECT tx FROM Transaction tx WHERE tx.client = :client"),
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
        @NamedQuery(name = Transaction.FIND_BY_CLIENT_AND_PAYMENT_METHOD, query = "SELECT tx FROM Transaction tx WHERE tx.client = :client AND tx.paymentMethod = :payment_method"),
        @NamedQuery(name = Transaction.FIND_BY_CLIENT_AND_PROVIDER, query = "SELECT tx FROM Transaction tx WHERE tx.client = :client AND tx.provider = :provider"),
        @NamedQuery(name = Transaction.FIND_BY_CLIENT_AND_SERVICE, query = "SELECT tx FROM Transaction tx WHERE tx.client = :client AND tx.service = :service"),
        @NamedQuery(name = Transaction.FIND_BY_CLIENT_AND_EMPLOYEE, query = "SELECT tx FROM Transaction tx WHERE tx.client = :client AND :employee MEMBER OF tx.employees"),
        @NamedQuery(name = Transaction.FIND_BY_CLIENT_AND_WORK_STATION, query = "SELECT tx FROM Transaction tx WHERE tx.client = :client AND tx.workStation = :work_station"),
        @NamedQuery(name = Transaction.FIND_BY_CLIENT_AND_SERVICE_POINT, query = "SELECT tx FROM Transaction tx WHERE tx.client = :client AND tx.servicePoint = :service_point"),
        @NamedQuery(name = Transaction.FIND_BY_TRANSACTION_TIME, query = "SELECT tx FROM Transaction tx WHERE tx.transactionTime >= :start_time AND tx.transactionTime <= :end_time"),
        @NamedQuery(name = Transaction.FIND_BY_BOOKED_TIME, query = "SELECT tx FROM Transaction tx WHERE tx.bookedTime >= :start_time AND tx.bookedTime <= :end_time"),
        @NamedQuery(name = Transaction.FIND_ONLY_PAID, query = "SELECT tx FROM Transaction tx WHERE tx.paid = TRUE"),
        @NamedQuery(name = Transaction.FIND_ONLY_UNPAID, query = "SELECT tx FROM Transaction tx WHERE tx.paid = FALSE"),
        @NamedQuery(name = Transaction.FIND_BY_PRICE_RANGE, query = "SELECT tx FROM Transaction tx WHERE tx.price >= :min_price AND tx.price <= :max_price"),
        @NamedQuery(name = Transaction.FIND_BY_CURRENCY_CODE, query = "SELECT tx FROM Transaction tx WHERE tx.priceCurrencyCode = :currency_code"),
        @NamedQuery(name = Transaction.FIND_BY_PRICE_RANGE_AND_CURRENCY_CODE, query = "SELECT tx FROM Transaction tx WHERE tx.price >= :min_price AND tx.price <= :max_price AND tx.priceCurrencyCode = :currency_code"),
        @NamedQuery(name = Transaction.FIND_BY_PAYMENT_METHOD, query = "SELECT tx FROM Transaction tx WHERE tx.paymentMethod = :payment_method"),
        @NamedQuery(name = Transaction.FIND_BY_PROVIDER, query = "SELECT tx FROM Transaction tx WHERE tx.provider = :provider"),
        @NamedQuery(name = Transaction.FIND_BY_SERVICE, query = "SELECT tx FROM Transaction tx WHERE tx.service = :service"),
        @NamedQuery(name = Transaction.FIND_BY_EMPLOYEE, query = "SELECT tx FROM Transaction tx WHERE :employee MEMBER OF tx.employees"),
        @NamedQuery(name = Transaction.FIND_BY_WORK_STATION, query = "SELECT tx FROM Transaction tx WHERE tx.workStation = :work_station"),
        @NamedQuery(name = Transaction.FIND_BY_SERVICE_POINT, query = "SELECT tx FROM Transaction tx WHERE tx.servicePoint = :service_point"),
        @NamedQuery(name = Transaction.DELETE_BY_CLIENT, query = "DELETE FROM Transaction tx WHERE tx.client = :client"),
})
public class Transaction extends AbstractTransaction implements Serializable {

    public static final String FIND_BY_CLIENT = "Transaction.findByClient";
    public static final String FIND_BY_CLIENT_AND_TRANSACTION_TIME = "Transaction.findByClientAndTransactionTime";
    public static final String FIND_BY_CLIENT_AND_BOOKED_TIME = "Transaction.findByClientAndBookedTime";
    public static final String FIND_BY_CLIENT_ONLY_PAID = "Transaction.findByClientOnlyPaid";
    public static final String FIND_BY_CLIENT_ONLY_UNPAID = "Transaction.findByClientOnlyUnpaid";
    public static final String FIND_BY_CLIENT_AND_PRICE_RANGE = "Transaction.findByClientAndPriceRange";
    public static final String FIND_BY_CLIENT_AND_CURRENCY_CODE = "Transaction.findByClientAndCurrencyCode";
    public static final String FIND_BY_CLIENT_AND_PRICE_RANGE_AND_CURRENCY_CODE = "Transaction.findByClientAndPriceRangeAndCurrencyCode";
    public static final String FIND_BY_CLIENT_AND_PAYMENT_METHOD = "Transaction.findByClientAndPaymentMethod";
    public static final String FIND_BY_CLIENT_AND_PROVIDER = "Transaction.findByClientAndProvider";
    public static final String FIND_BY_CLIENT_AND_SERVICE = "Transaction.findByClientAndService";
    public static final String FIND_BY_CLIENT_AND_EMPLOYEE = "Transaction.findByClientAndEmployee";
    public static final String FIND_BY_CLIENT_AND_WORK_STATION = "Transaction.findByClientAndWorkStation";
    public static final String FIND_BY_CLIENT_AND_SERVICE_POINT = "Transaction.findByClientAndServicePoint";
    public static final String FIND_BY_TRANSACTION_TIME = "Transaction.findByTransactionTime";
    public static final String FIND_BY_BOOKED_TIME = "Transaction.findByBookedTime";
    public static final String FIND_ONLY_PAID = "Transaction.findOnlyPaid";
    public static final String FIND_ONLY_UNPAID = "Transaction.findOnlyUnpaid";
    public static final String FIND_BY_PRICE_RANGE = "Transaction.findByPriceRange";
    public static final String FIND_BY_CURRENCY_CODE = "Transaction.findByCurrencyCode";
    public static final String FIND_BY_PRICE_RANGE_AND_CURRENCY_CODE = "Transaction.findByPriceRangeAndCurrencyCode";
    public static final String FIND_BY_PAYMENT_METHOD = "Transaction.findByPaymentMethod";
    public static final String FIND_BY_PROVIDER = "Transaction.findByProvider";
    public static final String FIND_BY_SERVICE = "Transaction.findByService";
    public static final String FIND_BY_EMPLOYEE = "Transaction.findByEmployee";
    public static final String FIND_BY_WORK_STATION = "Transaction.findByWorkStation";
    public static final String FIND_BY_SERVICE_POINT = "Transaction.findByServicePoint";
    public static final String DELETE_BY_CLIENT = "Transaction.deleteByClient";

    public Transaction() { }

    public Transaction(Client client, Integer transactionNumber) {
        super(client, transactionNumber);
    }

    public Transaction(Client client, Integer transactionNumber, Double price, CurrencyCode priceCurrencyCode, Date transactionTime, Date bookedTime, Boolean paid, Service service, WorkStation workStation, PaymentMethod paymentMethod, Term term) {
        super(client, transactionNumber, price, priceCurrencyCode, transactionTime, bookedTime, paid, service, workStation, paymentMethod, term);
    }
}
