package pl.salonea.ejb.stateless;

import pl.salonea.ejb.interfaces.TermEmployeeWorkOnFacadeInterface;
import pl.salonea.entities.Employee;
import pl.salonea.entities.Term;
import pl.salonea.entities.TermEmployeeWorkOn;
import pl.salonea.entities.WorkStation;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

/**
 * Created by michzio on 17/08/2015.
 */
@Stateless
@LocalBean
public class TermEmployeeWorkOnFacade extends AbstractFacade<TermEmployeeWorkOn>
            implements TermEmployeeWorkOnFacadeInterface.Local, TermEmployeeWorkOnFacadeInterface.Remote {

    @Inject
    private EntityManager em;

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    public TermEmployeeWorkOnFacade() {
        super(TermEmployeeWorkOn.class);
    }

    @Override
    public Integer deleteForEmployees(List<Employee> employees) {

        Query query = getEntityManager().createNamedQuery(TermEmployeeWorkOn.DELETE_FOR_EMPLOYEES);
        query.setParameter("employees", employees);
        return query.executeUpdate();
    }

    @Override
    public Integer deleteForWorkStations(List<WorkStation> workStations) {

        Query query = getEntityManager().createNamedQuery(TermEmployeeWorkOn.DELETE_FOR_WORK_STATIONS);
        query.setParameter("work_stations", workStations);
        return query.executeUpdate();
    }

    @Override
    public Integer deleteForTerms(List<Term> terms) {

        Query query = getEntityManager().createNamedQuery(TermEmployeeWorkOn.DELETE_FOR_TERMS);
        query.setParameter("terms", terms);
        return query.executeUpdate();
    }

    @Override
    public Integer deleteForEmployeesAndWorkStations(List<Employee> employees, List<WorkStation> workStations) {

        Query query = getEntityManager().createNamedQuery(TermEmployeeWorkOn.DELETE_FOR_EMPLOYEES_AND_WORK_STATIONS);
        query.setParameter("employees", employees);
        query.setParameter("work_stations", workStations);
        return query.executeUpdate();
    }

    @Override
    public Integer deleteForEmployeesAndTerms(List<Employee> employees, List<Term> terms) {

        Query query = getEntityManager().createNamedQuery(TermEmployeeWorkOn.DELETE_FOR_EMPLOYEES_AND_TERMS);
        query.setParameter("employees", employees);
        query.setParameter("terms", terms);
        return query.executeUpdate();
    }

    @Override
    public Integer deleteForWorkStationsAndTerms(List<WorkStation> workStations, List<Term> terms) {

        Query query = getEntityManager().createNamedQuery(TermEmployeeWorkOn.DELETE_FOR_WORK_STATIONS_AND_TERMS);
        query.setParameter("work_stations", workStations);
        query.setParameter("terms", terms);
        return query.executeUpdate();
    }
}
