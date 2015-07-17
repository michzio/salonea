package pl.salonea.ejb.stateless;

import pl.salonea.ejb.interfaces.NaturalPersonFacadeInterface;
import pl.salonea.entities.Client;
import pl.salonea.entities.NaturalPerson;
import pl.salonea.enums.Gender;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
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

    public List<NaturalPerson> findByFirstName(String firstName) {

        TypedQuery<NaturalPerson> query = getEntityManager().createNamedQuery(NaturalPerson.FIND_BY_FIRST_NAME, NaturalPerson.class);
        query.setParameter("fname", firstName);
        return query.getResultList();
    }

    public List<NaturalPerson> findByFirstName(String firstName, int start, int offset) {

        TypedQuery<NaturalPerson> query = getEntityManager().createNamedQuery(NaturalPerson.FIND_BY_FIRST_NAME, NaturalPerson.class);
        query.setParameter("fname", firstName);
        query.setFirstResult(start);
        query.setMaxResults(offset);
        return query.getResultList();
    }

    public List<NaturalPerson> findByLastName(String lastName) {
        TypedQuery<NaturalPerson> query = getEntityManager().createNamedQuery(NaturalPerson.FIND_BY_LAST_NAME, NaturalPerson.class);
        query.setParameter("lname", lastName);
        return query.getResultList();
    }

    public List<NaturalPerson> findByLastName(String lastName, int start, int offset) {

        TypedQuery<NaturalPerson> query = getEntityManager().createNamedQuery(NaturalPerson.FIND_BY_LAST_NAME, NaturalPerson.class);
        query.setParameter("lname", lastName);
        query.setFirstResult(start);
        query.setMaxResults(offset);
        return query.getResultList();
    }

    public List<NaturalPerson> findByNames(String firstName, String lastName) {

        TypedQuery<NaturalPerson> query = getEntityManager().createNamedQuery(NaturalPerson.FIND_BY_NAMES, NaturalPerson.class);
        query.setParameter("fname", firstName);
        query.setParameter("lname", lastName);
        return query.getResultList();
    }

    public List<NaturalPerson> findByNames(String firstName, String lastName, int start, int offset) {

        TypedQuery<NaturalPerson> query = getEntityManager().createNamedQuery(NaturalPerson.FIND_BY_NAMES, NaturalPerson.class);
        query.setParameter("fname", firstName);
        query.setParameter("lname", lastName);
        query.setFirstResult(start);
        query.setMaxResults(offset);
        return query.getResultList();
    }

    public List<NaturalPerson> findBornAfter(Date date) {

        TypedQuery<NaturalPerson> query = getEntityManager().createNamedQuery(NaturalPerson.FIND_BORN_AFTER, NaturalPerson.class);
        query.setParameter("date", date);
        return query.getResultList();
    }

    public List<NaturalPerson> findBornAfter(Date date, int start, int offset) {

        TypedQuery<NaturalPerson> query = getEntityManager().createNamedQuery(Client.FIND_BORN_AFTER, NaturalPerson.class);
        query.setParameter("date", date);
        query.setFirstResult(start);
        query.setMaxResults(offset);
        return query.getResultList();
    }

    public List<NaturalPerson> findBornBefore(Date date) {
        TypedQuery<NaturalPerson> query = getEntityManager().createNamedQuery(NaturalPerson.FIND_BORN_BEFORE, NaturalPerson.class);
        query.setParameter("date", date);
        return query.getResultList();
    }

    public List<NaturalPerson> findBornBefore(Date date, int start, int offset) {

        TypedQuery<NaturalPerson> query = getEntityManager().createNamedQuery(NaturalPerson.FIND_BORN_BEFORE, NaturalPerson.class);
        query.setParameter("date", date);
        query.setFirstResult(start);
        query.setMaxResults(offset);
        return query.getResultList();
    }

    public List<NaturalPerson> findBornBetween(Date startDate, Date endDate) {

        TypedQuery<NaturalPerson> query = getEntityManager().createNamedQuery(NaturalPerson.FIND_BORN_BETWEEN, NaturalPerson.class);
        query.setParameter("start_date", startDate);
        query.setParameter("end_date", endDate);
        return query.getResultList();
    }

    public List<NaturalPerson> findBornBetween(Date startDate, Date endDate, int start, int offset) {

        TypedQuery<NaturalPerson> query = getEntityManager().createNamedQuery(NaturalPerson.FIND_BORN_BEFORE, NaturalPerson.class);
        query.setParameter("start_date", startDate);
        query.setParameter("end_date", endDate);
        query.setFirstResult(start);
        query.setMaxResults(offset);
        return query.getResultList();
    }

    public List<NaturalPerson> findOlderThan(Integer age) {

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -age);
        Date youngestBirthDate = calendar.getTime();

        TypedQuery<NaturalPerson> query = getEntityManager().createNamedQuery(NaturalPerson.FIND_BORN_BEFORE, NaturalPerson.class);
        query.setParameter("date", youngestBirthDate);
        return query.getResultList();
    }

    public List<NaturalPerson> findOlderThan(Integer age, int start, int offset) {

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -age);
        Date youngestBirthDate = calendar.getTime();

        TypedQuery<NaturalPerson> query = getEntityManager().createNamedQuery(NaturalPerson.FIND_BORN_BEFORE, NaturalPerson.class);
        query.setParameter("date", youngestBirthDate);
        query.setFirstResult(start);
        query.setMaxResults(offset);
        return  query.getResultList();
    }

    public List<NaturalPerson> findYoungerThan(Integer age) {

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -age);
        Date oldestBirthDate = calendar.getTime();

        TypedQuery<NaturalPerson> query = getEntityManager().createNamedQuery(NaturalPerson.FIND_BORN_AFTER, NaturalPerson.class);
        query.setParameter("date", oldestBirthDate);
        return query.getResultList();
    }

    public List<NaturalPerson> findYoungerThan(Integer age, int start, int offset) {

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -age);
        Date oldestBirthDate = calendar.getTime();

        TypedQuery<NaturalPerson> query = getEntityManager().createNamedQuery(NaturalPerson.FIND_BORN_AFTER, NaturalPerson.class);
        query.setParameter("date", oldestBirthDate);
        query.setFirstResult(start);
        query.setMaxResults(offset);
        return query.getResultList();
    }

    public List<NaturalPerson> findBetweenAge(Integer youngestAge, Integer oldestAge) {
        return findBetweenAge(youngestAge, oldestAge, null, null);
    }

    public List<NaturalPerson> findBetweenAge(Integer youngestAge, Integer oldestAge, Integer start, Integer offset) {

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -youngestAge);
        Date youngestBirthDate = calendar.getTime();
        calendar.setTime(new Date());
        calendar.add(Calendar.YEAR, -oldestAge);
        Date oldestBirthDate = calendar.getTime();

        TypedQuery<NaturalPerson> query = getEntityManager().createNamedQuery(NaturalPerson.FIND_BORN_BETWEEN, NaturalPerson.class);
        query.setParameter("start_date", oldestBirthDate);
        query.setParameter("end_date", youngestBirthDate);
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    public List<NaturalPerson> findByLocation(String city, String state, String country, String street, String zipCode) {
        return findByLocation(city, state, country, street, zipCode, null, null);
    }

    public List<NaturalPerson> findByLocation(String city, String state, String country, String street, String zipCode, Integer start, Integer offset) {

        return findByAddress(NaturalPerson.FIND_BY_LOCATION, city, state, country, street, zipCode, start, offset);
    }

    public List<NaturalPerson> findByDelivery(String city, String state, String country, String street, String zipCode) {

        return findByDelivery(city, state, country, street, zipCode, null, null);
    }

    public List<NaturalPerson> findByDelivery(String city, String state, String country, String street, String zipCode, Integer start, Integer offset) {
        return findByAddress(NaturalPerson.FIND_BY_DELIVERY, city, state, country, street, zipCode, start, offset);
    }

    private List<NaturalPerson> findByAddress(String queryName, String city, String state, String country, String street, String zipCode, Integer start, Integer offset) {

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
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    public List<NaturalPerson> findByGender(Gender gender) {
        return findByGender(gender, null, null);
    }

    public List<NaturalPerson> findByGender(Gender gender, Integer start, Integer offset) {

        TypedQuery<NaturalPerson> query = getEntityManager().createNamedQuery(NaturalPerson.FIND_BY_GENDER, NaturalPerson.class);
        query.setParameter("gender", gender);
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }
}
