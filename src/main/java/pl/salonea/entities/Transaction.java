package pl.salonea.entities;

import org.apache.xpath.operations.Bool;
import org.hibernate.annotations.GenericGenerator;
import pl.salonea.entities.idclass.TransactionId;
import pl.salonea.enums.CurrencyCode;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "transaction")
@Access(AccessType.PROPERTY)
@IdClass(TransactionId.class)
public class Transaction {

    private Client client; // PK, FK
    private Integer transactionNumber; // PK

    private Double price;
    private CurrencyCode priceCurrencyCode;
    private Date transactionTime;
    private Date bookedTime;
    private Boolean paid;

    private ProviderService providerService;
    private Provider provider; // transient, accessed via ProviderService
    private Service service; // transient, accessed via ProviderService
    private PaymentMethod paymentMethod;
    private Term term;

    private Set<Employee> employees;

    /* constructors */

    public Transaction() { }

    public Transaction(Client client, Integer transactionNumber) {
        this.client = client;
        this.transactionNumber = transactionNumber;
    }

    public Transaction(Client client, Integer transactionNumber, Double price, CurrencyCode priceCurrencyCode, Date transactionTime, Date bookedTime, Boolean paid, ProviderService providerService, PaymentMethod paymentMethod, Term term) {
        this.client = client;
        this.transactionNumber = transactionNumber;
        this.price = price;
        this.priceCurrencyCode = priceCurrencyCode;
        this.transactionTime = transactionTime;
        this.bookedTime = bookedTime;
        this.paid = paid;
        this.providerService = providerService;
        this.paymentMethod = paymentMethod;
        this.term = term;
    }

    /* PK getters and setters */

    @Id
    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "client_id", referencedColumnName = "client_id", nullable = false, columnDefinition = "BIGINT UNSIGNED")
    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    @Id
    @Basic(optional = false)
    @Column(name = "transaction_no", nullable = false, columnDefinition = "INT UNSIGNED")
    public Integer getTransactionNumber() {
        return transactionNumber;
    }

    public void setTransactionNumber(Integer transactionNumber) {
        this.transactionNumber = transactionNumber;
    }

    /* other getters and setters */

    @NotNull
    @Digits(integer = 8, fraction = 2)
    @DecimalMin("0.00")
    @Column(name = "price", nullable = false, columnDefinition = "NUMERIC(10,2)")
    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "price_currency_code", nullable = false, columnDefinition = "CHAR(3)")
    public CurrencyCode getPriceCurrencyCode() {
        return priceCurrencyCode;
    }

    public void setPriceCurrencyCode(CurrencyCode priceCurrencyCode) {
        this.priceCurrencyCode = priceCurrencyCode;
    }

    @Past @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "transaction_time", nullable = false, columnDefinition = "DATETIME")
    public Date getTransactionTime() {
        return transactionTime;
    }

    public void setTransactionTime(Date transactionTime) {
        this.transactionTime = transactionTime;
    }

    @Future @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "booked_time", nullable = false, columnDefinition = "DATETIME")
    public Date getBookedTime() {
        return bookedTime;
    }

    public void setBookedTime(Date bookedTime) {
        this.bookedTime = bookedTime;
    }

    @NotNull
    @Column(name="paid", nullable = false, columnDefinition = "BOOL DEFAULT 0")
    public Boolean getPaid() {
        return paid;
    }

    public void setPaid(Boolean paid) {
        this.paid = paid;
    }

    /* many-to-one relationships */

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumns({
            @JoinColumn(name = "service_id", referencedColumnName = "service_id", nullable = false, columnDefinition = "INT UNSIGNED"),
            @JoinColumn(name = "provider_id", referencedColumnName = "provider_id", nullable = false, columnDefinition = "BIGINT UNSIGNED")
    })
    public ProviderService getProviderService() {
        return providerService;
    }

    public void setProviderService(ProviderService providerService) {
        this.providerService = providerService;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "provider_id", referencedColumnName = "provider_id", insertable = false, updatable = false)
    public Provider getProvider() {
        return provider;
    }

    private void setProvider(Provider provider) {
        this.provider = provider;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "service_id", referencedColumnName = "service_id", insertable = false, updatable = false)
    public Service getService() {
        return service;
    }

    private void setService(Service service) {
        this.service = service;
    }

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "payment_method_id", referencedColumnName = "payment_method_id", nullable = false, columnDefinition = "INT UNSIGNED")
    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "term_id", referencedColumnName = "term_id", nullable = false, columnDefinition = "BIGINT UNSIGNED")
    public Term getTerm() {
        return term;
    }

    public void setTerm(Term term) {
        this.term = term;
    }

    /* many-to-many relationships */

    @ManyToMany
    @JoinTable(name = "transaction_executed_by",
            joinColumns = {
                    @JoinColumn(name = "client_id", referencedColumnName = "client_id", nullable = false, columnDefinition = "BIGINT UNSIGNED"),
                    @JoinColumn(name = "transaction_no", referencedColumnName = "transaction_no", nullable = false, columnDefinition = "INT UNSIGNED")
            },
            inverseJoinColumns = @JoinColumn(name = "employee_id", referencedColumnName = "employee_id", nullable = false, columnDefinition = "BIGINT UNSIGNED")

    )
    public Set<Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(Set<Employee> employees) {
        this.employees = employees;
    }
}
