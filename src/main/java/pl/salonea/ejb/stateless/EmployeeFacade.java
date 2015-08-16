package pl.salonea.ejb.stateless;

import pl.salonea.ejb.interfaces.EmployeeFacadeInterface;
import pl.salonea.entities.*;
import pl.salonea.utils.Period;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by michzio on 13/08/2015.
 */
@Stateless
@LocalBean
public class EmployeeFacade extends AbstractFacade<Employee> implements EmployeeFacadeInterface.Local, EmployeeFacadeInterface.Remote {

    @Inject
    private EntityManager em;

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    public EmployeeFacade() {
        super(Employee.class);
    }

    @Override
    public List<Employee> findByDescription(String description) {
        return findByDescription(description, null, null);
    }

    @Override
    public List<Employee> findByDescription(String description, Integer start, Integer offset) {

        TypedQuery<Employee> query = getEntityManager().createNamedQuery(Employee.FIND_BY_DESCRIPTION, Employee.class);
        query.setParameter("description", "%" + description + "%");
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public List<Employee> findByJobPosition(String jobPosition) {
        return findByJobPosition(jobPosition, null, null);
    }

    @Override
    public List<Employee> findByJobPosition(String jobPosition, Integer start, Integer offset) {

        TypedQuery<Employee> query = getEntityManager().createNamedQuery(Employee.FIND_BY_JOB_POSITION, Employee.class);
        query.setParameter("job_position", jobPosition);
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public List<Employee> findBySkill(Skill skill) {
        return findBySkill(skill, null, null);
    }

    @Override
    public List<Employee> findBySkill(Skill skill, Integer start, Integer offset) {

        TypedQuery<Employee> query = getEntityManager().createNamedQuery(Employee.FIND_BY_SKILL, Employee.class);
        query.setParameter("skill", skill);
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public List<Employee> findByEducation(Education education) {
        return findByEducation(education, null, null);
    }

    @Override
    public List<Employee> findByEducation(Education education, Integer start, Integer offset) {

        TypedQuery<Employee> query = getEntityManager().createNamedQuery(Employee.FIND_BY_EDUCATION, Employee.class);
        query.setParameter("education", education);
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public List<Employee> findByEducationAndSkills(Education education, List<Skill> skills) {
        return findByEducationAndSkills(education, skills, null, null);
    }

    @Override
    public List<Employee> findByEducationAndSkills(Education education, List<Skill> skills, Integer start, Integer offset) {

        TypedQuery<Employee> query = getEntityManager().createNamedQuery(Employee.FIND_BY_EDUCATION_AND_SKILLS, Employee.class);
        query.setParameter("education", education);
        query.setParameter("skills", skills);
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public List<Employee> findByService(Service service) {
        return findByService(service, null, null);
    }

    @Override
    public List<Employee> findByService(Service service, Integer start, Integer offset) {

        TypedQuery<Employee> query = getEntityManager().createNamedQuery(Employee.FIND_BY_SERVICE, Employee.class);
        query.setParameter("service", service);
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public List<Employee> findByProviderService(ProviderService providerService) {
        return findByProviderService(providerService, null, null);
    }

    @Override
    public List<Employee> findByProviderService(ProviderService providerService, Integer start, Integer offset) {

        TypedQuery<Employee> query = getEntityManager().createNamedQuery(Employee.FIND_BY_PROVIDER_SERVICE, Employee.class);
        query.setParameter("provider_service",  providerService);
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public List<Employee> findByServicePoint(ServicePoint servicePoint) {
        return findByServicePoint(servicePoint, null, null);
    }

    @Override
    public List<Employee> findByServicePoint(ServicePoint servicePoint, Integer start, Integer offset) {

        TypedQuery<Employee> query = getEntityManager().createNamedQuery(Employee.FIND_BY_SERVICE_POINT, Employee.class);
        query.setParameter("service_point", servicePoint);
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public List<Employee> findByServicePointAndTerm(ServicePoint servicePoint, Date startTime, Date endTime) {
        return findByServicePointAndTerm(servicePoint, startTime, endTime, null, null);
    }

    @Override
    public List<Employee> findByServicePointAndTerm(ServicePoint servicePoint, Date startTime, Date endTime, Integer start, Integer offset) {

        TypedQuery<Employee> query = getEntityManager().createNamedQuery(Employee.FIND_BY_SERVICE_POINT_AND_TERM, Employee.class);
        query.setParameter("service_point", servicePoint);
        query.setParameter("start_time", startTime);
        query.setParameter("end_time", endTime);
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public List<Employee> findByServicePointAndTermStrict(ServicePoint servicePoint, Date startTime, Date endTime) {
        return findByServicePointAndTermStrict(servicePoint, startTime, endTime, null, null);
    }

    @Override
    public List<Employee> findByServicePointAndTermStrict(ServicePoint servicePoint, Date startTime, Date endTime, Integer start, Integer offset) {

        TypedQuery<Employee> query = getEntityManager().createNamedQuery(Employee.FIND_BY_SERVICE_POINT_AND_TERM_STRICT, Employee.class);
        query.setParameter("service_point", servicePoint);
        query.setParameter("start_time", startTime);
        query.setParameter("end_time", endTime);
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public List<Employee> findByWorkStation(WorkStation workStation) {
        return findByWorkStation(workStation, null, null);
    }

    @Override
    public List<Employee> findByWorkStation(WorkStation workStation, Integer start, Integer offset) {

        TypedQuery<Employee> query = getEntityManager().createNamedQuery(Employee.FIND_BY_WORK_STATION, Employee.class);
        query.setParameter("work_station", workStation);
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public List<Employee> findByWorkStationAndTerm(WorkStation workStation, Date startTime, Date endTime) {
        return findByWorkStationAndTerm(workStation, startTime, endTime, null, null);
    }

    @Override
    public List<Employee> findByWorkStationAndTerm(WorkStation workStation, Date startTime, Date endTime, Integer start, Integer offset) {

        TypedQuery<Employee> query = getEntityManager().createNamedQuery(Employee.FIND_BY_WORK_STATION_AND_TERM, Employee.class);
        query.setParameter("work_station", workStation);
        query.setParameter("start_time", startTime);
        query.setParameter("end_time", endTime);
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public List<Employee> findByWorkStationAndTermStrict(WorkStation workStation, Date startTime, Date endTime) {
        return findByWorkStationAndTermStrict(workStation, startTime, endTime, null, null);
    }

    @Override
    public List<Employee> findByWorkStationAndTermStrict(WorkStation workStation, Date startTime, Date endTime, Integer start, Integer offset) {

        TypedQuery<Employee> query = getEntityManager().createNamedQuery(Employee.FIND_BY_WORK_STATION_AND_TERM_STRICT, Employee.class);
        query.setParameter("work_station", workStation);
        query.setParameter("start_time", startTime);
        query.setParameter("end_time", endTime);
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public List<Employee> findByMultipleCriteria(String description, List<String> jobPositions, List<Skill> skills, List<Education> educations, List<Service> services, List<ProviderService> providerServices, List<ServicePoint> servicePoints, List<WorkStation> workStations, Period period, Boolean strictTerm) {
        return findByMultipleCriteria(description, jobPositions, skills, educations, services, providerServices, servicePoints, workStations, period, strictTerm, null, null);
    }

    @Override
    public List<Employee> findByMultipleCriteria(String description, List<String> jobPositions, List<Skill> skills, List<Education> educations, List<Service> services, List<ProviderService> providerServices, List<ServicePoint> servicePoints, List<WorkStation> workStations, Period period, Boolean strictTerm, Integer start, Integer offset) {

        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Employee> criteriaQuery = criteriaBuilder.createQuery(Employee.class);
        // FROM
        Root<Employee> employee = criteriaQuery.from(Employee.class);
        // SELECT
        criteriaQuery.select(employee).distinct(true);

        // INNER JOIN-s
        Join<Employee, TermEmployeeWorkOn> employeeTerm = null;
        Join<TermEmployeeWorkOn, WorkStation> workStation = null;
        Join<TermEmployeeWorkOn, Term> term = null;
        Join<WorkStation, ServicePoint> servicePoint = null;
        Join<Employee, ProviderService> providerService = null;
        Join<ProviderService, Service> service = null;

        // WHERE PREDICATES
        List<Predicate> predicates = new ArrayList<>();

        if(description != null) {
            predicates.add(criteriaBuilder.like(employee.get(Employee_.description), "%" + description + "%"));
        }

        if(jobPositions != null && jobPositions.size() > 0) {
            predicates.add( employee.get(Employee_.jobPosition).in(jobPositions) );
        }

        if(skills != null && skills.size() > 0) {

            List<Predicate> orPredicates = new ArrayList<>();

            for(Skill skill : skills) {
                orPredicates.add( criteriaBuilder.isMember(skill, employee.get(Employee_.skills)) );
            }

            predicates.add( criteriaBuilder.or(orPredicates.toArray(new Predicate[] { })) );
        }

        if(educations != null && educations.size() > 0) {

            List<Predicate> orPredicates = new ArrayList<>();

            for(Education education : educations) {
                orPredicates.add( criteriaBuilder.isMember(education, employee.get(Employee_.educations)) );
            }

            predicates.add( criteriaBuilder.or(orPredicates.toArray(new Predicate[] {})) );
        }

        if(services != null && services.size() > 0) {

            if(providerService == null) providerService = employee.join(Employee_.suppliedServices);
            if(service == null) service = providerService.join(ProviderService_.service);

            predicates.add( service.in(services) );
        }

        if(providerServices != null && providerServices.size() > 0) {

            List<Predicate> orPredicates = new ArrayList<>();

            for(ProviderService provService : providerServices) {
                orPredicates.add( criteriaBuilder.isMember(provService, employee.get(Employee_.suppliedServices)) );
            }

            predicates.add( criteriaBuilder.or(orPredicates.toArray(new Predicate[]{})) );
        }

        if(servicePoints != null && servicePoints.size() > 0) {

           if(employeeTerm == null) employeeTerm = employee.join(Employee_.termsOnWorkStation);
           if(workStation == null) workStation = employeeTerm.join(TermEmployeeWorkOn_.workStation);
           if(servicePoint == null) servicePoint =  workStation.join(WorkStation_.servicePoint);

           predicates.add( servicePoint.in(servicePoints) );

        }

        if(workStations != null && workStations.size() > 0) {

           if(employeeTerm == null) employeeTerm = employee.join(Employee_.termsOnWorkStation);

            predicates.add( employeeTerm.get(TermEmployeeWorkOn_.workStation).in(workStations) );
        }

        if(period != null) {

            if(employeeTerm == null) employeeTerm = employee.join(Employee_.termsOnWorkStation);
            if(term == null) term = employeeTerm.join(TermEmployeeWorkOn_.term);

            if(strictTerm != null && strictTerm) {
                predicates.add( criteriaBuilder.lessThanOrEqualTo( term.get(Term_.openingTime), period.getStartTime()) );
                predicates.add( criteriaBuilder.greaterThanOrEqualTo( term.get(Term_.closingTime), period.getEndTime()) );
            } else {
                predicates.add( criteriaBuilder.lessThan( term.get(Term_.openingTime), period.getEndTime()) );
                predicates.add( criteriaBuilder.greaterThan( term.get(Term_.closingTime), period.getStartTime()) );
            }
        }

        // take into account that work station must provide given services when searching by (term or work station or service point) and (services or provider services)
        if( (period != null || workStations != null || servicePoints != null) && (services != null || providerServices != null) ) {

            if(providerService == null) providerService = employee.join(Employee_.suppliedServices);
            if(employeeTerm == null) employeeTerm = employee.join(Employee_.termsOnWorkStation);
            if(workStation == null) workStation = employeeTerm.join(TermEmployeeWorkOn_.workStation);

            predicates.add( criteriaBuilder.isMember(providerService, (workStation.get(WorkStation_.providedServices)) ) );
        }

        // WHERE predicate1 AND predicate2 AND ... AND predicateN
        criteriaQuery.where(predicates.toArray(new Predicate[] { }));

        TypedQuery<Employee> query = getEntityManager().createQuery(criteriaQuery);
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }
}
