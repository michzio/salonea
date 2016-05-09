package pl.salonea.jaxrs.bean_params;

import pl.salonea.ejb.stateless.*;
import pl.salonea.entities.WorkStation;
import pl.salonea.entities.idclass.ProviderServiceId;
import pl.salonea.entities.idclass.ServicePointId;
import pl.salonea.entities.idclass.WorkStationId;
import pl.salonea.enums.CurrencyCode;
import pl.salonea.jaxrs.utils.RESTDateTime;

import javax.inject.Inject;
import javax.ws.rs.QueryParam;
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

    // TODO

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

    public List<Long> getProviderIds() {
        return providerIds;
    }

    public void setProviderIds(List<Long> providerIds) {
        this.providerIds = providerIds;
    }

    public List<Integer> getServiceIds() {
        return serviceIds;
    }

    public void setServiceIds(List<Integer> serviceIds) {
        this.serviceIds = serviceIds;
    }

    public List<ServicePointId> getServicePointIds() {
        return servicePointIds;
    }

    public void setServicePointIds(List<ServicePointId> servicePointIds) {
        this.servicePointIds = servicePointIds;
    }

    public List<WorkStationId> getWorkStationIds() {
        return workStationIds;
    }

    public void setWorkStationIds(List<WorkStationId> workStationIds) {
        this.workStationIds = workStationIds;
    }

    public List<Long> getEmployeeIds() {
        return employeeIds;
    }

    public void setEmployeeIds(List<Long> employeeIds) {
        this.employeeIds = employeeIds;
    }

    public List<ProviderServiceId> getProviderServiceIds() {
        return providerServiceIds;
    }

    public void setProviderServiceIds(List<ProviderServiceId> providerServiceIds) {
        this.providerServiceIds = providerServiceIds;
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

    public Boolean getPaid() {
        return paid;
    }

    public void setPaid(Boolean paid) {
        this.paid = paid;
    }
}
