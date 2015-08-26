package pl.salonea.ejb.interfaces;

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

    @javax.ejb.Remote
    interface Remote extends CreditCardFacadeInterface { }

    @javax.ejb.Local
    interface Local extends CreditCardFacadeInterface { }
}
