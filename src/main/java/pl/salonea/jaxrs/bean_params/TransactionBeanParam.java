package pl.salonea.jaxrs.bean_params;

import pl.salonea.ejb.stateless.*;
import pl.salonea.entities.*;
import pl.salonea.entities.idclass.ProviderServiceId;
import pl.salonea.entities.idclass.ServicePointId;
import pl.salonea.entities.idclass.WorkStationId;
import pl.salonea.enums.CurrencyCode;
import pl.salonea.jaxrs.exceptions.NotFoundException;
import pl.salonea.jaxrs.utils.RESTDateTime;

import javax.inject.Inject;
import javax.ws.rs.QueryParam;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by michzio on 27/04/2016.
 */
public class TransactionBeanParam extends PaginationBeanParam {

    private @QueryParam("clientId") List<Long> clientIds;
    private @QueryParam("providerId") List<Long> providerIds;
    private @QueryParam("serviceId") List<Integer> serviceIds;
    private @QueryParam("servicePointId") List<ServicePointId> servicePointIds; // {providerId}+{servicePointNumber} composite PK
    private @QueryParam("workStationId") List<WorkStationId> workStationIds; // {providerId}+{servicePointNumber}+{workStationNumber} composite PK
    private @QueryParam("employeeId") List<Long> employeeIds;
    private @QueryParam("providerServiceId") List<ProviderServiceId> providerServiceIds; // {providerId}+{serviceId} composite PK
    private @QueryParam("transactionTimeAfter") RESTDateTime transactionTimeAfter;
    private @QueryParam("transactionTimeBefore") RESTDateTime transactionTimeBefore;
    private @QueryParam("bookedTimeAfter") RESTDateTime bookedTimeAfter;
    private @QueryParam("bookedTimeBefore") RESTDateTime bookedTimeBefore;
    private @QueryParam("termId") List<Long> termIds;
    private @QueryParam("minPrice") Double minPrice;
    private @QueryParam("maxPrice") Double maxPrice;
    private @QueryParam("currencyCode") List<CurrencyCode> currencyCodes;
    private @QueryParam("paymentMethodId") List<Integer> paymentMethodIds;
    private @QueryParam("paid") Boolean paid;

    @Inject
    private ClientFacade clientFacade;
    @Inject
    private ProviderFacade providerFacade;
    @Inject
    private ServiceFacade serviceFacade;
    @Inject
    private ServicePointFacade servicePointFacade;
    @Inject
    private WorkStationFacade workStationFacade;
    @Inject
    private EmployeeFacade employeeFacade;
    @Inject
    private ProviderServiceFacade providerServiceFacade;
    @Inject
    private TermFacade termFacade;
    @Inject
    private PaymentMethodFacade paymentMethodFacade;

    public List<Long> getClientIds() {
        return clientIds;
    }

    public void setClientIds(List<Long> clientIds) {
        this.clientIds = clientIds;
    }

    public List<Client> getClients() throws NotFoundException {
        if(getClientIds() != null && getClientIds().size() > 0) {
            final List<Client> clients = clientFacade.find( new ArrayList<>(getClientIds()) );
            if(clients.size() != getClientIds().size()) throw new NotFoundException("Could not find clients for all provided ids.");
            return clients;
        }
        return null;
    }

    public List<Long> getProviderIds() {
        return providerIds;
    }

    public void setProviderIds(List<Long> providerIds) {
        this.providerIds = providerIds;
    }

    public List<Provider> getProviders() throws NotFoundException {
        if(getProviderIds() != null && getProviderIds().size() > 0) {
            final List<Provider> providers = providerFacade.find( new ArrayList<>(getProviderIds()) );
            if(providers.size() != getProviderIds().size()) throw new NotFoundException("Could not find providers for all provided ids.");
            return providers;
        }
        return null;
    }

    public List<Integer> getServiceIds() {
        return serviceIds;
    }

    public void setServiceIds(List<Integer> serviceIds) {
        this.serviceIds = serviceIds;
    }

    public List<Service> getServices() throws NotFoundException {
        if(getServiceIds() != null && getServiceIds().size() > 0) {
            final List<Service> services = serviceFacade.find( new ArrayList<>(getServiceIds()) );
            if(services.size() != getServiceIds().size()) throw new NotFoundException("Could not find services for all provided ids.");
            return services;
        }
        return null;
    }

    public List<ServicePointId> getServicePointIds() {
        return servicePointIds;
    }

    public void setServicePointIds(List<ServicePointId> servicePointIds) {
        this.servicePointIds = servicePointIds;
    }

    public List<ServicePoint> getServicePoints() throws NotFoundException {
        if(getServicePointIds() != null && getServicePointIds().size() > 0) {
            final List<ServicePoint> servicePoints = servicePointFacade.find( new ArrayList<>(getServicePointIds()) );
            if(servicePoints.size() != getServicePointIds().size()) throw new NotFoundException("Could not find service points for all provided ids.");
            return servicePoints;
        }
        return null;
    }

    public List<WorkStationId> getWorkStationIds() {
        return workStationIds;
    }

    public void setWorkStationIds(List<WorkStationId> workStationIds) {
        this.workStationIds = workStationIds;
    }

    public List<WorkStation> getWorkStations() throws NotFoundException {
        if(getWorkStationIds() != null && getWorkStationIds().size() > 0) {
            final List<WorkStation> workStations = workStationFacade.find( new ArrayList<>(getWorkStationIds()) );
            if(workStations.size() != getWorkStationIds().size()) throw new NotFoundException("Could not find work stations for all provided ids.");
            return workStations;
        }
        return null;
    }

    public List<Long> getEmployeeIds() {
        return employeeIds;
    }

    public void setEmployeeIds(List<Long> employeeIds) {
        this.employeeIds = employeeIds;
    }

    public List<Employee> getEmployees() throws NotFoundException {
        if(getEmployeeIds() != null && getEmployeeIds().size() > 0) {
            final List<Employee> employees = employeeFacade.find( new ArrayList<>(getEmployeeIds()) );
            if(employees.size() != getEmployeeIds().size()) throw new NotFoundException("Could not find employees for all provided ids.");
            return employees;
        }
        return null;
    }

    public List<ProviderServiceId> getProviderServiceIds() {
        return providerServiceIds;
    }

    public void setProviderServiceIds(List<ProviderServiceId> providerServiceIds) {
        this.providerServiceIds = providerServiceIds;
    }

    public List<ProviderService> getProviderServices() throws NotFoundException {
        if(getProviderServiceIds() != null && getProviderServiceIds().size() > 0) {
            final List<ProviderService> providerServices = providerServiceFacade.find( new ArrayList<>(getProviderServiceIds()) );
            if(providerServices.size() != getProviderServiceIds().size()) throw new NotFoundException("Could not find provider services for all provided ids.");
            return providerServices;
        }
        return null;
    }

    public RESTDateTime getTransactionTimeAfter() {
        return transactionTimeAfter;
    }

    public void setTransactionTimeAfter(RESTDateTime transactionTimeAfter) {
        this.transactionTimeAfter = transactionTimeAfter;
    }

    public RESTDateTime getTransactionTimeBefore() {
        return transactionTimeBefore;
    }

    public void setTransactionTimeBefore(RESTDateTime transactionTimeBefore) {
        this.transactionTimeBefore = transactionTimeBefore;
    }

    public RESTDateTime getBookedTimeAfter() {
        return bookedTimeAfter;
    }

    public void setBookedTimeAfter(RESTDateTime bookedTimeAfter) {
        this.bookedTimeAfter = bookedTimeAfter;
    }

    public RESTDateTime getBookedTimeBefore() {
        return bookedTimeBefore;
    }

    public void setBookedTimeBefore(RESTDateTime bookedTimeBefore) {
        this.bookedTimeBefore = bookedTimeBefore;
    }

    public List<Long> getTermIds() {
        return termIds;
    }

    public void setTermIds(List<Long> termIds) {
        this.termIds = termIds;
    }

    public List<Term> getTerms() throws NotFoundException {
        if(getTermIds() != null && getTermIds().size() > 0) {
            final List<Term> terms = termFacade.find( new ArrayList<>(getTermIds()) );
            if(terms.size() != getTermIds().size()) throw new NotFoundException("Could not find terms for all provided ids.");
            return terms;
        }
        return null;
    }

    public Double getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(Double minPrice) {
        this.minPrice = minPrice;
    }

    public Double getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(Double maxPrice) {
        this.maxPrice = maxPrice;
    }

    public List<CurrencyCode> getCurrencyCodes() {
        return currencyCodes;
    }

    public void setCurrencyCodes(List<CurrencyCode> currencyCodes) {
        this.currencyCodes = currencyCodes;
    }

    public List<Integer> getPaymentMethodIds() {
        return paymentMethodIds;
    }

    public void setPaymentMethodIds(List<Integer> paymentMethodIds) {
        this.paymentMethodIds = paymentMethodIds;
    }

    public List<PaymentMethod> getPaymentMethods() throws NotFoundException {
        if(getPaymentMethodIds() != null && getPaymentMethodIds().size() > 0) {
            final List<PaymentMethod> paymentMethods = paymentMethodFacade.find( new ArrayList<>(getPaymentMethodIds()) );
            if(paymentMethods.size() != getPaymentMethodIds().size()) throw new NotFoundException("Could not find payment methods for all provided ids.");
            return paymentMethods;
        }
        return null;
    }

    public Boolean getPaid() {
        return paid;
    }

    public void setPaid(Boolean paid) {
        this.paid = paid;
    }
}