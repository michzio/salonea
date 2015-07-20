package pl.salonea.entities;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import pl.salonea.constraints.CreditCardValidity;
import pl.salonea.entities.idclass.CreditCardId;
import pl.salonea.enums.CreditCardType;

import javax.persistence.*;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

@Entity
@IdClass(CreditCardId.class)
@Table(name = "credit_card")
@Access(AccessType.PROPERTY)
@NamedQueries({
        @NamedQuery(name = CreditCard.FIND_BY_TYPE, query = "SELECT cc FROM CreditCard cc WHERE cc.cardType = :card_type"),
        @NamedQuery(name = CreditCard.FIND_EXPIRED, query = "SELECT cc FROM CreditCard cc WHERE cc.expirationDate < current_timestamp"),
        @NamedQuery(name = CreditCard.FIND_NOT_EXPIRED, query = "SELECT cc FROM CreditCard cc WHERE cc.expirationDate > current_timestamp"),
        @NamedQuery(name = CreditCard.FIND_EXPIRATION_DATE_AFTER, query = "SELECT cc FROM CreditCard cc WHERE cc.expirationDate >= :date"),
        @NamedQuery(name = CreditCard.FIND_EXPIRATION_DATE_BEFORE, query = "SELECT cc FROM CreditCard cc WHERE cc.expirationDate <= :date"),
        @NamedQuery(name = CreditCard.FIND_EXPIRATION_DATE_BETWEEN, query = "SELECT cc FROM CreditCard cc WHERE cc.expirationDate >= :start_date AND cc.expirationDate <= :end_date"),
        @NamedQuery(name = CreditCard.DELETE_WITH_EXPIRATION_DATE_BEFORE, query = "DELETE FROM CreditCard cc WHERE cc.expirationDate <= :date"),
        @NamedQuery(name = CreditCard.DELETE_WITH_EXPIRATION_DATE_AFTER, query = "DELETE FROM CreditCard cc WHERE cc.expirationDate >= :date"),
        @NamedQuery(name = CreditCard.DELETE_WITH_EXPIRATION_DATE_BETWEEN, query = "DELETE FROM CreditCard cc WHERE cc.expirationDate >= :start_date AND cc.expirationDate <= :end_date"),
        @NamedQuery(name = CreditCard.DELETE_EXPIRED, query = "DELETE FROM CreditCard cc WHERE cc.expirationDate < current_timestamp"),
        @NamedQuery(name = CreditCard.DELETE_WITH_TYPE, query = "DELETE FROM CreditCard cc WHERE cc.cardType = :card_type")
})
@CreditCardValidity
// TODO some online check of credit card validity i.e. number, holder, exp_date, type
public class CreditCard implements Serializable {

    public static final String FIND_BY_TYPE = "CreditCard.findByType";
    public static final String FIND_EXPIRED = "CreditCard.findExpired";
    public static final String FIND_NOT_EXPIRED = "CreditCard.findNotExpired";
    public static final String FIND_EXPIRATION_DATE_AFTER = "CreditCard.findExpirationDateAfter";
    public static final String FIND_EXPIRATION_DATE_BEFORE = "CreditCard.findExpirationDateBefore";
    public static final String FIND_EXPIRATION_DATE_BETWEEN = "CreditCard.findExpirationDateBetween";
    public static final String DELETE_WITH_EXPIRATION_DATE_BEFORE = "CreditCard.deleteWithExpirationDateBefore";
    public static final String DELETE_WITH_EXPIRATION_DATE_AFTER = "CreditCard.deleteWithExpirationDateAfter";
    public static final String DELETE_WITH_EXPIRATION_DATE_BETWEEN = "CreditCard.deleteWithExpirationDateBetween";
    public static final String DELETE_EXPIRED = "CreditCard.deleteExpired";
    public static final String DELETE_WITH_TYPE = "CreditCard.deleteWithType";

    private String creditCardNumber; // PK
    private Date expirationDate; // PK
    private Client client; // PK, FK

    /* simple attributes */
    private String cardHolder;
    private CreditCardType cardType;

    /* constructors */

    public CreditCard() { }

    public CreditCard(Client client, String creditCardNumber, Date expirationDate, String cardHolder, CreditCardType cardType) {
        this.creditCardNumber = creditCardNumber;
        this.expirationDate = expirationDate;
        setClient(client);
        this.cardHolder = cardHolder;
        this.cardType = cardType;
    }

    /* PK, FK getters and setters */

    @Id
    @Basic(optional = false)
    @Size(min = 10, max = 20) // credit card numbers are usually 12-19 numbers long
    @Column(name = "card_no", nullable = false, length = 20)
    public String getCreditCardNumber() {
        return creditCardNumber;
    }

    public void setCreditCardNumber(String creditCardNumber) {
        this.creditCardNumber = creditCardNumber;
    }

    @Id
    @Basic(optional = false)
    @Temporal(TemporalType.TIMESTAMP) @Future
    @Column(name = "expiration_date", nullable = false, columnDefinition = "TIMESTAMP")
    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    @Id
    @JoinColumn(name = "client_id", referencedColumnName = "client_id", nullable = false, columnDefinition = "BIGINT UNSIGNED")
    @ManyToOne(fetch = FetchType.EAGER)
    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
        /* if(!client.getCreditCards().contains(this)) {
            client.getCreditCards().add(this);
        }*/
    }

    /* other getters and setters */

    @NotNull
    @Size(min = 2, max = 45)
    @Column(name = "card_holder", nullable = false, length = 45)
    public String getCardHolder() {
        return cardHolder;
    }

    public void setCardHolder(String cardHolder) {
        this.cardHolder = cardHolder;
    }

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "card_type", nullable = false, columnDefinition = "ENUM('VISA', 'VISA_ELECTRON', 'MASTERCARD', 'MAESTRO', 'AMERICAN_EXPRESS') DEFAULT 'VISA'")
    public CreditCardType getCardType() {
        return cardType;
    }

    public void setCardType(CreditCardType cardType) {
        this.cardType = cardType;
    }

    @Override
    public int hashCode() {

        return new HashCodeBuilder(17, 31). // two randomly chosen prime numbers
                // if deriving: appendSuper(super.hashCode()).
                append(getClient())
                .append(getCreditCardNumber())
                .append(getExpirationDate())
                .toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof CreditCard))
            return false;
        if (obj == this)
            return true;

        CreditCard rhs = (CreditCard) obj;
        return new EqualsBuilder().
                // if deriving: appendSuper(super.equals(obj)).
                append(getClient(), rhs.getClient())
                .append(getCreditCardNumber(), rhs.getCreditCardNumber())
                .append(getExpirationDate(), rhs.getExpirationDate())
                .isEquals();
    }

}
