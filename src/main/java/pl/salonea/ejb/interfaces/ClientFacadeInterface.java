package pl.salonea.ejb.interfaces;


import pl.salonea.ejb.stateless.AbstractFacade;
import pl.salonea.embeddables.Address;
import pl.salonea.entities.Client;
import pl.salonea.entities.Employee;
import pl.salonea.entities.Provider;
import pl.salonea.enums.ClientType;
import pl.salonea.enums.Gender;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by michzio on 14/07/2015.
 */
public interface ClientFacadeInterface extends AbstractFacadeInterface<Client> {

    // concrete interface
    Client update(Client client, Boolean retainTransientFields);
    List<Client> findAllEagerly();
    List<Client> findAllEagerly(Integer start, Integer limit);
    Client findByIdEagerly(Long clientId);
    List<Client> findByFirstName(String firstName);
    List<Client> findByFirstName(String firstName, Integer start, Integer limit);
    List<Client> findByLastName(String lastName);
    List<Client> findByLastName(String lastName, Integer start, Integer limit);
    List<Client> findByPersonNames(String firstName, String lastName);
    List<Client> findByPersonNames(String firstName, String lastName, Integer start, Integer limit);
    List<Client> findByFirmName(String firmName);
    List<Client> findByFirmName(String firmName, Integer start, Integer limit);
    List<Client> findByName(String name);
    List<Client> findByName(String name, Integer start, Integer limit);
    List<Client> findByDescription(String description);
    List<Client> findByDescription(String description, Integer start, Integer limit);
    List<Client> findBornAfter(Date date);
    List<Client> findBornAfter(Date date, Integer start, Integer limit);
    List<Client> findBornBefore(Date date);
    List<Client> findBornBefore(Date date, Integer start, Integer limit);
    List<Client> findBornBetween(Date startDate, Date endDate);
    List<Client> findBornBetween(Date startDate, Date endDate, Integer start, Integer limit);
    List<Client> findOlderThan(Integer age);
    List<Client> findOlderThan(Integer age, Integer start, Integer limit);
    List<Client> findYoungerThan(Integer age);
    List<Client> findYoungerThan(Integer age, Integer start, Integer limit);
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
    List<Client> findRatingEmployee(Employee employee);
    List<Client> findRatingEmployee(Employee employee, Integer start, Integer limit);
    List<Client> findByType(ClientType clientType);
    List<Client> findByType(ClientType clientType, Integer start, Integer limit);
    List<Client> findByMultipleCriteria(String firstName, String lastName, String firmName, String name, String description, Set<ClientType> clientTypes, Date oldestBirthDate, Date youngestBirthDate, Integer youngestAge, Integer oldestAge,  Address location, Address delivery, Gender gender, List<Provider> ratedProviders, List<Employee> ratedEmployees);
    List<Client> findByMultipleCriteria(String firstName, String lastName, String firmName, String name, String description, Set<ClientType> clientTypes, Date oldestBirthDate, Date youngestBirthDate, Integer youngestAge, Integer oldestAge, Address location, Address delivery, Gender gender, List<Provider> ratedProviders, List<Employee> ratedEmployees, Integer start, Integer limit);
    List<Client> findByMultipleCriteriaEagerly(String firstName, String lastName, String firmName, String name, String description, Set<ClientType> clientTypes, Date oldestBirthDate, Date youngestBirthDate, Integer youngestAge, Integer oldestAge, Address location, Address delivery, Gender gender, List<Provider> ratedProviders, List<Employee> ratedEmployees);
    List<Client> findByMultipleCriteriaEagerly(String firstName, String lastName, String firmName, String name, String description, Set<ClientType> clientTypes, Date oldestBirthDate, Date youngestBirthDate, Integer youngestAge, Integer oldestAge, Address location, Address delivery, Gender gender, List<Provider> ratedProviders, List<Employee> ratedEmployees, Integer start, Integer limit);

    @javax.ejb.Remote
    interface Remote extends ClientFacadeInterface {
    }

    @javax.ejb.Local
    interface Local extends ClientFacadeInterface {
    }

}
