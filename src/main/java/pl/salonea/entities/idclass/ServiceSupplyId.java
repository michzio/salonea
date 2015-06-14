package pl.salonea.entities.idclass;


import java.io.Serializable;

public class ServiceSupplyId implements Serializable {

    private Integer service;
    private Long employee;
    private Long term;

    /* constructors */

    public ServiceSupplyId() { }

    public ServiceSupplyId(Integer serviceId, Long employeeId, Long termId) {
        this.service = serviceId;
        this.employee = employeeId;
        this.term = termId;
    }

    /* getters and setters */

    public Integer getService() {
        return service;
    }

    public void setService(Integer serviceId) {
        this.service = serviceId;
    }

    public Long getEmployee() {
        return employee;
    }

    public void setEmployee(Long employeeId) {
        this.employee = employeeId;
    }

    public Long getTerm() {
        return term;
    }

    public void setTerm(Long termId) {
        this.term = termId;
    }

    /* equals() and hashCode() */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ServiceSupplyId that = (ServiceSupplyId) o;

        if (service != null ? !service.equals(that.service) : that.service != null) return false;
        if (employee != null ? !employee.equals(that.employee) : that.employee != null) return false;
        return !(term != null ? !term.equals(that.term) : that.term != null);

    }

    @Override
    public int hashCode() {
        int result = service != null ? service.hashCode() : 0;
        result = 31 * result + (employee != null ? employee.hashCode() : 0);
        result = 31 * result + (term != null ? term.hashCode() : 0);
        return result;
    }
}
