package pl.salonea.ejb.stateless;

import pl.salonea.ejb.interfaces.WorkStationFacadeInterface;
import pl.salonea.entities.*;
import pl.salonea.entities.idclass.ServicePointId;
import pl.salonea.entities.idclass.WorkStationId;
import pl.salonea.enums.WorkStationType;
import pl.salonea.utils.Period;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.*;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by michzio on 09/08/2015.
 */
@Stateless
@LocalBean
public class WorkStationFacade extends AbstractFacade<WorkStation> implements WorkStationFacadeInterface.Local, WorkStationFacadeInterface.Remote {

    @Inject
    private EntityManager em;

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    public WorkStationFacade() {
        super(WorkStation.class);
    }

    @Override
    public List<WorkStation> find(List<Object> workStationIds) {

        if(workStationIds == null || workStationIds.size() == 0)
            throw new IllegalArgumentException("The workStationIds argument must be not empty list.");

        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<WorkStation> criteriaQuery = criteriaBuilder.createQuery(WorkStation.class);
        // FROM
        Root<WorkStation> workStation = criteriaQuery.from(WorkStation.class);
        // SELECT
        criteriaQuery.select(workStation);

        // INNER JOIN
        Join<WorkStation, ServicePoint> servicePoint = workStation.join(WorkStation_.servicePoint);
        Join<ServicePoint, Provider> provider = servicePoint.join(ServicePoint_.provider);

        // WHERE PREDICATES on composite primary key
        List<Predicate> orPredicates = new ArrayList<>();

        for( Object object : workStationIds ) {

            if( !(object instanceof WorkStationId) )
                throw new IllegalArgumentException("The workStationIds argument should be list of WorkStationId typed objects.");

            WorkStationId workStationId = (WorkStationId) object;

            Predicate[] andPredicates = new Predicate[3];
            andPredicates[0] = criteriaBuilder.equal( provider.get(Provider_.userId), workStationId.getServicePoint().getProvider() );
            andPredicates[1] = criteriaBuilder.equal( servicePoint.get(ServicePoint_.servicePointNumber), workStationId.getServicePoint().getServicePointNumber() );
            andPredicates[2] = criteriaBuilder.equal( workStation.get(WorkStation_.workStationNumber), workStationId.getWorkStationNumber() );

            orPredicates.add( criteriaBuilder.and(andPredicates) );

        }

        // WHERE compositePK1 OR compositePK2 OR ... OR compositePK3
        criteriaQuery.where( criteriaBuilder.or(orPredicates.toArray(new Predicate[] {})) );

        TypedQuery<WorkStation> query = getEntityManager().createQuery(criteriaQuery);
        return query.getResultList();
    }

    @Override
    public WorkStation createForServicePoint(ServicePointId servicePointId, WorkStation workStation) {

        ServicePoint foundServicePoint = getEntityManager().find(ServicePoint.class, servicePointId);
        workStation.setServicePoint(foundServicePoint);

        return create(workStation);
    }

    @Override
    public WorkStation update(WorkStationId workStationId, WorkStation workStation) {

        return update(workStationId, workStation, true);
    }

    @Override
    public WorkStation update(WorkStationId workStationId, WorkStation workStation, Boolean retainTransientFields) {

        ServicePoint foundServicePoint = getEntityManager().find(ServicePoint.class, workStationId.getServicePoint());
        workStation.setServicePoint(foundServicePoint);
        workStation.setWorkStationNumber(workStationId.getWorkStationNumber());

        if(retainTransientFields) {
            // keep current collection attributes of resource (and other marked @XmlTransient)
            WorkStation currentWorkStation = findByIdEagerly(workStationId);
            if(currentWorkStation != null) {
                workStation.setProvidedServices(currentWorkStation.getProvidedServices());
                workStation.setTermsEmployeesWorkOn(currentWorkStation.getTermsEmployeesWorkOn());
            }
        }
        return update(workStation);
    }

    @Override
    public List<WorkStation> findAllEagerly() {
        return findAllEagerly(null, null);
    }

    @Override
    public List<WorkStation> findAllEagerly(Integer start, Integer limit) {

        TypedQuery<WorkStation> query = getEntityManager().createNamedQuery(WorkStation.FIND_ALL_EAGERLY, WorkStation.class);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public WorkStation findByIdEagerly(WorkStationId workStationId) {

        TypedQuery<WorkStation> query = getEntityManager().createNamedQuery(WorkStation.FIND_BY_ID_EAGERLY, WorkStation.class);
        query.setParameter("userId", workStationId.getServicePoint().getProvider());
        query.setParameter("servicePointNumber", workStationId.getServicePoint().getServicePointNumber());
        query.setParameter("workStationNumber", workStationId.getWorkStationNumber());
        try {
            return query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            return null;
        }
    }

    @Override
    public List<WorkStation> findByType(WorkStationType type) {
        return findByType(type, null, null);
    }

    @Override
    public List<WorkStation> findByType(WorkStationType type, Integer start, Integer limit) {

        TypedQuery<WorkStation> query = getEntityManager().createNamedQuery(WorkStation.FIND_BY_TYPE, WorkStation.class);
        query.setParameter("work_station_type", type);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<WorkStation> findByTerm(Date startTime, Date endTime) {
        return findByTerm(startTime, endTime, null, null);
    }

    @Override
    public List<WorkStation> findByTerm(Date startTime, Date endTime, Integer start, Integer limit) {

        TypedQuery<WorkStation> query = getEntityManager().createNamedQuery(WorkStation.FIND_BY_PERIOD, WorkStation.class);
        query.setParameter("start_time", startTime);
        query.setParameter("end_time", endTime);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<WorkStation> findByTermStrict(Date startTime, Date endTime) {
        return findByTermStrict(startTime, endTime, null, null);
    }

    @Override
    public List<WorkStation> findByTermStrict(Date startTime, Date endTime, Integer start, Integer limit) {

        TypedQuery<WorkStation> query = getEntityManager().createNamedQuery(WorkStation.FIND_BY_PERIOD_STRICT, WorkStation.class);
        query.setParameter("start_time", startTime);
        query.setParameter("end_time", endTime);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<WorkStation> findByTerm(Term term) {
        return findByTerm(term, null, null);
    }

    @Override
    public List<WorkStation> findByTerm(Term term, Integer start, Integer limit) {

        TypedQuery<WorkStation> query = getEntityManager().createNamedQuery(WorkStation.FIND_BY_TERM, WorkStation.class);
        query.setParameter("term", term);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<WorkStation> findByTermEagerly(Term term) {
        return findByTermEagerly(term, null, null);
    }

    @Override
    public List<WorkStation> findByTermEagerly(Term term, Integer start, Integer limit) {

        TypedQuery<WorkStation> query = getEntityManager().createNamedQuery(WorkStation.FIND_BY_TERM_EAGERLY, WorkStation.class);
        query.setParameter("term", term);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<WorkStation> findByServicePoint(ServicePoint servicePoint) {
        return findByServicePoint(servicePoint, null, null);
    }

    @Override
    public List<WorkStation> findByServicePoint(ServicePoint servicePoint, Integer start, Integer limit) {

        TypedQuery<WorkStation> query = getEntityManager().createNamedQuery(WorkStation.FIND_BY_SERVICE_POINT, WorkStation.class);
        query.setParameter("service_point", servicePoint);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<WorkStation> findByServicePointEagerly(ServicePoint servicePoint) {
        return findByServicePointEagerly(servicePoint, null, null);
    }

    @Override
    public List<WorkStation> findByServicePointEagerly(ServicePoint servicePoint, Integer start, Integer limit) {

        TypedQuery<WorkStation> query = getEntityManager().createNamedQuery(WorkStation.FIND_BY_SERVICE_POINT_EAGERLY, WorkStation.class);
        query.setParameter("service_point", servicePoint);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<WorkStation> findByServicePointAndType(ServicePoint servicePoint, WorkStationType type) {
        return findByServicePointAndType(servicePoint, type, null, null);
    }

    @Override
    public List<WorkStation> findByServicePointAndType(ServicePoint servicePoint, WorkStationType type, Integer start, Integer limit) {

        TypedQuery<WorkStation> query = getEntityManager().createNamedQuery(WorkStation.FIND_BY_SERVICE_POINT_AND_TYPE, WorkStation.class);
        query.setParameter("service_point", servicePoint);
        query.setParameter("work_station_type", type);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<WorkStation> findByServicePointAndTerm(ServicePoint servicePoint, Date startTime, Date endTime) {
        return findByServicePointAndTerm(servicePoint, startTime, endTime, null, null);
    }

    @Override
    public List<WorkStation> findByServicePointAndTerm(ServicePoint servicePoint, Date startTime, Date endTime, Integer start, Integer limit) {

        TypedQuery<WorkStation> query = getEntityManager().createNamedQuery(WorkStation.FIND_BY_SERVICE_POINT_AND_TERM, WorkStation.class);
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
    public List<WorkStation> findByServicePointAndTermStrict(ServicePoint servicePoint, Date startTime, Date endTime) {
        return findByServicePointAndTermStrict(servicePoint, startTime, endTime, null, null);
    }

    @Override
    public List<WorkStation> findByServicePointAndTermStrict(ServicePoint servicePoint, Date startTime, Date endTime, Integer start, Integer limit) {

        TypedQuery<WorkStation> query = getEntityManager().createNamedQuery(WorkStation.FIND_BY_SERVICE_POINT_AND_TERM_STRICT, WorkStation.class);
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
    public List<WorkStation> findByService(Service service) {
        return findByService(service, null, null);
    }

    @Override
    public List<WorkStation> findByService(Service service, Integer start, Integer limit) {

        TypedQuery<WorkStation> query = getEntityManager().createNamedQuery(WorkStation.FIND_BY_SERVICE, WorkStation.class);
        query.setParameter("service", service);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<WorkStation> findByServiceEagerly(Service service) {
        return findByServiceEagerly(service, null, null);
    }

    @Override
    public List<WorkStation> findByServiceEagerly(Service service, Integer start, Integer limit) {

        TypedQuery<WorkStation> query = getEntityManager().createNamedQuery(WorkStation.FIND_BY_SERVICE_EAGERLY, WorkStation.class);
        query.setParameter("service", service);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<WorkStation> findByServiceAndTerm(Service service, Date startTime, Date endTime) {
        return findByServiceAndTerm(service, startTime, endTime, null, null);
    }

    @Override
    public List<WorkStation> findByServiceAndTerm(Service service, Date startTime, Date endTime, Integer start, Integer limit) {

        TypedQuery<WorkStation> query = getEntityManager().createNamedQuery(WorkStation.FIND_BY_SERVICE_AND_TERM, WorkStation.class);
        query.setParameter("service", service);
        query.setParameter("start_time", startTime);
        query.setParameter("end_time", endTime);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<WorkStation> findByServiceAndTermStrict(Service service, Date startTime, Date endTime) {
        return findByServiceAndTermStrict(service, startTime, endTime, null, null);
    }

    @Override
    public List<WorkStation> findByServiceAndTermStrict(Service service, Date startTime, Date endTime, Integer start, Integer limit) {

        TypedQuery<WorkStation> query = getEntityManager().createNamedQuery(WorkStation.FIND_BY_SERVICE_AND_TERM_STRICT, WorkStation.class);
        query.setParameter("service", service);
        query.setParameter("start_time", startTime);
        query.setParameter("end_time", endTime);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<WorkStation> findByServiceAndServicePoint(Service service, ServicePoint servicePoint) {
        return findByServiceAndServicePoint(service, servicePoint, null, null);
    }

    @Override
    public List<WorkStation> findByServiceAndServicePoint(Service service, ServicePoint servicePoint, Integer start, Integer limit) {

        TypedQuery<WorkStation> query = getEntityManager().createNamedQuery(WorkStation.FIND_BY_SERVICE_AND_SERVICE_POINT, WorkStation.class);
        query.setParameter("service", service);
        query.setParameter("service_point", servicePoint);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<WorkStation> findByServiceAndServicePointAndTerm(Service service, ServicePoint servicePoint, Date startTime, Date endTime) {
        return findByServiceAndServicePointAndTerm(service, servicePoint, startTime, endTime, null, null);
    }

    @Override
    public List<WorkStation> findByServiceAndServicePointAndTerm(Service service, ServicePoint servicePoint, Date startTime, Date endTime, Integer start, Integer limit) {

        TypedQuery<WorkStation> query = getEntityManager().createNamedQuery(WorkStation.FIND_BY_SERVICE_AND_SERVICE_POINT_AND_TERM, WorkStation.class);
        query.setParameter("service", service);
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
    public List<WorkStation> findByServiceAndServicePointAndTermStrict(Service service, ServicePoint servicePoint, Date startTime, Date endTime) {
        return findByServiceAndServicePointAndTermStrict(service, servicePoint, startTime, endTime, null, null);
    }

    @Override
    public List<WorkStation> findByServiceAndServicePointAndTermStrict(Service service, ServicePoint servicePoint, Date startTime, Date endTime, Integer start, Integer limit) {

        TypedQuery<WorkStation> query = getEntityManager().createNamedQuery(WorkStation.FIND_BY_SERVICE_AND_SERVICE_POINT_AND_TERM_STRICT, WorkStation.class);
        query.setParameter("service", service);
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
    public List<WorkStation> findByProviderService(ProviderService providerService) {
        return findByProviderService(providerService, null, null);
    }

    @Override
    public List<WorkStation> findByProviderService(ProviderService providerService, Integer start, Integer limit) {

        TypedQuery<WorkStation> query = getEntityManager().createNamedQuery(WorkStation.FIND_BY_PROVIDER_SERVICE, WorkStation.class);
        query.setParameter("provider_service", providerService);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<WorkStation> findByProviderServiceEagerly(ProviderService providerService) {
        return findByProviderServiceEagerly(providerService, null, null);
    }

    @Override
    public List<WorkStation> findByProviderServiceEagerly(ProviderService providerService, Integer start, Integer limit) {

        TypedQuery<WorkStation> query = getEntityManager().createNamedQuery(WorkStation.FIND_BY_PROVIDER_SERVICE_EAGERLY, WorkStation.class);
        query.setParameter("provider_service", providerService);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<WorkStation> findByProviderServiceAndTerm(ProviderService providerService, Date startTime, Date endTime) {
        return findByProviderServiceAndTerm(providerService, startTime, endTime, null, null);
    }

    @Override
    public List<WorkStation> findByProviderServiceAndTerm(ProviderService providerService, Date startTime, Date endTime, Integer start, Integer limit) {

        TypedQuery<WorkStation> query = getEntityManager().createNamedQuery(WorkStation.FIND_BY_PROVIDER_SERVICE_AND_TERM, WorkStation.class);
        query.setParameter("provider_service", providerService);
        query.setParameter("start_time", startTime);
        query.setParameter("end_time", endTime);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<WorkStation> findByProviderServiceAndTermStrict(ProviderService providerService, Date startTime, Date endTime) {
        return findByProviderServiceAndTermStrict(providerService, startTime, endTime, null, null);
    }

    @Override
    public List<WorkStation> findByProviderServiceAndTermStrict(ProviderService providerService, Date startTime, Date endTime, Integer start, Integer limit) {

        TypedQuery<WorkStation> query = getEntityManager().createNamedQuery(WorkStation.FIND_BY_PROVIDER_SERVICE_AND_TERM_STRICT, WorkStation.class);
        query.setParameter("provider_service", providerService);
        query.setParameter("start_time", startTime);
        query.setParameter("end_time", endTime);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<WorkStation> findByProviderServiceAndServicePoint(ProviderService providerService, ServicePoint servicePoint) {
        return findByProviderServiceAndServicePoint(providerService, servicePoint, null, null);
    }

    @Override
    public List<WorkStation> findByProviderServiceAndServicePoint(ProviderService providerService, ServicePoint servicePoint, Integer start, Integer limit) {

        TypedQuery<WorkStation> query = getEntityManager().createNamedQuery(WorkStation.FIND_BY_PROVIDER_SERVICE_AND_SERVICE_POINT, WorkStation.class);
        query.setParameter("provider_service", providerService);
        query.setParameter("service_point", servicePoint);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<WorkStation> findByProviderServiceAndServicePointAndTerm(ProviderService providerService, ServicePoint servicePoint, Date startTime, Date endTime) {
        return findByProviderServiceAndServicePointAndTerm(providerService, servicePoint, startTime, endTime, null, null);
    }

    @Override
    public List<WorkStation> findByProviderServiceAndServicePointAndTerm(ProviderService providerService, ServicePoint servicePoint, Date startTime, Date endTime, Integer start, Integer limit) {

        TypedQuery<WorkStation> query = getEntityManager().createNamedQuery(WorkStation.FIND_BY_PROVIDER_SERVICE_AND_SERVICE_POINT_AND_TERM, WorkStation.class);
        query.setParameter("provider_service", providerService);
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
    public List<WorkStation> findByProviderServiceAndServicePointAndTermStrict(ProviderService providerService, ServicePoint servicePoint, Date startTime, Date endTime) {
        return findByProviderServiceAndServicePointAndTermStrict(providerService, servicePoint, startTime, endTime, null, null);
    }

    @Override
    public List<WorkStation> findByProviderServiceAndServicePointAndTermStrict(ProviderService providerService, ServicePoint servicePoint, Date startTime, Date endTime, Integer start, Integer limit) {

        TypedQuery<WorkStation> query = getEntityManager().createNamedQuery(WorkStation.FIND_BY_PROVIDER_SERVICE_AND_SERVICE_POINT_AND_TERM_STRICT, WorkStation.class);
        query.setParameter("provider_service", providerService);
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
    public List<WorkStation> findByEmployee(Employee employee) {
        return findByEmployee(employee, null, null);
    }

    @Override
    public List<WorkStation> findByEmployee(Employee employee, Integer start, Integer limit) {

        TypedQuery<WorkStation> query = getEntityManager().createNamedQuery(WorkStation.FIND_BY_EMPLOYEE, WorkStation.class);
        query.setParameter("employee", employee);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<WorkStation> findByEmployeeEagerly(Employee employee) {
        return findByEmployeeEagerly(employee, null, null);
    }

    @Override
    public List<WorkStation> findByEmployeeEagerly(Employee employee, Integer start, Integer limit) {

        TypedQuery<WorkStation> query = getEntityManager().createNamedQuery(WorkStation.FIND_BY_EMPLOYEE_EAGERLY, WorkStation.class);
        query.setParameter("employee", employee);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<WorkStation> findByEmployeeAndTerm(Employee employee, Date startTime, Date endTime) {
        return findByEmployeeAndTerm(employee, startTime, endTime, null, null);
    }

    @Override
    public List<WorkStation> findByEmployeeAndTerm(Employee employee, Date startTime, Date endTime, Integer start, Integer limit) {

        TypedQuery<WorkStation> query = getEntityManager().createNamedQuery(WorkStation.FIND_BY_EMPLOYEE_AND_TERM, WorkStation.class);
        query.setParameter("employee", employee);
        query.setParameter("start_time", startTime);
        query.setParameter("end_time", endTime);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<WorkStation> findByEmployeeAndTermStrict(Employee employee, Date startTime, Date endTime) {
        return findByEmployeeAndTermStrict(employee, startTime, endTime, null, null);
    }

    @Override
    public List<WorkStation> findByEmployeeAndTermStrict(Employee employee, Date startTime, Date endTime, Integer start, Integer limit) {

        TypedQuery<WorkStation> query = getEntityManager().createNamedQuery(WorkStation.FIND_BY_EMPLOYEE_AND_TERM_STRICT, WorkStation.class);
        query.setParameter("employee", employee);
        query.setParameter("start_time", startTime);
        query.setParameter("end_time", endTime);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<WorkStation> findByEmployeeAndServicePoint(Employee employee, ServicePoint servicePoint) {
        return findByEmployeeAndServicePoint(employee, servicePoint, null, null);
    }

    @Override
    public List<WorkStation> findByEmployeeAndServicePoint(Employee employee, ServicePoint servicePoint, Integer start, Integer limit) {
        TypedQuery<WorkStation> query = getEntityManager().createNamedQuery(WorkStation.FIND_BY_EMPLOYEE_AND_SERVICE_POINT, WorkStation.class);
        query.setParameter("employee", employee);
        query.setParameter("service_point", servicePoint);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<WorkStation> findByEmployeeAndServicePointAndTerm(Employee employee, ServicePoint servicePoint, Date startTime, Date endTime) {
        return findByEmployeeAndServicePointAndTerm(employee, servicePoint, startTime, endTime, null, null);
    }

    @Override
    public List<WorkStation> findByEmployeeAndServicePointAndTerm(Employee employee, ServicePoint servicePoint, Date startTime, Date endTime, Integer start, Integer limit) {

        TypedQuery<WorkStation> query = getEntityManager().createNamedQuery(WorkStation.FIND_BY_EMPLOYEE_AND_SERVICE_POINT_AND_TERM, WorkStation.class);
        query.setParameter("employee", employee);
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
    public List<WorkStation> findByEmployeeAndServicePointAndTermStrict(Employee employee, ServicePoint servicePoint, Date startTime, Date endTime) {
        return findByEmployeeAndServicePointAndTermStrict(employee, servicePoint, startTime, endTime, null, null);
    }

    @Override
    public List<WorkStation> findByEmployeeAndServicePointAndTermStrict(Employee employee, ServicePoint servicePoint, Date startTime, Date endTime, Integer start, Integer limit) {

        TypedQuery<WorkStation> query = getEntityManager().createNamedQuery(WorkStation.FIND_BY_EMPLOYEE_AND_SERVICE_POINT_AND_TERM_STRICT, WorkStation.class);
        query.setParameter("employee", employee);
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
    public List<WorkStation> findByEmployeeAndService(Employee employee, Service service) {
        return findByEmployeeAndService(employee, service, null, null);
    }

    @Override
    public List<WorkStation> findByEmployeeAndService(Employee employee, Service service, Integer start, Integer limit) {

        TypedQuery<WorkStation> query = getEntityManager().createNamedQuery(WorkStation.FIND_BY_EMPLOYEE_AND_SERVICE, WorkStation.class);
        query.setParameter("employee", employee);
        query.setParameter("service", service);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<WorkStation> findByEmployeeAndServiceAndTerm(Employee employee, Service service, Date startTime, Date endTime) {
        return findByEmployeeAndServiceAndTerm(employee, service, startTime, endTime, null, null);
    }

    @Override
    public List<WorkStation> findByEmployeeAndServiceAndTerm(Employee employee, Service service, Date startTime, Date endTime, Integer start, Integer limit) {

        TypedQuery<WorkStation> query = getEntityManager().createNamedQuery(WorkStation.FIND_BY_EMPLOYEE_AND_SERVICE_AND_TERM, WorkStation.class);
        query.setParameter("employee", employee);
        query.setParameter("service", service);
        query.setParameter("start_time", startTime);
        query.setParameter("end_time", endTime);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<WorkStation> findByEmployeeAndServiceAndTermStrict(Employee employee, Service service, Date startTime, Date endTime) {
        return findByEmployeeAndServiceAndTermStrict(employee, service, startTime, endTime, null, null);
    }

    @Override
    public List<WorkStation> findByEmployeeAndServiceAndTermStrict(Employee employee, Service service, Date startTime, Date endTime, Integer start, Integer limit) {

        TypedQuery<WorkStation> query = getEntityManager().createNamedQuery(WorkStation.FIND_BY_EMPLOYEE_AND_SERVICE_AND_TERM_STRICT, WorkStation.class);
        query.setParameter("employee", employee);
        query.setParameter("service", service);
        query.setParameter("start_time", startTime);
        query.setParameter("end_time", endTime);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public Integer deleteByServicePoint(ServicePoint servicePoint) {

        Query query = getEntityManager().createNamedQuery(WorkStation.DELETE_BY_SERVICE_POINT);
        query.setParameter("service_point", servicePoint);
        return query.executeUpdate();
    }

    @Override
    public Integer deleteById(WorkStationId workStationId) {

        ServicePoint servicePoint = getEntityManager().find(ServicePoint.class, workStationId.getServicePoint());

        Query query = getEntityManager().createNamedQuery(WorkStation.DELETE_BY_ID);
        query.setParameter("servicePoint", servicePoint);
        query.setParameter("workStationNumber", workStationId.getWorkStationNumber());
        return query.executeUpdate();
    }

    @Override
    public Long countByServicePoint(ServicePoint servicePoint) {

        TypedQuery<Long> query = getEntityManager().createNamedQuery(WorkStation.COUNT_BY_SERVICE_POINT, Long.class);
        query.setParameter("service_point", servicePoint);
        return query.getSingleResult();
    }

    @Override
    public Long countByService(Service service) {

        TypedQuery<Long> query = getEntityManager().createNamedQuery(WorkStation.COUNT_BY_SERVICE, Long.class);
        query.setParameter("service", service);
        return query.getSingleResult();
    }

    @Override
    public Long countByProviderService(ProviderService providerService) {

        TypedQuery<Long> query = getEntityManager().createNamedQuery(WorkStation.COUNT_BY_PROVIDER_SERVICE, Long.class);
        query.setParameter("provider_service", providerService);
        return query.getSingleResult();
    }

    @Override
    public Long countByEmployee(Employee employee) {

        TypedQuery<Long> query = getEntityManager().createNamedQuery(WorkStation.COUNT_BY_EMPLOYEE, Long.class);
        query.setParameter("employee", employee);
        return query.getSingleResult();
    }

    @Override
    public Long countByTerm(Term term) {

        TypedQuery<Long> query = getEntityManager().createNamedQuery(WorkStation.COUNT_BY_TERM, Long.class);
        query.setParameter("term", term);
        return query.getSingleResult();
    }

    @Override
    public List<WorkStation> findByMultipleCriteria(List<ServicePoint> servicePoints, List<Service> services, List<ProviderService> providerServices, List<Employee> employees, List<WorkStationType> types, Period period, Boolean strictTerm, List<Term> terms) {
        return findByMultipleCriteria(servicePoints, services, providerServices, employees, types, period, strictTerm, terms, null, null);
    }

    @Override
    public List<WorkStation> findByMultipleCriteria(List<ServicePoint> servicePoints, List<Service> services, List<ProviderService> providerServices, List<Employee> employees, List<WorkStationType> types, Period period, Boolean strictTerm, List<Term> terms, Integer start, Integer limit) {
        return findByMultipleCriteria(servicePoints, services, providerServices, employees, types, period, strictTerm, terms, false, start, limit);
    }

    @Override
    public List<WorkStation> findByMultipleCriteriaEagerly(List<ServicePoint> servicePoints, List<Service> services, List<ProviderService> providerServices, List<Employee> employees, List<WorkStationType> types, Period period, Boolean strictTerm, List<Term> terms) {
        return findByMultipleCriteriaEagerly(servicePoints, services, providerServices, employees, types, period, strictTerm, terms, null, null);
    }

    @Override
    public List<WorkStation> findByMultipleCriteriaEagerly(List<ServicePoint> servicePoints, List<Service> services, List<ProviderService> providerServices, List<Employee> employees, List<WorkStationType> types, Period period, Boolean strictTerm, List<Term> terms, Integer start, Integer limit) {
        return findByMultipleCriteria(servicePoints, services, providerServices, employees, types, period, strictTerm, terms, true, start, limit);
    }


    private List<WorkStation> findByMultipleCriteria(List<ServicePoint> servicePoints, List<Service> services, List<ProviderService> providerServices, List<Employee> employees, List<WorkStationType> types, Period period, Boolean strictTerm, List<Term> terms, Boolean eagerly, Integer start, Integer limit) {

        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<WorkStation> criteriaQuery = criteriaBuilder.createQuery(WorkStation.class);
        // FROM
        Root<WorkStation> workStation = criteriaQuery.from(WorkStation.class);
        // SELECT
        criteriaQuery.select(workStation).distinct(true);

        // INNER JOIN-s
        Join<WorkStation, ServicePoint> servicePoint = null;
        Join<WorkStation, ProviderService> providerService = null;
        Join<ProviderService, Service> service = null;
        Join<WorkStation, EmployeeTerm> employeeTerm = null;
        Join<EmployeeTerm, Term> term = null;
        Join<EmployeeTerm, Employee> employee = null;
        // Join<Employee, ProviderService> employeeProviderService = null;

        // WHERE PREDICATES
        List<Predicate> predicates = new ArrayList<>();

        if(servicePoints != null && servicePoints.size() > 0) {

            if(servicePoint == null) servicePoint = workStation.join(WorkStation_.servicePoint);

            predicates.add( servicePoint.in(servicePoints) );
        }

        if(services != null && services.size() > 0) {

            if(providerService == null) providerService = workStation.join(WorkStation_.providedServices);
            if(service == null) service = providerService.join(ProviderService_.service);

            predicates.add( service.in(services) );
        }

        if(providerServices != null && providerServices.size() > 0) {

            if(providerService == null) providerService = workStation.join(WorkStation_.providedServices);

            predicates.add( providerService.in(providerServices) );
        }

        if(employees != null && employees.size() > 0) {

            if(employeeTerm == null) employeeTerm = workStation.join(WorkStation_.termsEmployeesWorkOn);
            if(employee == null) employee = employeeTerm.join(EmployeeTerm_.employee);

            predicates.add( employee.in(employees) );
        }

        if(types != null && types.size() > 0) {
            predicates.add( workStation.get(WorkStation_.workStationType).in(types) );
        }

        if(period != null) {

            if(employeeTerm == null) employeeTerm = workStation.join(WorkStation_.termsEmployeesWorkOn);
            if(term == null) term = employeeTerm.join(EmployeeTerm_.term);

            if(strictTerm != null && strictTerm) {
                predicates.add( criteriaBuilder.lessThanOrEqualTo( term.get(Term_.openingTime), period.getStartTime()) );
                predicates.add( criteriaBuilder.greaterThanOrEqualTo( term.get(Term_.closingTime), period.getEndTime()) );
            } else {
                predicates.add( criteriaBuilder.lessThan( term.get(Term_.openingTime), period.getEndTime()) );
                predicates.add( criteriaBuilder.greaterThan( term.get(Term_.closingTime), period.getStartTime()) );
            }
        }

        if(terms != null && terms.size() > 0) {

            if(employeeTerm == null) employeeTerm = workStation.join(WorkStation_.termsEmployeesWorkOn);
            if(term == null) term = employeeTerm.join(EmployeeTerm_.term);

            predicates.add( term.in(terms) );
        }

        // take into account that employee must supply given provider services when searching by (term or employee) and services
        // if( (employees != null && services != null) || (employees != null && providerServices != null)
        //        || (services != null && period != null) || (providerServices != null && period != null)
        //        || (terms != null && providerServices != null) || (terms != null && services != null)) {
        if( (employees != null || period != null || terms != null) && (providerServices != null || services != null) ) {

            if(providerService == null) providerService = workStation.join(WorkStation_.providedServices);
            if(employeeTerm == null) employeeTerm = workStation.join(WorkStation_.termsEmployeesWorkOn);
            if(employee == null) employee = employeeTerm.join(EmployeeTerm_.employee);
            // if(employeeProviderService == null) employeeProviderService = employee.join(Employee_.suppliedServices);

            predicates.add( criteriaBuilder.isMember(providerService, (employee.get(Employee_.suppliedServices)) ) );
        }

        if(eagerly) {
            // then (left) fetch associated collections of entities

            if(providerService != null) {
                workStation.fetch("providedServices", JoinType.INNER);
            } else {
                workStation.fetch("providedServices", JoinType.LEFT);
            }

            if(employeeTerm != null) {
                workStation.fetch("termsEmployeesWorkOn", JoinType.INNER);
            } else {
                workStation.fetch("termsEmployeesWorkOn", JoinType.LEFT);
            }
        }

        // WHERE predicate1 AND predicate2 AND ... AND predicateN
        criteriaQuery.where(predicates.toArray(new Predicate[] { }));

        TypedQuery<WorkStation> query = getEntityManager().createQuery(criteriaQuery);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }
}
