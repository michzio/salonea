package pl.salonea.jaxrs.wrappers;

import pl.salonea.entities.EmployeeTerm;
import pl.salonea.entities.HistoricalTransaction;
import pl.salonea.entities.Term;
import pl.salonea.entities.Transaction;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by michzio on 18/03/2016.
 */
@XmlRootElement(name = "term")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class TermWrapper {

    private Term term;
    private Set<EmployeeTerm> employeeTerms;
    private Set<Transaction> transactions;
    private Set<HistoricalTransaction> historicalTransactions;

    // default no-args constructor
    public TermWrapper() { }

    public TermWrapper(Term term) {
        this.term = term;
        this.employeeTerms = term.getEmployeeTerms();
        this.transactions = term.getTransactions();
        this.historicalTransactions = term.getHistoricalTransactions();
    }

    public static List<TermWrapper> wrap(List<Term> terms) {

        List<TermWrapper> wrappedTerms = new ArrayList<>();

        for(Term term : terms)
            wrappedTerms.add(new TermWrapper(term));

        return wrappedTerms;
    }

    @XmlElement(name = "entity", nillable = true)
    public Term getTerm() {
        return term;
    }

    public void setTerm(Term term) {
        this.term = term;
    }

    @XmlElement(name = "employee-terms", nillable = true)
    public Set<EmployeeTerm> getEmployeeTerms() {
        return employeeTerms;
    }

    public void setEmployeeTerms(Set<EmployeeTerm> employeeTerms) {
        this.employeeTerms = employeeTerms;
    }

    @XmlElement(name = "transactions", nillable = true)
    public Set<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(Set<Transaction> transactions) {
        this.transactions = transactions;
    }

    @XmlElement(name = "historical-transactions", nillable = true)
    public Set<HistoricalTransaction> getHistoricalTransactions() {
        return historicalTransactions;
    }

    public void setHistoricalTransactions(Set<HistoricalTransaction> historicalTransactions) {
        this.historicalTransactions = historicalTransactions;
    }
}