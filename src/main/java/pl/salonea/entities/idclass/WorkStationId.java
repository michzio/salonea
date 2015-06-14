package pl.salonea.entities.idclass;


import java.io.Serializable;

public class WorkStationId implements Serializable{

    private Integer workStationNumber; // PK (matches name of attribute)
    private ServicePointId servicePoint; // composite PK, composite FK (matches name of attribute and type of ServicePoint PK)

    /* constructors */
    public WorkStationId() { }

    public WorkStationId(ServicePointId servicePointId, Integer workStationNumber) {
        this.servicePoint = servicePointId;
        this.workStationNumber = workStationNumber;
    }

    public WorkStationId(Long providerId, Integer servicePointNumber, Integer workStationNumber) {
        this.servicePoint = new ServicePointId(providerId, servicePointNumber);
        this.workStationNumber = workStationNumber;
    }

    /* getters and setters */

    public Integer getWorkStationNumber() {
        return workStationNumber;
    }

    public void setWorkStationNumber(Integer workStationNumber) {
        this.workStationNumber = workStationNumber;
    }

    public ServicePointId getServicePoint() {
        return servicePoint;
    }

    public void setServicePoint(ServicePointId servicePointId) {
        this.servicePoint = servicePointId;
    }

    /* equals() and hashCode() */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WorkStationId that = (WorkStationId) o;

        if (workStationNumber != null ? !workStationNumber.equals(that.workStationNumber) : that.workStationNumber != null)
            return false;
        return !(servicePoint != null ? !servicePoint.equals(that.servicePoint) : that.servicePoint != null);

    }

    @Override
    public int hashCode() {
        int result = workStationNumber != null ? workStationNumber.hashCode() : 0;
        result = 31 * result + (servicePoint != null ? servicePoint.hashCode() : 0);
        return result;
    }
}
