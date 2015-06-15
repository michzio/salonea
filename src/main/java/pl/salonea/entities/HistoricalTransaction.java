package pl.salonea.entities;

import pl.salonea.enums.CurrencyCode;
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
public class HistoricalTransaction extends AbstractTransaction implements Serializable {

    /* additional attributes */

    private Boolean completionStatus;
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

    public HistoricalTransaction(Client client, Integer transactionNumber, Double price, CurrencyCode priceCurrencyCode, Date transactionTime, Date bookedTime, Boolean paid, ProviderService providerService, PaymentMethod paymentMethod, Term term, Boolean completionStatus) {
        super(client, transactionNumber, price, priceCurrencyCode, transactionTime, bookedTime, paid, providerService, paymentMethod, term);
        this.completionStatus = completionStatus;
    }

    /* getters and setters */

    @NotNull
    @Column(name = "completion_status", nullable = false, columnDefinition = "BOOL DEFAULT 0")
    public Boolean getCompletionStatus() {
        return completionStatus;
    }

    public void setCompletionStatus(Boolean completionStatus) {
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
