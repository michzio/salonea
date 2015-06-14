package pl.salonea.entities.idclass;


import java.io.Serializable;

public class TransactionId  implements Serializable {

    private Long client;
    private Integer transactionNumber;

    /* constructors */

    public TransactionId() { }

    public TransactionId(Long clientId, Integer transactionNumber) {
        this.client = clientId;
        this.transactionNumber = transactionNumber;
    }

    /* getters and setters */

    public Long getClient() {
        return client;
    }

    public void setClient(Long client) {
        this.client = client;
    }

    public Integer getTransactionNumber() {
        return transactionNumber;
    }

    public void setTransactionNumber(Integer transactionNumber) {
        this.transactionNumber = transactionNumber;
    }

    /* equals() and hashCode() */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TransactionId that = (TransactionId) o;

        if (client != null ? !client.equals(that.client) : that.client != null) return false;
        return !(transactionNumber != null ? !transactionNumber.equals(that.transactionNumber) : that.transactionNumber != null);

    }

    @Override
    public int hashCode() {
        int result = client != null ? client.hashCode() : 0;
        result = 31 * result + (transactionNumber != null ? transactionNumber.hashCode() : 0);
        return result;
    }
}
