package pl.salonea.ejb.stateless;

import pl.salonea.ejb.interfaces.NaturalPersonFacadeInterface;
import pl.salonea.embeddables.Address;
import pl.salonea.embeddables.Address_;
import pl.salonea.entities.NaturalPerson;
import pl.salonea.entities.NaturalPerson_;
import pl.salonea.enums.Gender;
import pl.salonea.utils.Period;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by michzio on 14/07/2015.
 */
@Stateless
@LocalBean
public class NaturalPersonFacade extends AbstractFacade<NaturalPerson> implements NaturalPersonFacadeInterface.Local, NaturalPersonFacadeInterface.Remote {

    @Inject
    private EntityManager em;

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    public NaturalPersonFacade() {
        super(NaturalPerson.class);
    }

    @Override
    public List<NaturalPerson> findByFirstName(String firstName) {
        return findByFirstName(firstName, null, null);
    }

    @Override
    public List<NaturalPerson> findByFirstName(String firstName, Integer start, Integer limit) {

        TypedQuery<NaturalPerson> query = getEntityManager().createNamedQuery(NaturalPerson.FIND_BY_FIRST_NAME, NaturalPerson.class);
        query.setParameter("fname", "%" + firstName + "%");
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<NaturalPerson> findByLastName(String lastName) {
        return findByLastName(lastName, null, null);
    }

    @Override
    public List<NaturalPerson> findByLastName(String lastName, Integer start, Integer limit) {

        TypedQuery<NaturalPerson> query = getEntityManager().createNamedQuery(NaturalPerson.FIND_BY_LAST_NAME, NaturalPerson.class);
        query.setParameter("lname", "%" + lastName + "%");
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<NaturalPerson> findByNames(String firstName, String lastName) {
        return findByNames(firstName, lastName, null, null);
    }

    @Override
    public List<NaturalPerson> findByNames(String firstName, String lastName, Integer start, Integer limit) {

        TypedQuery<NaturalPerson> query = getEntityManager().createNamedQuery(NaturalPerson.FIND_BY_NAMES, NaturalPerson.class);
        query.setParameter("fname", "%" + firstName + "%");
        query.setParameter("lname", "%" + lastName + "%");
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<NaturalPerson> findBornAfter(Date date) {
        return findBornAfter(date, null, null);
    }

    @Override
    public List<NaturalPerson> findBornAfter(Date date, Integer start, Integer limit) {

        TypedQuery<NaturalPerson> query = getEntityManager().createNamedQuery(NaturalPerson.FIND_BORN_AFTER, NaturalPerson.class);
        query.setParameter("date", date);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<NaturalPerson> findBornBefore(Date date) {
        return findBornBefore(date, null, null);
    }

    @Override
    public List<NaturalPerson> findBornBefore(Date date, Integer start, Integer limit) {

        TypedQuery<NaturalPerson> query = getEntityManager().createNamedQuery(NaturalPerson.FIND_BORN_BEFORE, NaturalPerson.class);
        query.setParameter("date", date);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<NaturalPerson> findBornBetween(Date startDate, Date endDate) {
        return findBornBetween(startDate, endDate, null, null);
    }

    @Override
    public List<NaturalPerson> findBornBetween(Date startDate, Date endDate, Integer start, Integer limit) {

        TypedQuery<NaturalPerson> query = getEntityManager().createNamedQuery(NaturalPerson.FIND_BORN_BETWEEN, NaturalPerson.class);
        query.setParameter("start_date", startDate);
        query.setParameter("end_date", endDate);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<NaturalPerson> findOlderThan(Integer age) {
       return findOlderThan(age, null, null);
    }

    @Override
    public List<NaturalPerson> findOlderThan(Integer age, Integer start, Integer limit) {

        Date youngestBirthDate = convertAgeToBirthDate(age);

        TypedQuery<NaturalPerson> query = getEntityManager().createNamedQuery(NaturalPerson.FIND_BORN_BEFORE, NaturalPerson.class);
        query.setParameter("date", youngestBirthDate);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return  query.getResultList();
    }

    @Override
    public List<NaturalPerson> findYoungerThan(Integer age) {
        return findYoungerThan(age, null, null);
    }

    @Override
    public List<NaturalPerson> findYoungerThan(Integer age, Integer start, Integer limit) {

        Date oldestBirthDate = convertAgeToBirthDate(age);

        TypedQuery<NaturalPerson> query = getEntityManager().createNamedQuery(NaturalPerson.FIND_BORN_AFTER, NaturalPerson.class);
        query.setParameter("date", oldestBirthDate);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<NaturalPerson> findBetweenAge(Integer youngestAge, Integer oldestAge) {
        return findBetweenAge(youngestAge, oldestAge, null, null);
    }

    @Override
    public List<NaturalPerson> findBetweenAge(Integer youngestAge, Integer oldestAge, Integer start, Integer limit) {

        Date youngestBirthDate = convertAgeToBirthDate(youngestAge);
        Date oldestBirthDate = convertAgeToBirthDate(oldestAge);

        TypedQuery<NaturalPerson> query = getEntityManager().createNamedQuery(NaturalPerson.FIND_BORN_BETWEEN, NaturalPerson.class);
        query.setParameter("start_date", oldestBirthDate);
        query.setParameter("end_date", youngestBirthDate);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<NaturalPerson> findByLocation(String city, String state, String country, String street, String zipCode) {
        return findByLocation(city, state, country, street, zipCode, null, null);
    }

    @Override
    public List<NaturalPerson> findByLocation(String city, String state, String country, String street, String zipCode, Integer start, Integer limit) {

        return findByAddress(NaturalPerson.FIND_BY_LOCATION, city, state, country, street, zipCode, start, limit);
    }

    @Override
    public List<NaturalPerson> findByDelivery(String city, String state, String country, String street, String zipCode) {

        return findByDelivery(city, state, country, street, zipCode, null, null);
    }

    @Override
    public List<NaturalPerson> findByDelivery(String city, String state, String country, String street, String zipCode, Integer start, Integer limit) {
        return findByAddress(NaturalPerson.FIND_BY_DELIVERY, city, state, country, street, zipCode, start, limit);
    }

    private List<NaturalPerson> findByAddress(String queryName, String city, String state, String country, String street, String zipCode, Integer start, Integer limit) {

        TypedQuery<NaturalPerson> query = getEntityManager().createNamedQuery(queryName, NaturalPerson.class);
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
    public List<NaturalPerson> findByGender(Gender gender) {
        return findByGender(gender, null, null);
    }

    @Override
    public List<NaturalPerson> findByGender(Gender gender, Integer start, Integer limit) {

        TypedQuery<NaturalPerson> query = getEntityManager().createNamedQuery(NaturalPerson.FIND_BY_GENDER, NaturalPerson.class);
        query.setParameter("gender", gender);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<NaturalPerson> findByMultipleCriteria(String firstName, String lastName, Gender gender, Period bornBetween, Integer oldestAge, Integer youngestAge, Address location, Address delivery) {
        return findByMultipleCriteria(firstName, lastName, gender, bornBetween, oldestAge, youngestAge, location, delivery, null, null);
    }

    @Override
    public List<NaturalPerson> findByMultipleCriteria(String firstName, String lastName, Gender gender, Period bornBetween, Integer oldestAge, Integer youngestAge, Address location, Address delivery, Integer start, Integer limit) {

        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<NaturalPerson> criteriaQuery = criteriaBuilder.createQuery(NaturalPerson.class);
        // FROM
        Root<NaturalPerson> naturalPerson = criteriaQuery.from(NaturalPerson.class);
        // SELECT
        criteriaQuery.select(naturalPerson);

        // WHERE PREDICATES
        List<Predicate> predicates = new ArrayList<>();

        if( firstName != null ) {
            predicates.add( criteriaBuilder.like(naturalPerson.get(NaturalPerson_.firstName), "%" + firstName + "%") );
        }

        if( lastName != null ) {
            predicates.add( criteriaBuilder.like(naturalPerson.get(NaturalPerson_.lastName), "%" + lastName + "%") );
        }

        if(gender != null) {
            predicates.add( criteriaBuilder.equal(naturalPerson.get(NaturalPerson_.gender), gender) );
        }

        Date oldestBirthDate = null;
        Date youngestBirthDate = null;

        if(bornBetween != null) {
            oldestBirthDate = bornBetween.getStartTime();
            youngestBirthDate = bornBetween.getEndTime();
        }

        if(oldestAge != null) {
            Date birthDate = convertAgeToBirthDate(oldestAge);
            oldestBirthDate = (oldestBirthDate != null && birthDate.before(oldestBirthDate)) ? oldestBirthDate : birthDate;
        }

        if(youngestAge != null) {
            Date birthDate = convertAgeToBirthDate(youngestAge);
            youngestBirthDate = (youngestBirthDate != null && birthDate.after(youngestBirthDate)) ? youngestBirthDate : birthDate;
        }

        if(oldestBirthDate != null) {
            predicates.add( criteriaBuilder.greaterThanOrEqualTo(naturalPerson.<Date>get(NaturalPerson_.birthDate), new Date(oldestBirthDate.getTime())) );
        }

        if(youngestBirthDate != null) {
            predicates.add( criteriaBuilder.lessThanOrEqualTo(naturalPerson.<Date>get(NaturalPerson_.birthDate), new Date(youngestBirthDate.getTime())) );
        }

        if(location != null) {

            Path<Address> address = naturalPerson.get(NaturalPerson_.homeAddress);

            if(location.getCity() != null)
                predicates.add( criteriaBuilder.like(address.get(Address_.city), "%" + location.getCity() + "%") );

            if(location.getState() != null)
                predicates.add( criteriaBuilder.like(address.get(Address_.state), "%" + location.getState() + "%") );

            if(location.getCountry() != null)
                predicates.add( criteriaBuilder.like(address.get(Address_.country), "%" + location.getCountry() + "%") );

            if(location.getStreet() != null)
                predicates.add( criteriaBuilder.like(address.get(Address_.street), "%" + location.getStreet() + "%") );

            if(location.getZipCode() != null)
                predicates.add( criteriaBuilder.like(address.get(Address_.zipCode), "%" + location.getZipCode() + "%") );

            if(location.getFlatNumber() != null)
                predicates.add( criteriaBuilder.like(address.get(Address_.flatNumber), "%" + location.getFlatNumber() + "%") );

            if(location.getHouseNumber() != null)
                predicates.add( criteriaBuilder.like(address.get(Address_.houseNumber), "%" + location.getHouseNumber() + "%") );
        }

        if(delivery != null) {

            Path<Address> address = naturalPerson.get(NaturalPerson_.deliveryAddress);

            if(delivery.getCity() != null)
                predicates.add( criteriaBuilder.like(address.get(Address_.city), "%" + delivery.getCity() + "%") );

            if(delivery.getState() != null)
                predicates.add( criteriaBuilder.like(address.get(Address_.state), "%" + delivery.getState() + "%") );

            if(delivery.getCountry() != null)
                predicates.add( criteriaBuilder.like(address.get(Address_.country), "%" + delivery.getCountry() + "%") );

            if(delivery.getStreet() != null)
                predicates.add( criteriaBuilder.like(address.get(Address_.street), "%" + delivery.getStreet() + "%") );

            if(delivery.getZipCode() != null)
                predicates.add( criteriaBuilder.like(address.get(Address_.zipCode), "%" + delivery.getZipCode() + "%") );

            if(delivery.getFlatNumber() != null)
                predicates.add( criteriaBuilder.like(address.get(Address_.flatNumber), "%" + delivery.getFlatNumber() + "%") );

            if(delivery.getHouseNumber() != null)
                predicates.add( criteriaBuilder.like(address.get(Address_.houseNumber), "%" + delivery.getHouseNumber() + "%") );

        }

        // WHERE predicate1 AND predicate2 AND ... AND predicateN
        criteriaQuery.where(predicates.toArray(new Predicate[] { }));

        TypedQuery<NaturalPerson> query = getEntityManager().createQuery(criteriaQuery);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }

        return query.getResultList();
    }

    private Date convertAgeToBirthDate(Integer age) {

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -age);
        return  calendar.getTime();
    }
}
