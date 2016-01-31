package pl.salonea.ejb.stateless;


import pl.salonea.ejb.interfaces.ClientFacadeInterface;
import pl.salonea.embeddables.Address;
import pl.salonea.embeddables.Address_;
import pl.salonea.entities.*;
import pl.salonea.enums.ClientType;
import pl.salonea.enums.Gender;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.*;

/**
 * Created by michzio on 11/07/2015.
 */
@Stateless
@LocalBean
public class ClientFacade extends AbstractFacade<Client> implements ClientFacadeInterface.Remote, ClientFacadeInterface.Local {

    @Inject
    private EntityManager em;

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    public ClientFacade() {
        super(Client.class);
    }

    @Override
    public Client update(Client client, Boolean retainTransientFields) {

        if(retainTransientFields) {
            // keep current collection attributes of resource (and other marked @XmlTransient)
            Client currentClient = findByIdEagerly(client.getClientId());
            if(currentClient != null) {
                client.setNaturalPerson(currentClient.getNaturalPerson());
                client.setFirm(currentClient.getFirm());
                client.setCreditCards(currentClient.getCreditCards());
                client.setEmployeeRatings(currentClient.getEmployeeRatings());
                client.setProviderRatings(currentClient.getProviderRatings());
            }
        }
        return update(client);
    }

    @Override
    public List<Client> findAllEagerly() {
        return findAllEagerly(null, null);
    }

    @Override
    public List<Client> findAllEagerly(Integer start, Integer limit) {

        TypedQuery<Client> query = getEntityManager().createNamedQuery(Client.FIND_ALL_EAGERLY, Client.class);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public Client findByIdEagerly(Long clientId) {

        TypedQuery<Client> query = getEntityManager().createNamedQuery(Client.FIND_BY_ID_EAGERLY, Client.class);
        query.setParameter("clientId", clientId);
        try {
            return query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            return null;
        }
    }

    @Override
    public List<Client> findByFirstName(String firstName) {
        return findByFirstName(firstName, null, null);
    }

    @Override
    public List<Client> findByFirstName(String firstName, Integer start, Integer limit) {
        TypedQuery<Client> query = getEntityManager().createNamedQuery(Client.FIND_BY_FIRST_NAME, Client.class);
        query.setParameter("fname", "%" + firstName + "%");
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<Client> findByLastName(String lastName) {
        return findByLastName(lastName, null, null);
    }

    @Override
    public List<Client> findByLastName(String lastName, Integer start, Integer limit) {
        TypedQuery<Client> query = getEntityManager().createNamedQuery(Client.FIND_BY_LAST_NAME, Client.class);
        query.setParameter("lname", "%" + lastName + "%");
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<Client> findByPersonNames(String firstName, String lastName) {
        return findByPersonNames(firstName, lastName, null, null);
    }

    @Override
    public List<Client> findByPersonNames(String firstName, String lastName, Integer start, Integer limit) {

        TypedQuery<Client> query = getEntityManager().createNamedQuery(Client.FIND_BY_PERSON_NAMES, Client.class);
        query.setParameter("fname", "%" + firstName + "%");
        query.setParameter("lname", "%" + lastName + "%");
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<Client> findByFirmName(String firmName) {
        return findByFirmName(firmName, null, null);
    }

    @Override
    public List<Client> findByFirmName(String firmName, Integer start, Integer limit) {

        TypedQuery<Client> query = getEntityManager().createNamedQuery(Client.FIND_BY_FIRM_NAME, Client.class);
        query.setParameter("firm_name", "%" + firmName + "%");
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<Client> findByName(String name) {
        return findByName(name, null, null);
    }

    @Override
    public List<Client> findByName(String name, Integer start, Integer limit) {

        TypedQuery<Client> query = getEntityManager().createNamedQuery(Client.FIND_BY_NAME, Client.class);
        query.setParameter("name", "%" + name + "%");
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<Client> findByDescription(String description) {
        return findByDescription(description, null, null);
    }

    @Override
    public List<Client> findByDescription(String description, Integer start, Integer limit) {

        TypedQuery<Client> query = getEntityManager().createNamedQuery(Client.FIND_BY_DESCRIPTION, Client.class);
        query.setParameter("description", "%" + description + "%");
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<Client> findBornAfter(Date date) {
        return findBornAfter(date, null, null);
    }

    @Override
    public List<Client> findBornAfter(Date date, Integer start, Integer limit) {

        TypedQuery<Client> query = getEntityManager().createNamedQuery(Client.FIND_BORN_AFTER, Client.class);
        query.setParameter("date", date);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<Client> findBornBefore(Date date) {
        return findBornBefore(date, null, null);
    }

    @Override
    public List<Client> findBornBefore(Date date, Integer start, Integer limit) {

        TypedQuery<Client> query = getEntityManager().createNamedQuery(Client.FIND_BORN_BEFORE, Client.class);
        query.setParameter("date", date);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<Client> findBornBetween(Date startDate, Date endDate) {
        return findBornBetween(startDate, endDate, null, null);
    }

    @Override
    public List<Client> findBornBetween(Date startDate, Date endDate, Integer start, Integer limit) {

        TypedQuery<Client> query = getEntityManager().createNamedQuery(Client.FIND_BORN_BETWEEN, Client.class);
        query.setParameter("start_date", startDate);
        query.setParameter("end_date", endDate);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<Client> findOlderThan(Integer age) {
        return findOlderThan(age, null, null);
    }

    @Override
    public List<Client> findOlderThan(Integer age, Integer start, Integer limit) {

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -age);
        Date youngestBirthDate = calendar.getTime();

        TypedQuery<Client> query = getEntityManager().createNamedQuery(Client.FIND_BORN_BEFORE, Client.class);
        query.setParameter("date", youngestBirthDate);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return  query.getResultList();
    }

    @Override
    public List<Client> findYoungerThan(Integer age) {
        return findYoungerThan(age, null, null);
    }

    @Override
    public List<Client> findYoungerThan(Integer age, Integer start, Integer limit) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -age);
        Date oldestBirthDate = calendar.getTime();

        TypedQuery<Client> query = getEntityManager().createNamedQuery(Client.FIND_BORN_AFTER, Client.class);
        query.setParameter("date", oldestBirthDate);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<Client> findBetweenAge(Integer youngestAge, Integer oldestAge) {
        return findBetweenAge(youngestAge, oldestAge, null, null);
    }

    @Override
    public List<Client> findBetweenAge(Integer youngestAge, Integer oldestAge, Integer start, Integer limit) {

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -youngestAge);
        Date youngestBirthDate = calendar.getTime();
        calendar.setTime(new Date());
        calendar.add(Calendar.YEAR, -oldestAge);
        Date oldestBirthDate = calendar.getTime();

        TypedQuery<Client> query = getEntityManager().createNamedQuery(Client.FIND_BORN_BETWEEN, Client.class);
        query.setParameter("start_date", oldestBirthDate);
        query.setParameter("end_date", youngestBirthDate);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<Client> findByLocation(String city, String state, String country, String street, String zipCode) {
        return findByLocation(city, state, country, street, zipCode, null, null);
    }

    @Override
    public List<Client> findByLocation(String city, String state, String country, String street, String zipCode, Integer start, Integer limit) {

        return findByAddress(Client.FIND_BY_LOCATION, city, state, country, street, zipCode, start, limit);
    }

    @Override
    public List<Client> findByDelivery(String city, String state, String country, String street, String zipCode) {

        return findByDelivery(city, state, country, street, zipCode, null, null);
    }

    @Override
    public List<Client> findByDelivery(String city, String state, String country, String street, String zipCode, Integer start, Integer limit) {
        return findByAddress(Client.FIND_BY_DELIVERY, city, state, country, street, zipCode, start, limit);
    }

    private List<Client> findByAddress(String queryName, String city, String state, String country, String street, String zipCode, Integer start, Integer limit) {

        TypedQuery<Client> query = getEntityManager().createNamedQuery(queryName, Client.class);
        if(city == null) city = "";
        query.setParameter("city", "%" + city + "%");
        if(state == null) state = "";
        query.setParameter("state", "%" + state + "%");
        if(country == null) country = "";
        query.setParameter("country", "%" + country + "%");
        if(street == null) street = "";
        query.setParameter("street", "%" + street + "%");
        if(zipCode == null) zipCode = "";
        query.setParameter("zip_code", "%" + zipCode + "%");
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<Client> findByGender(Gender gender) {
        return findByGender(gender, null, null);
    }

    @Override
    public List<Client> findByGender(Gender gender, Integer start, Integer limit) {

        TypedQuery<Client> query = getEntityManager().createNamedQuery(Client.FIND_BY_GENDER, Client.class);
        query.setParameter("gender", gender);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<Client> findRatingProvider(Provider provider) {
        return findRatingProvider(provider, null, null);
    }

    @Override
    public List<Client> findRatingProvider(Provider provider, Integer start, Integer limit) {

        TypedQuery<Client> query = getEntityManager().createNamedQuery(Client.FIND_RATING_PROVIDER, Client.class);
        query.setParameter("provider", provider);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<Client> findRatingProviderEagerly(Provider provider) {
        return findRatingProviderEagerly(provider, null, null);
    }

    @Override
    public List<Client> findRatingProviderEagerly(Provider provider, Integer start, Integer limit) {

        TypedQuery<Client> query = getEntityManager().createNamedQuery(Client.FIND_RATING_PROVIDER_EAGERLY, Client.class);
        query.setParameter("provider", provider);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<Client> findRatingEmployee(Employee employee) {
        return findRatingEmployee(employee, null, null);
    }

    @Override
    public List<Client> findRatingEmployee(Employee employee, Integer start, Integer limit) {

        TypedQuery<Client> query = getEntityManager().createNamedQuery(Client.FIND_RATING_EMPLOYEE, Client.class);
        query.setParameter("employee", employee);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<Client> findRatingEmployeeEagerly(Employee employee) {
        return findRatingEmployeeEagerly(employee, null, null);
    }

    @Override
    public List<Client> findRatingEmployeeEagerly(Employee employee, Integer start, Integer limit) {

        TypedQuery<Client> query = getEntityManager().createNamedQuery(Client.FIND_RATING_EMPLOYEE_EAGERLY, Client.class);
        query.setParameter("employee", employee);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<Client> findByType(ClientType clientType) {
        return findByType(clientType, null, null);
    }

    @Override
    public List<Client> findByType(ClientType clientType, Integer start, Integer limit) {

        TypedQuery<Client> query;

        if (clientType == ClientType.FIRM) {
            query = getEntityManager().createNamedQuery(Client.FIND_ONLY_FIRMS, Client.class);
        } else if (clientType == ClientType.NATURAL_PERSON) {
            query = getEntityManager().createNamedQuery(Client.FIND_ONLY_NATURAL_PERSONS, Client.class);
        } else if (clientType == ClientType.NOT_ASSIGNED) {
            query = getEntityManager().createNamedQuery(Client.FIND_NOT_ASSIGNED, Client.class);
        } else { // clientType == null -> return all clients
            return findAll(start, limit);
        }

        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }

        return query.getResultList();
    }

    @Override
    public Long countByRatedProvider(Provider provider) {

        TypedQuery<Long> query = getEntityManager().createNamedQuery(Client.COUNT_BY_RATED_PROVIDER, Long.class);
        query.setParameter("provider", provider);
        return query.getSingleResult();
    }

    @Override
    public List<Client> findByMultipleCriteria(String firstName, String lastName, String firmName, String name, String description, Set<ClientType> clientTypes, Date oldestBirthDate, Date youngestBirthDate, Integer youngestAge, Integer oldestAge, Address location, Address delivery, Gender gender, List<Provider> ratedProviders, List<Employee> ratedEmployees) {
        return findByMultipleCriteria(firstName, lastName, firmName, name, description, clientTypes, oldestBirthDate, youngestBirthDate, youngestAge, oldestAge, location, delivery, gender, ratedProviders, ratedEmployees, null, null);
    }

    @Override
    public List<Client> findByMultipleCriteria(String firstName, String lastName, String firmName, String name, String description, Set<ClientType> clientTypes, Date oldestBirthDate, Date youngestBirthDate, Integer youngestAge, Integer oldestAge, Address location, Address delivery, Gender gender, List<Provider> ratedProviders, List<Employee> ratedEmployees,  Integer start, Integer limit) {
        return findByMultipleCriteria(firstName, lastName, firmName, name, description, clientTypes, oldestBirthDate, youngestBirthDate, youngestAge, oldestAge, location, delivery, gender, ratedProviders, ratedEmployees, false, start, limit);
    }

    @Override
    public List<Client> findByMultipleCriteriaEagerly(String firstName, String lastName, String firmName, String name, String description, Set<ClientType> clientTypes,  Date oldestBirthDate, Date youngestBirthDate, Integer youngestAge, Integer oldestAge,  Address location, Address delivery, Gender gender, List<Provider> ratedProviders, List<Employee> ratedEmployees) {
        return findByMultipleCriteriaEagerly(firstName, lastName, firmName, name, description, clientTypes, oldestBirthDate,youngestBirthDate, youngestAge, oldestAge, location, delivery,gender, ratedProviders, ratedEmployees, null, null);
    }

    @Override
    public List<Client> findByMultipleCriteriaEagerly(String firstName, String lastName, String firmName, String name, String description, Set<ClientType> clientTypes, Date oldestBirthDate, Date youngestBirthDate, Integer youngestAge, Integer oldestAge,  Address location, Address delivery, Gender gender, List<Provider> ratedProviders, List<Employee> ratedEmployees, Integer start, Integer limit) {
        return findByMultipleCriteria(firstName, lastName, firmName, name, description, clientTypes, oldestBirthDate, youngestBirthDate, youngestAge, oldestAge, location, delivery, gender, ratedProviders, ratedEmployees, true, start, limit);
    }

    private List<Client> findByMultipleCriteria(String firstName, String lastName, String firmName, String name, String description, Set<ClientType> clientTypes, Date oldestBirthDate, Date youngestBirthDate, Integer youngestAge, Integer oldestAge, Address location, Address delivery, Gender gender, List<Provider> ratedProviders, List<Employee> ratedEmployees, Boolean eagerly, Integer start, Integer limit) {

        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Client> criteriaQuery = criteriaBuilder.createQuery(Client.class);
        // FROM
        Root<Client> client = criteriaQuery.from(Client.class);
        // SELECT
        criteriaQuery.select(client);

        // INNER JOIN-s
        Join<Client, NaturalPerson> naturalPerson = null;
        Join<Client, Firm> firm = null;
        Join<Client, ProviderRating> providerRating = null;
        Join<ProviderRating, Provider> provider = null;
        Join<Client, EmployeeRating> employeeRating = null;
        Join<EmployeeRating, Employee> employee = null;

        // WHERE PREDICATES
        List<Predicate> predicates = new ArrayList<>();

        if(location != null) {

            if(naturalPerson == null || naturalPerson.getJoinType() == JoinType.INNER) naturalPerson = client.join(Client_.naturalPerson, JoinType.LEFT);
            if(firm == null || firm.getJoinType() == JoinType.INNER) firm = client.join(Client_.firm, JoinType.LEFT);

            Path<Address> personAddress = naturalPerson.get(NaturalPerson_.homeAddress);
            Path<Address> firmAddress = firm.get(Firm_.address);

            if(location.getCity() != null)
                predicates.add( criteriaBuilder.or( criteriaBuilder.like(personAddress.get(Address_.city), "%" + location.getCity() + "%"),
                        criteriaBuilder.like(firmAddress.get(Address_.city), "%" + location.getCity() + "%") ) );

            if(location.getState() != null)
                predicates.add( criteriaBuilder.or( criteriaBuilder.like(personAddress.get(Address_.state), "%" + location.getState() + "%"),
                        criteriaBuilder.like(firmAddress.get(Address_.state), "%" + location.getState() + "%") ) );

            if(location.getCountry() != null)
                predicates.add( criteriaBuilder.or( criteriaBuilder.like(personAddress.get(Address_.country), "%" + location.getCountry() + "%"),
                        criteriaBuilder.like(firmAddress.get(Address_.country), "%" + location.getCountry() + "%") ) );

            if(location.getStreet() != null)
                predicates.add( criteriaBuilder.or( criteriaBuilder.like(personAddress.get(Address_.street), "%" + location.getStreet() + "%"),
                        criteriaBuilder.like(firmAddress.get(Address_.street), "%" + location.getStreet() + "%") ) );

            if(location.getZipCode() != null)
                predicates.add( criteriaBuilder.or( criteriaBuilder.like(personAddress.get(Address_.zipCode), "%" + location.getZipCode() + "%"),
                        criteriaBuilder.like(firmAddress.get(Address_.zipCode), "%" + location.getZipCode() + "%") ) );

            if(location.getFlatNumber() != null)
                predicates.add( criteriaBuilder.or( criteriaBuilder.like(personAddress.get(Address_.flatNumber), "%" + location.getFlatNumber() + "%"),
                        criteriaBuilder.like(firmAddress.get(Address_.flatNumber), "%" + location.getFlatNumber() + "%") ) );

            if(location.getHouseNumber() != null)
                predicates.add( criteriaBuilder.or( criteriaBuilder.like(personAddress.get(Address_.houseNumber), "%" + location.getHouseNumber() + "%"),
                        criteriaBuilder.like(firmAddress.get(Address_.houseNumber), "%" + location.getHouseNumber() + "%") ) );
        }

        if(delivery != null) {

            if(naturalPerson == null || naturalPerson.getJoinType() == JoinType.INNER) naturalPerson = client.join(Client_.naturalPerson, JoinType.LEFT);
            if(firm == null || firm.getJoinType() == JoinType.INNER) firm = client.join(Client_.firm, JoinType.LEFT);

            Path<Address> personDeliveryAddress = naturalPerson.get(NaturalPerson_.deliveryAddress);
            Path<Address> firmAddress = firm.get(Firm_.address);

            if(delivery.getCity() != null)
                predicates.add( criteriaBuilder.or( criteriaBuilder.like(personDeliveryAddress.get(Address_.city), "%" + delivery.getCity() + "%"),
                                                    criteriaBuilder.like(firmAddress.get(Address_.city), "%" + delivery.getCity() + "%") ) );

            if(delivery.getState() != null)
                predicates.add( criteriaBuilder.or( criteriaBuilder.like(personDeliveryAddress.get(Address_.state), "%" + delivery.getState() + "%"),
                                                    criteriaBuilder.like(firmAddress.get(Address_.state), "%" + delivery.getState() + "%") ) );

            if(delivery.getCountry() != null)
                predicates.add( criteriaBuilder.or( criteriaBuilder.like(personDeliveryAddress.get(Address_.country), "%" + delivery.getCountry() + "%"),
                                                    criteriaBuilder.like(firmAddress.get(Address_.country), "%" + delivery.getCountry() + "%") ) );

            if(delivery.getStreet() != null)
                predicates.add( criteriaBuilder.or( criteriaBuilder.like(personDeliveryAddress.get(Address_.street), "%" + delivery.getStreet() + "%"),
                                                    criteriaBuilder.like(firmAddress.get(Address_.street), "%" + delivery.getStreet() + "%") ) );

            if(delivery.getZipCode() != null)
                predicates.add( criteriaBuilder.or( criteriaBuilder.like(personDeliveryAddress.get(Address_.zipCode), "%" + delivery.getZipCode() + "%"),
                                                    criteriaBuilder.like(firmAddress.get(Address_.zipCode), "%" + delivery.getZipCode() + "%") ) );

            if(delivery.getFlatNumber() != null)
                predicates.add( criteriaBuilder.or( criteriaBuilder.like(personDeliveryAddress.get(Address_.flatNumber), "%" + delivery.getFlatNumber() + "%"),
                                                    criteriaBuilder.like(firmAddress.get(Address_.flatNumber), "%" + delivery.getFlatNumber() + "%") ) );

            if(delivery.getHouseNumber() != null)
                predicates.add( criteriaBuilder.or( criteriaBuilder.like(personDeliveryAddress.get(Address_.houseNumber), "%" + delivery.getHouseNumber() + "%"),
                                                    criteriaBuilder.like(firmAddress.get(Address_.houseNumber), "%" + delivery.getHouseNumber() + "%") ) );
        }

        if(name != null) {

            if(naturalPerson == null || naturalPerson.getJoinType() == JoinType.INNER) naturalPerson = client.join(Client_.naturalPerson, JoinType.LEFT);
            if(firm == null || firm.getJoinType() == JoinType.INNER) firm = client.join(Client_.firm, JoinType.LEFT);

            List<Predicate> orPredicates = new ArrayList<>();
            orPredicates.add( criteriaBuilder.like(naturalPerson.get(NaturalPerson_.firstName), "%" + name + "%") );
            orPredicates.add( criteriaBuilder.like(naturalPerson.get(NaturalPerson_.lastName), "%" + name + "%") );
            orPredicates.add( criteriaBuilder.like(firm.get(Firm_.name), "%" + name + "%") );

            predicates.add( criteriaBuilder.or(orPredicates.toArray(new Predicate[]{})) );
        }

        if(clientTypes != null && clientTypes.size() > 0) {

            if(naturalPerson == null || naturalPerson.getJoinType() == JoinType.INNER) naturalPerson = client.join(Client_.naturalPerson, JoinType.LEFT);
            if(firm == null || firm.getJoinType() == JoinType.INNER) firm = client.join(Client_.firm, JoinType.LEFT);

            List<Predicate> orPredicates = new ArrayList<>();
            for(ClientType clientType : clientTypes) {

                if(clientType == ClientType.NATURAL_PERSON) {

                    orPredicates.add( criteriaBuilder.and( criteriaBuilder.isNull(firm),
                                    criteriaBuilder.isNotNull(naturalPerson)
                            )
                    );

                } else if(clientType == ClientType.FIRM) {

                    orPredicates.add( criteriaBuilder.and(  criteriaBuilder.isNotNull(firm),
                                    criteriaBuilder.isNull(naturalPerson)
                            )
                    );

                } else if(clientType == ClientType.NOT_ASSIGNED) {

                    orPredicates.add( criteriaBuilder.and(  criteriaBuilder.isNull(firm),
                                    criteriaBuilder.isNull(naturalPerson)
                            )
                    );

                }
            }

            predicates.add( criteriaBuilder.or(orPredicates.toArray(new Predicate[]{})) );
        }

        if(firstName != null) {

            if(naturalPerson == null) naturalPerson = client.join(Client_.naturalPerson);

            predicates.add( criteriaBuilder.like(naturalPerson.get(NaturalPerson_.firstName), "%" + firstName + "%") );
        }

        if(lastName != null) {

            if(naturalPerson == null) naturalPerson = client.join(Client_.naturalPerson);

            predicates.add( criteriaBuilder.like(naturalPerson.get(NaturalPerson_.lastName), "%" + lastName + "%") );
        }

        if(firmName != null) {

            if(firm == null) firm = client.join(Client_.firm);

            predicates.add( criteriaBuilder.like(firm.get(Firm_.name), "%" + firmName + "%") );
        }

        if(description != null) {

            predicates.add( criteriaBuilder.like(client.get(Client_.description), "%" + description + "%") );
        }

        if(oldestBirthDate != null) {

            if(naturalPerson == null) naturalPerson = client.join(Client_.naturalPerson);

            predicates.add( criteriaBuilder.greaterThanOrEqualTo(naturalPerson.get(NaturalPerson_.birthDate), oldestBirthDate) );
        }

        if(youngestBirthDate != null) {

            if(naturalPerson == null) naturalPerson = client.join(Client_.naturalPerson);

            predicates.add( criteriaBuilder.lessThanOrEqualTo(naturalPerson.get(NaturalPerson_.birthDate), youngestBirthDate) );
        }

        if(youngestAge != null) {

            if(naturalPerson == null) naturalPerson = client.join(Client_.naturalPerson);

            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.YEAR, -youngestAge);
            Date birthDate = calendar.getTime();

            predicates.add( criteriaBuilder.lessThanOrEqualTo(naturalPerson.get(NaturalPerson_.birthDate), birthDate) );
        }

        if(oldestAge != null) {

            if(naturalPerson == null) naturalPerson = client.join(Client_.naturalPerson);

            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.YEAR, -oldestAge);
            Date birthDate = calendar.getTime();

            predicates.add( criteriaBuilder.greaterThanOrEqualTo(naturalPerson.get(NaturalPerson_.birthDate), birthDate) );
        }

        if(gender != null) {

            if(naturalPerson == null) naturalPerson = client.join(Client_.naturalPerson);

            predicates.add( criteriaBuilder.equal(naturalPerson.get(NaturalPerson_.gender), gender) );
        }

        if(ratedProviders != null && ratedProviders.size() > 0) {

            if(providerRating == null) providerRating = client.join(Client_.providerRatings);
            if(provider == null) provider = providerRating.join(ProviderRating_.provider);

            predicates.add( provider.in(ratedProviders) );

            if(eagerly) {
                // then fetch associated collection of entities
                client.fetch("providerRatings", JoinType.INNER);
            }
        } else if(eagerly) {
            // then left fetch associated collection of entities
            client.fetch("providerRatings", JoinType.LEFT);
        }

        if(ratedEmployees != null && ratedEmployees.size() > 0) {

            if(employeeRating == null) employeeRating = client.join(Client_.employeeRatings);
            if(employee == null) employee = employeeRating.join(EmployeeRating_.employee);

            predicates.add( employee.in(ratedEmployees) );

            if(eagerly) {
                // then fetch associated collection of entities
                client.fetch("employeeRatings", JoinType.INNER);
            }
        } else if(eagerly) {
            // then left fetch associated collection of entities
            client.fetch("employeeRatings", JoinType.LEFT);
        }

        if(eagerly) {
            // then left fetch associated collection of entities
            client.fetch("creditCards", JoinType.LEFT);
        }

        // WHERE predicate1 AND predicate2 AND ... AND predicateN
        criteriaQuery.where(predicates.toArray(new Predicate[] {}));

        TypedQuery<Client> query = getEntityManager().createQuery(criteriaQuery);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }

        return query.getResultList();
    }

}