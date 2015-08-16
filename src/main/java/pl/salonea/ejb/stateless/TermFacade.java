package pl.salonea.ejb.stateless;

import pl.salonea.ejb.interfaces.TermFacadeInterface;
import pl.salonea.entities.*;
import pl.salonea.utils.Period;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.Date;
import java.util.List;

/**
 * Created by michzio on 16/08/2015.
 */
@Stateless
@LocalBean
public class TermFacade extends AbstractFacade<Term>
            implements TermFacadeInterface.Local, TermFacadeInterface.Remote {

    @Inject
    private EntityManager em;

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    public TermFacade() {
        super(Term.class);
    }


    @Override
    public List<Term> findByPeriod(Period period) {
        return null;
    }

    @Override
    public List<Term> findByPeriod(Period period, Integer start, Integer offset) {
        return null;
    }

    @Override
    public List<Term> findByPeriodStrict(Period period) {
        return null;
    }

    @Override
    public List<Term> findByPeriodStrict(Period period, Integer start, Integer offset) {
        return null;
    }

    @Override
    public List<Term> findAfter(Date time) {
        return null;
    }

    @Override
    public List<Term> findAfter(Date time, Integer start, Integer offset) {
        return null;
    }

    @Override
    public List<Term> findAfterStrict(Date time) {
        return null;
    }

    @Override
    public List<Term> findAfterStrict(Date time, Integer start, Integer offset) {
        return null;
    }

    @Override
    public List<Term> findBefore(Date time) {
        return null;
    }

    @Override
    public List<Term> findBefore(Date time, Integer start, Integer offset) {
        return null;
    }

    @Override
    public List<Term> findBeforeStrict(Date time) {
        return null;
    }

    @Override
    public List<Term> findBeforeStrict(Date time, Integer start, Integer offset) {
        return null;
    }

    @Override
    public List<Term> findByEmployee(Employee employee) {
        return null;
    }

    @Override
    public List<Term> findByEmployee(Employee employee, Integer start, Integer offset) {
        return null;
    }

    @Override
    public List<Term> findByWorkStation(WorkStation workStation) {
        return null;
    }

    @Override
    public List<Term> findByWorkStation(WorkStation workStation, Integer start, Integer offset) {
        return null;
    }

    @Override
    public List<Term> findByService(Service service) {
        return null;
    }

    @Override
    public List<Term> findByService(Service service, Integer start, Integer offset) {
        return null;
    }

    @Override
    public List<Term> findByProviderService(ProviderService providerService) {
        return null;
    }

    @Override
    public List<Term> findByProviderService(ProviderService providerService, Integer start, Integer offset) {
        return null;
    }

    @Override
    public List<Term> findByServiceAndEmployee(Service service, Employee employee) {
        return null;
    }

    @Override
    public List<Term> findByServiceAndEmployee(Service service, Employee employee, Integer start, Integer offset) {
        return null;
    }

    @Override
    public List<Term> findByProviderServiceAndEmployee(ProviderService providerService, Employee employee) {
        return null;
    }

    @Override
    public List<Term> findByProviderServiceAndEmployee(ProviderService providerService, Employee employee, Integer start, Integer offset) {
        return null;
    }

    @Override
    public List<Term> findByWorkStationAndEmployee(WorkStation workStation, Employee employee) {
        return null;
    }

    @Override
    public List<Term> findByWorkStationAndEmployee(WorkStation workStation, Employee employee, Integer start, Integer offset) {
        return null;
    }

    @Override
    public List<Term> findByWorkStationAndService(WorkStation workStation, Service service) {
        return null;
    }

    @Override
    public List<Term> findByWorkStationAndService(WorkStation workStation, Service service, Integer start, Integer offset) {
        return null;
    }

    @Override
    public List<Term> findByWorkStationAndProviderService(WorkStation workStation, ProviderService providerService) {
        return null;
    }

    @Override
    public List<Term> findByWorkStationAndProviderService(WorkStation workStation, ProviderService providerService, Integer start, Integer offset) {
        return null;
    }

    @Override
    public List<Term> findByWorkStationAndServiceAndEmployee(WorkStation workStation, Service service, Employee employee) {
        return null;
    }

    @Override
    public List<Term> findByWorkStationAndServiceAndEmployee(WorkStation workStation, Service service, Employee employee, Integer start, Integer offset) {
        return null;
    }

    @Override
    public List<Term> findByWorkStationAndProviderServiceAndEmployee(WorkStation workStation, ProviderService providerService, Employee employee) {
        return null;
    }

    @Override
    public List<Term> findByWorkStationAndProviderServiceAndEmployee(WorkStation workStation, ProviderService providerService, Employee employee, Integer start, Integer offset) {
        return null;
    }

    @Override
    public List<Term> findByServicePoint(ServicePoint servicePoint) {
        return null;
    }

    @Override
    public List<Term> findByServicePoint(ServicePoint servicePoint, Integer start, Integer offset) {
        return null;
    }

    @Override
    public List<Term> findByServicePointAndEmployee(ServicePoint servicePoint, Employee employee) {
        return null;
    }

    @Override
    public List<Term> findByServicePointAndEmployee(ServicePoint servicePoint, Employee employee, Integer start, Integer offset) {
        return null;
    }

    @Override
    public List<Term> findByServicePointAndService(ServicePoint servicePoint, Service service) {
        return null;
    }

    @Override
    public List<Term> findByServicePointAndService(ServicePoint servicePoint, Service service, Integer start, Integer offset) {
        return null;
    }

    @Override
    public List<Term> findByServicePointAndProviderService(ServicePoint servicePoint, ProviderService providerService) {
        return null;
    }

    @Override
    public List<Term> findByServicePointAndProviderService(ServicePoint servicePoint, ProviderService providerService, Integer start, Integer offset) {
        return null;
    }

    @Override
    public List<Term> findByServicePointAndServiceAndEmployee(ServicePoint servicePoint, Service service, Employee employee) {
        return null;
    }

    @Override
    public List<Term> findByServicePointAndServiceAndEmployee(ServicePoint servicePoint, Service service, Employee employee, Integer start, Integer offset) {
        return null;
    }

    @Override
    public List<Term> findByServicePointAndProviderServiceAndEmployee(ServicePoint servicePoint, ProviderService providerService, Employee employee) {
        return null;
    }

    @Override
    public List<Term> findByServicePointAndProviderServiceAndEmployee(ServicePoint servicePoint, ProviderService providerService, Employee employee, Integer start, Integer offset) {
        return null;
    }

    @Override
    public List<Term> findByMultipleCriteria(List<ServicePoint> servicePoints, List<WorkStation> workStations, List<Employee> employees, List<Service> services, List<ProviderService> providerServices, Period period, Boolean strictTerm) {
        return null;
    }

    @Override
    public List<Term> findByMultipleCriteria(List<ServicePoint> servicePoints, List<WorkStation> workStations, List<Employee> employees, List<Service> services, List<ProviderService> providerServices, Period period, Boolean strictTerm, Integer start, Integer offset) {
        return null;
    }

    @Override
    public Integer deleteOlderThan(Date time) {
        return null;
    }
}
