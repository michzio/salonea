package pl.salonea.entities;

import pl.salonea.enums.CurrencyCode;
import pl.salonea.mapped_superclasses.AbstractTransaction;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "transaction")
@Access(AccessType.PROPERTY)
public class Transaction extends AbstractTransaction implements Serializable {

    public Transaction() { }

    public Transaction(Client client, Integer transactionNumber) {
        super(client, transactionNumber);
    }

    public Transaction(Client client, Integer transactionNumber, Double price, CurrencyCode priceCurrencyCode, Date transactionTime, Date bookedTime, Boolean paid, ProviderService providerService, PaymentMethod paymentMethod, Term term) {
        super(client, transactionNumber, price, priceCurrencyCode, transactionTime, bookedTime, paid, providerService, paymentMethod, term);
    }
}
