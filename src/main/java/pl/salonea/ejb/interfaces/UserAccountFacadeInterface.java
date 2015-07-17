package pl.salonea.ejb.interfaces;

import pl.salonea.entities.UserAccount;

import java.util.Date;
import java.util.List;

/**
 * Created by michzio on 16/07/2015.
 */
public interface UserAccountFacadeInterface extends AbstractFacadeInterface<UserAccount> {

    // concrete class
    List<UserAccount> findAllNotActivated();
    List<UserAccount> findAllNotActivated(Integer start, Integer offset);
    List<UserAccount> findAllActivated();
    List<UserAccount> findAllActivated(Integer start, Integer offset);
    List<UserAccount> findByEmail(String email);
    List<UserAccount> findByEmail(String email, Integer start, Integer offset);
    List<UserAccount> findByLogin(String login);
    List<UserAccount> findByLogin(String login, Integer start, Integer offset);
    List<UserAccount> findCreatedBetween(Date startDate, Date endDate);
    List<UserAccount> findCreatedBetween(Date startDate, Date endDate, Integer start, Integer offset);
    List<UserAccount> findLastLoggedBetween(Date startDate, Date endDate);
    List<UserAccount> findLastLoggedBetween(Date startDate, Date endDate, Integer start, Integer offset);
    List<UserAccount> findLastFailedLoginBetween(Date startDate, Date endDate);
    List<UserAccount> findLastFailedLoginBetween(Date startDate, Date endDate, Integer start, Integer offset);
    Integer deleteOldNotActivated(Date oldestDate);
    Integer updateActivateAll();

    @javax.ejb.Remote
    interface Remote extends UserAccountFacadeInterface { }

    @javax.ejb.Local
    interface Local extends UserAccountFacadeInterface { }
}
