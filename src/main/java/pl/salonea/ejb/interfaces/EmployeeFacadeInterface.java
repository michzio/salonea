package pl.salonea.ejb.interfaces;

import pl.salonea.entities.*;
import pl.salonea.utils.Period;

import java.util.Date;
import java.util.List;

/**
 * Created by michzio on 13/08/2015.
 */
public interface EmployeeFacadeInterface extends AbstractFacadeInterface<Employee> {

    // concrete interface
    List<Employee> findByDescription(String description);
    List<Employee> findByDescription(String description, Integer start, Integer offset);
    List<Employee> findByJobPosition(String jobPosition);
    List<Employee> findByJobPosition(String jobPosition, Integer start, Integer offset);
    List<Employee> findBySkill(Skill skill);
    List<Employee> findBySkill(Skill skill, Integer start, Integer offset);
    List<Employee> findByEducation(Education education);
    List<Employee> findByEducation(Education education, Integer start, Integer offset);
    List<Employee> findByService(Service service);
    List<Employee> findByService(Service service, Integer start, Integer offset);
    List<Employee> findByProviderService(ProviderService providerService);
    List<Employee> findByProviderService(ProviderService providerService, Integer start, Integer offset);
    List<Employee> findByServicePoint(ServicePoint servicePoint);
    List<Employee> findByServicePoint(ServicePoint servicePoint, Integer start, Integer offset);
    List<Employee> findByServicePointAndTerm(ServicePoint servicePoint, Date startTime, Date endTime);
    List<Employee> findByServicePointAndTerm(ServicePoint servicePoint, Date startTime, Date endTime, Integer start, Integer offset);
    List<Employee> findByServicePointAndTermStrict(ServicePoint servicePoint, Date startTime, Date endTime);
    List<Employee> findByServicePointAndTermStrict(ServicePoint servicePoint, Date startTime, Date endTime, Integer start, Integer offset);
    List<Employee> findByWorkStation(WorkStation workStation);
    List<Employee> findByWorkStation(WorkStation workStation, Integer start, Integer offset);
    List<Employee> findByWorkStationAndTerm(WorkStation workStation, Date startTime, Date endTime);
    List<Employee> findByWorkStationAndTerm(WorkStation workStation, Date startTime, Date endTime, Integer start, Integer offset);
    List<Employee> findByWorkStationAndTermStrict(WorkStation workStation, Date startTime, Date endTime);
    List<Employee> findByWorkStationAndTermStrict(WorkStation workStation, Date startTime, Date endTime, Integer start, Integer offset);
    List<Employee> findByMultipleCriteria(String description, List<String> jobPositions, List<Skill> skills, List<Education> educations, List<Service> services, List<ProviderService> providerServices, List<ServicePoint> servicePoints, List<WorkStation> workStations, Period period, Boolean strictTerm);
    List<Employee> findByMultipleCriteria(String description, List<String> jobPositions, List<Skill> skills, List<Education> educations, List<Service> services, List<ProviderService> providerServices, List<ServicePoint> servicePoints, List<WorkStation> workStations, Period period, Boolean strictTerm, Integer start, Integer offset);

    @javax.ejb.Local
    interface Local extends EmployeeFacadeInterface { }

    @javax.ejb.Remote
    interface Remote extends EmployeeFacadeInterface { }
}