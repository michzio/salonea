package pl.salonea.ejb.interfaces;

import pl.salonea.entities.Employee;
import pl.salonea.entities.Term;
import pl.salonea.entities.TermEmployeeWorkOn;
import pl.salonea.entities.WorkStation;

import java.util.List;

/**
 * Created by michzio on 17/08/2015.
 */
public interface TermEmployeeWorkOnFacadeInterface extends AbstractFacadeInterface<TermEmployeeWorkOn> {

    // concrete interface
    Integer deleteForEmployees(List<Employee> employees);
    Integer deleteForWorkStations(List<WorkStation> workStations);
    Integer deleteForTerms(List<Term> terms);
    Integer deleteForEmployeesAndWorkStations(List<Employee> employees, List<WorkStation> workStations);
    Integer deleteForEmployeesAndTerms(List<Employee> employees, List<Term> terms);
    Integer deleteForWorkStationsAndTerms(List<WorkStation> workStations, List<Term> terms);

    @javax.ejb.Local
    interface Local extends TermEmployeeWorkOnFacadeInterface { }

    @javax.ejb.Remote
    interface Remote extends TermEmployeeWorkOnFacadeInterface { }
}
