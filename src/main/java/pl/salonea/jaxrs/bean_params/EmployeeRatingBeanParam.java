package pl.salonea.jaxrs.bean_params;

import pl.salonea.ejb.stateless.ClientFacade;
import pl.salonea.ejb.stateless.EmployeeFacade;
import pl.salonea.entities.Client;
import pl.salonea.entities.Employee;
import pl.salonea.jaxrs.exceptions.NotFoundException;

import javax.inject.Inject;
import javax.ws.rs.QueryParam;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by michzio on 06/11/2015.
 */
public class EmployeeRatingBeanParam extends RatingBeanParam {

    private @QueryParam("clientId") List<Long> clientIds;
    private @QueryParam("employeeId") List<Long> employeeIds;
    private @QueryParam("clientComment") String clientComment;
    private @QueryParam("employeeDementi") String employeeDementi;

    @Inject
    private EmployeeFacade employeeFacade;

    @Inject
    private ClientFacade clientFacade;

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

    public String getClientComment() {
        return clientComment;
    }

    public void setClientComment(String clientComment) {
        this.clientComment = clientComment;
    }

    public String getEmployeeDementi() {
        return employeeDementi;
    }

    public void setEmployeeDementi(String employeeDementi) {
        this.employeeDementi = employeeDementi;
    }
}
