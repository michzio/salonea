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
    Employee update(Employee employee, Boolean retainTransientFields);

    List<Employee> findAllEagerly();
    List<Employee> findAllEagerly(Integer start, Integer limit);
    Employee findByIdEagerly(Long employeeId);

    List<Employee> findByDescription(String description);
    List<Employee> findByDescription(String description, Integer start, Integer limit);
    List<Employee> findByJobPosition(String jobPosition);
    List<Employee> findByJobPosition(String jobPosition, Integer start, Integer limit);

    List<Employee> findBySkill(Skill skill);
    List<Employee> findBySkill(Skill skill, Integer start, Integer limit);
    List<Employee> findBySkillEagerly(Skill skill);
    List<Employee> findBySkillEagerly(Skill skill, Integer start, Integer limit);
    List<Employee> findByEducation(Education education);
    List<Employee> findByEducation(Education education, Integer start, Integer limit);
    List<Employee> findByEducationEagerly(Education education);
    List<Employee> findByEducationEagerly(Education education, Integer start, Integer limit);
    List<Employee> findByEducationAndSkills(Education education, List<Skill> skills);
    List<Employee> findByEducationAndSkills(Education education, List<Skill> skills, Integer start, Integer limit);

    List<Employee> findByService(Service service);
    List<Employee> findByService(Service service, Integer start, Integer limit);
    List<Employee> findByServiceEagerly(Service service);
    List<Employee> findByServiceEagerly(Service service, Integer start, Integer limit);
    List<Employee> findByProviderService(ProviderService providerService);
    List<Employee> findByProviderService(ProviderService providerService, Integer start, Integer limit);
    List<Employee> findByProviderServiceEagerly(ProviderService providerService);
    List<Employee> findByProviderServiceEagerly(ProviderService providerService, Integer start, Integer limit);
    List<Employee> findByServicePoint(ServicePoint servicePoint);
    List<Employee> findByServicePoint(ServicePoint servicePoint, Integer start, Integer limit);
    List<Employee> findByServicePointEagerly(ServicePoint servicePoint);
    List<Employee> findByServicePointEagerly(ServicePoint servicePoint, Integer start, Integer limit);
    List<Employee> findByServicePointAndTerm(ServicePoint servicePoint, Date startTime, Date endTime);
    List<Employee> findByServicePointAndTerm(ServicePoint servicePoint, Date startTime, Date endTime, Integer start, Integer limit);
    List<Employee> findByServicePointAndTermStrict(ServicePoint servicePoint, Date startTime, Date endTime);
    List<Employee> findByServicePointAndTermStrict(ServicePoint servicePoint, Date startTime, Date endTime, Integer start, Integer limit);
    List<Employee> findByWorkStation(WorkStation workStation);
    List<Employee> findByWorkStation(WorkStation workStation, Integer start, Integer limit);
    List<Employee> findByWorkStationEagerly(WorkStation workStation);
    List<Employee> findByWorkStationEagerly(WorkStation workStation, Integer start, Integer limit);
    List<Employee> findByWorkStationAndTerm(WorkStation workStation, Date startTime, Date endTime);
    List<Employee> findByWorkStationAndTerm(WorkStation workStation, Date startTime, Date endTime, Integer start, Integer limit);
    List<Employee> findByWorkStationAndTermStrict(WorkStation workStation, Date startTime, Date endTime);
    List<Employee> findByWorkStationAndTermStrict(WorkStation workStation, Date startTime, Date endTime, Integer start, Integer limit);
    List<Employee> findRatedByClient(Client client);
    List<Employee> findRatedByClient(Client client, Integer start, Integer limit);
    List<Employee> findRatedByClientEagerly(Client client);
    List<Employee> findRatedByClientEagerly(Client client, Integer start, Integer limit);

    Long countByServicePoint(ServicePoint servicePoint);

    List<Employee> findByMultipleCriteria(String description, List<String> jobPositions, List<Skill> skills, List<Education> educations, List<Service> services, List<ProviderService> providerServices, List<ServicePoint> servicePoints, List<WorkStation> workStations, Period period, Boolean strictTerm, Boolean rated, Double minAvgRating, Double maxAvgRating, List<Client> ratingClients);
    List<Employee> findByMultipleCriteria(String description, List<String> jobPositions, List<Skill> skills, List<Education> educations, List<Service> services, List<ProviderService> providerServices, List<ServicePoint> servicePoints, List<WorkStation> workStations, Period period, Boolean strictTerm, Boolean rated, Double minAvgRating, Double maxAvgRating, List<Client> ratingClients, Integer start, Integer limit);
    List<Employee> findByMultipleCriteriaEagerly(String description, List<String> jobPositions, List<Skill> skills, List<Education> educations, List<Service> services, List<ProviderService> providerServices, List<ServicePoint> servicePoints, List<WorkStation> workStations, Period period, Boolean strictTerm, Boolean rated, Double minAvgRating, Double maxAvgRating, List<Client> ratingClients);
    List<Employee> findByMultipleCriteriaEagerly(String description, List<String> jobPositions, List<Skill> skills, List<Education> educations, List<Service> services, List<ProviderService> providerServices, List<ServicePoint> servicePoints, List<WorkStation> workStations, Period period, Boolean strictTerm, Boolean rated, Double minAvgRating, Double maxAvgRating, List<Client> ratingClients,  Integer start, Integer limit);

    @javax.ejb.Local
    interface Local extends EmployeeFacadeInterface { }

    @javax.ejb.Remote
    interface Remote extends EmployeeFacadeInterface { }
}
