package pl.salonea.ejb.interfaces;

import pl.salonea.entities.Client;
import pl.salonea.entities.CreditCard;
import pl.salonea.entities.idclass.CreditCardId;
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
    CreditCard update(CreditCardId creditCardId, CreditCard creditCard);
    List<CreditCard> findByClient(Client client);
    List<CreditCard> findByClient(Client client, Integer start, Integer limit);
    List<CreditCard> findByType(CreditCardType cardType);
    List<CreditCard> findByType(CreditCardType cardType, Integer start, Integer limit);
    List<CreditCard> findByClientAndType(Client client, CreditCardType cardType);
    List<CreditCard> findByClientAndType(Client client, CreditCardType cardType, Integer start, Integer limit);
    List<CreditCard> findExpired();
    List<CreditCard> findExpired(Integer start, Integer limit);
    List<CreditCard> findExpiredByClient(Client client);
    List<CreditCard> findExpiredByClient(Client client, Integer start, Integer limit);
    List<CreditCard> findNotExpired();
    List<CreditCard> findNotExpired(Integer start, Integer limit);
    List<CreditCard> findNotExpiredByClient(Client client);
    List<CreditCard> findNotExpiredByClient(Client client, Integer start, Integer limit);
    List<CreditCard> findExpirationDateAfter(Date date);
    List<CreditCard> findExpirationDateAfter(Date date, Integer start, Integer limit);
    List<CreditCard> findExpirationDateAfterByClient(Date date, Client client);
    List<CreditCard> findExpirationDateAfterByClient(Date date, Client client, Integer start, Integer limit);
    List<CreditCard> findExpirationDateBefore(Date date);
    List<CreditCard> findExpirationDateBefore(Date date, Integer start, Integer limit);
    List<CreditCard> findExpirationDateBeforeByClient(Date date, Client client);
    List<CreditCard> findExpirationDateBeforeByClient(Date date, Client client, Integer start, Integer limit);
    List<CreditCard> findExpirationDateBetween(Date startDate, Date endDate);
    List<CreditCard> findExpirationDateBetween(Date startDate, Date endDate, Integer start, Integer limit);
    List<CreditCard> findExpirationDateBetweenByClient(Date startDate, Date endDate, Client client);
    List<CreditCard> findExpirationDateBetweenByClient(Date startDate, Date endDate, Client client, Integer start, Integer limit);
    Integer deleteForClient(Client client);
    Integer deleteWithExpirationDateBefore(Date date);
    Integer deleteWithExpirationDateBeforeForClient(Date date, Client client);
    Integer deleteWithExpirationDateAfter(Date date);
    Integer deleteWithExpirationDateAfterForClient(Date date, Client client);
    Integer deleteWithExpirationDateBetween(Date startDate, Date endDate);
    Integer deleteWithExpirationDateBetweenForClient(Date startDate, Date endDate, Client client);
    Integer deleteExpired();
    Integer deleteExpiredForClient(Client client);
    Integer deleteWithType(CreditCardType cardType);
    Integer deleteWithTypeForClient(CreditCardType cardType, Client client);
    Integer deleteById(CreditCardId creditCardId);
    Integer countByClient(Client client);
    List<CreditCard> findByMultipleCriteria(List<Client> clients, List<CreditCardType> cardTypes, String cardNumber, String cardHolder, Boolean expired, Date theEarliestExpirationDate, Date theLatestExpirationDate);
    List<CreditCard> findByMultipleCriteria(List<Client> clients, List<CreditCardType> cardTypes, String cardNumber, String cardHolder, Boolean expired, Date theEarliestExpirationDate, Date theLatestExpirationDate, Integer start, Integer limit);

    @javax.ejb.Remote
    interface Remote extends CreditCardFacadeInterface { }

    @javax.ejb.Local
    interface Local extends CreditCardFacadeInterface { }
}
