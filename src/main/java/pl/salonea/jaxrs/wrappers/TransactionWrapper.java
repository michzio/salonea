package pl.salonea.jaxrs.wrappers;

import pl.salonea.entities.Employee;
import pl.salonea.entities.Transaction;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by michzio on 15/05/2016.
 */
@XmlRootElement(name = "transaction")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class TransactionWrapper {

    private Transaction transaction;
    private Set<Employee> employees;

    // default no-args constructor
    public TransactionWrapper() { }

    public TransactionWrapper(Transaction transaction) {
        this.transaction = transaction;
        this.employees = transaction.getEmployees();
    }

    public static List<TransactionWrapper> wrap(List<Transaction> transactions) {

        List<TransactionWrapper> wrappedTransactions = new ArrayList<>();

        for(Transaction transaction : transactions)
            wrappedTransactions.add(new TransactionWrapper(transaction));

        return wrappedTransactions;
    }

    @XmlElement(name = "entity", nillable = true)
    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    @XmlElement(name = "employees", nillable = true)
    public Set<Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(Set<Employee> employees) {
        this.employees = employees;
    }
}
