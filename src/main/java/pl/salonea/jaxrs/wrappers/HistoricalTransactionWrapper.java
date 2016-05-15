package pl.salonea.jaxrs.wrappers;

import pl.salonea.entities.Employee;
import pl.salonea.entities.HistoricalTransaction;

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
@XmlRootElement(name = "historical-transaction")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class HistoricalTransactionWrapper {

    private HistoricalTransaction historicalTransaction;
    private Set<Employee> employees;

    // default no-args constructor
    public HistoricalTransactionWrapper() { }

    public HistoricalTransactionWrapper(HistoricalTransaction historicalTransaction) {
        this.historicalTransaction = historicalTransaction;
        this.employees = historicalTransaction.getEmployees();
    }

    public static List<HistoricalTransactionWrapper> wrap(List<HistoricalTransaction> historicalTransactions) {

        List<HistoricalTransactionWrapper> wrappedHistoricalTransactions = new ArrayList<>();

        for(HistoricalTransaction historicalTransaction : historicalTransactions)
            wrappedHistoricalTransactions.add(new HistoricalTransactionWrapper(historicalTransaction));

        return wrappedHistoricalTransactions;
    }

    @XmlElement(name = "entity", nillable = true)
    public HistoricalTransaction getHistoricalTransaction() {
        return historicalTransaction;
    }

    public void setHistoricalTransaction(HistoricalTransaction historicalTransaction) {
        this.historicalTransaction = historicalTransaction;
    }

    @XmlElement(name = "employees", nillable = true)
    public Set<Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(Set<Employee> employees) {
        this.employees = employees;
    }
}
