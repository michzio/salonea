package pl.salonea.ejb.stateless;

import pl.salonea.ejb.interfaces.EmployeeFacadeInterface;
import pl.salonea.entities.*;
import pl.salonea.utils.Period;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import javax.persistence.metamodel.SingularAttribute;
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
    public Employee update(Employee employee, Boolean retainTransientFields) {

        if(retainTransientFields) {
            // keep current collection attributes of resource (and other marked @XmlTransient)
            Employee currentEmployee = findByIdEagerly(employee.getUserId());
            if (currentEmployee != null ) {
                employee.setEducations(currentEmployee.getEducations());
                employee.setSkills(currentEmployee.getSkills());
                employee.setSuppliedServices(currentEmployee.getSuppliedServices());
                employee.setTermsOnWorkStation(currentEmployee.getTermsOnWorkStation());
                employee.setReceivedRatings(currentEmployee.getReceivedRatings());
            }
        }
        return update(employee);
    }

    @Override
    public List<Employee> findAllEagerly() {
        return findAllEagerly(null, null);
    }

    @Override
    public List<Employee> findAllEagerly(Integer start, Integer limit) {

        TypedQuery<Employee> query = getEntityManager().createNamedQuery(Employee.FIND_ALL_EAGERLY, Employee.class);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public Employee findByIdEagerly(Long employeeId) {

        TypedQuery<Employee> query = getEntityManager().createNamedQuery(Employee.FIND_BY_ID_EAGERLY, Employee.class);
        query.setParameter("employeeId", employeeId);
        try {
            return query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            return null;
        }
    }

    @Override
    public List<Employee> findByDescription(String description) {
        return findByDescription(description, null, null);
    }

    @Override
    public List<Employee> findByDescription(String description, Integer start, Integer limit) {

        TypedQuery<Employee> query = getEntityManager().createNamedQuery(Employee.FIND_BY_DESCRIPTION, Employee.class);
        query.setParameter("description", "%" + description + "%");
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<Employee> findByJobPosition(String jobPosition) {
        return findByJobPosition(jobPosition, null, null);
    }

    @Override
    public List<Employee> findByJobPosition(String jobPosition, Integer start, Integer limit) {

        TypedQuery<Employee> query = getEntityManager().createNamedQuery(Employee.FIND_BY_JOB_POSITION, Employee.class);
        query.setParameter("job_position", jobPosition);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<Employee> findBySkill(Skill skill) {
        return findBySkill(skill, null, null);
    }

    @Override
    public List<Employee> findBySkill(Skill skill, Integer start, Integer limit) {

        TypedQuery<Employee> query = getEntityManager().createNamedQuery(Employee.FIND_BY_SKILL, Employee.class);
        query.setParameter("skill", skill);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<Employee> findBySkillEagerly(Skill skill) {
        return findBySkillEagerly(skill, null, null);
    }

    @Override
    public List<Employee> findBySkillEagerly(Skill skill, Integer start, Integer limit) {

        TypedQuery<Employee> query = getEntityManager().createNamedQuery(Employee.FIND_BY_SKILL_EAGERLY, Employee.class);
        query.setParameter("skill", skill);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<Employee> findByEducation(Education education) {
        return findByEducation(education, null, null);
    }

    @Override
    public List<Employee> findByEducation(Education education, Integer start, Integer limit) {

        TypedQuery<Employee> query = getEntityManager().createNamedQuery(Employee.FIND_BY_EDUCATION, Employee.class);
        query.setParameter("education", education);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<Employee> findByEducationEagerly(Education education) {
        return findByEducationEagerly(education, null, null);
    }

    @Override
    public List<Employee> findByEducationEagerly(Education education, Integer start, Integer limit) {

        TypedQuery<Employee> query = getEntityManager().createNamedQuery(Employee.FIND_BY_EDUCATION_EAGERLY, Employee.class);
        query.setParameter("education", education);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<Employee> findByEducationAndSkills(Education education, List<Skill> skills) {
        return findByEducationAndSkills(education, skills, null, null);
    }

    @Override
    public List<Employee> findByEducationAndSkills(Education education, List<Skill> skills, Integer start, Integer limit) {

        TypedQuery<Employee> query = getEntityManager().createNamedQuery(Employee.FIND_BY_EDUCATION_AND_SKILLS, Employee.class);
        query.setParameter("education", education);
        query.setParameter("skills", skills);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<Employee> findByService(Service service) {
        return findByService(service, null, null);
    }

    @Override
    public List<Employee> findByService(Service service, Integer start, Integer limit) {

        TypedQuery<Employee> query = getEntityManager().createNamedQuery(Employee.FIND_BY_SERVICE, Employee.class);
        query.setParameter("service", service);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<Employee> findByServiceEagerly(Service service) {
        return findByServiceEagerly(service, null, null);
    }

    @Override
    public List<Employee> findByServiceEagerly(Service service, Integer start, Integer limit) {

        TypedQuery<Employee> query = getEntityManager().createNamedQuery(Employee.FIND_BY_SERVICE_EAGERLY, Employee.class);
        query.setParameter("service", service);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<Employee> findByProviderService(ProviderService providerService) {
        return findByProviderService(providerService, null, null);
    }

    @Override
    public List<Employee> findByProviderService(ProviderService providerService, Integer start, Integer limit) {

        TypedQuery<Employee> query = getEntityManager().createNamedQuery(Employee.FIND_BY_PROVIDER_SERVICE, Employee.class);
        query.setParameter("provider_service",  providerService);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<Employee> findByProviderServiceEagerly(ProviderService providerService) {
        return findByProviderServiceEagerly(providerService, null, null);
    }

    @Override
    public List<Employee> findByProviderServiceEagerly(ProviderService providerService, Integer start, Integer limit) {

        TypedQuery<Employee> query = getEntityManager().createNamedQuery(Employee.FIND_BY_PROVIDER_SERVICE_EAGERLY, Employee.class);
        query.setParameter("provider_service",  providerService);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<Employee> findByServicePoint(ServicePoint servicePoint) {
        return findByServicePoint(servicePoint, null, null);
    }

    @Override
    public List<Employee> findByServicePoint(ServicePoint servicePoint, Integer start, Integer limit) {

        TypedQuery<Employee> query = getEntityManager().createNamedQuery(Employee.FIND_BY_SERVICE_POINT, Employee.class);
        query.setParameter("service_point", servicePoint);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<Employee> findByServicePointEagerly(ServicePoint servicePoint) {
        return findByServicePointEagerly(servicePoint, null, null);
    }

    @Override
    public List<Employee> findByServicePointEagerly(ServicePoint servicePoint, Integer start, Integer limit) {

        TypedQuery<Employee> query = getEntityManager().createNamedQuery(Employee.FIND_BY_SERVICE_POINT_EAGERLY, Employee.class);
        query.setParameter("service_point", servicePoint);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<Employee> findByServicePointAndTerm(ServicePoint servicePoint, Date startTime, Date endTime) {
        return findByServicePointAndTerm(servicePoint, startTime, endTime, null, null);
    }

    @Override
    public List<Employee> findByServicePointAndTerm(ServicePoint servicePoint, Date startTime, Date endTime, Integer start, Integer limit) {

        TypedQuery<Employee> query = getEntityManager().createNamedQuery(Employee.FIND_BY_SERVICE_POINT_AND_TERM, Employee.class);
        query.setParameter("service_point", servicePoint);
        query.setParameter("start_time", startTime);
        query.setParameter("end_time", endTime);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<Employee> findByServicePointAndTermStrict(ServicePoint servicePoint, Date startTime, Date endTime) {
        return findByServicePointAndTermStrict(servicePoint, startTime, endTime, null, null);
    }

    @Override
    public List<Employee> findByServicePointAndTermStrict(ServicePoint servicePoint, Date startTime, Date endTime, Integer start, Integer limit) {

        TypedQuery<Employee> query = getEntityManager().createNamedQuery(Employee.FIND_BY_SERVICE_POINT_AND_TERM_STRICT, Employee.class);
        query.setParameter("service_point", servicePoint);
        query.setParameter("start_time", startTime);
        query.setParameter("end_time", endTime);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<Employee> findByWorkStation(WorkStation workStation) {
        return findByWorkStation(workStation, null, null);
    }

    @Override
    public List<Employee> findByWorkStation(WorkStation workStation, Integer start, Integer limit) {

        TypedQuery<Employee> query = getEntityManager().createNamedQuery(Employee.FIND_BY_WORK_STATION, Employee.class);
        query.setParameter("work_station", workStation);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<Employee> findByWorkStationEagerly(WorkStation workStation) {
        return findByWorkStationEagerly(workStation, null, null);
    }

    @Override
    public List<Employee> findByWorkStationEagerly(WorkStation workStation, Integer start, Integer limit) {

        TypedQuery<Employee> query = getEntityManager().createNamedQuery(Employee.FIND_BY_WORK_STATION_EAGERLY, Employee.class);
        query.setParameter("work_station", workStation);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<Employee> findByWorkStationAndTerm(WorkStation workStation, Date startTime, Date endTime) {
        return findByWorkStationAndTerm(workStation, startTime, endTime, null, null);
    }

    @Override
    public List<Employee> findByWorkStationAndTerm(WorkStation workStation, Date startTime, Date endTime, Integer start, Integer limit) {

        TypedQuery<Employee> query = getEntityManager().createNamedQuery(Employee.FIND_BY_WORK_STATION_AND_TERM, Employee.class);
        query.setParameter("work_station", workStation);
        query.setParameter("start_time", startTime);
        query.setParameter("end_time", endTime);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<Employee> findByWorkStationAndTermStrict(WorkStation workStation, Date startTime, Date endTime) {
        return findByWorkStationAndTermStrict(workStation, startTime, endTime, null, null);
    }

    @Override
    public List<Employee> findByWorkStationAndTermStrict(WorkStation workStation, Date startTime, Date endTime, Integer start, Integer limit) {

        TypedQuery<Employee> query = getEntityManager().createNamedQuery(Employee.FIND_BY_WORK_STATION_AND_TERM_STRICT, Employee.class);
        query.setParameter("work_station", workStation);
        query.setParameter("start_time", startTime);
        query.setParameter("end_time", endTime);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<Employee> findRatedByClient(Client client) {
        return findRatedByClient(client, null, null);
    }

    @Override
    public List<Employee> findRatedByClient(Client client, Integer start, Integer limit) {

        TypedQuery<Employee> query = getEntityManager().createNamedQuery(Employee.FIND_RATED_BY_CLIENT, Employee.class);
        query.setParameter("client", client);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<Employee> findRatedByClientEagerly(Client client) {
        return findRatedByClientEagerly(client, null, null);
    }

    @Override
    public List<Employee> findRatedByClientEagerly(Client client, Integer start, Integer limit) {

        TypedQuery<Employee> query = getEntityManager().createNamedQuery(Employee.FIND_RATED_BY_CLIENT_EAGERLY, Employee.class);
        query.setParameter("client", client);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<Employee> findByTerm(Term term) {
        return findByTerm(term, null, null);
    }

    @Override
    public List<Employee> findByTerm(Term term, Integer start, Integer limit) {

        TypedQuery<Employee> query = getEntityManager().createNamedQuery(Employee.FIND_BY_TERM, Employee.class);
        query.setParameter("term", term);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<Employee> findByTermEagerly(Term term) {
        return findByTermEagerly(term, null, null);
    }

    @Override
    public List<Employee> findByTermEagerly(Term term, Integer start, Integer limit) {

        TypedQuery<Employee> query = getEntityManager().createNamedQuery(Employee.FIND_BY_TERM_EAGERLY, Employee.class);
        query.setParameter("term", term);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public Long countByService(Service service) {

        TypedQuery<Long> query = getEntityManager().createNamedQuery(Employee.COUNT_BY_SERVICE, Long.class);
        query.setParameter("service", service);
        return query.getSingleResult();
    }

    @Override
    public Long countByProviderService(ProviderService providerService) {

        TypedQuery<Long> query = getEntityManager().createNamedQuery(Employee.COUNT_BY_PROVIDER_SERVICE, Long.class);
        query.setParameter("provider_service", providerService);
        return query.getSingleResult();
    }

    @Override
    public Long countByServicePoint(ServicePoint servicePoint) {

        TypedQuery<Long> query = getEntityManager().createNamedQuery(Employee.COUNT_BY_SERVICE_POINT, Long.class);
        query.setParameter("service_point", servicePoint);
        return query.getSingleResult();
    }

    @Override
    public Long countByWorkStation(WorkStation workStation) {

        TypedQuery<Long> query = getEntityManager().createNamedQuery(Employee.COUNT_BY_WORK_STATION, Long.class);
        query.setParameter("work_station", workStation);
        return query.getSingleResult();
    }

    @Override
    public Long countByTerm(Term term) {

        TypedQuery<Long> query = getEntityManager().createNamedQuery(Employee.COUNT_BY_TERM, Long.class);
        query.setParameter("term", term);
        return query.getSingleResult();
    }

    @Override
    public List<Employee> findByMultipleCriteria(List<String> descriptions, List<String> jobPositions, List<Skill> skills, List<Education> educations, List<Service> services, List<ProviderService> providerServices, List<ServicePoint> servicePoints, List<WorkStation> workStations, Period period, Boolean strictTerm, List<Term> terms, Boolean rated, Double minAvgRating, Double maxAvgRating, List<Client> ratingClients) {
        return findByMultipleCriteria(descriptions, jobPositions, skills, educations, services, providerServices, servicePoints, workStations, period, strictTerm, terms, rated, minAvgRating, maxAvgRating, ratingClients, null, null);
    }

    @Override
    public List<Employee> findByMultipleCriteria(List<String> descriptions, List<String> jobPositions, List<Skill> skills, List<Education> educations, List<Service> services, List<ProviderService> providerServices, List<ServicePoint> servicePoints, List<WorkStation> workStations, Period period, Boolean strictTerm, List<Term> terms, Boolean rated, Double minAvgRating, Double maxAvgRating, List<Client> ratingClients, Integer start, Integer limit) {
        return findByMultipleCriteria(descriptions, jobPositions, skills, educations, services, providerServices, servicePoints, workStations, period, strictTerm, terms, rated, minAvgRating, maxAvgRating, ratingClients, false, start, limit);
    }

    @Override
    public List<Employee> findByMultipleCriteriaEagerly(List<String> descriptions, List<String> jobPositions, List<Skill> skills, List<Education> educations, List<Service> services, List<ProviderService> providerServices, List<ServicePoint> servicePoints, List<WorkStation> workStations, Period period, Boolean strictTerm, List<Term> terms, Boolean rated, Double minAvgRating, Double maxAvgRating, List<Client> ratingClients) {
        return findByMultipleCriteriaEagerly(descriptions, jobPositions, skills, educations, services, providerServices, servicePoints, workStations, period, strictTerm, terms, rated, minAvgRating, maxAvgRating, ratingClients, null, null);
    }

    @Override
    public List<Employee> findByMultipleCriteriaEagerly(List<String> descriptions, List<String> jobPositions, List<Skill> skills, List<Education> educations, List<Service> services, List<ProviderService> providerServices, List<ServicePoint> servicePoints, List<WorkStation> workStations, Period period, Boolean strictTerm, List<Term> terms, Boolean rated, Double minAvgRating, Double maxAvgRating, List<Client> ratingClients, Integer start, Integer limit) {
        return findByMultipleCriteria(descriptions, jobPositions, skills, educations, services, providerServices, servicePoints, workStations, period, strictTerm, terms, rated, minAvgRating, maxAvgRating, ratingClients, true, start, limit);
    }

    private List<Employee> findByMultipleCriteria(List<String> descriptions, List<String> jobPositions, List<Skill> skills, List<Education> educations, List<Service> services, List<ProviderService> providerServices, List<ServicePoint> servicePoints, List<WorkStation> workStations, Period period, Boolean strictTerm,
                                                  List<Term> terms, Boolean rated, Double minAvgRating, Double maxAvgRating, List<Client> ratingClients, Boolean eagerly, Integer start, Integer limit) {

        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Employee> criteriaQuery = criteriaBuilder.createQuery(Employee.class);
        // FROM
        Root<Employee> employee = criteriaQuery.from(Employee.class);
        // SELECT
        criteriaQuery.select(employee).distinct(true);

        // INNER JOIN-s
        Join<Employee, EmployeeTerm> employeeTerm = null;
        Join<EmployeeTerm, WorkStation> workStation = null;
        Join<EmployeeTerm, Term> term = null;
        Join<WorkStation, ServicePoint> servicePoint = null;
        Join<Employee, ProviderService> providerService = null;
        Join<ProviderService, Service> service = null;
        Join<Employee, EmployeeRating> employeeRating = null;
        Join<EmployeeRating, Client> client = null;

        // WHERE PREDICATES
        List<Predicate> predicates = new ArrayList<>();
        // HAS BEEN GROUPED BY
        Boolean groupedBy = false;
        // HAVING PREDICATES
        List<Predicate> havingPredicates = new ArrayList<>();

        if(descriptions != null && descriptions.size() > 0) {

            List<Predicate> orDescriptionPredicates = new ArrayList<>();

            for(String description : descriptions) {
                SingularAttribute<Employee, String> descriptionAttr = Employee_.description;
                orDescriptionPredicates.add( criteriaBuilder.like(employee.get(descriptionAttr), "%" + description + "%") );
            }

            predicates.add( criteriaBuilder.or(orDescriptionPredicates.toArray(new Predicate[]{})) );
        }

        if(jobPositions != null && jobPositions.size() > 0) {
            predicates.add( employee.get(Employee_.jobPosition).in(jobPositions) );
        }

        if(skills != null && skills.size() > 0) {

            List<Predicate> orSkillPredicates = new ArrayList<>();

            for(Skill skill : skills) {
                orSkillPredicates.add( criteriaBuilder.isMember(skill, employee.get(Employee_.skills)) );
            }

            predicates.add( criteriaBuilder.or(orSkillPredicates.toArray(new Predicate[] { })) );

            if(eagerly) {
                // then fetch associated collection of entities
                employee.fetch("skills", JoinType.INNER);
            }
        } else if(eagerly) {
            // then left fetch associated collection of entities
            employee.fetch("skills", JoinType.LEFT);
        }

        if(educations != null && educations.size() > 0) {

            List<Predicate> orEducationPredicates = new ArrayList<>();

            for(Education education : educations) {
                orEducationPredicates.add( criteriaBuilder.isMember(education, employee.get(Employee_.educations)) );
            }

            predicates.add( criteriaBuilder.or(orEducationPredicates.toArray(new Predicate[]{})) );

            if(eagerly) {
                // then fetch associated collection of entities
                employee.fetch("educations", JoinType.INNER);
            }
        } else if(eagerly) {
            // then left fetch associated collection of entities
            employee.fetch("educations", JoinType.LEFT);
        }

        Fetch<Employee, ProviderService> fetchProviderServices = null;

        if(services != null && services.size() > 0) {

            if(providerService == null) providerService = employee.join(Employee_.suppliedServices);
            if(service == null) service = providerService.join(ProviderService_.service);

            predicates.add( service.in(services) );

            if(eagerly && fetchProviderServices == null) {
                // then fetch associated collection of entities
                fetchProviderServices = employee.fetch("suppliedServices", JoinType.INNER);
            }
        }

        if(providerServices != null && providerServices.size() > 0) {

            List<Predicate> orPredicates = new ArrayList<>();

            for(ProviderService provService : providerServices) {
                orPredicates.add( criteriaBuilder.isMember(provService, employee.get(Employee_.suppliedServices)) );
            }

            predicates.add( criteriaBuilder.or(orPredicates.toArray(new Predicate[]{})) );

            if(eagerly && fetchProviderServices == null) {
                // then fetch associated collection of entities
                fetchProviderServices = employee.fetch("suppliedServices", JoinType.INNER);
            }
        }

        if(eagerly && fetchProviderServices == null) {
            // then left fetch associated collection of entities
            fetchProviderServices = employee.fetch("suppliedServices", JoinType.LEFT);
        }

        Fetch<Employee, EmployeeTerm> fetchEmployeeTerms = null;

        if(servicePoints != null && servicePoints.size() > 0) {

            if(employeeTerm == null) employeeTerm = employee.join(Employee_.termsOnWorkStation);
            if(workStation == null) workStation = employeeTerm.join(EmployeeTerm_.workStation);
            if(servicePoint == null) servicePoint =  workStation.join(WorkStation_.servicePoint);

            predicates.add( servicePoint.in(servicePoints) );

            if(eagerly && fetchEmployeeTerms == null) {
                // then fetch associated collection of entities
                fetchEmployeeTerms = employee.fetch("termsOnWorkStation", JoinType.INNER);
            }

        }

        if(workStations != null && workStations.size() > 0) {

            if(employeeTerm == null) employeeTerm = employee.join(Employee_.termsOnWorkStation);

            predicates.add( employeeTerm.get(EmployeeTerm_.workStation).in(workStations) );

            if(eagerly && fetchEmployeeTerms == null) {
                // then fetch associated collection of entities
                fetchEmployeeTerms = employee.fetch("termsOnWorkStation", JoinType.INNER);
            }
        }

        if(period != null) {

            if(employeeTerm == null) employeeTerm = employee.join(Employee_.termsOnWorkStation);
            if(term == null) term = employeeTerm.join(EmployeeTerm_.term);

            if(strictTerm != null && strictTerm) {
                predicates.add( criteriaBuilder.lessThanOrEqualTo( term.get(Term_.openingTime), period.getStartTime()) );
                predicates.add( criteriaBuilder.greaterThanOrEqualTo( term.get(Term_.closingTime), period.getEndTime()) );
            } else {
                predicates.add( criteriaBuilder.lessThan( term.get(Term_.openingTime), period.getEndTime()) );
                predicates.add( criteriaBuilder.greaterThan( term.get(Term_.closingTime), period.getStartTime()) );
            }

            if(eagerly && fetchEmployeeTerms == null) {
                // then fetch associated collection of entities
                fetchEmployeeTerms = employee.fetch("termsOnWorkStation", JoinType.INNER);
            }
        }

        if(terms != null && terms.size() > 0) {

            if(employeeTerm == null) employeeTerm = employee.join(Employee_.termsOnWorkStation);
            if(term == null) term = employeeTerm.join(EmployeeTerm_.term);

            predicates.add( term.in(terms) );

            if(eagerly && fetchEmployeeTerms == null) {
                // then fetch associated collection of entities
                fetchEmployeeTerms = employee.fetch("termsOnWorkStation", JoinType.INNER);
            }
        }

        // take into account that work station must provide given services when searching by (term or work station or service point) and (services or provider services)
        if( (period != null || terms != null || workStations != null || servicePoints != null) && (services != null || providerServices != null) ) {

            if(providerService == null) providerService = employee.join(Employee_.suppliedServices);
            if(employeeTerm == null) employeeTerm = employee.join(Employee_.termsOnWorkStation);
            if(workStation == null) workStation = employeeTerm.join(EmployeeTerm_.workStation);

            predicates.add( criteriaBuilder.isMember(providerService, (workStation.get(WorkStation_.providedServices)) ) );

            if(eagerly && fetchEmployeeTerms == null) {
                // then fetch associated collection of entities
                fetchEmployeeTerms = employee.fetch("termsOnWorkStation", JoinType.INNER);
            }
        }

        if(eagerly && fetchEmployeeTerms == null) {
            // then left fetch associated collection of entities
            fetchEmployeeTerms = employee.fetch("termsOnWorkStation", JoinType.LEFT);
        }

        if(rated != null) {
            Expression<Integer> ratingsCount = criteriaBuilder.size(employee.get(Employee_.receivedRatings));
            if(rated) {
                predicates.add( criteriaBuilder.greaterThan(ratingsCount, 0) );
            } else {
                predicates.add( criteriaBuilder.equal(ratingsCount, 0) );
            }
        }

        Fetch<Employee, EmployeeRating> fetchRatings = null;

        if(minAvgRating != null) {

            if(employeeRating == null) employeeRating = employee.join(Employee_.receivedRatings);

            if(!groupedBy) {
                criteriaQuery.groupBy(employee);
                groupedBy = true;
            }

            final Expression<Double> avgRating = criteriaBuilder.avg(employeeRating.get(EmployeeRating_.clientRating));
            havingPredicates.add( criteriaBuilder.greaterThanOrEqualTo(avgRating, minAvgRating) );

            if(eagerly && fetchRatings == null) {
                // then fetch associated collection of entities
                fetchRatings = employee.fetch("receivedRatings", JoinType.INNER);
            }

        }

        if(maxAvgRating != null) {

            if(employeeRating == null) employeeRating = employee.join(Employee_.receivedRatings);

            if(!groupedBy) {
                criteriaQuery.groupBy(employee);
                groupedBy = true;
            }

            final Expression<Double> avgRating = criteriaBuilder.avg(employeeRating.get(EmployeeRating_.clientRating));
            havingPredicates.add( criteriaBuilder.lessThanOrEqualTo(avgRating, maxAvgRating) );

            if(eagerly && fetchRatings == null) {
                // then fetch associated collection of entities
                fetchRatings = employee.fetch("receivedRatings", JoinType.INNER);
            }
        }

        if(ratingClients != null && ratingClients.size() > 0) {

            if(employeeRating == null) employeeRating = employee.join(Employee_.receivedRatings);
            if(client == null) client = employeeRating.join(EmployeeRating_.client);

            predicates.add( client.in(ratingClients) );

            if(eagerly && fetchRatings == null) {
                // then fetch associated collection of entities
                fetchRatings = employee.fetch("receivedRatings", JoinType.INNER);
            }
        }

        if(eagerly && fetchRatings == null) {
            // then left fetch associated collection of entities
            fetchRatings = employee.fetch("receivedRatings", JoinType.LEFT);
        }

        // WHERE predicate1 AND predicate2 AND ... AND predicateN
        criteriaQuery.where(predicates.toArray(new Predicate[] { }));
        if(groupedBy) {
            // HAVING predicate1 AND predicate2 AND ... AND predicateN
            criteriaQuery.having(havingPredicates.toArray(new Predicate[]{}));
        }


        TypedQuery<Employee> query = getEntityManager().createQuery(criteriaQuery);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }
}
