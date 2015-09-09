package pl.salonea.ejb.interfaces;

import pl.salonea.ejb.stateless.NaturalPersonFacade;
import pl.salonea.embeddables.Address;
import pl.salonea.entities.NaturalPerson;
import pl.salonea.enums.Gender;
import pl.salonea.utils.Period;

import java.util.Date;
import java.util.List;


/**
 * Created by michzio on 14/07/2015.
 */
public interface NaturalPersonFacadeInterface extends AbstractFacadeInterface<NaturalPerson> {

    // concrete interface
    List<NaturalPerson> findByFirstName(String firstName);
    List<NaturalPerson> findByFirstName(String firstName, Integer start, Integer limit);
    List<NaturalPerson> findByLastName(String lastName);
    List<NaturalPerson> findByLastName(String lastName, Integer start, Integer limit);
    List<NaturalPerson> findByNames(String firstName, String lastName);
    List<NaturalPerson> findByNames(String firstName, String lastName, Integer start, Integer limit);
    List<NaturalPerson> findBornAfter(Date date);
    List<NaturalPerson> findBornAfter(Date date, Integer start, Integer limit);
    List<NaturalPerson> findBornBefore(Date date);
    List<NaturalPerson> findBornBefore(Date date, Integer start, Integer limit);
    List<NaturalPerson> findBornBetween(Date startDate, Date endDate);
    List<NaturalPerson> findBornBetween(Date startDate, Date endDate, Integer start, Integer limit);
    List<NaturalPerson> findOlderThan(Integer age);
    List<NaturalPerson> findOlderThan(Integer age, Integer start, Integer limit);
    List<NaturalPerson> findYoungerThan(Integer age);
    List<NaturalPerson> findYoungerThan(Integer age, Integer start, Integer limit);
    List<NaturalPerson> findBetweenAge(Integer youngestAge, Integer oldestAge);
    List<NaturalPerson> findBetweenAge(Integer youngestAge, Integer oldestAge, Integer start, Integer limit);
    List<NaturalPerson> findByLocation(String city, String state, String country, String street, String zipCode);
    List<NaturalPerson> findByLocation(String city, String state, String country, String street, String zipCode, Integer start, Integer limit);
    List<NaturalPerson> findByDelivery(String city, String state, String country, String street, String zipCode);
    List<NaturalPerson> findByDelivery(String city, String state, String country, String street, String zipCode, Integer start, Integer limit);
    List<NaturalPerson> findByGender(Gender gender);
    List<NaturalPerson> findByGender(Gender gender, Integer start, Integer limit);
    List<NaturalPerson> findByMultipleCriteria(String firstName, String lastName, Gender gender, Period bornBetween, Integer youngestAge, Integer oldestAge, Address location, Address delivery);
    List<NaturalPerson> findByMultipleCriteria(String firstName, String lastName, Gender gender, Period bornBetween, Integer youngestAge, Integer oldestAge, Address location, Address delivery, Integer start, Integer limit);

    @javax.ejb.Remote
    interface Remote extends NaturalPersonFacadeInterface { }

    @javax.ejb.Local
    interface Local extends NaturalPersonFacadeInterface { }


}
