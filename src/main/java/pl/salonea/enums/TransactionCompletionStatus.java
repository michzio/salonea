package pl.salonea.enums;

/**
 * Created by michzio on 25/08/2015.
 */
public enum TransactionCompletionStatus {
    COMPLETED, // transaction completed properly
    CANCELED,  // transaction canceled on client request
    REJECTED,   // transaction rejected by provider
    UNCOMPLETED, // default transaction status if nothing happen with transaction
}
