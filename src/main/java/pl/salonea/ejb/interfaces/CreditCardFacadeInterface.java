package pl.salonea.ejb.interfaces;

import pl.salonea.entities.Client;
import pl.salonea.entities.CreditCard;
import pl.salonea.enums.CreditCardType;

import javax.ejb.Local;
import javax.ejb.Remote;
import java.net.Inet4Address;
import java.util.Date;
import java.util.List;

/**
 * Created by michzio on 18/07/2015.
 */
public interface CreditCardFacadeInterface extends AbstractFacadeInterface<CreditCard> {

    // concrete interface
    CreditCard createForClient(Long clientId, CreditCard creditCard); 
    List<CreditCard> findByClient(Client client);
    List<CreditCard> findByClient(Client client, Integer start, Integer limit);
    List<CreditCard> findByType(CreditCardType cardType);
    List<CreditCard> findByType(CreditCardType cardType, Integer start, Integer limit);
    List<CreditCard> findExpired();
    List<CreditCard> findExpired(Integer start, Integer limit);
    List<CreditCard> findNotExpired();
    List<CreditCard> findNotExpired(Integer start, Integer limit);
    List<CreditCard> findExpirationDateAfter(Date date);
    List<CreditCard> findExpirationDateAfter(Date date, Integer start, Integer limit);
    List<CreditCard> findExpirationDateBefore(Date date);
    List<CreditCard> findExpirationDateBefore(Date date, Integer start, Integer limit);
    List<CreditCard> findExpirationDateBetween(Date startDate, Date endDate);
    List<CreditCard> findExpirationDateBetween(Date startDate, Date endDate, Integer start, Integer limit);
    Integer deleteWithExpirationDateBefore(Date date);
    Integer deleteWithExpirationDateAfter(Date date);
    Integer deleteWithExpirationDateBetween(Date startDate, Date endDate);
    Integer deleteExpired();
    Integer deleteWithType(CreditCardType cardType);
    List<CreditCard> findByMultipleCriteria(List<Client> clients, List<CreditCardType> cardTypes, String cardNumber, String cardHolder, Boolean expired, Date theEarliestExpirationDate, Date theLatestExpirationDate);
    List<CreditCard> findByMultipleCriteria(List<Client> clients, List<CreditCardType> cardTypes, String cardNumber, String cardHolder, Boolean expired, Date theEarliestExpirationDate, Date theLatestExpirationDate, Integer start, Integer limit);

    @javax.ejb.Remote
    interface Remote extends CreditCardFacadeInterface { }

    @javax.ejb.Local
    interface Local extends CreditCardFacadeInterface { }
}
