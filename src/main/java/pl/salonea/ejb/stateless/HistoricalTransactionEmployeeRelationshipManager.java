package pl.salonea.ejb.stateless;

import pl.salonea.ejb.interfaces.HistoricalTransactionEmployeeRelationshipManagerInterface;
import pl.salonea.entities.Employee;
import pl.salonea.entities.HistoricalTransaction;
import pl.salonea.entities.Transaction;
import pl.salonea.entities.idclass.TransactionId;
import pl.salonea.jaxrs.exceptions.NotFoundException;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.ws.rs.QueryParam;

/**
 * Created by michzio on 08/06/2016.
 */
@Stateless
@LocalBean
public class HistoricalTransactionEmployeeRelationshipManager
         implements HistoricalTransactionEmployeeRelationshipManagerInterface.Local, HistoricalTransactionEmployeeRelationshipManagerInterface.Remote {

    @Inject
    private HistoricalTransactionFacade historicalTransactionFacade;
    @Inject
    private EmployeeFacade employeeFacade;

    @Inject
    private EntityManager em;

    public EntityManager getEntityManager() {
        return em;
    }

    @Override
    public void addEmployeeToHistoricalTransaction(Long employeeId, TransactionId transactionId) throws NotFoundException{

        Employee employee = employeeFacade.find(employeeId);
        if(employee == null)
            throw new NotFoundException("Employee entity could not be found for id " + employeeId);
        HistoricalTransaction historicalTransaction = historicalTransactionFacade.find(transactionId);
        if(historicalTransaction == null)
            throw new NotFoundException("Historical Transaction entity could not be found for id (" + transactionId.getClient() + ", " + transactionId.getTransactionNumber() + ")");

        historicalTransaction.getEmployees().add(employee);
    }

    @Override
    public void removeEmployeeFromHistoricalTransaction(Long employeeId, TransactionId transactionId) throws NotFoundException {

        Employee employee = employeeFacade.find(employeeId);
        if(employee == null)
            throw new NotFoundException("Employee entity could not be found for id " + employeeId);
        HistoricalTransaction historicalTransaction = historicalTransactionFacade.find(transactionId);
        if(historicalTransaction == null)
            throw new NotFoundException("Historical Transaction entity could not be found for id (" + transactionId.getClient() + ", " + transactionId.getTransactionNumber() + ")");

        historicalTransaction.getEmployees().remove(employee);
    }

    @Override
    public Integer removeAllEmployeesFromHistoricalTransaction(TransactionId transactionId) {

        Query query = getEntityManager().createNamedQuery(HistoricalTransaction.DELETE_EMPLOYEES_ASSOCIATIONS_BY_TRANSACTION);
        query.setParameter("clientId", transactionId.getClient());
        query.setParameter("transaction_number", transactionId.getTransactionNumber());
        return query.executeUpdate();
    }

    @Override
    public Integer removeEmployeesFromHistoricalTransactionsByClient(Long clientId) {

        Query query = getEntityManager().createNamedQuery(HistoricalTransaction.DELETE_EMPLOYEES_ASSOCIATIONS_BY_CLIENT);
        query.setParameter("clientId", clientId);
        return query.executeUpdate();
    }
}
