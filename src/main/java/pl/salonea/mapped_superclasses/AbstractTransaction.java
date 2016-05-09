package pl.salonea.mapped_superclasses;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import pl.salonea.constraints.BookedTimeInTerm;
import pl.salonea.constraints.ChronologicalDates;
import pl.salonea.entities.*;
import pl.salonea.entities.idclass.TransactionId;
import pl.salonea.enums.CurrencyCode;
import pl.salonea.jaxrs.utils.hateoas.Link;

import javax.persistence.*;
import javax.validation.constraints.*;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlTransient;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

@MappedSuperclass
@IdClass(TransactionId.class)
@ChronologicalDates(dateAttributes = {"transactionTime", "bookedTime",}, order = ChronologicalDates.Order.ASCENDING)
@BookedTimeInTerm
// @ProviderServiceExists
// @ProviderServiceSuppliedOnWorkStation
// @EmployeeWorksOnWorkStationInGivenTerm
public abstract class AbstractTransaction {
    private Client client; // PK, FK
    private Integer transactionNumber; // PK

    private Double price;
    private CurrencyCode priceCurrencyCode;
    private Date transactionTime;
    private Date bookedTime;
    private Boolean paid;

    private ProviderService providerService;
    private Provider provider;
    private Service service;
    private ServicePoint servicePoint;
    private WorkStation workStation;
    private PaymentMethod paymentMethod;
    private Term term;

    private Set<Employee> employees;

    // HATEOAS support for RESTFul web service in JAX-RS
    private LinkedHashSet<Link> links = new LinkedHashSet<>();

    /* constructors */

    public AbstractTransaction() { }

    public AbstractTransaction(Client client, Integer transactionNumber) {
        this.client = client;
        this.transactionNumber = transactionNumber;
    }

    public AbstractTransaction(Client client, Integer transactionNumber, Double price, CurrencyCode priceCurrencyCode, Date transactionTime, Date bookedTime, Boolean paid, Service service, WorkStation workStation, PaymentMethod paymentMethod, Term term) {
        this.client = client;
        this.transactionNumber = transactionNumber;
        this.price = price;
        this.priceCurrencyCode = priceCurrencyCode;
        this.transactionTime = transactionTime;
        this.bookedTime = bookedTime;
        this.paid = paid;
        this.service = service;
        this.workStation = workStation;
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

    @Past
    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "transaction_time", nullable = false, columnDefinition = "DATETIME")
    public Date getTransactionTime() {
        return transactionTime;
    }

    public void setTransactionTime(Date transactionTime) {
        this.transactionTime = transactionTime;
    }

    @Future
    @NotNull
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

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumns({
            @JoinColumn(name = "service_id", referencedColumnName = "service_id", insertable = false, updatable = false),
            @JoinColumn(name = "provider_id", referencedColumnName = "provider_id", insertable = false, updatable = false)
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

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "service_id", referencedColumnName = "service_id", nullable = false, columnDefinition = "INT UNSIGNED")
    public Service getService() {
        return service;
    }

    private void setService(Service service) {
        this.service = service;
    }

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumns({
        @JoinColumn(name = "provider_id", referencedColumnName = "provider_id", nullable = false, columnDefinition = "BIGINT UNSIGNED"),
        @JoinColumn(name = "service_point_no", referencedColumnName = "service_point_no", nullable = false, columnDefinition = "INT UNSIGNED"),
        @JoinColumn(name = "work_station_no", referencedColumnName = "work_station_no", nullable = false, columnDefinition = "INT UNSIGNED")
    })
    public WorkStation getWorkStation() {
        return workStation;
    }

    public void setWorkStation(WorkStation workStation) {
        this.workStation = workStation;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumns({
            @JoinColumn(name = "provider_id", referencedColumnName = "provider_id", insertable = false, updatable = false),
            @JoinColumn(name = "service_point_no", referencedColumnName = "service_point_no", insertable = false, updatable = false)
    })
    public ServicePoint getServicePoint() {
        return servicePoint;
    }

    public void setServicePoint(ServicePoint servicePoint) {
        this.servicePoint = servicePoint;
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

    @XmlTransient
    @NotNull
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

    @Override
    public int hashCode() {

        return new HashCodeBuilder(17, 31) // two randomly chosen prime numbers
                // if deriving: .appendSuper(super.hashCode())
                .append(getClient())
                .append(getTransactionNumber())
                .toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null || !(obj instanceof AbstractTransaction))
            return false;
        if(obj == this)
            return true;

        AbstractTransaction rhs = (AbstractTransaction) obj;
        return new EqualsBuilder()
                // if deriving: .appendSuper(super.equals(obj))
                .append(getClient(), rhs.getClient())
                .append(getTransactionNumber(), rhs.getTransactionNumber())
                .isEquals();
    }

    @Transient
    @XmlElementWrapper(name = "links")
    @XmlElement(name = "link")
    public LinkedHashSet<Link> getLinks() {
        return links;
    }

    public void setLinks(LinkedHashSet<Link> links) {
        this.links = links;
    }
}
