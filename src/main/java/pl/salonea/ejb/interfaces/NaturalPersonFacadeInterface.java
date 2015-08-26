package pl.salonea.ejb.interfaces;

import pl.salonea.ejb.stateless.NaturalPersonFacade;
import pl.salonea.entities.NaturalPerson;
import pl.salonea.enums.Gender;

import java.util.Date;
import java.util.List;


/**
 * Created by michzio on 14/07/2015.
 */
public interface NaturalPersonFacadeInterface extends AbstractFacadeInterface<NaturalPerson> {

    // concrete interface
    List<NaturalPerson> findByFirstName(String firstName);
    List<NaturalPerson> findByFirstName(String firstName, int start, int limit);
    List<NaturalPerson> findByLastName(String lastName);
    List<NaturalPerson> findByLastName(String lastName, int start, int limit);
    List<NaturalPerson> findByNames(String firstName, String lastName);
    List<NaturalPerson> findByNames(String firstName, String lastName, int start, int limit);
    List<NaturalPerson> findBornAfter(Date date);
    List<NaturalPerson> findBornAfter(Date date, int start, int limit);
    List<NaturalPerson> findBornBefore(Date date);
    List<NaturalPerson> findBornBefore(Date date, int start, int limit);
    List<NaturalPerson> findBornBetween(Date startDate, Date endDate);
    List<NaturalPerson> findBornBetween(Date startDate, Date endDate, int start, int limit);
    List<NaturalPerson> findOlderThan(Integer age);
    List<NaturalPerson> findOlderThan(Integer age, int start, int limit);
    List<NaturalPerson> findYoungerThan(Integer age);
    List<NaturalPerson> findYoungerThan(Integer age, int start, int limit);
    List<NaturalPerson> findBetweenAge(Integer youngestAge, Integer oldestAge);
    List<NaturalPerson> findBetweenAge(Integer youngestAge, Integer oldestAge, Integer start, Integer limit);
    List<NaturalPerson> findByLocation(String city, String state, String country, String street, String zipCode);
    List<NaturalPerson> findByLocation(String city, String state, String country, String street, String zipCode, Integer start, Integer limit);
    List<NaturalPerson> findByDelivery(String city, String state, String country, String street, String zipCode);
    List<NaturalPerson> findByDelivery(String city, String state, String country, String street, String zipCode, Integer start, Integer limit);
    List<NaturalPerson> findByGender(Gender gender);
    List<NaturalPerson> findByGender(Gender gender, Integer start, Integer limit);

    @javax.ejb.Remote
    interface Remote extends NaturalPersonFacadeInterface { }

    @javax.ejb.Local
    interface Local extends NaturalPersonFacadeInterface { }


}
