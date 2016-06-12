package pl.salonea.ejb.interfaces;

import pl.salonea.entities.Transaction;
import pl.salonea.entities.idclass.TransactionId;

/**
 * Created by michzio on 08/06/2016.
 */
public interface TransactionEmployeeRelationshipManagerInterface {

    void addEmployeeToTransaction(Long employeeId, TransactionId transactionId);
    void removeEmployeeFromTransaction(Long employeeId, TransactionId transactionId);
    Integer removeAllEmployeesFromTransaction(TransactionId transactionId);
    Integer removeEmployeesFromTransactionsByClient(Long clientId);

    @javax.ejb.Remote
    interface Remote extends TransactionEmployeeRelationshipManagerInterface { }

    @javax.ejb.Local
    interface Local extends TransactionEmployeeRelationshipManagerInterface { }
}
