package pl.salonea.ejb.stateless;

import pl.salonea.ejb.interfaces.TransactionEmployeeRelationshipManagerInterface;
import pl.salonea.entities.Employee;
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
public class TransactionEmployeeRelationshipManager
         implements TransactionEmployeeRelationshipManagerInterface.Local, TransactionEmployeeRelationshipManagerInterface.Remote {

    @Inject
    private TransactionFacade transactionFacade;
    @Inject
    private EmployeeFacade employeeFacade;

    @Inject
    private EntityManager em;

    public EntityManager getEntityManager() {
        return em;
    }

    @Override
    public void addEmployeeToTransaction(Long employeeId, TransactionId transactionId) throws NotFoundException {

        Employee employee = employeeFacade.find(employeeId);
        if(employee == null)
            throw new NotFoundException("Employee entity could not be found for id " + employeeId);
        Transaction transaction = transactionFacade.find(transactionId);
        if(transaction == null)
            throw new NotFoundException("Transaction entity could not be found for id (" + transactionId.getClient() + ", " + transactionId.getTransactionNumber() + ")");

        transaction.getEmployees().add(employee);
    }

    @Override
    public void removeEmployeeFromTransaction(Long employeeId, TransactionId transactionId) throws NotFoundException {

        Employee employee = employeeFacade.find(employeeId);
        if(employee == null)
            throw new NotFoundException("Employee entity could not be found for id " + employeeId);
        Transaction transaction = transactionFacade.find(transactionId);
        if(transaction == null)
            throw new NotFoundException("Transaction entity could not be found for id (" + transactionId.getClient() + ", " + transactionId.getTransactionNumber() + ")");

        transaction.getEmployees().remove(employee);
    }

    @Override
    public Integer removeAllEmployeesFromTransaction(TransactionId transactionId) {

        Query query = getEntityManager().createNamedQuery(Transaction.DELETE_EMPLOYEES_ASSOCIATIONS_BY_TRANSACTION);
        query.setParameter("clientId", transactionId.getClient());
        query.setParameter("transaction_number", transactionId.getTransactionNumber());
        return query.executeUpdate();
    }

    @Override
    public Integer removeEmployeesFromTransactionsByClient(Long clientId) {

        Query query = getEntityManager().createNamedQuery(Transaction.DELETE_EMPLOYEES_ASSOCIATIONS_BY_CLIENT);
        query.setParameter("clientId", clientId);
        return query.executeUpdate();
    }
}
