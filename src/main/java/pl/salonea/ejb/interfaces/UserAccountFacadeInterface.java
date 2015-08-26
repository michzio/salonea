package pl.salonea.ejb.interfaces;

import pl.salonea.entities.UserAccount;

import java.util.Date;
import java.util.List;

/**
 * Created by michzio on 16/07/2015.
 */
public interface UserAccountFacadeInterface extends AbstractFacadeInterface<UserAccount> {

    // concrete interface
    List<UserAccount> findAllNotActivated();
    List<UserAccount> findAllNotActivated(Integer start, Integer limit);
    List<UserAccount> findAllActivated();
    List<UserAccount> findAllActivated(Integer start, Integer limit);
    List<UserAccount> findByEmail(String email);
    List<UserAccount> findByEmail(String email, Integer start, Integer limit);
    List<UserAccount> findByLogin(String login);
    List<UserAccount> findByLogin(String login, Integer start, Integer limit);
    List<UserAccount> findCreatedBetween(Date startDate, Date endDate);
    List<UserAccount> findCreatedBetween(Date startDate, Date endDate, Integer start, Integer limit);
    List<UserAccount> findLastLoggedBetween(Date startDate, Date endDate);
    List<UserAccount> findLastLoggedBetween(Date startDate, Date endDate, Integer start, Integer limit);
    List<UserAccount> findLastFailedLoginBetween(Date startDate, Date endDate);
    List<UserAccount> findLastFailedLoginBetween(Date startDate, Date endDate, Integer start, Integer limit);
    Integer deleteOldNotActivated(Date oldestDate);
    Integer updateActivateAll();

    @javax.ejb.Remote
    interface Remote extends UserAccountFacadeInterface { }

    @javax.ejb.Local
    interface Local extends UserAccountFacadeInterface { }
}
