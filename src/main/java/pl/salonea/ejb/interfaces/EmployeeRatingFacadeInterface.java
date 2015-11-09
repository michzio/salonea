package pl.salonea.ejb.interfaces;

import pl.salonea.entities.Client;
import pl.salonea.entities.Employee;
import pl.salonea.entities.EmployeeRating;
import pl.salonea.entities.idclass.EmployeeRatingId;

import java.util.List;

/**
 * Created by michzio on 23/08/2015.
 */
public interface EmployeeRatingFacadeInterface extends AbstractFacadeInterface<EmployeeRating> {

    // concrete interface
    EmployeeRating createForEmployeeAndClient(Long employeeId, Long clientId, EmployeeRating employeeRating);
    EmployeeRating update(EmployeeRatingId employeeRatingId, EmployeeRating employeeRating);
    List<EmployeeRating> findByClient(Client client);
    List<EmployeeRating> findByClient(Client client, Integer start, Integer limit);
    List<EmployeeRating> findByEmployee(Employee employee);
    List<EmployeeRating> findByEmployee(Employee employee, Integer start, Integer limit);
    List<EmployeeRating> findForEmployeeByRating(Employee employee, Short rating);
    List<EmployeeRating> findForEmployeeByRating(Employee employee, Short rating, Integer start, Integer limit);
    List<EmployeeRating> findForEmployeeAboveRating(Employee employee, Short minRating);
    List<EmployeeRating> findForEmployeeAboveRating(Employee employee, Short minRating, Integer start, Integer limit);
    List<EmployeeRating> findForEmployeeBelowRating(Employee employee, Short maxRating);
    List<EmployeeRating> findForEmployeeBelowRating(Employee employee, Short maxRating, Integer start, Integer limit);
    List<EmployeeRating> findFromClientByRating(Client client, Short rating);
    List<EmployeeRating> findFromClientByRating(Client client, Short rating, Integer start, Integer limit);
    List<EmployeeRating> findFromClientAboveRating(Client client, Short minRating);
    List<EmployeeRating> findFromClientAboveRating(Client client, Short minRating, Integer start, Integer limit);
    List<EmployeeRating> findFromClientBelowRating(Client client, Short maxRating);
    List<EmployeeRating> findFromClientBelowRating(Client client, Short maxRating, Integer start, Integer limit);
    Double findEmployeeAvgRating(Employee employee);
    Long countEmployeeRatings(Employee employee);
    Long countClientRatings(Client client);
    Integer deleteByClient(Client client);
    Integer deleteByEmployee(Employee employee);
    Integer deleteById(EmployeeRatingId employeeRatingId);
    List<EmployeeRating> findByMultipleCriteria(List<Client> clients, List<Employee> employees, Short minRating, Short maxRating, Short exactRating, String clientComment, String employeeDementi);
    List<EmployeeRating> findByMultipleCriteria(List<Client> clients, List<Employee> employees, Short minRating, Short maxRating, Short exactRating, String clientComment, String employeeDementi, Integer start, Integer limit);

    @javax.ejb.Local
    interface Local extends EmployeeRatingFacadeInterface { }

    @javax.ejb.Remote
    interface Remote extends EmployeeRatingFacadeInterface { }
}
