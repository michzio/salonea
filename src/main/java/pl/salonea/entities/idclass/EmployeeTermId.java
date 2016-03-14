package pl.salonea.entities.idclass;

import java.io.Serializable;

public class EmployeeTermId implements Serializable {

    private Long term; // PK (matches name of attribute)
    private Long employee; // PK (matches name of attribute)

    /* constructors */

    public EmployeeTermId() { }

    public EmployeeTermId(Long termId, Long employeeId) {
        this.term = termId;
        this.employee = employeeId;
    }

    /* getters and setters */

    public Long getTerm() {
        return term;
    }

    public void setTerm(Long termId) {
        this.term = termId;
    }

    public Long getEmployee() {
        return employee;
    }

    public void setEmployee(Long employeeId) {
        this.employee = employeeId;
    }

     /* equals() and hashCode() */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EmployeeTermId that = (EmployeeTermId) o;

        if (term != null ? !term.equals(that.term) : that.term != null) return false;
        return !(employee != null ? !employee.equals(that.employee) : that.employee != null);

    }

    @Override
    public int hashCode() {
        int result = term != null ? term.hashCode() : 0;
        result = 31 * result + (employee != null ? employee.hashCode() : 0);
        return result;
    }
}
