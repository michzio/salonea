package pl.salonea.entities.idclass;


import java.io.Serializable;
import java.util.Date;

public class CreditCardId implements Serializable {

    private Long client;
    private String creditCardNumber;
    private Date expirationDate;

    /* constructors */

    public CreditCardId() { }

    public CreditCardId(Long clientId, String creditCardNumber, Date expirationDate) {
        this.client = clientId;
        this.creditCardNumber = creditCardNumber;
        this.expirationDate = expirationDate;
    }

    /* getters and setters */

    public Long getClient() {
        return client;
    }

    public void setClient(Long clientId) {
        this.client = clientId;
    }

    public String getCreditCardNumber() {
        return creditCardNumber;
    }

    public void setCreditCardNumber(String creditCardNumber) {
        this.creditCardNumber = creditCardNumber;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    /* equals() and hashCode() */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CreditCardId that = (CreditCardId) o;

        if (client != null ? !client.equals(that.client) : that.client != null) return false;
        if (creditCardNumber != null ? !creditCardNumber.equals(that.creditCardNumber) : that.creditCardNumber != null)
            return false;
        return !(expirationDate != null ? !expirationDate.equals(that.expirationDate) : that.expirationDate != null);

    }

    @Override
    public int hashCode() {
        int result = client != null ? client.hashCode() : 0;
        result = 31 * result + (creditCardNumber != null ? creditCardNumber.hashCode() : 0);
        result = 31 * result + (expirationDate != null ? expirationDate.hashCode() : 0);
        return result;
    }
}
