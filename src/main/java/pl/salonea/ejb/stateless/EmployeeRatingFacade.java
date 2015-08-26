package pl.salonea.ejb.stateless;

import pl.salonea.ejb.interfaces.EmployeeRatingFacadeInterface;
import pl.salonea.entities.Client;
import pl.salonea.entities.Employee;
import pl.salonea.entities.EmployeeRating;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;

/**
 * Created by michzio on 23/08/2015.
 */
@Stateless
@LocalBean
public class EmployeeRatingFacade extends AbstractFacade<EmployeeRating>
        implements EmployeeRatingFacadeInterface.Local, EmployeeRatingFacadeInterface.Remote {

    @Inject
    private EntityManager em;

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    public EmployeeRatingFacade() {
        super(EmployeeRating.class);
    }

    @Override
    public List<EmployeeRating> findByClient(Client client) {
        return findByClient(client, null, null);
    }

    @Override
    public List<EmployeeRating> findByClient(Client client, Integer start, Integer limit) {

        TypedQuery<EmployeeRating> query = getEntityManager().createNamedQuery(EmployeeRating.FIND_BY_CLIENT, EmployeeRating.class);
        query.setParameter("client", client);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<EmployeeRating> findByEmployee(Employee employee) {
        return findByEmployee(employee, null, null);
    }

    @Override
    public List<EmployeeRating> findByEmployee(Employee employee, Integer start, Integer limit) {

        TypedQuery<EmployeeRating> query = getEntityManager().createNamedQuery(EmployeeRating.FIND_BY_EMPLOYEE, EmployeeRating.class);
        query.setParameter("employee", employee);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<EmployeeRating> findForEmployeeByRating(Employee employee, Short rating) {
        return findForEmployeeByRating(employee, rating, null, null);
    }

    @Override
    public List<EmployeeRating> findForEmployeeByRating(Employee employee, Short rating, Integer start, Integer limit) {

        TypedQuery<EmployeeRating> query = getEntityManager().createNamedQuery(EmployeeRating.FIND_FOR_EMPLOYEE_BY_RATING, EmployeeRating.class);
        query.setParameter("employee", employee);
        query.setParameter("rating", rating);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<EmployeeRating> findForEmployeeAboveRating(Employee employee, Short minRating) {
        return findForEmployeeAboveRating(employee, minRating, null, null);
    }

    @Override
    public List<EmployeeRating> findForEmployeeAboveRating(Employee employee, Short minRating, Integer start, Integer limit) {

        TypedQuery<EmployeeRating> query = getEntityManager().createNamedQuery(EmployeeRating.FIND_FOR_EMPLOYEE_ABOVE_RATING, EmployeeRating.class);
        query.setParameter("employee", employee);
        query.setParameter("min_rating", minRating);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<EmployeeRating> findForEmployeeBelowRating(Employee employee, Short maxRating) {
        return findForEmployeeBelowRating(employee, maxRating, null, null);
    }

    @Override
    public List<EmployeeRating> findForEmployeeBelowRating(Employee employee, Short maxRating, Integer start, Integer limit) {

        TypedQuery<EmployeeRating> query = getEntityManager().createNamedQuery(EmployeeRating.FIND_FOR_EMPLOYEE_BELOW_RATING, EmployeeRating.class);
        query.setParameter("employee", employee);
        query.setParameter("max_rating", maxRating);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<EmployeeRating> findFromClientByRating(Client client, Short rating) {
        return findFromClientByRating(client, rating, null, null);
    }

    @Override
    public List<EmployeeRating> findFromClientByRating(Client client, Short rating, Integer start, Integer limit) {

        TypedQuery<EmployeeRating> query = getEntityManager().createNamedQuery(EmployeeRating.FIND_FROM_CLIENT_BY_RATING, EmployeeRating.class);
        query.setParameter("client", client);
        query.setParameter("rating", rating);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<EmployeeRating> findFromClientAboveRating(Client client, Short minRating) {
        return findFromClientAboveRating(client, minRating, null, null);
    }

    @Override
    public List<EmployeeRating> findFromClientAboveRating(Client client, Short minRating, Integer start, Integer limit) {

        TypedQuery<EmployeeRating> query = getEntityManager().createNamedQuery(EmployeeRating.FIND_FROM_CLIENT_ABOVE_RATING, EmployeeRating.class);
        query.setParameter("client", client);
        query.setParameter("min_rating", minRating);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<EmployeeRating> findFromClientBelowRating(Client client, Short maxRating) {
        return findFromClientBelowRating(client, maxRating, null, null);
    }

    @Override
    public List<EmployeeRating> findFromClientBelowRating(Client client, Short maxRating, Integer start, Integer limit) {

        TypedQuery<EmployeeRating> query = getEntityManager().createNamedQuery(EmployeeRating.FIND_FROM_CLIENT_BELOW_RATING, EmployeeRating.class);
        query.setParameter("client", client);
        query.setParameter("max_rating", maxRating);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public Double findEmployeeAvgRating(Employee employee) {

        TypedQuery<Double> query = getEntityManager().createNamedQuery(EmployeeRating.FIND_EMPLOYEE_AVG_RATING, Double.class);
        query.setParameter("employee", employee);
        return query.getSingleResult();
    }

    @Override
    public Long countEmployeeRatings(Employee employee) {

        TypedQuery<Long> query = getEntityManager().createNamedQuery(EmployeeRating.COUNT_EMPLOYEE_RATINGS, Long.class);
        query.setParameter("employee", employee);
        return query.getSingleResult();
    }

    @Override
    public Long countClientRatings(Client client) {

        TypedQuery<Long> query = getEntityManager().createNamedQuery(EmployeeRating.COUNT_CLIENT_RATINGS, Long.class);
        query.setParameter("client", client);
        return query.getSingleResult();
    }

    @Override
    public Integer deleteByClient(Client client) {

        Query query = getEntityManager().createNamedQuery(EmployeeRating.DELETE_BY_CLIENT);
        query.setParameter("client", client);
        return query.executeUpdate();
    }

    @Override
    public Integer deleteByEmployee(Employee employee) {

        Query query = getEntityManager().createNamedQuery(EmployeeRating.DELETE_BY_EMPLOYEE);
        query.setParameter("employee", employee);
        return query.executeUpdate();
    }
}