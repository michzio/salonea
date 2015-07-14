package pl.salonea.ejb.stateless;


import pl.salonea.ejb.interfaces.ClientFacadeInterface;
import pl.salonea.entities.Client;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by michzio on 11/07/2015.
 */
@Stateless
@LocalBean
public class ClientFacade extends AbstractFacade<Client> implements ClientFacadeInterface.Remote, ClientFacadeInterface.Local {

    @Inject
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ClientFacade() {
        super(Client.class);
    }

    public List<Client> findByFirstName(String firstName) {

        TypedQuery<Client> query = getEntityManager().createNamedQuery(Client.FIND_BY_FIRST_NAME, Client.class);
        query.setParameter("fname", firstName);
        return query.getResultList();
    }

    public List<Client> findByFirstName(String firstName, int start, int offset) {
        TypedQuery<Client> query = getEntityManager().createNamedQuery(Client.FIND_BY_FIRST_NAME, Client.class);
        query.setParameter("fname", firstName);
        query.setFirstResult(start);
        query.setMaxResults(offset);
        return query.getResultList();
    }

    public List<Client> findByLastName(String lastName) {
        TypedQuery<Client> query = getEntityManager().createNamedQuery(Client.FIND_BY_LAST_NAME, Client.class);
        query.setParameter("lname", lastName);
        return query.getResultList();
    }

    public List<Client> findByLastName(String lastName, int start, int offset) {

        TypedQuery<Client> query = getEntityManager().createNamedQuery(Client.FIND_BY_LAST_NAME, Client.class);
        query.setParameter("lname", lastName);
        query.setFirstResult(start);
        query.setMaxResults(offset);
        return query.getResultList();
    }

    public List<Client> findByNames(String firstName, String lastName) {

        TypedQuery<Client> query = getEntityManager().createNamedQuery(Client.FIND_BY_NAMES, Client.class);
        query.setParameter("fname", firstName);
        query.setParameter("lname", lastName);
        return query.getResultList();
    }

    public List<Client> findByNames(String firstName, String lastName, int start, int offset) {

        TypedQuery<Client> query = getEntityManager().createNamedQuery(Client.FIND_BY_NAMES, Client.class);
        query.setParameter("fname", firstName);
        query.setParameter("lname", lastName);
        query.setFirstResult(start);
        query.setMaxResults(offset);
        return query.getResultList();
    }

    public List<Client> findBornAfter(Date date) {

        TypedQuery<Client> query = getEntityManager().createNamedQuery(Client.FIND_BORN_AFTER, Client.class);
        query.setParameter("date", date);
        return query.getResultList();
    }

    public List<Client> findBornAfter(Date date, int start, int offset) {

        TypedQuery<Client> query = getEntityManager().createNamedQuery(Client.FIND_BORN_AFTER, Client.class);
        query.setParameter("date", date);
        query.setFirstResult(start);
        query.setMaxResults(offset);
        return query.getResultList();
    }

    public List<Client> findBornBefore(Date date) {

        TypedQuery<Client> query = getEntityManager().createNamedQuery(Client.FIND_BORN_BEFORE, Client.class);
        query.setParameter("date", date);
        return query.getResultList();
    }

    public List<Client> findBornBefore(Date date, int start, int offset) {

        TypedQuery<Client> query = getEntityManager().createNamedQuery(Client.FIND_BORN_BEFORE, Client.class);
        query.setParameter("date", date);
        return query.getResultList();
    }

    public List<Client> findBornBetween(Date startDate, Date endDate) {

        TypedQuery<Client> query = getEntityManager().createNamedQuery(Client.FIND_BORN_BETWEEN, Client.class);
        query.setParameter("start_date", startDate);
        query.setParameter("end_date", endDate);
        return query.getResultList();
    }

    public List<Client> findBornBetween(Date startDate, Date endDate, int start, int offset) {

        TypedQuery<Client> query = getEntityManager().createNamedQuery(Client.FIND_BORN_BEFORE, Client.class);
        query.setParameter("start_date", startDate);
        query.setParameter("end_date", endDate);
        query.setFirstResult(start);
        query.setMaxResults(offset);
        return query.getResultList();
    }

    public List<Client> findOlderThan(Integer age) {

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -age);
        Date youngestBirthDate = calendar.getTime();

        TypedQuery<Client> query = getEntityManager().createNamedQuery(Client.FIND_BORN_BEFORE, Client.class);
        query.setParameter("date", youngestBirthDate);
        return query.getResultList();
    }

    public List<Client> findOlderThan(Integer age, int start, int offset) {

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -age);
        Date youngestBirthDate = calendar.getTime();

        TypedQuery<Client> query = getEntityManager().createNamedQuery(Client.FIND_BORN_BEFORE, Client.class);
        query.setParameter("date", youngestBirthDate);
        query.setFirstResult(start);
        query.setMaxResults(offset);
        return  query.getResultList();
    }

    public List<Client> findYoungerThan(Integer age) {

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -age);
        Date oldestBirthDate = calendar.getTime();

        TypedQuery<Client> query = getEntityManager().createNamedQuery(Client.FIND_BORN_AFTER, Client.class);
        query.setParameter("date", oldestBirthDate);
        return query.getResultList();
    }

    public List<Client> findYoungerThan(Integer age, int start, int offset) {

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -age);
        Date oldestBirthDate = calendar.getTime();

        TypedQuery<Client> query = getEntityManager().createNamedQuery(Client.FIND_BORN_AFTER, Client.class);
        query.setParameter("date", oldestBirthDate);
        query.setFirstResult(start);
        query.setMaxResults(offset);
        return query.getResultList();
    }

    public List<Client> findBetweenAge(Integer youngestAge, Integer oldestAge) {
        return findBetweenAge(youngestAge, oldestAge, null, null);
    }

    public List<Client> findBetweenAge(Integer youngestAge, Integer oldestAge, Integer start, Integer offset) {

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -youngestAge);
        Date youngestBirthDate = calendar.getTime();
        calendar.setTime(new Date());
        calendar.add(Calendar.YEAR, -oldestAge);
        Date oldestBirthDate = calendar.getTime();

        TypedQuery<Client> query = getEntityManager().createNamedQuery(Client.FIND_BORN_BETWEEN, Client.class);
        query.setParameter("start_date", oldestBirthDate);
        query.setParameter("end_date", youngestBirthDate);
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    public List<Client> findByLocation(String city, String state, String country, String street, String zipCode) {
        return findByLocation(city, state, country, street, zipCode, null, null);
    }

    public List<Client> findByLocation(String city, String state, String country, String street, String zipCode, Integer start, Integer offset) {

        return findByAddress(Client.FIND_BY_LOCATION, city, state, country, street, zipCode, start, offset);
    }

    public List<Client> findByDelivery(String city, String state, String country, String street, String zipCode) {

        return findByDelivery(city, state, country, street, zipCode, null, null);
    }

    public List<Client> findByDelivery(String city, String state, String country, String street, String zipCode, Integer start, Integer offset) {
        return findByDelivery(city, state, country, street, zipCode, start, offset);
    }

    private List<Client> findByAddress(String queryName, String city, String state, String country, String street, String zipCode, Integer start, Integer offset) {

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
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

}