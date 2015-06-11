package pl.salonea.entities;

import pl.salonea.entities.idclass.WorkStationId;

import javax.persistence.*;

@Entity
@Table(name = "work_station")
@Access(AccessType.PROPERTY)
@IdClass(WorkStationId.class)
public class WorkStation {

    private Integer workStationNumber; // PK
    private ServicePoint servicePoint; // composite PK, composite FK

    /* constructors */

    public  WorkStation() { }

    public WorkStation(ServicePoint servicePoint, Integer workStationNumber) {
        this.servicePoint = servicePoint;
        this.workStationNumber = workStationNumber;
    }

    /* getters and setters */

    @Id
    @Basic(optional = false)
    @Column(name = "work_station_no", nullable = false, columnDefinition = "INT UNSIGNED")
    public Integer getWorkStationNumber() {
        return workStationNumber;
    }

    public void setWorkStationNumber(Integer workStationNumber) {
        this.workStationNumber = workStationNumber;
    }

    @Id
    @JoinColumns(value = {
            @JoinColumn(name = "provider_id", referencedColumnName = "provider_id", nullable = false, columnDefinition = "BIGINT UNSIGNED"),
            @JoinColumn(name = "service_point_no", referencedColumnName = "service_point_no", nullable = false, columnDefinition = "INT UNSIGNED"),
    }, foreignKey = @ForeignKey(name = "fk_work_station_service_point"))
    @ManyToOne(fetch = FetchType.EAGER)
    public ServicePoint getServicePoint() {
        return servicePoint;
    }

    public void setServicePoint(ServicePoint servicePoint) {
        this.servicePoint = servicePoint;
    }
}
