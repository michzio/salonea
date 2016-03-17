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

    /**
     * Allowed serialized employee term id formats:
     * [1,2]  [1+2]  [1-2]
     * (1,2)  (1+2)  (1-2)
     * 1,2    1+2    1-2
     */
    public static EmployeeTermId valueOf(String employeeTermIdString) {

        if( !employeeTermIdString.matches("[(\\[]?\\d+[,+ -]{1}\\d+[\\])]?") )
            throw new IllegalArgumentException("Serialized employee term id doesn't match specified regex pattern.");

        // trim leading and trailing brackets
        employeeTermIdString = employeeTermIdString.replaceAll("[(\\[)\\]]", "");
        // split identifiers by several possible delimiters
        String[] tokens = employeeTermIdString.split("[,+ -]", 2);
        if( tokens.length != 2 )
            throw new IllegalArgumentException("Serialized employee term id should consist of two delimited tokens ex. 1+2");

        Long employeeId = Long.valueOf(tokens[0]);
        Long termId = Long.valueOf(tokens[1]);

        // construct employee term id
        return new EmployeeTermId(termId, employeeId);
    }
}
