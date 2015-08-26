package pl.salonea.ejb.interfaces;


import pl.salonea.ejb.stateless.AbstractFacade;
import pl.salonea.entities.Client;
import pl.salonea.entities.Provider;
import pl.salonea.enums.Gender;

import java.util.Date;
import java.util.List;

/**
 * Created by michzio on 14/07/2015.
 */
public interface ClientFacadeInterface extends AbstractFacadeInterface<Client> {

    // concrete interface
    List<Client> findByFirstName(String firstName);
    List<Client> findByFirstName(String firstName, int start, int limit);
    List<Client> findByLastName(String lastName);
    List<Client> findByLastName(String lastName, int start, int limit);
    List<Client> findByNames(String firstName, String lastName);
    List<Client> findByNames(String firstName, String lastName, int start, int limit);
    List<Client> findBornAfter(Date date);
    List<Client> findBornAfter(Date date, int start, int limit);
    List<Client> findBornBefore(Date date);
    List<Client> findBornBefore(Date date, int start, int limit);
    List<Client> findBornBetween(Date startDate, Date endDate);
    List<Client> findBornBetween(Date startDate, Date endDate, int start, int limit);
    List<Client> findOlderThan(Integer age);
    List<Client> findOlderThan(Integer age, int start, int limit);
    List<Client> findYoungerThan(Integer age);
    List<Client> findYoungerThan(Integer age, int start, int limit);
    List<Client> findBetweenAge(Integer youngestAge, Integer oldestAge);
    List<Client> findBetweenAge(Integer youngestAge, Integer oldestAge, Integer start, Integer limit);
    List<Client> findByLocation(String city, String state, String country, String street, String zipCode);
    List<Client> findByLocation(String city, String state, String country, String street, String zipCode, Integer start, Integer limit);
    List<Client> findByDelivery(String city, String state, String country, String street, String zipCode);
    List<Client> findByDelivery(String city, String state, String country, String street, String zipCode, Integer start, Integer limit);
    List<Client> findByGender(Gender gender);
    List<Client> findByGender(Gender gender, Integer start, Integer limit);
    List<Client> findRatingProvider(Provider provider);
    List<Client> findRatingProvider(Provider provider, Integer start, Integer limit);

    @javax.ejb.Remote
    interface Remote extends ClientFacadeInterface {
    }

    @javax.ejb.Local
    interface Local extends ClientFacadeInterface {
    }

}
