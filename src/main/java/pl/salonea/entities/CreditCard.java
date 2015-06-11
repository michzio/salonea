package pl.salonea.entities;

import pl.salonea.constraints.CreditCardValidity;
import pl.salonea.enums.CreditCardType;

import javax.persistence.*;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "credit_card")
@Access(AccessType.PROPERTY)
@CreditCardValidity
// TODO some online check of credit card validity i.e. number, holder, exp_date, type
public class CreditCard implements Serializable {

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
        this.client = client;
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

}
