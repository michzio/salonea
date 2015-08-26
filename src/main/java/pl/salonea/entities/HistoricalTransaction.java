package pl.salonea.entities;

import pl.salonea.enums.CurrencyCode;
import pl.salonea.enums.TransactionCompletionStatus;
import pl.salonea.mapped_superclasses.AbstractTransaction;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "historical_transaction")
@Access(AccessType.PROPERTY)
@AssociationOverride(
        name="employees",
        joinTable=@JoinTable(
                name="historical_transaction_executed_by",
                joinColumns = {
                        @JoinColumn(name = "client_id", referencedColumnName = "client_id", nullable = false, columnDefinition = "BIGINT UNSIGNED"),
                        @JoinColumn(name = "transaction_no", referencedColumnName = "transaction_no", nullable = false, columnDefinition = "INT UNSIGNED")
                },
                inverseJoinColumns = @JoinColumn(name = "employee_id", referencedColumnName = "employee_id", nullable = false, columnDefinition = "BIGINT UNSIGNED")
        )
)
@NamedQueries({
        @NamedQuery(name = HistoricalTransaction.FIND_BY_CLIENT, query = "SELECT tx FROM HistoricalTransaction tx WHERE tx.client = :client"),
        @NamedQuery(name = HistoricalTransaction.FIND_BY_CLIENT_AND_TRANSACTION_TIME, query = "SELECT tx FROM HistoricalTransaction tx WHERE tx.client = :client " +
                "AND tx.transactionTime >= :start_time AND tx.transactionTime <= :end_time"),
        @NamedQuery(name = HistoricalTransaction.FIND_BY_CLIENT_AND_BOOKED_TIME, query = "SELECT tx FROM HistoricalTransaction tx WHERE tx.client = :client " +
                "AND tx.bookedTime >= :start_time AND tx.bookedTime <= :end_time"),
        @NamedQuery(name = HistoricalTransaction.FIND_BY_CLIENT_ONLY_PAID, query = "SELECT tx FROM HistoricalTransaction tx WHERE tx.client = :client AND tx.paid = TRUE"),
        @NamedQuery(name = HistoricalTransaction.FIND_BY_CLIENT_ONLY_UNPAID, query = "SELECT tx FROM HistoricalTransaction tx WHERE tx.client = :client AND tx.paid = FALSE"),
        @NamedQuery(name = HistoricalTransaction.FIND_BY_CLIENT_AND_PRICE_RANGE, query = "SELECT tx FROM HistoricalTransaction tx WHERE tx.client = :client AND " +
                " tx.price >= :min_price AND tx.price <= :max_price"),
        @NamedQuery(name = HistoricalTransaction.FIND_BY_CLIENT_AND_CURRENCY_CODE, query = "SELECT tx FROM HistoricalTransaction tx WHERE tx.client = :client AND " +
                " tx.priceCurrencyCode = :currency_code"),
        @NamedQuery(name = HistoricalTransaction.FIND_BY_CLIENT_AND_PRICE_RANGE_AND_CURRENCY_CODE, query = "SELECT tx FROM HistoricalTransaction tx WHERE tx.client = :client AND " +
                " tx.price >= :min_price AND tx.price <= :max_price AND tx.priceCurrencyCode = :currency_code"),
        @NamedQuery(name = HistoricalTransaction.FIND_BY_CLIENT_AND_PAYMENT_METHOD, query = "SELECT tx FROM HistoricalTransaction tx WHERE tx.client = :client AND tx.paymentMethod = :payment_method"),
        @NamedQuery(name = HistoricalTransaction.FIND_BY_CLIENT_AND_PROVIDER, query = "SELECT tx FROM HistoricalTransaction tx WHERE tx.client = :client AND tx.provider = :provider"),
        @NamedQuery(name = HistoricalTransaction.FIND_BY_CLIENT_AND_SERVICE, query = "SELECT tx FROM HistoricalTransaction tx WHERE tx.client = :client AND tx.service = :service"),
        @NamedQuery(name = HistoricalTransaction.FIND_BY_CLIENT_AND_EMPLOYEE, query = "SELECT tx FROM HistoricalTransaction tx WHERE tx.client = :client AND :employee MEMBER OF tx.employees"),
        @NamedQuery(name = HistoricalTransaction.FIND_BY_CLIENT_AND_WORK_STATION, query = "SELECT tx FROM HistoricalTransaction tx WHERE tx.client = :client AND tx.workStation = :work_station"),
        @NamedQuery(name = HistoricalTransaction.FIND_BY_CLIENT_AND_SERVICE_POINT, query = "SELECT tx FROM HistoricalTransaction tx WHERE tx.client = :client AND tx.servicePoint = :service_point"),
        @NamedQuery(name = HistoricalTransaction.FIND_BY_CLIENT_AND_COMPLETION_STATUS, query = "SELECT tx FROM HistoricalTransaction tx WHERE tx.client = :client AND tx.completionStatus = :completion_status"),
        @NamedQuery(name = HistoricalTransaction.FIND_BY_CLIENT_AND_CLIENT_RATING_RANGE, query = "SELECT tx FROM HistoricalTransaction tx WHERE tx.client = :client AND tx.clientRating >= :min_rating AND tx.clientRating <= :max_rating"),
        @NamedQuery(name = HistoricalTransaction.FIND_BY_CLIENT_AND_PROVIDER_RATING_RANGE, query = "SELECT tx FROM HistoricalTransaction tx WHERE tx.client = :client AND tx.providerRating >= :min_rating AND tx.providerRating <= :max_rating"),
        @NamedQuery(name = HistoricalTransaction.FIND_BY_TRANSACTION_TIME, query = "SELECT tx FROM HistoricalTransaction tx WHERE tx.transactionTime >= :start_time AND tx.transactionTime <= :end_time"),
        @NamedQuery(name = HistoricalTransaction.FIND_BY_BOOKED_TIME, query = "SELECT tx FROM HistoricalTransaction tx WHERE tx.bookedTime >= :start_time AND tx.bookedTime <= :end_time"),
        @NamedQuery(name = HistoricalTransaction.FIND_ONLY_PAID, query = "SELECT tx FROM HistoricalTransaction tx WHERE tx.paid = TRUE"),
        @NamedQuery(name = HistoricalTransaction.FIND_ONLY_UNPAID, query = "SELECT tx FROM HistoricalTransaction tx WHERE tx.paid = FALSE"),
        @NamedQuery(name = HistoricalTransaction.FIND_BY_PRICE_RANGE, query = "SELECT tx FROM HistoricalTransaction tx WHERE tx.price >= :min_price AND tx.price <= :max_price"),
        @NamedQuery(name = HistoricalTransaction.FIND_BY_CURRENCY_CODE, query = "SELECT tx FROM HistoricalTransaction tx WHERE tx.priceCurrencyCode = :currency_code"),
        @NamedQuery(name = HistoricalTransaction.FIND_BY_PRICE_RANGE_AND_CURRENCY_CODE, query = "SELECT tx FROM HistoricalTransaction tx WHERE tx.price >= :min_price AND tx.price <= :max_price AND tx.priceCurrencyCode = :currency_code"),
        @NamedQuery(name = HistoricalTransaction.FIND_BY_PAYMENT_METHOD, query = "SELECT tx FROM HistoricalTransaction tx WHERE tx.paymentMethod = :payment_method"),
        @NamedQuery(name = HistoricalTransaction.FIND_BY_PROVIDER, query = "SELECT tx FROM HistoricalTransaction tx WHERE tx.provider = :provider"),
        @NamedQuery(name = HistoricalTransaction.FIND_BY_SERVICE, query = "SELECT tx FROM HistoricalTransaction tx WHERE tx.service = :service"),
        @NamedQuery(name = HistoricalTransaction.FIND_BY_EMPLOYEE, query = "SELECT tx FROM HistoricalTransaction tx WHERE :employee MEMBER OF tx.employees"),
        @NamedQuery(name = HistoricalTransaction.FIND_BY_WORK_STATION, query = "SELECT tx FROM HistoricalTransaction tx WHERE tx.workStation = :work_station"),
        @NamedQuery(name = HistoricalTransaction.FIND_BY_SERVICE_POINT, query = "SELECT tx FROM HistoricalTransaction tx WHERE tx.servicePoint = :service_point"),
        @NamedQuery(name = HistoricalTransaction.FIND_BY_COMPLETION_STATUS, query = "SELECT tx FROM HistoricalTransaction tx WHERE tx.completionStatus = :completion_status"),
        @NamedQuery(name = HistoricalTransaction.FIND_BY_CLIENT_RATING_RANGE, query = "SELECT tx FROM HistoricalTransaction tx WHERE tx.clientRating >= :min_rating AND tx.clientRating <= :max_rating"),
        @NamedQuery(name = HistoricalTransaction.FIND_BY_PROVIDER_RATING_RANGE, query = "SELECT tx FROM HistoricalTransaction tx WHERE tx.providerRating >= :min_rating AND tx.providerRating <= :max_rating"),
        @NamedQuery(name = HistoricalTransaction.DELETE_BY_CLIENT, query = "DELETE FROM HistoricalTransaction tx WHERE tx.client = :client"),
})
public class HistoricalTransaction extends AbstractTransaction implements Serializable {

    public static final String FIND_BY_CLIENT = "HistoricalTransaction.findByClient";
    public static final String FIND_BY_CLIENT_AND_TRANSACTION_TIME = "HistoricalTransaction.findByClientAndTransactionTime";
    public static final String FIND_BY_CLIENT_AND_BOOKED_TIME = "HistoricalTransaction.findByClientAndBookedTime";
    public static final String FIND_BY_CLIENT_ONLY_PAID = "HistoricalTransaction.findByClientOnlyPaid";
    public static final String FIND_BY_CLIENT_ONLY_UNPAID = "HistoricalTransaction.findByClientOnlyUnpaid";
    public static final String FIND_BY_CLIENT_AND_PRICE_RANGE = "HistoricalTransaction.findByClientAndPriceRange";
    public static final String FIND_BY_CLIENT_AND_CURRENCY_CODE = "HistoricalTransaction.findByClientAndCurrencyCode";
    public static final String FIND_BY_CLIENT_AND_PRICE_RANGE_AND_CURRENCY_CODE = "HistoricalTransaction.findByClientAndPriceRangeAndCurrencyCode";
    public static final String FIND_BY_CLIENT_AND_PAYMENT_METHOD = "HistoricalTransaction.findByClientAndPaymentMethod";
    public static final String FIND_BY_CLIENT_AND_PROVIDER = "HistoricalTransaction.findByClientAndProvider";
    public static final String FIND_BY_CLIENT_AND_SERVICE = "HistoricalTransaction.findByClientAndService";
    public static final String FIND_BY_CLIENT_AND_EMPLOYEE = "HistoricalTransaction.findByClientAndEmployee";
    public static final String FIND_BY_CLIENT_AND_WORK_STATION = "HistoricalTransaction.findByClientAndWorkStation";
    public static final String FIND_BY_CLIENT_AND_SERVICE_POINT = "HistoricalTransaction.findByClientAndServicePoint";
    public static final String FIND_BY_CLIENT_AND_COMPLETION_STATUS = "HistoricalTransaction.findByClientAndCompletionStatus";
    public static final String FIND_BY_CLIENT_AND_CLIENT_RATING_RANGE = "HistoricalTransaction.findByClientAndClientRatingRange";
    public static final String FIND_BY_CLIENT_AND_PROVIDER_RATING_RANGE = "HistoricalTransaction.findByClientAndProviderRatingRange";
    public static final String FIND_BY_TRANSACTION_TIME = "HistoricalTransaction.findByTransactionTime";
    public static final String FIND_BY_BOOKED_TIME = "HistoricalTransaction.findByBookedTime";
    public static final String FIND_ONLY_PAID = "HistoricalTransaction.findOnlyPaid";
    public static final String FIND_ONLY_UNPAID = "HistoricalTransaction.findOnlyUnpaid";
    public static final String FIND_BY_PRICE_RANGE = "HistoricalTransaction.findByPriceRange";
    public static final String FIND_BY_CURRENCY_CODE = "HistoricalTransaction.findByCurrencyCode";
    public static final String FIND_BY_PRICE_RANGE_AND_CURRENCY_CODE = "HistoricalTransaction.findByPriceRangeAndCurrencyCode";
    public static final String FIND_BY_PAYMENT_METHOD = "HistoricalTransaction.findByPaymentMethod";
    public static final String FIND_BY_PROVIDER = "HistoricalTransaction.findByProvider";
    public static final String FIND_BY_SERVICE = "HistoricalTransaction.findByService";
    public static final String FIND_BY_EMPLOYEE = "HistoricalTransaction.findByEmployee";
    public static final String FIND_BY_WORK_STATION = "HistoricalTransaction.findByWorkStation";
    public static final String FIND_BY_SERVICE_POINT = "HistoricalTransaction.findByServicePoint";
    public static final String FIND_BY_COMPLETION_STATUS = "HistoricalTransaction.findByCompletionStatus";
    public static final String FIND_BY_CLIENT_RATING_RANGE = "HistoricalTransaction.findByClientRatingRange";
    public static final String FIND_BY_PROVIDER_RATING_RANGE = "HistoricalTransaction.findByProviderRatingRange";
    public static final String DELETE_BY_CLIENT = "HistoricalTransaction.deleteByClient";

    /* additional attributes */

    private TransactionCompletionStatus completionStatus;
    private String clientComment;
    private Short clientRating;
    private String providerDementi;
    private Short providerRating;

    /* constructors */

    public HistoricalTransaction() {
    }

    public HistoricalTransaction(Client client, Integer transactionNumber) {
        super(client, transactionNumber);
    }

    public HistoricalTransaction(Client client, Integer transactionNumber, Double price, CurrencyCode priceCurrencyCode, Date transactionTime, Date bookedTime, Boolean paid, Service service, WorkStation workStation, PaymentMethod paymentMethod, Term term, TransactionCompletionStatus completionStatus) {
        super(client, transactionNumber, price, priceCurrencyCode, transactionTime, bookedTime, paid, service, workStation, paymentMethod, term);
        this.completionStatus = completionStatus;
    }

    /* getters and setters */

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "completion_status", nullable = false, columnDefinition = "ENUM('COMPLETED','CANCELED', 'REJECTED', 'UNCOMPLETED') DEFAULT 'UNCOMPLETED'")
    public TransactionCompletionStatus getCompletionStatus() {
        return completionStatus;
    }

    public void setCompletionStatus(TransactionCompletionStatus completionStatus) {
        this.completionStatus = completionStatus;
    }

    @Lob
    @Column(name = "client_comment", columnDefinition = "LONGTEXT DEFAULT NULL")
    public String getClientComment() {
        return clientComment;
    }

    public void setClientComment(String clientComment) {
        this.clientComment = clientComment;
    }

    @Min(0) @Max(10)
    @Column(name = "client_rating", columnDefinition = "TINYINT UNSIGNED DEFAULT NULL")
    public Short getClientRating() {
        return clientRating;
    }

    public void setClientRating(Short clientRating) {
        this.clientRating = clientRating;
    }

    @Lob
    @Column(name = "provider_dementi", columnDefinition = "LONGTEXT DEFAULT NULL")
    public String getProviderDementi() {
        return providerDementi;
    }

    public void setProviderDementi(String providerDementi) {
        this.providerDementi = providerDementi;
    }

    @Min(0) @Max(10)
    @Column(name = "provider_rating", columnDefinition = "TINYINT UNSIGNED DEFAULT NULL")
    public Short getProviderRating() {
        return providerRating;
    }

    public void setProviderRating(Short providerRating) {
        this.providerRating = providerRating;
    }
}
