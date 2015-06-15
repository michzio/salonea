package pl.salonea.entities.idclass;


import java.io.Serializable;

public class EmployeeRatingId implements Serializable {

    private Long client;
    private Long employee;

    /* constructors */

    public EmployeeRatingId() { }

    public EmployeeRatingId(Long employeeId, Long clientId) {
        this.employee = employeeId;
        this.client = clientId;
    }

    /* getters and setters */

    public Long getClient() {
        return client;
    }

    public void setClient(Long client) {
        this.client = client;
    }

    public Long getEmployee() {
        return employee;
    }

    public void setEmployee(Long employee) {
        this.employee = employee;
    }

    /* equals() and hashCode() */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EmployeeRatingId that = (EmployeeRatingId) o;

        if (client != null ? !client.equals(that.client) : that.client != null) return false;
        return !(employee != null ? !employee.equals(that.employee) : that.employee != null);

    }

    @Override
    public int hashCode() {
        int result = client != null ? client.hashCode() : 0;
        result = 31 * result + (employee != null ? employee.hashCode() : 0);
        return result;
    }
}
