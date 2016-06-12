package pl.salonea.ejb.interfaces;

import pl.salonea.entities.Transaction;
import pl.salonea.entities.idclass.TransactionId;

/**
 * Created by michzio on 08/06/2016.
 */
public interface HistoricalTransactionEmployeeRelationshipManagerInterface {

    void addEmployeeToHistoricalTransaction(Long employeeId, TransactionId transactionId);
    void removeEmployeeFromHistoricalTransaction(Long employeeId, TransactionId transactionId);
    Integer removeAllEmployeesFromHistoricalTransaction(TransactionId transactionId);
    Integer removeEmployeesFromHistoricalTransactionsByClient(Long clientId);

    @javax.ejb.Remote
    interface Remote extends HistoricalTransactionEmployeeRelationshipManagerInterface { }

    @javax.ejb.Local
    interface Local extends HistoricalTransactionEmployeeRelationshipManagerInterface { }
}
